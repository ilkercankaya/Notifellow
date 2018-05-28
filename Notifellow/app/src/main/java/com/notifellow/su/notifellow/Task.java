package com.notifellow.su.notifellow;

public class Task implements Comparable<Task>{
    private String title;
    private String startTime;
    private String endTime;
    private String remindTime;
    private String location;
    private String wifi;
    private String note;
    private String id;
    private String global;
    private String hasJoined;

    public Task(String id){
        this.id = id;
    }

    public Task(String id, String title, String startTime, String endTime, String remindTime, String location, String hasJoined){
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remindTime = remindTime;
        this.location = location;
        this.hasJoined = hasJoined;
    }

    public Task(String id, String title, String startTime, String endTime, String remindTime, String location, String wifi, String note, String global){
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remindTime = remindTime;
        this.location = location;
        this.wifi = wifi;
        this.note = note;
        this.global = global;
    }

    public String getID(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getStartTime(){
        return startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public String getRemindTime(){
        return remindTime;
    }

    public String getLocation(){
        return location;
    }

    public String getWifi(){
        return wifi;
    }

    public String getNote(){
        return note;
    }

    public String getGlobal(){return global;}

    public String getHasJoined(){return hasJoined;}

    public void setHasJoined(String s){
        this.hasJoined = s;
    }

    public int compareTo(Task other){
        return this.getStartTime().compareTo(other.getStartTime());
    }

    public boolean equals(Object other){
        if(other == null)
            return false;
        if(other == this)
            return true;

        if(!(other instanceof Task))
            return false;

        Task o = (Task) other; // necessary ?
        return id.equals(((Task) other).getID());
    }
}
