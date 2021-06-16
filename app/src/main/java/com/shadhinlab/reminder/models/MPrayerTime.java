package com.shadhinlab.reminder.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prayer_times")
public class MPrayerTime {
    int id;
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String date;
    private String startFajr, endtFajr, sunrise, startDhuhr, endDhuhr, startAsr, endAsr, Sunset, startMaghrib, endMaghrib, startIsha, endIsha, Imsak, Midnight;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartFajr() {
        return startFajr;
    }

    public void setStartFajr(String startFajr) {
        this.startFajr = startFajr;
    }

    public String getEndtFajr() {
        return endtFajr;
    }

    public void setEndtFajr(String endtFajr) {
        this.endtFajr = endtFajr;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getStartDhuhr() {
        return startDhuhr;
    }

    public void setStartDhuhr(String startDhuhr) {
        this.startDhuhr = startDhuhr;
    }

    public String getEndDhuhr() {
        return endDhuhr;
    }

    public void setEndDhuhr(String endDhuhr) {
        this.endDhuhr = endDhuhr;
    }

    public String getStartAsr() {
        return startAsr;
    }

    public void setStartAsr(String startAsr) {
        this.startAsr = startAsr;
    }

    public String getEndAsr() {
        return endAsr;
    }

    public void setEndAsr(String endAsr) {
        this.endAsr = endAsr;
    }

    public String getSunset() {
        return Sunset;
    }

    public void setSunset(String sunset) {
        Sunset = sunset;
    }

    public String getStartMaghrib() {
        return startMaghrib;
    }

    public void setStartMaghrib(String startMaghrib) {
        this.startMaghrib = startMaghrib;
    }

    public String getEndMaghrib() {
        return endMaghrib;
    }

    public void setEndMaghrib(String endMaghrib) {
        this.endMaghrib = endMaghrib;
    }

    public String getStartIsha() {
        return startIsha;
    }

    public void setStartIsha(String startIsha) {
        this.startIsha = startIsha;
    }

    public String getEndIsha() {
        return endIsha;
    }

    public void setEndIsha(String endIsha) {
        this.endIsha = endIsha;
    }

    public String getImsak() {
        return Imsak;
    }

    public void setImsak(String imsak) {
        Imsak = imsak;
    }

    public String getMidnight() {
        return Midnight;
    }

    public void setMidnight(String midnight) {
        Midnight = midnight;
    }
}
