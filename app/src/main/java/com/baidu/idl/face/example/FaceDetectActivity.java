
package com.baidu.idl.face.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.face.stat.Ast;
import com.baidu.idl.face.example.login.activity.RegisteredActivity;
import com.baidu.idl.face.example.login.litepal.Book;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.IDetectStrategy;
import com.baidu.idl.face.platform.IDetectStrategyCallback;
import com.baidu.idl.face.platform.ui.FaceSDKResSettings;
import com.baidu.idl.face.platform.ui.utils.CameraUtils;
import com.baidu.idl.face.platform.ui.utils.VolumeUtils;
import com.baidu.idl.face.platform.ui.widget.FaceDetectRoundView;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.CameraPreviewUtils;
import com.example.rnsb_start.R;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import tools.GsonUtils;
import tools.HttpUtil;
import tools.Token;

/**
 * 人脸采集接口
 */
public class FaceDetectActivity extends Activity implements
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback,
        VolumeUtils.VolumeCallback,
        IDetectStrategyCallback {

    public static final String TAG = FaceDetectActivity.class.getSimpleName();
    public static final String DETECT_CONFIG = "FaceOptions";

    // View
    protected View mRootView;
    protected FrameLayout mFrameLayout;
    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected TextView mTipsTopView;
    protected TextView mTipsBottomView;
    protected FaceDetectRoundView mFaceDetectRoundView;
    protected LinearLayout mImageLayout;
    // 人脸信息
    protected FaceConfig mFaceConfig;
    protected IDetectStrategy mIDetectStrategy;
    // 显示Size
    private Rect mPreviewRect = new Rect();
    protected int mDisplayWidth = 0;
    protected int mDisplayHeight = 0;
    protected int mSurfaceWidth = 0;
    protected int mSurfaceHeight = 0;
    protected Drawable mTipsIcon;
    // 状态标识
    protected volatile boolean mIsEnableSound = true;
    protected HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    protected boolean mIsCreateSurface = false;
    protected volatile boolean mIsCompletion = false;
    // 相机
    protected Camera mCamera;
    protected Camera.Parameters mCameraParam;
    protected int mCameraId;
    protected int mPreviewWidth;
    protected int mPreviewHight;
    protected int mPreviewDegree;
    public Context context;
    private Handler handler=new Handler();
    private ProgressDialog dialog;
    private String Class, name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(com.baidu.idl.face.platform.ui.R.layout.activity_face_detect_v3100);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = this.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;
        context=this;
        FaceSDKResSettings.initializeResId();
        mFaceConfig = FaceSDKManager.getInstance().getFaceConfig();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        mIsEnableSound = vol > 0 ? mFaceConfig.isSound : false;

        mRootView = this.findViewById(com.baidu.idl.face.platform.ui.R.id.detect_root_layout);
        mFrameLayout = (FrameLayout) mRootView.findViewById(com.baidu.idl.face.platform.ui.R.id.detect_surface_layout);

        mSurfaceView = new SurfaceView(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        int w = mDisplayWidth;
        int h = mDisplayHeight;

        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                (int) (w * FaceDetectRoundView.SURFACE_RATIO), (int) (h * FaceDetectRoundView.SURFACE_RATIO),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        mSurfaceView.setLayoutParams(cameraFL);
        mFrameLayout.addView(mSurfaceView);
        mFaceDetectRoundView = (FaceDetectRoundView) mRootView.findViewById(com.baidu.idl.face.platform.ui.R.id.detect_face_round);
        mTipsTopView = (TextView) mRootView.findViewById(com.baidu.idl.face.platform.ui.R.id.detect_top_tips);
        mTipsBottomView = (TextView) mRootView.findViewById(com.baidu.idl.face.platform.ui.R.id.detect_bottom_tips);
        mImageLayout = (LinearLayout) mRootView.findViewById(com.baidu.idl.face.platform.ui.R.id.detect_result_image_layout);
        if (mBase64ImageMap != null) {
            mBase64ImageMap.clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (mTipsTopView != null) {
            mTipsTopView.setText(com.baidu.idl.face.platform.ui.R.string.detect_face_in);
        }
        startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreview();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIDetectStrategy != null) {
            mIDetectStrategy.reset();
        }
        stopPreview();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void volumeChanged() {
        try {
            AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                int cv = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                mIsEnableSound = cv > 0;
                if (mIDetectStrategy != null) {
                    mIDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Camera open() {
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
            index++;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    protected void startPreview() {
        if (mSurfaceView != null && mSurfaceView.getHolder() != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        if (mCamera == null) {
            try {
                mCamera = open();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCamera == null) {
            return;
        }
        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        int degree = displayOrientation(this);
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        mPreviewDegree = degree;
        if (mIDetectStrategy != null) {
            mIDetectStrategy.setPreviewDegree(degree);
        }

        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));
        mPreviewWidth = point.x;
        mPreviewHight = point.y;
        // Preview 768,432
        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);
        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCamera.setParameters(mCameraParam);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }
    }
    protected void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setErrorCallback(null);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mCamera);
                mCamera = null;
            }
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
        }
        if (mIDetectStrategy != null) {
            mIDetectStrategy = null;
        }
    }

    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsCreateSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width,
                               int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (holder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsCreateSurface = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (mIsCompletion) {
            return;
        }

        if (mIDetectStrategy == null && mFaceDetectRoundView != null && mFaceDetectRoundView.getRound() > 0) {
            mIDetectStrategy = FaceSDKManager.getInstance().getDetectStrategyModule();
            mIDetectStrategy.setPreviewDegree(mPreviewDegree);
            mIDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);

            Rect detectRect = FaceDetectRoundView.getPreviewDetectRect(mDisplayWidth, mPreviewHight, mPreviewWidth);
            mIDetectStrategy.setDetectStrategyConfig(mPreviewRect, detectRect, this);
        }
        if (mIDetectStrategy != null) {
            mIDetectStrategy.detectStrategy(data);
        }
    }

    @Override
    public void onError(int error, Camera camera) {


    }

    @Override
    public void onDetectCompletion(FaceStatusEnum status, String message,
                                   HashMap<String, String> base64ImageMap) {
        if (mIsCompletion) {
            return;
        }
        //在刷新视图上
        onRefreshView(status, message);

        if (status == FaceStatusEnum.OK) {
            mIsCompletion = true;
            saveImage(base64ImageMap);
        }
        Ast.getInstance().faceHit("detect");
    }

    private void onRefreshView(FaceStatusEnum status, String message) {
        switch (status) {
            case OK:
                onRefreshTipsView(false, message);
                mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(false);

                break;
            case Detect_PitchOutOfUpMaxRange:
            case Detect_PitchOutOfDownMaxRange:
            case Detect_PitchOutOfLeftMaxRange:
            case Detect_PitchOutOfRightMaxRange:
                onRefreshTipsView(true, message);
                mTipsBottomView.setText(message);
                mFaceDetectRoundView.processDrawState(true);

                break;
            default:
                onRefreshTipsView(false, message);
                mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(true);

        }
    }

    private void onRefreshTipsView(boolean isAlert, String message) {
        if (isAlert) {
            if (mTipsIcon == null) {
                mTipsIcon = getResources().getDrawable(com.baidu.idl.face.platform.ui.R.mipmap.ic_warning);
                mTipsIcon.setBounds(0, 0, (int) (mTipsIcon.getMinimumWidth() * 0.7f),
                        (int) (mTipsIcon.getMinimumHeight() * 0.7f));
                mTipsTopView.setCompoundDrawablePadding(15);
            }
            mTipsTopView.setBackgroundResource(com.baidu.idl.face.platform.ui.R.drawable.bg_tips);
            mTipsTopView.setText(com.baidu.idl.face.platform.ui.R.string.detect_standard);
            mTipsTopView.setCompoundDrawables(mTipsIcon, null, null, null);
        } else {
            mTipsTopView.setBackgroundResource(com.baidu.idl.face.platform.ui.R.drawable.bg_tips_no);
            mTipsTopView.setCompoundDrawables(null, null, null, null);
            if (!TextUtils.isEmpty(message)) {
                mTipsTopView.setText(message);
            }
        }
    }


    private void saveImage(HashMap<String, String> imageMap) {
        Set<Map.Entry<String, String>> sets = imageMap.entrySet();
        mImageLayout.removeAllViews();
        for (Map.Entry<String, String> entry : sets) {
            base64ToBitmap(entry.getValue());//base64转bitmap
        }
    }

    //这里开始
    private Bitmap base64ToBitmap(String base64Data) {
        dialog=new ProgressDialog(this);
        dialog.setMessage("登陆中");
        dialog.setTitle("请稍后");
        dialog.show();
        Token token = new Token();
        new Thread(() -> {
            String access_token = token.getAuth();//获取访问权限
            String data = facesearch(access_token, base64Data);
            try {
                JSONObject jsonObject = new JSONObject(data);
                String id = jsonObject.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getString("user_id");
                double score = jsonObject.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getDouble("score");
                int sz=(int)score;
                if(sz>80){
                    initSectet(id);
                    handler.post(()->{
                        dialog.dismiss();
                        Toast.makeText(this, "学号:"+id, Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(this, Main_pageActivity.class);
                        intent.putExtra("id",id);
                        intent.putExtra("Class",Class);
                        intent.putExtra("name",name);
                        finish();
                        startActivity(intent);
                    });
                }else {
                    handler.post(()->{
                        Toast.makeText(this, "未检测到你的信息", Toast.LENGTH_SHORT).show();
                    });
                    initshowDialog(access_token,base64Data);
//                    String qqUrl = "mqqwpa://im/chat?chat_type=wpa&version=1&uin=3297364807";//uin是发送过去的qq号码
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void initSectet(String id) throws IOException {
        Socket client=new Socket("39.106.133.87",6666);//发送连接
        DataOutputStream dos=new DataOutputStream(client.getOutputStream());//发送数据
        dos.writeUTF(id);
        dos.flush();
        String data=null;
        DataInputStream dis=new DataInputStream(client.getInputStream());//接收服务器的数据
        data=dis.readUTF();
        System.out.println(data);
        String[] data2=data.split(",");
        for(int i=0;i<data2.length;i++){
            System.out.println(data2[i]);
        }
        Class=data2[3];
        name=data2[0];
        Book book=new Book();
        book.setName(data2[0]);
        book.setStudent(data2[1]);
        book.setCLASS(data2[3]);
        book.save();
        dos.close();
        dis.close();
        client.close();
    }


    private void initshowDialog(String access_token, String base64Data) {
        System.out.println("令牌:="+access_token);
        String []type = {"Fadein", "Slideleft", "Slidetop", "SlideBottom", "Slideright", "Fall", "Newspager", "Fliph", "Flipv", "RotateBottom", "RotateLeft", "Slit", "Shake", "Sidefill"};
        int i= (int) (type.length*Math.random());
        Effectstype effect = null;
        switch (i){
            case 0:effect=Effectstype.Fadein;break;
            case 1:effect=Effectstype.Slideright;break;
            case 2:effect=Effectstype.Slideleft;break;
            case 3:effect=Effectstype.Slidetop;break;
            case 4:effect=Effectstype.SlideBottom;break;
            case 5:effect=Effectstype.Newspager;break;
            case 6:effect=Effectstype.Fall;break;
            case 7:effect=Effectstype.Sidefill;break;
            case 8:effect=Effectstype.Fliph;break;
            case 9:effect=Effectstype.Flipv;break;
            case 10:effect=Effectstype.RotateBottom;break;
            case 11:effect=Effectstype.RotateLeft;break;
            case 12:effect=Effectstype.Slit;break;
            case 13:effect=Effectstype.Shake;break;
        }
        Looper.prepare();
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                // 重点设置
                .withEffect(effect)        //设置对话框弹出样式
                //.setCustomView(R.layout.custom, MainActivity.this) // 设置自定义对话框的布局
                .withDuration(700)              //动画显现的时间（时间长就类似放慢动作）
                // 基本设置
                .withTitle("注册人脸")         //设置对话框标题
                .withDialogColor("#000000")
                .withMessageColor("#000000")
                .withDividerColor("#000000")
                .withTitleColor("#000000")          //设置标题字体颜色
                .withDividerColor("#11000000")      //设置分隔线的颜色
                .withMessage("一秒即可注册登录")//设置对话框显示内容
                .withMessageColor("#000000")       //设置消息字体的颜色
                .withDialogColor("#FFFFFF")        //设置对话框背景的颜色
                .withIcon(getResources().getDrawable(R.drawable.background_images2)) //设置标题的图标
                // 设置是否模态，默认false，表示模态，
                //要求必须采取行动才能继续进行剩下的操作 | isCancelable(true)
                .isCancelable(false)
                .isCancelableOnTouchOutside(true)
                .withButton1Text("确认")               //设置按钮1的文本
                .withButton2Text("取消")
                //设置按钮2的文本
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        new Thread(()->{
//                            add(access_token,base64Data);
//                        }).start();
                           handler.post(()->{
                               dialogBuilder.dismiss();
                           });
                            finish();
                            Intent intent=new Intent(context,RegisteredActivity.class);
                            intent.putExtra("access_token",access_token);
                            intent.putExtra("base64Data",base64Data);
                            startActivity(intent);
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            dialogBuilder.dismiss();
                    }
                });
                dialogBuilder.show();
                Looper.loop();
    }






    private static String facesearch(String access_token, String base64Data) {
        System.out.println("打印" + access_token);
        System.out.println("打印" + base64Data);
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64Data);
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "2018jsj_3");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = access_token;
            String result = HttpUtil.post(url, accessToken, "application/json", param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
