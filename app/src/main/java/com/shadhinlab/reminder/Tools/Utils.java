package com.shadhinlab.reminder.Tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Utils {

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

    public static void showToast(String msg) {
        Toast.makeText(MyApp.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg) {
        Log.e("Reminder", msg);
    }
}
