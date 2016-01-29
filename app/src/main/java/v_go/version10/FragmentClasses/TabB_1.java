package v_go.version10.FragmentClasses;

import android.graphics.Color;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class TabB_1 extends Fragment   {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_b_1, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //window.setStatusBarColor(Color.BLACK);

        // change toolbar title
        getActivity().setTitle("Messaging");
        ((Main) getActivity()).enableBackButton(false);
        setHasOptionsMenu(true);

        // to test FB friend list
        if(Global.FB_LOGIN){
            facebookTesting();
        }

        return view;
    }

    private void facebookTesting(){
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        if(response != null) {
                            Log.d("DEBUG", response.toString());
                        }
                        try {
                            JSONArray friendArray = response.getJSONObject().getJSONArray("data");
                            Log.d("DEBUG", "size1: " + friendArray.length());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/taggable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONArray friendArray = response.getJSONObject().getJSONArray("data");
                            Log.d("DEBUG", "size2: " + friendArray.length());

                            for(int i=0; i<friendArray.length(); i++){
                                Log.d("DEBUG", friendArray.getJSONObject(i).getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
        ).executeAsync();
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