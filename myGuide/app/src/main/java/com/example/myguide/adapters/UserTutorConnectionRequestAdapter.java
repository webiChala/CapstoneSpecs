package com.example.myguide.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myguide.databinding.ItemConnectionRequestBinding;
import com.example.myguide.databinding.ItemEducationBinding;
import com.example.myguide.interfaces.MessageInterface;
import com.example.myguide.interfaces.UserTutorConnectionInterface;
import com.example.myguide.models.Education;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.example.myguide.models.UserTutorConnection;
import com.example.myguide.services.MessageServices;
import com.example.myguide.services.UserTutorConnectionServices;
import com.example.myguide.ui.ChatActivity;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class UserTutorConnectionRequestAdapter extends RecyclerView.Adapter<UserTutorConnectionRequestAdapter.ViewHolder> {

    private Context context;
    private List<UserTutorConnection> userTutorConnections;
    private boolean isShowingRequest;
    private User currentUser = (User) ParseUser.getCurrentUser();

    public UserTutorConnectionRequestAdapter(Context context, List<UserTutorConnection> userTutorConnections, boolean isShowingRequest) {
        this.context = context;
        this.userTutorConnections = userTutorConnections;
        this.isShowingRequest = isShowingRequest;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConnectionRequestBinding binding = ItemConnectionRequestBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserTutorConnection userTutorConnection = userTutorConnections.get(position);
        holder.bind(userTutorConnection);
    }

    @Override
    public int getItemCount() {
        return userTutorConnections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemConnectionRequestBinding itemConnectionRequestBinding;
        public ViewHolder(ItemConnectionRequestBinding binding) {
            super(binding.getRoot());
            itemConnectionRequestBinding = binding;
            itemConnectionRequestBinding.getRoot().setOnClickListener(this);
        }

        public void bind(UserTutorConnection userTutorConnection) {
            if (isShowingRequest) {
                itemConnectionRequestBinding.tvRequestorName.setText(userTutorConnection.getStudent().getName());
                itemConnectionRequestBinding.tvRequestorMessage.setText(userTutorConnection.getMessage());
                if (userTutorConnection.getStudent().getImage() != null) {
                    Glide.with(context).load(userTutorConnection.getStudent().getImage().getUrl()).circleCrop().into(itemConnectionRequestBinding.ivStudentProfileImage);
                }
                itemConnectionRequestBinding.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userTutorConnection.setHasAccepted(true);
                        userTutorConnection.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null) {
                                    userTutorConnections.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    Toast.makeText(context, "Accepted request!", Toast.LENGTH_SHORT).show();
                                    if (userTutorConnection.getMessage() != null && userTutorConnection.getMessage().length() > 0) {
                                        sendMessage(userTutorConnection);
                                    }
                                }
                            }
                        });
                    }
                });

                itemConnectionRequestBinding.btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userTutorConnection.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    userTutorConnections.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    Toast.makeText(context, "Declined request!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                User currentUser = (User) ParseUser.getCurrentUser();
                itemConnectionRequestBinding.tvRequestorMessage.setVisibility(View.GONE);
                itemConnectionRequestBinding.btnDeclineRequest.setVisibility(View.GONE);
                itemConnectionRequestBinding.btnAcceptRequest.setVisibility(View.GONE);
                if (currentUser.isLoggedAsTutor()) {
                    itemConnectionRequestBinding.tvRequestorName.setText(userTutorConnection.getStudent().getName());
                    if (userTutorConnection.getStudent().getImage() != null) {
                        Glide.with(context).load(userTutorConnection.getStudent().getImage().getUrl()).circleCrop().into(itemConnectionRequestBinding.ivStudentProfileImage);
                    }
                } else {
                    itemConnectionRequestBinding.tvRequestorName.setText(userTutorConnection.getTutor().getName());
                    if (userTutorConnection.getTutor().getImage() != null) {
                        Glide.with(context).load(userTutorConnection.getTutor().getImage().getUrl()).circleCrop().into(itemConnectionRequestBinding.ivStudentProfileImage);
                    }
                }

            }


        }

        private void sendMessage(UserTutorConnection userTutorConnection) {

            Message newMessage = new Message();
            newMessage.setMessage(userTutorConnection.getMessage());
            newMessage.setSender(userTutorConnection.getStudent());
            newMessage.setReceiver(userTutorConnection.getTutor());

            MessageServices messageServices = new MessageServices(new MessageInterface() {
                @Override
                public void getProcessFinish(List<Message> output) {

                }

                @Override
                public void postProcessFinish(ParseException e) {
                    if (e == null) {
                        Toast.makeText(context, "Request Message posted!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            messageServices.sendMessage(newMessage);
        }

        @Override
        public void onClick(View v) {
            UserTutorConnection clickedUserTutorConnection = userTutorConnections.get(getAdapterPosition());
            if (isShowingRequest) {
                //TODO  intent when requests are clicked
            } else {
                Intent i = new Intent(context, ChatActivity.class);
                User otherUser;
                if (currentUser.isLoggedAsTutor())
                {
                    otherUser = clickedUserTutorConnection.getStudent();
                } else {
                    otherUser = clickedUserTutorConnection.getTutor();
                }
                i.putExtra("otherUser", otherUser);
                context.startActivity(i);
            }

        }

    }
}
