package com.notifellow.su.notifellow.notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.notifellow.su.notifellow.R;

import java.util.ArrayList;


public class NotesMainActivity extends AppCompatActivity implements NotesAdapter.ItemClickListener{

    public static Schema notesSchema;
    RecyclerView recyclerView;
    ArrayList<String> arrayList;
    NotesAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Button btnGetNote, btnCreateNote;

    public static void updateEmailAddressesNotesDB(String oldEmail, String value){
        notesSchema.updateEmailAddresses(oldEmail, value);
    }


    public void getNotesFunc(View view) {
        long rowCount = notesSchema.getRowCount();

        if (rowCount > 0) {
            recyclerView = findViewById(R.id.notesList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            arrayList = new ArrayList<>();

            Cursor cursor = notesSchema.getAllFields();
            int titleID = cursor.getColumnIndex("title");

            cursor.moveToFirst();

            if (cursor != null & (cursor.getCount() > 0)) {
                do {
                    String title = cursor.getString(titleID);
                    arrayList.add(title);
                }
                while (cursor.moveToNext());
            }

            adapter = new NotesAdapter(this, arrayList);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        else{
            Toast.makeText(this, "You don't have any notes!", Toast.LENGTH_LONG).show();
        }
    }

    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NotesMainActivity.this, EditNotes.class);
        intent.putExtra("title", adapter.getItem(position));
        startActivity(intent);
    }


    public void createNoteFunc(View view){
        Intent intent = new Intent(NotesMainActivity.this, CreateNotes.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_main_activity);
        btnCreateNote = findViewById(R.id.btnCreateNotes);
        btnGetNote = findViewById(R.id.btnGetNotes);

        btnGetNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotesFunc(v);
            }
        });

        btnCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNoteFunc(v);
            }
        });
    }
}

