package com.shadhinlab.reminder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminder_number")
public class MReminderNumber {
    private String number, reminderTime;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hour, minute, pickedBeforeTime;

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public int getPickedBeforeTime() {
        return pickedBeforeTime;
    }

    public void setPickedBeforeTime(int pickedBeforeTime) {
        this.pickedBeforeTime = pickedBeforeTime;
    }
}
