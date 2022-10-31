package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;
import com.bonlala.fitalent.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;


public class RealHrBarChartView extends View {

    private int paddingLeft = DisplayUtils.dip2px(getContext(), 10);
    private int paddingTop = DisplayUtils.dip2px(getContext(), 15);
    private int paddingRight = DisplayUtils.dip2px(getContext(), 10);
    private int paddingBottom = 0;


    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                  //  Log.e("onTouchEvent", "mHandler isDown" + isDown + ",isDelay=" + isDelay);
                    if (!isDown) {
                        isDelay = false;
                    }
                    break;
            }

        }
    };
    private int barWidth = DisplayUtils.dip2px(getContext(), 8);//柱子宽度
    private int barSpace = DisplayUtils.dip2px(getContext(), 0.5f);//柱子间距

    //设置柱子宽度
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    private Paint barPaint;//画柱子
    private int barColor = Color.GREEN;//柱子颜色

    //设置柱子颜色
    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    private int legendTextColor = Color.BLACK;
    private int legendTextSize = 0;


    private List<Integer> hrList = new ArrayList<>();//y轴数据
    private List<Integer> hrColorList = new ArrayList<>();//y轴数据

    private int maxOffset;
    private float lastX;

    private VelocityTracker tracker;
    private Scroller scroller;



    //设置数据


    public void clearData() {
        hrList.clear();
        hrColorList.clear();
        clear();
    }


    public void addData(int hr, int hrColor, boolean isHide) {

        if (hrList.size() >= 30 * 60) {
            hrList.remove(0);
            hrColorList.remove(0);
        }
        hrList.add(hr);
        hrColorList.add(hrColor);
        if (isHide) {
            return;
        }
        int rmvermaxOffset = (hrList.size() * (barWidth + barSpace) - (getMeasuredWidth() - paddingRight - paddingLeft));//计算可滑动距离
      //  Log.e("getScrollX---", "getScrollX()" + getScrollX() + "-----" + getWidth() + "maxOffset" + rmvermaxOffset + "isDown=" + isDown + "isDelay=" + isDelay);
        if (getWidth() == 0) {
            return;
        }

        boolean isCanRemove = (!isDown && !isDelay);
        if (getWidth() > 0 && rmvermaxOffset > 0 && isCanRemove) {
          //  Log.e("getScrollX---2222", "getScrollX()" + getScrollX() + "-----" + getWidth() + "maxOffset" + rmvermaxOffset);
            scrollTo(-(rmvermaxOffset + DisplayUtils.dip2px(getContext(), 10f)), 0);
        }
        invalidate();
    }


    //得到Y轴最大值
    private float maxYData() {
        return 250;
    }

    public RealHrBarChartView(Context context) {
        this(context, null);
    }

    public RealHrBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RealHrBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        barPaint = new Paint();
        barPaint.setColor(barColor);
        barPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        barPaint.setAntiAlias(true);
        scroller = new Scroller(getContext());

    }

    private float ageY;//平均没等分是多少


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    int x0;
    int x1;
    float top0;
    int width;
    int height;
    int left, right, top, bottom;

    @Override
    protected void onDraw(Canvas canvas) {


      //  Log.e("onDraw", "BarChartView---------------getScrollX()" + getScrollX());
        width = getWidth();
        height = getHeight();
        if (hrList == null || hrList.size() == 0) return;
        left = paddingLeft;//paddingLeft+线的宽度/2+Y轴值最大宽度
        right = width - paddingRight;
        top = paddingTop;
        bottom = height - paddingBottom;//预留X轴文字高度
        canvas.save();
        canvas.translate(getScrollX(), 0);
        ageY = (height - paddingBottom - paddingTop) / 250f;//线的每等分
        for (int i = 0; i < hrList.size(); i++) {
            x0 = left + (barSpace + barWidth) * i + barSpace + getScrollX();
            x1 = x0 + barWidth;
            if (x1 <= left || x0 >= right) {
                continue;
            }
            top0 = (float) (height - paddingBottom - (hrList.get(i) * ageY));
            canvas.clipRect(left, top, right, bottom);//剪切柱状图区域
            barPaint.setColor(hrColorList.get(i));
            canvas.drawRect(x0, top0, x1, bottom, barPaint);//画柱状图
            //底部X轴文字
            //柱状图上加文字
        }
        maxOffset = (hrList.size() * (barWidth + barSpace) - (getMeasuredWidth() - paddingRight - paddingLeft));//计算可滑动距离
        if (maxOffset < 0) {
            maxOffset = 0;
        }
        canvas.restore();
    }

    boolean isDown = false;

    boolean isDelay = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                isDown = true;
                isDelay = true;
               // Log.e("onTouchEvent", "ACTION_DOWN isDown" + isDown + ",isDelay=" + isDelay);
                if (tracker != null) {
                    tracker.clear();
                }
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (tracker == null) {
                    tracker = VelocityTracker.obtain();
                }
                if (tracker != null) {
                    tracker.addMovement(event);
                }
                int sX = getScrollX();
                sX += event.getX() - lastX;
                sX = Math.max(Math.min(0, sX), -maxOffset);
                scrollTo(sX, 0);
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                setTracker();
              //  Log.e("onTouchEvent", "ACTION_UP isDown" + isDown + ",isDelay=" + isDelay);
                break;
            case MotionEvent.ACTION_CANCEL:
             //   Log.e("onTouchEvent", "ACTION_CANCEL isDown" + isDown + ",isDelay=" + isDelay);
                setTracker();
                break;
        }
        invalidate();
        return true;
    }


    public void clear() {
        // if (getScrollX() < 0) {
        scrollTo(0, 0);
        //}
    }

    private void setTracker() {
        getParent().requestDisallowInterceptTouchEvent(false);
        isDown = false;
        mHandler.removeMessages(0x00);
        mHandler.sendEmptyMessageDelayed(0x00, 5000);
        if (tracker != null) {
            tracker.computeCurrentVelocity(1000);
            scroller.forceFinished(true);
            scroller.fling(getScrollX(), 0, (int) (0.5 * tracker.getXVelocity()), 0, -maxOffset, 0, 0, 0);
            tracker.recycle();
        }
        tracker = null;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }

}