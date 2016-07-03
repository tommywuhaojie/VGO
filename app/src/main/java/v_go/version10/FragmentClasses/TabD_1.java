package v_go.version10.FragmentClasses;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import v_go.version10.ActivityClasses.LoginNew;
import v_go.version10.ActivityClasses.Main;
import v_go.version10.ActivityClasses.SignUp3;
import v_go.version10.ApiClasses.User;
import v_go.version10.HelperClasses.BackgroundService;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class TabD_1 extends Fragment   {

    private View view;

    @Override
    public void onResume() {
        super.onResume();

        // download the avatar of current user
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = User.DownloadAvatar();
                if(bitmap != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap circleBitmap = User.getCircularBitmap(bitmap);
                            ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
                            imageView.setImageBitmap(circleBitmap);
                        }
                    });
                }
            }
        });
        networkThread.start();
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

        // download the avatar of current user
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = User.DownloadAvatar();
                if(bitmap != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap circleBitmap = User.getCircularBitmap(bitmap);
                            ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
                            imageView.setImageBitmap(circleBitmap);
                        }
                    });
                }
            }
        });
        networkThread.start();

        // change avatar
        Button changeAvatar = (Button) view.findViewById(R.id.change_avatar);
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignUp3.class);
                startActivity(intent);
            }
        });

        // logout
        Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().stopService(new Intent(getActivity().getBaseContext(), BackgroundService.class));

                // reset all user global variables
                Global.resetAll();

                Intent intent = new Intent(getActivity(), LoginNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("logout", true);
                startActivity(intent);
            }
        });

        // change toolbar title
        getActivity().setTitle("Settings");
        ((Main) getActivity()).enableBackButton(false);
        setHasOptionsMenu(true);


        return view;
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