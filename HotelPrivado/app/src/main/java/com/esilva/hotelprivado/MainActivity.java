package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.CHANNEL_NOTIFICATION;
import static com.esilva.hotelprivado.Util.Constantes.FILE_REPORT;
import static com.esilva.hotelprivado.Util.Constantes.PACKAGE_FILE;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_TOTAL;
import static com.esilva.hotelprivado.Util.Constantes.SHA_BASE;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_INGLES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.esilva.hotelprivado.Util.Reporte;
import com.esilva.hotelprivado.Util.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    int REQUEST_COD = 200;
    private Button bt_id_espanol;
    private Button bt_id_ingles;
    private ImageButton bt_menu;
    private TextView copyright;
    private SharedPreferences preferences;
    private ConstraintLayout test;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        copyright = findViewById(R.id.copyright);
        bt_id_espanol = findViewById(R.id.bt_espanol);
        bt_id_espanol.setOnClickListener(this);
        bt_id_ingles = findViewById(R.id.bt_ingles);
        bt_id_ingles.setOnClickListener(this);
        bt_menu = findViewById(R.id.bt_menu);
        bt_menu.setOnClickListener(this);

        test=findViewById(R.id.backMain);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            solicitarPermisos();
        }
        copyright.setText("Powered by GST   Version:" + util.getVersionName(this));

        File directorio = new File(Environment.getExternalStorageDirectory(), PACKAGE_FILE);
        if (!directorio.exists()){
            directorio.mkdirs();
        }

        File directorio2 = new File(Environment.getExternalStorageDirectory(), FILE_REPORT);
        if (!directorio2.exists()){
            directorio2.mkdirs();
        }
        preferences = getSharedPreferences(SHA_BASE,MODE_PRIVATE);

        Log.v("HOTEL PRIVADO","SE CREA LA ACTIVIDAD PRINCIPAL");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Animation animation = AnimationUtils.loadAnimation(this,R.anim.app_item_zoomin);
        //test.setAnimation(animation);
        //bt_id_ingles.startAnimation(animation);
        //bt_id_espanol.startAnimation(animation);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.bt_espanol:
                Locale localizacion1= new Locale("es", "col");
                Locale.setDefault(localizacion1);
                Configuration config1 = new Configuration();
                config1.locale = localizacion1;
                getBaseContext().getResources().updateConfiguration(config1, getBaseContext().getResources().getDisplayMetrics());
                preferences.edit().putInt(SHA_IDIOMA,SHA_IDIOMA_ESPANOL).commit();
                intent = new Intent(this, selectActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.bt_ingles:
                Locale localizacion2 = new Locale("en", "rUS");
                Locale.setDefault(localizacion2);
                Configuration config2 = new Configuration();
                config2.locale = localizacion2;
                getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
                preferences.edit().putInt(SHA_IDIOMA,SHA_IDIOMA_INGLES).commit();
                intent = new Intent(this, selectActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.bt_menu:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermisos(){

        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }else{
            int PermisoStorageRead = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            int PermisoStorageWrite = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int PermisoStoragEManager = ContextCompat.checkSelfPermission(this,Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            int PermisoSetting = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_SETTINGS);

            if(PermisoSetting == PackageManager.PERMISSION_GRANTED && PermisoStorageRead == PackageManager.PERMISSION_GRANTED && PermisoStoragEManager == PackageManager.PERMISSION_GRANTED && PermisoStorageWrite == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"permiso staora otorgado",Toast.LENGTH_LONG);
            }else{
                requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE ,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.WRITE_SETTINGS

                },REQUEST_COD);

            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void testUsb(){
        Intent intent = new Intent(this,Driver_USB.class);
        startActivity(intent);
    }


    ///////////////////////////////////////////////////////////////////////////////
/*
    private String getFechaOld(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date fechaAnterior = calendar.getTime();
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        return format.format(fechaAnterior);
    }

    private Boolean saveReportTst(){
        Boolean ret = false;
        String nameFile = "Reporte "+getFechaOld()+".txt";
        Reporte reporte = new Reporte(null,this,true,true,true,true);
        reporte.setDateTime(getFechaOld(),null,"00:30",null);
        if(reporte.generarReporte(REPORT_TOTAL)){
            reporte.setNameFile(nameFile);
            return reporte.grabaReporte();
        }
        Log.v("ServiceBroadcastReceiver", "inicia el servicio SIN datos");
        return false;
    }*/
    
}