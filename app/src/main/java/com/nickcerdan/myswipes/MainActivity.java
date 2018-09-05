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
    Button swipeButton;
    Button settingsButton;
    int swipes;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize buttons and set click listeners
        swipeButton = (Button) findViewById(R.id.swipeBtn);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe();
            }
        });

        settingsButton = (Button) findViewById(R.id.settingsBtn);
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

        //meal plan
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mealPlanString = prefs.getString("setting_mealPlan", "");

        mealPlanText = (TextView) findViewById(R.id.mealPlanText);
        mealPlanText.setText(mealPlanString);

        //set date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d");
        String dateString = formatter.format(currentDate);

        dateText = (TextView) findViewById(R.id.dateText);
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

        quarterText = (TextView) findViewById(R.id.quarterText);
        quarterText.setText(quarterString);
    }

    //handles when user taps swipe button
    private void swipe() {

    }
}
