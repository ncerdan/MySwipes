package com.nickcerdan.myswipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //widgets
    TextView dateText;
    TextView quarterText;
    TextView swipesUsedText;
    TextView mealPlanText;
    TextView swipesLeftText;
    TextView paceText;
    View swipeButton;
    Button settingsButton;

    //vars
    String mealPlanString;
    SharedPreferences sharedPrefs;
    int swipesLeftNum;
    boolean isSwiping = false;

    //constants
    final Date ENDFALL18 = new Date(118, Calendar.DECEMBER, 14);
    final Date ENDWINTER19 = new Date(119, Calendar.MARCH, 22);
    final Date ENDSPRING19 = new Date(119, Calendar.JUNE, 14);

    //handles touching events on swipe button
    private void addSwipeTouchListener() {
        swipeButton = (View) findViewById(R.id.swipeBtn);
        swipeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int index = event.getActionIndex();
                int action = event.getActionMasked();
                int pointerID = event.getPointerId(index);

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isSwiping = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isSwiping) {
                            Log.d("swipe", "moving swipe card");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isSwiping /* and is over by edge of screen*/) {
                            Log.d("swipe", "successful swipe");
                            swipe();
                        }
                    case MotionEvent.ACTION_CANCEL:
                        isSwiping = false;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //set swipesLeft
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", -999);
        //if first time opening app, default to 14P
        if (swipesLeftNum == -999) {
            sharedPrefs.edit().putInt("swipesLeft", 158).apply();
            sharedPrefs.edit().putString("setting_swipesLeft", "158").apply();
            sharedPrefs.edit().putString("mealPlan", "14P").apply();
        }
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", 0);
        swipesLeftText = findViewById(R.id.swipesLeft);
        swipesLeftText.setText(String.format(Locale.US, "%d",swipesLeftNum));

        //redundant if also in onResume()?
        //setDateAndQuarter();

        //set touch listener from swipe button
        addSwipeTouchListener();

        //initialize settings button and listener
        settingsButton = findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //set date and quarter. in case its changed since last open
        setDateAndQuarter();

        //check if user changed swipesLeft in settings
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", -999);
        String swipesSettingsValue = sharedPrefs.getString("setting_swipesLeft", "");

        if (swipesLeftNum != Integer.valueOf(swipesSettingsValue)) {
            swipesLeftNum = Integer.valueOf(swipesSettingsValue);
            sharedPrefs.edit().putInt("swipesLeft", swipesLeftNum).apply();

            swipesLeftText = findViewById(R.id.swipesLeft);
            swipesLeftText.setText(String.format(Locale.US, "%d", swipesLeftNum));
        }

        //set mealPlan. here in case user changed in settings
        mealPlanString = sharedPrefs.getString("mealPlan", "");
        mealPlanText = findViewById(R.id.mealPlanText);
        mealPlanText.setText(mealPlanString);

        //set swipesUsed. here in case user changes swipesLeft in settings
        setSwipesUsed();

        //set pace. here in case user changes swipesLeft in settings
        setPace();
    }

    //calculates then sets pace
    private void setPace() {
        int paceNum = calculatePace();
        String paceString;
        if (paceNum > 0) {
            paceString = String.format(Locale.US, "+%d", paceNum);
        } else {
            paceString = String.format(Locale.US, "%d", paceNum);
        }
        paceText = findViewById(R.id.pace);
        paceText.setText(paceString);
    }

    //calculates pace value
    private int calculatePace() {
        Date currentDate = Calendar.getInstance().getTime();
        int daysLeft;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mealPlan = sharedPrefs.getString("mealPlan", "");

        if (mealPlan.equals("14P") || mealPlan.equals("19P")) {
            //calculates days left to end of quarter
            Date endOfCycle;
            if (currentDate.compareTo(ENDFALL18) <= 0) {
                endOfCycle = ENDFALL18;
            } else if (currentDate.compareTo(ENDWINTER19) <= 0) {
                endOfCycle = ENDWINTER19;
            } else if (currentDate.compareTo(ENDSPRING19) <= 0) {
                endOfCycle = ENDSPRING19;
            } else {
                endOfCycle = new Date(119, Calendar.SEPTEMBER, 20);
            }

            long diff = endOfCycle.getTime() - currentDate.getTime();
            daysLeft = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            daysLeft += 1; //to include last day

            //CHECK SETTINGS FOR PLANNED DAYS WITH NO SWIPING **HERE**

        } else {
            //calculates days left to end of current week
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            //if it's sunday, set days left to 0
            if (dayOfWeek == 1)
                daysLeft = 0;
            //otherwise, set days left to days until sunday
            else
                daysLeft = 8 - dayOfWeek;
        }

        //calculate correct number of swipes left
        int correctSwipesLeft = calculateCorrectSwipesLeft(daysLeft, mealPlan);

        //return difference of actual vs correct
        return swipesLeftNum - correctSwipesLeft;
    }

    //calculates correct number of swipesLeft depending on number of days left
    int calculateCorrectSwipesLeft(int daysLeft, String mealPlan) {
        switch (mealPlan) {
            case "11R":
                switch (daysLeft) {
                    //sunday
                    case 0:
                        return 0;
                    //saturday
                    case 1:
                        return 1;
                    //friday
                    case 2:
                        return 3;
                    //thursday
                    case 3:
                        return 5;
                    //wednesday
                    case 4:
                        return 6;
                    //tuesday
                    case 5:
                        return 8;
                    //monday
                    case 6:
                        return 9;
                    //will never get called
                    default:
                        return 0;
                }
            case "14R":
            case "14P":
                //both have 2 swipes per day left
                return 2 * daysLeft;
            case "19R":
                switch (daysLeft) {
                    //sunday
                    case 0:
                        return 0;
                    //saturday
                    case 1:
                        return 2;
                    //friday
                    case 2:
                        return 4;
                    //thursday
                    case 3:
                        return 7;
                    //wednesday
                    case 4:
                        return 10;
                    //tuesday
                    case 5:
                        return 13;
                    //monday
                    case 6:
                        return 16;
                    //will never get called
                    default:
                        return 0;
                }
            case "19P":
                //CALCULATE WEEK,DAY PAIR
                return calculate19P(daysLeft);

            //will never get called
            default:
                return 0;
        }
    }

    //calculates correct amount of swipes left for 19P meal plan
    private int calculate19P(int daysLeft) {
        //calculate number of weeks left and what day it is in week
        int weeksLeft = daysLeft / 7;
        int daysLeftInWeek = daysLeft % 7;

        //user should have 19 swipes for each week left,
        //plus certain amount based on day of week
        int res = 19 * weeksLeft;
        switch (daysLeftInWeek){
            //sunday
            case 0:
                return res;
            //saturday
            case 1:
                return res + 2;
            //friday
            case 2:
                return res + 4;
            //thursday
            case 3:
                return res + 7;
            //wednesday
            case 4:
                return res + 10;
            //tuesday
            case 5:
                return res + 13;
            //monday
            case 6:
                return res + 16;
            //will never get called
            default:
                return 0;
        }
    }

    //calculates date and quarter
    private void setDateAndQuarter() {
        //set date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d", Locale.US);
        String dateString = formatter.format(currentDate);

        dateText = findViewById(R.id.dateText);
        dateText.setText(dateString);

        //set quarter
        String quarterString;
        if (currentDate.compareTo(ENDFALL18) <= 0) {
            quarterString = "Fall 18";
        } else if (currentDate.compareTo(ENDWINTER19) <= 0) {
            quarterString = "Winter 19";
        } else if (currentDate.compareTo(ENDSPRING19) <= 0) {
            quarterString = "Spring 19";
        } else {
            quarterString = "ERROR";
        }

        quarterText = findViewById(R.id.quarterText);
        quarterText.setText(quarterString);
    }

    //handles when user taps swipe button
    private void swipe() {
        if (swipesLeftNum <= 0) {
            Toast.makeText(this, "You have no more swipes!", Toast.LENGTH_SHORT).show();
            return;
        }

        swipesLeftNum--;
        sharedPrefs.edit().putInt("swipesLeft", swipesLeftNum).apply();
        sharedPrefs.edit().putString("setting_swipesLeft", Integer.toString(swipesLeftNum)).apply();

        swipesLeftText = findViewById(R.id.swipesLeft);
        swipesLeftText.setText(String.format(Locale.US, "%d", swipesLeftNum));
        setSwipesUsed();
        setPace();
    }

    //handles calculating how many swipes have been used
    private void setSwipesUsed() {
        int mealPlanNum;
        String mealPlanString = sharedPrefs.getString("mealPlan", "");
        switch (mealPlanString) {
            case "11R":
                mealPlanNum = 11;
                break;
            case "14R":
                mealPlanNum = 14;
                break;
            case "19R":
                mealPlanNum = 19;
                break;
            case "14P":
                mealPlanNum = 158;
                break;
            case "19P":
                mealPlanNum = 214;
                break;
            default:
                mealPlanNum = -999;
                break;
        }
        int swipesUsedNum = mealPlanNum - swipesLeftNum;
        swipesUsedText = findViewById(R.id.swipesUsed);
        swipesUsedText.setText(String.format(Locale.US, "%d", swipesUsedNum));
    }
}
