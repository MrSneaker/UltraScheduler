package com.mrsneaker.ultrascheduler.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences prefs = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE);
            long timeInMillis = prefs.getLong("timeInMillis", -1);
            String title = prefs.getString("title", "");
            String message = prefs.getString("message", "");

            if (timeInMillis != -1 && !title.isEmpty() && !message.isEmpty()) {
                NotificationHelper.scheduleNotification(context, timeInMillis, title, message);
            }
        }
    }
}