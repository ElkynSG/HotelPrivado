package com.esilva.hotelprivado.Util;


import static com.esilva.hotelprivado.Util.Constantes.FILE_REPORT;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_FIN;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_INI;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_PARTIAL;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataVentas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Reporte {
    private Activity mActivity;
    private Context mContext;
    private int TotalCon,TotalSin,TotalSnack,TotalSouvenirs;
    private String nameFile;
    private Boolean isConAlcohol,isSinAlcohol,isSnacks,isSouvenir;
    private int typeReporte;
    private String date_start,date_end,hour_start,hour_end;
    private String fileDataOut;
    private Boolean isMessage;

    public Reporte(Activity activity, Context context,
                   Boolean conAlcohol,Boolean sinAlcohol,Boolean snacks,Boolean souvenir) {
        this.mActivity = activity;
        this.mContext = context;
        this.isConAlcohol = conAlcohol;
        this.isSinAlcohol = sinAlcohol;
        this.isSnacks = snacks;
        this.isSouvenir = souvenir;

        fileDataOut = "";
        isMessage = true;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    private List<DataVentas> arrayVentas;

    private String createFile(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        return /*formattedDate+*/nameFile;
    }

    public Boolean grabaReporte(){
        try
        {
            File tarjeta = Environment.getExternalStorageDirectory();
            File file = new File(tarjeta.getAbsolutePath()+ "/"+FILE_REPORT, createFile());

            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.v("grabar",e.getMessage());
                }
            }
/*
            FileWriter escritor = new FileWriter(file);
            escritor.write(fileDataOut);
            escritor.close();*/
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileDataOut.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();

        } catch (IOException ioe) {
            Log.v("grabar",ioe.getMessage());
            return false;
        }
        return true;
    }

    public void setDateTime(String dateStart,String dateEnd,String hourStart,String hourEnd){
        this.date_start = dateStart;
        this.date_end = dateEnd;
        this.hour_start = hourStart;
        this.hour_end = hourEnd;

        Log.v("setDateTime", date_start+" "+hour_start);
    }
    public Boolean generarReporte(int type) {
        this.typeReporte = type;
        return  getDataReporte();
    }
    private Boolean getDataReporte(){
        List<DataVentas> dataVentas=null;
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(mContext);
        switch (typeReporte){
            case REPORT_ALL:
                dataVentas = adminBaseDatos.getAllVentas(isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_PARTIAL:
                dataVentas = adminBaseDatos.getfechaPartialentas(date_start, hour_start,date_end, hour_end,isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_FIN:
                dataVentas = adminBaseDatos.getfechaFinVentas(date_end, hour_end,isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_INI:
                dataVentas = adminBaseDatos.getfechaIniVentas(date_start, hour_start,isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            default:
                dataVentas=null;
                break;
        }
        adminBaseDatos.closeBaseDtos();
        return armaDataString(dataVentas);
    }

    public void setIsShowMessage(Boolean isSMS){
        isMessage = isSMS;
    }
    public Boolean armaDataString(List<DataVentas> dataVentas) {
        if(dataVentas==null || dataVentas.size()<1) {
            if(mActivity!= null && isMessage)
                util.showToast(R.drawable.fail,"Sin DATOS", mActivity);
            return false;
        }

        TotalCon=0;
        TotalSin=0;
        TotalSnack=0;
        TotalSouvenirs=0;

        fileDataOut = "Numero de Aprobacion,Fecha,Hora,Nombre,Cantidad,Subtotal\n";

        if(isConAlcohol){
            fileDataOut+="Con Alcohol\n";
            fileDataOut+=reportConAlcohol(dataVentas);
        }

        if(isSinAlcohol){
            fileDataOut+="Sin Alcohol\n";
            fileDataOut+=reportSinAlcohol(dataVentas);
        }

        if(isSnacks){
            fileDataOut+="Snacks\n";
            fileDataOut+=reportSnack(dataVentas);
        }

        if(isSouvenir){
            fileDataOut+="Souvenirs\n";
            fileDataOut+=reportSouvenirs(dataVentas);
        }

        fileDataOut += "\n,,,,Gran Total,"+String.valueOf(TotalCon+TotalSin+TotalSnack+TotalSouvenirs)+"\n";

        return true;
    }

    private String reportSouvenirs(List<DataVentas> dataVentas){
        String repSovenirs="";
        TotalSouvenirs=0;

        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("4")) {
                repSovenirs +=  temp.getRepAproba() + "," + temp.getRepFechaTab() + "," + temp.getRepHoraTab() + "," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalSouvenirs += Integer.valueOf(temp.getRepTotal());
            }
        }
        repSovenirs += ",,,,Total,"+String.valueOf(TotalSouvenirs)+"\n";
        return repSovenirs;
    }

    private String reportSnack(List<DataVentas> dataVentas){
        String repSnack="";
        TotalSnack=0;
        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("3")) {
                repSnack +=  temp.getRepAproba() + "," + temp.getRepFechaTab() + "," + temp.getRepHoraTab() + "," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalSnack += Integer.valueOf(temp.getRepTotal());
            }
        }
        repSnack += ",,,,Total,"+String.valueOf(TotalSnack)+"\n";
        return repSnack;
    }

    private String reportConAlcohol(List<DataVentas> dataVentas){
        String repConAlcohol="";
        TotalCon=0;
        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("1")) {
                repConAlcohol += temp.getRepAproba() + "," +  temp.getRepFechaTab() + "," + temp.getRepHoraTab() + "," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalCon += Integer.valueOf(temp.getRepTotal());
            }
        }
        repConAlcohol += ",,,,Total,"+String.valueOf(TotalCon)+"\n";
        return repConAlcohol;
    }

    private String reportSinAlcohol(List<DataVentas> dataVentas){
        String repSinAlcohol="";
        TotalSin=0;
        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("2")) {
                repSinAlcohol += temp.getRepAproba() + "," + temp.getRepFechaTab() + "," + temp.getRepHoraTab() + "," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalSin += Integer.valueOf(temp.getRepTotal());
            }
        }
        repSinAlcohol += ",,,,Total,"+String.valueOf(TotalSin)+"\n";
        return repSinAlcohol;
    }


}
