package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.util.Calendar;
import java.util.List;

public class SetPhoneCallReminderActivity extends AppCompatActivity {
    private SeekBar sbBeforeStart;
    private TextView tvPickingBeforeStartTime, tvPickDate, tvTitleBeforeStartTime;
    private EditText edtNumber;
    private RecyclerView rvReminderCall;
    private Button btnSetNumber;
    private int pickBeforeStartTime, pickDay = 0, pickMonth = 0, alarmType = 0;
    private MyAlarmManager myAlarmManager;
    private MyDatabase myDatabase;
    private String prayerStartTime;
    private ReminderCallAdapter reminderCallAdapter;
    private List<MReminderNumber> mReminderNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder_call);
        MainActivity.isSettings=false;
        init();
        seekbarSetup();
        display();
        btnSetNumber.setOnClickListener(v -> createBeforeAlarm(Integer.parseInt(prayerStartTime.split(":")[0]), Integer.parseInt(prayerStartTime.split(":")[1])));
    }

    private void init() {
        Utils.hideKeyboard(SetPhoneCallReminderActivity.this);
        prayerStartTime = getIntent().getStringExtra(Global.PRAYER_START_TIME);
        sbBeforeStart = findViewById(R.id.sbBeforeStart);
        tvPickingBeforeStartTime = findViewById(R.id.tvPickingBeforeStartTime);
        tvTitleBeforeStartTime = findViewById(R.id.tvTitleBeforeStartTime);
        tvPickDate = findViewById(R.id.tvPickDate);
        edtNumber = findViewById(R.id.edtNumber);
        rvReminderCall = findViewById(R.id.rvReminderCall);
        btnSetNumber = findViewById(R.id.btnSetNumber);
        mReminderNumbers = new ArrayList<>();
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);
        tvPickDate.setOnClickListener(v -> datePicker());
        tvTitleBeforeStartTime.setText("Before Fajr " + prayerStartTime);
        edtNumber.setOnEditorActionListener((v, actionId, event) -> {
            Utils.hideKeyboard(SetPhoneCallReminderActivity.this);
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

    private void datePicker() {
        final Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 1);
        long twoDays = 2 * 24 * 60 * 60 * 1000;
        int mYear = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> showTime(year, monthOfYear + 1, dayOfMonth), mYear, month, mDayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + twoDays);
//        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void showTime(int year, int month, int day) {
        Utils.log("Date: " + month);
        pickDay = day;
        pickMonth = month;
        String stDay = day < 10 ? "0" + day : day + "";
        String stMonth = month < 10 ? "0" + month : month + "";
        tvPickDate.setText(stDay + "-" + stMonth + "-" + year);
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
        Utils.log("AlarmType: " + alarmType);
        if (!edtNumber.getText().toString().trim().isEmpty()) {
            Utils.log("Number: " + edtNumber.getText().toString().trim());
            if (Utils.isValidNumber(edtNumber.getText().toString().trim())) {
                String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickBeforeStartTime));
                Utils.log("pickTimes: " + pickTimes);
                setAlarm(hour, minute);
                saveDb(hour, minute, pickTimes);
            } else Utils.showToast("Please input valid number");
        } else Utils.showToast("Please input number");

    }

    private void setAlarm(int hour, int minute) {
        Utils.log("AlarmType: " + alarmType);
        if (pickDay > 0) {
            if (alarmType == 0) {
                //single day
                myAlarmManager.setAlarmDateWise(pickMonth - 1,
                        pickDay, hour, minute, 0,
                        0, 123, Global.REMINDER_CALL, "", false);
            } else if (alarmType == 1) {
                //every day
                myAlarmManager.setAlarmDateWiseEveryDay(pickMonth - 1,
                        pickDay, hour, minute, 0,
                        0, 123, Global.REMINDER_CALL, "", false);
            } else if (alarmType == 2) {
                //weekly
                myAlarmManager.setAlarmDateWiseWeekly(pickMonth - 1,
                        pickDay, hour, minute, 0,
                        0, 123, Global.REMINDER_CALL, "", false);
            }
        } else {
            if (alarmType == 0) {
                //single day
                myAlarmManager.setSingleAlarm(hour, minute, pickBeforeStartTime, 0, 123,
                        Global.REMINDER_CALL, edtNumber.getText().toString().trim(), true, false);
            } else if (alarmType == 1) {
                //every day
                myAlarmManager.setAlarmDateWiseEveryDay(0,
                        0, hour, minute, 0,
                        0, 123, Global.REMINDER_CALL, "", false);
            } else if (alarmType == 2) {
                //weekly
                myAlarmManager.setAlarmDateWiseWeekly(0,
                        0, hour, minute, 0,
                        0, 123, Global.REMINDER_CALL, "", false);
            }

//                myAlarmManager.setTestAlarm(0, 0, Utils.getMinute() + 1, 0, 1, 123, "Call", edtNumber.getText().toString().trim(), false);
        }

    }

    private void saveDb(int hour, int minute, String reminderTime) {
        MReminderNumber mReminderNumber = new MReminderNumber();
        mReminderNumber.setNumber(edtNumber.getText().toString());
        mReminderNumber.setDay(pickDay);
        mReminderNumber.setMonth(pickMonth);
        mReminderNumber.setAlarmType(alarmType);
        mReminderNumber.setHour(hour);
        mReminderNumber.setMinute(minute);
        mReminderNumber.setPickedBeforeTime(pickBeforeStartTime);
        mReminderNumber.setReminderTime(Utils.getDateTimeConverter(myAlarmManager.getTime));
        myDatabase.myDao().saveReminderNumber(mReminderNumber);
        Utils.showToast("Save");
        edtNumber.setText("");
        display();
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rbSingleDay:
                if (checked)
                    alarmType = 0;
                break;
            case R.id.rbEveryDay:
                if (checked)
                    alarmType = 1;
                break;
            case R.id.rbWeekly:
                if (checked)
                    alarmType = 2;
                break;

        }
    }
}