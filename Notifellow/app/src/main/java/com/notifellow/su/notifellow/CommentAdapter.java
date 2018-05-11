package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * Created by egealpay on 11.05.2018.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;

    public CommentAdapter(Context context, List<Comment> commentList){
        super(context, R.layout.row_comment, commentList);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        CommentAdapter.MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_comment, null);
        }

        if(rowView.getTag() == null){
            holder = new CommentAdapter.MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (CommentAdapter.MyViewHolder) rowView.getTag();
        }

        //SETTEXT OF COMPONENTS IN THIS PART

        holder.profilePicture.setImageURI(getItem(position).getProfilePicture());
        holder.userName.setText(getItem(position).getUserName());
        holder.comment.setText(getItem(position).getComment());

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS
        ImageView profilePicture;
        TextView userName, comment;

        public MyViewHolder(View itemView) {
            super(itemView);

            //FIND COMPONENTS FROM LAYOUT

            profilePicture = itemView.findViewById(R.id.rowCommentProfilePicture);
            userName = itemView.findViewById(R.id.rowCommentUsername);
            comment = itemView.findViewById(R.id.rowCommentCommentTxt);
        }
    }
}
