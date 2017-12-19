package com.example.jasminemai.timecrunch;

import android.util.Log;

import com.google.api.services.calendar.model.Event;

import org.joda.time.Interval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by jasminemai on 11/20/17.
 */

public class RankingAlgorithm {

    public static void matchingTasksAndTimeBlocks (List<Interval> freeTimes, Interval freeBlock,
                                                   int freeBlockIndex, Task task, ArrayList<Task> tasksList,
                                                   com.google.api.services.calendar.Calendar calendar,
                                                   String calendarId) throws IOException {

        org.joda.time.DateTime startDT = org.joda.time.DateTime.parse(freeBlock.getStart().toString());
        org.joda.time.DateTime endDT = startDT.plusMinutes(task.totalTime);

        Interval newTaskInterval = new Interval(startDT, endDT);

        Log.d("newtaskinterval", newTaskInterval.toString());

        //subtract from free interval
        Interval newFreeInterval = TimeFunctions.decreaseIntervalFromHead(freeBlock, newTaskInterval);

        if (newFreeInterval.toDuration().getStandardMinutes() > 0) {
            freeTimes.set(freeBlockIndex, newFreeInterval);
        } else {
            freeTimes.remove(freeBlockIndex);
        }

        Log.d("newtaskintervalStart", newTaskInterval.getStart().toString());
        Log.d("newtaskintervalEnd", newTaskInterval.getEnd().toString());

        task.setStartTime(newTaskInterval.getStart().toString());
        task.setEndTime(newTaskInterval.getEnd().toString());

        //add event to calendar, keeping track and deleting
        //from the tasks list.
        Event newlyadded = CalendarFunctions.addEventProperFormat(task, calendar, calendarId);
        Log.d("added event", newlyadded.toPrettyString());

        //remove from the list keeping track of the task
        tasksList.remove(task);

    }


}
