package com.shadhinlab.reminder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "repeat_alarm")
public class MRepeatAlarm {
    @PrimaryKey(autoGenerate = true)
    private int repeatId;
    private int id, pendingID, hour, minute;
    private int tag;
    private String day;
    private boolean isDay, isAll;
    private Date alarmDateTime;

    public Date getAlarmDateTime() {
        return alarmDateTime;
    }

    public void setAlarmDateTime(Date alarmDateTime) {
        this.alarmDateTime = alarmDateTime;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getPendingID() {
        return pendingID;
    }

    public void setPendingID(int pendingID) {
        this.pendingID = pendingID;
    }

    public int getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(int repeatId) {
        this.repeatId = repeatId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getDay() {
        return day;
    }

    public boolean isDay() {
        return isDay;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }
}
