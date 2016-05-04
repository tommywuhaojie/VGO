package v_go.version10.FragmentClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.Trip;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.MySimpleAdapter;
import v_go.version10.R;

public class TabA_3 extends Fragment{

    private View view;
    private SimpleAdapter adapter;
    private ListView tripList;
    JSONArray jsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_a_3, container, false);
        this.view = view;

        // change toolbar title
        getActivity().setTitle("Similar Trips");
        ((Main)getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        // Setup swipe refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

                simpleArray();
            }
        });

        // set up list
        tripList  = (ListView) view.findViewById(R.id.listView);
        simpleArray();

        // on listView item clicked
        tripList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                try {
                    JSONObject jsObject = jsonArray.getJSONObject(position);
                    args.putString("trip_id",jsObject.getString("trip_id"));
                    args.putString("start_location", jsObject.getString("start_location"));
                    args.putString("end_location", jsObject.getString("end_location"));
                    args.putString("time", jsObject.getString("time"));

                    //round of start and end differences
                    double start_diff = Math.round(jsObject.getDouble("start_dif") * 1000.000) / 1000.000;
                    double end_diff = Math.round(jsObject.getDouble("end_dif") * 1000.000) / 1000.000;

                    args.putString("a_diff", start_diff + " km");
                    args.putString("b_diff", end_diff + " km");

                    // calculate time difference
                    String time1 = getArguments().getString("time").trim();
                    String time2 = jsObject.getString("time");
                    time2 = time1.substring(0,time1.indexOf(" ")) +" "+ time2;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date1 = format.parse(time1);
                    Date date2 = format.parse(time2);
                    long ms = date2.getTime() - date1.getTime();
                    String diff;
                    if (ms < 0) { // get absolute value
                        ms *= -1;
                        diff = "- " + msToString(ms);
                    }else{
                        diff = "+ " + msToString(ms);
                    }
                    args.putString("time_diff",diff);

                    //type
                    if(jsObject.getInt("register_as") == 0){
                        args.putString("type", "Passenger");
                    }else{
                        args.putString("type", "Driver");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                Fragment fragment = new TabA_4();
                fragment.setArguments(args);
                ((Main) getActivity()).pushFragments(Global.TAB_A, fragment, true, true);
            }

        });

        // refresh only at the top of the ListView mechanism
        tripList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (tripList == null || tripList.getChildCount() == 0) ? 0 : tripList.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    private void simpleArray(){

        Thread networkThread = new Thread(new Runnable(){
            @Override
            public void run() {
                //allow multiple passengers 0=no 1=yes

                try {
                    // retrieve matching trip list
                    Trip trip = new Trip();
                    jsonArray = trip.MatchTripsBy(
                            getArguments().getDouble("lat_a"),
                            getArguments().getDouble("lng_a"),
                            getArguments().getDouble("lat_b"),
                            getArguments().getDouble("lng_b"),
                            getArguments().getString("time"),
                            getArguments().getInt("type"),
                            getArguments().getInt("allow_multi"));

                    if (jsonArray == null) {
                        Toast.makeText(getActivity(), "Error:API return null object", Toast.LENGTH_SHORT).show();
                    } else {

                        String[] from = new String[] {"start", "end", "time", "start_diff", "end_diff", "time_diff", "type"};
                        int[] to = new int[] { R.id.start_adr, R.id.end_adr, R.id.depart_time, R.id.start_diff, R.id.end_diff, R.id.time_diff, R.id.passenger_or_driver};
                        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

                        final int size = jsonArray.length();
                        if(size == 0){
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getActivity(), "Unfortunately, no trip matched.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        for(int i=0; i<size; i++) {
                            JSONObject jsObject = jsonArray.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("start", jsObject.getString("start_location"));
                            map.put("end", jsObject.getString("end_location"));
                            map.put("time",jsObject.getString("time"));

                            //round of start and end differences
                            double start_diff = Math.round(jsObject.getDouble("start_dif") * 1000.000) / 1000.000;
                            double end_diff = Math.round(jsObject.getDouble("end_dif") * 1000.000) / 1000.000;

                            map.put("start_diff", start_diff+" km");
                            map.put("end_diff", ""+ end_diff+" km");

                            // calculate time difference
                            String time1 = getArguments().getString("time").trim();
                            String time2 = jsObject.getString("time");
                            time2 = time1.substring(0,time1.indexOf(" ")) +" "+ time2;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date date1 = format.parse(time1);
                            Date date2 = format.parse(time2);
                            long ms = date2.getTime() - date1.getTime();
                            String diff;
                            if (ms < 0) { // get absolute value
                                ms *= -1;
                                diff = "- " + msToString(ms);
                            }else{diff = "+ " + msToString(ms); }

                            map.put("time_diff", diff);

                            //type
                            if(jsObject.getInt("register_as") == 0){
                                map.put("type", "PASSENGER");
                            }else{
                                map.put("type", "DRIVER");
                            }
                            fillMaps.add(map);
                        }
                        adapter = new MySimpleAdapter(getActivity(), fillMaps, R.layout.match_trip_info_row, from, to);

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                tripList.setAdapter(adapter);
                                // make address scrolling horizontally
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }});
        networkThread.start();
    }

    public static String msToString(long ms) {
        long totalSecs = ms/1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);
        if (hours > 0)
            return hours + "hr" + minsString + "min";
        else if (mins > 0)
            return mins + "min";
        else
            return (0 + "min");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_find_trip, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            ((Main)getActivity()).popFragments();
        }
        if(id == R.id.redo){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            //pop twice
                            ((Main)getActivity()).popFragments(2);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setMessage("Do you want to redo everything?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        if(id == R.id.postTrip){
            // post current trip
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            postTripe();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // prevent dialogs from closing by outside click
            builder.setCancelable(false);
            builder.setMessage("Do you want to post this trip?\n\n" +
                    "FROM:  " + getArguments().getString("address_a") +"\n\n"+
                    "TO:  " + getArguments().getString("address_b") + "\n\n"+
                    "WHEN: " + getArguments().getString("time")).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void postTripe(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    final ProgressDialog[] pDialog = new ProgressDialog[1];
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pDialog[0] = new ProgressDialog(getActivity());
                            pDialog[0].setCancelable(false);
                            pDialog[0].setCanceledOnTouchOutside(false);
                            pDialog[0].setMessage("Posting...");
                            pDialog[0].show();
                        }
                    });

                    // convert duration
                    String time = getArguments().getString("duration");
                    int estTime;
                    if(time.matches("(.*)mins")){
                        String min = time.substring(0, time.indexOf('m')).trim();
                        estTime = Integer.parseInt(min);
                    }else{
                        String hour = time.substring(0, time.indexOf('h')).trim();
                        String min = time.substring(time.indexOf('h') + 4, time.indexOf('m')).trim();
                        estTime = Integer.parseInt(hour) * 60 + Integer.parseInt(min);
                    }

                    // convert distance
                    String distance = getArguments().getString("distance");
                    double estDist = 0;
                    if (distance.matches("(.*)km")) {
                        String km = distance.substring(0, distance.indexOf('k')).trim();
                        estDist = Double.parseDouble(km);
                    } else if(distance.matches("(.*)mi")){
                        String mi = distance.substring(0, distance.indexOf('m')).trim();
                        estDist = Double.parseDouble(mi) * 1.60934;
                    }else if(distance.matches("(.*)m")){
                        String m = distance.substring(0, distance.indexOf('m')).trim();
                        estDist = Double.parseDouble(m) / 1000;
                    }

                    String result = "-5";
                    Trip trip = new Trip();
                    result = trip.RegisterTrip(
                            getArguments().getString("time"),
                            getArguments().getDouble("lat_a"),
                            getArguments().getDouble("lng_a"),
                            getArguments().getString("address_a"),
                            getArguments().getDouble("lat_b"),
                            getArguments().getDouble("lng_b"),
                            getArguments().getString("address_b"),
                            estTime,
                            estDist,
                            getArguments().getInt("type"));

                    final int res = Integer.parseInt(result);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);
                            pDialog[0].dismiss();
                            switch (res) {
                                case 1:
                                    alertDialog.setTitle("Done!");
                                    alertDialog.setIcon(R.drawable.check);
                                    alertDialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
                                    alertDialog.setMessage("Trip posted.");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // here you can add functions
                                            ((Main)getActivity()).popFragments(2);
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                                case -1:
                                    alertDialog.setMessage("Fail to register a new trip.");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // here you can add functions
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                                case -2:
                                    alertDialog.setTitle("Ops!");
                                    alertDialog.setIcon(R.drawable.fail);
                                    alertDialog.setMessage("A trip with a similar time already exist.");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // here you can add functions
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                                case -3:
                                    alertDialog.setMessage("Fill in all fields.");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // here you can add functions
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                                case -4:
                                    alertDialog.setMessage("Need to login first.");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // here you can add functions
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
