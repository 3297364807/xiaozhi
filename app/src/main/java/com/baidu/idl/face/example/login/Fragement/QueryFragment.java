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

public class QueryFragment extends Fragment {
    private Context context;
    private TabLayout tablelayout;
    private ViewPager vp;
    private String Class, name, id;
    private List<String> myTitle=new ArrayList<>();
    private List<Fragment>myFragment=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.queryfragment, null, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tablelayout = view.findViewById(R.id.tablelayout);
        vp = (ViewPager) view.findViewById(R.id.vp);
        tablelayout.setupWithViewPager(vp);
        String[] string = {"班级查询","微哨查询"};
        for (int i = 0; i < string.length; i++) {
            myTitle.add(string[i]);
        }
        myFragment.add(ClassFragment.newInstance(id, Class, name));//班级查询
        myFragment.add(Punch_inFragment.newInstance(id,Class,name));//微哨查询
    }

    public static final QueryFragment newInstance(String id, String Class, String name) {
        QueryFragment fragment = new QueryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("Class", Class);
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        Class = getArguments().getString("Class");
        name = getArguments().getString("name");
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
