package v_go.version10.Classes;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import v_go.version10.User;

/**
 * Created by Michael on 09/10/2015.
 */
/*
    time format: YYYY-MM-DD HH:mm
    estimate time in minutes;
    estimate distance in km;
    type:       (string) register as driver or passenger
                1->driver
                2->passenger;
    return:
        -1: fail to register a new trip;
        -2: a trip with a similar time already exist
        -3: fill in all fields
        -4: need to login first
        1:  register successful
*/
public class Trip {

    public String RegisterTrip (String time, double start_latitude, double start_longitude, String start_location,
                                double end_latitude, double end_longitude, String end_location, int estimate_time,
                                double estimate_distance,int type){
        String text = null;
        String url_string = new User().Rootpath()+"/create/tripRegister.php";
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
    public JSONArray ThisMonthTrip (String yearmonth){
        HttpURLConnection urlconnet = null;
        JSONArray json = null;
        String jsonarray_string = null;
        String url_string = "http://www.v-go.ca"+"/search/byMonth.php";
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
}
