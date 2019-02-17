package com.example.hp.model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.hp.model.QRCodeProducer.getDeviceQRCode;

public class MainActivity extends AppCompatActivity {

    private static boolean ACTIVE_FLAG;
    private static final String ArcAppId = "QCAJicnx5Z1K7f7pPEBRRPYN6DMcYD5v9CDs3isAaSj";
    private static final String ArcSdkKey = "2SWeGxKMUqjGQodm9nD7oW3YfABrSqwbvRRD9Tyja9y6";
    private static final int CODE_TAKE_PHOTO = 1;

    private FaceEngine engine = new FaceEngine();

    private ImageView iv_left;
    private ImageView iv_right;
    private TextView tv_similar;
    private Button btn_cmp;

    private Uri imagineUri;
    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;
    private int ivNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeExam();
        iv_left = findViewById(R.id.iv_left);
        iv_right = findViewById(R.id.iv_right);
        tv_similar = findViewById(R.id.tv_similar);
        btn_cmp = findViewById(R.id.btn_cmp);
        iv_left.setOnClickListener(listenerForIV);
        iv_right.setOnClickListener(listenerForIV);
        btn_cmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compare(bitmapLeft, bitmapRight);
            }
        });
        iv_left.setImageBitmap(getDeviceQRCode(this));
    }

    private void compare(Bitmap bitmapLeft, Bitmap bitmapRight) {

//        FaceEngine engine = new FaceEngine();

        byte [] NV21FaceLeft = PictureHandler.bitmapToNv21(bitmapLeft,
                bitmapLeft.getWidth(),bitmapLeft.getHeight());

        byte [] NV21FaceRight = PictureHandler.bitmapToNv21(bitmapRight,
                bitmapRight.getWidth(),bitmapRight.getHeight());

        //用来存放提取到的人脸信息, face_1 是注册的人脸，face_2 是要识别的人脸
        List<FaceInfo> faceLeft = new ArrayList<>();
        List<FaceInfo> faceRight = new ArrayList<>();

        //初始化人脸识别引擎，使用时请替换申请的 APPID 和 SDKKEY
        int errorCode = engine.init(this,
                FaceEngine.ASF_DETECT_MODE_IMAGE,
                FaceEngine.ASF_OP_0_HIGHER_EXT,
                16, 5,
                FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_FACE_RECOGNITION);
        Log.e("com.arcsoft", "AFR_FSDK_InitialEngine = " + errorCode);

       errorCode = engine.detectFaces(NV21FaceLeft,bitmapLeft.getWidth(),
                bitmapLeft.getHeight(),FaceEngine.CP_PAF_NV21,faceLeft);

        Log.e("com.arcsoft", "detectLeftFaces = " + errorCode);

        errorCode = engine.detectFaces(NV21FaceRight,bitmapRight.getWidth(),
                bitmapRight.getHeight(),FaceEngine.CP_PAF_NV21,faceRight);

        Log.e("com.arcsoft", "detectRightFaces = " + errorCode);

        if(faceLeft.size()!=1||faceRight.size()!=1){
            Toast.makeText(this,"请上传单人照片",Toast.LENGTH_SHORT).show();
            return;
        }

        FaceFeature featureLeft = new FaceFeature();
        FaceFeature featureRight = new FaceFeature();

        FaceInfo face1 = faceLeft.get(0);
        FaceInfo face2 = faceRight.get(0);
        
        //输入的 data 数据为 NV21 格式（如 Camera 里 NV21 格式的 preview 数据）；人脸坐标一般使用人脸检测返回的 Rect 传入；人脸角度请按照人脸检测引擎返回的值传入。
        errorCode = engine.extractFaceFeature(NV21FaceLeft, bitmapLeft.getWidth(), bitmapLeft.getHeight(),
                FaceEngine.CP_PAF_NV21, face1, featureLeft);
        Log.e("com.arcsoft", "Face=" + featureLeft.getFeatureData()[0]+ ","
                + featureLeft.getFeatureData()[1] + "," + featureLeft.getFeatureData()[2] + "," + errorCode);

        errorCode = engine.extractFaceFeature(NV21FaceRight, bitmapRight.getWidth(), bitmapRight.getHeight(),
                FaceEngine.CP_PAF_NV21, face2, featureRight);
        Log.e("com.arcsoft", "Face=" + featureRight.getFeatureData()[0]+ ","
                + featureRight.getFeatureData()[1] + "," + featureRight.getFeatureData()[2] + "," + errorCode);

        //score 用于存放人脸对比的相似度值
        FaceSimilar score = new FaceSimilar();
        errorCode = engine.compareFaceFeature(featureLeft, featureRight, score);
        tv_similar.setText("相似度 : "+(int)(score.getScore()*100)+"%");
        Log.e("com.arcsoft", "AFR_FSDK_FacePairMatching=" + errorCode);
        Log.e("com.arcsoft", "Score:" + score.getScore());

        Toast.makeText(this, String.valueOf((int)(score.getScore()*100)),Toast.LENGTH_SHORT).show();

        //销毁人脸识别引擎
        errorCode = engine.unInit();
        Log.e("com.arcsoft", "AFR_FSDK_UninitialEngine : " + errorCode);
    }

    private View.OnClickListener listenerForIV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == iv_left)
                ivNum = 0;
            else if(v == iv_right){
                ivNum = 1;
            }
            takeAPhoto();
        }
    };

    private void activeExam(){
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        ACTIVE_FLAG = sp.getBoolean("isActive",false);
        if(!ACTIVE_FLAG){//未激活，进行激活操作
//            FaceEngine engine = new FaceEngine();
            if(PermissionChecker.checkSelfPermission(this,"android.permission.READ_PHONE_STATE")
                    ==PermissionChecker.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.READ_PHONE_STATE"}, 1);
            }
            int errorCode = engine.active(this,ArcAppId,ArcSdkKey);
            if(errorCode!=ErrorInfo.MOK) {//激活失败
                Log.e("com.arcsoft", "AFR_FSDK_FailToActive : " + errorCode);
                Toast.makeText(this, "激活失败", Toast.LENGTH_SHORT).show();
            }else{//更新激活状态
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isActive",true);
                editor.apply();
            }
        }
    }

    private void takeAPhoto(){
        File outputImg = new File(getExternalCacheDir(),"photoTook.jpg");
        if(outputImg.exists()){
            outputImg.delete();
        }
        try {
            outputImg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT>=24){
            imagineUri = FileProvider.getUriForFile(this,
                    "com.example.hp.model.fileProvider",outputImg);
        }else{
            imagineUri = Uri.fromFile(outputImg);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagineUri);
        startActivityForResult(intent, CODE_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bitmap = null;
        if(requestCode == CODE_TAKE_PHOTO){
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagineUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(ivNum == 0)
                bitmapLeft = bitmap;
            else if(ivNum == 1)
                bitmapRight = bitmap;
        }
        switch (requestCode){
            case CODE_TAKE_PHOTO:
                if (ivNum == 0) {
                    iv_left.setImageBitmap(bitmap);
                } else if(ivNum == 1){
                    iv_right.setImageBitmap(bitmap);
                }
                break;
                default:
                    break;
        }
    }
}
