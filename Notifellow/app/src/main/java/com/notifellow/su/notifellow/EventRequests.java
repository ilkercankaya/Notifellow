package com.notifellow.su.notifellow;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.Map;

public class EventRequests extends AppCompatActivity {

    static EventRequestAdapter eventRequestAdapter;
    static ArrayList<EventRequest> eventRequestList = new ArrayList<EventRequest>();

    private RequestQueue MyRequestQueue;
    private SharedPreferences shared;

    ListView RequestList;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayout;

    public void getEventRequests(){
        shared = getSharedPreferences("shared", MODE_PRIVATE);
        final String userEmail = shared.getString("email", null);

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getEventReq",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        eventRequestAdapter.clear();
                        try {
                            eventRequestList.clear(); //Clear for new results
                            JSONArray jsonARR = new JSONArray(response);
                            for (int i = 0; i < jsonARR.length(); i++) {
                                JSONObject oneUser = jsonARR.getJSONObject(i);
                                String email = oneUser.getString("reqEmail");
                                String title = oneUser.getString("title");
                                String id = oneUser.getString("id");
                                String startDate = oneUser.getString("startDate");
                                String endDate = oneUser.getString("endDate");
                                String username = oneUser.getString("username");

                                EventRequest eventRequestObject = new EventRequest(username, email, null, title, id, startDate, endDate);
                                eventRequestList.add(eventRequestObject);
                            }
                            eventRequestAdapter.notifyDataSetChanged(); // REFRESH THE LIST (I GUESS :P)
                            swipeRefreshLayout.setRefreshing(false); // STOP ANIMATION
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar
                                .make(parentLayout, "Internet Connection Error!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGray));
                        swipeRefreshLayout.setRefreshing(false); // STOP ANIMATION
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserID", userEmail); //Add the data you'd like to send to the server.
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);


        RequestList = findViewById(R.id.listView_EventRequest);
        swipeRefreshLayout = findViewById(R.id.JoinRequestLayout);
        relativeLayout = findViewById(R.id.eventRequestLay);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Event Requests");

        MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        eventRequestAdapter = new EventRequestAdapter(this, eventRequestList);
        RequestList.setAdapter(eventRequestAdapter);
        getEventRequests();

        //// SWIPE TO REFRESH ////
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_3,
                R.color.refresh_progress_3,
                R.color.refresh_progress_3); //CHANGE COLOR SCHEME

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        //// END OF SWIPE TO REFRESH ///
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
