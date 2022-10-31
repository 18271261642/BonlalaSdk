package com.blala.blalable.listener;

import java.util.List;

/**
 * 返回24小时数据，包括心率和睡眠
 * Created by Admin
 * Date 2021/12/7
 */
public interface WriteBack24HourDataListener {

    void onWriteBack(byte[] data);
}
