package com.notifellow.su.notifellow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

//REMOVE COMMENTS AFTER LAYOUT IS DONE


public class TaskAdapter extends ArrayAdapter<Task>{

    Dialog taskInfoDialog;
    TextView titleTextView, startsTextView, endsTextView, remindsTextView, locationTextView, wifiTextView, noteTextView;

    public TaskAdapter(Context context, List<Task> taskList){
        super(context, R.layout.schedule_row, taskList);
    }

    public void rowOnClick(Task task){
        taskInfoDialog = new Dialog(getContext());
        taskInfoDialog.setContentView(R.layout.schedule_row_clicked);
        taskInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES
        titleTextView = taskInfoDialog.findViewById(R.id.reminderTitle_clicked);
        startsTextView = taskInfoDialog.findViewById(R.id.startDateTxt_clicked);
        endsTextView = taskInfoDialog.findViewById(R.id.endDateTxt_clicked);
        remindsTextView = taskInfoDialog.findViewById(R.id.RemindMeAtTxt_clicked);
        locationTextView = taskInfoDialog.findViewById(R.id.locNameTxt);
        wifiTextView = taskInfoDialog.findViewById(R.id.wifiNameTxt);
        noteTextView = taskInfoDialog.findViewById(R.id.noteClicked);

        titleTextView.setText(task.getTitle());

        String startTime = task.getStartTime();
        String[] splitted = startTime.split("\t\t");
        String date = splitted[0];
        String time = splitted[1];
        date = ScheduleFragment.formatDate(date);
        startsTextView.setText(time + "\t\t" + date);

        endsTextView.setText(task.getEndTime());
        remindsTextView.setText(task.getRemindTime());

        if(task.getLocation().equals("default")){
            locationTextView.setVisibility(View.GONE);
            TextView locName = taskInfoDialog.findViewById(R.id.locName);
            locName.setVisibility(View.GONE);
            ImageView locImage = taskInfoDialog.findViewById(R.id.imgLoc_clicked);
            locImage.setVisibility(View.GONE);
        }
        else {
            locationTextView.setText(task.getLocation());
        }

        if(task.getWifi().equals("")){
            wifiTextView.setVisibility(View.GONE);
            TextView wifiName = taskInfoDialog.findViewById(R.id.wifiName);
            wifiName.setVisibility(View.GONE);
            ImageView imgWifi = taskInfoDialog.findViewById(R.id.imgWifi);
            imgWifi.setVisibility(View.GONE);
        }
        else{
            wifiTextView.setText(task.getWifi());
        }

        noteTextView.setText(task.getNote());

        taskInfoDialog.show();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.schedule_row, null);
        }

        if(rowView.getTag() == null){
            holder = new MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (MyViewHolder) rowView.getTag();
        }


        holder.titleTextView.setText(getItem(position).getTitle());

        String startTime = getItem(position).getStartTime();
        String[] splitted = startTime.split("\t\t");
        String date = splitted[0];
        String time = splitted[1];
        date = ScheduleFragment.formatDate(date);
        holder.startTextView.setText(time + "\t\t" + date);

        holder.endTextView.setText(getItem(position).getEndTime());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowOnClick(getItem(position));
            }
        });

        holder.cancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.cancelAlarm(Integer.parseInt(getItem(position).getID()) + 1);
                ScheduleFragment.taskList.remove(position);
                ScheduleFragment.taskAdapter.notifyDataSetChanged();
            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView startTextView;
        private TextView endTextView;
        private ImageView cancelTask;

        public MyViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.row_RemTitle);
            startTextView = itemView.findViewById(R.id.taskStartTime);
            endTextView = itemView.findViewById(R.id.taskEndTime);
            cancelTask = itemView.findViewById(R.id.cancelTaskButton);
        }
    }
}
