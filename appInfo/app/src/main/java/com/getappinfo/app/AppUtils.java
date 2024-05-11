package com.getappinfo.app;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.RequiresApi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AppUtils {
    @RequiresApi(api = 24)
    public static List<AppInfo> getAllPackages(Context context) {
        ArrayList arrayList = new ArrayList();
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
            appInfo.setPackageName(packageInfo.packageName);
            appInfo.setPackMd5 = getApplicationPackageMD5(context, packageInfo.packageName);
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setVersionCode(packageInfo.versionCode);
            appInfo.setMinSdkVersion(packageInfo.applicationInfo.minSdkVersion + "");
            appInfo.setTargetSdkVersion(packageInfo.applicationInfo.targetSdkVersion + "");
            appInfo.setFirstInstallTime(packageInfo.firstInstallTime);
            appInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
            int i2 = packageInfo.applicationInfo.flags;
        }
        return arrayList;
    }

    public static String getApplicationPackageMD5(Context context, String str) {
        try {
            Signature signature = context.getPackageManager().getPackageInfo(str, 64).signatures[0];
            MessageDigest instance = MessageDigest.getInstance("MD5");
            MessageDigest instance2 = MessageDigest.getInstance("SHA1");
            instance.update(signature.toByteArray());
            instance2.update(signature.toByteArray());
            String hextring = toHextring(instance.digest());
            toHextring(instance2.digest());
            return hextring;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "未找到这个包名";
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            return "未找到对应的算法";
        }
    }

    public static String getApplicationPackageSHA1(Context context, String str) {
        try {
            Signature signature = context.getPackageManager().getPackageInfo(str, 64).signatures[0];
            MessageDigest instance = MessageDigest.getInstance("MD5");
            MessageDigest instance2 = MessageDigest.getInstance("SHA1");
            instance.update(signature.toByteArray());
            instance2.update(signature.toByteArray());
            toHextring(instance.digest());
            return toHextring(instance2.digest());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "未找到这个包名";
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            return "未找到对应的算法";
        }
    }

    private static String toHextring(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            byte2Hex(b, stringBuffer);
        }
        return stringBuffer.toString();
    }

    private static void byte2Hex(byte b, StringBuffer stringBuffer) {
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        stringBuffer.append(cArr[(b & 240) >> 4]);
        stringBuffer.append(cArr[b & 15]);
    }

    public static String getPackageName(Context context) {
        return context.getApplicationInfo().packageName;
    }

    public static String timeOf(long j) {
        return timeOf(j, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timeOf(long j, String str) {
        return new SimpleDateFormat(str).format(Long.valueOf(j));
    }
}