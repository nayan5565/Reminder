package com.shadhinlab.reminder.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ilya Anshmidt on 08.10.2017.
 */

public class RingtonePlayer {

    private final String LOG_TAG = RingtonePlayer.class.getSimpleName();
    private Context context;
    private SharedPreferencesHelper sharPrefHelper;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int initialRingerMode;
    private CountDownTimer countDownTimer;
    private int durationSeconds;
    private OnFinishListener onFinishListener;
    private boolean isReleased = false;

    public RingtonePlayer(Context context) {
        sharPrefHelper = new SharedPreferencesHelper(context);
        this.context = context;
        this.onFinishListener = (OnFinishListener) context;
    }

    public void start() {
        Utils.log("Playing started");
        setNormalRingerMode();

        durationSeconds = sharPrefHelper.getDurationInt();
        Utils.log("Duration : " + durationSeconds);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        if (durationSeconds > 0) {
            mediaPlayer.setLooping(true);
        }

        if (durationSeconds == 0) {
            durationSeconds = mediaPlayer.getDuration();
        }

        try {
            if (!TextUtils.isEmpty(Utils.getPref(Global.RINGTONE_URI, ""))) {
                Uri uri = Uri.parse(Utils.getPref(Global.RINGTONE_URI, ""));
                mediaPlayer.setDataSource(context, uri);
            } else
                mediaPlayer.setDataSource(context, getRingtone());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Getting data for mediaplayer failed: " + e);
            //if MediaPlayer fails to play ringtone from sharedPreferences, try to play default ringtone
            try {
                Log.d(LOG_TAG, "Using default ringtone");
                mediaPlayer.setDataSource(context, getDefaultRingtone());
                mediaPlayer.prepare();
            } catch (IOException e1) {
                Log.e(LOG_TAG, "Preparing MediaPlayer with default ringtone failed: " + e1);
            }
        }
        mediaPlayer.start();
        startCountDownTimer(durationSeconds);

        Utils.log("MediaPlayer started: duration = " + durationSeconds);
    }

    public void stop() {
        if (isReleased) {
            return;
        }
        stopCountDownTimer();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            isReleased = true;
        }
        setInitialRingerMode();

        if (onFinishListener != null) {
            onFinishListener.onPlayerFinished();
        }

    }

    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            return true;
        return false;
    }

    private void startCountDownTimer(int durationSec) {
        countDownTimer = new CountDownTimer(durationSec * 1000, durationSec * 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                stop();
            }
        };
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private Uri getRingtone() {
        String filename = sharPrefHelper.getRingtoneFileName();
        if (filename.equals("")) {   // if ringtone not chosen yet
            return getDefaultRingtone();
        } else {
            File ringtone = new File(context.getFilesDir(), filename);
            return Uri.fromFile(ringtone);
        }

    }

    private Uri getDefaultRingtone() {
        return sharPrefHelper.getDefaultRingtoneUri();
    }

    private void setNormalRingerMode() {  // in case phone is in "Vibrate" mode
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initialRingerMode = audioManager.getRingerMode();
        try {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } catch (SecurityException e) {
            Log.d(LOG_TAG, "Cannot set RingerMode: " + e);
        }
    }

    private void setInitialRingerMode() {
        audioManager.setRingerMode(initialRingerMode);
    }

    public interface OnFinishListener {
        void onPlayerFinished();
    }


}
