package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class FeedTaskAdapter extends ArrayAdapter<FeedTask> {

    public FeedTaskAdapter(Context context, List<FeedTask> feedTaskList){
        super(context, R.layout.row_feed, feedTaskList);
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
        holder.startDate.setText(getItem(position).getTask().getStartTime());
        holder.endDate.setText(getItem(position).getTask().getEndTime());
        holder.location.setText(getItem(position).getTask().getLocation());

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

            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS
        ImageView profilePicture;
        TextView userName, title, startDate, endDate, location;
        ImageButton join, participants, comment;

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
