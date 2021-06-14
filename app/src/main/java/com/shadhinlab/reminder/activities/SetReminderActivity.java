package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.dialogs.ConfirmationPopup;
import com.shadhinlab.reminder.dialogs.FireAlarmPopup;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MRepeatAlarm;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.RingtonePlayer;
import com.shadhinlab.reminder.tools.Utils;
import com.shadhinlab.reminder.tools.VibratePlayer;

import java.util.Calendar;
import java.util.List;

public class SetReminderActivity extends AppCompatActivity implements RingtonePlayer.OnFinishListener {
    public static MAlarm updateAlarm;
    private int pickBeforeTime, pickAfterTime, alarmSize = 0, prayerWakto;
    private String prayerTime = "";
    private List<MAlarm> alarmList;
    private SeekBar seekbar, seekbarAfter;
    private MyDatabase myDatabase;
    private Button btnSetAlarm, btnTestAlarm;
    private MyAlarmManager myAlarmManager;
    private MAlarm mAlarm;

    private RingtonePlayer ringtonePlayer;
    private VibratePlayer vibratePlayer;
    private FireAlarmPopup fireAlarmPopup;
    private ConfirmationPopup confirmationPopup;
    private TextView tvPickingTime, tvPickingAfterTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);
        init();
        seekbarSetup();

    }

    private void init() {
        prayerWakto = getIntent().getIntExtra("PrayerWakto", 0);
        prayerTime = getIntent().getStringExtra("PrayerTime");
        Utils.log("Intent: " + prayerWakto);
        Utils.log("Intent time: " + prayerTime.split(":")[0]);
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);
        alarmList = myDatabase.myDao().getAlarmDetails();
        tvPickingTime = findViewById(R.id.tvPickingTime);
        tvPickingAfterTime = findViewById(R.id.tvPickingAfterTime);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnTestAlarm = findViewById(R.id.btnTestAlarm);

        seekbar = findViewById(R.id.seekbar);
        seekbarAfter = findViewById(R.id.seekbarAfter);

        btnSetAlarm.setOnClickListener(view -> clickSetAlarm());

        btnTestAlarm.setOnClickListener(view -> testAlarm());

        initAlarmPopup(Utils.getPref(Global.CONTENT_TEXT, Global.DEFAULT_DIA_TEXT));

        confirmationPopup = new ConfirmationPopup(this, "Did you really wake up?") {
            @Override
            protected void onButtonClick(View view) {
                if (view.getId() == R.id.tvYes) {
                    ringtonePlayer.stop();
                    vibratePlayer.stopVibrate();
                    fireAlarmPopup.dismiss();
                }
//                else if (view.getId() == R.id.tvNo) {
//                    fireAlarmPopup.showTestAlarm();
//                }
            }
        };
    }

    private void initAlarmPopup(String diaText) {
        fireAlarmPopup = new FireAlarmPopup(this, diaText, 1) {
            @Override
            protected void onButtonClick(View view) {
                confirmationPopup.show();
            }

        };
    }


    private void testAlarm() {
//        Utils.statusBarTransparent(SetAlarmActivity.this);
//        Utils.changeStatusBarOthers(SetAlarmActivity.this, Color.TRANSPARENT);
        vibratePlayer = new VibratePlayer(this);
        fireAlarmPopup.showTestAlarm();
        ringtonePlayer = new RingtonePlayer(SetReminderActivity.this);
        ringtonePlayer.start();
        vibratePlayer.vibrateStart();
    }

    private void cancelAlarm() {
        List<MRepeatAlarm> mRepeatAlarms = myDatabase.myDao().getRepeatedAlarmDetails(true, updateAlarm.getId());
        for (MRepeatAlarm mRepeatAlarm : mRepeatAlarms) {
            if (mRepeatAlarm.isDay())
                myAlarmManager.cancelAlarm(mRepeatAlarm.getPendingID());

        }
        myDatabase.myDao().deleteRepeatAlarmDetails(updateAlarm.getId());
        if (mRepeatAlarms.size() > 0)
            myAlarmManager.cancelAlarm(updateAlarm.getPendingID());
    }


    private void clickSetAlarm() {
//        cancelAlarm();
        if (pickBeforeTime > 5)
            createBeforeAlarm(Integer.parseInt(prayerTime.split(":")[0]), Integer.parseInt(prayerTime.split(":")[1]));
        if (pickAfterTime > 5)
            createAfterAlarm(Integer.parseInt(prayerTime.split(":")[0]), Integer.parseInt(prayerTime.split(":")[1]));
    }

    private void seekbarSetup() {
        if (pickBeforeTime > 0) {
            tvPickingTime.setText(pickBeforeTime + " Minutes");
            int pickTime2 = pickBeforeTime / 5;
            seekbar.setProgress(pickTime2);
        } else {
            tvPickingTime.setText(pickBeforeTime + " Minutes");
            seekbar.setProgress(pickBeforeTime);
        }
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pickBeforeTime = progress / 5;
                pickBeforeTime = progress * 5;
                tvPickingTime.setText(pickBeforeTime + " Minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (pickAfterTime > 0) {
            tvPickingAfterTime.setText(pickAfterTime + " Minutes");
            int pickTime2 = pickBeforeTime / 5;
            seekbarAfter.setProgress(pickTime2);
        } else {
            tvPickingAfterTime.setText(pickAfterTime + " Minutes");
            seekbar.setProgress(pickBeforeTime);
        }
        seekbarAfter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pickAfterTime = progress / 5;
                pickAfterTime = progress * 5;
                tvPickingAfterTime.setText(pickAfterTime + " Minutes");
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
        String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickBeforeTime));
        Utils.log("pickTimes: " + pickTimes);
        myAlarmManager.setSingleAlarm(hour, minute, pickBeforeTime, prayerWakto, 123, "", true, false);
