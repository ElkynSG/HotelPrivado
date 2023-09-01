package com.esilva.a910private;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.pax.dal.exceptions.PrinterDevException;


import java.util.ArrayList;

public class ImpresionActivity extends AppCompatActivity {
    private  int status;
    private ProgressDialog loader;
    private int greyValue = 300;
    public static final int TAG_Resultado = 3;

    public static final String TAG_PARAM_IMPRESION = "Param";


    private String[] Param = null;

    private Button action;
    private TextView txImpresion;
    public static IDAL dal = null;

    private IPrinter printer;

    ArrayList<Integer> siseLetter;
    ArrayList<Integer> isRever;
    ArrayList<String> cadena;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impresion);
        txImpresion = findViewById(R.id.tv_impresion);

        printer = privateAppication.getDal().getPrinter();

        Intent intent = getIntent();
        desEmpaquetarData(intent);

    }

    private void desEmpaquetarData( Intent intent) {
        Bundle extras = intent.getExtras();
        Param = extras.getStringArray(TAG_PARAM_IMPRESION);
        if(Param == null){
            Log.v("Impresion", "P1 null");
        }
        else {
            getData();
        }

    }

    private void getData() {
        siseLetter = new ArrayList<>();
        isRever = new ArrayList<>();
        cadena = new ArrayList<>();

        for(String data:Param){
            if(!data.isEmpty()) {
                int aline;
                siseLetter.add(Integer.valueOf(data.substring(0, 2)));
                isRever.add(Integer.valueOf(data.substring(2, 4)));
                aline = Integer.valueOf(data.substring(4, 6));
                if (aline == 01)
                    cadena.add(Centrar(data.substring(6), Integer.valueOf(data.substring(0, 2))));
                else
                    cadena.add(data.substring(6) + "\n");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loader = new ProgressDialog(this);
        loader.setCancelable(false);
        loader.setTitle("Impresion");
        loader.setMessage("Imprimiendo...");
        loader.show();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        imprimir();

    }

    private void imprimir() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    printer.init();
                    printer.printBitmap(BitmapFactory.decodeResource(ImpresionActivity.this.getResources(), R.drawable.print_logo));
                    printer.doubleHeight(false,false);
                    printer.doubleWidth(false,false);
                    printer.spaceSet(Byte.parseByte("1"),Byte.parseByte("1"));
                    //printer.fontSet(EFontTypeAscii.FONT_8_16, EFontTypeExtCode.FONT_24_48);
                    printer.setGray(greyValue);
                    //printer.setFontPath(Environment.getExternalStorageDirectory() + "/RedebanComercios/aritmo.ttf");

                    printer.printStr("\n",null);
                    for (int i=0;i<cadena.size();i++){
                        int size;
                        int isReverLe;
                        size = siseLetter.get(i);
                        isReverLe = isRever.get(i);

                        printer.invert(isReverLe==1?true:false);

                        if(size == 32){
                            printer.fontSet(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_24_48);
                        }else{
                            printer.fontSet(EFontTypeAscii.FONT_8_16, EFontTypeExtCode.FONT_24_48);
                        }
                        printer.printStr(cadena.get(i),null);

                    }

                    printer.printStr("\n \n \n \n \n \n \n \n",null);

                    status = printer.start();

                } catch (PrinterDevException e) {
                    e.printStackTrace();
                }
                txImpresion.post(new Runnable() {
                    @Override
                    public void run() {
                        txImpresion.setText("");
                        txImpresion.setText(statusCode2Str(status));
                        loader.dismiss();
                        setResult(status);
                        finish();
                    }
                });
            }
        }).start();
    }


    private String Centrar(String s1, int maxCaracter) {
        String cadena = "";
        String espacios = "";
        int sizeS1, sizeS2, res, space;
        if(s1.length() >maxCaracter){
            s1 = s1.substring(0,maxCaracter);
        }
        sizeS1 = s1.length();
        space = maxCaracter - (sizeS1);
        if (space >= 0) {
            for (int i = 0; i < space/2; i++) {
                espacios = espacios + " ";
            }
            cadena = espacios + s1 + espacios;
        } else {
            cadena = "error";
        }
        return cadena+"\n";
    }


    private String Alinear(String s1, String s2, int maxCaracter) {
        String cadena = "";
        String espacios = "";
        int sizeS1, sizeS2, res, space;
        sizeS1 = s1.length();
        sizeS2 = s2.length();
        space = maxCaracter - (sizeS1 + sizeS2);
        if (space > 0) {
            for (int i = 0; i < space; i++) {
                espacios = espacios + " ";
            }
            cadena = s1 + espacios + s2 ;
        } else {
            cadena = "error" ;
        }
        return cadena+ "\n";
    }

    private String[] centrarParrafo(String parrafo,int numCaracter){
        String[] result;
        String[] resultTemp = new String[10];
        String[] temp;
        String tempD="";
        String tempD2="";
        int lineas = 0;
        int numLineas = 0;
        if(parrafo == null){
            return  null;
        }
        temp = parrafo.split(" ");

        for(int i=0;i<temp.length;i++){
            tempD = tempD+" "+temp[i];
            if(tempD.length() > numCaracter){
                i--;
                resultTemp[numLineas] = Centrar(tempD2,numCaracter);
                numLineas++;
                tempD = "";
                tempD2 = "";
            }else {
                tempD2 = tempD;
            }
        }
        if(!tempD.equals("")){
            numLineas++;
            resultTemp[numLineas-1] = Centrar(tempD,numCaracter);
        }
        result = new String[numLineas];
        for(int i=0;i<numLineas;i++){
            result[i]=resultTemp[i];
        }
        return result;
    }

    public String statusCode2Str(int status){
        String res="";
        switch (status) {
            case 0:
                res = "Impresion exitosa";
                break;
            case 1:
                res = "Impresora ocupada";
                break;
            case 2:
                res = "Sin papel";
                break;
            case 3:
                res = "El formato de impresión de error de paquetes de datos";
                break;
            case 4:
                res = "mal funcionamiento de la impresora";
                break;
            case 8:
                res = "Impresora se sobrecalienta ";
                break;
            case 9:
                res = "voltaje de la impresora es demasiado baja";
                break;
            case 240:
                res = "La impresión esta sin terminar ";
                break;
            case 252:
                res = "La impresora no se ha instalado la librería de fuentes ";
                break;
            case 254:
                res = "paquete de datos es demasiado largo ";
                break;
            default:
                break;
        }
        return res;
    }
}