package com.bonlala.fitalent.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bonlala.fitalent.bean.ChartSpo2Bean;
import com.bonlala.widget.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * 计步图表，可左右滑动
 * Created by Admin
 * Date 2020/7/17
 */
public class CustomerBpScrollView extends View {

    private static final String TAG = "CustomerScrollView";


    private float originX; // 原点x坐标

    private float originY; // 原点y坐标

    private float firstPointX; //第一个点x坐标

    private float firstMinX; // 移动时第一个点的最小x值

    private float firstMaxX; //移动时第一个点的最大x值

    private float intervalX = 140; // 坐标刻度的间隔

    private float intervalY = 80; // y轴刻度的间隔

    private float minValueY; // y轴最小值

    private float maxValueY = 0; // y轴最大值

    private float mWidth; // 控件宽度

    private float mHeight; // 控件高度

    private int paddingTop = 80;// 默认上下左右的padding

    private int paddingLeft = 20;

    private int paddingRight = 80;

    private int paddingDown = 80;

    private int scaleHeight = 10; // x轴刻度线高度

    private int textToXYAxisGap = 20; // xy轴的文字距xy线的距离

    private float leftRightExtra = intervalX / 3; //x轴左右向外延伸的长度

    private int lableCountY = 5; // Y轴刻度个数



    private List<String> xValues = new ArrayList<>();   //x轴数据

    private int backGroundColor = Color.TRANSPARENT; // view的背景颜色

    private int xVSize = 0; //x轴的数据大小，也即是数据源的大小

    //截取超出X轴坐标的画笔
    private Paint  paintBack;

    //间隔
    private float mCurrentWidth = dp2px(20);


    //绘制X轴数据
    private Paint xPaint;

    //绘制柱子的画笔
    private Paint mBarPaint;

    //无数据时的画笔
    private Paint emptyPaint;

    //连线的Path
    private Path linPath;

    //数据源
    private List<Map<String,Integer>> sourceList = new ArrayList<>();
    //数据源中的最大值
    private int maxValue;

    //手势识别
    private GestureDetector gestureDetector;


    private List<ChartSpo2Bean> chartSpo2BeanList;


    //Y轴刻度的画笔
    private Paint ySchedulePaint;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            firstPointX = paddingLeft;

            if(firstPointX == firstMinX){
                return;
            }else{
                firstMinX = mWidth - originX - (xValues.size() - 1) * intervalX - leftRightExtra;
                // 滑动时，第一个点x值最大为paddingLeft，在大于这个值就不能滑动了
                firstMaxX = firstPointX;
            }

            Log.e(TAG, "-----firstPointX=" + firstPointX + "---firstMinX=" + firstMinX + "---firstMaxX=" + firstMaxX+"---mWidth="+mWidth+"--paddingLeft="+paddingLeft+"---=intervalX="+intervalX);
            setBackgroundColor(backGroundColor);

        }
    };



    public CustomerBpScrollView(Context context) {
        super(context);
        initPaint(context);
        gestureDetector = new GestureDetector(context, new CusOnGestureListener());
    }

    public CustomerBpScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
        gestureDetector = new GestureDetector(context, new CusOnGestureListener());
    }

    public CustomerBpScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
        gestureDetector = new GestureDetector(context, new CusOnGestureListener());
    }

    private void initPaint(Context context) {

        //X轴数据
        xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xPaint.setColor(Color.parseColor("#FFDBDBDB"));
        xPaint.setAntiAlias(true);
        xPaint.setStrokeWidth(MiscUtil.dipToPx(context,1f));

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(Color.parseColor("#FFFC4242"));
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setStrokeWidth(MiscUtil.dipToPx(context,5f));
        mBarPaint.setTextSize(dp2px(10));

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setColor(Color.WHITE);
        emptyPaint.setTextSize(dp2px(13));
       // emptyPaint.setTextSize(dp2px(15));


        linPath = new Path();

        //截取超出X轴坐标的画笔
        paintBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBack.setColor(backGroundColor);
        paintBack.setStyle(Paint.Style.FILL);

        ySchedulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ySchedulePaint.setColor(Color.parseColor("#FF676767"));
        ySchedulePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        ySchedulePaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));
        ySchedulePaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.e(TAG,"------onMeasure-------");
