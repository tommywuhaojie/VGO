package v_go.version10.HelperClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import v_go.version10.ActivityClasses.Main;
import v_go.version10.ActivityClasses.SignUpAndLoginIn;
import v_go.version10.ApiClasses.Request;
import v_go.version10.R;
import v_go.version10.SocketIo.SocketIoHelper;

/** Background thread for long polling repeated by Timer **/
/** DON'T update any UI from this class !!! **/
/** This class is intended to collect data from backend database and push them to front-end ONLY **/
/** This class should keep all received data in their original form and structure **/
public class BackgroundService extends Service {

    private boolean onForeground = false;

    public BackgroundService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // init new intent that we're going to broadcast
        Intent broadcastIntent = new Intent("new_request");
        // pass number of new requests to Main
        //broadcastIntent.putExtra("num_of_new_req", numOfNotification);
        mLocalBroadcastManager.sendBroadcast(broadcastIntent);

        SocketIoHelper socketHelper = (SocketIoHelper) getApplication();
        Socket mSocket = socketHelper.getSocket();
        mSocket.connect();

        mSocket.on("chat message", onNewMessage);

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            sendNotification();

        }
    };

    private void sendNotification(){

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final int NOTIFICATION_ID = 12345;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.vgo_logo)
                        .setContentTitle("V-GO")
                        .setContentText("You have a new message")
                        .setSound(notificationSound)
                        .setLights(Color.RED, 300, 300);
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);

        Intent targetIntent = new Intent(this, SignUpAndLoginIn.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(!isScreenOn)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
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