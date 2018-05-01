package com.notifellow.su.notifellow;

import android.media.Image;
import android.net.Uri;

public class Friends {
    private String nameSurname;
    private String userName;
    private String email;
    private Uri profilePicture; //CHECK THIS!!!!

    public Friends(String nameSurname, String userName, String email, Uri profilePicture){
        this.nameSurname = nameSurname;
        this.userName = userName;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public String getUserName(){
        return userName;
    }

    public String getNameSurname(){
        return nameSurname;
    }

    public String getEmail(){
        return email;
    }

    public Uri getProfilePicture(){
        return profilePicture;
    }
}
