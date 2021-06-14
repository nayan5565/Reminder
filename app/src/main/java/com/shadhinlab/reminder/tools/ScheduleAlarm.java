package com.shadhinlab.reminder.tools;

import android.content.Context;


import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MRepeatAlarm;
import com.shadhinlab.reminder.models.MTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ScheduleAlarm {
    private List<MAlarm> alarmList;
    private MyDatabase myDatabase;
    private MyAlarmManager myAlarmManager;

    public ScheduleAlarm(Context activity) {
        myAlarmManager = new MyAlarmManager(activity);
    }

    public void alarmAnalysis() {

        alarmList = new ArrayList<>();
        myDatabase = MyDatabase.getInstance(MyApp.getInstance().getContext());
        alarmList = myDatabase.myDao().getAlarmDetails();
//        nextAllAlarm();

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

    private String getTomorrowPrayerTIme(int prayerWakto) {
        String time = "";
        if (myDatabase.myDao().getPrayerTimes() != null && myDatabase.myDao().getPrayerTimes().getData().size() > 0) {
            for (int i = 0; i < myDatabase.myDao().getPrayerTimes().getData().size(); i++) {
                if (myDatabase.myDao().getPrayerTimes().getData().get(i).getDate().getReadable().equals(Utils.getTomorrowDate())) {
                    MTime mTime = myDatabase.myDao().getPrayerTimes().getData().get(i).getTimings();
                    Utils.log("Wakto: " + prayerWakto + " : " + mTime.getIsha());
                    if (prayerWakto == 1) {
                        time = mTime.getFajr().split(" ")[0];
                    } else if (prayerWakto == 2) {
                        time = mTime.getDhuhr().split(" ")[0];
                    } else if (prayerWakto == 3) {
                        time = mTime.getAsr().split(" ")[0];
                    } else if (prayerWakto == 4) {
                        time = mTime.getMaghrib().split(" ")[0];
                    } else if (prayerWakto == 5) {
                        time = mTime.getIsha().split(" ")[0];
                    }
                    return time;
                }
            }
        }
        return time;
    }

    public void nextWaktoAlarm(int prayerWakto, int pendingId) {
        Utils.log("Next Day prayer: " + getTomorrowPrayerTIme(prayerWakto));
        List<MAlarm> alarmList = myDatabase.myDao().getAlarmByWakto(prayerWakto, pendingId);
        Utils.log("Alarm size: " + alarmList.size()+" : "+pendingId);
        if (!getTomorrowPrayerTIme(prayerWakto).isEmpty()) {
            int hour = Integer.parseInt(getTomorrowPrayerTIme(prayerWakto).split(":")[0]);
            int min = Integer.parseInt(getTomorrowPrayerTIme(prayerWakto).split(":")[1]);
            if (alarmList.size() > 0) {
                for (int i = 0; i < alarmList.size(); i++) {
                    if (alarmList.get(i).isBeforeAlarm())
                        myAlarmManager.setNextDayAlarmPrayer(hour, min, alarmList.get(i).getBeforePrayerTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                    else
                        myAlarmManager.setNextDayAlarmPrayer(hour, min, alarmList.get(i).getAfterPrayerTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                    saveDb(alarmList.get(0), hour, min);
                }
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
        mAlarm.setId(alarm.getId());
        mAlarm.setBeforePrayerTime(alarm.getBeforePrayerTime());
        mAlarm.setAfterPrayerTime(alarm.getAfterPrayerTime());
        mAlarm.setPrayerWakto(alarm.getPrayerWakto());
        mAlarm.setBeforeAlarm(alarm.isBeforeAlarm());
        mAlarm.setHour(hour);
        mAlarm.setMin(minute);
        mAlarm.setPendingID(alarm.getPendingID());
        mAlarm.setLongAlarmTime(myAlarmManager.alarmTimeLong);
        mAlarm.setPickTime(alarm.getPickTime());
        myDatabase.myDao().saveAlarmDetails(mAlarm);

    }


    public void cancelAlarm(MAlarm mAlarm) {
        if (mAlarm != null) {
            myAlarmManager.cancelAlarm(mAlarm.getPendingID());
        }

    }
}