package com.nickcerdan.myswipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //widgets
    TextView dateText;
    TextView quarterText;
    TextView swipesUsedText;
    TextView mealPlanText;
    TextView swipesLeftText;
    TextView paceText;
    Button swipeButton;
    Button settingsButton;

    //vars
    String mealPlanString;
    SharedPreferences sharedPrefs;
    int swipesLeftNum;

    //constants
    final Date endOfFall18 = new Date(118, Calendar.DECEMBER, 14);
    final Date endOfWinter19 = new Date(119, Calendar.MARCH, 22);
    final Date endOfSpring19 = new Date(119, Calendar.JUNE, 14);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //set swipesLeft
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", -999);
            //if first time opening app
        if (swipesLeftNum == -999) {
            sharedPrefs.edit().putInt("swipesLeft", 158).apply();
            sharedPrefs.edit().putString("setting_swipesLeft", "158").apply();
            sharedPrefs.edit().putString("mealPlan", "14P").apply();
        }
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", 0);
        swipesLeftText = findViewById(R.id.swipesLeft);
        swipesLeftText.setText(Integer.toString(swipesLeftNum));

        //set date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d");
        String dateString = formatter.format(currentDate);

        dateText = findViewById(R.id.dateText);
        dateText.setText(dateString);

        //set quarter
        String quarterString;
        if (currentDate.compareTo(endOfFall18) <= 0) {
            quarterString = "Fall 18";
        } else if (currentDate.compareTo(endOfWinter19) <= 0) {
            quarterString = "Winter 19";
        } else if (currentDate.compareTo(endOfSpring19) <= 0) {
            quarterString = "Spring 19";
        } else {
            quarterString = "ERROR";
        }

        quarterText = findViewById(R.id.quarterText);
        quarterText.setText(quarterString);

        //initialize swipe button and listener
        swipeButton = findViewById(R.id.swipeBtn);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe();
            }
        });

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

        //check if user changed swipesLeft in settings
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", -999);
        String swipesSettingsValue = sharedPrefs.getString("setting_swipesLeft", "");

        if (swipesLeftNum != Integer.valueOf(swipesSettingsValue)) {
            swipesLeftNum = Integer.valueOf(swipesSettingsValue);
            sharedPrefs.edit().putInt("swipesLeft", swipesLeftNum).apply();

            swipesLeftText = findViewById(R.id.swipesLeft);
            swipesLeftText.setText(Integer.toString(swipesLeftNum));
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
        paceText = findViewById(R.id.pace);
        paceText.setText(Integer.toString(paceNum));
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
            if (currentDate.compareTo(endOfFall18) <= 0) {
                endOfCycle = endOfFall18;
            } else if (currentDate.compareTo(endOfWinter19) <= 0) {
                endOfCycle = endOfWinter19;
            } else if (currentDate.compareTo(endOfSpring19) <= 0) {
                endOfCycle = endOfSpring19;
            } else {
                endOfCycle = new Date(119, 8, 20);
            }

            long diff = endOfCycle.getTime() - currentDate.getTime();
            daysLeft = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
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
                //14R and 14P both gives 2 swipes per day left
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
                //figure out how to calculate bc varies by day
                return 0;
            //will never get called
            default:
                return 0;
        }
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
        swipesLeftText.setText(Integer.toString(swipesLeftNum));
        setSwipesUsed();
        setPace();
    }

    //handles when it should calculate how many swipes have been used
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
        swipesUsedText.setText(Integer.toString(swipesUsedNum));
    }
}
