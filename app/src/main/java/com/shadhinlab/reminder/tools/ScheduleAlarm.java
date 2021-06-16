package com.shadhinlab.reminder.tools;

import android.content.Context;


import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MPrayerTime;

import java.util.ArrayList;
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

                if (alarmList.get(i).isStartTime()) {
                    int hour = Integer.parseInt(getTomorrowPrayerStartTime(alarmList.get(i).getPrayerWakto()).split(":")[0]);
                    int min = Integer.parseInt(getTomorrowPrayerStartTime(alarmList.get(i).getPrayerWakto()).split(":")[1]);
                    if (alarmList.get(i).isBeforeAlarm()) {
                        myAlarmManager.setSingleAlarm(hour, min, alarmList.get(i).getBeforePrayerStartTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                        myAlarmManager.setNextDayAlarmPrayer(hour, min, alarmList.get(i).getBeforePrayerStartTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                    } else {
                        myAlarmManager.setSingleAlarm(hour, min, alarmList.get(i).getAfterPrayerStartTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                        myAlarmManager.setNextDayAlarmPrayer(hour, min, alarmList.get(i).getAfterPrayerStartTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                    }
                } else {
                    int hour = Integer.parseInt(getTomorrowPrayerEndTime(alarmList.get(i).getPrayerWakto()).split(":")[0]);
                    int min = Integer.parseInt(getTomorrowPrayerEndTime(alarmList.get(i).getPrayerWakto()).split(":")[1]);
                    if (alarmList.get(i).isBeforeAlarm()) {
                        myAlarmManager.setSingleAlarm(hour, min, alarmList.get(i).getBeforePrayerEndTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                        myAlarmManager.setNextDayAlarmPrayer(hour, min, alarmList.get(i).getBeforePrayerEndTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                    } else {
                        myAlarmManager.setSingleAlarm(hour, min, alarmList.get(i).getAfterPrayerEndTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                        myAlarmManager.setNextDayAlarmPrayer(hour, min, alarmList.get(i).getAfterPrayerEndTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                    }
                }
            }
        }

    }

//    private String getTomorrowPrayerTIme(int prayerWakto) {
//        String time = "";
//        if (myDatabase.myDao().getPrayer() != null && myDatabase.myDao().getPrayer().getData().size() > 0) {
//            for (int i = 0; i < myDatabase.myDao().getPrayer().getData().size(); i++) {
//                if (myDatabase.myDao().getPrayer().getData().get(i).getDate().getReadable().equals(Utils.getTomorrowDate())) {
//                    MTime mTime = myDatabase.myDao().getPrayer().getData().get(i).getTimings();
//                    Utils.log("Wakto: " + prayerWakto + " : " + mTime.getIsha());
//                    if (prayerWakto == 1) {
//                        time = mTime.getFajr().split(" ")[0];
//                    } else if (prayerWakto == 2) {
//                        time = mTime.getDhuhr().split(" ")[0];
//                    } else if (prayerWakto == 3) {
//                        time = mTime.getAsr().split(" ")[0];
//                    } else if (prayerWakto == 4) {
//                        time = mTime.getMaghrib().split(" ")[0];
//                    } else if (prayerWakto == 5) {
//                        time = mTime.getIsha().split(" ")[0];
//                    }
//                    return time;
//                }
//            }
//        }
//        return time;
//    }

    private String getTomorrowPrayerStartTime(int prayerWakto) {
        MPrayerTime mPrayerTime = myDatabase.myDao().getPrayerTimesByDate(Utils.getTomorrowDate());
        String time = "";
        if (mPrayerTime != null && mPrayerTime.getDate() != null) {
            Utils.log("Wakto: " + prayerWakto + " : " + mPrayerTime.getStartAsr());
            if (prayerWakto == 1) {
                time = mPrayerTime.getStartFajr();
            } else if (prayerWakto == 2) {
                time = mPrayerTime.getStartDhuhr();
            } else if (prayerWakto == 3) {
                time = mPrayerTime.getStartAsr();
            } else if (prayerWakto == 4) {
                time = mPrayerTime.getStartMaghrib();
            } else if (prayerWakto == 5) {
                time = mPrayerTime.getStartIsha();
            }
            return time;
        }
        return time;
    }

    private String getTomorrowPrayerEndTime(int prayerWakto) {
        MPrayerTime mPrayerTime = myDatabase.myDao().getPrayerTimesByDate(Utils.getTomorrowDate());
        String time = "";
        if (mPrayerTime != null && mPrayerTime.getDate() != null) {
            Utils.log("Wakto: " + prayerWakto + " : " + mPrayerTime.getEndIsha());
            if (prayerWakto == 1) {
                time = mPrayerTime.getEndtFajr();
            } else if (prayerWakto == 2) {
                time = mPrayerTime.getEndDhuhr();
            } else if (prayerWakto == 3) {
                time = mPrayerTime.getEndAsr();
            } else if (prayerWakto == 4) {
                time = mPrayerTime.getEndMaghrib();
            } else if (prayerWakto == 5) {
                time = mPrayerTime.getEndIsha();
            }
            return time;
        }
        return time;
    }

    public void nextWaktoAlarm(int prayerWakto, int pendingId) {
        Utils.log("Next Day prayer: " + getTomorrowPrayerStartTime(prayerWakto));
        List<MAlarm> alarmList = myDatabase.myDao().getAlarmByWakto(prayerWakto, pendingId);
        Utils.log("Alarm size: " + alarmList.size() + " : " + pendingId);
        if (!getTomorrowPrayerStartTime(prayerWakto).isEmpty()) {


            if (alarmList.size() > 0) {
                for (int i = 0; i < alarmList.size(); i++) {
                    if (alarmList.get(i).isStartTime()) {
                        int hourStart = Integer.parseInt(getTomorrowPrayerStartTime(prayerWakto).split(":")[0]);
                        int minStart = Integer.parseInt(getTomorrowPrayerStartTime(prayerWakto).split(":")[1]);
                        if (alarmList.get(i).isBeforeAlarm())
                            myAlarmManager.setNextDayAlarmPrayer(hourStart, minStart, alarmList.get(i).getBeforePrayerStartTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                        else
                            myAlarmManager.setNextDayAlarmPrayer(hourStart, minStart, alarmList.get(i).getAfterPrayerStartTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                        saveDb(alarmList.get(i), hourStart, minStart);
                    } else {
                        int hourEnd = Integer.parseInt(getTomorrowPrayerEndTime(prayerWakto).split(":")[0]);
                        int minEnd = Integer.parseInt(getTomorrowPrayerEndTime(prayerWakto).split(":")[1]);
                        if (alarmList.get(i).isBeforeAlarm())
                            myAlarmManager.setNextDayAlarmPrayer(hourEnd, minEnd, alarmList.get(i).getBeforePrayerEndTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", true, true);
                        else
                            myAlarmManager.setNextDayAlarmPrayer(hourEnd, minEnd, alarmList.get(i).getAfterPrayerEndTime(), alarmList.get(i).getPrayerWakto(), alarmList.get(i).getPendingID(), "", false, true);
                        saveDb(alarmList.get(i), hourEnd, minEnd);
                    }


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
        mAlarm.setBeforePrayerStartTime(alarm.getBeforePrayerStartTime());
        mAlarm.setAfterPrayerStartTime(alarm.getAfterPrayerStartTime());
        mAlarm.setBeforePrayerEndTime(alarm.getBeforePrayerEndTime());
        mAlarm.setAfterPrayerEndTime(alarm.getAfterPrayerEndTime());
        mAlarm.setPrayerWakto(alarm.getPrayerWakto());
        mAlarm.setBeforeAlarm(alarm.isBeforeAlarm());
        mAlarm.setStartTime(alarm.isStartTime());
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