package com.esilva.hotelprivado;

import static com.esilva.hotelprivado.Util.Constantes.DISCONNECT_TIMEOUT;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.fragments.LoginFragment;
import com.esilva.hotelprivado.fragments.RegistroFragment;

public class LoginActivity extends AppCompatActivity implements RegistroFragment.InterfeceRegistro, LoginFragment.InterfeceLogin {
    private  final String fraReg = "fragmentRegistro";
    private final String fraIngre = "fragmentIngreso";
    private FragmentTransaction transaction;
    private Fragment fragRegistro, fragIngreso;
    private static Dialog customDialog;
    private ImageView bt_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        bt_back = findViewById(R.id.backSelectLo);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fragRegistro = new RegistroFragment();
        fragIngreso = new LoginFragment();

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(this);
        if(adminBaseDatos.isExisteUsuario()){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentLogin, fragIngreso,fraIngre)
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentLogin, fragRegistro,fraReg)
                    .commit();
        }
        Toast.makeText(LoginActivity.this,"Campos Vacios",Toast.LENGTH_LONG);
    }

    @Override
    public void resRegistro(int result) {
        if(result==-1) {
            showDialog("Registro","Campos Vacios");
        }
        else if(result==-2) {
            showDialog("Registro","Contraseñas no coinciden");
        }else if(result == 0) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentLogin,fragIngreso)
                    .commit();
        }
        else {
            showDialog("Registro","Error " + String.valueOf(result));
        }
    }

    @Override
    public void resultLogin(int result) {
        if(result==-1) {
            showDialog("login","Campos Vacios");
        }else if(result==-2) {
            showDialog("login","Contraseñas no coinciden");
        }else if(result==-3) {
            showDialog("login","Contraseñas incorrecta");
        }else if(result == 0) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            this.finish();
        }
        else {
            showDialog("login","Error " + String.valueOf(result));;
        }
    }

    private void showDialog(String title,String message){
        customDialog = new Dialog(LoginActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.login_dialog);

        TextView tv_titulo = customDialog.findViewById(R.id.diTitleialog);
        TextView tv_message = customDialog.findViewById(R.id.txt_dialog);
        tv_titulo.setText(title);
        tv_message.setText(message);
        customDialog.findViewById(R.id.btn_dialog_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();

    }

    private void finalizar(){
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        this.finish();
    }

    private static Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.v("TIMEOUT", "DECONEXION 2 ");
            return true;
        }
    });



    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            Log.v("TIMEOUT", "DECONEXION");
            finalizar();
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        Log.v("TIMEOUT", "YO");
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
    /////////////////////////////////////////////////
}