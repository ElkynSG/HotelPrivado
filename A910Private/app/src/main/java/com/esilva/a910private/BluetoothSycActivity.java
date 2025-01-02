package com.esilva.a910private;

import static com.esilva.a910private.Constants.JSON_COD_RES;
import static com.esilva.a910private.Constants.JSON_ID_TR;
import static com.esilva.a910private.Constants.JSON_NUM_CONFIR;
import static com.esilva.a910private.Constants.JSON_NUM_FECHA;
import static com.esilva.a910private.Constants.JSON_RECIBO;
import static com.esilva.a910private.Constants.PACKAGE;
import static com.esilva.a910private.Constants.REQUESTCOMERCIOS;
import static com.esilva.a910private.Constants.REQUESTPRINT;
import static com.esilva.a910private.Constants.SEND_COMERCIOS;


import static com.esilva.a910private.Constants.STATE_TR_ERROR;
import static com.esilva.a910private.Constants.STATE_TR_PROCCESS;
import static com.esilva.a910private.Constants.STATE_TR_WAIT;
import static com.esilva.a910private.Constants.dataInputPrint;
import static com.esilva.a910private.Constants.packageName;
import static com.esilva.a910private.Constants.packageNamePrint;
import static com.esilva.a910private.Protocolo.ACK;

import static com.esilva.a910private.Protocolo.ACK_TR;
import static com.esilva.a910private.Protocolo.FAIL;

import static com.esilva.a910private.Protocolo.NAK;

import static com.esilva.a910private.Protocolo.PO_RES;
import static com.esilva.a910private.Protocolo.PRO;

import static com.esilva.a910private.Protocolo.SHARE_BASE_TRANSAC;
import static com.esilva.a910private.Protocolo.SHARE_LAST_TR;
import static com.esilva.a910private.Protocolo.STATE_CONECTADO;
import static com.esilva.a910private.Protocolo.STATE_CONSULTA;
import static com.esilva.a910private.Protocolo.STATE_MONTO;
import static com.esilva.a910private.Protocolo.STATE_SYNC;
import static com.esilva.a910private.Protocolo.STATE_VOUCHER;
import static com.esilva.a910private.Protocolo.TA_CON;
import static com.esilva.a910private.Protocolo.TA_FIN;
import static com.esilva.a910private.Protocolo.TA_MON;
import static com.esilva.a910private.Protocolo.TA_RES;
import static com.esilva.a910private.Protocolo.TA_SYN;
import static com.esilva.a910private.Protocolo.TA_TRA;
import static com.esilva.a910private.Protocolo.TA_VOU;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.app.ActivityCompat;

