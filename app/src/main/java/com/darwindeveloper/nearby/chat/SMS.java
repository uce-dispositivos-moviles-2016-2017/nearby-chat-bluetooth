package com.darwindeveloper.nearby.chat;

/**
 * Created by Darwin Morocho on 31/12/2016.
 */

public class SMS {

    private boolean is_selected;
    private long ID;
    private String from, message, date_time;

    public SMS(long ID, String from, String message, String date_time) {
        this.ID = ID;
        this.from = from;
        this.message = message;
        this.date_time = date_time;
    }

    public long getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public boolean is_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
