package com.notifellow.su.notifellow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NoteTaskAdapter extends ArrayAdapter<NoteTask> {

    private Activity context;

    NoteTaskAdapter(Activity context, List<NoteTask> taskList) {
        super(context, R.layout.notes_row_layout, taskList);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.notes_row_layout, null);
        }

        final NoteTask task = getItem(position);

        TextView tvTitle = rowView.findViewById(R.id.notes_row_layout_tvTitle);
        TextView tvNote = rowView.findViewById(R.id.notes_row_layout_tvNote);
        ImageView imageView = rowView.findViewById(R.id.notes_row_layout_imagePreview);

        tvTitle.setText(task.title);
        tvNote.setText(task.note);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        Bitmap bitmap = BitmapFactory.decodeFile(task.imagePath, options);
        imageView.setImageBitmap(bitmap);

        return rowView;
    }

}
