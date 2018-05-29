package com.notifellow.su.notifellow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

//REMOVE COMMENTS AFTER LAYOUT IS DONE


public class TaskAdapter extends ArrayAdapter<Task>{

    Dialog taskInfoDialog;
    TextView titleTextView, startsTextView, endsTextView, remindsTextView, locationTextView, wifiTextView, noteTextView;

    ArrayList<String> usernameList;

    public void getParticipants(final String postOwner, final String taskID){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getGroups",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            usernameList.clear();
                            JSONArray rootJson = new JSONArray(response);
                            for (int i = 0; i < rootJson.length(); i++){
                                JSONObject participantJSON = rootJson.getJSONObject(i);
                                String username = participantJSON.getString("username");
                                usernameList.add(username);
                            }
                            final Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.feed_participants_dialog);
                            ImageView closeDialog = dialog.findViewById(R.id.closeParticipants);
                            closeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            ListView participantsListView = dialog.findViewById(R.id.listParticipants);
                            ParticipantsAdapter participantsAdapter = new ParticipantsAdapter(getContext(), usernameList);
                            participantsListView.setAdapter(participantsAdapter);

                            int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels); // fills the screen in terms of width
                            int height = (int)(getContext().getResources().getDisplayMetrics().heightPixels*0.90); //fills the 90% of screen in terms of height
                            dialog.getWindow().setLayout(width, height); // set the layout width and height
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES

                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Snackbar snackbar = Snackbar
                        //.make(getView(), "Internet Connection Error!", Snackbar.LENGTH_LONG);
                        //snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorGray));
                        //snackbar.show();
                        Toast.makeText(getContext(), "Internet Connection Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("postOwner", postOwner); //Add the data you'd like to send to the server.
                params.put("taskID", taskID);
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

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

    public static String getDayOfWeek(int value) {
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

        DateFormat formatToDate = new SimpleDateFormat("dd - MM - yyyy HH:mm");
        Date startDate = new Date();
        try {
            startDate = formatToDate.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatToString = new SimpleDateFormat("EEEE MMMM dd, HH:mm, yyyy");
        String startDateInString = formatToString.format(startDate);
        startsTextView.setText(startDateInString);



        String endTime = task.getEndTime();
        splitted = endTime.split("\t\t");
        date = splitted[1];
        time = splitted[0];

        Date endDate = new Date();
        try {
            endDate = formatToDate.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String endDateInString = formatToString.format(endDate);
        endsTextView.setText(endDateInString);



        String remindTime = task.getRemindTime();
        splitted = remindTime.split("\t\t");
        date = splitted[1];
        time = splitted[0];

        Date remindDate = new Date();
        try {
            remindDate = formatToDate.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String remindDateInString = formatToString.format(remindDate);
        remindsTextView.setText(remindDateInString);


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

        int width = (int)(getContext().getResources().getDisplayMetrics().widthPixels); // fills the screen in terms of width
        int height = (int)(getContext().getResources().getDisplayMetrics().heightPixels*0.90); //fills the 90% of screen in terms of height
        taskInfoDialog.getWindow().setLayout(width, height); // set the layout width and height
        
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

        usernameList = new ArrayList<String>();

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getItem(position).getGlobal().equals("2"))
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

        holder.participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getGlobal().equals("2")) {
                    String email = getItem(position).getTaskOwnerEmail();
                    String taskID = getItem(position).getTaskGlobalID();
                    getParticipants(email, taskID);
                }
                else{
                    SharedPreferences shared;
                    shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                    final String email = shared.getString("email", null);

                    getParticipants(email, getItem(position).getID());
                }
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItem(position).getGlobal().equals("2")){
                    Intent intent = new Intent(getContext(), Comments.class);
                    intent.putExtra("taskEmail", getItem(position).getTaskOwnerEmail());
                    intent.putExtra("taskID", getItem(position).getTaskGlobalID());
                    getContext().startActivity(intent);
                }
                else{
                    SharedPreferences shared;
                    shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                    final String email = shared.getString("email", null);

                    Intent intent = new Intent(getContext(), Comments.class);
                    intent.putExtra("taskEmail", email);
                    int taskID = Integer.parseInt(getItem(position).getID()) + 1;
                    intent.putExtra("taskID", String.valueOf(taskID));
                    getContext().startActivity(intent);
                }
            }
        });

        if(getItem(position).getGlobal().equals("0")){
            holder.participants.setVisibility(View.GONE);
            holder.comments.setVisibility(View.GONE);
        }
        else{
            holder.participants.setVisibility(View.VISIBLE);
            holder.comments.setVisibility(View.VISIBLE);
        }

        if(getItem(position).getGlobal().equals("2")){
            holder.cancelTask.setVisibility(View.GONE);
        }
        else{
            holder.cancelTask.setVisibility(View.VISIBLE);
        }

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView startTextView;
        private TextView endTextView;
        private ImageView cancelTask;
        private ImageView participants;
        private ImageView comments;


        public MyViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.row_RemTitle);
            startTextView = itemView.findViewById(R.id.taskStartTime);
            endTextView = itemView.findViewById(R.id.taskEndTime);
            cancelTask = itemView.findViewById(R.id.cancelTaskButton);
            participants = itemView.findViewById(R.id.scheduleParticipantsBtn);
            comments = itemView.findViewById(R.id.scheduleCommentBtn);
        }
    }
}
