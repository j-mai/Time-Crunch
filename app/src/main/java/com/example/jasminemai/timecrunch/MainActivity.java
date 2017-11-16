package com.example.jasminemai.timecrunch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Sends an intent to the tabActivity, allows user to begin creating schedule
    public void newScheduleButtonClicked(View v) {
        Log.d("ScheduleButtonClicked", "Create a new account");
        Intent newScheduleIntent = new Intent(this, TabActivity.class);
        startActivity(newScheduleIntent);
    }

}
