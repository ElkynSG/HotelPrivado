package com.esilva.a910private;

import static com.esilva.a910private.Constants.*;
import static com.pax.dal.IComm.EConnectStatus.CONNECTED;
import static com.pax.dal.IComm.EConnectStatus.DISCONNECTED;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pax.dal.IComm;
import com.pax.dal.entity.EUartPort;
import com.pax.dal.entity.UartParam;
import com.pax.dal.exceptions.CommException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class UsbActivity extends AppCompatActivity {
    private IComm serial;
    private int Amount;
    private int stateTr;
    private String numAprobacion;
    private String recibo;
    private String FechaTr;
    // test de commit para prueba
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
        getComm();
        connect();
        serviceRead();
    }

    private void flow(String trama){
        String[] parcer = trama.split(":");
        if(parcer.length>1){
            if(parcer[0].equals(CM_SALE)){
                flowSale(parcer[1]);
            }else if(parcer[0].equals(CM_PRINTER)){

            }else{

            }
        }

    }

    private int flowSale(String trama){
        try {
            String[] trPro = trama.split(":");
            Amount = Integer.valueOf(trPro[1]);
            String ackResult;
            if(instanceComercios(getJson()))
                ackResult = CM_SALE+":"+CM_ACK;
            else {
                ackResult = CM_SALE + ":" + CM_FAIL;
                serviceReadUI();
            }
            sendData(ackResult);
        }catch (Exception e){
            serviceReadUI();
        }


        return 0;
    }

    private Boolean instanceComercios(String data){
        Intent intent = new Intent(Intent.ACTION_SEND);
        ComponentName cn = new ComponentName(PACKAGE, SEND_COMERCIOS);
        intent.setComponent(cn);
        setResult(Activity.RESULT_OK,intent);
        intent.putExtra("data_input",data);
        intent.putExtra(packageName, getApplicationContext().getPackageName());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTCOMERCIOS){
            stateTr = STATE_TR_FINISH;
            if(data != null) {
                String output = data.getStringExtra("data_output");
                procesaResutlActivity(output);
            }else{
                sendData((CM_SALE_REQ+":"+CM_FAIL));
            }
        }else if(requestCode == REQUESTPRINT){
            Log.v("Impresion","resultado "+String.valueOf(resultCode));
        }

       serviceReadUI();
    }


    private void procesaResutlActivity(String result){
        try {
            JSONObject obk = new JSONObject(result);
            try {
                if(!obk.getString(JSON_NUM_CONFIR).isEmpty()){
                    String codAutoriza = obk.getString(JSON_NUM_CONFIR);
                    String jFecha = obk.getString(JSON_NUM_FECHA);
                    String jRecibo = obk.getString(JSON_RECIBO);
                    if(codAutoriza.isEmpty()) {
                        sendData((CM_SALE_REQ+":"+CM_FAIL));
                    } else{
                        numAprobacion = codAutoriza;
                    }
                    FechaTr = jFecha.replace(":","-");
                    recibo = jRecibo;
                    serial.send((CM_SALE_REQ +":"+CM_ACK+","+numAprobacion + "," + FechaTr + "," + recibo).getBytes());

                }else{
                    sendData((CM_SALE_REQ+":"+CM_FAIL));
                }
            } catch (CommException e) {
                sendData((CM_SALE_REQ+":"+CM_FAIL));
                e.printStackTrace();
            }
        } catch (JSONException e) {
            sendData((CM_SALE_REQ+":"+CM_FAIL));
            e.printStackTrace();
        }
    }

    private void serviceReadUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceRead();
            }
        });
    }

    private void serviceRead(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String data;
                    data = recvNonBlocking();
                    if(data.contains(CM_SALE) || data.contains(CM_SALE_REQ)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                            }
                        });
                        break;
                    }
                }
            }
        }).start();
    }

    public String recvNonBlocking() {
        try {
            if (serial.getConnectStatus() == CONNECTED) {
                byte[] result=null;
                while (true) {
                    result = serial.recvNonBlocking();
                    if (result.length >  0)
                        break;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.v("SERIAL", "recibido recvNonBlocking " + new String(result));
                return new String(result);
            } else {
                connect();
                Log.v("SERIAL","please connect first");
            }
        } catch (CommException e) {
            e.printStackTrace();
            Log.v("SERIAL","catch recvNonBlocking "+e.getMessage());
        }
        return null;
    }

    public void connect() {
        if (serial != null) {
            try {
                if (serial.getConnectStatus() == DISCONNECTED) {
                    serial.connect();
                    Log.v("SERIAL","Connect ");
                    Toast.makeText(this,"Connect ",Toast.LENGTH_LONG).show();

                } else {
                    Log.v("SERIAL","have connected ");
                    Toast.makeText(this,"have connected  ",Toast.LENGTH_LONG).show();
                }
            } catch (CommException e) {
                Log.v("SERIAL","Connect  "+e.getMessage());
                Toast.makeText(this,"Connect  "+e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private Boolean getComm() {
        UartParam uartParam = new UartParam();
        uartParam.setPort((EUartPort.USBDEV));
        uartParam.setAttr("9600,8,n,1");
        serial = privateAppication.getDal().getCommManager().getUartComm(uartParam);
        return true;
    }

    private String getJson() {
        String val = "{\"TipoTransaccion\":1, \"properties\": {\"Monto\":\"";
        val+= String.valueOf(Amount);
        val+= "\", \"Iva\": \"0\", \"Inc\": \"0\", \"Monto_base_iva\": \"0\", \"Monto_base_inc\": \"0\", \"Base_devolucion\": \"0\"}}";
        return val;

    }

    private void sendData(String data){
        try {
            serial.send(data.getBytes());
        } catch (CommException e) {
            e.printStackTrace();
            serviceReadUI();
        }
    }
}