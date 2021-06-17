package com.shadhinlab.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.shadhinlab.reminder.tools.Utils;

import java.lang.reflect.Method;


public class PhoneStateReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static String outgoingNumber;  //because the passed incoming is only valid in ringing
    private String incomingNumber;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null)
            return;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            if (intent.getExtras() != null) {
                outgoingNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
//                    String displayName = new CommonMethods().getContactName(outgoingNumber, context);
//                outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Utils.showToast("Outgoing number : ");
            }
        } else {
            if (intent.getExtras() == null)
                return;
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            int state = 0;
            if (stateStr == null)
                return;

            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                state = TelephonyManager.CALL_STATE_RINGING;
                Utils.showToast("Incoming number : ");
            }


            onCallStateChanged(context, state, incomingNumber, outgoingNumber);
        }

    }

    public void endCall(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);

            c = Class.forName(telephonyService.getClass().getName());
            m = c.getDeclaredMethod("endCall");
            m.setAccessible(true);
            m.invoke(telephonyService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCallStateChanged(Context context, int state, String number, String outgoingNumber) {
//        Log.e("State", "onCallStateChanged : " + state);
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
//                    Utils.savePref(Global.CALLING_NUMBER, outgoingNumber, context);
                    Utils.showToast("onOutgoingCallStarted : ");


                } else {
                    isIncoming = true;

//                    Toast.makeText(context, "onIncomingCallAnswered : " + number, Toast.LENGTH_LONG).show();
                    Utils.showToast("onIncomingCallAnswered : ");
                    endCall(context);
//                            Intent reivToServ = new Intent(context, RecorderService.class);
//                            reivToServ.putExtra("number", number);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                Utils.showToast( "Recording has started");
//                                context.startForegroundService(reivToServ);
//                            } else {
//                                Utils.showToast( "Recording has started");
//                                context.startService(reivToServ);
//                            }


                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
//                    onMissedCall(context, outgoingNumber, callStartTime);
                } else if (isIncoming) {
                    Utils.showToast("onIncomingCallEnded : " );

                } else {
                    Utils.showToast("onOutgoingCallEnded : " );
                }
                break;
        }
        lastState = state;
    }
}
