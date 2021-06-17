package com.shadhinlab.reminder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MPrayer;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.models.MTime;
import com.shadhinlab.reminder.service.PhoneCallStatesService;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.MyApp;
import com.shadhinlab.reminder.tools.PermissionUtils;
import com.shadhinlab.reminder.tools.Utils;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int PERMISSION_ID = 44, LOCATION_SETTINGS = 1, PERMISSIONS_REQUEST_PHONE_CALL = 2, PERMISSIONS_REQUEST_PHONE_STATE = 3;
    private TextView tvFazarTime, tvDuhurTime, tvAsarTime, tvMagribTime, tvIshaTime;
    private TextView tvSetFajrTime, tvSetDhuhrTime, tvSetAsrTime, tvSetMaghribTime, tvSetIshaTime;
    private MyDatabase myDatabase;
    private MPrayer mPrayer;
    private MTime mTime;
    private MPrayerTime mPrayerTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog myProgressBar;
    private MyAlarmManager myAlarmManager;
    private LocationCallback mLocationCallback;
    private Toolbar toolbar;
    private int prayerMethod = 1;
    private List<String> reqPerm;
    private Button btnAutoCall;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        phoneState();
//        directCall();
        clickListener();
//        PermissionUtils.displayLocationSettingsRequest(this, this, PERMISSION_ID);
        getCurrentPrayerTime();
        getLocation();
        locationCallBack();
        long unixTime = System.currentTimeMillis() / 1000L;

        Utils.log("unixTime: " + Utils.getYear());
        List<MAlarm> alarmList = myDatabase.myDao().getAllAlarmDetails();
//        List<MAlarm> alarmList = myDatabase.myDao().getAlarmByWakto(3, 170582503);
        Utils.log("Alarm size: " + alarmList.size());
        for (int i = 0; i < alarmList.size(); i++) {
            Utils.log("DB: " + alarmList.get(i).getPendingID() + " : " + alarmList.get(i).getPrayerWakto());
        }
        if (mPrayerTime != null)
            enableClickListener(true);
        else enableClickListener(false);


    }

    private void init() {
        prayerMethod = Utils.getPref(Global.PRAYER_METHOD, 1);
        toolbar = findViewById(R.id.toolbar);
        tvFazarTime = findViewById(R.id.tvFajrTime);
        btnAutoCall = findViewById(R.id.btnAutoCall);
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
        mPrayer = new MPrayer();
        mTime = new MTime();
        setSupportActionBar(toolbar);

    }

    private void phoneCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
        }

