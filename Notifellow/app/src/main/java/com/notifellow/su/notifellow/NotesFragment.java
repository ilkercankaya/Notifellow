package com.notifellow.su.notifellow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class NotesFragment extends Fragment {

    private FloatingActionButton fab;

// TODO: IMPLEMENT NOTES ACTIVITY AS FRAGMENT HERE
// TODO: INSTEAD OF ADD NEW NOTES BUTTON, USE THE fab I PROVIDED YOU

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fabAddNote);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getActivity(), "Add Note!", Toast.LENGTH_LONG).show();


                }
            });
        }
        return view;
    }

}
