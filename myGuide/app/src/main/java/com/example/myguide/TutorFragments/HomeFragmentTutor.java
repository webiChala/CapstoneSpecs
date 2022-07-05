package com.example.myguide.TutorFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.adapters.HomeConnectedTutorsAdapter;
import com.example.myguide.databinding.FragmentHomeBinding;
import com.example.myguide.databinding.FragmentHomeTutorBinding;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.services.UserTutorConnectionServices;
import com.example.myguide.ui.GetAllConnected;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFragmentTutor extends Fragment {

    FragmentHomeTutorBinding binding;
    private HomeConnectedTutorsAdapter adapter;
    private List<UserTutorConnection> userTutorConnectionLists;
    User currentUser = (User) ParseUser.getCurrentUser();

    public HomeFragmentTutor() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeTutorBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userTutorConnectionLists = new ArrayList<>();
        if (currentUser.getImage() != null) {
            Glide.with(getContext()).load(currentUser.getImage().getUrl()).circleCrop().into(binding.homeProfileTutor);
        }
        adapter = new HomeConnectedTutorsAdapter(userTutorConnectionLists, getContext());
        binding.rvHomeStudents.setAdapter(adapter);
        binding.rvHomeStudents.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.tvSeeMoreHomeTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), GetAllConnected.class);
                startActivity(i);
            }
        });
        getConnectedUsers();
    }

    private void getConnectedUsers() {
        ParseQuery<UserTutorConnection> query = ParseQuery.getQuery(UserTutorConnection.class);
        Log.i("HomeFragment", "getConnectedUsers: started fetching...");
        if (currentUser.isLoggedAsTutor() == true) {
            query.whereEqualTo(UserTutorConnection.KEY_TUTOR, currentUser);
        } else {
            query.whereEqualTo(UserTutorConnection.KEY_STUDENT, currentUser);
        }
        query.whereEqualTo(UserTutorConnection.KEY_ACCEPTED, true);
        query.setLimit(4);

        UserTutorConnectionServices newUserTutorConnectionServices = new UserTutorConnectionServices(new UserTutorConnectionInterface() {
            @Override
            public void getProcessFinish(List<UserTutorConnection> output) {
                userTutorConnectionLists.addAll(output);

                if (output.size() == 0) {
                    binding.rvHomeStudents.setVisibility(View.GONE);
                    binding.emptyViewTutor.setVisibility(View.VISIBLE);
                }
                binding.progressbarRvHomeStudents.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newUserTutorConnectionServices.getUserTutorConnections(query);

    }
}