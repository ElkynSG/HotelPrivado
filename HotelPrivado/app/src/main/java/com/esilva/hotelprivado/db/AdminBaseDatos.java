package com.esilva.hotelprivado.db;


import static com.esilva.hotelprivado.db.db_hotel.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.esilva.hotelprivado.fragments.ItemsFragment;

import java.util.ArrayList;
import java.util.List;

public class AdminBaseDatos {

    private Context context;
    private SQLiteDatabase BaseDeDatos;

    public AdminBaseDatos(Context context) {
        this.context = context;
        db_hotel admin = new db_hotel(context, null);
        BaseDeDatos = admin.getWritableDatabase();
    }

    ///////////////////////////    TABLA PRODUCTOS    ////////////////////////////////////////////////////////
    public long insert(String id,String nameIma, String nombre_espanol, String nombre_ingles, String precio, String descri_espanol, String descri_ingles, String typeProduct, String num_items ){
        try {
            ContentValues registro = new ContentValues();
            registro.put(base_id, id);
            registro.put(db_hotel.base_name_ima, nameIma);
            registro.put(db_hotel.base_nombre_es, nombre_espanol);
            registro.put(db_hotel.base_nombre_in, nombre_ingles);
            registro.put(db_hotel.base_precio, precio);
            registro.put(db_hotel.base_descripcion_es, descri_espanol);
            registro.put(db_hotel.base_descripcion_in, descri_ingles);
            registro.put(db_hotel.base_Type_Produc, typeProduct);
            registro.put(db_hotel.base_num_articulos, num_items);
            registro.put(db_hotel.base_num_arti_vendidos,"0");

            return BaseDeDatos.insert(db_hotel.name_table,null,registro);

        }catch (Exception e){
            return 0;
        }
    }

    public int deleteTableProd(){
        return  BaseDeDatos.delete(db_hotel.name_table, null, null);
    }

    public List<DataProduct> getAllRows(){
        List<DataProduct> productos = new ArrayList<DataProduct>();

        try {
            Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM "+ db_hotel.name_table,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                DataProduct numItem = new DataProduct();
                numItem.setDt_consecutivo(fila.getString(0));
                numItem.setDt_id_producto(fila.getString(1));
                numItem.setDt_nameImage(fila.getString(2));
                numItem.setDt_nombre_es(fila.getString(3));
                numItem.setDt_nombre_in(fila.getString(4));
                numItem.setDt_precio(fila.getString(5));
                numItem.setDt_descripcion_es(fila.getString(6));
                numItem.setDt_descripcion_in(fila.getString(7));
                numItem.setDt_type_product(fila.getString(8));
                numItem.setDt_num_articulos(fila.getString(9));
                numItem.setDt_num_vendidos(fila.getString(10));

                productos.add(numItem);
            }
        }catch (Exception e){
            return null;
        }