//        else {
//
//            //Open call function
//            String phone = "1955206144";
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:+880" + phone));
//            startActivity(intent);
//
//
//        }


    }

    public void phoneStatePermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS}, PERMISSIONS_REQUEST_PHONE_STATE);
        } else {
            Utils.log("already phoneStatePermission");
            Utils.showToast("already phoneStatePermission");
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
//        registerReceiver(new PhoneStateReceiver(), filter);
//            Intent i = new Intent(MainActivity.this, PhoneCallStatesService.class);
//            startService(i);

        }
    }


    private boolean checkPermission() {
        int i = 0;
        String[] perm = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS};
        reqPerm = new ArrayList<>();

        for (String permis : perm) {
            int resultPhone = ContextCompat.checkSelfPermission(MainActivity.this, permis);
            if (resultPhone == PackageManager.PERMISSION_GRANTED) {
                i++;

            } else {
                reqPerm.add(permis);
            }
        }
        if (i == 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean requestPermission(List<String> perm) {
        String[] listReq = new String[perm.size()];
        listReq = perm.toArray(listReq);
        ActivityCompat.requestPermissions(MainActivity.this, listReq, PERMISSIONS_REQUEST_PHONE_STATE);


        return false;
    }

    private void clickListener() {
        tvSetFajrTime.setOnClickListener(this);
        tvSetDhuhrTime.setOnClickListener(this);
        tvSetAsrTime.setOnClickListener(this);
        tvSetMaghribTime.setOnClickListener(this);
        tvSetIshaTime.setOnClickListener(this);
        btnAutoCall.setOnClickListener(this);
    }

    private void enableClickListener(boolean isEnable) {
        tvSetFajrTime.setEnabled(isEnable);
        tvSetDhuhrTime.setEnabled(isEnable);
        tvSetAsrTime.setEnabled(isEnable);
        tvSetMaghribTime.setEnabled(isEnable);
        tvSetIshaTime.setEnabled(isEnable);
    }


    @SuppressLint("SetTextI18n")
    private void display() {
        mPrayerTime = myDatabase.myDao().getPrayerTimesByDate(Utils.getDate());
//        tvFazarTime.setText(mTime.getFajr().split(" ")[0] + "-" + mTime.getSunrise().split(" ")[0]);
//        tvDuhurTime.setText(mTime.getDhuhr().split(" ")[0] + "-" + mTime.getAsr().split(" ")[0]);
//        tvAsarTime.setText(mTime.getAsr().split(" ")[0] + "-" + mTime.getMaghrib().split(" ")[0]);
//        tvMagribTime.setText(mTime.getMaghrib().split(" ")[0] + "-" + mTime.getIsha().split(" ")[0]);
//        tvIshaTime.setText(mTime.getIsha().split(" ")[0] + "-" + mTime.getFajr().split(" ")[0]);
        if (mPrayerTime != null) {
            tvFazarTime.setText(mPrayerTime.getStartFajr() + "-" + mPrayerTime.getEndtFajr());
            tvDuhurTime.setText(mPrayerTime.getStartDhuhr() + "-" + mPrayerTime.getEndDhuhr());
            tvAsarTime.setText(mPrayerTime.getStartAsr() + "-" + mPrayerTime.getEndAsr());
            tvMagribTime.setText(mPrayerTime.getStartMaghrib() + "-" + mPrayerTime.getEndMaghrib());
            tvIshaTime.setText(mPrayerTime.getStartIsha() + "-" + mPrayerTime.getEndIsha());
        }

    }


    private void getSunriseTimeOther(double lat, double lng) {
        Utils.log("getSunriseTimeOther");
        Call<MPrayer> call = ApiClient.getInstance().getPrayerTimes(lat, lng, prayerMethod, Utils.getMonth(), Utils.getYear());
        call.enqueue(new Callback<MPrayer>() {
            @Override
            public void onResponse(@NonNull Call<MPrayer> call, @NonNull Response<MPrayer> response) {
                myProgressBar.dismiss();
                Utils.log("onResponse");
                myDatabase.myDao().savePrayer(response.body());
                mPrayer = myDatabase.myDao().getPrayer();
                savePrayer(response.body());
                getCurrentPrayerTime();
                if (mPrayerTime != null)
                    enableClickListener(true);
                else enableClickListener(false);
                Log.e("Data", "Is: " + mPrayer.getData().get(0).getDate().getReadable());
            }

            @Override
            public void onFailure(@NonNull Call<MPrayer> call, @NonNull Throwable t) {
                Utils.log("onFailure: " + t.getMessage());
                myProgressBar.dismiss();
            }
        });
    }

    private void savePrayer(MPrayer prayer) {
        MPrayerTime mPrayerTime;
        if (prayer != null && prayer.getData() != null && prayer.getData().size() > 0) {
            for (int i = 0; i < prayer.getData().size(); i++) {
                mPrayerTime = new MPrayerTime();
                mPrayerTime.setDate(prayer.getData().get(i).getDate().getReadable());
                mPrayerTime.setStartFajr(prayer.getData().get(i).getTimings().getFajr().split(" ")[0]);
                mPrayerTime.setEndtFajr(prayer.getData().get(i).getTimings().getSunrise().split(" ")[0]);
                mPrayerTime.setStartDhuhr(prayer.getData().get(i).getTimings().getDhuhr().split(" ")[0]);
                mPrayerTime.setEndDhuhr(prayer.getData().get(i).getTimings().getAsr().split(" ")[0]);
                mPrayerTime.setStartAsr(prayer.getData().get(i).getTimings().getAsr().split(" ")[0]);
                mPrayerTime.setEndAsr(prayer.getData().get(i).getTimings().getMaghrib().split(" ")[0]);
                mPrayerTime.setStartMaghrib(prayer.getData().get(i).getTimings().getMaghrib().split(" ")[0]);
                mPrayerTime.setEndMaghrib(prayer.getData().get(i).getTimings().getIsha().split(" ")[0]);
                mPrayerTime.setStartIsha(prayer.getData().get(i).getTimings().getIsha().split(" ")[0]);
                mPrayerTime.setEndIsha(prayer.getData().get(i).getTimings().getFajr().split(" ")[0]);
                mPrayerTime.setSunrise(prayer.getData().get(i).getTimings().getSunrise().split(" ")[0]);
                mPrayerTime.setSunset(prayer.getData().get(i).getTimings().getSunset().split(" ")[0]);
                myDatabase.myDao().savePrayerTimes(mPrayerTime);
            }
        }

    }

    private boolean getCurrentPrayerTime() {
        if (myDatabase.myDao().getPrayer() != null && myDatabase.myDao().getPrayer().getData() != null && myDatabase.myDao().getPrayer().getData().size() > 0) {
            for (int i = 0; i < myDatabase.myDao().getPrayer().getData().size(); i++) {
                if (myDatabase.myDao().getPrayer().getData().get(i).getDate().getReadable().equals(Utils.getDate())) {
                    Utils.log("Pos: " + myDatabase.myDao().getPrayer().getData().get(i).getDate().getReadable());
                    Utils.log("Pos 2 : " + myDatabase.myDao().getPrayer().getData().get(i).getTimings().getIsha());
                    mTime = myDatabase.myDao().getPrayer().getData().get(i).getTimings();
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
                            } else {
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
                if (Utils.isInternetOn()) {
                    phoneCallPermission();

                    getLocation();
                }
//                if (Utils.isInternetOn()) getLastLocation();
                else Utils.showToast("No internet");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.log("Phone permission granted");
                if (Utils.isInternetOn()) {
                    phoneStatePermission();
                }
            }


        } else if (requestCode == PERMISSIONS_REQUEST_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.log("Phone state permission granted");
                if (Utils.isInternetOn()) {
                    phoneStatePermission();
                }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onRestart() {
        super.onRestart();
        prayerMethod = Utils.getPref(Global.PRAYER_METHOD, 1);
        Utils.log("PrayerMethod: " + prayerMethod);
        getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToReminderActivity(int prayerWakto, String prayerStartTime, String prayerEndTime, String prayerWaktoName) {
        startActivity(new Intent(this, SetReminderActivity.class)
                .putExtra(Global.PRAYER_WAKTO, prayerWakto)
                .putExtra(Global.PRAYER_START_TIME, prayerStartTime)
                .putExtra(Global.PRAYER_END_TIME, prayerEndTime)
                .putExtra(Global.PRAYER_WAKTO_NAME, prayerWaktoName));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSetFajrTime:
                goToReminderActivity(1, mPrayerTime.getStartFajr(),
                        mPrayerTime.getEndtFajr(), "Fajr");
                break;
            case R.id.tvSetDhuhrTime:
                goToReminderActivity(2, mPrayerTime.getStartDhuhr(),
                        mPrayerTime.getEndDhuhr(), "Dhuhr");
                break;
            case R.id.tvSetAsrTime:
                goToReminderActivity(3, mPrayerTime.getStartAsr(),
                        mPrayerTime.getEndAsr(), "Asr");
                break;
            case R.id.tvSetMaghribTime:
                goToReminderActivity(4, mPrayerTime.getStartMaghrib(),
                        mPrayerTime.getEndMaghrib(), "Maghrib");
                break;
            case R.id.tvSetIshaTime:
                goToReminderActivity(5, mPrayerTime.getStartIsha(),
                        mPrayerTime.getEndIsha(), "Isha");
                break;
            case R.id.btnAutoCall:
//                Utils.log("Minute: " + Utils.getMinute());
//                myAlarmManager.setTestAlarm(0, 0, Utils.getMinute() + 1, 0, 1, 123, "Call", false);
//                //Open call function
//                Utils.call("0191355565");
                startActivity(new Intent(MainActivity.this, SetReminderCallActivity.class)
                        .putExtra(Global.PRAYER_START_TIME, mPrayerTime.getStartFajr()));
                break;
        }
    }
}