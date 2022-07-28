package com.example.myguide.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myguide.R;
import com.example.myguide.Utils.MessageUtils;
import com.example.myguide.Utils.SnackBarUtil;
import com.example.myguide.adapters.ChatAdapter;
import com.example.myguide.databinding.ActivityChatBinding;
import com.example.myguide.interfaces.MessageInterface;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    public static final String TAG = "ChatActivity";
    List<Message> mMessages;
    private ChatAdapter mAdapter;
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    boolean mFirstLoad;
    User otherUser;
    User currentUser = (User) ParseUser.getCurrentUser();
    SnackBarUtil snackBar;
    SubscriptionHandling<ParseObject> subscriptionHandling;
    ParseLiveQueryClient parseLiveQueryClient = null;
    ParseQuery<ParseObject> parseQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        otherUser = getIntent().getParcelableExtra("otherUser");
        mFirstLoad = true;
        snackBar = new SnackBarUtil(this, binding.chatActivity);
        if (otherUser.getImage() != null) {
            Glide.with(this).load(otherUser.getImage().getUrl()).circleCrop().into(binding.ivOtherUserProfileImage);
        }
        if (otherUser.getName() != null) {
            binding.tvOtherUserName.setText(otherUser.getName());
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMessages = new ArrayList<>();
        mAdapter = new ChatAdapter(this, ParseUser.getCurrentUser().getObjectId(), mMessages);

        binding.rvChat.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        binding.rvChat.setLayoutManager(linearLayoutManager);


        setupMessagePosting(otherUser);
        refreshMessages();

        //setUpLiveQuery();
    }

    void setUpLiveQuery() {
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(getString(R.string.websocketurl)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        parseQuery = new ParseQuery<>("Message");
        subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
            Message output = (Message) object;
            try{
                User messageSender = (User) output.getSender().fetchIfNeeded();
                User messageReceiver = (User) output.getReceiver().fetchIfNeeded();
                if ((messageSender.getObjectId().equals(otherUser.getObjectId()) && messageReceiver.getObjectId().equals(currentUser.getObjectId()))) {

                    mMessages.add(0, output);

                    // RecyclerView updates need to be run on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.emptyViewChat.setVisibility(View.GONE);
                            mAdapter.notifyItemInserted(0);
                            binding.rvChat.smoothScrollToPosition(0);
                        }
                    });
                }

            } catch (ParseException e) {
            }

        });
    }

    void setupMessagePosting(User otherUser) {

        binding.ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = binding.etMessage.getText().toString();
                if (data == null || data.length() == 0) {
                    snackBar.setSnackBar("Please enter a message!");
                    return;
                }
                User currentUser = (User) ParseUser.getCurrentUser();

                Message message = new Message();
                message.setSender(currentUser);
                message.setReceiver(otherUser);
                message.setMessage(data);
                if (currentUser.isLoggedAsTutor() == true) {
                    message.setIsForTutor(false);
                } else {
                    message.setIsForTutor(true);
                }

                MessageUtils newMessageUtils = new MessageUtils(new MessageInterface() {
                    @Override
                    public void getProcessFinish(List<Message> output) {

                    }

                    @Override
                    public void postProcessFinish(ParseException e) {
                        if (e == null) {
                            binding.emptyViewChat.setVisibility(View.GONE);
                            mMessages.add(0, message);
                            mAdapter.notifyItemInserted(0);
                            binding.rvChat.smoothScrollToPosition(0);
                            binding.etMessage.setText(null);
                        } else {}}
                });
                newMessageUtils.sendMessage(message);

            }
        });
    }

    void refreshMessages() {
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        query1.whereEqualTo(Message.KEY_SENDER, (User) ParseUser.getCurrentUser());
        if (((User) ParseUser.getCurrentUser()).isLoggedAsTutor() == true) {
            query1.whereEqualTo(Message.KEY_IS_FOR_TUTOR, false);
        } else {
            query1.whereEqualTo(Message.KEY_IS_FOR_TUTOR, true);
        }
        query1.whereEqualTo(Message.KEY_RECEIVER, otherUser);

        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
        query2.whereEqualTo(Message.KEY_RECEIVER, (User) ParseUser.getCurrentUser());
        if (((User) ParseUser.getCurrentUser()).isLoggedAsTutor() == true) {
            query2.whereEqualTo(Message.KEY_IS_FOR_TUTOR, true);
        } else {
            query2.whereEqualTo(Message.KEY_IS_FOR_TUTOR, false);
        }
        query2.whereEqualTo(Message.KEY_SENDER, otherUser);


        List<ParseQuery<Message>> list = new ArrayList<ParseQuery<Message>>();
        list.add(query1);
        list.add(query2);

        ParseQuery<Message> query3 = ParseQuery.or(list);
        query3.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        MessageUtils newMessageUtils = new MessageUtils(new MessageInterface() {
            @Override
            public void getProcessFinish(List<Message> output) {
                mMessages.clear();
                mMessages.addAll(output);
                if (mMessages.size() == 0) {
                    binding.emptyViewChat.setVisibility(View.VISIBLE);
                }
                binding.progressbarChat.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                if (mFirstLoad) {
                    binding.rvChat.scrollToPosition(0);
                    mFirstLoad = false;
                }
                List<Message> allMessages = new ArrayList<>();
                for (Message m: output)
                {
                    m.setIsRead(true);
                    allMessages.add(m);
                }
                if (allMessages.size() > 0) {
                    ParseObject.saveAllInBackground(allMessages, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                        }
                    });
                }

            }

            @Override
            public void postProcessFinish(ParseException e) {

            }

        });
        newMessageUtils.getMessage(query3);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: chat activity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpLiveQuery();
        Log.i(TAG, "onResume: chat activity resumed");
    }

    @Override
    protected void onStop() {
        super.onStop();
        parseLiveQueryClient.unsubscribe(parseQuery, subscriptionHandling);
        Log.i(TAG, "onStop: unsubscribed and chat activity stopped!");

    }
}