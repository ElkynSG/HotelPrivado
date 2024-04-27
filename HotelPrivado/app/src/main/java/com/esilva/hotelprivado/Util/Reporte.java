package com.esilva.hotelprivado.Util;


import static com.esilva.hotelprivado.Util.Constantes.FILE_REPORT;
import static com.esilva.hotelprivado.Util.Constantes.PACKAGE_FILE;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_ALL;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_FIN;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_INI;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_PARTIAL;
import static com.esilva.hotelprivado.Util.Constantes.REPORT_TOTAL;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataVentas;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

    List<DataVentas> dataVentas=null;

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

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(mContext);
        switch (typeReporte){
            case REPORT_ALL:
                dataVentas = adminBaseDatos.getAllVentas(isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_PARTIAL:
                dataVentas = adminBaseDatos.getfechaPartialVentas(date_start, hour_start,date_end, hour_end,isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_FIN:
                dataVentas = adminBaseDatos.getfechaFiniVentas(date_end, hour_end,isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_INI:
                dataVentas = adminBaseDatos.getfechaIniVentas(date_start, hour_start,isConAlcohol,isSinAlcohol,isSnacks,isSouvenir);
                break;
            case REPORT_TOTAL:
                dataVentas = adminBaseDatos.getAllfechaVentas(date_start);
                break;
            default:
                dataVentas=null;
                break;
        }
        adminBaseDatos.closeBaseDtos();

        return true;

        //return buildReport();
        //return armaDataString(dataVentas);
    }

    public void setIsShowMessage(Boolean isSMS){
        isMessage = isSMS;
    }
    public Boolean TotalizarVentas(List<DataVentas> dataVentas) {
        if(dataVentas==null || dataVentas.size()<1) {
            if(mActivity!= null && isMessage) {
                util.showToast(R.drawable.fail, "Sin DATOS", mActivity);
                return false;
            }else{
                fileDataOut = date_start+" Sin Ventas\n";
                return true;
            }

        }

        TotalCon=0;
        TotalSin=0;
        TotalSnack=0;
        TotalSouvenirs=0;

        fileDataOut = "Numero de Aprobacion,Fecha,Hora,Grupo,Nombre,Cantidad,Subtotal\n";

        if(isConAlcohol){
            //fileDataOut+="Con Alcohol\n";
            fileDataOut+=reportConAlcohol(dataVentas);
        }

        if(isSinAlcohol){
            //fileDataOut+="Sin Alcohol\n";
            fileDataOut+=reportSinAlcohol(dataVentas);
        }

        if(isSnacks){
            //fileDataOut+="Snacks\n";
            fileDataOut+=reportSnack(dataVentas);
        }

        if(isSouvenir){
            //fileDataOut+="Souvenirs\n";
            fileDataOut+=reportSouvenirs(dataVentas);
        }

        //buildReport(dataVentas);

        fileDataOut += "\n,,,,,Gran Total,"+String.valueOf(TotalCon+TotalSin+TotalSnack+TotalSouvenirs)+"\n";

        return true;
    }

    private String reportSouvenirs(List<DataVentas> dataVentas){
        String repSovenirs="";
        TotalSouvenirs=0;

        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("4")) {
                //repSovenirs +=  temp.getRepAproba() + "," + temp.getRepFechaTab() + "," + temp.getRepHoraTab() + ",Souvenirs," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalSouvenirs += Integer.valueOf(temp.getRepTotal());
            }
        }
        //repSovenirs += ",,,,,Total,"+String.valueOf(TotalSouvenirs)+"\n";
        return repSovenirs;
    }

    private String reportSnack(List<DataVentas> dataVentas){
        String repSnack="";
        TotalSnack=0;
        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("3")) {
                //repSnack +=  temp.getRepAproba() + "," + temp.getRepFechaTab() + "," + temp.getRepHoraTab() + ",Snaks," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalSnack += Integer.valueOf(temp.getRepTotal());
            }
        }
       // repSnack += ",,,,,Total,"+String.valueOf(TotalSnack)+"\n";
        return repSnack;
    }

    private String reportConAlcohol(List<DataVentas> dataVentas){
        String repConAlcohol="";
        TotalCon=0;
        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("1")) {
               // repConAlcohol += temp.getRepAproba() + "," +  temp.getRepFechaTab() + "," + temp.getRepHoraTab() + ",Con Alcohol," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalCon += Integer.valueOf(temp.getRepTotal());
            }
        }
       // repConAlcohol += ",,,,,Total,"+String.valueOf(TotalCon)+"\n";
        return repConAlcohol;
    }

    private String reportSinAlcohol(List<DataVentas> dataVentas){
        String repSinAlcohol="";
        TotalSin=0;
        for(DataVentas temp:dataVentas){
            if(temp.getTypeProducto().equals("2")) {
               // repSinAlcohol += temp.getRepAproba() + "," + temp.getRepFechaTab() + "," + temp.getRepHoraTab() + ",Sin Alcohol," + temp.getRepNomProd() + "," + temp.getRepCantidad() + "," + temp.getRepTotal() + "\n";
                TotalSin += Integer.valueOf(temp.getRepTotal());
            }
        }
        //repSinAlcohol += ",,,,,Total,"+String.valueOf(TotalSin)+"\n";
        return repSinAlcohol;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private File template;
    private String rutaFile;
    private final String TEMPLATE_INVENTARIO = "template.xlsx";
    public boolean buildReport(){
        if(!isExitFileTemplate())
            copyTemplate();
        createFileDir();
        return createReport();
    }

    private boolean createReport() {
        boolean bRet = false;
        boolean isVentas = true;
        int conta = 6;
        Row rowNum;

        FileInputStream fis = null;
        try {

            fis = new FileInputStream(template);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            if(dataVentas==null || dataVentas.size()<1) {
                if(mActivity!= null && isMessage) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showToast(R.drawable.fail, "Sin DATOS", mActivity);
                        }
                    });

                    return false;
                }else{
                    Row newRow = sheet.createRow(1);
                    Cell cell1 = newRow.createCell(0);
                    cell1.setCellValue("Sin VENTAS");
                    isVentas = false;
                }

            }

            if(isVentas) {

                TotalizarVentas(dataVentas);
                for (int i = 0; i < dataVentas.size(); i++) {
                    Row newRow = sheet.createRow(i + 1);
                    Cell cell1 = newRow.createCell(0);
                    cell1.setCellValue(String.valueOf(i+1));

                    Cell cell2 = newRow.createCell(1);
                    cell2.setCellValue(dataVentas.get(i).getRepAproba());

                    Cell cell3 = newRow.createCell(2);
                    cell3.setCellValue(dataVentas.get(i).getRepFechaTab());

                    Cell cell4 = newRow.createCell(3);
                    cell4.setCellValue(dataVentas.get(i).getRepHoraTab());

                    Cell cell5 = newRow.createCell(4);
                    switch (dataVentas.get(i).getTypeProducto()){
                        case "1":
                            cell5.setCellValue("Con Alcohol");
                            break;
                        case "2":
                            cell5.setCellValue("Sin Alcohol");
                            break;
                        case "3":
                            cell5.setCellValue("Snaks");
                            break;
                        case "4":
                            cell5.setCellValue("Suvenirs");
                            break;
                        default:
                            cell5.setCellValue("no registra");
                            break;
                    }

                    Cell cell6 = newRow.createCell(5);
                    cell6.setCellValue(dataVentas.get(i).getRepNomProd());

                    Cell cell7 = newRow.createCell(6);
                    cell7.setCellValue(dataVentas.get(i).getRepCantidad());

                    Cell cell8 = newRow.createCell(7);
                    cell8.setCellValue(dataVentas.get(i).getRepTotal());
                }

                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                Cell cell1 = newRow.createCell(6);
                cell1.setCellValue("Total");

                Cell cell2 = newRow.createCell(7);
                cell2.setCellValue(String.valueOf(String.valueOf(TotalCon + TotalSin + TotalSnack + TotalSouvenirs)));
            }
            File path2 = new File(Environment.getExternalStorageDirectory(), FILE_REPORT+"/"+nameFile);

            if(path2.exists()) {
                path2.delete();
            }


            try {
                FileOutputStream outputStream = new FileOutputStream(path2);
                workbook.write(outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                bRet = false;
            }

            bRet = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bRet = false;
        } catch (IOException e) {
            e.printStackTrace();
            bRet = false;
        }
        return bRet;
    }

    private void createFileDir() {
        rutaFile = PACKAGE_FILE;
        File directorio2 = new File(Environment.getExternalStorageDirectory(), FILE_REPORT);
        if (!directorio2.exists()) {
            directorio2.mkdirs();
        }
    }

    private void copyTemplate(){
        File outputFile;
        InputStream inputStream;

        inputStream = mContext.getResources().openRawResource(R.raw.template);
        outputFile = new File(mContext.getFilesDir(), TEMPLATE_INVENTARIO);

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isExitFileTemplate() {
        template = new File(mContext.getFilesDir(), TEMPLATE_INVENTARIO);
        if(template.exists()) {
            return true;
        }
        return false;
    }

}
