package com.proton.smartkitchen;

import android.app.Application;

import com.backendless.Backendless;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Backendless.initApp( this, "AppID", "AndroidKey" );
    }
}
