package com.esilva.hotelprivado.Util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esilva.hotelprivado.R;

public class util {

    public static String getVersionName(Context ctx){
        String version = null;
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    @SuppressLint("ResourceAsColor")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showToast(int image, String mensaje,Activity context){
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custumer,
                (ViewGroup) context.findViewById(R.id.layout_toast));

        ImageView image1 = (ImageView) layout.findViewById(R.id.im_toast);
        image1.setImageResource(image);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast);
        text.setBackgroundColor(context.getResources().getColor(R.color.white));
        text.setText(mensaje);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showToast(int image, int idRes,Activity context){
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custumer,
                (ViewGroup) context.findViewById(R.id.layout_toast));

        ImageView image1 = (ImageView) layout.findViewById(R.id.im_toast);
        image1.setImageResource(image);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast);
        text.setBackgroundColor(context.getResources().getColor(R.color.white));
        text.setText(idRes);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
