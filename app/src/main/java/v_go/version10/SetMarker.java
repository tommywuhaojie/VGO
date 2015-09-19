package v_go.version10;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import v_go.version10.googleMapServices.DirectionsJSONParser;
import v_go.version10.googleMapServices.DirectionsJSONParser2;
import v_go.version10.googleMapServices.GeocodeJSONParser;

public class SetMarker extends AppCompatActivity{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String type;
    private String distance;
    private String time;
    private Marker[] markerAry;
    private String[] addressAry;
    private boolean isRouteFound;
    private boolean isRouteReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtity_set_marker);
        setUpMapIfNeeded();

        // Initialization
        markerAry = new Marker[2];
        addressAry = new String[2];
        type = "";
        distance = "";
        time = "";

        //Back button
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        //Action bar title and set button color
        Button doneButton = (Button)findViewById(R.id.done);
        doneButton.setBackgroundColor(Color.parseColor("#5DAB00"));//LIGHT GREEN
        doneButton.setVisibility(View.GONE);
        Intent intent = getIntent(); // gets the previously created intent
        final String inner_type = intent.getStringExtra("type");

        type = inner_type;
        getSupportActionBar().setTitle("Set Pickup Location");
        Button button = (Button)findViewById(R.id.set);
        button.setBackgroundColor(Color.parseColor("#5DAB00"));//LIGHT GREEN
        button.setText("SET PICKUP");

        //change SearchView background color
        final SearchView searchAddress = (SearchView) findViewById(R.id.searchAddress);
        searchAddress.setIconified(false);


        searchAddress.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddress.setBackgroundColor(Color.parseColor("#AAAAAA"));
            }
        });

        searchAddress.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchAddress.setBackgroundColor(Color.TRANSPARENT);
                return false;
            }
        });



        //search address and change camera position
        searchAddress.setOnQueryTextListener( new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {

                String location = searchAddress.getQuery().toString().trim();

                if(location==null || location.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Address cannot be empty.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                    toast.show();
                    return false;
                }

                String url = "https://maps.googleapis.com/maps/api/geocode/json?";

                try {
                    // encoding special characters like space in the user input place
                    location = URLEncoder.encode(location, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String address = "address=" + location;

                String sensor = "sensor=false";

                // url , from where the geocoding data is fetched
                url = url + address + "&" + sensor;

                // Instantiating DownloadTask to get places from Google Geocoding service
                // in a non-ui thread
                DownloadTask2 downloadTask = new DownloadTask2();

                // Start downloading the geocoding places
                downloadTask.execute(url);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });


        //GOOGLE MAP-----------------------------------------------------------------
        // enable my location button/ zoom button/ repositioning the button
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        /*mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0,1200,0,0);
        */

        //put marker in the centre of the map
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

                // Get the center of the Map.
                List<Address> addresses = null;

                LatLng centerOfMap = mMap.getCameraPosition().target;

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                try {
                    addresses = geocoder.getFromLocation(centerOfMap.latitude, centerOfMap.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses == null || addresses.size() == 0) {
                    //Do something
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();

                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                    }
                    String addr = "";
                    for (int i = 0; i < addressFragments.size(); i++) {
                        addr = addr +" "+ addressFragments.get(i);
                    }

                    if(type.matches("A")){
                        searchAddress.setQuery(addr, false);
                        addressAry[0] = addr.trim();
                    }else if(type.matches("B")){
                        searchAddress.setQuery(addr, false);
                        addressAry[1] = addr.trim();

                    }
                    //Log.d("DEBUG", addr);

                    // Update your Marker's position to the center of the Map.
                    //mMap.clear();
                    //mMap.addMarker(new MarkerOptions().position(centerOfMap).title("Marker"));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_marker, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }
        if(id == R.id.normal){
            if (mMap != null){
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
        if(id == R.id.satellite){
            if (mMap != null){
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        }
        if(id == R.id.hybrid){
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        final LatLng CENTRE_VANCOUVER = new LatLng(49.29372070685224 ,-123.06420173496008);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CENTRE_VANCOUVER, 10);
        mMap.moveCamera(update);

    }

    public void setIsClicked(View view){
        if(type.matches("A")) {

            // Add start_marker_resized to pick up location
            LatLng centerOfMap = mMap.getCameraPosition().target;
            // create marker
            MarkerOptions marker = new MarkerOptions().position(centerOfMap).title("PICKUP");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.a_marker_resized));
            // adding marker
            markerAry[0] = mMap.addMarker(marker);
                //markerAry[0].showInfoWindow();

            // switch to set destination mode
            getSupportActionBar().setTitle("Set Destination");
            Button button = (Button)findViewById(R.id.set);
            button.setBackgroundColor(Color.parseColor("#E74C3C"));//LIGHT RED
            button.setText("SET DESTINATION");
            // Change marker color to red
            ImageView image = (ImageView) findViewById(R.id.marker);
            image.setImageResource(R.drawable.b_marker);

            //clear search view
            SearchView searchView = (SearchView) findViewById(R.id.searchAddress);
            searchView.setQuery("", false);

            // Change type to "B"
            type = "B";
        }else if(type.matches("B")){

            // Add end_marker_resized to destination
            LatLng centerOfMap = mMap.getCameraPosition().target;

            // Check distance
            final int ONE_KM = 1;
            if(distance(markerAry[0].getPosition().latitude,
                        markerAry[0].getPosition().longitude,
                        centerOfMap.latitude,
                        centerOfMap.longitude) < ONE_KM) {

                Toast toast = Toast.makeText(getApplicationContext(), "This ride is too short.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                toast.show();
                return;
            }

            // create marker
            MarkerOptions marker = new MarkerOptions().position(centerOfMap).title("DESTINATION");
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.b_marker_resized));
            // adding marker
            markerAry[1] = mMap.addMarker(marker);

            // Calculate Route
            isRouteFound = false;
            isRouteReady = false;
            LatLng pickup = new LatLng(markerAry[0].getPosition().latitude , markerAry[0].getPosition().longitude);
            LatLng dest = new LatLng(markerAry[1].getPosition().latitude , markerAry[1].getPosition().longitude);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(pickup, dest);

            // Start downloading json data from Google Directions API
            //drawing the route between A and B
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            //calculate time and distance
            DownloadTask3 downloadTask3 = new DownloadTask3();
            downloadTask3.execute(url);

            // Zoom out to see all markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerAry[0].getPosition());
            builder.include(markerAry[1].getPosition());
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            mMap.moveCamera(cu);
            float zoom = mMap.getCameraPosition().zoom;
            LatLng center = mMap.getCameraPosition().target;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom - 1.0f));


            // hide marker
            ImageView image = (ImageView) findViewById(R.id.marker);
            image.setVisibility(View.GONE);

            // Switch confirm mode
            getSupportActionBar().setTitle("Confirm");
            Button doneButton = (Button)findViewById(R.id.done);
            doneButton.setVisibility(View.VISIBLE);
            Button button = (Button)findViewById(R.id.set);
            button.setText("RESET");

            //clear search view
            SearchView searchView = (SearchView) findViewById(R.id.searchAddress);
            searchView.setIconifiedByDefault(true);
            searchView.clearFocus();
            searchView.setQuery("", false);
            searchView.setBackgroundColor(Color.TRANSPARENT);
            searchView.setQueryHint("");
            searchView.setIconified(true);

            // Change type to "C"
            type = "C";

        }else{
            mMap.clear();// clear map

            getSupportActionBar().setTitle("Set Pickup Location");

            ImageView image = (ImageView) findViewById(R.id.marker);
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.a_marker);

            Button doneButton = (Button)findViewById(R.id.done);
            doneButton.setVisibility(View.GONE);
            Button button = (Button)findViewById(R.id.set);
            button.setBackgroundColor(Color.parseColor("#5DAB00"));//LIGHT GREEN
            button.setText("SET PICKUP");

            //clear search view
            SearchView searchView = (SearchView) findViewById(R.id.searchAddress);
            searchView.setBackgroundColor(Color.parseColor("#AAAAAA"));
            searchView.setQueryHint("Enter the address here...");
            searchView.setIconified(false);
            searchView.clearFocus();

            // Change type back to "A"
            type = "A";
        }
    }

    // DONE BUTTON IS CLICKED
    public void doneIsClicked(View view){

        if(isRouteFound && isRouteReady) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("address_a", addressAry[0]);
            returnIntent.putExtra("address_b", addressAry[1]);
            returnIntent.putExtra("lat_a", markerAry[0].getPosition().latitude + "");
            returnIntent.putExtra("lng_a", markerAry[0].getPosition().longitude + "");
            returnIntent.putExtra("lat_b", markerAry[1].getPosition().latitude + "");
            returnIntent.putExtra("lng_b", markerAry[1].getPosition().longitude + "");
            returnIntent.putExtra("distance", distance);
            returnIntent.putExtra("time", time);
            setResult(RESULT_OK, returnIntent);
            finish();
        }else if(!isRouteReady){
            Toast toast = Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
            toast.show();
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid Ride Please Reset", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
            toast.show();
        }
    }

    // CUSTOMIZED MYLOCATION IS CLICKED
    public void myLocationIsClicked(View view){
        Location location = mMap.getMyLocation();

        if(!isGpsEnable()){
            showSettingsAlert();
            return;
        }

        if( location != null ) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(14.0f).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    // CHECK GPA ENABLE
    private boolean isGpsEnable(){
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            //getting GPS status
            Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //getting network status
            Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGPSEnabled || isNetworkEnabled){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    //  Show settings alert dialog
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //Setting Dialog Title
        alertDialog.setTitle("Location Services is Off");
        //Setting Dialog Message
        alertDialog.setMessage("Please turn on Location Services to track your location");
        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    // FORMULA TO CALCULATE STRAIGHT DISTANCE BETWEEN TWO LATLNGS
    private double distance(double lat1, double lon1, double lat2, double lon2) {

        if(lat1 == lat2 && lon1 == lon2){
            return 0;
        }

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return dist;
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
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
    private String downloadUrl(String strUrl) throws IOException{
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
    private class DownloadTask extends AsyncTask<String, Void, String>{

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
                isRouteFound = true;
                isRouteReady = true;
            }

            try {
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }catch (Exception e){
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), "No Route Found", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                toast.show();
                isRouteFound = false;
                isRouteReady = true;
            }
        }
    }
    //GOOGLE FIND ROUTE SERVICES END-----------------------------------------------------------------------------------------

    //GOOGLE ADDRESS SERVICES START HERE-------------------------------------------------------------------------------------
    /** A class, to download Places from Geocoding webservice */
    private class DownloadTask2 extends AsyncTask<String, Integer, String>{

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){

            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask2 parserTask = new ParserTask2();

            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /** A class to parse the Geocoding Places in non-ui thread */
    class ParserTask2 extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){


            for(int i=0;i<list.size();i++){

                // Creating a marker
                //MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                //String name = hmPlace.get("formatted_address");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                //markerOptions.position(latLng);

                // Setting the title for the marker
                //markerOptions.title(name);

                // Placing a marker on the touched position
                //mMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
            }
        }
    }
    //GOOGLE ADDRESS SERVICES END-------------------------------------------------------------------------------------

    //FIND DISTANCE AND TRAVEL TIME BETWEEN A AND B START HERE--------------------------------------------------------------------
    // Fetches data from url passed
    private class DownloadTask3 extends AsyncTask<String, Void, String>{

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
            String duration = "";

            if(result.size()<1){
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){


                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance1 = point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                }

            }
            distance = distance1;
            time = duration;
            getSupportActionBar().setTitle("Confirm " + distance1 + " - " + duration);
        }
    }
    //FIND DISTANCE AND TRAVEL TIME BETWEEN A AND B END--------------------------------------------------------------------
}
