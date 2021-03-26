package com.example.saleservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notificationreciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent repeatingevent = new Intent(context,MainPage.class);
        repeatingevent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,repeatingevent,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "lets do it";
            String desc = "this is just for sample purpose";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("letsntify",name,importance);
            channel.setDescription(desc);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channel.getId())
                    .setSmallIcon(R.drawable.ic_action_show)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("Important Update")
                    .setContentText("Order has not been placed yet please give order asap!!!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(200,builder.build());
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Order Notification")
                    .setSmallIcon(R.drawable.ic_action_show)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("Important Update")
                    .setContentText("Order has not been placed yet please give order asap!!!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(200, builder.build());
        }
    }
}
