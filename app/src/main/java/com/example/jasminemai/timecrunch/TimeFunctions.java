package com.example.jasminemai.timecrunch;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.TimePeriod;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasminemai on 11/19/17.
 */

public class TimeFunctions {

    private static String GENERIC_TODAY = "2017-11-18";
    private static String GENERIC_TOMORROW = "2017-11-19";
    private static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static int MIN_SLEEP_MINUTES = 360;

    public static Boolean bedTimeCalc (String wakeTime, String sleepTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.US);
        wakeTime = wakeTime.replaceAll("\\s", "");
        sleepTime = sleepTime.replaceAll("\\s", "");

        String newWakeTime = GENERIC_TODAY + "T" + wakeTime;
        String newSleepTime = GENERIC_TODAY + "T" + sleepTime;

        try {
            Date wake = timeFormat.parse(newWakeTime);
            Date sleep = timeFormat.parse(newSleepTime);

            Log.d("wake", wake.toString());
            Log.d("sleep", sleep.toString());

            if (wake.getTime() < sleep.getTime()) {

                newWakeTime = GENERIC_TOMORROW + "T" + wakeTime;
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

    /*
    returns a list of Joda Interval objects that tell which time blocks are free times in a user's
    schedule
    */
    public static List<Interval> getFreeTimes (String startTime, String endTime,
                                               List<TimePeriod> busyTimes) {

        if (busyTimes.size() == 0) {
            return null;
        }

        List<Interval> freeTimes = new ArrayList<Interval>();
        LocalDate currentDate = new LocalDate(DateTimeZone.getDefault());

        String newStartDate = currentDate.toString() + "T" + startTime;
        org.joda.time.DateTime startDateTime = org.joda.time.DateTime.parse(newStartDate);

        DateTime dateTime = busyTimes.get(0).getStart();
        org.joda.time.DateTime firstBusyEndTime = org.joda.time.DateTime.parse(dateTime.toString());

        Interval interval = new Interval(startDateTime, firstBusyEndTime);

        if (interval.toDuration().getStandardMinutes() > 0) {
            freeTimes.add(interval);
        }

        for (int i=0; i < busyTimes.size() - 1; i++) {
            DateTime startInterval = busyTimes.get(i).getEnd();
            org.joda.time.DateTime currentDateTime = org.joda.time.DateTime.parse(startInterval.toString());
            DateTime endInterval = busyTimes.get(i+1).getStart();
            org.joda.time.DateTime nextStartDateTime = org.joda.time.DateTime.parse(endInterval.toString());
            Interval newInterval = new Interval(currentDateTime,
                    nextStartDateTime);
            if (newInterval.toDuration().getStandardMinutes() > 0) {
                freeTimes.add(newInterval);
            }
        }

        org.joda.time.LocalTime endTimeConvert = new org.joda.time.LocalTime(endTime);
        org.joda.time.LocalTime startTimeConvert =
                new org.joda.time.LocalTime(busyTimes.get(busyTimes.size()-1).getEnd().getValue());

        if (endTimeConvert.isBefore(startTimeConvert)) {
            LocalDate tomorrow = currentDate.plusDays(1);
            String end = tomorrow.toString() + "T" + endTime;
            org.joda.time.DateTime endingDateTime = org.joda.time.DateTime.parse(end);

            org.joda.time.DateTime lastEndDT = org.joda.time.DateTime.parse(busyTimes.get(busyTimes.size()-1).getEnd().toString());
            Interval lastInterval = new Interval(lastEndDT, endingDateTime);

            if (lastInterval.toDuration().getStandardMinutes() > 0) {
                freeTimes.add(lastInterval);
            }
        } else {
            String end = currentDate.toString() + "T" + endTime;
            org.joda.time.DateTime endingDateTime = org.joda.time.DateTime.parse(end);
            org.joda.time.DateTime lastEndDT = org.joda.time.DateTime.parse(busyTimes.get(busyTimes.size()-1).getEnd().toString());
            Interval lastInterval = new Interval(lastEndDT, endingDateTime);

            if (lastInterval.toDuration().getStandardMinutes() > 0) {
                freeTimes.add(lastInterval);
            }
        }

        return freeTimes;

    }
}
