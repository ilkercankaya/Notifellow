package com.notifellow.su.notifellow;

import android.net.Uri;

import java.util.List;

public class FeedTask implements Comparable<FeedTask>{
    private Task task;
    private String userName;
    private String email;
    private Uri profilePic;
    private String participants;
    private String comments;

    public FeedTask(Task task, String userName, String email, Uri profilePic, String participants, String comments){
        this.task = task;
        this.userName = userName;
        this.email = email;
        this.profilePic = profilePic;
        this.participants = participants;
        this.comments = comments;
    }

    public Task getTask(){
        return task;
    }

    public String getUserName(){
        return userName;
    }

    public Uri getProfilePic(){
        return profilePic;
    }

    public String getParticipants() {
        return participants;
    }

    public int compareTo(FeedTask other){
        return this.task.getStartTime().compareTo(other.task.getStartTime());
    }
}
