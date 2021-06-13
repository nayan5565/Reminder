package com.shadhinlab.reminder.tools;

import android.content.Context;


import com.shadhinlab.reminder.R;

import java.util.Random;

/**
 * After some time of using the app, dismiss button is displayed with different names.
 */

public class DismissButtonNameGiver {
    private final int DAYS_WITH_DEFAULT_NAME_NUMBER = 7;
    private String[] possibleNames;
    private Context context;

    public DismissButtonNameGiver(Context context) {
        this.context = context;
        possibleNames = context.getResources().getStringArray(R.array.dismiss_button_names_array);
    }

    public String getName() {
        SharedPreferencesHelper sharPrefHelper = new SharedPreferencesHelper(context);
        if (sharPrefHelper.getDaysSinceInstallation() < DAYS_WITH_DEFAULT_NAME_NUMBER) {
            return possibleNames[0];
        } else {
            return getRandomName();
        }
    }

    public String getRandomName() {
        Random random = new Random();
        return possibleNames[random.nextInt(possibleNames.length)];
    }


}