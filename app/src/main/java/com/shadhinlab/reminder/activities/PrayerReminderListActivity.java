package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.adapter.PrayerReminderAdapter;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MPrayerReminder;

import java.util.ArrayList;
import java.util.List;

public class PrayerReminderListActivity extends AppCompatActivity {

    RecyclerView rvReminderPrayer;
    PrayerReminderAdapter prayerReminderAdapter;
    MyDatabase myDatabase;
    List<MPrayerReminder> prayerReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_reminder_list);
        myDatabase = MyDatabase.getInstance(this);
        prayerReminders = new ArrayList<>();
        rvReminderPrayer = findViewById(R.id.rvReminderPrayer);
        rvReminderPrayer.setLayoutManager(new LinearLayoutManager(this));
        prayerReminderAdapter = new PrayerReminderAdapter(this) {
            @Override
            public void onClickItem(int itemPosition, View view) {

            }
        };
        rvReminderPrayer.setAdapter(prayerReminderAdapter);

        prayerReminders = myDatabase.myDao().getAllReminderPrayer();
        prayerReminderAdapter.setData(prayerReminders);


    }
}