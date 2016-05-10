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

        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                // this thread only runs when the App is on the foreground!
                if (onForeground) {

                    try {

                        Request request = new Request();
                        if(!Global.IS_LOGED_IN){
                            final String NUM_OF_NOTIF_RECEIVED_ALREADY = "0";
                            jsonArray = request.RequestList(NUM_OF_NOTIF_RECEIVED_ALREADY);
                            Global.IS_LOGED_IN = true;
                        }else {
                            jsonArray = request.Notification(Global.LATEST_REQ_ID + "");
                        }
                        numOfNotification = jsonArray.length();

                        Log.d("DEBUG", numOfNotification + " REQUESTS RECEIVED");

                        //--------------------------------------------------------------------------
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
                            // pass all location names
                            String startLocArray[] = new String[numOfNotification];
                            String endLocArray[] = new String[numOfNotification];
                            // pass all reg_as & accept/denied decision notification type
                            int notifTypeArray[] = new int[numOfNotification];
                            // receiver or sender of the request
                            int receivedFlagArray[] = new int[numOfNotification];

                            for(int j=0; j<numOfNotification; j++){

                                // trip id
                                tripIdArray[j] = jsonArray.getJSONObject(j).getInt("trip_id");
                                reqIdArray[j] = jsonArray.getJSONObject(j).getInt("request_id");
                                // send location
                                startLocArray[j] = jsonArray.getJSONObject(j).getString("start_location");
                                endLocArray[j] = jsonArray.getJSONObject(j).getString("end_location");
                                // sender name
                                nameArray[j] = jsonArray.getJSONObject(j).getString("name");
                                // receive flag
                                receivedFlagArray[j] = jsonArray.getJSONObject(j).getInt("received_flag");

                                Log.d("DEBUG", "notif index= "+j+"receive or send? "+jsonArray.getJSONObject(j).getString("received_flag")+" has reg_as? " + jsonArray.getJSONObject(j).has("reg_as")
                                        + " has accept/deny? "+ jsonArray.getJSONObject(j).has("accept"));

                                // notification type and results
                                if(jsonArray.getJSONObject(j).has("accept") && jsonArray.getJSONObject(j).getInt("accept") != 2) {
                                    int result = jsonArray.getJSONObject(j).getInt("accept");
                                    // denied
                                    if(result == 0) {
                                        notifTypeArray[j] = 2;
                                    // accepted
                                    }else if(result == 1){
                                        notifTypeArray[j] = 3;
                                    // pending
                                    }else if(result == 2){
                                        notifTypeArray[j] = 4;
                                    }
                                    Log.d("DEBUG", "decision = " + result);
                                }else if(jsonArray.getJSONObject(j).has("reg_as")) {
                                    notifTypeArray[j] = jsonArray.getJSONObject(j).getInt("reg_as");
                                }

                                // log every request id
                                Log.d("DEBUG", "req_id = " + jsonArray.getJSONObject(j).getInt("request_id"));
                            }

                            // put all Extra
                            broadcastIntent.putExtra("req_id_array", reqIdArray);
                            broadcastIntent.putExtra("trip_id_array", tripIdArray);
                            broadcastIntent.putExtra("notif_type_array", notifTypeArray);
                            broadcastIntent.putExtra("start_loc_array", startLocArray);
                            broadcastIntent.putExtra("end_loc_array", endLocArray);
                            broadcastIntent.putExtra("sender_name_array", nameArray);
                            broadcastIntent.putExtra("receive_flag_array", receivedFlagArray);

                            // update current notification number
                            Global.DISPLAYED_NOTIF_NUM += numOfNotification;

                            // collect everything and pass to Main
                            mLocalBroadcastManager.sendBroadcast(broadcastIntent);

                            // get the max req_id
                            int max = 0;
                            for(int i=0; i<numOfNotification; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int req_id = jsonObject.getInt("request_id");
                                // calculate the max req_id value
                                if(req_id > max){
                                    max = req_id;
                                }
                            }
                            Global.LATEST_REQ_ID = max;
                        }
                        //--------------------------------------------------------------------------

                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("DEBUG", "Exception occurs while polling !!!");
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