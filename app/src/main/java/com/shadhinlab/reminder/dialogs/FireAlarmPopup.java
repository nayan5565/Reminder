package com.shadhinlab.reminder.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;


import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.tools.DismissButtonNameGiver;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class FireAlarmPopup {
    private Context context;
    private Dialog dialog;
    private String dialogText = "";
    private int logicBtnGone = 0;

    public FireAlarmPopup(Context context, String dialogText, int logicBtnGone) {
        this.context = context;
        this.dialogText = dialogText;
        this.logicBtnGone = logicBtnGone;
    }

    protected abstract void onButtonClick(View view);

    @SuppressLint("SetTextI18n")
    public void showTestAlarm() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.alarm_bg);
        list.add(R.drawable.alarm_bg2);
        list.add(R.drawable.alarm_bg3);
//        dialog = new Dialog(context);
        dialog = new Dialog(context, R.style.full_screen_dialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.test_dismiss_alarm_popup);
        dialog.setCancelable(false);
        if (dialog.getWindow() == null)
            return;
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
//            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tvDiaMessage = dialog.findViewById(R.id.tvDiaMessage);
        ConstraintLayout dismissLayout = dialog.findViewById(R.id.dismissLayout);
        ConstraintLayout cnlAlarmMain = dialog.findViewById(R.id.cnlAlarmMain);
        TextView tvSnooze = dialog.findViewById(R.id.tvSnooze);
        TextView tvStop = dialog.findViewById(R.id.tvStop);
        TextClock text_clock_dismiss = dialog.findViewById(R.id.text_clock_dismiss);
        TextClock text_clock_dismissAmPm = dialog.findViewById(R.id.text_clock_dismissAmPm);
        DismissButtonNameGiver dismissButtonNameGiver = new DismissButtonNameGiver(context);
        Collections.shuffle(list);
        cnlAlarmMain.setBackgroundResource(list.get(0));

        if (!TextUtils.isEmpty(dialogText))
            tvDiaMessage.setText(dialogText + ".");
        else
            tvDiaMessage.setText(Utils.getPref(Global.SNOOZE_CONTENT_TEXT, Global.DEFAULT_DIA_TEXT));

//
//        View layout = dialog.findViewById(R.id.dismissLayout);
//
//        layout.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

//        CustomView.cornerView(dismissLayout, 5, Color.parseColor("#FCB13E"), Color.parseColor("#FCB13E"), 40.0f);
//        CustomView.cornerView(tvSnooze, 5, Color.RED, Color.RED, 15.0f);
//        CustomView.cornerView(dismissLayout, 5, Color.BLACK, Color.BLACK, 40.0f);
//        dismissLayout.setAlpha(0.50f);

        if (logicBtnGone < 1) {
            tvStop.setVisibility(View.VISIBLE);
            tvSnooze.setText("Snooze");
            if (Utils.getPrefBoolean(Global.SNOOZE_ENABLE, true)) {
                tvSnooze.setVisibility(View.VISIBLE);
            } else tvSnooze.setVisibility(View.GONE);
        } else {
            tvStop.setVisibility(View.GONE);
            tvSnooze.setText("Stop");
        }

        //            dialog.dismiss();
        tvSnooze.setOnClickListener(this::onButtonClick);
        //            dialog.dismiss();
        tvStop.setOnClickListener(this::onButtonClick);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}