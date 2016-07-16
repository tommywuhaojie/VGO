package v_go.version10.ActivityClasses;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.HashMap;
import java.util.Stack;

import v_go.version10.ApiClasses.User;
import v_go.version10.FragmentClasses.TabA_1;
import v_go.version10.FragmentClasses.TabB_1;
import v_go.version10.FragmentClasses.TabC_1_new;
import v_go.version10.FragmentClasses.TabC_2;
import v_go.version10.FragmentClasses.TabD_1;
import v_go.version10.HelperClasses.Global;
import v_go.version10.HelperClasses.Notification;
import v_go.version10.HelperClasses.UserCache;
import v_go.version10.PersistentCookieStore.SiCookieStore2;
import v_go.version10.R;
import v_go.version10.HelperClasses.BackgroundService;

public class Main extends AppCompatActivity{

    /* Your Tab host */
    private TabHost mTabHost;
    /* A HashMap of stacks, where we use tab identifier as keys..*/
    private HashMap<String, Stack<Fragment>> mStacks;
    /*Save current tabs identifier in this..*/
    private String mCurrentTab;
    // boolean var for switching tab delay
    private boolean allow = true;
    // top object of this stack equals to top item of the listView
    private Stack<Notification> notifStack;

    // unselected and selected tab resource ids
    private final int [] TAB_RESOURCE_ID_UNSELECTED = {R.drawable.tab1_grey, R.drawable.tab2_grey, R.drawable.tab3_grey, R.drawable.tab4_grey};
    private final int [] TAB_RESOURCE_ID_SELECTED = {R.drawable.tab1_selected, R.drawable.tab2_selected, R.drawable.tab3_selected, R.drawable.tab4_selected};
    private final String [] TAB_COLOR = {"#1B5F5F", "#50B9AC", "#DA4431", "#35332E"};
    private final String [] ACTIONBAR_TITLE = {"TRIP", "NOTIFICATION", "SOCIAL", "ME"};

    // previously selected tab id
    private int visitedTab = 0;

    // cached object to store current user's infomation
    private UserCache userCache = new UserCache();

    // to receive notification from background service
    private BroadcastReceiver broadcastReceiver;

    private static SharedPreferences sp;
    public static SharedPreferences getSharedPreferences(){
        return sp;
    }
    public UserCache getUserCache(){
        return userCache;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG", "Main onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DEBUG", "Main onPause");

        // pause long polling
        //stopService(new Intent(getBaseContext(), BackgroundService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG", "Main onDestroy");
    }

    public void setTabHostVisibility(Boolean visible){
        if(visible) {
            mTabHost.getTabWidget().setVisibility(View.VISIBLE);
        }else{
            mTabHost.getTabWidget().setVisibility(View.GONE);
        }

    }

    public void downloadCurrentUserAvatar(){
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = User.DownloadAvatar();
                if (bitmap != null) {
                    Bitmap circleBitmap = Global.getCircularBitmap(bitmap);
                    userCache.setAvatar(circleBitmap);
                    Global.my_avatar = circleBitmap;
                }
            }
        });
        networkThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize SharePreferences for local cookie store
        sp = getSharedPreferences(SiCookieStore2.COOKIE_PREFS, 0);

        // start socket io background service
        startService(new Intent(this, BackgroundService.class));

        // download necessary user information in a different thread: avatar, name, etc...
        downloadCurrentUserAvatar();
        // TO DO: download user info

        // lists initialization
        notifStack = new Stack<>();

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

        // setup action bar
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.custom_actionbar_layout);

        // init tabs + design
        initializeTabs();

        // prevent dialogs from closing by outside click
        setFinishOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // initialize the chat first
        //mTabHost.setCurrentTab(2);
        //Log.d("DEBUG", "set current tab");
    }

    public Stack<Notification> getNotifStack(){
        return notifStack;
    }

    public void setActionbarTitle(String title){
        //((TextView)getSupportActionBar().getCustomView().findViewById(R.id.action_bar_title)).setText(title);
        getSupportActionBar().setTitle(title);
    }

    public void initializeTabs(){
        // set Dividers
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        mTabHost.getTabWidget().setDividerPadding(0);

        /* Setup your tab icons and content views.. Nothing special in this..*/
        // tab1
        TabHost.TabSpec spec    =   mTabHost.newTabSpec(Global.TAB_A);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, TAB_RESOURCE_ID_UNSELECTED[0]));
        mTabHost.addTab(spec);

        // tab2
        spec = mTabHost.newTabSpec(Global.TAB_B);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, TAB_RESOURCE_ID_UNSELECTED[1]));
        mTabHost.addTab(spec);

        // tab3
        spec = mTabHost.newTabSpec(Global.TAB_C);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, TAB_RESOURCE_ID_UNSELECTED[2]));
        mTabHost.addTab(spec);

        // tab4
        spec = mTabHost.newTabSpec(Global.TAB_D);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        spec.setIndicator("", ContextCompat.getDrawable(this, TAB_RESOURCE_ID_UNSELECTED[3]));
        mTabHost.addTab(spec);

        // make the icon fill the entire tab
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++)
        {
            mTabHost.getTabWidget().getChildAt(i).setPadding(0,0,0,0);
        }

        // default tab
        mTabHost.setCurrentTab(0);
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
                    pushFragments(tabId, new TabC_1_new(), false,true);
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

            int currTab = mTabHost.getCurrentTab();

            // grey out the old one
            ImageView image = (ImageView) mTabHost.getTabWidget().getChildAt(visitedTab).findViewById(android.R.id.icon);
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), TAB_RESOURCE_ID_UNSELECTED[visitedTab], null));
            // change the icon for new one
            image = (ImageView) mTabHost.getTabWidget().getChildAt(currTab).findViewById(android.R.id.icon);
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), TAB_RESOURCE_ID_SELECTED[currTab], null));

            visitedTab = currTab;

            // change action bar to match the theme color
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(TAB_COLOR[currTab])));

            // change action bar title
            setActionbarTitle(ACTIONBAR_TITLE[currTab]);

            // if there is a red dot, dismiss it after switch to 3rd tab
            if(tabId.equals(Global.TAB_C) && Global.TAB3_NOTIFICATION){
                if(Global.TAB3_NOTIFICATION){
                    ImageView mImageView = (ImageView) mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.icon);
                    mImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tab3_b, null));
                    Global.TAB3_NOTIFICATION = false;
                }
            }
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

    public void pushFragmentsWithUpDownAnim(String tag, Fragment fragment, boolean shouldAdd){
        if(shouldAdd) {
            mStacks.get(tag).push(fragment);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);

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

        // different animation for different page
        if((mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 1)) instanceof TabC_2) {
            //ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }else {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
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

    public Fragment getCurrentFragment(){
        Fragment currentFragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 1);
        return  currentFragment;
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
                            moveTaskToBack(true);
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
            builder.setMessage("Go back to home screen?").setPositiveButton("Yes", dialogClickListener)
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

    public TabWidget getTabWidget(){
        return mTabHost.getTabWidget();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}

/** Programmatic Way to Get Hash Code **
 try {
 PackageInfo info = getPackageManager().getPackageInfo(
 "v_go.version10",
 PackageManager.GET_SIGNATURES);
 for (Signature signature : info.signatures) {
 MessageDigest md = MessageDigest.getInstance("SHA");
 md.update(signature.toByteArray());
 Log.d("DEBUG", Base64.encodeToString(md.digest(), Base64.DEFAULT));
 }
 } catch (PackageManager.NameNotFoundException e) {
 } catch (NoSuchAlgorithmException e) {}
 **/
