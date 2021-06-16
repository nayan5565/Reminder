package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.List;

public class SetReminderActivity extends AppCompatActivity implements RingtonePlayer.OnFinishListener {
    public static MAlarm updateAlarm;
    private int pickBeforeStartTime, pickAfterStartTime, pickBeforeEndTime,
            pickAfterEndTime, alarmSize = 0, prayerWakto;
    private String prayerStartTime = "", prayerEndTime = "", prayerWaktoName = "";
    private List<MAlarm> alarmList;
    private SeekBar sbBeforeStart, sbAfterStart, sbBeforeEnd, sbAfterEnd;
    private MyDatabase myDatabase;
    private Button btnSetAlarm, btnTestAlarm;
    private MyAlarmManager myAlarmManager;
    private MAlarm mAlarm;
    private Toolbar toolbar;
    private RingtonePlayer ringtonePlayer;
    private VibratePlayer vibratePlayer;
    private FireAlarmPopup fireAlarmPopup;
    private ConfirmationPopup confirmationPopup;
    private TextView tvPickingBeforeStartTime, tvPickingAfterStartTime, tvPickingBeforeEndTime, tvPickingAfterEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);
        init();
        seekbarSetup();
        myAlarmManager.setSingleAlarm(16, 23, 3, prayerWakto, 123, "", false, false);
    }

    private void init() {
        prayerWakto = getIntent().getIntExtra(Global.PRAYER_WAKTO, 0);
        prayerStartTime = getIntent().getStringExtra(Global.PRAYER_START_TIME);
        prayerEndTime = getIntent().getStringExtra(Global.PRAYER_END_TIME);
        prayerWaktoName = getIntent().getStringExtra(Global.PRAYER_WAKTO_NAME);

        Utils.log("PrayerWakto init: " + prayerWakto);
        Utils.log("Intent time: " + prayerStartTime.split(":")[0]);
        Utils.log("Intent end time: " + prayerEndTime.split(":")[0]);
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);
        alarmList = myDatabase.myDao().getAlarmDetails();
        tvPickingBeforeStartTime = findViewById(R.id.tvPickingBeforeStartTime);
        tvPickingAfterStartTime = findViewById(R.id.tvPickingAfterStartTime);
        tvPickingBeforeEndTime = findViewById(R.id.tvPickingBeforeEndTime);
        tvPickingAfterEndTime = findViewById(R.id.tvPickingAfterEndTime);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnTestAlarm = findViewById(R.id.btnTestAlarm);
        toolbar = findViewById(R.id.toolbar);
        sbBeforeStart = findViewById(R.id.sbBeforeStart);
        sbAfterStart = findViewById(R.id.sbAfterStart);
        sbBeforeEnd = findViewById(R.id.sbBeforeEnd);
        sbAfterEnd = findViewById(R.id.sbAfterEnd);

        btnSetAlarm.setOnClickListener(view -> clickSetAlarm());

        btnTestAlarm.setOnClickListener(view -> testAlarm());

        initAlarmPopup(Utils.getPref(Global.CONTENT_TEXT, Global.DEFAULT_DIA_TEXT));

        toolbar.setTitle("Set reminder for " + prayerWaktoName);

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
        if (pickBeforeStartTime > 5)
            createBeforeAlarm(Integer.parseInt(prayerStartTime.split(":")[0]), Integer.parseInt(prayerStartTime.split(":")[1]));
        if (pickAfterStartTime > 5)
            createAfterAlarm(Integer.parseInt(prayerStartTime.split(":")[0]), Integer.parseInt(prayerStartTime.split(":")[1]));

        if (pickBeforeEndTime > 5)
            createBeforeEndAlarm(Integer.parseInt(prayerEndTime.split(":")[0]), Integer.parseInt(prayerEndTime.split(":")[1]));
        if (pickAfterEndTime > 5)
            createAfterEndAlarm(Integer.parseInt(prayerEndTime.split(":")[0]), Integer.parseInt(prayerEndTime.split(":")[1]));
    }

    private void seekbarSetup() {

        //sb1
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

        //sb2
        if (pickAfterStartTime > 0) {
            tvPickingAfterStartTime.setText(pickAfterStartTime + " Minutes");
            int pickTime2 = pickAfterStartTime / 5;
            sbAfterStart.setProgress(pickTime2);
        } else {
            tvPickingAfterStartTime.setText(pickAfterStartTime + " Minutes");
            sbAfterStart.setProgress(pickAfterStartTime);
        }
        sbAfterStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pickAfterStartTime = progress / 5;
                pickAfterStartTime = progress * 5;
                tvPickingAfterStartTime.setText(pickAfterStartTime + " Minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //sb3
        if (pickBeforeEndTime > 0) {
            tvPickingBeforeEndTime.setText(pickBeforeEndTime + " Minutes");
            int pickTime2 = pickBeforeEndTime / 5;
            sbBeforeEnd.setProgress(pickTime2);
        } else {
            tvPickingBeforeEndTime.setText(pickBeforeEndTime + " Minutes");
            sbBeforeEnd.setProgress(pickBeforeEndTime);
        }
        sbBeforeEnd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pickBeforeEndTime = progress / 5;
                pickBeforeEndTime = progress * 5;
                tvPickingBeforeEndTime.setText(pickBeforeEndTime + " Minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //sb4
        if (pickAfterEndTime > 0) {
            tvPickingAfterEndTime.setText(pickAfterEndTime + " Minutes");
            int pickTime2 = pickAfterEndTime / 5;
            sbAfterEnd.setProgress(pickTime2);
        } else {
            tvPickingAfterEndTime.setText(pickAfterEndTime + " Minutes");
            sbAfterEnd.setProgress(pickAfterEndTime);
        }
        sbAfterEnd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pickAfterEndTime = progress / 5;
                pickAfterEndTime = progress * 5;
                tvPickingAfterEndTime.setText(pickAfterEndTime + " Minutes");
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
        myAlarmManager.setSingleAlarm(hour, minute, pickBeforeStartTime, prayerWakto, 123, "", true, false);
//        myAlarmManager.setSingleAlarm(Calendar.FRIDAY, hour, minute, pickBeforeTime, updateAlarm.getPendingID(), "", false);
        saveDb(hour, minute, pickTimes, true, true);

    }

    private void createAfterAlarm(int hour, int minute) {
        String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickAfterStartTime));
        Utils.log("createAfterAlarm: " + prayerWakto);
        myAlarmManager.setSingleAlarm(hour, minute, pickAfterStartTime, prayerWakto, 123, "", false, false);
//        myAlarmManager.setNextDayAlarmPrayer(hour, minute, pickAfterStartTime, prayerWakto, 123, "", false, false);
        saveDb(hour, minute, pickTimes, false, true);
    }

    private void createBeforeEndAlarm(int hour, int minute) {
        String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickBeforeEndTime));
        Utils.log("pickTimes: " + pickTimes);
        myAlarmManager.setSingleAlarm(hour, minute, pickBeforeEndTime, prayerWakto, 123, "", true, false);
        saveDb(hour, minute, pickTimes, true, false);

    }

    private void createAfterEndAlarm(int hour, int minute) {
        String pickTimes = Utils.getTimeConverter(Utils.timeCalculate(hour, minute, pickAfterEndTime));
        Utils.log("createAfterAlarm: " + prayerWakto);
        myAlarmManager.setSingleAlarm(hour, minute, pickAfterEndTime, prayerWakto, 123, "", false, false);
        saveDb(hour, minute, pickTimes, false, false);
    }

    private void saveDb(int hour, int minute, String pickTimes, boolean isBefore, boolean isStartTime) {
        mAlarm = new MAlarm();
        mAlarm.setBeforePrayerStartTime(pickBeforeStartTime);
        mAlarm.setAfterPrayerStartTime(pickAfterStartTime);
        mAlarm.setBeforePrayerEndTime(pickBeforeEndTime);
        mAlarm.setAfterPrayerEndTime(pickAfterEndTime);
        mAlarm.setPrayerWakto(prayerWakto);
        mAlarm.setBeforeAlarm(isBefore);
        mAlarm.setStartTime(isStartTime);
        mAlarm.setHour(hour);
        mAlarm.setMin(minute);
        mAlarm.setPendingID(myAlarmManager.pendingId);
        mAlarm.setLongAlarmTime(myAlarmManager.alarmTimeLong);
        mAlarm.setPickTime(pickTimes);
        myDatabase.myDao().saveAlarmDetails(mAlarm);
        List<MAlarm> alarmList = myDatabase.myDao().getAlarmByWakto(prayerWakto, myAlarmManager.pendingId);
        Utils.log("Alarm size: " + alarmList.size() + " : " + myAlarmManager.pendingId + " : " + prayerWakto);
        for (int i = 0; i < alarmList.size(); i++) {
            Utils.log("DB: " + alarmList.get(i).getPendingID() + " : " + alarmList.get(i).getPrayerWakto());
        }
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