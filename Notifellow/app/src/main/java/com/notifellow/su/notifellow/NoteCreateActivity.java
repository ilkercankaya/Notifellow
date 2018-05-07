package com.notifellow.su.notifellow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static com.notifellow.su.notifellow.NotePathUtils.isDownloadsDocument;
import static com.notifellow.su.notifellow.NotePathUtils.isExternalStorageDocument;


public class NoteCreateActivity extends AppCompatActivity {

    private static final String TASKS_KEY = "com.notifellow.su.notifellow.notes_key";
    private static final String TAG = NoteCreateActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA_PERMISSION = 1888;
//    private static final int EXTERNAL_STORAGE_PERMISSION = 665; // WE ARE NOT BEAST.. yet.
    private static final int REQUEST_CAMERA_ACTION = 9001;
    private static final int REQUEST_GALLERY = 999;
    private EditText etTitle;
    private EditText etEntry;
    private ImageView imageView;
    private ArrayList<Note> noteArrayList;
    private String path;
    private String email;
    static NoteDBSchema schema;


    static void updateEmailAddressesNotesDB(String oldEmail, String value){
        schema.updateEmailAddresses(oldEmail, value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_note);

        etTitle = findViewById(R.id.noteTitleTxt);
        etEntry = findViewById(R.id.noteTxt);
        imageView = findViewById(R.id.NoteImage);

        Intent receivedIntent = getIntent();

        email = receivedIntent.getStringExtra("email");

        FloatingActionButton fabSet = findViewById(R.id.fabSet);
        if (fabSet != null) {
            fabSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNoteTask();
                    NoteFragment.noteAdapter.notifyDataSetChanged();
                    finish(); //navigates to schedule.
                }
            });
        }

        FloatingActionButton fabImage = findViewById(R.id.fabImage);
        if (fabImage != null) {
            fabImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(NoteCreateActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
                }
            });
        }


//        hasSystemFeature(PackageManager.FEATURE_CAMERA) ?? :)
        FloatingActionButton fabCamera = findViewById(R.id.fabCamera);
        if (fabCamera != null) {
            fabCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(NoteCreateActivity.this,
//                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(NoteCreateActivity.this,
//                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION);
//                    }
                    ActivityCompat.requestPermissions(NoteCreateActivity.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            });
        }

        schema = new NoteDBSchema(this);
        if (savedInstanceState == null) {
            noteArrayList = new ArrayList<>();

            noteArrayList.add(new Note("0", "Default title", "Default note",
                    "defaultImagePath", "default email"));
        } else {
            noteArrayList = savedInstanceState.getParcelableArrayList(TASKS_KEY);
        }

        NoteFragment.noteAdapter = new NoteAdapter(this, noteArrayList);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have perms to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {// Received permission result for camera permission.

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA_ACTION);
            } else {
                Toast.makeText(this, "Permissions were not granted.", Toast.LENGTH_SHORT).show();
//                Snackbar.make(mLayout, "Permissions were not granted.", Snackbar.LENGTH_SHORT).show();
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean AddData(String newTitle, String newNote, String newImagePath, String email) {
        boolean insertData = NoteCreateActivity.schema.addData(newTitle, newNote, newImagePath, email);
        if(!insertData){
            Log.d(TAG, "During Data(note) insertion something went wrong.");
            toastMessage("Something went wrong");
            return false;
        }
        Log.d(TAG, "Data (note) insertion was successful.");
        return true;
    }

    private void createNoteTask() {
        String title = etTitle.getText().toString();
        if (title.equals("")) {
            toastMessage("Please enter a title to note entry.");
            return;
        }

        String note = etEntry.getText().toString();
        if(path == null) path = "defaultImagePath";
        if (AddData(title, note, path, email)) {
            String id = NoteCreateActivity.schema.getNoteID(title, note, path, email);
            noteArrayList.add(new Note(id, title, note, path, email));
            NoteFragment.noteAdapter.notifyDataSetChanged();
            if(path.equals("defaultImagePath")) path = null;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TASKS_KEY, noteArrayList);
    }

    /**
     * generify toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            path = getRealPathFromURI_API19(this, uri);
            imageView.setImageURI(uri);

//            Toast.makeText(this, path, Toast.LENGTH_LONG).show();

        } else if (requestCode == REQUEST_CAMERA_ACTION && resultCode == RESULT_OK) {

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.ImageColumns.ORIENTATION},
                    MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
            if(cursor != null && cursor.moveToLast()){
                Uri fileURI = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                path = fileURI.toString();
                cursor.close();
            }
            imageView.setImageBitmap(thumbnail);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {

                if (Build.VERSION.SDK_INT > 20) {
                    //getExternalMediaDirs() added in API 21
                    File extenal[] = context.getExternalMediaDirs();
                    if (extenal.length > 1) {
                        filePath = extenal[1].getAbsolutePath();
                        filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1];
                    }
                } else {
                    filePath = "/storage/" + type + "/" + split[1];
                }
                return filePath;
            }

        } else if (isDownloadsDocument(uri)) {
            // DownloadsProvider
            final String id = DocumentsContract.getDocumentId(uri);
            //final Uri contentUri = ContentUris.withAppendedId(
            // Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    String result = cursor.getString(index);
                    cursor.close();
                    return result;
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        } else if (DocumentsContract.isDocumentUri(context, uri)) {
            // MediaProvider
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String[] ids = wholeID.split(":");
            String id;
            String type;
            if (ids.length > 1) {
                id = ids[1];
                type = ids[0];
            } else {
                id = ids[0];
                type = ids[0];
            }

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{id};
            final String column = "_data";
            final String[] projection = {column};
            Cursor cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            return filePath;
        } else {
            String[] proj = {MediaStore.Audio.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                if (cursor.moveToFirst()) filePath = cursor.getString(column_index);
                cursor.close();
            }


            return filePath;
        }
        return null;
    }

}
