package com.baidu.idl.face.example.login.tools;

public class login_tools {
    public String getName() {
        return name;
    }

    public String getRecoding() {
        return recoding;
    }

    public String getImage() {
        return image;
    }

    private String name;
    private String recoding;

    public login_tools(String name, String recoding, String image) {
        this.name = name;
        this.recoding = recoding;
        this.image = image;
    }

    private String image;
}
