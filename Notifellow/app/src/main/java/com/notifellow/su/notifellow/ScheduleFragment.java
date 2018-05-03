package com.notifellow.su.notifellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ScheduleFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView taskListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    static List<Task> taskList;
    static TaskAdapter taskAdapter;
    private SharedPreferences shared;


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

                    Toast.makeText(getActivity(), "Add Reminder!", Toast.LENGTH_LONG).show();
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

                    endDate = formatDate(endDate);
                    remindDate = formatDate(remindDate);

                    taskList.add(new Task(id, title, startDate + "\t\t" + startTime, endTime + "\t\t" + endDate, remindTime + "\t\t" + remindDate, location, wifi, note));
                }
                tempCount--;
            }
            while (allTasks.moveToNext());
        }

        Collections.sort(taskList);

        taskAdapter = new TaskAdapter(getActivity(), taskList);
        taskListView.setAdapter(taskAdapter);

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
