package com.esilva.a910private;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btMenu;
    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btMenu = findViewById(R.id.menu);
        btMenu.setOnClickListener(this);
        countDownTimer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Boolean cone = privateAppication.getTypeConnection();
                Intent intent;
                if(cone)
                    intent = new Intent(MainActivity.this,UsbActivity.class);
                else
                    intent = new Intent(MainActivity.this,BluetoothActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.menu){
            showDialog();
        }
    }

    private void showDialog(){
        Dialog customDialog = new Dialog(MainActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.connect_dialog);


        Button bt_usb = customDialog.findViewById(R.id.bt_usb);
        bt_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                privateAppication.setTypeConnection(true);
                startActivity(new Intent(MainActivity.this,UsbActivity.class));
                finish();
            }
        });
        Button bt_bt = customDialog.findViewById(R.id.bt_bt);
        bt_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                privateAppication.setTypeConnection(false);
                startActivity(new Intent(MainActivity.this,BluetoothActivity.class));
                finish();
            }
        });

        customDialog.show();
        countDownTimer.cancel();
    }
}