package com.shadhinlab.reminder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hijri_reminder")
public class MHijriReminder {
    private String reminderTime;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hour, minute;

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
