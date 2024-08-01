package com.mrsneaker.ultrascheduler.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.mrsneaker.ultrascheduler.MainActivity;
import com.mrsneaker.ultrascheduler.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "ultra_scheduler";
    private static final String CHANNEL_NAME = "Ultra Scheduler";
    private static final String CHANNEL_DESC = "This channel is used for notifications";

    public static void scheduleNotification(Context context, long timeInMillis, String title, String message, long notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, (int) notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        SharedPreferences prefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("timeInMillis", timeInMillis);
        editor.putString("title", title);
        editor.putString("message", message);
         editor.putLong("notificationId", notificationId);
        editor.apply();
    }

    public static void cancelNotification(Context context, long notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, (int) notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    public static void updateNotification(Context context, long newTimeInMillis, String newTitle, String newMessage, long notificationId) {
        cancelNotification(context, notificationId);
        scheduleNotification(context, newTimeInMillis, newTitle, newMessage, notificationId);
    }


    public static void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESC);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            long notificationId = intent.getLongExtra("notificationId", 0);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(
                    (int) notificationId, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                    .setContentIntent(notificationPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify((int) notificationId, builder.build());
        }
    }
}
