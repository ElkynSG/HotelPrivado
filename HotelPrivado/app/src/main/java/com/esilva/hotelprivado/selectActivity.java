package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.DISCONNECT_TIMEOUT;
import static com.esilva.hotelprivado.Util.Constantes.SHA_BASE;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.esilva.hotelprivado.Util.storeAdapter;
import com.esilva.hotelprivado.Util.storeAdapterSale;
import com.esilva.hotelprivado.application.privadoApplication;
import com.esilva.hotelprivado.db.DataProduct;
import com.esilva.hotelprivado.fragments.ItemsFragment;
import com.esilva.hotelprivado.fragments.TypeItemsFragment;

import java.util.ArrayList;
import java.util.List;


public class selectActivity extends AppCompatActivity implements TypeItemsFragment.InterfeceTypeProducto, ItemsFragment.InterfeceItemProducto, View.OnClickListener {

    private  final String fraType = "fragmentType";
    private final String fraItem = "fragmentItems";

    private FragmentTransaction transaction;
    private Fragment fragTypeProduct, fragItems;
    private String StIdioma;
    private int StTipo;
    private int contaProdu;
    private List<DataProduct> listaProductosCarrito;
    private TextView tv_contador;
    private RelativeLayout carrito;
    private ImageView back;
    private Intent intent;
    //private storeAdapterSale adapter;
    private AlertDialog dialogAlert=null;

    SharedPreferences preferences;
    private int tipoIdioma;

