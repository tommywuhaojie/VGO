package v_go.version10.HelperClasses;

import android.util.Log;

public class Global {

    public static final String TAB_A  = "tab_a_identifier";
    public static final String TAB_B  = "tab_b_identifier";
    public static final String TAB_C  = "tab_c_identifier";
    public static final String TAB_D  = "tab_d_identifier";

    public static String DATE_TIME = "";
    public static int SELECTED_TYPE = 0;
    public static int ALLOW_MUL_PASSEN = 0;

    public static boolean TAB3_NOTIFICATION = false;
    public static int LATEST_REQ_ID = 0;
    public static int DISPLAYED_NOTIF_NUM = 0;

    // RESET
    public static void resetAll(){
        DATE_TIME = "";
        SELECTED_TYPE = 0;
        ALLOW_MUL_PASSEN = 0;

        TAB3_NOTIFICATION = false;
        LATEST_REQ_ID = 0;
        DISPLAYED_NOTIF_NUM = 0;
    }
}