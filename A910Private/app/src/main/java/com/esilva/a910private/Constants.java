package com.esilva.a910private;

public class Constants {
    public static final String SHARE_BASE = "baseDatos";
    public static final String BASE_CONNECTION = "typeConect";
        public static final Boolean TYPE_USB = true;
        public static final Boolean TYPE_BT = false;

    /////////////////////////////    COMANDOS     ///////////////////////////////////////////
    public static final String CM_SALE =        "CM_SAL";
    public static final String CM_SALE_REQ =    "CM_REQ";

    public static final String CM_PRINTER =     "CM_PRI";

    public static final String CM_FAIL =        "FAIL";

    public static final String CM_ACK =     "ACK";
    public static final String CM_NAK =     "NAK";

    /////////////////////////////    intent     ///////////////////////////////////////////
    public static String PACKAGE = "rbm.pax.wimobile.com.rbmappcomercioswm";
    public static String SEND_COMERCIOS = "rbm.pax.wimobile.com.rbmappcomercios.features.mainmenu.ui.MainMenuActivity";
    public static String packageName = "package";
    public static String data_input = "data_input";
    public static int REQUESTCOMERCIOS= 100;

    public static final int STATE_TR_WAIT =      1;
    public static final int STATE_TR_START =     2;
    public static final int STATE_TR_PROCCESS =  3;
    public static final int STATE_TR_FINISH =    4;
    public static final int STATE_TR_ERROR =     5;

    public static final String JSON_COD_RES="Codigo_respuesta";
    public static final String JSON_NUM_CONFIR="Numero_confirmacion";
    public static final String JSON_NUM_FECHA="Fecha_hora";
    public static final String JSON_RECIBO="Numero_recibo";
    public static final String JSON_ID_TR="id_transacion";
    public static final String JSON_MSM_ERROR="Mensaje_error";

    /////////////////////////////    printer     ///////////////////////////////////////////
    public static String PACKAGEPrint = "com.example.esilva.printerintent";
    public static String SEND_IMPRESION = "com.example.esilva.printerintent.ImpresionIn";
    public static String packageNamePrint = "package";
    public static String dataInputPrint = "Param";
    public static Integer REQUESTPRINT= 300;
}
