package v_go.version10.FragmentClasses;

import android.app.AlertDialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
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

import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.Request;
import v_go.version10.R;
import v_go.version10.googleMapServices.DirectionsJSONParser1;
import v_go.version10.googleMapServices.GeocodeJSONParser;

public class TabC_3 extends Fragment   {

    private int req_id;
    private int type;
    private int decision;
    private GoogleMap mMap;
    private MarkerOptions marker_a;
    private MarkerOptions marker_b;
    private int readyMarker = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_c_3, container, false);

        //map
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        setRetainInstance(true);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // change toolbar title
        type = getArguments().getInt("type");
        if(type == 0 || type == 1){
            getActivity().setTitle("Pending Request");
        }else {
            getActivity().setTitle("Trip Confirmation");
        }
        ((Main) getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        req_id = getArguments().getInt("req_id");

        Log.d("DEBUG", "req_id: " +getArguments().getInt("req_id")+" trip_id: " + getArguments().getInt("trip_id") + " type: " + getArguments().getInt("type")
        + " addr_a: " + getArguments().getString("addr_a") + " addr_b " + getArguments().getString("addr_b") + " first name: "
        + getArguments().getString("name"));

        Button accept = (Button) view.findViewById(R.id.accept);
        Button deny = (Button) view.findViewById(R.id.deny);

        /** Hide accept/refuse button **/
        // if the request has already been accepted
        if(getArguments().getInt("type")==2 || getArguments().getInt("type")==3){
            accept.setVisibility(View.GONE);
            deny.setVisibility(View.GONE);
            // OR if you are the sender
        }else if(getArguments().getInt("rec_flag") == 1){
            accept.setVisibility(View.GONE);
            deny.setVisibility(View.GONE);
        }

        // on accept
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decision = 1;
                new DecisionAsyncTask().execute(req_id, 1);
            }
        });

        // on deny
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decision = 0;
                new DecisionAsyncTask().execute(req_id, 0);
            }
        });

        /** Initialize the map view **/
        TextView addr_a = (TextView)view.findViewById(R.id.addr_a);
        TextView addr_b = (TextView)view.findViewById(R.id.addr_b);
        TextView message = (TextView)view.findViewById(R.id.message);
        message.setSelected(true);

        message.setText(getArguments().getString("message"));

        addr_a.setText(getArguments().getString("start_location"));
        addr_b.setText(getArguments().getString("end_location"));

        setMarker(getArguments().getString("start_location"));
        setMarker(getArguments().getString("end_location"));

        return  view;
    }

    class DecisionAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            Request request = new Request();
            return Integer.parseInt((request.RequestResponse(params[0] + "", params[1] + "")).trim());
        }

        @Override
        protected void onPostExecute(Integer i) {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            switch (i){
                case 1:
                    alertDialog.setIcon(R.drawable.check);
                    alertDialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
                    if(decision == 1)
                        alertDialog.setTitle("Accepted!");
                    else
                        alertDialog.setTitle("Refused!");
                    alertDialog.setMessage("Thank you for your respond!");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case 0:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
                    alertDialog.setTitle("Ops! Error 0");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case -1:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
                    alertDialog.setTitle("Ops! Error -1");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case -3:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
                    alertDialog.setTitle("Uh-Oh!");
                    alertDialog.setMessage("You've already responded to this request.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case -7:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
                    alertDialog.setTitle("Ops! Error -7");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
            }
        }
    }

    private void setMarker(String location){
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
    }

    /** A class, to download Places from Geocoding webservice */
    private class DownloadTask2 extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
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
                MarkerOptions markerOptions = new MarkerOptions();
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
                markerOptions.position(latLng);
                // Setting the title for the marker
                //markerOptions.title(name);

                // Increment ready marker
                readyMarker++;
                // Initialize maker_a & maker_b
                if(readyMarker == 1) {
                    marker_a = markerOptions;
                    marker_a.icon(BitmapDescriptorFactory.fromResource(R.drawable.a_marker_resized));
                    marker_a.title("Pickup");
                }else if(readyMarker == 2) {
                    marker_b = markerOptions;
                    marker_b.icon(BitmapDescriptorFactory.fromResource(R.drawable.b_marker_resized));
                    marker_b.title("Destination");
                }
                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
                // Locate the first location
                if(readyMarker == 2){
                    // Zoom out to see all markers
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(marker_a.getPosition());
                    builder.include(marker_b.getPosition());
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    mMap.moveCamera(cu);
                    float zoom = mMap.getCameraPosition().zoom;
                    LatLng center = mMap.getCameraPosition().target;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom - 0.7f));

                    // Draw Route
                    String url = getDirectionsUrl(marker_a.getPosition(), marker_b.getPosition());
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }

            }
        }
    }

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