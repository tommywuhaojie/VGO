package v_go.version10.ApiClasses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class REST {

    private final static int TIME_OUT_IN_SECOND = 10;

    public static JSONObject PerformPostRequestForJSONObject(final String URL, final String data) {
        HttpURLConnection connection = null;
        String json_text = null;
        try {
            java.net.URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(data);
            writer.flush();
            writer.close();
            //Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\r");
            }
            json_text = response.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray PerformPostRequestForJSONArray(final String URL, final String data){

        HttpURLConnection connection = null;
        JSONArray jsonArray = null;
        String jsonarray_string = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);
            if(data != null) {
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes(data);
                writer.flush();
                writer.close();
            }
            //Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\r");
            }
            jsonarray_string = response.toString();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (connection!=null){
                connection.disconnect();
            }
        }
        if (jsonarray_string == null){
            return null;
        }else {
            try {
                jsonArray = new JSONArray(jsonarray_string);
                return jsonArray;
            } catch (JSONException e) {
                e.printStackTrace();
                return jsonArray;
            }
        }
    }

    public static JSONObject PerformDeleteRequest(final String URL){
        String json_text = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(Urls.LOGOUT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\r");
            }
            json_text = response.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
