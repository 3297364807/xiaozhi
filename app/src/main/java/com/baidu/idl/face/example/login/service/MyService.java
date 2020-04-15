package com.baidu.idl.face.example.login.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Observable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.baidu.idl.face.example.login.tools.PollingUtil;

import java.util.Observer;

public class MyService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        PollingUtil pollingUtil=new PollingUtil(new Handler(getMainLooper()));
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyService.this, "定时任务", Toast.LENGTH_SHORT).show();
            }
        };
        pollingUtil.startPolling(runnable,5000,true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
