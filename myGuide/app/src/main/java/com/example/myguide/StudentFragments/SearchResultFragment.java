package com.example.myguide.StudentFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myguide.R;
import com.example.myguide.adapters.EducationAdapter;
import com.example.myguide.adapters.FilteredUsersAdapter;
import com.example.myguide.databinding.FragmentLookForTutorBinding;
import com.example.myguide.databinding.FragmentSearchResultBinding;
import com.example.myguide.models.Education;
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
import java.util.List;


public class SearchResultFragment extends Fragment {
    private FragmentSearchResultBinding binding;
    public static final String TAG = "SearchResultFragment";
    String selectedCourseId;
    String zipcode;
    String price;
    private FilteredUsersAdapter adapter;
    private List<User> allFilteredUsers;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        selectedCourseId = getArguments().getString("selectedCourseId");
        zipcode = getArguments().getString("zipcode");
        price = getArguments().getString("price");

        binding = FragmentSearchResultBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allFilteredUsers = new ArrayList<>();
        adapter = new FilteredUsersAdapter(getContext(), allFilteredUsers);
        binding.rvSearchResults.setAdapter(adapter);
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ibGoBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LookForTutorFragment lookForTutorFragment = new LookForTutorFragment();
                Bundle args = new Bundle();
                lookForTutorFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, lookForTutorFragment).commit();
            }
        });
        searchByLocationAnd();

    }


    private void searchByLocationAnd() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(User.KEY_ISTUTOR, true);
        Log.i(TAG, "searchByLocationAnd: "+zipcode);

        if (price != null && price.length() > 0) {
            Log.i(TAG, "price included");

            query.whereLessThan(User.KEY_PRICE, Integer.parseInt(price));
        }
        query.whereContains(User.KEY_COURSESTUTORED, selectedCourseId);
        query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        if (zipcode != null && zipcode.length() == 5) {
            Log.i(TAG, "zipcode included");
            query.whereEqualTo("isInPersonTutor", true);
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
                        Log.i(TAG, data.toString(2));
                        Double Latitude = data.getJSONArray("results").getJSONObject(0).getDouble("Latitude");
                        Log.i(TAG, String.valueOf(Latitude));
                        Double Longitude = data.getJSONArray("results").getJSONObject(0).getDouble("Longitude");
                        ParseGeoPoint currentUserLocation = new ParseGeoPoint(Latitude, Longitude);
                        query.whereNear("Location", currentUserLocation);
                        getFilteredUsers(query, currentUserLocation);

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        })).start();
    }



    private void getFilteredUsers(ParseQuery<ParseUser> query, ParseGeoPoint currentUserLocation) {
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
                if (e == null) {
                    // do something with the list of results of your query
                    List<User> newFilteredUsers = new ArrayList<>();
                    for (ParseUser u:nearUsers) {
                        Log.i(TAG, "user objectid: " + u.getUsername());
                        User user = (User) u;
                        double distance = currentUserLocation.distanceInKilometersTo(u.getParseGeoPoint("Location"));
                        user.setDistanceFromCurrentUser(distance);
                        newFilteredUsers.add(user);
                        Log.i(TAG, "distance: "+user.getDistanceFromCurrentUser());
                    }
                    allFilteredUsers.clear();
                    allFilteredUsers.addAll(newFilteredUsers);
                    adapter.notifyDataSetChanged();
                } else {
                    // handle the error
                    Log.e(TAG, "error getting user", e);
                }
            }
        });
        ParseQuery.clearAllCachedResults();

    }
    private ParseGeoPoint getCurrentUserLocation(){

        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            // if it's not possible to find the user, do something like returning to login activity
        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("Location");

    }
}