package v_go.version10.ActivityClasses;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.TabHost.*;

import v_go.version10.FragmentClasses.Tab1Fragment;
import v_go.version10.FragmentClasses.Tab2Fragment;
import v_go.version10.FragmentClasses.Tab3Fragment;
import v_go.version10.FragmentClasses.Tab4Fragment;
import v_go.version10.R;

public class Main extends AppCompatActivity{

    private FragmentTabHost mTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------------------------------------------------------

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        TabSpec tabSpec1 = mTabHost.newTabSpec("tag1");
        tabSpec1.setIndicator("", ContextCompat.getDrawable(this, R.drawable.match_tab));
        mTabHost.addTab(tabSpec1, Tab1Fragment.class, null);

        TabSpec tabSpec2 = mTabHost.newTabSpec("tag2");
        tabSpec2.setIndicator("", ContextCompat.getDrawable(this, R.drawable. msg_tab));
        mTabHost.addTab(tabSpec2, Tab2Fragment.class, null);

        TabSpec tabSpec3 = mTabHost.newTabSpec("tag3");
        tabSpec3.setIndicator("", ContextCompat.getDrawable(this, R.drawable.trip_tab));
        mTabHost.addTab(tabSpec3, Tab3Fragment.class, null);

        TabSpec tabSpec4 = mTabHost.newTabSpec("tag4");
        tabSpec4.setIndicator("", ContextCompat.getDrawable(this, R.drawable.setting_tab));
        mTabHost.addTab(tabSpec4, Tab4Fragment.class, null);

        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#43a399"));
        }
        mTabHost.getTabWidget().setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#6ec4bb"));

        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#43a399"));
                }
                mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#6ec4bb"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
