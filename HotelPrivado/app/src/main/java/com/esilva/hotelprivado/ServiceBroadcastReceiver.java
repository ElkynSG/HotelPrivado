package com.esilva.hotelprivado;


import static com.esilva.hotelprivado.Util.Constantes.CHANNEL_NOTIFICATION;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_INI;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_TOTAL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.esilva.hotelprivado.Util.Reporte;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataVentas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

    private  String nameFile;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("ServiceBroadcastReceiver", "inicia el servicio reporte");
        if(saveReport(context)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_NOTIFICATION);
            builder.setSmallIcon(R.drawable.report);
            builder.setContentTitle("Reporte");
            builder.setContentText("Reporte generado "+nameFile);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(201, builder.build());
        }
    }

    private String getNameFile(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        return nameFile="Reporte_"+sdf.format(date)+".txt";
    }

    private String getFechaOld(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date fechaAnterior = calendar.getTime();
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        return format.format(fechaAnterior);
    }

    private Boolean saveReport(Context context){
        Boolean ret = false;
        nameFile = "Reporte "+getFechaOld()+".txt";
        Reporte reporte = new Reporte(null,context,true,true,true,true);
        reporte.setDateTime(getFechaOld(),null,"00:30",null);
        if(reporte.generarReporte(REPORT_TOTAL)){
            reporte.setNameFile(nameFile);
            return reporte.grabaReporte();
        }
        Log.v("ServiceBroadcastReceiver", "inicia el servicio SIN datos");
        return false;
    }
}