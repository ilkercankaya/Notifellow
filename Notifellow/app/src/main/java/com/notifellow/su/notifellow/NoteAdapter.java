package com.notifellow.su.notifellow;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    Dialog noteInfoDialog;
    ImageView imageView;
    private TextView titleTextView;
    private TextView noteTextView;

    NoteAdapter(Activity context, List<Note> taskList) {
        super(context, R.layout.note_row, taskList);
    }

    public void rowOnClick(Note note) {
        noteInfoDialog = new Dialog(getContext());
        noteInfoDialog.setContentView(R.layout.note_row_clicked);
//        noteInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES
        titleTextView = noteInfoDialog.findViewById(R.id.note_row_clicked_title);
        noteTextView = noteInfoDialog.findViewById(R.id.note_row_clicked_describe);
        imageView = noteInfoDialog.findViewById(R.id.note_row_clicked_image);
        Button btnDelete = noteInfoDialog.findViewById(R.id.notes_bar_edit_btnDelete);
        Button btnImage = noteInfoDialog.findViewById(R.id.notes_bar_edit_btnImage);
        Button btnSave = noteInfoDialog.findViewById(R.id.notes_bar_edit_btnSave);
        final String id = note.id;
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesListActivity.schema.deleteByID(id);
                titleTextView.setText("");
                noteTextView.setText("");
                imageView.setImageResource(R.drawable.ic_launcher_background);
                imageView.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        });

        titleTextView.setText(note.title);
        noteTextView.setText(note.note);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(note.imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);

        noteInfoDialog.show();
    }

//    private void DeleteNote(View v){
//        NotesListActivity.mDatabaseHelper.deleteByID(String.valueOf(selectedID));
//        titleTextView.setText("");
//        noteTextView.setText("");
//        imageView.setImageResource(R.drawable.ic_launcher_background);
//        imageView.setVisibility(View.GONE);
//
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

//        View rowView = convertView;
//        if (rowView == null) {
//            LayoutInflater inflater = this.getLayoutInflater();
//            rowView = inflater.inflate(R.layout.fragment_notes, null);
//        }
//
//        final Note task = getItem(position);
//
//        TextView tvTitle = rowView.findViewById(R.id.note_row_title);
//        TextView tvNote = rowView.findViewById(R.id.note_row_description);
//        ImageView imageView = rowView.findViewById(R.id.note_row_image);
//
//        tvTitle.setText(task.title);
//        tvNote.setText(task.note);
//
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(task.imagePath, options);
//        imageView.setImageBitmap(bitmap);
//
//        return rowView;
        View rowView = convertView;
        MyViewHolder holder;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            rowView = inflater.inflate(R.layout.note_row, null);
        }

        if (rowView.getTag() == null) {
            holder = new MyViewHolder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (MyViewHolder) rowView.getTag();
        }


        holder.titleTextView.setText(getItem(position).title);

        holder.descriptionTextView.setText(getItem(position).note);


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(getItem(position).imagePath, bmOptions);

        holder.imageView.setImageBitmap(bitmap);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowOnClick(getItem(position));
            }
        });

        holder.cancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: IMPLEMENT DELETE FUNCTION!!!!!
//                Main.cancelAlarm(Integer.parseInt(getItem(position).()) + 1);
//                NotesListActivity.DeleteNote();
                Toast.makeText(NoteAdapter.this.getContext(), "DELETE CLICKED.", Toast.LENGTH_SHORT).show();
//                DeleteNote(view);
//                NotesFragment.noteList.remove(position);
                NotesFragment.noteAdapter.notifyDataSetChanged();
            }
        });

        return rowView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView imageView;
        private ImageView cancelTask;

        public MyViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.note_row_title);
            descriptionTextView = itemView.findViewById(R.id.note_row_description);
            imageView = itemView.findViewById(R.id.note_row_image);
            cancelTask = itemView.findViewById(R.id.note_row_delete_button);
        }
    }

}
