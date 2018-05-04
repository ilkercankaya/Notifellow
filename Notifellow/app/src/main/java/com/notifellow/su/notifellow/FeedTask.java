package com.notifellow.su.notifellow;

import android.net.Uri;

public class FeedTask {
    private Task task;
    private String user;
    private Uri profilePic;

    public FeedTask(Task task, String user, Uri profilePic){
        this.task = task;
        this.user = user;
        this.profilePic = profilePic;
    }

    public Task getTask(){
        return task;
    }

    public String getUser(){
        return user;
    }

    public Uri getProfilePic(){
        return profilePic;
    }
}
