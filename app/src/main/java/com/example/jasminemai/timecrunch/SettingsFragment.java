package com.example.jasminemai.timecrunch;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalTime;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public static String TC_SHARED_PREF = "my_sharedpref";

    int REQUEST_CODE = 0;
    private static final String DIALOG_TIME = "date";

    TextView wake;
    TextView bed;
    Spinner numHours;
    Spinner allHoursWake;
    Spinner allMinWake;
    Spinner allHoursSleep;
    Spinner allMinSleep;
    Button saveSettings;

    String defaulth1="00";
    String defaulth2="00";
    String defaultm1= "00";
    String defaultm2 = "00";
    String defaults = "8";

    EditText wakeTime;

    android.support.v4.app.FragmentManager fragmentManager;

//    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        numHours = (Spinner) view.findViewById(R.id.selectSleep);
        allHoursWake = view.findViewById(R.id.hoursWake);
        allMinWake = view.findViewById(R.id.minWake);
        allHoursSleep = view.findViewById(R.id.hoursSleep);
        allMinSleep = view.findViewById(R.id.minutesSleep);
        saveSettings = view.findViewById(R.id.saveSettings);
        wake = view.findViewById(R.id.wake);
        bed = view.findViewById(R.id.bed);

        try {
            setSavedSpinnersDefault();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //wakeTime = (EditText) view.findViewById(R.id.wakeTime);
        setupSpinner();
        setupHourSpinner(allHoursWake, defaulth1);
        setupHourSpinner(allHoursSleep, defaulth2);
        setupMinSpinner(allMinWake, defaultm1);
        setupMinSpinner(allMinSleep, defaultm2);

        setSaveButtonListener();

        return view;
    }

    public void setSavedSpinnersDefault() throws ParseException {
        SharedPreferences sp = getActivity().getSharedPreferences(TC_SHARED_PREF, 0);

        String savedWake = sp.getString("wakeTime", null);
        String savedSleep = sp.getString("bedTime", null);
        String desiredSleep = sp.getString("desiredSleep", null);

        if (savedWake != null) {
            LocalTime wake = LocalTime.parse(savedWake);
            int tempa = wake.getHourOfDay();
            int tempb = wake.getMinuteOfHour();
            if (tempa <10){
                defaulth1 = "0"+ Integer.toString(tempa);
            } else {
                defaulth1 = Integer.toString(wake.getHourOfDay());
            }
            if (tempb <10){
                defaultm1 = "0"+Integer.toString(tempb);
            } else{
                defaultm1 = Integer.toString(wake.getMinuteOfHour());
            }
            Log.d("setDefaults", defaulth1);

        }
        if (savedSleep != null) {
            LocalTime sleep = LocalTime.parse(savedSleep);
            int tempa = sleep.getHourOfDay();
            int tempb = sleep.getMinuteOfHour();
            if (tempa <10){
                defaulth2 = "0"+ Integer.toString(tempa);
            } else {
                defaulth2 = Integer.toString(sleep.getHourOfDay());
            }
            if (tempb <10){
                defaultm2 = "0"+Integer.toString(tempb);
            } else{
                defaultm2 = Integer.toString(sleep.getMinuteOfHour());
            }
        }
        if (desiredSleep != null) {
            defaults = desiredSleep;
        }
    }

    //sets up the spinner with the different amount of hours of sleep
    public void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.sleepHours, android.R.layout.simple_spinner_item);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                R.array.sleepHours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        String defaultVal = defaults; //the value you want the position for
        int spinnerPosition = adapter.getPosition(defaultVal);

        numHours.setAdapter(adapter);

        //set the default according to value
        numHours.setSelection(spinnerPosition);
    }

    //sets up the spinner with the different amount of hours of sleep
    public void setupHourSpinner(Spinner current, String defaultValue){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.allHours, android.R.layout.simple_spinner_item);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                R.array.sleepHours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        String defaultVal = defaultValue; //the value you want the position for
        Log.d("defaultValueWas", defaultVal);
        int spinnerPosition = adapter.getPosition(defaultVal);

        current.setAdapter(adapter);

        //set the default according to value
        current.setSelection(spinnerPosition);
        bed.setError(null);
    }

    //sets up the spinner with the different amount of min of sleep
    public void setupMinSpinner(Spinner current, String defaultValue){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.allMin, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        String defaultVal = defaultValue; //the value you want the position for
        int spinnerPosition = adapter.getPosition(defaultVal);

        current.setAdapter(adapter);

        //set the default according to value
        current.setSelection(spinnerPosition);
        bed.setError(null);
    }

    public void setSaveButtonListener(){
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!TimeFunctions.bedTimeCalc(wakeTime, sleepTime)){
////                    bed.setError("Must sleep at least 6 hours before waking");
//                    return;
//                } else{
                final String wakeTime = allHoursWake.getSelectedItem().toString() + ":" + allMinWake.getSelectedItem().toString()+":00";
                final String sleepTime = allHoursSleep.getSelectedItem().toString() + ":" + allMinSleep.getSelectedItem().toString()+ ":00";
                final String desiredTime = numHours.getSelectedItem().toString();

                Log.d("wake", wakeTime);
                Log.d("sleep", sleepTime);
                SharedPreferences sp = getActivity().getSharedPreferences(TC_SHARED_PREF, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("wakeTime", wakeTime);
                editor.putString("bedTime", sleepTime);
                editor.putString("desiredSleep", desiredTime);
                editor.apply();
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();

 //               }
            }
        });
    }

}
