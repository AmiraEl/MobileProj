package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class ComputersService extends Service {

    int count = 0;
    private ComputerApp app;
    private Timer timer;
    private String[] array = new String[]{};

    @Override
    public void onCreate() {
        array = new String[]{"Find the computer of your dreams!", "Discounts up to 30% for a limited time only!", "Top of the line computers are waiting for you!"};
        startTimer();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //-------------------------------------------------------------------------
        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
        NotificationChannel notificationChannel = new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
        Notification notification =
                new Notification.Builder(this, "Channel_ID")
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Click to start the App")
                        .build();

        //-----------------------------------------------------

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MT", "Service destroyed");
        stopTimer();
    }

    private void startTimer() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
//                for (int i = 0; i< array.length;i++) {
                // display notification
                sendNotification(count);
//                }
            }
        };


        timer = new Timer(true);
        int delay = 0;   // 1/2 hour
        int interval = 1000 * 60 * 30;   // 1/2 hour
        timer.schedule(task, delay, interval);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void sendNotification(int i) {
        // create the intent for the notification
        Intent notificationIntent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the pending intent
        int flags = PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, flags);//<--------

        // create the variables for the notification
        CharSequence contentTitle = "Computify";

        CharSequence tickerText = "New computers available";
        CharSequence contentText = array[count];

        NotificationChannel notificationChannel =
                new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);


        // create the notification and set its data
        Notification notification = new NotificationCompat
                .Builder(this, "Channel_ID")
                .setSmallIcon(R.drawable.computer)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("Channel_ID")
                .build();

        final int NOTIFICATION_ID = count + 1; //cannot be 0
        manager.notify(NOTIFICATION_ID, notification);

        count = count + 1 % 2;
    }
}
