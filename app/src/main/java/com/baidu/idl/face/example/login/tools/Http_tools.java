package com.baidu.idl.face.example.login.tools;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class Http_tools {
    public String post(String url,String data) throws IOException {//添加
        OkHttpClient client=new OkHttpClient();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"),data);
        Request request=new Request.Builder().url(url).post(body).build();
        String response=client.newCall(request).execute().body().string();
        return response;
    }
    public String get(String url) throws IOException {//查询
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        String response=client.newCall(request).execute().body().string();
        return response;
    }
    public String put(String url,String data) throws IOException {//更新
        OkHttpClient client=new OkHttpClient();
        RequestBody body=RequestBody.create(MediaType.parse("application/json"),data);
        Request request=new Request.Builder().url(url).put(body).build();
        String response=client.newCall(request).execute().body().string();
        return response;
    }
    public String Authority(String verify) throws IOException {//权限一
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        MediaType mediaType = MediaType.parse("application/json,text/plain");
        RequestBody body = RequestBody.create(mediaType, "{\"type\":\"org\",\"identity\":\"\",\"para\":{\"organization_id\":\""+verify+"\",\"organization_path_str\":\"1649,1650,1651,1652,1653,1654,1655\"},\"date\":\""+new SimpleDateFormat("YYYY-MM-dd").format(new Date()) +"\",\"activityid\":\"3195\",\"flag\":0,\"domain\":\"cqcfe\",\"stucode\":\"111111111111\"}");
        Request request = new Request.Builder()
                .url("https://lightapp.weishao.com.cn/api/reportstatistics/reportstatistics/getStatistical")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Referer", "https://lightapp.weishao.com.cn/reportstatistics/reportMember")
                .addHeader("Content-Length", "214")
                .addHeader("Host", "lightapp.weishao.com.cn")
                .addHeader("Content-Type", "text/plain")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
