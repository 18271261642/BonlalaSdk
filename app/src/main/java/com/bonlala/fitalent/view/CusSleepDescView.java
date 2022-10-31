package com.bonlala.fitalent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.db.model.SleepModel;

import androidx.annotation.Nullable;

/**
 * 睡眠百分比进度条
 * Created by Admin
 * Date 2022/10/9
 * @author Admin
 */
public class CusSleepDescView extends LinearLayout {

    /**深睡的进度**/
    private CusScheduleView sleepDeepScheduleView;
    private TextView sleepDeepTimeTv;
    /**浅睡的进度**/
    private CusScheduleView sleepLightScheduleView;
    private TextView sleepLightTime;
    /**清醒的进度**/
    private CusScheduleView sleepAwakeScheduleView;
    private TextView sleepAwakeTime;
    /**rem进度**/
    private CusScheduleView sleepRemScheduleView;



    public CusSleepDescView(Context context) {
        super(context);
        initViews(context);
    }

    public CusSleepDescView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }


    private void initViews(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_sleep_scanl_view,this,true);
        sleepDeepScheduleView = view.findViewById(R.id.sleepDeepScheduleView);
        sleepLightScheduleView = view.findViewById(R.id.sleepLightScheduleView);
        sleepAwakeScheduleView = view.findViewById(R.id.sleepAwakeScheduleView);
        sleepRemScheduleView = view.findViewById(R.id.sleepRemScheduleView);
        sleepDeepTimeTv = view.findViewById(R.id.sleepDeepTimeTv);
        sleepLightTime = view.findViewById(R.id.sleepLightTime);
        sleepAwakeTime = view.findViewById(R.id.sleepAwakeTime);
    }


    /**根据睡眠设置对应的数据**/
    public void setSleepData(SleepModel sleepModel){
        if(sleepModel.getCountSleepTime() == 0){
            showEmptyData();
            return;
        }
        //总的睡眠时长
        int allTime = sleepModel.getCountSleepTime();
        setDeepData(sleepModel.getDeepTime(),allTime);
        setLightData(sleepModel.getLightTime(),allTime);
        setAwakeData(sleepModel.getAwakeTime(),allTime);

    }

    /**设置默认值**/
    private void showEmptyData(){
        sleepDeepScheduleView.setAllScheduleValue(100f);
        sleepDeepScheduleView.setCurrScheduleValue(0f);
        sleepLightScheduleView.setAllScheduleValue(100f);
        sleepLightScheduleView.setCurrScheduleValue(0f);
        sleepAwakeScheduleView.setAllScheduleValue(100f);
        sleepAwakeScheduleView.setCurrScheduleValue(0f);
    }

    /**设置深睡的进度**/
    public void setDeepData(int value,float maxValue){
        sleepDeepScheduleView.setAllScheduleValue(maxValue);
        sleepDeepScheduleView.setCurrScheduleValue(value);
        sleepDeepTimeTv.setText(value+"min");
        invalidate();
    }
    /**设置浅睡的进度**/
    public void setLightData(int value,float maxValue){
        sleepLightScheduleView.setAllScheduleValue(maxValue);
        sleepLightScheduleView.setCurrScheduleValue(value);
        sleepLightTime.setText(value+"min");
        invalidate();
    }

    /**设置清醒**/
    public void setAwakeData(int value,float maxValue){
        sleepAwakeScheduleView.setAllScheduleValue(maxValue);
        sleepAwakeScheduleView.setCurrScheduleValue(value);
        sleepAwakeTime.setText(value == 0? "--min":value+"min");
        invalidate();
    }


    /**设置REM的进度**/
    public void setRemData(float value,float maxValue){
        sleepRemScheduleView.setAllScheduleValue(maxValue);
        sleepRemScheduleView.setCurrScheduleValue(value);
        invalidate();
    }
}
