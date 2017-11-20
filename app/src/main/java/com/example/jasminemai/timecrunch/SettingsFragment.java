package com.example.jasminemai.timecrunch;

import android.app.DialogFragment;
import android.content.Context;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int REQUEST_CODE = 0;
    private static final String DIALOG_TIME = "date";

    Spinner numHours;
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
        wakeTime = (EditText) view.findViewById(R.id.wakeTime);
        setupSpinner();

        wakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment  = new TimePickerFragment();
                //newFragment.show(getActivity().getFragmentManager(), DIALOG_TIME);
//                newFragment.show(getSupportFragmentManager(), DIALOG_TIME);
                // if you are using the nested fragment then user the
                //newFragment.show(getChildFragmentManager(), DIALOG_TIME);
            }
        });

        return view;
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

        String defaultVal = "8"; //the value you want the position for
        int spinnerPosition = adapter.getPosition(defaultVal);

        numHours.setAdapter(adapter);

        //set the default according to value
        numHours.setSelection(spinnerPosition);
    }

//    public void setUpWakeTime(){
//
//        fragmentManager = getFragmentManager();
//
//
//        wakeTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newTime = new TimePickerFragment();
//                newTime.show(getFragmentManager(), "timePicker");
//            }
//        });
//    }

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
