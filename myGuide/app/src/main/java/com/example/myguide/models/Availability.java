package com.example.myguide.models;

import android.content.Intent;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Availability")
public class Availability extends ParseObject {
    public static final String KEY_TITLE = "Title";
    public static final String KEY_AVAILABLE = "Available";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE= "minute";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_COLOR = "color";
    public static final String KEY_USER = "User";
    public static final String KEY_WEEKDAY = "WeekDay";


    public Availability() {

    }

    public String getTitle() {return getString(KEY_TITLE);}
    public void setTitle(String title) {put(KEY_TITLE, title);}

    public boolean getAvailability() {return getBoolean(KEY_AVAILABLE);}
    public void setAvailability(boolean availabile) {put(KEY_AVAILABLE, availabile);}

    public Number getHour() {return getNumber(KEY_HOUR);}
    public void setHour(Number hour) {put(KEY_HOUR, hour);}

    public Number getMinute() {return getNumber(KEY_MINUTE);}
    public void setMinute(Number minute) {put(KEY_MINUTE, minute);}

    public Number getDuration() {return getNumber(KEY_DURATION);}
    public void setDuration(Number duration) {put(KEY_DURATION, duration);}

    public Number getColor() {return getNumber(KEY_COLOR);}
    public void setColor(Number color) {put(KEY_COLOR, color);}

    public User getUser() {return (User) getParseUser(KEY_USER);}
    public void setUser(User user) {put(KEY_USER, user);}

    public String getWeekDay() {return getString(KEY_WEEKDAY);}
    public void setWeekDay(String weekDay) {put(KEY_WEEKDAY, weekDay);}




}
