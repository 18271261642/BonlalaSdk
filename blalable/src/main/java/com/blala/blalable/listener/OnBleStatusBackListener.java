package com.blala.blalable.listener;

/**
 * Created by Admin
 * Date 2021/9/9
 */
public interface OnBleStatusBackListener {

    //查找手机
    void findPhone();

    //进入音乐模式
    void intoMusicStatus(int status);
}
