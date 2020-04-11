package com.baidu.idl.face.example.login.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.idl.face.example.Main_pageActivity;
import com.baidu.idl.face.example.login.Title;
import com.baidu.idl.face.example.login.recycleview.Registered_ry;
import com.baidu.idl.face.example.login.tools.Book;
import com.baidu.idl.face.example.login.tools.Http_tools;
import com.baidu.idl.face.example.login.tools.Registered_tools;
import com.example.rnsb_start.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.GsonUtils;
import tools.HttpUtil;

public class RegisteredActivity extends Activity {
    private EditText edit,name,student;
    private RecyclerView ry;
    private String[] Student_class;
    private Handler handler = new Handler();
    private List<Registered_tools> list=new ArrayList<>();
    private Context context;
    private Button button;
    private JSONArray post_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        initView();
        initTitle();
        new Thread(() -> {
            initData();
        }).start();
        initCleck();
    }


    public static String add( String access_token, String base64Data,String group) {
        System.out.println("令牌2="+access_token);
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64Data);
            map.put("group_id", "2018jsj_3");
            map.put("user_id", group);
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");
            map.put("action_type","APPEND");
            String param = GsonUtils.toJson(map);
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = access_token;
            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private void initCleck() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>=18){
                    handler.post(()->{
                        name.setText("");
                    });
                    return;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        student.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>=18){
                    handler.post(()->{
                        student.setText("");
                    });
                    return;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        button.setOnClickListener(v -> {
            if(name.equals(null)||name.length()==0){
                Toast.makeText(context, "姓名未输入", Toast.LENGTH_SHORT).show();
                return;
            }
            if(student.equals(null)||student.length()==0){
                Toast.makeText(context, "学号未输入", Toast.LENGTH_SHORT).show();
                return;
            }
            if(edit.equals(null)||edit.length()==0){
                Toast.makeText(context, "班级未输入", Toast.LENGTH_SHORT).show();
                return;
            }
            if(checkNameChese(name.getText().toString())){
                new Thread(()->{
                    postData();
                }).start();
            }else {
                Toast.makeText(context, "只能输入中文", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void postData() {
        Http_tools tools=new Http_tools();
        try {
            JSONArray len = new JSONArray(tools.get("http://39.106.133.87:13456/list"));
            int lenght=len.length()+1;
            String data="{    \"name\": \""+name.getText().toString()+"\",    \"class\": \""+edit.getText().toString()+"\",    \"student\": \""+student.getText().toString()+"\",    \"integral\": \"0\",    \"id\": "+lenght+"}";
            String json=tools.post("http://39.106.133.87:13456/list/",data);
            JSONObject jsonObject=new JSONObject(json);
            add(getIntent().getStringExtra("access_token"),getIntent().getStringExtra("base64Data"),student.getText().toString());
            boolean result=jsonObject.getString("name").equals(name.getText().toString());
            if(result){
                handler.post(()->{
                    Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                });
                finish();
                Intent intent=new Intent(this, Main_pageActivity.class);
                intent.putExtra("id",student.getText().toString());
                intent.putExtra("Class",edit.getText().toString());
                intent.putExtra("name",name.getText().toString());
                startActivity(intent);
            }else {
                handler.post(()->{
                    Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //判断是否中文
    public  boolean checkNameChese(String name)
    {
        boolean res=true;
        char [] cTemp = name.toCharArray();
        for(int i=0;i<name.length();i++)
        {
            if(!isChinese(cTemp[i]))
            {
                res=false;
                break;
            }
        }
        return res;
    }
    public  boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
    private void initData() {
        Http_tools tools = new Http_tools();
        try {
            JSONArray data = new JSONArray(tools.get("http://39.106.133.87:13456/class"));
            Student_class = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                Student_class[i] = data.getJSONObject(i).getString("name");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>=18){
                    handler.post(()->{
                        edit.setText("");
                    });
                    return;
                }
                list.clear();
                for (int j = 0; j < Student_class.length; j++) {
                    boolean status = Student_class[j].contains(charSequence.toString());
                    if (status && charSequence.toString().length() >= 1) {
                        list.add(new Registered_tools(Student_class[j]));
                    } else {

                    }
                }
                handler.post(()->{
                    ry.setLayoutManager(new LinearLayoutManager(context));
                    Registered_ry registered_ry=new Registered_ry(list,edit);
                    ry.setAdapter(registered_ry);
                });
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    private void initTitle() {
        Title title = new Title(getActionBar(), getWindow().getDecorView());
        title.tv.setText("账号注册");
        title.iv.setOnClickListener(view -> {
            finish();
        });
    }

    private void initView() {
        edit = (EditText) findViewById(R.id.edit);
        name = (EditText) findViewById(R.id.name);
        student = (EditText) findViewById(R.id.student);
        ry = (RecyclerView) findViewById(R.id.ry);
        button=findViewById(R.id.button);
        context=this;
    }

}
