package com.example.jasminemai.timecrunch;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Time;


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

        //wakeTime = (EditText) view.findViewById(R.id.wakeTime);
        setupSpinner();
        setupHourSpinner(allHoursWake);
        setupHourSpinner(allHoursSleep);
        setupMinSpinner(allMinWake);
        setupMinSpinner(allMinSleep);

        setSaveButtonListener();

        return view;
    }
//
//    public void setSavedSpinners(){
//        SharedPreferences sp = getActivity().getSharedPreferences(TC_SHARED_PREF, 0);
//
//        String savedWake = sp.getString("wakeTime", null);
//        String savedSleep = sp.getString("bedTime", null);
//        if (savedWake != null){
//
//        }
//    }

    //sets up the spinner with the different amount of hours of sleep
    public void setupSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.sleepHours, android.R.layout.simple_spinner_item);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                R.array.sleepHours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        String defaultVal = "8"; //the value you want the position for
        int spinnerPosition = adapter.getPosition(defaultVal);

        numHours.setAdapter(adapter);

        //set the default according to value
        numHours.setSelection(spinnerPosition);
    }

    //sets up the spinner with the different amount of hours of sleep
    public void setupHourSpinner(Spinner current){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.allHours, android.R.layout.simple_spinner_item);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                R.array.sleepHours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        String defaultVal = "00"; //the value you want the position for
        int spinnerPosition = adapter.getPosition(defaultVal);

        current.setAdapter(adapter);

        //set the default according to value
        current.setSelection(spinnerPosition);
        bed.setError(null);
    }

    //sets up the spinner with the different amount of min of sleep
    public void setupMinSpinner(Spinner current){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.allMin, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        String defaultVal = "00"; //the value you want the position for
        int spinnerPosition = adapter.getPosition(defaultVal);

        current.setAdapter(adapter);

        //set the default according to value
        current.setSelection(spinnerPosition);
        bed.setError(null);
    }

    public void setSaveButtonListener(){
        final String wakeTime = allHoursWake.getSelectedItem().toString() + ":" + allMinWake.getSelectedItem().toString()+":00";
        final String sleepTime = allHoursSleep.getSelectedItem().toString() + ":" + allMinSleep.getSelectedItem().toString()+ ":00";

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TimeFunctions.bedTimeCalc(wakeTime, sleepTime)){
                    bed.setError("Must sleep at least 6 hours before waking");
                    return;
                } else{
                    SharedPreferences sp = getActivity().getSharedPreferences(TC_SHARED_PREF, 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("wakeTime", wakeTime);
                    editor.putString("bedTime", sleepTime);
                    editor.apply();
                }
            }
        });
    }


//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


}
