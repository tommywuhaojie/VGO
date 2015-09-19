package v_go.version10;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import v_go.version10.googleMapServices.DirectionsJSONParser;

public class NewTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {

    private GoogleMap mMap;
    private TextView when;
    private Button pickWhen;
    private int defaultTextColor;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                if(data != null){
                    String address_a = data.getStringExtra("address_a");
                    String address_b = data.getStringExtra("address_b");
                    String distance = data.getStringExtra("distance");
                    String time = data.getStringExtra("time");
                    String lat_a = data.getStringExtra("lat_a");
                    String lng_a = data.getStringExtra("lng_a");
                    String lat_b = data.getStringExtra("lat_b");
                    String lng_b = data.getStringExtra("lng_b");

                    mMap.clear();
                    LatLng latLng_a = new LatLng(Double.parseDouble(lat_a), Double.parseDouble(lng_a));
                    LatLng latLng_b = new LatLng(Double.parseDouble(lat_b), Double.parseDouble(lng_b));
                    MarkerOptions marker_a = new MarkerOptions().position(latLng_a).title("PICKUP");
                    MarkerOptions marker_b = new MarkerOptions().position(latLng_b).title("DESTINATION");
                    marker_a.icon(BitmapDescriptorFactory.fromResource(R.drawable.a_marker_resized));
                    marker_b.icon(BitmapDescriptorFactory.fromResource(R.drawable.b_marker_resized));
                    mMap.addMarker(marker_a);
                    mMap.addMarker(marker_b);
                    // Zoom out to see all markers
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(marker_a.getPosition());
                    builder.include(marker_b.getPosition());
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    mMap.moveCamera(cu);
                    float zoom = mMap.getCameraPosition().zoom;
                    LatLng center = mMap.getCameraPosition().target;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom - 1.5f));

                    // Draw Route
                    String url = getDirectionsUrl(latLng_a, latLng_b);
                    // Start downloading json data from Google Directions API
                    //drawing the route between A and B
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);

                    TextView fromView = (TextView) findViewById(R.id.from);
                    TextView toView = (TextView) findViewById(R.id.to);
                    TextView distanceView = (TextView) findViewById(R.id.distance);
                    TextView timeView = (TextView) findViewById(R.id.time);
                    TextView priceView = (TextView) findViewById(R.id.price);

                    fromView.setText(address_a);
                    fromView.setTextColor(Color.BLACK);
                    fromView.setSelected(true);
                    toView.setText(address_b);
                    toView.setTextColor(Color.BLACK);
                    toView.setSelected(true);
                    distanceView.setText(distance);
                    distanceView.setTextColor(Color.BLACK);
                    timeView.setText(time);
                    timeView.setTextColor(Color.BLACK);

                    //calculate suggested price
                    final double RATE = 0.36;
                    final double MILE_TO_KM = 1.60934;

                    String dis = distance.trim();

                    if (dis.matches("(.*)km")) {
                        String km = dis.substring(0, dis.indexOf('k')).trim();
                        double d = 0;
                        try {
                            d = Double.parseDouble(NumberFormat.getNumberInstance(Locale.US).parse(km).toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        d = Math.round(d * RATE * 100.0) / 100.0;
                        priceView.setText("$ " + d);
                        priceView.setTextColor(Color.BLACK);

                    } else if(dis.matches("(.*)mi")){
                        String mi = dis.substring(0, dis.indexOf('m')).trim();
                        double d = 0;
                        try {
                            d = Double.parseDouble(NumberFormat.getNumberInstance(Locale.US).parse(mi).toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        d = Math.round(d * MILE_TO_KM * RATE * 100.0) / 100.0;
                        priceView.setText("$ " + d);
                        priceView.setTextColor(Color.BLACK);

                    }else if(dis.matches("(.*)m")){
                        String m = dis.substring(0, dis.indexOf('m')).trim();
                        double d = 0;
                        try {
                            d = Double.parseDouble(NumberFormat.getNumberInstance(Locale.US).parse(m).toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        d = Math.round(d * RATE * 100.0) / 100.0;
                        priceView.setText("$ " + d / 1000);
                        priceView.setTextColor(Color.BLACK);
                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        //Back button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // check if GPS enabled
        //GPSTracker gpsTracker = new GPSTracker(this);

        /*
        if (gpsTracker.getIsGPSTrackingEnabled()){

            //---------------------------------------------------------------
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            String stringLongitude = String.valueOf(gpsTracker.longitude);
            String country = gpsTracker.getCountryName(this);
            String city = gpsTracker.getLocality(this);
            String postalCode = gpsTracker.getPostalCode(this);
            String addressLine = gpsTracker.getAddressLine(this);
            //---------------------------------------------------------------


            final LatLng CURRENT_LOCATION = new LatLng(gpsTracker.latitude,gpsTracker.longitude);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
        */

        final LatLng CENTRE_VANCOUVER = new LatLng(49.229749, -123.055929);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CENTRE_VANCOUVER, 9.0f);//the larger the number -> the closer it zooms in
        mMap.moveCamera(update);

        when = (TextView) findViewById(R.id.when);
        defaultTextColor = when.getTextColors().getDefaultColor();
    }

    //when SET button is clicked to set trip
    public void setTripIsClicked(View view){
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click));
        Intent intent = new Intent(this, SetMarker.class);
        intent.putExtra("type", "A");
        final int REQUEST_CODE = 1;
        startActivityForResult(intent, REQUEST_CODE);
    }
    // SCHEDULE BUTTON IS CLICKED
    public void scheduleIsClicked(View view){
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click));
        scheduleTrip(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            Intent intent = new Intent(this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        datePickerDialog.show( getFragmentManager(), "DatePickerDialog" );
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
        timePickerDialog.show(getFragmentManager(), "timePickerDialog");
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

        hour = i;
        minute = i1;

        //YYYY-MM-DD FORMAT
        when.setText("    " + year + "-" +
                (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "-" +
                (day < 10 ? "0" + day : "" + day) + " at " +
                (hour < 10 ? "0" + hour : hour) + ":" +
                (minute < 10 ? "0" + minute : minute));
        when.setTextColor(Color.BLACK);
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

    // Fetches data from url passed
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
        // doInBackground()
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
                DirectionsJSONParser parser = new DirectionsJSONParser();

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
                lineOptions.width(10);
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
    //GOOGLE FIND ROUTE SERVICES END-----------------------------------------------------------------------------------------
}
