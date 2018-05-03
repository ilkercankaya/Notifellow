package com.notifellow.su.notifellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class NotesFragment extends Fragment {

    private static final String TASKS_KEY = "com.notifellow.su.notifellow.tasks_key";

    private FloatingActionButton fab;
    private ListView taskListView;
    static List<Note> noteList;
    static NoteAdapter noteAdapter;
    private SharedPreferences shared;

// TODO: IMPLEMENT NOTES ACTIVITY AS FRAGMENT HERE
// TODO: INSTEAD OF ADD NEW NOTES BUTTON, USE THE fab I PROVIDED YOU

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        fab = view.findViewById(R.id.fabAddNote);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Add Note!", Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(NotesFragment.this.getActivity(), NoteCreateActivity.class);
                    myIntent.putExtra("email",shared.getString("email", "null"));
                    NotesFragment.this.startActivity(myIntent);
                }
            });
        }

        if (savedInstanceState == null) {
            NoteCreateActivity.schema = new NotesDBSchema(getContext());
            noteList = new ArrayList<>();
            noteList.add(new Note(null, null, null,null,null));
        } else {
            noteList = savedInstanceState.getParcelableArrayList(TASKS_KEY);
        }

        taskListView = view.findViewById(R.id.listView_Notes);
        int rowCount = 0;
        noteList = new ArrayList<>();

        Cursor allTasks = NoteCreateActivity.schema.getData();

        int idCol = allTasks.getColumnIndex("ID");
        int titleCol = allTasks.getColumnIndex("tittle");
        int noteCol = allTasks.getColumnIndex("notes");
        int imageCol = allTasks.getColumnIndex("image_path");
        int emailCol = allTasks.getColumnIndex("email");

        allTasks.moveToFirst();
        rowCount = allTasks.getCount();
        int tempCount = rowCount;

        shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
        String email = shared.getString("email", "null");//GET EMAIL FROM SHARED

        if (allTasks != null && (tempCount > 0)) {

            do {
                if (email.equals(allTasks.getString(emailCol))) {
                    String id = allTasks.getString(idCol);
                    String title = allTasks.getString(titleCol);
                    String note = allTasks.getString(noteCol);
                    String image = allTasks.getString(imageCol);

//                    noteList.add(new Task(id, title, startDate + "\t\t" + startTime, endTime + "\t\t" + endDate, remindTime + "\t\t" + remindDate, location, wifi, note));
                    noteList.add(new Note(id, title, note, image, email));
                }
                tempCount--;
            } while (allTasks.moveToNext());
        }

        Collections.sort(noteList);

        noteAdapter = new NoteAdapter(getActivity(), noteList);
        taskListView.setAdapter(noteAdapter);

        return view;
    }

}
