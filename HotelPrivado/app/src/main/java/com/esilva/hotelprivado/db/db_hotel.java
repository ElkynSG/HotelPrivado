package com.esilva.hotelprivado.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class db_hotel extends SQLiteOpenHelper {
    private static final String name_db = "db_hotel_privado";

    private static final int version = 3;

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

    //  TABLA  INVENTARIO
    public static final String INV_NAME_TABLE = "inventario";

    public static final String INV_NUM_APROB="numAprob";
    public static final String INV_FECHA="fecha";
    public static final String INV_COD_PROD="cod_prod";
    public static final String INV_NOM_PROD="nameProd";
    public static final String INV_PRECIO="precio";
    public static final String INV_CANTIDAD="canProd";
    public static final String INV_FECHA_TAB="fecha_tab";
    public static final String INV_HORA_TAB="hora_tab";
    public static final String INV_TOTAL="total";
    public static final String INV_TYPE="type_prod";
    public static final String INV_RECIBO="recibo";

    //  TABLA  VENTAS
    public static final String VEN_NAME_TABLE = "ventas";

    public static final String VEN_NUM_APROB="numAprob";
    public static final String VEN_FECHA="fecha";
    public static final String VEN_COD_PROD="cod_prod";
    public static final String VEN_NOM_PROD="nameProd";
    public static final String VEN_PRECIO="precio";
    public static final String VEN_CANTIDAD="canProd";
    public static final String VEN_FECHA_TAB="fecha_tab";
    public static final String VEN_HORA_TAB="hora_tab";
    public static final String VEN_TOTAL="total";
    public static final String VEN_TYPE="type_prod";
    public static final String VEN_RECIBO="recibo";
    public static final String VEN_ID_PRODUCTO="id_producto";

    //  TABLA  PENDIENTES
    public static final String VEN_NAME_PEN_TABLE = "pendientes";
    public static final String VEN_ID_TR="id_transac";


    public db_hotel(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, name_db, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        String base = "CREATE TABLE IF NOT EXISTS "+ name_table+"(" +
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

        String baseUsuario = "CREATE TABLE IF NOT EXISTS "+ TABLE_USUARIO+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BASE_USUARIO                +" text, " +
                BASE_CONTRASENA             +" text)";
        BaseDeDatos.execSQL(baseUsuario);

        String reporte = "CREATE TABLE IF NOT EXISTS "+ INV_NAME_TABLE+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                INV_NUM_APROB   +" text, " +
                INV_FECHA       +" text, " +
                INV_COD_PROD    +" text, " +
                INV_NOM_PROD    +" text, " +
                INV_PRECIO      +" text, " +
                INV_CANTIDAD    +" text, " +
                INV_FECHA_TAB   +" text, " +
                INV_HORA_TAB    +" text, " +
                INV_TOTAL       +" text, " +
                INV_TYPE        +" text, " +
                INV_RECIBO      +" text)";
        BaseDeDatos.execSQL(reporte);

        String ventas = "CREATE TABLE IF NOT EXISTS "+ VEN_NAME_TABLE+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VEN_NUM_APROB   +" text, " +
                VEN_FECHA       +" text, " +
                VEN_COD_PROD    +" text, " +
                VEN_NOM_PROD    +" text, " +
                VEN_PRECIO      +" text, " +
                VEN_CANTIDAD    +" text, " +
                VEN_FECHA_TAB   +" text, " +
                VEN_HORA_TAB    +" text, " +
                VEN_TOTAL       +" text, " +
                VEN_TYPE        +" text, " +
                VEN_RECIBO      +" text, " +
                VEN_ID_PRODUCTO +" text)";
        BaseDeDatos.execSQL(ventas);

        String pendientes = "CREATE TABLE IF NOT EXISTS "+ VEN_NAME_PEN_TABLE+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VEN_NUM_APROB   +" text, " +
                VEN_FECHA       +" text, " +
                VEN_COD_PROD    +" text, " +
                VEN_NOM_PROD    +" text, " +
                VEN_PRECIO      +" text, " +
                VEN_CANTIDAD    +" text, " +
                VEN_FECHA_TAB   +" text, " +
                VEN_HORA_TAB    +" text, " +
                VEN_TOTAL       +" text, " +
                VEN_TYPE        +" text, " +
                VEN_RECIBO      +" text, " +
                VEN_ID_PRODUCTO +" text, " +
                VEN_ID_TR       +" text)";
        BaseDeDatos.execSQL(pendientes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}