package com.esilva.a910private.dataBD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class db_hotel extends SQLiteOpenHelper {
    private static final String name_db = "db_hotel_privado";

    private static final int version = 1;

    //  TABLA  VENTAS
    public static final String VEN_NAME_TABLE = "ventas";

    public static final String VEN_ID_TRANS="id_transac";
    public static final String VEN_STATE="state";
    public static final String VEN_COD_RESP="respuesta";
    public static final String VEN_COD_APROB="aprobacion";
    public static final String VEN_RECIBO="recibo";
    public static final String VEN_MONTO="monto";
    public static final String VEN_FECHA="fecha";



    public db_hotel(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, name_db, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {

        String ventas = "CREATE TABLE IF NOT EXISTS "+ VEN_NAME_TABLE+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VEN_ID_TRANS   +" INTEGER UNIQUE, " +
                VEN_STATE      +" INTEGER, " +
                VEN_COD_RESP   +" text, " +
                VEN_COD_APROB  +" text, " +
                VEN_RECIBO     +" text, " +
                VEN_MONTO      +" text, " +
                VEN_FECHA      +" text)";
        BaseDeDatos.execSQL(ventas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}