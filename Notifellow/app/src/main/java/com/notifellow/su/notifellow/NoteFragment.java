package com.notifellow.su.notifellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class NoteFragment extends Fragment {

    private static final String TASKS_KEY = "com.notifellow.su.notifellow.notes_key";

    static List<Note> noteList;
    protected static NoteAdapter noteAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences shared;
    private int rowCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fabAddNote);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(NoteFragment.this.getActivity(), NoteCreateActivity.class);
                    myIntent.putExtra("email", shared.getString("email", "null"));
                    NoteFragment.this.startActivity(myIntent);
                }
            });
        }

        if (savedInstanceState == null) {
            NoteCreateActivity.schema = new NoteDBSchema(getContext());
            noteList = new ArrayList<>();
            noteList.add(new Note(null, null, null, null, null));
        } else {
            noteList = savedInstanceState.getParcelableArrayList(TASKS_KEY);
        }

        ListView noteListView = view.findViewById(R.id.listView_Notes);
        rowCount = 0;
        noteList = new ArrayList<>();

        Cursor allNotes = NoteCreateActivity.schema.getAllNotes();

        int idCol = allNotes.getColumnIndex("ID");
        int titleCol = allNotes.getColumnIndex("title");
        int noteCol = allNotes.getColumnIndex("note");
        int imageCol = allNotes.getColumnIndex("image_path");
        int emailCol = allNotes.getColumnIndex("email");

        allNotes.moveToFirst();
        rowCount = allNotes.getCount();
        int tempCount = rowCount;

        shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
        String email = shared.getString("email", "null");//GET EMAIL FROM SHARED

        if (allNotes != null && (tempCount > 0)) {

            do {
                if (email.equals(allNotes.getString(emailCol))) {
                    String id = allNotes.getString(idCol);
                    String title = allNotes.getString(titleCol);
                    String note = allNotes.getString(noteCol);
                    String image = allNotes.getString(imageCol);

                    noteList.add(new Note(id, title, note, image, email));
                }
                tempCount--;
            } while (allNotes.moveToNext());
        }
        allNotes.close();
        Collections.sort(noteList);

        noteAdapter = new NoteAdapter(getActivity(), noteList);
        noteListView.setAdapter(noteAdapter);


        //// SWIPE TO REFRESH ////
        swipeRefreshLayout = view.findViewById(R.id.notesLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_3, R.color.refresh_progress_3, R.color.refresh_progress_3); //CHANGE COLOR SCHEME

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (rowCount != 0) {
                    noteAdapter.notifyDataSetChanged(); // REFRESH THE LIST (I GUESS :P)
                    // This is required, otherwise it will crash the program.
                }
                swipeRefreshLayout.setRefreshing(false); // STOP ANIMATION
            }
        });
        //// END OF SWIPE TO REFRESH ///
        return view;
    }
    
}
