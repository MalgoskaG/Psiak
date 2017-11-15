package com.example.android.psiak;

import android.app.Application;

import com.example.android.psiak.Utils.AutoValueGsonFactory;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DoggieApplication extends Application {

    Retrofit retrofit;

    public Retrofit getRetrofitInstance() {
        if(retrofit != null) {
            return retrofit;
        }
        String baseUrl = "https://api.myjson.com";
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(this.getCacheDir(),cacheSize);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache).build();

        GsonConverterFactory  gsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder().registerTypeAdapterFactory(AutoValueGsonFactory.create())
                        .create());


        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(gsonConverterFactory)
                .build();
        return retrofit;
    }

    @Override
    public void onCreate() {
        retrofit = null;
        super.onCreate();
    }
}