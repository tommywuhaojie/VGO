package v_go.version10.ActivityClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import v_go.version10.R;

public class SignUp2 extends AppCompatActivity {

    private final String GREY_HINT = "#808080";
    private int userSex = 0; // 0 -> female 1 -> male
    private int userTypeToggle = 0; // 0 -> rider 1 -> driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);

        EditText editText1 = (EditText)findViewById(R.id.first_name);
        EditText editText2 = (EditText)findViewById(R.id.last_name);
        EditText editText3 = (EditText)findViewById(R.id.email_address);
        EditText editText4 = (EditText)findViewById(R.id.password);
        EditText editText5 = (EditText)findViewById(R.id.license_plate);
        EditText editText6 = (EditText)findViewById(R.id.driver_license);

        // set hint color match background
        editText1.setHintTextColor(Color.parseColor(GREY_HINT));
        editText2.setHintTextColor(Color.parseColor(GREY_HINT));
        editText3.setHintTextColor(Color.parseColor(GREY_HINT));
        editText4.setHintTextColor(Color.parseColor(GREY_HINT));
        editText5.setHintTextColor(Color.parseColor(GREY_HINT));
        editText6.setHintTextColor(Color.parseColor(GREY_HINT));

        // set license plate to all cap
        editText5.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        // underline vehicle info text
        TextView textView = (TextView)findViewById(R.id.vehicle_info_text);
        SpannableString content = new SpannableString("VEHICLE INFORMATION");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
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
        if(userSex == 1){
            userSex = 0;
            ((ImageView)findViewById(R.id.radio_female)).setImageResource(R.drawable.radio_check);
            ((ImageView)findViewById(R.id.radio_male)).setImageResource(R.drawable.radio_uncheck);
        }
    }
    public void onFemaleTextClicked(View view){
        if(userSex == 1){
            userSex = 0;
            ((ImageView)findViewById(R.id.radio_female)).setImageResource(R.drawable.radio_check);
            ((ImageView)findViewById(R.id.radio_male)).setImageResource(R.drawable.radio_uncheck);
        }
    }

    public void onMaleChecked(View view){
        if(userSex == 0){
            userSex = 1;
            ((ImageView)findViewById(R.id.radio_female)).setImageResource(R.drawable.radio_uncheck);
            ((ImageView)findViewById(R.id.radio_male)).setImageResource(R.drawable.radio_check);
        }
    }
    public void onMaleTextClicked(View view){
        if(userSex == 0){
            userSex = 1;
            ((ImageView)findViewById(R.id.radio_female)).setImageResource(R.drawable.radio_uncheck);
            ((ImageView)findViewById(R.id.radio_male)).setImageResource(R.drawable.radio_check);
        }
    }

    public void onSubmitClicked(View view){
        Intent intent = new Intent(this, SignUp3.class);
        startActivity(intent);
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