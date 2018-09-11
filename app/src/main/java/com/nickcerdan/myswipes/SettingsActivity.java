package com.nickcerdan.myswipes;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    //settings fragment
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);

            final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            //if current swipesLeft > new mealPlan's max, set swipesLeft to new mealPlan's max
            ListPreference mealPlanInput = (ListPreference) getPreferenceScreen().findPreference("mealPlan");
            mealPlanInput.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newMealPlanString = (String) newValue;
                    int newMealPlanNum = getMealPlanNum(newMealPlanString);

                    String currentSwipesLeft = sharedPrefs.getString("setting_swipesLeft", "");
                    if (Integer.valueOf(currentSwipesLeft) > newMealPlanNum) {
                        sharedPrefs.edit().putString("setting_swipesLeft", Integer.toString(newMealPlanNum)).apply();
                    }
                    return true;
                }
            });

            //validate swipes left input is > 0, < max number for selected meal plan
            EditTextPreference swipesLeftInput = (EditTextPreference) getPreferenceScreen().findPreference("setting_swipesLeft");
            swipesLeftInput.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int input = Integer.valueOf((String) newValue);
                    String mealPlanString = sharedPrefs.getString("mealPlan", "");
                    int mealPlanNum = getMealPlanNum(mealPlanString);
                    
                    if (input >= 0 && input <= mealPlanNum) {
                        return true;
                    } else {
                        String msg;
                        if (input < 0) {
                            msg = "You cannot input a number less than 0!";
                        } else {
                            msg = "Your current meal plan is " + mealPlanString + ", so you can't have more than " + mealPlanNum + " swipes left.";
                        }
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            });
        }

        //returns number associated with mealPlanString
        private int getMealPlanNum(String mealPlanString) {
            switch (mealPlanString) {
                case "11R":
                    return 11;
                case "14R":
                    return 14;
                case "19R":
                    return 19;
                case "14P":
                    return 158;
                case "19P":
                    return 214;
                default:
                    return -999;
            }
        }
    }
}
