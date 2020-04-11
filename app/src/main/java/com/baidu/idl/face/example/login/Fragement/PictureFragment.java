package com.baidu.idl.face.example.login.Fragement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rnsb_start.R;

import java.util.ArrayList;
import java.util.List;

public class PictureFragment extends Fragment {
    private Context context;
    private TabLayout tablelayout;
    private ViewPager vp;
    private List<String> myTitle=new ArrayList<>();
    private List<Fragment>myFragment=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.picturefragment, null, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tablelayout = view.findViewById(R.id.tablelayout);
        vp = (ViewPager) view.findViewById(R.id.vp);
        tablelayout.setupWithViewPager(vp);
        tablelayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        String[] string = getResources().getStringArray(R.array.anime);

        for (int i = 0; i < string.length; i++) {
            myTitle.add(string[i]);
        }
        myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/selfie_anime"));//动漫化
        myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/colourize"));//黑白图像上色
        myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/dehaze"));//图像去雾
        myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/stretch_restore"));//拉伸恢复
        myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/contrast_enhance"));//对比度增强
        myFragment.add(AnimeFragment.newInstance("https://aip.baidubce.com/rest/2.0/image-process/v1/image_quality_enhance"));//无损放大
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        vp.setOffscreenPageLimit(myFragment.size());
        vp.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
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


}
