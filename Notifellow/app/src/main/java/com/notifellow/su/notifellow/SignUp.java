package com.notifellow.su.notifellow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static Button signUp, signIn;
    private static EditText bEmail, bUsername, bPassword, bPassword_repeat;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    RequestQueue MyRequestQueue;
    private String url = "http://188.166.149.168:3030/addUser";

    public void initializeElements() {
        signUp = (Button) findViewById(R.id.btnSignUp);
        signIn = (Button) findViewById(R.id.btnSignUpSignIn);
        bEmail = (EditText) findViewById(R.id.emailSignUp);
        bUsername = (EditText) findViewById(R.id.usernameSignUp);
        bPassword = (EditText) findViewById(R.id.passwordSignUp);
        bPassword_repeat = (EditText) findViewById(R.id.passwordRepeatSignUp);
        progressDialog = new ProgressDialog(SignUp.this);
        mAuth = FirebaseAuth.getInstance();
        MyRequestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeElements(); // Initialize buttons and other fields.

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SignUp.this, LoginScreen.class);
                SignUp.this.startActivity(myIntent);
                finish();
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = bUsername.getText().toString().trim();
                final String email = bEmail.getText().toString().trim();
                final String lowerCaseMail = email.toLowerCase();
                final String password = bPassword.getText().toString().trim();
                String passwordRpt = bPassword_repeat.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter An Email Adress!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordRpt)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Repeated Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordRpt)) {
                    Toast.makeText(getApplicationContext(), "Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password Should Be Longer Than 6 Characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Signing in Please Wait...");
                progressDialog.show();

                //Now create user on Notifellow server
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, final String registrationId) {
                        Log.d("debug", "User:" + userId);
                        if (registrationId != null)
                        {
                        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //This code is executed if the server responds, whether or not the response contains data.
                                //The String 'response' contains the server's response.
                                if (response.equals("CREATED 201")) {
                                    //create user in notifellowserver
                                    mAuth.createUserWithEmailAndPassword(lowerCaseMail, password)
                                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        //Firebase is succesfull
                                                        Toast.makeText(getApplicationContext(), "Register Successful!", Toast.LENGTH_LONG).show();
                                                        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = shared.edit();
                                                        //giving in dummy values since rooted phones can access shared objects without security.
                                                        editor.putString("username", username);
                                                        editor.commit();
                                                        startActivity(new Intent(SignUp.this, LoginScreen.class));
                                                        finish();
                                                        progressDialog.dismiss();
                                                    }
                                                    // If sign in fails, display a message to the user. If sign in succeeds
                                                    // the auth state listener will be notified and logic to handle the
                                                    // signed in user can be handled in the listener.
                                                }
                                            }).addOnFailureListener(new OnFailureListener() { //When the task fails
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUp.this, e.getLocalizedMessage(),
                                                    Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                } else if (response.equals("Failed to insert") || response.equals("Query Error Occured!")) {
                                    Toast.makeText(getApplicationContext(), "Failed Due To Server Side", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                } else if (response.equals("EXISTS")) {
                                    Toast.makeText(getApplicationContext(), "The Username Already Exists. Please Choice Another!", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //This code is executed if there is an error.
                                Toast.makeText(getApplicationContext()," Internet Connection Fail!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }) {
                            protected Map<String, String> getParams() {
                                Map<String, String> MyData = new HashMap<String, String>();
                                MyData.put("usernameGiven", username); //Add the data you'd like to send to the server.
                                MyData.put("emailGiven", lowerCaseMail); //Add the data you'd like to send to the server. oneSignal
                                MyData.put("oneSignal", registrationId); //Add the data you'd like to send to the server.

                                return MyData;
                            }
                        };
                        MyRequestQueue.add(MyStringRequest);
                    }
                    else
                            Toast.makeText(getApplicationContext()," Internet Connection Fail!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
}
