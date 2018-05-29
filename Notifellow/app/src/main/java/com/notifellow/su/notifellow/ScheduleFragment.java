package com.notifellow.su.notifellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class ScheduleFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView taskListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    static List<Task> taskList;
    static TaskAdapter taskAdapter;
    private SharedPreferences shared;

    public void getJoinedEvents(){
        shared = getActivity().getSharedPreferences("shared", MODE_PRIVATE);
        final String email = shared.getString("email", null);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getJoinedEvents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String taskOwner = jsonObject.getString("postOwner");
                                String taskOwnerUsername = jsonObject.getString("postOwnerUsername");
                                String globalTaskID = jsonObject.getString("orijinalId");
                                String taskTitle = jsonObject.getString("title");
                                String taskStartTime = jsonObject.getString("startTime");
                                String taskEndTime = jsonObject.getString("endTime");

                                String[] startTimeDate = taskStartTime.split("\t\t\t");
                                String[] endTimeDate = taskEndTime.split("\t\t\t");

                                Task task = new Task(taskOwner, taskOwnerUsername, globalTaskID, taskTitle, startTimeDate[0] + "\t\t" + startTimeDate[1], endTimeDate[0] + "\t\t" + endTimeDate[1]);
                                taskList.add(task);
                            }
                            Collections.sort(taskList);
                            taskAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Snackbar snackbar = Snackbar
                                .make(getView(), "Internet Connection Error!", Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorGray));
                        snackbar.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserID", email); //Add the data you'd like to send to the server.

                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    public static String formatDate(String date) {
        String[] dateSplit = date.split("-");
        String year = dateSplit[0];
        String month = dateSplit[1];
        String day = dateSplit[2];
        return day + " - " + month + " - " + year;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);



        fab = (FloatingActionButton) view.findViewById(R.id.fabAddReminder);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(ScheduleFragment.this.getActivity(), AddReminder.class);
                    ScheduleFragment.this.startActivity(myIntent);

                }
            });
        }

        taskListView = view.findViewById(R.id.listView_Reminder);
        int rowCount = 0;
        taskList = new ArrayList<>();

        Cursor allTasks = Main.schema.getAllRowsForEvents();
        int idCol = allTasks.getColumnIndex("ID");
        int titleCol = allTasks.getColumnIndex("title");
        int startTimeCol = allTasks.getColumnIndex("start_time");
        int startDateCol = allTasks.getColumnIndex("start_date");
        int endTimeCol = allTasks.getColumnIndex("end_time");
        int endDateCol = allTasks.getColumnIndex("end_date");
        int remindTimeCol = allTasks.getColumnIndex("remind_time");
        int remindDateCol = allTasks.getColumnIndex("remind_date");
        int locationCol = allTasks.getColumnIndex("location");
        int wifiCol = allTasks.getColumnIndex("wifiname");
        int noteCol = allTasks.getColumnIndex("notes");
        int emailCol = allTasks.getColumnIndex("email");
        int globalCol = allTasks.getColumnIndex("global");

        allTasks.moveToFirst();
        rowCount = allTasks.getCount();
        int tempCount = rowCount;

        shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
        String email = shared.getString("email", "null");//GET EMAIL FROM SHARED

        if (allTasks != null && (tempCount > 0)) {

            do {
                if(email.equals(allTasks.getString(emailCol))) {
                    String id = allTasks.getString(idCol);
                    String title = allTasks.getString(titleCol);
                    String startTime = allTasks.getString(startTimeCol);
                    String startDate = allTasks.getString(startDateCol);
                    String endTime = allTasks.getString(endTimeCol);
                    String endDate = allTasks.getString(endDateCol);
                    String remindTime = allTasks.getString(remindTimeCol);
                    String remindDate = allTasks.getString(remindDateCol);
                    String location = allTasks.getString(locationCol);
                    String wifi = allTasks.getString(wifiCol);
                    String note = allTasks.getString(noteCol);
                    String global = allTasks.getString(globalCol);

                    endDate = formatDate(endDate);
                    remindDate = formatDate(remindDate);

                    taskList.add(new Task(id, title, startDate + "\t\t" + startTime, endTime + "\t\t" + endDate, remindTime + "\t\t" + remindDate, location, wifi, note, global));
                }
                tempCount--;
            }
            while (allTasks.moveToNext());
        }

        Collections.sort(taskList);

        taskAdapter = new TaskAdapter(getActivity(), taskList);
        taskListView.setAdapter(taskAdapter);

        getJoinedEvents();

        //// SWIPE TO REFRESH ////
        swipeRefreshLayout = view.findViewById(R.id.remindersLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_3,
                R.color.refresh_progress_3,
                R.color.refresh_progress_3); //CHANGE COLOR SCHEME

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                taskAdapter.notifyDataSetChanged(); // REFRESH THE LIST (I GUESS :P)
                swipeRefreshLayout.setRefreshing(false); // STOP ANIMATION
            }
        });
        //// END OF SWIPE TO REFRESH ///
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        //inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


}
