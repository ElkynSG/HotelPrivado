package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.CM_ACK;
import static com.esilva.hotelprivado.Util.Constantes.CM_FAIL;
import static com.esilva.hotelprivado.Util.Constantes.CM_PRINTER;
import static com.esilva.hotelprivado.Util.Constantes.CM_SALE;

import static com.esilva.hotelprivado.Util.Constantes.CM_SALE_REQ;
import static com.esilva.hotelprivado.Util.Constantes.DISCONNECT_TIMEOUT;
import static com.esilva.hotelprivado.Util.Constantes.INTENT_ACTION_GRANT_USB;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;
import static com.esilva.hotelprivado.Util.Constantes.WRITE_WAIT_MILLIS;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.esilva.hotelprivado.Util.Constantes.*;
import com.esilva.hotelprivado.Util.CustomProber;
import com.esilva.hotelprivado.application.privadoApplication;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SaleUSBActivity extends AppCompatActivity implements View.OnClickListener, SerialInputOutputManager.Listener {
    private NumberFormat currencyFormatter;
    private TextView tv_total;
    private Button pay, Cancel;

    int valorTotal;
    private String numAprobacion;
    private String fecha;
    private String recibo;
    private String fecha_tab;
    private String hora_tab;
    private String NitEmpresa;

    ArrayList<DataProduct> miCArrito;
    ArrayList<DataProduct> miCArritoVentas;
    Boolean tr = true;

    CountDownTimer countDownTimer;

    private USB_Permission usbPermission = USB_Permission.Unknown;

    private String USB_name;
    private int USB_deviceID;
    private int USB_port;
    private Boolean USB_connected = false;
    private UsbSerialPort usbSerialPort;

    private Boolean isGuardaCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sale_bt);

        setViewInit();

        //preferences = privadoApplication.getPreferences();
        //tipoIdioma = preferences.getInt(SHA_IDIOMA,0);

        USB_deviceID = privadoApplication.getUSB_deviceID();
        USB_port = privadoApplication.getUSB_port();
        USB_name = privadoApplication.getUSB_deviceName();



        // obtener valor total con formato
        {
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;

        Bundle bundle = getIntent().getExtras().getBundle("monto");
        ArrayList<String> obj;
        obj = bundle.getStringArrayList("carrito");
        miCArrito = new ArrayList<>();
        miCArritoVentas = new ArrayList<>();
        for (String tmp : obj) {
            DataProduct dataProduct = new DataProduct();
            DataProduct dataProductVenta = new DataProduct();
            dataProduct = dataProduct.DesSerializar(tmp);
            dataProductVenta = dataProductVenta.DesSerializar(tmp);
            miCArrito.add(dataProduct);
            miCArritoVentas.add(dataProductVenta);
            valorTotal += Integer.valueOf(dataProduct.dt_precio.replace("$", "").replace(".", "")) * Integer.valueOf(dataProduct.dt_num_articulos);
        }

        String ValorTotal = currencyFormatter.format(Long.valueOf(valorTotal));

        tv_total.setText(ValorTotal);
        }
        USB_Connect();
        isGuardaCarrito = true;
    }

    private void processSale(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dataRecv = null;
                String dataSend = null;
                int iRet = 0;
                read(1);
                dataSend = CM_SALE+":"+valorTotal;
                for (int i=0;i<2;i++){
                    iRet = write(dataSend);
                    if(iRet != 0)
                        return;
                    dataRecv = read(10);
                    if(dataRecv == null)
                        return;

                    iRet = ProcesaSale(dataRecv);
                    if(iRet == 1)
                        continue;
                    if(iRet != 0)
                        return;
                    break;
                }

                dataRecv = read(120);
                if(dataRecv == null)
                    return;

                iRet = ProcesaSale(dataRecv);
                if(iRet != 0)
                    dataSend = CM_SALE_REQ+":"+CM_FAIL;
                else
                    dataSend = CM_SALE_REQ+":"+CM_ACK;
                write(dataSend);
            }
        }).start();
    }

    private String BuildPrinter(){
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

        return DATA;
    }
    private void processPrinter(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dataRecv = null;
                String dataSend = null;
                int iRet = 0;
                read(1);
                dataSend = CM_PRINTER+":"+BuildPrinter();
                for (int i=0;i<2;i++){
                    iRet = write(dataSend);
                    if(iRet != 0)
                        return;
                    dataRecv = read(10);
                    if(dataRecv == null)
                        return;

                    iRet = ProcesaSale(dataRecv);
                    if(iRet == 1)
                        continue;
                    if(iRet != 0)
                        return;
                    break;
                }
            }
        }).start();
    }

    private int ProcesaSale(String trama){
        int iRet = -1;
        String[] parcer = trama.split(":");
        if(parcer.length>1){
            if(parcer[0].equals(CM_SALE)) {
                if(parcer[1].equals(CM_ACK))
                    return 0;
                else
                    return 1;
            }else if(parcer[0].equals(CM_SALE_REQ)){
                Log.v("prosainfo","ok");
                String[] data = parcer[1].split(",");
                if(data[0].equals(CM_ACK)){
                    numAprobacion = data[1];
                    fecha = data[2];
                    recibo = data[3];
                    guardaCarrito();
                    iRet = 0;
                }
            }
        }
        return iRet;
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
                //adminBaseDatos.insertInventario(numAprobacion, fecha, product.dt_id_producto, product.dt_nombre_es, product.dt_precio, product.dt_num_articulos, fecha_tab, hora_tab, product.dt_type_product, recibo);
                adminBaseDatos.insertVentas(numAprobacion, fecha, product.dt_id_producto, product.dt_nombre_es, product.dt_precio, product.dt_num_articulos, fecha_tab, hora_tab, product.dt_type_product, recibo,product.dt_id_producto);
            }
            adminBaseDatos.closeBaseDtos();
            isGuardaCarrito = false;
        }
    }

    private void USB_Connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UsbDevice device = null;
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                for(UsbDevice v : usbManager.getDeviceList().values()) {
                    if (v.getProductName().equals(USB_name))
                        device = v;
                }
                if(device == null) {
                    Log.v("USB","connection failed: device not found");
                    return;
                }
                UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
                if(driver == null) {
                    driver = CustomProber.getCustomProber().probeDevice(device);
                }
                if(driver == null) {
                    Log.v("USB","connection failed: no driver for device");
                    return;
                }
                if(driver.getPorts().size() < USB_port) {
                    Log.v("USB","connection failed: not enough ports at device");
                    return;
                }
                usbSerialPort = driver.getPorts().get(USB_port);
                UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
                if(usbConnection == null && usbPermission == USB_Permission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
                    usbPermission = USB_Permission.Requested;
                    int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
                    PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(SaleUSBActivity.this, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
                    usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
                    return;
                }
                if(usbConnection == null) {
                    if (!usbManager.hasPermission(driver.getDevice()))
                        Log.v("USB","connection failed: permission denied");
                    else
                        Log.v("USB","connection failed: open failed");

                    return;
                }

                try {
                    if(!usbSerialPort.isOpen())
                        usbSerialPort.open(usbConnection);
                    usbSerialPort.setParameters(9600, 8, 1, UsbSerialPort.PARITY_NONE);

                    Log.v("USB","connected");
                    USB_connected = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pay.setEnabled(true);
                            Cancel.setEnabled(false);
                        }
                    });

                } catch (Exception e) {
                    Log.v("USB","connection failed: " + e.getMessage());
                    try {
                        usbSerialPort.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int  write(String dataSend){
        int iRet;
        if(!USB_connected) {
            Toast.makeText(this, "NO Conectado", Toast.LENGTH_SHORT).show();
            return -1;
        }
        try {
            byte[] data = dataSend.getBytes();
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
            iRet = 0;
        } catch (Exception e) {
            Toast.makeText(this, "Error enviando "+e.getMessage(), Toast.LENGTH_SHORT).show();
            onRunError(e);
            iRet = -1;
        }
        return iRet;
    }

    private String read(int timer) {
        String data=null;
        if(!USB_connected) {
            return data;
        }
        try {
            byte[] buffer = new byte[8192];
            int len = usbSerialPort.read(buffer, timer*1000);
            return new String(Arrays.copyOf(buffer, len));
        } catch (Exception e) {
            onRunError(e);
        }
        return data;
    }

    private void desconnect(){
        try {
            usbSerialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btSalePagar:
                pay.setEnabled(false);
                stopDisconnectTimer();
                processSale();
                break;
            case R.id.btSaleCancelar:
                //read(30);
                finalizar();
                break;
            default:
                break;
        }
    }















    private void checkConexionTerminal(){
        CountDownTimer t = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v("Conectando","check conexion");
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }



/////////////////////////////   timer de pantalla  //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
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

    private void finalizar(){
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        desconnect();
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setViewInit(){
        tv_total = findViewById(R.id.tv_total);
        pay = findViewById(R.id.btSalePagar);
        pay.setOnClickListener(this);
        pay.setEnabled(false);

        Cancel= findViewById(R.id.btSaleCancelar);
        Cancel.setOnClickListener(this);
        Cancel.setEnabled(true);
    }

    @Override
    public void onNewData(byte[] data) {

    }

    @Override
    public void onRunError(Exception e) {

    }

}