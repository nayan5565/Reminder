package com.shadhinlab.reminder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.models.MTime;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.PermissionUtils;
import com.shadhinlab.reminder.tools.Utils;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int PERMISSION_ID = 44, LOCATION_SETTINGS = 1;
    private TextView tvFazarTime, tvDuhurTime, tvAsarTime, tvMagribTime, tvIshaTime;
    private TextView tvSetFajrTime, tvSetDhuhrTime, tvSetAsrTime, tvSetMaghribTime, tvSetIshaTime;
    private MyDatabase myDatabase;
    private MPrayerTime mPrayerTime;
    private MTime mTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog myProgressBar;
    private MyAlarmManager myAlarmManager;
    private LocationCallback mLocationCallback;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        clickListener();
//        PermissionUtils.displayLocationSettingsRequest(this, this, PERMISSION_ID);
        getCurrentPrayerTime();
        getLocation();
        locationCallBack();
        long unixTime = System.currentTimeMillis() / 1000L;
        Utils.log("unixTime: " + Utils.getMonth());
        Utils.log("unixTime: " + Utils.getYear());

//        myAlarmManager.setTestAlarm(0, 0, 10, 0, 123, "", false);
    }

    private void init() {
        tvFazarTime = findViewById(R.id.tvFajrTime);
        tvIshaTime = findViewById(R.id.tvIshaTime);
        tvDuhurTime = findViewById(R.id.tvDhuhrTime);
        tvAsarTime = findViewById(R.id.tvAsrTime);
        tvMagribTime = findViewById(R.id.tvMaghribTime);
        tvSetFajrTime = findViewById(R.id.tvSetFajrTime);
        tvSetDhuhrTime = findViewById(R.id.tvSetDhuhrTime);
        tvSetAsrTime = findViewById(R.id.tvSetAsrTime);
        tvSetMaghribTime = findViewById(R.id.tvSetMaghribTime);
        tvSetIshaTime = findViewById(R.id.tvSetIshaTime);
        myProgressBar = new ProgressDialog(this);
        myDatabase = MyDatabase.getInstance(this);
        myAlarmManager = new MyAlarmManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPrayerTime = new MPrayerTime();
        mTime = new MTime();
    }

    private void clickListener() {
        tvSetFajrTime.setOnClickListener(this);
        tvSetDhuhrTime.setOnClickListener(this);
        tvSetAsrTime.setOnClickListener(this);
        tvSetMaghribTime.setOnClickListener(this);
        tvSetIshaTime.setOnClickListener(this);
    }

    private void display() {
        tvFazarTime.setText(mTime.getFajr().split(" ")[0]);
        tvDuhurTime.setText(mTime.getDhuhr().split(" ")[0]);
        tvAsarTime.setText(mTime.getAsr().split(" ")[0]);
        tvMagribTime.setText(mTime.getMaghrib().split(" ")[0]);
        tvIshaTime.setText(mTime.getIsha().split(" ")[0]);
    }


    private void getSunriseTimeOther(double lat, double lng) {
        Utils.log("getSunriseTimeOther");
        Call<MPrayerTime> call = ApiClient.getInstance().getPrayerTimes(lat, lng, 1, Utils.getMonth(), Utils.getYear());
        call.enqueue(new Callback<MPrayerTime>() {
            @Override
            public void onResponse(@NonNull Call<MPrayerTime> call, @NonNull Response<MPrayerTime> response) {
                myProgressBar.dismiss();
                Utils.log("onResponse");
                myDatabase.myDao().savePrayerTimes(response.body());
                mPrayerTime = myDatabase.myDao().getPrayerTimes();
                getCurrentPrayerTime();
                Log.e("Data", "Is: " + mPrayerTime.getData().get(0).getDate().getReadable());
            }

            @Override
            public void onFailure(@NonNull Call<MPrayerTime> call, @NonNull Throwable t) {
                Utils.log("onFailure: " + t.getMessage());
                myProgressBar.dismiss();
            }
        });
    }

    private boolean getCurrentPrayerTime() {
        if (myDatabase.myDao().getPrayerTimes() != null && myDatabase.myDao().getPrayerTimes().getData() != null && myDatabase.myDao().getPrayerTimes().getData().size() > 0) {
            for (int i = 0; i < myDatabase.myDao().getPrayerTimes().getData().size(); i++) {
                if (myDatabase.myDao().getPrayerTimes().getData().get(i).getDate().getReadable().equals(Utils.getDate())) {
                    Utils.log("Pos: " + myDatabase.myDao().getPrayerTimes().getData().get(i).getDate().getReadable());
                    Utils.log("Pos 2 : " + myDatabase.myDao().getPrayerTimes().getData().get(i).getTimings().getIsha());
                    mTime = myDatabase.myDao().getPrayerTimes().getData().get(i).getTimings();
                    display();
                    return true;
                }
            }

        }

        return false;
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
            Utils.log("requestNewLocationData");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        myProgressBar.setMessage("Detect your location....");
        myProgressBar.setCanceledOnTouchOutside(false);
        myProgressBar.show();
        if (PermissionUtils.checkPermissions(this)) {
            Utils.log("checkPermissions");
            if (isLocationEnabled()) {
                Utils.log("isLocationEnabled");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Utils.log("getLocation 2");
                    myProgressBar.dismiss();
                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            Utils.log("getLocation 3");
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                getSunriseTimeOther(location.getLatitude(), location.getLongitude());
                                Log.e("Location", "mFusedLocationClient Lat : " + location.getLatitude() + " Lng : " + location.getLongitude());
                            }else {
                                Utils.log("getLocation null");
                                requestNewLocationData();
                            }
                        });
            } else {
                Utils.log("isLocationEnabled else");
                myProgressBar.dismiss();
                PermissionUtils.displayLocationSettingsRequest(MainActivity.this, this, LOCATION_SETTINGS);
            }

        } else {
            Utils.log("requestPermissions");
            myProgressBar.dismiss();
            PermissionUtils.requestPermissions(this, PERMISSION_ID);
        }

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            Utils.log("onRequestPermissionsResult");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.log("PermissionsResult granted");
                if (Utils.isInternetOn()) getLocation();
//                if (Utils.isInternetOn()) getLastLocation();
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
                if (Utils.isInternetOn()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getLocation();
                } else Utils.showToast("No internet");
            } else {
                Utils.log("PERMISSION CANCEL");
            }

        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSetFajrTime:
                startActivity(new Intent(this, SetReminderActivity.class).putExtra("PrayerWakto", 1).putExtra("PrayerTime", mTime.getFajr().split(" ")[0]));
                break;
            case R.id.tvSetDhuhrTime:
                startActivity(new Intent(this, SetReminderActivity.class).putExtra("PrayerWakto", 2).putExtra("PrayerTime", mTime.getDhuhr().split(" ")[0]));
                break;
            case R.id.tvSetAsrTime:
                startActivity(new Intent(this, SetReminderActivity.class).putExtra("PrayerWakto", 3).putExtra("PrayerTime", mTime.getAsr().split(" ")[0]));
                break;
            case R.id.tvSetMaghribTime:
                startActivity(new Intent(this, SetReminderActivity.class).putExtra("PrayerWakto", 4).putExtra("PrayerTime", mTime.getMaghrib().split(" ")[0]));
                break;
            case R.id.tvSetIshaTime:
                startActivity(new Intent(this, SetReminderActivity.class).putExtra("PrayerWakto", 5).putExtra("PrayerTime", mTime.getIsha().split(" ")[0]));
                break;
        }
    }
}