package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_FIN;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_INI;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_PARTIAL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.esilva.hotelprivado.Util.Reporte;
import com.esilva.hotelprivado.Util.util;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataVentas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edFechaInicial;
    private EditText edFechaFinal;
    private EditText edHoraInicial;
    private EditText edHoraFinal;

    private Button btGenerar;

    private ImageView backReporte;
    private CheckBox checkSnacks;
    private CheckBox checkConAlco;
    private CheckBox checkSinAlco;
    private CheckBox checkSouvenirs;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    private String fechaINI;
    private String fechaFin;
    private String horaINI;
    private String horaFin;

    private int typeReporte = 0;
    private Boolean isRepSnacks;
    private Boolean isRepConAlcohol;
    private Boolean isRepSinAlcohol;
    private Boolean isRepSouvenirs;

    private String StrReporte;
    private String StrNameReport;

    private Reporte reporte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_report);

        initView();
    }

    private void initView() {
        edFechaInicial = findViewById(R.id.ed_fecha_inicial);
        edFechaInicial.setOnClickListener(this);

        edFechaFinal = findViewById(R.id.ed_fecha_final);
        edFechaFinal.setOnClickListener(this);

        edHoraInicial = findViewById(R.id.ed_hora_inicial);
        edHoraInicial.setOnClickListener(this);

        edHoraFinal = findViewById(R.id.ed_hora_final);
        edHoraFinal.setOnClickListener(this);

        btGenerar = findViewById(R.id.bt_generar_rp);
        btGenerar.setOnClickListener(this);

        checkSnacks = findViewById(R.id.cheSnack);
        checkConAlco = findViewById(R.id.cheCon);
        checkSinAlco = findViewById(R.id.cheSin);
        checkSouvenirs = findViewById(R.id.cheSouven);

        backReporte = findViewById(R.id.backSelectReporte);
        backReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportActivity.this.finish();
            }
        });


        fechaINI = "";
        fechaFin = "";
        horaINI = "";
        horaFin = "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ed_fecha_inicial:
                showDatePickerDialog(edFechaInicial);
                break;
            case R.id.ed_fecha_final:
                showDatePickerDialog(edFechaFinal);
                break;
            case R.id.ed_hora_inicial:
                showTimePickerDialog(edHoraInicial);
                break;
            case R.id.ed_hora_final:
                showTimePickerDialog(edHoraFinal);
                break;
            case R.id.bt_generar_rp:
                generaReporte();
                break;
            default:
                break;
        }
    }

    private void showDatePickerDialog(EditText edIn) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edIn.setText(year + "-" + String.format("%02d",(monthOfYear + 1)) + "-" +  String.format("%02d",dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText edIn) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY); // current year
        int mMinute = c.get(Calendar.MINUTE); // current month
        int mSecond = c.get(Calendar.SECOND); // current day
        // date picker dialog
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                edIn.setText(i + ":" + i1 );
            }
        },mHour,mMinute,true);
        timePickerDialog.show();
    }

    private void generaReporte(){

        if(!validaCampos()) {
            util.showToast(R.drawable.fail,"Valide datos configurados para el reporte",  ReportActivity.this);
            Log.v("reporte", "error validaCampos");
            return;
        }

        if(!validaFecha()) {
            util.showToast(R.drawable.fail,"Valide datos configurados para el reporte",  ReportActivity.this);
            Log.v("reporte", "error validaFecha");
            return;
        }

        if(!validaHora()) {
            util.showToast(R.drawable.fail,"Valide datos configurados para el reporte",  ReportActivity.this);
            Log.v("reporte", "error validaFecha");
            return;
        }

        if(!validaChecks()) {
            util.showToast(R.drawable.fail,"Seleccion algun tipo de producto",  ReportActivity.this);
            Log.v("reporte", "error validaChecks");
            return;
        }

        if(generarTypeReport()){
            showDialogNameReport();
            Log.v("reporte", "valida datos OK");
        }else{
            util.showToast(R.drawable.fail,"Error generando Reporte",  ReportActivity.this);
            Log.v("reporte", "valida datos FAIL");
        }

    }

    private Boolean validaCampos(){
        fechaINI = edFechaInicial.getText().toString();
        fechaFin = edFechaFinal.getText().toString();
        horaFin = edHoraFinal.getText().toString();
        horaINI = edHoraInicial.getText().toString();

        if(fechaINI.isEmpty() && !horaINI.isEmpty())
            return false;

        if(!fechaINI.isEmpty() && horaINI.isEmpty())
            return false;

        if(fechaFin.isEmpty() && !horaFin.isEmpty())
            return false;

        if(!fechaFin.isEmpty() && horaFin.isEmpty())
            return false;

        return true;
    }

    private Boolean validaFecha() {

        try {
            if(!fechaINI.isEmpty() && !fechaFin.isEmpty()){
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaInicioDate = date.parse(fechaINI);
                Date fechaFinalDate = date.parse(fechaFin);

                if(fechaInicioDate.equals(fechaFinalDate)) {
                    Log.v("fecha","fecha igual");
                    return true;
                }else if(fechaInicioDate.after(fechaFinalDate)){
                    Log.v("fecha","fecha inicio mayor");
                    return false;
                }else{
                    Log.v("fecha","fecha inicio menor");
                    return true;
                }
            }

            if(!fechaINI.isEmpty() && fechaFin.isEmpty()) {
                Date fechaactual = new Date(System.currentTimeMillis());
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaInicioDate = date.parse(fechaINI);  //String a date

                if (fechaactual.after(fechaInicioDate)) {
                    Log.v("fecha", "fecha actual mayor");
                    return true;
                } else {
                    Log.v("fecha", "fecha actual menor");
                    return false;
                }
            }

            if(fechaINI.isEmpty() && !fechaFin.isEmpty()) {
                Date fechaactual = new Date(System.currentTimeMillis());
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaInicioDate = date.parse(fechaFin);  //String a date

                if (fechaactual.after(fechaInicioDate)) {
                    Log.v("fecha", "fecha actual mayor");
                    return true;
                } else {
                    Log.v("fecha", "fecha actual menor");
                    return false;
                }
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    private Boolean validaHora(){

        if(fechaINI.isEmpty() && fechaFin.isEmpty())
            return true;

        if(fechaINI.equals(fechaFin)){
            String[] ini = horaINI.split(":");
            String[] fin = horaFin.split(":");
            int initHora = Integer.valueOf(ini[0]);
            int initMin = Integer.valueOf(ini[1]);
            int fintHora = Integer.valueOf(fin[0]);
            int fintMin = Integer.valueOf(fin[1]);

            if(initHora == fintHora){
                if(initMin > fintMin)
                    return false;
                else
                    return true;
            }

            if(initHora > fintHora){
                return false;
            }
        }
        return true;
    }

    private Boolean validaChecks(){
        if(!checkSinAlco.isChecked() && !checkSnacks.isChecked() && !checkConAlco.isChecked()&& !checkSouvenirs.isChecked())
            return false;

        return true;
    }


    private Boolean generarTypeReport(){
        isRepSnacks = checkSnacks.isChecked()?true:false;
        isRepConAlcohol = checkConAlco.isChecked()?true:false;
        isRepSinAlcohol = checkSinAlco.isChecked()?true:false;
        isRepSouvenirs = checkSouvenirs.isChecked()?true:false;

        reporte = new Reporte(ReportActivity.this,this,isRepConAlcohol,isRepSinAlcohol,isRepSnacks,isRepSouvenirs);
        reporte.setDateTime(fechaINI,fechaFin,horaINI,horaFin);

        if(fechaINI.isEmpty() && fechaFin.isEmpty())
            return reporte.generarReporte(REPORT_ALL);

        if(!fechaINI.isEmpty() && !fechaFin.isEmpty())
            return reporte.generarReporte(REPORT_PARTIAL);

        if(fechaINI.isEmpty())
            return reporte.generarReporte(REPORT_FIN);

        if(fechaFin.isEmpty() )
            return reporte.generarReporte(REPORT_INI);

        util.showToast(R.drawable.fail,"Error generando el reporte",ReportActivity.this);
        return false;
    }

    private void showDialogNameReport(){
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.name_report_dialog);


        EditText name = customDialog.findViewById(R.id.ed_re_name);
        name.requestFocus();


        Button acept = customDialog.findViewById(R.id.btaceptar);
        acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().isEmpty()){
                    StrNameReport = name.getText().toString()+".txt";
                    reporte.setNameFile(StrNameReport);
                    reporte.grabaReporte();
                    customDialog.dismiss();
                    util.showToast(R.drawable.ok,"Reporte Generado",  ReportActivity.this);
                }
            }
        });
        Button cancel = customDialog.findViewById(R.id.btcancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                ReportActivity.this.finish();
            }
        });

        customDialog.show();
        customDialog.getWindow().setLayout(900,500);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
    }

}
