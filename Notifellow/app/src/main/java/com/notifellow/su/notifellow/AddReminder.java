package com.notifellow.su.notifellow;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        setTitle("Add Reminder");

        refreshWifi = findViewById(R.id.RefreshBtn);
        calendar = Calendar.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fabSet);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addTask();

                }
            });
        }

        switchPublic = findViewById(R.id.switchPublic);
        publicity = "0";
        switchPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publicity = "1";

                } else {
                    publicity = "0";
                }
            }
        });


        titleTextView = findViewById(R.id.reminderTxt);

        wifiSpinner = findViewById(R.id.spinnerWiFi);
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
                    return MyData;
                }
            };
            queue.add(MyStringRequest);
        }

        Toast.makeText(getBaseContext(), "Alarm is set for: " + remind_time + " " + remind_date, Toast.LENGTH_LONG).show();
    }

    //ToDo: This function will store information about the reminder in local database
    public void addTask() {
        if(titleTextView.getText().toString().equals("")){
            Toast.makeText(getBaseContext(), "You should specify a title!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endYear != 0 && endYear <= startYear && endMonth <= startMonth && endDay <= startDay && endHour <= startHour && endMinute <= startMinute) {
            Toast.makeText(getBaseContext(), "End date is not correct!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int alarmCode = getAlarmCode();

        if (startDateTextView.getText().toString().equals("Not Specified.") && endDateTextView.getText().toString().equals("Not Specified.") && remindDateTextView.getText().toString().equals("Not Specified.")) {
            if (!wifiName.equals("")) {
                //THIS ALARM WILL BE FIRED WHEN THE USER CONNECTS TO CHOSEN WIFI

                Main.schema.insertForWifiListener(String.valueOf(alarmCode), titleTextView.getText().toString(), wifiName, notesTextView.getText().toString());
                int code = alarmCode;
                updateAlarmCode(code);

                Intent intent = new Intent(this, ServiceListenWiFi.class);
                this.startService(intent);

                Toast.makeText(getBaseContext().getApplicationContext(), "Alarm set for: " + wifiName, Toast.LENGTH_SHORT).show();


            }
            else{
                Toast.makeText(getBaseContext(), "You should select a WiFi name, in order to set a WiFi based reminder!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), "INVALID DATE. Please check the date and time for the reminder", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please turn on your WiFi, in order to see your saved WiFi's! " + wifiName, Toast.LENGTH_SHORT).show();
        }
    }

    public void openStartTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if (startDay == calendar.get(Calendar.DAY_OF_MONTH) && startMonth == calendar.get(Calendar.MONTH) && startYear == calendar.get(Calendar.YEAR)) {
                    if (hour <= calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.MINUTE)) {
                        Toast.makeText(getBaseContext(), "You cannot choose time before now!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getBaseContext(), "You cannot choose time before now!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), "You need choose Starting Time of the event first!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getBaseContext(), "You cannot choose time before now!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "You need to choose Starting and Ending time of th event first!", Toast.LENGTH_SHORT).show();
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




}
