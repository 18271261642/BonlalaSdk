package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartSpo2Bean;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.widget.utils.MiscUtil;

import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * 单个的血氧，
 * Created by Admin
 * Date 2022/10/11
 * @author Admin
 */
public class CustomizeSingleSpo2View extends View {


    private float mWidth,mHeight;

    //绘制柱子矩形的画笔
    private Paint barPaint;
    //绘制X轴文字的画笔
    private Paint txtPaint;


    //点击后文字的画笔
    private Paint clickTxtPaint;
    //背景画笔
    private Paint clickBgPaint;
    //点击线的画笔
    private Paint clickLinePaint;
    //点击状态的画笔
    private Paint clickStatusPaint;

    //柱子的宽度
    private float mBarWidth;

    /**是否是中文状态**/
    private boolean isChinese;

    private ChartSpo2Bean chartSpo2Bean;

    public CustomizeSingleSpo2View(Context context) {
        super(context);
        initPaint();
    }

    public CustomizeSingleSpo2View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CustomizeSingleSpo2View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    private void initPaint(){

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        barPaint.setColor(Color.parseColor("#FFFC4242"));
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),2f));

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        txtPaint.setColor(Color.parseColor("#FF676767"));
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),0.5f));
        txtPaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));

        clickBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clickBgPaint.setColor(Color.parseColor("#FFF4F6FA"));
        clickBgPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),1f));
        clickBgPaint.setAntiAlias(true);

        clickTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickTxtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clickTxtPaint.setColor(Color.parseColor("#FF676767"));
        clickTxtPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),0.5f));
        clickTxtPaint.setTextSize(MiscUtil.dipToPx(getContext(),13f));
        clickTxtPaint.setAntiAlias(true);


        clickLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickLinePaint.setStyle(Paint.Style.STROKE);
        clickLinePaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),1f));
        clickLinePaint.setColor(Color.parseColor("#FFDBDBDB"));
        clickLinePaint.setAntiAlias(true);

        clickStatusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickStatusPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clickStatusPaint.setTextAlign(Paint.Align.CENTER);
        clickStatusPaint.setColor(Color.parseColor("#FF4DCC4B"));
        clickStatusPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),0.8f));
        clickStatusPaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));
        clickStatusPaint.setAntiAlias(true);

        isChinese = BaseApplication.getInstance().getIsChinese();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasBar(canvas);
    }

    private void canvasBar(Canvas canvas) {
        canvas.translate(0,mHeight);
        canvas.save();
        float modulus = mHeight/1.5f/25;

        if(chartSpo2Bean == null)
            return;

        int spo2Value = chartSpo2Bean.getSpo2Value();

        mBarWidth = MiscUtil.dipToPx(getContext(),10f);



        //绘制底部的文字
//        String txt = chartBpBean.getxValue();
//        float txtWidth = MiscUtil.getTextWidth(txtPaint,txt);
//        canvas.drawText(txt,mWidth/2,0,txtPaint);

        long timeStr = Long.parseLong(chartSpo2Bean.getxValue());
        float tH = MiscUtil.measureTextHeight(txtPaint);
        //年
        String year = isChinese ? BikeUtils.getFormatDate(timeStr,"yyyy-MM-dd") : BikeUtils.getFormatDate(timeStr,"MMM dd,yyyy", Locale.ENGLISH);
        String seconds = BikeUtils.getFormatDate(timeStr,"HH:mm:ss");

        canvas.drawText(year,mWidth/2,-tH*1.5f,txtPaint);
        canvas.drawText(seconds,mWidth/2,0f,txtPaint);




        float middleV = -(spo2Value-85) * modulus-tH*2.5f;

        float rectFLeft = mWidth/2-mBarWidth/2;
        float rectFTop = middleV-MiscUtil.dipToPx(getContext(),2f);
        float rectFRight = mWidth/2+mBarWidth/2;
        float rectFBottom = middleV+MiscUtil.dipToPx(getContext(),2f);
        RectF rectF = new RectF(rectFLeft,rectFTop,rectFRight,rectFBottom);
        canvas.drawRoundRect(rectF,5f,5f,barPaint);

        if(chartSpo2Bean.isChecked()){
            canvasClickBg(canvas,year,seconds);
        }
    }


    //绘制点击的效果
    private void canvasClickBg(Canvas canvas,String year,String seconds){
        float paddingWidth = MiscUtil.dipToPx(getContext(),5f);
        String bpStr = chartSpo2Bean.getSpo2Value()+"%";
        clickTxtPaint.setTextAlign(Paint.Align.CENTER);
        float width = MiscUtil.getTextWidth(clickTxtPaint,bpStr);
        float txtHeight = MiscUtil.measureTextHeight(clickTxtPaint);


        float rectFBottom = -mHeight+txtHeight*4f;

        RectF rectF = new RectF(0,-mHeight+paddingWidth*2/2,mWidth,rectFBottom);
        canvas.drawRoundRect(rectF,10f,10f,clickBgPaint);

        clickStatusPaint.setColor(Color.parseColor("#FF4DCC4B"));
        canvas.drawText(getResources().getString(R.string.string_normal),mWidth/2,-mHeight+txtHeight*2.2f+paddingWidth*2,clickStatusPaint);

        canvas.drawText(bpStr,mWidth/2,-mHeight+txtHeight+paddingWidth*2,clickTxtPaint);

        clickStatusPaint.setColor(Color.parseColor("#FF676767"));
        long timeStr = Long.parseLong(chartSpo2Bean.getxValue());
        //年
//        String year = BikeUtils.getFormatDate(timeStr,"yyyy-MM-dd");
//        String seconds = BikeUtils.getFormatDate(timeStr,"HH:mm:ss");
//        canvas.drawText(year,mWidth/2,-mHeight+txtHeight*3.5f+paddingWidth*2,clickStatusPaint);
//        canvas.drawText(seconds,mWidth/2,-mHeight+txtHeight*4.5f+paddingWidth*2,clickStatusPaint);


        canvas.drawLine(mWidth/2,-txtHeight*2.5f,mWidth/2,-mHeight+txtHeight*2f+paddingWidth*2,clickLinePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float x= event.getX();
            float y = event.getY();

            float rectFLeft = mWidth/2-mBarWidth/2;
            float rectFRight = mWidth/2+mBarWidth/2;
            if(x>rectFLeft && x < rectFRight){
                chartSpo2Bean.setChecked(true);
            }
        }

        invalidate();

        return false;
    }


    public ChartSpo2Bean getChartSpo2Bean() {
        return chartSpo2Bean;
    }

    public void setChartSpo2Bean(ChartSpo2Bean chartSpo2Bean) {
        this.chartSpo2Bean = chartSpo2Bean;
        invalidate();
    }
}
