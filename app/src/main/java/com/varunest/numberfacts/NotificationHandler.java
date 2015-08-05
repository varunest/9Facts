package com.varunest.numberfacts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Varun on 29/03/15.
 */
public class NotificationHandler {

    private static final String tag = "DEBUG_NOTIFICATION";
    NotificationManager notificationManager;

    public NotificationHandler (Context context, String title, String content, int dailyFact) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, NumbersMain.class), 0);

        if (Build.VERSION.SDK_INT >=11){
            Notification.Builder mBuilder = new Notification.Builder(context);
            mBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.notificationicon) ;
            if (Build.VERSION.SDK_INT >= 16 ) {
               mBuilder.setStyle(new Notification.BigTextStyle().bigText(content)).setPriority(Notification.PRIORITY_HIGH);
               notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
               notificationManager.notify(dailyFact, mBuilder.build());
            } else {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(dailyFact, mBuilder.getNotification());
            }
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.notificationicon)
                    .setContentIntent(contentIntent);
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(dailyFact, mBuilder.getNotification());
        }
    }
}
