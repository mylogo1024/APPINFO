package com.getappinfo.app.Adapter;

import android.app.ActivityManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getappinfo.app.AppUtils;
import com.jz.sdkdemo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RecycleViewAdapterDemo extends RecyclerView.Adapter<RecycleViewAdapterDemo.MyViewHolder> {
    private Context context;
    private View inflater;
    private List<PackageInfo> list;

    public RecycleViewAdapterDemo(Context context, List<PackageInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.context).inflate(R.layout.recycleview_item, viewGroup, false);
        this.inflater = inflate;
        return new MyViewHolder(inflate);
    }

    @RequiresApi(api = 24)
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        if (list.size() == 0) {

        } else {
            try {
                final PackageInfo packageInfo = this.list.get(i);
                myViewHolder.appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(this.context.getPackageManager()));
                TextView textView = myViewHolder.appName;
                textView.setText("第" + (i + 1) + "款应用：");
                TextView textView2 = myViewHolder.app_allinfo;
                RelativeLayout relativeLayout = myViewHolder.rl_bg;
                int i2 = Build.VERSION.SDK_INT;
                String appNames = packageInfo.applicationInfo.loadLabel(this.context.getPackageManager()).toString();
                int appVersionCode = packageInfo.versionCode;
                String appVersionName = packageInfo.versionName;
                String appPackageName = packageInfo.packageName;

                textView2.setText(appNames + "\nversionCode：" + appVersionCode + "\nversionName: " + appVersionName + "\n包名: " + appPackageName + "\n应用签名MD5: \n" + AppUtils.getApplicationPackageMD5(this.context, appPackageName) + "\nSHA1: " + AppUtils.getApplicationPackageSHA1(this.context, appPackageName) + "\ntargetSdkVersion: " + packageInfo.applicationInfo.targetSdkVersion + "\nminSdkVersion: " + packageInfo.applicationInfo.minSdkVersion + "\n首次安装时间: " + AppUtils.timeOf(packageInfo.firstInstallTime) + "\n最后更新时间: " + AppUtils.timeOf(packageInfo.lastUpdateTime) + "\napk路径: " + packageInfo.applicationInfo.sourceDir+ "\napk缓存路径: " + packageInfo.applicationInfo.dataDir + "\nSO路径: " + packageInfo.applicationInfo.nativeLibraryDir+"\nSO路径: " + packageInfo.applicationInfo.deviceProtectedDataDir+ "\n");
                myViewHolder.export_app.setText(appNames+"\n导出应用");
                myViewHolder.export_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String appDir = packageInfo.applicationInfo.sourceDir;
                        String rootPath = Environment.getExternalStorageDirectory().getPath();
                        String targetPath = rootPath + "/" + appNames + "_" + appPackageName + "_" + appVersionCode + "_" + appVersionName +".apk" ;
                        FileInputStream fis = null;
                        FileOutputStream fos = null;
                        try {
                            fis = new FileInputStream(new File(appDir));
                            fos = new FileOutputStream(new File(targetPath));

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                                if (fis != null) {
                                    fis.close();
                                }

                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
//                        new Handler()
                        Toast.makeText(context,"已导出到 ："+targetPath,Toast.LENGTH_SHORT).show();
//                        更新媒体库
                        MediaScannerConnection.scanFile(context, new String[]{targetPath}, null, null);

                    }
                });

                relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.getappinfo.app.Adapter.RecycleViewAdapterDemo.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", appPackageName, null));
                        RecycleViewAdapterDemo.this.context.startActivity(intent);
                        try {
                            RecycleViewAdapterDemo.this.forceStopPackage(appPackageName, RecycleViewAdapterDemo.this.context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 设置缩进
                limitStringTo140(textView2.getText().toString(), textView2, new View.OnClickListener() {
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        Log.e("limitStringTo140 ", "");
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceStopPackage(String str, Context context) throws Exception {
        Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class).invoke((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE), str);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.list.size();
    }

    @RequiresApi(api = 26)
    public static HashMap<String, Long> getAppStorage(Context context, String str) {
        UUID uuid;
        HashMap<String, Long> hashMap = new HashMap<>();
        StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        for (StorageVolume storageVolume : ((StorageManager) context.getSystemService(Context.STORAGE_SERVICE)).getStorageVolumes()) {
            String uuid2 = storageVolume.getUuid();
            try {
                if (TextUtils.isEmpty(uuid2)) {
                    uuid = StorageManager.UUID_DEFAULT;
                } else {
                    uuid = UUID.fromString(uuid2);
                }
            } catch (Exception unused) {
                uuid = StorageManager.UUID_DEFAULT;
            }
            try {
                StorageStats queryStatsForPackage = storageStatsManager.queryStatsForPackage(uuid, str, Process.myUserHandle());
                long totalBytes = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT);
                long freeBytes = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT);
                Log.e("", "totalBytes:" + formatFileSize(totalBytes) + " freeBytes:" + formatFileSize(freeBytes) + " aleadyUseBytes:" + formatFileSize(totalBytes - freeBytes));
                hashMap.put("AppAllSize", Long.valueOf(queryStatsForPackage.getAppBytes() + queryStatsForPackage.getCacheBytes() + queryStatsForPackage.getDataBytes()));
                hashMap.put("AppBytesSize", Long.valueOf(queryStatsForPackage.getAppBytes()));
                hashMap.put("cacheBytesSize", Long.valueOf(queryStatsForPackage.getCacheBytes()));
                hashMap.put("dataBytesSize", Long.valueOf(queryStatsForPackage.getDataBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

    public static String formatFileSize(long j) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (j == 0) {
            return "";
        }
        if (j < 1024) {
            return decimalFormat.format(j) + "B";
        } else if (j < 1048576) {
            return decimalFormat.format(j / 1048576.0d) + "KB";
        } else if (j < 1073741824) {
            return decimalFormat.format(j / 1.073741824E9d) + "MB";
        } else {
            return decimalFormat.format(j / 1.073741824E9d) + "GB";
        }
    }

    public static int getLastCharIndexForLimitTextView(TextView textView, String str, int i, int i2) {
        Log.i("Alex", "宽度是" + i);
        StaticLayout staticLayout = new StaticLayout(str, textView.getPaint(), i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (staticLayout.getLineCount() > i2) {
            return staticLayout.getLineStart(i2) - 1;
        }
        return -1;
    }

    public static int[] measureTextViewHeight(TextView textView, String str, int i, int i2) {
        Log.i("Alex", "宽度是" + i);
        TextPaint paint = textView.getPaint();
        StaticLayout staticLayout = new StaticLayout(str, paint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int[] iArr = new int[2];
        if (staticLayout.getLineCount() > i2) {
            int lineStart = staticLayout.getLineStart(i2) - 1;
            iArr[0] = lineStart;
            iArr[1] = new StaticLayout(str.substring(0, lineStart), paint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).getHeight();
            return iArr;
        }
        iArr[0] = -1;
        iArr[1] = staticLayout.getHeight();
        return iArr;
    }

    public static void limitStringTo140(final String str, final TextView textView, final View.OnClickListener onClickListener) {
        long currentTimeMillis = System.currentTimeMillis();
        if (textView != null) {
            int width = textView.getWidth();
            if (width == 0) {
                width = 1000;
            }
            int lastCharIndexForLimitTextView = getLastCharIndexForLimitTextView(textView, str, width, 11);
            //设置收起的宽度
            int TextShrinkageWidth = 230;


            if (lastCharIndexForLimitTextView >= 0 || str.length() > TextShrinkageWidth) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                if (lastCharIndexForLimitTextView > TextShrinkageWidth || lastCharIndexForLimitTextView < 0) {
                    lastCharIndexForLimitTextView = TextShrinkageWidth;
                }
                String str2 = null;
                if (str.charAt(lastCharIndexForLimitTextView) == '\n') {
                    str2 = str.substring(0, lastCharIndexForLimitTextView);
                } else if (lastCharIndexForLimitTextView > 12) {
                    Log.i("Alex", "the last char of this line is --" + lastCharIndexForLimitTextView);
                    str2 = str.substring(0, lastCharIndexForLimitTextView - 12);
                }
                int length = str2.length();
                String str3 = str2 + "...show more";
                SpannableString spannableString = new SpannableString(str3);
                spannableString.setSpan(new ClickableSpan() { // from class: com.getappinfo.app.Adapter.RecycleViewAdapterDemo.3
                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setColor(textView.getResources().getColor(R.color.purple_700));
                        textPaint.setAntiAlias(true);
                        textPaint.setUnderlineText(false);
                    }

                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        Log.i("Alex", "click showmore");
                        textView.setText(str);
                        textView.setOnClickListener(null);
                        new Handler().postDelayed(new Runnable() { // from class: com.getappinfo.app.Adapter.RecycleViewAdapterDemo.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (onClickListener != null) {
                                    textView.setOnClickListener(onClickListener);
                                }
                            }
                        }, 20L);
                    }
                }, length, str3.length(), 33);
                textView.setText(spannableString);
                Log.i("Alex", "字符串处理耗时" + (System.currentTimeMillis() - currentTimeMillis));
                return;
            }
            textView.setText(str);
        }
    }

    /* loaded from: classes.dex */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView app_allinfo, export_app;
        public RelativeLayout rl_bg;

        public MyViewHolder(View view) {
            super(view);
            this.rl_bg = (RelativeLayout) view.findViewById(R.id.rl_bg);
            this.app_allinfo = (TextView) view.findViewById(R.id.app_allinfo);
            this.appIcon = (ImageView) view.findViewById(R.id.app_icon);
            this.appName = (TextView) view.findViewById(R.id.app_name);
            this.export_app = (TextView) view.findViewById(R.id.export_app);
        }
    }
}