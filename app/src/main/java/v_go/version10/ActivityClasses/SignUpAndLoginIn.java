package v_go.version10.ActivityClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import v_go.version10.PersistentCookieStore.SiCookieStore2;
import v_go.version10.R;

public class SignUpAndLoginIn extends AppCompatActivity {

    private static SharedPreferences sp;
    public static SharedPreferences getSharedPreferences(){
        return sp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_and_login_in);

        // initialize SharePreferences for local cookie store
        sp = getSharedPreferences(SiCookieStore2.COOKIE_PREFS, 0);

        SharedPreferences settings = getApplicationContext().getSharedPreferences("cache", 0);

        if(settings.getBoolean("is_logged_in", false)){
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
        }
    }

    // sign up
    public void onSignUpClicked(View view) {
        Intent intent = new Intent(this, SignUp1.class);
        startActivity(intent);
    }

    // login in
    public void onLoginClicked(View view){
        Intent intent = new Intent(this, LoginNew.class);
        startActivity(intent);
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
