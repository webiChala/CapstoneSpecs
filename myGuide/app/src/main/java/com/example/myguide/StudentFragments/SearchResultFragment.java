package com.example.myguide.StudentFragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myguide.R;
import com.example.myguide.adapters.FilteredUsersAdapter;
import com.example.myguide.databinding.FragmentLookForTutorBinding;
import com.example.myguide.databinding.FragmentSearchResultBinding;
import com.example.myguide.models.Availability;
import com.example.myguide.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SearchResultFragment extends Fragment {
    private FragmentSearchResultBinding binding;
    public static final String TAG = "SearchResultFragment";
    String selectedCourseId;
    String zipcode;
    String minPrice;
    String maxPrice;
    String rangeInMiles;
    String availability;
    boolean specificTimeSearch;

    private FilteredUsersAdapter adapter;
    private List<User> allFilteredUsers;
    private List<User> localUsers;
    private List<User> onlineUsers;

    Calendar c = Calendar.getInstance();
    int hour1  = c.get(Calendar.HOUR_OF_DAY);;
    int hour2 = c.get(Calendar.HOUR_OF_DAY);

    int minute1 = c.get(Calendar.MINUTE);
    int minute2 = c.get(Calendar.MINUTE);
    Date startTime = null;
    Date endTime = null;


    public SearchResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        selectedCourseId = getArguments().getString("selectedCourseId");
        zipcode = getArguments().getString("zipcode");
        minPrice = getArguments().getString("minPrice");
        maxPrice = getArguments().getString("maxPrice");
        rangeInMiles = getArguments().getString("rangeInMiles");
        availability = getArguments().getString("Availability");
        specificTimeSearch = getArguments().getBoolean("specificTimeSearch");

        if (specificTimeSearch) {
            hour1 = getArguments().getInt("hour1");
            hour2 = getArguments().getInt("hour2");
            minute1 = getArguments().getInt("minute1");
            minute2 = getArguments().getInt("minute2");
        }

        binding = FragmentSearchResultBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allFilteredUsers = new ArrayList<>();
        localUsers = new ArrayList<>();
        onlineUsers = new ArrayList<>();
        adapter = new FilteredUsersAdapter(getContext(), allFilteredUsers);
        binding.rvSearchResults.setAdapter(adapter);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ibGoBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LookForTutorFragment lookForTutorFragment = new LookForTutorFragment();
                Bundle args = new Bundle();
                lookForTutorFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainerScheduleFragment, lookForTutorFragment).commit();
            }
        });
        binding.btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnOnline.setBackgroundColor(getActivity().getColor(R.color.blue));
                binding.btnLocal.setBackgroundColor(Color.WHITE);
                allFilteredUsers.clear();
                allFilteredUsers.addAll(localUsers);
                if (allFilteredUsers.size() == 0) {
                    binding.rvSearchResults.setVisibility(View.GONE);
                    binding.emptyViewSearchResult.setVisibility(View.VISIBLE);
                } else {
                    binding.rvSearchResults.setVisibility(View.VISIBLE);
                    binding.emptyViewSearchResult.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        binding.btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnLocal.setBackgroundColor(getActivity().getColor(R.color.blue));
                binding.btnOnline.setBackgroundColor(Color.WHITE);
                allFilteredUsers.clear();
                allFilteredUsers.addAll(onlineUsers);
                if (allFilteredUsers.size() == 0) {
                    binding.rvSearchResults.setVisibility(View.GONE);
                    binding.emptyViewSearchResult.setVisibility(View.VISIBLE);
                } else {
                    binding.rvSearchResults.setVisibility(View.VISIBLE);
                    binding.emptyViewSearchResult.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        search();

    }


    private void search() {
        if (availability!=null) {
            List<String> availableUsersId = new ArrayList<>();
            ParseQuery<Availability> queryAvailability = ParseQuery.getQuery(Availability.class);
            queryAvailability.whereEqualTo(Availability.KEY_AVAILABLE, true);
            queryAvailability.whereEqualTo(Availability.KEY_WEEKDAY, availability);
            queryAvailability.findInBackground(new FindCallback<Availability>() {
                @Override
                public void done(List<Availability> objects, ParseException e) {
                    if (e==null) {
                        if (objects.size()==0) {
                            refreshList();
                            return;
                        }

                        if (specificTimeSearch) {
                            startTime = getTime(hour1, minute1);
                            endTime = getTime(hour2, minute2);
                            for (Availability a: objects) {
                                Calendar availStart = Calendar.getInstance();
                                Calendar availEnd = Calendar.getInstance();
                                availStart.set(Calendar.HOUR_OF_DAY, a.getHour().intValue());
                                availStart.set(Calendar.MINUTE, a.getMinute().intValue());
                                availEnd.set(Calendar.HOUR_OF_DAY, a.getHour().intValue());
                                availEnd.set(Calendar.MINUTE, a.getMinute().intValue());
                                availEnd.add(Calendar.MINUTE, a.getDuration().intValue());
                                if ((availStart.getTime().before(startTime) || availStart.getTime().equals(startTime)) && (availEnd.getTime().after(endTime) || availEnd.getTime().equals(endTime))) {
                                    availableUsersId.add(a.getUser().getObjectId());
                                }
                            }
                        } else {
                            for (Availability a: objects) {
                                availableUsersId.add(a.getUser().getObjectId());
                            }
                        }

                        search2(availableUsersId);
                    }
                }
            });
        } else {
            search2(null);
        }
    }

    private Date getTime(Integer hour, Integer minute) {
        Calendar time = Calendar.getInstance();
        time.setTime(new Date());
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);
        return time.getTime();
    }

    private void search2(List<String> availableUsersId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(User.KEY_ISTUTOR, true);
        if (availableUsersId != null) {
            query.whereContainedIn(User.KEY_OBJECT_ID, availableUsersId);
        }
        if (minPrice != null && minPrice.length() > 0) {
            query.whereGreaterThanOrEqualTo(User.KEY_PRICE, Integer.parseInt(minPrice));
        }
        if (maxPrice != null && maxPrice.length() > 0) {
            query.whereLessThanOrEqualTo(User.KEY_PRICE, Integer.parseInt(maxPrice));
        }
        query.addAscendingOrder(User.KEY_PRICE);
        query.whereContains(User.KEY_COURSESTUTORED, selectedCourseId);
        query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        if (zipcode != null && zipcode.length() == 5) {
            getGeoLocationFromZipcode(zipcode, query);
        } else {
            getFilteredUsers(query, getCurrentUserLocation());
        }

    }

    private void getGeoLocationFromZipcode(String zip_code, ParseQuery<ParseUser> query) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String where = URLEncoder.encode("{" +
                            "    \"US_Zip_Code\": " + zip_code +
                            "}", "utf-8");
                    URL url = new URL("https://parseapi.back4app.com/classes/Uszipcode_US_Zip_Code?limit=1&where=" + where);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestProperty("X-Parse-Application-Id", "r9pjIpWlLiO3gfmU6ZKPZPa0OPi5jrknUOlsx24g"); // This is your app's application id
                    urlConnection.setRequestProperty("X-Parse-REST-API-Key", "IZUWWVVGXfs0Kk7aUqWcHMfRo5RzQ3DkhXZ7dZvL"); // This is your app's REST API key
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        JSONObject data = new JSONObject(stringBuilder.toString()); // Here you have the data that you need
                        Double Latitude = data.getJSONArray("results").getJSONObject(0).getDouble("Latitude");
                        Double Longitude = data.getJSONArray("results").getJSONObject(0).getDouble("Longitude");
                        ParseGeoPoint currentUserLocation = new ParseGeoPoint(Latitude, Longitude);
                        query.whereNear("Location", currentUserLocation);
                        getFilteredUsers(query, currentUserLocation);

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                }
            }
        })).start();
    }



    private void getFilteredUsers(ParseQuery<ParseUser> query, ParseGeoPoint currentUserLocation) {
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {
                    List<User> newFilteredUsers = new ArrayList<>();
                    for (ParseUser u:nearUsers) {
                        User user = (User) u;
                        if (user.isInPersonTutor()) {

                            double distance = currentUserLocation.distanceInKilometersTo(u.getParseGeoPoint("Location"));
                            user.setDistanceFromCurrentUser(distance);

                            if (rangeInMiles.length() > 0) {
                                if (distance < Integer.parseInt(rangeInMiles)) {
                                    localUsers.add(user);
                                }
                            } else {
                                localUsers.add(user);
                            }
                        }

                        if (user.isOnlineTutor()) {
                            User newUser = user;
                            newUser.setIsShowingLocal(false);
                            onlineUsers.add(newUser);
                        }
                    }
                    refreshList();

                } else {}
            }
        });
        ParseQuery.clearAllCachedResults();

    }

    private void refreshList() {
        allFilteredUsers.clear();
        allFilteredUsers.addAll(localUsers);
        if (allFilteredUsers.size() == 0) {
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.emptyViewSearchResult.setVisibility(View.VISIBLE);
        }
        binding.progressbarSearchResult.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
    private ParseGeoPoint getCurrentUserLocation(){
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
        }
        return currentUser.getParseGeoPoint("Location");

    }
}