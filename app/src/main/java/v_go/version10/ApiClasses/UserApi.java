package v_go.version10.ApiClasses;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextUtils;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        String data="phone_number="+phone_number;
        return REST.PerformPostRequestForJSONObject(Urls.SEND_CODE_URL, data);
    }

    public static JSONObject VerifyCode(String phone_number, String code){

        String data="phone_number="+phone_number+"&code="+code;
        return REST.PerformPostRequestForJSONObject(Urls.VERIFY_CODE_URL, data);
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
        String data;
        if(isDriver == 0){
            data ="objectid="+object_id+"&email="+email+"&password="+password+"&first_name="+first_name+"&last_name="
                    +last_name+"&sex="+sex;
        }else{
            data ="objectid="+object_id+"&email="+email+"&password="+password+"&first_name="+first_name+"&last_name="
                    +last_name+"&sex="+sex+"&driver_license="+driver_license+"&plate_number="+plate_number+"&car_model="+car_model
                    +"&colour="+colour;
        }
        return REST.PerformPostRequestForJSONObject(Urls.REGISTER_URL, data);
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
        String data = type + "=" + identification;
        return REST.PerformPostRequestForJSONObject(Urls.GET_USER_INFO_URL, data);
    }

    /*
    * return value:
    * -1    : fail to login
    *  1    : successfully login
    * */
    public static JSONObject Login(String phone_number,String password, Context context){
        String data="phone_number="+phone_number+"&password="+password;
        SiCookieStore2 siCookieStore = new SiCookieStore2(context);
        CookieManager cookieManager = new CookieManager(siCookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        return REST.PerformPostRequestForJSONObject(Urls.LOGIN_URL, data);
    }

    /*
    * return value:
    *  1    : successfully logged out
    * */
    public static JSONObject Logout(){
       return REST.PerformDeleteRequest(Urls.LOGOUT_URL);
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
            URL url = new URL(Urls.UPLOAD_AVATAR_URL);
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
            URL url = new URL(Urls.DOWNLOAD_AVATAR_URL);
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
            URL url = new URL(Urls.DOWNLOAD_AVATAR_URL);
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

    public static String saveAvatarToStorage(Bitmap bitmapImage, String user_id, Context applicationContext){
        ContextWrapper cw = new ContextWrapper(applicationContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "avatar_" + user_id + ".jpg");
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                fos.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return mypath.getAbsolutePath();
    }

    public static Bitmap loadAvatarFromStorage(String user_id, Context applicationContext)
    {
        ContextWrapper cw = new ContextWrapper(applicationContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File f = new File(directory, "avatar_" + user_id + ".jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static String getMyUserId(Context appContext){
        SharedPreferences settings = appContext.getSharedPreferences("cache", 0);
        return settings.getString("user_id", "");
    }
    public static Bitmap loadMyAvatar(Context appContext){
        return loadAvatarFromStorage(getMyUserId(appContext), appContext);
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float r = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
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
