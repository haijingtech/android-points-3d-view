package com.example.mysurfaceview3dapplication;

import android.content.Context;
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
public class Points3DView1209 extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    String TAG="GameView";
    int viewWidth,viewHeight;
    private SurfaceHolder mSurfaceHolder;
    //绘图的Canvas
    public Canvas mCanvas;
    private int bgcolor;
    private int pointsColorRandom;
    final private ArrayList<float[]> points=new ArrayList<>();
    final private ArrayList<float[]> pointsCd=new ArrayList<>();
    final private ArrayList<float[]> points2D=new ArrayList<>();
    final private ArrayList<double[]> m1PointsInfo=new ArrayList<>();
    final private ArrayList<float[]> YStringsPos=new ArrayList<>();
    final private ArrayList<float[]> YStringsPos2D =new ArrayList<>();

    private String[] YStrings={"","","","","","","","","",""};


    private String infoTextX="x";
    private String infoTextY="y";
    private String infoTextZ="z";
    private String infoTextValue="value";

    final private  float[] point_low_center=new float[3];
    ArrayList<float[][]> lines=new ArrayList<>();
    ArrayList<float[][]> lines2D=new ArrayList<>();


    ArrayList<float[]> text_pos=new ArrayList<>();
    ArrayList<float[]> text_pos2D=new ArrayList<>();

    private Paint paint;
    private float kx=1;
    private float ky=1;
    private float kz=1;



    boolean draggable=false;
    int mode=0;

    private float zoom=1.0f;
    private float zoom_trance_x=0;
    private float zoom_trance_y=0;
    private Boolean showZoomInfo=false;
    private int XYZ_Text_size=40;
    private int XYZ_Value_Text_size=20;

    //float h=0.27f;

    private int shiftx=550;
    private int ZHeight=900;//像素

    final private int x=0;
    final private int y=1;
    final private int z=2;

    String XString="X";
    String YString="Y";
    String ZString="Z";

    private float degrees =1;//每旋转一次的角度

    XYZ_To_Value xyz_to_value;
    int mark=7;
    int lastMark=2;

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setXYZ_Name_size(int XYZ_name_size) {
        this.XYZ_Text_size = XYZ_name_size;
    }

    public void setXYZ_Value_Text_size(int XYZ_Value_Text_size){
        this.XYZ_Value_Text_size=XYZ_Value_Text_size;
    }

    public void setInfoTextX(String infoTextX) {
        this.infoTextX = infoTextX;
    }

    public void setInfoTextY(String infoTextY) {
        this.infoTextY = infoTextY;
    }

    public void setInfoTextZ(String infoTextZ) {
        this.infoTextZ = infoTextZ;
    }

    public void setInfoTextValue(String infoTextValue) {
        this.infoTextValue = infoTextValue;
    }

    public float getDegrees() {
        return degrees;
    }

    /**
     *
     * @param degrees 每旋转一次的角度
     */
    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }


    /**
     *
     * @param mode  if mode==1
     * @param xyz_to_value if mode==1 , xyz_to_value should not be null, or
     */
    public void setMode(int mode, XYZ_To_Value xyz_to_value){
        this.mode=mode;
        calculateSize();
        if(mode==1){
            xmin=0;
            ymin=-100;
            ymax=100;
        }
        this.xyz_to_value=xyz_to_value;
        coordinateInit();
        compute(true);
    }
    public void setXName(String XString) {
        this.XString = XString;
    }

    public void setYName(String YString) {
        this.YString = YString;
    }

    public void setZName(String ZString) {
        this.ZString = ZString;
    }

    public Boolean getShowZoomInfo() {
        return showZoomInfo;
    }

    public void setShowZoomInfo(Boolean showZoomInfo) {
        this.showZoomInfo = showZoomInfo;
        drawSomething();
    }

    /**
     *
     * @param YStrings M1模式 设置Y轴的数据
     */
    public void setM1YStrings(String[] YStrings) {
        this.YStrings = YStrings;
    }


    public void addM1Point(float x, float y, float z, int n, int line, int color) throws Exception{
        if(xyz_to_value==null){
            throw new Exception("xyz_to_value==null Exception\n " +
                    "使用addM1Points(float x, float y, float z, int n, int line, " +
                    "int color)之前，需要设置xyz_to_value。你可以通过setXyz_to_value(XYZ_To_Value " +
                    "xyz_to_value)或者setMode(int mode, XYZ_To_Value xyz_to_value)来设置");
        }else{
            double[] point={x,y,z,(float)color,(float)n};
            m1PointsInfo.add(point);

            float[] pcd={(float)n,(float)line,

                    xyz_to_value!=null?xyz_to_value.xyzToValue(x,y,z):
                            Math.abs(x)+Math.abs(y)+Math.abs(z),

                    (float)color};
            pointsCd.add(pcd);
        }
    }

    /**
     *
     * @param x 原始数据的x
     * @param y 原始数据的y
     * @param z 原始数据的z
     * @param n 原始数据的n
     * @param value 通过计算后的值
     * @param line 显示在第几行
     * @param color 颜色
     */
    public void addM1Point(float x, float y, float z, int n, float value, int line, int color) {
        double[] point={x,y,z,(float)color,(float)n};
        m1PointsInfo.add(point);

        float[] pcd={(float)n,(float)line,value, (float)color};
        pointsCd.add(pcd);
    }

    public void setXyz_to_value(XYZ_To_Value xyz_to_value) {
        this.xyz_to_value = xyz_to_value;
    }

    /**
     *
     * @param value 显示点的z轴
     * @param source 最初的的source   需要包含x y z n 四个参数。
     * @param line  第几行
     * @param color 颜色
     * @throws Exception value和source的数量不匹配
     */
    public void addM1Points(float[] value, float[][] source, int line, int color) throws Exception{

        if(value.length!=source.length){
            throw new Exception("value和source的数量不匹配");
        }else{
            for(int i=0;i<value.length;i++){
                double[] point={source[i][0],source[i][1],source[i][2],(float)color,source[i][3]};
                m1PointsInfo.add(point);

                float[] pcd={source[i][3],(float)line, value[i],(float)color};
                pointsCd.add(pcd);
            }
        }

    }

    /**
     *
     * @param source  带x y z n 的原始数据数组
     * @param line 显示在的行数
     * @param color 显示的颜色
     * @throws Exception xyz_to_value==null 异常 或者 数据越界异常
     * xyz_to_value 用于计算xyz三个参数 然后显示在z轴上
     */
    public void addM1Points(float[][] source, int line, int color) throws Exception{

        if(xyz_to_value==null){
            throw new Exception("xyz_to_value==null Exception\n " +
                    "使用addM1Points(float x, float y, float z, int n, int line, " +
                    "int color)之前，需要设置xyz_to_value。你可以通过setXyz_to_value(XYZ_To_Value " +
                    "xyz_to_value)或者setMode(int mode, XYZ_To_Value xyz_to_value)来设置");
        }else{
            for (float[] data : source) {
                double[] point = {data[0], data[1], data[2], (float) color, data[3]};
                m1PointsInfo.add(point);

                float[] pcd = {data[3], (float) line,
                        xyz_to_value.xyzToValue(data[0], data[1], data[2]),
                        (float) color};
                pointsCd.add(pcd);
            }
        }

    }

    public void addM1Points(float[] values, int line, int color) throws Exception{

        if(xyz_to_value==null){
            throw new Exception("xyz_to_value==null Exception\n " +
                    "使用addM1Points(float x, float y, float z, int n, int line, " +
                    "int color)之前，需要设置xyz_to_value。你可以通过setXyz_to_value(XYZ_To_Value " +
                    "xyz_to_value)或者setMode(int mode, XYZ_To_Value xyz_to_value)来设置");
        }else{
            for(int i=0;i<values.length;i++){
                double[] point={Double.NaN,Double.NaN,Double.NaN,(float)color,Double.NaN};
                m1PointsInfo.add(point);

                float[] pcd={i,(float)line,values[i], (float)color};
                pointsCd.add(pcd);
            }
        }

    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        //计算中线点
        float Tzmin=(zmin-(zmax+zmin)/2)*kz;
        float Tzmax=(zmax-(zmax+zmin)/2)*kz;
        zoom_trance_x=lines2D.get(41)[0][0]-lines2D.get(41)[0][0]*zoom;
        zoom_trance_y=lines2D.get(41)[0][1]-lines2D.get(41)[0][1]*zoom+viewHeight/4-(Tzmax+Tzmin)/4;;
        drawSomething();
    }

    public float getZoom_trance_x() {
        return zoom_trance_x;
    }

    public void setZoom_trance_x(float zoom_trance_x) {
        this.zoom_trance_x = -zoom_trance_x;
        drawSomething();
    }

    public float getZoom_trance_y() {
        return zoom_trance_y;
    }

    public void setZoom_trance_y(float zoom_trance_y) {
        this.zoom_trance_y = -zoom_trance_y;
        drawSomething();
    }



    private float xmax=60;

    public float getXmax() {
        return xmax;
    }

    public void setXmax(float xmax) {
        this.xmax = xmax;
        coordinateInit();
        compute(false);
        rollScreenX(1,0);
        roll_Z_axis(0,0);
    }

    public float getXmin() {
        return xmin;
    }

    public void setXmin(float xmin) {
        this.xmin = xmin;
        coordinateInit();
        compute(false);
    }

    public float getYmax() {
        return ymax;
    }

    public void setYmax(float ymax) {
        if(mode==1){
            this.ymax = ymax*200;
        }else{
            this.ymax = ymax;
        }


        coordinateInit();
        compute(false);
    }

    public float getYmin() {
        return ymin;
    }

    public void setYmin(float ymin) {
        this.ymin = ymin;
        coordinateInit();
        compute(false);
        rollScreenX(1,0);
        roll_Z_axis(0,0);
    }

    public float getZmax() {
        return zmax;
    }

    public void setZmax(float zmax) {
        this.zmax = zmax;
        coordinateInit();
        compute(false);
        rollScreenX(1,0);
        roll_Z_axis(0,0);
    }

    public float getZmin() {
        return zmin;
    }

    public void setZmin(float zmin) {
        this.zmin = zmin;
        coordinateInit();
        compute(false);
        rollScreenX(1,0);
        roll_Z_axis(0,0);
    }

    private float xmin=-60;
    private float ymax=60;
    private float ymin=-60;
    private float zmax=60;
    private float zmin=0;

    private MyCheckBox cb;

    //子线程标志位
    private boolean drawPointInfo=false;
    private int drawPointInfoIndex=0;
    public Points3DView1209(Context context) {
        super(context);
        initView();
    }

    public Points3DView1209(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public Points3DView1209(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启子线程
        Log.i("llll","surfaceCreated");
        setOnTouchListener(this);
        //new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("llll","surfaceChanged");
        viewWidth=width;
        viewHeight=height;
        if(viewCanDraw !=null){
            viewCanDraw.candraw(this);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    int height_level=7;

    public void compute(boolean refreshNow){
        try{
            ZHeight=2*viewHeight/5;
            //h=ZHeight*1f/zmax;
            //kz=ZHeight*1f/(zmax-zmin);
            int n;
            if(viewHeight<viewWidth){
                n=viewHeight/17;
            }else{
                n=viewWidth/17;
            }
            //int m=n/2;
            int m=n;
            kx=(n*17/2)/(xmax-xmin);
            ky=(m*17/2)/(ymax-ymin);
            kz=height_level*(5*m/5f)/(zmax-zmin);
            shiftx=viewWidth/2;
            float yuandianX=shiftx;
            float yuandianY=ZHeight/2+zmin*kz;

            float Cx=(xmax+xmin)/2*kx;

            points2D.clear();
            points.clear();
            //float Tzmax=(zmax-(zmax+zmin)/2)*kz;
            //float Tzmin=(zmin-(zmax+zmin)/2)*kz;
            float centerZ=(zmax+zmin)/2;
//            Log.i("aaaa","kx="+kx+" xmax="+xmax+" xmin="+xmin+
//                    " ky="+ky+" ymax="+ymax+" ymin="+ymin+
//                    " kz="+kz+" zmax="+zmax+" zmin="+zmin);
            for(float[] p:pointsCd){
                float[] pcd;
                if(mode==1){
                    pcd=new float[]{p[x]*kx-Cx,(p[y]-3)*(ymax-ymin)/6*ky,(p[z]-centerZ)*kz,(p[z]-centerZ)*kz+0};
                }else{
                    pcd=new float[]{p[x]*kx-Cx,p[y]*ky,(p[z]-centerZ)*kz,(p[z]-centerZ)*kz+0};
                }

                points.add(pcd);
                /*float[] newP={yuandianX+(pcd[x]),
                        yuandianY+(pcd[y])+(float)((-pcd[z]))};*/
                float[] newP={yuandianX+(pcd[x]),
                        yuandianY+(pcd[y])};
                points2D.add(newP);
            }

            lines2D.clear();
            for(float[][] line:lines){
                /*float[][] line2D={
                        {yuandianX+(line[0][x]),
                                yuandianY+(line[0][y])+(float)((-line[0][z]))},
                        {yuandianX+(line[1][x]),
                                yuandianY+(line[1][y])+(float)((-line[1][z]))}
                };*/
                float[][] line2D={
                        {yuandianX+(line[0][x]),
                                yuandianY+(line[0][y])},
                        {yuandianX+(line[1][x]),
                                yuandianY+(line[1][y])}
                };
                lines2D.add(line2D);
            }

            text_pos2D.clear();
            for(float[] pos:text_pos){
                /*float[] newPos={yuandianX+(pos[x]),
                        yuandianY+(pos[y])+(float)((-pos[z]))};*/
                float[] newPos={yuandianX+(pos[x]),
                        yuandianY+(pos[y])};
                text_pos2D.add(newPos);
            }
            if(mode==1){
                for(float[] pos:YStringsPos){
                    /*float[] newPos={yuandianX+(pos[x]),
                            yuandianY+(pos[y])+(float)((-pos[z]))};*/
                    float[] newPos={yuandianX+(pos[x]),
                            yuandianY+(pos[y])};
                    YStringsPos2D.add(newPos);
                }
            }

            if(refreshNow){
                drawSomething();
            }

        }catch (Exception e){

        }
    }

    private void coordinateInit(){
        try{
            ZHeight=2*viewHeight/5;
            //h=ZHeight*1f/zmax;
            //kz=ZHeight*1f/(zmax-zmin);
            int n;
            if(viewHeight<viewWidth){
                n=viewHeight/17;
            }else{
                n=viewWidth/17;
            }
            //int m=n/2;
            int m=n;
            shiftx=viewWidth/2;
            kx=(n*17/2)/(xmax-xmin);
            ky=(m*17/2)/(ymax-ymin);
            kz=height_level*(5*m/5f)/(zmax-zmin);

            point_low_center[0]=0;
            point_low_center[1]=0;
            point_low_center[2]=(zmin-(zmax+zmin)/2)*kz;
            lines.clear();



            float Tzmax=(zmax-(zmax+zmin)/2)*kz;
            float Tzmin=(zmin-(zmax+zmin)/2)*kz;
            float Txmin=(xmin-(xmax+xmin)/2)*kx;
            float Txmax=(xmax-(xmax+xmin)/2)*kx;
            float Tymin=(ymin-(ymax+ymin)/2)*ky;
            float Tymax=(ymax-(ymax+ymin)/2)*ky;
            float Dxs=(xmax-xmin)/6*kx;
            float Dys=(ymax-ymin)/6*ky;
            float Dzs=(zmax-zmin)/height_level*kz;

            float yuandianX=shiftx;
            float yuandianY=ZHeight/2+Tzmin;

            zoom_trance_y=viewHeight/4-(Tzmax+Tzmin)/4;
            //Log.i("aaaa","ZHeight="+ZHeight+" yuandianY="+yuandianY+" Tzmax="+Tzmax+" Tzmin="+Tzmin+" dzs="+Dzs);

            float[][] line0={{Txmin,Tymax,Tzmin},{Txmin,Tymax,Tzmax}}; //0

            float[][] line_left_1={{Txmin,Tymax-Dys,Tzmin},{Txmin,Tymax-Dys,Tzmax}};//1
            float[][] line_left_2={{Txmin,Tymax-2*Dys,Tzmin},{Txmin,Tymax-2*Dys,Tzmax}};
            float[][] line_left_3={{Txmin,Tymax-3*Dys,Tzmin},{Txmin,Tymax-3*Dys,Tzmax}};
            float[][] line_left_4={{Txmin,Tymax-4*Dys,Tzmin},{Txmin,Tymax-4*Dys,Tzmax}};
            float[][] line_left_5={{Txmin,Tymax-5*Dys,Tzmin},{Txmin,Tymax-5*Dys,Tzmax}};
            float[][] line_left_6={{Txmin,Tymax-6*Dys,Tzmin},{Txmin,Tymax-6*Dys,Tzmax}};//6

            float[][] line_back_1={{Txmin+Dxs,Tymax,Tzmin},{Txmin+Dxs,Tymax,Tzmax}};//7
            float[][] line_back_2={{Txmin+2*Dxs,Tymax,Tzmin},{Txmin+2*Dxs,Tymax,Tzmax}};
            float[][] line_back_3={{Txmin+3*Dxs,Tymax,Tzmin},{Txmin+3*Dxs,Tymax,Tzmax}};
            float[][] line_back_4={{Txmin+4*Dxs,Tymax,Tzmin},{Txmin+4*Dxs,Tymax,Tzmax}};
            float[][] line_back_5={{Txmin+5*Dxs,Tymax,Tzmin},{Txmin+5*Dxs,Tymax,Tzmax}};
            float[][] line_back_6={{Txmin+6*Dxs,Tymax,Tzmin},{Txmin+6*Dxs,Tymax,Tzmax}};//12

            float[][] line_left_a={{Txmin,Tymax,Tzmin},{Txmin,Tymin,Tzmin}};//13
            float[][] line_back_a={{Txmin,Tymax,Tzmin},{Txmax,Tymax,Tzmin}};//14

            float[][] line_down_1={{Txmin+Dxs,Tymax,Tzmin},{Txmin+Dxs,Tymin,Tzmin}};//15
            float[][] line_down_2={{Txmin+2*Dxs,Tymax,Tzmin},{Txmin+2*Dxs,Tymin,Tzmin}};
            float[][] line_down_3={{Txmin+3*Dxs,Tymax,Tzmin},{Txmin+3*Dxs,Tymin,Tzmin}};
            float[][] line_down_4={{Txmin+4*Dxs,Tymax,Tzmin},{Txmin+4*Dxs,Tymin,Tzmin}};
            float[][] line_down_5={{Txmin+5*Dxs,Tymax,Tzmin},{Txmin+5*Dxs,Tymin,Tzmin}};
            float[][] line_down_6={{Txmin+6*Dxs,Tymax,Tzmin},{Txmin+6*Dxs,Tymin,Tzmin}};//20

            float[][] line_left_a_u_1={{Txmin,Tymax,Tzmin+Dzs},{Txmin,Tymin,Tzmin+Dzs}};//21
            float[][] line_left_a_u_2={{Txmin,Tymax,Tzmin+2*Dzs},{Txmin,Tymin,Tzmin+2*Dzs}};
            float[][] line_left_a_u_3={{Txmin,Tymax,Tzmin+3*Dzs},{Txmin,Tymin,Tzmin+3*Dzs}};
            float[][] line_left_a_u_4={{Txmin,Tymax,Tzmin+4*Dzs},{Txmin,Tymin,Tzmin+4*Dzs}};
            float[][] line_left_a_u_5={{Txmin,Tymax,Tzmin+5*Dzs},{Txmin,Tymin,Tzmin+5*Dzs}};
            float[][] line_left_a_u_6={{Txmin,Tymax,Tzmin+6*Dzs},{Txmin,Tymin,Tzmin+6*Dzs}};
            float[][] line_left_a_u_7={{Txmin,Tymax,Tzmin+7*Dzs},{Txmin,Tymin,Tzmin+7*Dzs}};//27

            float[][] line_down_a_1={{Txmin,Tymax-Dys,Tzmin},{Txmax,Tymax-Dys,Tzmin}};//28
            float[][] line_down_a_2={{Txmin,Tymax-2*Dys,Tzmin},{Txmax,Tymax-2*Dys,Tzmin}};
            float[][] line_down_a_3={{Txmin,Tymax-3*Dys,Tzmin},{Txmax,Tymax-3*Dys,Tzmin}};
            float[][] line_down_a_4={{Txmin,Tymax-4*Dys,Tzmin},{Txmax,Tymax-4*Dys,Tzmin}};
            float[][] line_down_a_5={{Txmin,Tymax-5*Dys,Tzmin},{Txmax,Tymax-5*Dys,Tzmin}};
            float[][] line_down_a_6={{Txmin,Tymax-6*Dys,Tzmin},{Txmax,Tymax-6*Dys,Tzmin}};//33

            float[][] line_back_a_1={{Txmin,Tymax,Tzmin+Dzs},{Txmax,Tymax,Tzmin+Dzs}};//34
            float[][] line_back_a_2={{Txmin,Tymax,Tzmin+2*Dzs},{Txmax,Tymax,Tzmin+2*Dzs}};
            float[][] line_back_a_3={{Txmin,Tymax,Tzmin+3*Dzs},{Txmax,Tymax,Tzmin+3*Dzs}};
            float[][] line_back_a_4={{Txmin,Tymax,Tzmin+4*Dzs},{Txmax,Tymax,Tzmin+4*Dzs}};
            float[][] line_back_a_5={{Txmin,Tymax,Tzmin+5*Dzs},{Txmax,Tymax,Tzmin+5*Dzs}};
            float[][] line_back_a_6={{Txmin,Tymax,Tzmin+6*Dzs},{Txmax,Tymax,Tzmin+6*Dzs}};
            float[][] line_back_a_7={{Txmin,Tymax,Tzmin+7*Dzs},{Txmax,Tymax,Tzmin+7*Dzs}};//40

            float[][] linex={{0,0,0}, {0,Tymax,0}}; //lines in 41
            float[][] liney={{0,0,0}, {Txmax,0,0}};  //lines in 42
            float[][] linez={{0,0,0}, {0,0,Tzmax}}; //lines in 43

            float[][] line44={{Txmax,Tymin,Tzmin},{Txmax,Tymin,Tzmax}};//lines in 44

            float[][] line_right_1={{Txmax,Tymin+Dys,Tzmin},{Txmax,Tymin+Dys,Tzmax}};//45
            float[][] line_right_2={{Txmax,Tymin+2*Dys,Tzmin},{Txmax,Tymin+2*Dys,Tzmax}};
            float[][] line_right_3={{Txmax,Tymin+3*Dys,Tzmin},{Txmax,Tymin+3*Dys,Tzmax}};
            float[][] line_right_4={{Txmax,Tymin+4*Dys,Tzmin},{Txmax,Tymin+4*Dys,Tzmax}};
            float[][] line_right_5={{Txmax,Tymin+5*Dys,Tzmin},{Txmax,Tymin+5*Dys,Tzmax}};
            float[][] line_right_6={{Txmax,Tymin+6*Dys,Tzmin},{Txmax,Tymin+6*Dys,Tzmax}};//50

            float[][] line_front_1={{Txmin+Dxs,Tymin,Tzmin},{Txmin+Dxs,Tymin,Tzmax}};//51
            float[][] line_front_2={{Txmin+2*Dxs,Tymin,Tzmin},{Txmin+2*Dxs,Tymin,Tzmax}};
            float[][] line_front_3={{Txmin+3*Dxs,Tymin,Tzmin},{Txmin+3*Dxs,Tymin,Tzmax}};
            float[][] line_front_4={{Txmin+4*Dxs,Tymin,Tzmin},{Txmin+4*Dxs,Tymin,Tzmax}};
            float[][] line_front_5={{Txmin+5*Dxs,Tymin,Tzmin},{Txmin+5*Dxs,Tymin,Tzmax}};
            float[][] line_front_6={{Txmin+6*Dxs,Tymin,Tzmin},{Txmin+6*Dxs,Tymin,Tzmax}};//56

            float[][] line_right_a={{Txmin,Tymin,Tzmax},{Txmax,Tymin,Tzmax}};//57
            float[][] line_front_a={{Txmax,Tymin,Tzmax},{Txmax,Tymax,Tzmax}};//58

            float[][] line_top_1={{Txmin+Dxs,Tymax,Tzmax},{Txmin+Dxs,Tymin,Tzmax}};//59
            float[][] line_top_2={{Txmin+2*Dxs,Tymax,Tzmax},{Txmin+2*Dxs,Tymin,Tzmax}};
            float[][] line_top_3={{Txmin+3*Dxs,Tymax,Tzmax},{Txmin+3*Dxs,Tymin,Tzmax}};
            float[][] line_top_4={{Txmin+4*Dxs,Tymax,Tzmax},{Txmin+4*Dxs,Tymin,Tzmax}};
            float[][] line_top_5={{Txmin+5*Dxs,Tymax,Tzmax},{Txmin+5*Dxs,Tymin,Tzmax}};
            float[][] line_top_6={{Txmin+6*Dxs,Tymax,Tzmax},{Txmin+6*Dxs,Tymin,Tzmax}};//64

            float[][] line_right_a_1={{Txmax,Tymin,Tzmin+Dzs},{Txmax,Tymax,Tzmin+Dzs}};//65
            float[][] line_right_a_2={{Txmax,Tymin,Tzmin+2*Dzs},{Txmax,Tymax,Tzmin+2*Dzs}};
            float[][] line_right_a_3={{Txmax,Tymin,Tzmin+3*Dzs},{Txmax,Tymax,Tzmin+3*Dzs}};
            float[][] line_right_a_4={{Txmax,Tymin,Tzmin+4*Dzs},{Txmax,Tymax,Tzmin+4*Dzs}};
            float[][] line_right_a_5={{Txmax,Tymin,Tzmin+5*Dzs},{Txmax,Tymax,Tzmin+5*Dzs}};
            float[][] line_right_a_6={{Txmax,Tymin,Tzmin+6*Dzs},{Txmax,Tymax,Tzmin+6*Dzs}};
            float[][] line_right_a_7={{Txmax,Tymin,Tzmin+7*Dzs},{Txmax,Tymax,Tzmin+7*Dzs}};//71

            float[][] line_top_a_1={{Txmin,Tymax-Dys,Tzmax},{Txmax,Tymax-Dys,Tzmax}};//72
            float[][] line_top_a_2={{Txmin,Tymax-2*Dys,Tzmax},{Txmax,Tymax-2*Dys,Tzmax}};
            float[][] line_top_a_3={{Txmin,Tymax-3*Dys,Tzmax},{Txmax,Tymax-3*Dys,Tzmax}};
            float[][] line_top_a_4={{Txmin,Tymax-4*Dys,Tzmax},{Txmax,Tymax-4*Dys,Tzmax}};
            float[][] line_top_a_5={{Txmin,Tymax-5*Dys,Tzmax},{Txmax,Tymax-5*Dys,Tzmax}};
            float[][] line_top_a_6={{Txmin,Tymax-6*Dys,Tzmax},{Txmax,Tymax-6*Dys,Tzmax}};//77

            float[][] line_front_a_1={{Txmin,Tymin,Tzmin+Dzs},{Txmax,Tymin,Tzmin+Dzs}};//78
            float[][] line_front_a_2={{Txmin,Tymin,Tzmin+2*Dzs},{Txmax,Tymin,Tzmin+2*Dzs}};
            float[][] line_front_a_3={{Txmin,Tymin,Tzmin+3*Dzs},{Txmax,Tymin,Tzmin+3*Dzs}};
            float[][] line_front_a_4={{Txmin,Tymin,Tzmin+4*Dzs},{Txmax,Tymin,Tzmin+4*Dzs}};
            float[][] line_front_a_5={{Txmin,Tymin,Tzmin+5*Dzs},{Txmax,Tymin,Tzmin+5*Dzs}};
            float[][] line_front_a_6={{Txmin,Tymin,Tzmin+6*Dzs},{Txmax,Tymin,Tzmin+6*Dzs}};
            float[][] line_front_a_7={{Txmin,Tymin,Tzmin+7*Dzs},{Txmax,Tymin,Tzmin+7*Dzs}};//84

            float[][] baseLine1={{Txmin,Tymax,Tzmin},{Txmin,Tymax,Tzmax}}; //85
            float[][] baseLine2={{Txmin,Tymax-6*Dys,Tzmin},{Txmin,Tymax-6*Dys,Tzmax}}; //86
            float[][] baseLine3={{Txmax,Tymin,Tzmin},{Txmax,Tymin,Tzmax}}; //87
            float[][] baseLine4={{Txmax,Tymin+6*Dys,Tzmin},{Txmax,Tymin+6*Dys,Tzmax}}; //88
            float[][] baseLine5={{Txmin,Tymax,Tzmin},{Txmin,Tymin,Tzmin}}; //89
            float[][] baseLine6={{Txmin,Tymax,Tzmin+7*Dzs},{Txmin,Tymin,Tzmin+7*Dzs}}; //90
            float[][] baseLine7={{Txmax,Tymin,Tzmin+7*Dzs},{Txmax,Tymax,Tzmin+7*Dzs}}; //91
            float[][] baseLine8={{Txmax,Tymin,Tzmin},{Txmax,Tymax,Tzmin}}; //92
            float[][] baseLine9={{Txmin,Tymax,Tzmin},{Txmax,Tymax,Tzmin}}; //93
            float[][] baseLine10={{Txmin,Tymax,Tzmin+7*Dzs},{Txmax,Tymax,Tzmin+7*Dzs}}; //94
            float[][] baseLine11={{Txmin,Tymin,Tzmax},{Txmax,Tymin,Tzmax}}; //95
            float[][] baseLine12={{Txmin,Tymin,Tzmin},{Txmax,Tymin,Tzmin}}; //96

            lines.add(line0);
            lines.add(line_left_1);
            lines.add(line_left_2);
            lines.add(line_left_3);
            lines.add(line_left_4);
            lines.add(line_left_5);
            lines.add(line_left_6);
            lines.add(line_back_1);
            lines.add(line_back_2);
            lines.add(line_back_3);
            lines.add(line_back_4);
            lines.add(line_back_5);
            lines.add(line_back_6);
            lines.add(line_left_a);
            lines.add(line_back_a);
            lines.add(line_down_1);
            lines.add(line_down_2);
            lines.add(line_down_3);
            lines.add(line_down_4);
            lines.add(line_down_5);
            lines.add(line_down_6);
            lines.add(line_left_a_u_1);
            lines.add(line_left_a_u_2);
            lines.add(line_left_a_u_3);
            lines.add(line_left_a_u_4);
            lines.add(line_left_a_u_5);
            lines.add(line_left_a_u_6);
            lines.add(line_left_a_u_7);
            lines.add(line_down_a_1);
            lines.add(line_down_a_2);
            lines.add(line_down_a_3);
            lines.add(line_down_a_4);
            lines.add(line_down_a_5);
            lines.add(line_down_a_6);
            lines.add(line_back_a_1);
            lines.add(line_back_a_2);
            lines.add(line_back_a_3);
            lines.add(line_back_a_4);
            lines.add(line_back_a_5);
            lines.add(line_back_a_6);
            lines.add(line_back_a_7);
            lines.add(linex);//41
            lines.add(liney);//42
            lines.add(linez);//43
            lines.add(line44);//44
            lines.add(line_right_1);
            lines.add(line_right_2);
            lines.add(line_right_3);
            lines.add(line_right_4);
            lines.add(line_right_5);
            lines.add(line_right_6);
            lines.add(line_front_1);
            lines.add(line_front_2);
            lines.add(line_front_3);
            lines.add(line_front_4);
            lines.add(line_front_5);
            lines.add(line_front_6);
            lines.add(line_right_a);
            lines.add(line_front_a);
            lines.add(line_top_1);
            lines.add(line_top_2);
            lines.add(line_top_3);
            lines.add(line_top_4);
            lines.add(line_top_5);
            lines.add(line_top_6);
            lines.add(line_right_a_1);
            lines.add(line_right_a_2);
            lines.add(line_right_a_3);
            lines.add(line_right_a_4);
            lines.add(line_right_a_5);
            lines.add(line_right_a_6);
            lines.add(line_right_a_7);
            lines.add(line_top_a_1);
            lines.add(line_top_a_2);
            lines.add(line_top_a_3);
            lines.add(line_top_a_4);
            lines.add(line_top_a_5);
            lines.add(line_top_a_6);
            lines.add(line_front_a_1);
            lines.add(line_front_a_2);
            lines.add(line_front_a_3);
            lines.add(line_front_a_4);
            lines.add(line_front_a_5);
            lines.add(line_front_a_6);
            lines.add(line_front_a_7);
            lines.add(baseLine1);
            lines.add(baseLine2);
            lines.add(baseLine3);
            lines.add(baseLine4);
            lines.add(baseLine5);
            lines.add(baseLine6);
            lines.add(baseLine7);
            lines.add(baseLine8);
            lines.add(baseLine9);
            lines.add(baseLine10);
            lines.add(baseLine11);
            lines.add(baseLine12);

            lines2D.clear();
            for(float[][] line:lines){
                /*float[][] line2D={
                        {yuandianX+(line[0][x]),
                                yuandianY+(line[0][y])+(float)((-line[0][z]))},
                        {yuandianX+(line[1][x]),
                                yuandianY+(line[1][y])+(float)((-line[1][z]))}
                };*/
                float[][] line2D={
                        {yuandianX+(line[0][x]),
                                yuandianY+(line[0][y])},
                        {yuandianX+(line[1][x]),
                                yuandianY+(line[1][y])}
                };
                lines2D.add(line2D);
            }

            text_pos.clear();

            ///////// 4*8=32
            /*if(Math.abs(zmax)<1000 && Math.abs(zmin)<1000){
                for(int i=0;i<8;i++){
                    float[] pos={Txmin-Dxs/2,Tymin-Dys/2,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }

                for(int i=0;i<8;i++){
                    float[] pos={Txmax+Dxs/2,Tymin-Dys/2,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }

                for(int i=0;i<8;i++){
                    float[] pos={Txmax+Dxs/2,Tymax+Dys/2,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }

                for(int i=0;i<8;i++){
                    float[] pos={Txmin-Dxs/2,Tymax+Dys/2,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }
            }else{*/
                for(int i=0;i<8;i++){
                    //float[] pos={Txmin-Dxs,Tymin-Dys,Tzmin+i*Dzs};
                    float[] pos={Txmin-10,Tymin-10,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }

                for(int i=0;i<8;i++){
                    //float[] pos={Txmax+Dxs,Tymin-Dys,Tzmin+i*Dzs};
                    float[] pos={Txmax+10,Tymin-10,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }

                for(int i=0;i<8;i++){
                    //float[] pos={Txmax+Dxs,Tymax+Dys,Tzmin+i*Dzs};
                    float[] pos={Txmax+10,Tymax+10,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }

                for(int i=0;i<8;i++){
                    //float[] pos={Txmin-Dxs,Tymax+Dys,Tzmin+i*Dzs};
                    float[] pos={Txmin-10,Tymax+10,Tzmin+i*Dzs};
                    text_pos.add(pos);
                }
            //}

            //////////////  7*8=56
            for(int i=0;i<7;i++){
                float[] pos={Txmin+i*Dxs,Tymin-Dys,Tzmin};
                text_pos.add(pos);
            }
            for(int i=0;i<7;i++){
                float[] pos={Txmax+Dxs,Tymin+i*Dys,Tzmin};
                text_pos.add(pos);
            }
            for(int i=0;i<7;i++){
                float[] pos={Txmax-i*Dxs,Tymax+Dys,Tzmin};
                text_pos.add(pos);
            }
            for(int i=0;i<7;i++){
                float[] pos={Txmax-Dxs,Tymax-i*Dys,Tzmin};
                text_pos.add(pos);
            }

            for(int i=0;i<7;i++){
                float[] pos={Txmin+i*Dxs,Tymin-Dys,Tzmax};
                text_pos.add(pos);
            }
            for(int i=0;i<7;i++){
                float[] pos={Txmax+Dxs,Tymin+i*Dys,Tzmax};
                text_pos.add(pos);
            }
            for(int i=0;i<7;i++){
                float[] pos={Txmax-i*Dxs,Tymax+Dys,Tzmax};
                text_pos.add(pos);
            }
            for(int i=0;i<7;i++){
                float[] pos={Txmax-Dxs,Tymax-i*Dys,Tzmax};
                text_pos.add(pos);
            }
            ////////////// 7*8=56
            if(mode==1){
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+Dxs/5,Tymin+(i+1)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i+1)*Dxs,Tymax+Dys/5,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin-Dxs/5,Tymax-(i+1)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i+1)*Dxs,Tymin-Dys/5,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+Dxs/5,Tymin+(i+1)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i+1)*Dxs,Tymax+Dys/5,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin-Dxs/5,Tymax-(i+1)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i+1)*Dxs,Tymin-Dys/5,Tzmax};
                    text_pos.add(pos);
                }
                /////////////// 7*8=56
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+2*Dxs,Tymin+(i+1)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i+1)*Dxs,Tymax+2*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-2*Dxs,Tymax-(i+1)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i+1)*Dxs,Tymin-2*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+2*Dxs,Tymin+(i+1)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i+1)*Dxs,Tymax+2*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-2*Dxs,Tymax-(i+1)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i+1)*Dxs,Tymin-2*Dys,Tzmax};
                    text_pos.add(pos);
                }
            }else{
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+Dxs,Tymin+(i)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i)*Dxs,Tymax+Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin-Dxs,Tymax-(i)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i)*Dxs,Tymin-Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+Dxs,Tymin+(i)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i)*Dxs,Tymax+Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin-Dxs,Tymax-(i)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i)*Dxs,Tymin-Dys,Tzmax};
                    text_pos.add(pos);
                }

                ////////
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+2*Dxs,Tymin+(i+1)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i+1)*Dxs,Tymax+2*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-2*Dxs,Tymax-(i+1)*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i+1)*Dxs,Tymin-2*Dys,Tzmin};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax+2*Dxs,Tymin+(i+1)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-(i+1)*Dxs,Tymax+2*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmax-2*Dxs,Tymax-(i+1)*Dys,Tzmax};
                    text_pos.add(pos);
                }
                for(int i=0;i<7;i++){
                    float[] pos={Txmin+(i+1)*Dxs,Tymin-2*Dys,Tzmax};
                    text_pos.add(pos);
                }
            }

            float zPos;
            if(Dzs>100){
                zPos=0;
            }else{
                zPos=0;
            }
            float[] Z1={Txmin-Dxs,Tymin-Dys,!ZString.equals("Z")?zPos+Dzs:zPos+Dzs/2};
            float[] Z2={Txmax+Dxs,Tymin-Dys,!ZString.equals("Z")?zPos+Dzs:zPos+Dzs/2};
            float[] Z3={Txmax+Dxs,Tymax+Dys,!ZString.equals("Z")?zPos+Dzs:zPos+Dzs/2};
            float[] Z4={Txmin-Dxs,Tymax+Dys,!ZString.equals("Z")?zPos+Dzs:zPos+Dzs/2};
            text_pos.add(Z1);//200
            text_pos.add(Z2);//201
            text_pos.add(Z3);//202
            text_pos.add(Z4);//203

            float[] X1={(Txmax+Txmin)/2-(!XString.equals("X")?Dxs:Dxs/2),(Tymin-3*Dys/2),Tzmin-Dzs};
            float[] X2={(Txmax+Txmin)/2+(!XString.equals("X")?Dxs:Dxs/2),(Tymax+3*Dys/2),Tzmin-Dzs};
            float[] X3={(Txmax+Txmin)/2+(!XString.equals("X")?Dxs:Dxs/2),(Tymax+3*Dys/2),Tzmax+Dzs};
            float[] X4={(Txmax+Txmin)/2-(!XString.equals("X")?Dxs:Dxs/2),(Tymin-3*Dys/2),Tzmax+Dzs};
            float[] Y1={(Txmax+3*Dxs/2),(Tymax+Tymin)/2-(!YString.equals("Y")?Dys:Dys/2),Tzmin-Dzs};
            float[] Y2={(Txmax+3*Dxs/2),(Tymax+Tymin)/2-(!YString.equals("Y")?Dys:Dys/2),Tzmax+Dzs};
            float[] Y3={(Txmin-3*Dxs/2),(Tymax+Tymin)/2+(!YString.equals("Y")?Dys:Dys/2),Tzmax+Dzs};
            float[] Y4={(Txmin-3*Dxs/2),(Tymax+Tymin)/2+(!YString.equals("Y")?Dys:Dys/2),Tzmin-Dzs};

            text_pos.add(X1);//204
            text_pos.add(X2);
            text_pos.add(X3);
            text_pos.add(X4);
            text_pos.add(Y1);//208
            text_pos.add(Y2);
            text_pos.add(Y3);
            text_pos.add(Y4);

            //

            //Log.i("aaaa","text_pos size="+text_pos.size());


            text_pos2D.clear();
            for(float[] pos:text_pos){
                /*float[] newPos={yuandianX+(pos[x]),
                        yuandianY+(pos[y])+(float)((-pos[z]))};*/
                float[] newPos={yuandianX+(pos[x]),
                        yuandianY+(pos[y])};
                text_pos2D.add(newPos);
            }

            int count=0;
            YStringsPos.clear();
            for(String s:YStrings){
                float[] pos={Txmax,(count-3)*(ymax+ymin)/6*ky-(ymax+ymin)/2*ky,Tzmin};
                YStringsPos.add(pos);
                count++;
            }

            if(mode==1){
                for(float[] pos:YStringsPos){
                    /*float[] newPos={yuandianX+(pos[x]),
                            yuandianY+(pos[y])+(float)((-pos[z]))};*/
                    float[] newPos={yuandianX+(pos[x]),
                            yuandianY+(pos[y])};
                    YStringsPos2D.add(newPos);
                }
            }

        }catch (Exception e){

        }
    }

    boolean isAnimationRunning=false;
    private void initView(){
        bgcolor=Color.argb(255,255,255,255);
        pointsColorRandom=Color.argb(
                255,
                (int)(Math.random()*256),
                (int)(Math.random()*256),
                (int)(Math.random()*256));
        mSurfaceHolder = getHolder();
        //注册回调方法
        cb=new MyCheckBox(this.getContext());
        cb.setInfo("X Y Z");
        mSurfaceHolder.addCallback(this);
        //设置一些参数方便后面绘图
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        paint=new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(0xffff0000);
    }



    public void addPoint(float x,float y, float z, int color){
        float[] p={x,y,z,(float)color};
        pointsCd.add(p);
    }

    public void addPoint(float x,float y, float z){
        float[] p={x,y,z,pointsColorRandom};
        pointsCd.add(p);
    }

    public void addPoint(float[] point,int color){
        float[] p={point[0],point[1],point[2],(float)color};
        pointsCd.add(p);
    }

    public void addPoint(float[] point){
        float[] p={point[0],point[1],point[2],pointsColorRandom};
        pointsCd.add(p);
    }

    public void addPoints(float[][] points_set,int color){
        for(float[] point:points_set){
            float[] p={point[0],point[1],point[2],(float)color};
            pointsCd.add(p);
        }
    }

