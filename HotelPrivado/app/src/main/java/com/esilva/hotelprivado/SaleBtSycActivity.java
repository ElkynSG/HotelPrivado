package com.esilva.hotelprivado;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.esilva.hotelprivado.Util.Constantes.SHA_BASE;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_INGLES;
import static com.esilva.hotelprivado.Util.Constantes.SHA_ID_TRANS;
import static com.esilva.hotelprivado.Util.Protocolo.*;

import com.esilva.hotelprivado.application.privadoApplication;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;
import com.esilva.hotelprivado.db.DataVentas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;

public class SaleBtSycActivity extends AppCompatActivity {

    /******  Bluetooth  ******/
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private int REQUEST_ENABLE_BLUETOOTH_2 = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    /************************/
    /********  View  ********/
    private TextView tv_total;
    private Button bt_pay,Cancel;
    private static Dialog customDialog;
    private TextView tvMessageDialog;
    private TextView tvProgresoDialog;
    private Button btButtonDialog;
    private GifImageView gifDialogo;
    private ImageView imaDialog;

    /************************/

    private int id_trans;
    private int state_current;
    private int tipoIdioma;
    private SharedPreferences preferences;
    private NumberFormat currencyFormatter;
    private TextToSpeech speck;

    private String numAprobacion;
    private String recibo;
    private String fecha_tab;
    private String hora_tab;
    private int TotalMonto;
    private ArrayList<DataProduct>  miCArrito;
    private ArrayList<DataProduct>  miCarritoTemp;

    private int messageNotiError;
    private boolean isCompraExitosa;


