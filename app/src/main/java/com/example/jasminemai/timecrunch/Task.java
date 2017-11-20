package com.example.jasminemai.timecrunch;

import android.support.annotation.NonNull;

import com.google.api.client.util.DateTime;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.time.format.DateTimeFormatter;

/**
 * Created by jasminemai on 11/18/17.
 * Task class for creating task objects containing all of the information of each task
 */

public class Task implements Comparable<Task>{
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    String type;
    String eventID;
    String description;
    String name;
    int totalTime;
    boolean breakUp;

    public Task (String startDate, String endDate, String name, String type, int totalTime, boolean breakUp) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.type = type;
        this.totalTime = totalTime;
        this.breakUp = breakUp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventID (String eventID) {
        this.eventID = eventID;
    }

    public void setStartTime (String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime (String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(@NonNull Task o) {
        org.joda.time.LocalDate currDate = new org.joda.time.LocalDate(LocalDate.parse(this.endDate));
        org.joda.time.LocalDate otherDate = new org.joda.time.LocalDate(LocalDate.parse(o.endDate));

        return currDate.compareTo(otherDate);
    }
}
