package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by egealpay on 11.05.2018.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;

    private RequestQueue MyRequestQueue;

    public CommentAdapter(Context context, List<Comment> commentList){
        super(context, R.layout.row_comment, commentList);
        this.context = context;
        MyRequestQueue = Volley.newRequestQueue(getContext());
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent){
        View rowView = convertView;
        CommentAdapter.MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_comment, null);
        }

        if(rowView.getTag() == null){
            holder = new CommentAdapter.MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (CommentAdapter.MyViewHolder) rowView.getTag();
        }

        //SETTEXT OF COMPONENTS IN THIS PART

        holder.profilePicture.setImageURI(getItem(position).getProfilePicture());
        holder.userName.setText(getItem(position).getUserName());
        holder.comment.setText(getItem(position).getComment());

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                String userEmail = getItem(position).getEmail();
                SharedPreferences shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
                final String currentEmail = shared.getString("email", "null");//GET EMAIL FROM SHARED

                if(currentEmail.equals(userEmail)){
                    final String ownerEmail = shared.getString("taskOwnerEmail", null);
                    final String ownerID = shared.getString("taskOwnerID", null);

                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteComment",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    if (response.equals("DELETED 201")) {
                                        Comments.commentList.remove(position);
                                        Comments.commentAdapter.notifyDataSetChanged();
                                        Toast.makeText(context, "Comment Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    View parentLayout = convertView.findViewById(android.R.id.content);
                                    Snackbar snackbar = Snackbar
                                            .make(parentLayout, "Internet Connection Error!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorGray));
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("postOwner", ownerEmail); //Add the data you'd like to send to the server.
                            params.put("deleteEmail", currentEmail); //Add the data you'd like to send to the server.
                            params.put("alarmID", ownerID);
                            return params;
                        }
                    };
                    MyRequestQueue.add(postRequest);
                }
                return false;
            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //LAYOUT COMPONENTS
        ImageView profilePicture;
        TextView userName, comment;

        public MyViewHolder(View itemView) {
            super(itemView);

            //FIND COMPONENTS FROM LAYOUT

            profilePicture = itemView.findViewById(R.id.rowCommentProfilePicture);
            userName = itemView.findViewById(R.id.rowCommentUsername);
            comment = itemView.findViewById(R.id.rowCommentCommentTxt);
        }
    }
}
