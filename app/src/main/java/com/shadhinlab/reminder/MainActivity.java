package com.shadhinlab.reminder;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shadhinlab.reminder.Tools.GPSTracker;
import com.shadhinlab.reminder.Tools.PermissionUtils;
import com.shadhinlab.reminder.Tools.Utils;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private int PERMISSION_ID = 44, LOCATION_SETTINGS = 1;
    private TextView tvTime;
    private MyDatabase myDatabase;

    private String locationName = "";
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog myProgressBar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        GPSTracker gpsTracker = new GPSTracker(this);
        tvTime = findViewById(R.id.tvTime);
        myProgressBar = new ProgressDialog(this);
        myDatabase = MyDatabase.getInstance(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        Utils.log("longitude " + longitude);
        getLocation();
        locationCallBack();


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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, (android.location.LocationListener) locationListener);
//        getSunriseTimeOther(23.7671267, 90.3605818);
    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Utils.log("longitude " + location.getLongitude());
        }
    };

    private void locationCallBack() {
        Utils.log("locationCallBack");
        myProgressBar.setMessage("Detect your location....");
        myProgressBar.setCanceledOnTouchOutside(false);
        myProgressBar.show();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Utils.log("onLocationResult");
                Location mLastLocation = locationResult.getLastLocation();
                displayLocation();
                getSunriseTimeOther(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                Log.e("Location", "onLocationResult Lat : " + mLastLocation.getLatitude() + " Lng : " + mLastLocation.getLongitude());
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Utils.log("onLocationAvailability");
            }
        };
    }

    private void displayLocation() {
        if (locationName.contains(",")) {
//            tvDiv.setText(locationName.substring(0, locationName.lastIndexOf(",")));
//            tvCity.setText(locationName.substring(locationName.lastIndexOf(",") + 1));
        }

    }


    private void getSunriseTimeOther(double lat, double lng) {
        Call<MPrayerTime> call = ApiClient.getInstance().getPrayerTimes(lat, lng, 1, 6, "2021");
        call.enqueue(new Callback<MPrayerTime>() {
            @Override
            public void onResponse(@NonNull Call<MPrayerTime> call, @NonNull Response<MPrayerTime> response) {
                myProgressBar.dismiss();
                myDatabase.myDao().savePrayerTimes(response.body());
                MPrayerTime mPrayerTime = myDatabase.myDao().getPrayerTimes();
                Log.e("Data", "Is: " + mPrayerTime.getData().get(0).getDate().getReadable());
                tvTime.setText(mPrayerTime.getData().get(0).getDate().getReadable() + " " + mPrayerTime.getData().get(0).getTimings().getFajr().split(" ")[0]);
            }

            @Override
            public void onFailure(@NonNull Call<MPrayerTime> call, @NonNull Throwable t) {
                myProgressBar.dismiss();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLastLocation() {

        if (PermissionUtils.checkPermissions(this)) {
            if (isLocationEnabled()) {
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
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                displayLocation();
                                getSunriseTimeOther(location.getLatitude(), location.getLongitude());
                            }
                        }
                );
            } else {
                PermissionUtils.displayLocationSettingsRequest(MainActivity.this, this, LOCATION_SETTINGS);
            }
        } else {
            PermissionUtils.requestPermissions(this, PERMISSION_ID);
        }
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
                                Log.e("Location", "onLocationResult Lat : " + location.getLatitude() + " Lng : " + location.getLongitude());
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
                if (Utils.isInternetOn()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getLastLocation();
                } else Utils.showToast("No internet");
            } else {
                Utils.log("PERMISSION CANCEL");
            }

        }

    }

}