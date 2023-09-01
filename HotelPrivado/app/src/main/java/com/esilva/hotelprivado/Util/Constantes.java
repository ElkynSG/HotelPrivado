package com.esilva.hotelprivado.Util;

import androidx.fragment.app.FragmentOnAttachListener;

import com.esilva.hotelprivado.BuildConfig;

public class Constantes {
    public static final String SHA_BASE = "base";
    public static final String SHA_IDIOMA = "idioma";
        public static final int SHA_IDIOMA_ESPANOL = 1;
        public static final int SHA_IDIOMA_INGLES = 2;
    public static final long DISCONNECT_TIMEOUT = 30000;
    public static final String CHANNEL_NOTIFICATION = "201";
    public static final String PACKAGE_FILE = "HotelPrivado";
    public static final String FILE_REPORT = "HotelPrivado/reportes";
    public static final int REPORT_ALL = 1;
    public static final int REPORT_FIN = 2;
    public static final int REPORT_INI = 3;
    public static final int REPORT_PARTIAL = 4;

    public static final String REP_AUTO = "AutoRepHab";
        public static final Boolean REPORT_AUTO_OFF = false;
        public static final Boolean REPORT_AUTO_ON = true;

    public enum USB_Permission { Unknown, Requested, Granted, Denied }
    public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    public static final String USB_ID = "ID_usb";
    public static final String USB_PORT = "ID_port";
    public static final String USB_NAME = "ID_names";
    public static final String TYPE_CONNECT = "typeConecction";
        public static final boolean CONNECT_BT = false;
        public static final boolean CONNECT_USB = true;
    public static final int WRITE_WAIT_MILLIS = 2000;

    /////////////////////////////    HORA REPORTE     ///////////////////////////////////////////
    public static final int re_hora =   23;
    public static final int re_minu =   0;
    public static final int re_seg =    0;

    /////////////////////////////    COMANDOS     ///////////////////////////////////////////
    public static final String CM_SALE =        "CM_SAL";
    public static final String CM_SALE_REQ =    "CM_REQ";

    public static final String CM_PRINTER =     "CM_PRI";

    public static final String CM_FAIL =        "FAIL";

    public static final String CM_ACK =     "ACK";
    public static final String CM_NAK =     "CM_NAK";
}
