package com.example.jasminemai.timecrunch;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {
    public static String TC_SHARED_PREF = "my_sharedpref";

    View todoFragView;
    private ListView mListView;


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

        getTasksList();

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

    /*
 * Creates the list of tasks in this tab
 */
    public void getTasksList() {
        SharedPreferences sp = getActivity().getSharedPreferences(TC_SHARED_PREF, 0);
        String tasks = sp.getString("tasksMap",null);
        HashMap<String, JSONObject> map = Converter.spToMap(tasks);
        ArrayList<Task> taskArray = Converter.mapToArray(map);

        if (!taskArray.isEmpty()){
            mListView = todoFragView.findViewById(R.id.todoList);
            Task[] newTaskArray = taskArray.toArray(new Task[taskArray.size()]);
            taskAdapter taskAdapter = new taskAdapter(getContext(), R.layout.todo_item, newTaskArray);
            mListView.setAdapter(taskAdapter);
        }else {
            Log.d("no tasks", "taskListEmpty");
        }
    }

    //create the to-do list
    public class taskAdapter extends ArrayAdapter<Task> {

        public taskAdapter(@NonNull Context context, int resource, @NonNull Task[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Task myTask = getItem(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.todo_item, null);
            }

            //Create variables for each thing in the todo_item layout
            TextView eventName = convertView.findViewById(R.id.event);
            TextView fromTime = convertView.findViewById(R.id.start);
            ImageButton delete = convertView.findViewById(R.id.delete);
            //delete.setImageDrawable(getResources().getDrawable(R.drawable.delete));

            eventName.setText(myTask.name);
            String end = " to " + myTask.endDate;
            if (myTask.endDate.equals("")){
                end = "...";
            }
            fromTime.setText(myTask.startDate + end);
            return convertView;
        }
    }

}

