package v_go.version10.FragmentClasses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.Switch;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.Request;
import v_go.version10.R;

public class TabC_3 extends Fragment   {

    private int req_id;

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

        req_id = getArguments().getInt("req_id");

        Button accept = (Button) view.findViewById(R.id.accept);
        Button deny = (Button) view.findViewById(R.id.deny);

        // on accept
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DecisionAsyncTask().execute(req_id, 1);
            }
        });

        // on deny
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DecisionAsyncTask().execute(req_id, 0);
            }
        });

        return  view;
    }

    class DecisionAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            Request request = new Request();
            return Integer.parseInt((request.RequestResponse(params[0] + "", params[1] + "")).trim());
        }

        @Override
        protected void onPostExecute(Integer i) {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            switch (i){
                case 1:
                    alertDialog.setIcon(R.drawable.check);
                    alertDialog.setTitle("Done!");
                    alertDialog.setMessage("Thank you for your respond!");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case 0:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.setTitle("Failed!");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case -1:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.setTitle("Failed!");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case -3:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.setTitle("Failed!");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
                case -7:
                    alertDialog.setIcon(R.drawable.fail);
                    alertDialog.setTitle("Failed!");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    break;
            }
        }
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