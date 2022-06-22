package com.example.myguide.models;

public class Profile {
    String linkedinFirstName;
    String linkedinLastName;
    String linkedinProfileImage;
    String linkedinId;

    public Profile() {
    }

    public String getLinkedinFirstName() {
        return linkedinFirstName;
    }

    public String getLinkedinLastName() {
        return linkedinLastName;
    }

    public String getLinkedinProfileImage() {
        return linkedinProfileImage;
    }

    public String getLinkedinId() {
        return linkedinId;
    }

    public void setLinkedinFirstName(String linkedinFirstName) {
        this.linkedinFirstName = linkedinFirstName;
    }

    public void setLinkedinLastName(String linkedinLastName) {
        this.linkedinLastName = linkedinLastName;
    }

    public void setLinkedinProfileImage(String linkedinProfileImage) {
        this.linkedinProfileImage = linkedinProfileImage;
    }

    public void setLinkedinId(String linkedinId) {
        this.linkedinId = linkedinId;
    }
}
