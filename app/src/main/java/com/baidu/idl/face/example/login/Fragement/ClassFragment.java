package com.baidu.idl.face.example.login.Fragement;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.idl.face.example.login.recycleview.login_ry;
import com.baidu.idl.face.example.login.tools.login_tools;
import com.baidu.idl.face.example.login.tools.login_tools2;
import com.example.rnsb_start.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClassFragment extends Fragment {
    private String Class, name, id;
    private Context context;
    private List<login_tools2> list2 = new ArrayList<login_tools2>();
    private Handler handler = new Handler();
    private RecyclerView ry;
    private List<login_tools> list = new ArrayList<>();
    private SwipeRefreshLayout sw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.classfragment, null, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ry = view.findViewById(R.id.recycler_view);
        sw = view.findViewById(R.id.sw);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(() -> {
            initData();
        }).start();
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(() -> {
                    initData();
                    sw.setRefreshing(false);
                    handler.post(()->{
                        Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            }
        });
    }

    private void initData() {
        list.clear();
        list2.clear();
        try {
            init_List2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list2.size(); i++) {
            list.add(new login_tools(list2.get(i).getName(), list2.get(i).getRecording(), list2.get(i).getStudent()));
        }
        handler.post(() -> {
            ry.setLayoutManager(new LinearLayoutManager(context));
            login_ry login_ry = new login_ry(list, Class, name);
            ry.setAdapter(login_ry);
        });
    }

    private void init_List2() throws IOException {
        Socket client = new Socket("39.106.133.87", 7656);//发送连接
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());//发送数据
        dos.writeUTF(Class);
        dos.flush();
        String data = null;
        DataInputStream dis = new DataInputStream(client.getInputStream());//接收服务器的数据
        data = dis.readUTF();
        String[] list1 = data.split(" ");
        for (int i = 0; i < list1.length; i++) {
            if (list1[i] != null) {
                String[] unknown = list1[i].split(",");
                list2.add(new login_tools2(unknown[0], unknown[1], unknown[2]));
            }
        }
        dos.close();
        dis.close();
        client.close();
    }


    public static final ClassFragment newInstance(String id, String Class, String name) {
        ClassFragment fragment = new ClassFragment();
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


}
