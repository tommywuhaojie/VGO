package v_go.version10.FragmentClasses;

import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.MySimpleAdapter;
import v_go.version10.R;

public class TabC_2 extends Fragment   {

    private SimpleAdapter adapter;
    private ListView notificationListView;
    private ArrayList<Integer> reqList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_c_2, container, false);

        // set status bar color to black
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // change toolbar title
        getActivity().setTitle("Notifications");
        ((Main) getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        // Setup swipe refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_dark,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

                //simpleArray();
            }
        });


        // set up the notification list
        notificationListView = (ListView) view.findViewById(R.id.listView);
        setupAdapter();
        notificationListView.setAdapter(adapter);

        // set up req id list
        reqList = ((Main)getActivity()).getAllReqList();
        Collections.reverse(reqList);


        // on listView item clicked
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                try {

                    args.putInt("req_id", reqList.get(position));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Fragment fragment = new TabC_3();
                fragment.setArguments(args);
                ((Main) getActivity()).pushFragments(Global.TAB_C, fragment, true, true);
            }
        });


        // refresh only at the top of the ListView mechanism
        notificationListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (notificationListView == null || notificationListView.getChildCount() == 0) ? 0 : notificationListView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
    }

    private void setupAdapter(){
        String[] from = new String[] {"name", "message"};
        int[] to = new int[] { R.id.firstLine, R.id.secondLine};
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

        ArrayList<String> nameList = ((Main)getActivity()).getAllSenderName();
        ArrayList<Integer> typeList = ((Main)getActivity()).getALLNotificationType();
        int size = nameList.size();

        if(size == 0){
            Toast.makeText(getActivity(), "You don't have any notification.", Toast.LENGTH_LONG).show();
        }else{
            for(int i=size-1; i>=0; i--) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", nameList.get(i));

                // determine notification type and result
                if(typeList.get(i) == 0){
                    map.put("message", "wishes to joint your trip as a passenger!");
                }else if(typeList.get(i) == 1){
                    map.put("message", "wishes to joint your trip as a driver!");
                }else if(typeList.get(i) == 2){
                    map.put("message", "has denied your request.");
                }else if(typeList.get(i) == 3){
                    map.put("message", "has accepted your request!");
                }

                fillMaps.add(map);
            }
            adapter = new MySimpleAdapter(getActivity(), fillMaps, R.layout.notification_row, from, to);
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