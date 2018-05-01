package com.notifellow.su.notifellow;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import static android.content.Context.MODE_PRIVATE;


public class AlarmReceiver extends BroadcastReceiver{
    AlarmDBSchema schema;
    Location curLoc;
    double latitude, longitude;
    private NotificationManager notifManager;
    String ID;
    private SharedPreferences shared;

    public void deleteAlarmFromGlobal(final Context context, String ID, String email){
        NetworkInfo netInfo;
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo != null) {
            RequestQueue queue;
            queue = Volley.newRequestQueue(context);

            final String id = ID;
            final String mailAddress = email;

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteAlarm", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    if (response.equals("DELETED 201")) {
                        //SUCCESFULL QUERY WILL EXECUTE FOLLOWING LINES
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //This code is executed if there is an error.
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("emailGiven", mailAddress); //Dont change THIS LINE!!!
                    MyData.put("alarmID", id); //Event ID
                    return MyData;
                }
            };
            queue.add(MyStringRequest);
        }
    }

    public void callAlarm(Context context, String wifiName, String location, String ID, String email) {
        String ssid = "";
        if(!wifiName.equals("")) { //If the WifiName of the Alarm == "", do not try to get the current connection name
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    //String ssid = wifiManager.getConnectionInfo().getSSID();
                    ssid = networkInfo.getExtraInfo();
                }
            }
        }

        shared = context.getSharedPreferences("shared", MODE_PRIVATE);
        String userEmail = shared.getString("email", "null");//GET EMAIL FROM SHARED


        if (wifiName.equals(ssid) && userEmail.equals(email)) {
            Log.i("AlarmReceiver", "ALARM ALARM ALARM.");

            //THIS PART WILL BE CHANGED W.R.T TYPE OF THE NOTIFICATION OR ALARM

            /*NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "default");
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.drawable.ic_launcher_background);
            notification.setTicker("The Ticker");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(schema.getTitle(ID));
            notification.setContentText(schema.getStartTime(ID) + " - " + schema.getEndTime(ID));

            Intent intent1 = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);
            notification.setFullScreenIntent(pendingIntent, true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(ID), notification.build());*/


            final int NOTIFY_ID = Integer.parseInt(ID);

            String name = "my_package_channel";
            String id = "my_package_channel_1"; // The user-visible name of the channel.
            String description = "my_package_first_channel"; // The user-visible description of the channel.


            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            switch(audio.getRingerMode()){
                case AudioManager.RINGER_MODE_NORMAL:
                    MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
                    mp.start();
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1500); // 1500 miliseconds = 1.5 seconds
                    break;
            }


            Intent intent;
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;

            if (notifManager == null) {
                notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = notifManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notifManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(context, id);

                intent = new Intent(context, AlarmReceiver.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                builder.setContentTitle(schema.getTitle(ID))  // required
                        .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                        .setContentText(schema.getStartTime(ID) + " - " + schema.getEndTime(ID))  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker("Notifellow");
            } else {

                builder = new NotificationCompat.Builder(context, "default");

                intent = new Intent(context, AlarmReceiver.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                builder.setContentTitle(schema.getTitle(ID))  // required
                        .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                        .setContentText(schema.getStartTime(ID) + " - " + schema.getEndTime(ID))  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker("Notifellow")
                        .setPriority(Notification.PRIORITY_HIGH);
            }
            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);

            deleteAlarmFromGlobal(context, ID, email);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        schema = AlarmDBSchema.getInstance(context);
        Log.i("AlarmReceiver", "Broadcast has received.");

        ID = intent.getExtras().getString("ID");
        String wifiName = schema.getWifiName(ID);
        String location = schema.getLocation(ID);
        String email = schema.getEmail(ID);
        callAlarm(context, wifiName, location, ID, email);
        schema.deleteByID(ID);

        //ScheduleFragment.taskList.remove(new Task(ID));
        //ScheduleFragment.taskAdapter.notifyDataSetChanged();

        Log.i("AlarmReceiver", "Alarm has deleted!");

    }
}
