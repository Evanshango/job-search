package com.evans.jobs.network;

import com.evans.jobs.models.Job;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiClient {

    @GET("positions.json")
    Call<List<Job>> getAvailableJobs();

    @GET("positions.json")
    Call<List<Job>> getQueriedJobs(
            @Query("description") String description,
            @Query("location") String location
    );
}
