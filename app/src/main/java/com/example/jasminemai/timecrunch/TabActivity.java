package com.example.jasminemai.timecrunch;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/*
    Much of the code working with the google API is based on the Google API Calendar
    Quickstart code
 */

public class TabActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    //private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private ArrayList<Fragment> fragments;
    private TabViewPagerAdapter mViewPagerAdapter;

    //auth2.0 credential to access user's calendar
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    public static String TC_SHARED_PREF = "my_sharedpref";
    public static String EVENTS_SHARED_PREF = "events_sharedpref";

    private static String wakeTime;
    private static String sleepTime;

    private static String calendarId = "primary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_tab);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        fragments = new ArrayList<Fragment>();
        fragments.add(new ToDoFragment());
        fragments.add(new SettingsFragment());

        //Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), fragments);

        mViewPager.setAdapter(mViewPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //initialize the credential object
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, "There is no network connection available", Toast.LENGTH_LONG);
        } else {
            new MakeRequestTask(mCredential).execute();
        }

    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                Log.d("account name", "null account name, please choose");
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }

        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            Log.d("requesting permissions", "try to access Google acount");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "Please Install Google Play Services", Toast.LENGTH_LONG);
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                //after user has chosen an account
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        Log.d("account name", "got account name");
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                TabActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /*
        This is an AsyncTask to make the API calls to Google Calendar in a background thread.
     */
    private class MakeRequestTask extends AsyncTask<Void, String, String> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            Log.d("Credential", credential.getSelectedAccountName());
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Time Crunch")
                    .build();

        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * This is the main algorithm of the program. The following are the steps:
         * 1. Call FreeBusy Query to the Google calendar of the user to get the
         * times that the user is busy
         * 2. Calculate the intervals of time that the user is free
         * 3. Convert the task information saved in shared preferences into proper data structures,
         * with breakable events broken up into several smaller tasks of 1 hour and smaller
         * 4. If there are tasks that cannot be broken up and are over an hour,
         * we put those in first, matching the
         * longest task with the longest free time chunks
         * 5. We update the data structure of tasks that keep track of what we added
         * 6. When we are done with the long tasks, we move on the the hour task and short tasks.
         * 7. For short tasks, we compare the times available and see if there are tasks the
         * that fit the time period
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private String getDataFromApi() throws IOException, JSONException {

            setWakeSleepTimes();
            LocalDate today = new LocalDate(DateTimeZone.getDefault());
            LocalTime currentTime = new LocalTime(DateTimeZone.getDefault());
            org.joda.time.DateTime currentDateTime = new org.joda.time.DateTime(DateTimeZone.getDefault());
            FreeBusyResponse fbResponse;

            //first, see when the user is free
//            if (currentTime.isAfter(new LocalTime(wakeTime))) {
//                Log.d("freetimes", "using current time");
//                fbResponse = CalendarFunctions.getFreeBusy(mService, today.toString(),
//                        currentTime.toString(), today.toString(), sleepTime, calendarId);
//            } else {
//                Log.d("freetimes", "using waketime");
//                fbResponse = CalendarFunctions.getFreeBusy(mService, today.toString(),
//                        wakeTime, today.toString(), sleepTime, calendarId);
//            }
//
//            List<TimePeriod> busyTimes;


            List<Interval> freeTimes;

            //error check on free/Busy response
//            if ((fbResponse.getGroups() != null && fbResponse.getGroups().get(calendarId).getErrors()
//                    != null) || fbResponse.getCalendars().get(calendarId).getErrors() != null) {
//                Log.e("freeBusy Error", "could not get free busy");
//                Log.e("freeBusy Error", fbResponse.toPrettyString());
//                return null;
//            } else {
//                Log.d("FreeBusy", "gotBusy!");
//                busyTimes = fbResponse.getCalendars().get(calendarId).getBusy();
//                freeTimes = TimeFunctions.getFreeTimes(wakeTime, sleepTime,
//                        busyTimes);
//            }

            freeTimes = calculateFreeTimes(currentDateTime, mService);

            //if there are free times
            if (freeTimes != null) {
                //get shared Preferences, where task information is
                SharedPreferences taskSP = getSharedPreferences(TC_SHARED_PREF, Context.MODE_PRIVATE);
                String task = taskSP.getString("tasksMap", "");

                SharedPreferences eventsSP = getSharedPreferences(EVENTS_SHARED_PREF, Context.MODE_PRIVATE);

                if (!task.equals("")) {

                    String lastInput = eventsSP.getString("lastInputted", "");

                    HashMap<String, ArrayList<EventInfo>> eventsMap;
                    HashMap<String, ArrayList<JSONObject>> eventsJsonMap;

                    LocalDate todayDate = new LocalDate();

                    if (lastInput.equals("") || todayDate.isAfter(new LocalDate(lastInput))) {
                        eventsMap = new HashMap<String, ArrayList<EventInfo>>();
                        eventsJsonMap = new HashMap<>();
                    } else {
                        String events = eventsSP.getString("events", "");
                        eventsMap = Converter.spToEventInfo(events);
                        eventsJsonMap = Converter.spToEventJSON(events);
                    }

                    //convert information in Shared Preferences into Map
                    Map<String, JSONObject> tasksAsJSON = Converter.spToMap(task);

                    //Create temporary map of type to task objects
                    ArrayList<Task> allTasks = Converter.splitUpTasks(tasksAsJSON, eventsMap, mService, calendarId,
                            currentDateTime);

                    //show error if there is not enough free time
                    if (TimeFunctions.totalFreeTime(freeTimes) < TimeFunctions.totalTaskTimeReq(tasksAsJSON)) {
                        publishProgress("NOTIME");

                    } else {


                        boolean overHourTask = true;

                        //while there are still tasks over one hour
                        while (overHourTask) {
                            //get the largest schedule block and fit it into the largest time block
                            Interval largestFreeTime = TimeFunctions.getLargestTimeBlock(freeTimes);
                            int largestFreeTimeIndex = TimeFunctions.getLargestTimeBlockIndex(freeTimes);
                            Task longestTask = TimeFunctions.getLongestTaskBlock(allTasks);

                            if (longestTask == null) {
                                overHourTask = false;
                            } else if (longestTask.getTotalTime() > 60) {
                                //go with longer tasks first as priority
                                if (longestTask.getTotalTime() <= largestFreeTime.toDuration().getStandardMinutes()) {

                                    //fits task into block, shifting free times accordingly
                                   JSONObject event = matchingTasksAndTimeBlocks(freeTimes, largestFreeTime,
                                            largestFreeTimeIndex, longestTask, longestTask.getTotalTime(), allTasks, mService, calendarId);

                                   if (eventsJsonMap.containsKey(longestTask.getTaskName())) {
                                       eventsJsonMap.get(longestTask.getTaskName()).add(event);
                                   } else {
                                       ArrayList<JSONObject> eventJsonList = new ArrayList<>();
                                       eventJsonList.add(event);
                                       eventsJsonMap.put(longestTask.getTaskName(), eventJsonList);
                                   }

                                } else {
                                    publishProgress("TASKTOOLONG");
                                    TimeFunctions.removeFromTempList(allTasks, longestTask);
                                }

                            } else {
                                overHourTask = false;
                            }
                        }

                        Log.d ("added long tasks", "going to short tasks");

                        String lastInputType = null;
                        String lastTaskName = null;
                        Interval lastTaskInterval = null;
                        //go through remaining list of events
                        while (!allTasks.isEmpty() && freeTimes.size() > 0) {
                            Interval interval = freeTimes.get(0);
                            Task bestTaskFit = null;
                            for (Task t : allTasks) {
                                if (t.getTotalTime() <= interval.toDuration().getStandardMinutes()) {
                                    if (bestTaskFit == null) {
                                        bestTaskFit = t;
                                    }

                                    if (t.getTotalTime() >= bestTaskFit.getTotalTime()) {
                                        if (t.getType().equals("Study") && lastTaskName != null && !lastTaskName.equals(t.getTaskName())) {
                                            bestTaskFit = t;
                                            break;
                                        } else {
                                            bestTaskFit = t;
                                        }
                                    }
                                }
                            }

                            if (bestTaskFit != null) {
                                lastInputType = bestTaskFit.getType();
                                lastTaskName = bestTaskFit.getTaskName();
                                int totalTime = Integer.parseInt(tasksAsJSON.get(lastTaskName).get("totalTime").toString());
                                JSONObject event = matchingTasksAndTimeBlocks(freeTimes, interval, 0,
                                        bestTaskFit, totalTime, allTasks, mService, calendarId);

                                if (eventsJsonMap.containsKey(bestTaskFit.getTaskName())) {
                                    eventsJsonMap.get(bestTaskFit.getTaskName()).add(event);
                                } else {
                                    ArrayList<JSONObject> eventJsonList = new ArrayList<>();
                                    eventJsonList.add(event);
                                    eventsJsonMap.put(bestTaskFit.getTaskName(), eventJsonList);
                                }

                            } else {
                                freeTimes.remove(0);
                            }
                        }

                        if (allTasks.isEmpty()) { //all tasks scheduled
                            saveEventsToSP(eventsJsonMap, today);
                            return "FINISHED";

                        } else if (freeTimes.size() <= 0) { //error, too many tasks
                            saveEventsToSP(eventsJsonMap, today);
                            publishProgress("TOOMANYTASKS");
                        }

                    }

                }

            } else {
                Log.e("freetimes", "freeTimes is null");
            }

            return null;
        }

        /*
        Makes toasts to let users know that their schedule/pattern is not acheivable
         */
        @Override
        protected void onProgressUpdate(String... params) {
           if (params[0].equals("NOTIME")) {
            Toast.makeText(getApplicationContext(),
                    "You don't have enough free time to do everything", Toast.LENGTH_LONG).show();
           } else if (params[0].equals("TASKTOOLONG")) {
               Toast.makeText(getApplicationContext(),
                       "One of your tasks is too long", Toast.LENGTH_LONG).show();
           } else if (params[0].equals("TOOMANYTASKS")) {
               Toast.makeText(getApplicationContext(),
                       "Sorry, we couldn't fit all of your tasks in!", Toast.LENGTH_LONG).show();
           }
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.hide();
            if (output != null) {
                Log.d("postExecute", output);
            }

        }

        @Override
        protected void onCancelled() {
            mProgress.show();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            TabActivity.REQUEST_AUTHORIZATION);
                } else {
                    if(mLastError.getMessage() != null){
                        Log.e("error", mLastError.getMessage());
                    }else{
                        Log.e("error", "mLastError is null");
                    }
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                }
            } else {
                Log.d("cancelled", "request cancelled");
//                mOutputText.setText("Request cancelled.");
            }
        }
    }


    //makes sure the wake and sleep times are set properly
    private void setWakeSleepTimes() {

        SharedPreferences settingsSP = getSharedPreferences(TC_SHARED_PREF, Context.MODE_PRIVATE);
        wakeTime = settingsSP.getString("wakeTime", "");
        sleepTime = settingsSP.getString("bedTime", "");

        if (wakeTime.equals("")) {
            wakeTime = "06:00:00";
        }

        if (sleepTime.equals("") || LocalTime.parse(sleepTime).isAfter(LocalTime.parse("23:59:59"))) {
            sleepTime = "23:59:59";
        }

        Log.d("wakeTime", wakeTime);
        Log.d("sleepTime", sleepTime);

    }

    //add event to calendar and delete from temporary task list properly
    public JSONObject matchingTasksAndTimeBlocks (List<Interval> freeTimes, Interval freeBlock,
                                                        int freeBlockIndex, Task task, int totalTime,
                                                        ArrayList<Task> tasksList,
                                                        com.google.api.services.calendar.Calendar calendar,
                                                        String calendarId) throws IOException {

        org.joda.time.DateTime startDT = org.joda.time.DateTime.parse(freeBlock.getStart().toString());
        org.joda.time.DateTime endDT = startDT.plusMinutes(task.getTotalTime());

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
        //from the temp list.
        Event newlyadded = CalendarFunctions.addEventProperFormat(task, calendar, calendarId);
        Log.d("added event", newlyadded.toPrettyString());

        //remove from the list keeping track of the task
        tasksList.remove(task);

        if (newlyadded != null && newlyadded.getId() != null) {
            Log.d("makingEventInfo", newlyadded.getStart().getDateTime().toString());
            Log.d("makingEventInfo", newlyadded.getEnd().getDateTime().toString());
            JSONObject event = Converter.stringsToEventJSON(newlyadded.getSummary(),
                    totalTime, task.getBreakUp(), task.getTotalTime(),
                    newlyadded.getStart().getDateTime().toString(),
                    newlyadded.getEnd().getDateTime().toString(),
                    newlyadded.getId(), task.getType());

            return event;

        } else {
            return null;
        }

    }

    //save calendar-inputted events as strings in shared preferences

    private void saveEventsToSP (HashMap<String, ArrayList<JSONObject>> eventsJsonMap, LocalDate today) {
        String eventsString = Converter.eventsMapToString(eventsJsonMap);
        SharedPreferences eventsSP = getSharedPreferences(EVENTS_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor eventsEdit = eventsSP.edit();
        eventsEdit.putString("lastInputted", today.toString());
        eventsEdit.putString("events", eventsString);
    }

    private List<Interval> calculateFreeTimes (org.joda.time.DateTime currentDateTime, Calendar calendar) throws IOException {

        FreeBusyResponse fbResponse;
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalTime currentTime = currentDateTime.toLocalTime();

        List<Interval> freeTimes = null;

        //first, see when the user is free
        if (currentTime.isAfter(new LocalTime(wakeTime))) {
            Log.d("freetimes", "using current time");
            try {
                fbResponse = CalendarFunctions.getFreeBusy(calendar, currentDate.toString(),
                        currentTime.toString(), currentDate.toString(), sleepTime, calendarId);
            } catch (IOException e) {
                Log.e("calculateFreeTimes", e.toString());
                fbResponse = null;
                freeTimes = null;
            }
        } else {
            Log.d("freetimes", "using waketime");
            try {
                fbResponse = CalendarFunctions.getFreeBusy(calendar, currentDate.toString(),
                        wakeTime, currentDate.toString(), sleepTime, calendarId);
            } catch (IOException e) {
                Log.e("calculateFreeTimes", e.toString());
                fbResponse = null;
                freeTimes = null;
            }

        }

        List<TimePeriod> busyTimes;

        //error check on free/Busy response
        if (fbResponse != null) {
            if ((fbResponse.getGroups() != null && fbResponse.getGroups().get(calendarId).getErrors()
                    != null) || fbResponse.getCalendars().get(calendarId).getErrors() != null) {
                Log.e("freeBusy Error", "could not get free busy");
                Log.e("freeBusy Error", fbResponse.toPrettyString());
                freeTimes = null;

            } else {
                Log.d("FreeBusy", "gotBusy!");
                busyTimes = fbResponse.getCalendars().get(calendarId).getBusy();

                if (currentTime.isAfter(new LocalTime(wakeTime))) {
                    Log.d("freebusy", "using current time");
                    freeTimes = TimeFunctions.getFreeTimes(currentTime.toString(), sleepTime,
                            busyTimes);
                    Log.d("freeTimes", "finished freeTimes");
                } else {
                    Log.d("freeBusy", "using wakeTime");
                    freeTimes = TimeFunctions.getFreeTimes(wakeTime, sleepTime,
                            busyTimes);
                }
            }
        }

        return freeTimes;
    }

}






