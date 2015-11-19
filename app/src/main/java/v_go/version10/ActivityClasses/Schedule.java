package v_go.version10.ActivityClasses;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CalendarView;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import v_go.version10.R;

public class Schedule extends AppCompatActivity {

    private ImageView topImg;
    private ImageView bottomImg;
    private View linearLayout;
    private CalendarView calendarView;
    private View root;
    private int screenHeight;
    private int calendarHeight;
    private int rowHeight;
    private int first;
    private int second;
    private int third;
    private int fourth;
    private int fifth;
    private float touchY;
    private Long currentDate;
    private int debug;
    private int currentLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //Back button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        topImg = (ImageView) findViewById(R.id.top);
        bottomImg = (ImageView) findViewById(R.id.bottom);
        linearLayout = findViewById(R.id.linearLayout);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        initializeCalendar();

        topImg.setVisibility(View.GONE);
        bottomImg.setVisibility(View.GONE);

        root = getWindow().getDecorView().findViewById(android.R.id.content);
        root.setDrawingCacheEnabled(true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int height = displaymetrics.heightPixels;

        ViewTreeObserver vto = linearLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                // Calculation of the height of each row
                final int NUM_OF_ROWS = 7;
                final int TWO = 2;

                screenHeight = height;
                calendarHeight = calendarView.getHeight();
                rowHeight = calendarHeight / NUM_OF_ROWS;
                first = screenHeight - calendarHeight + (rowHeight * TWO);
                Log.d("DEBUG", screenHeight + "/" + calendarHeight + "/" + rowHeight + "/" + first + "");
                second = first + rowHeight;
                third = second + rowHeight;
                fourth = third + rowHeight;
                fifth = fourth + rowHeight;

                currentDate = calendarView.getDate();
                //---------------------------------

                linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchY = event.getRawY();
        }
        return super.dispatchTouchEvent(event);
    }

    public void initializeCalendar() {

        calendarView.setShowWeekNumber(false);
        //calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        /*
        @attr ref android.R.styleable#CalendarView_showWeekNumber
        @attr ref android.R.styleable#CalendarView_firstDayOfWeek
        @attr ref android.R.styleable#CalendarView_minDate
        @attr ref android.R.styleable#CalendarView_maxDate
        @attr ref android.R.styleable#CalendarView_shownWeekCount
        @attr ref android.R.styleable#CalendarView_selectedWeekBackgroundColor
        @attr ref android.R.styleable#CalendarView_focusedMonthDateColor
        @attr ref android.R.styleable#CalendarView_unfocusedMonthDateColor
        @attr ref android.R.styleable#CalendarView_weekNumberColor
        @attr ref android.R.styleable#CalendarView_weekSeparatorLineColor
        @attr ref android.R.styleable#CalendarView_selectedDateVerticalBar
        @attr ref android.R.styleable#CalendarView_weekDayTextAppearance
        @attr ref android.R.styleable#CalendarView_dateTextAppearance
        */

        //calendarView.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));
        //calendarView.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));
        //calendarView.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));
        //calendarView.setSelectedDateVerticalBar(R.color.darkgreen);

        calendarView.setFocusedMonthDateColor(Color.WHITE);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                if (calendarView.getDate() != currentDate) {

                    currentDate = calendarView.getDate();

                    month = month + 1; // month increments by one because month is list as 0~11

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();

                    try {
                        String dateString = year + "-" + month + "-" + day;
                        date = simpleDateFormat.parse(dateString);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int dayOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

                    Log.d("DEBUG", dayOfMonth + "");

                    // split screen (pop up window)
                    if (touchY < first) {
                        currentLayer = 1;
                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.destroyDrawingCache();
                                        Bitmap bmp = root.getDrawingCache();

                                        int splitYCoord = first - rowHeight;
                                        Bitmap mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
                                        Bitmap mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);

                                        topImg.setImageBitmap(mBmp1);
                                        bottomImg.setImageBitmap(mBmp2);
                                        topImg.setVisibility(View.VISIBLE);
                                        bottomImg.setVisibility(View.VISIBLE);

                                        ObjectAnimator transAnimationB = ObjectAnimator.ofFloat(bottomImg, "translationY", 0f, 0.2f * screenHeight);
                                        transAnimationB.setDuration(250);//set duration
                                        transAnimationB.start();//start animation

                                        //HIDE BACKGROUND
                                        linearLayout.setBackgroundColor(Color.WHITE);
                                        calendarView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };
                        thread.start();

                        calendarView.dispatchSetSelected(false);


                    }
                    if (touchY >= first && touchY < second) {
                        currentLayer = 2;

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.destroyDrawingCache();
                                        Bitmap bmp = root.getDrawingCache();

                                        int splitYCoord = first;
                                        Bitmap mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
                                        Bitmap mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);

                                        topImg.setImageBitmap(mBmp1);
                                        bottomImg.setImageBitmap(mBmp2);
                                        topImg.setVisibility(View.VISIBLE);
                                        bottomImg.setVisibility(View.VISIBLE);

                                        ObjectAnimator transAnimationB = ObjectAnimator.ofFloat(bottomImg, "translationY", 0f, 0.2f * screenHeight);
                                        transAnimationB.setDuration(250);//set duration
                                        transAnimationB.start();//start animation

                                        //HIDE BACKGROUND
                                        linearLayout.setBackgroundColor(Color.WHITE);
                                        calendarView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };
                        thread.start();

                    }
                    if (touchY >= second && touchY < third) {
                        currentLayer = 3;

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.destroyDrawingCache();
                                        Bitmap bmp = root.getDrawingCache();

                                        int splitYCoord = second;
                                        Bitmap mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
                                        Bitmap mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);

                                        topImg.setImageBitmap(mBmp1);
                                        bottomImg.setImageBitmap(mBmp2);
                                        topImg.setVisibility(View.VISIBLE);
                                        bottomImg.setVisibility(View.VISIBLE);

                                        ObjectAnimator transAnimationB = ObjectAnimator.ofFloat(bottomImg, "translationY", 0f, 0.2f * screenHeight);
                                        transAnimationB.setDuration(250);//set duration
                                        transAnimationB.start();//start animation

                                        //HIDE BACKGROUND
                                        linearLayout.setBackgroundColor(Color.WHITE);
                                        calendarView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };
                        thread.start();

                    }
                    if (touchY >= third && touchY < fourth) {
                        currentLayer = 4;

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.destroyDrawingCache();
                                        Bitmap bmp = root.getDrawingCache();

                                        int splitYCoord = third;
                                        Bitmap mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
                                        Bitmap mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);

                                        topImg.setImageBitmap(mBmp1);
                                        bottomImg.setImageBitmap(mBmp2);
                                        topImg.setVisibility(View.VISIBLE);
                                        bottomImg.setVisibility(View.VISIBLE);

                                        ObjectAnimator transAnimationB = ObjectAnimator.ofFloat(bottomImg, "translationY", 0f, 0.2f * screenHeight);
                                        transAnimationB.setDuration(250);//set duration
                                        transAnimationB.start();//start animation

                                        //HIDE BACKGROUND
                                        linearLayout.setBackgroundColor(Color.WHITE);
                                        calendarView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };
                        thread.start();

                    }
                    if (touchY >= fourth && touchY < fifth) {
                        currentLayer = 5;

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.destroyDrawingCache();
                                        Bitmap bmp = root.getDrawingCache();

                                        int splitYCoord = fourth;
                                        Bitmap mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
                                        Bitmap mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);

                                        topImg.setImageBitmap(mBmp1);
                                        bottomImg.setImageBitmap(mBmp2);
                                        topImg.setVisibility(View.VISIBLE);
                                        bottomImg.setVisibility(View.VISIBLE);

                                        ObjectAnimator transAnimationB = ObjectAnimator.ofFloat(topImg, "translationY", 0f, -0.2f * screenHeight);
                                        transAnimationB.setDuration(250);//set duration
                                        transAnimationB.start();//start animation

                                        //HIDE BACKGROUND
                                        linearLayout.setBackgroundColor(Color.WHITE);
                                        calendarView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };
                        thread.start();

                    }
                    if (touchY >= fifth) {
                        currentLayer = 6;

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        root.destroyDrawingCache();
                                        Bitmap bmp = root.getDrawingCache();

                                        int splitYCoord = fourth;
                                        Bitmap mBmp1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), splitYCoord);
                                        Bitmap mBmp2 = Bitmap.createBitmap(bmp, 0, splitYCoord, bmp.getWidth(), bmp.getHeight() - splitYCoord);

                                        topImg.setImageBitmap(mBmp1);
                                        bottomImg.setImageBitmap(mBmp2);
                                        topImg.setVisibility(View.VISIBLE);
                                        bottomImg.setVisibility(View.VISIBLE);

                                        ObjectAnimator transAnimationB = ObjectAnimator.ofFloat(topImg, "translationY", 0f, -0.2f * screenHeight);
                                        transAnimationB.setDuration(250);//set duration
                                        transAnimationB.start();//start animation

                                        //HIDE BACKGROUND
                                        linearLayout.setBackgroundColor(Color.WHITE);
                                        calendarView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };
                        thread.start();

                    }
                }
            }

        });
    }

    // TOP PARK IS CLICKED
    public void topIsClicked(View view){

        float topF = 0;
        float bottomF = 0;
        switch (currentLayer) {
            case 1:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 2:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 3:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 4:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 5:
                topF = -0.2f;
                bottomF = 0;
                break;
            case 6:
                topF = -0.2f;
                bottomF = 0;
                break;
            default:
                break;
        }

        // ANIMATION OF CLOSE
        ObjectAnimator transAnimationT= ObjectAnimator.ofFloat(topImg, "translationY", topF * screenHeight, 0f);
        transAnimationT.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setBackgroundColor(Color.parseColor("#6495ED"));
                calendarView.setVisibility(View.VISIBLE);
                topImg.clearAnimation();
                bottomImg.clearAnimation();
                topImg.setVisibility(View.GONE);
                bottomImg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        transAnimationT.setDuration(250);//set duration
        transAnimationT.start();//start animation

        ObjectAnimator transAnimationB= ObjectAnimator.ofFloat(bottomImg, "translationY", bottomF * screenHeight, 0f);
        transAnimationB.setDuration(250);//set duration
        transAnimationB.start();//start animation
    }

    // BOTTOM PART IS CLICKED
    public void bottomIsClicked(View view){

        float topF = 0;
        float bottomF = 0;
        switch (currentLayer) {
            case 1:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 2:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 3:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 4:
                topF = 0;
                bottomF = 0.2f;
                break;
            case 5:
                topF = -0.2f;
                bottomF = 0;
                break;
            case 6:
                topF = -0.2f;
                bottomF = 0;
                break;
            default:
                break;
        }

        // ANIMATION OF CLOSE
        ObjectAnimator transAnimationT= ObjectAnimator.ofFloat(topImg, "translationY", topF * screenHeight, 0f);
        transAnimationT.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setBackgroundColor(Color.parseColor("#6495ED"));
                calendarView.setVisibility(View.VISIBLE);
                topImg.clearAnimation();
                bottomImg.clearAnimation();
                topImg.setVisibility(View.GONE);
                bottomImg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        transAnimationT.setDuration(250);//set duration
        transAnimationT.start();//start animation

        ObjectAnimator transAnimationB= ObjectAnimator.ofFloat(bottomImg, "translationY", bottomF * screenHeight, 0f);
        transAnimationB.setDuration(250);//set duration
        transAnimationB.start();//start animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
