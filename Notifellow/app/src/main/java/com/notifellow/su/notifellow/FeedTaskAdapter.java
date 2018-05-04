package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import java.util.List;

public class FeedTaskAdapter extends ArrayAdapter<FeedTask> {

    public FeedTaskAdapter(Context context, List<FeedTask> feedTaskList){
        super(context, LAYOUT, taskList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        FeedTaskAdapter.MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(LAYOUT, null);
        }

        if(rowView.getTag() == null){
            holder = new FeedTaskAdapter.MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (FeedTaskAdapter.MyViewHolder) rowView.getTag();
        }

        //SETTEXT OF COMPONENTS IN THIS PART

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS

        public MyViewHolder(View itemView) {
            super(itemView);

            //FIND COMPONENTS FROM LAYOUT
        }
    }
}
