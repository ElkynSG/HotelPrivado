package com.esilva.a910private;

import static com.esilva.a910private.Constants.*;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ASK_QUESTION_REQUEST = 3;
    private int stateTr;
    private int stateConectionBt;

    private int intActivity;

    //View
    private TextView tv_status, tv_msm;
    private LinearLayout bt_status, bt_inicio;

    Boolean test;

    //Bluetooth
    private static final String APP_NAME = "BTPrivate";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private int stateBT;
    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;
    private static final int STATE_MESSAGE_NO_CONNECTED = 6;
    private int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothActivity.SendReceive sendReceive;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket = null;

    // comercios
    private int montoPagar;
    private Boolean UltimaTr;

    private String numAprobacion;
    private String recibo;
    private String FechaTr;
    private String stInfo;

    private String DataRecv="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initFindView();
        stateTr = STATE_TR_WAIT;
        numAprobacion = null;
        recibo = null;
        FechaTr = null;
        stateConectionBt = STATE_LISTENING;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        UltimaTr = false;
        montoPagar = 0;
        intActivity = 100;

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)*/ {
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                return;
            }
        }

        socket = privateAppication.getSocket();


        //StateConection stateConection = new StateConection();
        //stateConection.start();

        if(socket== null){
            inicioServer();
        }
        else if(socket.isConnected() == false ){
            inicioServer();
        }else{
            stateBT = STATE_CONNECTED;
        }

        test = true;

    }


    private void initFindView() {
        tv_status = findViewById(R.id.tv_status_bt);
        tv_msm = findViewById(R.id.tv_sms_bt);
        bt_status = findViewById(R.id.li_incio);
        bt_status.setOnClickListener(this);

        bt_inicio = findViewById(R.id.li_status);
        bt_inicio.setOnClickListener(this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            tv_msm.setText("Powered by GST   Version:" +  pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.li_status){
            if(stateBT != STATE_CONNECTING) {
                reinico();
            }
        }else if(view.getId() == R.id.li_incio){
            if(stateBT != STATE_CONNECTING) {
                reinico();
            }
        }


    }
    private void inicioServer(){
        ServerClass serverClass = new BluetoothActivity.ServerClass();
        serverClass.start();
        stateTr = STATE_TR_WAIT;
    }

    private void terminarCom(){
        if(socket != null) {
            if (socket.isConnected()) {

                try {
                    serverSocket.close();
                    socket = null;
                    if(sendReceive != null && sendReceive.isAlive()){
                        sendReceive.setRunHilo(false);
                        sendReceive.inputStream.close();
                        sendReceive.outputStream.close();
                        Thread.sleep(1000);
                        sendReceive = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain();
                message.what = STATE_MESSAGE_NO_CONNECTED;
                handler.sendMessage(message);

                Log.v("TestBt", "cierra socket");

            }
        }
    }

    private void reinico(){
        stateConectionBt = STATE_LISTENING;
        terminarCom();
        inicioServer();
    }

    private void checkConexionTerminal(){
        CountDownTimer t = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v("Conectando","check conexion");
            }

            @Override
            public void onFinish() {
                if(stateConectionBt != STATE_LISTENING){
                    reinico();
                }
            }
        }.start();
    }

    private final int CO_CONECTADO =    1;
    private final int CO_MONTO =        2;
    private final int CO_PROCESANDO =   3;
    private final int CO_REQ_STATE =    4;
    private final int CO_RES_TRANSAC =  5;
    private final int CO_REQ_REINICIO = 6;
    private final int CO_REQ_INFO     = 7;
    private final int CO_REQ_IMPRE     = 8;
    private final int CO_FAIL =         9;


    private static final String TAG_CONNECTED="CONECTADO";
    private static final String TAG_MONTO="MONTO";
    private static final String TAG_PROCESA="PROCESANDO";
    private static final String TAG_REC_STATE="ESTADO";
    private static final String TAG_RES_TRAN="RES_TRANSA";
    private static final String TAG_RES_INFO="INFO";
    private static final String TAG_RES_IMPRE="IMPRE";
    private static final String TAG_REQ_REINICIO="REINICIO";
    private static final String TAG_FAIL="TAG_FAIL";

    private static final String TAG_ACK_CONEC="ACK_CONECTADO";
    private static final String TAG_ACK_MONTO="ACK_MONTO";
    private static final String TAG_ACK_PROCE="ACK_PROCESANDO";
    private static final String TAG_ACK_ESTADO="ACK_ESTADO";
    private static final String TAG_ACK_TRANS="ACK_RES_TRANSA";
    private static final String TAG_ACK_INFO="ACK_RES_INFO";
    private static final String TAG_ACK_IMPRE="ACK_RES_IMPRE";
    private static final String TAG_TR="RES_TR";
    private static final String TAG_NOT="RES_NO";

    private String ArmaRest(Boolean isOK,int comando){
        String resS= "";
        switch (comando){
            case CO_CONECTADO:
                resS = TAG_ACK_CONEC+":";
                resS += isOK?"OK":"FAIL";
                break;
            case CO_MONTO:
                resS = TAG_ACK_MONTO+":";
                resS += isOK?"OK":"FAIL";
                break;
            case CO_PROCESANDO:
                resS = TAG_ACK_PROCE+":";
                resS += isOK?"OK":"FAIL";
                break;
            case CO_REQ_STATE:
                resS = TAG_ACK_ESTADO+":";
                resS += String.valueOf(stateTr);
                break;
            case CO_RES_TRANSAC:
                resS = TAG_ACK_TRANS+":";
                resS += isOK?"OK":"FAIL";
                break;
            case CO_REQ_INFO:
                resS = TAG_ACK_INFO+":";
                resS += isOK?stInfo:"FAIL";
                break;
            case CO_REQ_IMPRE:
                resS = TAG_ACK_IMPRE+":";
                resS += isOK?"OK":"FAIL";
                break;
            default:
                resS = TAG_FAIL+":";
                resS += isOK?"OK":"FAIL";
                break;
        }

        return resS;
    }


    ////////////////////////////////////////////////////////////////////////////////////
    private int GetCommand(String dataIn){
        String[] parcer = dataIn.split(":");
        int iRet=-1;
        if(parcer.length>1)
        {
            if(parcer[0].equals(TAG_CONNECTED))
                iRet = CO_CONECTADO;
            else if(parcer[0].equals(TAG_MONTO))
                iRet = CO_MONTO;
            else if(parcer[0].equals(TAG_PROCESA))
                iRet = CO_PROCESANDO;
            else if(parcer[0].equals(TAG_REC_STATE))
                iRet = CO_REQ_STATE;
            else if(parcer[0].equals(TAG_RES_TRAN))
                iRet = CO_RES_TRANSAC;
            else if(parcer[0].equals(TAG_REQ_REINICIO)) {
                stateConectionBt = STATE_LISTENING;
                iRet = CO_REQ_REINICIO;
            }else if(parcer[0].equals(TAG_RES_INFO))
                iRet = CO_REQ_INFO;
            else if(parcer[0].equals(TAG_RES_IMPRE))
                iRet = CO_REQ_IMPRE;
            else if(parcer[0].equals(TAG_FAIL))
                iRet = CO_FAIL;
            else
                iRet = CO_FAIL;
        }
        return iRet;
    }

    private Boolean procesaComandos(String dataIn){
        Boolean result=false;
        Log.v("procesar","procesar");
        int comando = GetCommand(dataIn);

        switch (comando){
            case CO_CONECTADO:
                result = proceConectado();
                break;
            case CO_MONTO:
                result = proceMonto(dataIn);
                break;
            case CO_PROCESANDO:
                result = procePocesa();
                break;
            case CO_REQ_STATE:
                result = proceReqState();
                break;
            case CO_RES_TRANSAC:
                result = proceStateUltTr();
                break;
            case CO_REQ_REINICIO:
                result = proceReinicio();
                break;
            case CO_REQ_INFO:
                result = proceInfo();
                break;
            case CO_REQ_IMPRE:
                result = proceImpresion(dataIn);
                break;
            case CO_FAIL:
                result = proceFail();
                break;
            default:
                break;
        }

        if(sendReceive != null && CO_REQ_IMPRE!=comando)
            sendReceive.write(ArmaRest(result,comando).getBytes());

        return result;
    }

    private Boolean proceConectado(){
        return true;
    }

    private Boolean proceMonto(String data_in){
        String[] dta = parcerComand(data_in);
        if(dta != null) {
            if (dta[0].equals(TAG_MONTO)) {
                try {
                    montoPagar = Integer.valueOf(dta[1]);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    private Boolean procePocesa(){

        if(test) {
            //test = false;
            String result = getJson();
            if (instanceComercios(result)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private Boolean proceReqState(){
        return true;
    }

    private Boolean proceStateUltTr(){
        return UltimaTr;
    }

    private Boolean proceReinicio(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reinico();
            }
        });
        return true;
    }

    private Boolean proceFail(){
        return true;
    }

    private Boolean proceInfo(){
        if(numAprobacion != null && FechaTr != null && recibo != null){
            stInfo = numAprobacion + "," + FechaTr + "," + recibo;
        }else{
            return false;
        }
        return true;
    }

    private String[] getLineasPrint(String DataInDecoder){
        ArrayList<String> listTemp = new ArrayList<>();
        String[] temp = DataInDecoder.split("\n",-1);
        return temp;
    }
    private Boolean proceImpresion(String impre){
        String[] dataImpre = impre.split(":");
        if(dataImpre[1] == null)
            return false;

        if(dataImpre[1].isEmpty())
            return false;
        String[] lineasPrint = getLineasPrint(dataImpre[1]);

        instanceImpresion(lineasPrint);
        return true;
    }


    //////////////////////////////////////////////////////////////////////////////////////////


    private String[] parcerComand(String data){
        String[] parcer = data.split(":");
        if(parcer.length>1)
        {
            return parcer;
        }
        else
            return null;
    }

    private Boolean instanceComercios(String data){
        Intent intent = new Intent(Intent.ACTION_SEND);
        ComponentName cn = new ComponentName(PACKAGE, SEND_COMERCIOS);
        intent.setComponent(cn);
        setResult(Activity.RESULT_OK,intent);
        intent.putExtra("data_input",data);
        intent.putExtra(packageName, getApplicationContext().getPackageName());
        stateConectionBt = STATE_CONNECTED;

        try {
            startActivityForResult(intent, REQUESTCOMERCIOS);
            stateTr = STATE_TR_PROCCESS;
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),"App no encontrada",  Toast.LENGTH_SHORT).show();
            stateTr = STATE_TR_ERROR;
            return false;
        }
    }

    private String getJson() {
        String val = "{\"TipoTransaccion\":1, \"properties\": {\"Monto\":\"";
        val+= String.valueOf(montoPagar);
        val+= "\", \"Iva\": \"0\", \"Inc\": \"0\", \"Monto_base_iva\": \"0\", \"Monto_base_inc\": \"0\", \"Base_devolucion\": \"0\"}}";
        return val;

    }

    private Boolean instanceImpresion(String[] data){
        Intent intent = new Intent(this,ImpresionActivity.class);
        /*ComponentName cn = new ComponentName(PACKAGEPrint, SEND_IMPRESION);
        intent.setComponent(cn);*/
        setResult(Activity.RESULT_OK,intent);
        intent.putExtra(dataInputPrint,data);
        intent.putExtra(packageNamePrint, getApplicationContext().getPackageName());
        stateConectionBt = STATE_CONNECTED;

        try {
            startActivityForResult(intent, REQUESTPRINT);
            stateTr = STATE_TR_PROCCESS;
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),"App no encontrada",  Toast.LENGTH_SHORT).show();
            stateTr = STATE_TR_ERROR;
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTCOMERCIOS){
            stateTr = STATE_TR_FINISH;
            checkConexionTerminal();
            if(data != null) {
                String output = data.getStringExtra("data_output");
                procesaResutlActivity(output);
            }else{

            }
        }else if(requestCode == REQUESTPRINT){
            if(sendReceive != null )
                sendReceive.write(ArmaRest(true,CO_REQ_IMPRE).getBytes());
            Log.v("Impresion","resultado "+String.valueOf(resultCode));
        }

    }

    private static final String RES_VENTA_OK="VENTA_OK";
    private static final String RES_VENTA_FAIL="VENTA_FAIL";

    private void procesaResutlActivity(String result){
        try {
            JSONObject obk = new JSONObject(result);
            if(!obk.getString(JSON_NUM_CONFIR).isEmpty()){
                String codAutoriza = obk.getString(JSON_NUM_CONFIR);
                String jFecha = obk.getString(JSON_NUM_FECHA);
                String jRecibo = obk.getString(JSON_RECIBO);
                if(codAutoriza.isEmpty()) {
                    sendReceive.write(ArmaRest(false,CO_RES_TRANSAC).getBytes());
                }else
                    numAprobacion = codAutoriza;
                FechaTr = jFecha.replace(":","-");
                recibo = jRecibo;
                sendReceive.write(ArmaRest(true,CO_RES_TRANSAC).getBytes());
            }else{
                sendReceive.write(ArmaRest(false,CO_RES_TRANSAC).getBytes());
            }
        } catch (JSONException e) {
            sendReceive.write(ArmaRest(false,CO_RES_TRANSAC).getBytes());
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////   Esperas conexion    //////////////////////////////////////////////////////////
    private class ServerClass extends Thread {


        public ServerClass() {
            try {
                /* if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) */{
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);

                    return ;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    stateBT = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    stateBT =STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    stateBT =STATE_CONNECTED;
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new BluetoothActivity.SendReceive();
                    sendReceive.start();



                    break;
                }
            }
        }
    }

    ///////////////////////////////////////   Espera mensajes y envia    //////////////////////////////////////////////////////////
    private class SendReceive extends Thread
    {
        //private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private Boolean isHilo;

        public SendReceive ()
        {
            //bluetoothSocket=socket;
            isHilo = true;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=socket.getInputStream();
                tempOut=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }
        @Override
        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;
            Log.v("TestBt", "Inicia hilo");
            while (isHilo)
            {

                try {

                    bytes=inputStream.read(buffer);
                    stateBT =STATE_MESSAGE_RECEIVED;
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.v("TestBt","sale del hilo");
        }

        public void write(byte[] bytes)
        {
            try {

                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setRunHilo(Boolean value){
            isHilo = false;
        }
    }


    /////////////////////////////////////////  handle    /////////////////////////////////////////////////////////
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    stateBT =STATE_LISTENING;
                    tv_status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    stateBT =STATE_CONNECTING;
                    intActivity=0;
                    tv_status.setText("Wait");
                    break;
                case STATE_CONNECTED:
                    stateBT =STATE_CONNECTED;
                    intActivity=0;
                    tv_status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    stateBT =STATE_CONNECTION_FAILED;
                    tv_status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_NO_CONNECTED:
                    stateBT =STATE_MESSAGE_NO_CONNECTED;
                    tv_status.setText("Not Connected");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    stateBT =STATE_MESSAGE_RECEIVED;
                    intActivity=0;
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    //tv_msm.setText(tempMsg);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            procesaComandos(tempMsg);
                        }
                    });

                    break;
            }
            return true;
        }
    });

    private class StateConection extends Thread
    {
        //private final BluetoothSocket bluetoothSocket;

        private Boolean isHilo;
        private Boolean isCheck;

        public StateConection ()
        {
            isHilo = true;
            isCheck = true;
        }
        @Override
        public void run()
        {

            while (isHilo)
            {


                if(isCheck) {
                    if (intActivity > 120) {    // cada hora revisa actividad
                        if (stateBT != STATE_CONNECTING) {
                            intActivity = 0;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    reinico();
                                }
                            });
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Reinicio","actividad["+String.valueOf(intActivity)+"]");
                    }
                });

                try {
                    intActivity++;
                    Thread.sleep(10000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("Reinicio","actividad["+String.valueOf(intActivity)+"]");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void pausa(){
            isCheck = false;
        }
        public void retomar(){
            isCheck = true;
        }


        public void cancel(){
            isHilo = false;
        }
    }

    @Override
    public void onBackPressed() {

    }
}