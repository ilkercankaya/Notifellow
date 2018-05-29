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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

/**
 * Created by egealpay on 29.05.2018.
 */

public class EventRequestAdapter extends ArrayAdapter<EventRequest> {
    private Context context;
    private RequestQueue MyRequestQueue;

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

    public void acceptEventRequest(final int position, final String UserID, final String AddedID, final String eventID, final String eventName){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/joinEventAccept",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(context, "You have accepted the request!", Toast.LENGTH_SHORT).show();
                        EventRequests.eventRequestList.remove(position);
                        EventRequests.eventRequestAdapter.notifyDataSetChanged();
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
                params.put("AddedID", AddedID);
                params.put("UserID", UserID); //User of this app
                params.put("eventID", eventID);
                params.put("eventName", eventName);
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    public void rejectEventRequest(final int position, final String UserID, final String deletedID, final String eventID){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/leaveEvent",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        if(response.equals("DELETED 201")){
                            Toast.makeText(context, "You have rejected the request!", Toast.LENGTH_SHORT).show();
                            EventRequests.eventRequestList.remove(position);
                            EventRequests.eventRequestAdapter.notifyDataSetChanged();
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
                params.put("deletedID", UserID); //Add the data you'd like to send to the server.
                params.put("UserID", deletedID);
                params.put("eventID", eventID);
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    public EventRequestAdapter(Context context, List<EventRequest> eventRequestList){
        super(context, R.layout.event_join_layout, eventRequestList);
        this.context = context;
        MyRequestQueue = Volley.newRequestQueue(getContext());
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent){
        View rowView = convertView;
        EventRequestAdapter.MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.event_join_layout, null);
        }

        if(rowView.getTag() == null){
            holder = new EventRequestAdapter.MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (EventRequestAdapter.MyViewHolder) rowView.getTag();
        }

        //SETTEXT OF COMPONENTS IN THIS PART
        //holder.profilePicture.setImageURI(null);
        holder.userName.setText(getItem(position).getUserName());
        holder.eventTitle.setText(getItem(position).getEventTitle());
        //holder.startDate.setText(getItem(position).getStartDate());
        //holder.endDate.setText(getItem(position).getEndDate());

        String startTime = getItem(position).getStartDate();
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

        Map<TimeUnit,Long> dayDifference = computeDiff(currentDate, startDate);
        long days = dayDifference.get(TimeUnit.DAYS);

        if(days < 7){
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            holder.startDate.setText(time + "\t\t" + getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
        }
        else{
            holder.startDate.setText(time + "\t\t" + date);
        }


        String endTime = getItem(position).getEndDate();
        splitted = endTime.split("\t\t\t");
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
            holder.endDate.setText(time + "\t\t" + getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)));
        }
        else{
            holder.endDate.setText(time + "\t\t" + date);
        }

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                String addedID = shared.getString("email", null); //User of this application
                String userID = getItem(position).getEmail();
                String eventID = getItem(position).getEventID();
                String eventName = getItem(position).getEventTitle();
                acceptEventRequest(position, userID, addedID, eventID, eventName);
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                String UserID = shared.getString("email", null);
                String deletedID = getItem(position).getEmail();
                String eventID = getItem(position).getEventID();
                rejectEventRequest(position, UserID, deletedID, eventID);
            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS
        ImageView profilePicture, accept, reject;
        TextView userName, eventTitle, startDate, endDate;

        public MyViewHolder(View itemView) {
            super(itemView);

            //FIND COMPONENTS FROM LAYOUT

            profilePicture = itemView.findViewById(R.id.joinProfilePic);
            accept = itemView.findViewById(R.id.joinAcceptBtn);
            reject = itemView.findViewById(R.id.joinDeclineBtn);
            userName = itemView.findViewById(R.id.joinUsernameTxt);
            eventTitle = itemView.findViewById(R.id.joinTitleTxt);
            startDate = itemView.findViewById(R.id.joinDateStartsAtTxt);
            endDate = itemView.findViewById(R.id.joinDateEndsAtTxt);
        }
    }
}
