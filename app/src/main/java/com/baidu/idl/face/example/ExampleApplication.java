package com.baidu.idl.face.example;

import android.app.Application;

import com.baidu.idl.face.example.login.service.TraceServiceImpl;
import com.baidu.idl.face.example.login.util.DaemonEnv;
import com.baidu.idl.face.platform.LivenessTypeEnum;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ExampleApplication extends Application {

    public static List<LivenessTypeEnum> livenessList = new ArrayList<LivenessTypeEnum>();
    public static boolean isLivenessRandom = false;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        TraceServiceImpl.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

    }
}
