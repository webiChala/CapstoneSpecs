package com.example.myguide.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myguide.StudentFragments.ConnectedTutorsFragment;
import com.example.myguide.StudentFragments.LookForTutorFragment;

public class MyFragmentAdapter extends FragmentStateAdapter {
    public MyFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new LookForTutorFragment();

        }

        return new ConnectedTutorsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
