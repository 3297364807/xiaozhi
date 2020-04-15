package com.baidu.idl.face.example.login.litepal;

import org.litepal.crud.LitePalSupport;

public class First_time extends LitePalSupport {
   private int check;

    public void setCheck(int check) {
        this.check = check;
    }

    public int getCheck() {
        return check;
    }
}
