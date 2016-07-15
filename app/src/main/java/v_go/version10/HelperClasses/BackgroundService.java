package v_go.version10.HelperClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import v_go.version10.ActivityClasses.Main;
import v_go.version10.ActivityClasses.SignUpAndLoginIn;
import v_go.version10.ApiClasses.Request;
import v_go.version10.Chat.App;
import v_go.version10.R;
import v_go.version10.SocketIo.SocketIoHelper;

/** Background thread for long polling repeated by Timer **/
/** DON'T update any UI from this class !!! **/
/** This class is intended to collect data from backend database and push them to front-end ONLY **/
/** This class should keep all received data in their original form and structure **/
public class BackgroundService extends Service {

    private boolean onForeground = false;
    private int numberOfPushNotifications = 0;

    private LocalBroadcastManager mLocalBroadcastManager;

    public BackgroundService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // setup socket.io
        SocketIoHelper socketHelper = new SocketIoHelper();
        Socket mSocket = socketHelper.getSocket();
        Global.socket = mSocket;
        mSocket.connect();
        mSocket.on("private message", onNewPrivateMessage);

    }

    private Emitter.Listener onNewPrivateMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            sendNotification();

            try {

                JSONObject data = (JSONObject) args[0];
                String sender_user_id = data.getString("sender_user_id");
                String message = data.getString("message");

                Intent broadcastIntent = new Intent("private message");
                broadcastIntent.putExtra("sender_user_id", sender_user_id);
                broadcastIntent.putExtra("message", message);

                mLocalBroadcastManager.sendBroadcast(broadcastIntent);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private void sendNotification(){

        numberOfPushNotifications++;
        String displayMsg;
        if(numberOfPushNotifications > 1){
            displayMsg = "You have " + numberOfPushNotifications + " new messages.";
        }else{
            displayMsg = "You have " + numberOfPushNotifications + " new message.";
        }

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final int NOTIFICATION_ID = 12345;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.vgo_logo_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.vgo_logo_large))
                        .setContentTitle("V-GO")
                        .setContentText(displayMsg)
                        .setSound(notificationSound);
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);

        Intent targetIntent = new Intent(this, SignUpAndLoginIn.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        wakeUpScreen();
    }

    private void wakeUpScreen(){
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(!isScreenOn)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(5000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(5000);
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