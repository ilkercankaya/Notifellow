package com.notifellow.su.notifellow;

import android.net.Uri;
import android.widget.ImageView;

import java.net.URI;

public class card_user {

    private String userName;
    private String nameSurname;
    private String email;
    private String status;
    private Uri profilePic; // making this variable int for mnow since every drawable etc. can be accessed as int
                            // since i dont know how are we storing them on database.

    //constructor
    public card_user(String userName, String nameSurname, String email, String Status, Uri profilePic){

        this.nameSurname = nameSurname;
        this.email = email;
        this.status = Status;
        this.userName = userName;
        this.profilePic = profilePic;
    }

// Getters and setters
    public String getNameSurname() {
        return this.nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUSREmail() {
        return this.email;
    }

    public void setUSREmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status= status;
    }

    public Uri getProfilePic() {
        return this.profilePic;
    }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    // end of getters and setters
}
