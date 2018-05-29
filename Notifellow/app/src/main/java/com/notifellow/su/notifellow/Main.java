package com.notifellow.su.notifellow;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sendbird.android.SendBird;
//import com.notifellow.su.notifellow.notes.NotesMainActivity;
//import com.notifellow.su.notifellow.notes.Schema;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navBot; //bottom bar
    private FrameLayout mainFrame;

    private FeedFragment feedFragment;
    private NoteFragment noteFragment;
    private MessagingFragment messagingFragment;
    private ScheduleFragment scheduleFragment;
    private ExploreFragment exploreFragment;
    private TextView usernameAT, userfullNameAT;
    private CircleImageView userPP;
    static AlarmDBSchema schema;
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    static boolean reminderFragmentVisited = false;

    private static SharedPreferences shared;
    GoogleApiClient mGoogleApiClient;
    private static Context context;
    private static RequestQueue queue;

    private String uniqID;

    @Override
    protected void onStart() {
        super.onStart();
        SendBird.init("4EDE48B8-215F-402B-AE9B-A90458524107", this.getApplicationContext());
        mGoogleApiClient.connect();
    }

    public static void updateEmailAddressesLocalDB(String oldEmail, String value) {
        Log.i("Schedule: ", "Calling function from LocalDB");
        schema.updateEmailAddresses(oldEmail, value);
    }


    public static void cancelAlarm(final int alarmCode) {
        //REMOVE FROM GLOBAL DATABASE
        final String email = shared.getString("email", null);

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("NO INTERNET CONNECTION");
            builder.setMessage("No Internet connection detected. If you proceed, your friends will still see this event!");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(context, AlarmReceiver.class);
                    int code = alarmCode - 1;
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, code, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);

                    schema.deleteByID(String.valueOf(code));

                    Toast.makeText(context, "" + "" + "Task deleted!", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteAlarm", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    if (response.equals("DELETED 201")) {
                        //SUCCESFULL QUERY WILL EXECUTE FOLLOWING LINES
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent myIntent = new Intent(context, AlarmReceiver.class);
                        int code = alarmCode - 1;
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, code, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);

                        schema.deleteByID(String.valueOf(code));

                        Toast.makeText(context, "" + "" + "Task deleted!", Toast.LENGTH_LONG).show();

                    } else if (response.equals("Error On Delete")) {
                        //Query fail
                        Toast.makeText(context, "" + "" + "Query delete fail!", Toast.LENGTH_LONG).show();
                    } else {
                        //mongodb connection fail
                        Toast.makeText(context, "" + "Cannot connect to mongoDB " + "!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //This code is executed if there is an error.
                    Toast.makeText(context, "" + "" + " Cannot connect to Internet!", Toast.LENGTH_LONG).show();

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);

                    schema.deleteByID(String.valueOf(alarmCode));

                    Toast.makeText(context, "" + "" + "Task deleted!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("emailGiven", email);
                    MyData.put("alarmID", String.valueOf(alarmCode));
                    return MyData;
                }
            };
            queue.add(MyStringRequest);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        navBot = (BottomNavigationView) findViewById(R.id.NavBot);


        feedFragment = new FeedFragment();
        noteFragment = new NoteFragment();
        messagingFragment = new MessagingFragment();
        scheduleFragment = new ScheduleFragment();
        exploreFragment = new ExploreFragment();


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Schedule");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlue));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBlue));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);


        navBot.getMenu().getItem(2).setChecked(true);
        setIconByDate();

        setFragment(scheduleFragment);

        navBot.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.btnFeed:
                        toolbar.setTitle("Feed");
                        setFragment(feedFragment);
                        return true;
                    case R.id.btnSearch:
                        toolbar.setTitle("Explore");
                        setFragment(exploreFragment);
                        return true;
                    case R.id.btnSche:
                        toolbar.setTitle("Schedule");
                        setFragment(scheduleFragment);
                        return true;
                    case R.id.btnNotes:
                        toolbar.setTitle("Notes");
                        setFragment(noteFragment);
                        return true;
                    case R.id.btnMessages:
                        toolbar.setTitle("Direct Messaging");
                        setFragment(messagingFragment);
                        return true;
                }

                return false;
            }
        });




        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.drawable.ic_profile);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ITINLIASE PP AND STUFF
        queue = Volley.newRequestQueue(this);
        shared = getSharedPreferences("shared", MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://notify-7bef0.appspot.com");    //change the url according to your firebase app
        final String email = shared.getString("email", null);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getUniqID", new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                uniqID = response;
                final String ppSTAT = shared.getString("ppSTAT", null);

                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    //if (ppSTAT == null) {

                    if (true) {
                        storageRef.child(response).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created load the pic
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "exists");
                                editor.putString("ppDIR", response);
                                editor.commit();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                //  Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG);
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "doesntExists");
                                editor.commit();
                            }
                        });
                    } else if (localFile == null || !localFile.exists()) {
                        storageRef.child(response).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created load the pic
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "exists");
                                editor.putString("ppDIR", response);
                                editor.commit();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                //  Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG);
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "doesntExists");
                                editor.commit();
                            }
                        });
                    }
                }
                catch (IOException e){

                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                //   Toast.makeText(getApplicationContext(), "" + "" + " Cannot connect to Internet!", Toast.LENGTH_LONG).show();
                //uniqID = "404_NOT_FOUND"; //For errors
                //if exists with no internet
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("emailGiven", email);
                // MyData.put("alarmID", String.valueOf(alarmCode));
                return MyData;
            }
        };
        queue.add(MyStringRequest);


        schema = AlarmDBSchema.getInstance(getApplicationContext());
