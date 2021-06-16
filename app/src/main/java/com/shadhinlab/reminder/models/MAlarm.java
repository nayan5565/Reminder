package com.shadhinlab.reminder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "alarm_list")
public class MAlarm {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int beforePrayerStartTime, afterPrayerStartTime, beforePrayerEndTime, afterPrayerEndTime, pendingID, hour, min, prayerWakto;
    private long longAlarmTime;
    private String alarmName, dialogText, pickTime;
    private Date alarmDateTime;
    private boolean isStopAlarm, isLongClick, isVibrate, isBeforeAlarm, isStartTime;

    public String getPickTime() {
        return pickTime;
    }

    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }

    public boolean isBeforeAlarm() {
        return isBeforeAlarm;
    }

    public void setBeforeAlarm(boolean beforeAlarm) {
        isBeforeAlarm = beforeAlarm;
    }

    public boolean isStartTime() {
        return isStartTime;
    }

    public void setStartTime(boolean startTime) {
        isStartTime = startTime;
    }

    public int getAfterPrayerStartTime() {
        return afterPrayerStartTime;
    }

    public void setAfterPrayerStartTime(int afterPrayerStartTime) {
        this.afterPrayerStartTime = afterPrayerStartTime;
    }

    public int getPrayerWakto() {
        return prayerWakto;
    }

    public void setPrayerWakto(int prayerWakto) {
        this.prayerWakto = prayerWakto;
    }

    public String getDialogText() {
        return dialogText;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }


    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }

    public boolean isStopAlarm() {
        return isStopAlarm;
    }

    public void setStopAlarm(boolean stopAlarm) {
        isStopAlarm = stopAlarm;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public long getLongAlarmTime() {
        return longAlarmTime;
    }

    public void setLongAlarmTime(long longAlarmTime) {
        this.longAlarmTime = longAlarmTime;
    }

    public int getPendingID() {
        return pendingID;
    }

    public void setPendingID(int pendingID) {
        this.pendingID = pendingID;
    }


    public Date getAlarmDateTime() {
        return alarmDateTime;
    }

    public void setAlarmDateTime(Date alarmDateTime) {
        this.alarmDateTime = alarmDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBeforePrayerStartTime() {
        return beforePrayerStartTime;
    }

    public void setBeforePrayerStartTime(int beforePrayerStartTime) {
        this.beforePrayerStartTime = beforePrayerStartTime;
    }

    public int getBeforePrayerEndTime() {
        return beforePrayerEndTime;
    }

    public void setBeforePrayerEndTime(int beforePrayerEndTime) {
        this.beforePrayerEndTime = beforePrayerEndTime;
    }

    public int getAfterPrayerEndTime() {
        return afterPrayerEndTime;
    }

    public void setAfterPrayerEndTime(int afterPrayerEndTime) {
        this.afterPrayerEndTime = afterPrayerEndTime;
    }
}