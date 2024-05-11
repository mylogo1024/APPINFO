package com.getappinfo.app;

import static android.support.v4.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class DevicesUtil {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public static String getPhoneName() {
        return "";
    }

    public static String getResolution(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics == null) {
            return "";
        }
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        return i + "*" + i2;
    }

    public static double calcScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity) context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 29) {
            activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        } else {
            activity.getWindowManager().getDefaultDisplay().getSize(point);
        }
        return Math.sqrt(Math.pow(point.x / displayMetrics.xdpi, 2.0d) + Math.pow(point.y / displayMetrics.ydpi, 2.0d));
    }

    public static double dp2px(Context context, float f) {
        return f * (Density(context) / 160.0d);
    }

    public static double px2dp(Context context, float f) {
        return f / (Density(context) / 160.0d);
    }

    public static double Density(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics == null) {
            return 0.0d;
        }
        double sqrt = Math.sqrt(Math.pow(displayMetrics.heightPixels, 2.0d) + Math.pow(displayMetrics.widthPixels, 2.0d)) / calcScreenSize(context);
        Log.e("Density", "Density:" + calcScreenSize(context));
        return sqrt;
    }

    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getOperators(Context context) {
        TelephonyManager telephonyManager;
        return (ContextCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0 && (telephonyManager = (TelephonyManager) context.getSystemService("phone")) != null) ? telephonyManager.getNetworkOperatorName() : "";
    }

    public static String getBOOTLOADER() {
        return Build.BOOTLOADER;
    }

    public static String getBOARD() {
        return Build.BOARD;
    }

    public static String getCPU_ABI() {
        return Build.CPU_ABI;
    }

    public static String getCPU_ABI2() {
        return Build.CPU_ABI2;
    }

    public static String getDEVICE() {
        return Build.DEVICE;
    }

    public static String getDISPLAY() {
        return Build.DISPLAY;
    }

    public static String getFINGERPRINT() {
        return Build.FINGERPRINT;
    }

    public static String getHARDWARE() {
        return Build.HARDWARE;
    }

    public static String getHOST() {
        return Build.HOST;
    }

    public static String getID() {
        return Build.ID;
    }

    public static String getMODEL() {
        return Build.MODEL;
    }

    public static long getTIME() {
        return Build.TIME;
    }

    public static String getTYPE() {
        return Build.TYPE;
    }

    public static String getUSER() {
        return Build.USER;
    }

    public static String getVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    public static String getVersionCODENAME() {
        return Build.VERSION.CODENAME;
    }

    public static String getVersionINCREMENTAL() {
        return Build.VERSION.INCREMENTAL;
    }

    public static int getVersionSDK_INT() {
        return Build.VERSION.SDK_INT;
    }

    public static String getSerial() {
        return Build.VERSION.SDK_INT >= 26 ? Build.getSerial() : "";
    }

    public static String shellExec(String str) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop").getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            char[] cArr = new char[1024];
            while (true) {
                int read = bufferedReader.read(cArr);
                if (read != -1) {
                    stringBuffer.append(cArr, 0, read);
                } else {
                    bufferedReader.close();
                    return stringBuffer.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIMEI(Context context) {
//        TelephonyManager telephonyManager;
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            return "";
//        } else {
//            TelephonyManager tm = (TelephonyManager) context
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//            String imei = "";
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (!TextUtils.isEmpty(tm.getImei(1))) {
//                    imei = tm.getImei(1);
//                }
//            }

            return "";

    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }
}