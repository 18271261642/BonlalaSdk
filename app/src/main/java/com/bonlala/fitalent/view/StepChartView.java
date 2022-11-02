package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bonlala.fitalent.db.model.OneDayStepModel;
import com.bonlala.fitalent.db.model.StepItem;
import com.bonlala.fitalent.emu.StepType;
import com.bonlala.fitalent.utils.CalculateUtils;
import com.bonlala.widget.utils.MiscUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created by Admin
 * Date 2022/9/29
 */
public class StepChartView extends View {

    //柱子的画笔
    private Paint pillarPaint;

    //空数据的画笔
    private Paint emptyPaint;

    //点击详情的矩形画笔
    private Paint rectPaint;

    //柱子的颜色
    private int pillarColor;
    //空数据的颜色
    private int emptyColor;

    //绘制x轴线的画笔
    private Paint xLinPaint;

    //X轴刻度线的画笔
    private Paint xTxtPaint;


    //点击的刻度画笔
    private Paint clickPaint;

    //宽度
    private float mWidth,mHeight;

    //右侧Y轴标线的宽度，根据最大值的宽度
    private float yWidth;
    //绘制Y轴刻度线的画笔
    private Paint yPaint;


    //日期
    private String dayStar;
    //数据源，每天的数据，只绘制一整天24小时的数据
    private OneDayStepModel oneDayStepModel;

    private List<StepItem> list = new ArrayList<>();
    int maxValue = 0;


    //单个柱子的宽度
    float singleBarWidth;
    //24小时数据
    private final String[] hourList = new String[]{"00:00","06:00","12:00","18:00","23:00"};


    //类型再设置数据源前设置
    private StepType stepType = StepType.DAY;

    public StepChartView(Context context) {
        super(context);
        initPaint(context);
    }

    public StepChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    public StepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    private void initPaint(Context context) {
        pillarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pillarPaint.setAntiAlias(true);
        pillarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pillarPaint.setColor(Color.parseColor("#5067E08E"));
        pillarPaint.setStrokeWidth(MiscUtil.dipToPx(context,2f));

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setAntiAlias(true);
        emptyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        emptyPaint.setColor(Color.GREEN);
        emptyPaint.setTextSize(MiscUtil.dipToPx(context,15f));
        emptyPaint.setStrokeWidth(1f);


        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.parseColor("#F4F6FA"));
        rectPaint.setStrokeWidth(5f);

        xLinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xLinPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        xLinPaint.setColor(Color.parseColor("#DBDBDB"));
        xLinPaint.setStrokeWidth(MiscUtil.dipToPx(getContext(),0.5f));

        xTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xTxtPaint.setColor(Color.parseColor("#DBDBDB"));
        xTxtPaint.setStrokeWidth(1f);
        xTxtPaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));
        xTxtPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        clickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickPaint.setTextSize(MiscUtil.dipToPx(getContext(),12f));
        clickPaint.setStrokeWidth(1f);
        clickPaint.setColor(Color.parseColor("#676767"));
        clickPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        yPaint.setColor(Color.parseColor("#676767"));
        yPaint.setStrokeWidth(1f);
        yPaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));

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
//        if(oneDayStepModel== null || oneDayStepModel.getDetailStep() == null){
//
//            return;
//        }

        drawChartView(canvas);
    }


    private void drawChartView(Canvas canvas){
        canvas.translate(0,mHeight);
        canvas.save();
        maxValue = 0;
        //单个柱子的宽度
        singleBarWidth = MiscUtil.dipToPx(getContext(),8f);
        //得到最大值 空数据时数据都是0
        for(StepItem stepItem : list){
            int step = stepItem.getStep();
            if(step>maxValue)
                maxValue = step;
        }

        maxValue = CalculateUtils.calculateStepChartMaxValue(maxValue);

        Timber.e("---max="+maxValue);

        //最大值的所占的宽度
        float maxValueWidth = MiscUtil.getTextWidth(yPaint,String.valueOf(maxValue));

        //图表的x轴终止点
        float chartWidth = mWidth-maxValueWidth-singleBarWidth;



        float timeHeight = MiscUtil.measureTextHeight(xTxtPaint);

        float xItemWidth = chartWidth / 4;

        float singleWidth = MiscUtil.getTextWidth(xTxtPaint,"00:00");

        //屏幕平分的宽度
        float barAvgWidth = chartWidth / list.size();

        float modulus = (mHeight - (timeHeight*5f)) / (maxValue == 0 ? 300 : maxValue);

        Timber.e("----modules="+modulus);

        //绘制3条Y轴刻度线
        //绘制3条标线，0点线是x轴线
        //绘制x坐标轴
        canvas.drawLine(0f,-timeHeight-MiscUtil.dipToPx(getContext(),2f),chartWidth,-timeHeight-MiscUtil.dipToPx(getContext(),2f),xLinPaint);

        //当数据为空时，绘制三条Y轴线，但不显示数值，所以就赋值了300
        if(maxValue == 0)
            maxValue = 300;


        int middleV = maxValue / 2;

        float maxYHeight = (mHeight - (timeHeight*5f));


        canvas.drawText(maxValue == 300 ? " ":"0",chartWidth,-timeHeight-MiscUtil.dipToPx(getContext(),2f),yPaint);
        canvas.drawText(maxValue == 300 ? " ":(maxValue/2+""),chartWidth,-modulus*maxValue/2-timeHeight/2,yPaint);
        canvas.drawText(maxValue == 300 ? " ":(maxValue+""),chartWidth,-modulus*maxValue-timeHeight/2,yPaint);
        canvas.drawLine(0,-maxYHeight/2,chartWidth,-maxYHeight/2,xLinPaint);
        canvas.drawLine(0,-modulus*maxValue,chartWidth, -maxYHeight,xLinPaint);


        if(list != null && list.size()>0){
            for(int i = 0;i<list.size();i++){
                StepItem stepItem = list.get(i);
                int step = list.get(i).getStep();
                float x = i * barAvgWidth + barAvgWidth/2 -  singleBarWidth/2  ;
                float y = -step * modulus-MiscUtil.dipToPx(getContext(),3f)-timeHeight;
                Timber.e("----y="+y+" "+step);
                if(step != 0){
                    RectF rectF = new RectF(x,-timeHeight-MiscUtil.dipToPx(getContext(),3f),x+singleBarWidth,y);
                    pillarPaint.setColor(list.get(i).isChecked() ? Color.parseColor("#67E08E") : Color.parseColor("#5067E08E"));
                    canvas.drawRoundRect(rectF,10f,10f,pillarPaint);
                }

                //日
                if(stepType == StepType.DAY){
                    xTxtPaint.setTextAlign(Paint.Align.LEFT);
                    if(i == 0 || i == 6 || i == 12 || i == 18 || i == 23){
                        String timeTxt = stepItem.getHour()+":00";
                        canvas.drawText(timeTxt,(x+singleBarWidth)-singleBarWidth/2-singleWidth/2,-2f,xTxtPaint);
                    }
                }


                //月
                if(stepType == StepType.MONTH || stepType == StepType.WEEK || stepType == StepType.YEAR){
                    String dayStr = stepType == StepType.MONTH ? String.valueOf(stepItem.getHour()) :stepItem.getWeekXStr();
                    xTxtPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(dayStr,(x+singleBarWidth)-singleBarWidth/2,-2f,xTxtPaint);
                }

                if(list.get(i).isChecked()){
                    if(list.get(i).getPoint() == null){
                        list.get(i).setPoint(new Point((int) (x+singleBarWidth/2),0));
                    }
                    canvasTopRectf(canvas,list.get(i));
                }

            }
        }


    }



    //绘制显示的月
    private void canvasTopMonthRectf(Calendar calendar,StepItem stepItem){

    }


    //绘制显示计步
    private void canvasTopRectf(Canvas canvas,StepItem stepItem) {

        Point point = stepItem.getPoint();
        if(point == null)
        {
           return;
        }

        float paddingWidth = MiscUtil.dipToPx(getContext(),8f);
        //宽度
        float mTxtWidth = MiscUtil.getTextWidth(clickPaint,stepItem.getStep()+"step");
        float mTxtHeight = MiscUtil.measureTextHeight(clickPaint);

        float x = point.x-(mTxtWidth/2)-paddingWidth;
        Timber.e("---x="+x);
        if(x <0)
            x = 0;
        RectF rectF = new RectF(x,stepType == StepType.DAY ? -mHeight+mTxtHeight*2.5f+paddingWidth :  -mHeight+mTxtHeight*2f+paddingWidth,x == 0 ? point.x+mTxtWidth + paddingWidth : point.x+mTxtWidth/2+paddingWidth,-mHeight);

        canvas.drawRoundRect(rectF,10f,10f,rectPaint);

        float txtX = point.x-(mTxtWidth/2);
        canvas.drawText(stepItem.getStep()+"step",txtX<0 ? paddingWidth/2: txtX,-mHeight+mTxtHeight+paddingWidth/2,clickPaint);

        if(stepType == StepType.DAY){
            String hourStr = stepItem.getHour()+":00";
            float hourWidth = MiscUtil.getTextWidth(clickPaint,hourStr);
            canvas.drawText(hourStr,hourWidth,-mHeight+mTxtHeight*2.8f,clickPaint);

        }

        canvas.drawLine(point.x,-MiscUtil.dipToPx(getContext(),10f),point.x,stepType == StepType.DAY ? -mHeight+mTxtHeight*2.5f+paddingWidth : -mHeight+mTxtHeight*1.2f+paddingWidth,xTxtPaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();

            Timber.e("-----clic="+x +" "+y);
            if(list == null || list.isEmpty())
                return false;
            for(StepItem stepItem : list){
                stepItem.setChecked(false);
                stepItem.setPoint(new Point(0,0));
            }

            //最大值的所占的宽度
            float maxValueWidth = MiscUtil.getTextWidth(yPaint,String.valueOf(maxValue));

            //图表的x轴终止点
            float chartWidth = mWidth-maxValueWidth-singleBarWidth;

            //单个柱子的宽度
            float singleBarWidth = MiscUtil.dipToPx(getContext(),8f);
            //屏幕平分的宽度
            float barAvgWidth = chartWidth / list.size();




            for(int i = 0;i<list.size();i++){
                int step = list.get(i).getStep();
                float barX = i * barAvgWidth + barAvgWidth/2 -  singleBarWidth/2  ;


                //点击到了
                if(x>barX && x<barX+singleBarWidth){
                    Timber.e("-----点击到了="+(barX+singleBarWidth/2));
                    list.get(i).setChecked(true);
                    list.get(i).setPoint(new Point((int)((barX+singleBarWidth/2)),(int)y));
                    break;
                }
            }

           invalidate();

        }

        return super.onTouchEvent(event);
    }

    public OneDayStepModel getOneDayStepModel() {
        return oneDayStepModel;
    }


    public StepType getStepType() {
        return stepType;
    }

    //设置数据源类型，在设置数据源前设置
    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    /**日的数据源，一天24个数据，每小时一条数据**/
    public void setOneDayStepModel(OneDayStepModel oneDayStepModel) {
        this.oneDayStepModel = oneDayStepModel;
        maxValue = 0;
        //得到数据源
        String stepStr = oneDayStepModel.getDetailStep();
        list = new Gson().fromJson(stepStr,new TypeToken<List<StepItem>>(){}.getType());
        invalidate();
    }
}