        return productos;
    }

    public List<DataProduct> getProductoTipo(int type){
        List<DataProduct> productos = new ArrayList<DataProduct>();

        try {
            Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM "+ db_hotel.name_table,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                if(Integer.valueOf(fila.getString(8)) == type) {
                    DataProduct numItem = new DataProduct();
                    numItem.setDt_consecutivo(fila.getString(0));
                    numItem.setDt_id_producto(fila.getString(1));
                    numItem.setDt_nameImage(fila.getString(2));
                    numItem.setDt_nombre_es(fila.getString(3));
                    numItem.setDt_nombre_in(fila.getString(4));
                    numItem.setDt_precio(fila.getString(5));
                    numItem.setDt_descripcion_es(fila.getString(6));
                    numItem.setDt_descripcion_in(fila.getString(7));
                    numItem.setDt_type_product(fila.getString(8));
                    numItem.setDt_num_articulos(fila.getString(9));
                    numItem.setDt_num_vendidos(fila.getString(10));

                    productos.add(numItem);
                }
            }
        }catch (Exception e){
            return null;
        }
        return productos;
    }

    public Boolean updateProducto(DataProduct product){
        try {
            String sentence = "UPDATE "+ db_hotel.name_table+ " SET "+
                    base_num_articulos+"=\""+product.dt_num_articulos+"\","+
                    base_num_arti_vendidos+"=\""+product.dt_num_vendidos +
                    "\" WHERE "+base_id+"=\""+ product.dt_id_producto+"\"";
            Log.v("sentence",sentence);
            BaseDeDatos.execSQL(sentence);
        }catch (Exception e){
            return false;
        }

        return true;

    }

    public ArrayList<Integer> getProductoTabla(DataProduct product){
        ArrayList<Integer> numArticulo = new ArrayList<>();
        String prod;
        try {
            String sentence = "SELECT "+base_num_articulos+","+base_num_arti_vendidos+" FROM "+ db_hotel.name_table+ " WHERE "+base_id+"=\""+product.dt_consecutivo+"\"";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);
            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            numArticulo.add(Integer.valueOf(fila.getString(0)));
            numArticulo.add(Integer.valueOf(fila.getString(1)));
        }catch (Exception e){
            Log.v("BASEDATOS",e.getMessage());
            return null;
        }
        return numArticulo;
    }

    public DataProduct getProductoTabla(DataVentas product){
        DataProduct numItem;
        try {
            String sentence = "SELECT * FROM "+ db_hotel.name_table+ " WHERE "+base_id+"=\""+product.getIdProducto()+"\"";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);
            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            fila.moveToFirst();
            numItem = new DataProduct();
            numItem.setDt_consecutivo(fila.getString(0));
            numItem.setDt_id_producto(fila.getString(1));
            numItem.setDt_nameImage(fila.getString(2));
            numItem.setDt_nombre_es(fila.getString(3));
            numItem.setDt_nombre_in(fila.getString(4));
            numItem.setDt_precio(fila.getString(5));
            numItem.setDt_descripcion_es(fila.getString(6));
            numItem.setDt_descripcion_in(fila.getString(7));
            numItem.setDt_type_product(fila.getString(8));
            numItem.setDt_num_articulos(fila.getString(9));
            numItem.setDt_num_vendidos(fila.getString(10));
        }catch (Exception e){
            Log.v("BASEDATOS",e.getMessage());
            return null;
        }
        return numItem;
    }

    public Boolean isExiteProductos(){
        try {
            Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM "+ db_hotel.name_table,null);

            if(fila == null )
                return false;

            if(!fila.moveToFirst())
                return false;

        }catch (Exception e){
            return false;
        }

        return true;
    }

    ///////////////////////////    TABLA DE USUARIO    ////////////////////////////////////////////////////////

    public long insertUsuario(String usuario,String contrasena){
        try {
            ContentValues registro = new ContentValues();
            registro.put(db_hotel.BASE_USUARIO, usuario);
            registro.put(BASE_CONTRASENA, contrasena);


            return BaseDeDatos.insert(db_hotel.TABLE_USUARIO,null,registro);

        }catch (Exception e){
            return 0;
        }
    }

    public DataUsuario getUsurio(){
        DataUsuario dataUsuario;
        try {
            Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM "+ db_hotel.TABLE_USUARIO,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            dataUsuario = new DataUsuario();
            dataUsuario.setUsUsuario(fila.getString(1));
            dataUsuario.setUsContrasena(fila.getString(2));

        }catch (Exception e){
            return null;
        }

        return dataUsuario;
    }

    public Boolean isExisteUsuario(){
        {
            try {
                Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM "+ db_hotel.TABLE_USUARIO,null);
                if(fila == null )
                    return false;

                if(!fila.moveToFirst())
                    return false;

            }catch (Exception e){
                return false;
            }

            return true;
        }
    }

    public Boolean updateUser(String pass){

        try {
            String sentence = "UPDATE "+ db_hotel.TABLE_USUARIO+ " SET "+BASE_CONTRASENA+"=\""+pass+"\" WHERE ID=\"1\"";
            Log.v("sentence",sentence);
            BaseDeDatos.execSQL(sentence);
        }catch (Exception e){
            return false;
        }

        return true;

    }

    //////////////////////////////    INVENTARIO    ///////////////////////////////////////

    public void deleteInventario(){
        BaseDeDatos.delete(db_hotel.INV_NAME_TABLE,null,null);
    }

    public long insertInventario(String numAproba,String fecha,String codProdu,String nombreProd,String precio,String cantidad,String fechaTab,String horaTab,String typeProdu,String recibo){
        int totalObj = Integer.valueOf(cantidad) * Integer.valueOf(precio.replace("$","").replace(".",""));
        ContentValues registro = new ContentValues();
        registro.put(db_hotel.INV_NUM_APROB, numAproba);
        registro.put(db_hotel.INV_FECHA, fecha);
        registro.put(db_hotel.INV_COD_PROD, codProdu);
        registro.put(db_hotel.INV_NOM_PROD, nombreProd);
        registro.put(db_hotel.INV_PRECIO, precio);
        registro.put(db_hotel.INV_CANTIDAD, cantidad);
        registro.put(db_hotel.INV_FECHA_TAB, fechaTab);
        registro.put(db_hotel.INV_HORA_TAB, horaTab);
        registro.put(db_hotel.INV_TOTAL, String.valueOf(totalObj));
        registro.put(db_hotel.INV_TYPE, typeProdu);
        registro.put(db_hotel.INV_RECIBO, recibo);
        return BaseDeDatos.insert(db_hotel.INV_NAME_TABLE,null,registro);
    }

    public List<DataVentas> getAllInventario(boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String sentence = "SELECT * FROM "+ db_hotel.INV_NAME_TABLE;
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));

                if(conAlcohol){
                    if(numItem.getTypeProducto().equals("1")){
                        ventas.add(numItem);
                        continue;
                    }
                }

                if(sinAlcohol){
                    if(numItem.getTypeProducto().equals("2")){
                        ventas.add(numItem);
                        continue;
                    }
                }

                if(snacks){
                    if(numItem.getTypeProducto().equals("3")){
                        ventas.add(numItem);
                        continue;
                    }
                }

                if(souvenirs){
                    if(numItem.getTypeProducto().equals("4")){
                        ventas.add(numItem);
                        continue;
                    }
                }
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getAllInventarioNumAproba(String numAprobacion){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String sentence = "SELECT * FROM "+ db_hotel.INV_NAME_TABLE+" WHERE "+INV_NUM_APROB+"="+numAprobacion;
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));

                ventas.add(numItem);
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getfechaIniInventario(String fechaIni, String horaIni,boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        List<DataVentas> ventas = new ArrayList<DataVentas>();
        String[] hoMin = horaIni.split(":");
        int intHora = Integer.valueOf(hoMin[0]);
        int intMin = Integer.valueOf(hoMin[1]);
        Boolean isSave;

        try {
            String sentence = "SELECT * FROM "+ db_hotel.INV_NAME_TABLE+" WHERE "+INV_FECHA_TAB+">='"+fechaIni+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                isSave = false;
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));

                if(fechaIni.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHora == intHoraBase && intMinBase >= intMin){
                        isSave=true;
                    }else if(intHoraBase > intHora){
                        isSave=true;
                    }else{
                        Log.v("hotel","hora menor "+numItem.getRepHoraTab());
                    }
                }else{
                    isSave=true;
                }

                if(isSave){
                    if(conAlcohol){
                        if(numItem.getTypeProducto().equals("1")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(sinAlcohol){
                        if(numItem.getTypeProducto().equals("2")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(snacks){
                        if(numItem.getTypeProducto().equals("3")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(souvenirs){
                        if(numItem.getTypeProducto().equals("4")){
                            ventas.add(numItem);
                            continue;
                        }
                    }
                }

            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getfechaFiniInventario(String fechaFin, String horaFin,boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        List<DataVentas> ventas = new ArrayList<DataVentas>();
        String[] hoMin = horaFin.split(":");
        int intHora = Integer.valueOf(hoMin[0]);
        int intMin = Integer.valueOf(hoMin[1]);
        Boolean isSave;

        try {
            String sentence = "SELECT * FROM "+ db_hotel.INV_NAME_TABLE+" WHERE "+INV_FECHA_TAB+"<='"+fechaFin+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                isSave = false;
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));

                if(fechaFin.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHora == intHoraBase && intMinBase <= intMin){
                        isSave = true;
                    }else if(intHoraBase < intHora){
                        isSave = true;
                    }else{
                        Log.v("hotel","hora mayor "+numItem.getRepHoraTab());
                    }
                }else{
                    isSave = true;
                }

                if(isSave){
                    if(conAlcohol){
                        if(numItem.getTypeProducto().equals("1")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(sinAlcohol){
                        if(numItem.getTypeProducto().equals("2")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(snacks){
                        if(numItem.getTypeProducto().equals("3")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(souvenirs){
                        if(numItem.getTypeProducto().equals("4")){
                            ventas.add(numItem);
                            continue;
                        }
                    }
                }

            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getfechaPartialInventa(String fechaIni, String horaIni,String fechaFin, String horaFin,
                                                 boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        Boolean isIni,isFin;
        List<DataVentas> ventas = new ArrayList<DataVentas>();
        String[] hoMinIni = horaIni.split(":");
        int intHoraIni = Integer.valueOf(hoMinIni[0]);
        int intMinIni = Integer.valueOf(hoMinIni[1]);
        String[] hoMinFin = horaFin.split(":");
        int intHoraFin = Integer.valueOf(hoMinFin[0]);
        int intMinFin = Integer.valueOf(hoMinFin[1]);

        try {
            String sentence = "SELECT * FROM "+ db_hotel.INV_NAME_TABLE+" WHERE "+INV_FECHA_TAB+">='"+fechaIni +"' and "+INV_FECHA_TAB+"<='"+fechaFin+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                isFin =false;
                isIni =false;

                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));

                if(fechaIni.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHoraIni == intHoraBase && intMinBase >= intMinIni){
                        isIni=true;
                    }else if(intHoraBase > intHoraIni){
                        isIni=true;
                    }else{
                        Log.v("hotel","hora menor "+numItem.getRepHoraTab());
                    }
                }else{
                    isIni=true;
                }


                if(fechaFin.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHoraFin == intHoraBase && intMinBase <= intMinFin){
                        isFin = true;
                    }else if(intHoraBase < intHoraFin){
                        isFin = true;
                    }else{
                        Log.v("hotel","hora mayor "+numItem.getRepHoraTab());
                    }
                }else{
                    isFin = true;
                }



                if((isFin && isIni) ){

                        if(conAlcohol){
                            if(numItem.getTypeProducto().equals("1")){
                                ventas.add(numItem);
                                continue;
                            }
                        }

                        if(sinAlcohol){
                            if(numItem.getTypeProducto().equals("2")){
                                ventas.add(numItem);
                                continue;
                            }
                        }

                        if(snacks){
                            if(numItem.getTypeProducto().equals("3")){
                                ventas.add(numItem);
                                continue;
                            }
                        }

                    if(souvenirs){
                        if(numItem.getTypeProducto().equals("4")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                }

            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    //////////////////////////////    HISTORICO DE VENTAS    ///////////////////////////////////////

    public void deleteVentas(){
        BaseDeDatos.delete(VEN_NAME_TABLE,null,null);
    }

    public long insertVentas(String numAproba,String fecha,String codProdu,String nombreProd,String precio,String cantidad,String fechaTab,String horaTab,String typeProdu,String recibo,String idProducto){
        int totalObj = Integer.valueOf(cantidad) * Integer.valueOf(precio.replace("$","").replace(".",""));
        ContentValues registro = new ContentValues();
        registro.put(db_hotel.VEN_NUM_APROB, numAproba);
        registro.put(db_hotel.VEN_FECHA, fecha);
        registro.put(db_hotel.VEN_COD_PROD, codProdu);
        registro.put(db_hotel.VEN_NOM_PROD, nombreProd);
        registro.put(db_hotel.VEN_PRECIO, precio);
        registro.put(db_hotel.VEN_CANTIDAD, cantidad);
        registro.put(db_hotel.VEN_FECHA_TAB, fechaTab);
        registro.put(db_hotel.VEN_HORA_TAB, horaTab);
        registro.put(db_hotel.VEN_TOTAL, String.valueOf(totalObj));
        registro.put(db_hotel.VEN_TYPE, typeProdu);
        registro.put(db_hotel.VEN_RECIBO, recibo);
        registro.put(VEN_ID_PRODUCTO, idProducto);
        return BaseDeDatos.insert(db_hotel.VEN_NAME_TABLE,null,registro);
    }

    public long insertVentasData(List<DataVentas> data){
        int cantidad = 0;
        for (DataVentas dt: data) {
            ContentValues registro = new ContentValues();
            registro.put(db_hotel.VEN_NUM_APROB, dt.getRepAproba());
            registro.put(db_hotel.VEN_FECHA, dt.getRepFecha());
            registro.put(db_hotel.VEN_COD_PROD, dt.getRepCodProd());
            registro.put(db_hotel.VEN_NOM_PROD, dt.getRepNomProd());
            registro.put(db_hotel.VEN_PRECIO, dt.getRepPrecio());
            registro.put(db_hotel.VEN_CANTIDAD, dt.getRepCantidad());
            registro.put(db_hotel.VEN_FECHA_TAB, dt.getRepFechaTab());
            registro.put(db_hotel.VEN_HORA_TAB, dt.getRepHoraTab());
            registro.put(db_hotel.VEN_TOTAL, dt.getRepTotal());
            registro.put(db_hotel.VEN_TYPE, dt.getTypeProducto());
            registro.put(db_hotel.VEN_RECIBO, dt.getRecibo());
            registro.put(db_hotel.VEN_ID_PRODUCTO, dt.getIdProducto());
            BaseDeDatos.insert(db_hotel.VEN_NAME_TABLE,null,registro);
            cantidad++;
        }
        return cantidad;
    }

    public List<DataVentas> getAllVentas(boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String sentence = "SELECT * FROM "+ db_hotel.VEN_NAME_TABLE;
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));

                if(conAlcohol){
                    if(numItem.getTypeProducto().equals("1")){
                        ventas.add(numItem);
                        continue;
                    }
                }

                if(sinAlcohol){
                    if(numItem.getTypeProducto().equals("2")){
                        ventas.add(numItem);
                        continue;
                    }
                }

                if(snacks){
                    if(numItem.getTypeProducto().equals("3")){
                        ventas.add(numItem);
                        continue;
                    }
                }

                if(souvenirs){
                    if(numItem.getTypeProducto().equals("4")){
                        ventas.add(numItem);
                        continue;
                    }
                }
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getAllVentasNumAproba(String numAprobacion){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String sentence = "SELECT * FROM "+ VEN_NAME_TABLE+" WHERE "+VEN_NUM_APROB+"="+numAprobacion;
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));

                ventas.add(numItem);
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getAllfechaVentas(String fecha){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String sentence = "SELECT * FROM "+ VEN_NAME_TABLE+" WHERE "+VEN_FECHA_TAB+"='"+fecha+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));
                ventas.add(numItem);
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getfechaIniVentas(String fechaIni, String horaIni,boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        List<DataVentas> ventas = new ArrayList<DataVentas>();
        String[] hoMin = horaIni.split(":");
        int intHora = Integer.valueOf(hoMin[0]);
        int intMin = Integer.valueOf(hoMin[1]);
        Boolean isSave;

        try {
            String sentence = "SELECT * FROM "+ VEN_NAME_TABLE+" WHERE "+VEN_FECHA_TAB+">='"+fechaIni+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                isSave = false;
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));

                if(fechaIni.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHora == intHoraBase && intMinBase >= intMin){
                        isSave=true;
                    }else if(intHoraBase > intHora){
                        isSave=true;
                    }else{
                        Log.v("hotel","hora menor "+numItem.getRepHoraTab());
                    }
                }else{
                    isSave=true;
                }

                if(isSave){
                    if(conAlcohol){
                        if(numItem.getTypeProducto().equals("1")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(sinAlcohol){
                        if(numItem.getTypeProducto().equals("2")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(snacks){
                        if(numItem.getTypeProducto().equals("3")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(souvenirs){
                        if(numItem.getTypeProducto().equals("4")){
                            ventas.add(numItem);
                            continue;
                        }
                    }
                }

            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getfechaFiniVentas(String fechaFin, String horaFin,boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        List<DataVentas> ventas = new ArrayList<DataVentas>();
        String[] hoMin = horaFin.split(":");
        int intHora = Integer.valueOf(hoMin[0]);
        int intMin = Integer.valueOf(hoMin[1]);
        Boolean isSave;

        try {
            String sentence = "SELECT * FROM "+ VEN_NAME_TABLE+" WHERE "+VEN_FECHA_TAB+"<='"+fechaFin+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                isSave = false;
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));

                if(fechaFin.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHora == intHoraBase && intMinBase <= intMin){
                        isSave = true;
                    }else if(intHoraBase < intHora){
                        isSave = true;
                    }else{
                        Log.v("hotel","hora mayor "+numItem.getRepHoraTab());
                    }
                }else{
                    isSave = true;
                }

                if(isSave){
                    if(conAlcohol){
                        if(numItem.getTypeProducto().equals("1")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(sinAlcohol){
                        if(numItem.getTypeProducto().equals("2")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(snacks){
                        if(numItem.getTypeProducto().equals("3")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(souvenirs){
                        if(numItem.getTypeProducto().equals("4")){
                            ventas.add(numItem);
                            continue;
                        }
                    }
                }

            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getfechaPartialVentas(String fechaIni, String horaIni,String fechaFin, String horaFin,
                                                   boolean conAlcohol,boolean sinAlcohol,boolean snacks,boolean souvenirs){
        Boolean isIni,isFin;
        List<DataVentas> ventas = new ArrayList<DataVentas>();
        String[] hoMinIni = horaIni.split(":");
        int intHoraIni = Integer.valueOf(hoMinIni[0]);
        int intMinIni = Integer.valueOf(hoMinIni[1]);
        String[] hoMinFin = horaFin.split(":");
        int intHoraFin = Integer.valueOf(hoMinFin[0]);
        int intMinFin = Integer.valueOf(hoMinFin[1]);

        try {
            String sentence = "SELECT * FROM "+ VEN_NAME_TABLE+" WHERE "+VEN_FECHA_TAB+">='"+fechaIni +"' and "+VEN_FECHA_TAB+"<='"+fechaFin+"'";
            Log.v("sentence",sentence);
            Cursor fila = BaseDeDatos.rawQuery(sentence,null);

            if(fila == null )
                return null;

            if(!fila.moveToFirst())
                return null;

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()){
                isFin =false;
                isIni =false;

                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));

                if(fechaIni.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHoraIni == intHoraBase && intMinBase >= intMinIni){
                        isIni=true;
                    }else if(intHoraBase > intHoraIni){
                        isIni=true;
                    }else{
                        Log.v("hotel","hora menor "+numItem.getRepHoraTab());
                    }
                }else{
                    isIni=true;
                }


                if(fechaFin.equals(numItem.getRepFechaTab())){
                    String[] hoMinBase = numItem.getRepHoraTab().split(":");
                    int intHoraBase = Integer.valueOf(hoMinBase[0]);
                    int intMinBase = Integer.valueOf(hoMinBase[1]);
                    if(intHoraFin == intHoraBase && intMinBase <= intMinFin){
                        isFin = true;
                    }else if(intHoraBase < intHoraFin){
                        isFin = true;
                    }else{
                        Log.v("hotel","hora mayor "+numItem.getRepHoraTab());
                    }
                }else{
                    isFin = true;
                }



                if((isFin && isIni) ){

                    if(conAlcohol){
                        if(numItem.getTypeProducto().equals("1")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(sinAlcohol){
                        if(numItem.getTypeProducto().equals("2")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(snacks){
                        if(numItem.getTypeProducto().equals("3")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                    if(souvenirs){
                        if(numItem.getTypeProducto().equals("4")){
                            ventas.add(numItem);
                            continue;
                        }
                    }

                }

            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

///////////////////////////////////    PENDIENTES    ///////////////////////////////////////

    public void deletePendientes(){
        BaseDeDatos.delete(VEN_NAME_PEN_TABLE,null,null);
    }

    public long insertVentasPend(DataVentas dataPnt){
        int totalObj = Integer.valueOf(dataPnt.getRepCantidad()) * Integer.valueOf(dataPnt.getRepPrecio().replace("$","").replace(".",""));
        ContentValues registro = new ContentValues();
        registro.put(db_hotel.VEN_NUM_APROB, dataPnt.getRepAproba());
        registro.put(db_hotel.VEN_FECHA, dataPnt.getRepFecha());
        registro.put(db_hotel.VEN_COD_PROD, dataPnt.getRepCodProd());
        registro.put(db_hotel.VEN_NOM_PROD, dataPnt.getRepNomProd());
        registro.put(db_hotel.VEN_PRECIO, dataPnt.getRepPrecio());
        registro.put(db_hotel.VEN_CANTIDAD, dataPnt.getRepCantidad());
        registro.put(db_hotel.VEN_FECHA_TAB, dataPnt.getRepFechaTab());
        registro.put(db_hotel.VEN_HORA_TAB, dataPnt.getRepHoraTab());
        registro.put(db_hotel.VEN_TOTAL, String.valueOf(totalObj));
        registro.put(db_hotel.VEN_TYPE, dataPnt.getTypeProducto());
        registro.put(db_hotel.VEN_RECIBO, dataPnt.getRecibo());
        registro.put(VEN_ID_PRODUCTO, dataPnt.getIdProducto());
        registro.put(VEN_ID_TR, dataPnt.getIdTr());
        return BaseDeDatos.insert(db_hotel.VEN_NAME_TABLE,null,registro);
    }

    public long insertAllVentasPend(List<DataProduct> dataPnt,String numAprobacion,String fecha,String fecha_tab,String hora_tab,String recibo,int idTr){
        long ret;
        for(DataProduct tm:dataPnt) {
            int totalObj = Integer.valueOf(tm.dt_num_articulos) * Integer.valueOf(tm.dt_precio.replace("$","").replace(".",""));
            ContentValues registro = new ContentValues();
            registro.put(db_hotel.VEN_NUM_APROB, numAprobacion);
            registro.put(db_hotel.VEN_FECHA, fecha);
            registro.put(db_hotel.VEN_COD_PROD, tm.dt_id_producto);
            registro.put(db_hotel.VEN_NOM_PROD, tm.dt_nombre_es);
            registro.put(db_hotel.VEN_PRECIO, tm.dt_precio);
            registro.put(db_hotel.VEN_CANTIDAD, tm.dt_num_articulos);
            registro.put(db_hotel.VEN_FECHA_TAB, fecha_tab);
            registro.put(db_hotel.VEN_HORA_TAB, hora_tab);
            registro.put(db_hotel.VEN_TOTAL, String.valueOf(totalObj));
            registro.put(db_hotel.VEN_TYPE, tm.dt_type_product);
            registro.put(db_hotel.VEN_RECIBO, recibo);
            registro.put(db_hotel.VEN_ID_PRODUCTO, tm.dt_consecutivo);
            registro.put(VEN_ID_TR, idTr);
            ret = BaseDeDatos.insert(VEN_NAME_PEN_TABLE,null,registro);
            Log.d("DP_DLOG","insertAllVentasPend "+ret);
        }
        return 0;
    }


    public int getCountVentasPent(){

        int Ret = 0;
        try {
            //String sentence = "SELECT * FROM "+ VEN_NAME_PEN_TABLE;
            String query = "SELECT COUNT(DISTINCT "+VEN_ID_TR+") AS num_id_tr FROM "+VEN_NAME_PEN_TABLE;
            Log.v("sentence",query);
            Cursor cursor = BaseDeDatos.rawQuery(query, null);
            // Mover el cursor al primer resultado
            if (cursor.moveToFirst()) {
                Ret = cursor.getInt(cursor.getColumnIndex("num_id_tr"));
            }
        }catch (Exception e){
            return 0;
        }
        return Ret;
    }

    public List<DataVentas> getAllIdTransacPen(int idTransac){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String query = "SELECT * FROM "+VEN_NAME_PEN_TABLE+" WHERE "+VEN_ID_TR+"="+String.valueOf(idTransac);
            Log.v("sentence",query);
            Cursor fila = BaseDeDatos.rawQuery(query, null);

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));
                numItem.setIdTr(fila.getInt(13));
                ventas.add(numItem);
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public List<DataVentas> getAllPen(){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String query = "SELECT * FROM "+VEN_NAME_PEN_TABLE;
            Log.v("sentence",query);
            Cursor fila = BaseDeDatos.rawQuery(query, null);

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
                DataVentas numItem = new DataVentas();

                numItem.setId(fila.getString(0));
                numItem.setRepAproba(fila.getString(1));
                numItem.setRepFecha(fila.getString(2));
                numItem.setRepCodProd(fila.getString(3));
                numItem.setRepNomProd(fila.getString(4));
                numItem.setRepPrecio(fila.getString(5));
                numItem.setRepCantidad(fila.getString(6));
                numItem.setRepFechaTab(fila.getString(7));
                numItem.setRepHoraTab(fila.getString(8));
                numItem.setRepTotal(fila.getString(9));
                numItem.setTypeProducto(fila.getString(10));
                numItem.setRecibo(fila.getString(11));
                numItem.setIdProducto(fila.getString(12));
                numItem.setIdTr(fila.getInt(13));

                ventas.add(numItem);
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public boolean deleteIdTransacPen(int idTransac){
        try {
            // Consulta para eliminar las ventas correspondientes al id_tr específico
            String whereClause = VEN_ID_TR+" = ?";
            Log.v("sentence",whereClause);
            String[] whereArgs = { String.valueOf(idTransac) };
            BaseDeDatos.delete(VEN_NAME_PEN_TABLE, whereClause, whereArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
/////////////////////////////////// ////////////    ///////////////////////////////////////


    public void closeBaseDtos(){
        BaseDeDatos.close();
    }
}
