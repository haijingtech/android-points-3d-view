package com.example.mysurfaceview3dapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity2 extends Activity {
    WebView wv;
    Button bt;
    EditText editText;
    boolean doit=false;

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        wv=findViewById(R.id.wv);
        bt=findViewById(R.id.ok);
        editText=findViewById(R.id.ed_url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new MyWebViewClient());
        wv.loadUrl("file:///android_asset/test.html");
        try {
            String[] s=getAssets().list("");
            for(String str:s){
                Log.i("aaaa","str="+str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        wv.addJavascriptInterface(this,"android");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(MainActivity2.this, "请输入URL", Toast.LENGTH_SHORT).show();
                }else{
                    wv.loadUrl(editText.getText().toString());
                }
            }
        });
    }

    @JavascriptInterface
    public void showDialog(String str){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //避免跳转到其它浏览器
    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
            if(url.startsWith("intent://") && doit){
                try {
                    Intent intent=Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
                    if(intent!=null){
                        startActivity(intent);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return true;
            }else{
                view.loadUrl(url);

                return super.shouldOverrideUrlLoading(view, url);
            }


        }
    }
}