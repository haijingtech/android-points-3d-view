package com.example.mysurfaceview3dapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AgeHeightViewTestActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout tab1,tab2,tab3,tab4;
    TextView tv_1,tv_2,tv_3,tv_4;
    ImageView iv_tab3,iv_tab4;
    LinearLayout selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_height_view_text);
        tab1=findViewById(R.id.tab1);
        tab2=findViewById(R.id.tab2);
        tab3=findViewById(R.id.tab3);
        tab4=findViewById(R.id.tab4);
        tv_1=findViewById(R.id.tv_1);
        tv_2=findViewById(R.id.tv_2);
        tv_3=findViewById(R.id.tv_3);
        tv_4=findViewById(R.id.tv_4);
        iv_tab3=findViewById(R.id.iv_tab3);
        iv_tab4=findViewById(R.id.iv_tab4);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        selectedTab=tab1;
        AgeHeightView ahv=findViewById(R.id.ahv);

        ArrayList<People> peoples=new ArrayList<>();
        ArrayList<AgeHeight> ageHeights=new ArrayList<>();
        for(int i=12;i<48;i++){
            AgeHeight ageHeight=new AgeHeight(i,70+i-12,"2022.10.30");
            ageHeights.add(ageHeight);
        }
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.touxiang1);
        People people=new People(ageHeights,"2011.11.1",0xff77ab00,bitmap);
        peoples.add(people);

        ArrayList<AgeHeight> ageHeights1=new ArrayList<>();
        for(int i=120;i<168;i++){
            AgeHeight ageHeight=new AgeHeight(i,140+(i-120)*0.3F,"2022.12.30");
            ageHeights1.add(ageHeight);
        }
        Bitmap bitmap1= BitmapFactory.decodeResource(getResources(),R.drawable.touxiang);
        People people1=new People(ageHeights1,"2013.11.1",0xff7Ab9fa,bitmap1);
        peoples.add(people1);

        ahv.setPeoplesToView(peoples);
    }

    String TAG="AgeHeightViewTestActivity";
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                Log.i(TAG,"keyCode="+keyCode);
                if(selectedTab==tab1){
                    onFocus(tab4);
                }else if(selectedTab==tab2){
                    onFocus(tab1);
                }else if(selectedTab==tab3){
                    onFocus(tab2);
                }else if(selectedTab==tab4){
                    onFocus(tab3);
                }
            }else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                Log.i(TAG,"getCurrentFocus="+selectedTab.getId());
                if(selectedTab==tab1){
                    onFocus(tab2);
                }else if(selectedTab==tab2){
                    onFocus(tab3);
                }else if(selectedTab==tab3){
                    onFocus(tab4);
                }else if(selectedTab==tab4){
                    onFocus(tab1);
                }
            }else if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                if(selectedTab==tab1){
                    //onFocus(tab2);
                }else if(selectedTab==tab2){
                    //onFocus(tab3);
                }else if(selectedTab==tab3){
                    AgeHeightView ageHeightView=findViewById(R.id.ahView);
                    if(ageHeightView!=null){

                    }
                }else if(selectedTab==tab4){
                    AgeHeightView ageHeightView=findViewById(R.id.ahView);
                    if(ageHeightView!=null){

                    }
                }
            }else if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
                onFocus(selectedTab);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onFocus(View v){
        Log.i(TAG,"onFocus="+v.getId());
        tab1.setBackgroundResource(R.drawable.shape_retangle_3);
        tab2.setBackgroundResource(R.drawable.shape_retangle_3);
        tab3.setBackgroundResource(R.drawable.shape_retangle_3);
        tab4.setBackgroundResource(R.drawable.shape_retangle_3);
        tv_1.setTextColor(0xffffffff);
        tv_2.setTextColor(0xffffffff);
        tv_3.setTextColor(0xffffffff);
        tv_4.setTextColor(0xffffffff);
        tab1.setPadding(0,10,0,10);
        tab2.setPadding(0,10,0,10);
        tab3.setPadding(0,10,0,10);
        tab4.setPadding(0,10,0,10);
        ((LinearLayout.LayoutParams)tab1.getLayoutParams()).gravity= Gravity.BOTTOM;
        ((LinearLayout.LayoutParams)tab2.getLayoutParams()).gravity= Gravity.BOTTOM;
        ((LinearLayout.LayoutParams)tab3.getLayoutParams()).gravity= Gravity.BOTTOM;
        ((LinearLayout.LayoutParams)tab4.getLayoutParams()).gravity= Gravity.BOTTOM;

        v.setBackgroundResource(R.drawable.shape_retangle_1);

        v.setPadding(0,40,0,40);
        ((LinearLayout.LayoutParams)v.getLayoutParams()).gravity= Gravity.NO_GRAVITY;
        iv_tab3.setImageResource(R.drawable.girl);
        iv_tab4.setImageResource(R.drawable.boy);
        if(v.getId()==R.id.tab3){
            iv_tab3.setImageResource(R.drawable.girl1);
            tv_3.setTextColor(0xff333333);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, AgeHightFragment.newInstance("0",""));
            transaction.commit();
        }else if(v.getId()==R.id.tab4){
            tv_4.setTextColor(0xff333333);
            iv_tab4.setImageResource(R.drawable.boy1);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, AgeHightFragment.newInstance("1",""));
            transaction.commit();
        } else if(v.getId()==R.id.tab1){
            tv_1.setTextColor(0xff333333);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, BlankFragment.newInstance("0",""));
            transaction.commit();
        }else if(v.getId()==R.id.tab2){
            tv_2.setTextColor(0xff333333);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, BlankFragment.newInstance("0",""));
            transaction.commit();
        }
        tv_1.invalidate();
        Log.i(TAG,"v="+v.getId());
        try{
            selectedTab=(LinearLayout) v;
        }catch (Exception e){e.printStackTrace();}
    }



    @Override
    public void onClick(View v) {
        onFocus(v);
    }
}