package com.notifellow.su.notifellow;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class Comments extends AppCompatActivity {

    private ImageView profilePic;
    private ImageView sendComment;
    private EditText commentTxt;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profilePic = findViewById(R.id.writeCommentProfilePic);
        sendComment = findViewById(R.id.writeCommentSend);
        commentTxt = findViewById(R.id.writeCommentTxt);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Comments.this,"Comment Sent.",
                        Toast.LENGTH_SHORT).show();
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
