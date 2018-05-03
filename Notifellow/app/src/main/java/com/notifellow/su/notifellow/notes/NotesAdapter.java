//package com.notifellow.su.notifellow.notes;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.notifellow.su.notifellow.R;
//
//import java.util.ArrayList;
//
///**
// * Created by egealpay on 25.03.2018.
// */
//
//public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{
//
//    private ArrayList<String> arrayList;
//    private LayoutInflater mInflater;
//    private ItemClickListener mClickListener;
//
//    NotesAdapter(Context context, ArrayList<String> arrayList) {
//        this.mInflater = LayoutInflater.from(context);
//        this.arrayList = arrayList;
//    }
//
//    @Override
//    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.note_recyclerview_row, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(NotesAdapter.ViewHolder holder, int position) {
//        String animal = arrayList.get(position);
//        holder.titleTextView.setText(animal);
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrayList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        TextView titleTextView;
//
//        ViewHolder(View itemView){
//            super(itemView);
//            titleTextView = itemView.findViewById(R.id.noteTitle);
//            itemView.setOnClickListener(this);
//        }
//
//        public void onClick(View view){
//            if(mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
//        }
//    }
//
//    String getItem(int id) {
//        return arrayList.get(id);
//    }
//
//    // allows clicks events to be caught
//    void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
//
//    // parent activity will implement this method to respond to click events
//    public interface ItemClickListener {
//        void onItemClick(View view, int position);
//    }
//}
