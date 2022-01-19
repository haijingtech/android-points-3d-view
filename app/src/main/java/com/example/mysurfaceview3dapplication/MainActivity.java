package com.example.mysurfaceview3dapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    int step=0;
    int stepRollX=0;
    int mark=0;
    boolean ismode1=false;
    Points3DView msv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mark==1){
            setContentView(R.layout.activity_main3);
        }else{
            setContentView(R.layout.activity_main);
        }
        msv=findViewById(R.id.mysview);
        Button bt=findViewById(R.id.button);
        bt.setOnClickListener(this);
        Button bt0=findViewById(R.id.button0);
        bt0.setOnClickListener(this);
        Button bta=findViewById(R.id.button_a);
        bta.setOnClickListener(this);
        Button btb=findViewById(R.id.button_b);
        btb.setOnClickListener(this);
        Button bt1=findViewById(R.id.button1);
        bt1.setOnClickListener(this);
        Button bt2=findViewById(R.id.button2);
        bt2.setOnClickListener(this);
        Button bt3=findViewById(R.id.button3);
        bt3.setOnClickListener(this);

        msv.setDrawCtrl(new Points3DView.ViewCanDraw() {
            @Override
            public void candraw(Points3DView v) {
                if(mark==1){
                    msv.addPoints(PointsData1.points1,0xffff0000);
                    //msv.addPoints(PointsData2.points2,0xffff0000);
                    //msv.addPoints(PointsData3.points3,0xffff0000);
                    //msv.addPoints(PointsData4.points4,0xffff0000);
                    //msv.addPoints(PointsData5.points5,0xffff0000);
                    //msv.addPoints(PointsData6.points6,0xffff0000);
                    msv.addPoints(PointsData7.points7,0xffff0000);
                    //msv.setZmax(7000);
                    msv.addPoints(PointsData8.points8,0xffff0000);
                    msv.setXName("位移(um)");
                    msv.setYName("位移(um)");
                    msv.setZName("时间(ms)");
                    msv.show();

                }else {

                    if(ismode1){
                        msv.setMode(1, new Points3DView.XYZ_To_Value() {

                            @Override
                            public float xyzToValue(float x, float y, float z) {
                                //return Math.abs(x) + Math.abs(y) + Math.abs(z);
                                return z;
                            }
                        });
                        try {
                            msv.addM1Points(Jiasudushiyu2.points, 5, 0xfffff43b);
                            msv.addM1Points(Jiasudushiyu2.points, 4, 0xff98f43b);
                            msv.addM1Points(Jiasudushiyu2.points, 3, 0xff98f4ff);
                            msv.addM1Points(Jiasudushiyu2.points, 2, 0xff98fffb);
                            //msv.addPoints(Jiasudushiyu.points);
                            msv.addM1PointsSet(Jiasudushiyu3.points,1,0xff988afb);
                            //
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        msv.setXName("频率（Hz）");
                        msv.setYName("时间");
                        msv.setZName("幅值");
                        msv.setM1YStrings(new String[]{
                                "2021-12-02 19:46:28",
                                "2021-12-03 10:46:20",
                                "2021-12-04 19:40:28",
                                "2021-12-05 15:46:28",
                                "2021-12-06 19:46:08"});
                        //msv.setXmin(0);
                        //msv.setYmin(0);
                        //msv.setYmax(10);
                        //msv.compute(false);
                        msv.setInfoTextX("频率(Hz)");
                        msv.setInfoTextY("时间");
                        msv.setInfoTextZ("幅值");
                        msv.setInfoPaperTextColor(0xff999999);
                        msv.show();

                    }else{
                        msv.addPoints(PointsData1.points1,0xffff0000);
                        msv.addPoints(PointsData2.points2,0xffff0000);
                        msv.addPoints(PointsData3.points3,0xffff0000);
                        msv.addPoints(PointsData4.points4,0xffff0000);
                        msv.addPoints(PointsData5.points5,0xffff0000);
                        msv.addPoints(PointsData6.points6,0xffff0000);
                        msv.addPoints(PointsData7.points7,0xffff0000);
                        msv.setZmax(7000);
                        msv.setXName("位移(um)");
                        msv.setYName("位移(um)");
                        msv.setZName("时间(ms)");
                        msv.show();
                    }



                }
            }
        });

        Points3DView msv1=findViewById(R.id.mySurfaceView);
        msv1.setDrawCtrl(new Points3DView.ViewCanDraw() {
            @Override
            public void candraw(Points3DView v) {
                /*v.addPoints(PointsData1.points1,0xffff0000);
                v.addPoints(PointsData2.points2,0xffff0000);
                v.addPoints(PointsData3.points3,0xffff0000);
                v.addPoints(PointsData4.points4,0xffff0000);
                v.addPoints(PointsData5.points5,0xffff0000);
                v.addPoints(PointsData6.points6,0xffff0000);
                v.addPoints(PointsData7.points7,0xffff0000);*/
                v.addPoints(PointsData8.points8,0xffff0000);
                v.setXName("x位移(um)");
                v.setYName("y位移(um)");
                v.setZName("时间(ms)");
                v.setBgColor(0xff7835a3);
                //v.setInfoTextX("位移");
                //v.setInfoTextY("位移");
                //v.setInfoTextZ("位移");
                v.setInfoPaperTextColor(0xff00ff00);
                v.setPointsLinkByLine(true);
                v.show();

                //v.animationShow(3,true);
            }
        });

        final Points3DView msv2=findViewById(R.id.mySurfaceView1);
        //msv2.show(60,60,60,-60,-60,-60);

        msv2.setDrawCtrl(new Points3DView.ViewCanDraw() {
            @Override
            public void candraw(Points3DView v) {
                v.setBgColor(0xff983f45);
                msv2.addPoints(PointsData2.points2,0xffff0000);
                msv2.show(120,60,60,-120,-60,-60);
                msv2.show();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        boolean isEnd=false;
                        int i=0;
                        msv2.isAnimationRunning=true;
                        while(!isEnd){
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            msv2.addPoint(PointsData1.points1[i]);
                            msv2.compute(true);
                            i++;
                            if(PointsData1.points1.length==i){
                                isEnd=true;
                            }

                        }
                        msv2.isAnimationRunning=false;
                    }
                }.start();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){
            //msv.doStep(step);
            //msv.roll_Z_axis(1,true);
            step++;
            if(step==5){
                step=0;
            }
        }
        if(v.getId()==R.id.button_a){
            //msv.doStep(step);
            //msv.roll_Z_axis(0,true);
            step++;
            if(step==5){
                step=0;
            }
        }
        if(v.getId()==R.id.button_b){
            //msv.doStep(step);
            //msv.rollScreenX(0,true);
            stepRollX++;
            if(stepRollX==5){
                stepRollX=0;
            }
        }
        if(v.getId()==R.id.button0){
            //msv.rollScreenX(1,true);
            stepRollX++;
            if(stepRollX==5){
                stepRollX=0;
            }
        }

        if(v.getId()==R.id.button1){
            //msv.setShowZoomInfo(true);
            //msv.setZoom(msv.getZoom()+0.1f);

        }

        if(v.getId()==R.id.button2){
            //msv.setShowZoomInfo(true);
            //msv.setZoom(msv.getZoom()-0.1f);
        }
        if(v.getId()==R.id.button3){
            //msv.draggable=!msv.draggable;

            //msv.clear();
            //msv.XYZ_Name_TextFloat(1f,0.8f,1f,0.8f,1.2f,1.2f);
            //msv.addPoints(PointsData2.points2,0xffff0000);
            //msv.show();
        }
    }
}