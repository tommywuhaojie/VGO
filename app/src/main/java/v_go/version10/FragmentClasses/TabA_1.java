package v_go.version10.FragmentClasses;

import java.net.MalformedURLException;
import org.json.JSONArray;
import org.json.JSONException;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

import v_go.version10.ActivityClasses.Main;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.MapStateListener;
import v_go.version10.HelperClasses.TouchableMapFragment;
import v_go.version10.R;
import v_go.version10.googleMapServices.GeocodeJSONParser;
import v_go.version10.googleMapServices.PlaceProvider;


public class TabA_1 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String type;
    private Marker[] markerAry;
    private String[] addressAry;
    private View view;
    private int firstTime; // first time of camera changing
    private boolean mapIsDragged = false;
    private boolean mapIsDragging = false;
    private boolean fromAutoComplete = false;
    private LatLng oldLatLng = new LatLng(0,0);

    // variables for places auto complete
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyB8RTulLwXTIBphsG9E8fDwWYxsl41xMVM";
    private AutoCompleteTextView autoCompView;

    // address update handler and thread
    Handler updateHandler = new Handler();
    Runnable updateRunnable;

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        fromAutoComplete = true;
        String location = (String) adapterView.getItemAtPosition(position);
        String addr = (String) adapterView.getItemAtPosition(position);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?";
        try {
            location = URLEncoder.encode(location, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String address = "address=" + location;
        String sensor = "sensor=false";
        url = url + address + "&" + sensor;
        DownloadTask2 downloadTask = new DownloadTask2();
        downloadTask.execute(url);
        // hide keyboard
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(autoCompView.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        autoCompView.clearFocus();

        // trim the address and put it back
        autoCompView.setText(addr.trim());
        // store address to local
        if(type.matches("A")){
            addressAry[0] = addr.trim();
        }else if(type.matches("B")){
            addressAry[1] = addr.trim();
        }
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:ca");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            //System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.d("DEBUG", "Error API URL");
            return resultList;
        } catch (IOException e) {
            Log.d("DEBUG", "Error Places API");
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            //System.out.println(jsonObj.getString("error_message"));
            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                //System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                //System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description")
                        + "                                                          "); // add empty space after location name
            }
        } catch (JSONException e) {
            Log.d("DEBUG", "Cannot process JSON results");
            e.printStackTrace();
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.tab_a_1, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // reset
        firstTime=0;

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

        // turn on option menu
        setHasOptionsMenu(true);

        // set up google map if needed
        setUpMapIfNeeded();

        // auto complete
        autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.auto_complete_list));
        autoCompView.setOnItemClickListener(this);
        // search button return
        autoCompView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String location = autoCompView.getText().toString();
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?";
                    try {
                        location = URLEncoder.encode(location, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String address = "address=" + location;
                    String sensor = "sensor=false";
                    url = url + address + "&" + sensor;
                    DownloadTask2 downloadTask = new DownloadTask2();
                    downloadTask.execute(url);
                    // keyboard
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(autoCompView.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    autoCompView.clearFocus();
                    return true;
                }
                return false;
            }
        });

        // focus change
        autoCompView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    autoCompView.setText("");
                } //else {...loss the focus...}
            }
        });


        // Initialization
        markerAry = new Marker[2];
        addressAry = new String[2];
        type = "";

        // Action bar title and set button color
        Button doneButton = (Button) view.findViewById(R.id.done);
        doneButton.setBackgroundColor(Color.parseColor("#7CB342"));//LIGHT GREEN
        doneButton.setVisibility(View.GONE);

        // initialize type
        type = "A";
        getActivity().setTitle("Set Pickup Location");
        ((Main) getActivity()).enableBackButton(false);
        Button button = (Button)view.findViewById(R.id.set);
        button.setBackgroundColor(Color.parseColor("#7CB342"));//LIGHT GREEN
        button.setText("SET PICKUP");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(type.matches("A")) {
                    // wait for address A to update
                    if(autoCompView.getText().toString().equals("Updating...")){
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                        toast.show();
                        return;
                    }
                    // change title
                    getActivity().setTitle("Set Destination");
                    ((Main) getActivity()).enableBackButton(true);

                    // Add start_marker_resized to pick up location
                    LatLng centerOfMap = mMap.getCameraPosition().target;
                    // create marker
                    MarkerOptions marker = new MarkerOptions().position(centerOfMap).title(addressAry[0]);
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.a_m));
                    // adding marker
                    markerAry[0] = mMap.addMarker(marker);
                    //markerAry[0].showInfoWindow();

                    Button button = (Button)view.findViewById(R.id.set);
                    button.setBackgroundColor(Color.parseColor("#FF7043"));//LIGHT RED
                    button.setText("SET DESTINATION");
                    // Change marker color to red
                    ImageView image = (ImageView) view.findViewById(R.id.marker);
                    image.setImageResource(R.drawable.b_m);

                    // Change type to "B"
                    type = "B";

                }else{
                    // Add end_marker_resized to destination
                    LatLng centerOfMap = mMap.getCameraPosition().target;
                    // Check distance
                    final int ONE_KM = 1;
                    if(distance(markerAry[0].getPosition().latitude,
                            markerAry[0].getPosition().longitude,
                            centerOfMap.latitude,
                            centerOfMap.longitude) < ONE_KM) {

                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Sorry, this ride is too short.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                        toast.show();
                        return;
                    }
                    // wait for address B to update
                    if(autoCompView.getText().toString().equals("Updating...")){
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 500);
                        toast.show();
                        return;
                    }

                    // change center marker to green
                    ImageView image = (ImageView) view.findViewById(R.id.marker);
                    image.setVisibility(View.GONE);

                    // create marker
                    MarkerOptions marker = new MarkerOptions().position(centerOfMap).title("DESTINATION");
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.b_m));
                    markerAry[1] = mMap.addMarker(marker);

                    LatLng pickup = new LatLng(markerAry[0].getPosition().latitude , markerAry[0].getPosition().longitude);
                    LatLng dest = new LatLng(markerAry[1].getPosition().latitude , markerAry[1].getPosition().longitude);

                    // Pass data arguments to TabA_2
                    Bundle args = new Bundle();
                    args.putDouble("lat_a", pickup.latitude);
                    args.putDouble("lng_a", pickup.longitude);
                    args.putDouble("lat_b", dest.latitude);
                    args.putDouble("lng_b", dest.longitude);
                    args.putString("address_a", addressAry[0]);
                    args.putString("address_b", addressAry[1]);

                    Fragment fragment = new TabA_2();
                    fragment.setArguments(args);
                    ((Main) getActivity()).pushFragments(Global.TAB_A, fragment, true, true);
                }
            }
        });

        // my location button
        ImageButton mImageButton = (ImageButton) view.findViewById(R.id.myLocation);
        mImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
        });

        // google map
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
                // do something when camera finishes moving
                if(fromAutoComplete){
                    fromAutoComplete = false;
                    return;
                }
                if(mapIsDragging){
                    return;
                }
                autoCompView.setText("Updating...");
                // stop updating
                if(updateRunnable != null) {
                    updateHandler.removeCallbacks(updateRunnable);
                }
                // update now
                updateRunnable = new Runnable() {
                    public void run() {
                        //what ever you do here will be done after 0.5 second delay.
                        updateAddress();
                    }
                };
                updateHandler.postDelayed(updateRunnable, 500);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoCompView.setHint("Enter address here...");
        autoCompView.clearFocus();
    }

    private void setUpMapIfNeeded() {

        TouchableMapFragment mMapFragment = (TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Try to obtain the map from the SupportMapFragment.
        mMap = mMapFragment.getMap();
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            setUpMap();
        }

        // Handle map touch events
        new MapStateListener(mMapFragment) {
            @Override
            public void onMapDragged(){
                // Map is dragged
                if(mapIsDragging)
                    return;

                mapIsDragged = true;
                mapIsDragging = true;
                autoCompView.setText("Updating...");
            }
            @Override
            public void onMapTouched() {
                // Map is touched
            }
            @Override
            public void onMapReleased() {
                // Map is released
                if(!mapIsDragged && oldLatLng.equals(mMap.getCameraPosition().target)) {
                    return;
                }

                mapIsDragged = false;
                mapIsDragging = false;
                oldLatLng = mMap.getCameraPosition().target;

                // update address after 500 ms
                updateRunnable = new Runnable() {
                    public void run() {
                        //what ever you do here will be done after 0.5 second delay.
                        updateAddress();
                    }
                };
                updateHandler.postDelayed(updateRunnable, 500);
            }
        };
    }

    private void updateAddress(){
        // Get the center of the Map.
        List<Address> addresses = null;
        LatLng centerOfMap = mMap.getCameraPosition().target;
        if(getActivity() == null) {
            return;
        }
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.ENGLISH);
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

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            String addr = "";
            for (int i = 0; i < addressFragments.size(); i++) {
                addr = addr +" "+ addressFragments.get(i);
            }
            autoCompView.setText(addr);
            autoCompView.clearFocus();

            if(firstTime==0){
                autoCompView.setText("");
                firstTime++;
            }

            if(type.matches("A")){
                addressAry[0] = addr.trim();
            }else if(type.matches("B")){
                addressAry[1] = addr.trim();
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
        final LatLng CENTRE_VANCOUVER = new LatLng(49.20741810800015, -123.00997015088797);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(CENTRE_VANCOUVER, 10);
        mMap.moveCamera(update);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            if(type.matches("B")) {
                getActivity().setTitle("Set Pickup Location");
                ((Main)getActivity()).enableBackButton(false);

                Button button = (Button)view.findViewById(R.id.set);
                button.setBackgroundColor(Color.parseColor("#7CB342"));//LIGHT GREEN
                button.setText("SET PICKUP");
                mMap.clear();
                ImageView image = (ImageView) view.findViewById(R.id.marker);
                image.setVisibility(View.VISIBLE);
                image.setImageResource(R.drawable.a_m);

                // Change type back to "A"
                type = "A";
            }
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

    // CHECK GPA ENABLE
    private boolean isGpsEnable(){
        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
            public void onClick(DialogInterface dialog, int which) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;
        if(arg0==0)
            cLoader = new CursorLoader(getActivity().getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
        else if(arg0==1)
            cLoader = new CursorLoader(getActivity().getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
        return cLoader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
            }
        }
    }
}
