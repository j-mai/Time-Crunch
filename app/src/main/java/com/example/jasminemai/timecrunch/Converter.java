package com.example.jasminemai.timecrunch;

import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.content.SharedPreferences;
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
        if (tasks != null){
            java.lang.reflect.Type type = new TypeToken<HashMap<String, JSONObject>>(){}.getType();
            tasksMap = gson.fromJson(tasks, type);
        } else {
            tasksMap = new HashMap<String, JSONObject>();
        }
        return tasksMap;
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
    public static ArrayList<Task> splitUpTasks (Map<String, JSONObject> tasks) {
        ArrayList<Task> tasksSplitUp = new ArrayList<>();

        for (String task : tasks.keySet()) {
            JSONObject taskInfo = tasks.get(task);
            Task t = jsonToTask(taskInfo);
            if (t.breakUp) {
                while (t.totalTime > 60) {
                    Task newTask = new Task(t.startDate, t.endDate, t.name, t.type, 60, true);
                    tasksSplitUp.add(newTask);
                    t.totalTime = t.totalTime - 60;
                }

                if (t.totalTime > 0) {
                    Task newTask = new Task(t.startDate, t.endDate, t.name, t.type, t.totalTime, true);
                    tasksSplitUp.add(newTask);
                }
            } else {
                Task newTask = new Task(t.startDate, t.endDate, t.name, t.type, t.totalTime, true);
                tasksSplitUp.add(newTask);
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
