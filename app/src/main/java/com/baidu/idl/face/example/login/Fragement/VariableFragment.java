package com.baidu.idl.face.example.login.Fragement;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rnsb_start.R;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import static android.app.Activity.RESULT_OK;
public class VariableFragment extends Fragment{
    private ImageView imageView_okhttp;
    private Button Select_pictures;
    private Button UploadImage;
    private Context context;
    private Handler handler=new Handler();
    private ProgressDialog progressDialog;
    private Bitmap TCP_images;
    private boolean Check=true;
    private String id,name,Class;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=container.getContext();
        View view=inflater.inflate(R.layout.variablefragment,null,false);
        initView(view);
        return view;
    }
    public static final VariableFragment newInstance(String id,String Class,String name)
    {
        VariableFragment fragment = new VariableFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("Class", Class);
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment ;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id=getArguments().getString("id");
        Class=getArguments().getString("Class");
        name=getArguments().getString("name");
    }

    private void initView(View view) {
        UploadImage = view.findViewById(R.id.UploadImage);//上传图片
        Select_pictures = view.findViewById(R.id.Select_pictures);//选择图片
        UploadImage=view.findViewById(R.id.UploadImage);
        imageView_okhttp=view.findViewById(R.id.imageView_okhttp);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyClick_events();//点击事件监听
    }
    //点击事件监听
    private void MyClick_events() {
        MyClick_Events myClick_events = new MyClick_Events();
        UploadImage.setOnClickListener(myClick_events);
        Select_pictures.setOnClickListener(myClick_events);
    }

    //选择图片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if (resultCode == RESULT_OK) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = context.getContentResolver();
            try {
                //获取图片
                long startTime = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                long endTime = System.currentTimeMillis();
                if ((endTime - startTime) < 900) {
                    imageView_okhttp.setImageBitmap(bitmap);
                    TCP_images = bitmap;
                } else {
                    Toast.makeText(context, "❤仅支持9M以下图片❤", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else {
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //打开本地相册
    public void openAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    //点击监听类 继承 OnClickListener
    class MyClick_Events implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.Select_pictures://选择图片
                    openAlbum();
                    break;
                case R.id.UploadImage://上传
                    UploadImage_Method();
                    break;
            }
        }
    }

    //上传准备
    private void UploadImage_Method() {
        if (Check) {
            new Thread(() -> {
                handler.post(() -> {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("上传中");
                    progressDialog.show();
                });
                TCP_socket2();
                Check = false;
                TCP_socket(Bitmap2InputStream(TCP_images));// 将Bitmap转换成InputStream   and  连接服务器
            }).start();
        } else if (Check == false) {
            Toast.makeText(context, "❤请勿重新上传❤", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "❤未选择图片❤", Toast.LENGTH_SHORT).show();
        }
    }
    private void TCP_socket2() {
        Socket client = null;//发送连接
        try {
            client = new Socket("39.106.133.87", 5288);
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());//发送数据
            dos.writeUTF(id);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 将Bitmap转换成InputStream
    public InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    //上传开始
    private void TCP_socket(InputStream is) {
        try {
            Socket client = new Socket("39.106.133.87", 5204);//发送连接
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());//发送数据
            dos.writeUTF(name+","+Class);
            dos.flush();
            OutputStream os = new BufferedOutputStream(client.getOutputStream());
            byte[] flusk = new byte[1024*1024];
            int len = -1;
            while ((len = is.read(flusk)) != -1) {
                os.write(flusk, 0, len);
            }
            new Thread(() -> {
                handler.post(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "上传完成", Toast.LENGTH_SHORT).show();
                });
            }).start();
            SystemClock.sleep(500);
            //释放资源
            is.close();
            dos.close();
            client.close();
            System.out.println("关闭连接");
        } catch (IOException e) {
            e.printStackTrace();
            handler.post(() -> {
                progressDialog.dismiss();
                Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
            });
        }
    }




}
