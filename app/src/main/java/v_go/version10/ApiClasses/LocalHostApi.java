package v_go.version10.ApiClasses;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocalHostApi {

    public static JSONObject Register(String phone_number,String email,String password, String first_name, String last_name ){
        String json_text = null;
        String data="email="+email+"&password="+password+"&first_name="+first_name+"&last_name="+last_name+"&phone_number="+phone_number;
        HttpURLConnection connection = null;
        URL url;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(ServerConstants.LOCAL_REGISTER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

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
            json_text = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (connection!=null){
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

    public static JSONObject Login(String phone_number,String password){
        String json_text = null;
        String data="phone_number="+phone_number+"&password="+password;
        HttpURLConnection connection = null;
        URL url;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(ServerConstants.LOCAL_LOGIN_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
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

    public static JSONObject Logout(){
        String json_text = null;
        HttpURLConnection connection = null;
        URL url;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(ServerConstants.LOCAL_LOGOUT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
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