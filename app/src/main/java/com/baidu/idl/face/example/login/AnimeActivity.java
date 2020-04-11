package com.baidu.idl.face.example.login;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.idl.face.example.login.Title;
import com.bm.library.PhotoView;
import com.example.rnsb_start.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

import tools.Base64Util;
import tools.FileUtil;
import tools.HttpUtil;
import tools.Token;

public class AnimeActivity extends Activity implements View.OnClickListener {
    private Handler handler=new Handler();
    private ImageView iv1;
    private ImageView iv2;
    private Button Select_images;
    private Button save_images;
    private Bitmap bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime);
        initView();
        initTitle();
        initData();
    }

    private void initData() {

    }

    public void openAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public  String selfie_anime(Bitmap bitmap) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-process/v1/selfie_anime";
        try {
            String imgStr = bitmapToBase64(bitmap);//图片转bitmap
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            Token token = new Token();
            String accessToken = token.getAuth2();
            String result = HttpUtil.post(url, accessToken, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //base64转bitmap
    public  Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    //bitmap转base64
    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void initTitle() {
        Title title = new Title(getActionBar(), getWindow().getDecorView());
        title.tv.setText("动漫制作");
        title.iv.setOnClickListener(view -> {
            finish();
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if (resultCode == RESULT_OK) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                long startTime = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                long endTime = System.currentTimeMillis();
//                send_id = (endTime - startTime);
//                System.out.println("程序运行时间>>>" + send_id + "ms");
                if ((endTime - startTime) < 900) {
                    iv1.setImageBitmap(bitmap);
                    this.bitmap = bitmap;
                    new Thread(()->{
                        try {
                            JSONObject jsonObject=new JSONObject(selfie_anime(bitmap));
                            System.out.println(">>>>>>>"+jsonObject.getString("image"));
                            this.bitmap=base64ToBitmap(jsonObject.getString("image"));
                            handler.post(()-> {
                                if(this.bitmap!=null){
                                    iv2.setImageBitmap(this.bitmap);
                                }

                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }).start();
                } else {
                    Toast.makeText(this, "❤仅支持9M以下图片❤", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else {
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void initView() {
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        Select_images = (Button) findViewById(R.id.Select_images);
        save_images = (Button) findViewById(R.id.save_images);
        Select_images.setOnClickListener(this);
        save_images.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Select_images:
                openAlbum();
                break;
            case R.id.save_images:
                break;
        }
    }
}
