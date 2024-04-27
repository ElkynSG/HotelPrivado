package com.esilva.a910private.dataBD;


import static com.esilva.a910private.dataBD.db_hotel.VEN_COD_APROB;
import static com.esilva.a910private.dataBD.db_hotel.VEN_COD_RESP;
import static com.esilva.a910private.dataBD.db_hotel.VEN_FECHA;
import static com.esilva.a910private.dataBD.db_hotel.VEN_ID_TRANS;
import static com.esilva.a910private.dataBD.db_hotel.VEN_MONTO;
import static com.esilva.a910private.dataBD.db_hotel.VEN_NAME_TABLE;
import static com.esilva.a910private.dataBD.db_hotel.VEN_RECIBO;
import static com.esilva.a910private.dataBD.db_hotel.VEN_STATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

///////////////////////////    TABLA VENTAS    ////////////////////////////////////////////////////////
    public void deleteAllVentas(){
    BaseDeDatos.delete(VEN_NAME_TABLE,null,null);
}

    public boolean deleteIdTransac(int idTransac){
        try {
            // Consulta para eliminar las ventas correspondientes al id_tr específico
            String whereClause = VEN_ID_TRANS+" = ?";
            Log.v("sentence",whereClause);
            String[] whereArgs = { String.valueOf(idTransac) };
            BaseDeDatos.delete(VEN_NAME_TABLE, whereClause, whereArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public long insertVentas(DataVentas data){
        ContentValues registro = new ContentValues();
        registro.put(VEN_ID_TRANS,data.getId_transac());
        registro.put(VEN_STATE,data.getState());
        registro.put(VEN_COD_RESP,data.getCodResp());
        registro.put(VEN_COD_APROB,data.getNumAproba());
        registro.put(VEN_RECIBO,data.getRecibo());
        registro.put(VEN_MONTO,data.getMonto());
        registro.put(VEN_FECHA,data.getFecha());

        return BaseDeDatos.insert(VEN_NAME_TABLE,null,registro);
    }

    public List<DataVentas> getAllTransac(){
        List<DataVentas> ventas = new ArrayList<DataVentas>();

        try {
            String query = "SELECT * FROM "+VEN_NAME_TABLE;
            Log.v("sentence",query);
            Cursor fila = BaseDeDatos.rawQuery(query, null);

            for(fila.moveToFirst(); !fila.isAfterLast(); fila.moveToNext()) {
                DataVentas numItem = new DataVentas();

                numItem.setId_transac(fila.getInt(1));
                numItem.setState(fila.getInt(2));
                numItem.setCodResp(fila.getString(3));
                numItem.setNumAproba(fila.getString(4));
                numItem.setRecibo(fila.getString(5));
                numItem.setMonto(fila.getString(6));
                numItem.setFecha(fila.getString(7));
                ventas.add(numItem);
            }
        }catch (Exception e){
            return null;
        }
        return ventas;
    }

    public boolean updateTrans(DataVentas data){
        try {
            ContentValues registro = new ContentValues();
            registro.put(VEN_ID_TRANS,data.getId_transac());
            registro.put(VEN_STATE,data.getState());
            registro.put(VEN_COD_RESP,data.getCodResp());
            registro.put(VEN_COD_APROB,data.getNumAproba());
            registro.put(VEN_RECIBO,data.getRecibo());
            registro.put(VEN_MONTO,data.getMonto());
            registro.put(VEN_FECHA,data.getFecha());

            String whereClause = VEN_ID_TRANS+" = ?";
            String[] whereArgs = { String.valueOf(data.getId_transac()) };

            BaseDeDatos.update("ventas", registro, whereClause, whereArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////


    public void closeBaseDtos(){
        BaseDeDatos.close();
    }
}
