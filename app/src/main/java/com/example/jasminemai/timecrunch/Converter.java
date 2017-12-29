package com.example.jasminemai.timecrunch;

import android.content.SharedPreferences;

import com.google.api.services.calendar.Calendar;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.content.SharedPreferences;
import android.util.EventLog;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by michelle on 11/20/17.
 * Here is where a lot of conversion between strings and data structures happen
 */

public class Converter {
    public static String TC_SHARED_PREF = "my_sharedpref";


    /**
     * pass in the string from shared preferences:
     * String tasks = sp.getString("tasksMap",null);
     *
     * returns a HashMap that maps Strings to JSON Objects
     * @param tasks
     * @return tasksMap
     */
    public static HashMap<String, JSONObject> spToMap(String tasks){
        HashMap<String, JSONObject> tasksMap;

        Gson gson = new Gson();

        //If a values map already exists, set it equal to tasksMap variable
        //else, create a new map
        if (tasks != null && !tasks.equals("")){
            java.lang.reflect.Type type = new TypeToken<HashMap<String, JSONObject>>(){}.getType();
            tasksMap = gson.fromJson(tasks, type);
        } else {
            tasksMap = new HashMap<String, JSONObject>();
        }
        return tasksMap;
    }

    //takes string from shared preferences and changes it to map of event name to
    //array list of inputted calendar events
    public static HashMap<String, ArrayList<EventInfo>> spToEventInfo (String events) {
        HashMap<String, ArrayList<EventInfo>> eventsMap;

        Gson gson = new Gson();

        //if events map already exists, set it equal to eventsMap
        //else, create a new map

        if (events != null && !events.equals("")) {
            Type type = new TypeToken<HashMap<String, ArrayList<EventInfo>>>(){}.getType();
            eventsMap = gson.fromJson(events, type);
        } else {
            eventsMap = new HashMap<String, ArrayList<EventInfo>>();
        }

        return eventsMap;
    }

    //takes string from shared preferences and changes it to map of event name to
    //array list of JSONObjects of inputted calendar events
    public static HashMap<String, ArrayList<JSONObject>> spToEventJSON (String events) {
        HashMap<String, ArrayList<JSONObject>> eventsMap;

        Gson gson = new Gson();

        //if events map already exists, set it equal to eventsMap
        //else, create a new map

        if (events != null && !events.equals("")) {
            Type type = new TypeToken<HashMap<String, ArrayList<JSONObject>>>(){}.getType();
            eventsMap = gson.fromJson(events, type);
        } else {
            eventsMap = new HashMap<String, ArrayList<JSONObject>>();
        }

        return eventsMap;
    }

    /**
     * pass in a map of string to array list of events
     * returns a string that can be put into sp
     */
    public static String eventsMapToString (Map <String, ArrayList<JSONObject>> events) {
        Gson gson = new Gson();
        String eventsString = gson.toJson(events);
        return eventsString;
    }

    /**
     * pass in a map:
     * Returns a string that can be put into sp
     *
     * returns a HashMap that maps Strings to JSON Objects
     * @param taskMap
     * @return tasksMapString
     */
    public static String mapToString(Map<String, JSONObject> taskMap){
        Gson gson = new Gson();
        String tasksMapString = gson.toJson(taskMap);
        return tasksMapString;
    }

    //Takes 6 strings and a bool and returns them in a JSON Object
    public static JSONObject stringsToJSON(String event, String type, String from, String to, String time, Boolean breakBool){
        JSONObject newJson = new JSONObject();
        try {
            newJson.put("name", event);
            newJson.put("type", type);
            newJson.put("startDate", from);
            newJson.put("endDate", to);
            newJson.put("totalTime", time);
            newJson.put("breakUp", breakBool);
            //newJson.put("eventID", eventIDArray);

        } catch (JSONException e) {
            Log.d("JSON", e.toString());
        }
        return newJson;
    }

    //Takes strings, int, and a bool and returns them in a JSONObject for EventInfo
    public static JSONObject stringsToEventJSON (String name, int totalTime, Boolean breakable, int duration,
                                                 String start, String end, String eventID, String type) {
        JSONObject newJson = new JSONObject();
        try {
            newJson.put("name", name);
            newJson.put("totalTime", totalTime);
            newJson.put("breakUp", breakable);
            newJson.put("duration", duration);
            newJson.put("start", start);
            newJson.put("end", end);
            newJson.put("eventID", eventID);
            newJson.put("type", type);

        } catch (JSONException e) {
            Log.e("JSON", e.toString());
        }

        return newJson;
    }

