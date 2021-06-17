package com.shadhinlab.reminder.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MReminderNumber;
import com.shadhinlab.reminder.models.MPrayer;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.models.MRepeatAlarm;

@Database(entities = {MAlarm.class, MRepeatAlarm.class, MPrayer.class, MPrayerTime.class, MReminderNumber.class}, version = 1, exportSchema = false)
@TypeConverters(MyConverters.class)
public abstract class MyDatabase extends RoomDatabase {
    private static MyDatabase instance;

    public static MyDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, MyDatabase.class, "TShop")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }

    public abstract MyDao myDao();
}