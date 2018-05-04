package com.notifellow.su.notifellow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by berk aktug on 27/05/2017.
 */

public class NotesEditActivity extends AppCompatActivity {

    private static final String TAG = NotesEditActivity.class.getSimpleName();

//    NotesDBSchema schema;
    final int REQUEST_CODE_GALLERY = 999;
    private Uri uri;
    private String selectedTitle;
    private String selectedNote;
    private String selectedImagePath;

    EditText etTitle;
    EditText etNote;
    ImageView imageView;

    private int selectedID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notes_edit_data_layout);

        Button btnSave = findViewById(R.id.notes_bar_edit_btnSave);
        Button btnImage = findViewById(R.id.notes_bar_edit_btnImage);
        Button btnDelete = findViewById(R.id.notes_bar_edit_btnDelete);

        etTitle = findViewById(R.id.note_row_clicked_title);
        etNote = findViewById(R.id.notes_edit_data_layout_editable_note);
        imageView = findViewById(R.id.notes_edit_data_layout_editable_image);

//        schema = new NotesDBSchema(this);

        //get the intent extra from the NotesListActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id", -1); //NOTE: -1 is just the default value

        //now get the title we passed as an extra
        selectedTitle = receivedIntent.getStringExtra("title");

        //note from extra
        selectedNote = receivedIntent.getStringExtra("note");

        //imagePath from extra
        selectedImagePath = receivedIntent.getStringExtra("image_path");

        //set the text to show the current selected name
        etTitle.setText(selectedTitle);
        etNote.setText(selectedNote);



//        Bitmap bitmap = BitmapFactory.decodeResource();
        imageView.setImageURI(Uri.parse(selectedImagePath));



//        imageView.setImageBitmap((selectedImagePath));

        // Update item
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updated_title = etTitle.getText().toString();
                String updated_note = etNote.getText().toString();
                // I KNOW WHAT I AM DOING. DO NOT QUESTION ME YOU FILTHY PEASANT!!
                String updated_image_path = "NA";
                if (uri != null) updated_image_path = uri.toString();
                else if (!selectedImagePath.equals("NA")) updated_image_path = selectedImagePath;


                if (!updated_title.equals("")) {
//                    schema.updateAllFields(selectedID, updated_title, updated_note, updated_image_path);
                    finish();
                    //TODO fix the update and delete functions in databaseHelper.
                } else {
                    toastMessage("You must enter a name");
                }
            }
        });

        // Delete item
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotesListActivity.schema.deleteByID(String.valueOf(selectedID));
                etTitle.setText("");
                etNote.setText("");
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                toastMessage("removed from database");
                finish();
            }
        });

        // Update Image
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(NotesEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

