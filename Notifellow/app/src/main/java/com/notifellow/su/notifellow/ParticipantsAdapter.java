package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


public class ParticipantsAdapter extends ArrayAdapter<String>{
    private Context context;

    public ParticipantsAdapter(Context context, List<String> participantsList){
        super(context, R.layout.row_comment, participantsList);
        this.context = context;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent){
        View rowView = convertView;
        ParticipantsAdapter.MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_feed_participants, null);
        }

        if(rowView.getTag() == null){
            holder = new ParticipantsAdapter.MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (ParticipantsAdapter.MyViewHolder) rowView.getTag();
        }

        //SETTEXT OF COMPONENTS IN THIS PART

        holder.profilePicture.setImageURI(null);
        holder.userName.setText(getItem(position));

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS
        ImageView profilePicture;
        TextView userName;

        public MyViewHolder(View itemView) {
            super(itemView);

            //FIND COMPONENTS FROM LAYOUT

            profilePicture = itemView.findViewById(R.id.userPicPar);
            userName = itemView.findViewById(R.id.usernamePar);
        }
    }
}
