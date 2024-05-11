package com.getappinfo.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.huawei.hms.ads.identifier.AdvertisingIdClient;
import com.jz.sdkdemo.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PermissionTestActivity extends Activity {

    private static final String TAG = PermissionTestActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE = 101101;
    private int ANDROID11_REQUEST_CODE = 1011;
    private TextView textView,tv_phone;
    private EditText et_text;
    private ImageView iv;
    private Button btn_read_iv,btn_write_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_test_layout);
        textView = (TextView) findViewById(R.id.text);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        et_text = (EditText) findViewById(R.id.et_text);
        iv = (ImageView) findViewById(R.id.iv);
        btn_write_iv = (Button) findViewById(R.id.btn_write_iv);
        btn_read_iv = (Button) findViewById(R.id.btn_read_iv);

        // 读取权限和获取手机状态权限写成两个方法一起申请只会申请前面的，后面的不会有效果 即使第一个权限已经获取权限，
//        解决方法：1.封装成一个 2.接受到对用的权限回调之后在申请另一个权限
        checkRWPermissions();
//        checkPhonePermissions();


    }

    private static final int REQUEST_RW_PERMISSION = 10111;
    private static final int REQUEST_PHONE_PERMISSION = 1;

    private void checkPhonePermissions(){
//        permission：要检查的权限字符串。
//        pid：进程ID（Process ID）。
//        uid：用户ID（User ID）。
        int result = checkPermission(Manifest.permission.READ_PHONE_STATE, android.os.Process.myPid(), android.os.Process.myUid());
        Log.e(TAG,"checkPhonePermissions result:"+result);
        if (result == PackageManager.PERMISSION_GRANTED) {
            // 应用程序具有指定权限
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_PHONE_PERMISSION);
        }

    }
    private void checkRWPermissions() {
//        Android 11 的版本号是 30  <uses-permission android:name="android.permission.MANAGE_DOCUMENTS" />
//        Android 6.0 的版本号是 23  Manifest.permission.READ_EXTERNAL_STORAGE、Manifest.permission.WRITE_EXTERNAL_STORAGE

        textView.setText("Build.VERSION:" + Build.VERSION.SDK_INT);

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
                startActivityForResult(intent, ANDROID11_REQUEST_CODE);
            }
            else{
                checkPhonePermissions();
                performAction();
            }
        }
        //android 6~android 10 （API 23~ 29）动态申请权限
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int r_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int w_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            Log.e(TAG, "r_permission:"+r_permission+" ,w_permission:"+w_permission);
            // 是否以获取权限
            if (r_permission != PackageManager.PERMISSION_GRANTED || w_permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PermissionTestActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_RW_PERMISSION);

            }
            else {
                Log.e(TAG, "权限已授予，执行相关操作");
                checkPhonePermissions();
                performAction();
            }
        }
        else {
            Log.e(TAG, "Android版本较低，无需动态请求权限");
            checkPhonePermissions();
            performAction();
        }
    }

    private void performAction() {
        // 执行需要权限的操作
        performReadOperation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult requestCode:"+requestCode + " ,");
        if (requestCode == ANDROID11_REQUEST_CODE) {
            performAction();
        } else if (requestCode == REQUEST_RW_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "权限已授予，执行相关操作");
                performAction();
            } else {
                Log.e(TAG, "权限被拒绝，可以根据需要进行处理");
            }
        }

        checkPhonePermissions();
    }


    /**
     * 读取文件
     */
    private String performReadOperation() {
        Log.e(TAG, "---------------performReadOperation------------------------");
        File rootDirectory = Environment.getExternalStorageDirectory();
        File file = new File(rootDirectory, "example.txt");
        String contents = "";
        try {
            if (file.exists() == false){
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            StringBuilder content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            br.close();
            isr.close();
            fis.close();
            contents = content.toString();
            // 在content中获取到了文件内容，可以进行处理
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return contents;
    }

    /**
     * 写入文件
     */
    private void performWriteOperation(String text) {
        Log.e(TAG, "---------------performReadOperation------------------------");
        File rootDirectory = Environment.getExternalStorageDirectory();
        File file = new File(rootDirectory, "example.txt");
        FileWriter writer = null;
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            writer = new FileWriter(file);
            writer.append(text);
            writer.flush();
            System.out.println("文本已成功写入文件: " + file.getAbsolutePath());

            // 在content中获取到了文件内容，可以进行处理
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    // 关闭 FileWriter
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Onclick(View view) {
        if (view.getId() == R.id.btn_write){
            performWriteOperation(et_text.getText().toString());
        }
        else if (view.getId() == R.id.btn_read){
            textView.setText(performReadOperation());
        }
        else if (view.getId() == R.id.btn_read_Phone){
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String imei = "";
//            tv_phone.setText("androidId:"+androidId );
//            IMEI 权限 在android 8.0（API 26）以下 和 android 10（API 29）以上不可读取
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            Log.e(TAG, "androidId:"+androidId );
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = telephonyManager.getImei();
                }
            }

            tv_phone.setText("androidId:"+androidId + "\nimei:"+imei);
        }
        else if (view.getId() == R.id.btn_write_iv){
            
        }else if (view.getId() == R.id.btn_read_iv){
            loadImage();
        }

    }

    // 在某个事件触发的方法中读取图片
    public void loadImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        // 设置文件类型
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    // 在Activity中重写onActivityResult()方法获取图片路径或字节流，并显示在ImageView中
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" onActivityResult requestCode:" + requestCode + " ,resultCode:"+resultCode);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                iv.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    

}