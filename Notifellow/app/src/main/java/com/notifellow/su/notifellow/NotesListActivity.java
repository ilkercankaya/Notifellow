package com.notifellow.su.notifellow;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by berk aktug on 27/05/2017.
 */

public class NotesListActivity extends AppCompatActivity {

    private static final String TAG = NotesListActivity.class.getSimpleName();
    private static final String TASKS_KEY = "com.notifellow.su.notifellow.tasks_key";

    static NotesDBSchema mDatabaseHelper;

    ArrayList<Note> taskList;

    public static void updateEmailAddressesNotesDB(String oldEmail, String value){
        mDatabaseHelper.updateEmailAddresses(oldEmail, value);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notes);
        ListView mListView = findViewById(R.id.listView_Notes);
        mDatabaseHelper = new NotesDBSchema(this);

        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();


        if (savedInstanceState == null) taskList = new ArrayList<>();
        else taskList = savedInstanceState.getParcelableArrayList(TASKS_KEY);

        //get the value from the database in columns then add it to the ArrayList
        while (data.moveToNext()) {
            taskList.add(new Note(data.getString(0), data.getString(1), data.getString(2), data.getString(3)));
        }

        //create the list adapter and set the adapter
        ListAdapter adapter = new NoteAdapter(this,taskList);
        mListView.setAdapter(adapter);


        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = taskList.get(i).title;
                String note = taskList.get(i).note;
                String imagePath= taskList.get(i).imagePath;

                Log.d(TAG, "onItemClick: You Clicked on " + title);

                Cursor data = mDatabaseHelper.getItemID(title); //get the id associated with that name

                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if (itemID > -1) {
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);

                    Intent editScreenIntent = new Intent(
                            NotesListActivity.this, NotesEditActivity.class);

                    editScreenIntent.putExtra("id", itemID);
                    editScreenIntent.putExtra("title", title);
                    editScreenIntent.putExtra("note", note);
                    editScreenIntent.putExtra("image_path", imagePath);

                    startActivity(editScreenIntent);
                } else {
                    toastMessage("No ID associated with that title");
                }
            }
        });
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
