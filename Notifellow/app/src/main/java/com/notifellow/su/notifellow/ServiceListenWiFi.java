package com.notifellow.su.notifellow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class ServiceListenWiFi extends Service {
    String wifiToCheck;
    AlarmDBSchema schema;
    String ID;
    private NotificationManager notifManager;
    private SharedPreferences shared;

    public IBinder onBind(Intent intent){
        return null;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        String ssid = wifiManager.getConnectionInfo().getSSID();

                        ssid = networkInfo.getExtraInfo();

                        Cursor cursor = schema.getTasksForWifiListener();
                        int colWifi = cursor.getColumnIndex("wifiname");
                        int colID = cursor.getColumnIndex("ID");
                        int emailID = cursor.getColumnIndex("email");
                        cursor.moveToFirst();

                        int rowCount = cursor.getCount();

                        if (cursor != null && (rowCount > 0)) {

                            do {
                                wifiToCheck = cursor.getString(colWifi);
                                ID = cursor.getString(colID);
                                String email = cursor.getString(emailID);

                                shared = getSharedPreferences("shared", MODE_PRIVATE);
                                String userEmail = shared.getString("email", null); //GET MAIL ADDRESS FROM SHARED

                                if (ssid.equals(wifiToCheck) && userEmail.equals(email)){
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

                                    String name = "my_package_channel";
                                    String id = "my_package_channel_1"; // The user-visible name of the channel.
                                    String description = "my_package_first_channel"; // The user-visible description of the channel.

                                    Intent intentNoti;
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

                                        intentNoti = new Intent(context, AlarmReceiver.class);
                                        intentNoti.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        pendingIntent = PendingIntent.getActivity(context, 0, intentNoti, 0);

                                        builder.setContentTitle(schema.getTitle(ID))  // required
                                                .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                                                .setContentText(schema.getTitle(ID))  // required
                                                .setDefaults(Notification.DEFAULT_ALL)
                                                .setAutoCancel(true)
                                                .setContentIntent(pendingIntent)
                                                .setTicker("Notifellow");
                                    } else {

                                        builder = new NotificationCompat.Builder(context, "default");

                                        intentNoti = new Intent(context, AlarmReceiver.class);
                                        intentNoti.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        pendingIntent = PendingIntent.getActivity(context, 0, intentNoti, 0);

                                        builder.setContentTitle(schema.getTitle(ID))  // required
                                                .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                                                .setContentText(schema.getTitle(ID))  // required
                                                .setDefaults(Notification.DEFAULT_ALL)
                                                .setAutoCancel(true)
                                                .setContentIntent(pendingIntent)
                                                .setTicker("Notifellow")
                                                .setPriority(Notification.PRIORITY_HIGH);
                                    }
                                    Notification notification = builder.build();
                                    notifManager.notify(Integer.parseInt(ID), notification);

                                    rowCount--;
                                    schema.deleteRowForWifiListener(wifiToCheck);

                                    break;
                                }
                            }
                            while(cursor.moveToNext());

                            if(rowCount == 0)
                                stopSelf();
                        }
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, intentFilter);

        schema = AlarmDBSchema.getInstance(this);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
