package com.notifellow.su.notifellow;

import android.net.Uri;

/**
 * Created by egealpay on 29.05.2018.
 */

public class EventRequest {
    private String userName;
    private String email;
    private Uri profilePicture;
    private String eventTitle;
    private String eventID;
    private String startDate;
    private String endDate;

    public EventRequest(String userName, String email, Uri profilePicture, String eventTitle, String eventID, String startDate, String endDate){
        this.userName = userName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.eventTitle = eventTitle;
        this.eventID = eventID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getUserName(){
        return userName;
    }

    public String getEmail(){
        return email;
    }

    public Uri getProfilePicture(){
        return profilePicture;
    }

    public String getEventTitle(){
        return eventTitle;
    }

    public String getEventID(){
        return eventID;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

}
