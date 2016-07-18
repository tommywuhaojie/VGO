package v_go.version10.ApiClasses;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

import v_go.version10.PersistentCookieStore.SiCookieStore2;

public class UserApi {

    final static int TIME_OUT_IN_SECOND = 10;

    public static JSONObject SendVerificationCode(String phone_number){

        String json_text = null;
        String data="phone_number="+phone_number;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(URLs.SEND_CODE_URL);
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

    public static JSONObject VerifyCode(String phone_number, String code){

        String json_text = null;
        String data="phone_number="+phone_number+"&code="+code;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(URLs.VERIFY_CODE_URL);
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
    /* INPUT:
    *
    * 'objectid'
    * 'email'
    * 'password'
    * 'first_name'
    * 'last_name'
    * 'sex'          (1 for male 0 for female)
    * 'driver_license'
    * 'plate_number'
    * 'colour'
    * 'car_model' format example(2016-HONDA-SEDAN)
    *
    * OUTPUT: JSON Object that contains
    *  'code' : respond code
    *  'msg' : respond message
    *
    *  1 -> Successfully registered
    * -2 -> Invalid objectid
    * -3 -> User have not yet vertify
    * -4 -> Password is too weak
    * -5 -> Invalid email format
    * -6 -> Invalid sex field (only 0/1 allow)
    * -300 -> Invalid first name format
    * -301 -> Invalid last name format
 */


    public static JSONObject Register(String object_id,String email,String password, String first_name, String last_name,
                                      int sex, String driver_license, String plate_number, String car_model, String colour, int isDriver){

        String json_text = null;

        String data;
        if(isDriver == 0){
            data ="objectid="+object_id+"&email="+email+"&password="+password+"&first_name="+first_name+"&last_name="
                    +last_name+"&sex="+sex;
        }else{
            data ="objectid="+object_id+"&email="+email+"&password="+password+"&first_name="+first_name+"&last_name="
                    +last_name+"&sex="+sex+"&driver_license="+driver_license+"&plate_number="+plate_number+"&car_model="+car_model
                    +"&colour="+colour;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(URLs.REGISTER_URL);
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
            json_text = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    /* PATH: host_url:8080/account/getUserInfo
     *
     * INPUT:
     * 'phone_number' or 'user_id' (only one input will be accepted at a time)
     *
     * OUTPUT: JSON Object that contains
     *  'code' : respond code
     *  'msg' : respond message
     *
     *   1 -> Get user information successfully
     *  -1 -> phone_number or user_id does not exist
     *  -2 -> Either phone_number or user_id is not specified or undefined
     *  -3 -> You can only specify one user identifications, either phone_number or user_id(object_id)
     *
     * ** Upon successful request, you will also be return with:
     *
     * 'user_id': String
     * 'phone_number': String
     * 'email': String
     * 'first_name': String
     * 'last_name': String
     * 'sex': String 0 -> female, 1 -> male
     * 'driver_flag': Boolean true -> driver, false -> not-a-driver
     *
     * ** The following info will be returned if driver_flag is true,
     * ** otherwise empty strings will be returned
     *
     * 'driver_license': String
     * 'plate_number': String
     * 'colour': String
     * 'car_model': String (eg. "2017-Ferrari-Coupe")
     *
     */
    // type: "phone_number" or "user_id"
    public static JSONObject GetUserInfo(String identification, String type){

        String json_text = null;
        String data =  type + "=" + identification;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URLs.GET_USER_INFO_URL);
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

    /*
    * return value:
    * -1    : fail to login
    *  1    : successfully login
    * */
    public static JSONObject Login(String phone_number,String password, Context context){

        String json_text = null;
        String data="phone_number="+phone_number+"&password="+password;
        HttpURLConnection connection = null;

        SiCookieStore2 siCookieStore = new SiCookieStore2(context);
        CookieManager cookieManager = new CookieManager(siCookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);


        try {
            URL url = new URL(URLs.LOGIN_URL);
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

    /*
    * return value:
    *  1    : successfully logged out
    * */
    public static JSONObject Logout(){

        String json_text = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URLs.LOGOUT_URL);
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

    public static JSONObject UploadAvatar(Bitmap bitmap) {

        String attachmentName = "avatar";
        String attachmentFileName = ".jpg";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        HttpURLConnection httpUrlConnection;
        String response;
        String json_text = null;
        try {
            URL url = new URL(URLs.UPLOAD_AVATAR_URL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            httpUrlConnection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);

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
            json_text = response.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Bitmap DownloadAvatar(String user_id){

        InputStream inputStream;

        try {
            String data = "user_id=" + user_id;
            URL url = new URL(URLs.DOWNLOAD_AVATAR_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

            inputStream = connection.getInputStream();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return BitmapFactory.decodeStream(inputStream);
    }

    public static Bitmap DownloadAvatar(){

        InputStream inputStream;

        try {
            URL url = new URL(URLs.DOWNLOAD_AVATAR_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setReadTimeout(TIME_OUT_IN_SECOND * 1000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            inputStream = connection.getInputStream();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return BitmapFactory.decodeStream(inputStream);
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPassword(String pwd){
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
        return pwd.matches(pattern);
    }
}