    //takes a json object and converts to a task object
    public static Task jsonToTask(JSONObject json){
        Gson gson = new GsonBuilder().create();
        Task task = gson.fromJson(json.toString(), Task.class);
        return task;
    }

    //takes a map of strings to JSON objects and converts it into an ArrayList of Task Objects
    public static ArrayList<Task> mapToArray(HashMap<String, JSONObject> map){
        ArrayList<Task> tasks = new ArrayList<>();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            JSONObject taskInfo = (JSONObject) pair.getValue();
            Task task = jsonToTask(taskInfo);
            tasks.add(task);
            it.remove(); // avoids a ConcurrentModificationException
        }
        Collections.sort(tasks);
        return tasks;
    }

    //create arraylist of tasks split up properly
    public static ArrayList<Task> splitUpTasks (Map<String, JSONObject> tasks,
                                                Map<String, ArrayList<EventInfo>> eventsMap, Calendar calendar,
                                                String calendarID, DateTime currentDateTime) {

        ArrayList<Task> tasksSplitUp = new ArrayList<>();

        for (String task : tasks.keySet()) {
            JSONObject taskInfo = tasks.get(task);
            Task t = jsonToTask(taskInfo);
            LocalDate startDate = new LocalDate(t.getStartDate());
            LocalDate currentDate = currentDateTime.toLocalDate();
            Boolean taskValid = false;
            if (!t.getEndDate().equals("none")) {
                LocalDate endDate = new LocalDate(t.getEndDate());
                if ((endDate.isAfter(currentDate) ||
                        endDate.isEqual(currentDate)) && (startDate.isBefore(currentDate)
                        || startDate.isEqual(LocalDate.now()))) {
                    taskValid = true;
                }
            } else {
                if (startDate.isBefore(currentDate)
                        || startDate.isEqual(currentDate)) {
                    taskValid = true;
                }
            }

            if (taskValid) {
                int totalTime = t.getTotalTime();

                if (eventsMap != null) {
                    ArrayList<EventInfo> events = eventsMap.get(t.getTaskName());
                    if (events != null) {
                        try {
                            totalTime = totalTime - TimeFunctions.totalTimeSpent(events, currentDateTime, calendar, calendarID);
                        } catch (IOException e) {
                            Log.e("totalTimeSpentError", e.toString());
                        }
                    }


                }

                if (t.getBreakUp()) {
                    while (totalTime > 60) {
                        Task newTask = new Task(t.getStartDate(), t.getEndDate(), t.getTaskName(),
                                t.getType(), 60, true);
                        tasksSplitUp.add(newTask);
                        totalTime = totalTime - 60;
                    }

                    if (totalTime > 0) {
                        Task newTask = new Task(t.getStartDate(), t.getEndDate(), t.getTaskName(),
                                t.getType(), totalTime, true);
                        tasksSplitUp.add(newTask);
                    }
                } else {
                    Task newTask = new Task(t.getStartDate(),
                            t.getEndDate(), t.getTaskName(), t.getType(), totalTime, true);
                    tasksSplitUp.add(newTask);
                }
            }
        }

        return tasksSplitUp;

    }

//    //create map of task type to split up tasks properly if user has chosen to split them up.
//    //calls helper function
//    public static Map<String, ArrayList<Task>> createSplitUpMap(Map <String, JSONObject> tasks) {
//
//        Map<String, ArrayList<Task>> tempMap = new HashMap<String, ArrayList<Task>>();
//
//        tempMap.put("Study", new ArrayList<Task>());
//        tempMap.put("Other", new ArrayList<Task>());
//        tempMap.put("Exercise", new ArrayList<Task>());
//
//        for (String key : tasks.keySet()) {
//            Task task = jsonToTask(tasks.get(key));
//            String taskType = task.type;
//
//            if (task.breakUp) {
//                ArrayList<Task> tasksArray = TimeFunctions.splitUpTask(task);
//
//                for (Task eachTask : tasksArray) {
//                    tempMap.get(taskType).add(eachTask);
//                }
//
//
//            } else {
//                tempMap.get(taskType).add(task);
//
//            }
//
//        }
//
//        return tempMap;
//
//    }

}
