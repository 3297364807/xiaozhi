package com.baidu.idl.face.example.login.Fragement;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.rnsb_start.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import tools.HttpUtil;
import tools.Token;

import static android.app.Activity.RESULT_OK;

public class AnimeFragment extends Fragment implements View.OnClickListener {
    private Handler handler=new Handler();
    private ImageView iv1;
    private ImageView iv2;
    private Button Select_images;
    private Button save_images;
    private Bitmap bitmap=null;
    private Context context;
    public String data;

    public static final AnimeFragment newInstance(String url)
    {
        AnimeFragment fragment = new AnimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data=getArguments().getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=container.getContext();
        View view=inflater.inflate(R.layout.activity_anime,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void openAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public  String selfie_anime(Bitmap bitmap) {
        // 请求url
        String url = data;
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



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if (resultCode == RESULT_OK) {
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr =context.getContentResolver();
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
                    Toast.makeText(context, "❤仅支持9M以下图片❤", Toast.LENGTH_SHORT).show();
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
    private void initView(View view) {
        iv1 = view.findViewById(R.id.iv1);
        iv2 = view.findViewById(R.id.iv2);
        Select_images = (Button) view.findViewById(R.id.Select_images);
        save_images = (Button) view.findViewById(R.id.save_images);
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
                if(bitmap!=null){
                    String data=new SimpleDateFormat("YYYY.MM.dd HH:mm:ss").format(new Date());
                    saveImageToGallery(bitmap,data);
                }else {
                    Toast.makeText(context, "请选择图片", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void saveImageToGallery(Bitmap bmp, String datas) {
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        String fileName = datas + ".png";
        File file = new File(galleryPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            if (isSuccess) {
                String str = file.getPath();
                handler.post(()->{
                    Toast.makeText(context, "保存完成", Toast.LENGTH_SHORT).show();
                    System.out.println("保存地址："+str);
                });
            } else {
                Toast.makeText(context, "请先制作完图片", Toast.LENGTH_SHORT).show();
            }
            fos.close();
        } catch (IOException e) {
            Log.e("TAG", "保存图片找不到文件夹");
            e.printStackTrace();
        }
    }
}
