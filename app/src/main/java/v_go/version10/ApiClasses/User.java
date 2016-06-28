package v_go.version10.ApiClasses;


import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class User {

    public static JSONObject Register(String phone_number,String email,String password, String first_name, String last_name ){
        String json_text = null;
        String data="email="+email+"&password="+password+"&first_name="+first_name+"&last_name="+last_name+"&phone_number="+phone_number;
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(ServerConstants.REGISTER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10 * 1000);

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

    /*
    * return value:
    * -1    : fail to login
    *  1    : successfully login
    * */
    public static JSONObject Login(String phone_number,String password){
        String json_text = null;
        String data="phone_number="+phone_number+"&password="+password;
        HttpURLConnection connection = null;
        URL url;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(ServerConstants.LOGIN_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10 * 1000);
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
    *  1    : successfully logged out
    * */
    public static JSONObject Logout(){
        String json_text = null;
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(ServerConstants.LOGOUT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setConnectTimeout(10 * 1000);
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

    public static String UploadAvatar(Bitmap bitmap) {
        String attachmentName = "avatar";
        String attachmentFileName = ".jpg";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        HttpURLConnection httpUrlConnection = null;
        String response = null;
        URL url = null;
        try {
            url = new URL("http://192.168.1.73:8080/profile");
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            byte[] pixels = bos.toByteArray();
            request.write(pixels);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();
            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            httpUrlConnection.disconnect();
            response = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return response;
    }
}
