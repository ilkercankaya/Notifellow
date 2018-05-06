package com.notifellow.su.notifellow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity{

    private static Button sendEmail, signIn; // Daha clean olsun diye functionda yapabilmek için static yaptim
    private static EditText email; // Daha clean olsun diye functionda yapabilmek için static yaptim
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initializeElements(); // Initialize buttons and other fields.

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ForgotPassword.this, LoginScreen.class);
                ForgotPassword.this.startActivity(myIntent);
                finish();
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserEmail = email.getText().toString().trim();

                if (TextUtils.isEmpty(UserEmail)) {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "Please Enter Your Email!", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                    snackbar.show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                progressDialog.setMessage("Sending Instructions Please Wait...");
                progressDialog.show();
                auth.sendPasswordResetEmail(UserEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // do something when mail was sent successfully.
                                    Snackbar snackbar = Snackbar
                                            .make(findViewById(android.R.id.content), "Please Check Your E-mail For Further Instructions!", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGreen));
                                    snackbar.show();
                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                        snackbar.show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    public void initializeElements()
    {
        sendEmail = findViewById(R.id.btnSendEmail);
        signIn = findViewById(R.id.btnGotoSignIn);
        email = findViewById(R.id.emailForPW);
        progressDialog = new ProgressDialog(ForgotPassword.this);
    }

}
