package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.DISCONNECT_TIMEOUT;
import static com.esilva.hotelprivado.Util.Constantes.SHA_BASE;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_INGLES;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esilva.hotelprivado.application.privadoApplication;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;

public class SaleBtActivity extends AppCompatActivity {

    static final int TIMER_SALE = 120000;
    private NumberFormat currencyFormatter;
    private TextView tv_total;
    private ImageView ima_check;
    private GifImageView giDialogo;
    private Button pay,Cancel, button;
    private static Dialog customDialog;
    private SharedPreferences preferences;
    private int tipoIdioma;
    private int intentos;
    private Boolean isGuardaCarrito;

    TextView tv_message;
    private  TextToSpeech speck;

    //  Blutooth
    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    static final int STATE_MESSAGE_RECV = 6;
    private int stateConectionBt;

    SendReceive sendReceive;
    int valorTotal;
    private String numAprobacion;
    private String fecha;
    private String recibo;
    private String fecha_tab;
    private String hora_tab;
    private String NitEmpresa;

    ListView listView;
    BluetoothDevice[] btArray;
    ArrayList<DataProduct>  miCArrito;
    ArrayList<DataProduct>  miCArritoVentas;
    Boolean tr = true;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sale);

        preferences = getSharedPreferences(SHA_BASE,MODE_PRIVATE);
        tipoIdioma = preferences.getInt(SHA_IDIOMA,0);
        stateConectionBt = STATE_LISTENING;
        numAprobacion = "NO PROC";
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.edit().putString("macBt",btArray[i].getAddress()).commit();
            }
        });


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || privadoApplication.getSdkPermision()==0) {
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                return;
            }
        }

        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;


        tv_total = findViewById(R.id.tv_total);
        Cancel= findViewById(R.id.btSaleCancelar);
        pay = findViewById(R.id.btSalePagar);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cancel.setEnabled(false);
                pay.setEnabled(false);
                stateConectionBt = STATE_CONNECTING;
                stopDisconnectTimer();
                showDialog();
                if(privadoApplication.getPreferences().getInt(SHA_IDIOMA,SHA_IDIOMA_INGLES) == SHA_IDIOMA_INGLES)
                    speck.speak("Please follow the instructions at the payment terminal.",TextToSpeech.QUEUE_FLUSH,null,null);
                else
                    speck.speak("Por favor siga las instrucciones en la terminal de pago.",TextToSpeech.QUEUE_FLUSH,null,null);

                tr = false;
            }
        });




        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizar();
            }
        });
        Bundle bundle = getIntent().getExtras().getBundle("monto");
        ArrayList<String>  obj;
        obj = bundle.getStringArrayList("carrito");
        miCArrito = new ArrayList<>();
        miCArritoVentas = new ArrayList<>();
        for(String tmp:obj){
            DataProduct dataProduct = new DataProduct();
            DataProduct dataProductVenta = new DataProduct();
            dataProduct = dataProduct.DesSerializar(tmp);
            dataProductVenta = dataProductVenta.DesSerializar(tmp);
            miCArrito.add(dataProduct);
            miCArritoVentas.add(dataProductVenta);
            valorTotal += Integer.valueOf(dataProduct.dt_precio.replace("$","").replace(".",""))*Integer.valueOf(dataProduct.dt_num_articulos);
        }

        //valorTotal = getIntent().getExtras().getInt("monto");
        String ValorTotal = currencyFormatter.format(Long.valueOf(valorTotal));

        tv_total.setText(ValorTotal);

        isGuardaCarrito = true;

        speck=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    if(privadoApplication.getPreferences().getInt(SHA_IDIOMA,SHA_IDIOMA_INGLES) == SHA_IDIOMA_INGLES)
                        speck.setLanguage(new Locale("en", "rUS"));
                    else
                        speck.setLanguage(new Locale("es", "col"));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private void showDialog(){
        Log.d("DP_DLOG","showDialog ");
        if(customDialog != null) {
            customDialog.dismiss();

            customDialog = null;
        }

        customDialog = new Dialog(SaleBtActivity.this,R.style.popup_dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.pago_dialog);

        tv_message = customDialog.findViewById(R.id.tv_mensaje);
        tv_message.setText(R.string.instruccion);
        button = customDialog.findViewById(R.id.btok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                customDialog = null;
            }
        });

        giDialogo = customDialog.findViewById(R.id.gifres);

        ima_check = customDialog.findViewById(R.id.im_estado);

        if(!isFinishing()) {
                customDialog.show();
                customDialog.getWindow().setLayout(900, 720);
        }
        conectarBt();

    }

    private void showDialog2(int text){
        Log.d("DP_DLOG","showDialog2 ");
        if(customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }

        customDialog = new Dialog(SaleBtActivity.this,R.style.popup_dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.pago_dialog);


        tv_message = customDialog.findViewById(R.id.tv_mensaje);
        tv_message.setText(text);
        Button button = customDialog.findViewById(R.id.btok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        giDialogo = customDialog.findViewById(R.id.gifres);
        giDialogo.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        ima_check = customDialog.findViewById(R.id.im_estado);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.app_item_zoomin);

        if(text == R.string.dialog_sale_retira) {
            ima_check.setImageResource(R.drawable.ok);
            ima_check.setVisibility(View.VISIBLE);
        }else {
            ima_check.setImageResource(R.drawable.fail);
            ima_check.setVisibility(View.VISIBLE);
        }

        if(!isFinishing()) {
            customDialog.show();
            customDialog.getWindow().setLayout(900, 720);
            ima_check.startAnimation(animation);
        }
        resetDisconnectTimer(3000);
    }

    private BluetoothDevice getDevice(){
        String mac = preferences.getString("macBt",null);
        if(mac == null) {
            Toast.makeText(getApplicationContext(), R.string.bt_no_config, Toast.LENGTH_LONG);
            return null;
        }

        BluetoothDevice btRet=null;
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || privadoApplication.getSdkPermision()==0) {

            Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
            String[] strings = new String[bt.size()];
            if (bt.size() > 0) {
                for (BluetoothDevice device : bt) {
                    String Addr = device.getAddress();
                    if(Addr.equals(mac)){
                        btRet = device;
                        break;
                    }
                }
            }
       }
        return btRet;
    }

    private void conectarBt() {

        terminarBt();
        BluetoothDevice device = getDevice();
        if(device != null) {
            ClientClass clientClass = new ClientClass(device);
            clientClass.start();
            checkConexionTerminal();

            //tv_state.setText("Connecting");
        }

    }

    private void terminarBt()  {
        if(sendReceive!= null){
            if(sendReceive.isAlive()){
                try {
                    sendReceive.outputStream.close();
                    sendReceive.setTaskFinish(false);
                    sendReceive.inputStream.close();
                    sendReceive = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(countDownTimer != null)
            countDownTimer.cancel();
    }

    private void checkConexionTerminal(){
        CountDownTimer t = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v("Conectando","check conexion");
            }

            @Override
            public void onFinish() {
                if(stateConectionBt != STATE_CONNECTED){
                    Cancel.setEnabled(true);
                    Toast.makeText(SaleBtActivity.this,R.string.sale_error_conect,Toast.LENGTH_LONG).show();
                }
            }
        }.start();
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
        device = device1;

        try {
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || privadoApplication.getSdkPermision()==0) {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void run() {
        try {
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || privadoApplication.getSdkPermision()==0) {
                socket.connect();
                sendReceive=new SendReceive(socket);
                sendReceive.start();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                return;
            }


        } catch (IOException e) {
            e.printStackTrace();
            Message message=Message.obtain();
            message.what=STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
        }
    }
}

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private Boolean task = true;

        public SendReceive (BluetoothSocket socket)
        {
            task = true;
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (task)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_MESSAGE_RECV;
                    handler.sendMessage(message);
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setTaskFinish(Boolean value){
            task = value;
        }
    }
    private final int CO_CONECTADO =    1;
    private final int CO_MONTO =        2;
    private final int CO_PROCESANDO =   3;
    private final int CO_REQ_STATE =    4;
    private final int CO_RES_TRANSAC =  5;
    private final int CO_REQ_REINICIO = 6;
    private final int CO_FAIL =         7;

    private static final String TAG_CONNECTED="CONECTADO";
    private static final String TAG_MONTO="MONTO";
    private static final String TAG_PROCESA="PROCESANDO";
    private static final String TAG_REC_STATE="ESTADO";
    private static final String TAG_RES_TRAN="RES_TRANSA";
    private static final String TAG_REQ_INFO="INFO";
    private static final String TAG_REQ_REINICIO="REINICIO";
    private static final String TAG_REQ_IMPRESION="IMPRE";

    private static final String TAG_FAIL="TAG_FAIL";

    private static final String TAG_ACK_CONEC="ACK_CONECTADO";
    private static final String TAG_ACK_MONTO="ACK_MONTO";
    private static final String TAG_ACK_PROCE="ACK_PROCESANDO";
    private static final String TAG_ACK_ESTADO="ACK_ESTADO";
    private static final String TAG_ACK_TRANS="ACK_RES_TRANSA";
    private static final String TAG_ACK_INFO="ACK_RES_INFO";
    private static final String TAG_ACK_IMPRE="ACK_RES_IMPRE";

    private void procesaTrama(String trama){
        String[] parcer = trama.split(":");
        if(parcer.length>1){
            if(parcer[0].equals(TAG_ACK_CONEC)) {
                stateConectionBt = STATE_CONNECTED;
                enviarMonto();
            }else if(parcer[0].equals(TAG_ACK_MONTO)) {
                sendProcesa(parcer[1]);
            }else if(parcer[0].equals(TAG_ACK_PROCE)) {
                checkConection();
                // SE MUESTRA QUE SE ESTA PROCESANDO LA TRANSACCION
            }else if(parcer[0].equals(TAG_ACK_INFO)) {
                if(countDownTimer != null)
                    countDownTimer.cancel();
                procesaInfo(trama);
            }else if(parcer[0].equals(TAG_ACK_ESTADO)) {
                procesaEstato(parcer[1]);
            }else if(parcer[0].equals(TAG_ACK_TRANS)) {
                transProcesa(parcer[1]);
            }else if(parcer[0].equals(TAG_REQ_REINICIO)) {

                // ESTADO DE LA ULTIMA TRASANCCION
            }else if(parcer[0].equals(TAG_ACK_IMPRE)) {
                String fa=TAG_REQ_REINICIO+":OK";
                sendReceive.write(fa.getBytes());
            }else if(parcer[0].equals(TAG_FAIL)) {
                // FALLA
            }else {
                // FALA
            }
        }else{
            String fa=TAG_FAIL+":OK";
            sendReceive.write(TAG_FAIL.getBytes());
        }
    }
    private static final int STATE_TR_WAIT =      1;
    private static final int STATE_TR_START =     2;
    private static final int STATE_TR_PROCCESS =  3;
    private static final int STATE_TR_FINISH =    4;
    private static final int STATE_TR_ERROR =     5;
    private void procesaEstato(String data){
        int value = 0;
        final Toast toast;
        Toast toast1;
        try {
            value = Integer.valueOf(data);
            switch (value){
                case STATE_TR_WAIT:
                    Cancel.setEnabled(true);
                    toast1 = Toast.makeText(this,"STATE_TR_WAIT",Toast.LENGTH_SHORT);
                    break;
                case STATE_TR_START:
                    Cancel.setEnabled(true);
                    toast1 = Toast.makeText(this,"STATE_TR_START",Toast.LENGTH_SHORT);
                    break;
                case STATE_TR_PROCCESS:
                    toast1 = Toast.makeText(this,R.string.sale_process,Toast.LENGTH_SHORT);
                    break;
                case STATE_TR_FINISH:
                    Cancel.setEnabled(true);
                    toast1 = Toast.makeText(this,"STATE_TR_FINISH",Toast.LENGTH_SHORT);
                    break;
                case STATE_TR_ERROR:
                    toast1 = Toast.makeText(this,"STATE_TR_ERROR",Toast.LENGTH_SHORT);
                    Cancel.setEnabled(true);
                    break;
                default:
                    toast1 = Toast.makeText(this,"default",Toast.LENGTH_SHORT);
                    break;
            }

        }catch (Exception e){
            toast1 = Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
        }

        toast = toast1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }

    private void procesaInfo(String trama){
        String[] parcer = trama.split(":");
        if(parcer[1].equals("FAIL")){
            Log.v("prosainfo","fail");
        }else{
            Log.v("prosainfo","ok");
            String[] data = parcer[1].split(",");
            numAprobacion = data[0];
            fecha = data[1];
            recibo = data[2];
            guardaCarrito();

        }
        resetDisconnectTimer(3000);

        enviariMPRESION();
        //String fa=TAG_REQ_REINICIO+":OK";
        //sendReceive.write(fa.getBytes());
    }

    private void transProcesa(String data){
        String res="";
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.app_item_zoomin);
        if(data.equals("OK")) {
            if(customDialog != null) {
                tv_message.setText(R.string.dialog_sale_retira);
                button.setVisibility(View.GONE);
                giDialogo.setVisibility(View.GONE);
                ima_check.setImageResource(R.drawable.ok
                );
                ima_check.setVisibility(View.VISIBLE);
                ima_check.startAnimation(animation);
                if(privadoApplication.getPreferences().getInt(SHA_IDIOMA,SHA_IDIOMA_INGLES) == SHA_IDIOMA_INGLES)
                    speck.speak("Successful transaction. Please pick up your products",TextToSpeech.QUEUE_FLUSH,null,null);
                else
                    speck.speak("Transaccion Aprobada. Por favor recoja sus productos",TextToSpeech.QUEUE_FLUSH,null,null);

            }else {
                showDialog2(R.string.dialog_sale_retira);
                if(privadoApplication.getPreferences().getInt(SHA_IDIOMA,SHA_IDIOMA_INGLES) == SHA_IDIOMA_INGLES)
                    speck.speak("Successful transaction. Please pick up your products",TextToSpeech.QUEUE_FLUSH,null,null);
                else
                    speck.speak("Transaccion Aprobada. Por favor recoja sus productos",TextToSpeech.QUEUE_FLUSH,null,null);
            }

            actualizaTabla(miCArrito);
            res = TAG_REQ_INFO + ":OK";
        }else{
            if(customDialog != null) {
                tv_message.setText(R.string.sale_fail);
                button.setVisibility(View.GONE);
                giDialogo.setVisibility(View.GONE);
                ima_check.setImageResource(R.drawable.fail);
                ima_check.setVisibility(View.VISIBLE);
                ima_check.startAnimation(animation);
            }else {
                showDialog2(R.string.sale_fail);


            }

            res = TAG_REQ_REINICIO + ":OK";
            resetDisconnectTimer(3000);
        }





        sendReceive.write(res.getBytes());
        intentos = 0;
    }

    private void  checkConection(){
        String dt = TAG_REC_STATE+":OK";
        countDownTimer = new CountDownTimer(TIMER_SALE,5000) {
            @Override
            public void onTick(long l) {
                sendReceive.write(dt.getBytes());
            }

            @Override
            public void onFinish() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkConection2();
                        if(privadoApplication.getPreferences().getInt(SHA_IDIOMA,SHA_IDIOMA_INGLES) == SHA_IDIOMA_INGLES)
                            speck.speak("Please. Complete the transaction at the payment terminal.",TextToSpeech.QUEUE_FLUSH,null,null);
                        else
                            speck.speak("Por favor. Finalice la transaccion en la terminal de pago",TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                });
            }
        }.start();
    }

    private void  checkConection2(){
        String dt = TAG_REC_STATE+":OK";
        countDownTimer = new CountDownTimer(30000,5000) {
            @Override
            public void onTick(long l) {
                sendReceive.write(dt.getBytes());
            }

            @Override
            public void onFinish() {
                Cancel.setEnabled(true);
                numAprobacion = "NO CONF";
                guardaCarrito();
                resetDisconnectTimer(5000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SaleBtActivity.this,"TIEMPO COMPLETADO PARA LA TRANSACCION",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    private void actualizaTabla(ArrayList<DataProduct> products){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        for(DataProduct product: miCArrito){
            ArrayList<Integer> numItems = adminBaseDatos.getProductoTabla(product);
            int numART = numItems.get(0);
            int numVen = numItems.get(1);
            int numCompra = Integer.valueOf(product.dt_num_articulos);
            int disponible = numART - numCompra;
            int vendidos = numVen + numCompra;
            product.setDt_num_articulos(String.valueOf(disponible));
            product.setDt_num_vendidos(String.valueOf(vendidos));
           if(adminBaseDatos.updateProducto(product)){
               Log.d("DP_DLOG","actualizaTabla "+"OK");
            }else{
               Log.d("DP_DLOG","actualizaTabla "+"FAIL");
            }
        }
        adminBaseDatos.closeBaseDtos();
    }
    private void guardaCarrito(){
        if(isGuardaCarrito) {
            AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date resultdate = new Date(System.currentTimeMillis());
            fecha_tab = sdf.format(resultdate);

            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            hora_tab = sdf2.format(resultdate);

            for (DataProduct product : miCArritoVentas) {
                long val = adminBaseDatos.insertVentas(numAprobacion, fecha, product.dt_id_producto, product.dt_nombre_es, product.dt_precio, product.dt_num_articulos, fecha_tab, hora_tab, product.dt_type_product, recibo);
                Log.d("DP_DLOG","guardaCarrito "+val);
            }
            adminBaseDatos.closeBaseDtos();
            isGuardaCarrito = false;
        }
    }

    private void sendProcesa(String data){
        if(intentos < 3){
            if(data.equals("OK")){
                intentos = 0;
                //showDialog();
                sendReceive.write((TAG_PROCESA+":OK").getBytes());
            }else{
                intentos++;
            }
        }else{
            sendReceive.write((TAG_REQ_REINICIO+":OK").getBytes());
            intentos = 0;
        }
    }

    private void enviarConectado(){
        if(sendReceive != null){
            String trama = TAG_CONNECTED+":OK";
            sendReceive.write(trama.getBytes());
        }
    }


    private void enviarMonto(){
        if(sendReceive != null){
            String trama = TAG_MONTO+":"+String.valueOf(valorTotal);
            sendReceive.write(trama.getBytes());
        }
    }

    private void enviariMPRESION(){
        if(sendReceive != null){
            NitEmpresa = "901275377-1";
            String DATA="";

            DATA += "420002  \n";
            DATA += "320001Fecha "+fecha_tab+" Hora "+hora_tab+"\n";
            DATA += "420002  \n";
            DATA += "420001NIT:"+NitEmpresa+"\n";
            DATA += "420002Num. Aprobacion:  "+numAprobacion+"\n";
            DATA += "420002Num. Recibo:  "+String.format("%06d",Integer.valueOf(recibo))+"\n";
            DATA += "420002  \n";
            DATA += "320002Cant Descripcion     Vr/Total\n";
            DATA += "320002-----------------------------\n";
            for(DataProduct prod:miCArritoVentas){
                String tm = "320002";
                int cantidad = Integer.valueOf(prod.dt_num_articulos);
                int precioUni = Integer.valueOf(prod.dt_precio.replace("$","").replace(".",""));
                tm+=String.format("%03d",cantidad);
                tm+="  ";
                if(prod.dt_nombre_es.length() >13)
                    tm+= prod.dt_nombre_es.substring(0,14).replace("ñ","n");
                else
                    tm+= String.format("%-14s", prod.dt_nombre_es).replace("ñ","n");

                tm+= String.format("%10s", currencyFormatter.format(Long.valueOf(cantidad*precioUni)));

                DATA+=tm+"\n";

            }
            DATA += "320102T0TAL"+String.format("%24s",currencyFormatter.format(Long.valueOf(valorTotal)))+"\n";
            DATA += "420002  \n";
            DATA += "420002Telefono: (+57) 310 251 3695\n";
            DATA += "420002Direccion: Calle de la soledad 5-9\n";
            DATA += "420002Cartagena Bolivar";
            String trama = TAG_REQ_IMPRESION+":"+DATA.replace(":","-");
            sendReceive.write(trama.getBytes());
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    //tv_state.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    //tv_state.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    //tv_state.setText("Connected");
                    enviarConectado();
                    break;
                case STATE_CONNECTION_FAILED:
                    //tv_state.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    //tv_state.setText("Receive");
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    procesaTrama(tempMsg);
                    break;
                case STATE_MESSAGE_RECV:
                    terminarBt();
                    //Toast.makeText(SaleBtActivity.this,"Error en la conexion con el dispositivo",Toast.LENGTH_LONG).show();
                    break;

            }
            return true;
        }
    });

    /////////////////////////////////////////////////

    private void finalizar(){
        terminarBt();
        if(customDialog != null)
            customDialog.dismiss();
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        this.finish();
    }

    private static Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.v("TIMEOUT", "DECONEXION 2 ");
            return true;
        }
    });



    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            Log.v("TIMEOUT", "DECONEXION");
            finalizar();
        }
    };

    public void resetDisconnectTimer(long timer){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, timer);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        if(tr) {
            Log.v("TIMEOUT", "YO");

            resetDisconnectTimer(DISCONNECT_TIMEOUT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer(DISCONNECT_TIMEOUT);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
    /////////////////////////////////////////////////

}