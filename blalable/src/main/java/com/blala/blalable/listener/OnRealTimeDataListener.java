package com.blala.blalable.listener;

/**
 * 实时数据的回调
 * Created by Admin
 * Date 2022/9/22
 */
public interface OnRealTimeDataListener {

    void realTimeData(int ht,int step,int kcal,int distance);
}
