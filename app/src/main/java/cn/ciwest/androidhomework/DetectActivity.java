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
import cn.ciwest.androidhomework.entity.FaceDetectRequest;
import cn.ciwest.androidhomework.entity.FaceDetectResult;
import cn.ciwest.androidhomework.restfulclient.Client;


public class DetectActivity extends BasicActivity implements OnClickListener {

    private ImageView myPhoto;
    private Button selectBT;
    private Button compareBT;
    private Bitmap resbitmap;
    private TextView valueTV;
    private MyHandler myHandler;
    private String value;
    private String s;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            FaceDetectResult parseObject = JSON.parseObject(value, FaceDetectResult.class);
            System.out.println(parseObject.toString());
            valueTV.setText(parseObject.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        securing();
        init();
    }

    private void init() {
        myHandler = new MyHandler();
        myPhoto = findViewById(R.id.my_photo);
        selectBT = findViewById(R.id.button_select);
        compareBT = findViewById(R.id.button_compare);
        valueTV = findViewById(R.id.textView_value);
        compareBT.setOnClickListener(this);
        selectBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.button_compare:
                valueTV.setText("");
                if (s == null) {
                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        public void run() {
                            Client client = new Client();
                            try {
                                value = client.PostMethod(Const.FaceDeteiveUrl, s);
                                myHandler.sendEmptyMessage(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                }
                break;
            case R.id.button_select:
                show();
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
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                resbitmap = BitmapUtil.getSmallBitmap(picturePath);
            } catch (Exception e) {
            }
        }
        if (resbitmap != null) {
            myPhoto.setImageBitmap(resbitmap);
            String faceImageBase64Str = BitmapUtil.bitmaptoString(resbitmap);
            FaceDetectRequest faceDetectRequest = new FaceDetectRequest();
            faceDetectRequest.setFaceImage(faceImageBase64Str);
            s = JSON.toJSONString(faceDetectRequest);
        }
    }
}
