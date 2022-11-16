package com.bonlala.fitalent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.db.model.SleepItem;
import com.bonlala.fitalent.db.model.SleepModel;
import com.bonlala.fitalent.emu.SleepType;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.widget.utils.MiscUtil;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created by Admin
 * Date 2022/9/20
 * @author Admin
 */
public class SleepChartView extends View {

    //绘制睡眠色块的画笔
    private Paint sleepPaint;
    //开始和结束时间的画笔
    private Paint timePaint;
    //点击事件效果的画笔
    private Paint clickPaint;
    //点击事件背景的画笔
    private Paint clickBgPaint;
    //点击气泡内容的画笔
    private Paint clickTxtPaint;
    //点击的线画笔
    private Paint clickLinePaint;

    //是否绘制开始和结束时间
    private boolean isCanvasStartTime;

    //屏幕的宽度，高度
    private float mWidth,mHeight;

    float mSingleWidth;

    //睡眠的实体类对象
    private SleepModel sleepModel;

    //noData的画笔
    private Paint noDataPaint;

    //是否需要点击事件
    private boolean isNeedClick = false;

    float paddingWidth ;

    public SleepChartView(Context context) {
        super(context);
    }

    public SleepChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public SleepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.SleepChartView);

        typedArray.recycle();

        initPaint(context);
    }


    private void initPaint(Context context){
        sleepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sleepPaint.setStrokeWidth(1f);
        sleepPaint.setAntiAlias(true);
        sleepPaint.setStyle(Paint.Style.FILL);
        sleepPaint.setTextSize(1f);

        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setTextSize(MiscUtil.dipToPx(context,10f));
        timePaint.setAntiAlias(true);
        timePaint.setStrokeWidth(2f);

        noDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        noDataPaint.setTextSize(MiscUtil.dipToPx(context,18f));
        noDataPaint.setColor(Color.parseColor("#6E6E77"));
        noDataPaint.setAntiAlias(true);
        noDataPaint.setTextAlign(Paint.Align.CENTER);

        clickBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickBgPaint.setColor(Color.parseColor("#F4F6FA"));
        clickBgPaint.setAntiAlias(true);
        clickBgPaint.setStyle(Paint.Style.FILL);

        clickTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickTxtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clickTxtPaint.setAntiAlias(true);
        clickTxtPaint.setColor(Color.parseColor("#676767"));
        clickTxtPaint.setTextSize(MiscUtil.dipToPx(getContext(),12f));

        clickLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clickLinePaint.setColor(Color.parseColor("#DBDBDB"));
        clickLinePaint.setStrokeWidth(1f);
        clickLinePaint.setAntiAlias(true);
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
        if(sleepModel == null && isCanvasStartTime ){
            canvas.drawText(getContext().getResources().getString(R.string.string_no_data),mWidth/2,mHeight/2,noDataPaint);
            return;
        }
        canvasSleepChart(canvas);
    }


    //开始绘制睡眠图表
    private void canvasSleepChart(Canvas canvas){
        if(sleepModel == null)
            return;
        paddingWidth = MiscUtil.dipToPx(getContext(),8f);
        //总的睡眠时长
        int allSleepTime = sleepModel.getCountSleepTime();

        String startTime = BikeUtils.formatMinute(sleepModel.getFallAsleepTime());
        String endTime = BikeUtils.formatMinute(sleepModel.getWakeUpTime());
        float txtWidth = MiscUtil.getTextWidth(timePaint,endTime);

        //绘制开始和结束时间
        canvasStartAndEndTime(canvas);


        //屏幕的宽度/总的时间长=单位每分钟的睡眠所占屏幕宽度
         mSingleWidth = (mWidth -(isNeedClick ? txtWidth*2 : 0)) / allSleepTime;

         Timber.e("------sWidth="+mSingleWidth+" "+mWidth+" "+allSleepTime);
        String listStr = sleepModel.getSleepSource();

        List<SleepItem> sleepItemList = sleepModel.getSleepSourceList();

        if(sleepItemList == null)
            return;
        //累计的时间长
        int itemTotalAllTime = isNeedClick() ? (int) txtWidth :  0;
        //文字的高度
        float txtHeight = MiscUtil.measureTextHeight(timePaint);


        for(int i = 0;i<sleepItemList.size();i++){
            SleepItem sleepItem = sleepItemList.get(i);
           // Timber.e("-------sleepItem="+sleepItem.toString());
            //单个的长度
            int itemLength = sleepItem.getSleepLength();

            itemTotalAllTime += itemLength * mSingleWidth;

            //类型
            int type = sleepItem.getSleepType();
            //深睡
            if(type == SleepType.SLEEP_TYPE_DEEP){
                sleepPaint.setColor(getResources().getColor(R.color.sleep_deep_color));
            }
            //浅睡
            if(type == SleepType.SLEEP_TYPE_LIGHT){
                sleepPaint.setColor(getResources().getColor(R.color.sleep_light_color));
            }
            //清醒
            if(type == SleepType.SLEEP_TYPE_AWAKE){
                sleepPaint.setColor(getResources().getColor(R.color.sleep_awake_color));
            }
            if(type == 4){
                sleepPaint.setColor(getResources().getColor(R.color.sleep_rem_color));
            }

          //  RectF rectF = new RectF(itemTotalAllTime-itemLength*mSingleWidth,  MiscUtil.dipToPx(getContext(),isNeedClick ? 75f : 10f) ,isNeedClick ? (i ==sleepItemList.size()-1 ? itemTotalAllTime - txtWidth :  itemTotalAllTime) : itemTotalAllTime,mHeight-(isCanvasStartTime ? txtHeight : 0));

            RectF rectF = new RectF(itemTotalAllTime-itemLength*mSingleWidth,  MiscUtil.dipToPx(getContext(),isNeedClick ? 75f : 10f) ,isNeedClick ? ( itemTotalAllTime) : itemTotalAllTime,mHeight-(isCanvasStartTime ? txtHeight : 0));

            canvas.drawRect(rectF,sleepPaint);

            if(isNeedClick() && sleepItem.isClick()){
                if(sleepItem.getPoint() == null){
                    float x = itemTotalAllTime-itemLength*mSingleWidth+itemTotalAllTime - txtWidth;
                    sleepItem.setPoint(new Point((int) (x/2),0));
                }
                canvasClickBg(canvas,sleepItem);
            }
        }

    }


    //绘制点击的效果气泡
    private void canvasClickBg(Canvas canvas,SleepItem sleepItem){
        Point point = sleepItem.getPoint();
        if(point == null)
            return;
        Timber.e("-----丁阿基="+sleepItem.toString());
        float txtWidth = MiscUtil.getTextWidth(clickTxtPaint,"23:30~23:59");
        float txtHeight = MiscUtil.measureTextHeight(clickTxtPaint);
        RectF rectF = new RectF(point.x-txtWidth/2 -paddingWidth,0,point.x+txtWidth/2+paddingWidth,txtHeight*4f+paddingWidth);
        canvas.drawRoundRect(rectF,10f,10f,clickBgPaint);

        canvas.drawLine(point.x,rectF.bottom,point.x,mHeight,clickLinePaint);

        float marginTop = MiscUtil.dipToPx(getContext(),3f);
        //时间
        String startTime = BikeUtils.formatMinuteSleep(sleepItem.getStartTime())+"~"+BikeUtils.formatMinuteSleep(sleepItem.getEndTime());
        clickTxtPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(startTime,point.x,txtHeight+marginTop,clickTxtPaint);

        //睡眠状态
        String typeStr = getSleepType(sleepItem.getSleepType());
        canvas.drawText(typeStr,point.x,txtHeight*2.3f+marginTop,clickTxtPaint);

        //时长
        String timeLengthStr = BikeUtils.formatMinuteNoHour(sleepItem.getSleepLength(),getContext());
        canvas.drawText(timeLengthStr,point.x,txtHeight*4f+marginTop,clickTxtPaint);


    }

    //绘制开始和结束
    private void canvasStartAndEndTime(Canvas canvas){
        if(sleepModel == null)
            return;
        if(!isCanvasStartTime)
            return;
        String startTime = BikeUtils.formatMinuteSleep(sleepModel.getFallAsleepTime());
        String endTime = BikeUtils.formatMinuteSleep(sleepModel.getWakeUpTime());
        //文字的高度
        float txtHeight = MiscUtil.measureTextHeight(timePaint);
        //文字的宽度
        float txtWidth = MiscUtil.getTextWidth(timePaint,endTime);

        canvas.drawText(startTime,0,mHeight,timePaint);
        canvas.drawText(endTime,mWidth-txtWidth,mHeight,timePaint);
    }

    public boolean isCanvasStartTime() {
        return isCanvasStartTime;
    }

    public void setCanvasStartTime(boolean canvasStartTime) {
        isCanvasStartTime = canvasStartTime;
    }

    public SleepModel getSleepModel() {
        return sleepModel;
    }

    public void setSleepModel(SleepModel sleepModel) {
        this.sleepModel = sleepModel;
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();

            Timber.e("----xy="+x +" "+y+" "+event.getRawX()+" "+event.getRawY());
            if(sleepModel == null)
                return false;
            List<SleepItem> sleepItemList = sleepModel.getSleepSourceList();

            if(sleepItemList == null)
                return false;
            for(SleepItem st : sleepItemList){
                st.setClick(false);
            }


            float tempCount = MiscUtil.getTextWidth(timePaint,"00:00");
            for(SleepItem sleepItem : sleepItemList){
                int itemLength = sleepItem.getSleepLength();
                //对应的坐标
                float  tempX = mSingleWidth * itemLength;
                tempCount +=tempX;
                //开始的x
                float startX = tempCount - itemLength*mSingleWidth;
                //结束的X
                float endX = tempCount;


                if(x>=startX && x<=endX){
                    Timber.e("-------点击了这个区域="+sleepItem.toString());
                    sleepItem.setClick(true);
                    sleepItem.setPoint(new Point((int) (startX+(endX-startX)/2),0));
                    sleepModel.setSleepSource(new Gson().toJson(sleepItemList));
                    invalidate();
                    return true;
                }
            }

        }

        if(action == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();

            Timber.e("--move--xy="+x +" "+y);
        }

        return super.onTouchEvent(event);
    }


    public boolean isNeedClick() {
        return isNeedClick;
    }

    public void setNeedClick(boolean needClick) {
        isNeedClick = needClick;
    }

    private String getSleepType(int type){
        if(type == SleepType.SLEEP_TYPE_DEEP)
            return getContext().getResources().getString(R.string.string_deep);
        if(type == SleepType.SLEEP_TYPE_LIGHT){
            return getContext().getResources().getString(R.string.string_light_sleep);
        }
        if(type == SleepType.SLEEP_TYPE_AWAKE){
            return getContext().getResources().getString(R.string.string_awake);
        }
        if(type == 4){
            return "REM";
        }
        return getContext().getResources().getString(R.string.string_awake);
    }
}
