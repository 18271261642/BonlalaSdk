package com.bonlala.fitalent.listeners;

/**
 * 用于测量完成后的回调，更新心率、血压、血氧的界面
 * Created by Admin
 * Date 2022/11/16
 * @author Admin
 */
public interface OnMeasureStatusListener {

    /**
     * 测量的状态
     * @param status 状态
     */
    void onMeasureStatus(int status);
}
