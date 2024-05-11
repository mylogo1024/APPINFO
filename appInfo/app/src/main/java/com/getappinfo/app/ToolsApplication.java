package com.getappinfo.app;

import android.app.Application;

public class ToolsApplication extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}