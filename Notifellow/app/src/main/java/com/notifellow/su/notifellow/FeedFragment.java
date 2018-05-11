package com.notifellow.su.notifellow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FeedFragment extends Fragment {

    private SharedPreferences shared;

    static FeedTaskAdapter feedTaskAdapter;
    static ArrayList<FeedTask> feedTaskList = new ArrayList<FeedTask>();
    SwipeRefreshLayout swipeRefreshLayout;

    private RequestQueue MyRequestQueue;

    ListView feedsListView;

    public void fillFeed(String response){
        try {
            feedTaskList.clear(); //Clear for new results
            JSONArray jsonARR = new JSONArray(response);
            for (int i = 0; i < jsonARR.length(); i++) {
                JSONObject oneUser = jsonARR.getJSONObject(i);

                String startTime = oneUser.getString("startTime");

                String[] splitted = startTime.split("\t\t\t");
                String date = splitted[0];
                String time = splitted[1];

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date startDate = new Date();
                try {
                    startDate = format.parse(date + " " + time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date currentDate = format.parse(format.format(new Date()));

                Map<TimeUnit,Long> dayDifference = TaskAdapter.computeDiff(currentDate, startDate);
                long days = dayDifference.get(TimeUnit.DAYS);
                long minutes = dayDifference.get(TimeUnit.MINUTES);

                if(days > 0 && minutes > 0){
                    String email = oneUser.getString("email");
                    String username = oneUser.getString("username");
                    String id = oneUser.getString("id");
                    String title = oneUser.getString("title");
                    String location = oneUser.getString("location");
                    String endTime = oneUser.getString("endTime");
                    String remindTime = oneUser.getString("remindAt");
                    //String eventJoiners = oneUser.getString("eventJoiners");
                    //String comments = oneUser.getString("comments");

                    Task task = new Task(id, title, startTime, endTime, remindTime, location);
                    FeedTask feedTask = new FeedTask(task, username, email, null, "", "");
                    feedTaskList.add(feedTask);
                }
            }
            //SORT
            Collections.sort(feedTaskList);
            feedTaskAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);



        shared = getActivity().getSharedPreferences("shared", MODE_PRIVATE);
        MyRequestQueue = Volley.newRequestQueue(getActivity());

        feedsListView = view.findViewById(R.id.listView_Feed);

        feedTaskAdapter = new FeedTaskAdapter(getContext(), feedTaskList);
        feedsListView.setAdapter(feedTaskAdapter);

        final String email = shared.getString("email", null);
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getFeed",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                       fillFeed(response);
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
                params.put("emailGiven", email); //Add the data you'd like to send to the server.

                return params;
            }
        };
        MyRequestQueue.add(postRequest);


        //// SWIPE TO REFRESH ////
        swipeRefreshLayout = view.findViewById(R.id.feedLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_3, R.color.refresh_progress_3, R.color.refresh_progress_3); //CHANGE COLOR SCHEME

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedTaskAdapter.notifyDataSetChanged(); // REFRESH THE LIST (I GUESS :P)
                swipeRefreshLayout.setRefreshing(false); // STOP ANIMATION
            }
        });
        //// END OF SWIPE TO REFRESH ///


        return view;
    }

}
