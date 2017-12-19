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
    public static HashMap<String, ArrayList<JSONObject>> spToMap(String tasks){
        HashMap<String, ArrayList<JSONObject>> tasksMap;

        Gson gson = new Gson();

        //If a values map already exists, set it equal to tasksMap variable
        //else, create a new map
        if (tasks != null){
            java.lang.reflect.Type type = new TypeToken<HashMap<String, ArrayList<JSONObject>>>(){}.getType();
            tasksMap = gson.fromJson(tasks, type);
        } else {
            tasksMap = new HashMap<String, ArrayList<JSONObject>>();
        }

        Log.d("map after spToMap", tasksMap.entrySet().toString());

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
    public static String mapToString(Map<String, ArrayList<JSONObject>> taskMap){
        Gson gson = new Gson();
        String tasksMapString = gson.toJson(taskMap);
        Log.d("map after mapToString", tasksMapString);
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

    //breaks up breakable tasks and breaks them into JSON objects; returns an ArrayList of one-hour tasks
    public static ArrayList<JSONObject> taskBreakUp(String event, String type, String from, String to, int time, Boolean breakBool) {
        ArrayList<JSONObject> taskBrokenUp = new ArrayList<>();
        if (breakBool) {
            while (time > 60) {
                JSONObject newJSON = stringsToJSON(event, type, from, to, "60", breakBool);
                taskBrokenUp.add(newJSON);
                time = time - 60;
            }

            if (time > 0) {
                JSONObject newJSON = stringsToJSON(event, type, from, to, Integer.toString(time), breakBool);
                taskBrokenUp.add(newJSON);
            }

            return taskBrokenUp;
        } else {
            JSONObject newJSON = stringsToJSON(event, type, from, to, Integer.toString(time), breakBool);
            taskBrokenUp.add(newJSON);
            return taskBrokenUp;
        }
    }

    //takes a map of strings to arraylist of JSON objects and converts it into an ArrayList of Task Objects
    public static ArrayList<Task> mapToArray(HashMap<String, ArrayList<JSONObject>> map){

        ArrayList<Task> allTasks = new ArrayList<>();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            ArrayList<JSONObject> tasks = (ArrayList<JSONObject>) pair.getValue();
            for (int i = 0; i < tasks.size(); i++) {
                JSONObject taskInfo = (JSONObject) tasks.get(i);
                Task task = jsonToTask(taskInfo);
                allTasks.add(task);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        Collections.sort(allTasks);
        return allTasks;

    }

}
