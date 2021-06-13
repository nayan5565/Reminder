package com.shadhinlab.reminder.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.shadhinlab.reminder.activities.MainActivity;
import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.activities.DismissActivity;
import com.shadhinlab.reminder.tools.DismissAlarmNotificationController;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.Utils;


public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String KEY_IS_ONE_TIME = "onetime";
    DismissAlarmNotificationController dismissAlarmNotificationController;
    String contentValue = "";
    int pendingId, alarmNumber, alarmID;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Utils.showToast(Utils.getPrefBoolean(Global.ALARM_ENABLE, false) + "");
        Utils.log("AlarmEnable : " + Utils.getPrefBoolean(Global.ALARM_ENABLE, true));
        if (Utils.getPrefBoolean(Global.ALARM_ENABLE, true)) {
            contentValue = intent.getStringExtra("ContentValue");
            pendingId = intent.getIntExtra("PendingId", 0);
            alarmID = intent.getIntExtra("AlarmID", 0);
            alarmNumber = intent.getIntExtra(pendingId + "", 0);
//        createNotification(context);


            dismissAlarmNotificationController = new DismissAlarmNotificationController(context);

            // since Android Q it's not allowed to start activity from the background
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                dismissAlarmNotificationController.showNotification();
            } else {
                Intent dismissAlarmIntent = new Intent(context, DismissActivity.class);
                dismissAlarmIntent.putExtra("ContentValue", contentValue);
                dismissAlarmIntent.putExtra("AlarmID", alarmID);
                dismissAlarmIntent.putExtra(pendingId + "", alarmNumber);
                dismissAlarmIntent.putExtra("PendingId", pendingId);
                dismissAlarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dismissAlarmIntent);
            }

        }

    }


    public void createNotification(Context context) {
        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setTicker(context.getString(R.string.app_name))
                .setContentText("wake up user!");

        notificationBuilder.setContentIntent(notificationIntent);

        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        notificationBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
