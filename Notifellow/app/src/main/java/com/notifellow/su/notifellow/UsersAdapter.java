package com.notifellow.su.notifellow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;

import static android.content.Context.MODE_PRIVATE;

public class UsersAdapter extends BaseAdapter {

    //Variables
    Context mContext;
    LayoutInflater inflater;
    List<card_user> userList;
    ArrayList<card_user> arrayList;
    private static RequestQueue MyRequestQueue;
    private static SharedPreferences shared;
    //end of variables
    private RelativeLayout mRoot;

    //Constructor
    public UsersAdapter(Context context, List<card_user> userList) {
        mContext = context;
        this.userList = userList;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<card_user>();
        this.arrayList.addAll(userList);
        MyRequestQueue = Volley.newRequestQueue(mContext);
        shared = context.getSharedPreferences("shared", MODE_PRIVATE);
    }

    public class ViewHolder //ViewHolder for userList
    {
        TextView name_surnameTv, usernameTv, statusUSR;
        Button addUSRBtn;
        ImageView profilepicIv;
        ImageView addAsFriend;
    }


    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final Activity activity = (Activity) mContext;

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_users, null);

            //initialize view
            holder.name_surnameTv = view.findViewById(R.id.userNameSurname);
            holder.usernameTv = view.findViewById(R.id.userId);
            holder.profilepicIv = view.findViewById(R.id.userPic);
            holder.statusUSR = view.findViewById(R.id.statusUser);
            holder.addUSRBtn = view.findViewById(R.id.userAddBTN);
            //holder.addAsFriend = view.findViewById(R.id.addUserAsFriend); //Deleted for now
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!userList.get(position).getStatus().equals("You Can Add This User As A Friend."))
            holder.addUSRBtn.setVisibility(View.INVISIBLE);

        // Setting the results into views.
        holder.name_surnameTv.setText(userList.get(position).getNameSurname());
        holder.usernameTv.setText(userList.get(position).getUserName());
        holder.statusUSR.setText(userList.get(position).getStatus());
        holder.profilepicIv.setImageURI(userList.get(position).getProfilePic());// used setImageResource but there are options,
        // setImageBitmap may be suitable for database i guess.
        if (userList.get(position).getStatus().equals("You Can Add This User As A Friend.") ) {
            holder.addUSRBtn.setVisibility(View.VISIBLE);
            holder.addUSRBtn.setText("+");
        }
        else if (userList.get(position).getStatus().equals("Friend Request Sent!") ) {
            holder.addUSRBtn.setVisibility(View.VISIBLE);
            holder.addUSRBtn.setText("-");
        }
        final String user_info = (String) holder.name_surnameTv.getText();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: IMPLEMENT VIEW USER PROFILE HERE
                Toast.makeText(mContext, "You have clicked on the Row for " + userList.get(position).getStatus()  + ".",
                        Toast.LENGTH_SHORT).show();
            }
        });


        holder.addUSRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Button button = (Button) view;
                final String email = shared.getString("email", null);
                if (button.getText().toString().equals("+"))
                {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/addFR",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    holder.statusUSR.setText("Friend Request Sent!");
                                    userList.get(position).setStatus("Friend Request Sent!");
                                    button.setText("-");
                                    Snackbar snackbar = Snackbar
                                            .make(activity.findViewById(android.R.id.content), "You have sent friend request to " + userList.get(position).getNameSurname() + ".", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(mContext.getResources().getColor(R.color.colorBlue));
                                    snackbar.show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Snackbar snackbar = Snackbar
                                            .make(activity.findViewById(android.R.id.content), "Internet Connection Fail!", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(mContext.getResources().getColor(R.color.colorGray));
                                    snackbar.show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("UserID", email); //Add the data you'd like to send to the server.
                            params.put("AddedID", userList.get(position).getUSREmail()); //Add the data you'd like to send to the server.
                            return params;
                        }
                    };
                    MyRequestQueue.add(postRequest);
                }
                else if (button.getText().toString().equals("-")){
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteOrRejectFR",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    holder.statusUSR.setText("You Can Add This User As A Friend.");
                                    userList.get(position).setStatus("You Can Add This User As A Friend.");
                                    button.setText("+");
                                    Snackbar snackbar = Snackbar
                                            .make(activity.findViewById(android.R.id.content), "You have deleted your request to " + userList.get(position).getNameSurname() + ".", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(mContext.getResources().getColor(R.color.colorBlue));
                                    snackbar.show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Snackbar snackbar = Snackbar
                                            .make(activity.findViewById(android.R.id.content), "Internet Connection Fail!", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(mContext.getResources().getColor(R.color.colorGray));
                                    snackbar.show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("UserID", email); //Add the data you'd like to send to the server.
                            params.put("deletedID", userList.get(position).getUSREmail()); //Add the data you'd like to send to the server.
                            return params;
                        }
                    };
                    MyRequestQueue.add(postRequest);
                }
            }
        });
       /* holder.addAsFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMPLEMENT ADD USER AS FRIEND HERE
                Toast.makeText(mContext, "You have clicked on the Add as Friend Button for " + user_info +".",
                        Toast.LENGTH_SHORT).show();
            }
        });*/ // Deleted for now


        return view;
    }

    //Filter
    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        userList.clear();

        if (charText.length() == 0) {
            userList.addAll(arrayList);
        } else {
            for (card_user user : arrayList) {
                if (user.getNameSurname().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    userList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }


}
