package com.getappinfo.app;

import android.Manifest;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bun.miitmdid.core.MdidSdkHelper;

import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.getappinfo.app.Adapter.RecycleViewAdapterDemo;
import com.getappinfo.app.AppInfo;
import com.getappinfo.app.DevicesUtil;
import com.getappinfo.app.DriverTypeUtils;
import com.jz.sdkdemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int QUERY_ALL_PACKAGES_CODE = 101010;
    private static final int STORAGE_PERMISSION_PHONE_REQUEST_CODE = 101011;
    private static final int WRITE_REQUEST_CODE = 101012;
    private static final int READ_PHONE_STATE_CODE = 101013;
    public ArrayList<AppInfo> appList;
    private String content;
    private List<PackageInfo> installedPackages;
    public Handler mHandler;
    private TextView phone_info;
    public RecyclerView recyclerView;
    private ScrollView scrollView;
    private ArrayList<PackageInfo> systemApps;
    private ArrayList<PackageInfo> userAppLists;
    private ArrayList<PackageInfo> searchAppLists = new ArrayList<>();
    private EditText searchView;
    private Button btn_search, btn_save_data;

    @Override // android.app.Activity
    @RequiresApi(api = 23)
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        this.scrollView = (ScrollView) findViewById(R.id.sc1);
        this.phone_info = (TextView) findViewById(R.id.phone_info);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(1);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        searchView = findViewById(R.id.searchView);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);

        btn_search = findViewById(R.id.btn_search);
//        btn_save_data = findViewById(R.id.btn_save_data);


        checkPermission();

