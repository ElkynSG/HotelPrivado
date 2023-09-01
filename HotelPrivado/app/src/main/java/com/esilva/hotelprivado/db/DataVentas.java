package com.esilva.hotelprivado.db;

public class DataVentas {
    private String id="";
    private String repAproba="";
    private String repFecha="";
    private String repCodProd="";
    private String repNomProd="";
    private String repPrecio="";
    private String repCantidad="";
    private String repFechaTab="";
    private String repHoraTab="";
    private String repTotal="";
    private String typeProducto="";
    private String recibo="";


    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public DataVentas() {

    }

    public String getTypeProducto() {
        return typeProducto;
    }

    public void setTypeProducto(String typeProducto) {
        this.typeProducto = typeProducto;
    }

    public String getRepFechaTab() {
        return repFechaTab;
    }

    public void setRepFechaTab(String repFechaTab) {
        this.repFechaTab = repFechaTab;
    }

    public String getRepHoraTab() {
        return repHoraTab;
    }

    public void setRepHoraTab(String repHoraTab) {
        this.repHoraTab = repHoraTab;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepAproba() {
        return repAproba;
    }

    public void setRepAproba(String repAproba) {
        this.repAproba = repAproba;
    }

    public String getRepFecha() {
        return repFecha;
    }

    public void setRepFecha(String repFecha) {
        this.repFecha = repFecha;
    }

    public String getRepCodProd() {
        return repCodProd;
    }

    public void setRepCodProd(String repCodProd) {
        this.repCodProd = repCodProd;
    }

    public String getRepNomProd() {
        return repNomProd;
    }

    public void setRepNomProd(String repNomProd) {
        this.repNomProd = repNomProd;
    }

    public String getRepPrecio() {
        return repPrecio;
    }

    public void setRepPrecio(String repPrecio) {
        this.repPrecio = repPrecio;
    }

    public String getRepCantidad() {
        return repCantidad;
    }

    public void setRepCantidad(String repCantidad) {
        this.repCantidad = repCantidad;
    }

    public String getRepTotal() {
        return repTotal;
    }

    public void setRepTotal(String repTotal) {
        this.repTotal = repTotal;
    }
}
