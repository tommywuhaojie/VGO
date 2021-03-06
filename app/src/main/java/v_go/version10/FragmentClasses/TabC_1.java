package v_go.version10.FragmentClasses;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.CalendarHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hirondelle.date4j.DateTime;
import v_go.version10.ActivityClasses.Main;
import v_go.version10.ApiClasses.Trip;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;


@SuppressLint("SimpleDateFormat")
public class TabC_1 extends Fragment  {

    private CaldroidFragment caldroidFragment;
    final private JSONArray[] jsonArray = new JSONArray[1];
    private DateTime oldDate;
    private int oldInteger;
    private View view;
    private MenuItem notification_icon_menu_item;
    private TextView notifNumTextView;
    private boolean finishLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_c_1, container, false);

        caldroidFragment = new CaldroidFragment();

        oldDate = null;

        // change toolbar title
        getActivity().setTitle("Schedule");
        ((Main) getActivity()).enableBackButton(false);
        setHasOptionsMenu(true);

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // //////////////////////////////////////////////////////////////////////
        // **** This is to show customized fragment. If you want customized
        // version, uncomment below line ****
        // caldroidFragment = new CaldroidSampleCustomFragment();

        // Setup arguments

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
            CaldroidFragment.MONDAY); // Tuesday

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            //args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);
        }


        setCustomResourceForDates();

        final TableLayout tableLay = (TableLayout)view.findViewById(R.id.trip_table);
        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(final Date date, View view) {

                // set selected date
                setSelected(date);

                try {
                    // clean table
                    tableLay.removeAllViews();

                    if (jsonArray[0] == null) {
                        Toast.makeText(getActivity(), "Error:API return null object", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < jsonArray[0].length(); i++) {
                            JSONObject jsonObject = jsonArray[0].getJSONObject(i);
                            Date date1 = formatter.parse(jsonObject.getString("starting_date"));

                            //if same date
                            if(date1.compareTo(date) == 0){
                                View tableRow = getActivity().getLayoutInflater().inflate(R.layout.calendar_trip_info_row, null, false);
                                TextView addr_a = (TextView) tableRow.findViewById(R.id.addr_a);
                                TextView addr_b = (TextView) tableRow.findViewById(R.id.addr_b);
                                TextView start_date = (TextView) tableRow.findViewById(R.id.date);
                                TextView start_time = (TextView) tableRow.findViewById(R.id.date_time);
                                TextView duration = (TextView) tableRow.findViewById(R.id.duration);
                                TextView distance = (TextView) tableRow.findViewById(R.id.distance);
                                TextView type = (TextView) tableRow.findViewById(R.id.type);

                                Log.d("DEBUG", "trip_id: " + jsonObject.getString("trip_id"));

                                addr_a.setText(jsonObject.getString("starting"));
                                addr_b.setText(jsonObject.getString("destination"));
                                start_date.setText(jsonObject.getString("starting_date"));
                                start_time.setText("@ " + jsonObject.getString("starting_time"));
                                duration.setText("~ " + jsonObject.getString("est_time") + " min");
                                distance.setText("~ " + jsonObject.getString("est_distance") + " km");
                                if(jsonObject.getInt("driver_flag") == 0){
                                    type.setText("Passenger");
                                }else{
                                    type.setText("Driver");
                                }
                                if(tableLay.getChildCount()%2 == 0){
                                    tableRow.setBackgroundColor(Color.parseColor("#cccccc"));
                                }
                                tableLay.addView(tableRow);
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {

                /** clear up top disable dates' background **/
                // take care all possible cases
                int yearBefore, yearAfter, monthBefore, monthAfter;
                if(caldroidFragment.getMonth() == 1){
                    yearBefore = caldroidFragment.getYear() - 1;
                    yearAfter = caldroidFragment.getYear();
                    monthBefore = 11;
                    monthAfter = 1;
                }else if(caldroidFragment.getMonth() == 12){
                    yearBefore = caldroidFragment.getYear();
                    yearAfter = caldroidFragment.getYear() + 1;
                    monthBefore = 10;
                    monthAfter = 0;
                }else{
                    yearBefore = caldroidFragment.getYear();
                    yearAfter = caldroidFragment.getYear();
                    // note that caldroid month starts at 1 NOT 0, so we have to minus 1 below
                    monthBefore = caldroidFragment.getMonth() - 1 - 1;
                    monthAfter = caldroidFragment.getMonth() + 1 - 1;
                }

                // clear top disable dates' background
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yearBefore);
                cal.set(Calendar.MONTH, monthBefore);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i < maxDay; i++)
                {
                    cal.set(Calendar.DAY_OF_MONTH, i + 1);
                    caldroidFragment.clearBackgroundResourceForDate(cal.getTime());
                }
                // clear bottom disable dates' background
                Calendar cal2 = Calendar.getInstance();
                cal2.set(Calendar.YEAR, yearAfter);
                cal2.set(Calendar.MONTH, monthAfter);
                cal2.set(Calendar.DAY_OF_MONTH, 1);
                int maxDay2 = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 0; i < maxDay2; i++)
                {
                    cal2.set(Calendar.DAY_OF_MONTH, i + 1);
                    caldroidFragment.clearBackgroundResourceForDate(cal2.getTime());
                }

                // clean table on swiping to change month
                tableLay.removeAllViews();

                // retrieve trips of this month from server
                if(month < 10){
                    showTripsOnCalendar(year + "-0" + month);
                }else {
                    showTripsOnCalendar(year + "-" + month);
                }
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                // set selected date
                setSelected(date);

                // long click post trip
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabhost);
                                host.setCurrentTab(0);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setMessage("Do you want to post a new trip on "
                        + formatter.format(date)+" ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    //Toast.makeText(getActivity().getApplicationContext(),
                            //"Caldroid view is created", Toast.LENGTH_SHORT)
                            //.show();
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        /* Customize
        caldroidFragment.setMinDate(minDate);
        caldroidFragment.setMaxDate(maxDate);
        caldroidFragment.setDisableDates(disabledDates);
        caldroidFragment.setSelectedDates(fromDate, toDate);
        caldroidFragment.setShowNavigationArrows(false);
        caldroidFragment.setEnableSwipe(false);
        caldroidFragment.refreshView();
        */

        this.view = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calendar1, caldroidFragment);
        transaction.commit();
    }

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -7);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            //caldroidFragment.setBackgroundResourceForDate(R.color.blue, blueDate);
            //caldroidFragment.setBackgroundResourceForDate(R.color.green2, greenDate);
            //caldroidFragment.setTextColorForDate(R.color.white, blueDate);
            //caldroidFragment.setTextColorForDate(R.color.white, greenDate);
        }
    }

    private void showTripsOnCalendar(final String year_month){

        // show progress bar
        final ProgressBar pbHeaderProgress = (ProgressBar) view.findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        // set its height
        pbHeaderProgress.setScaleY(2f);

        // disable tab when loading trips
        ((Main)getActivity()).getTabWidget().setEnabled(false);
        // disable notification icon when loading trips
        finishLoading = false;


        Thread networkThread = new Thread(new Runnable(){
            @Override
            public void run() {
                    Trip trip = new Trip();
                    jsonArray[0] = trip.ThisMonthTrip(year_month);
                    if(jsonArray[0] == null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Error:API return null object", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        try {
                            // mark all the dates that contains trip(s)
                            int size = jsonArray[0].length();
                            for (int i = 0; i < size; i++) {
                                JSONObject jsonObject = jsonArray[0].getJSONObject(i);
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("starting_date"));
                                //caldroidFragment.setBackgroundResourceForDate(R.color.green2, date);
                                //caldroidFragment.setBackgroundResourceForDate(R.color.blue, date);
                                //caldroidFragment.setTextColorForDate(R.color.white, date);

                                // if the date contains a request , show "envelop icon"

                                /**if(((Main)getActivity()).isMatched(jsonObject.getInt("trip_id"))){
                                     caldroidFragment.setBackgroundResourceForDate(R.drawable.request_envelope, date);
                                     continue;
                                 }**/

                                // else show "star icon"
                                caldroidFragment.setBackgroundResourceForDate(R.drawable.star2, date);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                // refresh calendar in UiThread after all trips are fully retrieved
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        caldroidFragment.refreshView();
                        pbHeaderProgress.setVisibility(View.GONE);
                        // enable tabs & notification icon
                        ((Main) getActivity()).getTabWidget().setEnabled(true);
                        finishLoading = true;
                    }
                });
            }
        });
        networkThread.start();
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
        /*
        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
        */
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_calendar, menu);

        // setup red dot number for bell icon
        notification_icon_menu_item = menu.findItem(R.id.notification);
        MenuItemCompat.setActionView(notification_icon_menu_item, R.layout.new_notification_count);
        notifNumTextView = (TextView) notification_icon_menu_item.getActionView().findViewById(R.id.notif_num);


        /** Notifcation Icon clicked listener **/
        View icon_view = notification_icon_menu_item.getActionView();
        icon_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!finishLoading) {
                    return;
                }
                // dismiss number icon
                notifNumTextView.setVisibility(View.GONE);

                // update global display new notification number
                Global.DISPLAYED_NOTIF_NUM = 0;

                // bring up notification list page
                Fragment fragment = new TabC_2();
                /// fragment.setArguments(args);
                ((Main) getActivity()).pushFragments(Global.TAB_C, fragment, false, true);
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home) {
            //nothing
        }

        /**
        if(id == R.id.notification){
            notifNumTextView.setVisibility(View.GONE);
        }
        if(id == R.id.add_trip){
            ((Main) getActivity()).setCurrentTab(0);
        }
        **/

        return super.onOptionsItemSelected(item);
    }


    public void updateUi(){

        // update notification icon / "bell"
        int num = Global.DISPLAYED_NOTIF_NUM;
        if(num != 0) {
            if(num < 10) {
                notifNumTextView.setText(" " + String.valueOf(num) + " ");
            }else{
                notifNumTextView.setText(String.valueOf(num));
            }
        }else{
            notifNumTextView.setVisibility(View.GONE);
        }
        getActivity().invalidateOptionsMenu();

        //update calendar
        if(caldroidFragment.getMonth() < 10){
            showTripsOnCalendar(caldroidFragment.getYear() + "-0" + caldroidFragment.getMonth());
        }else {
            showTripsOnCalendar(caldroidFragment.getYear() + "-" + caldroidFragment.getMonth());
        }
    }


    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        int num = Global.DISPLAYED_NOTIF_NUM;
        if(num != 0) {
            if(num < 10) {
                notifNumTextView.setText(" " + String.valueOf(num) + " ");
            }else{
                notifNumTextView.setText(String.valueOf(num));
            }
        }else{
            notifNumTextView.setVisibility(View.GONE);
        }
    }

    private void setSelected(Date date){
        if(oldDate == null) {
            oldDate = CalendarHelper.convertDateToDateTime(date);
            if(caldroidFragment.getBackgroundForDateTimeMap().get(oldDate) == null) {
                oldInteger = 0;
            }else{
                oldInteger = caldroidFragment.getBackgroundForDateTimeMap().get(oldDate);
            }
            caldroidFragment.setBackgroundResourceForDate(R.drawable.red_border_orange, date);
        }else{

            if(oldInteger == 0){
                caldroidFragment.clearBackgroundResourceForDate(CalendarHelper.convertDateTimeToDate(oldDate));
            }else{
                caldroidFragment.setBackgroundResourceForDate(oldInteger, CalendarHelper.convertDateTimeToDate(oldDate));
            }
            oldDate = CalendarHelper.convertDateToDateTime(date);

            if(caldroidFragment.getBackgroundForDateTimeMap().get(oldDate) == null) {
                oldInteger = 0;
            }else{
                oldInteger = caldroidFragment.getBackgroundForDateTimeMap().get(oldDate);
            }
            caldroidFragment.setBackgroundResourceForDate(R.drawable.red_border_orange, date);
        }
        caldroidFragment.refreshView();
    }

}