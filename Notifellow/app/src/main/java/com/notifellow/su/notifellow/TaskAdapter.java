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


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//REMOVE COMMENTS AFTER LAYOUT IS DONE


public class TaskAdapter extends ArrayAdapter<Task>{

    Dialog taskInfoDialog;
    TextView titleTextView, startsTextView, endsTextView, remindsTextView, locationTextView, wifiTextView, noteTextView;


    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long milliesRest = diffInMillies;
        for ( TimeUnit unit : units ) {
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit,diff);
        }
        return result;
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }

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

        DateFormat format = new SimpleDateFormat("dd - MM - yyyy HH:mm");
        Date startDate = new Date();
        try {
            startDate = format.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currentDate = new Date();

        Map<TimeUnit,Long> dayDifference = computeDiff(currentDate, startDate);
        long days = dayDifference.get(TimeUnit.DAYS);

        if(days < 7){
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            holder.startTextView.setText(time + "\t\t" + getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
        }
        else{
            holder.startTextView.setText(time + "\t\t" + date);
        }




        String endTime = getItem(position).getEndTime();
        splitted = endTime.split("\t\t");
        date = splitted[1];
        time = splitted[0];

        Date endDate = new Date();
        try {
            endDate = format.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dayDifference = computeDiff(currentDate, endDate);
        days = dayDifference.get(TimeUnit.DAYS);

        if(days < 7){
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            holder.endTextView.setText(time + "\t\t" + getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
        }
        else{
            holder.endTextView.setText(time + "\t\t" + date);
        }



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
