package com.shadhinlab.reminder.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static ApiInterface apiRequests;
    private static String baseUrlPrayerTime = "http://api.aladhan.com/";

    public static ApiInterface getInstance() {
        if (apiRequests == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrlPrayerTime)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiRequests = retrofit.create(ApiInterface.class);

        }
        return apiRequests;
    }
}
