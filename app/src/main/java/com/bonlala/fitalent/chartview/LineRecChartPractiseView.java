package com.bonlala.fitalent.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.utils.CalculateUtils;
import com.bonlala.fitalent.utils.DisplayUtils;
import com.bonlala.widget.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者：chs on 2016/9/6 14:17
 * 邮箱：657083984@qq.com
 * 线形图表
 * @author Admin
 */
public class LineRecChartPractiseView extends View {
    private static final String TAG = LineRecChartPractiseView.class.getSimpleName();
    private Context mContext;
    /**
     * 背景的颜色
     */
    private static final int BG_COLOR = Color.WHITE;


    private Integer maxHr, minHr;

    private String strStartTime, strEndTime;

    private Integer maxIndex, minIndex;

    /**
     * 视图的宽和高
     */
    private int mTotalWidth, mTotalHeight;
    private int paddingRight, paddingBottom, paddingTop;
    /**
     * x轴 y轴 起始坐标
     */
    private float mStartX, mStartY;
    /**
     * 图表绘制区域的顶部和底部  图表绘制区域的最大高度
     */
    private float paintTop, paintBottom, maxHeight;
    //距离底部的多少 用来显示底部的文字
    private int bottomMargin;
    //距离顶部的多少 用来显示顶部的文字
    private int topMargin;
    /**
     * 左边和上边的边距
     */
    private int leftMargin, rightMargin;
    /**
     * 画笔 背景，轴 ，线 ，text ,点 提示线
     */
    private Paint axisPaint, linePaint, textPaint, pointPaint;//, hintPaint;
    /**
     * 原点的半径
     */
    private static final float RADIUS = 8;
    private List<LineChartEntity> mData;//数据集合
    /**
     * 右边的最大和最小值
     */
    private int maxRight, minRight;
    /**
     * item中的Y轴最大值
     */
    private float maxYValue;
    /**
     * 最大分度值
     */
    private float maxYDivisionValue;
    /**
     * /**
     * 向右边滑动的距离
     */
    private float leftMoving;
    //左边Y轴的单位
    private String leftAxisUnit = "单位";
    /**
     * 两个点之间的距离
     */
    private float space;
    /**
     * 绘制的区域
     */
    private RectF mDrawArea, mHintArea;
    private Rect leftWhiteRect, rightWhiteRect;
    /**
     * 保存点的x坐标
     */
    private List<Point> linePoints = new ArrayList<>();
    /**
     * 左后一次的x坐标
     */
    private float lastPointX;
    /**
     * 当前移动的距离
     */
    private float movingThisTime = 0.0f;

    /**
     * 是不是绘制曲线
     */
    private boolean isCurv = false;
    /**
     * 点击的点的位置
     */
    private int selectIndex;

    /**
     * 是否绘制提示文字
     */


    protected Paint mPaint;

    public LineRecChartPractiseView(Context context) {
        super(context);
        init(context);
    }

    public LineRecChartPractiseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineRecChartPractiseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        mContext = context;
        mBarRect = new RectF(0, 0, 0, 0);
        initMagin(context);

        initAxisPaint(context);

        initTextPaint(context);


