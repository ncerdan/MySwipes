package com.nickcerdan.myswipes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView dateText;
    TextView quarterText;
    TextView mealPlanText;
    String mealPlanString;
    TextView swipesLeftText;
    Button swipeButton;
    Button settingsButton;
    SharedPreferences sharedPrefs;
    int swipesLeftNum;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //set swipesLeft
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", 158);
        swipesLeftText = findViewById(R.id.swipesLeft);
        swipesLeftText.setText(Integer.toString(swipesLeftNum));

        //set mealPlan
        mealPlanString = sharedPrefs.getString("mealPlan", "x.x");
        mealPlanText = findViewById(R.id.mealPlanText);
        mealPlanText.setText(mealPlanString);


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

        //check if user changed swipesLeft
        swipesLeftNum = sharedPrefs.getInt("swipesLeft", 158);
        String settingsValue = sharedPrefs.getString("setting_swipesLeft", "");

        if (!settingsValue.equals("") && swipesLeftNum != Integer.valueOf(settingsValue)) {
            swipesLeftNum = Integer.valueOf(settingsValue);
            sharedPrefs.edit().putInt("swipesLeft", swipesLeftNum).apply();

            swipesLeftText = findViewById(R.id.swipesLeft);
            swipesLeftText.setText(Integer.toString(swipesLeftNum));
        }

        //set mealPlan in case user changed in settings
        mealPlanString = sharedPrefs.getString("mealPlan", "14P");

        mealPlanText = findViewById(R.id.mealPlanText);
        mealPlanText.setText(mealPlanString);

        //set date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d");
        String dateString = formatter.format(currentDate);

        dateText = findViewById(R.id.dateText);
        dateText.setText(dateString);

        //set quarter
        Date endOfFall18 = new Date(2018, 12, 14);
        Date endOfWinter19 = new Date(2019, 3, 22);
        Date endOfSpring19 = new Date(2019, 6, 14);

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
    }

    //handles when user taps swipe button
    private void swipe() {
        swipesLeftNum--;
        sharedPrefs.edit().putInt("swipesLeft", swipesLeftNum).apply();
        sharedPrefs.edit().putString("setting_swipesLeft", Integer.toString(swipesLeftNum)).apply();

        swipesLeftText = findViewById(R.id.swipesLeft);
        swipesLeftText.setText(Integer.toString(swipesLeftNum));
    }
}
