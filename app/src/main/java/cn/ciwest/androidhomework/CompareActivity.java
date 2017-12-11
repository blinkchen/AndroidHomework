package cn.ciwest.androidhomework;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import cn.ciwest.androidhomework.comm.BitmapUtil;
import cn.ciwest.androidhomework.comm.Const;
import cn.ciwest.androidhomework.entity.FaceCompareRequest;
import cn.ciwest.androidhomework.entity.FaceCompareResult;
import cn.ciwest.androidhomework.restfulclient.Client;

public class CompareActivity extends BasicActivity implements OnClickListener {
    private ImageView photo1;
    private ImageView photo2;
    private TextView valueTV;
    private Button select1BT;
    private Button select2BT;
    private Button compareBT;
    private String value;
    private String s;
    private MyHandler myHandler;
    private Bitmap resbitmap;
    private int select;
    private FaceCompareRequest faceCompareRequest;
    private boolean lock1 = false;
    private boolean lock2 = false;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            FaceCompareResult parseObject = JSON.parseObject(value, FaceCompareResult.class);
            valueTV.setText(parseObject.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        securing();
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        myHandler = new MyHandler();
        faceCompareRequest = new FaceCompareRequest();
        photo1 = (ImageView) findViewById(R.id.photo_1);
        photo2 = (ImageView) findViewById(R.id.photo_2);
        valueTV = (TextView) findViewById(R.id.textView_value);
        select1BT = (Button) findViewById(R.id.button_select_1);
        select2BT = (Button) findViewById(R.id.button_select_2);
        compareBT = (Button) findViewById(R.id.button_compare);
        select1BT.setOnClickListener(this);
        select2BT.setOnClickListener(this);
        compareBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.button_select_1:
            case R.id.button_select_2:
                show();
                if (arg0.getId() == R.id.button_select_1) {
                    select = 0;
                    lock1 = true;
                } else if (arg0.getId() == R.id.button_select_2) {
                    select = 1;
                    lock2 = true;
                }
                break;
            case R.id.button_compare:
                valueTV.setText("");
                if(lock1 && lock2){
                    new Thread() {
                        public void run() {
                            // 实例化RESTFul客户端
                            Client client = new Client();
                            // 请求数据
                            try {
                                value = client.PostMethod(Const.FaceCompareUrl, s);
                                myHandler.sendEmptyMessage(1);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    Toast.makeText(this,"请选择图片",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0) {
            if (resultCode == -1) {
                resbitmap = BitmapUtil.saveBitmap(photo_path, photoFile);
            }
        } else if (requestCode == 1) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                resbitmap = BitmapUtil.getSmallBitmap(picturePath);
            } catch (Exception e) {
            }
        }
        if (resbitmap != null) {
            // 图片Base64编码
            String faceImageBase64Str = BitmapUtil.bitmaptoString(resbitmap);
            // 创建人脸检测对象
            if (select == 0) {
                photo1.setImageBitmap(resbitmap);
                faceCompareRequest.setFaceimage1(faceImageBase64Str);
            } else {
                photo2.setImageBitmap(resbitmap);
                faceCompareRequest.setFaceimage2(faceImageBase64Str);
            }
            // 序列化人脸检测对象
            s = JSON.toJSONString(faceCompareRequest);
        }
    }
}
