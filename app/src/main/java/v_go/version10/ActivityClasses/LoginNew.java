package v_go.version10.ActivityClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import v_go.version10.ApiClasses.User;
import v_go.version10.FragmentClasses.TabA_1;
import v_go.version10.R;
import v_go.version10.SocketIo.SocketIoHelper;

public class LoginNew extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        EditText phone = (EditText)findViewById(R.id.phone);
        EditText password = (EditText)findViewById(R.id.password);

        // set hint color match background
        phone.setHintTextColor(Color.parseColor("#50B9AC"));
        password.setHintTextColor(Color.parseColor("#50B9AC"));

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

        SocketIoHelper socketHelper = (SocketIoHelper) getApplication();
        Socket mSocket = socketHelper.getSocket();
        mSocket.connect();
    }

    public void onLoginClicked(View view){

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in...");
        pDialog.show();

        // hardcode username + password login (to be removed when phone api is ready)
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(User.Login("1165637488@qq.com", "123456").getString("code").equals("1")){
                        Intent main = new Intent(LoginNew.this, Main.class);
                        startActivity(main);
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();
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

