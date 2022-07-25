package com.example.myguide.CommonFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.myguide.R;
import com.example.myguide.adapters.EventAdapter;
import com.example.myguide.databinding.FragmentScheduleTutorBinding;
import com.example.myguide.models.Event;
import com.example.myguide.models.User;
import com.example.myguide.ui.AvailabilitySchedulingActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ScheduleFragment extends Fragment {

    FragmentScheduleTutorBinding binding;
    public static final String TAG = "ScheduleFragment";
    MenuItem menuItem;
    RecyclerView rvEvents;
    EventAdapter eventAdapter;
    List<Event> events;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    HashMap<String, List<Event>> map = new HashMap<>();
    List<Event> savedEvent;

    public ScheduleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScheduleTutorBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivCheckAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AvailabilitySchedulingActivity.class);
                startActivity(i);

            }
        });

        savedEvent = new ArrayList<>();

        BottomSheetFragment b = new BottomSheetFragment();
        b.setFragment(b);

        events = new ArrayList<>();
        rvEvents = binding.rvEvents;
        eventAdapter = new EventAdapter(getContext(), events);
        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.createNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                b.show(getActivity().getSupportFragmentManager(), "BottomSheetDialog");
            }
        });

        binding.toolbarScheduleTutor.setSubtitle("search event by title");
        binding.toolbarScheduleTutor.inflateMenu(R.menu.menu_search);
        binding.toolbarScheduleTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActionMenuItemView)view.findViewById(R.id.action_search)).callOnClick();
            }
        });


        binding.toolbarScheduleTutor.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        binding.calendarView.setVisibility(View.GONE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, new ScheduleFragment()).commit();
                        return true;
                    }
                });
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Type here to search");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        events.clear();
                        eventAdapter.notifyDataSetChanged();
                        getAllEvents(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                return true;
            }
        });


        binding.calendarView.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = binding.calendarView.getSelectedDay();
                String selectedDay = (((day.getDay() < 10) ? "0" + day.getDay() : day.getDay() )  + "/" + ((day.getMonth() + 1 < 10) ? "0" + (day.getMonth() + 1) : (day.getMonth() + 1) )  + "/" + day.getYear());
                events.clear();
                if (map.containsKey(selectedDay)) {
                    events.addAll(map.get(selectedDay));
                    binding.tvErrorMessage.setVisibility(View.INVISIBLE);
                } else {
                    binding.tvErrorMessage.setVisibility(View.VISIBLE);
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(@NonNull View view) {

            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int i) {

            }

            @Override
            public void onClickListener() {

            }

            @Override
            public void onDayChanged() {

            }
        });

        getAllEvents(null);
    }

    private void getAllEvents(String searchQuery) {

        ParseQuery<Event> query1 = ParseQuery.getQuery(Event.class);
        ParseQuery<Event> query2 = ParseQuery.getQuery(Event.class);
        if (searchQuery != null) {
            query1.whereContains(Event.KEY_TITLE, searchQuery);
            query2.whereContains(Event.KEY_TITLE, searchQuery);
        }
        query1.whereEqualTo(Event.KEY_ADDEDUSER, (User) ParseUser.getCurrentUser());
        query2.whereEqualTo(Event.KEY_USER, (User) ParseUser.getCurrentUser());
        List<ParseQuery<Event>> listOfEvents = new ArrayList<>();
        listOfEvents.add(query1);
        listOfEvents.add(query2);
        ParseQuery<Event> query = ParseQuery.or(listOfEvents);
        query.include(Event.KEY_EVENTDATE);
        query.include(Event.KEY_REPETITION);
        query.include(Event.KEY_DETAIL);
        query.include(Event.KEY_LOCATION);
        query.include(Event.KEY_ADDEDUSER);
        query.include(Event.KEY_STARTDATE);
        query.include(Event.KEY_ENDDATE);
        query.include(Event.KEY_TITLE);
        query.include(Event.KEY_USER);
        query.addAscendingOrder(Event.KEY_EVENTDATE);

        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e==null) {
                    events.clear();
                    if (searchQuery == null) {
                        for (Event event: objects) {
                            Date eventDate = event.getEventDate();
                            String strDate = sdf.format(eventDate);
                            if (map.containsKey(strDate)) {
                                List<Event> value = map.get(strDate);
                                value.add(event);
                                map.put(strDate, value);
                            } else {
                                List<Event> newValue = new ArrayList<>();
                                newValue.add(event);
                                map.put(strDate, newValue);
                            }
                        }
                        Date today = Calendar.getInstance().getTime();
                        String todayInString = sdf.format(today);
                        if (map.containsKey(todayInString)) {
                            events.addAll(map.get(todayInString));
                            eventAdapter.notifyDataSetChanged();
                            if (events.size() == 0) {
                                binding.tvErrorMessage.setVisibility(View.VISIBLE);
                                binding.tvErrorMessage.setText("Nothing planned for this day!");
                            }
                        }
                    } else {
                        events.addAll(objects);
                        eventAdapter.notifyDataSetChanged();
                        if (events.size() == 0) {
                            binding.tvErrorMessage.setVisibility(View.VISIBLE);
                            binding.tvErrorMessage.setText("No result");
                        }
                    }
                }
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
        menuItem = menu.findItem(R.id.action_search);

    }
}