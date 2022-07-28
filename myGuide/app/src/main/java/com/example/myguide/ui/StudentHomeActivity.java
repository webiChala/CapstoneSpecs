package com.example.myguide.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.myguide.CommonFragments.ChatFragment;
import com.example.myguide.R;
import com.example.myguide.Service.NotificationService;
import com.example.myguide.StudentFragments.LookForTutorFragment;
import com.example.myguide.CommonFragments.ScheduleFragment;
import com.example.myguide.StudentFragments.HomeFragment;
import com.example.myguide.TutorFragments.HomeFragmentTutor;
import com.example.myguide.databinding.ActivityStudentHomeBinding;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
import com.google.android.flexbox.AlignItems;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity {

    private static final String TAG = "StudentHomeActivity";
    ActivityStudentHomeBinding studentHomeBinding;
    User currentUser = (User) ParseUser.getCurrentUser();
    ParseLiveQueryClient parseLiveQueryClient = null;
    ParseQuery<ParseObject> parseQuery;
    SubscriptionHandling<ParseObject> subscriptionHandling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentHomeBinding = ActivityStudentHomeBinding.inflate(getLayoutInflater());
        View view = studentHomeBinding.getRoot();
        setContentView(view);

        studentHomeBinding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragmentToShow = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragmentToShow = new HomeFragmentTutor();
                        break;
                    case R.id.action_chat:
                        fragmentToShow = new ChatFragment();
                        break;
                    case R.id.action_search:
                        fragmentToShow = new LookForTutorFragment();
                        break;
                    case R.id.action_schedule:
                        fragmentToShow = new ScheduleFragment();
                        break;
                    default:
                        break;
                }

                if (fragmentToShow != null) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, fragmentToShow).commit();
                }
                return true;
            }
        });
        studentHomeBinding.bottomNavigation.setSelectedItemId(R.id.action_home);
        //setUpLiveQuery();
        updateBadgeOnBottomNavigationView();
    }

    private void setUpLiveQuery() {

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
                if (messageSender.getObjectId().equals(currentUser.getObjectId()) || messageReceiver.getObjectId().equals(currentUser.getObjectId()) ) {
                    updateBadgeOnBottomNavigationView();
                }

            } catch (ParseException e) {
            }

        });
//        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Message");
//        //ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Message");
//        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
//
//        SubscriptionHandling<ParseObject> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
//        //subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
//        subscriptionHandling.handleSubscribe(q -> {
//            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
//                Log.i(TAG, "setUpLiveQuery: yes");
//                StudentHomeActivity.this.runOnUiThread(() -> {
//                    Message output = (Message) object;
//                    try{
//                        User messageSender = (User) output.getSender().fetchIfNeeded();
//                        User messageReceiver = (User) output.getReceiver().fetchIfNeeded();
//                        if (messageSender.getObjectId().equals(currentUser.getObjectId()) || messageReceiver.getObjectId().equals(currentUser.getObjectId()) ) {
//                            updateBadgeOnBottomNavigationView();
//                        }
//
//                    } catch (ParseException e) {
//                    }
//                });
//            });
//            subscriptionHandling.handleEvent(SubscriptionHandling.Event.DELETE, (query, object) -> {
//                StudentHomeActivity.this.runOnUiThread(() -> {
//                    //messagesAdapter.removeItem(object);
//                });
//            });
//            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query, object) -> {
//                StudentHomeActivity.this.runOnUiThread(() -> {
//                    //messagesAdapter.updateItem(object);
//                    Message output = (Message) object;
//                    try{
//                        User messageSender = (User) output.getSender().fetchIfNeeded();
//                        User messageReceiver = (User) output.getReceiver().fetchIfNeeded();
//                        if (messageSender.getObjectId().equals(currentUser.getObjectId()) || messageReceiver.getObjectId().equals(currentUser.getObjectId()) ) {
//                            updateBadgeOnBottomNavigationView();
//                        }
//
//                    } catch (ParseException e) {
//                    }
//                });
//            });
//        });
//        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query, object) -> {
//
//        });
    }


    private void updateBadgeOnBottomNavigationView() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(Message.KEY_RECEIVER, (User) ParseUser.getCurrentUser());
        if (currentUser.isLoggedAsTutor()) {
            query.whereEqualTo(Message.KEY_IS_FOR_TUTOR, true);
        } else {
            query.whereEqualTo(Message.KEY_IS_FOR_TUTOR, false);
        }
        query.whereEqualTo(Message.KEY_READ, false);

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e==null) {
                    if (objects.size() > 0) {
                        Log.i(TAG, "done: " + objects.size());
                        studentHomeBinding.bottomNavigation.getOrCreateBadge(R.id.action_chat).setNumber(objects.size());
                    } else {
                        studentHomeBinding.bottomNavigation.getOrCreateBadge(R.id.action_chat).clearNumber();
                    }
                } else {
                    Log.i(TAG, "done: Error getting  unread messages");
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        parseLiveQueryClient.unsubscribe(parseQuery, subscriptionHandling);
        Log.i(TAG, "onStop: unsubscribed from student home live activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpLiveQuery();
        updateBadgeOnBottomNavigationView();
        Log.i(TAG, "onResume: resumed student home");
    }
}