//        mWidth = getMeasuredWidth();
//        mHeight = getMeasuredHeight();

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e(TAG,"-----changed="+changed);
        if(changed){
            mWidth = getWidth();
            mHeight = getHeight();
            Log.e(TAG,"------mHeight="+mHeight);
            originX = paddingLeft - leftRightExtra;
            originY = mHeight - paddingDown;
            firstPointX = paddingLeft;

            firstMinX = mWidth - originX - (xValues.size() - 1) * intervalX - leftRightExtra;

            // 滑动时，第一个点x值最大为paddingLeft，在大于这个值就不能滑动了
            firstMaxX = firstPointX;

            setBackgroundColor(backGroundColor);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,mHeight);
        canvas.save();

        if(sourceList == null || sourceList.isEmpty()){
            emptyPaint.setTextSize(dp2px(14));
            emptyPaint.setStrokeWidth(2f);
            String nodataStr = "无数据";
            canvas.drawText(nodataStr,mWidth/2-(MiscUtil.getTextWidth(emptyPaint,nodataStr)/2),-mHeight/2+(MiscUtil.measureTextHeight(emptyPaint)/2),emptyPaint);
            return;
        }
        //绘制X轴白线
        drawXLinView(canvas);
        drawSourceData(canvas);
        //汇总折线图
        drawLinView(canvas);

        //绘制右侧Y轴
        drawYLine(canvas);
    }

    //绘制Y轴
    private void drawYLine(Canvas canvas) {

    }


    //汇总折线
    private void drawLinView(Canvas canvas) {

    }

    //汇总X轴
    private void drawXLinView(Canvas canvas) {
        canvas.drawLine(0,-dp2px(15),mWidth,-dp2px(15),xPaint);
    }

    private void drawSourceData(Canvas canvas) {
        canvas.save();
        //系数
        float modulsValue = (mHeight/2f) / 15 ;
        //modulsValue = modulsValue +4f;
        Timber.e("------系数="+modulsValue+" "+((mHeight*0.75f) /maxValue));

        //85的线
        canvas.drawText("85%",mWidth-MiscUtil.getTextWidth(xPaint,"100%")-dp2px(20),-dp2px(15),ySchedulePaint);

        //90
        canvas.drawLine(0,-5*modulsValue,mWidth,-5*modulsValue,xPaint);

        //95的线
        canvas.drawLine(0,-10*modulsValue,mWidth,-10*modulsValue,xPaint);
        canvas.drawText("95%",mWidth-MiscUtil.getTextWidth(xPaint,"100%")-dp2px(20),-10*modulsValue,ySchedulePaint);

        //100的线
        canvas.drawLine(0,-15*modulsValue,mWidth,-15*modulsValue,xPaint);
        canvas.drawText("100%",mWidth-MiscUtil.getTextWidth(xPaint,"100%")-dp2px(20),-15*modulsValue,ySchedulePaint);


        for(int i = 0;i<xValues.size();i++){
            Map<String,Integer> tmpMap = sourceList.get(i);

            float rectfLeft = firstPointX + intervalX * i + mCurrentWidth;

            float rectfTop = -(tmpMap.get(xValues.get(i))) * modulsValue + 85 * modulsValue;

            Timber.e("------rectfTop="+rectfTop+" "+85*modulsValue);

            float rectfRight = firstPointX+intervalX * i + mCurrentWidth  + dp2px(15);
            float rectfBottom = -tmpMap.get(xValues.get(i)) * modulsValue+dp2px(5)+ 85 * modulsValue;

            RectF rectF = new RectF(rectfLeft,rectfTop,rectfRight,rectfBottom);
            canvas.drawRoundRect(rectF,5f,5f,mBarPaint);

            canvas.drawCircle(firstPointX + intervalX * i + mCurrentWidth,-(tmpMap.get(xValues.get(i)) * modulsValue)- dp2px(15),5f,mBarPaint);


//            RectF rectF = new RectF(firstPointX + intervalX * i + mCurrentWidth ,-(tmpMap.get(xValues.get(i)) * modulsValue)- dp2px(15), firstPointX+intervalX * i + mCurrentWidth  + dp2px(6) , - dp2px(15));
//            canvas.drawRect(rectF, mBarPaint);



            //汇总数值显示
            float xV = firstPointX+intervalX * i + mCurrentWidth -(MiscUtil.getTextWidth(mBarPaint,tmpMap.get(xValues.get(i))+"")/2);
            float yV = (tmpMap.get(xValues.get(i)) * modulsValue) + dp2px(18);
            canvas.drawText(tmpMap.get(xValues.get(i))+"",xV,-yV, mBarPaint);

            if(i % 3 == 0){
                canvas.drawText(xValues.get(i)+"",firstPointX +intervalX * i + mCurrentWidth-((MiscUtil.getTextWidth(mBarPaint,xValues.get(i)))/2),-(MiscUtil.measureTextHeight(mBarPaint))/2,mBarPaint);
            }

//            if(i == 0){
//                linPath.moveTo(firstPointX+ mCurrentWidth,-((tmpMap.get(xValues.get(i)) / modulsValue)));
//            }else {
//                linPath.lineTo(firstPointX+intervalX * i + mCurrentWidth,-(tmpMap.get(xValues.get(i)) / modulsValue));
//            }
        }

//        canvas.drawPath(linPath,emptyPaint);

        //将折线超出x轴坐标的部分截取掉（左边）
        paintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        RectF rectF = new RectF(0, -mHeight, 2, 0);
        canvas.drawRect(rectF, paintBack);
        canvas.restore();

    }


    class CusOnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

            if (motionEvent.getX() > originX && motionEvent.getX() < mWidth - paddingRight && motionEvent.getY() > paddingTop && motionEvent.getY() < mHeight - paddingDown){
                v = -v;
                if (firstPointX + v > firstMaxX) {
                    firstPointX = firstMaxX;
                } else if (firstPointX + v < firstMinX) {
                    firstPointX = firstMinX;
                } else {
                    firstPointX = (int) (firstPointX + v);
                }

                if(firstPointX == firstMinX){
                    return false;
                }
                invalidate();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(xValues.size() < 4)
            return false;
        gestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){  //按下事件

            float x = event.getX();
            float y = event.getY();
            //系数
            float modulsValue = (mHeight/2f) / 15 ;



        }

        return true;
    }



    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public List<String> getxValues() {
        return xValues;
    }

    public void setxValues(List<String> xValuess) {
        xValues.clear();
        this.xValues.addAll(xValuess);
    }

    public List<Map<String, Integer>> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Map<String, Integer>> sourceLists) {
        sourceList.clear();
        this.sourceList.addAll(sourceLists);
        linPath.reset();
        handler.sendEmptyMessage(0x01);
        postInvalidate();
    }

    private float dp2px(int dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }


    public List<ChartSpo2Bean> getChartSpo2BeanList() {
        return chartSpo2BeanList;
    }

    public void setChartSpo2BeanList(List<ChartSpo2Bean> chartSpo2BeanList) {
        this.chartSpo2BeanList = chartSpo2BeanList;
        invalidate();
    }
}
