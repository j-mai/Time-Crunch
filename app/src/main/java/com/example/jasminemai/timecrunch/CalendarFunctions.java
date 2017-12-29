package com.example.jasminemai.timecrunch;

import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;

import org.joda.time.Period;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jasminemai on 11/18/17.
 * This class contains functions to connect to the Google Calendar API
 */

public class CalendarFunctions {

    public static DateTime makeDate (String date, String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        date = date.replaceAll("\\s", "");
        time = time.replaceAll("\\s", "");

        String dateToParse = date + "T" + time;
        try {
            Date d = dateFormat.parse(dateToParse);
            DateTime dateTime = new DateTime(d, TimeZone.getDefault());
            return dateTime;
        } catch (ParseException exception) {
            Log.w("DateTime parsing", exception.toString());
            return null;
        }
    }

    //function to return the freeBusy sections of a user's calendar
    //valid response gives back the busy sections of a user's calendar
    public static FreeBusyResponse getFreeBusy(Calendar calendar, String startDate, String startTime,
                                        String endDate, String endTime, String calendarId) throws IOException, UserRecoverableAuthIOException {

        //create DateTime objects for the starting and ending times
        DateTime startDateTime = makeDate(startDate, startTime);
        DateTime endDateTime = makeDate(endDate, endTime);

        if (startDateTime != null && endDateTime != null) {
            FreeBusyRequestItem id = new FreeBusyRequestItem();
            id.setId(calendarId);

            List<FreeBusyRequestItem> fbItems = new ArrayList<FreeBusyRequestItem>();
            fbItems.add(id);

            String timeZone = TimeZone.getDefault().getID().toString();

            FreeBusyRequest request = new FreeBusyRequest();
            request.setTimeMin(startDateTime);
            request.setTimeMax(endDateTime);
            request.setTimeZone(timeZone);
            Log.d("getFreeBusy", timeZone);
            request.setItems(fbItems);

            Calendar.Freebusy.Query fbQuery = calendar.freebusy().query(request);
            FreeBusyResponse fbResponse = fbQuery.execute();
            return fbResponse;


        } else {
            Log.w("getFreeBusy", "either startDateTime or endDateTime is null," +
                    "check parsing/conversion");
            return null;
        }
    }

    //function to add event to calendar - already proper format, so no need to add Date and parse it
    public static Event addEventProperFormat(Task task, Calendar calendar, String calendarId) throws IOException{

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        Event event = new Event()
                .setSummary(task.getTaskName());

        if (task.getDescription() != null) {
            event.setDescription(task.getDescription());
        }

        //try creating a new DateTime from parsing into the correct format
        DateTime startDateTime = null;
        try {
            Date d = dateFormat.parse(task.getStartTime());
            startDateTime = new DateTime(d, TimeZone.getDefault());
        } catch (ParseException exception) {
            Log.w("DateTime parsing", exception.toString());
            return null;
        }

        String timeZone = TimeZone.getDefault().getID();

        if (startDateTime != null) {
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(timeZone);
            event.setStart(start);
        }


        DateTime endDateTime = null;

        try {
            Date d = dateFormat.parse(task.getEndTime());
            endDateTime = new DateTime(d, TimeZone.getDefault());
        } catch (ParseException exception) {
            Log.w("DateTime parsing", exception.toString());
            return null;
        }

        if (endDateTime != null) {
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(timeZone);
            event.setEnd(end);
        }

        //send a request to Google API to insert event into calendar
        event = calendar.events().insert(calendarId, event).execute();

        return event;

    }

//    //function to add event to calendar - used if the there is not time in the time string
//    public static Event addEvent(Task task, Calendar calendar, String calendarId) throws IOException{
//
//        Event event = new Event()
//                .setSummary(task.getTaskName());
//
//        if (task.getDescription() != null) {
//            event.setDescription(task.description);
//        }
//
//        DateTime startDateTime = makeDate(task.startDate, task.startTime);
//        String timeZone = TimeZone.getDefault().getID();
//        EventDateTime start = new EventDateTime()
//                .setDateTime(startDateTime)
//                .setTimeZone(timeZone);
//
//        event.setStart(start);
//
//        DateTime endDateTime = makeDate(task.endDate, task.endTime);
//        EventDateTime end = new EventDateTime()
//                .setDateTime(endDateTime)
//                .setTimeZone(timeZone);
//
//        event.setEnd(end);
//
//        event = calendar.events().insert(calendarId, event).execute();
//
//        return event;
//
//    }

    //function to delete an event from a calendar
    public static Boolean deleteEvent (String eventID, Calendar calendar,
                                       String calendarId) throws IOException {

        if (eventID != null) {
            Calendar.Events.Delete response = calendar.events().delete(calendarId, eventID);

            if (response == null) {
                return true;
            } else {
                Log.e("deleteEvent failed", response.toString());
                return false;
            }
        }

        return false;
    }

//    //function to delete an event from a calendar
//    public static Boolean deleteEvent (Task task, Calendar calendar, String calendarId) throws IOException {
//        if (task != null && task.eventID != null) {
//            for (String eventID : task.eventID) {
//                if (calendar.events().delete(calendarId, eventID).execute() == null) {
//                    return true;
//                } else {
//                    Log.e("deleteEvent failed", (calendar.events().delete(calendarId,
//                            eventID).execute()).toString());
//                    return false;
//                }
//            }
//
//        } else if (task == null) {
//            Log.w("deleteEvent", "task is null");
//        } else if (task.eventID == null) {
//            Log.w("deleteEvent", "task eventID is null");
//
//        }
//
//        return false;
//    }

    public static List<TimePeriod> getBusy (FreeBusyResponse response, String calendarId) {

        return response.getCalendars().get(calendarId).getBusy();
    }
}
