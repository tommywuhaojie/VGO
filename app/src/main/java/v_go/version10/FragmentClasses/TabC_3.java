package v_go.version10.FragmentClasses;

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

import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.Request;
import v_go.version10.R;

public class TabC_3 extends Fragment   {

    final private int req_id[] = new int[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_c_3, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // change toolbar title
        getActivity().setTitle("Request");
        ((Main) getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        req_id[0] = getArguments().getInt("req_id");

        Button accept = (Button) view.findViewById(R.id.accept);
        Button deny = (Button) view.findViewById(R.id.deny);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = new Request();
                        request.RequestResponse(req_id[0] + "", 1+"");
                    }

                });
                thread.run();
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = new Request();
                        request.RequestResponse(req_id[0] + "", 0+"");
                    }

                });
                thread.run();

            }
        });

        return  view;
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
        if(id == android.R.id.home){
            ((Main)getActivity()).popFragments();
        }
        return super.onOptionsItemSelected(item);
    }

}