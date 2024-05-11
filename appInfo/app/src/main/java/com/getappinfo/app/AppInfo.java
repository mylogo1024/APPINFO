package com.getappinfo.app;


import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public class AppInfo {
    private Drawable appIcon;
    private String appName;
    private long firstInstallTime;
    private long lastUpdateTime;
    private String minSdkVersion;
    private String packageName;
    public String setPackMd5;
    private String targetSdkVersion;
    private int versionCode;
    private String versionName;

    public String getMinSdkVersion() {
        return this.minSdkVersion;
    }

    public void setMinSdkVersion(String str) {
        this.minSdkVersion = str;
    }

    public String getTargetSdkVersion() {
        return this.targetSdkVersion;
    }

    public void setTargetSdkVersion(String str) {
        this.targetSdkVersion = str;
    }

    public long getFirstInstallTime() {
        return this.firstInstallTime;
    }

    public void setFirstInstallTime(long j) {
        this.firstInstallTime = j;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(long j) {
        this.lastUpdateTime = j;
    }

    public String getSetPackMd5() {
        return this.setPackMd5;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public void setSetPackMd5(String str) {
        this.setPackMd5 = str;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setVersionName(String str) {
        this.versionName = str;
    }

    public void setVersionCode(int i) {
        this.versionCode = i;
    }

    public void setAppIcon(Drawable drawable) {
        this.appIcon = drawable;
    }

    public String toString() {
        return "AppInfo{setPackMd5='" + this.setPackMd5 + "', appName='" + this.appName + "', packageName='" + this.packageName + "', versionName='" + this.versionName + "', versionCode=" + this.versionCode + ", appIcon=" + this.appIcon + '}';
    }
}