package com.cabot.androidtemplateproject.application;

import android.app.Application;

import com.cabot.androidtemplateproject.Utilities.PreferenceHandler;

import java.util.Locale;

/**
 * Created by neethu on 18/7/17.
 */

public class AppApplication extends Application {

    public static String sDefSystemLanguage;
    private static AppApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        sDefSystemLanguage = Locale.getDefault().getLanguage();
        if (PreferenceHandler.getLocale(this) == "") {
            PreferenceHandler.setLocale(this, sDefSystemLanguage);
        } else {
            PreferenceHandler.setLocale(this, PreferenceHandler.getLocale(this));
        }
    }

}
