package com.esilva.hotelprivado.Util;


import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.db.DataProduct;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapterIndividual extends BaseAdapter {
    private Context mContext;
    private List<DataProduct> mList = new ArrayList<DataProduct>();
    private int selected = -1;
    private int idioma;
    private LayoutInflater mInflater;

    public StoreAdapterIndividual(Context pContext, List<DataProduct> list) {
        this.mContext = pContext;
        this.idioma = idioma;
        mInflater = LayoutInflater.from(mContext);

        mList = list;
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

    public void setData( List<DataProduct> list){
        mList = list;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.app_gridview_item_indivi, null);

        ImageView icon = (ImageView) convertView.findViewById(R.id.iconImage);

        TextView name = (TextView) convertView.findViewById(R.id.testName);
        TextView descrip = (TextView) convertView.findViewById(R.id.testDescrip);
        TextView precio = (TextView) convertView.findViewById(R.id.testPrecio);
        TextView vendidos = (TextView) convertView.findViewById(R.id.itemVendidos);
        TextView totales = (TextView) convertView.findViewById(R.id.itemTotales);
        ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        int vendi = Integer.valueOf(mList.get(position).dt_num_vendidos);
        int total = Integer.valueOf(mList.get(position).dt_num_articulos)+vendi;
        name.setText(mList.get(position).dt_nombre_es);
        descrip.setText(mList.get(position).dt_descripcion_es);
        vendidos.setText(mList.get(position).dt_num_vendidos);
        totales.setText(String.valueOf(total));

        precio.setText(mList.get(position).dt_precio);
        bar.setMax(total);
        bar.setProgress(vendi);
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