import com.esilva.a910private.dataBD.AdminBaseDatos;
import com.esilva.a910private.dataBD.DataVentas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothSycActivity extends AppCompatActivity implements View.OnClickListener {
    private final int TR_ESPERANDO  = 1;
    private final int TR_CONECTADO  = 2;
    private final int TR_EN_PROCESO = 3;
    private final int TR_FINALIZADO = 4;
    private final int TR_FALLIDA    = 5;

    private static final int ASK_QUESTION_REQUEST = 3;
    private int stateTrans;
    private int stateProceso;
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
    private BluetoothSycActivity.SendReceive sendReceive;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket = null;

    // comercios
    private int montoPagar;
    private int idTran;
    private DataVentas dataVentas;
    private Boolean UltimaTr;

    private String codRespuesta;
    private String numAprobacion;
    private String recibo;
    private String FechaTr;
    private String stInfo;

    private String DataRecv = "";
    private CountDownTimer waitResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initFindView();
        stateTrans = STATE_TR_WAIT;
        numAprobacion = null;
        recibo = null;
        FechaTr = null;
        stateConectionBt = STATE_LISTENING;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        UltimaTr = false;
        montoPagar = 0;
        intActivity = 100;

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)*/
            {
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                return;
            }
        }
        dataVentas = new DataVentas();
        socket = privateAppication.getSocket();

        inicioServer();

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
            tv_msm.setText("Powered by GST   Version:" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.li_status) {
            if (stateBT != STATE_CONNECTING) {
                reinico();
            }
        } else if (view.getId() == R.id.li_incio) {
            if (stateBT != STATE_CONNECTING) {
                reinico();
            }
        }


    }

    private void inicioServer() {
        /*AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        adminBaseDatos.deleteAllVentas();
        adminBaseDatos.closeBaseDtos();*/

        logPendiente("Inicio y fin de ventas");
        ServerClass serverClass = new ServerClass();
        serverClass.start();
        if(dataVentas != null){
            dataVentas.clearData();
        }
        stateTrans = TR_ESPERANDO;
        stateProceso = 0;
    }

    private void terminarCom() {
        if (socket != null) {
            if (socket.isConnected()) {

                try {
                    serverSocket.close();
                    socket = null;
                    if (sendReceive != null && sendReceive.isAlive()) {
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
            socket = null;
        }
    }

    private void reinico() {
        stateConectionBt = STATE_LISTENING;
        terminarCom();
        inicioServer();
    }





    private static final String TAG_FAIL = "TAG_FAIL";

    private static final String TAG_ACK_CONEC = "ACK_CONECTADO";
    private static final String TAG_ACK_MONTO = "ACK_MONTO";
    private static final String TAG_ACK_PROCE = "ACK_PROCESANDO";
    private static final String TAG_ACK_ESTADO = "ACK_ESTADO";
    private static final String TAG_ACK_TRANS = "ACK_RES_TRANSA";
    private static final String TAG_ACK_INFO = "ACK_RES_INFO";
    private static final String TAG_ACK_IMPRE = "ACK_RES_IMPRE";
    private static final String TAG_TR = "RES_TR";
    private static final String TAG_NOT = "RES_NO";



    ////////////////////////////////////////////////////////////////////////////////////
    private int GetProcess(String dataIn) {
        if (dataIn.contains(TA_CON))
            procesaConectado();
        else if (dataIn.contains(TA_SYN))
            procesaSync();
        else if (dataIn.contains(TA_MON))
            procesaCompra(dataIn);
        else if (dataIn.contains(TA_RES))
            procesaRespuesta();
        else if (dataIn.contains(TA_VOU))
            proceImpresion(dataIn);
        else if (dataIn.contains(TA_TRA))
            procesaCompra(dataIn);
        else if (dataIn.contains(TA_FIN))
            procesaFIN();
        else if (dataIn.contains(NAK))
            procesaNAK();
        else if (dataIn.contains(ACK_TR))
            procesaDeleteTr();
        else
            procesaConectado();

        return 0;
    }

    private String[] parcerMonto(String data){
        String[] parcer = data.split("\n",-1);;
        if(parcer.length>1)
        {
            return parcer;
        }
        else
            return null;
    }

    private String[] parcerComand(String data){
        String[] parcer = data.split(":");
        if(parcer.length>1)
        {
            return parcer;
        }
        else
            return null;
    }

    //todo  *************   procesa ACK PROCESA LA CONFIRMACION DE LA RECEPCION DE LA TRANSCCION   ********************************
    // PENSAR EN ENVIAR EL ESTADO DONDE SE ENCUNTRA

    //todo  *************   procesa NAK   ********************************
    private void procesaNAK(){
        switch (stateProceso){
            case STATE_CONECTADO:
                enviarData(ACK);
                break;
            case STATE_SYNC:
                procesaSync();
                break;
            case STATE_MONTO:
                enviarData(ACK);
                break;
            case STATE_CONSULTA:
                procesaRespuesta();
                break;
            case STATE_VOUCHER:
                break;

            default:
                break;

        }
        //enviarData(ACK);
        //stateTrans = TR_CONECTADO;
        //stateProceso = STATE_CONECTADO;
    }

    //todo  *************   procesa conectado   ********************************
    private void procesaConectado(){
        logPendiente("conectado");
        enviarData(ACK);
        stateTrans = TR_CONECTADO;
        stateProceso = STATE_CONECTADO;
    }

    //todo  *************   procesa SYNC   ********************************
    private void procesaSync(){
        //logPendiente("conectado");
        SharedPreferences preferences = getSharedPreferences(SHARE_BASE_TRANSAC,MODE_PRIVATE);
        String dataTr = preferences.getString(SHARE_LAST_TR, null);
        if(dataTr != null){
            enviarData(dataTr);
        }else {
            enviarData(ACK);
        }
    }
    //todo  *************   procesa monto   ********************************
    private void procesaCompra(String data){
        String[] dta1 = parcerMonto(data);
        if(dta1 != null){
            String[] monto = parcerComand(dta1[0]);
            try {
                if (monto != null) {
                    montoPagar = Integer.valueOf(monto[1]);
                }else {
                    enviarData(NAK);
                    return;
                }
            }catch (Exception e){
                enviarData(NAK);
                return;
            }
            String[] idTransac = parcerComand(dta1[1]);
            try {
                if (idTransac != null) {
                    idTran = Integer.valueOf(idTransac[1]);
                   // dataVentas.setId_transac(idTran);
                    //dataVentas.setMonto(String.valueOf(montoPagar));

                    enviarData(ACK);
                }else {
                    enviarData(NAK);
                    return;
                }
            }catch (Exception e){
                enviarData(NAK);
                return;
            }

        }else{
            enviarData(NAK);
            return;
        }

        String result = getJson();
        instanceComercios(result);
    }

    private String getJson() {
        String val = "{\"TipoTransaccion\":1, \"properties\": {\"Monto\":\"";
        val += String.valueOf(montoPagar);
        val += "\", \"Iva\": \"0\", \"Inc\": \"0\", \"Monto_base_iva\": \"0\", \"Monto_base_inc\": \"0\", \"Base_devolucion\": \"0\"}}";
        return val;

    }

    private void instanceComercios(String data) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        ComponentName cn = new ComponentName(PACKAGE, SEND_COMERCIOS);
        intent.setComponent(cn);
        setResult(Activity.RESULT_OK, intent);
        intent.putExtra("data_input", data);
        intent.putExtra(packageName, getApplicationContext().getPackageName());
        stateConectionBt = STATE_CONNECTED;
        codRespuesta = "99";
        try {
            startActivityForResult(intent, REQUESTCOMERCIOS);
            guardaShareInicial();
            stateTrans = TR_EN_PROCESO;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "App no encontrada", Toast.LENGTH_SHORT).show();
            stateTrans = TR_FALLIDA;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUESTCOMERCIOS) {
            if (data != null) {
                String output = data.getStringExtra("data_output");
                procesaResutlCompra(output);
            } else {
                stateTrans = TR_FALLIDA;
            }
        } else if (requestCode == REQUESTPRINT) {
            enviarData(ACK);
            Log.v("Impresion", "resultado " + String.valueOf(resultCode));
        }
    }

    private void procesaResutlCompra(String result) {
        //AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        try {
            JSONObject obk2 = new JSONObject(result);
            String codRespuesta2 = obk2.getString(JSON_COD_RES);
            codRespuesta = codRespuesta2;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject obk = new JSONObject(result);
            String codAutoriza = obk.getString(JSON_NUM_CONFIR);
            if (!codAutoriza.isEmpty()){
                String jFecha = obk.getString(JSON_NUM_FECHA);
                String jRecibo = obk.getString(JSON_RECIBO);
                FechaTr = jFecha.replace(":", "-");
                recibo = jRecibo;
                numAprobacion = codAutoriza;
                guardaOK_Transac();
                stateTrans = TR_FINALIZADO;
            }else {
                //dataVentas.setState(TR_FALLIDA);
                //adminBaseDatos.updateTrans(dataVentas);
                stateTrans = TR_FALLIDA;
            }
            timeEsperar();

        } catch (JSONException e) {
            //dataVentas.setState(TR_FALLIDA);
            //adminBaseDatos.updateTrans(dataVentas);
            stateTrans = TR_FALLIDA;
        }
       // adminBaseDatos.closeBaseDtos();

    }
    private void guardaShareInicial(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_ID_TR,idTran);
            jsonObject.put(JSON_COD_RES,"99");
            stInfo = PO_RES+":"+jsonObject.toString();
            guardaShare(stInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//todo  *************   procesa respuesta   ********************************
    private void procesaRespuesta(){
        String dtSend=FAIL;
        timeCancelar();
        switch (stateTrans){
            case TR_EN_PROCESO:
                enviarData(PRO);
                break;
            case TR_FALLIDA:
                enviarData(FAIL);
                timeEsperar();
                break;
            case TR_FINALIZADO:
                enviaDtTrans();
                timeEsperar();
                break;
            default:
                enviarData(FAIL);
                break;
        }


    }

    private void enviaDtTrans(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_COD_RES,codRespuesta);
            jsonObject.put(JSON_NUM_CONFIR,numAprobacion);
            jsonObject.put(JSON_NUM_FECHA,FechaTr);
            jsonObject.put(JSON_RECIBO,recibo);
            jsonObject.put(JSON_ID_TR,idTran);
            stInfo = PO_RES+":"+jsonObject.toString();
            guardaShare(stInfo);
            enviarData(stInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void guardaOK_Transac(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_COD_RES,codRespuesta);
            jsonObject.put(JSON_NUM_CONFIR,numAprobacion);
            jsonObject.put(JSON_NUM_FECHA,FechaTr);
            jsonObject.put(JSON_RECIBO,recibo);
            jsonObject.put(JSON_ID_TR,idTran);
            stInfo = PO_RES+":"+jsonObject.toString();
            guardaShare(stInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//todo  *************   procesa VOUCHER   ********************************
    private Boolean proceImpresion(String impre){
        String[] dataImpre = impre.split(":");
        if(dataImpre[1] == null)
            return false;

        if(dataImpre[1].isEmpty())
            return false;
        String[] lineasPrint = dataImpre[1].split("\n",-1);

        instanceImpresion(lineasPrint);
        return true;
    }

    private Boolean instanceImpresion(String[] data){
        Intent intent = new Intent(this,ImpresionActivity.class);
        setResult(Activity.RESULT_OK,intent);
        intent.putExtra(dataInputPrint,data);
        intent.putExtra(packageNamePrint, getApplicationContext().getPackageName());
        stateConectionBt = STATE_CONNECTED;

        try {
            startActivityForResult(intent, REQUESTPRINT);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),"App no encontrada",  Toast.LENGTH_SHORT).show();
            return false;
        }
    }
//todo  *************   procesa borra transaccion pendiente   ********************************
    private void procesaDeleteTr(){
        timeCancelar();
        guardaShare(null);

        /*logPendiente("inicio borra pendiente");
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        adminBaseDatos.deleteIdTransac(idTran);
        adminBaseDatos.closeBaseDtos();
        logPendiente("fin borra pendiente");*/
    }
    //todo  *************   procesa FIN   ********************************
    private void procesaFIN(){
        enviarData(ACK);
        reinico();
    }















private void guardaShare(String data){
    SharedPreferences preferences = getSharedPreferences(SHARE_BASE_TRANSAC,MODE_PRIVATE);
    preferences.edit().putString(SHARE_LAST_TR,data).commit();
}

///////////////////////////////////////   Esperas conexion    //////////////////////////////////////////////////////////
    private class ServerClass extends Thread {
        public ServerClass() {
            try {
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                    return ;
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

                    sendReceive = new BluetoothSycActivity.SendReceive();
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    ///////////////////////////////////////   Espera mensajes y envia    //////////////////////////////////////////////////////////
    private class SendReceive extends Thread
    {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private Boolean isHilo;

        public SendReceive ()
        {
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

        public void write(String data)
        {
            byte[] bytes = data.getBytes();
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
                    SharedPreferences preferences = getSharedPreferences(SHARE_BASE_TRANSAC,MODE_PRIVATE);
                    String dataTr = preferences.getString(SHARE_LAST_TR, null);
                    if(dataTr != null){
                        tv_status.setText("Wait Sync");
                    }else{
                        tv_status.setText("Wait");
                    }
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
                            GetProcess(tempMsg);
                        }
                    });

                    break;
            }
            return true;
        }
    });

    private void enviarData(String dtSend){
        if (sendReceive != null)
            sendReceive.write(dtSend);
    }

    private void timeEsperar(){
        waitResp = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long l) {
                Log.v("esperando","esperando respuesta");
            }

            @Override
            public void onFinish() {
                reinico();

            }
        }.start();
    }

    private void timeCancelar(){
        if(waitResp != null){
            waitResp.cancel();
            waitResp = null;
        }

    }



    @Override
    public void onBackPressed() {

    }


    private void logPendiente(String pos){
        Log.d("PENDINTES","-------------   "+pos+"   ----------------");
        Log.d("PENDINTES","--------   ID TRANSAC["+idTran+"]   ----------");
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        List<DataVentas> allPen = adminBaseDatos.getAllTransac();
        adminBaseDatos.closeBaseDtos();
        if(allPen == null) {
            Log.d("PENDINTES", "SIN DATOS");
            return;
        }

        for (DataVentas ven : allPen) {
            Log.d("PENDINTES","ID_TR:"+ven.getId_transac()+" APR:"+ven.getNumAproba()+" REC:"+ven.getRecibo()+" state:"+ven.getState()+" fecha:"+ven.getFecha()+" monto:"+ven.getMonto());
        }
        Log.d("PENDINTES","--------------- FIN -----------------\n");
    }
}