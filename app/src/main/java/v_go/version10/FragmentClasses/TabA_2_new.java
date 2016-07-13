package v_go.version10.FragmentClasses;

import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;
import v_go.version10.googleMapServices.DirectionsJSONParser1;
import v_go.version10.googleMapServices.DirectionsJSONParser2;

public class TabA_2_new extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {

    private View view; // This view
    private GoogleMap mMap;

    //Global variables for post trip
    private int year, month, day, hour, minute;
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

    private TextView dateTextView;
    private TextView timeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_a_2_new, container, false);
        this.view = view;

        // change toolbar title
        getActivity().setTitle("Summary");
        ((Main)getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        dateTextView = (TextView) view.findViewById(R.id.date);
        timeTextView = (TextView) view.findViewById(R.id.date_time);

        // initial default datetime as now
        Calendar cal = Calendar.getInstance();
        int yr = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        int dy = cal.get(Calendar.DAY_OF_MONTH);
        int hr = cal.get(Calendar.HOUR); hour = hr;
        int min = cal.get(Calendar.MINUTE); minute = min;
        dateTextView.setText(customDateFormat(yr, mon, dy));
        timeTextView.setText(customTimeFormat(hr, min));

        // init map
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        setRetainInstance(true);

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

        // draw marker and route on map
        final MarkerOptions marker_a = new MarkerOptions();
        marker_a.position(new LatLng(latA, lngA));
        marker_a.icon(BitmapDescriptorFactory.fromResource(R.drawable.point_a));
        marker_a.title("Pickup");

        final MarkerOptions marker_b = new MarkerOptions();
        marker_b.position(new LatLng(latB, lngB));
        marker_b.icon(BitmapDescriptorFactory.fromResource(R.drawable.point_b));
        marker_b.title("Destination");

        mMap.addMarker(marker_a);
        mMap.addMarker(marker_b);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                drawRoute(marker_a, marker_b);
            }
        });

        // show addresses
        TextView pointA = (TextView)view.findViewById(R.id.point_a);
        TextView pointB = (TextView)view.findViewById(R.id.point_b);
        pointA.setTextColor(Color.BLACK);
        pointB.setTextColor(Color.BLACK);
        pointA.setText(locaA);
        pointB.setText(locaB);


        // getting URL to the Google Directions API
        String url = getDirectionsUrl(
                new LatLng(latA, lngA),
                new LatLng(latB, lngB));

        // calculate time and distance
        DownloadTask3 downloadTask3 = new DownloadTask3();
        downloadTask3.execute(url);

        // preset date & time
        //TextView dateTime = (TextView)view.findViewById(R.id.date);
        //if(Global.DATE_TIME != ""){
        //    dateTime.setText(Global.DATE_TIME);
        //}

        // pop up select date dialog
        ImageView dateBoxView = (ImageView) view.findViewById(R.id.date_box);
        dateBoxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iv.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
                openSelectDatePickerDialog();
            }
        });

        // pop up select time dialog
        ImageView timeBoxView = (ImageView) view.findViewById(R.id.time_box);
        timeBoxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectTimePickerDialog();
            }
        });


        ImageView findRideButton = (ImageView)view.findViewById(R.id.find_ride_button);
        findRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dateTime = year + "-" +
                        (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "-" +
                        (day < 10 ? "0" + day : "" + day) + " at " +
                        (hour < 10 ? "0" + hour : hour) + ":" +
                        (minute < 10 ? "0" + minute : minute);

                // save it as global variable
                //Global.DATE_TIME = dateTime;

                //YYYY-MM-DD HH:mm
                time = dateTime;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void drawRoute(MarkerOptions marker_a, MarkerOptions marker_b){
        // zoom out to see all markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker_a.getPosition());
        builder.include(marker_b.getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        mMap.moveCamera(cu);
        float zoom = mMap.getCameraPosition().zoom;
        LatLng center = new LatLng(mMap.getCameraPosition().target.latitude + 0.02, mMap.getCameraPosition().target.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom - 1.1f));
        // draw route
        String url = getDirectionsUrl(marker_a.getPosition(), marker_b.getPosition());
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    // PICK DATE AND TIME
    public void openSelectDatePickerDialog(){
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

        datePickerDialog.setSelectableDays(daysArray);
        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }

    private void initDateTimeData(){
        if( year == 0 ){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            //hour = c.get(Calendar.HOUR_OF_DAY);
            //minute = c.get(Calendar.MINUTE);
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
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2)
    {
        year = i;
        month = i1;
        day = i2;

        String dateStr = customDateFormat(year, month, day);
        if(dateStr != null){
            dateTextView.setText(dateStr);
        }
    }

    private void openSelectTimePickerDialog(){
        Calendar tDefault = Calendar.getInstance();

        tDefault.set(year, month, day, hour, minute);

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                tDefault.get(Calendar.HOUR_OF_DAY),
                tDefault.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getActivity().getFragmentManager(), "timePickerDialog");
        timePickerDialog.setTitle("Pickup Time");
        timePickerDialog.setThemeDark(true);
    }


    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1)
    {
        hour = i;
        minute = i1;
        timeTextView.setText(customTimeFormat(hour, minute));
    }

    private String customDateFormat(int year, int mouth, int day){
        String date;
        try {
            DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
            fromFormat.setLenient(false);
            DateFormat toFormat = new SimpleDateFormat("MMM d yyyy");
            toFormat.setLenient(false);
            String dateStr = year + "-" +(month + 1) + "-" + day;
            Date dateObj = fromFormat.parse(dateStr);
            date = toFormat.format(dateObj).trim();

            String capMonth = date.substring(0,3).toUpperCase();
            String dayStr = "";
            if(day == 1 || day == 21 || day == 31)
                dayStr = day + "st";
            else if(day == 2 || day == 22)
                dayStr = day + "nd";
            else if(day == 3 || day == 23)
                dayStr = day + "rd";
            else
                dayStr = day + "th";

            date = capMonth + " " + dayStr + " " + year;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    private String customTimeFormat(int hour, int minute){
        String hourStr;
        String am_or_pm;
        if(hour >= 12) {
            if(hour != 12) {
                hourStr = (hour - 12) + "";
            }else{
                hourStr = hour + "";
            }
            am_or_pm = "pm";
        }else{
            if(hour == 0) {
                hourStr = "12";
            }else {
                hourStr = hour + "";
            }
            am_or_pm = "am";
        }
        return hourStr + ":" + (minute < 10 ? "0" + minute : minute) + am_or_pm;
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

    /** Draw the route between two markers methods **/
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser1 parser = new DirectionsJSONParser1();
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
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.BLUE);
            }
            try {
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    // FIND DISTANCE AND TRAVEL TIME BETWEEN A AND B START HERE ***********************************
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
