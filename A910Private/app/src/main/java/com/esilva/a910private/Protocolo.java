package com.esilva.a910private;

public class Protocolo {
    public static final String SHARE_BASE_TRANSAC = "baseTransac";
    public static final String SHARE_LAST_TR = "ultima_tr";
    public static final String BASE_CONNECTION = "typeConect";
        public static final Boolean TYPE_USB = true;
        public static final Boolean TYPE_BT = false;

    /////////////////////////////    COMANDOS     ///////////////////////////////////////////
    public static final String ACK =    "TAG_ACK";
    public static final String ACK_TR = "TAG_OKT";
    public static final String NAK =    "TAG_NAK";
    public static final String PRO =    "TAG_PRO";
    public static final String FAIL =   "TAG_FAI";

    public static final String TA_CON = "TAG_CON";
    public static final String TA_MON = "TAG_MON";
    public static final String TA_RES = "TAG_RES";
    public static final String TA_VOU = "TAG_VOU";
    public static final String TA_SYN = "TAG_SYN";
    public static final String TA_TRA = "TAG_TRA";
    public static final String TA_FIN = "TAG_FIN";

    public static final String PO_RES = "POS_RES";
    public static final String PO_VOU = "POS_VOU";
    public static final String PO_NUM = "POS_NUM";
    public static final String PO_INF = "POS_INF";

    /////////////////////////////    intent     ///////////////////////////////////////////
    public static String PACKAGE = "rbm.pax.wimobile.com.rbmappcomercioswm";
    public static String SEND_COMERCIOS = "rbm.pax.wimobile.com.rbmappcomercios.features.mainmenu.ui.MainMenuActivity";
    public static String packageName = "package";
    public static String data_input = "data_input";
    public static int REQUESTCOMERCIOS= 100;

    public static final int STATE_CONECTADO = 1;
    public static final int STATE_MONTO     = 2;
    public static final int STATE_CONSULTA  = 3;
    public static final int STATE_VOUCHER   = 4;
    public static final int STATE_SYNC      = 5;




    /////////////////////////////    printer     ///////////////////////////////////////////
    public static String PACKAGEPrint = "com.example.esilva.printerintent";
    public static String SEND_IMPRESION = "com.example.esilva.printerintent.ImpresionIn";
    public static String packageNamePrint = "package";
    public static String dataInputPrint = "Param";
    public static Integer REQUESTPRINT= 300;



}
