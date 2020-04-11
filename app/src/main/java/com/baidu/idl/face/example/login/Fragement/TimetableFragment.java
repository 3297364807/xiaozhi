package com.baidu.idl.face.example.login.Fragement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rnsb_start.R;

public class TimetableFragment extends Fragment {
    private String id, name, Class;
    private Context context;
    private PhotoView pv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.timetablefragment, null, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtain();
    }

    //获取头像图片
    private void obtain() {
        String path = "http://39.106.133.87/kebiao/" + Class + ".png";
        System.out.println(path);
        Glide.with(this).load(path).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(pv);
        pv.enable();
    }


    public static final TimetableFragment newInstance(String id, String Class, String name) {
        TimetableFragment fragment = new TimetableFragment();
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

    private void initView(View view) {
        pv = (PhotoView) view.findViewById(R.id.pv);
    }
}
