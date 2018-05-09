package com.notifellow.su.notifellow;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddReminder extends AppCompatActivity {

    private Button locationButton, refreshWifi;

    FloatingActionButton fab;
    private String publicity;
    TextView titleTextView, startDateTextView, endDateTextView, remindDateTextView, notesTextView;
    protected static TextView locationTv;

    int startHour, startMinute, endHour, endMinute, remindHour, remindMinute;
    int startDay, startMonth, startYear, endDay, endMonth, endYear, remindDay, remindMonth, remindYear;

    boolean isStartChosen = false;
    boolean isEndChosen = false;

    boolean reminderAdded = false;

    String wifiName;
    Calendar calendar;
    Spinner wifiSpinner;
    NetworkInfo netInfo;


    /**
     * Marker Location variable.
     *
     * Usage:
     * 1st option is doing it with object.
     *
     * 2nd option is doing it with helper functions of MapPlaces class.
     * MapsPlaces mapPlaces;
     *
     * String placeName = String.format("%s", markerPlace.getName());
     * String PlaceName = mapPlaces.getPlaceName();
     *
     * String latitude = String.valueOf(markerPlace.getLatLng().latitude);
     * String longitude = mapPlaces.getLatLng().latitude;
     *
     * String longitude = String.valueOf(markerPlace.getLatLng().longitude);
     * String longitude = mapPlaces.getLatLng().longitude;
     *
     * String placeID = String.valueOf(markerPlace.getId());
     * String placeID = mapPlaces.getPlaceID();
     *
     * Fallowing functionality returns full address of marker including zip-code and country name.
     * String address = String.format("%s", markerPlace.getAddress());
     * String address = mapPlaces.getAddress();
     *
     */
    static Place markerPlace = null;

    private Switch switchPublic;
    private Switch switchStat;


    String time, date;

    private SharedPreferences shared;

    RequestQueue queue;
    private ProgressDialog progressDialog;
    Dialog InfoDialog;

    private EditText comment;
    private TextView commentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        setTitle("Add Reminder");


        //// INFO DIALOG IMPLEMENTATION START /////
        InfoDialog = new Dialog(AddReminder.this);
        InfoDialog.setContentView(R.layout.add_reminder_info_dialog);
        InfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES
        //// INFO DIALOG IMPLEMENTATION END /////


        //// Comment Section ///// /
        comment = findViewById(R.id.makeInitialComment); // text of comment, you will take the comment data from here.
        commentTitle = findViewById(R.id.reminderComment);  // title, you dont have any business with this
        // i will disable comments if visible to friends is not setted true.
        // because comment is needed for feed, if a task will not be visible to others,
        // comments is not needed.
           comment.setEnabled(false);
           commentTitle.setTextColor(getResources().getColor(R.color.colorGray));
        /// end of comment section ///

        refreshWifi = findViewById(R.id.RefreshBtn);
        calendar = Calendar.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fabSet);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addTask();
                    if(reminderAdded)
                        finish(); //navigates to schedule.

                }
            });
        }

        switchPublic = findViewById(R.id.switchPublic);
        publicity = "0";
        switchPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publicity = "1";
                    // make comment section enabled
                    comment.setClickable(true);
                    comment.setEnabled(true);
                    comment.setFocusable(true);
                    commentTitle.setTextColor(getResources().getColor(R.color.colorBlue));
                    comment.requestFocus();
                    comment.setPressed(true);

                } else {
                    publicity = "0";
                    //Make comment section grayed out and not editable
                    comment.setText("");
                    comment.setEnabled(false);
                    commentTitle.setTextColor(getResources().getColor(R.color.colorGray));
                }
            }
        });




        titleTextView = findViewById(R.id.reminderTxt);

        wifiSpinner = findViewById(R.id.spinnerWiFi);
        final List<String> list = new ArrayList<String>();
        list.add("Please Click Refresh");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        wifiSpinner.setAdapter(adapter);
        wifiName = ""; //By Default, set WiFi name := ""
        refreshWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWifiList(); //Get saved WiFi list and add it to Spinner
            }
        });


        startDateTextView = findViewById(R.id.startDateTxt);
        startDateTextView.setOnClickListener(new View.OnClickListener() { //When user wants to set Start Date
            @Override
            public void onClick(View view) {
                setStart();
            }
        });


        endDateTextView = findViewById(R.id.endDateTxt);
        endDateTextView.setOnClickListener(new View.OnClickListener() { //When user wants to set End Date
            @Override
            public void onClick(View view) {
                setEnd();
            }
        });


        remindDateTextView = findViewById(R.id.RemindMeAtTxt);
        remindDateTextView.setOnClickListener(new View.OnClickListener() { //When user wants to set Remind Date
            @Override
            public void onClick(View view) {
                setRemind();
            }
        });

        // Choose location from map
        locationTv = findViewById(R.id.LocationTxt);
        locationButton = findViewById(R.id.LocationBtn);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReminder.this, MapsPlaces.class);
                startActivity(intent);
            }
        });

        notesTextView = findViewById(R.id.takeNotesTxt);

        createAlarmCodeFile();

        Main.reminderFragmentVisited = true; //TODO Since its not fragment anymore, you  can change the name
    }

    private void createAlarmCodeFile() {
        File file = this.getFileStreamPath("myTextFile.txt");
        if (file == null || !file.exists()) {
            String msg = String.valueOf(0);
            try {
                FileOutputStream fileOutputStream = this.openFileOutput("myTextFile.txt", MODE_PRIVATE);
                fileOutputStream.write(msg.getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateAlarmCode(int code) {
        String msg = String.valueOf(code);
        try {
            FileOutputStream fileOutputStream = this.openFileOutput("myTextFile.txt", MODE_PRIVATE);
            fileOutputStream.write(msg.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAlarmCode() {
        StringBuilder stringBuffer = new StringBuilder();
        try {
            String temp;
            FileInputStream fileInputStream = this.openFileInput("myTextFile.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp);
            }

            return Integer.parseInt(stringBuffer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(stringBuffer.toString());
    }

    public void createTask(int alarmCode) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, remindYear);
        cal.set(Calendar.MONTH, remindMonth);
        cal.set(Calendar.DAY_OF_MONTH, remindDay);
        cal.set(Calendar.MINUTE, remindMinute);
        cal.set(Calendar.HOUR_OF_DAY, remindHour);
        cal.set(Calendar.SECOND, 0);

        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd");
        String remind_date = dateFormatLocal.format(cal.getTime());
        SimpleDateFormat endFormatLocal = new SimpleDateFormat("HH:mm");
        String remind_time = endFormatLocal.format(cal.getTime());

        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ID", String.valueOf(alarmCode));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmCode, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        cal.set(Calendar.YEAR, startYear);
        cal.set(Calendar.MONTH, startMonth);
        cal.set(Calendar.DAY_OF_MONTH, startDay);
        cal.set(Calendar.MINUTE, startMinute);
        cal.set(Calendar.HOUR_OF_DAY, startHour);
        cal.set(Calendar.SECOND, 0);

        String start_date = dateFormatLocal.format(cal.getTime());
        String start_time = endFormatLocal.format(cal.getTime());

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date startDateObject = new Date();
        try {
            startDateObject = formatter.parse(start_date + " " + start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.set(Calendar.YEAR, endYear);
        cal.set(Calendar.MONTH, endMonth);
        cal.set(Calendar.DAY_OF_MONTH, endDay);
        cal.set(Calendar.MINUTE, endMinute);
        cal.set(Calendar.HOUR_OF_DAY, endHour);
        cal.set(Calendar.SECOND, 0);

        String end_date = dateFormatLocal.format(cal.getTime());
        String end_time = endFormatLocal.format(cal.getTime());

        Date endDateObject = new Date();
        try {
            endDateObject = formatter.parse(end_date + " " + end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        shared = this.getSharedPreferences("shared", MODE_PRIVATE);
        String email = shared.getString("email", "null");//GET EMAIL FROM SHARED

        String location;
        try{
            location = String.valueOf(markerPlace.getAddress());
        }
        catch (Exception ex){
            location = "default";
        }

        Main.schema.insertData(String.valueOf(alarmCode), titleTextView.getText().toString(),
                start_date, start_time, end_date, end_time, remind_date, remind_time,
                location, wifiName, notesTextView.getText().toString(), email);

        end_date = ScheduleFragment.formatDate(end_date);
        remind_date = ScheduleFragment.formatDate(remind_date);

        ScheduleFragment.taskList.add(new Task(String.valueOf(alarmCode), titleTextView.getText().toString(), start_date + "\t\t\t" + start_time, end_time + "\t\t\t" + end_date, remind_time + "\t\t\t" + remind_date, location, wifiName, notesTextView.getText().toString()));
        ScheduleFragment.taskAdapter.notifyDataSetChanged();

        updateAlarmCode(++alarmCode);


        if (netInfo != null) {
            final String startDateString = startDateObject.toString();
            final String endDateString = endDateObject.toString();

            queue = Volley.newRequestQueue(this);
            SharedPreferences settings = this.getSharedPreferences("shared", MODE_PRIVATE);
            final String mapTypeString = settings.getString("email", ""); //get username
            progressDialog = new ProgressDialog(this);

            final int finalAlarm = alarmCode;
            final String loc = location;

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/addAlarm", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    if (response.equals("CREATED 201")) {
                        //SUCCESFULL QUERY WILL EXECUTE FOLLOWING LINES
                    } else if (response.equals("QUERY ADD FAIL") || response.equals("Query Error Occured!")) {
                        //Query fail
                        Toast.makeText(getBaseContext(), "" + "" + "Query add fail!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        //mongodb connection fail
                        Toast.makeText(getBaseContext(), "" + "Cannot connect to mongoDB " + "!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //This code is executed if there is an error.
                    Toast.makeText(getBaseContext(), "" + "" + " Cant connect to the server!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                   /* Log.i("TEST: ", mapTypeString);
                    Log.i("TEST: ", String.valueOf(finalAlarm));
                    Log.i("TEST: ", titleTextView.getText().toString());
                    Log.i("TEST: ", loc);
                    Log.i("TEST: ", startDateString);
                    Log.i("TEST: ", endDateString);
                    Log.i("TEST: ", publicity);
                    */
                    MyData.put("UserID", mapTypeString); //Dont change THIS LINE!!!
                    MyData.put("id", String.valueOf(finalAlarm)); //Event ID
                    MyData.put("title", titleTextView.getText().toString()); //Event Title
                    MyData.put("location", loc); //Event Location
                    MyData.put("startDate", startDateString); //Start of event
                    MyData.put("endDate", endDateString); //End of event
                    MyData.put("public", publicity); //Public or not
                    MyData.put("initialComment", comment.getText().toString()); //initial comment
                    return MyData;
                }
            };
            queue.add(MyStringRequest);
        }

        String[] splitted = remind_date.split("-");
        remind_date = splitted[2] + "-" + splitted[1] + "-" + splitted[0];

        Toast.makeText(getBaseContext(), "Alarm is set for: " + remind_time + " " + remind_date, Toast.LENGTH_LONG).show();

        wifiName = "";
        markerPlace = null;
        reminderAdded = true;
    }

    //ToDo: This function will store information about the reminder in local database
    public void addTask() {
        if(titleTextView.getText().toString().equals("")){
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "You should specify a title!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
            return;
        }

        if (endYear != 0 && endYear <= startYear && endMonth <= startMonth && endDay <= startDay && endHour <= startHour && endMinute <= startMinute) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "End date is not correct!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
            return;
        }

        final int alarmCode = getAlarmCode();

        if (startDateTextView.getText().toString().equals("Not Specified.") && endDateTextView.getText().toString().equals("Not Specified.") && remindDateTextView.getText().toString().equals("Not Specified.")) {
            if (!wifiName.equals("")) {
                //THIS ALARM WILL BE FIRED WHEN THE USER CONNECTS TO CHOSEN WIFI

                shared = this.getSharedPreferences("shared", MODE_PRIVATE);
                String email = shared.getString("email", "null");//GET EMAIL FROM SHARED

                Main.schema.insertForWifiListener(String.valueOf(alarmCode), titleTextView.getText().toString(), wifiName, notesTextView.getText().toString(), email);
                int code = alarmCode;
                updateAlarmCode(code);

                Intent intent = new Intent(this, ServiceListenWiFi.class);
                this.startService(intent);

                Toast.makeText(this, "Alarm set for: " + wifiName, Toast.LENGTH_SHORT).show();

                reminderAdded = true;
            }
            else{
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "You should select a WiFi name, in order to set a WiFi based reminder!", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                snackbar.show();
            }
            /*else if (markerPlace != null) {
                //THIS ALARM WILL BE FIRED WHEN THE USER ONLY CHOOSE A LOCATION FOR ALARM

                Schedule.schema.insertForLocationListener(String.valueOf(alarmCode), titleTextView.getText().toString(), markerPlace.getLatLng().toString(), notesTextView.getText().toString());

                //Intent intent = new Intent(getContext(), GPS_Service.class);
                //getContext().startService(intent);

                Toast.makeText(getContext().getApplicationContext(), "Alarm has set for chosen Location!", Toast.LENGTH_SHORT).show();

            }*/
        } else if (!startDateTextView.getText().toString().equals("Not Specified.") && !endDateTextView.getText().toString().equals("Not Specified.") && !remindDateTextView.getText().toString().equals("Not Specified.")) {


            ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = conMgr.getActiveNetworkInfo();

            if (netInfo == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("NO INTERNET CONNECTION");
                builder.setMessage("No Internet connection detected. If you proceed, your friends won't see this event!");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createTask(alarmCode);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                createTask(alarmCode);
            }

        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "INVALID DATE. Please check the date and time for the reminder", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
        }
    }

    public void getWifiList() {
        WifiManager wifimanager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE); //Wifi Manager Object

        try {
            List<WifiConfiguration> myList = wifimanager.getConfiguredNetworks(); //Get List of saved SSID's

            String[] tempStr = new String[myList.size()];
            ArrayList<String> wifiArr = new ArrayList<String>();


            for (WifiConfiguration item : myList) { //For each SSID
                wifiArr.add(item.SSID); //Add SSID to ArrayList
            }

            tempStr = wifiArr.toArray(tempStr); //ArrayList to array

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tempStr); //Adapter

            wifiSpinner.setAdapter(arrayAdapter);

            wifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    wifiName = wifiSpinner.getItemAtPosition(i).toString();
                    Toast.makeText(getBaseContext(), "You have selected " + wifiName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (Exception ex) { //If WiFi is turned off
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "Please turn on your WiFi, in order to see your saved WiFi's!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
        }
    }

    public void openStartTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (startDay == calendar.get(Calendar.DAY_OF_MONTH) && startMonth == calendar.get(Calendar.MONTH) && startYear == calendar.get(Calendar.YEAR)) {
                    if (hour <= calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), "You cannot choose time before now!", Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbar.show();
                        startDateTextView.setText("Not Specified");
                    } else {
                        time = Integer.toString(hour) + ":" + String.format("%02d", minute);
                        startHour = hour;
                        startMinute = minute;
                        startDateTextView.setText(date + "\t\t\t" + time);
                        isStartChosen = true;
                    }
                } else {
                    time = Integer.toString(hour) + ":" + String.format("%02d", minute);
                    startHour = hour;
                    startMinute = minute;
                    startDateTextView.setText(date + "\t\t\t" + time);
                    isStartChosen = true;
                }

            }
        }, startHour, startMinute, true);
        timePickerDialog.show();
    }

    public void setStart() {
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMinute = calendar.get(Calendar.MINUTE);

        startDay = calendar.get(Calendar.DAY_OF_MONTH);
        startMonth = calendar.get(Calendar.MONTH);
        startYear = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { //Open Dialog for Picking Date
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date = Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year); //+1 needed since Months start from 0 in Calendar Objects
                startDay = day;
                startMonth = month;
                startYear = year;
                //startDateTextView.setText(date);
                openStartTimePicker(); //If the user chooses date, then open a Dialog for Picking Time
            }
        }, startYear, startMonth, startDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void openEndTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (endDay == calendar.get(Calendar.DAY_OF_MONTH) && endMonth == calendar.get(Calendar.MONTH) && endYear == calendar.get(Calendar.YEAR)) {
                    if (hour <= calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), "You cannot choose time before now!", Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbar.show();
                        endDateTextView.setText("Not Specified");
                    } else {
                        String time = Integer.toString(hour) + ":" + String.format("%02d", minute);
                        endHour = hour;
                        endMinute = minute;
                        endDateTextView.setText(date + "\t\t\t" + time);
                        isEndChosen = true;
                    }
                } else {
                    String time = Integer.toString(hour) + ":" + String.format("%02d", minute);
                    endHour = hour;
                    endMinute = minute;
                    endDateTextView.setText(date + "\t\t\t" + time);
                    isEndChosen = true;
                }
            }
        }, endHour, endMinute, true);
        timePickerDialog.show();
    }

    public void setEnd() {
        if (!isStartChosen) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "You need choose Starting Time of the event first!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
        } else {
            endHour = calendar.get(Calendar.HOUR_OF_DAY);
            endMinute = calendar.get(Calendar.MINUTE);

            endDay = calendar.get(Calendar.DAY_OF_MONTH);
            endMonth = calendar.get(Calendar.MONTH);
            endYear = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { //Open Dialog for Picking Date
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    date = Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year); //+1 needed since Months start from 0 in Calendar Objects
                    endDay = day;
                    endMonth = month;
                    endYear = year;

                    openEndTimePicker(); //If the user chooses date, then open a Dialog for Picking Time
                }
            }, endYear, endMonth, endDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }

    public void openRemindTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (remindDay == calendar.get(Calendar.DAY_OF_MONTH) && remindMonth == calendar.get(Calendar.MONTH) && remindYear == calendar.get(Calendar.YEAR)) {
                    if (hour <= calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), "You cannot choose time before now!", Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbar.show();
                        remindDateTextView.setText("Not Specified");
                    } else {
                        String time = Integer.toString(hour) + ":" + String.format("%02d", minute);
                        remindHour = hour;
                        remindMinute = minute;
                        remindDateTextView.setText(date + "\t\t\t" + time);
                    }
                } else {
                    String time = Integer.toString(hour) + ":" + String.format("%02d", minute);
                    remindHour = hour;
                    remindMinute = minute;
                    remindDateTextView.setText(date + "\t\t\t" + time);
                }
            }
        }, remindHour, remindMinute, true);
        timePickerDialog.show();
    }

    public void setRemind() {
        if (!isStartChosen || !isEndChosen) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "You need to choose Starting and Ending time of the event first!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
        } else {
            remindHour = calendar.get(Calendar.HOUR_OF_DAY);
            remindMinute = calendar.get(Calendar.MINUTE);

            remindDay = calendar.get(Calendar.DAY_OF_MONTH);
            remindMonth = calendar.get(Calendar.MONTH);
            remindYear = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { //Open Dialog for Picking Date
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    date = Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year); //+1 needed since Months start from 0 in Calendar Objects
                    remindDay = day;
                    remindMonth = month;
                    remindYear = year;
                    openRemindTimePicker(); //If the user chooses date, then open a Dialog for Picking Time
                }
            }, remindYear, remindMonth, remindDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_addrem,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.InfoRem:
                InfoDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
