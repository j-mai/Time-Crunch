package com.example.jasminemai.timecrunch;

import android.app.DatePickerDialog;
import android.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.common.reflect.TypeToken;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NewTask extends FragmentActivity implements DatePickerDialog.OnDateSetListener, SameNameFragment.DialogListener {

    private SameNameFragment sameNameFrag;
    public static String TC_SHARED_PREF = "my_sharedpref";
    boolean replace = false;

    org.joda.time.LocalDate currentDate = new org.joda.time.LocalDate(DateTimeZone.getDefault());
    org.joda.time.LocalDate start = currentDate;
    org.joda.time.LocalDate end = currentDate;
    EditText startDate;
    EditText eventName;
    Spinner chooseType;
    EditText currentDatePicked = null;
    EditText endDate;
    Switch repeat;
    EditText hours;
    EditText minutes;
    CheckBox dontBreak;

    JSONObject saveTask;

    int hoursTotal;
    int minTotal;
    Map<String, JSONObject> tasksMap;

    //Create the New Task
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        JodaTimeAndroid.init(this);
        initializeVariables();

        onRepeatClicked();
        onHoursSet();
        onMinutesSet();
        setupSpinner();
    }

    //Initialize each editable value in the new task
    protected void initializeVariables(){
        eventName = findViewById(R.id.eventName);
        endDate = (EditText) findViewById((R.id.endDate));
        startDate = (EditText) findViewById(R.id.startDate);
        repeat = ((Switch) findViewById(R.id.repeat));
        chooseType = (Spinner) findViewById(R.id.chooseTask);
        hours = (EditText) findViewById(R.id.hr);
        minutes = (EditText) findViewById(R.id.min);
        dontBreak = (CheckBox) findViewById(R.id.breakUp);

        String currDate = String.format(getResources().getString(R.string.date), currentDate.getYear(), currentDate.getMonthOfYear(), currentDate.getDayOfMonth());
        startDate.setText(currDate);
        endDate.setText(currDate);
    }

    //Called when the start or the end date is picked
    //Opens CalendarView Fragment for user to pick start/end date
    public void showDatePickerDialog(View v) {

        //Check to see if they are picking the start or the end date
        switch (v.getId()) {
            case R.id.startDate:
                currentDatePicked = findViewById(R.id.startDate);
                break;
            case R.id.endDate:
                currentDatePicked = findViewById(R.id.endDate);
                break;
        }
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    //Once they pick a date, change the EditText to reflect their selection
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.w("DatePicker", "a date has been set");
        //convert the set date to a string
        String date = String.format(getResources().getString(R.string.date), year, (month + 1), dayOfMonth);
        org.joda.time.LocalDate setDate = new org.joda.time.LocalDate(year, (month +1), dayOfMonth);

        //if they are setting the endDate, make sure it is valid
        if (currentDatePicked.equals(endDate)){
            //check if the date is before the current date
            if (setDate.isBefore(currentDate)){
                endDate.setError("End Date must be after current date");
                endDate.setText("");
                return;
            }
            end = setDate;

        } else {
            start = setDate;
        }

        //the end date must be after the start
        if (end.isBefore(start)){
            endDate.setError("End Date must be after start date");
            endDate.setText("");
        }else{
            currentDatePicked.setError(null);
            currentDatePicked.setText(date);
        }

    }


    //Handle case of user clicking toggle of whether or not they want an end date
    public void onRepeatClicked(){
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton toggleButton, boolean isChecked)
            {
                //This Even repeats each day
                if (isChecked){
                    endDate.setText("None");
                    endDate.setError(null);
                    endDate.setTextIsSelectable(false);
                }
                //Allow user to set an end date
                else{
                    endDate.setTextIsSelectable(true);
                    endDate.setText("");
                }
            }
        });

    }

    //Check if hours value is valid
    public void onHoursSet(){
        hours.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    int numHours = Integer.parseInt(hours.getText().toString());
                    if (numHours >= 18){
                        hours.setText("");
                        hours.setError("Leave Time to Sleep!");
                        hoursTotal = 0;
                    } else{
                        hoursTotal = numHours;
                    }
                }
                catch (NumberFormatException e){
                    Log.d("parseInt Excpetion", "exception" + e);
                    hours.setText("0");
                    hoursTotal = 0;
                }
            }
        });

    }

    //Check if minutes value is valid
    public void onMinutesSet(){
        minutes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    try {
                        int numMin = Integer.parseInt(minutes.getText().toString());
                        minTotal = numMin;
                        if (numMin >= 60){
                            minTotal = 59;
                            minutes.setText("59");
                        }
                    }
                    catch (NumberFormatException e){
                        Log.d("parseInt Exception", "exception" + e);
                        minutes.setText("0");
                        minTotal = 0;
                    }
                }
            }
        });

    }

    //sets up the spinner with the different types of tasks
    public void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.chooseType, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        chooseType.setAdapter(adapter);
    }

    //saves the task in shared preferences
    public void onSaveButtonClicked(View v){

        SharedPreferences sp = getSharedPreferences(TC_SHARED_PREF, 0);

        String tasks = sp.getString("tasksMap",null);
        tasksMap = Converter.spToMap(tasks);

        //If an event by this name already exists, check if they want it replaced
        if (tasksMap.containsKey(eventName.getText().toString())){
            showDialog();
            if (!replace){
                Log.d("cancel", "user canceled save");
                return;
            } else {
                saveTaskAndReturn();
            }
        } else {
            saveTaskAndReturn();
        }
    }

    public void saveTaskAndReturn(){
        SharedPreferences sp = getSharedPreferences(TC_SHARED_PREF, 0);
        Log.d("taskToJSON", "saving task as JSON");
        JSONObject saveTask = taskToJSON();

        //save this task using the event name as a key
        tasksMap.put(eventName.getText().toString(), saveTask);
        String tasksMapString = Converter.mapToString(tasksMap);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("tasksMap", tasksMapString);

        editor.apply();
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

        Intent todoIntent = new Intent(this, TabActivity.class);
        startActivity(todoIntent);
    }

    public JSONObject taskToJSON(){
        int totalTime = (hoursTotal * 60) + minTotal;
        String timeString = Integer.toString(totalTime);
        String eventString = eventName.getText().toString();
        String typeString = chooseType.getSelectedItem().toString();
        String fromString = startDate.getText().toString();
        String toString = endDate.getText().toString();
        Boolean breakBool = dontBreak.isChecked();
//        ArrayList <String> eventIDs = new ArrayList<String>();
//        String eventIDString = eventIDs.toString();

        //Save all objects inside JSON
        saveTask = Converter.stringsToJSON(eventString, typeString, fromString, toString, timeString, !breakBool);
        return saveTask;
    }

    //open sameName Dialog
    public void showDialog() {
        // Create an instance of the dialog fragment and show it
        sameNameFrag = new SameNameFragment();
        sameNameFrag.show(getFragmentManager(), "SameNameFragment");
    }

    //user wants to replace task with new one
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        saveTaskAndReturn();
        replace = true;
    }

    //user wants to cancel
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        replace = false;
    }

    //When you click outside an edit box, change the focus
    //Taken from stack overflow
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    public void onCancelClicked(View v){
        Intent todoIntent = new Intent(this, TabActivity.class);
        startActivity(todoIntent);
    }
}
