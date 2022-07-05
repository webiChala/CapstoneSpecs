package com.example.myguide.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.myguide.R;
import com.example.myguide.StudentFragments.ChatFragment;
import com.example.myguide.StudentFragments.HomeFragment;
import com.example.myguide.StudentFragments.LookForTutorFragment;
import com.example.myguide.StudentFragments.ScheduleFragment;
import com.example.myguide.TutorFragments.ChatFragmentTutor;
import com.example.myguide.TutorFragments.HomeFragmentTutor;
import com.example.myguide.TutorFragments.ProfileFragmentTutor;
import com.example.myguide.TutorFragments.ScheduleFragmentTutor;
import com.example.myguide.databinding.ActivitySplashBinding;
import com.example.myguide.databinding.ActivityStudentHomeBinding;
import com.example.myguide.databinding.ActivityTutorHomeBinding;
import com.google.android.material.navigation.NavigationBarView;

public class TutorHomeActivity extends AppCompatActivity {

    ActivityTutorHomeBinding tutorHomeBinding;
    public static final String TAG = "TutorHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorHomeBinding = ActivityTutorHomeBinding.inflate(getLayoutInflater());
        setContentView(tutorHomeBinding.getRoot());

        tutorHomeBinding.bottomNavigationTutor.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragmentToShow = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragmentToShow = new HomeFragmentTutor();
                        break;
                    case R.id.action_chat:
                        fragmentToShow = new ChatFragmentTutor();
                        break;
                    case R.id.action_profile:
                        fragmentToShow = new ProfileFragmentTutor();
                        break;
                    case R.id.action_schedule:
                        fragmentToShow = new ScheduleFragmentTutor();
                        break;
                    default:
                        break;
                }

                if (fragmentToShow != null) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragmentToShow).commit();
                }
                return true;
            }
        });
        tutorHomeBinding.bottomNavigationTutor.setSelectedItemId(R.id.action_home);
    }
}