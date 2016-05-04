package v_go.version10.ActivityClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import v_go.version10.R;

public class SignUp1 extends AppCompatActivity {

    private final String THEME_COLOR = "#50B9AC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_1);

        EditText phone = (EditText)findViewById(R.id.phone);
        EditText passcode = (EditText)findViewById(R.id.passcode);

        // set hint color match background
        phone.setHintTextColor(Color.parseColor(THEME_COLOR));
        passcode.setHintTextColor(Color.parseColor(THEME_COLOR));
    }

    public void onSendCodeClicked(View view){
        Toast.makeText(SignUp1.this, "Code sent", Toast.LENGTH_SHORT).show();
    }

    public void onSignUpClicked(View view){
        Intent intent = new Intent(this, SignUp2.class);
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