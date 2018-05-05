package com.notifellow.su.notifellow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import java.text.MessageFormat;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {


    Activity context;

    private Dialog noteInfoDialog;
    protected static ImageView imageView;
    private TextView tv_tittle;
    private TextView tv_note;
    protected static String path;
    String id;

    NoteAdapter(Activity context, List<Note> taskList) {
        super(context, R.layout.note_row, taskList);
        this.context = context;
    }

    private void rowOnClick(Note note) {
        noteInfoDialog = new Dialog(getContext());
        noteInfoDialog.setContentView(R.layout.note_row_clicked);
//        noteInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // DO NOT TOUCH, DESIGN ISSUES
        tv_tittle = noteInfoDialog.findViewById(R.id.note_row_clicked_title);
        tv_note = noteInfoDialog.findViewById(R.id.note_row_clicked_describe);
        imageView = noteInfoDialog.findViewById(R.id.note_row_clicked_image);
        Button btnImage = noteInfoDialog.findViewById(R.id.notes_bar_edit_on_clicked_btnImage);
        Button btnSave = noteInfoDialog.findViewById(R.id.notes_bar_edit_on_clicked_btnSave);
        id = note.getId();

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteAdapter.this.getContext(), GalleryActivity.class);
                NoteAdapter.this.getContext().startActivity(intent);
                imageView.setVisibility(View.VISIBLE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tv_tittle.getText().toString();
                if (title.equals("")) {
                    toastMessage("Please enter a title to note entry.");
                    return;
                }
                String note = tv_note.getText().toString();
                NoteCreateActivity.schema.updateAllButEmailAndId(Integer.parseInt(id),title, note, path);
                Toast.makeText(getContext(), "Note Updated. Please refresh the page to see changes.",
                        Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
                noteInfoDialog.dismiss();
            }
        });

        Cursor cursor = NoteCreateActivity.schema.getItemImagePath(id);
        cursor.moveToFirst();
        path = cursor.getString(0);

        tv_tittle.setText(note.getTitle());
        tv_note.setText(note.getNote());

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath(), bmOptions);
        imageView.setImageBitmap(bitmap);
        if(bitmap == null){
            imageView.setVisibility(View.INVISIBLE);
        }

        noteInfoDialog.show();
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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

        String title = getItem(position).getTitle();
        if (title.length() > 30) {
            holder.titleTextView.setText(MessageFormat.format("{0}...", title.substring(0, 27)));
        } else {
            holder.titleTextView.setText(getItem(position).getTitle());
        }

//        holder.titleTextView.setText(getItem(position).getTitle());


        String note = getItem(position).getNote();
        if (note.length() > 70) {
            holder.descriptionTextView.setText(MessageFormat.format("{0}...", note.substring(0, 67)));
        } else {
            holder.descriptionTextView.setText(getItem(position).getNote());
        }


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(getItem(position).getImagePath(), bmOptions);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete this note?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int code = Integer.parseInt(getItem(position).getId());
                        NoteCreateActivity.schema.deleteByID(String.valueOf(code));
                        NotesFragment.noteList.remove(position);
                        NotesFragment.noteAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "" + "" + "Task deleted!", Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return rowView;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView imageView;
        private ImageView cancelTask;

        MyViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.note_row_title);
            descriptionTextView = itemView.findViewById(R.id.note_row_description);
            imageView = itemView.findViewById(R.id.note_row_image);
            cancelTask = itemView.findViewById(R.id.note_row_delete_button);
        }
    }

}
