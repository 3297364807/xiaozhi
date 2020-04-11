package com.baidu.idl.face.example.login.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.rnsb_start.R;


public class account extends Activity{
    private Handler handler = new Handler();
    private ImageView return_on, image;
    private TextView registered, forget,LoGon;
    private EditText Student, name;
    private Button btn_dl;
    private String CLASS;//接收上个上个页面传值
    private String name_id, xh, xm;
    private int id = 0;//mysql_id
    private boolean dltc, login = true, admin = true, app = true;

    @Override
    //开始
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        Intent intent = new Intent(account.this, Images.class);
        intent.putExtra("the", String.valueOf(id));
        intent.putExtra("student_id", xh);
        intent.putExtra("name", xm);
        intent.putExtra("class", CLASS);
        startActivity(intent);
    }
}
