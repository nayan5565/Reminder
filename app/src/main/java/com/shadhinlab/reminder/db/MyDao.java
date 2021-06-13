package com.shadhinlab.reminder.db;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.models.MRepeatAlarm;

import java.util.Date;
import java.util.List;

@Dao
public interface MyDao {
    @Query("delete from prayer_times ")
    int clearPrayerTimes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePrayerTimes(MPrayerTime prayerTimes);


    @Query("select * from prayer_times")
    MPrayerTime getPrayerTimes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveAlarmDetails(MAlarm alarm);

    @Query("select * from alarm_list order by alarmDateTime asc")
    List<MAlarm> getAlarmDetails();

    @Query("select * from alarm_list")
    List<MAlarm> getAllAlarmDetails();

    @Query("select * from alarm_list where id=:id")
    MAlarm getAlarmDetails(int id);

    @Query("select * from alarm_list where pendingID=:pendingID")
    MAlarm getAlarmDetailsByPending(int pendingID);


    @Query("delete from alarm_list where id=:id")
    int deleteAlarmDetails(int id);

    @Query("delete from alarm_list where alarmDateTime<:date")
    int deleteAlarmDetailsByDate(Date date);

    @Query("delete from alarm_list")
    void deleteAllAlarmDetails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRepeatAlarmDetails(MRepeatAlarm repeatAlarm);

    @Query("select * from repeat_alarm where id=:id")
    List<MRepeatAlarm> getRepeatAlarmDetails(int id);

    @Query("select * from repeat_alarm where id=:id AND isDay=:isDay order by alarmDateTime asc")
    List<MRepeatAlarm> getRepeatedAlarmDetails(boolean isDay, int id);

    @Query("delete from repeat_alarm where id=:parentID")
    void deleteRepeatAlarmDetails(int parentID);

}