//        File filesDir = getFilesDir();
//        File cacheDir = getCacheDir();
//        String absolutePath = filesDir.getAbsolutePath();
//        String absolutePath2 = cacheDir.getAbsolutePath();
//        File file = new File(getFilesDir().getAbsolutePath() + "/test123");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        Log.e("MainActivity", "absolutePath:" + absolutePath + " cacheAbsolutePath:" + absolutePath2);
//        File file2 = new File(getExternalFilesDir(null), "test123/123/");
//        if (!file2.exists()) {
//            file2.mkdir();
//        }
//        Log.e("MainActivity", "tets:" + file2.getAbsolutePath());
    }

    /**
     * 搜索結果
     */
    private void queryTextSubmit(String query) {
        this.installedPackages = getPackageManager().getInstalledPackages(0);
        if (this.installedPackages != null && this.installedPackages.size() > 0) {
            for (int i = 0; i < this.installedPackages.size(); i++) {
                PackageInfo packageInfo = installedPackages.get(i);
                Log.e(TAG, "queryTextSubmit packageName:" + packageInfo.packageName.toLowerCase() + ",appname:" + packageInfo.applicationInfo.loadLabel(this.getPackageManager()).toString() + " ,query:" + query);
                if (packageInfo.packageName.toLowerCase().contains(query)) {
                    searchAppLists.add(packageInfo);
                } else if (packageInfo.applicationInfo.loadLabel(this.getPackageManager()).toString().contains(query)) {
                    searchAppLists.add(packageInfo);
                }
            }
        }

        if (this.searchAppLists != null) {
//            if (this.searchAppLists.size() > 0) {
            this.recyclerView.setAdapter(new RecycleViewAdapterDemo(this, this.searchAppLists));
//            recyclerView.notifyAll();
//            }
            if (this.searchAppLists.size() == 0) {
                Toast.makeText(MainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void writerAndAndroidID(Activity context, String fileName, String content) {
        Log.e("MainActivity", "writerAndAndroidID------");
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Download" + "/" + "01.txt";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
        File file1 = new File(path);

        try {
            if (file1.exists() == false) {
                file1.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file1.getAbsoluteFile());
            byte[] bytes = content.getBytes();
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writerAndAndroidID1(Activity context, String content) {
        Log.e("MainActivity", "writerAndAndroidID------");

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "01.txt");

        context.startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    private void initData() {

        PackageManager packageManager = getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;
            Log.d("APP_INFO", "App name: " + appName + ", package name: " + packageName);
        }

        this.installedPackages = getPackageManager().getInstalledPackages(0);
        this.recyclerView.setAdapter(new RecycleViewAdapterDemo(this, this.installedPackages));
        this.userAppLists = new ArrayList<>();
        this.systemApps = new ArrayList<>();
        for (int i = 0; i < this.installedPackages.size(); i++) {
            PackageInfo packageInfo = this.installedPackages.get(i);
            int i2 = packageInfo.applicationInfo.flags;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            PrintStream printStream = System.out;
            printStream.println("i2:" + i2 + " " + packageInfo.packageName + "<------>" + packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            if ((1 & i2) <= 0) {
                this.userAppLists.add(packageInfo);
            } else {
                this.systemApps.add(packageInfo);
            }
        }

//
    }


    private void checkPermission() {

        //本APP所需权限：1.读取应用列表权限 、2.手机设备码权限 、3.读写权限
        // android 13 读取应用列表权限(xuya)
        boolean hasAllPackageP = ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED;
        boolean hasPhoneStateP = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean hasReadWriteP = hasStorageAndPhoenPermissions(this);

        Log.e(TAG, "hasAllPackageP:" + hasAllPackageP + " , hasPhoneStateP:" + hasPhoneStateP + " , hasReadWriteP:" + hasReadWriteP);
        if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && hasAllPackageP == false) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                            QUERY_ALL_PACKAGES_CODE);
                } else {
                    // 已经授予了该权限，执行应用程序列表获取操作

                }
            }
         else if (hasPhoneStateP == false) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_CODE);
        } else if (hasReadWriteP == false) {
            requestStoragePermissions(this);
        } else {
            initData();
        }
        //app 读取使用详情 APP的大小
//        ContextCompat.checkSelfPermission(this, "android.permission.PACKAGE_USAGE_STATS");
//        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
//        Calendar instance = Calendar.getInstance();
//        long timeInMillis = instance.getTimeInMillis();
//        boolean z = true;
//        instance.add(1, -1);
//        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(3, instance.getTimeInMillis(), timeInMillis);
//        if (queryUsageStats == null || queryUsageStats == Collections.EMPTY_LIST) {
//            z = false;
//        }
//        Log.e("MainActivity", "granted:" + z);
//        if (!z) {
//            startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
//        }


        //检测是否有权限


    }

    /**
     * 读写权限检查
     *
     * @param context
     * @return
     */
    private boolean hasStorageAndPhoenPermissions(Context context) {
        //版本判断，如果比android 13 就走正常的权限获取
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            int phonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
//            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED && phonePermission == PackageManager.PERMISSION_GRANTED;
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        } else {
            int audioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO);
            int imagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES);
            int videoPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO);
            boolean externalStorageManager = Environment.isExternalStorageManager();
//            int phonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            return audioPermission == PackageManager.PERMISSION_GRANTED && imagePermission == PackageManager.PERMISSION_GRANTED
                    && videoPermission == PackageManager.PERMISSION_GRANTED && externalStorageManager == true;
