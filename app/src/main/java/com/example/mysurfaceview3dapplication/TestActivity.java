package com.example.mysurfaceview3dapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TestActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //这个是坑哦 有了这个flag 会立马走onActivityResult 而不等待页面关闭才走onActivityResult
                startActivityForResult(intent,101);
            }
        });
        findViewById(R.id.tv_click2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TestActivity.this,MainActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //这个是坑哦 有了这个flag 会立马走onActivityResult 而不等待页面关闭才走onActivityResult
                startActivityForResult(intent,101);
            }
        });
    }

    String TAG="TestActivity";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG,"onActivityResult requestCode="+requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }
}