package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.myguide.adapters.UserTutorConnectionRequestAdapter;
import com.example.myguide.databinding.ActivityChatBinding;
import com.example.myguide.databinding.ActivityGetAllConnectedBinding;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.services.UserTutorConnectionServices;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GetAllConnected extends AppCompatActivity {

    ActivityGetAllConnectedBinding binding;
    private User currentUser;
    private UserTutorConnectionRequestAdapter adapter;
    private List<UserTutorConnection> allConnectedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetAllConnectedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = (User) ParseUser.getCurrentUser();
        allConnectedUsers = new ArrayList<>();
        adapter = new UserTutorConnectionRequestAdapter(this, allConnectedUsers, false);
        binding.rvConnectedUsers.setAdapter(adapter);
        binding.rvConnectedUsers.setLayoutManager(new LinearLayoutManager(this));

        getAllConnected();
    }

    private void getAllConnected() {
        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        if (currentUser.isLoggedAsTutor() == true) {
            query.whereEqualTo(UserTutorConnection.KEY_TUTOR, currentUser);
        } else {
            query.whereEqualTo(UserTutorConnection.KEY_STUDENT, currentUser);
        }
        query.whereEqualTo(UserTutorConnection.KEY_ACCEPTED, true);

        UserTutorConnectionServices newUserTutorConnectionServices = new UserTutorConnectionServices(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                allConnectedUsers.addAll(output);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newUserTutorConnectionServices.getUserTutorConnections(query);


    }
}