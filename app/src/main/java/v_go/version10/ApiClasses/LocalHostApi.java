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

public class LocalHostApi {

    private static final String URL = "http://127.0.0.1:8080/register";

    public String Register(String phone_number,String email,String password, String first_name, String last_name ){
        String text = null;

        String data="email="+email+"&password="+password+"&first_name="+first_name+"&last_name="+last_name+"&phone_number="+phone_number;

        HttpURLConnection urlconnect = null;
        URL url = null;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            url = new URL(URL);
            urlconnect = (HttpURLConnection) url.openConnection();
            urlconnect.setRequestMethod("POST");

            urlconnect.setUseCaches(false);
            urlconnect.setDoInput(true);
            urlconnect.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(urlconnect.getOutputStream());
            writer.writeBytes(data);
            writer.flush();
            writer.close();

            //Response
            InputStream is = urlconnect.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine())!=null) {
                response.append(line);
                response.append("\r");
            }
            text = response.toString();
            System.out.println(text);
        } catch (MalformedURLException e) {
            //Log.i("DEBUG", "error0");
            e.printStackTrace();
        } catch (IOException e) {
            //Log.i("DEBUG","error1");
            e.printStackTrace();
        }finally {
            if (urlconnect!=null){
                urlconnect.disconnect();

            }
        }
        return text;
    }
}
