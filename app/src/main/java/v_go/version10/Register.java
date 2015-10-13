package v_go.version10;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import v_go.version10.User;


public class Register extends AppCompatActivity {

    /*
    //hide keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Back button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
        if(id == android.R.id.home){
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //When register button is clicked
    public void registerIsClicked(View view){

        boolean ok = true;

        //CHECK INTERNET CONNECTION
        if(!isNetworkAvailable()) {
            AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
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

        EditText emailET = (EditText) findViewById(R.id.email);
        EditText pwdET = (EditText) findViewById(R.id.pwd);
        EditText pwdConET = (EditText) findViewById(R.id.pwdCon);
        EditText fnET = (EditText) findViewById(R.id.firstName);
        EditText lnET = (EditText) findViewById(R.id.lastName);
        EditText phoneET = (EditText) findViewById(R.id.phoneNum);

        String email = emailET.getText().toString().trim();
        String pwd = pwdET.getText().toString();
        String pwdCon = pwdConET.getText().toString();
        final String firstName = fnET.getText().toString().trim();
        String lastName = lnET.getText().toString().trim();
        String phoneNum = phoneET.getText().toString().trim();

        if(!isValidEmail(email)){
            emailET.setError("Invalid email address");
            ok = false;
        }

        if(!isValidPassword(pwd)){
            pwdET.setError("Must contain:\n" +
                    "at least 8 characters\n"+
                    "at least 1 number\n" +
                    "at least 1 lower case letter\n" +
                    "at least 1 upper case letter\n" +
                    "no whitespace");
            ok = false;
        }

        if(!isValidPassword(pwdCon)){
            pwdConET.setError("Must contain:\n" +
                    "at least 8 characters\n" +
                    "at least 1 number\n" +
                    "at least 1 lower case letter\n" +
                    "at least 1 upper case letter\n" +
                    "no whitespace");
            ok = false;
        }

        if(isValidPassword(pwd) && isValidPassword(pwdCon) && !pwdCon.matches(pwd)){
            pwdConET.setError("Two passwords do NOT match");
            ok = false;
        }

        if(!firstName.matches( "[a-zA-Z]*" ) || firstName.length() == 0){
            fnET.setError("Invalid first name");
            ok = false;
        }

        if(!lastName.matches( "[a-zA-Z]*" ) || lastName.length() == 0){
            lnET.setError("Invalid last name");
            ok = false;
        }

        if(phoneNum.length() != 10){
            phoneET.setError("Must be 10 digits long");
            ok = false;
        }

        if(ok) {

            //AsyncTask-------------------------------------------------------------
            class registerAsyncTask extends AsyncTask<Void, Void, String>
            {
                private String email, pwd, phone, lastname, firstname;
                private int result;

                private final int FAIL = -1;
                private final int INVALID_INPUT = -2;
                private final int EMAIL_ALREADY_EXISTS = -3;
                private final int NOT_ACTIVATED = 0;
                private final int SUCCESS = 1;

                public registerAsyncTask(String email,String password,String phone,String lastname,String firstname) {
                    super();
                    this.email = email;
                    this.pwd = password;
                    this.lastname = lastname;
                    this.firstname = firstname;
                    this.phone = phone;
                }

                private ProgressDialog pDialog;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    pDialog = new ProgressDialog(Register.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                }
                @Override

                protected String doInBackground(Void... params)
                {


                    try {
                        User user = new User();
                        result = Integer.parseInt(user.Register(email, pwd, phone, lastname, firstname).trim());

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
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setMessage("Unable to register at this time");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // here you can add functions
                            }
                        });
                        alertDialog.show();

                    }
                    if(result == INVALID_INPUT) {
                        //Dialog----------------------------------------------------------
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setMessage("Invalid user information");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // here you can add functions
                            }
                        });
                        alertDialog.show();

                    }
                    if(result == EMAIL_ALREADY_EXISTS) {
                        //Dialog----------------------------------------------------------
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setMessage("Email or phone number has already been taken");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // here you can add functions
                            }
                        });
                        alertDialog.show();

                    }
                    if(result == NOT_ACTIVATED) {
                        //Dialog----------------------------------------------------------
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setMessage("Not activated");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // here you can add functions
                            }
                        });
                        alertDialog.show();

                    }
                    if(result == SUCCESS) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
                        alertDialog.setTitle("SUCCEEDED");
                        alertDialog.setMessage("Returning to Login Page...");
                        alertDialog.show();

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(Register.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }, 2000);//delay for 2 seconds
                    }
                }
            }
            // run Register thread
            new registerAsyncTask(email, pwd, phoneNum, lastName, firstName).execute();
        }
    }

    // Check Internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
