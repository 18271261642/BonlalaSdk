package com.bonlala.fitalent.listeners;

/**
 * 开始或结束的接口
 * Created by Admin
 * Date 2022/12/14
 * @author Admin
 */
public interface OnStartOrEndListener {

    /**
     * 开始或结束
     * @param isStart true 开始，false 结束
     */
    void startOrEndStatus(boolean isStart);

    /**
     * 结束运动，把开始运动和运动时长带上
     * @param 开始时间 ；运动时长，秒
     */
    void endSportStatus(long startTime,int sportTime);
}
