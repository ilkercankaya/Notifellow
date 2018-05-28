package com.notifellow.su.notifellow;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.notifellow.su.notifellow.notes.NotesMainActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Settings extends AppCompatPreferenceActivity {
    private static RequestQueue MyRequestQueue;
    private static SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment()).commit();
        setupActionBar();
        MyRequestQueue = Volley.newRequestQueue(this);
        shared = getSharedPreferences("shared", MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
    }


    private void setupActionBar() {
        getLayoutInflater().inflate(R.layout.settings_toolbar, (ViewGroup) findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlue));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBlue));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(5);
        }
    }


    public static class PrefFragment extends PreferenceFragment {
        // initialize
        private int PICK_IMAGE_REQUEST = 1;
        private FirebaseStorage storage;
        private StorageReference storageRef;
        private ProgressDialog progressDialog;
        private String uniqID;
        private NetworkInfo netInfo;

        public void removeAndCancelAlarms(String email) {
            AlarmDBSchema schema = AlarmDBSchema.getInstance(getActivity());

            Cursor cursor = schema.getAlarmCodeByEmail(email);
            int colID = cursor.getColumnIndex("ID");

            long rowCount = schema.getRowCountByEmail(email);

            cursor.moveToFirst();

            if (cursor != null && (rowCount > 0)) {
                do {
                    String ID = cursor.getString(colID);

                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), Integer.parseInt(ID), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);

                    schema.deleteByID(ID);

                    rowCount--;
                }
                while (cursor.moveToNext());
            }
        }

        private void showFileChooser(final String uniqID) {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
            pictureDialog.setTitle("Select Action");
            String[] pictureDialogItems = {
                    "Select photo from gallery",
                    "Delete Profile Picture"};
            pictureDialog.setItems(pictureDialogItems,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        pickImageIntent.setType("image/*");
                                        pickImageIntent.putExtra("aspectX", 1);
                                        pickImageIntent.putExtra("aspectY", 1);
                                        pickImageIntent.putExtra("scale", true);
                                        pickImageIntent.putExtra("outputFormat",
                                                Bitmap.CompressFormat.JPEG.toString());
                                        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
                                    } else {
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content), "Internet Connection Fail!", Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();
                                    }
                                    break;
                                case 1:
                                    ConnectivityManager connectivityManagerTwo = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    if (connectivityManagerTwo.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                            connectivityManagerTwo.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                        //You can replace it with your name
                                        final String ppDIR = shared.getString("ppDIR", null);
                                        progressDialog.setMessage("Deleting Picture...");
                                        progressDialog.show();
                                        storageRef.child(ppDIR).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted
                                                SharedPreferences.Editor editor = shared.edit();
                                                editor.putString("ppSTAT", "doesntExists");
                                                editor.commit();
                                                Snackbar snackbar = Snackbar
                                                        .make(getActivity().findViewById(android.R.id.content),"Profile Picture Deleted!", Snackbar.LENGTH_LONG);
                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                                snackbar.show();
                                                CircleImageView img = getActivity().findViewById(R.id.pictureSettings);
                                                img.setImageResource(R.drawable.ic_profile);
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                                Snackbar snackbar = Snackbar
                                                        .make(getActivity().findViewById(android.R.id.content), exception.getMessage(), Snackbar.LENGTH_LONG);
                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                                snackbar.show();
                                                progressDialog.dismiss();

                                            }
                                        });
                                    } else
                                    {
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content), "Internet Connection Fail!", Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();
                                    }
                                    break;
                            }
                        }
                    });
            pictureDialog.show();


        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                final Uri filePath = data.getData();
                if (filePath != null) {
                    progressDialog.setMessage("Uploading Picture...");
                    progressDialog.show();

                    StorageReference childRef = storageRef.child(uniqID);

                    //uploading the image

                    //You can replace it with your name
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Snackbar snackbarUno = Snackbar
                                    .make(getActivity().findViewById(android.R.id.content),"Upload Successful", Snackbar.LENGTH_LONG);
                            snackbarUno.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                            snackbarUno.show();
                            CircleImageView img = getActivity().findViewById(R.id.pictureSettings);
                            img.setImageURI(filePath);

                            try {
                                InputStream in = getActivity().getContentResolver().openInputStream(filePath);
                                OutputStream out = new FileOutputStream(new File(getActivity().getCacheDir(), uniqID + ".jpg"));
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                out.close();
                                in.close();
                            }catch(FileNotFoundException e){
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                snackbar.show();

                            }catch(IOException e){
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                snackbar.show();
                            }

                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("ppSTAT", "exists");
                            editor.putString("ppDIR", uniqID);
                            editor.commit();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar
                                    .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                            snackbar.show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    Snackbar snackbar = Snackbar
                            .make(getActivity().findViewById(android.R.id.content),"Select an image", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                    snackbar.show();
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_all);
            //Set inital values for the boxes
            //Upload PP
            final String email = shared.getString("email", null);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReferenceFromUrl("gs://notify-7bef0.appspot.com");    //change the url according to your firebase app
            progressDialog = new ProgressDialog(getActivity());
            //Get pp
            progressDialog.setMessage("Getting The Information...");
            progressDialog.show();

            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getUniqID", new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(final String response) {
                    uniqID = response;
                    final String ppSTAT = shared.getString("ppSTAT", null);

                    final CircleImageView ppImg = getActivity().findViewById(R.id.pictureSettings);
                    final File localFile = new File(getActivity().getCacheDir(), response + ".jpg");
                    if (ppSTAT == null) {
                        storageRef.child(response).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created load the pic
                                SharedPreferences.Editor editor = shared.edit();
                                editor.putString("ppSTAT", "exists");
                                editor.putString("ppDIR", response);
                                editor.commit();
                                Glide.with(getContext())
                                        .load(localFile)
                                        .listener(new RequestListener<File, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                Snackbar snackbar = Snackbar
                                                        .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                                snackbar.show();
                                                progressDialog.dismiss();

                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                //Image is loaded
                                                progressDialog.dismiss();
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
                    } else if(ppSTAT.equals( "exists")){ //PP exists
                        Glide.with(getContext())
                                .load(localFile)
                                .listener(new RequestListener<File, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();

                                        progressDialog.dismiss();
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("ppDIR", response);
                                        editor.commit();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        //Image is loaded
                                        progressDialog.dismiss();
                                        return false;
                                    }
                                })
                                .into(ppImg);
                    }else{
                        //Do nothings the user dont have any PP
                        progressDialog.dismiss();
                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onErrorResponse(VolleyError error) {
                    //This code is executed if there is an error.
                    //   Toast.makeText(getApplicationContext(), "" + "" + " Cannot connect to Internet!", Toast.LENGTH_LONG).show();
                    //uniqID = "404_NOT_FOUND"; //For errors
                    //if exists with no internet
                    final CircleImageView ppImg = getActivity().findViewById(R.id.pictureSettings);
                    final String ppDir = shared.getString("ppDIR", null);
                    final String ppSTAT = shared.getString("ppSTAT", null);
                    if(! (ppDir == null) &&  ppSTAT.equals("exists") ){ //PP exists
                        final File localFile = new File(getActivity().getCacheDir(), ppDir + ".jpg");
                        Glide.with(getContext())
                                .load(localFile)
                                .listener(new RequestListener<File, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();
                                        progressDialog.dismiss();

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        //Image is loaded
                                        progressDialog.dismiss();
                                        return false;
                                    }
                                })
                                .into(ppImg);
                    }
                    else
                        progressDialog.dismiss();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("emailGiven", email);
                    // MyData.put("alarmID", String.valueOf(alarmCode));
                    return MyData;
                }
            };
            MyRequestQueue.add(MyStringRequest);




            Preference myPref = (Preference) findPreference("profile_picture");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    showFileChooser(uniqID);
                    return true;
                }
            });


            // Create a reference with an initial file path and name

            // INITIALIZAATION OF DELETE ACCOUNT PREFERENCE
            Preference deleteAcc = (Preference) findPreference("Delete Account");
            deleteAcc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.deleteaccount_dialogue);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES
                    dialog.show();

                    Button btn_yes, btn_no;
                    btn_yes = dialog.findViewById(R.id.btn_yes_del);
                    btn_no = dialog.findViewById(R.id.btn_no_del);

                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getActivity(), "You Clicked Yes Button", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //User deleted from firebase now from mongoDB
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/deleteUser",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // response
                                                        if (response.equals("DELETED 201")) {
                                                            //DELETED
                                                            //Delete all shared value
                                                            SharedPreferences.Editor editor = shared.edit();
                                                            editor.clear();
                                                            editor.commit();
                                                            Intent intent = new Intent(getActivity(), LoginScreen.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            Toast.makeText(getActivity(), "User Account Deleted!", Toast.LENGTH_SHORT).show();
                                                            removeAndCancelAlarms(email);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        Snackbar snackbar = Snackbar
                                                                .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                                        snackbar.show();
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

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),task.getException().getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() { //When the task fails
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar snackbar = Snackbar
                                            .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                    snackbar.show();
                                }
                            });
                            dialog.dismiss();
                        }

                    });

                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //DO NOTHING
                            //Toast.makeText(getActivity(), "You Clicked No Button", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    });

                    return false;
                }
            });

            // INITIALIZAATION OF CHANGE PASSWORD PREFERENCE
            Preference changePw = (Preference) findPreference("Change Password");
            changePw.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.changepassword_dialog_v2);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES
                    dialog.show();

                    Button btn_ok, btn_cancel;
                    final EditText oldPw, newPw, repPw;
                    btn_ok = dialog.findViewById(R.id.btn_savePW);
                    btn_cancel = dialog.findViewById(R.id.btn_cancelPW);
                    oldPw = dialog.findViewById(R.id.oldpw);
                    newPw = dialog.findViewById(R.id.newpw);
                    repPw = dialog.findViewById(R.id.repeatnewpw);

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //   Toast.makeText(getActivity(), "You Clicked Ok Button", Toast.LENGTH_SHORT).show();
                            if (!newPw.getText().toString().trim().equals(repPw.getText().toString().trim())) {
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(android.R.id.content),"Your New Passwords Dont Match!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                snackbar.show();

                            } else {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String email = shared.getString("email", null);
                                // Get auth credentials from the user for re-authentication. The example below shows
                                // email and password credentials but there are multiple possible providers,
                                // such as GoogleAuthProvider or FacebookAuthProvider.
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(email, oldPw.getText().toString().trim());

                                // Prompt the user to re-provide their sign-in credentials
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    user.updatePassword(newPw.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Snackbar snackbar = Snackbar
                                                                        .make(getActivity().findViewById(android.R.id.content),"Your Password Has Been Updated!", Snackbar.LENGTH_LONG);
                                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                                                snackbar.show();
                                                            } else {
                                                                Snackbar snackbar = Snackbar
                                                                        .make(getActivity().findViewById(android.R.id.content),task.getException().getMessage(), Snackbar.LENGTH_LONG);
                                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                                                snackbar.show();

                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Snackbar snackbar = Snackbar
                                                            .make(getActivity().findViewById(android.R.id.content),task.getException().getMessage(), Snackbar.LENGTH_LONG);
                                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                                    snackbar.show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() { //When the task fails
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),e.getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();
                                    }
                                });
                            }
                            dialog.dismiss();

                        }

                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //DO NOTHING
                            // Toast.makeText(getActivity(), "You Clicked Cancel Button", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    });

                    return false;
                }
            });


            //fullname box
            final EditTextPreference name = (EditTextPreference) this.findPreference("name");
            String value = shared.getString("name", null);
            if (value == null) {
                //check sharedreference and if its not null update the box otherwise make a web call and update
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getFullName",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                if (response.equals("")) {
                                    name.setText("");
                                    name.setSummary("Please Add Your Fullname!");
                                } else {
                                    SharedPreferences.Editor editor = shared.edit();
                                    //giving in dummy values since rooted phones can access shared objects without security.
                                    editor.putString("name", response);
                                    editor.commit();
                                    //check sharedreference and if its not null update the box otherwise make a web call and update
                                    name.setText(response);
                                    name.setSummary(response);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                name.setText("");
                                name.setSummary("No internet Connection To Display Fullname!");
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                snackbar.show();
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

            } else {
                //check sharedreference and if its not null update the box otherwise make a web call and update
                name.setText(value);
                name.setSummary(value);
            }

            name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final String value = (String) newValue;
                    //Make server call
                    final String email = shared.getString("email", null);
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/editName",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    if (value == null || value.equals("")) {
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("name", value);
                                        name.setText("");
                                        name.setSummary("Please Add Your Fullname!");
                                    } else {
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("name", value);
                                        editor.commit();
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),"Your Name Has Been Updated!", Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                        snackbar.show();
                                        name.setText(value);
                                        name.setSummary(value);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Snackbar snackbar = Snackbar
                                            .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                    snackbar.show();
                                    String prevValue = shared.getString("name", null);
                                    if (prevValue == null || prevValue.equals("")) {
                                        //check sharedreference and if its not null update the box otherwise make a web call and update
                                        name.setText("");
                                        name.setSummary("Please Add Your Fullname!");
                                    } else {
                                        name.setText(prevValue);
                                        name.setSummary(prevValue);
                                    }

                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("emailGiven", email); //Add the data you'd like to send to the server.
                            params.put("nameGiven", value); //Add the data you'd like to send to the server.
                            return params;
                        }
                    };
                    MyRequestQueue.add(postRequest);
                    return true; // indicates you processed the new value
                }
            });


            //username box
            final EditTextPreference username = (EditTextPreference) this.findPreference("username");
            String usernamevalue = shared.getString("username", null);
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
                                username.setText(response);
                                username.setSummary(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                username.setText("");
                                username.setSummary("No internet Connection To Display Username!");
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                snackbar.show();
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
            } else {
                //check sharedreference and if its not null update the box otherwise make a web call and update
                username.setText(usernamevalue);
                username.setSummary(usernamevalue);
            }

            username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final String value = (String) newValue;
                    final String email = shared.getString("email", null);
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/editUsername",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    if (response.equals("EXISTS")) {
                                        //if exists store the old user back
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),"The Username Is Choicen!", Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();

                                        final String prevUsername = shared.getString("username", null);
                                        //check sharedreference and if its not null update the box otherwise make a web call and update
                                        username.setText(prevUsername);
                                        username.setSummary(prevUsername);
                                    } else if (response.equals("UPDATED 201")) {
                                        SharedPreferences.Editor editor = shared.edit();
                                        //giving in dummy values since rooted phones can access shared objects without security.
                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),"Your Username Has Been Updated!", Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                        snackbar.show();
                                        editor.putString("username", value);
                                        editor.commit();
                                        //check sharedreference and if its not null update the box otherwise make a web call and update
                                        username.setText(value);
                                        username.setSummary(value);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    final String prevUsername = shared.getString("username", null);
                                    //check sharedreference and if its not null update the box otherwise make a web call and update
                                    username.setText(prevUsername);
                                    username.setSummary(prevUsername);
                                    Snackbar snackbar = Snackbar
                                            .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                    snackbar.show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("emailGiven", email); //Add the data you'd like to send to the server.
                            params.put("userIDGiven", value); //Add the data you'd like to send to the server.
                            return params;
                        }
                    };
                    MyRequestQueue.add(postRequest);

                    return true; // indicates you processed the new value
                }
            });
            //email box
            final EditTextPreference usermailREF = (EditTextPreference) this.findPreference("email");
            String usermail = shared.getString("email", null);
            //check sharedreference and if its not null update the box otherwise make a web call and update
            usermailREF.setText(usermail);
            usermailREF.setSummary(usermail);

            //On Change
            usermailREF.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final String value = (String) newValue;
                    final String email = shared.getString("email", null);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.updateEmail(value)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Change email in mongoDB
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/editEmail",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // response
                                                        if (response.equals("UPDATED 201")) {
                                                            final String oldEmail = shared.getString("email", null);
                                                            SharedPreferences.Editor editor = shared.edit();
                                                            //giving in dummy values since rooted phones can access shared objects without security.
                                                            editor.putString("email", value);
                                                            editor.commit();
                                                            Log.i("Settings: ", "Email updated, change in LocalDB");
                                                            Snackbar snackbar = Snackbar
                                                                    .make(getActivity().findViewById(android.R.id.content),"Your Email Has Been Updated!", Snackbar.LENGTH_LONG);
                                                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                                            snackbar.show();
                                                            Main.updateEmailAddressesLocalDB(oldEmail, value);
                                                            NoteCreateActivity.updateEmailAddressesNotesDB(oldEmail, value);
                                                            //check sharedreference and if its not null update the box otherwise make a web call and update
                                                            usermailREF.setText(value);
                                                            usermailREF.setSummary(value);
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        final String prevUsername = shared.getString("email", null);
                                                        //check sharedreference and if its not null update the box otherwise make a web call and update
                                                        usermailREF.setText(prevUsername);
                                                        usermailREF.setSummary(prevUsername);
                                                        Snackbar snackbar = Snackbar
                                                                .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                                        snackbar.show();
                                                    }
                                                }
                                        ) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("emailGiven", email); //Add the data you'd like to send to the server.
                                                params.put("newEmailGiven", value); //Add the data you'd like to send to the server.
                                                return params;
                                            }
                                        };
                                        MyRequestQueue.add(postRequest);
                                    } else {
                                        //Update Failed
                                        usermailREF.setText(email);
                                        usermailREF.setSummary(email);

                                        Snackbar snackbar = Snackbar
                                                .make(getActivity().findViewById(android.R.id.content),task.getException().getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        snackbar.show();
                                    }
                                }
                            });
                    return true; // indicates you processed the new value
                }
            });

            //phonenumber box
            final EditTextPreference pnumber = (EditTextPreference) this.findPreference("pnumber");
            final String pnumberStr = shared.getString("pnumber", null);
            if (pnumberStr == null) {
                //check sharedreference and if its not null update the box otherwise make a web call and update
                StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/getPnumber",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                if (response.equals("")) {
                                    pnumber.setText("");
                                    pnumber.setSummary("Please Add Your Phone Number!");
                                } else {
                                    SharedPreferences.Editor editor = shared.edit();
                                    //giving in dummy values since rooted phones can access shared objects without security.
                                    editor.putString("pnumber", response);
                                    editor.commit();
                                    //check sharedreference and if its not null update the box otherwise make a web call and update
                                    pnumber.setText(response);
                                    pnumber.setSummary(response);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                pnumber.setText("");
                                pnumber.setSummary("No internet Connection To Display Phone Number!");
                                Snackbar snackbar = Snackbar
                                        .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                snackbar.show();
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

            } else {
                //check sharedreference and if its not null update the box otherwise make a web call and update
                pnumber.setText(pnumberStr);
                pnumber.setSummary(pnumberStr);
            }

            pnumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final String value = (String) newValue;
                    final String email = shared.getString("email", null);
                    StringRequest postRequest = new StringRequest(Request.Method.POST, "http://188.166.149.168:3030/editPnumber",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    if (value == null || value.equals("")) {
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("pnumber", value);
                                        pnumber.setText("");
                                        pnumber.setSummary("Please Add Your Phone Number!");
                                    } else {
                                        SharedPreferences.Editor editor = shared.edit();
                                        editor.putString("pnumber", value);
                                        editor.commit();
                                        pnumber.setText(value);
                                        pnumber.setSummary(value);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Snackbar snackbar = Snackbar
                                            .make(getActivity().findViewById(android.R.id.content),"Internet Connection Error!", Snackbar.LENGTH_LONG);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGray));
                                    snackbar.show();
                                    String prevValue = shared.getString("pnumber", null);
                                    if (prevValue == null || prevValue.equals("")) {
                                        //check sharedreference and if its not null update the box otherwise make a web call and update
                                        pnumber.setText("");
                                        pnumber.setSummary("Please Add Your Phone Number!");
                                    } else {
                                        pnumber.setText(prevValue);
                                        pnumber.setSummary(prevValue);
                                    }

                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("emailGiven", email); //Add the data you'd like to send to the server.
                            params.put("pnumberGiven", value); //Add the data you'd like to send to the server.
                            return params;
                        }
                    };
                    MyRequestQueue.add(postRequest);

                    return true; // indicates you processed the new value
                }
            });

            //gender
            final ListPreference listPreference = (ListPreference) findPreference("gender");
            CharSequence currText = listPreference.getEntry();
            String currValue = listPreference.getValue();
            listPreference.setSummary(currValue);
            if (currValue.equals("0")) {
                listPreference.setSummary("Not Specified");
                listPreference.setValueIndex(0);
            }
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = (String) newValue;

                    listPreference.setSummary(value);

                    return true; // indicates you processed the new value
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            // calculate margins
            int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            //int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.), getResources().getDisplayMetrics());
            TypedValue tv = new TypedValue();
            int actionBarHeight = 46;
            if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                actionBarHeight = (int) TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
            view.setPadding(horizontalMargin, actionBarHeight, horizontalMargin, verticalMargin);
            return view;
        }

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) //Back Button for settings.
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return false;
    }

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }
}