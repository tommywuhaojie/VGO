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

public class ChatApi {

    private static final int TIME_OUT_IN_SECOND = UserApi.TIME_OUT_IN_SECOND;

    public static JSONObject AddNewContact(String other_user_id){

        String json_text = null;
        String data="other_user_id="+other_user_id;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(URLs.ADD_CONTACT_URL_);
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

    public static JSONArray GetContactList(){

        HttpURLConnection connection = null;
        JSONArray jsonArray = null;
        String jsonarray_string = null;

        String url_string = URLs.GET_CONTACT_LIST_URL;

        try {
            URL url = new URL(url_string);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);

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

    public static JSONArray GetChatHistory(String other_user_id, int number_of_message){

        HttpURLConnection connection = null;
        JSONArray jsonArray = null;
        String jsonarray_string = null;

        String url_string = URLs.GET_CHAT_HISTORY_URL;

        String data = "other_user_id="+other_user_id+"&number_of_message="+number_of_message;

        try {
            URL url = new URL(url_string);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(data);
            writer.flush();
            writer.close();
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
}
