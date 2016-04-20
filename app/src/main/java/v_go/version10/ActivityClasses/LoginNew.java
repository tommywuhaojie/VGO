package v_go.version10.ActivityClasses;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import v_go.version10.R;

public class LoginNew extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        EditText phone = (EditText)findViewById(R.id.phone);
        EditText passcode = (EditText)findViewById(R.id.passcode);

        // set hint color match background
        phone.setHintTextColor(Color.parseColor("#50B9AC"));
        passcode.setHintTextColor(Color.parseColor("#50B9AC"));
    }

    public void onBackArrowClicked(View view){
        onBackPressed();
    }

}

