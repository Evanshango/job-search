package com.evans.jobs.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    public static final String BASE_URL = "https://jobs.github.com/";
    public static Retrofit sRetrofit = null;

    private static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient sOkHttpClient = logging(logging);

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(sOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }

    private static OkHttpClient logging(HttpLoggingInterceptor logging) {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    public static ApiClient getApiClient() {
        return getRetrofit().create(ApiClient.class);
    }
}
