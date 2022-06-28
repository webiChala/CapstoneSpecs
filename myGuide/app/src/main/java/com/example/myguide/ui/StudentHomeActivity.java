package com.example.myguide.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.myguide.R;
import com.example.myguide.databinding.ActivityStudentHomeBinding;
import com.example.myguide.StudentFragments.ChatFragment;
import com.example.myguide.StudentFragments.TutorFragment;
import com.example.myguide.StudentFragments.HomeFragment;
import com.example.myguide.StudentFragments.ScheduleFragment;
import com.google.android.material.navigation.NavigationBarView;

public class StudentHomeActivity extends AppCompatActivity {

    ActivityStudentHomeBinding studentHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentHomeBinding = ActivityStudentHomeBinding.inflate(getLayoutInflater());
        View view = studentHomeBinding.getRoot();
        setContentView(view);

        studentHomeBinding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragmentToShow = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragmentToShow = new HomeFragment();
                        break;
                    case R.id.action_chat:
                        fragmentToShow = new ChatFragment();
                        break;
                    case R.id.action_search:
                        fragmentToShow = new TutorFragment();
                        break;
                    case R.id.action_schedule:
                        fragmentToShow = new ScheduleFragment();
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
        studentHomeBinding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }
}