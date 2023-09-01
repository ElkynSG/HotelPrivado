package com.esilva.hotelprivado.db;

import org.json.JSONException;
import org.json.JSONObject;

public class DataProduct {
    public String dt_consecutivo="id";
    public String dt_id_producto="id_product";
    public String dt_nameImage="name_ima";
    public String dt_nombre_es="name_esp";
    public String dt_nombre_in="name_ing";
    public String dt_precio="price";
    public String dt_descripcion_es="descrip_esp";
    public String dt_descripcion_in="descrip_ing";
    public String dt_type_product="typeProduct";
    public String dt_num_articulos="num_items";
    public String dt_num_vendidos="num_vendidos";

    public DataProduct() {

    }

    public DataProduct(String dt_consecutivo, String dt_id_producto, String dt_nameImage, String dt_nombre_es, String dt_nombre_in, String dt_precio, String dt_descripcion_es, String dt_descripcion_in, String dt_type_product, String dt_num_articulos, String dt_num_vendidos) {
        this.dt_consecutivo = dt_consecutivo;
        this.dt_id_producto = dt_id_producto;
        this.dt_nameImage = dt_nameImage;
        this.dt_nombre_es = dt_nombre_es;
        this.dt_nombre_in = dt_nombre_in;
        this.dt_precio = dt_precio;
        this.dt_descripcion_es = dt_descripcion_es;
        this.dt_descripcion_in = dt_descripcion_in;
        this.dt_type_product = dt_type_product;
        this.dt_num_articulos = dt_num_articulos;
        this.dt_num_vendidos = dt_num_vendidos;
    }

    public String getDt_consecutivo() {
        return dt_consecutivo;
    }

    public void setDt_consecutivo(String dt_consecutivo) {
        this.dt_consecutivo = dt_consecutivo;
    }

    public String getDt_id_producto() {
        return dt_id_producto;
    }

    public void setDt_id_producto(String dt_id_producto) {
        this.dt_id_producto = dt_id_producto;
    }

    public String getDt_nameImage() {
        return dt_nameImage;
    }

    public void setDt_nameImage(String dt_nameImage) {
        this.dt_nameImage = dt_nameImage;
    }

    public String getDt_nombre_es() {
        return dt_nombre_es;
    }

    public void setDt_nombre_es(String dt_nombre_es) {
        this.dt_nombre_es = dt_nombre_es;
    }

    public String getDt_nombre_in() {
        return dt_nombre_in;
    }

    public void setDt_nombre_in(String dt_nombre_in) {
        this.dt_nombre_in = dt_nombre_in;
    }

    public String getDt_precio() {
        return dt_precio;
    }

    public void setDt_precio(String dt_precio) {
        this.dt_precio = dt_precio;
    }

    public String getDt_descripcion_es() {
        return dt_descripcion_es;
    }

    public void setDt_descripcion_es(String dt_descripcion_es) {
        this.dt_descripcion_es = dt_descripcion_es;
    }

    public String getDt_descripcion_in() {
        return dt_descripcion_in;
    }

    public void setDt_descripcion_in(String dt_descripcion_in) {
        this.dt_descripcion_in = dt_descripcion_in;
    }

    public String getDt_type_product() {
        return dt_type_product;
    }

    public void setDt_type_product(String dt_type_product) {
        this.dt_type_product = dt_type_product;
    }

    public String getDt_num_articulos() {
        return dt_num_articulos;
    }

    public void setDt_num_articulos(String dt_num_articulos) {
        this.dt_num_articulos = dt_num_articulos;
    }

    public String getDt_num_vendidos() {
        return dt_num_vendidos;
    }

    public void setDt_num_vendidos(String dt_num_vendidos) {
        this.dt_num_vendidos = dt_num_vendidos;
    }

    private static final String JSON_ID = "id";
    private static final String JSON_ID_PRO = "id_pro";
    private static final String JSON_IMA = "nam_ima";
    private static final String JSON_NAMEE = "na_es";
    private static final String JSON_NAMEI = "na_in";
    private static final String JSON_PRE = "pre";
    private static final String JSON_DEES = "des_es";
    private static final String JSON_DEIN = "des_in";
    private static final String JSON_TYPE = "ty_pro";
    private static final String JSON_NUMART = "num_art";
    private static final String JSON_NUMVEN = "vendidos";
    public String Serializar(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_ID,dt_consecutivo);
            jsonObject.put(JSON_ID_PRO,dt_id_producto);
            jsonObject.put(JSON_IMA,dt_nameImage);
            jsonObject.put(JSON_NAMEE,dt_nombre_es);
            jsonObject.put(JSON_NAMEI,dt_nombre_in);
            jsonObject.put(JSON_PRE,dt_precio);
            jsonObject.put(JSON_DEES,dt_descripcion_es);
            jsonObject.put(JSON_DEIN,dt_descripcion_in);
            jsonObject.put(JSON_TYPE,dt_type_product);
            jsonObject.put(JSON_NUMART,dt_num_articulos);
            jsonObject.put(JSON_NUMVEN,dt_num_vendidos);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject.toString();

    }
    public DataProduct DesSerializar(String serializer){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(serializer);
            dt_consecutivo = jsonObject.getString(JSON_ID);
            dt_id_producto = jsonObject.getString(JSON_ID_PRO);
            dt_nameImage = jsonObject.getString(JSON_IMA);
            dt_nombre_es = jsonObject.getString(JSON_NAMEE);
            dt_nombre_in = jsonObject.getString(JSON_NAMEI);
            dt_precio = jsonObject.getString(JSON_PRE);
            dt_descripcion_es = jsonObject.getString(JSON_DEES);
            dt_descripcion_in = jsonObject.getString(JSON_DEIN);
            dt_type_product = jsonObject.getString(JSON_TYPE);
            dt_num_articulos = jsonObject.getString(JSON_NUMART);
            dt_num_vendidos = jsonObject.getString(JSON_NUMVEN);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return this;
    }
}
