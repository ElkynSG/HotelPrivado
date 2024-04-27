package com.esilva.hotelprivado.inventario;

import static com.esilva.hotelprivado.Util.Constantes.PACKAGE_FILE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import com.esilva.hotelprivado.InventarioActivity;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataProduct;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.ls.LSException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CargaInventario {
    DataProduct dataProductInv;
    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;

    public CargaInventario(Activity activity,Context context) {
        this.context = context;
        this.activity = activity;
    }

    /*public Boolean cargarDatos(){
        String data[];
        long aLong=0;
        NumberFormat currencyFormatter;
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;

        AdminBaseDatos adminBaseDatos;
        List<String> trama;
        OutputStream out;
        File directorio = new File(Environment.getExternalStorageDirectory(), "HotelPrivado/inventario.txt");
        if(!directorio.exists()){
            return null;
        }
        adminBaseDatos = new AdminBaseDatos(context);
        adminBaseDatos.deleteTableProd();

        trama = readFileInventario(directorio);
        try {
            for(String line:trama){
                aLong = adminBaseDatos.insert(getDataProduct(line).dt_id_producto.trim()
                        ,getDataProduct(line).dt_nameImage.trim()
                        ,getDataProduct(line).dt_nombre_es.trim()
                        ,getDataProduct(line).dt_nombre_in.trim()
                        ,currencyFormatter.format(Long.valueOf(getDataProduct(line).dt_precio.trim()))
                        ,getDataProduct(line).dt_descripcion_es.trim()
                        ,getDataProduct(line).dt_descripcion_in.trim()
                        ,getDataProduct(line).dt_type_product.trim()
                        ,getDataProduct(line).dt_num_articulos.trim()
                );
            }

            adminBaseDatos.closeBaseDtos();
        }catch (Exception e){

            return false;
        }


        return aLong>0?true:false;
    }*/

    public Boolean cargarDatosExcel(){
        boolean bRet = false;
        Row rowNum;
        Cell cell;
        int rowConta = 1;
        AdminBaseDatos adminBaseDatos;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Cargando Datos");
                progressDialog.show();
            }
        });

        adminBaseDatos = new AdminBaseDatos(context);
        adminBaseDatos.deleteTableProd();

        NumberFormat currencyFormatter;
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        custom.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("$###,###.##");
        df.setDecimalFormatSymbols(custom);
        currencyFormatter = df;

        try {
            File path = new File(Environment.getExternalStorageDirectory(), PACKAGE_FILE+ "/inventario.xlsx");
            if(!path.exists())
                return false;

            FileInputStream fis = new FileInputStream(path);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);  // Accede a la primera hoja

            while (true){
                int index;
                rowNum = sheet.getRow(rowConta);
                if(rowNum != null){
                    DataProduct dt = new DataProduct();
                    cell = rowNum.getCell(0);
                    dt.dt_id_producto = cell.getStringCellValue().trim();

                    cell = rowNum.getCell(1);
                    dt.dt_nameImage = cell.getStringCellValue().trim();

                    cell = rowNum.getCell(2);
                    dt.dt_nombre_es = cell.getStringCellValue().trim();

                    cell = rowNum.getCell(3);
                    dt.dt_nombre_in = cell.getStringCellValue().trim();

                    cell = rowNum.getCell(4);
                    index = String.valueOf(cell.getNumericCellValue()).indexOf('.');
                    if (index != -1)
                        dt.dt_precio = String.valueOf(cell.getNumericCellValue()).substring(0, index);
                    else
                        dt.dt_precio = String.valueOf(cell.getNumericCellValue());

                    cell = rowNum.getCell(5);
                    dt.dt_descripcion_es = cell.getStringCellValue().trim();

                    cell = rowNum.getCell(6);
                    dt.dt_descripcion_in = cell.getStringCellValue().trim();

                    cell = rowNum.getCell(7);
                    index = String.valueOf(cell.getNumericCellValue()).indexOf('.');
                    if (index != -1)
                        dt.dt_type_product = String.valueOf(cell.getNumericCellValue()).substring(0, index);
                    else
                        dt.dt_type_product = String.valueOf(cell.getNumericCellValue());

                    cell = rowNum.getCell(8);
                    index = String.valueOf(cell.getNumericCellValue()).indexOf('.');
                    if (index != -1)
                        dt.dt_num_articulos = String.valueOf(cell.getNumericCellValue()).substring(0, index);
                    else
                        dt.dt_num_articulos = String.valueOf(cell.getNumericCellValue());

                    adminBaseDatos.insert(dt.dt_id_producto.trim()
                            ,dt.dt_nameImage.trim()
                            ,dt.dt_nombre_es.trim()
                            ,dt.dt_nombre_in.trim()
                            ,currencyFormatter.format(Long.valueOf(dt.dt_precio.trim()))
                            ,dt.dt_descripcion_es.trim()
                            ,dt.dt_descripcion_in.trim()
                            ,dt.dt_type_product.trim()
                            ,dt.dt_num_articulos.trim()
                    );
                }else{
                    break;
                }
                rowConta++;
            }
            bRet = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            adminBaseDatos.closeBaseDtos();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });

        }

        //showResult(bRet,"Tecnicos");
        return bRet;
    }


    private DataProduct getDataProduct(String in){
        DataProduct parse=new DataProduct();
        String dta[] = in.split(",");
        parse.setDt_id_producto(dta[0]);
        parse.setDt_nameImage(dta[1]);
        parse.setDt_nombre_es(dta[2]);
        parse.setDt_nombre_in(dta[3]);
        parse.setDt_precio(dta[4]);
        parse.setDt_descripcion_es(dta[5]);
        parse.setDt_descripcion_in(dta[6]);
        parse.setDt_type_product(dta[7]);
        parse.setDt_num_articulos(dta[8]);
        return parse;
    }

    private List<String> readFileInventario(File file) {
        Boolean val = false;
        boolean isTitle = false;
        String temp;
        List<String> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            while ((temp = reader.readLine()) != null){
                try {
                    if(isTitle)
                        data.add(temp);
                    else
                        isTitle = true;
                }catch (Exception e){
                    Log.e("hotel",e.getMessage());
                    return null;
                }
            }


        } catch (Exception e) {
            Log.e("hotel",e.getMessage());
            return null;
        }
        return data;
    }

    public Boolean isExite(){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(context);
        Boolean isex =adminBaseDatos.isExiteProductos();
        adminBaseDatos.closeBaseDtos();
        if(isex){
            return true;
        }else{
            return false;
        }
    }

    public void eliminarTabla(){
        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(context);
        adminBaseDatos.deleteTableProd();
    }

}
