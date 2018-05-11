package com.notifellow.su.notifellow;

import android.net.Uri;

/**
 * Created by egealpay on 11.05.2018.
 */

public class Comment {
    private String email;
    private String userName;
    private Uri profilePicture;
    private String comment;
    private String timeCommented;

    public Comment(String email, Uri profilePicture, String userName, String comment, String timeCommented){
        this.email = email;
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.comment = comment;
        this.timeCommented = timeCommented;
    }

    public String getUserName(){
        return userName;
    }

    public Uri getProfilePicture(){
        return profilePicture;
    }

    public String getComment(){
        return comment;
    }

    public String getEmail(){
        return email;
    }

    public String getTimeCommented(){
        return timeCommented;
    }
}
