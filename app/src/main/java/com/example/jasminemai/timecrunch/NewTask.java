package com.example.jasminemai.timecrunch;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

public class NewTask extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    EditText currentDatePicked = null;
    EditText endDate;
    Switch repeat;
    Spinner chooseType;


    //Create the New Task
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        //initialize variables
        endDate = (EditText) findViewById((R.id.endDate));
        repeat = ((Switch) findViewById(R.id.repeat));
        chooseType = (Spinner) findViewById(R.id.chooseTask);
        onRepeatClicked();
        setupSpinner();
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
        Log.w("DatePicker", "year = " + year);
        currentDatePicked.setText(month + "/" + dayOfMonth + "/" + year);
    }


    //Handle case of user clicking toggle of whether or not they want an end date
    public void onRepeatClicked(){
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton toggleButton, boolean isChecked)
            {
                //This Even repeats each day
                if (isChecked){
                    endDate.setText("Forever");
                    endDate.setTextIsSelectable(false);
                }
                //Allow user to set an end date
                else{
                    endDate.setTextIsSelectable(true);
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


}
