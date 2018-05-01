package com.notifellow.su.notifellow;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class ExploreFragment extends Fragment {

    ListView listView;
    UsersAdapter adapter;
    String[] nameSurname;
    String[] username;
    Uri[] userPicture;
    private static RequestQueue MyRequestQueue;
    private static SharedPreferences shared;
    ArrayList<card_user> arrayList = new ArrayList<card_user>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MyRequestQueue = Volley.newRequestQueue(getActivity());
        shared = getActivity().getSharedPreferences("shared", MODE_PRIVATE);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        getActivity().setTitle(null);
        setHasOptionsMenu(true);
        listView = view.findViewById(R.id.listView);
        arrayList.clear();
        adapter = new UsersAdapter(this.getActivity(), arrayList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                final String email = shared.getString("email", null);
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/searchDB",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                try {
                                    arrayList.clear(); //Clear for new results
                                    JSONArray jsonARR = new JSONArray(response);
                                    for (int i = 0; i < jsonARR.length(); i++) {
                                        JSONObject oneUser = jsonARR.getJSONObject(i);
                                        String name = oneUser.getString("fullName");
                                        String username = oneUser.getString("username");
                                        String userEmail = oneUser.getString("email");
                                        String ppDEST = oneUser.getString("ppDest");
                                        String Status = oneUser.getString("status");
                                        if (name.equals(""))
                                            name = "-";
                                        if (Status.equals("2"))
                                            Status = "Friends With This User";
                                        else if (Status.equals("0"))
                                            Status = "Friend Request Sent!";
                                        else if (Status.equals("1"))
                                            Status = "Friend Request Has Been Received!";
                                        else if (Status.equals("-1"))
                                            Status = "You Can Add This User As A Friend.";
                                        card_user user = new card_user(name, username, userEmail, Status, null);
                                        arrayList.add(user);
                                    }
                                    adapter.notifyDataSetChanged();
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
                        params.put("queryGiv", query); //Add the data you'd like to send to the server.

                        return params;
                    }
                };
                MyRequestQueue.add(postRequest);

                Log.i("well", " this worked");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.i("well", " this worked");
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_search):

                return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
