package com.notifellow.su.notifellow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.onesignal.OneSignal;
import java.util.HashMap;
import java.util.Map;



public class LoginScreen extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonSignup;
    private Button forgot;
    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "MAIN_ACTIVITY";
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private TextView exception;
    private EditText editTextEmail;
    private EditText editTextPassword; //
    private String url = "http://188.166.149.168:3030/addUser"; //server link

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        setContentView(R.layout.activity_login_screen);
        buttonLogin = findViewById(R.id.login);
        buttonSignup = findViewById(R.id.signup);
        exception = findViewById(R.id.exceptionRegLog);
        editTextEmail = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginScreen.this);
        mGoogleBtn = findViewById(R.id.googleSignIn);
        forgot = findViewById(R.id.forgot);
        queue = Volley.newRequestQueue(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        SharedPreferences settings = getSharedPreferences("shared", MODE_PRIVATE);

        // Launch to Schedule imeediatly
        String value = settings.getString("email", "");
        if (!TextUtils.isEmpty(value)) {
            Intent myIntent = new Intent(LoginScreen.this, Main.class);
            LoginScreen.this.startActivity(myIntent);
            finish();
        }

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginScreen.this, SignUp.class);
                LoginScreen.this.startActivity(myIntent);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginScreen.this, ForgotPassword.class);
                LoginScreen.this.startActivity(myIntent);
            }
        });

        Intent intent = getIntent();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                progressDialog.setMessage("Signing in Please Wait...");
                progressDialog.show();
                firebaseAuthWithGoogle(account);
            } else {
                //if canceled
                switch (result.getStatus().getStatusCode()) {
                    //  https://developers.google.com/android/reference/com/google/android/gms/common/api/CommonStatusCodes.html#NETWORK_ERROR
                    case 15:
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), "Timeout Occured!", Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbar.show();
                        break;
                    case 7:
                        Snackbar snackbarTwo = Snackbar
                                .make(findViewById(android.R.id.content), "Network Error Occured!", Snackbar.LENGTH_LONG);
                        snackbarTwo.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbarTwo.show();
                        break;
                    case 5:
                        Snackbar snackbarThree = Snackbar
                                .make(findViewById(android.R.id.content), "Invalid Account!", Snackbar.LENGTH_LONG);
                        snackbarThree.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbarThree.show();
                        break;
                    case 13:
                        Snackbar snackbarFour = Snackbar
                                .make(findViewById(android.R.id.content), "An Unknown Error Account!", Snackbar.LENGTH_LONG);
                        snackbarFour.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                        snackbarFour.show();
                        break;
                }
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final String userMail = acct.getEmail();
        final String lowerCaseMail = userMail.toLowerCase();
        String[] parts = lowerCaseMail.split("@");
        final String UserMailName = parts[0];
        //Create user on Notifellow server
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, final String registrationId) {
                Log.d("debug", "User:" + userId);
                if (userId != null) {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.getMessage().toString().trim());
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("usernameGiven", UserMailName);
                            params.put("emailGiven", lowerCaseMail); //Add the data you'd like to send to the server.
                            params.put("oneSignal", userId); //Add the data you'd like to send to the server.
                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
                else
                {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "Internet Connection Fail!", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                    snackbar.show();
                }

            }});

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithCredential:success");
                            //To launch without having to login multiple times
                            SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            //giving in dummy values since rooted phones can access shared objects without security.
                            editor.putString("email", lowerCaseMail.toLowerCase());
                            editor.remove("username");
                            editor.remove("name");
                            editor.remove("pnumber");
                            editor.remove("gender");
                            editor.apply();
                            editor.commit();
                            Intent myIntent = new Intent(LoginScreen.this, Main.class);
                            LoginScreen.this.startActivity(myIntent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            String msg = task.getException().getMessage();
                            Log.w(TAG, "signInWithCredential:", task.getException());
                            exception.setText(msg);
                            exception.setTextColor(Color.parseColor("#ff1919"));
                            exception.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() { //When the task fails
            @Override
            public void onFailure(@NonNull Exception e) {

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                snackbar.show();
                progressDialog.dismiss();
                exception.setText(e.getLocalizedMessage());
                exception.setTextColor(Color.parseColor("#ff1919"));
                exception.setVisibility(View.VISIBLE);
            }
        });
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }
    */

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login() {

        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "Enter an email address!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "Enter a password!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
            snackbar.show();
            return;
        }
        progressDialog.setMessage("Signing in Please Wait...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            // there was an error
                            progressDialog.dismiss();
                            String msg = task.getException().getMessage();
                            exception.setText(msg);
                            exception.setTextColor(Color.parseColor("#ff1919"));
                            exception.setVisibility(View.VISIBLE);
                        } else {
                            progressDialog.dismiss();
                            //if user is allowed to be recognized
                            SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            //giving in dummy values since rooted phones can access shared objects without security.
                            editor.putString("email", editTextEmail.getText().toString());
                            editor.remove("username");
                            editor.remove("name");
                            editor.remove("pnumber");
                            editor.remove("gender");
                            editor.apply();
                            editor.commit();
                            Intent myIntent = new Intent(LoginScreen.this, Main.class);
                            LoginScreen.this.startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }
}
