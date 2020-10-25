package com.breathetaichi.classtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    public static int notificationID;
    public static String channelID;
    public static CharSequence name;
    public static String content;
    public static String title;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String description = "Course Alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.alarm)
                .setContentText(content)
                .setContentTitle(title).build();

        NotificationManager notificationManager2 = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager2.notify(notificationID++, notification);
    }
}
