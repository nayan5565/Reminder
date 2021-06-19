package com.shadhinlab.reminder.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.shadhinlab.reminder.receiver.MyBroadcastReceiver;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.ALARM_SERVICE;

public class MyAlarmManager {
    public long alarmTimeLong;
    public int pendingId, hours, minutes;
    private Context activity;
    private AlarmManager alarmManager;
    private long intervalWeek = 7 * 24 * 60 * 60 * 1000;
    private long intervalMinutes = 12 * 60 * 1000;

    //week = 7*24 * 60 * 60 * 1000

    public MyAlarmManager(Context activity) {
        this.activity = activity;
        alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
    }

    public void getMinHourTime(int hour, int min, int pickTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmTimeLong = calendar.getTime().getTime();

        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        pendingId = (int) calendar.getTime().getTime();


        Log.e("Time", "Calendar " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);
    }

    public void setSingleAlarm(int hour, int min, int pickTime, int prayerWakto, int intentId, String contentValue, String reminderNumber, boolean isBefore, boolean isEdit) {
        Utils.log("setSingleAlarm wakto: " + prayerWakto);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        if (isBefore)
            calendar.set(Calendar.MINUTE, min - pickTime);
        else calendar.set(Calendar.MINUTE, min + pickTime);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
            hours = hour;
            minutes = min;
        } else {
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            pendingId = (int) calendar.getTime().getTime();
        }

        Log.e("Alarm single", "Calendar " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);


        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeLong, pendingIntent(pendingId, prayerWakto, contentValue, reminderNumber));


//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7 * 24 * 3600 * 1000, pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent(intentId));

//        Toast.makeText(this, "Alarm Set." + calendar.getTime(), Toast.LENGTH_LONG).show();
//        SetAlarmRepeating(alarmTimeLong, pendingId);
    }


    public void setNextDayAlarmPrayer(int hour, int min, int pickTime, int prayerWakto, int intentId, String contentValue, String reminderNumber, boolean isBefore, boolean isEdit) {
        Calendar calendar = Calendar.getInstance();
        //for next day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        if (isBefore)
            calendar.set(Calendar.MINUTE, min - pickTime);
        else calendar.set(Calendar.MINUTE, min + pickTime);
        calendar.set(Calendar.SECOND, 0);

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
            hours = hour;
            minutes = min;
        } else {
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            pendingId = (int) calendar.getTime().getTime();
        }

        Log.e("Alarm next day", "Calendar " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeLong, pendingIntent(pendingId, prayerWakto, contentValue, reminderNumber));

    }

    public void setAlarmDayWise(int day, int hour, int min, int pickTime, int prayerWakto, int intentId, String contentValue, String reminderNumber, boolean isEdit) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
//        calendar.add(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);


        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
//        if (calendar.before(Calendar.getInstance())) {
//            Utils.showToast("Before");
//            calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_WEEK, day);
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, min - pickTime);
//            calendar.set(Calendar.SECOND, 0);
////            calendar.add(Calendar.DAY_OF_MONTH, 1);
//        }

        if (calendar.before(new GregorianCalendar())) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH, 7);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
        } else
            pendingId = (int) calendar.getTime().getTime();

        Utils.log("Set Alarm " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);

//        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeLong, pendingIntent(pendingId, prayerWakto, contentValue, reminderNumber));
    }

    public void setAlarmDateWise(int month, int date,int hour, int min, int pickTime, int prayerWakto, int intentId, String contentValue, String reminderNumber, boolean isEdit) {
        Calendar calendar = Calendar.getInstance();
//        Utils.log("Current Month: "+calendar.get(Calendar.MONTH));
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);



        if (calendar.before(new GregorianCalendar())) {
            Utils.log("Before");
            calendar.add(GregorianCalendar.MONTH, 1);
//            calendar.add(GregorianCalendar.DAY_OF_MONTH, 30);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
        } else
            pendingId = (int) calendar.getTime().getTime();

        Utils.log("Set date wise Alarm " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);

//        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeLong, pendingIntent(pendingId, prayerWakto, contentValue, reminderNumber));
    }

    public void setAlarmRepeatWeekly(int day, int hour, int min, int pickTime, int intentId, String contentValue, boolean isEdit, int alarmID) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
//        if (calendar.before(Calendar.getInstance())) {
//            Utils.showToast("Before " + day);
////            calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.DAY_OF_WEEK, day);
//            Utils.log("Before Day : " + calendar.getTime());
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, min - pickTime);
//            calendar.set(Calendar.SECOND, 0);
//        }

        if (calendar.before(new GregorianCalendar())) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH, 7);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
        } else
            pendingId = (int) calendar.getTime().getTime();

        Utils.log("TimeConvert : " + Utils.getDateTimeConverter(calendar.getTime()));
        Utils.log("Set Alarm " + calendar.getTime() + " long : " + calendar.getTime().getTime()
                + " pendingId : " + pendingId + " Day : " + day);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeLong, intervalWeek, pendingIntentWithAlarmID(pendingId, alarmID, contentValue));

