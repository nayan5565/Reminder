package com.shadhinlab.reminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.models.MTime;

import java.util.ArrayList;
import java.util.List;


public abstract class RepeatAlarmAdapter extends RecyclerView.Adapter<RepeatAlarmAdapter.MyViewHolder> {
    private List<MTime> mAlarms;
    private Context context;
    private LayoutInflater inflater;

    public RepeatAlarmAdapter(Context context) {
        mAlarms = new ArrayList<>();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<MTime> mAlarms) {
        this.mAlarms = mAlarms;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RepeatAlarmAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_repeat, parent, false);
        return new RepeatAlarmAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RepeatAlarmAdapter.MyViewHolder holder, final int position) {
        MTime mAlarm = mAlarms.get(position);
        holder.tvDay.setText(mAlarm.getAsr());
//            holder.tvDay.setTextColor(Color.BLACK);
        holder.relDay.setBackgroundResource(R.drawable.circle_unselect);

        holder.relDay.setOnClickListener(view -> onClickItem(position, view));
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
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
