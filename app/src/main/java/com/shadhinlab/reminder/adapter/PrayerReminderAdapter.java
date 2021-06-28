package com.shadhinlab.reminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.models.MPrayerReminder;

import java.util.ArrayList;
import java.util.List;

public abstract class PrayerReminderAdapter extends RecyclerView.Adapter<PrayerReminderAdapter.MyViewHolder> {
    private List<MPrayerReminder> mHijriReminders;
    private Context context;
    private LayoutInflater inflater;

    public PrayerReminderAdapter(Context context) {
        mHijriReminders = new ArrayList<>();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<MPrayerReminder> mAlarms) {
        this.mHijriReminders = mAlarms;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public PrayerReminderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_hijri, parent, false);
        return new PrayerReminderAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PrayerReminderAdapter.MyViewHolder holder, final int position) {
        MPrayerReminder mReminderNumber = mHijriReminders.get(position);
        holder.tvDay.setText("Set reminder "+mReminderNumber.getReminderTime());
        holder.tvDay.setTextColor(Color.WHITE);
//        holder.relDay.setBackgroundResource(R.drawable.circle_unselect);

        holder.relDay.setOnClickListener(view -> onClickItem(position, view));
    }

    @Override
    public int getItemCount() {
        return mHijriReminders.size();
    }

    public abstract void onClickItem(int itemPosition, View view);

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay;
        RelativeLayout relDay;

        MyViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDayName);
            relDay = itemView.findViewById(R.id.relDay);
        }
    }
}