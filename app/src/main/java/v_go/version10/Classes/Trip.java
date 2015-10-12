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
/**
 * Created by wu on 09/10/2015.
 */
/*
    time format: YYYY-MM-DD
    estimate time in mintues;
    restimate distance in km;
    type:       (string) register as driver or passanger
                1->driver
                2->passanger;
    return:
        -1: fail to register a new trip;
        -2: a trip with a similar time already exist
        -3: fill in all fields
        -4: need to login first
        1:  register successful
*/
public class Trip {
    public String RegisterTrip (String time,double start_latitude, double start_longtitude, String start_location, double end_latitude, double end_longtitude, String end_location, int estimate_time, double estimate_distance,String type){
        String text = null;
        String url_string = new User().Rootpath()+"/create/tripRegister.php";
        String data="date_id="+time+"&start_lat="+start_latitude+"&start_lng="+start_longtitude+"&start_location="+start_location+"&end_lat="+end_latitude+"&end_lng="+end_longtitude+"&end_location="+end_location+"&est_time="+estimate_time+"&est_distance="+estimate_distance+"&reg_as="+type;
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
}
