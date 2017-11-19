package com.example.jasminemai.timecrunch;

import com.google.api.client.util.DateTime;

/**
 * Created by jasminemai on 11/18/17.
 * Task class for creating task objects containing all of the information of each task
 */

public class Task {
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    String type;
    String eventID;
    String description;
    String name;

    public Task (String startDate, String startTime, String endDate, String endTime, String name, String type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventID (String eventID) {
        this.eventID = eventID;
    }

}
