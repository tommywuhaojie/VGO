package v_go.version10.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;

import java.util.Timer;
import java.util.TimerTask;

import v_go.version10.ApiClasses.Request;

/** Background thread for long polling handle by AsyncTask **/
public class BackgroundService extends Service {

    private Timer mTimer;
    private boolean onForeground = false;

    public BackgroundService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (onForeground) {
                    try {
                        String old_id = "0";
                        JSONArray jsonArray;
                        int numOfNotif = 0;

                        Request request = new Request();
                        jsonArray = request.Notification(old_id);
                        numOfNotif = jsonArray.length();

                        Log.d("DEBUG", "" + numOfNotif);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, 500, 500);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onForeground = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onForeground = false;
    }
}