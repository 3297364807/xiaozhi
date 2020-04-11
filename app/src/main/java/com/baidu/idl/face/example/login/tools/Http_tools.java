package com.baidu.idl.face.example.login.tools;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
    public String login() throws IOException {//登录
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        Request request = new Request.Builder()
                .url("https://service.weishao.com.cn/whistlenew/index.php?m=user&a=userLogin&password=123456&student_number=19922912620&verfiy_image_code=&app_version=6.6.1.72552&school=cqcfe&optfun=%7B%22phone_type%22%3A%22MI+6%22%2C%22os_version%22%3A%229%22%2C%22equipment_type%22%3A%22phone%22%2C%22ip_addr%22%3A%22192.168.31.14%22%2C%22network_type%22%3A%22WIFI%22%7D&package_name=com.ruijie.whistle&device_type=android&language=zh&client_id=d10588808ce11088e15baedf43b5f167&platform=android&city_id=10")
                .build();
        String response = client.newCall(request).execute().body().string();
        return response;
    }
    public String Authority(String verify, String key) throws IOException {//权限一
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        Request request = new Request.Builder()
                .url("https://api.weishao.com.cn/oauth/authorize?client_id=pqZ3wGM07i8R9mR3&redirect_uri=https%3A%2F%2Flightapp.weishao.com.cn%2Fcheck%2Freportstatistics&response_type=code&scope=base_api&state=ruijie&code=cqcfe&ip=service.weishao.com.cn&key="+verify+"&domain=cqcfe&verify="+verify+"&lang=zh&package_name=com.ruijie.whistle")
                .addHeader("Cookie", "whistlekey="+key+"; domain=.weishao.com.cn; path=/")
                .addHeader("Host", "api.weishao.com.cn")
                .build();
        Response response = client.newCall(request).execute();
        Headers headers=response.headers();
        List<String> cookies=headers.values("Set-Cookie");
        String session=cookies.get(0);
        String sessionId =session.substring(0,session.indexOf(";"));
        return sessionId;
    }
    public void Authority2() throws IOException {//2
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://service.weishao.com.cn/whistlenew/index.php?m=app&a=setAppPV&app_ip=null&app_version=6.6.1.72552&optfun=%7B%22phone_type%22%3A%22MI+6%22%2C%22student_number%22%3A%22032018030186%22%2C%22org_id%22%3A%221005%22%2C%22os_version%22%3A%229%22%2C%22name%22%3A%22%E6%B8%B8%E5%9F%B9%22%2C%22equipment_type%22%3A%22phone%22%2C%22ip_addr%22%3A%22192.168.31.14%22%2C%22network_type%22%3A%22WIFI%22%2C%22org_name%22%3A%22%E7%AE%A1%E7%90%86%E7%BB%84%E7%BB%87%22%7D&device_type=android&language=zh&type=1&platform=android&school=cqcfe&identity=teacher&package_name=com.ruijie.whistle&verify=34032%3Acqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&aid=34032&app_id=3268e6cda1c31441&city_id=10")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
    }

    public String Authority3(String key,String cookie) throws IOException {//3
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        Request request = new Request.Builder()
                .url("https://api.weishao.com.cn/login?client_id=pqZ3wGM07i8R9mR3&redirect_uri=https%3A%2F%2Flightapp.weishao.com.cn%2Fcheck%2Freportstatistics&response_type=code&scope=base_api&state=ruijie&code=cqcfe&ip=service.weishao.com.cn&key=34032:cqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&domain=cqcfe&verify=34032:cqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&lang=zh&package_name=com.ruijie.whistle")
                .method("GET", null)
                .addHeader("Cookie", "Cookie: whistle-oauth2="+cookie+"; whistlekey="+key+"; domain=.weishao.com.cn; path=/")
                .build();
        Response response = client.newCall(request).execute();
        Headers headers=response.headers();
        List<String> cookies=headers.values("Set-Cookie");
        String session=cookies.get(0);
        String sessionId =session.substring(0,session.indexOf(";"));
        return sessionId;
    }

    public String Authority4(String key,String cookie) throws IOException {//4
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://api.weishao.com.cn/login?client_id=pqZ3wGM07i8R9mR3&redirect_uri=https%3A%2F%2Flightapp.weishao.com.cn%2Fcheck%2Freportstatistics&response_type=code&scope=base_api&state=ruijie&code=cqcfe&ip=service.weishao.com.cn&key=34032:cqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&domain=cqcfe&verify=34032:cqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&lang=zh&package_name=com.ruijie.whistle")
                .method("POST", body)
                .addHeader("Cookie", "whistle-oauth2="+cookie+"; whistlekey="+key+"; domain=.weishao.com.cn; path=/")
                .build();
        Response response = client.newCall(request).execute();
        Headers headers=response.headers();
        List<String> cookies=headers.values("Set-Cookie");
        String session=cookies.get(0);
        String sessionId =session.substring(0,session.indexOf(";"));
        return sessionId;
    }
    public String Authority5(String key,String cookie) throws IOException {//4
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        Request request = new Request.Builder()
                .url("https://api.weishao.com.cn/oauth/authorize?client_id=pqZ3wGM07i8R9mR3&redirect_uri=https%3A%2F%2Flightapp.weishao.com.cn%2Fcheck%2Freportstatistics&response_type=code&scope=base_api&state=ruijie&code=cqcfe&ip=service.weishao.com.cn&key=34032:cqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&domain=cqcfe&verify=34032:cqcfe_android_5e914400de268_1d18856b2f2069be129bc8379836a7a7&lang=zh&package_name=com.ruijie.whistle")
                .method("GET", null)
                .addHeader("Cookie", "whistlekey="+key+"; domain=.weishao.com.cn; path=/; whistle-oauth2="+cookie+"; whistlekey="+key+"; domain=.weishao.com.cn; path=/")
                .build();
        Response response = client.newCall(request).execute();
        Headers headers=response.headers();
        List<String> cookies=headers.values("Set-Cookie");
        String session=cookies.get(0);
        String sessionId =session.substring(0,session.indexOf(";"));
        return sessionId;
    }
    public String Authority6(String key,String cookie) throws IOException {//4
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        Request request = new Request.Builder()
                .url("https://lightapp.weishao.com.cn/check/reportstatistics?code=cqcfe&state=ruijie")
                .method("GET", null)
                .addHeader("Cookie", "whistlekey="+key+"; domain=.weishao.com.cn; path=/")
                .addHeader("Cookie", "whistle-spaserver="+cookie+"")
                .build();
        Response response = client.newCall(request).execute();
        Headers headers=response.headers();
        List<String> cookies=headers.values("Set-Cookie");
        String session=cookies.get(0);
        String sessionId =session.substring(0,session.indexOf(";"));
        return sessionId;
    }

    public String Punch_in(String date) throws IOException {//微哨打卡查询
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"type\": \"org\",\n    \"identity\": \"\",\n    \"para\": {\n        \"organization_id\": \"1649,1650,1651,1652,1653,1654,1655\",\n        \"organization_path_str\": \"1649,1650,1651,1652,1653,1654,1655\"\n    },\n    \"date\": \""+date+"\",\n    \"activityid\": \"3195\",\n    \"flag\": 1,\n    \"domain\": \"cqcfe\",\n    \"stucode\": \"032018030186\"\n}");
        Request request = new Request.Builder()
                .url("https://lightapp.weishao.com.cn/api/reportstatistics/reportstatistics/getStatistical")
                .method("POST", body)
                .addHeader("Cookie", "whistle-spaserver="+"s%3AImF96_uHU2DQNcsoAZljMbTfq2P52BtF.A4bgpnFq7CDFRCpUxG%2B%2F1%2BvclnyOUr6TtHNF%2B%2B5C3mc"+"; whistlekey="+"D979239CD6BB331F1963AD670169DF9BE0C2B52C8AE7E87B57CE1CD89D5503E2CD2F3189A6FD38430D6B3961BB5F02D2"+"; domain=.weishao.com.cn; path=/; Hm_lvt_2897656ea377e58fb1af08554ed019b4=1586512951,1586534053,1586575120,1586578448; Hm_lpvt_2897656ea377e58fb1af08554ed019b4=1586578448")
                .addHeader("Host", "lightapp.weishao.com.cn")
                .addHeader("Content-Type", "application/json")
                .build();
        String response = client.newCall(request).execute().body().string();
        return response;
    }
    public String Punch_in2(String key,String cookie,String date) throws IOException {//微哨打卡查询
        OkHttpClient client=new CA_Certificate().getTrustAllClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"type\": \"org\",\n    \"identity\": \"\",\n    \"para\": {\n        \"organization_id\": \"1649,1650,1651,1652,1653,1654,1655\",\n        \"organization_path_str\": \"1649,1650,1651,1652,1653,1654,1655\"\n    },\n    \"date\": \""+date+"\",\n    \"activityid\": \"3195\",\n    \"flag\": 1,\n    \"domain\": \"cqcfe\",\n    \"stucode\": \"032018030186\"\n}");
        Request request = new Request.Builder()
                .url("https://lightapp.weishao.com.cn/api/reportstatistics/reportstatistics/getStatistical")
                .method("POST", body)
                .addHeader("Cookie", "whistle-spaserver="+cookie+"; whistlekey="+key+"; domain=.weishao.com.cn; path=/; Hm_lvt_2897656ea377e58fb1af08554ed019b4=1586512951,1586534053,1586575120,1586578448; Hm_lpvt_2897656ea377e58fb1af08554ed019b4=1586578448")
                .addHeader("Host", "lightapp.weishao.com.cn")
                .addHeader("Content-Type", "application/json")
                .build();
        String response = client.newCall(request).execute().body().string();
        return response;
    }




}
