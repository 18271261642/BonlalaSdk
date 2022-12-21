package com.blala.blalable.listener;

import androidx.fragment.app.FragmentManager;

/**
 * Created by Admin
 * Date 2021/7/6
 * @author Admin
 */
public class InterfaceManager {



    //连接状态
    public BleConnStatusListener bleConnStatusListener;

    /**
     * 写入数据返回
     */
    public WriteBackDataListener writeBackDataListener;

    //写入数据返回，带tag
    public WriteBack24HourDataListener writeBack24HourDataListener;


    /**
     * 监听各种设备状态接口，查找手机，音乐控制等
     */
    public OnBleStatusBackListener onBleBackListener;


    //测量数据返回，心率、血压、血氧等
    public OnMeasureDataListener onMeasureDataListener;


    //app给蓝牙设备发送指令的回调
    public OnSendWriteDataListener onSendWriteDataListener;


    /**锻炼数据回调**/
    public OnExerciseDataListener onExerciseDataListener;



    //实时数据的回调
    public OnRealTimeDataListener onRealTimeDataListener;

    //存储的数据，24小时数据



    public void setOnSendWriteDataListener(OnSendWriteDataListener onSendWriteDataListener) {
        this.onSendWriteDataListener = onSendWriteDataListener;
    }

    public OnBleStatusBackListener getOnBleBackListener() {
        return onBleBackListener;
    }

    public void setOnBleBackListener(OnBleStatusBackListener onBleBackListener) {
        this.onBleBackListener = onBleBackListener;
    }

    public void setBleConnStatusListener(BleConnStatusListener bleConnStatusListener) {
        this.bleConnStatusListener = bleConnStatusListener;
    }

    public void setWriteBackDataListener(WriteBackDataListener writeBackDataListener) {
        this.writeBackDataListener = writeBackDataListener;
    }

    public void setWriteBack24HourDataListener(WriteBack24HourDataListener writeBack24HourDataListener) {
        this.writeBack24HourDataListener = writeBack24HourDataListener;
    }

    public void setOnRealTimeDataListener(OnRealTimeDataListener onRealTimeDataListener) {
        this.onRealTimeDataListener = onRealTimeDataListener;
    }

    public void setOnMeasureDataListener(OnMeasureDataListener onMeasureDataListener) {
        this.onMeasureDataListener = onMeasureDataListener;
    }

    public void setOnExerciseDataListener(OnExerciseDataListener onExerciseDataListener) {
        this.onExerciseDataListener = onExerciseDataListener;
    }
}
