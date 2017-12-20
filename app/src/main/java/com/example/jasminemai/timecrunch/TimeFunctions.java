package com.example.jasminemai.timecrunch;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.TimePeriod;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

        List<Interval> freeTimes = new ArrayList<Interval>();
        LocalDate currentDate = new LocalDate(DateTimeZone.getDefault());
        String newStartDate = currentDate.toString() + "T" + startTime;
        org.joda.time.DateTime startDateTime = org.joda.time.DateTime.parse(newStartDate);

        org.joda.time.LocalTime endTimeConvert = new org.joda.time.LocalTime(endTime);

        if (busyTimes.size() == 0) {

            if (endTimeConvert.isBefore(org.joda.time.LocalTime.parse(startTime))) {
                LocalDate tomorrow = currentDate.plusDays(1);
                String end = tomorrow.toString() + "T" + endTime;
                org.joda.time.DateTime endingDateTime = org.joda.time.DateTime.parse(end);

//                org.joda.time.DateTime lastEndDT = org.joda.time.DateTime.parse(busyTimes.get(busyTimes.size()-1).getEnd().toString());
                Interval lastInterval = new Interval(startDateTime, endingDateTime);

                if (lastInterval.toDuration().getStandardMinutes() > 0) {
                    freeTimes.add(lastInterval);
                }

            } else {
                String end = currentDate.toString() + "T" + endTime;
                org.joda.time.DateTime endingDateTime = org.joda.time.DateTime.parse(end);
//                org.joda.time.DateTime lastEndDT = org.joda.time.DateTime.parse(busyTimes.get(busyTimes.size()-1).getEnd().toString());
                Interval lastInterval = new Interval(startDateTime, endingDateTime);

                if (lastInterval.toDuration().getStandardMinutes() > 0) {
                    freeTimes.add(lastInterval);
                }
            }

            return freeTimes;

        }



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

    //returns the amount of free time that the user has in a day
    public static int totalFreeTime (List<Interval> freeTimes) {

        int minutes = 0;

        for (int i = 0; i < freeTimes.size(); i++) {
            minutes = minutes + (int)(freeTimes.get(i).toDuration().getStandardMinutes());
        }

        return minutes;
    }

    //get largest free time block
    public static Interval getLargestTimeBlock (List<Interval> freeTimes) {

        Interval biggest = freeTimes.get(0);

        for (int i=0; i < freeTimes.size(); i++) {
            if (freeTimes.get(i).toDuration().getStandardMinutes() > biggest.toDuration().getStandardMinutes()) {
                biggest = freeTimes.get(i);
            }
        }

        return biggest;
    }

    //get largest time block's index in list
    public static int getLargestTimeBlockIndex (List<Interval> freeTimes) {

        int biggest = 0;

        for (int i=0; i < freeTimes.size(); i++) {
            if (freeTimes.get(i).toDuration().getStandardMinutes() >
                    freeTimes.get(biggest).toDuration().getStandardMinutes()) {
                biggest = i;
            }
        }

        return biggest;
    }

    //get longest Task Block
    public static Task getLongestTaskBlock (ArrayList<Task> tasksList) {
        Task longest = null;

        for (Task task : tasksList) {
            if (longest == null) {
                longest = task;
            } else {
                if (longest.totalTime < task.totalTime) {
                    longest = task;
                }
            }
        }

        return longest;

    }

    //get index of longest Task Block
    public static int getLongestTaskBlockIndex (Map<String, ArrayList<Task>> taskMap) {

        int index = 0;

        for (String key : taskMap.keySet()) {
            for (int i = 0; i < taskMap.get(key).size(); i++) {
                if (taskMap.get(key).get(i).totalTime > taskMap.get(key).get(index).totalTime) {
                    index = i;
                }
            }
        }

        return index;

    }

    public static Boolean removeFromTempList (ArrayList<Task> tasksList, Task task) {
        return (tasksList.remove(task));
    }

    //subtract from Intervals, for decreasing free periods
    public static Interval decreaseIntervalFromHead (Interval bigInterval, Interval smallInterval) {

        Interval overlap = bigInterval.overlap(smallInterval);

        Interval newInteveral = new Interval (overlap.getEnd(), bigInterval.getEnd());

        return newInteveral;

    }

    //total time that tasks require
    public static int totalTaskTimeReq (Map<String, JSONObject> hashMap) {

        int totalTime = 0;
        for (String key : hashMap.keySet()) {
            try {
                int time = hashMap.get(key).getInt("totalTime");
                totalTime += time;
            } catch (JSONException e) {
                Log.e("totalTaskTimeReq error:", e.toString());
                return -1;
            }

        }

        return totalTime;
    }

//    //split up splittable tasks into various 1 hour tasks
//    public static ArrayList<Task> splitUpTask (Task task) {
//
//        ArrayList<Task> splitUp = new ArrayList<>();
//
//        while (task.totalTime > 60) {
//
//            Task newTask = new Task (task.startDate, task.endDate, task.name, task.type,
//                    60, true);
//
//            splitUp.add(newTask);
//
//            task.totalTime = task.totalTime - 60;
//        }
//
//        splitUp.add(task);
//
//        return splitUp;
//    }


}
