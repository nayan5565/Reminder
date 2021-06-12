package com.shadhinlab.reminder.db;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shadhinlab.reminder.models.MPrayerTime;

import java.util.List;

@Dao
public interface MyDao {
    @Query("delete from prayer_times ")
    int clearPrayerTimes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePrayerTimes(MPrayerTime prayerTimes);


    @Query("select * from prayer_times")
    MPrayerTime getPrayerTimes();


}