package com.example.myguide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.databinding.ItemConnectionRequestBinding;
import com.example.myguide.databinding.ItemHomeConnectedtutorsBinding;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.parse.ParseUser;

import java.util.List;

public class HomeConnectedTutorsAdapter extends RecyclerView.Adapter<HomeConnectedTutorsAdapter.ViewHolder> {

    private List<UserTutorConnection> userTutorConnections;
    private Context context;

    public HomeConnectedTutorsAdapter(List<UserTutorConnection> userTutorConnections, Context context) {
        this.userTutorConnections = userTutorConnections;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeConnectedTutorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeConnectedtutorsBinding binding = ItemHomeConnectedtutorsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeConnectedTutorsAdapter.ViewHolder holder, int position) {

        UserTutorConnection userTutorConnection = userTutorConnections.get(position);
        holder.bind(userTutorConnection);

    }

    @Override
    public int getItemCount() {
        return userTutorConnections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ItemHomeConnectedtutorsBinding itemHomeConnectedtutorsBinding;
        public ViewHolder(ItemHomeConnectedtutorsBinding binding) {
            super(binding.getRoot());
            itemHomeConnectedtutorsBinding = binding;
            itemHomeConnectedtutorsBinding.getRoot().setOnClickListener(this);
        }

        public void bind(UserTutorConnection userTutorConnection) {
            User currentUser = (User) ParseUser.getCurrentUser();
            if (currentUser.isLoggedAsTutor()) {
                itemHomeConnectedtutorsBinding.tvConnectedTutorsName.setText(userTutorConnection.getStudent().getName());
                if (userTutorConnection.getStudent().getImage() != null) {
                    Glide.with(context).load(userTutorConnection.getStudent().getImage().getUrl()).circleCrop().into(itemHomeConnectedtutorsBinding.ivHomeConnectedTutorProfile);
                }
            } else {
                itemHomeConnectedtutorsBinding.tvConnectedTutorsName.setText(userTutorConnection.getTutor().getName());
                if (userTutorConnection.getTutor().getImage() != null) {
                    Glide.with(context).load(userTutorConnection.getTutor().getImage().getUrl()).circleCrop().into(itemHomeConnectedtutorsBinding.ivHomeConnectedTutorProfile);
                }
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
