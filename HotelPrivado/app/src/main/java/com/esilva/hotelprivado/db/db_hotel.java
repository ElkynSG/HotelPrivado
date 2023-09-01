package com.esilva.hotelprivado.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class db_hotel extends SQLiteOpenHelper {
    private static final String name_db = "db_hotel";

    private static final int version = 1;

    //  TABLA  PRODUCTOS
    public static final String name_table = "product";

    public static final String base_id="idProduct";
    public static final String base_name_ima="nameIma";
    public static final String base_nombre_es="nameEsp";
    public static final String base_nombre_in="nameIng";
    public static final String base_precio="Price";
    public static final String base_descripcion_es="descripEsp";
    public static final String base_descripcion_in="descripIng";
    public static final String base_Type_Produc="typeProduct";
    public static final String base_num_articulos="numItems";
    public static final String base_num_arti_vendidos="numVendido";

    //  TABLA  USUARIO
    public static final String TABLE_USUARIO = "tb_usuario";

    public static final String BASE_USUARIO = "usuario";
    public static final String BASE_CONTRASENA="contrasena";

    //  TABLA  REPORTE
    public static final String REP_NAME_TABLE = "ventas";

    public static final String REP_NUM_APROB="numAprob";
    public static final String REP_FECHA="fecha";
    public static final String REP_COD_PROD="cod_prod";
    public static final String REP_NOM_PROD="nameProd";
    public static final String REP_PRECIO="precio";
    public static final String REP_CANTIDAD="canProd";
    public static final String REP_FECHA_TAB="fecha_tab";
    public static final String REP_HORA_TAB="hora_tab";
    public static final String REP_TOTAL="total";
    public static final String REP_TYPE="type_prod";
    public static final String REP_RECIBO="recibo";


    public db_hotel(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, name_db, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        String base = "CREATE TABLE "+ name_table+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                base_id              +" text, " +
                base_name_ima        +" text, " +
                base_nombre_es       +" text, " +
                base_nombre_in       +" text, " +
                base_precio          +" text, " +
                base_descripcion_es  +" text, " +
                base_descripcion_in  +" text, " +
                base_Type_Produc     +" text, " +
                base_num_articulos   +" text, " +
                base_num_arti_vendidos+" text)";
        BaseDeDatos.execSQL(base);

        String baseUsuario = "CREATE TABLE "+ TABLE_USUARIO+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BASE_USUARIO                +" text, " +
                BASE_CONTRASENA             +" text)";
        BaseDeDatos.execSQL(baseUsuario);

        String reporte = "CREATE TABLE "+ REP_NAME_TABLE+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REP_NUM_APROB   +" text, " +
                REP_FECHA       +" text, " +
                REP_COD_PROD    +" text, " +
                REP_NOM_PROD    +" text, " +
                REP_PRECIO      +" text, " +
                REP_CANTIDAD    +" text, " +
                REP_FECHA_TAB   +" text, " +
                REP_HORA_TAB    +" text, " +
                REP_TOTAL       +" text, " +
                REP_TYPE        +" text, " +
                REP_RECIBO      +" text)";
        BaseDeDatos.execSQL(reporte);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}