package com.bonlala.fitalent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.utils.CalculateUtils;
import com.bonlala.widget.utils.MiscUtil;

import androidx.annotation.Nullable;

/**
 * 水平渐变进度条
 * Created by Admin
 * Date 2022/10/13
 * @author Admin
 */
public class LinearProgressView extends View {

    private int mStartColor;//渐变开始颜色
    private int mEndColor;//渐变结束颜色
    private float mProgress = 100;//进度条总进度
    private int mbgColor;//背景边框颜色
    private float mCurrentProgress;//当前进度
    private float mRadius;
    private Paint mPaint;
    private LinearGradient gradient;
    private Path path;
    private Path pathBg;
    private Path pathPro;
    private RectF rectF;

    /**是否是下载模式，下载模式显示进度**/
    private boolean isDownload = false;

    /**里面文字的画笔**/
    private Paint txtPaint;

    public LinearProgressView(Context context) {
        super(context, null);
    }

    public LinearProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initColor(context,attrs);
        mRadius = MiscUtil.dipToPx(context,10);

    }

    public LinearProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initColor(context,attrs);

    }

    private void initColor(Context context,AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,R.styleable.LinearProgressView);
        mbgColor = typedArray.getColor(R.styleable.LinearProgressView_linear_bg_color,Color.parseColor("#FF4EDD7D"));
        mStartColor = typedArray.getColor(R.styleable.LinearProgressView_linear_start_color,Color.parseColor("#FF4EDD7D"));
        mEndColor = typedArray.getColor(R.styleable.LinearProgressView_linear_end_color,Color.parseColor("#FF87FE4D"));

        mRadius = MiscUtil.dipToPx(context,10);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        path = new Path();
        pathBg = new Path();

        pathPro = new Path();

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(MiscUtil.dipToPx(getContext(),14f));
        txtPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),2f));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setCurrentProgressValue(float currentProgressValue, float allValue) {
        mCurrentProgress = currentProgressValue;
        mProgress = allValue;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rectF == null) {
            rectF = new RectF();
        }
        //绘制背景
        mPaint.setColor(mbgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),1f));
        rectF.left = 0;
        rectF.bottom = getHeight();
        rectF.right = getWidth();
        rectF.top = 0;
        mRadius = getHeight()/2;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#E8E9ED"));
        pathBg.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CCW);
        canvas.drawPath(pathBg, mPaint);
        //绘制进度条
        if (gradient == null) {
            gradient = new LinearGradient(0, getHeight() / 2, getWidth(), getHeight() / 2,
                    mStartColor, mEndColor , Shader.TileMode.MIRROR);
        }

        mPaint.setShader(gradient);
        mPaint.setStyle(Paint.Style.FILL);
        float progressValue = 0;
        if (mProgress != 0) {
            progressValue = mCurrentProgress / mProgress * getWidth();
        }
        if (progressValue>getWidth()){
            progressValue = getWidth();
        }
        rectF.bottom = getHeight();
        rectF.left = progressValue < getHeight() ? progressValue - getHeight() : 0;
        rectF.right = progressValue;
        rectF.top = 0;
        pathPro.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CW);
        path.op(pathBg, pathPro, Path.Op.INTERSECT);
        canvas.drawPath(path, mPaint);
        path.reset();
        pathBg.reset();
        pathPro.reset();
        mPaint.reset();

        if(isDownload){
            String txt = "Download: "+(CalculateUtils.div(mCurrentProgress,mProgress,200) * 100)+"%";
            float txtHeight = MiscUtil.measureTextHeight(txtPaint);
            canvas.drawText(txt,getWidth()/2,getHeight()/2+txtHeight/2,txtPaint);
        }else{
            String txt = (int)mCurrentProgress+"/"+(int) mProgress;
            float txtHeight = MiscUtil.measureTextHeight(txtPaint);
            canvas.drawText(txt,getWidth()/2,getHeight()/2+txtHeight/2,txtPaint);
        }

    }


    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }
}
