package com.shadhinlab.reminder.network;

import com.shadhinlab.reminder.models.MCallHijriCalender;
import com.shadhinlab.reminder.models.MHijriCalender;
import com.shadhinlab.reminder.models.MPrayer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    public static final long unixTime = System.currentTimeMillis() / 1000L;

    @GET("v1/calendar")
    Call<MPrayer> getPrayerTimes(@Query("latitude") double latitude,
                                 @Query("longitude") double longitude,
                                 @Query("method") int method,
                                 @Query("month") int month,
                                 @Query("year") String year);

    @GET("timings/1623562097")
    Call<MPrayer> getCurrentDayPrayerTimes(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method);

    @GET("v1/gToHCalendar/{month}/{year}")
    Call<MCallHijriCalender> getHijriMonth(@Path("month") int month,
                                           @Path("year") int year,
                                           @Query("adjustment") int adjustment);
}

