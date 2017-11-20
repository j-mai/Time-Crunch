package com.example.jasminemai.timecrunch;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by jasminemai on 11/19/17.
 */

public class TimeCrunchApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
