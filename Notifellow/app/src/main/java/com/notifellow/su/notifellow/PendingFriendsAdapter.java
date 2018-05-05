package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PendingFriendsAdapter extends ArrayAdapter<Friends> {
    private static SharedPreferences shared;
    private static RequestQueue MyRequestQueue;

    public PendingFriendsAdapter(Context context, List<Friends> taskList){
        super(context, R.layout.row_request, taskList);
        MyRequestQueue = Volley.newRequestQueue(context);
        shared = context.getSharedPreferences("shared", MODE_PRIVATE);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View rowView = convertView;
        MyViewHolder holder;
        if(rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_request, null); //SET IT TO SUITABLE LAYOUT
        }

        if(rowView.getTag() == null){
            holder = new MyViewHolder(rowView);
            rowView.setTag(holder);
        }else{
            holder = (MyViewHolder) rowView.getTag();
        }

        holder.usernameTextView.setText(getItem(position).getUserName());
        holder.nameSurnameTextView.setText(getItem(position).getNameSurname());
        holder.profilePictureView.setImageURI(getItem(position).getProfilePicture());

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = shared.getString("email", null);
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/acceptFR", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("UPDATED 201")) {
                            Snackbar snackbar = Snackbar
                                    .make(parent, "Added User " + getItem(position).getUserName()+ "!", Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorGreen));
                            snackbar.show();

                            SocialFriends.friendsList.add(getItem(position));
                            SocialFriends.friendsAdapter.notifyDataSetChanged();

                            SocialRequests.pendingFriendsList.remove(position);
                            SocialRequests.pendingFriendsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Database Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Snackbar snackbar = Snackbar
                                        .make(parent, "Internet Connection Error!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorRed));
                                snackbar.show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("UserID", email); //Add the data you'd like to send to the server.
                        params.put("addedID", getItem(position).getEmail()); //Add the data you'd like to send to the server.

                        return params;
                    }
                };
                MyRequestQueue.add(postRequest);

                Log.i("well", " this worked");
            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = shared.getString("email", null);
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteOrRejectFR", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("DELETED 201")) {
                            Snackbar snackbar = Snackbar
                                    .make(parent, "Deleted User " + getItem(position).getUserName()+ "!", Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorGreen));
                            snackbar.show();
                            SocialRequests.pendingFriendsList.remove(position);
                            SocialRequests.pendingFriendsAdapter.notifyDataSetChanged();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(parent, "Database Error!", Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorRed));
                            snackbar.show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Snackbar snackbar = Snackbar
                                        .make(parent, "Internet Connection Error!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorRed));
                                snackbar.show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("UserID", email); //Add the data you'd like to send to the server.
                        params.put("deletedID", getItem(position).getEmail()); //Add the data you'd like to send to the server.

                        return params;
                    }
                };
                MyRequestQueue.add(postRequest);

                Log.i("well", " this worked");
            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private TextView nameSurnameTextView;
        private ImageView profilePictureView;
        private ImageView acceptButton;
        private ImageView rejectButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.usernameReq);
            nameSurnameTextView = itemView.findViewById(R.id.userNameSurnameReq);
            profilePictureView = itemView.findViewById(R.id.userPicReq);
            acceptButton = itemView.findViewById(R.id.acceptBtn);
            rejectButton = itemView.findViewById(R.id.rejectBtn);
        }
    }
}