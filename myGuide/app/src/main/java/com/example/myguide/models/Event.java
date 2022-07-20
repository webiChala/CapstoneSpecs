package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_TITLE = "Title";
    public static final String KEY_STARTDATE = "StartDate";
    public static final String KEY_ENDDATE = "EndDate";
    public static final String KEY_ADDEDUSER= "AddedUser";
    public static final String KEY_LOCATION = "Location";
    public static final String KEY_DETAIL = "Detail";
    public static final String KEY_REPETITION = "Repetition";
    public static final String KEY_EVENTDATE = "EventDate";
    public static final String KEY_USER = "User";

    public Event() {

    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }
    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public Date getStartDate() {
        return getDate(KEY_STARTDATE);
    }
    public void setStartDate(Date startDate) {
        put(KEY_STARTDATE, startDate);
    }

    public Date getEndDate() {
        return getDate(KEY_ENDDATE);
    }
    public void setEndDate(Date endDate) {
        put(KEY_ENDDATE, endDate);
    }

    public User getAddedUser() {
        return (User) getParseUser(KEY_ADDEDUSER);
    }
    public void setAddedUser(User addedUser) {
        put(KEY_ADDEDUSER, addedUser);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }
    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public String getDetail() {
        return getString(KEY_DETAIL);
    }
    public void setDetail(String detail) {
        put(KEY_DETAIL, detail);
    }

    public String getRepetition() {
        return getString(KEY_REPETITION);
    }
    public void setRepetition(String repetition) {
        put(KEY_REPETITION, repetition);
    }

    public Date getEventDate() {
        return getDate(KEY_EVENTDATE);
    }
    public void setEventDate(Date eventDate) {
        put(KEY_EVENTDATE, eventDate);
    }

    public User getUser() {
        return (User) getParseUser(KEY_USER);
    }
    public void setUser(User user) {
        put(KEY_USER, user);
    }

}
