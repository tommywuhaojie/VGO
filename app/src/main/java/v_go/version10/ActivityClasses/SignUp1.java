package v_go.version10.ActivityClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import v_go.version10.ApiClasses.User;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class SignUp1 extends AppCompatActivity {

    private final String THEME_COLOR = "#50B9AC";
    private TextView timerIndicator;
    private EditText phone;
    private EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_1);

        phone = (EditText)findViewById(R.id.phone);
        code = (EditText)findViewById(R.id.code);
        timerIndicator = (TextView)findViewById(R.id.timer_indicator);

        if(Global.CAN_SEND_CODE_AGAIN_IN_SECOND != 0){
            new CountDownTimer(Global.CAN_SEND_CODE_AGAIN_IN_SECOND * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    timerIndicator.setText("You can resend code in " + millisUntilFinished / 1000 + " seconds");
                }
                public void onFinish() {
                    timerIndicator.setText("");
                }
            }.start();
        }

        // set hint color match background
        phone.setHintTextColor(Color.parseColor(THEME_COLOR));
        code.setHintTextColor(Color.parseColor(THEME_COLOR));
    }

    public void onSendCodeClicked(View view){

        String phoneNum = phone.getText().toString().trim();
        if(!phoneNum.matches("[0-9]+") || phoneNum.length() != 10 || phoneNum.equals("")){
            phone.setError("Invalid phone number");
            return;
        }

        if(Global.CAN_SEND_CODE_AGAIN_IN_SECOND == 0){

            Thread networkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final JSONObject jsonObject = User.SendVerificationCode(phone.getText().toString().trim());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(jsonObject != null){
                                try {
                                    Log.d("DEBUG", "SendCodeApi: " + jsonObject.getString("msg"));

                                    if (jsonObject.getString("code").matches("1")) {
                                        Toast.makeText(SignUp1.this, "Code is sent successfully.", Toast.LENGTH_LONG).show();

                                        // set 60 seconds count down
                                        Global.CAN_SEND_CODE_AGAIN_IN_SECOND = 60;
                                        Global.startSendCodeTimer(60);
                                        new CountDownTimer(Global.CAN_SEND_CODE_AGAIN_IN_SECOND * 1000, 1000) {
                                            public void onTick(long millisUntilFinished) {
                                                timerIndicator.setText("You can resend code in " + millisUntilFinished / 1000 + " seconds");
                                            }
                                            public void onFinish() {
                                                timerIndicator.setText("");
                                            }
                                        }.start(); // start timer only when code is sent out successfully

                                    } else {
                                        Toast.makeText(SignUp1.this, "Failed to send code, please ensure your phone number is correct.", Toast.LENGTH_LONG).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(SignUp1.this, "Server error occurs.", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(SignUp1.this, "Unable to reach server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            networkThread.start();
        }
    }

    public void onSignUpClicked(View view){

        boolean ok = true;

        String phoneNum = phone.getText().toString().trim();
        if(!phoneNum.matches("[0-9]+") || phoneNum.length() != 10 || phoneNum.equals("")){
            phone.setError("Invalid phone number");
            ok = false;
        }

        String codeStr = code.getText().toString().trim();
        if(!codeStr.matches("[0-9]+") || codeStr.length() != 4 || codeStr.equals("")){
            code.setError("Invalid verification code");
            ok = false;
        }

        if(!ok){
            return;
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.setMessage("Verifying...");
        pDialog.show();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject jsonObject = User.VerifyCode(phone.getText().toString().trim(), code.getText().toString().trim());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject != null) {
                            try {
                                Log.d("DEBUG", "VerifyCodeApi: " + jsonObject.getString("msg"));

                                if(jsonObject.getString("code").matches("1")) {
                                    String object_id = jsonObject.getString("object_id");
                                    Intent intent = new Intent(SignUp1.this, SignUp2.class);
                                    intent.putExtra("object_id", object_id);
                                    startActivity(intent);
                                    pDialog.dismiss();

                                }else if(jsonObject.getString("code").matches("-1")){
                                    pDialog.dismiss();
                                    showDialogWithMessage("Incorrect verification code.");

                                }else if(jsonObject.getString("code").matches("-2")){
                                    pDialog.dismiss();
                                    showDialogWithMessage("Please send code for this phone number first.");

                                }else {
                                    pDialog.dismiss();
                                    showDialogWithMessage("Invalid phone number or verification code.");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                pDialog.dismiss();
                                Toast.makeText(SignUp1.this, "Server error occurs.", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            pDialog.dismiss();
                            Toast.makeText(SignUp1.this, "Unable to reach server.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        networkThread.start();
    }

    public void showDialogWithMessage(final String message){
        final AlertDialog alertDialog = new AlertDialog.Builder(SignUp1.this).create();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setMessage(message);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertDialog.show();
            }
        });
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