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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.notifellow.su.notifellow.NotePathUtils.isDownloadsDocument;
import static com.notifellow.su.notifellow.NotePathUtils.isExternalStorageDocument;


public class NoteCreateActivity extends AppCompatActivity {

    private static final String TASKS_KEY = "com.notifellow.su.notifellow.tasks_key";
    private static final String TAG = NoteCreateActivity.class.getSimpleName();

    Uri uri;

    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_CODE_GALLERY = 999;
    private EditText etTitle;
    private EditText etEntry;
    private ImageView imageView;
    private ArrayList<Note> noteArrayList;
    private String path;
    private NoteAdapter noteAdapter;


    private String email;
    FloatingActionButton fabSet, fabImage, fabCamera;
    static NotesDBSchema schema;


    public static void updateEmailAddressesNotesDB(String oldEmail, String value){
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

        fabSet = findViewById(R.id.fabSet);
        if (fabSet != null) {
            fabSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNoteTask();
                    finish(); //navigates to schedule.
                }
            });
        }

        fabImage = findViewById(R.id.fabImage);
        if (fabImage != null) {
            fabImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(NoteCreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
                }
            });
        }


//        hasSystemFeature(PackageManager.FEATURE_CAMERA) ?? :)
        fabCamera = findViewById(R.id.fabCamera);
        if (fabCamera != null) {
            fabCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });
        }

        schema = new NotesDBSchema(this);
        if (savedInstanceState == null) {
            noteArrayList = new ArrayList<>();

            noteArrayList.add(new Note("0", "Default title", "Default note", String.valueOf(R.drawable.ic_launcher_background), "default email"));
        } else {
            noteArrayList = savedInstanceState.getParcelableArrayList(TASKS_KEY);
        }

        noteAdapter = new NoteAdapter(this, noteArrayList);
    }

    private boolean AddData(String newTitle, String newNote, String newImagePath, String email) {
        boolean insertData = NoteCreateActivity.schema.addData(newTitle, newNote, newImagePath, email);
        if (insertData) {
            toastMessage("Data Successfully Inserted!");
            return true;
        } else {
            toastMessage("Something went wrong");
            return false;
        }
    }

    private void createNoteTask() {
        String title = etTitle.getText().toString();
        if (title.equals("")) {
            showToast("Please enter a title to note entry.");
            return;
        }

        String note = etEntry.getText().toString();


        if (AddData(title, note, path, email)) {
            Cursor cursor = NoteCreateActivity.schema.getItemID(title);
            cursor.moveToFirst();
            String id = cursor.getString(0);
            noteArrayList.add(new Note(id, title, note, path, email));
            noteAdapter.notifyDataSetChanged();
        }

    }


    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TASKS_KEY, noteArrayList);
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            path = getRealPathFromURI_API19(this, uri);
            imageView.setImageURI(uri);

//            Toast.makeText(this, path, Toast.LENGTH_LONG).show();

        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
            path = storeImage(photo);
            imageView.setImageBitmap(photo);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param image
     * @return string location of Photo taken.
     */
    private String storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return pictureFile.getAbsolutePath();
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have perms to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
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
