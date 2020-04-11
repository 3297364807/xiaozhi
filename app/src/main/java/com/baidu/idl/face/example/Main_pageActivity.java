package com.baidu.idl.face.example;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.idl.face.example.login.Fragement.AnimeFragment;
import com.baidu.idl.face.example.login.Fragement.ClassFragment;
import com.baidu.idl.face.example.login.Fragement.PictureFragment;
import com.baidu.idl.face.example.login.Fragement.Punch_inFragment;
import com.baidu.idl.face.example.login.Fragement.TimetableFragment;
import com.baidu.idl.face.example.login.Fragement.VariableFragment;
import com.baidu.idl.face.example.login.Title;
import com.baidu.idl.face.example.login.tools.Book;
import com.example.rnsb_start.R;

import org.litepal.LitePal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main_pageActivity extends AppCompatActivity {
    private TabLayout tablelayout;
    private ViewPager vp;
    private List<String> myTitle = new ArrayList<>();
    private List<Fragment> myFragment = new ArrayList<>();
    private String Class, name, id;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initTitle();
        Class = getIntent().getStringExtra("Class");
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        initView();
        initData();
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


    private void initTitle() {
        Title title = new Title(getActionBar(), getWindow().getDecorView());
        title.tv.setText("用户中心");
        title.iv.setOnClickListener(view -> {
            finish();
        });
    }

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
            myFragment.add(new Punch_inFragment());//对比度增强
            myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance"));//无损放大
        } else {
            System.out.println("空的》》》》》》》》》》》》");
        }


    }
}
