package com.fgtit.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class PostMaster extends Service {
    NetworkChangeReceiverPostMaster networkChangeReceiverPostMaster;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        networkChangeReceiverPostMaster = new NetworkChangeReceiverPostMaster();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //register an internet connectivity broadcast receiver
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        networkChangeReceiverPostMaster.onReceive(this,intent);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

}
