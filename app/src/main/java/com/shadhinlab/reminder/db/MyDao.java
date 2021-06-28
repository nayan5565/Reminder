package com.shadhinlab.reminder.db;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shadhinlab.reminder.models.MPrayerReminder;
import com.shadhinlab.reminder.models.MArabicEnglishMonth;
import com.shadhinlab.reminder.models.MHijriReminder;
import com.shadhinlab.reminder.models.MPrayer;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.models.MReminderNumber;
import com.shadhinlab.reminder.models.MRepeatAlarm;

import java.util.Date;
import java.util.List;

@Dao
public interface MyDao {
    @Query("delete from prayer")
    int clearPrayer();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePrayer(MPrayer prayerTimes);


    @Query("select * from prayer")
    MPrayer getPrayer();

    @Query("delete from arabic_english_month")
    int clearHijriCalender();

    @Query("delete from hijri_reminder")
    int clearHijriReminder();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveHijriReminder(MHijriReminder hijriReminder);


    @Query("select * from hijri_reminder")
    List<MHijriReminder> getHijriReminder();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveHijriCalender(MArabicEnglishMonth arabicEnglishMonth);


    @Query("select * from arabic_english_month")
    List<MArabicEnglishMonth> getHijriCalender();


    @Query("delete from prayer_times")
    int clearPrayerTimes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePrayerTimes(MPrayerTime prayerTimes);


    @Query("select * from prayer_times")
    List<MPrayerTime> getPrayerTimes();

    @Query("select * from prayer_times where date=:date")
    MPrayerTime getPrayerTimesByDate(String date);

    @Query("delete from reminder_number")
    int clearReminderNumber();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveReminderNumber(MReminderNumber reminderNumber);


    @Query("select * from reminder_number")
    List<MReminderNumber> getReminderNumber();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveReminderPrayer(MPrayerReminder alarm);

    @Query("select * from prayer_reminder where prayerWakto=:prayerWakto AND pendingID=:pendingId")
    List<MPrayerReminder> getReminderPrayerWakto(int prayerWakto, int pendingId);

    @Query("select * from prayer_reminder order by alarmDateTime asc")
    List<MPrayerReminder> getReminderPrayerSorting();

    @Query("select * from prayer_reminder")
    List<MPrayerReminder> getAllReminderPrayer();

    @Query("select * from prayer_reminder where id=:id")
    MPrayerReminder getReminderPrayerDetails(int id);

    @Query("select * from prayer_reminder where pendingID=:pendingID")
    MPrayerReminder getReminderPrayerDetailsByPending(int pendingID);


    @Query("delete from prayer_reminder where id=:id")
    int deletePrayerReminder(int id);

    @Query("delete from prayer_reminder where alarmDateTime<:date")
    int deleteAllPrayerReminderByDate(Date date);

    @Query("delete from prayer_reminder")
    void deleteAllReminderPrayer();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRepeatAlarmDetails(MRepeatAlarm repeatAlarm);

    @Query("select * from repeat_alarm where id=:id")
    List<MRepeatAlarm> getRepeatAlarmDetails(int id);

    @Query("select * from repeat_alarm where id=:id AND isDay=:isDay order by alarmDateTime asc")
    List<MRepeatAlarm> getRepeatedAlarmDetails(boolean isDay, int id);

    @Query("delete from repeat_alarm where id=:parentID")
    void deleteRepeatAlarmDetails(int parentID);

}