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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.shadhinlab.reminder.activities.calender.MyCalendarView;
import com.shadhinlab.reminder.models.MAlarm;
import com.shadhinlab.reminder.models.MArabicEnglishMonth;
import com.shadhinlab.reminder.models.MCallHijriCalender;
import com.shadhinlab.reminder.models.MHijriCalender;
import com.shadhinlab.reminder.models.MPrayer;
import com.shadhinlab.reminder.models.MPrayerTime;
import com.shadhinlab.reminder.models.MTime;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.MyAlarmManager;
import com.shadhinlab.reminder.tools.PermissionUtils;
import com.shadhinlab.reminder.tools.Utils;
import com.shadhinlab.reminder.db.MyDatabase;
import com.shadhinlab.reminder.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyCalendarView.OnDateSetListener {
    public static boolean isSettings = false;
    private int PERMISSIONS_REQUEST_LOCATION = 44, LOCATION_SETTINGS = 1, PERMISSIONS_REQUEST_PHONE_CALL = 2, PERMISSIONS_REQUEST_PHONE_STATE = 3;
    private TextView tvFazarTime, tvDuhurTime, tvAsarTime, tvMagribTime, tvSet, tvSet2, tvSet3,
            tvIshaTime, tvEnDate, tvEnDate2, tvEnDate3, tvArDate, tvArDate2, tvArDate3;
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
    List<MArabicEnglishMonth> arabicEnglishMonths;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.log("ct: " + Utils.getTodaysTime24Fomat());
        Utils.log("Diff: " + Utils.calculateMinutes("03:44", Utils.getTodaysTime24Fomat()));
        init();
        clickListener();
        getCurrentPrayerTime();
        locationPermission();
        locationCallBack();
        Utils.log("Save Day: " + Utils.getPref(Global.SAVE_DAY, 0) + " : " + Utils.getDay());
        if (Utils.getPref(Global.SAVE_MONTH, 0) < Utils.getMonth()
                || Utils.getPref(Global.SAVE_DAY, 0) < Utils.getDay()) {

            if (Utils.getPref(Global.SAVE_MONTH, 0) == Utils.getMonth()
                    && Utils.getPref(Global.SAVE_DAY, 0) < Utils.getDay()) {
                Utils.log("get Hijri month is equal but day is smaller than current day");
                getHijriCalender(Utils.getMonth() + 1);
            } else {
                Utils.log("get Hijri month is smaller than current month");
                getHijriCalender(Utils.getMonth());
            }

        } else Utils.log("Already get Hijri");

        displayHijriCalender();
        long unixTime = System.currentTimeMillis() / 1000L;
//        myAlarmManager.setAlarmDateWise(Utils.getMonthFromCalender(), 16, 10, 10, 0, 0, 123, "", "", false);

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
        tvIshaTime = findViewById(R.id.tvIshaTime);
        tvDuhurTime = findViewById(R.id.tvDhuhrTime);
        tvAsarTime = findViewById(R.id.tvAsrTime);
        tvMagribTime = findViewById(R.id.tvMaghribTime);
        tvSetFajrTime = findViewById(R.id.tvSetFajrTime);
        tvSetDhuhrTime = findViewById(R.id.tvSetDhuhrTime);
        tvSetAsrTime = findViewById(R.id.tvSetAsrTime);
        tvSetMaghribTime = findViewById(R.id.tvSetMaghribTime);
        tvSetIshaTime = findViewById(R.id.tvSetIshaTime);
        tvEnDate = findViewById(R.id.tvEnDate);
        tvEnDate2 = findViewById(R.id.tvEnDate2);
        tvEnDate3 = findViewById(R.id.tvEnDate3);
        tvArDate = findViewById(R.id.tvArDate);
        tvArDate2 = findViewById(R.id.tvArDate2);
        tvArDate3 = findViewById(R.id.tvArDate3);
        tvSet = findViewById(R.id.tvSet);
        tvSet2 = findViewById(R.id.tvSet2);
        tvSet3 = findViewById(R.id.tvSet3);
        myProgressBar = new ProgressDialog(this);
        myDatabase = MyDatabase.getInstance(this);
        myAlarmManager = new MyAlarmManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPrayer = new MPrayer();
        mTime = new MTime();
        arabicEnglishMonths = new ArrayList<>();
        setSupportActionBar(toolbar);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void locationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
            getLocation();
        }

    }

    private void phoneCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
        }

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
        tvSet.setOnClickListener(this);
        tvSet2.setOnClickListener(this);
        tvSet3.setOnClickListener(this);
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


    private void getNamzTimes(double lat, double lng) {
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

    private void getHijriCalender(int month) {
        Utils.log("Month: " + month);
        Call<MCallHijriCalender> call = ApiClient.getInstance().getHijriMonth(month, Integer.parseInt(Utils.getYear()), 0);
        call.enqueue(new Callback<MCallHijriCalender>() {
            @Override
            public void onResponse(@NonNull Call<MCallHijriCalender> call, @NonNull Response<MCallHijriCalender> response) {
                if (response.body() != null && response.body().getData().size() > 0) {

                    saveHijriCalender(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MCallHijriCalender> call, @NonNull Throwable t) {
                Utils.log("getHijriCalender onFailure: " + t.getMessage());
            }
        });
    }

    private void saveHijriCalender(List<MHijriCalender> hijriCalenders) {
        myDatabase.myDao().clearHijriCalender();
        for (int i = 0; i < hijriCalenders.size(); i++) {
            if (hijriCalenders.get(i).getHijri().getDay().equals("13")
                    || hijriCalenders.get(i).getHijri().getDay().equals("14")
                    || hijriCalenders.get(i).getHijri().getDay().equals("15")) {
                MArabicEnglishMonth arabicEnglishMonth = new MArabicEnglishMonth();
                arabicEnglishMonth.setEnDate(hijriCalenders.get(i).getGregorian().getDate());
                arabicEnglishMonth.setEnDay(Integer.parseInt(hijriCalenders.get(i).getGregorian().getDay()));
                arabicEnglishMonth.setEnMonth(Integer.parseInt(hijriCalenders.get(i).getGregorian().getDate().split("-")[1]));
                arabicEnglishMonth.setArDate(hijriCalenders.get(i).getHijri().getDate());
                arabicEnglishMonth.setArDay(hijriCalenders.get(i).getHijri().getDay());
                arabicEnglishMonth.setArYear(hijriCalenders.get(i).getHijri().getYear());
                Utils.log("Pick Date: " + hijriCalenders.get(i).getGregorian().getDate());
                myDatabase.myDao().saveHijriCalender(arabicEnglishMonth);
            }

        }
        displayHijriCalender();
    }


    private void displayHijriCalender() {
        arabicEnglishMonths = myDatabase.myDao().getHijriCalender();
        if (arabicEnglishMonths.size() > 2) {
            tvArDate.setText(arabicEnglishMonths.get(0).getArDate());
            tvArDate2.setText(arabicEnglishMonths.get(1).getArDate());
            tvArDate3.setText(arabicEnglishMonths.get(2).getArDate());
            tvEnDate.setText(arabicEnglishMonths.get(0).getEnDate());
            tvEnDate2.setText(arabicEnglishMonths.get(1).getEnDate());
            tvEnDate3.setText(arabicEnglishMonths.get(2).getEnDate());
            Utils.savePref(Global.SAVE_DAY, arabicEnglishMonths.get(2).getEnDay());
            Utils.savePref(Global.SAVE_MONTH, arabicEnglishMonths.get(2).getEnMonth());
            Utils.log("Hijri month: " + Utils.getPref(Global.SAVE_MONTH, 0));
        }

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

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    private void locationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                getNamzTimes(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                Utils.log("onLocationResult Lat : " + mLastLocation.getLatitude() + " Lng : " + mLastLocation.getLongitude());
            }
        };
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        myProgressBar.setMessage("Detect your location....");
        myProgressBar.setCanceledOnTouchOutside(false);
        myProgressBar.show();
        if (isLocationEnabled()) {
            Utils.log("isLocationEnabled");
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        Utils.log("getLocation 3");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            getNamzTimes(location.getLatitude(), location.getLongitude());
                            Log.e("Location", "mFusedLocationClient Lat : " + location.getLatitude() + " Lng : " + location.getLongitude());
                        } else {
                            Utils.log("getLocation null");
//                                requestNewLocationData();
                            getLocation();
                        }
                    });
        } else {
            Utils.log("isLocationEnabled else");
            myProgressBar.dismiss();
            PermissionUtils.displayLocationSettingsRequest(MainActivity.this, this, LOCATION_SETTINGS);
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
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
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
                    Utils.showToast("Permission granted activity");
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
        if (isSettings)
            getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.actionCalender:
                MainActivity.isSettings=false;
                showCalender();
                return true;
            case R.id.actionReminderPhone:
                //                Utils.log("Minute: " + Utils.getMinute());
//                myAlarmManager.setTestAlarm(0, 0, Utils.getMinute() + 1, 0, 1, 123, "Call", false);
//                //Open call function
//                Utils.call("0191355565");
                startActivity(new Intent(MainActivity.this, SetPhoneCallReminderActivity.class)
                        .putExtra(Global.PRAYER_START_TIME, mPrayerTime.getStartFajr()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCalender() {
        MyCalendarView
                myCalendarView = MyCalendarView.getInstance(MainActivity.this, true);

        myCalendarView.setOnDateSetListener(MainActivity.this);
        myCalendarView.setMinMaxHijriYear(1430, 1450);
        myCalendarView.setMinMaxGregorianYear(2013, 2020);
        myCalendarView.setMode(MyCalendarView.Mode.Hijri);
        myCalendarView.setAdjustment(2);
        myCalendarView.setUILanguage(MyCalendarView.Language.Arabic);
//                        .setDefaultHijriDate(8, 0, 1437)//months start from 0
        myCalendarView.setEnableScrolling(false);

        myCalendarView.showDialog();
    }

    private void goToReminderActivity(int prayerWakto, String prayerStartTime, String prayerEndTime, String prayerWaktoName) {
        startActivity(new Intent(this, SetPrayerReminderActivity.class)
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
            case R.id.tvSet:
                ReminderHijriActivity.arabicEnglishMonth = arabicEnglishMonths.get(0);
                startActivity(new Intent(MainActivity.this, ReminderHijriActivity.class));
                break;
            case R.id.tvSet2:
                ReminderHijriActivity.arabicEnglishMonth = arabicEnglishMonths.get(1);
                startActivity(new Intent(MainActivity.this, ReminderHijriActivity.class));
                break;
            case R.id.tvSet3:
                ReminderHijriActivity.arabicEnglishMonth = arabicEnglishMonths.get(2);
                startActivity(new Intent(MainActivity.this, ReminderHijriActivity.class));
                break;
        }
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        Utils.log("Pick Hijri: " + day + "/" + (month) + "/" + year);
    }
}