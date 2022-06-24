package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Education")
public class Education extends ParseObject{
    public static final String KEY_SCHOOL = "School";
    public static final String KEY_DEGREE = "degree";
    public static final String KEY_FIELDOFSTUDY = "fieldOfStudy";
    public static final String KEY_OWNER = "Owner";

    public Education(){

    }

    public String getSchool() {
        return getString(KEY_SCHOOL);
    }
    public String getDegree() {
        return getString(KEY_DEGREE);
    }
    public String getFieldofStudy() {
        return getString(KEY_FIELDOFSTUDY);
    }
    public User getOwner() {
        return (User) getParseUser(KEY_OWNER);
    }

    public void setSchool(String school) {
        put(KEY_SCHOOL, school);
    }

    public void setDegree(String degree) {
        put(KEY_DEGREE, degree);
    }
    public void setFieldofstudy(String fieldofstudy) {
        put(KEY_FIELDOFSTUDY, fieldofstudy);
    }

    public void setOwner(User owner) {
        put(KEY_OWNER, owner);
    }





}



