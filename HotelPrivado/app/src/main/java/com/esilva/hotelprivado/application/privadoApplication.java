package com.esilva.hotelprivado.application;

import static com.esilva.hotelprivado.Util.Constantes.CHANNEL_NOTIFICATION;
import static com.esilva.hotelprivado.Util.Constantes.CONNECT_BT;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_AUTO_OFF;
import static com.esilva.hotelprivado.Util.Constantes.REP_AUTO;
import static com.esilva.hotelprivado.Util.Constantes.SHA_BASE;
import static com.esilva.hotelprivado.Util.Constantes.SHA_ID_TRANS;
import static com.esilva.hotelprivado.Util.Constantes.TYPE_CONNECT;
import static com.esilva.hotelprivado.Util.Constantes.USB_ID;
import static com.esilva.hotelprivado.Util.Constantes.USB_NAME;
import static com.esilva.hotelprivado.Util.Constantes.USB_PORT;
import static com.esilva.hotelprivado.Util.Constantes.re_hora;
import static com.esilva.hotelprivado.Util.Constantes.re_minu;
import static com.esilva.hotelprivado.Util.Constantes.re_seg;
import static com.esilva.hotelprivado.Util.Constantes.re_hora2;
import static com.esilva.hotelprivado.Util.Constantes.re_minu2;
import static com.esilva.hotelprivado.Util.Constantes.re_seg2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.esilva.hotelprivado.ServiceBroadcastReceiver;
import com.esilva.hotelprivado.db.db_hotel;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.Calendar;

public class privadoApplication extends Application {
    private static String USB_deviceName;
    private static int USB_deviceID;
    private static int USB_port;
    private static Context appContext;
    private static SharedPreferences preferences;
    private static int sdkNoti;
    private static int sdkPermision;
    private static Boolean typeConnection;

    public static Boolean getTypeConnection() {
        return typeConnection;
    }

    public static void setTypeConnection(Boolean typeConnection) {
        preferences.edit().putBoolean(TYPE_CONNECT,typeConnection).commit();
        privadoApplication.typeConnection = typeConnection;
    }



    public static int getSdkPermision() {
        return sdkPermision;
    }

    public static int getSdkNoti() {
        return sdkNoti;
    }

    public static String getUSB_deviceName() {
        return USB_deviceName;
    }

    public static void setUSB_deviceName(String USB_deviceName) {
        preferences.edit().putString(USB_NAME,USB_deviceName).commit();
        privadoApplication.USB_deviceName = USB_deviceName;
    }

    public static int getUSB_port() {
        return USB_port;
    }

    public static void setUSB_port(int USB_port) {
        preferences.edit().putInt(USB_PORT,USB_port).commit();
        privadoApplication.USB_port = USB_port;
    }

    public static int getUSB_deviceID() {
        return USB_deviceID;
    }

    public static void setUSB_deviceID(int USB_deviceID) {
        preferences.edit().putInt(USB_ID,USB_deviceID).commit();
        privadoApplication.USB_deviceID = USB_deviceID;
    }

    public static void setSecuenciaTr(int secuenciaTransac) {
        secuenciaTransac++;
        if(secuenciaTransac>10000)
            secuenciaTransac=0;
        preferences.edit().putInt(SHA_ID_TRANS,secuenciaTransac).commit();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        Log.v("aplicacion","se inicia la aplicacion");
        sdkNoti = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S?PendingIntent.FLAG_IMMUTABLE:0;
        sdkPermision = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S?0:1;
        preferences = getSharedPreferences(SHA_BASE,MODE_PRIVATE);
        if(/*preferences.getBoolean(REP_AUTO,REPORT_AUTO_OFF)*/true){
            configService();
            createService();
            createService2();
        }

        USB_deviceID = preferences.getInt(USB_ID,0);
        USB_port = preferences.getInt(USB_PORT,0);
        USB_deviceName = preferences.getString(USB_NAME,null);
        typeConnection = preferences.getBoolean(TYPE_CONNECT,CONNECT_BT);
        Log.v("aplicacion","ID USB "+ String.valueOf(USB_deviceID)+" Port "+String.valueOf(USB_port));

        db_hotel dbHelper = new db_hotel(this, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
        db.close();
    }

    private void configService(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "hotel";
            String description = "reportes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_NOTIFICATION, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void createService(){
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(ALARM_SERVICE);

        Intent intentToRepeat = new Intent(appContext, ServiceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, 0, intentToRepeat, sdkNoti);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, re_hora);
        calendar.set(Calendar.MINUTE, re_minu);
        calendar.set(Calendar.SECOND, re_seg);

        Log.v("time","hora reporte "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY
                , pendingIntent);
    }

    private void createService2(){
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(ALARM_SERVICE);

        Intent intentToRepeat = new Intent(appContext, ServiceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, 0, intentToRepeat, sdkNoti);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, re_hora2);
        calendar.set(Calendar.MINUTE, re_minu2);
        calendar.set(Calendar.SECOND, re_seg2);

        Log.v("time","hora reporte "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY
                , pendingIntent);
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }
}
