package com.baidu.idl.face.example.login.Fragement;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.example.login.tools.CA_Certificate;
import com.baidu.idl.face.example.login.tools.Cookie;
import com.baidu.idl.face.example.login.tools.Http_tools;
import com.example.rnsb_start.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Punch_inFragment extends Fragment {
    private static final String TAG = "Punch_inFragment:";
    private Context context;
    private TextView tv;
    private Handler handler=new Handler();
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.punch_in, null, false);

            Http_tools tools=new Http_tools();
            new Thread(()->{
                String data= null;//請求
                try {
                    data = tools.Punch_in(new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
                    if(data.charAt(0)=='{'){//請求到了
                        System.out.println(data);
                    }else {
                        List<Cookie> list= LitePal.findAll(Cookie.class);//数据库读取出来
                        if(list.size()>=1){//读取读
                            data=tools.Punch_in2(list.get(0).getKey(),list.get(0).getCookie(),new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
                            System.out.println(data);
                        }else {//未读取到
                            new Thread(()->{
                                initView(view);
                            }).start();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();







        return view;
    }

    private void saveDatabase(String cookie,String key) {
        Cookie cookie1=new Cookie();
        cookie1.setCookie(cookie);
        cookie1.setKey(key);
        cookie1.save();
    }
    private void initView(View view) {
        tv=view.findViewById(R.id.tv);
        Log.e(TAG, "开始》》》》》》》");
        Http_tools tools=new Http_tools();
        handler.post(()->{
            progressDialog=new ProgressDialog(context);
            progressDialog.setTitle("小智");
            progressDialog.setMessage("加载中");
            progressDialog.show();
        });
        try {

            List<Cookie> list= LitePal.findAll(Cookie.class);
            for(int i=0;i<list.size();i++){

            }
            JSONObject jsonObject = new JSONObject(tools.login());//登录获取key，verify
            String key=jsonObject.getJSONObject("data").getJSONObject("my_info").getString("skey");
            String verify=jsonObject.getJSONObject("data").getString("verify");
            String cookies=tools.Authority(verify,key);//1.带key，verify请求返回cookie
            tools.Authority2();//2.无返回值请求
            cookies=tools.Authority3(key,cookies);//3.重新赋值cookie
            cookies=tools.Authority4(key,cookies);//4.继续重新赋值cookie
            cookies=tools.Authority5(key,cookies);//5.继续重新赋值cookie
            cookies=tools.Authority6(key,cookies);//6.获得最终查询cookie
            String date=new SimpleDateFormat("YYYY-MM-dd").format(new Date());
            String data = tools.Punch_in2(key,cookies,date);
            saveDatabase(cookies,key);
            handler.post(()->{
                tv.setText(data);
            });
            handler.post(()->{
                progressDialog.dismiss();
                Toast.makeText(context, "加载完成", Toast.LENGTH_SHORT).show();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//            String[] one=cookies.split("=");
//            String cookies2=tools.Authority2(one[1]);
//
//            System.out.println("cookies2"+cookies2);
//
//            String[] cookie3=cookies2.split("=");
//
//
//            for(int i=0;i<cookie3.length;i++){
//                System.out.println(cookie3[i]);
//            }

//            String data=tools.Punch_in(cookie3[1],key);
//            System.out.println(data);



        Log.e(TAG, "结束》》》》》》》");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
