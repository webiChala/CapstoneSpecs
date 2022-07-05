package com.example.myguide.TutorFragments;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.StudentFragments.ChatFragment;
import com.example.myguide.adapters.MessageAdapter;
import com.example.myguide.databinding.FragmentChatTutorBinding;
import com.example.myguide.databinding.FragmentLookForTutorBinding;
import com.example.myguide.interfaces.MessageInterface;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.example.myguide.services.MessageServices;
import com.example.myguide.ui.GetAllConnected;
import com.example.myguide.ui.TutorNotificationActivity;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatFragmentTutor extends Fragment {

    private FragmentChatTutorBinding binding;
    private MessageAdapter adapter;
    private List<Message> allMessages;
    public static final String TAG = "ChatFragmentTutor";
    private User currentUser;

    public ChatFragmentTutor() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatTutorBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allMessages = new ArrayList<>();
        adapter = new MessageAdapter(allMessages, getContext());
        binding.rvMessageTutor.setAdapter(adapter);
        binding.rvMessageTutor.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = (User) ParseUser.getCurrentUser();

        if (currentUser.isLoggedAsTutor()) {
            binding.ibNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), TutorNotificationActivity.class);
                    startActivity(i);
                }
            });
        }

        binding.sendNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), GetAllConnected.class);
                startActivity(i);
            }
        });


        getMessage();

    }

    private void getMessage() {
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        query1.whereEqualTo(Message.KEY_SENDER, (User) ParseUser.getCurrentUser());

        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
        query2.whereEqualTo(Message.KEY_RECEIVER, (User) ParseUser.getCurrentUser());

        List<ParseQuery<Message>> list = new ArrayList<ParseQuery<Message>>();
        list.add(query1);
        list.add(query2);

        ParseQuery<Message> query = ParseQuery.or(list);
        MessageServices newMessageService = new MessageServices(new MessageInterface() {
            @Override
            public void getProcessFinish(List<Message> output) {
                if (output == null) {
                    Toast.makeText(getContext(), "Message Loading Failed!", Toast.LENGTH_SHORT).show();
                } else {
                    allMessages.clear();
                    Set<String> set = new HashSet<String>();
                    String SenderAndReceiver;
                    for (Message m:output)
                    {
                        if (m.getSender().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            SenderAndReceiver = m.getSender().getObjectId() + " " + m.getReceiver().getObjectId();

                        } else {
                            SenderAndReceiver = m.getReceiver().getObjectId() + " " + m.getSender().getObjectId();
                        }

                        if (set.contains(SenderAndReceiver)) {
                            continue;
                        } else {
                            allMessages.add(m);
                            set.add(SenderAndReceiver);
                        }

                    }
                    if (allMessages.size() == 0) {
                        binding.rvMessageTutor.setVisibility(View.GONE);
                        binding.emptyViewMessage.setVisibility(View.VISIBLE);
                    }
                    set.clear();
                    binding.progressbarMessage.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newMessageService.getMessage(query);
    }
}