    Bundle bundle;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select);
        tv_contador = findViewById(R.id.conta);
        carrito = findViewById(R.id.relativeCarri);
        carrito.setOnClickListener(this);
        back  = findViewById(R.id.backSelect);
        back.setOnClickListener(this);
        fragTypeProduct = new TypeItemsFragment();
        fragItems = new ItemsFragment(this);



        listaProductosCarrito = new ArrayList<DataProduct>();
        contaProdu = 0;

        if(privadoApplication.getTypeConnection())
            intent = new Intent(this,SaleUSBActivity.class);
        else
            intent = new Intent(this,SaleBtActivity.class);

        tv_contador.setVisibility(View.GONE);

        animation = AnimationUtils.loadAnimation(this,R.anim.app_item_zoomin);
        carrito.setAnimation(animation);

        getSupportFragmentManager().beginTransaction()
            .add(R.id.fragmentTienda, fragTypeProduct,fraType)
                .commit();

    }


    private void InitFragmentProductos(){
        preferences = getSharedPreferences(SHA_BASE,MODE_PRIVATE);
        tipoIdioma = preferences.getInt(SHA_IDIOMA,0);
        Bundle bundle = new Bundle();
        bundle.putInt(ItemsFragment.ARG_TIPO,    StTipo);
        bundle.putInt(ItemsFragment.ARG_IDIOMA,    tipoIdioma);

        transaction = getSupportFragmentManager().beginTransaction();
        fragItems.setArguments(bundle);
        transaction.replace(R.id.fragmentTienda,fragItems)
                .commit();
        transaction.addToBackStack(null);

    }

    @Override
    public void typeProduct(int typo) {
        if(typo == 5) {
            onBackPressed();
            return;
        }
        this.StTipo = typo;
        this.StIdioma = "ingles";

        InitFragmentProductos();
    }

    @Override
    public void typeProductData(DataProduct product) {
        this.StTipo = 0;
        this.StIdioma = "ingles";
        resetDisconnectTimer();

        if(guardaTienda(product)) {
            contaProdu += Integer.valueOf(product.dt_num_articulos);
            if (contaProdu > 0)
                tv_contador.setVisibility(View.VISIBLE);
            tv_contador.setText(String.valueOf(contaProdu));
            carrito.startAnimation(animation);
        }
    }

    private Boolean verificaObjet(DataProduct data){
        Boolean val=false;
        for(DataProduct dd: listaProductosCarrito){
            if(dd.dt_id_producto.equals(data.dt_id_producto)){
                val=true;
                break;
            }
        }
        return val;
    }

    private Boolean guardaTienda(DataProduct dp) {
        if(!verificaObjet(dp)) {
            listaProductosCarrito.add(dp);
            return true;
        }else{
            showDialooOther(R.string.dialog_error);

            return false;
        }
    }

    private void showDialooOther(int mensaje){
        Dialog customDialog = new Dialog(selectActivity.this,R.style.popup_dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.pago_dialog_other);


        TextView tv_message = customDialog.findViewById(R.id.tv_mensaje_other);
        tv_message.setText(mensaje);
        customDialog.show();
        resetDisconnectTimer();

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    customDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.relativeCarri:
                if(contaProdu>0)
                    showDialogSale(R.string.app_name);
                break;
            case R.id.backSelect:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private Bundle calculatotal(){
        int total=0;
            if(listaProductosCarrito.size() > 0){
                ArrayList<String> obJ = new ArrayList<>();
                for(DataProduct temp: listaProductosCarrito){
                    obJ.add(temp.Serializar());
                    //total +=  Integer.valueOf(temp.dt_num_articulos) * Integer.valueOf(temp.dt_precio.replace("$","").replace(".",""));
                }
                bundle= new Bundle();
                bundle.putStringArrayList("carrito",obJ);
            }
            return bundle;
    }

    private void showDialogSale(final int title){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialog = inflater.inflate(R.layout.app_add_item, null);
        dialogBuilder.setView(dialog);
        GridView gred_compras =  dialog.findViewById(R.id.gridSale);

        storeAdapterSale adapter = new storeAdapterSale(getBaseContext(),listaProductosCarrito,tipoIdioma);
        adapter.setInteface(new storeAdapterSale.InterfaceAdpterSale() {
            @Override
            public void typeProductData(DataProduct prod, int oper,int pos) {
                if(oper==1){
                    contaProdu++;
                }else{
                    contaProdu--;
                    if(prod != null && prod.dt_num_articulos.equals("0")){
                        listaProductosCarrito.remove(pos);
                    }

                }
                if(contaProdu==0) {
                    listaProductosCarrito = new ArrayList<>();
                    dialogAlert.dismiss();
                    tv_contador.setVisibility(View.GONE);
                }
                tv_contador.setText(String.valueOf(contaProdu));
                carrito.startAnimation(animation);
                resetDisconnectTimer();
            }
        });
        gred_compras.setAdapter(adapter);


        final Button seguir = dialog.findViewById(R.id.bt_seguir);
        final Button comprar = dialog.findViewById(R.id.bt_sale);
        dialogAlert = dialogBuilder.create();
        dialogAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (dialogAlert.getWindow() != null) {
                    dialogAlert.getWindow().getDecorView().setAlpha(0.0f);
                    dialogAlert.getWindow().getDecorView().animate().alpha(1.0f).setDuration(260);
                }
            }
        });


        dialogAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (dialogAlert.getWindow() != null) {
                    dialogAlert.getWindow().getDecorView().setAlpha(1.0f);
                    dialogAlert.getWindow().getDecorView().animate().alpha(0.0f).setDuration(250);
                }
            }
        });
        dialogAlert.show();
        dialogAlert.setCancelable(false);


        seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaProductosCarrito.size();
                resetDisconnectTimer();
                if(dialogAlert != null)
                    dialogAlert.dismiss();
            }
        });
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("monto",calculatotal());
                if(dialogAlert != null)
                    dialogAlert.dismiss();
                startActivity(intent);
                finish();
            }
        });

        gred_compras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // ListAdapter.notifyDataSetInvalidated
                //listaProductos.get(i).dt_num_articulos = "34";
                //adapter.notifyDataSetChanged((int)l);
                Log.v("eventClick","i="+String.valueOf(i)+" L "+String.valueOf(l));
            }
        });

    }


    private void finalizar(){
        Intent main = new Intent(this,MainActivity.class);
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