package com.shadhinlab.reminder.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shadhinlab.reminder.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {


    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static int getMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }

    public static String getYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static String getTimeConverter(Date time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String date_time = sdf.format(time);
        return date_time;
    }

    public static String getAMPM(Date time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("a", Locale.getDefault());
        String date_time = sdf.format(time);
        return date_time;
    }

    public static String getTimeConverterWithAmPm(Date time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        String date_time = sdf.format(time);
        return date_time;
    }

    public static String getDateTimeConverter(Date time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date_time = sdf.format(time);
        return date_time;
    }

    public static Date getDateTime(String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date_time = null;
        try {
            date_time = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_time;
    }

    public static Date converterDate(String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date date_time = null;
        try {
            date_time = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_time;
    }

    public static String getTimeAfterSixHours(Date date) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 6);
        return dateFormat.format(cal.getTime());
    }

    public static Date calculateDate(int day, int hour, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(new GregorianCalendar())) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH, 7);
        }

        return calendar.getTime();
    }

    public static Date getCurrentTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        Date date_time = null;
        try {
            date_time = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_time;
    }

    public static Date getCurrentTimeSec(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        Date date_time = null;
        try {
            date_time = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_time;
    }

    public static String getTodaysDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static String getTodaysDateTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static String getTodaysDateTimeWithAmPm() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static String getTodaysTimeWithAmPm() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static String getTodaysTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static String getTodaysOnlyAmPm() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("a");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static String getTodaysDateTimeNew() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static Date getDate(String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date_time = null;
        try {
            date_time = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_time;
    }

    public static String getFutureDate(int day) {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, day);
        return dateFormat.format(cal.getTime());
    }

    public static String getTomorrowDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return dateFormat.format(cal.getTime());
    }

    public static String getDateConvert(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String date_time = sdf.format(date);
        return date_time;
    }

    public static String getDateConvertNew(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_time = sdf.format(date);
        return date_time;
    }

    public static String getDayName(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String goal = outFormat.format(date);
        return goal;
    }

    public static String getCurrentTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String date_time = sdf.format(new Date());
        return date_time;
    }

    public static boolean isBeforeSunriseTime(String currentTime, String sunRiseTime) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat st = new SimpleDateFormat("HH:mm");
        try {

            Date time = st.parse(currentTime);
            Date time2 = st.parse(sunRiseTime);
            Log.e("Alarm", "currentDate : " + time + " pickTime : " + time2);
            if (time.after(time2)) {
                return false;
            }
            if (time.before(time2)) {
                return true;

            }
            if (time.equals(time2)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isBeforeCurrentDateTime(String currentDateTime, String storeDateTime) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat st = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {

            Date time = st.parse(currentDateTime);
            Date time2 = st.parse(storeDateTime);
            if (time.after(time2)) {
                return false;
            }
            if (time.before(time2)) {
                return true;

            }
            if (time.equals(time2)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Convert Calendar to Date
    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    // save data to sharedPreference
    public static void savePref(String name, String value) {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, value);
        editor.apply();
    } // save data to sharedPreference

    public static void savePref(String name, int value) {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public static void savePrefBoolean(String name, boolean value) {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public static void clearPref() {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }

    // get data EventFrom shared preference
    public static String getPref(String name, String defaultValue) {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        return pref.getString(name, defaultValue);
    }  // get data EventFrom shared preference

    public static int getPref(String name, int defaultValue) {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        return pref.getInt(name, defaultValue);
    }

    public static Boolean getPrefBoolean(String name, boolean defaultValue) {
        SharedPreferences pref = MyApp.getInstance().getContext().getSharedPreferences(MyApp.getInstance().getPackageName(), Context.MODE_PRIVATE);
        return pref.getBoolean(name, defaultValue);
    }

    public static boolean isInternetOn() {

        try {
            ConnectivityManager con = (ConnectivityManager) MyApp.getInstance().getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi, mobile;
            assert con != null;
            wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mobile = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting()) {
                return true;
            }


        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    public static String getMinutesLeft(long toMillis) {
        long millisLeft = getMillisLeft(toMillis);
        return String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisLeft) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisLeft)));
    }

    public static String getHoursLeft(long toMillis) {
        return String.format("%d", TimeUnit.MILLISECONDS.toHours(getMillisLeft(toMillis)));
    }

    public static long getMillisLeft(long toMillis) {
        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
        return toMillis - currentTimeMillis;
    }

    public static void showToast(String msg) {
        Toast.makeText(MyApp.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg) {
        Log.e("Alarm", msg);
    }

    public static void shareMyApp(Context context) {
        if (context == null)
            return;
//        String link = "https://drive.google.com/file/d/17KNEF_z-rHhdZxcZ_A67WM4x5C64uRl8/view?usp=sharing";
        String link = "https://play.google.com/store/apps/details?id=" + context.getPackageName();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Via"));
    }

    public static void rateMyApp(final Context context) {

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null)
            return;
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        final TextView dialog_message = dialogView.findViewById(R.id.dialog_message);
        final Button dialog_button_positive = dialogView.findViewById(R.id.dialog_button_positive);
        final Button dialog_button_negative = dialogView.findViewById(R.id.dialog_button_negative);

        dialog_title.setText(context.getString(R.string.rate_app));
        dialog_message.setText(context.getString(R.string.description));
        dialog_button_positive.setText(context.getString(R.string.rate_now));
        dialog_button_negative.setText(context.getString(R.string.not_now));


        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button_negative.setOnClickListener(v -> alertDialog.dismiss());

        dialog_button_positive.setOnClickListener(v -> {
            alertDialog.dismiss();

            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

            try {
                context.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                showToast("Couldn't launch the market");
            }
        });
    }

    public static Date timeCalculate(int hour, int min, int pickTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min - pickTime);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {

            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            log("Utils : " + calendar.getTime() + " " + hour + " " + min + " " + pickTime);
        }

        return calendar.getTime();
    }

    public static void setTextColor(TextView tv, String colorStr) {
        String mainString = tv.getText().toString();
        if (!TextUtils.isEmpty(colorStr) && mainString.contains(colorStr)) {
            int startIndex = mainString.indexOf(colorStr);
            int lastIndex = startIndex + colorStr.length();
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString redSpannable = new SpannableString(tv.getText());
            redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#1674B1")), startIndex, lastIndex, 0);
            builder.append(redSpannable);

            tv.setText(builder, TextView.BufferType.SPANNABLE);
        }
    }

    public static void setTextColorInActive(TextView tv, String colorStr) {
        String mainString = tv.getText().toString();
        if (!TextUtils.isEmpty(colorStr) && mainString.contains(colorStr)) {
            int startIndex = mainString.indexOf(colorStr);
            int lastIndex = startIndex + colorStr.length();
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString redSpannable = new SpannableString(tv.getText());
            redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#661674B1")), startIndex, lastIndex, 0);
            builder.append(redSpannable);

            tv.setText(builder, TextView.BufferType.SPANNABLE);
        }


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View editText, Activity activity) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyboardForce(EditText editText, Activity activity) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public static void changeStatusBar(Activity activity, int color) {
        Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(color);
    }

    public static void changeStatusBarOthers(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            activity.getWindow().setStatusBarColor(color);
        } else changeStatusBar(activity, color);
    }

    public static void setLightStatusBar(View view, Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void changeStatusBarItemColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    public static void statusBarTransparent(Activity activity) {
        Window w = activity.getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}
