package com.blala.blalable.listener;

/**
 * Created by Admin
 * Date 2022/10/10
 */
public interface OnMeasureDataListener {

    //测量心率返回，手表测量后主动返回 time 毫秒时间戳
    void onMeasureHeart(int heart,long time);

    //测量血压返回
    void onMeasureBp(int sBp,int disBp,long time);

    //测量血氧返回
    void onMeasureSpo2(int spo2,long time);
}