//    public void addPoint(int line, int x,float value){
//        float[] p={x,y,z,pointsColorRandom};
//        pointsCd.add(p);
//    }

    public void addPoint(int x, int y,int z, int color){
        float[] p={x,y,z,(float)color};
        pointsCd.add(p);
    }

    public void addPoints(float[][] points_set){
        for(float[] point:points_set){
            float[] p={point[0],point[1],point[2],pointsColorRandom};
            pointsCd.add(p);
        }
    }

    interface ViewCanDraw {
        public void candraw(Points3DView1209 v);
    }


    ViewCanDraw viewCanDraw;


    public void setDrawCtrl(ViewCanDraw viewCanDraw){
        this.viewCanDraw =viewCanDraw;
    }

    public void setBgColor(int color){
        this.bgcolor=color;
    }

    public void show(){
        if(pointsCd.size()>0 && pointsCd.get(0).length>=3){
            Log.i("bbbbb","calculateSize");
            calculateSize();
        }else{
            Log.i("bbbbb","not calculateSize");
        }
        coordinateInit();
        compute(false);
        if(mode==1){
            rollScreenX(0,135);
            roll_Z_axis(0,45);
        }else{
            rollScreenX(0,135);
            roll_Z_axis(0,45);
        }

    }

    public void show(float maxX, float maxY, float maxZ, float minX, float minY, float minZ){
        xmax=maxX;
        ymax=maxY;
        zmax=maxZ;
        xmin=minX;
        ymin=minY;
        zmin=minZ;
        coordinateInit();
        compute(false);
        if(mode==1){
            rollScreenX(0,135);
            roll_Z_axis(0,45);
        }else{
            rollScreenX(0,135);
            roll_Z_axis(0,45);
        }

    }

    private void calculateSize(){
        if(!(pointsCd.size()>0) || pointsCd.get(0).length<3){
            return;
        }
        xmax=pointsCd.get(0)[0];
        ymax=pointsCd.get(0)[1];
        zmax=pointsCd.get(0)[2];
        xmin=pointsCd.get(0)[0];
        ymin=pointsCd.get(0)[1];
        zmin=pointsCd.get(0)[2];
        for(int i=1;i<pointsCd.size();i++){
            if(pointsCd.get(i)[0]>xmax){
                xmax=pointsCd.get(i)[0];

            }
        }
        if(xmax>0){
            xmax++;
        }
        for(int i=1;i<pointsCd.size();i++){
            if(pointsCd.get(i)[1]>ymax){
                ymax=pointsCd.get(i)[1];
            }
        }
        if(ymax>0){
            ymax++;
        }
        if(xmax>ymax){
            ymax=xmax;
        }else{
            xmax=ymax;
        }
        for(int i=1;i<pointsCd.size();i++){
            if(pointsCd.get(i)[2]>zmax){
                zmax=pointsCd.get(i)[2];
            }
        }
        if(zmax>0){
            zmax++;
        }

        for(int i=1;i<pointsCd.size();i++){
            if(pointsCd.get(i)[0]<xmin){
                xmin=pointsCd.get(i)[0];
            }
        }
        if(xmin<0){
            xmin--;
        }
        for(int i=1;i<pointsCd.size();i++){
            if(pointsCd.get(i)[1]<ymin){
                ymin=pointsCd.get(i)[1];
            }
        }
        if(ymin<0){
            ymin--;
        }
        if(ymin<xmin){
            xmin=ymin;
        }else{
            ymin=xmin;
        }

        for(int i=1;i<pointsCd.size();i++){
            if(pointsCd.get(i)[2]<zmin){
                zmin=pointsCd.get(i)[2];
            }
        }
        if(zmin<0){
            zmin--;
        }

        if(Math.abs(xmax)>Math.abs(xmin)){
            xmin=-xmax;
        }else{
            xmax=-xmin;
        }
        if(Math.abs(ymax)>Math.abs(ymin)){
            ymin=-ymax;
        }else{
            ymax=-ymin;
        }
        if(mode==1){
            xmin=0;
            ymin=-100;
            ymax=100;
        }
    }

    private void drawlines(int index_a, int index_b){
        for(int i=index_a;i<index_b;i++){
            mCanvas.drawLine(
                    lines2D.get(i)[0][0]*zoom+zoom_trance_x,
                    lines2D.get(i)[0][1]*zoom+zoom_trance_y,
                    lines2D.get(i)[1][0]*zoom+zoom_trance_x,
                    lines2D.get(i)[1][1]*zoom+zoom_trance_y,paint);
        }
    }

    private void drawlines(int[] index_a, int[] index_b){
        paint.setColor(0XFFDDDDDD);//0XFFDDDDDD 0XFF000000
        for(int i:index_a){
            mCanvas.drawLine(
                    lines2D.get(i)[0][0]*zoom+zoom_trance_x,
                    lines2D.get(i)[0][1]*zoom+zoom_trance_y,
                    lines2D.get(i)[1][0]*zoom+zoom_trance_x,
                    lines2D.get(i)[1][1]*zoom+zoom_trance_y,paint);
        }
        paint.setColor(0XFF000000);//0XFFDDDDDD 0XFF000000
        for(int i:index_b){
            mCanvas.drawLine(
                    lines2D.get(i)[0][0]*zoom+zoom_trance_x,
                    lines2D.get(i)[0][1]*zoom+zoom_trance_y,
                    lines2D.get(i)[1][0]*zoom+zoom_trance_x,
                    lines2D.get(i)[1][1]*zoom+zoom_trance_y,paint);
        }
    }

    private float XYZNameRotation(int lineIndex){
        float dx=lines2D.get(lineIndex)[0][0]-lines2D.get(lineIndex)[1][0];
        float dy=lines2D.get(lineIndex)[0][1]-lines2D.get(lineIndex)[1][1];
        float rotation=0;
        if(dx!=0){
            double rotation1=Math.toDegrees(Math.atan(dy/dx));
            if(Double.isNaN(rotation1)){
                rotation=90;
            }else{
                rotation=(float) rotation1;
            }
        }else{
            rotation=90;
        }
        return rotation;
    }

    //绘图逻辑
    private void drawSomething() {
        try {
            //获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas();
            ZHeight=2*mCanvas.getHeight()/5;
            int n;
            if(mCanvas.getHeight()<mCanvas.getWidth()){
                n=mCanvas.getHeight()/17;
            }else{
                n=mCanvas.getWidth()/17;
            }

            int m=n;
            shiftx=mCanvas.getWidth()/2;
            kx=(n*17F/2)/(xmax-xmin);
            ky=(m*17F/2)/(ymax-ymin);
            kz=height_level*(5*m/5f)/(zmax-zmin);
            float yuandianX=shiftx;
            float yuandianY=ZHeight/2F+zmin*kz;
            float Txmax=xmax*kx;
            float Tymax=ymax*ky;

            //绘制背景
            mCanvas.drawColor(bgcolor);
            //画坐标

            paint.setColor(0xffdddddd);

            int i=0;
            mark=7;
            if(lines.get(41)[0][x]-lines.get(41)[1][x]>0){
                mark=mark&5;
                drawlines(45,51);
                drawlines(65,72);
            }else{
                drawlines(1,7);
                drawlines(21,28);
            }
            if(lines.get(42)[0][x]-lines.get(42)[1][x]>0){
                drawlines(51,57);
                drawlines(78,84);
            }else{
                mark=mark&6;
                drawlines(7,13);
                drawlines(34,41);
            }
            //Log.i("bbbb","θ="+θ+" xita="+(θ%360)+" γ="+γ+" 43y0=+"+lines.get(43)[0][y]+" 43y1="+lines.get(43)[1][y]+" d="+(lines.get(43)[0][y]-lines.get(43)[1][y]));
            if(γ <= -90){
                mark=mark&3;
                drawlines(15,21);
                drawlines(28,34);
            }else{
                drawlines(59,65);
                drawlines(72,78);

            }
            Log.i("aaaa","mark="+mark);

            if(mark==0){
                //drawlines(new int[]{88,91,92,93,94},new int[]{85,87,89,96});
                drawlines(new int[]{88,91,92,93,94,87},new int[]{85,89,96});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(203)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(203)[1]*zoom+zoom_trance_y,paint);
                }else{
                    mCanvas.rotate(90,text_pos2D.get(203)[0]*zoom+zoom_trance_x,text_pos2D.get(203)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(203)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(203)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(203)[0]*zoom+zoom_trance_x,text_pos2D.get(203)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(204)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(204)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(96);
                    mCanvas.rotate(rotation,text_pos2D.get(204)[0]*zoom+zoom_trance_x,text_pos2D.get(204)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(204)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(204)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(204)[0]*zoom+zoom_trance_x,text_pos2D.get(204)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(211)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(211)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(89);
                    mCanvas.rotate(rotation,text_pos2D.get(211)[0]*zoom+zoom_trance_x,text_pos2D.get(211)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(211)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(211)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(211)[0]*zoom+zoom_trance_x,text_pos2D.get(211)[1]*zoom+zoom_trance_y);
                }

                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i+24)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+24)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*i))+"",
                            text_pos2D.get(i+32)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    align=paint.getTextAlign();
                    for(i=0;i<YStrings.length;i++){
                        paint.setTextAlign(Paint.Align.RIGHT);
                        mCanvas.drawText(
                                YStrings[YStrings.length-1-i],
                                text_pos2D.get(i+88+14)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+14)[1]*zoom+zoom_trance_y,paint);

                    }
                    paint.setTextAlign(align);
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*(6-i)))+"",
                                text_pos2D.get(i+88+14)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+14)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==1){
                drawlines(new int[]{87,91,92,95,96,86},new int[]{88,89,93});

                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(202)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(202)[1]*zoom+zoom_trance_y,paint);
                }else{
                    mCanvas.rotate(90,text_pos2D.get(202)[0]*zoom+zoom_trance_x,text_pos2D.get(202)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(202)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(202)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(202)[0]*zoom+zoom_trance_x,text_pos2D.get(202)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(205)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(205)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(93);
                    mCanvas.rotate(rotation,text_pos2D.get(205)[0]*zoom+zoom_trance_x,text_pos2D.get(205)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(205)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(205)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(205)[0]*zoom+zoom_trance_x,text_pos2D.get(205)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(211)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(211)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(89);
                    mCanvas.rotate(rotation,text_pos2D.get(211)[0]*zoom+zoom_trance_x,text_pos2D.get(211)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(211)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(211)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(211)[0]*zoom+zoom_trance_x,text_pos2D.get(211)[1]*zoom+zoom_trance_y);
                }


                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i+16)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+16)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*(6-i)))+"",
                            text_pos2D.get(i+32+14)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32+14)[1]*zoom+zoom_trance_y,paint);
                }

                if(mode==1 ){
                    for(i=0;i<YStrings.length;i++){
                        mCanvas.drawText(
                                YStrings[YStrings.length-1-i],
                                text_pos2D.get(i+88+14)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+14)[1]*zoom+zoom_trance_y,paint);
                    }
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*(6-i)))+"",
                                text_pos2D.get(i+88+14)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+14)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==2){
                //drawlines(new int[]{85,89,90,93,94},new int[]{86,88,92,96});
                drawlines(new int[]{85,89,90,93,94,88},new int[]{86,92,96});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(200)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(200)[1]*zoom+zoom_trance_y,paint);
                }else {
                    mCanvas.rotate(90,text_pos2D.get(200)[0]*zoom+zoom_trance_x,text_pos2D.get(200)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(200)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(200)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(200)[0]*zoom+zoom_trance_x,text_pos2D.get(200)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(204)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(204)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(96);
                    mCanvas.rotate(rotation,text_pos2D.get(204)[0]*zoom+zoom_trance_x,text_pos2D.get(204)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(204)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(204)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(204)[0]*zoom+zoom_trance_x,text_pos2D.get(204)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(208)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(208)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(92);
                    mCanvas.rotate(rotation,text_pos2D.get(208)[0]*zoom+zoom_trance_x,text_pos2D.get(208)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(208)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(208)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(208)[0]*zoom+zoom_trance_x,text_pos2D.get(208)[1]*zoom+zoom_trance_y);
                }

                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*i))+"",
                            text_pos2D.get(i+32)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    for(i=0;i<YStrings.length;i++){
                        mCanvas.drawText(
                                YStrings[i],
                                text_pos2D.get(i+88)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88)[1]*zoom+zoom_trance_y,paint);
                    }
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*i))+"",
                                text_pos2D.get(i+88)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==3){
                //drawlines(new int[]{86,89,90,95,96},new int[]{85,87,92,93});
                drawlines(new int[]{86,89,90,95,96,85},new int[]{87,92,93});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(201)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(201)[1]*zoom+zoom_trance_y,paint);
                }else{
                    mCanvas.rotate(90,text_pos2D.get(201)[0]*zoom+zoom_trance_x,text_pos2D.get(201)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(201)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(201)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(201)[0]*zoom+zoom_trance_x,text_pos2D.get(201)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(205)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(205)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(93);
                    mCanvas.rotate(rotation,text_pos2D.get(205)[0]*zoom+zoom_trance_x,text_pos2D.get(205)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(205)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(205)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(205)[0]*zoom+zoom_trance_x,text_pos2D.get(205)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(208)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(208)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(92);
                    mCanvas.rotate(rotation,text_pos2D.get(208)[0]*zoom+zoom_trance_x,text_pos2D.get(208)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(208)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(208)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(208)[0]*zoom+zoom_trance_x,text_pos2D.get(208)[1]*zoom+zoom_trance_y);
                }

                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i+8)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+8)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*(6-i)))+"",
                            text_pos2D.get(i+32+14)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32+14)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    align=paint.getTextAlign();
                    paint.setTextAlign(Paint.Align.RIGHT);
                    for(i=0;i<YStrings.length;i++){
                        paint.setTextAlign(Paint.Align.RIGHT);
                        mCanvas.drawText(
                                YStrings[i],
                                text_pos2D.get(i+88)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88)[1]*zoom+zoom_trance_y,paint);
                    }
                    paint.setTextAlign(align);
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*i))+"",
                                text_pos2D.get(i+88)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==4){
                //drawlines(new int[]{88,91,92,93,94},new int[]{85,87,90,95});
                drawlines(new int[]{88,91,92,93,94,87},new int[]{85,90,95});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(203)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(203)[1]*zoom+zoom_trance_y,paint);
                }else {
                    mCanvas.rotate(90,text_pos2D.get(203)[0]*zoom+zoom_trance_x,text_pos2D.get(203)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(203)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(203)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(203)[0]*zoom+zoom_trance_x,text_pos2D.get(203)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(207)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(207)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(95);
                    mCanvas.rotate(rotation,text_pos2D.get(207)[0]*zoom+zoom_trance_x,text_pos2D.get(207)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(207)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(207)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(207)[0]*zoom+zoom_trance_x,text_pos2D.get(207)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(210)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(210)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(90);
                    mCanvas.rotate(rotation,text_pos2D.get(210)[0]*zoom+zoom_trance_x,text_pos2D.get(210)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(210)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(210)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(210)[0]*zoom+zoom_trance_x,text_pos2D.get(210)[1]*zoom+zoom_trance_y);
                }


                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i+24)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+24)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*i))+"",
                            text_pos2D.get(i+32+28)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32+28)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    for(i=0;i<YStrings.length;i++){
                        mCanvas.drawText(
                                YStrings[YStrings.length-1-i],
                                text_pos2D.get(i+88+42)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+42)[1]*zoom+zoom_trance_y,paint);
                    }
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*(6-i)))+"",
                                text_pos2D.get(i+88+42)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+42)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==5){
                //drawlines(new int[]{87,91,92,95,96},new int[]{86,88,90,94});
                drawlines(new int[]{87,91,92,95,96,86,},new int[]{88,90,94});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(202)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(202)[1]*zoom+zoom_trance_y,paint);
                }else{
                    mCanvas.rotate(90,text_pos2D.get(202)[0]*zoom+zoom_trance_x,text_pos2D.get(202)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(202)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(202)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(202)[0]*zoom+zoom_trance_x,text_pos2D.get(202)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(206)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(206)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(94);
                    mCanvas.rotate(rotation,text_pos2D.get(206)[0]*zoom+zoom_trance_x,text_pos2D.get(206)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(206)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(206)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(206)[0]*zoom+zoom_trance_x,text_pos2D.get(206)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(210)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(210)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(90);
                    mCanvas.rotate(rotation,text_pos2D.get(210)[0]*zoom+zoom_trance_x,text_pos2D.get(210)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(210)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(210)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(210)[0]*zoom+zoom_trance_x,text_pos2D.get(210)[1]*zoom+zoom_trance_y);
                }


                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i+16)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+16)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*(6-i)))+"",
                            text_pos2D.get(i+32+42)[0]*zoom+zoom_trance_x,

                            text_pos2D.get(i+32+42)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    for(i=0;i<YStrings.length;i++){
                        mCanvas.drawText(
                                YStrings[YStrings.length-1-i],
                                text_pos2D.get(i+88+42)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+42)[1]*zoom+zoom_trance_y,paint);
                    }
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*(6-i)))+"",
                                text_pos2D.get(i+88+42)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+42)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==6){
                //drawlines(new int[]{85,89,90,93,94},new int[]{86,88,91,95});
                drawlines(new int[]{85,89,90,93,94,88},new int[]{86,91,95});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(200)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(200)[1]*zoom+zoom_trance_y,paint);
                }else {
                    mCanvas.rotate(90,text_pos2D.get(200)[0]*zoom+zoom_trance_x,text_pos2D.get(200)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(200)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(200)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(200)[0]*zoom+zoom_trance_x,text_pos2D.get(200)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(207)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(207)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(95);
                    mCanvas.rotate(rotation,text_pos2D.get(207)[0]*zoom+zoom_trance_x,text_pos2D.get(207)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(207)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(207)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(207)[0]*zoom+zoom_trance_x,text_pos2D.get(207)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(209)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(209)[1]*zoom+zoom_trance_y,paint);
                }else {
                    float rotation=XYZNameRotation(91);
                    mCanvas.rotate(rotation,text_pos2D.get(209)[0]*zoom+zoom_trance_x,text_pos2D.get(209)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(209)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(209)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(209)[0]*zoom+zoom_trance_x,text_pos2D.get(209)[1]*zoom+zoom_trance_y);
                }

                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*i))+"",
                            text_pos2D.get(i+32+28)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32+28)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    for(i=0;i<YStrings.length;i++){
                        mCanvas.drawText(
                                YStrings[i],
                                text_pos2D.get(i+88+28)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+28)[1]*zoom+zoom_trance_y,paint);
                    }
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*i))+"",
                                text_pos2D.get(i+88+28)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+28)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }else if(mark==7){
                //drawlines(new int[]{86,89,90,95,96},new int[]{85,87,91,94});
                drawlines(new int[]{86,89,90,95,96,85},new int[]{87,91,94});
                paint.setAntiAlias(true);
                paint.setTextSize(XYZ_Text_size);
                paint.setColor(0xff000000);
                if(ZString.equalsIgnoreCase("Z")){
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(201)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(201)[1]*zoom+zoom_trance_y,paint);
                }else{
                    mCanvas.rotate(90,text_pos2D.get(201)[0]*zoom+zoom_trance_x,text_pos2D.get(201)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            ZString,
                            text_pos2D.get(201)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(201)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-90,text_pos2D.get(201)[0]*zoom+zoom_trance_x,text_pos2D.get(201)[1]*zoom+zoom_trance_y);
                }

                if(XString.equalsIgnoreCase("X")){
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(206)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(206)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(94);
                    mCanvas.rotate(rotation,text_pos2D.get(206)[0]*zoom+zoom_trance_x,text_pos2D.get(206)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            XString,
                            text_pos2D.get(206)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(206)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(206)[0]*zoom+zoom_trance_x,text_pos2D.get(206)[1]*zoom+zoom_trance_y);
                }

                if(YString.equalsIgnoreCase("Y")){
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(209)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(209)[1]*zoom+zoom_trance_y,paint);
                }else{
                    float rotation=XYZNameRotation(91);
                    mCanvas.rotate(rotation,text_pos2D.get(209)[0]*zoom+zoom_trance_x,text_pos2D.get(209)[1]*zoom+zoom_trance_y);
                    mCanvas.drawText(
                            YString,
                            text_pos2D.get(209)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(209)[1]*zoom+zoom_trance_y,paint);
                    mCanvas.rotate(-rotation,text_pos2D.get(209)[0]*zoom+zoom_trance_x,text_pos2D.get(209)[1]*zoom+zoom_trance_y);
                }

                paint.setTextSize(XYZ_Value_Text_size);
                Paint.Align align=paint.getTextAlign();
                paint.setTextAlign(Paint.Align.RIGHT);
                for(i=0;i<8;i++){
                    mCanvas.drawText(
                            (int)((zmin+(zmax-zmin)/height_level*i))+"",
                            text_pos2D.get(i+8)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+8)[1]*zoom+zoom_trance_y,paint);
                }
                paint.setTextAlign(align);
                for(i=0;i<7;i++){
                    mCanvas.drawText(
                            (int)((xmin+(xmax-xmin)/6*(6-i)))+"",
                            text_pos2D.get(i+32+42)[0]*zoom+zoom_trance_x,
                            text_pos2D.get(i+32+42)[1]*zoom+zoom_trance_y,paint);
                }
                if(mode==1 ){
                    for(i=0;i<YStrings.length;i++){
                        mCanvas.drawText(
                                YStrings[i],
                                text_pos2D.get(i+88+28)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+28)[1]*zoom+zoom_trance_y,paint);
                    }
                }else{
                    for(i=0;i<height_level;i++){
                        mCanvas.drawText(
                                (int)((ymin+(ymax-ymin)/6*i))+"",
                                text_pos2D.get(i+88+28)[0]*zoom+zoom_trance_x,
                                text_pos2D.get(i+88+28)[1]*zoom+zoom_trance_y,paint);
                    }
                }
            }

            Log.i("aaaa","画点阵");
            //画点阵
            paint.setColor(0xffdddddd);
            int count=0;
            if((θ%360>-90 && θ%360< 90) || (θ%360>270) || (θ%360<-270)){
                for(float[] a: points2D){
                    paint.setColor((int)pointsCd.get(count)[3]);
                    mCanvas.drawPoint(a[x]*zoom+zoom_trance_x,a[y]*zoom+zoom_trance_y,paint);
                    count++;
                }
            }else{
                for(i=points2D.size()-1;i>=0;i--){
                    paint.setColor((int)pointsCd.get(i)[3]);
                    mCanvas.drawPoint(points2D.get(i)[x]*zoom+zoom_trance_x,points2D.get(i)[y]*zoom+zoom_trance_y,paint);
                }
            }
            Log.i("aaaa","画点信息：drawPointInfoIndex="+drawPointInfoIndex);
            //画点信息
            if(drawPointInfo){
                paint.setColor(0xff777777);
                paint.setStrokeWidth(3);
                float[] point1=null,p=null,pp=null;
                if(mark==0){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(20)[0],lines.get(20)[1],lines.get(30)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(87)[0],lines.get(87)[1],lines.get(88)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(7)[0],lines.get(7)[1],lines.get(12)[0]);
                }else if(mark==1){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(20)[0],lines.get(20)[1],lines.get(30)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(87)[0],lines.get(87)[1],lines.get(88)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(86)[0],lines.get(86)[1],lines.get(87)[0]);
                }else if(mark==2){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(20)[0],lines.get(20)[1],lines.get(30)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(0)[0],lines.get(0)[1],lines.get(6)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(7)[0],lines.get(7)[1],lines.get(12)[0]);
                }else if(mark==3){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(20)[0],lines.get(20)[1],lines.get(30)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(85)[0],lines.get(85)[1],lines.get(86)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(86)[0],lines.get(86)[1],lines.get(87)[0]);
                }else if(mark==4){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(90)[0],lines.get(90)[1],lines.get(91)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(87)[0],lines.get(87)[1],lines.get(88)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(85)[0],lines.get(85)[1],lines.get(88)[0]);
                }else if(mark==5){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(90)[0],lines.get(90)[1],lines.get(91)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(87)[0],lines.get(87)[1],lines.get(88)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(86)[0],lines.get(86)[1],lines.get(87)[0]);
                }else if(mark==6){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(90)[0],lines.get(90)[1],lines.get(91)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(85)[0],lines.get(85)[1],lines.get(86)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(85)[0],lines.get(85)[1],lines.get(88)[0]);
                }else if(mark==7){
                    point1=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(90)[0],lines.get(90)[1],lines.get(91)[0]);
                    p=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(85)[0],lines.get(85)[1],lines.get(86)[0]);
                    pp=findFootPoint(points.get(drawPointInfoIndex),
                            lines.get(86)[0],lines.get(86)[1],lines.get(87)[0]);
                }
                float[] point1_2D={yuandianX+(point1[x]),
                        yuandianY+(point1[y])};
                mCanvas.drawLine(
                        points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x,
                        points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y,
                        point1_2D[0]*zoom+zoom_trance_x,
                        point1_2D[1]*zoom+zoom_trance_y,paint);

                float[] newP={yuandianX+(p[x]),
                        yuandianY+(p[y])};
                mCanvas.drawLine(
                        points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x,
                        points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y,
                        newP[0]*zoom+zoom_trance_x,
                        newP[1]*zoom+zoom_trance_y,paint);

                float[] newPP={yuandianX+(pp[x]),
                        yuandianY+(pp[y])};
                mCanvas.drawLine(
                        points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x,
                        points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y,
                        newPP[0]*zoom+zoom_trance_x,
                        newPP[1]*zoom+zoom_trance_y,paint);
                if(mode==1){

                    paint.setColor(0xff0000ff);
                    paint.setStyle(Paint.Style.FILL);
                    mCanvas.drawRoundRect(
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+10,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+10,
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+250,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+250,
                            10,10,paint);
                    paint.setColor(0xffffffff);
                    mCanvas.drawRoundRect(
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+13,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+13,
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+247,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+247,
                            10,10,paint);

                    paint.setColor(0xff999999);
                    /*mCanvas.drawText(
                            "Index:"+drawPointInfoIndex,
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+73,paint);
*/
                    mCanvas.drawText(
                            infoTextValue+":"+pointsCd.get(drawPointInfoIndex)[2],
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+113,paint);

                    paint.setStrokeWidth(5);
                    if((int)(pointsCd.get(drawPointInfoIndex)[3])==0xffff0000){
                        paint.setColor(0xff00ff00);
                    }else {
                        paint.setColor(0xffff0000);
                    }
                    mCanvas.drawCircle(points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+33,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+33,10,paint);
                    mCanvas.drawCircle(points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y,5,paint);

                    if(m1PointsInfo!=null && !Double.isNaN(m1PointsInfo.get(drawPointInfoIndex)[0])){


                        mCanvas.drawText(
                                "- "+infoTextX+":"+(float)(m1PointsInfo.get(drawPointInfoIndex)[0]),
                                points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                                points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+153,paint);
                        mCanvas.drawText(
                                "- "+infoTextY+":"+(float)(m1PointsInfo.get(drawPointInfoIndex)[1]),
                                points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                                points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+193,paint);
                        mCanvas.drawText(
                                "- "+infoTextZ+":"+(float)(m1PointsInfo.get(drawPointInfoIndex)[2]),
                                points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                                points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+233,paint);
                    }


                    Log.i("aaaa","1111 end drawPointInfo");
                }else{


                    paint.setColor(0xff0000ff);
                    paint.setStyle(Paint.Style.FILL);
                    mCanvas.drawRoundRect(
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+10,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+10,
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+250,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+250,
                            10,10,paint);
                    paint.setColor(0xffffffff);
                    mCanvas.drawRoundRect(
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+13,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+13,
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+247,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+247,
                            10,10,paint);

                    paint.setColor(0xff999999);
                    /*mCanvas.drawText(
                            "Index:"+drawPointInfoIndex,
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+73,paint);

                     */
                    mCanvas.drawText(
                            "- "+infoTextX+":"+pointsCd.get(drawPointInfoIndex)[0],
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+113,paint);
                    mCanvas.drawText(
                            "- "+infoTextY+":"+pointsCd.get(drawPointInfoIndex)[1],
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+153,paint);
                    mCanvas.drawText(
                            "- "+infoTextZ+":"+pointsCd.get(drawPointInfoIndex)[2],
                            points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+23,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+193,paint);

                    paint.setStrokeWidth(5);
                    if((int)(pointsCd.get(drawPointInfoIndex)[3])==0xffff0000){
                        paint.setColor(0xff00ff00);
                    }else {
                        paint.setColor(0xffff0000);
                    }
                    mCanvas.drawCircle(points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x+33,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y+33,10,paint);
                    mCanvas.drawCircle(points2D.get(drawPointInfoIndex)[0]*zoom+zoom_trance_x,
                            points2D.get(drawPointInfoIndex)[1]*zoom+zoom_trance_y,5,paint);
                    Log.i("aaaa","22222 end drawPointInfo");
                }

            }

            lastMark=mark;
            if(showZoomInfo && zoom!=1){
                mCanvas.drawText("Zoom:"+zoom+" x="+(-zoom_trance_x)+" y="+(-zoom_trance_y),
                        30,30,paint);
            }

        }catch (Exception e){

        }finally {
            if (mCanvas != null){
                //释放canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     *
     * @param point ABC三点构成的平面外的一点Point
     * @param pA_inPlane 平面上的点A
     * @param pB_inPlane 平面上的点B
     * @param pC_inPlane 平面上的点c
     * @return point在平面上的投影
     */
    private float[] findFootPoint(float[] point,float[] pA_inPlane,float[] pB_inPlane,float[] pC_inPlane){
        double a=(pB_inPlane[1]-pA_inPlane[1])*(pC_inPlane[2]-pA_inPlane[2])-
                (pC_inPlane[1]-pA_inPlane[1])*(pB_inPlane[2]-pA_inPlane[2]);
        double b=(pC_inPlane[0]-pA_inPlane[0])*(pB_inPlane[2]-pA_inPlane[2])-
                (pB_inPlane[0]-pA_inPlane[0])*(pC_inPlane[2]-pA_inPlane[2]);
        double c=(pB_inPlane[0]-pA_inPlane[0])*(pC_inPlane[1]-pA_inPlane[1])-
                (pC_inPlane[0]-pA_inPlane[0])*(pB_inPlane[1]-pA_inPlane[1]);
        double t=(a*pA_inPlane[0]+b*pA_inPlane[1]+c*pA_inPlane[2]-
                (a*point[0]+b*point[1]+c*point[2]))
                /(a*a+b*b+c*c);
        float xx=(float)(point[0]+a*t);
        float yy=(float)(point[1]+b*t);
        float zz=(float)(point[2]+c*t);
        return new float[]{xx,yy,zz};
    };


    double alpha=0;
    //float dy=0;

    public void rollScreenX(int fangxiang, float degrees){

        //float deg=5;
        double cos_deg=Math.cos(Math.toRadians(degrees));
        double sin_deg=Math.sin(Math.toRadians(degrees));
        if(fangxiang==0){
            γ=γ- degrees;
            for(float[][] line:lines) {
                float y = (float) (line[0][1] * cos_deg - line[0][2] * sin_deg);
                float z = (float) (line[0][1] * sin_deg + line[0][2] * cos_deg);
                line[0][1] = y;
                line[0][2] = z;
                float y1 = (float) (line[1][1] * cos_deg - line[1][2] * sin_deg);
                float z1 = (float) (line[1][1] * sin_deg + line[1][2] * cos_deg);
                line[1][1] = y1;
                line[1][2] = z1;
            }
            for(float[] test_pos:text_pos){
                float y = (float) (test_pos[1] * cos_deg - test_pos[2] * sin_deg);
                float z = (float) (test_pos[1] * sin_deg + test_pos[2] * cos_deg);
                test_pos[1] = y;
                test_pos[2] = z;
            }
            for(float[] point:points){
                float y = (float) (point[1] * cos_deg - point[2] * sin_deg);
                float z = (float) (point[1] * sin_deg + point[2] * cos_deg);
                point[1] = y;
                point[2] = z;
            }
            float yy=(float) (point_low_center[1] * cos_deg - point_low_center[2] * sin_deg);
            float zz=(float) (point_low_center[1] * sin_deg + point_low_center[2] * cos_deg);
            point_low_center[1]=yy;
            point_low_center[2]=zz;
        }else{
            γ=γ+ degrees;
            for(float[][] line:lines) {
                float yy = (float) (line[0][1] * cos_deg + line[0][2] * sin_deg);
                float zz = (float) (line[0][2] * cos_deg - line[0][1] * sin_deg);
                line[0][1] = yy;
                line[0][2] = zz;

                float y1 = (float) (line[1][1] * cos_deg + line[1][2] * sin_deg);
                float z1 = (float) (line[1][2] * cos_deg - line[1][1] * sin_deg);
                line[1][1] = y1;
                line[1][2] = z1;
            }
            for(float[] test_pos:text_pos){
                float yy = (float) (test_pos[1] * cos_deg + test_pos[2] * sin_deg);
                float zz = (float) (test_pos[2] * cos_deg - test_pos[1] * sin_deg);
                test_pos[1] = yy;
                test_pos[2] = zz;
            }
            for(float[] point:points){
                float yy = (float) (point[1] * cos_deg + point[2] * sin_deg);
                float zz = (float) (point[2] * cos_deg - point[1] * sin_deg);
                point[1] = yy;
                point[2] = zz;
            }
            float yy=(float) (point_low_center[1] * cos_deg + point_low_center[2] * sin_deg);
            float zz=(float) (point_low_center[2] * cos_deg - point_low_center[1] * sin_deg);
            point_low_center[1]=yy;
            point_low_center[2]=zz;
        }

        process();
    }

    public void rollScreenX(int fangxiang){
        rollScreenX(fangxiang,degrees);
    }

    int TextLine=20;

    public void roll_Z_axis(int fangxiang, float degrees){
        double cos_deg=Math.cos(Math.toRadians(degrees));
        double sin_deg=Math.sin(Math.toRadians(degrees));
        double dx,dy,dz;
        int xiangxian=0;
        double beta,xita;
        dx = lines.get(43)[1][0] - lines.get(43)[0][0];
        dy = lines.get(43)[1][1] - lines.get(43)[0][1];
        dz = lines.get(43)[1][2] - lines.get(43)[0][2];
        if(dx==0 && dy==0 && dz==0){

        }else if(dx>0 && dy>0 && dz>0){
            xiangxian =1;
        }else if(dx<0 && dy>0 && dz>0){
            xiangxian =2;
        }else if(dx<0 && dy<0 && dz>0){
            xiangxian =3;
        }else if(dx>0 && dy<0 && dz>0){
            xiangxian =4;
        }else if(dx>0 && dy>0 && dz<0){
            xiangxian =5;
        }else if(dx<0 && dy>0 && dz<0){
            xiangxian =6;
        }else if(dx<0 && dy<0 && dz<0){
            xiangxian =7;
        }else if(dx>0 && dy<0 && dz<0){
            xiangxian =8;
        }else if(dx>0 && dy==0 && dz>0){
            xiangxian =9;
        }else if(dx==0 && dy>0 && dz>0){
            xiangxian =10;
        }else if(dx<0 && dy==0 && dz>0){
            xiangxian =11;
        }else if(dx==0 && dy<0 && dz>0){
            xiangxian =12;
        }else if(dx>0 && dy==0 && dz<0){
            xiangxian =13;
        }else if(dx==0 && dy>0 && dz<0){
            xiangxian =14;
        }else if(dx<0 && dy==0 && dz<0){
            xiangxian =15;
        }else if(dx==0 && dy<0 && dz<0){
            xiangxian =16;
        }else if(dx>0 && dy==0 && dz==0){
            xiangxian =17;
        }else if(dx==0 && dy>0 && dz==0){
            xiangxian =18;
        }else if(dx<0 && dy==0 && dz==0){
            xiangxian =19;
        }else if(dx==0 && dy<0 && dz==0){
            xiangxian =20;
        }else if(dx==0 && dy==0 && dz>0){
            xiangxian =21;
        }else if(dx==0 && dy==0 && dz<0){
            xiangxian =22;
        }


        if(dx==0 && dy==0){
            beta=0;
        }else{
            beta=Math.asin(dy/(Math.sqrt(dy*dy+dx*dx)));
            if(Double.isNaN(beta)){
                beta=0;
            }
        }
        //Log.i("aaaa","xiangxian="+xiangxian+" beta="+beta);
        if(xiangxian==1 || xiangxian==5){

        }else if(xiangxian==2 || xiangxian==6){
            if(beta==0){
                beta=3*Math.PI/2;
            }else{
                beta=-beta-Math.PI;
            }

        }else if(xiangxian==3 || xiangxian==7){
            beta=(Math.PI-beta);
        }else if(xiangxian==4 || xiangxian==8){

        }else if(xiangxian==10 ||xiangxian==14){
            beta=3*Math.PI/2;
        }else if(xiangxian==12 ||xiangxian==16){
            beta=Math.PI/2;
        }else if(xiangxian==21){
            //Log.i("aaaa","0a ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
            //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
            if(fangxiang==0){
                θ=θ+degrees;
                //Log.i("aaaa","1111111111a ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
                for(float[][] line:lines){
                    float p0x = (float) (line[0][0] * cos_deg + line[0][1] * sin_deg);
                    float p0y = (float) (line[0][1] * cos_deg - line[0][0] * sin_deg);
                    line[0][0] = p0x;
                    line[0][1] = p0y;
                    float p1x = (float) (line[1][0] * cos_deg + line[1][1] * sin_deg);
                    float p1y = (float) (line[1][1] * cos_deg - line[1][0] * sin_deg);
                    line[1][0] = p1x;
                    line[1][1] = p1y;
                }
                for(float[] point:points){
                    float p0x = (float) (point[0] * cos_deg + point[1] * sin_deg);
                    float p0y = (float) (point[1] * cos_deg - point[0] * sin_deg);
                    point[0] = p0x;
                    point[1] = p0y;
                }
                for(float[] test_pos:text_pos){
                    float p0x = (float) (test_pos[0] * cos_deg + test_pos[1] * sin_deg);
                    float p0y = (float) (test_pos[1] * cos_deg - test_pos[0] * sin_deg);
                    test_pos[0] = p0x;
                    test_pos[1] = p0y;
                }
                float xx=(float) (point_low_center[0] * cos_deg + point_low_center[1] * sin_deg);
                float yy=(float) (point_low_center[1] * cos_deg - point_low_center[0] * sin_deg);
                point_low_center[0]=xx;
                point_low_center[1]=yy;
                //Log.i("aaaa","1111111111b ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
            }else{
                θ=θ-degrees;
                //Log.i("aaaa","222222222222a ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
                for(float[][] line:lines){
                    float p0x = (float) (line[0][0] * cos_deg - line[0][1] * sin_deg);
                    float p0y = (float) (line[0][1] * cos_deg + line[0][0] * sin_deg);
                    line[0][0] = p0x;
                    line[0][1] = p0y;

                    float p1x = (float) (line[1][0] * cos_deg - line[1][1] * sin_deg);
                    float p1y = (float) (line[1][1] * cos_deg + line[1][0] * sin_deg);
                    line[1][0] = p1x;
                    line[1][1] = p1y;
                }
                for(float[] point:points){
                    float p0x = (float) (point[0] * cos_deg - point[1] * sin_deg);
                    float p0y = (float) (point[1] * cos_deg + point[0] * sin_deg);
                    point[0] = p0x;
                    point[1] = p0y;
                }
                for(float[] test_pos:text_pos){
                    float p0x = (float) (test_pos[0] * cos_deg - test_pos[1] * sin_deg);
                    float p0y = (float) (test_pos[1] * cos_deg + test_pos[0] * sin_deg);
                    test_pos[0] = p0x;
                    test_pos[1] = p0y;
                }
                float xx=(float) (point_low_center[0] * cos_deg - point_low_center[1] * sin_deg);
                float yy=(float) (point_low_center[1] * cos_deg + point_low_center[0] * sin_deg);
                point_low_center[0]=xx;
                point_low_center[1]=yy;
                //Log.i("aaaa","222222222222b ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
            }
            //Log.i("aaaa","0b ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
            //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
            process();
            return;
        }


        double cos_beta=Math.cos(beta);
        double sin_beta=Math.sin(beta);
        //Log.i("aaaa","dx="+dx+" dy="+dy+" beta="+beta+" sin(beta)="+sin_beta+" cos(beta)="+cos_beta);
        //Log.i("aaaa","0a ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","0a bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        for(float[][] line:lines){

            float x = (float) (line[0][0] * cos_beta + line[0][1] * sin_beta);
            float y = (float) (line[0][1] * cos_beta - line[0][0] * sin_beta);
            line[0][0] = x;
            line[0][1] = y;

            float x1 = (float) (line[1][0] * cos_beta + line[1][1] * sin_beta);
            float y1 = (float) (line[1][1] * cos_beta - line[1][0] * sin_beta);
            line[1][0] = x1;
            line[1][1] = y1;
        }
        for(float[] point:points){
            float x = (float) (point[0] * cos_beta + point[1] * sin_beta);
            float y = (float) (point[1] * cos_beta - point[0] * sin_beta);
            point[0] = x;
            point[1] = y;
        }
        for(float[] test_pos:text_pos){
            float x = (float) (test_pos[0] * cos_beta + test_pos[1] * sin_beta);
            float y = (float) (test_pos[1] * cos_beta - test_pos[0] * sin_beta);
            test_pos[0] = x;
            test_pos[1] = y;
        }
        float xx=(float) (point_low_center[0] * cos_beta + point_low_center[1] * sin_beta);
        float yy=(float) (point_low_center[1] * cos_beta - point_low_center[0] * sin_beta);
        point_low_center[0]=xx;
        point_low_center[1]=yy;
        //Log.i("aaaa","0b ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","0b bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        ///step 1
        //float dy = lines.get(43)[1][1] - lines.get(43)[0][1];
        //float dz = lines.get(43)[1][2] - lines.get(43)[0][2];
        dx = lines.get(43)[1][0] - lines.get(43)[0][0];
        dz = lines.get(43)[1][2] - lines.get(43)[0][2];
        if(dy==0 && dz==0){
            xita=0;
        }else {
            xita=Math.asin(dx/(Math.sqrt(dx*dx+dz*dz)));
            if(Double.isNaN(xita)){
                xita=0;
            }
        }

        if(xiangxian==1 || xiangxian==2 || xiangxian==3 || xiangxian==4 ||
                xiangxian==9 || xiangxian==10 ||xiangxian==11 ||xiangxian==12
                || xiangxian==21){
            xita=xita;
        }else{
            xita=Math.PI-xita;
        }

        double sin_xita=Math.sin(xita);
        double cos_xita=Math.cos(xita);
        //y轴顺时针转动xita度数
        //Log.i("aaaa","dy="+dy+" dz="+dz+" xita="+xita+" sin(xita)="+cos_xita+" cos(xita)="+sin_xita);
        //Log.i("aaaa","1a ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","1a bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        for (float[][] line:lines){
            float x = (float) (line[0][0] * cos_xita - line[0][2] * sin_xita);
            float z = (float) (line[0][2] * cos_xita + line[0][0] * sin_xita);
            line[0][0] = x;
            line[0][2] = z;

            float x1 = (float) (line[1][0] * cos_xita - line[1][2] * sin_xita);
            float z1 = (float) (line[1][2] * cos_xita + line[1][0] * sin_xita);
            line[1][0] = x1;
            line[1][2] = z1;
        }
        for(float[] point:points){
            float x = (float) (point[0] * cos_xita - point[2] * sin_xita);
            float z = (float) (point[2] * cos_xita + point[0] * sin_xita);
            point[0] = x;
            point[2] = z;
        }
        for(float[] test_pos:text_pos){
            float x = (float) (test_pos[0] * cos_xita - test_pos[2] * sin_xita);
            float z = (float) (test_pos[2] * cos_xita + test_pos[0] * sin_xita);
            test_pos[0] = x;
            test_pos[2] = z;
        }
        xx=(float) (point_low_center[0] * cos_xita - point_low_center[2] * sin_xita);
        float zz=(float) (point_low_center[2] * cos_xita + point_low_center[0] * sin_xita);
        point_low_center[0]=xx;
        point_low_center[2]=zz;
        //Log.i("aaaa","1b ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","1b bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");

        //step 2
        //z轴旋转
        //Log.i("aaaa","cos_deg="+cos_deg+" sin_deg="+sin_deg);
        //Log.i("aaaa","2a ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","2a bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");


        if(fangxiang==0){
            θ=θ+degrees;
            //Log.i("aaaa","1111111111 ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
            for(float[][] line:lines){
                float p0x = (float) (line[0][0] * cos_deg + line[0][1] * sin_deg);
                float p0y = (float) (line[0][1] * cos_deg - line[0][0] * sin_deg);
                line[0][0] = p0x;
                line[0][1] = p0y;
                float p1x = (float) (line[1][0] * cos_deg + line[1][1] * sin_deg);
                float p1y = (float) (line[1][1] * cos_deg - line[1][0] * sin_deg);
                line[1][0] = p1x;
                line[1][1] = p1y;
            }
            for(float[] point:points){
                float p0x = (float) (point[0] * cos_deg + point[1] * sin_deg);
                float p0y = (float) (point[1] * cos_deg - point[0] * sin_deg);
                point[0] = p0x;
                point[1] = p0y;
            }
            for(float[] test_pos:text_pos){
                float p0x = (float) (test_pos[0] * cos_deg + test_pos[1] * sin_deg);
                float p0y = (float) (test_pos[1] * cos_deg - test_pos[0] * sin_deg);
                test_pos[0] = p0x;
                test_pos[1] = p0y;
            }
            xx=(float) (point_low_center[0] * cos_deg + point_low_center[1] * sin_deg);
            yy=(float) (point_low_center[1] * cos_deg - point_low_center[0] * sin_deg);
            point_low_center[0]=xx;
            point_low_center[1]=yy;
        }else{
            θ=θ-degrees;
            //Log.i("aaaa","222222222222 ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
            for(float[][] line:lines){
                float p0x = (float) (line[0][0] * cos_deg - line[0][1] * sin_deg);
                float p0y = (float) (line[0][1] * cos_deg + line[0][0] * sin_deg);
                line[0][0] = p0x;
                line[0][1] = p0y;

                float p1x = (float) (line[1][0] * cos_deg - line[1][1] * sin_deg);
                float p1y = (float) (line[1][1] * cos_deg + line[1][0] * sin_deg);
                line[1][0] = p1x;
                line[1][1] = p1y;
            }
            for(float[] point:points){
                float p0x = (float) (point[0] * cos_deg - point[1] * sin_deg);
                float p0y = (float) (point[1] * cos_deg + point[0] * sin_deg);
                point[0] = p0x;
                point[1] = p0y;
            }
            for(float[] test_pos:text_pos){
                float p0x = (float) (test_pos[0] * cos_deg - test_pos[1] * sin_deg);
                float p0y = (float) (test_pos[1] * cos_deg + test_pos[0] * sin_deg);
                test_pos[0] = p0x;
                test_pos[1] = p0y;
            }
            xx=(float) (point_low_center[0] * cos_deg - point_low_center[1] * sin_deg);
            yy=(float) (point_low_center[1] * cos_deg + point_low_center[0] * sin_deg);
            point_low_center[0]=xx;
            point_low_center[1]=yy;
        }

        //Log.i("aaaa","2b ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","2b bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        //step 3

        //Log.i("aaaa","3a ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","3a bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        for (float[][] line:lines){
            float x = (float) (line[0][0] * cos_xita + line[0][2] * sin_xita);
            float z = (float) (line[0][2] * cos_xita - line[0][0] * sin_xita);
            line[0][0] = x;
            line[0][2] = z;

            float x1 = (float) (line[1][0] * cos_xita + line[1][2] * sin_xita);
            float z1 = (float) (line[1][2] * cos_xita - line[1][0] * sin_xita);
            line[1][0] = x1;
            line[1][2] = z1;
        }
        for(float[] point:points){
            float x = (float) (point[0] * cos_xita + point[2] * sin_xita);
            float z = (float) (point[2] * cos_xita - point[0] * sin_xita);
            point[0] = x;
            point[2] = z;
        }
        for(float[] text_point:text_pos){
            float x = (float) (text_point[0] * cos_xita + text_point[2] * sin_xita);
            float z = (float) (text_point[2] * cos_xita - text_point[0] * sin_xita);
            text_point[0] = x;
            text_point[2] = z;
        }
        xx=(float) (point_low_center[0] * cos_xita + point_low_center[2] * sin_xita);
        zz=(float) (point_low_center[2] * cos_xita - point_low_center[0] * sin_xita);
        point_low_center[0]=xx;
        point_low_center[2]=zz;
        //Log.i("aaaa","3b ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","3b bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        //step 4
        //Log.i("aaaa","4a ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","4a bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");
        for(float[][] line:lines){
            float x = (float) (line[0][0] * cos_beta - line[0][1] * sin_beta);
            float y = (float) (line[0][1] * cos_beta + line[0][0] * sin_beta);
            line[0][0] = x;
            line[0][1] = y;

            float x1 = (float) (line[1][0] * cos_beta - line[1][1] * sin_beta);
            float y1 = (float) (line[1][1] * cos_beta + line[1][0] * sin_beta);
            line[1][0] = x1;
            line[1][1] = y1;
        }
        for(float[] point:points){
            float x = (float) (point[0] * cos_beta - point[1] * sin_beta);
            float y = (float) (point[1] * cos_beta + point[0] * sin_beta);
            point[0] = x;
            point[1] = y;
        }
        for(float[] test_pos:text_pos){
            float x = (float) (test_pos[0] * cos_beta - test_pos[1] * sin_beta);
            float y = (float) (test_pos[1] * cos_beta + test_pos[0] * sin_beta);
            test_pos[0] = x;
            test_pos[1] = y;
        }
        xx=(float) (point_low_center[0] * cos_beta - point_low_center[1] * sin_beta);
        yy=(float) (point_low_center[1] * cos_beta + point_low_center[0] * sin_beta);
        point_low_center[0]=xx;
        point_low_center[1]=yy;
        //Log.i("aaaa","4b ("+lines.get(TextLine)[0][0]+","+lines.get(TextLine)[0][1]+","+lines.get(TextLine)[0][2]+")," +
        //        "("+lines.get(TextLine)[1][0]+","+lines.get(TextLine)[1][1]+","+lines.get(TextLine)[1][2]+")");
        //Log.i("aaaa","4b bbbb t ("+text_pos.get(0)[0]+","+text_pos.get(0)[1]+","+text_pos.get(0)[2]+")");

        process();

    }

    private void process(){
        //int n;
        /*if(viewHeight<viewWidth){
            n=viewHeight/17;
        }else{
            n=viewWidth/17;
        }*/
        //int m=n/2;
        //int m=n;
        shiftx=viewWidth/2;
        //coordinateInit();
        float yuandianX=shiftx;
        float yuandianY=ZHeight/2+zmin*kz;
        float Txmax=xmax*kx;
        float Tymax=ymax*ky;
        if(!isAnimationRunning){
            points2D.clear();
            //Log.i("aaaa","point size="+points.size());
            for(float[] p:points){
                float[] newP;
                if(mode==1){
                    /*newP=new float[]{yuandianX+(p[x]),
                            yuandianY+(p[y])+(float)((-p[z]))};*/
                    newP=new float[]{yuandianX+(p[x]),
                            yuandianY+(p[y])};
                }else{
                    /*newP=new float[]{yuandianX+(p[x]),
                            yuandianY+(p[y])+(float)((-p[z]))};*/
                    newP=new float[]{yuandianX+(p[x]),
                            yuandianY+(p[y])};
                }

                points2D.add(newP);
            }

            lines2D.clear();
            /*if(lines.get(41)[1][1]-lines.get(41)[0][1]<0){
                for(int i=0;i<=6;i++){
                    lines.get(i)[0][x]=lines.get(27+i)[1][x];
                    lines.get(i)[0][y]=lines.get(27+i)[1][y];
                    lines.get(i)[1][x]=lines.get(27+i)[1][x];
                    lines.get(i)[1][y]=lines.get(27+i)[1][y];
                }

            }
            if(lines.get(41)[1][1]-lines.get(41)[0][1]>0){
                for(int i=0;i<=6;i++){
                    lines.get(i)[0][x]=lines.get(27+i)[0][x];
                    lines.get(i)[0][y]=lines.get(27+i)[0][y];
                    lines.get(i)[1][x]=lines.get(27+i)[0][x];
                    lines.get(i)[1][y]=lines.get(27+i)[0][y];
                }
            }*/
            for(float[][] line:lines){

                /*float[][] line2D={
                        {yuandianX+(line[0][x]),
                                yuandianY+(line[0][y])+(float)((-line[0][z]))},
                        {yuandianX+line[1][x],
                                yuandianY+(line[1][y])+(float)((-line[1][z]))}
                };*/
                float[][] line2D={
                        {yuandianX+(line[0][x]),
                                yuandianY+(line[0][y])},
                        {yuandianX+line[1][x],
                                yuandianY+(line[1][y])}
                };
                lines2D.add(line2D);
            }

            text_pos2D.clear();
            for(float[] pos:text_pos){
                /*float[] newPos={yuandianX+(pos[x]),
                        yuandianY+(pos[y])+(float)((-pos[z]))};*/
                float[] newPos={yuandianX+(pos[x]),
                        yuandianY+(pos[y])};
                text_pos2D.add(newPos);
            }

            if(mode==1){
                for(float[] pos:YStringsPos){
                    /*float[] newPos={yuandianX+(pos[x]),
                            yuandianY+(pos[y])+(float)((-pos[z]))};*/
                    float[] newPos={yuandianX+(pos[x]),
                            yuandianY+(pos[y])};
                    YStringsPos2D.add(newPos);
                }
            }
            drawSomething();
        }
    }

    public void roll_Z_axis(int fangxiang){
        roll_Z_axis(fangxiang,5);
    }

    float startX=0,startY=0;
    float LastX=0,LastY=0;
    float θ=0,γ=0;
    double pointerLen=0;
    @Override
    synchronized public boolean onTouch(View v, MotionEvent event) {
        Log.i("bbbb"," event.getAction()="+event.getAction());
        if(event.getAction()==MotionEvent.ACTION_POINTER_2_DOWN){
            if(event.getPointerCount()==2){
                float p0x=event.getX(0);
                float p0y=event.getY(0);
                float p1x=event.getX(1);
                float p1y=event.getY(1);
                pointerLen=Math.sqrt((p0x-p1x)*(p0x-p1x)+(p0y-p0y)*(p0y-p1y));

            }
        }else if(event.getAction()==MotionEvent.ACTION_DOWN){

            startX=event.getX();
            startY=event.getY();
            LastX=startX;
            LastY=startY;
            try{
                double len=0;
                drawPointInfoIndex=0;
//                Log.i("lllll","startx="+startX+" starty="+startY+
//                        " 0x="+(points2D.get(0)[0]*zoom+zoom_trance_x)+
//                        " 0y="+(points2D.get(0)[1]*zoom+zoom_trance_y));
                if(points2D.size()>0){
                    float rs=(startX-(points2D.get(0)[0]*zoom+zoom_trance_x))*
                            (startX-(points2D.get(0)[0])*zoom+zoom_trance_x)
                            +
                            (startY-(points2D.get(0)[1]*zoom+zoom_trance_y))*
                                    (startY-(points2D.get(0)[1]*zoom+zoom_trance_y));
                    //Log.i("lllll","111 rs="+rs);
                    len=Math.sqrt(Math.abs(rs));
                    if(Double.isNaN(len)){
                        //Log.i("lllll","find index=0 len=NaN");
                        len=0;
                    }
                }
                int count=0;
                for(float[] point:points2D){
                    float rs=(startX-(point[0]*zoom+zoom_trance_x))*
                            (startX-(point[0]*zoom+zoom_trance_x))+
                            (startY-(point[1]*zoom+zoom_trance_y))*
                                    (startY-(point[1]*zoom+zoom_trance_y));
//                    if(Double.isNaN(rs)){
//                        Log.i("lllll","222 rs="+rs);
//                    }

                    double m=Math.sqrt(Math.abs(rs));
//                    if(Double.isNaN(m)){
//                        Log.i("lllll","startx="+startX+" starty="+startY+
//                                " 0x="+(points2D.get(count)[0]*zoom+zoom_trance_x)+
//                                " 0y="+(points2D.get(count)[1]*zoom+zoom_trance_y));
//                        Log.i("lllll","find index="+count+" len=NaN");
//                        m=0;
//                    }
                    if(m<len){
                        drawPointInfoIndex=count;
                        len=m;
                    }
                    count++;
                }
                Log.i("lllll","find index="+drawPointInfoIndex+" len="+len);
                if(len<150){
                    drawPointInfo=true;
                }else{
                    drawPointInfo=false;
                }
                drawSomething();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(event.getAction()==MotionEvent.ACTION_MOVE) {
            if(event.getPointerCount()==2){
                float p0x=event.getX(0);
                float p0y=event.getY(0);
                float p1x=event.getX(1);
                float p1y=event.getY(1);
                double len=Math.sqrt((p0x-p1x)*(p0x-p1x)+(p0y-p0y)*(p0y-p1y));
                if(len>pointerLen){
                    setZoom(getZoom()+0.03f);
                }else if(len<pointerLen){
                    if(getZoom()>0.2){
                        setZoom(getZoom()-0.03f);
                    }
                }
                pointerLen=len;
                return true;
            }
            if(isAnimationRunning){
                return true;
            }
            if(draggable){
                zoom_trance_x=zoom_trance_x+event.getX()-LastX;
                zoom_trance_y=zoom_trance_y+event.getY()-LastY;
                LastX=event.getX();
                LastY=event.getY();
                drawSomething();
                return true;
            }
            try {
                if(Math.abs(event.getX()-LastX)>Math.abs(event.getY()-LastY)){

                    if(event.getX()-LastX>5){
                        LastX=event.getX();
                        LastY=event.getY();

                        roll_Z_axis(1);
                    }else if(event.getX()-LastX<-5){
                        LastX=event.getX();
                        LastY=event.getY();

                        roll_Z_axis(0);
                    }

                }else{
                    if(event.getY()-LastY>5){
                        LastY=event.getY();
                        LastX=event.getX();

                        if(γ-degrees<-180){

                        }else{
                            rollScreenX(0);
                        }

                    }else if(event.getY()-LastY<-5){
                        LastY=event.getY();
                        LastX=event.getX();
                        if(mode==1){
                            if(γ+degrees>-90){

                            }else{
                                rollScreenX(1);
                            }
                        }else{
                            if(γ+degrees>0){

                            }else{
                                rollScreenX(1);
                            }
                        }


                    }
                }
            } catch (Exception e) {

            }
        }

        return true;
    }

    interface XYZ_To_Value{
        public float xyzToValue(float x, float y, float z);
    }
}