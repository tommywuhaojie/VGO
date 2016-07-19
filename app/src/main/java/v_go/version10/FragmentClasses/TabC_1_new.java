package v_go.version10.FragmentClasses;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.socket.emitter.Emitter;
import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.ChatApi;
import v_go.version10.ApiClasses.UserApi;
import v_go.version10.Chat.ChatActivity;
import v_go.version10.HelperClasses.BackgroundService;
import v_go.version10.HelperClasses.ContactListAdapter;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class TabC_1_new extends Fragment {

    private View view;
    private SimpleAdapter adapter;
    private ListView contactListView;
    private List<HashMap<String, String>> hashMap = new ArrayList<>();
    private List<Bitmap> avatarList = new ArrayList<>();
    private List<String> userIdList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Integer> badgeList = new ArrayList<>();

    private ProgressDialog pDialog;
    private ProgressDialog proDialog;
    private int totalContactsToLoad = 0;

    private boolean isFirstTime = true;
    private boolean isInChatActivity = false;
    private String userIdIsTalkingTo = "";
    public static boolean isInitialize;
    public static boolean isVisible;

    private final String[] from = new String[]{"first_name_last_name", "last_message", "date_time"};
    private final int[] to = new int[]{R.id.firstLine, R.id.secondLine, R.id.date_time};

    private final String REQUEST_BY_PHONE_NUMBER = "phone_number";
    private final String REQUEST_BY_USER_ID = "user_id";

    private static String o_user_id;
    private static String my_last_msg;
    private static boolean needToUpdateLastMsg;

    public static void updateLastMessage(String other_user_id, String message, Boolean need){
        o_user_id = other_user_id;
        my_last_msg = message;
        needToUpdateLastMsg = need;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isInitialize = true;

        // inflate the layout for this fragment
        view = inflater.inflate(R.layout.tab_c_1_new, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // change toolbar title
        setHasOptionsMenu(true);

        // set up the notification list
        contactListView = (ListView) view.findViewById(R.id.listView);

        // retrieve ListView
        if(adapter != null) {
            contactListView.setAdapter(adapter);
        }

        // on listView item clicked
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // send needed information to chat activity
                String user_id = userIdList.get(position);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("full_name", nameList.get(position));
                Global.other_avatar = avatarList.get(position);
                // clear up badge
                badgeList.set(position, 0);
                adapter = new ContactListAdapter(getActivity(), hashMap, R.layout.contact_row, from, to, avatarList, badgeList);
                contactListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                userIdIsTalkingTo = user_id;
                isInChatActivity = true;
                // clear up badge number in BG service
                if(BackgroundService.unReadMessage.containsKey(userIdList.get(position))){
                    BackgroundService.unReadMessage.put(userIdList.get(position), 0);
                }
                // new activity and wait for last message result
                getActivity().startActivityForResult(intent, 1);
            }
        });

        // Setup swipe refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // disable tab when loading notification
                ((Main) getActivity()).getTabWidget().setEnabled(false);

                //setupAdapter();
                //contactListView.setAdapter(adapter);
                Toast.makeText(getActivity(), "Everything is up to date.", Toast.LENGTH_SHORT).show();
                // enable tab
                ((Main) getActivity()).getTabWidget().setEnabled(true);

                // refresh animation
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        // refresh only at the top of the ListView mechanism
        contactListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (contactListView == null || contactListView.getChildCount() == 0) ? 0 : contactListView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        isInChatActivity = false;
        isVisible = true;

        // clear up notification
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(BackgroundService.NOTIFICATION_ID);
        BackgroundService.resetNumberOfPushNotification();

        if(isFirstTime) {
            isFirstTime = false;
            // load contact list for the first time
            loadInitialContactList();
            // init receiver for contact update
            regBroadcastReceiver();
        }

        if(needToUpdateLastMsg){
            int positionToUpdate = userIdList.indexOf(o_user_id);
            if(positionToUpdate != -1) {
                // update the last message
                hashMap.get(positionToUpdate).put("last_message", my_last_msg);
                // update listview
                adapter = new ContactListAdapter(Main.activity, hashMap, R.layout.contact_row, from, to, avatarList, badgeList);
                contactListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        isVisible = false;
        needToUpdateLastMsg = false;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        isInitialize = false;
    }

    private void loadInitialContactList(){
        proDialog = new ProgressDialog(getActivity());
        proDialog.setCanceledOnTouchOutside(false);
        proDialog.setCancelable(false);
        proDialog.setMessage("Loading contacts...");
        proDialog.show();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONArray jsonArray = ChatApi.GetContactList();
                    JSONObject firstObj = jsonArray.getJSONObject(0);

                    if(firstObj.getString("code").equals("1")){

                        int numberOfContacts = firstObj.getInt("number_of_contact");
                        totalContactsToLoad = numberOfContacts;

                        for(int i=1; i<(1+numberOfContacts); i++){

                            String other_user_id = jsonArray.getJSONObject(i).getString("other_user_id");

                            addToContactList(other_user_id, REQUEST_BY_USER_ID, false, true);

                        }
                    }else{
                        proDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();
    }

    private void regBroadcastReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction("private message");

        LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String messageText = intent.getStringExtra("message");
                String sender_user_id = intent.getStringExtra("sender_user_id");

                int positionToUpdate = userIdList.indexOf(sender_user_id);

                if(positionToUpdate != -1) {
                    // update the last message
                    hashMap.get(positionToUpdate).put("last_message", messageText);
                    // update the badge icon only if this page is visible
                    // or user is in ChatActivity but receives a message from a different user
                    if(!isInChatActivity || (isInChatActivity && !sender_user_id.equals(userIdIsTalkingTo))) {
                        int currentValue = badgeList.get(positionToUpdate);
                        badgeList.set(positionToUpdate, currentValue + 1);
                    }
                    // update listview
                    adapter = new ContactListAdapter(Main.activity, hashMap, R.layout.contact_row, from, to, avatarList, badgeList);
                    contactListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }
        };
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    /** setup the notification listview **/
    private void setupAdapter(final String first_name_last_name, final String user_id, final String lastMessage,final String lastMessageDateTime, final int unread_message, final boolean dismissDialog) throws InterruptedException {

        final HashMap<String, String> map = new HashMap<>();

        map.put("first_name_last_name", first_name_last_name);
        map.put("last_message", lastMessage);
        map.put("date_time", lastMessageDateTime);

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = UserApi.DownloadAvatar(user_id);

                bitmap = Global.getCircularBitmap(bitmap);

                avatarList.add(0, bitmap);

                nameList.add(0, first_name_last_name);

                badgeList.add(0, unread_message);

                userIdList.add(0, user_id);

                hashMap.add(0, map);

                adapter = new ContactListAdapter(getActivity(), hashMap, R.layout.contact_row, from, to, avatarList, badgeList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactListView.setAdapter(adapter);
                        if(dismissDialog)
                            pDialog.dismiss();
                        else if(hashMap.size() == totalContactsToLoad) {
                            proDialog.dismiss();
                            // update the initial unRead message after all contacts have been loaded
                        }
                    }
                });
            }
        });
        networkThread.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_chat_contacts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add) {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.add_contact_dialog, null);
            final EditText phoneET = (EditText)promptsView.findViewById(R.id.add_contact_phone_number);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Add",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    addToContactList(phoneET.getText().toString(), REQUEST_BY_PHONE_NUMBER, true, false);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToContactList(final String phone_number, final String REQUEST_TYPE, final boolean showDialog, final boolean assumingNoConflict){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog) {
                    if (pDialog == null)
                        pDialog = new ProgressDialog(getActivity());
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Loading contact...");
                    pDialog.show();
                }

                Thread networkThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final JSONObject getUserInfoResult = UserApi.GetUserInfo(phone_number, REQUEST_TYPE);
                            if (getUserInfoResult.getString("code").matches("1")) {

                                String target_user_id = getUserInfoResult.getString("user_id");

                                JSONObject addNewContactResult = ChatApi.AddNewContact(target_user_id);

                                JSONObject getSingleContactResult = ChatApi.GetSingleContact(target_user_id);

                                String last_msg = "";
                                String last_time = "";
                                int num_of_unread_message = 0;

                                if(getSingleContactResult.getString("code").matches("1")){
                                    last_msg = getSingleContactResult.getString("last_msg");
                                    last_time = getSingleContactResult.getString("last_msg_date_time");

                                    Long time = Long.parseLong(last_time);
                                    Date date = new Date(time);
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm aa");
                                    last_time = formatter.format(date).trim();

                                    num_of_unread_message = getSingleContactResult.getInt("number_of_unread_msg");
                                }

                                if (assumingNoConflict) {
                                    String displayName = getUserInfoResult.getString("first_name") + " " + getUserInfoResult.getString("last_name");
                                    String user_id = getUserInfoResult.getString("user_id");
                                    setupAdapter(displayName, user_id, last_msg, last_time, num_of_unread_message, showDialog);

                                } else if (addNewContactResult.getString("code").matches("1")) {

                                    String displayName = getUserInfoResult.getString("first_name") + " " + getUserInfoResult.getString("last_name");
                                    String user_id = getUserInfoResult.getString("user_id");
                                    setupAdapter(displayName, user_id, last_msg, last_time, num_of_unread_message, showDialog);

                                } else if (addNewContactResult.getString("code").matches("-3")) {
                                    if(showDialog){
                                        pDialog.dismiss();
                                    }
                                    makeToast("You cannot add yourself as a new contact.");

                                } else if (addNewContactResult.getString("code").matches("-5")) {
                                    if(showDialog){
                                        pDialog.dismiss();
                                    }
                                    makeToast("You've already added this contact.");
                                }
                            } else if (getUserInfoResult.getString("code").matches("-1")) {
                                if(showDialog){
                                    pDialog.dismiss();
                                }else{
                                    proDialog.dismiss();
                                }
                                makeToast("User is not found.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(showDialog){
                                pDialog.dismiss();
                            }else{
                                proDialog.dismiss();
                            }
                            makeToast("Server error occurs!");
                        }
                    }
                });
                networkThread.start();
            }
        });
    }

    private void makeToast(final String toastMsg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
