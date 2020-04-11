package com.baidu.idl.face.example.login.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import com.example.rnsb_start.R;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class admin_login2 extends Activity {
    private Handler handler=new Handler();
    private StringBuffer stringBuffer=new StringBuffer();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login2);
        Intent intent=getIntent();
       String Class_2=intent.getStringExtra("class");
        for(int i=0;i<Class_2.length()-5;i++){
            stringBuffer.append(Class_2.charAt(i));
        }
    }
    public void Empty2() {
        new Thread(()-> {
            // TODO Auto-generated method stub
            try {
                handler.post(()->{
                    progressDialog=new ProgressDialog(admin_login2.this);
                    progressDialog.setMessage("发起中>>>>请勿关闭");
                    progressDialog.show();
                });
                delectimage();//发起删除照片请求
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("创建驱动");
                Connection connection = DriverManager.getConnection("jdbc:mysql://39.106.133.87/meigui?" + "user=root&password=admin");
                System.out.println("mysql连接成功");
                for (int i = 1; i <=60; i++) {
                    String sql = "Update"+"\t"+stringBuffer+"\t"+"set recording='未完成'  where id=" + i;
                    System.out.println(sql);
                    Statement coon = (Statement) connection.createStatement();
                    coon.executeUpdate(sql);
                }
                handler.post(()->{
                    progressDialog.dismiss();
                    Toast.makeText(this, "发起成功", Toast.LENGTH_SHORT).show();
                });
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }
    private void delectimage(){//删除照片
        try {
            Socket client=new Socket("39.106.133.87",5200);
            DataOutputStream dos=new DataOutputStream(client.getOutputStream());
            dos.writeUTF(String.valueOf(stringBuffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showNormalDialog() {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.background_images2);
        normalDialog.setTitle("确定要发起截图");
        normalDialog.setMessage("此操作将会删除所有图片 所有记录 不可逆");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Empty2();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        handler.post(() -> {
            normalDialog.show();
        });
    }
    public void Empty(View view) {//修改数据
        showNormalDialog();
    }


    public void Preservation(View view) {
        String Class= String.valueOf(stringBuffer);
        Intent intent=new Intent(this,image_path.class);
        intent.putExtra("Class",Class);
        startActivity(intent);
    }
}
