package com.example.myguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.databinding.ItemTutorBinding;
import com.example.myguide.models.User;
import com.example.myguide.ui.TutorDetailActivity;

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
            Intent i = new Intent(context, TutorDetailActivity.class);
            i.putExtra("user", users.get(getAdapterPosition()));
            context.startActivity(i);

        }

        public void bind(User user) {
            itemTutorBinding.tvFoundTutorAbout.setText(user.getAbout());
            itemTutorBinding.tvFoundTutorDistance.setText(String.valueOf(user.getDistanceFromCurrentUser()) + " miles");
            itemTutorBinding.tvFoundtutorPricePerHour.setText(String.valueOf(user.getPrice()) + "/hr");
            itemTutorBinding.tvFoundTutorname.setText(user.getName());
            if (user.getImage() != null) {
                Glide.with(context).load(user.getImage().getUrl()).circleCrop().into(itemTutorBinding.ivFoundTutorProfile);
            } else {
                itemTutorBinding.ivFoundTutorProfile.setImageDrawable(context.getDrawable(R.drawable.profile_icon));
            }
        }
    }
}
