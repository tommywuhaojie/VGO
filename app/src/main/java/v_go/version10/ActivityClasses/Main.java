package v_go.version10.ActivityClasses;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TabHost.*;

import com.roomorama.caldroid.CaldroidFragment;

import v_go.version10.FragmentClasses.FragmentChangeListener;
import v_go.version10.FragmentClasses.Tab1Fragment;
import v_go.version10.FragmentClasses.Tab2Fragment;
import v_go.version10.FragmentClasses.Tab3Fragment;
import v_go.version10.FragmentClasses.Tab4Fragment;
import v_go.version10.R;

public class Main extends AppCompatActivity implements FragmentChangeListener {

    private FragmentTabHost mTabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------------------------------------------------------

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        TabSpec tabSpec1 = mTabHost.newTabSpec("tag1");
        tabSpec1.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab1));
        mTabHost.addTab(tabSpec1, Tab1Fragment.class, null);

        TabSpec tabSpec2 = mTabHost.newTabSpec("tag2");
        tabSpec2.setIndicator("", ContextCompat.getDrawable(this, R.drawable. tab2));
        mTabHost.addTab(tabSpec2, Tab2Fragment.class, null);

        TabSpec tabSpec3 = mTabHost.newTabSpec("tag3");
        tabSpec3.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab3));
        mTabHost.addTab(tabSpec3, Tab3Fragment.class, null);

        TabSpec tabSpec4 = mTabHost.newTabSpec("tag4");
        tabSpec4.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab4));
        mTabHost.addTab(tabSpec4, Tab4Fragment.class, null);

        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#cccccc"));
        }
        mTabHost.getTabWidget().setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#a6a6a6"));

        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#cccccc"));
                }
                mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#a6a6a6"));
            }
        });

        // prevent dialogs from closing by outside click
        setFinishOnTouchOutside(false);
    }

    // change actionBar title from fragment
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    //enable/disable back button from fragment
    public void enableBackButton(Boolean b){
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
        getSupportActionBar().setDisplayShowHomeEnabled(b);
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

    @Override
    //go next
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                                R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.realtabcontent, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    //go back
    public void goBackToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.realtabcontent, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void onBackPressedFragment() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();

        } else {
            Log.d("DEBUG", "popBackStack is call");
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    public void onBackPressed(){
        return;
    }



}
