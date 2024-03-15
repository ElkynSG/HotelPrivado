package com.esilva.hotelprivado.inventario;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;

import org.w3c.dom.ls.LSException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CargaInventario {
    DataProduct dataProductInv;
    private Context context;

    public CargaInventario(Context context) {
        this.context = context;
    }

    public Boolean cargarDatos(){
        String data[];
        long aLong=0;
        NumberFormat currencyFormatter;
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;

        AdminBaseDatos adminBaseDatos;
        List<String> trama;
        OutputStream out;
        File directorio = new File(Environment.getExternalStorageDirectory(), "HotelPrivado/inventario.txt");
        if(!directorio.exists()){
            return null;
        }
        adminBaseDatos = new AdminBaseDatos(context);
        adminBaseDatos.deleteTableProd();

        trama = readFileInventario(directorio);
        try {
            for(String line:trama){
                aLong = adminBaseDatos.insert(getDataProduct(line).dt_id_producto.trim()
                        ,getDataProduct(line).dt_nameImage.trim()
                        ,getDataProduct(line).dt_nombre_es.trim()
                        ,getDataProduct(line).dt_nombre_in.trim()
                        ,currencyFormatter.format(Long.valueOf(getDataProduct(line).dt_precio.trim()))
                        ,getDataProduct(line).dt_descripcion_es.trim()
                        ,getDataProduct(line).dt_descripcion_in.trim()
                        ,getDataProduct(line).dt_type_product.trim()
                        ,getDataProduct(line).dt_num_articulos.trim()
                );
            }

            adminBaseDatos.closeBaseDtos();
        }catch (Exception e){

            return false;
        }


        return aLong>0?true:false;
    }


    private DataProduct getDataProduct(String in){
        DataProduct parse=new DataProduct();
        String dta[] = in.split(",");
        parse.setDt_id_producto(dta[0]);
        parse.setDt_nameImage(dta[1]);
        parse.setDt_nombre_es(dta[2]);
        parse.setDt_nombre_in(dta[3]);
        parse.setDt_precio(dta[4]);
        parse.setDt_descripcion_es(dta[5]);
        parse.setDt_descripcion_in(dta[6]);
        parse.setDt_type_product(dta[7]);
        parse.setDt_num_articulos(dta[8]);
        return parse;
    }

    private List<String> readFileInventario(File file) {
        Boolean val = false;
        boolean isTitle = false;
        String temp;
        List<String> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            while ((temp = reader.readLine()) != null){
                try {
                    if(isTitle)
                        data.add(temp);
                    else
                        isTitle = true;
                }catch (Exception e){
                    Log.e("hotel",e.getMessage());
                    return null;
                }
            }


        } catch (Exception e) {
            Log.e("hotel",e.getMessage());
            return null;
        }
        return data;
    }

    public Boolean isExite(){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(context);
        Boolean isex =adminBaseDatos.isExiteProductos();
        adminBaseDatos.closeBaseDtos();
        if(isex){
            return true;
        }else{
            return false;
        }
    }

    public void eliminarTabla(){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(context);
        adminBaseDatos.deleteTableProd();
    }

}
