package com.example.myguide.services;


import com.example.myguide.models.Profile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ProfileApi {

    @GET("rest/me")
    Call<Profile> getLinkedinProfile(@Header("Authorization") String authorization);
}
