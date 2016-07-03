package v_go.version10.HelperClasses;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import v_go.version10.ApiClasses.Request;

/** Background thread for long polling repeated by Timer **/
/** DON'T update any UI from this class !!! **/
/** This class is intended to collect data from backend database and push them to front-end ONLY **/
/** This class should keep all received data in their original form and structure **/
public class BackgroundService extends Service {

    private boolean onForeground = false;
    private int numOfNotification = 0;
    private JSONArray jsonArray;

    public BackgroundService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
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