//        NoteCreateActivity.notesSchema = Schema.getInstance(this); //TODO: Uncomment and change this once you put notes.

        NoteCreateActivity.schema = NoteDBSchema.getInstance(this);

//        NotesListActivity.schema = NoteDBSchema.getInstance(this);

        Main.context = getApplicationContext();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Get variables
        usernameAT = findViewById(R.id.userNameHeader);
        userfullNameAT = findViewById(R.id.nameSurnameHeader);
        final String email = shared.getString("email", null);
        String value = shared.getString("name", null);
        final String ppSTAT = shared.getString("ppSTAT", null);
        final String ppDir = shared.getString("ppDIR", null);
        final CircleImageView ppImg = findViewById(R.id.profilePic);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            //if (ppSTAT == null) {

            //if (!(ppDir == null) && ppSTAT.equals("exists")) { //PP exists
            if (true) {
                storageRef.child(ppDir).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created load the pic
                        Glide.with(getApplicationContext())
                                .load(localFile)
                                .listener(new RequestListener<File, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        //Image is loaded
                                        return false;
                                    }
                                })
                                .into(ppImg);

                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("ppSTAT", "exists");
                        editor.putString("ppDIR", ppDir);
                        editor.commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        //  Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("ppSTAT", "doesntExists");
                        editor.commit();
                    }
                });


            } else if (ppSTAT == null) {
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getUniqID", new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        storageRef.child(response).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created load the pic
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "exists");
                                editor.putString("ppDIR", response);
                                editor.commit();
                                Glide.with(getApplicationContext())
                                        .load(localFile)
                                        .listener(new RequestListener<File, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                //Image is loaded
                                                return false;
                                            }
                                        })
                                        .into(ppImg);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                //  Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG);
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "doesntExists");
                                editor.commit();
                            }
                        });
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        //   Toast.makeText(getApplicationContext(), "" + "" + " Cannot connect to Internet!", Toast.LENGTH_LONG).show();
                        //uniqID = "404_NOT_FOUND"; //For errors
                        //if exists with no internet
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("emailGiven", email);
                        // MyData.put("alarmID", String.valueOf(alarmCode));
                        return MyData;
                    }
                };
                queue.add(MyStringRequest);
            } else {
                //Set empty picutre
          /*
           TODO: UNCOMMENT AND FIX THIS AREA, IT THREW EXCEPTION.
           Resources res = getResources();
            ppImg.setBackground(res.getDrawable(R.drawable.circular_profile_picture));
            ppImg.setImageDrawable(res.getDrawable(R.mipmap.notifellowlogo_round));
            */
            }
        }
        catch (IOException e){

        }
        if (value == null) {
            //check sharedreference and if its not null update the box otherwise make a web call and update
            StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getFullName",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response.equals("")) {
                                userfullNameAT.setText("Add Your Name In The Setting!");
                            } else {
                                SharedPreferences.Editor editor = shared.edit();
                                //giving in dummy values since rooted phones can access shared objects without security.
                                editor.putString("name", response);
                                editor.commit();
                                //check sharedreference and if its not null update the box otherwise make a web call and update
                                userfullNameAT.setText(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            userfullNameAT.setText("");
                            Toast.makeText(getApplication(), "Internet Connection Error!", Toast.LENGTH_SHORT).show();
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
            queue.add(postRequest);
        } else {
            userfullNameAT.setText(value);
        }
        final String usernamevalue = shared.getString("username", null);
        if (usernamevalue == null) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getUsername",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            SharedPreferences.Editor editor = shared.edit();
                            //giving in dummy values since rooted phones can access shared objects without security.
                            editor.putString("username", response);
                            editor.commit();
                            //check sharedreference and if its not null update the box otherwise make a web call and update
                            usernameAT.setText(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            usernameAT.setText("");
                            Toast.makeText(getApplicationContext(), "Internet Connection Error!", Toast.LENGTH_SHORT).show();
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
            queue.add(postRequest);
        } else {
            usernameAT.setText(usernamevalue);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
            Intent myIntent = new Intent(Main.this, Settings.class);
            Main.this.startActivity(myIntent);

        } else if (id == R.id.nav_interests) {

            Intent myIntent = new Intent(Main.this, EventRequests.class);
            Main.this.startActivity(myIntent);

        } else if (id == R.id.nav_social) {
            Intent myIntent = new Intent(Main.this, Social.class);
            Main.this.startActivity(myIntent);
        }else if (id == R.id.nav_aboutus) {

        } else if (id == R.id.nav_logout) {
            //Sign-off from firebase
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            //Sign off g+
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            //Remove relogger
            SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
            //Launch loginPage and Sign-off from g+
            Intent myIntent = new Intent(Main.this, LoginScreen.class);
            Main.this.startActivity(myIntent);
            finish();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment current) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, current).commit();

    }

    public void setIconByDate() {
        // takes the current day of month and sats the schedule icon to that day.
        Calendar calendar = Calendar.getInstance();
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (thisDay == 1)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day1));
        else if (thisDay == 2)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day2));
        else if (thisDay == 3)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day3));
        else if (thisDay == 4)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day4));
        else if (thisDay == 5)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day5));
        else if (thisDay == 6)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day6));
        else if (thisDay == 7)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day7));
        else if (thisDay == 8)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day8));
        else if (thisDay == 9)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day9));
        else if (thisDay == 10)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day10));
        else if (thisDay == 11)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day11));
        else if (thisDay == 12)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day12));
        else if (thisDay == 13)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day13));
        else if (thisDay == 14)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day14));
        else if (thisDay == 15)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day15));
        else if (thisDay == 16)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day16));
        else if (thisDay == 17)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day17));
        else if (thisDay == 18)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day18));
        else if (thisDay == 19)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day19));
        else if (thisDay == 20)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day20));
        else if (thisDay == 21)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day21));
        else if (thisDay == 22)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day22));
        else if (thisDay == 23)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day23));
        else if (thisDay == 24)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day24));
        else if (thisDay == 25)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day25));
        else if (thisDay == 26)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day26));
        else if (thisDay == 27)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day27));
        else if (thisDay == 28)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day28));
        else if (thisDay == 29)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day29));
        else if (thisDay == 30)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day30));
        else if (thisDay == 31)
            navBot.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_day31));
    }

}
