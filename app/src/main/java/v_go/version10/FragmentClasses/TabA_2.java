package v_go.version10.FragmentClasses;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.SegmentedButton;
import v_go.version10.R;
import v_go.version10.googleMapServices.DirectionsJSONParser2;

public class TabA_2 extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {

    private View view; // This view

    //Global variables for post trip
    private String time = "";
    private String duration = "";
    private String distance = "";
    private String locaA = "";
    private String locaB = "";
    private double latA = 0;
    private double lngA = 0;
    private double latB = 0;
    private double lngB = 0;
    private int type = 0;
    private int allow_multi = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_a_2, container, false);
        this.view = view;

        // change toolbar title
        getActivity().setTitle("Summary");
        ((Main)getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        // Create the segmented buttons
        SegmentedButton buttons = (SegmentedButton) view.findViewById(R.id.segmented);
        buttons.clearButtons();
        buttons.addButtons("Passenger", "Driver");

        // First button is selected
        buttons.setPushedButtonIndex(Global.SELECTED_TYPE);

        // Some example click handlers. Note the click won't get executed
        // if the segmented button is already selected (dark blue)
        buttons.setOnClickListener(new SegmentedButton.OnClickListenerSegmentedButton() {
            @Override
            public void onClick(int index) {
                if (index == 0) {
                    type = 0;
                    Global.SELECTED_TYPE = 0;
                } else {
                    type = 1;
                    Global.SELECTED_TYPE = 1;
                }
            }
        });

        // get args from map fragment
        try {
            latA = getArguments().getDouble("lat_a");
            lngA = getArguments().getDouble("lng_a");
            latB = getArguments().getDouble("lat_b");
            lngB = getArguments().getDouble("lng_b");
            locaA = getArguments().getString("address_a");
            locaB = getArguments().getString("address_b");
        }catch (Exception e){
            e.printStackTrace();
        }
        // show addresses
        TextView pointA = (TextView)view.findViewById(R.id.point_a);
        TextView pointB = (TextView)view.findViewById(R.id.point_b);
        pointA.setTextColor(Color.BLACK);
        pointB.setTextColor(Color.BLACK);
        pointA.setText(locaA);
        pointB.setText(locaB);

        // initialize two latlng objects
        LatLng pickup = new LatLng(latA, lngA);
        LatLng dest = new LatLng(latB, lngB);

        // getting URL to the Google Directions API
        String url = getDirectionsUrl(pickup, dest);

        // calculate time and distance
        DownloadTask3 downloadTask3 = new DownloadTask3();
        downloadTask3.execute(url);

        // preset date & time
        TextView dateTime = (TextView)view.findViewById(R.id.when);
        if(Global.DATE_TIME != ""){
            dateTime.setText(Global.DATE_TIME);
            dateTime.setTextColor(Color.BLACK);
        }

        // SCHEDULE BUTTON IS CLICKED
        final ImageView iv = (ImageView) view.findViewById(R.id.calendar);
        final View innerView = view;
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                scheduleTrip(innerView);
            }
        });

        Button matchButton = (Button)view.findViewById(R.id.match);
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(time.matches("")){
                    Toast.makeText(getActivity(), "You haven't selected data & time.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Pass data arguments to TabA_3
                Bundle args = new Bundle();
                args.putDouble("lat_a", latA);
                args.putDouble("lng_a", lngA);
                args.putDouble("lat_b", latB);
                args.putDouble("lng_b", lngB);
                args.putString("address_a", locaA);
                args.putString("address_b", locaB);
                args.putString("distance", distance);
                args.putString("duration", duration);
                args.putString("time", time);
                args.putInt("type", type);
                args.putInt("allow_multi", allow_multi);

                Fragment fragment = new TabA_3();
                fragment.setArguments(args);
                ((Main) getActivity()).pushFragments(Global.TAB_A, fragment, true, true);
            }
        });

        // search on google button is pressed
        Button toGoogleButton = (Button)view.findViewById(R.id.toGoogle);
        toGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+latA+","+lngA+"&daddr="+latB+","+lngB));
                startActivity(intent);
            }
        });
        // allow multiple passenger toggle
        final Switch sw = (Switch)view.findViewById(R.id.switch1);
        if(Global.ALLOW_MUL_PASSEN == 1) {
            sw.setChecked(true);
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    allow_multi = 1;
                    Global.ALLOW_MUL_PASSEN = 1;
                } else {
                    allow_multi = 0;
                    Global.ALLOW_MUL_PASSEN = 0;
                }
            }
        });

        return view;
    }


    // PICK DATE AND TIME
    private int year, month, day, hour, minute;
    public void scheduleTrip(View view){
        initDateTimeData();
        Calendar cDefault = Calendar.getInstance();
        cDefault.set(year, month, day);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.DAY_OF_MONTH)
        );

        Calendar cMin = Calendar.getInstance();
        Calendar cMax = Calendar.getInstance();
        cMax.set( cMax.get(Calendar.YEAR), 11, 31 );
        datePickerDialog.setMinDate(cMin);
        datePickerDialog.setMaxDate(cMax);

        List<Calendar> daysList = new LinkedList<>();
        Calendar[] daysArray;
        Calendar cAux = Calendar.getInstance();

        while( cAux.getTimeInMillis() <= cMax.getTimeInMillis() ){

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis( cAux.getTimeInMillis() );

            daysList.add( c );

            cAux.setTimeInMillis( cAux.getTimeInMillis() + ( 24 * 60 * 60 * 1000 ) );
        }
        daysArray = new Calendar[ daysList.size() ];
        for( int i = 0; i < daysArray.length; i++ ){
            daysArray[i] = daysList.get(i);
        }

        datePickerDialog.setSelectableDays( daysArray );
        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show( getActivity().getFragmentManager(), "DatePickerDialog" );
    }

    private void initDateTimeData(){
        if( year == 0 ){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        /*
        year = month = day = hour = minute = 0;
        when.setText("  please set the time & date");
        when.setTextColor(defaultTextColor);
        */
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        Calendar tDefault = Calendar.getInstance();
        tDefault.set(year, month, day, hour, minute);

        year = i;
        month = i1;
        day = i2;

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                tDefault.get(Calendar.HOUR_OF_DAY),
                tDefault.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getActivity().getFragmentManager(), "timePickerDialog");
        timePickerDialog.setTitle("PICKUP TIME");
        timePickerDialog.setThemeDark(true);
    }


    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {
        /*
        if( i < 9 || i > 18 ){
            onDateSet(null, year, month, day);
            Toast.makeText(this, "Somente entre 9h e 18h", Toast.LENGTH_SHORT).show();
            return;
        }
        */
        TextView when = (TextView)view.findViewById(R.id.when);

        hour = i;
        minute = i1;

        //YYYY-MM-DD at HH:mm
        when.setText("    " + year + "-" +
                (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "-" +
                (day < 10 ? "0" + day : "" + day) + " at " +
                (hour < 10 ? "0" + hour : hour) + ":" +
                (minute < 10 ? "0" + minute : minute));
        when.setTextColor(Color.BLACK);
        // save it as global variable
        Global.DATE_TIME = "    " + year + "-" +
                (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "-" +
                (day < 10 ? "0" + day : "" + day) + " at " +
                (hour < 10 ? "0" + hour : hour) + ":" +
                (minute < 10 ? "0" + minute : minute);

        //YYYY-MM-DD HH:mm
        this.time = (year + "-" +
                (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "-" +
                (day < 10 ? "0" + day : "" + day) + " " +
                (hour < 10 ? "0" + hour : hour) + ":" +
                (minute < 10 ? "0" + minute : minute));
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

    //GOOGLE FIND ROUTE SERVICES START HERE--------------------------------------------------------------------------------

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception download url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //FIND DISTANCE AND TRAVEL TIME BETWEEN A AND B START HERE--------------------------------------------------------------------
    // Fetches data from url passed
    private class DownloadTask3 extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask3 parserTask3 = new ParserTask3();

            // Invokes the thread for parsing the JSON data
            parserTask3.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask3 extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser2 parser = new DirectionsJSONParser2();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            String distance1 = "";
            String duration1 = "";

            try {
                if (result.size() < 1) {
                    return;
                }

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {


                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance1 = point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration1 = point.get("duration");
                            continue;
                        }

                    }

                }
                TextView distancetv = (TextView) view.findViewById(R.id.distance);
                TextView durationtv = (TextView) view.findViewById(R.id.duration);
                distancetv.setText(distance1);
                durationtv.setText(duration1);
                distancetv.setTextColor(Color.BLACK);
                durationtv.setTextColor(Color.BLACK);
                distance = distance1;
                duration = duration1;

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
