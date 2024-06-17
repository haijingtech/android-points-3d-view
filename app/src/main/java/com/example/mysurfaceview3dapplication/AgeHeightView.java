package com.example.mysurfaceview3dapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class AgeHeightView extends View {
    String TAG="AgeHeightView";
    public AgeHeightView(Context context) {
        super(context);
    }

    public AgeHeightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG,"AgeHeightView v2");
    }

    public AgeHeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG,"AgeHeightView v3");
    }

    public AgeHeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.i(TAG,"AgeHeightView v4");
    }

    public ArrayList<People> peoples=new ArrayList<>();
    public void setPeoplesToView(ArrayList<People> peoples){
        Log.i(TAG,"setPeoplesToView");
        this.peoples=peoples;
        invalidate();
    }

    int sex=0;//0女  1男
    int bgColor=0XFF000000;
    int bgNetColor=0Xff444444;
    int sdLinesColor=0xffFAC011;
    int xColor=0XFFFFFFFF;
    int yColor=0XFFFFFFFF;
    int xTextColor=0XFFFFFFFF;
    int yTextColor=0XFFFFFFFF;
    boolean isShowSelectedInfo =false;
    //float[] selectedPoint=new float[]{-100,-100,0xffff0000};
    People selectedPeople;
    AgeHeight selectedAgeHeight;
    String[] xValueText={"1","2","3","4","5","6","7","8","9","10",
            "11","12","13","14","15","16","17","18"};
    String[] yValueText={"30","40","50","60","70","80","90","100","110","120","130",
            "140","150","160","170","180","190"};


    double[] mS =new double[]{50,79.7,88.6,96.0,101.2,110.0,115.6,122.0,127.8,131.8,138.4,145.7,
            153.1,160.3,165.2,168.0,173.9,169.2,170.0};
    double[] msd=new double[]{3,3.6,3.9,3.5,4.1,4,5.5,4.9,4.9,6.8,6.5,8,7.9,7.6,6.5,5.4,5.2,5.8,5.7};

    double[] fS =new double[]{50,77.2,87.5,96.1,102.3,109.3,114.8,119.6,125.7,134.4,140.6,146.6,
            150.1,154.6,154.9,158.2,156.6,154.8,157.4};
    double[] fsd=new double[]{3,5.9,3.4,4.7,5.2,4.7,5.9,6.1,4.5,5.8,7.7,6.6,5.7,5.9,5.1,5.1,3.6,5.7,6};


    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    Paint paint=new Paint();

    /**
     *
     * @param sex 0女 1男
     */
    public void setSex(int sex){
        this.sex=sex;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            float x=event.getX();
            float y=event.getY();
            //计算测定点
            float minLen=Float.MAX_VALUE;
            for(People people:peoples){
                for(AgeHeight ageHeight:people.myRecords){
                    float l=(ageHeight.x-x)*(ageHeight.x-x)+(ageHeight.y-y)*(ageHeight.y-y);
                    if(l<minLen){
                        minLen=l;
                        selectedPeople=people;
                        selectedAgeHeight=ageHeight;
                        isShowSelectedInfo =true;
                    }
                }
            }
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    int boardShowIndex=0;

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG,"onDraw");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(boardShowIndex<peoples.size()-1){
                    boardShowIndex++;
                }else {
                    boardShowIndex=0;
                }
                invalidate();
            }
        },5000);

        super.onDraw(canvas);
        canvas.drawColor(bgColor);//画背景
        paint.setAntiAlias(true);
        int dy=(int) (canvas.getHeight()/18.8);//获取canvas高度
        int dx=canvas.getWidth()/21;//获取canvas高度

        //画x轴
        paint.setColor(xColor);
        int startX=dx;
        int startY=canvas.getHeight()-dy;
        int stopX=canvas.getWidth()-dx;
        int stopY=canvas.getHeight()-dy;
        canvas.drawLine(startX,startY,stopX,stopY,paint);
        paint.setColor(yColor);
        //画y轴
        startX=dx;
        startY=canvas.getHeight()-dy;
        stopX=dx;
        stopY=dy;
        //Log.i(TAG,"startX="+startX+" startY="+startY+" stopX="+stopX+" stopY="+stopY);
        canvas.drawLine(startX,startY,stopX,stopY,paint);

        //画文字
        TextPaint tp=new TextPaint();
        tp.setColor(xTextColor);
        tp.setTextSize(dx/3);
        tp.setAntiAlias(true);
        tp.setStyle(Paint.Style.FILL);
        int index=0;

        StaticLayout charsLayout = new StaticLayout("好", tp,
                (int)StaticLayout.getDesiredWidth("好",tp),
                Layout.Alignment.ALIGN_NORMAL, 1F, 0.1F, true);
        int HeightOfBottomToBaseLine=charsLayout.getLineBottom(0)-charsLayout.getLineBaseline(0);

        for(String s:xValueText){
            float cw=StaticLayout.getDesiredWidth(s,tp);
            canvas.drawText(s,(2*dx)+(index*dx)-cw/2 ,canvas.getHeight()-HeightOfBottomToBaseLine,tp);
            index++;
        }
        canvas.drawText("年齢(year)",
                (2*dx)+(index*dx)-StaticLayout.getDesiredWidth("年齢(year)",tp)/2,
                canvas.getHeight()-HeightOfBottomToBaseLine,tp);


        tp.setColor(yTextColor);
        index=0;
        for(String s:yValueText){
            canvas.drawText(s,5 ,(canvas.getHeight()-dy)-index*dy+5,tp);
            index++;
        }
        canvas.drawText("身長(cm)",dx/2 ,(canvas.getHeight()-dy)-index*dy-5,tp);

        //画背景网格
        paint.setColor(bgNetColor);
        paint.setStrokeWidth(0);
        //1.生成网格点并绘制
        int m=18*12;
        for(int i=1;i<m;i++){
            for(int j=1;j<17;j++){
                int x=(int)(dx+dx*1f/12*i);
                int y=canvas.getHeight()-dy-dy*j;
                canvas.drawLine(x-1,y,x+1,y,paint);
            }
        }
        for(int i=0;i<18;i++){
            canvas.drawLine(2*dx+i*dx,canvas.getHeight()-dy,2*dx+i*dx,dy,paint);
        }

        //绘制曲线数据
        Paint p=new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(0xFFFF0000);
        double[] S;
        double[] SD;
        if(sex==0){
            S=fS;
            SD=fsd;
        }else {
            S=mS;
            SD=msd;
        }
        path(canvas,dx,dy,S,SD,0,p,tp,"平均");
        path(canvas,dx,dy,S,SD,1,p,tp,"+1.0SD");
        path(canvas,dx,dy,S,SD,2,p,tp,"+2.0SD");
        path(canvas,dx,dy,S,SD,3,p,tp,"+3.0SD");
        path(canvas,dx,dy,S,SD,-1,p,tp,"-1.0SD");
        path(canvas,dx,dy,S,SD,-2,p,tp,"-2.0SD");
        path(canvas,dx,dy,S,SD,-2.5f,p,tp,"-2.5SD");
        path(canvas,dx,dy,S,SD,-3,p,tp,"-3.0SD");

        paint.setStyle(Paint.Style.STROKE);
        for(People people:peoples){
            paint.setColor(people.color);
            Log.i(TAG,"color1="+people.color);
            Path path=new Path();
            float sX=people.myRecords.get(0).month*dx/12+dx;
            float sY=canvas.getHeight()-((people.myRecords.get(0).height-30)*dy/10+dy);
            path.moveTo(sX,sY);
            for(AgeHeight ageHeight:people.myRecords){
                ageHeight.x=ageHeight.month*dx/12+dx;
                ageHeight.y=canvas.getHeight()-((ageHeight.height-30)*dy/10+dy);
                path.lineTo(ageHeight.x,ageHeight.y);
                canvas.drawPath(path,paint);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        Log.i(TAG,"StrokeWidth="+paint.getStrokeWidth());

        //绘制被选中的点
        if(isShowSelectedInfo){
            Log.i(TAG,"color="+selectedPeople.color);
            drawSelected(canvas,selectedAgeHeight.x,selectedAgeHeight.y,selectedPeople.color,
                    selectedPeople, selectedAgeHeight.date, selectedAgeHeight.height,
                    selectedAgeHeight.height-3f);
        }else {
            for(People people:peoples){
                try {
                    drawSelected(canvas,people.myRecords.get(people.myRecords.size()-1).x,
                            people.myRecords.get(people.myRecords.size()-1).y, people.color,people,
                            people.myRecords.get(people.myRecords.size()-1).date,
                            people.myRecords.get(people.myRecords.size()-1).height,
                            people.myRecords.get(people.myRecords.size()-1).height-3);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    private void path(Canvas canvas,float dx,float dy,double[] S,double[] SD,float sdTime,
                      Paint p,TextPaint tp,String text){
        p.setStyle(Paint.Style.STROKE);
        p.setColor(sdLinesColor);
        Path path=new Path();
        float y;
        path.moveTo(dx,canvas.getHeight()-(float)((50+sdTime*SD[0]-30)*dy/10+dy));
        //Log.i(TAG,"yyy="+(canvas.getHeight()-(float)(50+sdTime*SD[0]-30)*dy/10+dy));
        float lastX=dx,lastY=canvas.getHeight()-((float)(50+sdTime*SD[0]-30)*dy/10+dy);
        for(int i=0;i<S.length;i++){
            float d=(float)S[i];
            y=canvas.getHeight()-(float)((d+sdTime*SD[i]-30)*dy/10+dy);
            //Log.i(TAG,"d="+d+" yyy1="+(canvas.getHeight()-(float)((d+sdTime*SD[i]-30)*dy/10+dy)));
            path.quadTo((i*dx+dx+(lastX))/2,(y+lastY)/2,i*dx+dx,y);
            lastX=i*dx+dx;
            lastY=y;
        }

        if(sdTime==-2.5){
            sdTime=-3;
        }else if(sdTime==-3){
            sdTime=-4;
        }
        path.lineTo(lastX+dx/3,lastY-sdTime*dy/3);
        path.lineTo(lastX+1.1f*dx,lastY-sdTime*dy/3);
        canvas.drawPath(path,p);
        p.setColor(0xFFFFFFFF);
        p.setTextSize(20);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        float textSize=tp.getTextSize();
        tp.setTextSize(textSize*2/3);

        canvas.drawText(text,lastX+dx/2,lastY-sdTime*dy/3-3,tp);
        tp.setTextSize(textSize);
    }

    private void drawSelected(Canvas canvas,float pointX,float pointY,int color,People selectedPeople,
                              String slctDate,float slctHeight,float standerHeight){
        Log.i(TAG,"color3="+color);
        float radium=40;//头像圆形半径
        paint.setStrokeWidth(5);
        paint.setColor(color);
        canvas.drawCircle(pointX,pointY,5,paint);
        paint.setStrokeWidth(1);
        //绘制三角形
        Path path = new Path();
        path.moveTo(pointX, pointY);
        path.lineTo(pointX+20, pointY+50);
        path.lineTo(pointX+80, pointY+50);
        path.close();
        canvas.drawPath(path, paint);
        //绘制圆形bitmap
        canvas.drawCircle(pointX+50,pointY+60,radium,paint);
        Bitmap bitmap=selectedPeople.bitmap;
        Matrix matrix=new Matrix();
        matrix.postScale(radium*2/bitmap.getWidth(),radium*2/bitmap.getHeight());
        matrix.postTranslate(pointX+50-radium,pointY+60-radium);
        canvas.drawBitmap(getRoundBitmap(bitmap),matrix,paint);

        //绘制信息框
        if(peoples.get(boardShowIndex).equals(selectedPeople)){
            float l=radium*radium/2;
            drawBoard(pointX+60+(float)Math.sqrt(l),pointY+60+(float)Math.sqrt(l),25,
                    new String[]{"出身年月","測定日","身長","標準値","SDスコア"},
                    new String[]{selectedPeople.birthday, slctDate,slctHeight+"cm",
                            standerHeight+"cm","0.5SD"},color,canvas);
        }

    }

    private void drawBoard(float x, float y, float textSize, String[] keys,String[] values,
                           int color,Canvas canvas){
        Log.i(TAG,"color4="+color);
        TextPaint paint=new TextPaint();
        paint.setTextSize(textSize);

        float keyMaxLength=0;
        String keyMaxLen="";
        for(String s:keys){
            float len=StaticLayout.getDesiredWidth(s, paint);
            if(len>keyMaxLength){
                keyMaxLength=len;
                keyMaxLen=s;
            }
        }
        float valueMaxLength=0;
        String valueMaxLen="";
        for(String s:values){
            float len=StaticLayout.getDesiredWidth(": "+s, paint);
            if(len>valueMaxLength){
                valueMaxLength=len;
                valueMaxLen=s;
            }
        }
        String maxLen=keyMaxLen+": "+valueMaxLen;
        float maxLength=StaticLayout.getDesiredWidth(keyMaxLen+": "+valueMaxLen, paint);

        Log.i(TAG,"keyMaxLength="+keyMaxLength+" valueMaxLength="+valueMaxLength+" maxLength="+maxLength);

        //Paint paint=new Paint();
        paint.setAntiAlias(true);
        //paint.setTextSize(textSize);

        float cw=StaticLayout.getDesiredWidth(maxLen, paint);

        StaticLayout charsLayout = new StaticLayout("好", paint, (int)cw,
                Layout.Alignment.ALIGN_NORMAL, 1F, 0.1F, true);
        int HeightOfBottomToBaseLine=charsLayout.getLineBottom(0)-charsLayout.getLineBaseline(0);
        int cH=charsLayout.getHeight();
        //float width=cw+40+textSize;
        float width=maxLength+20;
        float boardHeight=(cH+6)*keys.length;

        if(y+boardHeight>canvas.getHeight()){
            y=y-(y+boardHeight-canvas.getHeight());
        }

        if(x+width>canvas.getWidth()){
            x=x-(x+width-canvas.getWidth());
        }

        //float width=maxLength*cw;
        paint.setColor(0XFFFFFFFF);
        canvas.drawRoundRect(x,y,x+width,y+boardHeight,20,20,paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(color);
        canvas.drawRoundRect(x,y,x+width,y+boardHeight,20,20,paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        int line=0;

        for(String s:keys){
            paint.setColor(0Xff444444);
            drawScaledText(canvas,s,x+10,y+(line+1)*cH+6,keyMaxLength,paint);
            paint.setColor(color);
            canvas.drawLine(x+10,y+(line+1)*cH+HeightOfBottomToBaseLine+3,
                    x+10+keyMaxLength,y+(line+1)*cH+HeightOfBottomToBaseLine+3,paint);
            line++;
        }
        line=0;
        for(String s:values){
            paint.setColor(0Xff444444);
            canvas.drawText(": "+s, x+10+keyMaxLength, y+(line+1)*cH+6, paint);
            paint.setColor(color);
            canvas.drawLine(x+10+keyMaxLength,y+(line+1)*cH+HeightOfBottomToBaseLine+3,
                    x+10+keyMaxLength+valueMaxLength,y+(line+1)*cH+HeightOfBottomToBaseLine+3,paint);
            line++;
        }
    }

    private void drawScaledText(Canvas canvas, String line, float startX, float baseLineY,
                                float lineWidth,TextPaint textPaint) {
        if (line.length() < 1) {
            return;
        }
        textPaint.setStrokeWidth(0);
        textPaint.setStyle(Paint.Style.FILL);
        //float x = getPaddingLeft();
        boolean forceNextLine = line.charAt(line.length() - 1) == 10;
        int length = line.length() - 1;
        if (forceNextLine || length == 0) {
            canvas.drawText(line, startX, baseLineY, textPaint);
            return;
        }

        String lastC = String.valueOf(line.charAt(line.length()-1));
        float lastCw = StaticLayout.getDesiredWidth(lastC, textPaint);

        float d = (lineWidth-lastCw)/ length;
        //Log.i(TAG,"lineWidth="+lineWidth+" lastCw="+lastCw+" length="+length+" d="+d);

        for (int i = 0; i < line.length(); ++i) {
            String c = String.valueOf(line.charAt(i));
            canvas.drawText(c, startX, baseLineY, textPaint);
            startX += d;
        }
    }

    private static Bitmap getRoundBitmap(Bitmap bitmap) {
        //依据原有的图片丶创建一个新的图片   格式是：Config.ARGB_4444
        Bitmap bt = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        //创建一个画布
        Canvas canvas = new Canvas(bt);
        //创建一个画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //画笔的颜色
        paint.setColor(Color.WHITE);
        //画布的格式默认为  零
        canvas.drawARGB(0, 0, 0, 0);
        //求得圆的半径
        float radius = Math.min(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
        //重置画笔
        paint.reset();
        //调用截图图层的方法
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //画图片
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bt;
    }

}
