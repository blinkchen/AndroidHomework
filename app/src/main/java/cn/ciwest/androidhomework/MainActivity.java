package cn.ciwest.androidhomework;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_detect;
    private Button button_compare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        securing();
        init();
    }

    public void securing(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.CAMERA }, 1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }
    }

    public void init(){
        button_detect = findViewById(R.id.button_detect);
        button_compare = findViewById(R.id.button_compare);
        button_detect.setOnClickListener(this);
        button_compare.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_detect:
                startActivity(new Intent(this, DetectActivity.class));
                break;
            case R.id.button_compare:
                startActivity(new Intent(this, CompareActivity.class));
                break;
            default:
                break;
        }
    }
}