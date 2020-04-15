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
import android.widget.Toast;

import com.baidu.idl.face.example.login.Fragement.AnimeFragment;
import com.baidu.idl.face.example.login.Fragement.ClassFragment;
import com.baidu.idl.face.example.login.Fragement.PictureFragment;
import com.baidu.idl.face.example.login.Fragement.Punch_inFragment;
import com.baidu.idl.face.example.login.Fragement.TimetableFragment;
import com.baidu.idl.face.example.login.Fragement.VariableFragment;
import com.baidu.idl.face.example.login.Title;
import com.baidu.idl.face.example.login.litepal.First_time;
import com.baidu.idl.face.example.login.service.MyService;
import com.baidu.idl.face.example.login.service.TraceServiceImpl;
import com.baidu.idl.face.example.login.util.DaemonEnv;
import com.baidu.idl.face.example.login.util.IntentWrapper;
import com.example.rnsb_start.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class Main_pageActivity extends AppCompatActivity {
    private TabLayout tablelayout;
    private ViewPager vp;
    private List<String> myTitle = new ArrayList<>();
    private List<Fragment> myFragment = new ArrayList<>();
    private String Class, name, id;
    private Context context;
    private Intent intent;
    private ProgressDialog progressDialog;
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        handler.post(()->{
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("小智");
            progressDialog.setMessage("加载中");
            progressDialog.show();
        });
//        if(LitePal.findAll(First_time.class).size()==0){
//            System.out.println("进来");
//            intent = new Intent(this, MyService.class);
//            startService(intent);
//        }
        TraceServiceImpl.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

        initTitle();
        Class = getIntent().getStringExtra("Class");
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        initView();
        initData();

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

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return myTitle.get(position);
            }
        });


    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        startService(intent);
//        System.out.println("关闭服务");
//    }

    private void initTitle() {
        Title title = new Title(getActionBar(), getWindow().getDecorView());
        title.tv.setText("用户中心");
        title.iv.setVisibility(View.GONE);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        startService(intent);
//        System.out.println("关闭服务");
//    }

    private void initView() {
        tablelayout = findViewById(R.id.tablelayout);
        vp = (ViewPager) findViewById(R.id.vp);
        tablelayout.setupWithViewPager(vp);
        tablelayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        String[] string = getResources().getStringArray(R.array.bottom);

        for (int i = 0; i < string.length; i++) {
            myTitle.add(string[i]);
        }
        if (id != null || Class != null || name != null) {
            myFragment.add(VariableFragment.newInstance(id, Class, name));
            myFragment.add(ClassFragment.newInstance(id, Class, name));//班级查询
            myFragment.add(TimetableFragment.newInstance(id, Class, name));//课表查询
            myFragment.add(new PictureFragment());//图像处理
            myFragment.add(Punch_inFragment.newInstance(id,Class,name));//对比度增强
            myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance"));//无损放大
        } else {
            System.out.println("空的》》》》》》》》》》》》");
        }
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
