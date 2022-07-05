package com.example.myguide.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_USERIMAGE = "userImage";
    public static final String KEY_ISTUTOR = "isTutor";
    public static final String KEY_ISSTUDENT = "isStudent";
    public static final String KEY_LOGGEDASTUTOR = "loggedAsTutor";
    public static final String KEY_ISNEW = "isNew";
    public static final String KEY_NAME = "name";
    public static final String KEY_COURSESTUTORED = "coursesTutored";
    public static final String KEY_ABOUT = "About";
    public static final String KEY_PRICE = "Price";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_ZIPCODE = "Zipcode";
    public static final String KEY_ISONLINETUTOR = "isOnlineTutor";
    public static final String KEY_ISINPERSONTUTOR = "isInPersonTutor";
    public double distanceFromCurrentUser = 0;
    public boolean isShowingLocal = true;

    public User(){

    }


    public boolean isTutor() {
        return getBoolean(KEY_ISTUTOR);
    }
    public void setKeyIstutor(boolean istutor) {
        put(KEY_ISTUTOR, istutor);
    }

    public boolean isLoggedAsTutor() {
        return getBoolean(KEY_LOGGEDASTUTOR);
    }
    public void setKeyLoggedastutor(boolean isLoggedAsTutor) { put(KEY_LOGGEDASTUTOR, isLoggedAsTutor);}

    public boolean isStudent() {
        return getBoolean(KEY_ISSTUDENT);
    }
    public void setKeyIsstudent(boolean isStudent) {
        put(KEY_ISSTUDENT, isStudent);
    }

    public boolean isNew() {
        return getBoolean(KEY_ISNEW);
    }
    public void setKeyIsnew(boolean isNew) { put(KEY_ISNEW, isNew);}

    public String getName() {
        return getString(KEY_NAME);
    }
    public void setName(String name) { put(KEY_NAME, name);}

    public ParseFile getImage() {
        return getParseFile(KEY_USERIMAGE);
    }
    public void setImage(ParseFile parseFile) {
        put(KEY_USERIMAGE, parseFile);
    }

    public List<String> getCourses() {return getList(KEY_COURSESTUTORED);}
    public void setKeyCoursestutored(List<String> coursesTutored) { put(KEY_COURSESTUTORED, coursesTutored);}

    public void setAbout(String about) {put(KEY_ABOUT, about);}
    public String getAbout() {return getString(KEY_ABOUT);}

    public void setPrice(Number price) {put(KEY_PRICE, price);}
    public Number getPrice() {return getNumber(KEY_PRICE);}

    public void setKeyPhonenumber(String phonenumber) {put(KEY_PHONENUMBER, phonenumber);}
    public String getPhonenumber() {return getString(KEY_PHONENUMBER);}

    public void setKeyZipcode(String zipcode) {put(KEY_ZIPCODE, zipcode);}
    public String getKeyZipcode() {return getString(KEY_ZIPCODE);}

    public boolean isOnlineTutor() {
        return getBoolean(KEY_ISONLINETUTOR);
    }
    public void setKeyIsonlinetutor(boolean isonlinetutor) { put(KEY_ISONLINETUTOR, isonlinetutor);}

    public boolean isInPersonTutor() {
        return getBoolean(KEY_ISINPERSONTUTOR);
    }
    public void setKeyIsinpersontutor(boolean isinpersontutor) { put(KEY_ISINPERSONTUTOR, isinpersontutor);}

    public void setDistanceFromCurrentUser(double distance) {distanceFromCurrentUser = distance;}
    public double getDistanceFromCurrentUser()
    {
        return Math.round(distanceFromCurrentUser);
    }

    public boolean isShowingLocal() {
        return isShowingLocal;
    }
    public void setIsShowingLocal(boolean isLocal) {
        isShowingLocal = isLocal;
    }



}
