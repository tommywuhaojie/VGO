package v_go.version10.ApiClasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Request implements RequestInterface {
    @Override
    public String TripRequest(String trip_id, String reg_as){
        String url_string = new User().Rootpath()+"/request/tripRequest.php";
        String data="trip_id="+trip_id+"&reg_as="+reg_as;
        HttpURLConnection urlconnet = null;
        URL url = null;
        String text = null;
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
            Log.i("michaellog","error0");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("michaellog","error1");
            e.printStackTrace();
        }finally {
            if (urlconnet!=null){
                urlconnet.disconnect();

            }
        }
        if (text==null){
            return null;
        }else {
            return text;
        }
    }

    @Override
    public String RequestResponse(String request_id, String state) {
        HttpURLConnection urlconnet = null;
        String jsonarray_string = null;
        try {
            String data = "request_id="+request_id+"&state="+state;
            String url_string = new User().Rootpath() + "/request/responseRequest.php?" + data;
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("michaellog", "error0");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("michaellog", "error1");
        } finally {
            if (urlconnet != null) {
                urlconnet.disconnect();
            }
        }
        return jsonarray_string;
    }
    @Override
    public JSONArray Notification(String last_id){
        HttpURLConnection urlconnet = null;
        JSONArray json = null;
        String jsonarray_string = null;
        String url_string = new User().Rootpath()+"/request/Notification.php"+"?request_id="+last_id;

        try {
            URL url = new URL(url_string);
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet.setRequestMethod("GET");
            urlconnet.setUseCaches(true);
            InputStream is = urlconnet.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\n");
            }
            jsonarray_string = response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("michaellog", "notification error0");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("michaellog", "notification error1");
        }finally {
            if (urlconnet!=null){
                urlconnet.disconnect();
            }

        }
        //test return output
        Log.i("michaellog","!!!" + jsonarray_string);
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

    @Override
    public JSONArray RequestList(String count) {
        HttpURLConnection urlconnet = null;
        JSONArray json = null;
        String jsonarray_string = null;
        String url_string = new User().Rootpath()+"/request/listRequest.php"+"?count="+count;

        try {
            URL url = new URL(url_string);
            urlconnet = (HttpURLConnection) url.openConnection();
            urlconnet.setRequestMethod("GET");
            urlconnet.setUseCaches(true);
            InputStream is = urlconnet.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\n");
            }
            jsonarray_string = response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("michaellog", "request list error0");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("michaellog", "request list error1");
        }finally {
            if (urlconnet!=null){
                urlconnet.disconnect();
            }

        }
        //test return output
        Log.i("michaellog",jsonarray_string);
        if (jsonarray_string==null){
            Log.i("michaellog","request list error2");
            return null;
        }else {
            try {
                json = new JSONArray(jsonarray_string);
                return json;
            } catch (JSONException e) {
                Log.i("michaellog","request list error3");
                e.printStackTrace();
                return json;
            }
        }
    }
}
