package com.shadhinlab.reminder.tools;

import android.content.Context;


import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MRepeatAlarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ScheduleAlarm {
    private List<MAlarm> alarmList;
    private List<MRepeatAlarm> mRepeatAlarms;
    private MyDatabase myDatabase;
    private MyAlarmManager myAlarmManager;

    public ScheduleAlarm(Context activity) {
        myAlarmManager = new MyAlarmManager(activity);
    }

    public void alarmAnalysis() {

        alarmList = new ArrayList<>();
        mRepeatAlarms = new ArrayList<>();
        myDatabase = MyDatabase.getInstance(MyApp.getInstance().getContext());
        alarmList = myDatabase.myDao().getAlarmDetails();
        nextAllAlarm();

    }

    public void nextAllAlarm() {
        alarmList = myDatabase.myDao().getAlarmDetails();
        if (alarmList.size() > 0) {
            for (int i = 0; i < alarmList.size(); i++) {
                if (alarmList.get(i).isBeforeAlarm())
                    myAlarmManager.setNextDayAlarmPrayer(alarmList.get(i).getHour(), alarmList.get(i).getMin(), alarmList.get(i).getBeforePrayerTime(), alarmList.get(i).getPrayerWakto(), 123, "", true, true);
                else
                    myAlarmManager.setNextDayAlarmPrayer(alarmList.get(i).getHour(), alarmList.get(i).getMin(), alarmList.get(i).getAfterPrayerTime(), alarmList.get(i).getPrayerWakto(), 123, "", false, true);
            }
        }

    }

    public void nextWaktoAlarm(int prayerWakto) {
        alarmList = myDatabase.myDao().getAlarmByWakto(prayerWakto);
        if (alarmList.size() > 0) {
            for (int i = 0; i < alarmList.size(); i++) {
                if (alarmList.get(i).isBeforeAlarm())
                    myAlarmManager.setNextDayAlarmPrayer(alarmList.get(i).getHour(), alarmList.get(i).getMin(), alarmList.get(i).getBeforePrayerTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                else
                    myAlarmManager.setNextDayAlarmPrayer(alarmList.get(i).getHour(), alarmList.get(i).getMin(), alarmList.get(i).getAfterPrayerTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                saveDb(alarmList.get(0), alarmList.get(i).getHour(), alarmList.get(i).getMin());
            }
        }

    }

    public void snoozeAlarm(int pendingID, String dialogText) {
        if (!Utils.getPrefBoolean(Global.SNOOZE_START_ALREADY, false) && Utils.getPrefBoolean(Global.SNOOZE_ENABLE, true)) {
//            Utils.showToast("started snooze");
            int interval = Utils.getPref(Global.SNOOZE_INTERVAL, 5);
            int hour = Integer.parseInt(Utils.getCurrentTime().split(":")[0]);
            int min = Integer.parseInt(Utils.getCurrentTime().split(":")[1]);
            Utils.savePrefBoolean(Global.SNOOZE_START_ALREADY, true);
            myAlarmManager.setSnooze(hour, min + interval, 123, interval, 3, dialogText);
        }
//        else Utils.showToast("Already started snooze");
    }

    private void saveDb(MAlarm alarm, int hour, int minute) {
        MAlarm mAlarm = new MAlarm();
        mAlarm.setBeforePrayerTime(alarm.getBeforePrayerTime());
        mAlarm.setAfterPrayerTime(alarm.getAfterPrayerTime());
        mAlarm.setPrayerWakto(alarm.getPrayerWakto());
        mAlarm.setBeforeAlarm(alarm.isBeforeAlarm());
        mAlarm.setHour(hour);
        mAlarm.setMin(minute);
        mAlarm.setPendingID(alarm.getPendingID());
        mAlarm.setLongAlarmTime(myAlarmManager.alarmTimeLong);
        mAlarm.setPickTime(alarm.getPickTime());
    }


    public void cancelAlarm(MAlarm mAlarm) {
        String nameOfDay = "", alarmDays = "";
        if (mAlarm != null) {
            mRepeatAlarms = myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId());
            myAlarmManager.cancelAlarm(mAlarm.getPendingID());
        }

    }
}