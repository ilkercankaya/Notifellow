package com.notifellow.su.notifellow;

import android.net.Uri;

import java.util.List;

public class FeedTask {
    private Task task;
    private String userName;
    private Uri profilePic;
    private List<String> participants;

    public FeedTask(Task task, String userName, Uri profilePic, List<String> participants){
        this.task = task;
        this.userName = userName;
        this.profilePic = profilePic;
        this.participants = participants;
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

    public List<String> getParticipants() {
        return participants;
    }
}
