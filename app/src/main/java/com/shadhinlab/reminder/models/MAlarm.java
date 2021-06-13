package com.shadhinlab.reminder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "alarm_list")
public class MAlarm {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int beforeSunriseTime, pendingID, hour, min, noOfDays;
    private long longAlarmTime;
    private String alarmTime, alarmDate, pickTime, alarmDateTimes, days, alarmName, dialogText;
    private Date alarmDateTime;
    private boolean isStopAlarm, isLongClick, isVibrate;

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

    public int getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
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

    public String getAlarmDateTimes() {
        return alarmDateTimes;
    }

    public void setAlarmDateTimes(String alarmDateTimes) {
        this.alarmDateTimes = alarmDateTimes;
    }

    public String getPickTime() {
        return pickTime;
    }

    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
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

    public int getBeforeSunriseTime() {
        return beforeSunriseTime;
    }

    public void setBeforeSunriseTime(int beforeSunriseTime) {
        this.beforeSunriseTime = beforeSunriseTime;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }
}