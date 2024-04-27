package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;
import static com.esilva.hotelprivado.Util.Constantes.TYPE_PROD_ALCOHOL;
import static com.esilva.hotelprivado.Util.Constantes.TYPE_PROD_SIN_ALCOHOL;
import static com.esilva.hotelprivado.Util.Constantes.TYPE_PROD_SNACK;
import static com.esilva.hotelprivado.Util.Constantes.TYPE_PROD_SUVENIRS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esilva.hotelprivado.Util.Reporte;
import com.esilva.hotelprivado.Util.util;
import com.esilva.hotelprivado.inventario.CargaInventario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InventarioActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btBack;
    private Button btTotal,btIndividual;
    private Button bt_ca_total,bt_ca_alcohol,bt_ca_sin_alcohol,bt_ca_snacks,bt_ca_suvenirs;
    private LinearLayout lyTotal,lyIndividual;

    private static Dialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inventario);

        setView();
    }

    private void setView() {
        btTotal = findViewById(R.id.bt_total);
        btTotal.setOnClickListener(this);
        btIndividual = findViewById(R.id.bt_individual);
        btIndividual.setOnClickListener(this);
        btBack = findViewById(R.id.backSelectInve);
        btBack.setOnClickListener(this);

        bt_ca_total = findViewById(R.id.bt_cargar_total);
        bt_ca_total.setOnClickListener(this);
        bt_ca_alcohol = findViewById(R.id.bt_alcohol);
        bt_ca_alcohol.setOnClickListener(this);
        bt_ca_sin_alcohol = findViewById(R.id.bt_sin_alcohol);
        bt_ca_sin_alcohol.setOnClickListener(this);
        bt_ca_snacks = findViewById(R.id.bt_snacks);
        bt_ca_snacks.setOnClickListener(this);
        bt_ca_suvenirs = findViewById(R.id.bt_suvenirs);
        bt_ca_suvenirs.setOnClickListener(this);

        lyTotal = findViewById(R.id.ly_total);
        lyIndividual = findViewById(R.id.ly_individual);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.bt_total:
                lyTotal.setVisibility(View.VISIBLE);
                lyIndividual.setVisibility(View.GONE);
                break;
            case R.id.bt_individual:
                lyTotal.setVisibility(View.GONE);
                lyIndividual.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_cargar_total:
                CargaInventario cargaInventario = new CargaInventario(InventarioActivity.this,this);
                if(cargaInventario.isExite()){
                    showDialog("Productos","Ya existe productos cargados. Desea reemplazarlos?");
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Boolean aBoolean = cargaInventario.cargarDatosExcel();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(aBoolean){
                                        showDialogConfir("Productos","Carga de datos Exitosa");
                                    }else {
                                        showDialogConfir("Productos","Carga de datos Fallida");
                                    }
                                }
                            });
                        }
                    }).start();
                    /*if(cargaInventario.cargarDatosExcel()){
                        showDialogConfir("Productos","Carga de datos Exitosa");
                    }else {
                        showDialogConfir("Productos","Carga de datos Fallida");
                    }*/
                }
                break;
            case R.id.bt_alcohol:
                intent = new Intent(this,IndividualActivity.class);
                intent.putExtra("dtProd",TYPE_PROD_ALCOHOL);
                startActivity(intent);
                break;
            case R.id.bt_sin_alcohol:
                intent = new Intent(this,IndividualActivity.class);
                intent.putExtra("dtProd",TYPE_PROD_SIN_ALCOHOL);
                startActivity(intent);
                break;
            case R.id.bt_snacks:
                intent = new Intent(this,IndividualActivity.class);
                intent.putExtra("dtProd",TYPE_PROD_SNACK);
                startActivity(intent);
                break;
            case R.id.bt_suvenirs:
                intent = new Intent(this,IndividualActivity.class);
                intent.putExtra("dtProd",TYPE_PROD_SUVENIRS);
                startActivity(intent);
                break;
            case R.id.backSelectInve:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void showDialog(String title,String message){
        customDialog = new Dialog(InventarioActivity.this);
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
            }
        });

        customDialog.show();

    }

    private void showDialogConfir(String title,String message){
        customDialog = new Dialog(InventarioActivity.this);
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
    Reporte reporte;
    private void guardarReporteTotal() {

        reporte = new Reporte(InventarioActivity.this,this,true,true,true,true);
        Log.d("DP_DLOG","guardarReporteTotal "+"reporte");
        reporte.setIsShowMessage(false);
        if(reporte.generarReporte(REPORT_ALL)) {
            Log.d("DP_DLOG","guardarReporteTotal "+"name "+ getNameFile());
            reporte.setNameFile(getNameFile());
            generarReporte();
            //Log.d("DP_DLOG","guardarReporteTotal "+"grabar reporte "+aBoolean);
        }else{
            Log.d("DP_DLOG","guardarReporteTotal "+"no genera reporte");
        }
    }

    private Boolean reemplazarProdu(){
        CargaInventario cargaInventario = new CargaInventario(InventarioActivity.this,this);
        cargaInventario.eliminarTabla();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean aBoolean = cargaInventario.cargarDatosExcel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(aBoolean){
                            showDialogConfir("Productos","Carga de datos Exitosa");
                        }else {
                            showDialogConfir("Productos","Carga de datos Fallida");
                        }
                    }
                });
            }
        }).start();
        return  true;
    }

    private String getNameFile(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        return "ReporteCierre "+sdf.format(date)+".xlsx";
    }

    private ProgressDialog progressDialog;
    private void generarReporte() {
        progressDialog = new ProgressDialog(InventarioActivity.this);
        progressDialog.setMessage("Generando Reporte Totales");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean b = reporte.buildReport();
                showResult(b);
            }
        }).start();
    }

    private void showResult(boolean result){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                reemplazarProdu();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}