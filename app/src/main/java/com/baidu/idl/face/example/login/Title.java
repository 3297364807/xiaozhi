package com.baidu.idl.face.example.login;

import android.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rnsb_start.R;

public class Title {
    public TextView tv;
    public ImageView iv;
    public Title(ActionBar actionBar, View decorView) {
        if(actionBar!=null){
            actionBar.hide();
        }
        tv=decorView.findViewById(R.id.title);
        iv=decorView.findViewById(R.id.back);
    }
}
