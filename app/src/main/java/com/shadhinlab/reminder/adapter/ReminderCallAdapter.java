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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.models.MReminderNumber;
import com.shadhinlab.reminder.models.MTime;

import java.util.ArrayList;
import java.util.List;


public abstract class ReminderCallAdapter extends RecyclerView.Adapter<ReminderCallAdapter.MyViewHolder> {
    private List<MReminderNumber> mReminderNumbers;
    private Context context;
    private LayoutInflater inflater;

    public ReminderCallAdapter(Context context) {
        mReminderNumbers = new ArrayList<>();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<MReminderNumber> mAlarms) {
        this.mReminderNumbers = mAlarms;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ReminderCallAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_repeat, parent, false);
        return new ReminderCallAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReminderCallAdapter.MyViewHolder holder, final int position) {
        MReminderNumber mReminderNumber = mReminderNumbers.get(position);
        holder.tvDay.setText(mReminderNumber.getNumber() + "\n" + mReminderNumber.getReminderTime());
        String alarmType = mReminderNumber.getAlarmType() < 1 ? "Single day" :
                mReminderNumber.getAlarmType() == 1 ? "Every day" : "Weekly";
        holder.tvAlarmType.setText("Alarm type\n" + alarmType);
        holder.tvDay.setTextColor(Color.WHITE);
//        holder.relDay.setBackgroundResource(R.drawable.circle_unselect);

        holder.relDay.setOnClickListener(view -> onClickItem(position, view));
    }

    @Override
    public int getItemCount() {
        return mReminderNumbers.size();
    }

    public abstract void onClickItem(int itemPosition, View view);

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay, tvAlarmType;
        LinearLayoutCompat relDay;

        MyViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDayName);
            tvAlarmType = itemView.findViewById(R.id.tvAlarmType);
            relDay = itemView.findViewById(R.id.relDay);
        }
    }
}
