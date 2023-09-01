package com.esilva.hotelprivado;


import static android.content.Context.ALARM_SERVICE;
import static com.esilva.hotelprivado.Util.Constantes.CHANNEL_NOTIFICATION;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.esilva.hotelprivado.Util.Reporte;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private  String nameFile;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BootBroadcastReceiver", "inicia el servicio boot");
    }


}