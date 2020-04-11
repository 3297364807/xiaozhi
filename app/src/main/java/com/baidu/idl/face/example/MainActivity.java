package com.baidu.idl.face.example;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.idl.face.example.login.tools.Book;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends Activity {
    private int writeflag = 0;//判断储存权限是否获取;
    private String Class, name,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            List<Book> books= LitePal.findAll(Book.class);
            if(books.size()==0){
                LitePal.getDatabase();
                System.out.println("空的");
                Intent intent=new Intent(this,FaceDetectExpActivity.class);
                startActivity(intent);
            }else {
                Class=books.get(0).getCLASS();
                name=books.get(0).getName();
                id=books.get(0).getStudent();
                System.out.println("不为空");
                Intent intent=new Intent(this, Main_pageActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("Class",Class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        finish();
    }
}
