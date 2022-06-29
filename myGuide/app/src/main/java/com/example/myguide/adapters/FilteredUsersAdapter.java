package com.example.myguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.databinding.ItemEducationBinding;
import com.example.myguide.databinding.ItemTutorBinding;
import com.example.myguide.models.Education;
import com.example.myguide.models.User;

import java.util.List;

public class FilteredUsersAdapter extends RecyclerView.Adapter<FilteredUsersAdapter.ViewHolder> {

    private Context context;
    private List<User> users;

    public FilteredUsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTutorBinding binding = ItemTutorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        ItemTutorBinding itemTutorBinding;

        public ViewHolder(ItemTutorBinding binding) {
            super(binding.getRoot());
            itemTutorBinding = binding;
            itemTutorBinding.getRoot().setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Toast.makeText(context, "User clicked!", Toast.LENGTH_SHORT).show();
        }

        public void bind(User user) {
            itemTutorBinding.tvFoundTutorAbout.setText(user.getAbout());
            itemTutorBinding.tvFoundTutorDistance.setText(String.valueOf(user.getDistanceFromCurrentUser()) + "kms");
            itemTutorBinding.tvFoundtutorPricePerHour.setText(String.valueOf(user.getPrice()) + "/hr");
            itemTutorBinding.tvFoundTutorname.setText(user.getName());
            if (user.getImage() != null) {
                Glide.with(context).load(user.getImage().getUrl()).circleCrop().into(itemTutorBinding.ivFoundTutorProfile);
            }
        }
    }
}
