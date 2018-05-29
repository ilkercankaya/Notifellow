package com.notifellow.su.notifellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Comments extends AppCompatActivity {
    static CommentAdapter commentAdapter;
    static ArrayList<Comment> commentList = new ArrayList<Comment>();
    ListView commentListView;

    private RequestQueue MyRequestQueue;
    private SharedPreferences shared;

    private ImageView profilePic;
    private ImageView sendComment;
    private EditText commentTxt;
    SwipeRefreshLayout swipeRefreshLayout;

    public void addComment(final String feedTaskEmail, final String taskID){
        final String commentedEmail = shared.getString("email", null);
        final String comment = commentTxt.getText().toString();
        final String username = shared.getString("username", null);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMMM");
        final String currentDateandTime = sdf.format(new Date());

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/addComment", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("CREATED 201")) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Comment added!", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                    snackbar.show();

                    commentList.add(new Comment(commentedEmail, null, username, comment, currentDateandTime));
                    commentAdapter.notifyDataSetChanged();

                    commentTxt.setText("");
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Database Error!", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                    snackbar.show();
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
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbar.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("commentedEmail", commentedEmail); //Add the data you'd like to send to the server.
                params.put("postOwner", feedTaskEmail); //Add the data you'd like to send to the server.
                params.put("alarmID", taskID);
                params.put("comment", comment);
                params.put("givenTime", currentDateandTime);
                return params;
            }
        };
        MyRequestQueue.add(postRequest);
    }

    public void getComments(final String feedTaskEmail, final String taskID){

        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getComment",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        commentAdapter.clear();
                        try {
                            commentList.clear(); //Clear for new results
                            JSONArray jsonARR = new JSONArray(response);
                            for (int i = 0; i < jsonARR.length(); i++) {
                                JSONObject oneUser = jsonARR.getJSONObject(i);
                                String email = oneUser.getString("email");
                                String username = oneUser.getString("username");
                                String comment = oneUser.getString("comment");
                                //String ppDEST = oneUser.getString("ppDest");
                                //String timeCommented = oneUser.getString("timeCommented");

                                Comment commentObject = new Comment(email, null, username, comment, null);
                                commentList.add(commentObject);
                            }
                            commentAdapter.notifyDataSetChanged(); // REFRESH THE LIST (I GUESS :P)
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
                params.put("postOwner", feedTaskEmail); //Add the data you'd like to send to the server.
                params.put("taskID", taskID); //Add the data you'd like to send to the server.
                return params;
            }
        };
        MyRequestQueue.add(postRequest);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profilePic = findViewById(R.id.writeCommentProfilePic);
        sendComment = findViewById(R.id.writeCommentSend);
        commentTxt = findViewById(R.id.writeCommentTxt);

        commentListView = findViewById(R.id.listView_Comments);
        commentAdapter = new CommentAdapter(this, commentList);
        commentListView.setAdapter(commentAdapter);

        shared = getSharedPreferences("shared", MODE_PRIVATE);

        Intent intent = getIntent();

        final String feedTaskEmail = intent.getStringExtra("taskEmail");
        final String taskID = intent.getStringExtra("taskID");

        SharedPreferences.Editor prefEdit = shared.edit();
        prefEdit.putString("taskOwnerEmail", feedTaskEmail);
        prefEdit.putString("taskOwnerID", taskID);
        prefEdit.commit();

        MyRequestQueue = Volley.newRequestQueue(getApplicationContext());

        getComments(feedTaskEmail, taskID);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(feedTaskEmail, taskID);
            }
        });


        //// SWIPE TO REFRESH ////
        swipeRefreshLayout = findViewById(R.id.commentsLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_3, R.color.refresh_progress_3, R.color.refresh_progress_3); //CHANGE COLOR SCHEME

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // adaptercomeshere.notifyDataSetChanged(); // REFRESH THE LIST (I GUESS :P)
                swipeRefreshLayout.setRefreshing(false); // STOP ANIMATION
            }
        });
        //// END OF SWIPE TO REFRESH ///


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
