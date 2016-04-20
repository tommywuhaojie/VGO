package v_go.version10.ActivityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import v_go.version10.R;

public class SignUpAndLoginIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_and_login_in);
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
}
