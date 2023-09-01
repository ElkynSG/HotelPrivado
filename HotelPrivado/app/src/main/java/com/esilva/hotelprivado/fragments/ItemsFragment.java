package com.esilva.hotelprivado.fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA;
import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.esilva.hotelprivado.Util.storeAdapter;

import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.Util.storeAdapterSale;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;
import com.esilva.hotelprivado.selectActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_TIPO = "tipo";
    public static final String ARG_IDIOMA = "idioma";

    // TODO: Rename and change types of parameters
    private int mTipo;
    private int mIdioma;
    private List<DataProduct> listItem;
    GridView gridItem;
    InterfeceItemProducto interfeceItemProducto;
    Activity activity;
    int numArt,numMaxArt;

    AlertDialog dialogAlert=null;


    public ItemsFragment(Activity activity) {
        this.activity =activity;
    }


    public static ItemsFragment newInstance(int tipoPro, String idiomaa,Activity activity) {
        ItemsFragment fragment = new ItemsFragment(activity);
        Bundle args = new Bundle();
        args.putInt(ARG_TIPO, tipoPro);
        args.putString(ARG_IDIOMA, idiomaa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(getContext());
        if (getArguments() != null) {
            mTipo = getArguments().getInt(ARG_TIPO);
            mIdioma = getArguments().getInt(SHA_IDIOMA);
            listItem = adminBaseDatos.getProductoTipo(mTipo);

        }else {
            listItem = adminBaseDatos.getAllRows();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_items, container, false);
        gridItem = root.findViewById(R.id.gridStore);



        if(listItem != null) {

            gridItem.setAdapter(new storeAdapter(getContext(), listItem,mIdioma));
            gridItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(listItem != null){

                        DataProduct dp =new DataProduct(listItem.get(i).dt_id_producto,
                                listItem.get(i).getDt_id_producto(),listItem.get(i).dt_nameImage,
                                listItem.get(i).dt_nombre_es,listItem.get(i).dt_nombre_in,
                                listItem.get(i).dt_precio,listItem.get(i).dt_descripcion_es,
                                listItem.get(i).dt_descripcion_in,listItem.get(i).dt_type_product,
                                listItem.get(i).dt_num_articulos,listItem.get(i).dt_num_vendidos);
                        if(Integer.valueOf(dp.dt_num_articulos)>0)
                            showDialogProducto(dp);
                        else
                            showDialooOther(R.string.dialog_errornodis);

                        //interfeceItemProducto.typeProductData(dp);
                    }
                }
            });
        }

        return root;
    }

    private void showDialooOther(int mensaje){
        Dialog customDialog = new Dialog(getContext(),R.style.popup_dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.pago_dialog_other);


        TextView tv_message = customDialog.findViewById(R.id.tv_mensaje_other);
        tv_message.setText(mensaje);
        customDialog.show();


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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void showDialogProducto(DataProduct dp){

        numArt = 1;
        numMaxArt = Integer.valueOf(dp.dt_num_articulos);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dp.setDt_num_articulos(String.valueOf(numArt));


        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialog = inflater.inflate(R.layout.product_item, null);
        dialogBuilder.setView(dialog);


        TextView name = dialog.findViewById(R.id.prod_testName);
        name.setText(mIdioma==SHA_IDIOMA_ESPANOL?dp.dt_nombre_es:dp.dt_nombre_in);

        TextView desc = dialog.findViewById(R.id.prod_testDescrip);
        desc.setText(mIdioma==SHA_IDIOMA_ESPANOL?dp.dt_descripcion_es:dp.dt_descripcion_in);

        TextView valor = dialog.findViewById(R.id.prod_testPrecio);
        valor.setText(dp.dt_precio);

        TextView numItem = dialog.findViewById(R.id.prod_tvSuma);
        numItem.setText(String.valueOf(numArt));

        ImageView icon = (ImageView) dialog.findViewById(R.id.produ_iconImage);

        String direct = "/storage/emulated/0/HotelPrivado/"+dp.dt_nameImage;
        Uri myUri = (Uri.parse(direct));
        icon.setImageURI(myUri);

        final ImageView bt_x = dialog.findViewById(R.id.prod_x);
        bt_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finalizar compra
                dialogAlert.dismiss();
            }
        });

        final Button bt_seguir = dialog.findViewById(R.id.prod_bt_seguir);
        bt_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlert.dismiss();
            }
        });

        final Button bt_comprar = dialog.findViewById(R.id.prod_bt_sale);
        bt_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numMaxArt > 0){
                    interfeceItemProducto.typeProductData(dp);
                    dialogAlert.dismiss();
                }else{
                    dialogAlert.dismiss();
                    showDialooOther(R.string.dialog_errornodis);
                }

            }
        });

        final Button bt_menos = dialog.findViewById(R.id.prod_btMenos);
        bt_menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numArt >1){
                    numArt--;
                    dp.setDt_num_articulos(String.valueOf(numArt));
                    numItem.setText(String.valueOf(numArt));
                }
                // resta al producto
            }
        });
        final Button bt_mas = dialog.findViewById(R.id.prod_btMas);
        bt_mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numMaxArt > numArt){
                    numArt++;
                    dp.setDt_num_articulos(String.valueOf(numArt));
                    numItem.setText(String.valueOf(numArt));
                }
                // suma al producto
            }
        });


        dialogAlert = dialogBuilder.create();

        //dialogAlert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (dialogAlert.getWindow() != null) {
                    dialogAlert.getWindow().getDecorView().setAlpha(0.0f);
                    dialogAlert.getWindow().getDecorView().animate().alpha(1.0f).setDuration(250);
                }
            }
        });

// Configurar la animación de salida
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

        //dialogAlert.getWindow().setLayout(900, 720);
    }

    private void showDialog(int mensaje){
        Dialog customDialog = new Dialog(getContext(),R.style.popup_dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.pago_dialog);


        TextView tv_message = customDialog.findViewById(R.id.tv_mensaje);
        tv_message.setText(mensaje);
        customDialog.show();


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

    public interface InterfeceItemProducto{
        public void typeProductData(DataProduct product);
    }

    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof TypeItemsFragment.InterfeceTypeProducto){
            interfeceItemProducto = (InterfeceItemProducto) context;
        }else{
            throw new RuntimeException(context.toString()+"listener fragment");
        }

    }
}