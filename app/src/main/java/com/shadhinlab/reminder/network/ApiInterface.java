package com.shadhinlab.reminder.network;

import com.shadhinlab.reminder.models.MPrayerTime;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    public static final long unixTime = System.currentTimeMillis() / 1000L;

    @GET("v1/calendar")
    Call<MPrayerTime> getPrayerTimes(@Query("latitude") double latitude,
                                     @Query("longitude") double longitude,
                                     @Query("method") int method,
                                     @Query("month") int month,
                                     @Query("year") String year);

    @GET("timings/1623562097")
    Call<MPrayerTime> getCurrentDayPrayerTimes(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method);
}

