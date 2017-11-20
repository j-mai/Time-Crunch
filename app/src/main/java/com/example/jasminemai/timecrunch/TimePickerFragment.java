package com.example.jasminemai.timecrunch;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.Gravity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimePickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }
}
//
//    private TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            Message msg = new Message();
//            Bundle data = new Bundle();
//            data.putString("btn", "Selected Time - " + hourOfDay + ":" + minute);
//            msg.setData(data);
////            h.sendMessage(msg);
//        }
//        };
//    }


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState){
//        //Use the current time as the default values for the time picker
//        final Calendar c = Calendar.getInstance();
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//
//        //Create and return a new instance of TimePickerDialog
//        /*
//            public constructor.....
//            TimePickerDialog(Context context, int theme,
//             TimePickerDialog.OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView)
//
//            The 'theme' parameter allow us to specify the theme of TimePickerDialog
//
//            .......List of Themes.......
//            THEME_DEVICE_DEFAULT_DARK
//            THEME_DEVICE_DEFAULT_LIGHT
//            THEME_HOLO_DARK
//            THEME_HOLO_LIGHT
//            THEME_TRADITIONAL
//
//         */
//        return new TimePickerDialog(getActivity(), callback, hour, minute, false);
//
//    }



//    private void sendResult(int REQUEST_CODE) {
//        Intent intent = new Intent();
//        intent.putStringExtra(EDIT_TEXT_BUNDLE_KEY, editTextString);
//        getTargetFragment().onActivityResult(
//                getTargetRequestCode(), REQUEST_CODE, intent);
//    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_time_picker, container, false);
//    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        //Do something with the user chosen time
//        //Get reference of host activity (XML Layout File) TextView widget
////        TextView tv = (TextView) getActivity().findViewById(R.id.tv);
////        //Set a message for user
////        tv.setText("Your chosen time is...\n\n");
////        //Display the user changed time on TextView
////        tv.setText(tv.getText()+ "Hour : " + String.valueOf(hourOfDay)
////                + "\nMinute : " + String.valueOf(minute) + "\n");
//
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

