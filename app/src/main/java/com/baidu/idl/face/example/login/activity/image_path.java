package com.baidu.idl.face.example.login.activity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rnsb_start.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class image_path extends Activity {
    private TextView File_DCIM, File_DCIM2;
    int s = 1;
    private Handler handler = new Handler();
    private String[] datas = new String[100];//计算个数 并存放姓名
    private String Class_class = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_path);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECEIVER_VISIBLE_TO_INSTANT_APPS);
            finish();
        } else {
            File_DCIM = findViewById(R.id.File_DCIM);
            File_DCIM2 = findViewById(R.id.File_DCIM2);
            Intent intent = getIntent();
            Class_class = intent.getStringExtra("Class");//获取班级
            new Thread(() -> {
                getMysql();
                for (int i = 1; i < s; i++) {
                    String path = "http://39.106.133.87/" + Class_class + "/" + datas[i] + ".png";
                    getBitmap(path, i, datas[i]);
                }
                handler.post(()->{
                    Toast.makeText(this, "保存完成了", Toast.LENGTH_SHORT).show();
                });
            }).start();
        }
    }
    private void getMysql() {//存入姓名
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");// 动态加载类
            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection("jdbc:mysql://39.106.133.87:3306/meigui", "root", "admin");
//            String sql = "select name,student,recording from"+"\t"+Class_2+"\t"+"where id>?";
            String sql = "select name,student,recording from" + "\t" + Class_class + "\t" + "where id>?";
            System.out.println(sql);
            PreparedStatement pt = connection.prepareStatement(sql);
            pt.setObject(1, 0);//把id大于2我的记录提取出来//从第2个  id=0 开始查询
            ResultSet rsResultSet = pt.executeQuery();
            while (rsResultSet.next() == true) {
                datas[s] = rsResultSet.getString(1);
                s++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBitmap(String imgUrl, int Count, String datas) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        URL url = null;
        try {
            url = new URL(imgUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(2000);
            httpURLConnection.connect();
            //网络连接成功
            inputStream = httpURLConnection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024 * 8];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            byte[] bu = outputStream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bu, 0, bu.length);
            saveImageToGallery(bitmap, Count, datas);//保存到本地
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveImageToGallery(Bitmap bmp, int Count, String datas) {
        System.out.println(Count);
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        String fileName = datas + ".png";
        File file = new File(galleryPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            if (isSuccess) {
                String str = file.getPath();
                String[] data = str.split("/");
                handler.post(()->{
                    Toast.makeText(this, "开始下载....", Toast.LENGTH_SHORT).show();
                    File_DCIM.setText("图片保存在手机目录下:" + data[4] + "/" + data[5]);
                    File_DCIM2.setText("已完成:" + Count + "/" + (s - 1));
                    System.out.println(">>>>>>");
                });


            } else {
                Log.e("TAG", "图片保存失败");
            }
            fos.close();
        } catch (IOException e) {
            Log.e("TAG", "保存图片找不到文件夹");
            e.printStackTrace();
        }
    }
}
