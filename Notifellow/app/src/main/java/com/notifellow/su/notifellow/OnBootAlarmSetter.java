package com.notifellow.su.notifellow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.xml.validation.Schema;

import static android.content.Context.MODE_PRIVATE;



public class OnBootAlarmSetter extends BroadcastReceiver {

    AlarmDBSchema schema;

    private void setAlarmsAgain(Context context) {
        Cursor cursor = schema.getAllRowsForEvents();
        int colDate = cursor.getColumnIndex("remind_date");
        Log.i("Remind Date ID: ", " " + colDate);

        int colTime = cursor.getColumnIndex("remind_time");
        Log.i("Remind Date ID: ", " " + colTime);

        int colID = cursor.getColumnIndex("ID");
        Log.i("Remind Date ID: ", " " + colID);

        cursor.moveToFirst();

        int rowCount = cursor.getCount();

        if (cursor != null && (rowCount > 0)) {

            do {
                String time = cursor.getString(colTime);
                String date = cursor.getString(colDate);

                int alarmCode = Integer.parseInt(cursor.getString(colID));

                if (!date.equals("NA")) { //If the Reminder is NOT for WifiListener

                    Log.i("OnBootReceiver DATE: ", date);
                    Log.i("OnBootReceiver TIME: ", time);


                    String[] dateSplit = date.split("-");
                    String year = dateSplit[0];
                    String month = dateSplit[1];
                    String day = dateSplit[2];

                    String[] timeSplit = time.split(":");
                    String hour = timeSplit[0];
                    String minute = timeSplit[1];


                    Calendar cal = Calendar.getInstance();

                    cal.set(Calendar.YEAR, Integer.parseInt(year));
                    cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                    cal.set(Calendar.MINUTE, Integer.parseInt(minute));
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                    cal.set(Calendar.SECOND, 0);

                    Log.i("OnBootReceiver YEAR: ", year);
                    Log.i("OnBootReceiver MONTH: ", month);
                    Log.i("OnBootReceiver DAY: ", day);
                    Log.i("OnBootReceiver HOUR: ", hour);
                    Log.i("OnBootReceiver MINUTE: ", minute);

                    //int alarmCode = getAlarmCode(context);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.putExtra("ID", String.valueOf(alarmCode));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmCode, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                    //updateAlarmCode(++alarmCode, context);

                    Log.i("OnBootReceiver", "Alarm has SETTED!");


                    rowCount--;
                }
            }
            while (cursor.moveToNext());
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        schema = AlarmDBSchema.getInstance(context);
        Log.i("OnBootReceiver: ", "On Boot Received!");

        if (schema.getRowCountOfEvents() > 0) {
            Log.i("OnBootReceiver: ", "Going to set Alarms again!");
            Log.i("OnBootReceiver: ", "There are " + schema.getRowCountOfEvents());
            setAlarmsAgain(context);
        }

        Intent serviceIntent = new Intent(context, ServiceListenWiFi.class);
        context.startService(serviceIntent);
    }
}
