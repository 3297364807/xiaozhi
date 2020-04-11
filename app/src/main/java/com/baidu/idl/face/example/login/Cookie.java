package com.baidu.idl.face.example.login.tools;

import org.litepal.crud.LitePalSupport;

public class Cookie extends LitePalSupport {


    public void setCookie(String cookie) {
        this.cookie = cookie;
    }


    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }


    public String getCookie() {
        return cookie;
    }

    private String cookie;
}
