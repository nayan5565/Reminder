package com.shadhinlab.reminder.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.PermissionUtils;
import com.shadhinlab.reminder.tools.Utils;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private int PERMISSION_ID = 44, LOCATION_SETTINGS = 1;
    private TextView tvFazarTime, tvDuhurTime, tvAsarTime, tvMagribTime, tvIshaTime;
    private MyDatabase myDatabase;

    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog myProgressBar;
    private MyAlarmManager myAlarmManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFazarTime = findViewById(R.id.tvFazarTime);
        tvIshaTime = findViewById(R.id.tvIshaTime);
        tvDuhurTime = findViewById(R.id.tvDuhurTime);
        tvAsarTime = findViewById(R.id.tvAsarTime);
        tvMagribTime = findViewById(R.id.tvMagribTime);
        myProgressBar = new ProgressDialog(this);
        myDatabase = MyDatabase.getInstance(this);
        myAlarmManager = new MyAlarmManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        PermissionUtils.displayLocationSettingsRequest(this, this, PERMISSION_ID);
        getLocation();
        long unixTime = System.currentTimeMillis() / 1000L;
        Utils.log("unixTime: " + Utils.getMonth());
        Utils.log("unixTime: " + Utils.getYear());
//        getPos();
        myAlarmManager.setTestAlarm(0, 0, 10, 0, 123, "", false);
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
                MPrayerTime mPrayerTime = myDatabase.myDao().getPrayerTimes();
                Log.e("Data", "Is: " + mPrayerTime.getData().get(0).getDate().getReadable());
                tvFazarTime.setText(mPrayerTime.getData().get(0).getDate().getReadable() + " " + mPrayerTime.getData().get(0).getTimings().getFajr().split(" ")[0]);
            }

            @Override
            public void onFailure(@NonNull Call<MPrayerTime> call, @NonNull Throwable t) {
                Utils.log("onFailure: " + t.getMessage());
                myProgressBar.dismiss();
            }
        });
    }

    private int getPos() {
        int pos = 0;
        for (int i = 0; i < myDatabase.myDao().getPrayerTimes().getData().size(); i++) {
            if (myDatabase.myDao().getPrayerTimes().getData().get(i).getDate().getReadable().equals(Utils.getDate())) {
                pos = i;
                Utils.log("Pos: " + myDatabase.myDao().getPrayerTimes().getData().get(i).getDate().getReadable());
                Utils.log("Pos 2 : " + myDatabase.myDao().getPrayerTimes().getData().get(i).getTimings().getIsha());
            }
        }
        return pos;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        Utils.log("getLocation");
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
                            }
                        });
            } else {
                Utils.log("isLocationEnabled else");
                PermissionUtils.displayLocationSettingsRequest(MainActivity.this, this, LOCATION_SETTINGS);
            }

        } else {
            Utils.log("requestPermissions");
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

}