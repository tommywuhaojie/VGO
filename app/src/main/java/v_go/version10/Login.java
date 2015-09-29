package v_go.version10;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import v_go.version10.User;

public class Login extends AppCompatActivity {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Facebook SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        LoginButton mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("user_friends");
        mLoginButton.setReadPermissions("public_profile");
        mLoginButton.setReadPermissions("email");

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                    System.out.println("ERROR");
                                } else {
                                    System.out.println("Success");
                                    try {

                                        String jsonresult = String.valueOf(json);
                                        System.out.println("JSON Result"+jsonresult);

                                        //String str_email = json.getString("email");
                                        String str_id = json.getString("id");
                                        String str_name = json.getString("name");

                                        Intent intent = new Intent(Login.this, MainMenu.class);
                                        intent.putExtra("isFacebookLogin", true);
                                        intent.putExtra("id", str_id);
                                        intent.putExtra("name", str_name);
                                        startActivity(intent);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });


        Intent intent = getIntent();
        Boolean isLogout = intent.getBooleanExtra("logout", false);

        // Get login information from the SharedPreferences
        SharedPreferences settings = getApplicationContext().getSharedPreferences("loginInfo", 0);
        String email = settings.getString("email", "");
        String pwd = settings.getString("password", "");
        Boolean rememberPwd = settings.getBoolean("rememberPwd", false);

        if(!isLogout) {
            EditText emailField = (EditText) findViewById(R.id.email);
            emailField.setText(email);
        }
        if(rememberPwd) {
            if(!isLogout) {
                EditText pwdField = (EditText) findViewById(R.id.pwd);
                pwdField.setText(pwd);
            }
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
            checkBox.setChecked(true);
        }

        Button button = (Button)findViewById(R.id.loginButton);
        final String[] content = new String[1];

        //AsyncTask-------------------------------------------------------------
        class LoginAsyncTask extends AsyncTask<Void, Void, String>
        {
            private ProgressDialog pDialog;
            private String email, pwd;
            private int result;
            private final int SUCCESS = 1;
            private final int FAIL = -1;

            public LoginAsyncTask(String email, String pwd) {
                super();
                this.email = email;
                this.pwd = pwd;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("Logging in...");
                pDialog.show();
            }
            @Override
            protected String doInBackground(Void... params)
            {
                try {
                    User user = new User();
                    result = Integer.parseInt(user.Login(email, pwd).trim());
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute (String res){

                pDialog.dismiss();

                if(result == FAIL) {
                    //Dialog----------------------------------------------------------
                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setTitle("LOGIN FAILED");
                    alertDialog.setMessage("Incorrect email or password");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // here you can add functions
                        }
                    });
                    alertDialog.show();

                }else if(result == SUCCESS){
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    startActivity(intent);
                }
            }
        }

        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {


                        EditText emailET = (EditText) findViewById(R.id.email);
                        EditText pwdET = (EditText) findViewById(R.id.pwd);

                        String email = emailET.getText().toString();
                        String pwd = pwdET.getText().toString();

                        boolean ok = true;

                        //CHECK INTERNET CONNECTION
                        if (!isNetworkAvailable()) {
                            AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                            alertDialog.setTitle("NO INTERNET CONNECTION");
                            alertDialog.setMessage("Please check your network connection and try again.");
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // here you can add functions
                                }
                            });
                            alertDialog.show();
                            ok = false;
                        }

                        if(!isValidEmail(email)){
                            emailET.setError("Invalid email address");
                            ok = false;
                        }

                        if (pwd.trim().length() == 0) {
                            pwdET.setError("Password cannot be empty");
                            ok = false;
                        }

                        if (ok) {
                            //Remember user's email
                            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("loginInfo", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("email", email.trim());
                            //Remember user's password
                            if(checkBox.isChecked()) {
                                editor.putString("password", pwd);
                                editor.putBoolean("rememberPwd", true);
                            }else{
                                editor.putString("password", "");
                                editor.putBoolean("rememberPwd", false);
                            }
                            // Apply the edits!
                            editor.apply();
                            // run Login thread
                            new LoginAsyncTask(email, pwd).execute();
                        }
                    }
                }
        );
    }
    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //When Register Button is clicked
    public void registerIsClicked(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    // Check Internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
