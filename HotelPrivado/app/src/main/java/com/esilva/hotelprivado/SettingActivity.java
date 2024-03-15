package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.CHANNEL_NOTIFICATION;
import static com.esilva.hotelprivado.Util.Constantes.CONNECT_BT;
import static com.esilva.hotelprivado.Util.Constantes.CONNECT_USB;
import static com.esilva.hotelprivado.Util.Constantes.DISCONNECT_TIMEOUT;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_AUTO_OFF;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_AUTO_ON;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_INI;
import static com.esilva.hotelprivado.Util.Constantes.REP_AUTO;
import static com.esilva.hotelprivado.Util.Constantes.SHA_BASE;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;
import static com.esilva.hotelprivado.Util.Constantes.re_hora;
import static com.esilva.hotelprivado.Util.Constantes.re_minu;
import static com.esilva.hotelprivado.Util.Constantes.re_seg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esilva.hotelprivado.Util.Reporte;
import com.esilva.hotelprivado.application.privadoApplication;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.inventario.CargaInventario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btcargar;
    private Button bteliminar;
    private Button btcontrasena;
    private Button btguardar;
    private ImageView btBack;

    private EditText passActual;
    private EditText passOld;
    private EditText passOld2;
    private Switch swHabRepAuto;
    private Switch swTypeConnection;
    private Boolean IsreportAuto;
    private SharedPreferences preferences;
    private static Dialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        btcargar = findViewById(R.id.bt_cargar_data);
        btcargar.setOnClickListener(this);
        btguardar = findViewById(R.id.bt_descargar);
        btguardar.setOnClickListener(this);
        btcontrasena = findViewById(R.id.bt_cambiar);
        btcontrasena.setOnClickListener(this);
        bteliminar = findViewById(R.id.bt_eliminar);
        bteliminar.setOnClickListener(this);
        btBack= findViewById(R.id.backSelectSe);
        btBack.setOnClickListener(this);

        preferences = privadoApplication.getPreferences();
        swHabRepAuto = findViewById(R.id.swt_rep_auto);
        IsreportAuto = preferences.getBoolean(REP_AUTO,REPORT_AUTO_OFF);
        swHabRepAuto.setChecked(IsreportAuto);
        swHabRepAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    preferences.edit().putBoolean(REP_AUTO,REPORT_AUTO_ON).commit();
                    configService();
                    createService(REPORT_AUTO_ON);
                    Toast.makeText(SettingActivity.this,"Reporte automatico habilitado",Toast.LENGTH_SHORT).show();


                }else {
                    preferences.edit().putBoolean(REP_AUTO,REPORT_AUTO_OFF).commit();
                    createService(REPORT_AUTO_OFF);
                    Toast.makeText(SettingActivity.this,"Reporte automatico deshabilitado",Toast.LENGTH_SHORT).show();

                }
            }
        });

        swTypeConnection = findViewById(R.id.swt_usb_bt);
        swTypeConnection.setChecked(privadoApplication.getTypeConnection());
        swTypeConnection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    privadoApplication.setTypeConnection(CONNECT_USB);
                    Toast.makeText(SettingActivity.this,"USB",Toast.LENGTH_SHORT).show();
                }else {
                    privadoApplication.setTypeConnection(CONNECT_BT);
                    Toast.makeText(SettingActivity.this,"BLUETOOTH",Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    private void createService(Boolean IShab){
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);

        Intent intentToRepeat = new Intent(this, ServiceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intentToRepeat, privadoApplication.getSdkNoti());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, re_hora);
        calendar.set(Calendar.MINUTE, re_minu);
        calendar.set(Calendar.SECOND, re_seg);

        Log.v("time","hora "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        if(IShab){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY
                    , pendingIntent);
        }else{
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_cargar_data:
                CargaInventario cargaInventario = new CargaInventario(this);
                if(cargaInventario.isExite()){
                    showDialog("Productos","Ya existe productos cargados. Desea reemplazarlos?");
                }else{
                    if(cargaInventario.cargarDatos()){
                        showDialogConfir("Productos","Carga de datos Exitosa");
                    }else {
                        showDialogConfir("Productos","Carga de datos Fallida");
                    }
                }

                break;
            case R.id.bt_descargar:
                Intent intent2 = new Intent(this,ReportActivity.class);
                startActivity(intent2);
                break;
            case R.id.bt_cambiar:
                showDialogPass();
                break;
            case R.id.bt_eliminar:
                Intent intent;
                if(privadoApplication.getTypeConnection())
                    intent = new Intent(this,USBConfigActivity.class);
                else
                    intent = new Intent(this,BluetoothActivity.class);
                startActivity(intent);
                break;
            case R.id.backSelectSe:
                onBackPressed();
                break;
        }
    }
    private String getNameFile(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        return "ReporteCierre "+sdf.format(date)+".txt";
    }

    private void guardarReporteTotal() {
        Reporte reporte;
        reporte = new Reporte(SettingActivity.this,this,true,true,true,true);
        Log.d("DP_DLOG","guardarReporteTotal "+"reporte");
        reporte.setIsShowMessage(false);
        if(reporte.generarReporte(REPORT_ALL)) {
            Log.d("DP_DLOG","guardarReporteTotal "+"name "+ getNameFile());
            reporte.setNameFile(getNameFile());
            Boolean aBoolean = reporte.grabaReporte();
            Log.d("DP_DLOG","guardarReporteTotal "+"grabar reporte "+aBoolean);
        }else{
            Log.d("DP_DLOG","guardarReporteTotal "+"no genera reporte");
        }
    }

    private void showDialog(String title,String message){
        customDialog = new Dialog(SettingActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.login_dialog_setti);

        TextView tv_titulo = customDialog.findViewById(R.id.diTitleialog);
        TextView tv_message = customDialog.findViewById(R.id.txt_dialog);
        tv_titulo.setText(title);
        tv_message.setText(message);
        customDialog.findViewById(R.id.btn_dialog_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.btn_dialog_si).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarReporteTotal();
                customDialog.dismiss();
                if(reemplazarProdu()){
                    showDialogConfir("Productos","Carga de datos Exitosa");
                }else {
                    showDialogConfir("Productos","Carga de datos Fallida");
                }
            }
        });

        customDialog.show();

    }

    private void showDialogConfir(String title,String message){
        customDialog = new Dialog(SettingActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.login_dialog);

        TextView tv_titulo = customDialog.findViewById(R.id.diTitleialog);
        TextView tv_message = customDialog.findViewById(R.id.txt_dialog);
        tv_titulo.setText(title);
        tv_message.setText(message);
        customDialog.findViewById(R.id.btn_dialog_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();

    }

    private void showDialogPass(){
        customDialog = new Dialog(SettingActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.app_change_pass);

        passActual = (EditText) customDialog.findViewById(R.id.ed_actualPass);
        passOld = (EditText) customDialog.findViewById(R.id.ed_passViejo);
        passOld2 = (EditText) customDialog.findViewById(R.id.ed_passViejo2);

        customDialog.findViewById(R.id.bt_chage_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret = cambiarPass();
                customDialog.dismiss();
                if(ret == 0)
                    showDialogConfir("Exitosa","Cambio de contraseña exitosa");
                if(ret == -1)
                    showDialogConfir("Error","Campos vacios");
                if(ret == -2)
                    showDialogConfir("Error","Contraseñas NO coinciden");
                if(ret == -3)
                    showDialogConfir("Error","Contraseñas son iguales");
                if(ret == -4)
                    showDialogConfir("Error","Contraseña actual erronea");
                if(ret == -5)
                    showDialogConfir("Error","Error guardando datos");



            }
        });
        customDialog.findViewById(R.id.bt_cancel_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();

    }

    private int cambiarPass(){
        int ret = ValDatos();
        if(ret != 0)
            return ret;

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        if(!adminBaseDatos.getUsurio().getUsContrasena().equals(passActual.getText().toString().trim()))
            return -4;
        if(!adminBaseDatos.updateUser(passOld.getText().toString().trim()))
            return -5;

        return 0;
    }

    private int ValDatos(){
        if(passActual.getText().toString().trim().isEmpty() || passOld.getText().toString().trim().isEmpty() || passOld2.getText().toString().trim().isEmpty())
            return -1;

        if(!passOld.getText().toString().trim().equals(passOld2.getText().toString().trim()))
            return -2;

        if(passActual.getText().toString().trim().equals(passOld.getText().toString().trim()))
            return -3;

        return 0;
    }


    private Boolean reemplazarProdu(){
        CargaInventario cargaInventario = new CargaInventario(this);
        cargaInventario.eliminarTabla();
        return  cargaInventario.cargarDatos();
    }

    private void finalizar(){
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

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        Log.v("TIMEOUT", "YO");
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
    /////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}