        initPointPaint();

//        initHintPaint(context);


    }

    private void initMagin(Context context) {
        bottomMargin = MiscUtil.dipToPx(getContext(), 30);
        topMargin = DisplayUtils.dip2px(context, 30);
        rightMargin = DisplayUtils.dip2px(getContext(), 20);
        leftMargin = DisplayUtils.dip2px(getContext(), 0);
    }


    private void initAxisPaint(Context context) {
        axisPaint = new Paint();
        axisPaint.setColor(Color.parseColor("#2F2F33"));//#F3546A
        axisPaint.setStrokeWidth(DisplayUtils.dip2px(context, 1));
    }

    private void initTextPaint(Context context) {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DisplayUtils.dip2px(getContext(), 11));
        textPaint.setColor(Color.parseColor("#2F2F33"));//#F3546A//common_tips_color
    }

    private void initLinePaint(Context context) {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(DisplayUtils.dip2px(context, 1));
        linePaint.setColor(Color.parseColor("#99FFFFFF"));//#F3546A
        linePaint.setStyle(Paint.Style.FILL);
    }

    private void initPointPaint() {
        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);


    }


    public void setData(List<LineChartEntity> list, boolean isCurv, int maxHr, int minHr, String strStarTime, String strEndTime) {
        this.mData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).yValue != 0) {
                this.mData.add(list.get(i));
            }
        }
        if (list.size() < 5) {
            for (int i = list.size(); i < 5; i++) {
                this.mData.add(new LineChartEntity("0", 0f));
            }
        }
        this.isCurv = isCurv;
        this.strStartTime = strStarTime;
        this.strEndTime = strEndTime;
        //计算最大值
        if (list.size() > 0) {
            maxYValue = list.get(0).yValue;
            maxYValue = calculateMax(list, maxHr, minHr);
            getRange((int) maxYValue);
        }

        this.minHr = minHr;
        this.maxHr = maxHr;
        initLinePaint(mContext);


        invalidate();


    }

    /**
     * 计算出Y轴最大值
     *
     * @return
     */

    ArrayList<String> yList = new ArrayList<>();
    ArrayList<String> xList = new ArrayList<>();

    private float calculateMax(List<LineChartEntity> list, int maxHr, int minHr) {
        /**
         * NSMutableArray *yMutableArray = [NSMutableArray array];

         int b=maxValue%5;
         if (b!=0)
         {
         maxValue+=5-b;
         }

         maxValue=maxValue==0?maxValue=4:maxValue;
         int a= maxValue/4;
         b=a%5;
         if (b!=0&&a>=5)
         {
         a+=5-b;
         }
         for (int i = 0; i <5*a ; i+=a) {
         [yMutableArray addObject:[NSString stringWithFormat:@"%d",i]];
         }
         self.yArray = [NSArray arrayWithArray:yMutableArray];
         */


        float maxValue = (list.get(0).yValue);
        //  for (LineChartEntity entity : list) {
        //获取最大值和最小只的index
        for (int i = 0; i < list.size(); i++) {
            LineChartEntity entity = list.get(i);
            if (entity.yValue > maxValue) {
                maxValue = entity.yValue;
            }
            if (entity.yValue == maxHr) {
                maxIndex = i;

            }
            if (entity.yValue == minHr) {
                minIndex = i;
            }
        }

        int b = (int) (maxValue) % 5;
        if (b != 0) {
            maxValue += 5 - b;
        }

        maxValue = maxValue == 0 ? maxValue = 4 : maxValue;
        int a = (int) (maxValue / 4);
        b = a % 5;
        if (b != 0 && a >= 5) {
            a += 5 - b;
        }
        yList.clear();
        if (maxHr == 0 && minHr == 0) {
            for (int i = 1; i < 5 * a; i += a) {

                yList.add((50 * i) + "");
            }
        } else {
            for (int i = a; i < 5 * a; i += a) {
                yList.add(i + "");
            }
        }
        xList.clear();
        for (int i = 0; i <= 24; i += 4) {
            xList.add(i + "");
        }
        return 4 * a;
    }

    /**
     * 得到柱状图的最大和最小的分度值
     *
     * @param maxValueInItems
     */
    private void getRange(int maxValueInItems) {//1200
        int scale = CalculateUtils.getScale(maxValueInItems);//获取这个最大数 数总共有几位 3
        float unScaleValue = (float) (maxValueInItems / Math.pow(10, scale));//最大值除以位数之后剩下的值  比如1200/1000 后剩下1.2

        maxYDivisionValue = (float) (unScaleValue * Math.pow(10, scale));//获取Y轴的最大的分度值 1500
        mStartX = DisplayUtils.dip2px(mContext, 30);//获取1500的宽度
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//一般是视图大小发生变化的回调
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        mTotalWidth = w - paddingLeft - paddingRight;//除去内间距的实际视图的宽度
        mTotalHeight = h - paddingTop - paddingBottom;//除去内间距的实际视图的高度
        maxHeight = h - getPaddingTop() - getPaddingBottom() - bottomMargin - topMargin;//图表绘制区域的最大高度

        //  Log.e(TAG, "paddingLeft:" + paddingLeft + "--paddingTop:" + paddingTop + "--paddingRight:" + paddingRight + "--paddingBottom:" + paddingBottom);
        //  Log.e(TAG, "mTotalWidth:" + mTotalWidth + "--mTotalHeight:" + mTotalHeight + "--maxHeight:" + maxHeight);
        //  Log.e(TAG, "w:" + w + "--h:" + h);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    //获取滑动范围和指定区域
    private void getArea() {
        if (mData != null) {
            //给右边一点空间,多减右边的5dp
            space = (mTotalWidth - mStartX - DisplayUtils.dip2px(mContext, 5)) / (float) (mData.size() - 1);
            mStartY = mTotalHeight - bottomMargin - paddingBottom;
            mDrawArea = new RectF(mStartX, paddingTop, mTotalWidth - paddingRight - rightMargin, mTotalHeight - paddingBottom);
//            mHintArea = new RectF(mDrawArea.right - mDrawArea.right / 4, mDrawArea.top + topMargin / 2,
//                    mDrawArea.right, mDrawArea.top + mDrawArea.height() / 4 + topMargin / 2);
            Log.e(TAG, " space: " + space + "--mStartY:" + mStartY);//+"--maxRight:"+maxRight+"--minRight:"+minRight
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null || mData.isEmpty()) return;
        getArea();
        linePoints.clear();
        canvas.drawColor(mContext.getResources().getColor(com.bonlala.base.R.color.transparent));

        //画左边的Y轴text
        drawLeftYAxis(canvas);
        //画X轴的text
        drawXAxisText(canvas);

        //画线形图
        drawLines(canvas);
        //drawXline(canvas);

    }


    private void drawXline(Canvas canvas) {
        canvas.drawLine(mStartX, mStartY, mTotalWidth - leftMargin * 2, mStartY, axisPaint);
    }

    private void drawXAxisText(Canvas canvas) {
        //这里设置 x 轴的字一条最多显示3个，大于三个就换行
        // float starX = (mTotalWidth - leftMargin * 2 + mStartX / 2) / 2;
        canvas.drawText(strStartTime, mStartX, mStartY + (textPaint.measureText("24")), textPaint);
        canvas.drawText(strEndTime, (mTotalWidth - leftMargin * 2) - (textPaint.measureText(strEndTime)), mStartY + (textPaint.measureText("24")), textPaint);

    }

    boolean isAnimatorStart;

    private float percent = 1f;


    private RectF mBarRect;

    float lineHeight;
    float width;
    float clearanceWidth;//间隔

    private void drawLines(Canvas canvas) {
        clearanceWidth = DisplayUtils.dip2px(mContext, 50) / (mData.size() / 5.0f);
        width = mData.size()<8 ? MiscUtil.dipToPx(mContext,15f) :(mTotalWidth - mStartX - clearanceWidth * (mData.size() - 1)) / mData.size();


        if (width < 1) {
            clearanceWidth = clearanceWidth + width - 1;
            width = 1;
        }


        //  Log.e("drawLines", "clearanceWidth=" + clearanceWidth + ",width=" + width);

        for (int i = 0; i < mData.size(); i++) {
            lineHeight = mData.get(i).yValue * maxHeight / maxYDivisionValue;
            mBarRect.left = (int) ((i * (width + clearanceWidth)) + mStartX);
            mBarRect.top = (int) ((mStartY - lineHeight) * percent);
            mBarRect.bottom = (int) mStartY;
            mBarRect.right = (int) (mBarRect.left + width);
            linePaint.setColor(mData.get(i).corlors);//#F3546A
            canvas.drawRoundRect(mBarRect, (mBarRect.right-mBarRect.left)/2, (mBarRect.right-mBarRect.left)/2, linePaint);

            //   Log.e("drawLines", "clearanceWidth=" + clearanceWidth + ",width=" + width + "mBarRect" + mBarRect.left + "---" + mBarRect.right + "---" + mBarRect.top + "---" + mBarRect.bottom + "mData.get(i).corlors" + mData.get(i).corlors);
        }

    }


    /**
     * 画Y轴上的text (1)当最大值大于1 的时候 将其分成5份 计算每个部分的高度  分成几份可以自己定
     * （2）当最大值大于0小于1的时候  也是将最大值分成5份
     * （3）当为0的时候使用默认的值
     *
     * @param canvas
     */
    private void drawLeftYAxis(Canvas canvas) {
        float eachHeight = (maxHeight / yList.size());

        for (int i = 1; i <= yList.size(); i++) {
            float startY = mStartY - eachHeight * i;
            String text = yList.get(i - 1);
            canvas.drawText(text, 0 + DisplayUtils.dip2px(mContext, 30) / 2 - textPaint.measureText(text) / 2, startY + textPaint.measureText("0") - 10, textPaint);
        }
    }


    @Override
    public void computeScroll() {

    }


}
