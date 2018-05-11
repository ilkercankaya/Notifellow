package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FeedTaskAdapter extends ArrayAdapter<FeedTask> {

    private Context context;


    public FeedTaskAdapter(Context context, List<FeedTask> feedTaskList){
        super(context, R.layout.row_feed, feedTaskList);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        FeedTaskAdapter.MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_feed, null);
        }

        if(rowView.getTag() == null){
            holder = new FeedTaskAdapter.MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (FeedTaskAdapter.MyViewHolder) rowView.getTag();
        }

        //SETTEXT OF COMPONENTS IN THIS PART

        holder.profilePicture.setImageURI(getItem(position).getProfilePic()); //MAY PRODUCE ERROR
        holder.userName.setText(getItem(position).getUserName());
        holder.title.setText(getItem(position).getTask().getTitle());


        String startTime = getItem(position).getTask().getStartTime();
        String[] splitted = startTime.split("\t\t\t");
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

        Map<TimeUnit,Long> dayDifference = TaskAdapter.computeDiff(currentDate, startDate);
        long days = dayDifference.get(TimeUnit.DAYS);

        if(days < 7){
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            holder.startDate.setText(time + "\t\t" + TaskAdapter.getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
        }
        else{
            holder.startDate.setText(time + "\t\t" + date);
        }


        String endTime = getItem(position).getTask().getEndTime();
        splitted = endTime.split("\t\t\t");
        date = splitted[1];
        time = splitted[0];

        Date endDate = new Date();
        try {
            endDate = format.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dayDifference = TaskAdapter.computeDiff(currentDate, endDate);
        days = dayDifference.get(TimeUnit.DAYS);

        if(days < 7){
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            holder.endDate.setText(time + "\t\t" + TaskAdapter.getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
        }
        else{
            holder.endDate.setText(time + "\t\t" + date);
        }


        if(getItem(position).getTask().getLocation().equals("default")){
            holder.location.setVisibility(View.GONE);
            ImageView feedLocImg = rowView.findViewById(R.id.feedLocationImg);
            feedLocImg.setVisibility(View.GONE);
        }
        else {
            holder.location.setText(getItem(position).getTask().getLocation());
        }


        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, Comments.class);
                intent.putExtra("taskEmail", getItem(position).getEmail());
                intent.putExtra("taskID", getItem(position).getTask().getID());
                context.startActivity(intent);

            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS
        ImageView profilePicture;
        TextView userName, title, startDate, endDate, location;
        ImageView join, participants, comment;

        public MyViewHolder(View itemView) {
            super(itemView);

            //FIND COMPONENTS FROM LAYOUT

            profilePicture = itemView.findViewById(R.id.feedProfilePic);
            userName = itemView.findViewById(R.id.feedUsernameTxt);
            title = itemView.findViewById(R.id.feedTitleTxt);
            startDate = itemView.findViewById(R.id.feedDateStartsAtTxt);
            endDate = itemView.findViewById(R.id.feedDateEndsAtTxt);
            location = itemView.findViewById(R.id.feedLocationTxt);
            join = itemView.findViewById(R.id.feedJoinBtn);
            participants = itemView.findViewById(R.id.feedParticipantsBtn);
            comment = itemView.findViewById(R.id.feedCommentBtn);
        }
    }
}
