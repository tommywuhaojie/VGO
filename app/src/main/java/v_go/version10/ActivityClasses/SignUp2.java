package v_go.version10.ActivityClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import v_go.version10.ApiClasses.User;
import v_go.version10.R;

public class SignUp2 extends AppCompatActivity {

    private final String GREY_HINT = "#808080";
    private int userSex = -1; // 0 -> female 1 -> male -1 -> not selected
    private int userTypeToggle = 0; // 0 -> rider 1 -> driver

    private Button showOrHideButton;

    private EditText firstNameET;
    private EditText lastNameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText licensePlateET;
    private EditText driveLicenseET;

    private Spinner brandSp;
    private Spinner typeSp;
    private Spinner yearSp;
    private Spinner colorSp;

    private boolean showPwd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);

        firstNameET = (EditText)findViewById(R.id.first_name);
        lastNameET = (EditText)findViewById(R.id.last_name);
        emailET = (EditText)findViewById(R.id.email_address);
        passwordET = (EditText)findViewById(R.id.password);
        licensePlateET = (EditText)findViewById(R.id.license_plate);
        driveLicenseET = (EditText)findViewById(R.id.driver_license);

        showOrHideButton = (Button) findViewById(R.id.show_hide);

        brandSp = (Spinner)findViewById(R.id.brand);
        typeSp = (Spinner)findViewById(R.id.type);
        yearSp = (Spinner)findViewById(R.id.year);
        colorSp = (Spinner)findViewById(R.id.color);

        // set hint color match background
        firstNameET.setHintTextColor(Color.parseColor(GREY_HINT));
        lastNameET.setHintTextColor(Color.parseColor(GREY_HINT));
        emailET.setHintTextColor(Color.parseColor(GREY_HINT));
        passwordET.setHintTextColor(Color.parseColor(GREY_HINT));
        licensePlateET.setHintTextColor(Color.parseColor(GREY_HINT));
        driveLicenseET.setHintTextColor(Color.parseColor(GREY_HINT));

        // set license plate to all cap
        licensePlateET.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        // underline vehicle info text
        TextView textView = (TextView)findViewById(R.id.vehicle_info_text);
        SpannableString content = new SpannableString("VEHICLE INFORMATION");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    public void onSubmitClicked(View view){

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.setMessage("Submitting...");
        pDialog.show();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                String object_id = getIntent().getStringExtra("object_id");
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                String first_name = firstNameET.getText().toString().trim();
                String last_name = lastNameET.getText().toString().trim();
                String driver_license = driveLicenseET.getText().toString().trim();
                String plate_number = licensePlateET.getText().toString().trim();
                String model = yearSp.getSelectedItem().toString().trim()
                        + "-" + brandSp.getSelectedItem().toString().trim()
                        + "-" + typeSp.getSelectedItem().toString().trim();
                String color = colorSp.getSelectedItem().toString().trim();

                final JSONObject jsonObject = User.Register(object_id, email, password, first_name, last_name,
                        userSex, driver_license, plate_number, model, color);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(jsonObject.getString("code").matches("1")){
                                Intent intent = new Intent(SignUp2.this, SignUp3.class);
                                startActivity(intent);
                                pDialog.dismiss();
                            }else{
                                System.out.println("error msg: " + jsonObject.getString("msg"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            pDialog.dismiss();
                            Toast.makeText(SignUp2.this, "Server error occurs.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        networkThread.start();
    }

    public void switchToDriverInfo(View view){

        if(userTypeToggle == 0)
            return;

        findViewById(R.id.rider_infobox).setVisibility(View.GONE);
        findViewById(R.id.tab_switch_white).setVisibility(View.GONE);

        findViewById(R.id.driver_infobox).setVisibility(View.VISIBLE);
        findViewById(R.id.tab_switch_black).setVisibility(View.VISIBLE);
        findViewById(R.id.toggle_2nd).setVisibility(View.VISIBLE);
    }

    public void switchToRiderInfo(View view){
        findViewById(R.id.driver_infobox).setVisibility(View.GONE);
        findViewById(R.id.tab_switch_black).setVisibility(View.GONE);
        findViewById(R.id.toggle_2nd).setVisibility(View.GONE);

        findViewById(R.id.rider_infobox).setVisibility(View.VISIBLE);
        findViewById(R.id.tab_switch_white).setVisibility(View.VISIBLE);
    }

    public void toggleOnFirstPage(View view){
        if(userTypeToggle == 0){
            userTypeToggle = 1;
            ((ImageView)view).setImageResource(R.drawable.toggle_driver);
        }else{
            userTypeToggle = 0;
            ((ImageView)view).setImageResource(R.drawable.toggle_rider);
        }
    }
    public void toggleOnSecondPage(View view){
        if(userTypeToggle == 1){
            userTypeToggle = 0;
            switchToRiderInfo(findViewById(R.id.tab_switch_black));
            ((ImageView)findViewById(R.id.toggle_1st)).setImageResource(R.drawable.toggle_rider);
        }
    }

    public void onFemaleChecked(View view){
        if(userSex == 1 || userSex == -1){
            userSex = 0;
            ((ImageView)findViewById(R.id.radio_female)).setImageResource(R.drawable.female_check);
            ((ImageView)findViewById(R.id.radio_male)).setImageResource(R.drawable.male_uncheck);
        }
    }
    public void onMaleChecked(View view){
        if(userSex == 0 || userSex == -1){
            userSex = 1;
            ((ImageView)findViewById(R.id.radio_female)).setImageResource(R.drawable.female_uncheck);
            ((ImageView)findViewById(R.id.radio_male)).setImageResource(R.drawable.male_check);
        }
    }

    public void onShowOrHidePwdClicked(View view){
        if(showPwd){
            passwordET.setTransformationMethod(new PasswordTransformationMethod());
            showOrHideButton.setText("SHOW");
            showPwd = false;
        }else{
            passwordET.setTransformationMethod(null);
            showOrHideButton.setText("HIDE");
            showPwd = true;
        }
    }

    public void onBackArrowClicked(View view){
        onBackPressed();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}