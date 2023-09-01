package com.esilva.hotelprivado.Util;


import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import com.esilva.hotelprivado.db.DataProduct;
import com.esilva.hotelprivado.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class storeAdapter extends BaseAdapter {
    private Context mContext;
    private List<DataProduct> mList = new ArrayList<DataProduct>();
    private int selected = -1;
    private int idioma;
    private LayoutInflater mInflater;

    public storeAdapter(Context pContext, List<DataProduct> list,int idioma) {
        this.mContext = pContext;
        this.idioma = idioma;
        mInflater = LayoutInflater.from(mContext);


        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(int id) {
        selected = id;
        super.notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.app_gridview_item, null);

        ImageView icon = (ImageView) convertView.findViewById(R.id.iconImage);

        TextView name = (TextView) convertView.findViewById(R.id.testName);
        TextView descrip = (TextView) convertView.findViewById(R.id.testDescrip);
        TextView precio = (TextView) convertView.findViewById(R.id.testPrecio);
        TextView dispo = (TextView) convertView.findViewById(R.id.testDisponi);

        if(idioma == SHA_IDIOMA_ESPANOL) {
            name.setText(mList.get(position).dt_nombre_es);
            descrip.setText(mList.get(position).dt_descripcion_es);
            dispo.setText("Disponible: "+mList.get(position).dt_num_articulos);
        }else{
            name.setText(mList.get(position).dt_nombre_in);
            descrip.setText(mList.get(position).dt_descripcion_in);
            dispo.setText("Available: "+mList.get(position).dt_num_articulos);
        }

        precio.setText(mList.get(position).dt_precio);
        try {
            String directorio = "/storage/emulated/0/HotelPrivado/"+mList.get(position).dt_nameImage;
            Uri myUri = (Uri.parse(directorio));
            icon.setImageURI(myUri);
        }catch (Exception e){
            Log.v("storeAdapter",e.getMessage());
        }



        return convertView;

    }

}
