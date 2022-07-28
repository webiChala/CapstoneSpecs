package com.example.myguide.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.myguide.R;
import com.example.myguide.CommonFragments.ChatFragment;
import com.example.myguide.TutorFragments.HomeFragmentTutor;
import com.example.myguide.TutorFragments.ProfileFragmentTutor;
import com.example.myguide.CommonFragments.ScheduleFragment;
import com.example.myguide.databinding.ActivityTutorHomeBinding;
import com.example.myguide.models.Message;
import com.example.myguide.models.User;
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

public class TutorHomeActivity extends AppCompatActivity {

    ActivityTutorHomeBinding tutorHomeBinding;
    public static final String TAG = "TutorHomeActivity";
    User currentUser = (User) ParseUser.getCurrentUser();

    ParseLiveQueryClient parseLiveQueryClient = null;
    ParseQuery<ParseObject> parseQuery;
    SubscriptionHandling<ParseObject> subscriptionHandling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorHomeBinding = ActivityTutorHomeBinding.inflate(getLayoutInflater());
        setContentView(tutorHomeBinding.getRoot());


        tutorHomeBinding.bottomNavigationTutor.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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
        tutorHomeBinding.bottomNavigationTutor.setSelectedItemId(R.id.action_home);
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
                        tutorHomeBinding.bottomNavigationTutor.getOrCreateBadge(R.id.action_chat).setNumber(objects.size());
                    } else {
                        tutorHomeBinding.bottomNavigationTutor.getOrCreateBadge(R.id.action_chat).clearNumber();
                    }
                } else {
                    Log.i(TAG, "done: Error getting  unread messages");
                }
            }
        });
    }

    protected void onStop() {
        super.onStop();
        parseLiveQueryClient.unsubscribe(parseQuery, subscriptionHandling);
        Log.i(TAG, "onStop: unsubscribed from tutor home live activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpLiveQuery();
        updateBadgeOnBottomNavigationView();
        Log.i(TAG, "onResume: resumed tutor home");
    }
}