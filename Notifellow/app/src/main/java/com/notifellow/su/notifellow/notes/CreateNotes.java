//package com.notifellow.su.notifellow.notes;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.notifellow.su.notifellow.R;
//import com.notifellow.su.notifellow.notes.camera.CameraActivity;
//
//public class CreateNotes extends AppCompatActivity {
//
//    EditText textTitle;
//    EditText textNote;
//    private ImageView imageView;
////    private Button btnSave;
//    private static final int PICK_IMAGE = 100;
//    TextView textError;
//    Uri Selected_Image_Uri;
//    private static final int CAMERA_REQUEST = 1888;
//    private SharedPreferences shared;
//
//
//    public void saveNoteFunc(View view) {
//        String title = textTitle.getText().toString();
//        String note = textNote.getText().toString();
//
//        shared = getSharedPreferences("shared", MODE_PRIVATE);
//        String email = shared.getString("email", null);//GET EMAIL FROM SHARED
//
//
//        long tempNum = NotesMainActivity.notesSchema.insertData(email, title, note);
////        long tempNum = NoteCreateActivity.notesSchema.insertData(email, title, note, Selected_Image_Uri);
//
//        if (tempNum == -1) { //ERROR
//            textError.setText("Note Exists!");
//            textError.setTextColor(Color.RED);
//            textError.setVisibility(View.VISIBLE);
//        } else {
//            textError.setText("Note Saved!");
//            textError.setTextColor(Color.parseColor("#43A047"));
//            textError.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.note_create_note);
//
//        textTitle = findViewById(R.id.textTitle);
//        textNote = findViewById(R.id.textNote);
//        textError = findViewById(R.id.textError);
//        imageView = findViewById(R.id.imageView);
//        Button btnSelectImage = findViewById(R.id.btnSelectImage);
//        Button btnCamera = findViewById(R.id.btnPhotoShot);
//        Button btnSave = findViewById(R.id.btnSaveNote);
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveNoteFunc(v);
//            }
//        });
//        btnCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CreateNotes.this, CameraActivity.class);
//                startActivity(intent);
////                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            }
//        });
//
//        btnSelectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("image/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.
//                        Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");
//
//                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
//
//                startActivityForResult(chooserIntent, PICK_IMAGE);
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
//            if (data == null) {
//                //Display an error
//                Toast.makeText(this, "Error happened while loading the image from gallery", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Selected_Image_Uri = data.getData();
//            imageView.setImageURI(Selected_Image_Uri);
//        }
//        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
//        }
//    }
//
//}
