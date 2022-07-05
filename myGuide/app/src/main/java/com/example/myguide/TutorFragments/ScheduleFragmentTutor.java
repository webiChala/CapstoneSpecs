package com.example.myguide.TutorFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myguide.R;


public class ScheduleFragmentTutor extends Fragment {


    public ScheduleFragmentTutor() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_tutor, container, false);
    }
}