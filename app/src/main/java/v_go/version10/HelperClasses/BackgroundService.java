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
import v_go.version10.HelperClasses.Global;

/** Background thread for long polling repeated by Timer **/
/** DON'T update UI from this class !!! **/
public class BackgroundService extends Service {

    private Timer mTimer;
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

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (onForeground) {
                    try {

                        if(numOfNotification != 0){
                            int max = 0;
                            for(int i=0; i<numOfNotification; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int req_id = jsonObject.getInt("request_id");

                                Log.d("DEBUG", "req_id = " + req_id);

                                if(req_id > max){
                                    max = req_id;
                                }
                            }
                            Global.LATEST_REQ_ID = max;
                        }

                        Request request = new Request();
                        jsonArray = request.Notification(Global.LATEST_REQ_ID + "");
                        numOfNotification = jsonArray.length();

                        Log.d("DEBUG", numOfNotification + " REQUESTS RECEIVED");

                        // use LocalBroadcastManager to update ui
                        if(numOfNotification != 0){

                            // init new intent that we're going to broadcast
                            Intent broadcastIntent = new Intent("new_request");

                            // pass number of new requests to Main
                            broadcastIntent.putExtra("num_of_new_req", numOfNotification);

                            // pass all the request trip_id & request_id as int array to Main
                            int tripIdArray[] = new int[numOfNotification];
                            int reqIdArray[] = new int[numOfNotification];
                            // pass all the name of all notifications' senders to Main
                            String nameArray[] = new String[numOfNotification];
                            // pass all reg_as & accept/denied decision notification type
                            int notifTypeArray[] = new int[numOfNotification];

                            for(int j=0; j<numOfNotification; j++){
                                // trip id
                                tripIdArray[j] = jsonArray.getJSONObject(j).getInt("trip_id");
                                reqIdArray[j] = jsonArray.getJSONObject(j).getInt("request_id");
                                // sender name
                                nameArray[j] = jsonArray.getJSONObject(j).getString("name");
                                // notification type and results
                                if(jsonArray.getJSONObject(j).has("reg_as")) {
                                    notifTypeArray[j] = jsonArray.getJSONObject(j).getInt("reg_as");
                                }else if(jsonArray.getJSONObject(j).has("accept")) {
                                    int result = jsonArray.getJSONObject(j).getInt("accept");
                                    if(result == 0) {
                                        notifTypeArray[j] = 2;
                                    }else if(result == 1){
                                        notifTypeArray[j] = 3;
                                    }
                                }
                            }
                            broadcastIntent.putExtra("trip_id_array", tripIdArray);
                            broadcastIntent.putExtra("req_id_array", reqIdArray);
                            broadcastIntent.putExtra("sender_name_array", nameArray);
                            broadcastIntent.putExtra("notif_type_array", notifTypeArray);

                            // update current notification number
                            Global.DISPLAYED_NOTIF_NUM += numOfNotification;

                            // collect everything and pass to Main
                            mLocalBroadcastManager.sendBroadcast(broadcastIntent);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, 1, 1);
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