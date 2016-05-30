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
import org.json.JSONException;
import org.json.JSONObject;

import v_go.version10.HelperClasses.Global;
import v_go.version10.R;
import v_go.version10.ApiClasses.User;

public class Login extends AppCompatActivity {


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

        assert button != null;
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
