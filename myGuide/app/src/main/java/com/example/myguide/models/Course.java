package com.example.myguide.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Course")
public class Course extends ParseObject {
    public static final String KEY_TITLE = "title";

    public Course(){

    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }
    public void setTitle(String title) {put(KEY_TITLE, title);}

    @Override
    public String toString() {
        return getTitle();
    }
}