//        myAlarmManager.setSingleAlarm(Calendar.FRIDAY, hour, minute, pickBeforeTime, updateAlarm.getPendingID(), "", false);
        saveDb(hour, minute, pickTimes, true);

    }

    private void createAfterAlarm(int hour, int minute) {
        String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickAfterTime));
        Utils.log("pickAfterTimes: " + pickTimes);
        myAlarmManager.setSingleAlarm(hour, minute, pickAfterTime, prayerWakto, 123, "", false, false);
        myAlarmManager.setNextDayAlarmPrayer(hour, minute, pickAfterTime, prayerWakto, 123, "", false, false);
        saveDb(hour, minute, pickTimes, false);
    }

    private void saveDb(int hour, int minute, String pickTimes, boolean isBefore) {
        mAlarm = new MAlarm();
        mAlarm.setBeforePrayerTime(pickBeforeTime);
        mAlarm.setAfterPrayerTime(pickAfterTime);
        mAlarm.setPrayerWakto(prayerWakto);
        mAlarm.setBeforeAlarm(isBefore);
        mAlarm.setHour(hour);
        mAlarm.setMin(minute);
        mAlarm.setPendingID(myAlarmManager.pendingId);
        mAlarm.setLongAlarmTime(myAlarmManager.alarmTimeLong);
        mAlarm.setPickTime(pickTimes);
    }


    private boolean isAlreadyPickTime(String pickTimes) {
        if (alarmList.size() > 0) {
            for (int i = 0; i < alarmList.size(); i++) {
                if (alarmList.get(i).getPickTime().equals(pickTimes)) {
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ringtonePlayer != null && hasWindowFocus()) {
            ringtonePlayer.stop();
            vibratePlayer.stopVibrate();
            fireAlarmPopup.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
            vibratePlayer.stopVibrate();
            fireAlarmPopup.dismiss();
        }
    }


    @Override
    public void onPlayerFinished() {
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
            vibratePlayer.stopVibrate();
            fireAlarmPopup.dismiss();
        }
    }

}