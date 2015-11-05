package v_go.version10.ActivityClasses;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import v_go.version10.HelperClasses.RegisterInfoAdapter;
import v_go.version10.R;


public class FindTrip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trip);

        //Back button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Setup swipe refresh
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
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
            }
        });

        String[] description = {"PULL TO REFRESH",
                                "UBC->LANGARA",
                                "VANCOUVER->BURNABY",
                                "VANCOUVER->BURNABY",
                                "VANCOUVER->RICHMOND",
                                "VANCOUVER->RICHMOND",
                                "VANCOUVER->RICHMOND",
                                "VANCOUVER->RICHMOND",
                                "VANCOUVER->YVR",
                                "YVR->RICHMOND",
                                "VANCOUVER->BURNABY",
                                "LANGARA->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",
                                "UBC->SFU",};

        ListView listView  = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new RegisterInfoAdapter(this, description, listView);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            Intent intent = new Intent(this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
