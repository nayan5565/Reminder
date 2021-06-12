package com.shadhinlab.reminder.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "prayer_times")
public class MPrayerTime {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private List<MData> data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MData> getData() {
        return data;
    }

    public void setData(List<MData> data) {
        this.data = data;
    }
}
