package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.adapter.ReminderCallAdapter;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MReminderNumber;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class SetReminderCallActivity extends AppCompatActivity {
    private SeekBar sbBeforeStart;
    private TextView tvPickingBeforeStartTime;
    private EditText edtNumber;
    private RecyclerView rvReminderCall;
    private Button btnSetNumber;
    private int pickBeforeStartTime;
    private MyAlarmManager myAlarmManager;
    private MyDatabase myDatabase;
    private String prayerStartTime;
    private ReminderCallAdapter reminderCallAdapter;
    private List<MReminderNumber> mReminderNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder_call);
        init();
        seekbarSetup();
        display();
        btnSetNumber.setOnClickListener(v -> createBeforeAlarm(Integer.parseInt(prayerStartTime.split(":")[0]), Integer.parseInt(prayerStartTime.split(":")[1])));
    }

    private void init() {
        prayerStartTime = getIntent().getStringExtra(Global.PRAYER_START_TIME);
        sbBeforeStart = findViewById(R.id.sbBeforeStart);
        tvPickingBeforeStartTime = findViewById(R.id.tvPickingBeforeStartTime);
        edtNumber = findViewById(R.id.edtNumber);
        rvReminderCall = findViewById(R.id.rvReminderCall);
        btnSetNumber = findViewById(R.id.btnSetNumber);
        mReminderNumbers = new ArrayList<>();
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);

        edtNumber.setOnEditorActionListener((v, actionId, event) -> {
            Utils.hideKeyboard(SetReminderCallActivity.this);
            return false;
        });

        reminderCallAdapter = new ReminderCallAdapter(this) {
            @Override
            public void onClickItem(int itemPosition, View view) {

            }
        };
        rvReminderCall.setLayoutManager(new LinearLayoutManager(this));
        rvReminderCall.setAdapter(reminderCallAdapter);
    }

    private void display() {
        mReminderNumbers = myDatabase.myDao().getReminderNumber();
        Utils.log("Reminders Size: " + mReminderNumbers.size());
        reminderCallAdapter.setData(mReminderNumbers);
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
        if (!edtNumber.getText().toString().trim().isEmpty()) {
            if (Utils.isValidNumber(edtNumber.getText().toString().trim())) {
                String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickBeforeStartTime));
                Utils.log("pickTimes: " + pickTimes);
                myAlarmManager.setSingleAlarm(hour, minute, pickBeforeStartTime, 0, 123, "Call", edtNumber.getText().toString().trim(), true, false);
//                myAlarmManager.setTestAlarm(0, 0, Utils.getMinute() + 1, 0, 1, 123, "Call", edtNumber.getText().toString().trim(), false);
                saveDb(hour, minute, pickTimes);
            } else Utils.showToast("Please input valid number");
        } else Utils.showToast("Please input number");


    }

    private void saveDb(int hour, int minute, String reminderTime) {
        MReminderNumber mReminderNumber = new MReminderNumber();
        mReminderNumber.setNumber(edtNumber.getText().toString());
        mReminderNumber.setHour(hour);
        mReminderNumber.setMinute(minute);
        mReminderNumber.setPickedBeforeTime(pickBeforeStartTime);
        mReminderNumber.setReminderTime(reminderTime);
        myDatabase.myDao().saveReminderNumber(mReminderNumber);
        Utils.showToast("Save");
        edtNumber.setText("");
        display();
    }
}