package v_go.version10.HelperClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import v_go.version10.ActivityClasses.LoginNew;
import v_go.version10.ActivityClasses.Main;
import v_go.version10.Chat.ChatActivity;
import v_go.version10.FragmentClasses.TabC_1_new;
import v_go.version10.PersistentCookieStore.SiCookieStore2;
import v_go.version10.R;
import v_go.version10.SocketIo.SocketIoHelper;

public class BackgroundService extends Service {

    private static int numberOfPushNotifications = 0;
    public static void resetNumberOfPushNotification(){
        numberOfPushNotifications = 0;
    }

    private LocalBroadcastManager mLocalBroadcastManager;
    private Socket socket;

    public final static int NOTIFICATION_ID = 12345;
    private Uri notificationSound;

    public static HashMap<String, Integer> unReadMessage = new HashMap<>();

    public BackgroundService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences(SiCookieStore2.COOKIE_PREFS, 0);
        SiCookieStore2 siCookieStore = new SiCookieStore2(sharedPreferences);
        CookieManager cookieManager = new CookieManager(siCookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // setup socket.io
        SocketIoHelper app = (SocketIoHelper) getApplication();
        socket = app.getSocket();
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("private message", onNewPrivateMessage);
        socket.on("server error", onServerError);

        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    }
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("DEBUG", "socket connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("DEBUG", "socket disconnected");
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("DEBUG", "socket failed to connect");
        }
    };
    private Emitter.Listener onServerError = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            JSONObject error = (JSONObject) args[0];
            try {
                Log.d("DEBUG", error.getString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onNewPrivateMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            try {

                JSONObject data = (JSONObject) args[0];
                String sender_user_id = data.getString("sender_user_id");
                String message = data.getString("message");

                Intent broadcastIntent = new Intent("private message");
                broadcastIntent.putExtra("sender_user_id", sender_user_id);
                broadcastIntent.putExtra("message", message);

                mLocalBroadcastManager.sendBroadcast(broadcastIntent);

                // send push notification
                if(!TabC_1_new.isVisible || !ChatActivity.isUserIdMatched(sender_user_id)){
                    sendNotification("chat message");
                }else{
                    // play sound and vibrate but no push notification
                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
                    ringtone.play();
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(300);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private void sendNotification(String message){

        numberOfPushNotifications++;
        String displayMsg = "";

        if(message.equals("chat message")) {
            if (numberOfPushNotifications > 1) {
                displayMsg = "You have " + numberOfPushNotifications + " new messages.";
            } else {
                displayMsg = "You have " + numberOfPushNotifications + " new message.";
            }
        }else{
            // TO DO: trip notification, request, etc
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.vgo_logo_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.vgo_logo_large))
                        .setContentTitle("V-GO")
                        .setContentText(displayMsg)
                        .setSound(notificationSound);
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);

        Intent targetIntent = new Intent(this, Main.class);
        targetIntent.putExtra("toTab", 2);
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
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // disconnect from server
        socket.disconnect();
    }
}