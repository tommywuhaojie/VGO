package v_go.version10.FragmentClasses;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.MySimpleAdapter;
import v_go.version10.HelperClasses.Notification;
import v_go.version10.R;

public class TabC_2 extends Fragment   {

    private View view;
    private SimpleAdapter adapter;
    private ListView notificationListView;
    private Stack<Notification> notifStack;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        view = inflater.inflate(R.layout.tab_c_2, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // change toolbar title
        getActivity().setTitle("Notifications");
        ((Main) getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        // set up the notification list
        notificationListView = (ListView) view.findViewById(R.id.listView);
        // on listView item clicked
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                try {
                    Notification notif = notifStack.get(notifStack.size() - 1 - position);
                    args.putInt("req_id", notif.getRequestId());
                    args.putInt("trip_id", notif.getTripId());
                    args.putInt("type", notif.getType());
                    args.putInt("rec_flag", notif.getRecFlag());
                    args.putString("start_location", notif.getStartLocation());
                    args.putString("end_location", notif.getEndLocation());
                    args.putString("name", notif.getFirstName());
                    args.putString("message", getMessage(notif));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Fragment fragment = new TabC_3();
                fragment.setArguments(args);
                ((Main) getActivity()).pushFragments(Global.TAB_C, fragment, true, true);
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

                setupAdapter();
                notificationListView.setAdapter(adapter);
                // enable tab
                ((Main) getActivity()).getTabWidget().setEnabled(true);

                // refresh animation
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        // refresh only at the top of the ListView mechanism
        notificationListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (notificationListView == null || notificationListView.getChildCount() == 0) ? 0 : notificationListView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    /** setup adapter here to avoid lagging during changing view **/
    /*
    @Override
    public void onResume() {
        super.onResume();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // progress bar
                progressBar = (ProgressBar) view.findViewById(R.id.pbHeaderProgress);
                progressBar.setVisibility(View.VISIBLE);
                // load data to the listview
                // refresh the notif stack
                ((Main)getActivity()).refreshNotifStack();
            }
        });
        thread.start();
    }
    */

    /** setup the notification listview **/
    public void setupAdapter() {
        String[] from = new String[]{"name", "message"};
        int[] to = new int[]{R.id.firstLine, R.id.secondLine};
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

        // refresh the notif stack
        //((Main)getActivity()).refreshNotifStack();
        // get the notif stack from main
        notifStack = ((Main)getActivity()).getNotifStack();

        // make a clone copy of the notif tack
        @SuppressWarnings("unchecked")
        Stack<Notification> notifStackCopy = ((Stack<Notification>)notifStack.clone());

        int size = notifStack.size();
        if(size == 0){
            Toast.makeText(getActivity(), "You don't have any notification.", Toast.LENGTH_LONG).show();
        }else{
            for(int i=0; i<size; i++) {

                HashMap<String, String> map = new HashMap<String, String>();

                /**
                if(i==size){ // for the last row
                    map.put("name", "Press Here to Load Older Notifications...");
                    map.put("message", "");
                    fillMaps.add(map);
                    continue;
                }*/

                Notification notif = notifStackCopy.pop();
                map.put("name", notif.getFirstName());

                /** determine notification type and result **/
                map.put("message",getMessage(notif));

                fillMaps.add(map);
            }
            adapter = new MySimpleAdapter(getActivity(), fillMaps, R.layout.notification_row, from, to);
        }

        // run on ui thread
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                setupListView();
            }
        });
    }

    private void setupListView(){
        // setAdapter for listview
        notificationListView.setAdapter(adapter);
        // dismiss progress bar
        progressBar.setVisibility(View.GONE);
    }

    /** determine notification type and result message **/
    private String getMessage(Notification notif){

        /** --------------------------------- Pending Request as Passenger -----------------------------------------------------**/
        if(notif.getType() == 0){
            // receive
            if(notif.getRecFlag() == 0){
                return (notif.getFirstName()+" wishes to join your trip as a passenger!");
                //send
            }else{
                return ("Passenger request sent to "+notif.getFirstName()+"!");
            }

            /** ------------------------------- Pending Request as Driver ------------------------------------------------------**/
        }else if(notif.getType() == 1){
            // receive
            if(notif.getRecFlag() == 0){
                return (notif.getFirstName()+" wishes to join your trip as a driver!");
                //send
            }else{
                return ("Driver request sent to "+notif.getFirstName()+"!");
            }

            /** -------------------------------------- Rejected ----------------------------------------------------------------**/
        }else if(notif.getType() == 2){
            // receive
            if(notif.getRecFlag() == 0){
                return ("You have rejected "+notif.getFirstName()+"'s request.");
                //send
            }else{
                return ("Your request has been rejected by "+notif.getFirstName()+".");
            }
            /** -------------------------------------- Accepted -----------------------------------------------------------------**/
        }else if(notif.getType() == 3){
            // receive
            if(notif.getRecFlag() == 0){
                return ("You have accepted "+notif.getFirstName()+"'s request.");
                //send
            }else{
                return ("Your request has been accepted by "+notif.getFirstName()+"!");
            }
        }
        /** --------------------------------------------------------------------------------------------------------------------  **/
        return "";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            ((Main)getActivity()).popFragments();
        }
        return super.onOptionsItemSelected(item);
    }
}