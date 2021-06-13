package com.shadhinlab.reminder.tools;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;


import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MRepeatAlarm;

import java.util.ArrayList;
import java.util.List;

import static com.shadhinlab.reminder.tools.Utils.calculateDate;


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

        if (alarmList.size() > 0) {
            for (int i = 0; i < alarmList.size(); i++) {
                if (alarmList.get(i).getAlarmDateTime().before(Utils.getDateTime(Utils.getTodaysDateTime()))) {
                    Utils.log("Past date pos : " + i);
                    mRepeatAlarms = myDatabase.myDao().getRepeatAlarmDetails(alarmList.get(i).getId());
                    if (mRepeatAlarms.size() > 0) {
                        int alarmDay = 0;
                        for (MRepeatAlarm mRepeatAlarm : mRepeatAlarms) {
                            if (mRepeatAlarm.isDay()) {
                                alarmDay++;
                                if (mRepeatAlarm.getAlarmDateTime().before(Utils.getDateTime(Utils.getTodaysDateTime()))) {
                                    mRepeatAlarm.setAlarmDateTime(calculateDate(mRepeatAlarm.getTag(), mRepeatAlarm.getHour(), mRepeatAlarm.getMinute()));
                                    myDatabase.myDao().saveRepeatAlarmDetails(mRepeatAlarm);
                                }
                                MAlarm mAlarm = alarmList.get(i);
                                mAlarm.setId(mAlarm.getId());
                                mAlarm.setAlarmDateTime(myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId()).get(0).getAlarmDateTime());
                                mAlarm.setLongAlarmTime(myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId()).get(0).getAlarmDateTime().getTime());
                                myDatabase.myDao().saveAlarmDetails(mAlarm);
                                Utils.log("has repeat " + myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId()).get(0).getAlarmDateTime());
                            }
                        }
                        Utils.log("Complete Repeat loop : " + alarmDay);
                        if (alarmDay == 0) {
                            myAlarmManager.cancelAlarm(alarmList.get(i).getPendingID());
                            myDatabase.myDao().deleteRepeatAlarmDetails(alarmList.get(i).getId());
                            myDatabase.myDao().deleteAlarmDetailsByDate(Utils.getDateTime(Utils.getTodaysDateTime()));
                        }

                    } else {
                        myAlarmManager.cancelAlarm(alarmList.get(i).getPendingID());
                        myDatabase.myDao().deleteAlarmDetailsByDate(Utils.getDateTime(Utils.getTodaysDateTime()));
                    }

                }
            }

        }

    }

    public void nextAlarm() {
        alarmList = myDatabase.myDao().getAlarmDetails();
        if (alarmList.size() > 0) {
            MAlarm mAlarm = alarmList.get(0);
            mRepeatAlarms = myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId());
            if (mRepeatAlarms.size() > 0) {
                MRepeatAlarm mRepeatAlarm = mRepeatAlarms.get(0);
                myAlarmManager.setAlarmRepeatWeekly(mRepeatAlarm.getTag(), mRepeatAlarm.getHour(), mRepeatAlarm.getMinute(), 0, mRepeatAlarm.getPendingID(), "", true, mAlarm.getId());
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

    public void cancelAlarm(MAlarm mAlarm) {
        String nameOfDay = "", alarmDays = "";
        if (mAlarm != null) {
            mRepeatAlarms = myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId());
            if (mRepeatAlarms.size() > 0) {
                MRepeatAlarm mRepeatAlarm = mRepeatAlarms.get(0);
                myAlarmManager.cancelAlarm(mRepeatAlarm.getPendingID());
                mRepeatAlarm.setDay(false);
                myDatabase.myDao().saveRepeatAlarmDetails(mRepeatAlarm);
                mRepeatAlarms = myDatabase.myDao().getRepeatedAlarmDetails(true, mAlarm.getId());
                if (mRepeatAlarms.size() < 1) {
                    myAlarmManager.cancelAlarm(mAlarm.getPendingID());
                    myDatabase.myDao().deleteAlarmDetails(mAlarm.getId());
                } else {
                    for (MRepeatAlarm mRepeatAlarm1 : mRepeatAlarms) {
                        if (mRepeatAlarm1.isDay())
                            nameOfDay = nameOfDay.concat(mRepeatAlarm1.getDay() + ", ");

                    }
                    if (!TextUtils.isEmpty(nameOfDay))
                        alarmDays = nameOfDay.substring(0, nameOfDay.lastIndexOf(","));
                    mAlarm.setId(mAlarm.getId());
                    mAlarm.setNoOfDays(mAlarm.getNoOfDays() - 1);
                    mAlarm.setDays(alarmDays);
                    mAlarm.setAlarmDateTime(mRepeatAlarms.get(0).getAlarmDateTime());
                    mAlarm.setLongAlarmTime(mRepeatAlarms.get(0).getAlarmDateTime().getTime());
                    myDatabase.myDao().saveAlarmDetails(mAlarm);
                }
            } else {
                myAlarmManager.cancelAlarm(mAlarm.getPendingID());
                myDatabase.myDao().deleteAlarmDetails(mAlarm.getId());
            }
        }

    }
}