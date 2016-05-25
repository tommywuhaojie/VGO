package v_go.version10.ActivityClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import v_go.version10.HelperClasses.Global;
import v_go.version10.R;
import v_go.version10.ApiClasses.User;

public class Login extends AppCompatActivity {

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Programmatic Way to Get Hash Code **
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "v_go.version10",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("DEBUG", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {}
        **/

        // change action bar title
        getSupportActionBar().setTitle("Sign In");

        // Initialize Facebook SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        /** --------------------------- Facebook Login ----------------------------- **/
        LoginButton FBLoginButton = (LoginButton) findViewById(R.id.login_button);
        FBLoginButton.setReadPermissions("user_friends");
        FBLoginButton.setReadPermissions("public_profile");
        FBLoginButton.setReadPermissions("email");

        FBLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                    Log.d("DEBUG", "FB Login Failed");
                                } else {
                                    Log.d("DEBUG", "FB Login Success");
                                    Global.FB_LOGIN = true;
                                    try {

                                        String jsonresult = String.valueOf(json);
                                        System.out.println("JSON Result"+jsonresult);

                                        //String str_email = json.getString("email");
                                        String str_id = json.getString("id");
                                        String str_name = json.getString("name");

                                        Intent intent = new Intent(Login.this, Main.class);
                                        intent.putExtra("isFacebookLogin", true);
                                        intent.putExtra("id", str_id);
                                        intent.putExtra("name", str_name);

                                        // if successful enter Main activity
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

        //AsyncTask-------------------------------------------------------------
        class LoginAsyncTask extends AsyncTask<Void, Void, String> {
            private ProgressDialog pDialog;
            private String email, pwd;
            private int result;
            private final int SUCCESS = 1;
            private final int FAIL = 0;

            public LoginAsyncTask(String email, String pwd) {
                super();
                this.email = email;
                this.pwd = pwd;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(Login.this);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
                pDialog.setMessage("Logging in...");
                pDialog.show();
            }
            @Override
            protected String doInBackground(Void... params)
            {
                try {
                    User user = new User();
                    result = Integer.parseInt("1");
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute (String res){

                pDialog.dismiss();

                if(result == FAIL) {
                    // Dialog----------------------------------------------------------
                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                    alertDialog.setMessage("Incorrect email or password.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // here you can add functions
                        }
                    });
                    alertDialog.show();

                }else if(result == SUCCESS){
                    Intent intent = new Intent(Login.this, Main.class);
                    startActivity(intent);
                }
            }
        }


        Intent intent = getIntent();
        Boolean isLogout = intent.getBooleanExtra("logout", false);

        // Get login information from the SharedPreferences
        SharedPreferences settings = getApplicationContext().getSharedPreferences("loginInfo", 0);
        String email = settings.getString("email", "");
        String pwd = settings.getString("password", "");
        Boolean keepMeLoggedIn = settings.getBoolean("KeepMeLoggedIn", false);
        Boolean fromRegister = intent.getBooleanExtra("fromRegister", false);

        // recover email from history
        EditText emailField = (EditText) findViewById(R.id.email_address);
        emailField.setText(email);

        // keep me login toggle listener
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences settings = getApplicationContext().getSharedPreferences("loginInfo", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    if (isChecked) {
                        editor.putBoolean("KeepMeLoggedIn", true);
                    } else {
                        editor.putBoolean("KeepMeLoggedIn", false);
                    }
                    // Apply the edits!
                    editor.apply();
                }
            }
        );

        if(keepMeLoggedIn){
            checkBox = (CheckBox) findViewById(R.id.checkBox);
            checkBox.setChecked(true);
        }
        // if keepMeLoggedIn is checked AND not from logout AND not from register page, then auto login
        if(keepMeLoggedIn && !isLogout && !fromRegister) {

            EditText pwdField = (EditText) findViewById(R.id.pwd);
            pwdField.setText(pwd);

            checkBox = (CheckBox) findViewById(R.id.checkBox);
            checkBox.setChecked(true);

            String email_field = emailField.getText().toString();
            String pwd_field = pwdField.getText().toString();

            new LoginAsyncTask(email_field, pwd_field).execute();
        }

        Button button = (Button)findViewById(R.id.loginButton);

        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {


                        EditText emailET = (EditText) findViewById(R.id.email_address);
                        EditText pwdET = (EditText) findViewById(R.id.pwd);

                        String email = emailET.getText().toString();
                        String pwd = pwdET.getText().toString();

                        boolean ok = true;

                        // CHECK INTERNET CONNECTION
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

                        if (!isValidEmail(email)) {
                            emailET.setError("Invalid email address");
                            ok = false;
                        }

                        if (pwd.trim().length() == 0) {
                            pwdET.setError("Password cannot be empty");
                            ok = false;
                        }

                        if (ok) {
                            // Remember user's email
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("loginInfo", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("email", email.trim());
                            // remember password
                            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
                            if (checkBox.isChecked()) {
                                editor.putString("password", pwd);
                            } else {
                                editor.putString("password", "");
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

    // When Register Button is clicked
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

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}
