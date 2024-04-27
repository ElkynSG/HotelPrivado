package com.esilva.a910private.dataBD;

public class DataVentas {
    private int id_transac;
    private String numAproba;
    private String codResp;
    private String recibo;
    private String fecha;
    private String monto;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public int getId_transac() {
        return id_transac;
    }

    public void setId_transac(int id_transac) {
        this.id_transac = id_transac;
    }

    public String getNumAproba() {
        return numAproba;
    }

    public void setNumAproba(String numAproba) {
        this.numAproba = numAproba;
    }

    public String getCodResp() {
        return codResp;
    }

    public void setCodResp(String codResp) {
        this.codResp = codResp;
    }

    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void clearData(){
         id_transac=0;
         numAproba=null;
         codResp=null;
         recibo=null;
         fecha=null;
    }
}
