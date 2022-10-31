package com.blala.blalable.listener;

/**
 * 连接状态
 * Created by Admin
 * Date 2019/7/1
 */
public interface ConnStatusListener {

    void connStatus(int status);

    void setNoticeStatus(int code);
}
