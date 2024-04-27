package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.esilva.hotelprivado.Util.StoreAdapterIndividual;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;

import java.util.List;

public class IndividualActivity extends AppCompatActivity {
private GridView grid;
    private int tipoProducto;
    private ImageView backSelectInve;

    private AlertDialog dialogAlert=null;
    private  StoreAdapterIndividual storeAdapterIndividual;
    private List<DataProduct> listItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_individual);

        tipoProducto = getIntent().getIntExtra("dtProd",0);
        Log.d("DP_DLOG","onCreate "+"tipoProducto "+tipoProducto);

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        listItem = adminBaseDatos.getProductoTipo(tipoProducto);

        setView();
    }

    private void setView() {
        storeAdapterIndividual = new StoreAdapterIndividual(this, listItem);
        grid = findViewById(R.id.gridStoreIndi);
        grid.setAdapter(storeAdapterIndividual);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialogProducto(i);
            }
        });

        backSelectInve = findViewById(R.id.backSelectInve);
        backSelectInve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showDialogProducto(int id){

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialog = inflater.inflate(R.layout.product_indivi, null);
        dialogBuilder.setView(dialog);


        TextView titulo = dialog.findViewById(R.id.prod_testName);
        titulo.setText(listItem.get(id).dt_nombre_es);
        TextView subTitutlo = dialog.findViewById(R.id.prod_testDescrip);
        subTitutlo.setText(listItem.get(id).dt_descripcion_es);
        TextView precio = dialog.findViewById(R.id.prod_testPrecio);
        precio.setText(listItem.get(id).dt_precio);

        EditText numDisponibles = dialog.findViewById(R.id.prod_btMas);
        numDisponibles.setText(listItem.get(id).dt_num_articulos);



        ImageView icon = (ImageView) dialog.findViewById(R.id.produ_iconImage);
        String direct = "/storage/emulated/0/HotelPrivado/"+listItem.get(id).dt_nameImage;
        Uri myUri = (Uri.parse(direct));
        icon.setImageURI(myUri);

        Button btCancel = (Button) dialog.findViewById(R.id.prod_bt_cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finalizar cancelar
                dialogAlert.dismiss();
            }
        });
        Button btModificar = (Button) dialog.findViewById(R.id.prod_bt_modificar);
        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminBaseDatos adminBaseDatos = new AdminBaseDatos(IndividualActivity.this);
                listItem.get(id).dt_num_articulos = numDisponibles.getText().toString().trim();
                listItem.get(id).dt_num_vendidos = "0";
                storeAdapterIndividual.setData(listItem);
                storeAdapterIndividual.notifyDataSetChanged();
                adminBaseDatos.updateProducto(listItem.get(id));
                adminBaseDatos.closeBaseDtos();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(numDisponibles.getWindowToken(), 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogAlert.dismiss();
                    }
                }, 400); // Retraso de 500 milisegundos
            }
        });

        dialogAlert = dialogBuilder.create();
        dialogAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // Mostrar el teclado cuando el diálogo esté completamente visible
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(numDisponibles, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialogAlert.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}