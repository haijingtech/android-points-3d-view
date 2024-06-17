package com.example.mysurfaceview3dapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.haijingtech.myview.MyCheckBox;

import java.util.ArrayList;

/**
 * Author：梧州市海静科技有限公司 QQ 664119227 微信 664119227
 */
public class RgbDataView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    String TAG="RgbDataView";
    int viewWidth,viewHeight;
    private SurfaceHolder mSurfaceHolder;
    public Canvas mCanvas;
    Paint paint;

    public RgbDataView(Context context) {
        super(context);
        initView();
    }

    public RgbDataView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public RgbDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        paint=new Paint();
        mSurfaceHolder = getHolder();
        //注册回调方法
        mSurfaceHolder.addCallback(this);
        Log.i(TAG,"initView");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启子线程
        Log.i(TAG,"surfaceCreated");
        //setOnTouchListener(this);
        //new Thread(this).start();

        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    Log.i(TAG,"1111111111111");
                    byte[] b=new byte[700*700*3];
                    for(int i=0;i<700;i++){
                        for(int j=0;j<700*3;j++){
                            b[i*700+j]= (byte) (Math.random()*256);

                        }
                    }
                    Log.i(TAG,"222222222");
                    drawSomething(b,700,700);
                }


            }
        };

        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG,"surfaceChanged");
        viewWidth=width;
        viewHeight=height;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public static int convertByteToInt(byte data) {

        int heightBit = (int) ((data >> 4) & 0x0F);
        int lowBit = (int) (0x0F & data);
        return heightBit * 16 + lowBit;
    }


    // 将纯RGB数据数组转化成int像素数组
    public static int[] convertByteToColor(byte[] data) {
        int size = data.length;
        if (size == 0) {
            return null;
        }

        int arg = 0;
        if (size % 3 != 0) {
            arg = 1;
        }

        // 一般RGB字节数组的长度应该是3的倍数，
        // 不排除有特殊情况，多余的RGB数据用黑色0XFF000000填充
        int[] color = new int[size / 3 + arg];
        int red, green, blue;
        int colorLen = color.length;
        if (arg == 0) {
            for (int i = 0; i < colorLen; ++i) {
                red = convertByteToInt(data[i * 3]);
                green = convertByteToInt(data[i * 3 + 1]);
                blue = convertByteToInt(data[i * 3 + 2]);

                // 获取RGB分量值通过按位或生成int的像素值
                color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
            }
        } else {
            for (int i = 0; i < colorLen - 1; ++i) {
                red = convertByteToInt(data[i * 3]);
                green = convertByteToInt(data[i * 3 + 1]);
                blue = convertByteToInt(data[i * 3 + 2]);
                color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
            }

            color[colorLen - 1] = 0xFF000000;
        }

        return color;
    }

    static public Bitmap rgb2Bitmap(byte[] data, int width, int height) {
        int[] colors = convertByteToColor(data);    //取RGB值转换为int数组
        if (colors == null) {
            return null;
        }

        Bitmap bmp = Bitmap.createBitmap(colors, 0, width, width, height,
                Bitmap.Config.ARGB_8888);
        return bmp;
    }


    //绘图逻辑
    private void drawSomething(byte data[],int width,int height ) {
        try {
            //获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawBitmap(rgb2Bitmap(data, width, height),0,0,paint);
            Log.i(TAG,"drawBitmap:");
        }catch (Exception e){

        }finally {
            if (mCanvas != null){
                //释放canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}