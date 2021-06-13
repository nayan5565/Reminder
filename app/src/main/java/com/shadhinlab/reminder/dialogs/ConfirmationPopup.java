package com.shadhinlab.reminder.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.tools.CustomView;


public abstract class ConfirmationPopup {
    private Context context;
    private Dialog dialog;
    private String title;

    public ConfirmationPopup(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    protected abstract void onButtonClick(View view);

    public void show() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dia_confirm);
        dialog.setCancelable(false);
        if (dialog.getWindow() == null)
            return;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout mainConfirm = dialog.findViewById(R.id.mainConfirm);
        TextView tvYes = dialog.findViewById(R.id.tvYes);
        TextView tvNo = dialog.findViewById(R.id.tvNo);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvNo.setOnClickListener(view -> {
            dialog.dismiss();
            onButtonClick(view);
        });
        tvYes.setOnClickListener(view -> {
            dialog.dismiss();
            onButtonClick(view);
        });


        tvTitle.setText(title);

        CustomView.cornerView(mainConfirm, 5, Color.parseColor("#FCB13E"), Color.parseColor("#FCB13E"), 20.0f);

        dialog.show();
    }
}
