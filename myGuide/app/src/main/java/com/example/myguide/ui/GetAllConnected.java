package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.myguide.adapters.UserTutorConnectionRequestAdapter;
import com.example.myguide.databinding.ActivityGetAllConnectedBinding;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.Utils.UserTutorConnectionUtils;
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
        binding.allconnectedGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        UserTutorConnectionUtils newUserTutorConnectionUtils = new UserTutorConnectionUtils(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                if (output.size() == 0) {
                    binding.rvConnectedUsers.setVisibility(View.GONE);
                    binding.emptyViewConnected.setVisibility(View.VISIBLE);
                }

                allConnectedUsers.addAll(output);
                binding.progressbarConnectedusers.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newUserTutorConnectionUtils.getUserTutorConnections(query);


    }
}