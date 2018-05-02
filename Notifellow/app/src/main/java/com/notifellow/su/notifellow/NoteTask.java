package com.notifellow.su.notifellow;

import android.os.Parcel;
import android.os.Parcelable;

public class NoteTask implements Comparable<NoteTask>, Parcelable {

    public static final Creator CREATOR = new Creator() {
        public NoteTask createFromParcel(Parcel in) {
            return new NoteTask(in);
        }

        public NoteTask[] newArray(int size) {
            return new NoteTask[size];
        }
    };

    //    String id;
    String title;
    String note;
    String imagePath;

    public NoteTask() {
        this("", "", "");
    }

    //    public NoteTask(String id, String title, String note, String imagePath) {
    NoteTask(String title, String note, String imagePath) {
//        this.id = id;
        this.title = title;
        this.note = note;
        this.imagePath = imagePath;
    }

    public NoteTask(NoteTask other) {
//        this.id = id;
        this.title = other.title;
        this.note = other.note;
        this.imagePath = other.imagePath;
    }

    public NoteTask(Parcel in) {
//        this.id = in.readString();
        this.title = in.readString();
        this.note = in.readString();
        this.imagePath = in.readString(); // TODO fix this readString
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(note);
        dest.writeString(imagePath);
    }

    @Override
    public int compareTo(NoteTask another) { // i know.. this makes no sense but compiler cries otherwise
        return title.compareTo(another.title);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
