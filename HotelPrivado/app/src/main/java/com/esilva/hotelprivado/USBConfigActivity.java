package com.esilva.hotelprivado;


import static com.esilva.hotelprivado.Util.Constantes.INTENT_ACTION_GRANT_USB;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.esilva.hotelprivado.Util.Constantes.*;
import com.esilva.hotelprivado.Util.CustomProber;
import com.esilva.hotelprivado.application.privadoApplication;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.ArrayList;
import java.util.Collection;

public class USBConfigActivity extends AppCompatActivity implements  View.OnClickListener, SerialInputOutputManager.Listener {


    static class ListItemUSB {
        UsbDevice device;
        int port;
        UsbSerialDriver driver;

        ListItemUSB(UsbDevice device, int port, UsbSerialDriver driver) {
            this.device = device;
            this.port = port;
            this.driver = driver;

        }
    }

    private Button bt_listDevices;
    private ListView li_listView;
    private final ArrayList<ListItemUSB> listItems = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private int USB_deviceID;
    private int USB_port;
    private Boolean USB_connected;
    private UsbSerialPort usbSerialPort;
    private USB_Permission usbPermission = USB_Permission.Unknown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbconfig);
        findViewByIdes();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_configBt:
                refresh();
                break;
            default:
                break;
        }
    }


    private void refresh() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        listItems.clear();
        Collection<UsbDevice> values = usbManager.getDeviceList().values();
        for(UsbDevice device : values) {
            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if(driver == null) {
                driver = usbCustomProber.probeDevice(device);

            }
            if(driver != null) {
                for(int port = 0; port < driver.getPorts().size(); port++)
                    listItems.add(new ListItemUSB(device, port, driver));
            } else {
                listItems.add(new ListItemUSB(device, 0, null));
            }
            ArrayList<String> data = new ArrayList<>();

            for (ListItemUSB list: listItems){
                data.add(list.device.getManufacturerName()+"  "+list.device.getProductName()+" Port "+list.port);
            }
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_private, data);
            li_listView.setAdapter(arrayAdapter);
        }

    }

    private void USB_CheckPermission() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                UsbDevice device = null;
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                for(UsbDevice v : usbManager.getDeviceList().values())
                    if(v.getDeviceId() == USB_deviceID)
                        device = v;
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
                    PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(USBConfigActivity.this, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
                    usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
                    return;
                }
                String data  =  "USB Configurado";
                if(usbConnection == null) {
                    if (!usbManager.hasPermission(driver.getDevice()))
                        data = "Sin permisos USB";
                }

                String finalData = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(USBConfigActivity.this, finalData,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void findViewByIdes() {
        bt_listDevices = (Button) findViewById(R.id.bt_configBt);
        bt_listDevices.setOnClickListener(this);
        li_listView = (ListView) findViewById(R.id.listViewBt);
        li_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                USB_deviceID = listItems.get(i).device.getDeviceId();
                USB_port = listItems.get(i).port;
                privadoApplication.setUSB_deviceName(listItems.get(i).device.getProductName());
                privadoApplication.setUSB_deviceID(USB_deviceID);
                privadoApplication.setUSB_port(USB_port);
                USB_CheckPermission();
            }
        });
    }

    @Override
    public void onNewData(byte[] data) {

    }

    @Override
    public void onRunError(Exception e) {

    }

}