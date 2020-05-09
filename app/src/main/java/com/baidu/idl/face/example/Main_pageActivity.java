package com.baidu.idl.face.example;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.example.login.Fragement.AnimeFragment;
import com.baidu.idl.face.example.login.Fragement.ClassFragment;
import com.baidu.idl.face.example.login.Fragement.NoScrollViewPager;
import com.baidu.idl.face.example.login.Fragement.PictureFragment;
import com.baidu.idl.face.example.login.Fragement.Punch_inFragment;
import com.baidu.idl.face.example.login.Fragement.QueryFragment;
import com.baidu.idl.face.example.login.Fragement.TimetableFragment;
import com.baidu.idl.face.example.login.Fragement.UserFragment;
import com.baidu.idl.face.example.login.Fragement.VariableFragment;
import com.baidu.idl.face.example.login.Title;
import com.baidu.idl.face.example.login.litepal.Book;
import com.baidu.idl.face.example.login.litepal.First_time;
import com.baidu.idl.face.example.login.service.MyService;
import com.baidu.idl.face.example.login.service.TraceServiceImpl;
import com.baidu.idl.face.example.login.tools.Http_tools;
import com.baidu.idl.face.example.login.util.DaemonEnv;
import com.baidu.idl.face.example.login.util.IntentWrapper;
import com.example.rnsb_start.R;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main_pageActivity extends AppCompatActivity {
    private NoScrollViewPager vp;
    private List<Fragment> myFragment = new ArrayList<>();
    private String Class, name, id;
    private Context context;
    private Intent intent;
    private TextView Exit_tv;
    private ProgressDialog progressDialog;
    private Handler handler=new Handler();
    private LinearLayout btn1,btn2,btn3,btn4,btn5;
    private ImageView iv1,iv2,iv3,iv4,iv5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initTitle();
        initView();
        initData();
        initClick();
    }



    private void initClick() {
        btn1.setOnClickListener(v -> {
            vp.setCurrentItem(0);
            iv1.setBackgroundResource(R.mipmap.kebiao2);
            iv2.setBackgroundResource(R.mipmap.chaxun1);
            iv4.setBackgroundResource(R.mipmap.tuxiang1);
            iv5.setBackgroundResource(R.mipmap.wode);
        });
        btn2.setOnClickListener(v -> {
            vp.setCurrentItem(1);
            iv2.setBackgroundResource(R.mipmap.chaxun2);
            iv1.setBackgroundResource(R.mipmap.kebiao1);
            iv4.setBackgroundResource(R.mipmap.tuxiang1);
            iv5.setBackgroundResource(R.mipmap.wode);
        });
        btn3.setOnClickListener(v -> {
            vp.setCurrentItem(2);
        });
        btn4.setOnClickListener(v -> {
            vp.setCurrentItem(3);
            iv4.setBackgroundResource(R.mipmap.tuxiang2);
            iv1.setBackgroundResource(R.mipmap.kebiao1);
            iv2.setBackgroundResource(R.mipmap.chaxun1);
            iv5.setBackgroundResource(R.mipmap.wode);
        });
        btn5.setOnClickListener(v -> {
            vp.setCurrentItem(4);
            iv5.setBackgroundResource(R.mipmap.wode1);
            iv2.setBackgroundResource(R.mipmap.chaxun1);
            iv1.setBackgroundResource(R.mipmap.kebiao1);
            iv4.setBackgroundResource(R.mipmap.tuxiang1);
        });
        handler.post(()->{
            progressDialog.dismiss();
            Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show();
        });
    }
    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    public void onBackPressed() {
        IntentWrapper.onBackPressed(this);
    }
    private void initData() {
        if (id != null || Class != null || name != null) {
            myFragment.add(TimetableFragment.newInstance(id, Class, name));//课表查询
            myFragment.add(QueryFragment.newInstance(id,Class,name));//班级查询
            myFragment.add(VariableFragment.newInstance(id, Class, name));//图片上传
            myFragment.add(PictureFragment.newInstance(id,Class,name));//图像处理
            myFragment.add(UserFragment.newInstance(id, Class, name));//个人中心
        } else {
            System.out.println("空的》》》》》》》》》》》》");
        }
        vp.setOffscreenPageLimit(myFragment.size());
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return myFragment.get(i);
            }
            @Override
            public int getCount() {
                return myFragment.size();
            }
        });
        Exit_tv.setOnClickListener(v -> {
            finish();
            LitePal.deleteAll(Book.class);
            Intent intent=new Intent(this, FaceDetectExpActivity.class);
            startActivity(intent);
        });
    }

    private void initTitle() {
        Title title = new Title(getActionBar(), getWindow().getDecorView());
        title.tv.setText("用户中心");
        title.iv.setVisibility(View.GONE);
    }
    private void initView() {
        handler.post(()->{
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("小智");
            progressDialog.setMessage("加载中");
            progressDialog.show();
        });
        TraceServiceImpl.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
        vp = findViewById(R.id.vp);
        Exit_tv=findViewById(R.id.Exit_tv);
        btn1=findViewById(R.id.btn1);
        btn2=findViewById(R.id.btn2);
        btn3=findViewById(R.id.btn3);
        btn4=findViewById(R.id.btn4);
        btn5=findViewById(R.id.btn5);
        iv1=findViewById(R.id.iv1);
        iv2=findViewById(R.id.iv2);
        iv3=findViewById(R.id.iv3);
        iv4=findViewById(R.id.iv4);
        iv5=findViewById(R.id.iv5);
        Class = getIntent().getStringExtra("Class");
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
