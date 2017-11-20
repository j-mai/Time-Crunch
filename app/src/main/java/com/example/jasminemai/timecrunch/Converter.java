package com.example.jasminemai.timecrunch;

import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;


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
}