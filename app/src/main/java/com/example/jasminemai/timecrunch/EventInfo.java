package com.example.jasminemai.timecrunch;

import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * Created by jasminemai on 12/24/17.
 */

public class EventInfo {

    private String eventName;
    private int totalTime;
    private Boolean breakable;
    private int duration;
    private DateTime start;
    private DateTime end;
    private String eventID;
    private String type;

    public EventInfo (String eventName, int totalTime, Boolean breakable, int duration, DateTime start,
                      DateTime end, String eventID, String type) {

        this.eventName = eventName;
        this.totalTime = totalTime;
        this.breakable = breakable;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.eventID = eventID;
        this.type = type;
    }

    public String getEventName () {
        return this.eventName;
    }

    public int getTotalTime () {
        return this.totalTime;
    }

    public int getDuration () {
        return this.duration;
    }

    public DateTime getStart () {
        return this.start;
    }

    public DateTime getEnd () {
        return this.end;
    }

    public String getEventID () {
        return this.eventID;
    }

    public String getEventType () {
        return this.type;
    }

    public Boolean getBreakable () {
        return this.breakable;
    }

    public Boolean onGoing (DateTime now) {
        return (now.isEqual(this.start) || now.isAfter(this.start) && now.isBefore(this.end));
    }

    public int remainingTime (DateTime now) {
        Period period = new Period(now, this.end);
        return (period.getMinutes());
    }

    public int passedTime (DateTime now) {
        Period period = new Period (this.start, now);
        return (period.getMinutes());
    }

    public Boolean finishedTask (DateTime now) {
        return (now.isAfter(this.start) && (now.isAfter(this.end) || now.isEqual(this.end)));
    }
}
