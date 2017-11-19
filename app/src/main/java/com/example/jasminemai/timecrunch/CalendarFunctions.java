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

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by jasminemai on 11/18/17.
 */

public class CalendarFunctions {

    public static DateTime makeDate (String date, String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = date.replaceAll("\\s", "");
        time = time.replaceAll("\\s", "");
        String dateToParse = date + " " + time;
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
    public static FreeBusyResponse getFreeBusy(Calendar calendar, String startDate, String startTime,
                                        String endDate, String endTime, String calendarId) throws IOException, UserRecoverableAuthIOException {

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

//            try {
//
//
//            } catch (UserRecoverableAuthIOException exception) {
//                //TODO: fix signing in from another account, changing accounts, signing out
//                startActivityForResult(exception.getIntent(), REQUEST_AUTHORIZATION);
//                Log.w("freeBusy error", exception.toString());
//                return null;
//            } catch (IOException exception) {
//                Log.w("freeBusy error", exception.toString());
//                return null;
//            }
//
//        } else {
//            Log.d("getFreeBusy", "either startDateTime or endDateTime is null," +
//                    "check parsing/conversion");
//            return null;
//        }


        } else {
            Log.d("getFreeBusy", "either startDateTime or endDateTime is null," +
                    "check parsing/conversion");
            return null;
        }
    }

    //function to add event to calendar
    public static Event addEvent(Task task, Calendar calendar, String calendarId) throws IOException{

        Event event = new Event()
                .setSummary(task.name);

        if (task.description != null) {
            event.setDescription(task.description);
        }

        DateTime startDateTime = makeDate(task.startDate, task.startTime);
        String timeZone = TimeZone.getDefault().getID();
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(timeZone);

        event.setStart(start);

        DateTime endDateTime = makeDate(task.endDate, task.endTime);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(timeZone);

        event.setEnd(end);

        event = calendar.events().insert(calendarId, event).execute();

        return event;

    }

    //function to delete an event from a calendar
    public static Boolean deleteEvent (Task task, Calendar calendar, String calendarId) throws IOException {
        if (task != null && task.eventID != null) {
            if (calendar.events().delete(calendarId, task.eventID).execute() == null) {
                return true;
            } else {
                Log.e("deleteEvent failed", (calendar.events().delete(calendarId,
                        task.eventID).execute()).toString());
                return false;
            }
//            return (calendar.events().delete(calendarId, task.eventID).execute()).toString();

        } else if (task == null) {
            Log.w("deleteEvent", "task is null");
        } else if (task.eventID == null) {
            Log.w("deleteEvent", "task eventID is null");

        }

        return false;
    }
}