//        Utils.showToast("Alarm Set." + calendar.getTime());

    }

    public void setTestAlarm(int day, int hour, int min, int pickTime, int prayerWakto, int intentId, String contentValue, String reminderNumber, boolean isEdit) {
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_WEEK, day);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
        } else
            pendingId = (int) calendar.getTime().getTime();

        Utils.log("Set Alarm " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeLong, intervalMinutes, pendingIntent(pendingId, prayerWakto, contentValue, reminderNumber));
        Utils.showToast("Will Call " + calendar.getTime());

    }

    public void setAlarmSnooze(int day, int hour, int min, int pickTime, int intentId, String contentValue,
                               boolean isEdit, int snooze, int alarmNumber) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
//        if (calendar.before(Calendar.getInstance())) {
//            Utils.showToast("Before");
//            calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_WEEK, day);
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, min - pickTime);
//            calendar.set(Calendar.SECOND, 0);
//        }

        if (calendar.before(new GregorianCalendar())) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH, 7);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
        } else
            pendingId = (int) calendar.getTime().getTime();

        Utils.log("Set Alarm snooze " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeLong, snooze * 60 * 1000, pendingIntent(pendingId, contentValue, alarmNumber));

    }

    public void setTestAlarmSnooze(int day, int hour, int min, int pickTime, int intentId, String contentValue,
                                   boolean isEdit, int snooze, int alarmNumber) {
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_WEEK, day);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmTimeLong = calendar.getTime().getTime();
        if (isEdit) {
            pendingId = intentId;
        } else
            pendingId = (int) calendar.getTime().getTime();

        Utils.log("Set Test Alarm snooze " + calendar.getTime() + " long : " + calendar.getTime().getTime() + " pendingId : " + pendingId);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeLong, snooze * 60 * 1000, pendingIntent(pendingId, contentValue, alarmNumber));

    }

    public void setSnooze(int hour, int min, int intentId, int snooze, int alarmNumber, String dialogText) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        //Add a day if alarm is set for before current time, so the alarm is triggered the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmTimeLong = calendar.getTime().getTime();
        pendingId = intentId;

        Utils.log("Set snooze " + calendar.getTime() + " pendingId : " + pendingId);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTimeLong, snooze * 60 * 1000, pendingIntent(pendingId, dialogText, alarmNumber));

    }

    public void setAlarmEveryDay(long alarmtime, int intentId) {

        Utils.log(" long : " + alarmtime + " pendingId : " + intentId);
//        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmtime, AlarmManager.INTERVAL_DAY, pendingIntent(intentId));

//        Toast.makeText(this, "Alarm Set." + calendar.getTime(), Toast.LENGTH_LONG).show();

    }

    public void setAlarmSnooze(long alarmtime, int intentId, int snooze, int alarmNumber) {

        Log.e("Alarm", " long : " + alarmtime + " pendingId : " + intentId);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmtime, snooze * 60 * 1000, pendingIntent(intentId, "", alarmNumber));
//        Toast.makeText(this, "Alarm Set." + calendar.getTime(), Toast.LENGTH_LONG).show();

    }


    public void startAlertAtParticularTime() {

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval

        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 24);

//        alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);


        Toast.makeText(activity, "Alarm will vibrate at time specified",
                Toast.LENGTH_SHORT).show();

    }


    private PendingIntent pendingIntent(int id, int prayerWakto, String content, String reminderNumber) {
        Utils.log("pendingIntent wakto: " + prayerWakto);
        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        intent.putExtra("ContentValue", content);
        intent.putExtra("AlarmID", prayerWakto);
        intent.putExtra(Global.REMINDER_NUMBER, reminderNumber);
//        intent.putExtra(id + "", 0);
        intent.putExtra("PendingId", id);
        intent.putExtra("Prayer", prayerWakto);
        return PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
    }

    private PendingIntent pendingIntentWithAlarmID(int id, int alarmID, String content) {
        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        intent.putExtra("ContentValue", content);
        intent.putExtra("AlarmID", alarmID);
//        intent.putExtra(id + "", 0);
        intent.putExtra("PendingId", id);
        return PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
    }

    private PendingIntent pendingIntent(int id, String content, int alarmNumber) {
        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        intent.putExtra("ContentValue", content);
        intent.putExtra(id + "", alarmNumber);
        intent.putExtra("PendingId", id);
        return PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
    }

    //pIntent method
    private PendingIntent pendingIntent(int id) {
        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        intent.putExtra("PendingId", id);
//        return PendingIntent.getBroadcast(this.getApplicationContext(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return PendingIntent.getBroadcast(activity.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
    }


    public void cancelAlarm(int pendingId) {
//        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        Utils.log("Cancel alarm : " + pendingId);
        alarmManager.cancel(pendingIntent(pendingId));
    }
}