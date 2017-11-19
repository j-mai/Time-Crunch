package com.example.jasminemai.timecrunch;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasminemai on 11/19/17.
 */

public class TimeFunctions {

    private static String GENERIC_TODAY = "2017-11-18";
    private static String GENERIC_TOMORROW = "2017-11-19";
    private static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static int MIN_SLEEP_MINUTES = 360;

    public static Boolean bedTimeCalc (String wakeTime, String sleepTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.US);
        wakeTime = wakeTime.replaceAll("\\s", "");
        sleepTime = sleepTime.replaceAll("\\s", "");

        String newWakeTime = GENERIC_TODAY + " " + wakeTime;
        String newSleepTime = GENERIC_TODAY + " " + sleepTime;

        try {
            Date wake = timeFormat.parse(newWakeTime);
            Date sleep = timeFormat.parse(newSleepTime);

            Log.d("wake", wake.toString());
            Log.d("sleep", sleep.toString());

            if (wake.getTime() < sleep.getTime()) {

                newWakeTime = GENERIC_TOMORROW + " " + wakeTime;
                Log.d("newWakeTime", newWakeTime);
                wake = timeFormat.parse(newWakeTime);

                long diff = wake.getTime() - sleep.getTime();

                long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(diff));

                Log.d("BedTimeCheck", "minutes are: " + minutes);

                if (minutes < MIN_SLEEP_MINUTES) {
                    return false;
                } else {
                    return true;
                }

            } else {
                long diff = sleep.getTime() - wake.getTime();

                long minutes = Math.abs(TimeUnit.MILLISECONDS.toMinutes(diff));

                Log.d("BedTimeCheck", "minutes are: " + minutes);

                if (minutes < MIN_SLEEP_MINUTES) {
                    return false;
                } else {
                    return true;
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }
}
