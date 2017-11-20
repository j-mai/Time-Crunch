package com.example.jasminemai.timecrunch;

import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by michelle on 11/20/17.
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
    public static Map spToMap(String tasks){
        Map<String, JSONObject> tasksMap;

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

    //Takes 6 strings and returns them in a JSON Object
    public static JSONObject stringsToJSON(String event, String type, String from, String to, String time, Boolean breakBool){
        JSONObject newJson = new JSONObject();
        try {
            newJson.put("name", event);
            newJson.put("type", type);
            newJson.put("startDate", from);
            newJson.put("endDate", to);
            newJson.put("totalTime", time);
            newJson.put("breakUp", breakBool);

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

    public static ArrayList<Task> mapToArray(Map map){
        ArrayList<Task> tasks = new ArrayList<>();
        return tasks;
    }

    public static Map createSplitUpMap(Map <String, JSONObject> tasks) {

        Map<String, ArrayList<Task>> tempMap = new HashMap<String, ArrayList<Task>>();

        tempMap.put("Study", new ArrayList<Task>());
        tempMap.put("Other", new ArrayList<Task>());
        tempMap.put("Exercise", new ArrayList<Task>());

        for (String key : tasks.keySet()) {
            Task task = jsonToTask(tasks.get(key));

            if (task.type.equals("Study"))

        }

    }


//    public static Map spToTempMap(String tasks) {
//
//        Map<String, JSONObject> taskMap = spToMap(tasks);
//
//        Map<String, ArrayList<Task>> tempMap = new HashMap<String, ArrayList<Task>>();
//
//        tempMap.put("Study", new ArrayList<Task>());
//        tempMap.put("Other", new ArrayList<Task>());
//        tempMap.put("Exercise", new ArrayList<Task>());
//
//        for (String key : taskMap.keySet()) {
//
//        }
//    }
}