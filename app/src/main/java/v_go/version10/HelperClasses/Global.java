package v_go.version10.HelperClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.CountDownTimer;

import java.util.Random;

public class Global {

    public static final String TAB_A  = "tab_a_identifier";
    public static final String TAB_B  = "tab_b_identifier";
    public static final String TAB_C  = "tab_c_identifier";
    public static final String TAB_D  = "tab_d_identifier";

    public static String DATE_TIME = "";
    public static int SELECTED_TYPE = 0;
    public static int ALLOW_MUL_PASSEN = 0;

    public static boolean TAB3_NOTIFICATION = false;
    public static boolean IS_LOGED_IN = false;
    public static boolean NEED_TO_DOWNLOAD_TAB_D_AVATAR = false;
    public static int LATEST_REQ_ID = 0;
    public static int DISPLAYED_NOTIF_NUM = 0;

    public static Bitmap my_avatar;
    public static Bitmap other_avatar;

    // Reset all when user is logged out
    public static void resetAll(){
        DATE_TIME = "";
        SELECTED_TYPE = 0;
        ALLOW_MUL_PASSEN = 0;

        TAB3_NOTIFICATION = false;
        IS_LOGED_IN = false;
        NEED_TO_DOWNLOAD_TAB_D_AVATAR = false;
        LATEST_REQ_ID = 0;
        DISPLAYED_NOTIF_NUM = 0;
    }
    // GET RANDOM INT
    public static int getRandomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float r = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static int CAN_SEND_CODE_AGAIN_IN_SECOND = 0;
    public static void startSendCodeTimer(int sec){

        new CountDownTimer(sec * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                CAN_SEND_CODE_AGAIN_IN_SECOND = ((int)(millisUntilFinished / 1000));
            }
            public void onFinish() {
                CAN_SEND_CODE_AGAIN_IN_SECOND = 0;
            }
        }.start();
    }
}