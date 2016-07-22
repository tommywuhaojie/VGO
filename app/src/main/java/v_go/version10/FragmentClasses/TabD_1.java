package v_go.version10.FragmentClasses;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.ActivityClasses.SignUp3;
import v_go.version10.ApiClasses.UserApi;
import v_go.version10.HelperClasses.BackgroundService;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class TabD_1 extends Fragment   {

    private View view;
    private ImageView avatarImageView;
    private Bitmap avatarBitmap;

    @Override
    public void onResume() {
        super.onResume();

        if(Global.NEED_TO_DOWNLOAD_TAB_D_AVATAR){
            Global.NEED_TO_DOWNLOAD_TAB_D_AVATAR = false;
            downloadCurrentUserAvatar();
        }else{
            avatarBitmap = UserApi.loadAvatarFromStorage(
                    getCachedUserId(getActivity().getApplicationContext()),
                    getActivity().getApplicationContext());
            if(avatarBitmap != null){
                avatarBitmap = UserApi.getCircularBitmap(avatarBitmap);
                avatarImageView.setImageBitmap(avatarBitmap);
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
            avatarBitmap.recycle();
            avatarBitmap = null;
            System.gc();
        }
    }
    private void downloadCurrentUserAvatar(){
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                avatarBitmap = UserApi.DownloadAvatar();
                if (avatarBitmap != null) {
                    UserApi.saveAvatarToStorage(avatarBitmap,
                            getCachedUserId(getActivity().getApplicationContext()),
                            getActivity().getApplicationContext());
                    avatarBitmap = UserApi.getCircularBitmap(avatarBitmap);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            avatarImageView.setImageBitmap(avatarBitmap);
                        }
                    });

                }
            }
        });
        networkThread.start();
    }

    private String getCachedUserId(Context AppContext){
        SharedPreferences settings = AppContext.getSharedPreferences("cache", 0);
        return settings.getString("user_id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.tab_d_1, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //window.setStatusBarColor(Color.BLACK);

        avatarImageView = (ImageView) view.findViewById(R.id.avatar);

        if(((Main)getActivity()).getUserCache().getAvatar() != null){
            avatarImageView.setImageBitmap(((Main)getActivity()).getUserCache().getAvatar());
        }

        // change avatar
        Button changeAvatar = (Button) view.findViewById(R.id.change_avatar);
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignUp3.class);
                intent.putExtra("isUpdate",true);
                startActivity(intent);
            }
        });

        // logout
        Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                logout();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setMessage("Are you sure you want to sign out?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        // change toolbar title
        getActivity().setTitle("Settings");
        ((Main) getActivity()).enableBackButton(false);
        setHasOptionsMenu(true);


        return view;
    }

    private void logout(){

        // calling logout to kill session
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = UserApi.Logout();
                    Log.d("DEBUG", "logout msg: " + jsonObject.getString("msg"));
                }catch (Exception e){
                    Log.d("DEBUG", "something went wrong when attempting to logout " + e.getMessage());
                }}});
        networkThread.start();

        // stop service & disconnect socket
        getActivity().stopService(new Intent(getActivity().getBaseContext(), BackgroundService.class));

        // clear all notifications if there is any
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(BackgroundService.NOTIFICATION_ID);

        // clear local is_logged_in flag
        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("cache", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        // reset all user global variables
        Global.resetAll();

        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home) {
        }
        return super.onOptionsItemSelected(item);
    }
}