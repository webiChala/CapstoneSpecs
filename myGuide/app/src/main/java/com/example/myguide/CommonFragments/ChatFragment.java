package com.example.myguide.CommonFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myguide.R;
import com.example.myguide.adapters.MessageAdapter;
import com.example.myguide.databinding.FragmentChatTutorBinding;
import com.example.myguide.interfaces.MessageInterface;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.example.myguide.Utils.MessageUtils;
import com.example.myguide.ui.GetAllConnected;
import com.example.myguide.ui.TutorNotificationActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatFragment extends Fragment {

    private FragmentChatTutorBinding binding;
    private MessageAdapter adapter;
    private List<Message> allMessages;
    public static final String TAG = "ChatFragment";
    private User currentUser;

    public ChatFragment() {
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
        } else {
            binding.ibNotification.setVisibility(View.GONE);
        }

        binding.toolbar.setSubtitle("search for a message");
        binding.toolbar.inflateMenu(R.menu.menu_search);
        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActionMenuItemView)view.findViewById(R.id.action_search)).callOnClick();
            }
        });


        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, new ChatFragment()).commit();
                        return true;
                    }
                });
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Type here to search");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        getMessage(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
                return true;
            }
        });


        binding.sendNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), GetAllConnected.class);
                startActivity(i);
            }
        });

        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(getString(R.string.websocketurl)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Message");
        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
            Message output = (Message) object;
            try{
                User messageSender = (User) output.getSender().fetchIfNeeded();
                User messageReceiver = (User) output.getReceiver().fetchIfNeeded();
                if (messageSender.getObjectId().equals(currentUser.getObjectId()) || messageReceiver.getObjectId().equals(currentUser.getObjectId()) ) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            getMessage(null);
                        }
                    });
                }

            } catch (ParseException e) {
            }

        });


        getMessage(null);

    }

    private void getMessage(String SearchQuery) {
        binding.progressbarMessage.setVisibility(View.VISIBLE);
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        query1.whereEqualTo(Message.KEY_SENDER, (User) ParseUser.getCurrentUser());
        if (currentUser.isLoggedAsTutor()) {
            query1.whereEqualTo(Message.KEY_IS_FOR_TUTOR, false);
        } else {
            query1.whereEqualTo(Message.KEY_IS_FOR_TUTOR, true);
        }

        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
        query2.whereEqualTo(Message.KEY_RECEIVER, (User) ParseUser.getCurrentUser());
        if (currentUser.isLoggedAsTutor()) {
            query2.whereEqualTo(Message.KEY_IS_FOR_TUTOR, true);
        } else {
            query2.whereEqualTo(Message.KEY_IS_FOR_TUTOR, false);
        }


        List<ParseQuery<Message>> list = new ArrayList<ParseQuery<Message>>();
        list.add(query1);
        list.add(query2);

        ParseQuery<Message> query = ParseQuery.or(list);
        if (SearchQuery!=null) {
            query.whereContains(Message.KEY_MESSAGE, SearchQuery);
        }
        MessageUtils newMessageService = new MessageUtils(new MessageInterface() {
            @Override
            public void getProcessFinish(List<Message> output) {
                if (output == null) {
                    Toast.makeText(getContext(), "Message Loading Failed!", Toast.LENGTH_SHORT).show();
                } else {
                    if (SearchQuery==null) {
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
                    } else {
                        allMessages.clear();
                        allMessages.addAll(output);
                        binding.progressbarMessage.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void postProcessFinish(ParseException e) {

            }
        });

        newMessageService.getMessage(query);
    }
}