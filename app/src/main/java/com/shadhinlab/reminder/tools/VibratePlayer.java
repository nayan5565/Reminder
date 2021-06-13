package com.shadhinlab.reminder.tools;

import android.content.Context;
import android.os.Vibrator;

public class VibratePlayer {
    private Vibrator vibrator;
    private Context context;

    public VibratePlayer(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrateStart() {
        String durationStr = Utils.getPref(Global.DURATION_RINGTONE, "120 seconds");
        int vibrateDuration = Integer.parseInt(durationStr.split(" ")[0]);
        if (Utils.getPrefBoolean(Global.VIBRATE_ENABLE, true))
            vibrator.vibrate(vibrateDuration * 1000);
    }

    public void stopVibrate() {
        if (vibrator != null && vibrator.hasVibrator())
            vibrator.cancel();
    }
}
