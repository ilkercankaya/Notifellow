package com.notifellow.su.notifellow.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.notifellow.su.notifellow.R;

public class EditNotes extends AppCompatActivity {

    EditText noteText;
    TextView confirmation;
    String title;
    Button btnUpdate, btnEditImage, btnDelete;
    private SharedPreferences shared;

    public void updateNoteFunc(View view){
        String note = noteText.getText().toString();

        shared = getSharedPreferences("shared", MODE_PRIVATE);
        String email = shared.getString("email", null);//GET EMAIL FROM SHARED

        NotesMainActivity.notesSchema.updateData(email, title, note);

        confirmation.setText("Note Saved!");
        confirmation.setTextColor(Color.GREEN);
        confirmation.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_note);

        btnEditImage = findViewById(R.id.btnChangeImage);
        btnDelete = findViewById(R.id.btnDeleteNote);
        btnUpdate = findViewById(R.id.btnUpdateNotes);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNoteFunc(v);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteFunction(v);
            }
        });

        btnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImageFunc(v);
            }
        });

        noteText = findViewById(R.id.noteText);
        confirmation = findViewById(R.id.textConfirmation);

        Intent myIntent = getIntent();
        title = myIntent.getStringExtra("title");

        String note = NotesMainActivity.notesSchema.getNote(title);
        noteText.setText(note);

        noteText.setSelection(noteText.length());
    }

    private void editImageFunc(View v) {

    }
    
    private void DeleteFunction(View v) {

    }
}


