package com.evans.jobs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evans.jobs.adapters.JobsAdapter;
import com.evans.jobs.models.Job;
import com.evans.jobs.network.ApiClient;
import com.evans.jobs.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements JobsAdapter.JobInteraction {

    private RecyclerView mJobRecycler;
    private JobsAdapter mJobsAdapter;
    private ProgressBar loader;
    private List<Job> mJobList = new ArrayList<>();
    private EditText edLocation, edDescription;
    private Button btnSearch;
    private ApiClient apiClient;
    private boolean initialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = ApiService.getApiClient();

        initViews();

        mJobsAdapter = new JobsAdapter(this, this);

        setUpRecycler();

        btnSearch.setOnClickListener(view -> performSearch());
    }

    private void performSearch() {
        String location = edLocation.getText().toString().toLowerCase().trim();
        String description = edDescription.getText().toString().toLowerCase().trim();

        if (!location.isEmpty() || !description.isEmpty()) {
            if (location.contains(" ")) {
                location = location.replace(" ", "+");
            }
            initialLoad = false;
            makeRequest(location, description);
        } else {
            if (!initialLoad)
                setUpRecycler();
        }
    }

    private void makeRequest(String location, String description) {
        loader.setVisibility(View.VISIBLE);
        apiClient.getQueriedJobs(description, location).enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                loadResponse(response);
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                showConnectionError();
            }
        });
    }

    private void initViews() {
        mJobRecycler = findViewById(R.id.jobs_recycler);
        loader = findViewById(R.id.jobs_loader);
        btnSearch = findViewById(R.id.btn_search);
        edLocation = findViewById(R.id.search_location);
        edDescription = findViewById(R.id.search_desc);
    }

    private void setUpRecycler() {
        mJobRecycler.setLayoutManager(new LinearLayoutManager(this));
        mJobRecycler.setHasFixedSize(true);

        loader.setVisibility(View.VISIBLE);
        apiClient.getAvailableJobs().enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                initialLoad = true;
                loadResponse(response);
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                showConnectionError();
            }
        });
    }

    private void loadResponse(Response<List<Job>> response) {
        mJobList.clear();
        if (response.isSuccessful() && response.body() != null) {
            loader.setVisibility(View.GONE);
            mJobList.addAll(response.body());
            mJobsAdapter.setJobData(mJobList);
            mJobRecycler.setAdapter(mJobsAdapter);
        } else {
            loader.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConnectionError() {
        loader.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void jobClicked(Job job, View view) {
        switch (view.getId()) {
            case R.id.btn_job_details:
            case R.id.txt_job_link:
                openWebView(job);
                break;
        }
    }

    private void openWebView(Job job) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(job.getJobUrl()));
        startActivity(intent);
    }
}