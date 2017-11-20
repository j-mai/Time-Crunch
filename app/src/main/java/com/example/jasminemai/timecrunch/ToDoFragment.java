package com.example.jasminemai.timecrunch;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {

    View todoFragView;

//    private OnFragmentInteractionListener mListener;

    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        todoFragView = inflater.inflate(R.layout.fragment_to_do, container, false);
        onNewTaskButtonClicked(todoFragView);
        onAuthButtonClicked(todoFragView);
        return todoFragView;
    }

    //Sends an intent to the NewTask, allows user to add another task
    public void onNewTaskButtonClicked(View v) {
        final FloatingActionButton addTask = v.findViewById(R.id.addTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent task = new Intent(getContext(), NewTask.class);
                startActivity(task);
            }
        });
        Log.d("addTask", "Create a new task");

    }

    public void onAuthButtonClicked(View v) {
        final Button button = v.findViewById(R.id.Schedule);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                ((TabActivity) getActivity()).getResultsFromApi();
                button.setEnabled(true);
            }
        });

    }
}

