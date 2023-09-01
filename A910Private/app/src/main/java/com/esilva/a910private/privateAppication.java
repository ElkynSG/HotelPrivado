package com.esilva.a910private;

import static com.esilva.a910private.Constants.BASE_CONNECTION;
import static com.esilva.a910private.Constants.SHARE_BASE;
import static com.esilva.a910private.Constants.TYPE_BT;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

import java.io.InputStream;
import java.io.OutputStream;

public class privateAppication extends Application {
    private static IDAL dal;
    private static Context appContext;
    private static BluetoothSocket socket = null;
    private static Boolean typeConnection;
    private static SharedPreferences preferences;

    public static Boolean getTypeConnection() {
        return typeConnection;
    }

    public static void setTypeConnection(Boolean typeConnection) {
        privateAppication.typeConnection = typeConnection;
        preferences.edit().putBoolean(BASE_CONNECTION,typeConnection).commit();
    }




    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        preferences = getSharedPreferences(SHARE_BASE,MODE_PRIVATE);
        typeConnection = preferences.getBoolean(BASE_CONNECTION,TYPE_BT);
        dal = getDal();
    }

    public static IDAL getDal(){
        if(dal == null){
            try {
                long start = System.currentTimeMillis();
                dal = NeptuneLiteUser.getInstance().getDal(appContext);
                Log.i("Test","get dal cost:"+(System.currentTimeMillis() - start)+" ms");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(appContext, "error occurred,DAL is null.", Toast.LENGTH_LONG).show();
            }
        }
        return dal;
    }

    public static BluetoothSocket getSocket() {
        return socket;
    }

    public static void setSocket(BluetoothSocket socket) {
        privateAppication.socket = socket;
    }
}
