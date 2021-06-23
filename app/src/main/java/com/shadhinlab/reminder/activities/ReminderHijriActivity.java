package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.os.Bundle;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.adapter.HijriReminderAdapter;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MArabicEnglishMonth;
import com.shadhinlab.reminder.models.MHijriReminder;
import com.shadhinlab.reminder.models.MReminderNumber;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class ReminderHijriActivity extends AppCompatActivity {
    public static MArabicEnglishMonth arabicEnglishMonth;

    private TextView time;
    private String format = "";
    private int mHour, mMinute, pickHour, pickMinute;
    Button btnSetReminder, btnPickTime;
    private MyAlarmManager myAlarmManager;
    private RecyclerView rvReminderHijri;
    private HijriReminderAdapter hijriReminderAdapter;
    private MyDatabase myDatabase;
    private List<MHijriReminder> hijriReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_hijri);

        time = findViewById(R.id.textView);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnPickTime = findViewById(R.id.btnPickTime);
        rvReminderHijri = findViewById(R.id.rvReminderHijri);
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);
        hijriReminders = new ArrayList<>();
        btnPickTime.setOnClickListener(v -> timePicker());
        btnSetReminder.setOnClickListener(v -> setReminder());
        hijriReminderAdapter = new HijriReminderAdapter(this) {
            @Override
            public void onClickItem(int itemPosition, View view) {

            }
        };
        rvReminderHijri.setLayoutManager(new LinearLayoutManager(this));
        rvReminderHijri.setAdapter(hijriReminderAdapter);
        display();
    }


    private void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> showTime(hourOfDay, minute), mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void showTime(int hourOfDay, int minute) {
        pickHour = hourOfDay;
        pickMinute = minute;
        time.setText(hourOfDay + ":" + minute);
    }

    private void setReminder() {
        if (pickHour > 0) {
            myAlarmManager.setAlarmDateWise(arabicEnglishMonth.getEnMonth(), arabicEnglishMonth.getEnDay(), pickHour, pickMinute, 0, 0, 123, "reminderHijri", "", false);
            String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(pickHour, pickMinute, 0));
            saveDb(pickHour, pickMinute, Utils.getDateTimeConverter(myAlarmManager.getTime));
        }
    }

    private void display() {
        hijriReminders = myDatabase.myDao().getHijriReminder();
        Utils.log("Reminders Size: " + hijriReminders.size());
        hijriReminderAdapter.setData(hijriReminders);
    }

    private void saveDb(int hour, int minute, String reminderTime) {
        MHijriReminder mReminderNumber = new MHijriReminder();
        mReminderNumber.setHour(hour);
        mReminderNumber.setMinute(minute);
        mReminderNumber.setReminderTime(reminderTime);
        myDatabase.myDao().saveHijriReminder(mReminderNumber);
        Utils.showToast("Save");
        display();
    }
}