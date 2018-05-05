package com.notifellow.su.notifellow;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Comparable<Note>, Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private String id;
    private String title;
    private String note;
    private String imagePath;
    private String email;

    public Note(String id, String title, String note, String imagePath, String email) {
        this.setId(id);
        this.setTitle(title);
        this.setNote(note);
        this.setImagePath(imagePath);
        this.setEmail(email);
    }

    public Note(Note other) {
        this.setId(getId());
        this.setTitle(other.getTitle());
        this.setNote(other.getNote());
        this.setImagePath(other.getImagePath());
        this.setEmail(getEmail());
    }

    public Note(Parcel in) {
        this.setId(in.readString());
        this.setTitle(in.readString());
        this.setNote(in.readString());
        this.setImagePath(in.readString());
        this.setEmail(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getTitle());
        dest.writeString(getNote());
        dest.writeString(getImagePath());
        dest.writeString(getEmail());
    }

    @Override
    public int compareTo(Note another) { // i know.. this makes no sense but compiler cries otherwise
        return getId().compareTo(another.getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
