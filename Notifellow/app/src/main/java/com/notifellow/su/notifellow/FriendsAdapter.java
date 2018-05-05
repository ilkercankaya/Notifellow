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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FriendsAdapter extends ArrayAdapter<Friends> {
    private static RequestQueue MyRequestQueue;
    private static SharedPreferences shared;

    public FriendsAdapter(Context context, List<Friends> taskList) {
        super(context, R.layout.row_friends, taskList);
        MyRequestQueue = Volley.newRequestQueue(context);
        shared = context.getSharedPreferences("shared", MODE_PRIVATE);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View rowView = convertView;
        MyViewHolder holder;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_friends, null); //SET IT TO SUITABLE LAYOUT
        }

        if (rowView.getTag() == null) {
            holder = new MyViewHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (MyViewHolder) rowView.getTag();
        }

        holder.profilePicImageView.setImageURI(getItem(position).getProfilePicture());// used setImageResource but there are options,
        holder.nameSurnameTextView.setText(getItem(position).getUserName());
        holder.userIDTextView.setText(getItem(position).getNameSurname());

        holder.messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: IMPLEMENT CHAT FIREBASE
            }
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = shared.getString("email", null);
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteOrRejectFR",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Snackbar snackbar = Snackbar
                                        .make(parent, "Deleted User " + getItem(position).getUserName()+ "!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorGreen));
                                snackbar.show();
                                remove(getItem(position));
                                notifyDataSetChanged();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Snackbar snackbar = Snackbar
                                        .make(parent, "Database Error!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getContext().getResources().getColor(R.color.colorRed));
                                snackbar.show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("UserID", email); //Add the data you'd like to send to the server.
                        params.put("deletedID", getItem(position).getEmail()); //Add the data you'd like to send to the server.
                        return params;
                    }
                };
                MyRequestQueue.add(postRequest);
            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePicImageView;
        private TextView nameSurnameTextView;
        private TextView userIDTextView;
        private ImageView messageImageView;
        private ImageView deleteImageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            profilePicImageView = itemView.findViewById(R.id.userPic);
            nameSurnameTextView = itemView.findViewById(R.id.userNameSurname);
            userIDTextView = itemView.findViewById(R.id.userId);
            messageImageView = itemView.findViewById(R.id.chatFriend);
            deleteImageView = itemView.findViewById(R.id.deleteFriend);
        }
    }
}
