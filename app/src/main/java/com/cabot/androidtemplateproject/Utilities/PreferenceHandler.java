package com.cabot.androidtemplateproject.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by neethu on 18/7/17.
 *
 * used the handle application's shared preference
 */

public class PreferenceHandler {

    private static final String LANGUAGE_KEY = "language";
    public static final String SHARED_PREF_NAME = "app_name";
    private static final String IS_INITIAL_LAUNCH_KEY = "initialLaunch";

    //Preference Methods
    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    //Set Preferences
    public static void setStringSharedPreference(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static void setLocale(Context context, String language) {
        setStringSharedPreference(context, LANGUAGE_KEY, language);
    }

    public static String getLocale(Context context) {
        SharedPreferences preferences = getPreference(context);
        return preferences.getString(LANGUAGE_KEY, "en");
    }

    //Get Preferences
    public static boolean isInitialLaunch(Context context) {
        SharedPreferences preferences = getPreference(context);
        return preferences.getBoolean(IS_INITIAL_LAUNCH_KEY, true);
    }
}
