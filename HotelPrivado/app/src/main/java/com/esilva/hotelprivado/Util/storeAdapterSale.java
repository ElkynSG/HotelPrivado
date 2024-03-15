package com.esilva.hotelprivado.Util;


import static com.esilva.hotelprivado.Util.Constantes.SHA_IDIOMA_ESPANOL;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;
import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.fragments.ItemsFragment;
import com.esilva.hotelprivado.fragments.TypeItemsFragment;

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

public class storeAdapterSale extends BaseAdapter {
    private Context mContext;
    private List<DataProduct> mList;
    private int selected = -1;
    private LayoutInflater mInflater;
    NumberFormat currencyFormatter;
    private int idioma;

    InterfaceAdpterSale interfaceAdapter;


    public storeAdapterSale(Context context, List<DataProduct> list, int idioma) {
        this.mContext = context;
        this.mList = list;
        this.idioma = idioma;
        mInflater = LayoutInflater.from(mContext);

        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;
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

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.app_gridview_item_sale, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.iconImage);
            holder.name = convertView.findViewById(R.id.testName);
            holder.descrip = convertView.findViewById(R.id.testDescrip);
            holder.precio = convertView.findViewById(R.id.testPrecio);
            holder.bt_menos = convertView.findViewById(R.id.btMenos);
            holder.bt_mas = convertView.findViewById(R.id.btMas);
            holder.item = convertView.findViewById(R.id.tvSuma);
            Log.v("uri","convertView");
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (idioma == SHA_IDIOMA_ESPANOL) {
            holder.name.setText(mList.get(position).dt_nombre_es);
            holder.descrip.setText(mList.get(position).dt_descripcion_es);
        } else {
            holder.name.setText(mList.get(position).dt_nombre_in);
            holder.descrip.setText(mList.get(position).dt_descripcion_in);
        }

        holder.precio.setText(mList.get(position).dt_precio);

        try {
            String directorio = "/storage/emulated/0/HotelPrivado/" + mList.get(position).dt_nameImage;
            Uri myUri = Uri.parse(directorio);
            Log.v("uri", myUri.toString());
            holder.icon.setImageURI(myUri);
        } catch (Exception e) {
            Log.v("Store", e.getMessage());
        }

        holder.item.setText(mList.get(position).dt_num_articulos);

        holder.bt_menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mList.size() > 0) {
                    int num = Integer.valueOf(mList.get(position).dt_num_articulos);
                    if (num > 0) {
                        num--;
                        mList.get(position).dt_num_articulos = String.valueOf(num);
                        DataProduct pro = null;
                        if (num == 0) {
                            pro = mList.get(position);
                            mList.remove(position);
                        }
                        notifyDataSetChanged(view.getId());
                        interfaceAdapter.typeProductData(pro, 2, position);
                    }
                } else {
                    interfaceAdapter.typeProductData(null, 3, position);
                }
            }
        });

        holder.bt_mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(mList.get(position).dt_num_articulos);
                AdminBaseDatos adminBaseDatos = new AdminBaseDatos(mContext);
                ArrayList<Integer> array = adminBaseDatos.getProductoTabla(mList.get(position));
                adminBaseDatos.closeBaseDtos();
                int max = array.get(0);

                if (num < max) {
                    num++;
                    mList.get(position).dt_num_articulos = String.valueOf(num);
                    notifyDataSetChanged(view.getId());
                    interfaceAdapter.typeProductData(mList.get(position), 1, position);
                }
            }
        });


        return convertView;
    }

    public void setInteface(InterfaceAdpterSale interfaceAdapter){
        this.interfaceAdapter=interfaceAdapter;
    }

    public interface InterfaceAdpterSale{
        public void typeProductData(DataProduct product,int oper,int pos);
    }


    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView descrip;
        TextView precio;
        Button bt_menos;
        Button bt_mas;
        TextView item;
    }
}
