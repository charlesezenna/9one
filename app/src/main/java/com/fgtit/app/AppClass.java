package com.fgtit.app;

import android.app.Application;

public class AppClass extends Application {
    private static AppClass appClass;

    @Override
    public void onCreate() {
        super.onCreate();
        appClass = this;
    }

    public static AppClass getInstance() {
        return appClass;

    }
}