package v_go.version10.ApiClasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Trip implements TripInterface {
    private String trip_id;
    private String user_id;
    private String starting_date;
    private String starting_time;
    private double start_lat;
    private double start_lng;
    private String starting;
    private double end_lat;
    private double end_lng;
    private String destination;
    private String est_time;
    private String est_distance;
    private String driver_flag;
    private boolean active;
    private boolean match_flag;
    private String update;

    // default constructor
    public Trip(){}

    public Trip(String trip_id) {
        HttpURLConnection urlconnet = null;
        JSONArray json = null;


        String jsonarray_string = null;
        Boolean flag = true;
        try {
            String url_string = URLs.TARGET_ROOT_URL + "/trip/tripInfo.php?trip_id=";
            URL url = new URL(url_string);
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet.setRequestMethod("GET");
            urlconnet.setUseCaches(true);
            InputStream is = urlconnet.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            jsonarray_string = response.toString();
            Log.i("michaellog t:", jsonarray_string);
        } catch (MalformedURLException e) {
            flag = false;
            e.printStackTrace();

            Log.i("michaellog", "error0");
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
            Log.i("michaellog", "error1");
        } finally {
            if (urlconnet != null) {
                urlconnet.disconnect();
            }
        }
        if (jsonarray_string == null) {
            flag = false;
            Log.i("michaellog", "error2");
        } else {
            try {
                json = new JSONArray(jsonarray_string);
            } catch (JSONException e) {
                flag = false;
                Log.i("michaellog", "error3");
                e.printStackTrace();
            }
        }
        if (flag) {
            try {
                JSONObject one = json.getJSONObject(0);
                this.trip_id = one.getString("trip_id");
                this.user_id = one.getString("user_id");
                this.starting_date = one.getString("starting_date");
                this.starting_time = one.getString("starting_time");
                this.start_lat = Double.parseDouble(one.getString("start_lat"));
                this.start_lng = Double.parseDouble(one.getString("start_lng"));
                this.starting = one.getString("starting");
                this.end_lat = Double.parseDouble(one.getString("end_lat"));
                this.end_lng = Double.parseDouble(one.getString("end_lng"));
                this.destination = one.getString("destination");
                this.est_distance = one.getString("est_distance");
                this.driver_flag = one.getString("driver_flag");
                if (one.getString("active").equals("1")){
                    this.active = true;
                }else {
                    this.active = false;
                }
                if (one.getString("match_flag").equals("1")){
                    this.match_flag = true;
                }else {
                    this.match_flag = false;
                }
                this.update = one.getString("update");
            } catch (JSONException e) {
                this.trip_id = "-1";
                Log.i("michaellog", "error4");
                e.printStackTrace();
            }
        } else {

        }
    }
    @Override
    public String getTrip_id(){return this.trip_id;}
    @Override
    public String getUser_id(){return this.user_id;};
    @Override
    public String getStarting_date(){return this.starting_date;}
    @Override
    public String getStarting_time(){return this.starting_time;}
    @Override
    public String getStarting(){return this.starting;}
    @Override
    public String getDestination(){return this.destination;}
    @Override
    public String getEst_time(){return  this.est_time;}
    @Override
    public String getEst_distance() {return  this.est_distance;}
    @Override
    public String getDriver_flag(){return  this.driver_flag;}
    @Override
    public String getUpdate(){return this.update;}
    @Override
    public double getStart_lat(){return this.start_lat;}
    @Override
    public double getStart_lng() {return this.start_lng;}
    @Override
    public double getEnd_lat(){return  this.end_lat;}
    @Override
    public double getEnd_lng() {return this.end_lng;}
    @Override
    public String RegisterTrip(String time, double start_latitude, double start_longitude, String start_location,
                               double end_latitude, double end_longitude, String end_location, int estimate_time,
                               double estimate_distance, int type){
        String text = null;

        String url_string = URLs.TARGET_ROOT_URL+"/create/tripRegister.php";

        String data="date_id="+time+"&start_lat="+start_latitude+"&start_lng="+start_longitude
                +"&start_location="+start_location+"&end_lat="+end_latitude+"&end_lng="+end_longitude
                +"&end_location="+end_location+"&est_time="+estimate_time+"&est_distance="
                +estimate_distance+"&reg_as="+type;
        HttpURLConnection urlconnet = null;
        URL url = null;
        try {
            url = new URL(url_string);
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet.setRequestMethod("POST");
            //urlconnet.setRequestProperty("Content-Type", "application/x-www.urlencoded");
            //urlconnet.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
            //urlconnet.setRequestProperty("Content-Language", "en-US");
            urlconnet.setUseCaches(false);
            urlconnet.setDoInput(true);
            urlconnet.setDoOutput(true);
            // Log.i("michalelog input:",data);
            DataOutputStream writer = new DataOutputStream(urlconnet.getOutputStream());
            writer.writeBytes(data);
            writer.flush();
            writer.close();
            //Response
            InputStream is = urlconnet.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\r");
            }
            text = response.toString().trim();
        } catch (MalformedURLException e) {
            Log.i("DEBUG", "error0");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("DEBUG", "error1");
            e.printStackTrace();
        }finally {
            if (urlconnet!=null){
                urlconnet.disconnect();

            }
        }
        Log.i("michaellog b:",text);
        return text;
    }
    /*
    * Description: return a speicfy month's trips;
    * Input:
    *       yearmonth:          Fortmat:YYYY-MM
    * output
    *       Json array          Format:
    *           ['trip_id']			trip id
                ['starting_date']	starting date of the trip; 	format YYYY-MM-DD;
                ['starting_time']	starting time of the trip;	format HH:mm;
                ['starting']		starting location of the trip (english)
                ['destination']		destination of the trip (english)
                ['est_time']		estimate travel time;
                ['est_distance']	estimate distance;
                ['driver_flag']		if the session user is a driver or a passanger in this trip; 1: driver; 0: passanger;
                ['match_flag']		if this trip is already matched with other user. 1: yes; 0: no;(not implement yet)
    * */
    @Override
    public JSONArray ThisMonthTrip(String yearmonth){
        HttpURLConnection urlconnet = null;
        JSONArray json = null;
        String jsonarray_string = null;

        String url_string = URLs.TARGET_ROOT_URL + "/search/byMonth.php";

        String data="yearMonth="+yearmonth;
        URL url = null;
        try {
            url = new URL(url_string);
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet.setRequestMethod("POST");
            //urlconnet.setRequestProperty("Content-Type", "application/x-www.urlencoded");
            //urlconnet.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
            //urlconnet.setRequestProperty("Content-Language", "en-US");
            urlconnet.setUseCaches(false);
            urlconnet.setDoInput(true);
            urlconnet.setDoOutput(true);
            // Log.i("michalelog input:",data);
            DataOutputStream writer = new DataOutputStream(urlconnet.getOutputStream());
            writer.writeBytes(data);
            writer.flush();
            writer.close();
            //Response
            InputStream is = urlconnet.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\r");
            }
            jsonarray_string = response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("michaellog", "thimonth error0");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("michaellog", "thimonth error1");
        }finally {
            if (urlconnet!=null){
                urlconnet.disconnect();
            }

        }
        //test return output
        //Log.i("michaellog",jsonarray_string);
        if (jsonarray_string==null){
            Log.i("michaellog","thimonth error2");
            return null;
        }else {
            try {
                json = new JSONArray(jsonarray_string);
                return json;
            } catch (JSONException e) {
                Log.i("michaellog","thimonth error3");
                e.printStackTrace();
                return json;
            }
        }
    }
    /*
    * 'start_lat'			(double)starting latitude
		'start_lng'			(double)starting longtitude
		'end_lat'			(double)ending latitude
		'end_lng'			(double)ending longtitude
		'time'				(string)format: YYYY-MM-DD HH:mm  starting time
		'register_as'		(int)
							0 -> passanger
							1 -> driver
		'multi_allow'		(int)allow multiple passangers;
							0 -> no;
							1 -> yes;
	Output:
	    Json array:
		'trip_id'
		'start_location'	(string)
		'end_location'		(string)
		'time'				(string) format: YYYY-MM-DD HH:mm
		'start_dif'			(double) starting location distance difference unit km
		'end_dif'			(double) ending location  distance difference unit km
		'register_as' 		(int)
							0 -> passanger
							1 -> driver
	Error:
	    ouput null pointer
    * */
    @Override
    public JSONArray MatchTripsBy(double start_lat, double start_lng, double end_lat, double end_lng, String start_time, int reg_as, int mult_allow) {
        HttpURLConnection urlconnet = null;
        JSONArray json = null;
        String jsonarray_string = null;

        String url_string =  URLs.TARGET_ROOT_URL + "/match/matchTrip.php";

        String data = "start_lat="+start_lat+"&start_lng="+start_lng+"&end_lat="+end_lat+"&end_lng="+end_lng+"&time="+start_time+"&reg_as="+reg_as+"&multi_allow="+mult_allow;
        try {
            url_string+="?"+"start_lat="+start_lat+"&start_lng="+start_lng+"&end_lat="+end_lat+"&end_lng="+end_lng+"&time="+ URLEncoder.encode(start_time, "utf-8")+"&reg_as="+reg_as+"&multi_allow="+mult_allow;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            url_string+="?"+data.replaceAll(" ", "%20");
        }
        Log.i("michaellog",url_string);
        URL url = null;
        try {
            url = new URL(url_string);
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet.setRequestMethod("GET");
            InputStream is = urlconnet.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\r");
            }
            jsonarray_string = response.toString();
            // Log.i("michaellog",jsonarray_string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("michaellog", " error0");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("michaellog", " error1");
        } finally {
            if (urlconnet != null) {
                urlconnet.disconnect();
            }

        }
        //test return output
        //Log.i("michaellog",jsonarray_string);
        if (jsonarray_string == null) {
            Log.i("michaellog", " error2");
            return null;
        } else {
            try {
                json = new JSONArray(jsonarray_string);
                return json;
            } catch (JSONException e) {
                Log.i("michaellog", " error3");
                e.printStackTrace();
                return json;
            }
        }
    }
}
