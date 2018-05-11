package com.notifellow.su.notifellow;

import android.net.Uri;

/**
 * Created by egealpay on 11.05.2018.
 */

public class Comment {
    private String userName;
    private Uri profilePicture;
    private String comment;

    public Comment(Uri profilePicture, String userName, String comment){
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.comment = comment;
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
}
