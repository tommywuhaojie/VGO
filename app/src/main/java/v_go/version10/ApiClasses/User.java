package v_go.version10.ApiClasses;

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

public class User {
    /*
    * v-go url addrress
    * */
    public String Rootpath(){return "http://www.v-go.ca";}
    /*
    * return value:
    * -1    : fail to register;
    * -2    :invalid inputs/ did not fill in all fields;
    * -3    :email already exist;
    * 0     :account not activited(not implement yet)
    * 1     :successfully register;
    * */
    public String Register(String email,String password,String phone,String lastname,String firstname){
        String text = null;
        String url_string = this.Rootpath()+"/create/register.php";
        String data="email="+email+"&password="+password+"&first_name="+firstname+"&last_name="+lastname+"&phone_number="+phone;
        HttpURLConnection urlconnet = null;
        URL url = null;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
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
            Log.i("DEBUG","error1");
            e.printStackTrace();
        }finally {
            if (urlconnet!=null){
                urlconnet.disconnect();

            }
        }
        return text;
    }
    /*
    * return value:
    * -1    : fail to login
    *1      : sucessfully login as passanger
    * */
    public String Login(String email,String password){
        String text = null;
        String url_string = this.Rootpath()+"/login/logIn.php";
        String data="email="+email+"&password="+password;
        HttpURLConnection urlconnet = null;
        URL url = null;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
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
            Log.i("DEBUG","error0");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("DEBUG","error1");
            e.printStackTrace();
        } finally {
            if (urlconnet!=null){
                urlconnet.disconnect();

            }
        }
        return text;
    }
}
