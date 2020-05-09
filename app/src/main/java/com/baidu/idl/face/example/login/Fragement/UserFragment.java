package com.baidu.idl.face.example.login.Fragement;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rnsb_start.R;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import de.hdodenhof.circleimageview.CircleImageView;
public class UserFragment extends Fragment {
    private String id, Class, name,integral;
    private CircleImageView User_iv;
    private TextView name_tv;
    private TextView student_tv;
    private TextView Class_tv;
    private TextView integral_tv;
    private SwipeRefreshLayout sw;
    private Context context;
    private Handler handler=new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=container.getContext();
        View view = inflater.inflate(R.layout.userfragment, container, false);
        initView(view);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                sw.setRefreshing(false);
                handler.post(()->{
                    Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void initData() {
        if(id.equals("QQ:3297364807")){
            obtain("http://q.qlogo.cn/headimg_dl?dst_uin=3297364807&spec=640");//显示头像,用户信息
            User_iv.setOnClickListener(v -> {
                    String qqUrl = "mqqwpa://im/chat?chat_type=wpa&version=1&uin=3297364807";//uin是发送过去的qq号码
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
            });
        }else {
            obtain("http://39.106.133.87/images/" + id + ".jpg");
        }
        new Thread(()->{
            initCleck();
        }).start();
    }

    private void initCleck() {
        try {
            init_List2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void init_List2() throws IOException {
        Socket client=new Socket("39.106.133.87",7656);//发送连接
        DataOutputStream dos=new DataOutputStream(client.getOutputStream());//发送数据
        dos.writeUTF(Class);
        dos.flush();
        String data=null;
        DataInputStream dis=new DataInputStream(client.getInputStream());//接收服务器的数据
        data=dis.readUTF();
        String[] list1=data.split(" ");
        for(int i=0;i<list1.length;i++) {
            if(list1[i]!=null) {
                String[] unknown=list1[i].split(",");
                if(id.equals(unknown[1])){
                    handler.post(()->{
                        name_tv.setText(unknown[0]);
                        student_tv.setText(unknown[1]);
                        Class_tv.setText(unknown[3]);
                        integral_tv.setText(unknown[4]);
                    });
                }
            }
        }
        dos.close();
        dis.close();
        client.close();
    }

    private void obtain(String path) {
        ViewGroup.LayoutParams param;
        param = User_iv.getLayoutParams();
        int pixels=(this.getResources().getDisplayMetrics().widthPixels)/4;
        param.height = pixels;
        param.width = pixels;
        Glide.with(this).load(path).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(User_iv);
    }

    public static final UserFragment newInstance(String id, String Class, String name) {
        UserFragment fragment = new UserFragment();
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
        integral=getArguments().getString("integral");

    }


    private void initView(View view) {
        User_iv = (CircleImageView) view.findViewById(R.id.User_iv);
        name_tv = (TextView) view.findViewById(R.id.name_tv);
        student_tv = (TextView) view.findViewById(R.id.student_tv);
        Class_tv = (TextView) view.findViewById(R.id.Class_tv);
        integral_tv=view.findViewById(R.id.integral_tv);
        sw=view.findViewById(R.id.sw);
    }
}
