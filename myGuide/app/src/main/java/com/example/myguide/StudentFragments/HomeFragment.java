package com.example.myguide.StudentFragments;

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
import com.example.myguide.databinding.FragmentSearchResultBinding;
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


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private HomeConnectedTutorsAdapter adapter;
    private List<UserTutorConnection> userTutorConnectionLists;
    User currentUser;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser =  (User) ParseUser.getCurrentUser();

        if (currentUser.getImage() != null) {
            Glide.with(getContext()).load(currentUser.getImage().getUrl()).circleCrop().into(binding.homeProfile);
        }
        userTutorConnectionLists = new ArrayList<>();
        adapter = new HomeConnectedTutorsAdapter(userTutorConnectionLists, getContext());
        binding.rvHomeTutors.setAdapter(adapter);
        binding.rvHomeTutors.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.tvSeeMore.setOnClickListener(new View.OnClickListener() {
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
                    binding.rvHomeTutors.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
                binding.progressbarRvHomeTutors.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newUserTutorConnectionServices.getUserTutorConnections(query);

    }


}