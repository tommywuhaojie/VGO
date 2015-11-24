package v_go.version10.ActivityClasses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import java.util.HashMap;
import java.util.Stack;

import v_go.version10.FragmentClasses.TabA_1;
import v_go.version10.FragmentClasses.TabB_1;
import v_go.version10.FragmentClasses.TabC_1;
import v_go.version10.FragmentClasses.TabD_1;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class Main extends AppCompatActivity{

    /* Your Tab host */
    private TabHost mTabHost;
    /* A HashMap of stacks, where we use tab identifier as keys..*/
    private HashMap<String, Stack<Fragment>> mStacks;
    /*Save current tabs identifier in this..*/
    private String mCurrentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------------------------------------------------------

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        /*
         *  Navigation stacks for each tab gets created..
         *  tab identifier is used as key to get respective stack for each tab
         */
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Global.TAB_A, new Stack<Fragment>());
        mStacks.put(Global.TAB_B, new Stack<Fragment>());
        mStacks.put(Global.TAB_C, new Stack<Fragment>());
        mStacks.put(Global.TAB_D, new Stack<Fragment>());

        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();

        initializeTabs();

        // prevent dialogs from closing by outside click
        setFinishOnTouchOutside(false);
    }

    public void initializeTabs(){
        /* Setup your tab icons and content views.. Nothing special in this..*/
        // tab1
        TabHost.TabSpec spec    =   mTabHost.newTabSpec(Global.TAB_A);
        mTabHost.setCurrentTab(0);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab1));
        mTabHost.addTab(spec);

        // tab2
        spec = mTabHost.newTabSpec(Global.TAB_B);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab2));
        mTabHost.addTab(spec);

        // tab3
        spec = mTabHost.newTabSpec(Global.TAB_C);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab3));
        mTabHost.addTab(spec);

        // tab4
        spec = mTabHost.newTabSpec(Global.TAB_D);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, R.drawable.tab4));
        mTabHost.addTab(spec);

        // color
        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#cccccc"));
        }
        mTabHost.getTabWidget().setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#a6a6a6"));
    }

    /*Comes here when user switch tab, or we do programmatically*/
    TabHost.OnTabChangeListener listener    =   new TabHost.OnTabChangeListener() {
        public void onTabChanged(String tabId) {
        /*Set current tab..*/
            mCurrentTab = tabId;

            if(mStacks.get(tabId).size() == 0){
          /*
           *    First time this tab is selected. So add first fragment of that tab.
           *    Dont need animation, so that argument is false.
           *    We are adding a new fragment which is not present in stack. So add to stack is true.
           */
                if(tabId.equals(Global.TAB_A)){
                    pushFragments(tabId, new TabA_1(), false,true);
                }else if(tabId.equals(Global.TAB_B)){
                    pushFragments(tabId, new TabB_1(), false,true);
                }else if(tabId.equals(Global.TAB_C)){
                    pushFragments(tabId, new TabC_1(), false,true);
                }else if(tabId.equals(Global.TAB_D)){
                    pushFragments(tabId, new TabD_1(), false,true);
                }

            }else {
          /*
           *    We are switching tabs, and target tab is already has at least one fragment.
           *    No need of animation, no need of stack pushing. Just show the target fragment
           */
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false,false);
            }

            // change the color of tabs
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#cccccc"));
            }
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#a6a6a6"));
        }
    };

    /*
     *      To add fragment to a tab.
     *  tag             ->  Tab identifier
     *  fragment        ->  Fragment to show, in tab identified by tag
     *  shouldAnimate   ->  should animate transaction. false when we switch tabs, or adding first fragment to a tab
     *                      true when when we are pushing more fragment into navigation stack.
     *  shouldAdd       ->  Should add to fragment navigation stack (mStacks.get(tag)). false when we are switching tabs (except for the first time)
     *                      true in all other cases.
     */
    public void pushFragments(String tag, Fragment fragment,boolean shouldAnimate, boolean shouldAdd){
        if(shouldAdd) {
            mStacks.get(tag).push(fragment);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if(shouldAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }


    public void popFragments(){
      /*
       *    Select the second last fragment in current tab's stack..
       *    which will be shown after the fragment transaction given below
       */
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);

      /*pop current fragment from stack.. */
        mStacks.get(mCurrentTab).pop();
        //clear global variable
        cleanGlobal();

      /* We have the target fragment in hand.. Just show it.. Show a standard navigation animation*/
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }

    public void popFragments(int n){
      /*
       *    Select the second last fragment in current tab's stack..
       *    which will be shown after the fragment transaction given below
       */
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - (n+1));

        for(int i=0; i<n; i++) { // pop n times
            /*pop current fragment from stack.. */
            mStacks.get(mCurrentTab).pop();
        }
        //clear global variable
        cleanGlobal();

      /* We have the target fragment in hand.. Just show it.. Show a standard navigation animation*/
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.realtabcontent, fragment);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        // We are already showing first fragment of current tab, so when back pressed, we will finish this activity..
        if(mStacks.get(mCurrentTab).size() == 1){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Do you want to log out?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return;
        }
        /* Goto previous fragment in navigation stack of this tab */
        popFragments();
    }

    /* Might be useful if we want to switch tab programmatically, from inside any of the fragment.*/
    public void setCurrentTab(int val){
        mTabHost.setCurrentTab(val);
    }

    //enable/disable back button from fragment
    public void enableBackButton(Boolean b){
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
        getSupportActionBar().setDisplayShowHomeEnabled(b);
    }
    // clean global variable for some fragments
    private void cleanGlobal(){
        if(mCurrentTab.matches(Global.TAB_A)){
            if(mStacks.get(mCurrentTab).size() == 1){
                Global.DATE_TIME = "";
                Global.ALLOW_MUL_PASSEN = 0;
                Global.SELECTED_TYPE = 0;
            }
        }
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
