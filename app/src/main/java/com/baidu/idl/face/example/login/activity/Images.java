package com.baidu.idl.face.example.login.activity;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.example.FaceDetectExpActivity;
import com.baidu.idl.face.example.login.Title;
import com.baidu.idl.face.example.login.litepal.Book;
import com.bumptech.glide.Glide;
import com.example.rnsb_start.R;

import org.litepal.LitePal;


public class Images extends Activity {
    private Button UploadImage;
    private ImageView imageView, image2;
    private Bitmap TCP_images;
    private Button Select_pictures,kebiao_Selcect;
    private TextView UploadQuery, ClassQuery;
    private String id;
    private long send_id = 0;
    private boolean admin = true;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private String Class, name;
    private TextView notice;
    private boolean[] btn_n = {true};
    private Context context;
    private TextView images_tczh,new_function;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images);
        initTitle();
        Btn_Id();//绑定id
        receive();//获取上个页面的数据
        int check=Integer.valueOf(getIntent().getStringExtra("check"));
        if(check==0){
            LitePal.deleteAll(Book.class);
            new Thread(()->{
                try {
                    initSectet(getIntent().getStringExtra("id"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            List<Book> books=LitePal.findAll(Book.class);
            Class=books.get(0).getCLASS();
            name=books.get(0).getName();
            id=books.get(0).getStudent();
        }

        obtain();//获取头像图片
        MyClick_events();//点击事件监听
    }

    private void initTitle() {
        Title title=new Title(getActionBar(),getWindow().getDecorView());
        title.tv.setText("用户中心");
        title.iv.setOnClickListener(view -> {
            finish();
        });
        context=this;
    }

    private void initSectet(String id) throws IOException {
        Socket client=new Socket("39.106.133.87",6666);//发送连接
        DataOutputStream dos=new DataOutputStream(client.getOutputStream());//发送数据
        dos.writeUTF(id);
        dos.flush();
        String data=null;
        DataInputStream  dis=new DataInputStream(client.getInputStream());//接收服务器的数据
        data=dis.readUTF();
        System.out.println(data);
        String[] data2=data.split(",");
        for(int i=0;i<data2.length;i++){
            System.out.println(data2[i]);
        }
        Class=data2[3];
        name=data2[0];
        Book book=new Book();
        book.setName(data2[0]);
        book.setStudent(data2[1]);
        book.setCLASS(data2[3]);
        book.save();
        dos.close();
        dis.close();
        client.close();
    }

    //获取头像图片
    private void obtain() {
        String path = "http://39.106.133.87/images/" + id + ".jpg";
        image2 = findViewById(R.id.image_sc);
        ViewGroup.LayoutParams param;
        param = image2.getLayoutParams();
        param.height = 350;
        param.width = 350;
        Glide.with(this).load(path).into(image2);
    }

    //接收上个页面的数据
    private void receive() {
        Intent getIntent = getIntent();//接收上个数据的传值
        id = getIntent.getStringExtra("id");
    }

    //绑定id
    private void Btn_Id() {

        ClassQuery = findViewById(R.id.ClassQuery);//班级查询

        imageView = findViewById(R.id.imageView_okhttp);//图片_未监听
        kebiao_Selcect=findViewById(R.id.kebiao_Selcect);
        images_tczh=findViewById(R.id.images_tczh);
        new_function=findViewById(R.id.new_function);
    }

    //点击事件监听
    private void MyClick_events() {
        MyClick_Events myClick_events = new MyClick_Events();
        UploadImage.setOnClickListener(myClick_events);
        ClassQuery.setOnClickListener(myClick_events);
        Select_pictures.setOnClickListener(myClick_events);
        kebiao_Selcect.setOnClickListener(myClick_events);
        images_tczh.setOnClickListener(myClick_events);
        new_function.setOnClickListener(myClick_events);
    }

    //选择图片
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if (resultCode == RESULT_OK) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                long startTime = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                long endTime = System.currentTimeMillis();
                send_id = (endTime - startTime);
                System.out.println("程序运行时间>>>" + send_id + "ms");
                if ((endTime - startTime) < 900) {
                    imageView.setImageBitmap(bitmap);
                    TCP_images = bitmap;
                } else {
                    Toast.makeText(this, "❤仅支持9M以下图片❤", Toast.LENGTH_SHORT).show();
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
                    Select_Picture_Method();
                    break;
                case R.id.ClassQuery://班级查询
                    ClassQuery_Method();
                    break;
                case R.id.UploadImage://上传
                    UploadImage_Method();
                    break;
                case R.id.kebiao_Selcect:
//                    Intent intent=new Intent(context,KebiaoActivity.class);
//                    if(Class!=null){
//                        intent.putExtra("Class",Class);
//                        startActivity(intent);
//                    }
                    break;
                case R.id.images_tczh:
                    finish();
                    Intent intent1=new Intent(context, FaceDetectExpActivity.class);
                    startActivity(intent1);
                    break;
                case  R.id.new_function:
//                    Intent intent2=new Intent(context,New_functionActivity.class);
//                    startActivity(intent2);
                    break;
            }
        }
    }

    //上传准备
    private void UploadImage_Method() {
        if (send_id > 0 && btn_n[0]) {
            new Thread(() -> {
                handler.post(() -> {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("上传中");
                    progressDialog.show();
                });
                TCP_socket2();
                btn_n[0] = false;
                TCP_socket(Bitmap2InputStream(TCP_images));// 将Bitmap转换成InputStream   and  连接服务器
            }).start();
        } else if (btn_n[0] == false) {
            Toast.makeText(Images.this, "❤请勿重新上传❤", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Images.this, "❤未选择图片❤", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "上传完成", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
            });
        }
    }

    //班级查询
    private void ClassQuery_Method() {
        if (admin) {
            if(Class!=null){
//                Intent intent = new Intent(this, login.class);
//                intent.putExtra("Class",Class);
//                intent.putExtra("name",name);
//                startActivity(intent);
            }
        } else {
            Toast.makeText(Images.this, "❤请勿重新点击❤", Toast.LENGTH_SHORT).show();
        }
    }

    //查询图片
    private void Select_Picture_Method() {
        btn_n[0] = true;
        openAlbum();
    }
}