    private void setView(){
        tv_total = findViewById(R.id.tv_total);
        Cancel= findViewById(R.id.btSaleCancelar);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cancel.setEnabled(false);
                bt_pay.setEnabled(false);
                process_estado_8();
            }
        });
        bt_pay = findViewById(R.id.btSalePagar);
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cancel.setEnabled(false);
                bt_pay.setEnabled(false);

                id_trans = privadoApplication.getPreferences().getInt(SHA_ID_TRANS,0);

                privadoApplication.setSecuenciaTr(id_trans);
                notifyState(ESTADO_1);

                if(privadoApplication.getPreferences().getInt(SHA_IDIOMA,SHA_IDIOMA_INGLES) == SHA_IDIOMA_INGLES)
                    speck.speak("Please follow the instructions at the payment terminal.",TextToSpeech.QUEUE_FLUSH,null,null);
                else
                    speck.speak("Por favor siga las instrucciones en la terminal de pago.",TextToSpeech.QUEUE_FLUSH,null,null);

            }
        });

        crearDialog();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sale);

        setView();

        inputStream = null;
        outputStream = null;

        preferences = getSharedPreferences(SHA_BASE,MODE_PRIVATE);
        tipoIdioma = preferences.getInt(SHA_IDIOMA,0);

        state_current = ESTADO_1;
        isCompraExitosa = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || privadoApplication.getSdkPermision()==0) {
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH_2);
                return;
            }
        }

        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;

        Bundle bundle = getIntent().getExtras().getBundle("monto");
        ArrayList<String> obj;
        obj = bundle.getStringArrayList("carrito");
        miCArrito = new ArrayList<>();
        miCarritoTemp = new ArrayList<>();
        //miCArritoVentas = new ArrayList<>();
        for(String tmp:obj){
            DataProduct dataProduct = new DataProduct();
            DataProduct dataProductVenta = new DataProduct();
            dataProduct = dataProduct.DesSerializar(tmp);
            dataProductVenta = dataProductVenta.DesSerializar(tmp);
            miCArrito.add(dataProduct);
            miCarritoTemp.add(dataProductVenta);
            TotalMonto += Integer.valueOf(dataProduct.dt_precio.replace("$","").replace(".",""))*Integer.valueOf(dataProduct.dt_num_articulos);
        }

        //valorTotal = getIntent().getExtras().getInt("monto");
        String ValorTotal = currencyFormatter.format(Long.valueOf(TotalMonto));

        tv_total.setText(ValorTotal);
        //borrarPen();
        speck=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    if(tipoIdioma == SHA_IDIOMA_INGLES)
                        speck.setLanguage(new Locale("en", "rUS"));
                    else
                        speck.setLanguage(new Locale("es", "col"));
                }
            }
        });
    }

    private void notifyState(final int state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state){
                    case ESTADO_1:
                        process_estado_1();
                        break;
                    case ESTADO_2:
                        process_estado_2();
                        break;
                    case ESTADO_3:
                        process_estado_3();
                        break;
                    case ESTADO_4:
                        process_estado_4();
                        break;
                    case ESTADO_5:
                        process_estado_5();
                        break;
                    case ESTADO_6:
                        process_estado_6();
                        break;
                    case ESTADO_7:
                        process_estado_7();
                        break;
                    case ESTADO_8:
                        process_estado_8();
                        break;
                    default:
                        process_estado_8();
                        break;

                }
            }
        });
    }

    //todo*********************************************  ESTADO 1 *********************************************/
    //todo*******************************************  CONECTANDO *********************************************/
    private void process_estado_1(){
        showDialogParam(R.string.instruccion,R.string.conectandoBt,false,R.drawable.ok,true,false);
        //logVentas("VENTAS INI");
        device = getDevice();
        if(device != null){
            conectarBt();
        }else{
            messageNotiError = R.string.persmisoBt;
            //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Dispositivo NO Encontrado";
            //messageNotiError[SHA_IDIOMA_INGLES-1]="Device NOT Found";
            notifyState(ESTADO_7);
        }
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
    private void conectarBt(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || privadoApplication.getSdkPermision()==0) {
                        socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        socket.connect();
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        notifyState(ESTADO_2);
                    }else {
                        messageNotiError = R.string.persmisoBt;
                        //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Sin permisos de Bluetooth";
                        //messageNotiError[SHA_IDIOMA_INGLES-1]="No Bluetooth permissions";
                        notifyState(ESTADO_7);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    messageNotiError = R.string.errorConectandoBt;
                    //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Conectando bluetooth: "+e.getMessage();
                    //messageNotiError[SHA_IDIOMA_INGLES-1]="Connecting bluetooth: "+e.getMessage();
                    notifyState(ESTADO_7);
                }

            }
        }).start();
    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 2 ************************************************/
    //todo*****************************************  CHECK CONEXION *********************************************/
    private void process_estado_2(){
        tvProgresoDialog.setText(R.string.sync);
        new Thread(new Runnable() {
            @Override
            public void run() {

                int state = ESTADO_7;

                for(int i=0;i<2;i++){
                    if(writeBt(TA_CON)){
                        String data = readBt(5000);
                        if(data != null){
                            if(data.contains(ACK)) {
                                state = ESTADO_3;
                                break;
                            }else
                                continue;
                        }else
                            continue;
                    }else{
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(state == ESTADO_7){
                    messageNotiError =R.string.errorCheckConexion;
                    //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Error conectando con el dispositivo";
                    //messageNotiError[SHA_IDIOMA_INGLES-1]="Error connecting to device";
                }
                notifyState(state);
            }
        }).start();
    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 3 *********************************************/
    //todo**********************************************  SYNC ***********************************************/
    private void process_estado_3(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                int state = ESTADO_7;

                for(int i=0;i<2;i++){
                    if(writeBt(TA_SYN)){
                        String data = readBt(3000);
                        if(data != null){
                            if(data.contains(PO_RES)) {
                                if(actualizaDataTr(data)){
                                    state = ESTADO_4;
                                    break;
                                }else{
                                    continue;
                                }
                            }else if(data.contains(ACK)){
                                state = ESTADO_4;
                                borrarVentasTemp();
                                break;
                            }else{
                                continue;
                            }
                        }else {
                            continue;
                        }
                    }
                }

                if(state == ESTADO_7){
                    messageNotiError = R.string.errorSincronizando;
                    //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Error Sincronizando";
                    //messageNotiError[SHA_IDIOMA_INGLES-1]="Error Synchronizing";
                }
                notifyState(state);
            }
        }).start();
    }
    private void borrarVentasTemp(){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        adminBaseDatos.deletePendientes();
        adminBaseDatos.closeBaseDtos();
    }
    private boolean actualizaDataTr(String data){
        String prt = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
        // Obtener los valores
        try {
            AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
            JSONObject jsonObject = new JSONObject(prt);
            String codRes = jsonObject.getString(JSON_COD_RES);
            int id_tr = Integer.valueOf(jsonObject.getString(JSON_ID_TR));
            if(!codRes.equals("00")){
                adminBaseDatos.deleteIdTransacPen(id_tr);
                adminBaseDatos.closeBaseDtos();
                writeBt(ACK_TR);
                return true;
            }
            String numAproba = jsonObject.getString(JSON_NUM_CONFIR);
            String fecha = jsonObject.getString(JSON_NUM_FECHA);
            String recibo = jsonObject.getString(JSON_RECIBO);


            List<DataVentas> allIdTransac = adminBaseDatos.getAllIdTransacPen(id_tr);
            if(allIdTransac == null ){
                adminBaseDatos.closeBaseDtos();
                writeBt(ACK_TR);
                return true;
            }

            if(allIdTransac.size()<1){
                adminBaseDatos.closeBaseDtos();
                writeBt(ACK_TR);
                return true;
            }

            for (int i = 0;i<allIdTransac.size();i++) {
                allIdTransac.get(i).setRepAproba(numAproba);
                allIdTransac.get(i).setRepFecha(fecha);
                allIdTransac.get(i).setRecibo(recibo);
            }

            adminBaseDatos.insertVentasData(allIdTransac);
            actualizaTablaSync(allIdTransac);
            adminBaseDatos.deleteIdTransacPen(id_tr);
            adminBaseDatos.closeBaseDtos();
            writeBt(ACK_TR);
            //logPendiente("check 4");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void actualizaTablaSync(List<DataVentas> carro){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        for(DataVentas product: carro){
            DataProduct numItems = adminBaseDatos.getProductoTabla(product);
            int numART = Integer.valueOf(numItems.dt_num_articulos);
            int numVen = Integer.valueOf(numItems.dt_num_vendidos);
            int numCompra = Integer.valueOf(product.getRepCantidad());
            int disponible = numART - numCompra;
            int vendidos = numVen + numCompra;
            numItems.setDt_num_articulos(String.valueOf(disponible));
            numItems.setDt_num_vendidos(String.valueOf(vendidos));
            if(adminBaseDatos.updateProducto(numItems)){
                Log.d("DP_DLOG","actualizaTabla "+"OK");
            }else{
                Log.d("DP_DLOG","actualizaTabla "+"FAIL");
            }
        }
        adminBaseDatos.closeBaseDtos();
    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 4 *********************************************/
    //todo**********************************************  MONTO ***********************************************/
    private void process_estado_4(){
        tvProgresoDialog.setText(R.string.notificaCompra);
        Date resultdate = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fecha_tab = sdf.format(resultdate);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String hora_tab = sdf2.format(resultdate);

        logPendiente("monto 1");

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        adminBaseDatos.insertAllVentasPend(miCArrito,null,null,fecha_tab,hora_tab,null,id_trans);
        adminBaseDatos.closeBaseDtos();
        logPendiente("monto 2");

        new Thread(new Runnable() {
            @Override
            public void run() {
                int state = ESTADO_7;
                for(int i=0;i<2;i++){
                    if(writeBt(TA_MON+":"+String.valueOf(TotalMonto)+"\n"+TA_IDT+":"+String.valueOf(id_trans))){
                        String data = readBt(5000);
                        if(data != null){
                            if(data.contains(ACK)) {
                                state = ESTADO_5;
                                break;
                            }else if(data.contains(PRO)){
                                state = ESTADO_5;
                                break;
                            }else
                                continue;
                        }else
                            continue;
                    }
                }
                if(state == ESTADO_7){
                    messageNotiError = R.string.errorCompra;
                    //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Error notificando compra";
                    //messageNotiError[SHA_IDIOMA_INGLES-1]="Error notifying purchase";
                }
                notifyState(state);
            }
        }).start();
    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 5 *********************************************/
    //todo**********************************************  CHECK ***********************************************/
    private void process_estado_5(){
        tvProgresoDialog.setText(R.string.verificandoCompra);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean issms = false;
                int state = ESTADO_7;
                long startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) < 120000){
                    showConected();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (writeBt(TA_RES)) {
                        String data = readBt(1000);
                        if (data != null) {
                            if (data.contains(PO_RES)) {
                                logPendiente("check 1");
                                if (procesaRes(data)) {
                                    state = ESTADO_6;
                                    isCompraExitosa = true;
                                    messageNotiError = R.string.transacExitosa;
                                    //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Transaccion Exitosa";
                                    //messageNotiError[SHA_IDIOMA_INGLES-1]="Successful Transaction";
                                    break;
                                }else {
                                    writeBt(NAK);
                                }
                            }else if(data.contains(PRO)){
                                continue;
                            }
                            else if(data.contains(FAIL)){
                                procesaResFail();
                                messageNotiError = R.string.transacFallida;
                                //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Transacción fallida";
                                //messageNotiError[SHA_IDIOMA_INGLES-1]="Failed transaction";
                                issms = true;
                                break;
                            }
                        }
                    }
                }
                if(state == ESTADO_7 && issms==false){
                    messageNotiError = R.string.errorEsperando;
                    //messageNotiError[SHA_IDIOMA_ESPANOL-1]="Error: Esperando respuesta";
                    //messageNotiError[SHA_IDIOMA_INGLES-1]="Error: Waiting for response";
                }
                notifyState(state);
            }
        }).start();

    }
    private void showConected(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msm = tipoIdioma == SHA_IDIOMA_INGLES?"Waiting for POS response":"Esperando respuesta del POS";
                Toast.makeText(SaleBtSycActivity.this,msm,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean procesaRes(String data){
        String prt = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date resultdate = new Date(System.currentTimeMillis());
        fecha_tab = sdf.format(resultdate);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        hora_tab = sdf2.format(resultdate);
        // Obtener los valores
        try {
            JSONObject jsonObject = new JSONObject(prt);
            String codRes = jsonObject.getString(JSON_COD_RES);
            numAprobacion = jsonObject.getString(JSON_NUM_CONFIR);
            String fecha = jsonObject.getString(JSON_NUM_FECHA);
            recibo = jsonObject.getString(JSON_RECIBO);
            AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
            List<DataVentas> allIdTransac = adminBaseDatos.getAllIdTransacPen(id_trans);
            for (int i = 0;i<allIdTransac.size();i++) {
                allIdTransac.get(i).setRepAproba(numAprobacion);
                allIdTransac.get(i).setRepFecha(fecha);
                allIdTransac.get(i).setRecibo(recibo);
                allIdTransac.get(i).setRepHoraTab(hora_tab);
                allIdTransac.get(i).setRepFechaTab(fecha_tab);
            }
            adminBaseDatos.insertVentasData(allIdTransac);
            adminBaseDatos.deleteIdTransacPen(id_trans);
            adminBaseDatos.closeBaseDtos();
            actualizaTabla(miCArrito);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        writeBt(ACK_TR);
        return true;
    }

    private void actualizaTabla(ArrayList<DataProduct> carro){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        for(DataProduct product: carro){
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
    private boolean procesaResFail(){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        adminBaseDatos.deleteIdTransacPen(id_trans);
        adminBaseDatos.closeBaseDtos();
        writeBt(ACK_TR);
        return true;
    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 6 *********************************************/
    //todo*********************************************  IMPRESION ***********************************************/
    private void process_estado_6(){
        tvProgresoDialog.setText(R.string.imprimiedo);
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeBt(enviariMPRESION());
                readBt(5000);
                notifyState(ESTADO_7);
            }
        }).start();
    }
    private String enviariMPRESION(){
        String DATA="";
        String strNoti="";

        try {
            DATA += "420002  \n";
            DATA += "320001Fecha "+fecha_tab+" Hora "+hora_tab+"\n";
            DATA += "420002  \n";
            DATA += "420001NIT:901275377-1"+"\n";
            DATA += "420002Num. Aprobacion:  "+numAprobacion+"\n";
            DATA += "420002Num. Recibo:  "+String.format("%06d",Integer.valueOf(recibo))+"\n";
            DATA += "420002  \n";
            DATA += "320002Cant Descripcion     Vr/Total\n";
            DATA += "320002-----------------------------\n";
            for(DataProduct prod:miCarritoTemp){
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
            DATA += "320102T0TAL"+String.format("%24s",currencyFormatter.format(Long.valueOf(TotalMonto)))+"\n";
            DATA += "420002  \n";
            DATA += "420002Telefono: (+57) 310 251 3695\n";
            DATA += "420002Direccion: Calle de la soledad 5-9\n";
            DATA += "420002Cartagena Bolivar";
            strNoti = TA_VOU+":"+DATA.replace(":","-");
        }catch (Exception e){
            strNoti = TA_VOU;
        }


        return strNoti;
    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 7 *********************************************/
    private void process_estado_7(){
        if(isCompraExitosa){
            speck.speak(getString(R.string.instrucc_recoja), TextToSpeech.QUEUE_FLUSH,null,null);
            showDialogParam(R.string.instrucc_recoja,R.string.transacExitosa,true,R.drawable.ok,false,false);
        }else {
            speck.speak(getString(R.string.instrucc_fallida), TextToSpeech.QUEUE_FLUSH,null,null);
            if(getString(messageNotiError).contains("Error")){
                tvProgresoDialog.setTextColor(getResources().getColor(R.color.red));
            }
            showDialogParam(R.string.instrucc_fallida,messageNotiError,true,R.drawable.fail,false,false);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notifyState(ESTADO_8);
            }
        }).start();


    }
    /*****************************************************************************************************/


    //todo*********************************************  ESTADO 8 *********************************************/
    private void process_estado_8(){
        //logVentas("VENTAS FIN");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<2;i++){
                    if(writeBt(TA_FIN)){
                        String data = readBt(5000);
                        if(data != null){
                            if(data.contains(ACK)) {
                                break;
                            }else
                                continue;
                        }else
                            continue;
                    }
                }
                terminarBt();
                finSale();
                //notifyState(state);
            }
        }).start();

    }
    private void terminarBt()  {
        if(customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        try {

            if(socket != null && socket.isConnected()) {
                outputStream.close();
                inputStream.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void finSale(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
    /*****************************************************************************************************/

/*==================================================================================================================*/
/*==================================================================================================================*/
/*==================================================================================================================*/

    private void crearDialog(){
        Log.d("DP_DLOG","crearDialog");
        if(customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }

        customDialog = new Dialog(SaleBtSycActivity.this,R.style.popup_dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.pago_dialog_sync);

        tvMessageDialog = customDialog.findViewById(R.id.tv_mensaje);
        tvProgresoDialog = customDialog.findViewById(R.id.tv_progreso);
        btButtonDialog = customDialog.findViewById(R.id.btok);
        gifDialogo = customDialog.findViewById(R.id.gifres);
        imaDialog = customDialog.findViewById(R.id.im_estado);
        btButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
    }

    private void showDialogParam(int titulo, int progreso, boolean isImage, int image, boolean isGif, boolean isButton){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessageDialog.setText(titulo);
                tvProgresoDialog.setText(progreso);

                if(isGif){
                    gifDialogo.setVisibility(View.VISIBLE);
                }else{
                    gifDialogo.setVisibility(View.GONE);
                }

                if(isButton){
                    btButtonDialog.setVisibility(View.VISIBLE);
                }else{
                    btButtonDialog.setVisibility(View.GONE);
                }

                if(isImage){
                    imaDialog.setImageResource(image);
                    imaDialog.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(SaleBtSycActivity.this,R.anim.app_item_zoomin);
                    imaDialog.startAnimation(animation);
                }else{
                    imaDialog.setVisibility(View.GONE);
                }

                if(!customDialog.isShowing()){
                    customDialog.show();
                    customDialog.getWindow().setLayout(1000, 800);
                }

            }
        });
    }

    public boolean writeBt(String data)
    {
        byte[] bytes = data.getBytes();

        try {
            if(outputStream != null) {
                outputStream.write(bytes);
            }else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String readBt(int timeout){
        int len;
        String tempMsg = null;
        byte[] buffer=new byte[1024];
        long startTime;
        startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeout){
            try {
                if(inputStream.available() > 0){
                    len=inputStream.read(buffer);
                    tempMsg = new String(buffer, 0, len);
                    break;
                }else {
                    Thread.sleep(50);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return tempMsg;
    }

    private void logPendiente(String pos){
        Log.d("PENDINTES","-------------   "+pos+"   ----------------");
        Log.d("PENDINTES","--------   ID TRANSAC["+id_trans+"]   ----------");
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        List<DataVentas> allPen = adminBaseDatos.getAllPen();
        adminBaseDatos.closeBaseDtos();
        if(allPen == null) {
            Log.d("PENDINTES", "SIN DATOS");
            return;
        }

        for (DataVentas ven : allPen) {
            Log.d("PENDINTES","ID_TR:"+ven.getIdTr()+" APR:"+ven.getRepAproba()+" REC:"+ven.getRecibo()+" total:"+ven.getRepTotal()+" fecha:"+ven.getRepFecha()+" Nombre:"+ven.getRepNomProd());
        }
        Log.d("PENDINTES","--------------- FIN -----------------");
    }

    private void logVentas(String pos){
        Log.d("PENDINTES","-------------   "+pos+"   ----------------");
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        List<DataVentas> allPen = adminBaseDatos.getAllVentas(true,true,true,true);
        adminBaseDatos.closeBaseDtos();
        if(allPen == null) {
            Log.d("PENDINTES", "SIN DATOS");
                return;
        }

        for (DataVentas ven : allPen) {
            Log.d("PENDINTES","APR:"+ven.getRepAproba()+" REC:"+ven.getRecibo()+" total:"+ven.getRepTotal()+" fecha:"+ven.getRepFecha()+" Nombre:"+ven.getRepNomProd());
        }
        Log.d("PENDINTES","--------------- FIN -----------------");
    }

    @Override
    public void onBackPressed() {

    }
}