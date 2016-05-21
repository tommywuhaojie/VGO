package v_go.version10.ApiClasses;

import org.json.JSONException;
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

    private static final String REG_URL = "http://127.0.0.1:8080/register";
    private static final String LOGIN_URL = "http://127.0.0.1:8080/login";
    private static final String LOGOUT_URL = "http://127.0.0.1:8080/logout";

    public JSONObject Register(String phone_number,String email,String password, String first_name, String last_name ){
        String json_text = null;

        String data="email="+email+"&password="+password+"&first_name="+first_name+"&last_name="+last_name+"&phone_number="+phone_number;

        HttpURLConnection connection = null;
        URL url = null;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(REG_URL);
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
            System.out.println(json_text);
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

    /*
    * return value:
    * -1    : fail to login
    *  1    : sucessfully login as passanger
    * */
    public JSONObject Login(String phone_number,String password){
        String json_text = null;
        //String url_string = this.Rootpath()+"/login/logIn.php";
        String data="phone_number="+phone_number+"&password="+password;
        HttpURLConnection connection = null;
        URL url;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(LOGIN_URL);
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

    /*
    * return value:
    * -1    : fail to logout
    *  1    : sucessfully logged out
    * */
    public JSONObject Logout(){
        String json_text = null;
        HttpURLConnection connection = null;
        URL url;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(LOGOUT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            //writer.writeBytes(data);
            //writer.flush();
            //writer.close();
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
