package com.shadhinlab.reminder.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "arabic_english_month")
public class MArabicEnglishMonth {
    @PrimaryKey
    @NonNull
    private String enDate;
    private String arDate, arDay, arYear;
    private int enDay, enMonth;

    @NonNull
    public String getEnDate() {
        return enDate;
    }

    public void setEnDate(@NonNull String enDate) {
        this.enDate = enDate;
    }

    public int getEnDay() {
        return enDay;
    }

    public void setEnDay(int enDay) {
        this.enDay = enDay;
    }

    public int getEnMonth() {
        return enMonth;
    }

    public void setEnMonth(int enMonth) {
        this.enMonth = enMonth;
    }

    public String getArDate() {
        return arDate;
    }

    public void setArDate(String arDate) {
        this.arDate = arDate;
    }

    public String getArDay() {
        return arDay;
    }

    public void setArDay(String arDay) {
        this.arDay = arDay;
    }

    public String getArYear() {
        return arYear;
    }

    public void setArYear(String arYear) {
        this.arYear = arYear;
    }
}
