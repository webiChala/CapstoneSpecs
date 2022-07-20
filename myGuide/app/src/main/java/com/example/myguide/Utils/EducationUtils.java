package com.example.myguide.Utils;

import com.example.myguide.interfaces.EducationInterface;
import com.example.myguide.models.Education;
import com.example.myguide.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class EducationUtils {

    public EducationInterface delegate = null;

    public EducationUtils(EducationInterface asyncResponse) {
        delegate = asyncResponse;
    }

    public void getAllEducation(User user) {
        ParseQuery<Education> query = ParseQuery.getQuery(Education.class);
        query.whereEqualTo("Owner", user);
        query.include(Education.KEY_OWNER);
        query.include(Education.KEY_FIELDOFSTUDY);
        query.include(Education.KEY_DEGREE);
        query.include(Education.KEY_SCHOOL);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Education>() {
            @Override
            public void done(List<Education> educations, ParseException e) {
                if (e != null) {
                    return;
                }
                delegate.processFinish(educations);

            }
        });
    }
}
