package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MReminderNumber;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.Utils;

public class SetReminderCallActivity extends AppCompatActivity {
    private SeekBar sbBeforeStart;
    private TextView tvPickingBeforeStartTime;
    private Button btnSetNumber;
    private int pickBeforeStartTime;
    private MyAlarmManager myAlarmManager;
    private MyDatabase myDatabase;
    private String prayerStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder_call);
        prayerStartTime = getIntent().getStringExtra(Global.PRAYER_START_TIME);
        sbBeforeStart = findViewById(R.id.sbBeforeStart);
        tvPickingBeforeStartTime = findViewById(R.id.tvPickingBeforeStartTime);
        btnSetNumber = findViewById(R.id.btnSetNumber);
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);
        seekbarSetup();
        btnSetNumber.setOnClickListener(v -> {
            createBeforeAlarm(Integer.parseInt(prayerStartTime.split(":")[0]), Integer.parseInt(prayerStartTime.split(":")[1]));
        });
    }

    private void seekbarSetup() {
        if (pickBeforeStartTime > 0) {
            tvPickingBeforeStartTime.setText(pickBeforeStartTime + " Minutes");
            int pickTime2 = pickBeforeStartTime / 5;
            sbBeforeStart.setProgress(pickTime2);
        } else {
            tvPickingBeforeStartTime.setText(pickBeforeStartTime + " Minutes");
            sbBeforeStart.setProgress(pickBeforeStartTime);
        }
        sbBeforeStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pickBeforeStartTime = progress / 5;
                pickBeforeStartTime = progress * 5;
                tvPickingBeforeStartTime.setText(pickBeforeStartTime + " Minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    private void createBeforeAlarm(int hour, int minute) {
        String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickBeforeStartTime));
        Utils.log("pickTimes: " + pickTimes);
        myAlarmManager.setSingleAlarm(hour, minute, pickBeforeStartTime, 0, 123, "Call", true, false);
        saveDb(hour, minute, pickTimes);

    }

    private void saveDb(int hour, int minute, String reminderTime) {
        MReminderNumber mReminderNumber=new MReminderNumber();
        mReminderNumber.setHour(hour);
        mReminderNumber.setMinute(minute);
        mReminderNumber.setPickedBeforeTime(pickBeforeStartTime);
        mReminderNumber.setReminderTime(reminderTime);
        myDatabase.myDao().saveReminderNumber(mReminderNumber);
        Utils.showToast("Save");
        Utils.log("reminders: "+myDatabase.myDao().getReminderNumber().size());
    }
}