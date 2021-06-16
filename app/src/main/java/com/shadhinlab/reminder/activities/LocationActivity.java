package com.shadhinlab.reminder.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.models.MPrayer;
import com.shadhinlab.reminder.network.ApiClient;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.PermissionUtils;
import com.shadhinlab.reminder.tools.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "Address";
    private int PERMISSION_ID = 44, LOCATION_SETTINGS = 1;
    private String sunriseTime = "", currentTime = "", newAlarm = "";
    private int pickTime = 0, alarmListID, alarmListPos;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private ProgressDialog myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Utils.statusBarTransparent(this);
        Utils.changeStatusBarOthers(this, Color.TRANSPARENT);
        currentTime = Utils.getCurrentTime();
        init();
        locationCallBack();
        getLastLocation();
    }


    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myProgressBar = new ProgressDialog(this);
    }


    private void locationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                getSunriseTimeOther(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                Utils.log("onLocationResult Lat : " + mLastLocation.getLatitude() + " Lng : " + mLastLocation.getLongitude());
            }
        };
    }


    private void getSunriseTimeOther(double lat, double lng) {
        Utils.log("Current Date : " + Utils.getTodaysDate());
        myProgressBar.setMessage("Detecting your location....");
        myProgressBar.setCanceledOnTouchOutside(false);
        myProgressBar.show();
        Call<MPrayer> call = ApiClient.getInstance().getPrayerTimes(lat, lng, 2, 6, "2021");
        call.enqueue(new Callback<MPrayer>() {
            @Override
            public void onResponse(@NonNull Call<MPrayer> call, @NonNull Response<MPrayer> response) {
                myProgressBar.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Utils.showToast(Global.TIME_UPDATED);


                    Utils.savePref(Global.PICK_LOCATION_DATE, Utils.getTodaysDate());
                    Utils.savePref(Global.PICK_LOCATION_ONLY_AM_PM, Utils.getTodaysOnlyAmPm());
                    Utils.savePref(Global.PICK_LOCATION_DATE_TIME_AM, Utils.getTodaysDateTimeNew() + " | " + Utils.getTodaysTime());
                    sunriseTime = Utils.getPref(Global.SUNRISE_TODAY, "");
                    String lastUpdateTime = Utils.getPref(Global.PICK_LOCATION_DATE_TIME_AM, "");
                    String lastUpdateAmPm = Utils.getPref(Global.PICK_LOCATION_ONLY_AM_PM, "");


                    if (!TextUtils.isEmpty(sunriseTime)) {
                        String[] separated = sunriseTime.split(":");
                        int time = Integer.parseInt(separated[0]);
                        String time2 = separated[1];

                        int time3 = Integer.parseInt(time2.split(" ")[0]);

                        Utils.log("Time : " + time + " : " + time3);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MPrayer> call, @NonNull Throwable t) {
                myProgressBar.dismiss();
                Utils.log("Retrofit onFailure : " + t.getMessage());
                Utils.showToast(t.getMessage());
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (PermissionUtils.checkPermissions(this)) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                Utils.log("Lat : " + location.getLatitude() + " Lng : " + location.getLongitude());
                                getSunriseTimeOther(location.getLatitude(), location.getLongitude());
                            }
                        }
                );
            } else {
                PermissionUtils.displayLocationSettingsRequest(LocationActivity.this, this, LOCATION_SETTINGS);
            }
        } else {
            PermissionUtils.requestPermissions(this, PERMISSION_ID);
        }
    }


    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Utils.isInternetOn()) getLastLocation();
                else Utils.showToast("No internet");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Utils.log("PERMISSION_GRANTED");
                if (Utils.isInternetOn()) getLastLocation();
                else Utils.showToast("No internet");
            } else {
                Utils.log("PERMISSION CANCEL");
            }

        }
    }




}