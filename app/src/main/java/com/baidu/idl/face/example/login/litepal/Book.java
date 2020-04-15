package com.baidu.idl.face.example.login.litepal;

import org.litepal.crud.LitePalSupport;

public class Book extends LitePalSupport {

    public void setName(String name) {
        Name = name;
    }

    public void setStudent(String student) {
        Student = student;
    }

    public void setCLASS(String CLASS) {
        this.CLASS = CLASS;
    }



    public String getName() {
        return Name;
    }

    public String getStudent() {
        return Student;
    }

    public String getCLASS() {
        return CLASS;
    }

    private String Name;
    private String Student;
    private String CLASS;
}
