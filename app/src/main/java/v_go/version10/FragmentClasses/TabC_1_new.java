package v_go.version10.FragmentClasses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.User;
import v_go.version10.HelperClasses.ContactListAdapter;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.MySimpleAdapter;
import v_go.version10.HelperClasses.Notification;
import v_go.version10.R;

public class TabC_1_new extends Fragment {

    private View view;
    private SimpleAdapter adapter;
    private ListView contactListView;
    List<HashMap<String, String>> hashMap = new ArrayList<>();
    List<Bitmap> avatarList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        view = inflater.inflate(R.layout.tab_c_1_new, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // change toolbar title
        setHasOptionsMenu(true);

        // set up the notification list
        contactListView = (ListView) view.findViewById(R.id.listView);

        // retrieve ListView
        if(adapter != null) {
            contactListView.setAdapter(adapter);
        }

        // on listView item clicked
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create new private chat activity
            }
        });

        // Setup swipe refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // disable tab when loading notification
                ((Main) getActivity()).getTabWidget().setEnabled(false);

                //setupAdapter();
                //contactListView.setAdapter(adapter);
                Toast.makeText(getActivity(), "Everything is up to date.", Toast.LENGTH_SHORT).show();
                // enable tab
                ((Main) getActivity()).getTabWidget().setEnabled(true);

                // refresh animation
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        // refresh only at the top of the ListView mechanism
        contactListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (contactListView == null || contactListView.getChildCount() == 0) ? 0 : contactListView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    /** setup adapter here to avoid lagging during changing view **/
    @Override
    public void onResume() {
        super.onResume();
    }

    /** setup the notification listview **/
    public void setupAdapter(String first_name_last_name, final String user_id) {
        final String[] from = new String[]{"first_name_last_name", "last_message"};
        final int[] to = new int[]{R.id.firstLine, R.id.secondLine};

        final HashMap<String, String> map = new HashMap<>();

        map.put("first_name_last_name", first_name_last_name);
        map.put("last_message", "Hello");

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = User.DownloadAvatar(user_id);

                bitmap = Global.getCircularBitmap(bitmap);

                avatarList.add(0, bitmap);

                hashMap.add(0, map);

                adapter = new ContactListAdapter(getActivity(), hashMap, R.layout.contact_row, from, to, avatarList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactListView.setAdapter(adapter);
                    }
                });
            }
        });
        networkThread.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_chat_contacts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add) {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.add_contact_dialog, null);
            final EditText phoneET = (EditText)promptsView.findViewById(R.id.add_contact_phone_number);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Add",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                    addToContactList(phoneET.getText().toString());
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToContactList(final String phone_number){

        final String REQUEST_BY_PHONE_NUMBER = "phone_number";

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                final JSONObject result = User.GetUserInfo(phone_number, REQUEST_BY_PHONE_NUMBER);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(result.getString("code").matches("1")){

                                String displayName = result.getString("first_name") + " " + result.getString("last_name");
                                String user_id = result.getString("user_id");
                                setupAdapter(displayName, user_id);

                            }else if(result.getString("code").matches("-1")) {
                                Toast.makeText(getContext(), "User is not found.", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Server error occurs.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        networkThread.start();
    }

}