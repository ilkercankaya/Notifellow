package com.notifellow.su.notifellow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SocialFriends extends Fragment {

    ListView friendsListView;
    static FriendsAdapter friendsAdapter;
    static ArrayList<Friends> friendsList = new ArrayList<Friends>();

    private RequestQueue MyRequestQueue;
    private SharedPreferences shared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        MyRequestQueue = Volley.newRequestQueue(getActivity());
        shared = getActivity().getSharedPreferences("shared", MODE_PRIVATE);

        friendsListView = rootView.findViewById(R.id.listView_Friends);
        friendsAdapter = new FriendsAdapter(getContext(), friendsList);
        friendsListView.setAdapter(friendsAdapter);

        final String email = shared.getString("email", null);
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getUserFriends",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            friendsList.clear(); //Clear for new results
                            JSONArray jsonARR = new JSONArray(response);
                            for (int i = 0; i < jsonARR.length(); i++) {
                                JSONObject oneUser = jsonARR.getJSONObject(i);
                                String name = oneUser.getString("fullName");
                                String username = oneUser.getString("username");
                                String userEmail = oneUser.getString("email");
                                String ppDEST = oneUser.getString("ppDest");

                                if (name.equals(""))
                                    name = "-";

                                Friends friend = new Friends(name, username, userEmail, null);
                                friendsList.add(friend);
                            }
                            friendsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getActivity(), "Internet Connection Error!", Toast.LENGTH_SHORT).show();
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

        Log.i("well", " this worked");

        return rootView;
    }
}
