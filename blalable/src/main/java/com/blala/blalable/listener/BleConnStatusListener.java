package com.blala.blalable.listener;

/**
 * 监听连接状态
 * Created by Admin
 * Date 2019/7/3
 * @author Admin
 */
public interface BleConnStatusListener {

    void onConnectStatusChanged(String mac,int status);
}