//            return phonePermission == PackageManager.PERMISSION_GRANTED && audioPermission == PackageManager.PERMISSION_GRANTED && imagePermission == PackageManager.PERMISSION_GRANTED && videoPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermissions(Context context) {
//        String [] permissions;
//        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
//            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
//        }
//        else{
//            permissions = new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_PHONE_STATE};
//        }
        //android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            boolean externalStorageManager = Environment.isExternalStorageManager();
            Log.e(TAG, "应用程序是否具有 MANAGE_EXTERNAL_STORAGE 权限，该权限可以管理外部存储空间:" + externalStorageManager);

            // 是否以获取权限
            if (externalStorageManager == false) {
                // 应用程序没有 MANAGE_EXTERNAL_STORAGE 权限，需要请求该权限
                //申请所有文件访问权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, STORAGE_PERMISSION_PHONE_REQUEST_CODE);
            } else {
                initData();
            }
        }
        //android 6~android 10 （API 23~ 29）动态申请权限
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int r_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int w_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            Log.e(TAG, "r_permission:" + r_permission + " ,w_permission:" + w_permission);
            // 是否以获取权限
            if (r_permission != PackageManager.PERMISSION_GRANTED || w_permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_PHONE_REQUEST_CODE);

            } else {
                Log.e(TAG, "权限已授予，执行相关操作");
                initData();
            }
        } else {
            Log.e(TAG, "Android版本较低，无需动态请求权限");
            initData();
        }

    }


    void oadiTest() {

        MdidSdkHelper.InitSdk(this, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean b, IdSupplier idSupplier) {
                Log.e("tag oaid:" + b, "获取oaid:\n" + idSupplier.getOAID() + " " + Thread.currentThread());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.content = "获取oaid:" + idSupplier.getOAID() + "\n\n";
                        MainActivity.this.showConten();
                    }
                });


            }
        });

    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConten() {
        String cpuabi = DriverTypeUtils.getCPUABI();
        Log.e("tagDriverType", "cpuabi==：" + cpuabi);
        this.content += "手机品牌: " + DevicesUtil.getBrand() + "\n";
        this.content += "手机型号: " + DevicesUtil.getPhoneModel() + "\n";
        this.content += "手机名称: " + DevicesUtil.getPhoneName() + "\n";
        this.content += "设备AndroidId: " + DevicesUtil.getAndroidId(this) + "\n";
        this.content += "cpu型号: " + cpuabi + "\n";
        this.content += "手机运营商: " + DevicesUtil.getOperators(this) + "\n";
        this.content += "安卓版本: " + DevicesUtil.getSysVersion() + "\n";
        this.content += "设备密度: " + DevicesUtil.Density(this) + "\n";
        this.content += "设备尺寸: " + DevicesUtil.calcScreenSize(this) + "英寸\n";
        this.content += "分辨率  : " + DevicesUtil.getResolution(this) + "px\n";
        this.content += "10dp2px  : " + DevicesUtil.dp2px(this, 10.0f) + "\n";
        this.content += "10px2dp  : " + DevicesUtil.px2dp(this, 10.0f) + "\n";

        this.content += "设备基板名称: " + DevicesUtil.getBOARD() + "\n";
        this.content += "设备引导程序版本号: " + DevicesUtil.getBOOTLOADER() + "\n";
        this.content += "设备的唯一标识: " + DevicesUtil.getFINGERPRINT() + "\n";
        this.content += "设备指令集名称（CPU的类型）: " + DevicesUtil.getCPU_ABI() + "\n";
        this.content += "获取第二个指令集名称: " + DevicesUtil.getCPU_ABI2() + "\n";
        this.content += "设备驱动名称: " + DevicesUtil.getDEVICE() + "\n";
        this.content += "获取设备显示的版本包: " + DevicesUtil.getDISPLAY() + "\n";
        this.content += "设备硬件名称,一般和基板名称一样: " + DevicesUtil.getHARDWARE() + "\n";
        this.content += "设备主机地址: " + DevicesUtil.getHOST() + "\n";
        this.content += "设备版本号: " + DevicesUtil.getID() + "\n";
        this.content += "手机的型号 设备名称: " + DevicesUtil.getMODEL() + "\n";
        this.content += "整个产品的名称: " + DevicesUtil.getTIME() + "\n";
        this.content += "设备版本类型: " + DevicesUtil.getTYPE() + "\n";
        this.content += "设备用户名: " + DevicesUtil.getUSER() + "\n";
        this.content += "系统版本字符串: " + DevicesUtil.getVersionRelease() + "\n";
        this.content += "设备当前的系统开发代号: " + DevicesUtil.getVersionCODENAME() + "\n";
        this.content += "系统源代码控制值: " + DevicesUtil.getVersionINCREMENTAL() + "\n";
        this.content += "系统的API级别: " + DevicesUtil.getVersionSDK_INT() + "\n";
        this.content += "Resolution: " + DevicesUtil.getResolution(this) + "\n\n\n";
        this.content += "手机安装列表信息: \n";
        PackageInfo packageInfo = null;
        if (installedPackages != null && installedPackages.size() > 0) {
            for (int i = 0; i < this.installedPackages.size(); i++) {
                packageInfo = installedPackages.get(i);
                if ((packageInfo.applicationInfo.flags & 1) <= 0) {
                    this.content += "" + packageInfo.applicationInfo.loadLabel(getPackageManager()).toString() + ":\n" + packageInfo.packageName + "\n";
                } else {
                    this.content += "" + packageInfo.applicationInfo.loadLabel(getPackageManager()).toString() + ":\n" + packageInfo.packageName + "\n";
                }
            }
        }
        this.phone_info.setText("\n" + this.content.toString());
        if (hasStorageAndPhoenPermissions(this) == true) {
            writerAndAndroidID(this, "01.txt", content.toString());
        }
    }

    int clickCountPhoneText = 0;

    public void Onclick(View view) {
        if (view.getId() == R.id.get_allAPPinfo) {
            this.recyclerView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            btn_search.setVisibility(View.VISIBLE);
            searchView.setText("");
            this.phone_info.setVisibility(View.GONE);
            this.scrollView.setVisibility(View.GONE);
            System.out.println("获取系统+用户软件：");
            if (this.systemApps != null && this.systemApps.size() > 0) {
                this.recyclerView.setAdapter(new RecycleViewAdapterDemo(this, this.systemApps));
            }
        }
        if (view.getId() == R.id.get_userAPPinfo) {
            this.recyclerView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            btn_search.setVisibility(View.VISIBLE);
            searchView.setText("");
            this.phone_info.setVisibility(View.GONE);
            this.scrollView.setVisibility(View.GONE);
            System.out.println("获取所有非系统软件：");
            if (this.userAppLists != null && this.userAppLists.size() > 0) {
                this.recyclerView.setAdapter(new RecycleViewAdapterDemo(this, this.userAppLists));
            }
        }
        if (view.getId() == R.id.get_phoneinfo) {
            System.out.println("获取设备信息：");
            oadiTest();
            searchView.setText("");
            this.phone_info.setVisibility(View.VISIBLE);
            this.scrollView.setVisibility(View.VISIBLE);
            this.recyclerView.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            btn_search.setVisibility(View.GONE);
        }
        if (view.getId() == R.id.btn_search) {

            queryTextSubmit(searchView.getText().toString());
            //隐藏收起键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        }

        if (view.getId() == R.id.phone_info) {
            clickCountPhoneText++;
            if (clickCountPhoneText > 4) {
                writerAndAndroidID(this, "app列表信息.txt", content.toString());
                clickCountPhoneText = 0;
                Toast.makeText(MainActivity.this,"写入成功",Toast.LENGTH_SHORT).show();
            }
        }
//        if (view.getId() == R.id.btn_save_data) {
//            writerAndAndroidID(this, "app列表信息.txt", content.toString());
//        }
    }

    private static String getApplicationPackageMD5(Context context, String str) {
        try {
            Signature signature = context.getPackageManager().getPackageInfo(str, PackageManager.GET_SIGNATURES).signatures[0];
            MessageDigest instance = MessageDigest.getInstance("MD5");
            MessageDigest instance2 = MessageDigest.getInstance("SHA1");
            instance.update(signature.toByteArray());
            instance2.update(signature.toByteArray());
            String hextring = toHextring(instance.digest());
            String hextring2 = toHextring(instance2.digest());
            return "md5 = " + hextring + "\n sha1 = " + hextring2;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == QUERY_ALL_PACKAGES_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                            QUERY_ALL_PACKAGES_CODE);
                } else {
                    // 已经授予了该权限，执行应用程序列表获取操作
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                READ_PHONE_STATE_CODE);
                    }
                }
            }
        } else if (requestCode == READ_PHONE_STATE_CODE) {

            // 已经授予了该权限，执行应用程序列表获取操作
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        READ_PHONE_STATE_CODE);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                        QUERY_ALL_PACKAGES_CODE);
            } else if (hasStorageAndPhoenPermissions(this) == false) {
                requestStoragePermissions(this);
            } else {
                initData();
            }
        }

        if (requestCode == STORAGE_PERMISSION_PHONE_REQUEST_CODE) {
            if (hasStorageAndPhoenPermissions(this) == false) {
                requestStoragePermissions(this);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                        QUERY_ALL_PACKAGES_CODE);
            } else {
                initData();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
                    if (pfd != null) {
                        FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                        fileOutputStream.write(content.getBytes());
                        fileOutputStream.close();
                        pfd.close();
                        Log.e("MainActivity", "File write successful.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}