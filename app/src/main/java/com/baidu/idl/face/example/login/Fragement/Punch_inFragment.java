package com.baidu.idl.face.example.login.Fragement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.example.login.notification.NotificationUtils;
import com.baidu.idl.face.example.login.recycleview.Punch_ry;
import com.baidu.idl.face.example.login.tools.Http_tools;
import com.baidu.idl.face.example.login.tools.Punch_tools;
import com.example.rnsb_start.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Cookie;

public class Punch_inFragment extends Fragment {
    private static final String TAG = "Punch_inFragment:";
    private Context context;
    private TextView completed_user, sum_user;
    private Handler handler = new Handler();
    private String id, name, Class;
    private List<Punch_tools> list = new ArrayList<>();
    private RecyclerView ry;
    private NotificationUtils notificationUtils;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.punch_in, null, false);
        return view;
    }

    private void initView(View view) {
        completed_user = view.findViewById(R.id.completed_user);
        sum_user = view.findViewById(R.id.sum_user);
        ry = view.findViewById(R.id.ry);
        id = getArguments().getString("id");
        Class = getArguments().getString("Class");
        name = getArguments().getString("name");
    }


    private void initData() {
        Http_tools tools = new Http_tools();
        String[] id = getResources().getStringArray(R.array.id);
        String[] Class = getResources().getStringArray(R.array.Class);
        new Thread(() -> {
            String data = null;
            try {
                for (int i = 0; i < Class.length; i++) {
                    if (this.Class.equals(Class[i])) {
                        data = tools.Authority(id[i]);//找到所在班级id
                    }
                }
                if (data != null) {
                    JSONObject jsonObject = new JSONObject(data);//登录获取key，verify
                    String orgUserCount = jsonObject.getJSONObject("data").getString("orgUserCount");//总人数
                    String reportCount = jsonObject.getJSONObject("data").getString("reportCount");//完成人数
                    for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("users").length(); i++) {
                        String name = jsonObject.getJSONObject("data").getJSONArray("users").getJSONObject(i).getString("user_name");
                        String is_report = jsonObject.getJSONObject("data").getJSONArray("users").getJSONObject(i).getString("is_report");
                        list.add(new Punch_tools(name, is_report));
                        if (this.name.equals(name)) {
                            if (is_report.equals("0")) {
                                System.out.println("发送通知");
                                StartNotice();
                            } else {
                                System.out.println("已完成");
                            }
                        }
                    }
                    handler.post(() -> {
                        sum_user.setText("总人数:" + orgUserCount);
                        completed_user.setText("完成:" + reportCount);
                        ry.setLayoutManager(new LinearLayoutManager(context));
                        Punch_ry punch_ry = new Punch_ry(list);
                        ry.setAdapter(punch_ry);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void StartNotice() {
        NotificationUtils notificationUtils=new NotificationUtils(context);
        notificationUtils.sendNotification("截图","今天微哨未打卡");
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    public static final Punch_inFragment newInstance(String id, String Class, String name) {
        Punch_inFragment fragment = new Punch_inFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("Class", Class);
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

}
