package com.example.myguide.StudentFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myguide.R;
import com.example.myguide.adapters.MyFragmentAdapter;
import com.example.myguide.databinding.FragmentFindTutorStudentBinding;
import com.google.android.material.tabs.TabLayout;

public class TutorFragment extends Fragment {
    FragmentFindTutorStudentBinding binding;
    private MyFragmentAdapter adapter;

    public TutorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindTutorStudentBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tabLayoutFind.addTab(binding.tabLayoutFind.newTab().setText("Connected"));
        binding.tabLayoutFind.addTab(binding.tabLayoutFind.newTab().setText("Find"));

        FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
        adapter = new MyFragmentAdapter(fragmentManager, getLifecycle());

        binding.viewPager2.setAdapter(adapter);
        binding.tabLayoutFind.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayoutFind.selectTab(binding.tabLayoutFind.getTabAt(position));
            }
        });
    }
}