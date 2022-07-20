package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.myguide.adapters.UserTutorConnectionRequestAdapter;
import com.example.myguide.databinding.ActivityTutorNotificationBinding;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.Utils.UserTutorConnectionUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TutorNotificationActivity extends AppCompatActivity {

    private ActivityTutorNotificationBinding binding;
    private UserTutorConnectionRequestAdapter adapter;
    private List<UserTutorConnection> allConnectionrequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTutorNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        allConnectionrequests = new ArrayList<>();
        adapter = new UserTutorConnectionRequestAdapter(this, allConnectionrequests, true);
        binding.rvRequest.setAdapter(adapter);
        binding.rvRequest.setLayoutManager(new LinearLayoutManager(this));
        binding.connectionRequestGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllConnectionRequests();


    }

    private void getAllConnectionRequests() {
        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        query.whereEqualTo(UserTutorConnection.KEY_TUTOR, (User) ParseUser.getCurrentUser());
        query.whereEqualTo(UserTutorConnection.KEY_ACCEPTED, false);
        UserTutorConnectionUtils userTutorConnectionUtils = new UserTutorConnectionUtils(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                if (output.size() == 0) {
                    binding.rvRequest.setVisibility(View.GONE);
                    binding.emptyViewConnectionRequest.setVisibility(View.VISIBLE);
                }

                allConnectionrequests.addAll(output);
                binding.progressbarConnectionRequest.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        userTutorConnectionUtils.getUserTutorConnections(query);

    }
}