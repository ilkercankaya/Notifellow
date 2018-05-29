package com.notifellow.su.notifellow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FeedTaskAdapter extends ArrayAdapter<FeedTask> {

    private Context context;

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
                            Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.feed_participants_dialog);
                            ListView participantsListView = dialog.findViewById(R.id.listParticipants);
                            ParticipantsAdapter participantsAdapter = new ParticipantsAdapter(getContext(), usernameList);
                            participantsListView.setAdapter(participantsAdapter);
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
                        Toast.makeText(context, "Internet Connection Error!", Toast.LENGTH_SHORT).show();
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

    public void leaveEvent(final int position, final String deletedID, final String UserID, final String eventID, final MyViewHolder holder){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/leaveEvent",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response.equals("DELETED 201")){
                            Toast.makeText(context, "You have left the Event!", Toast.LENGTH_SHORT).show();
                            getItem(position).getTask().setHasJoined("0");
                            holder.join.setImageResource(R.drawable.ic_join_event);
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
                        Toast.makeText(context, "Internet Connection Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deletedID", deletedID); //Add the data you'd like to send to the server.
                params.put("UserID", UserID);
                params.put("eventID", eventID);
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    public void joinEvent(final int position, final String UserID, final String AddedID, final String eventID, final String eventName, final MyViewHolder holder){

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/joinEventReq",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response.equals("CREATED 201")){
                            Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show();
                            getItem(position).getTask().setHasJoined("1");
                            holder.join.setImageResource(R.drawable.ic_cancel_join);
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
                        Toast.makeText(context, "Internet Connection Error!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserID", UserID); //Add the data you'd like to send to the server.
                params.put("AddedID", AddedID);
                params.put("eventID", eventID);
                params.put("eventName", eventName);

                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    public FeedTaskAdapter(Context context, List<FeedTask> feedTaskList){
        super(context, R.layout.row_feed, feedTaskList);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        final FeedTaskAdapter.MyViewHolder holder;
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

        usernameList = new ArrayList<String>();

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


        if(getItem(position).getTask().getHasJoined().equals("0")){
            holder.join.setImageResource(R.drawable.ic_join_event);
        }
        else{
            holder.join.setImageResource(R.drawable.ic_cancel_join);
        }

        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getTask().getHasJoined().equals("0")) {
                    SharedPreferences shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                    String UserID = shared.getString("email", null);
                    String AddedID = getItem(position).getEmail();
                    String eventID = getItem(position).getTask().getID();
                    String eventName = getItem(position).getTask().getTitle();
                    joinEvent(position, UserID, AddedID, eventID, eventName, holder);
                }
                else{
                    SharedPreferences shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                    String UserID = shared.getString("email", null);
                    String deletedID = getItem(position).getEmail();
                    String eventID = getItem(position).getTask().getID();
                    leaveEvent(position, deletedID, UserID, eventID, holder);
                }
            }
        });

        holder.participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getItem(position).getEmail();
                String taskID = getItem(position).getTask().getID();
                getParticipants(email, taskID);
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
