package com.blogspot.zone4apk.bookstash;

import android.app.Application;

public class MyApplication extends Application {
    public static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReciever.ConnectivityRecieverListener listener) {
        ConnectivityReciever.connectivityRecieverListener = listener;
    }
}
