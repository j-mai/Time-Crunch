package com.example.jasminemai.timecrunch;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static String TC_SHARED_PREF = "my_sharedpref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Sends an intent to the tabActivity, allows user to begin creating schedule
    public void newScheduleButtonClicked(View v) {
        Log.d("ScheduleButtonClicked", "Plan Day");

        //clear sharedPreferences
//        SharedPreferences sp = getSharedPreferences(TC_SHARED_PREF, 0);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.clear();
//        editor.apply();
        Intent newScheduleIntent = new Intent(this, TabActivity.class);
        startActivity(newScheduleIntent);
    }

    public void onTimeClicked(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

}
