package com.shadhinlab.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shadhinlab.reminder.tools.ScheduleAlarm;

public class DeviceBootReceiver extends BroadcastReceiver {
    private ScheduleAlarm scheduleAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                scheduleAlarm = new ScheduleAlarm(context);
                scheduleAlarm.alarmAnalysis();
                scheduleAlarm.nextAlarm();
            }
        }
    }
}

