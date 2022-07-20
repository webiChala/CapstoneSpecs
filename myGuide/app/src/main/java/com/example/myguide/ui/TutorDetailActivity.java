package com.example.myguide.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.Utils.CourseUtils;
import com.example.myguide.Utils.UserTutorConnectionUtils;
import com.example.myguide.adapters.CourseAdapter;
import com.example.myguide.adapters.EducationAdapter;
import com.example.myguide.databinding.ActivityTutorDetailBinding;
import com.example.myguide.databinding.SendConnectionDialogBinding;
import com.example.myguide.interfaces.CourseInterface;
import com.example.myguide.interfaces.EducationInterface;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.Course;
import com.example.myguide.models.Education;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.Utils.EducationUtils;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TutorDetailActivity extends AppCompatActivity {

    ActivityTutorDetailBinding binding;
    private List<Course> coursesSupported;
    public static final String TAG = "TutorDetailActivity";
    private List<Education> educations;
    private CourseAdapter courseAdapter;
    private EducationAdapter educationAdapter;
    private SendConnectionDialogBinding sendConnectionDialogBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTutorDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sendConnectionDialogBinding = SendConnectionDialogBinding.inflate(getLayoutInflater());

        coursesSupported = new ArrayList<>();
        educations = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, coursesSupported);
        educationAdapter = new EducationAdapter(this, educations, false);
        binding.rvCourses.setAdapter(courseAdapter);
        binding.rvDetailedEducation.setAdapter(educationAdapter);
        binding.rvCourses.setLayoutManager(new FlexboxLayoutManager(this));
        binding.rvDetailedEducation.setLayoutManager(new LinearLayoutManager(this));

        User user = getIntent().getParcelableExtra("user");
        binding.tvDetailedTutorName.setText(user.getName());
        binding.tvDetailedAbout.setText(user.getAbout());
        binding.tvHourlyRateDetailed.setText(String.valueOf(user.getPrice()) + "/hr");

        if(user.isOnlineTutor()) {
            binding.tvIsOnline.setText("online");
        } else {
            binding.tvIsOnline.setText("not online");
        }
        if (user.isInPersonTutor()) {
            binding.tvIsInperson.setText(String.valueOf(user.getDistanceFromCurrentUser()) + " kms");
        } else {
            binding.tvIsInperson.setText("not inperson");
        }

        if (user.getImage() != null) {
            Glide.with(this).load(user.getImage().getUrl()).circleCrop().into(binding.ivDetailTutorProfile);
        }

        getUserTutorConnection(user);
        getUserEducation(user);
        getUserCourses(user);


    }

    private void getUserTutorConnection(User user) {

        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        query.whereEqualTo(UserTutorConnection.KEY_TUTOR, user);
        query.whereEqualTo(UserTutorConnection.KEY_STUDENT, (User) ParseUser.getCurrentUser());
        UserTutorConnectionUtils userTutorConnectionUtils = new UserTutorConnectionUtils(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                if (output.size() > 0) {
                    if (output.get(0).hasAccepted() == true) {
                        binding.btnConnect.setText("connected");
                        binding.btnConnect.setBackgroundColor(Color.DKGRAY);
                    } else {
                        binding.btnConnect.setText("requested");
                        binding.btnConnect.setBackgroundColor(Color.DKGRAY);
                    }
                } else {
                    binding.btnConnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestConnectionRequest(user);
                        }
                    });
                }
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        userTutorConnectionUtils.getUserTutorConnections(query);
    }

    private void requestConnectionRequest(User user) {

        final View view = sendConnectionDialogBinding.getRoot();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Request");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Send a connection request");


        final EditText etComments = (EditText) view.findViewById(R.id.etComments);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendConnectionRequest(user, etComments.getText().toString());
                alertDialog.dismiss();
                if(view.getParent() != null) {
                    ((ViewGroup)view.getParent()).removeView(view);
                }
            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                if(view.getParent() != null) {
                    ((ViewGroup)view.getParent()).removeView(view); // <- fix
                }

            }
        });


        alertDialog.setView(view);
        alertDialog.show();
    }

    private void sendConnectionRequest(User tutor, String message) {
        UserTutorConnection userTutorConnection = new UserTutorConnection();
        userTutorConnection.setTutor(tutor);
        userTutorConnection.setStudent((User) ParseUser.getCurrentUser());
        userTutorConnection.setHasAccepted(false);
        if (message != null && message.length() > 0) {
            userTutorConnection.setMessage(message);
        }
        UserTutorConnectionUtils asyncTask =new UserTutorConnectionUtils(new UserTutorConnectionInterface() {

            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {

            }

            @Override
            public void postProcessFinish(ParseException e) {
                if (e == null) {
                    Toast.makeText(TutorDetailActivity.this, "Requested!", Toast.LENGTH_SHORT).show();
                    binding.btnConnect.setText("requested");
                    binding.btnConnect.setBackgroundColor(Color.DKGRAY);
                }

            }

        });
        asyncTask.sendUserTutorConnection(userTutorConnection);
    }

    private void getUserEducation(User user) {
        EducationUtils asyncTask =new EducationUtils(new EducationInterface() {

            @Override
            public void processFinish(List<Education> output) {
                educations.addAll(output);
                educationAdapter.notifyDataSetChanged();
            }
        });
        asyncTask.getAllEducation(user);
    }
    private void getUserCourses(User user) {

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.include(Course.KEY_TITLE);
        query.orderByAscending(Course.KEY_TITLE);
        query.whereContainedIn(Course.KEY_OBJECT_ID, user.getCourses());
        CourseUtils courseService = new CourseUtils(new CourseInterface() {
            @Override
            public void processFinish(List<Course> output) {
                coursesSupported.addAll(output);
                courseAdapter.notifyDataSetChanged();
            }
        });

        courseService.getAllCourses(query);
    }


}