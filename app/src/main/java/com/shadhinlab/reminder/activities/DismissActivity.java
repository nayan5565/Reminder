package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.dialogs.ConfirmationPopup;
import com.shadhinlab.reminder.dialogs.FireAlarmPopup;
import com.shadhinlab.reminder.tools.DismissAlarmNotificationController;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.RingtonePlayer;
import com.shadhinlab.reminder.tools.ScheduleAlarm;
import com.shadhinlab.reminder.tools.SharedPreferencesHelper;
import com.shadhinlab.reminder.tools.Utils;
import com.shadhinlab.reminder.tools.VibratePlayer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class DismissActivity extends AppCompatActivity implements RingtonePlayer.OnFinishListener {
    DismissAlarmNotificationController dismissAlarmNotificationController;
    private FireAlarmPopup fireAlarmPopup;
    private SharedPreferencesHelper sharPrefHelper;
    private int numberOfAlreadyRangAlarms;
    private RingtonePlayer ringtonePlayer;
    private String contentValue = "";
    private VibratePlayer vibrator;
    private int pendingId, alarmNumber, alarmID, prayerWakto;
    private MyAlarmManager myAlarmManager;
    private ScheduleAlarm scheduleAlarm;
    private ConfirmationPopup confirmationPopup;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dismiss);
        Utils.statusBarTransparent(this);
        Utils.changeStatusBarOthers(this, Color.TRANSPARENT);
        showOnLockedScreen();
        View layout = findViewById(R.id.main);

        layout.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        init();
        scheduleAlarm.alarmAnalysis();
        fireAlarmPopup = new FireAlarmPopup(this, contentValue, 1) {
            @Override
            protected void onButtonClick(View view) {
                if (view.getId() == R.id.tvSnooze) {
                    ringtonePlayer.stop();
                    vibrator.stopVibrate();
                    fireAlarmPopup.dismiss();
                    finish();
                } else if (view.getId() == R.id.tvStop) {
//                    if (Utils.getPrefBoolean(Global.SNOOZE_ENABLE, true) && Utils.getPref(Global.SNOOZE_REPEAT, 3) == 0) {
//                        myAlarmManager.cancelAlarm(123);
//                    }

                    confirmationPopup.show();

                }

            }
        };
        fireAlarmPopup.showTestAlarm();
        if (contentValue.equals("Call"))
            Utils.call("1955206144");
        confirmationPopup = new ConfirmationPopup(this, "Did you really wake up?") {
            @Override
            protected void onButtonClick(View view) {
                if (view.getId() == R.id.tvYes) {
                    if (Utils.getPrefBoolean(Global.SNOOZE_ENABLE, true)) {
                        Utils.savePrefBoolean(Global.SNOOZE_START_ALREADY, false);
                        myAlarmManager.cancelAlarm(pendingId);
                    }

//                    scheduleAlarm.cancelAlarm(myDatabase.myDao().getAlarmDetails(alarmID));
                    if (contentValue.equals(Global.REMINDER_HIJRI))
                        scheduleAlarm.callHijriCalender();
                    else
                        scheduleAlarm.nextWaktoAlarm(prayerWakto, pendingId);
                    ringtonePlayer.stop();
                    vibrator.stopVibrate();
                    fireAlarmPopup.dismiss();
                    finish();
                }
//                else if (view.getId() == R.id.tvNo) {
//                   fireAlarmPopup.showTestAlarm();
//                }
            }
        };

        ringtonePlayer.start();
        vibrator.vibrateStart();

//        snoozeLogic();
    }

    private void init() {
        scheduleAlarm = new ScheduleAlarm(this);
        myAlarmManager = new MyAlarmManager(this);
        myDatabase = MyDatabase.getInstance(this);
        contentValue = getIntent().getStringExtra("ContentValue");
        if (!TextUtils.isEmpty(contentValue))
            Utils.savePref(Global.SNOOZE_CONTENT_TEXT, contentValue);
        pendingId = getIntent().getIntExtra("PendingId", 0);
//        prayerWakto = getIntent().getIntExtra("Prayer", 0);
        prayerWakto = getIntent().getIntExtra("AlarmID", 0);
        alarmNumber = Utils.getPref(Global.SNOOZE_REPEAT, 1);
//        alarmNumber = getIntent().getIntExtra(pendingId + "", 0);
        Utils.log("Content : " + contentValue + " pendingId : " + pendingId + " prayerWakto : " + prayerWakto + " AlarmID : " + alarmID);

        vibrator = new VibratePlayer(this);

        dismissAlarmNotificationController = new DismissAlarmNotificationController(DismissActivity.this);
        dismissAlarmNotificationController.cancelNotification();

        ringtonePlayer = new RingtonePlayer(DismissActivity.this);
        sharPrefHelper = new SharedPreferencesHelper(DismissActivity.this);
    }

    private void snoozeLogic() {
        scheduleAlarm.snoozeAlarm(pendingId, contentValue);
        if (Utils.getPrefBoolean(Global.SNOOZE_ENABLE, true)) {
            if (alarmNumber > 0) {
                numberOfAlreadyRangAlarms = sharPrefHelper.getNumberOfAlreadyRangAlarms() + 1;
                Utils.log("numberOfAlreadyRangAlarms (including current one) = " + numberOfAlreadyRangAlarms);
                sharPrefHelper.setNumberOfAlreadyRangAlarms(numberOfAlreadyRangAlarms);

                if (numberOfAlreadyRangAlarms >= alarmNumber) {
                    Utils.savePrefBoolean(Global.SNOOZE_START_ALREADY, false);
                    Utils.log("Alarm Over");
                    myAlarmManager.cancelAlarm(pendingId);
                    sharPrefHelper.setNumberOfAlreadyRangAlarms(0);
                }
            }
        }

    }


    @Override
    public void onPlayerFinished() {
        if (contentValue.equals(Global.REMINDER_HIJRI))
            scheduleAlarm.callHijriCalender();
        else
            scheduleAlarm.nextWaktoAlarm(prayerWakto, pendingId);
        fireAlarmPopup.dismiss();
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (hasWindowFocus()) {
            ringtonePlayer.stop();
            vibrator.stopVibrate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
            vibrator.stopVibrate();
        }
    }


    private void showOnLockedScreen() {
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
