package com.baidu.idl.face.example.login.service;

import android.content.*;
import android.os.*;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.baidu.idl.face.example.login.litepal.Book;
import com.baidu.idl.face.example.login.notification.NotificationUtils;
import com.baidu.idl.face.example.login.recycleview.Punch_ry;
import com.baidu.idl.face.example.login.tools.Http_tools;
import com.baidu.idl.face.example.login.tools.Punch_tools;
import com.baidu.idl.face.example.login.util.*;
import com.example.rnsb_start.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import io.reactivex.disposables.*;

public class TraceServiceImpl extends AbsWorkService {

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        PollingUtil pollingUtil=new PollingUtil(new Handler(getMainLooper()));
        Http_tools tools = new Http_tools();
        String[] id = getResources().getStringArray(R.array.id);
        String[] Class = getResources().getStringArray(R.array.Class);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                List<Book> list= LitePal.findAll(Book.class);
                if(list.size()!=0){
                    new Thread(()->{
                        String data = null;
                        try {
                            for (int i = 0; i < Class.length; i++) {
                                if (list.get(0).getCLASS().equals(Class[i])) {
                                    data = tools.Authority(id[i]);//找到所在班级id
                                }
                            }
                            if (data != null) {
                                JSONObject jsonObject = new JSONObject(data);//登录获取key，verify
                                for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("users").length(); i++) {
                                    String name = jsonObject.getJSONObject("data").getJSONArray("users").getJSONObject(i).getString("user_name");
                                    String is_report = jsonObject.getJSONObject("data").getJSONArray("users").getJSONObject(i).getString("is_report");
                                    if (list.get(0).getName().equals(name)) {
                                        if (is_report.equals("0")) {//未完成发送通知
                                            System.out.println("发送通知");
                                            StartNotice();
                                        } else {
                                            System.out.println("已完成");
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }else {
                    System.out.println("未保存读取不到");
                }

            }
        };
        pollingUtil.startPolling(runnable,1800000,false);
    }
    private void StartNotice() {
        NotificationUtils notificationUtils=new NotificationUtils(this);
        notificationUtils.sendNotification("截图","今天微哨未打卡");
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        System.out.println("保存数据到磁盘。");
    }
}
