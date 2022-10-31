package com.bonlala.fitalent.emu;

/**
 * 连接状态枚举
 * Created by Admin
 * Date 2022/8/17
 * @author Admin
 */
public enum ConnStatus {
    /**未连接 默认状态**/
    NOT_CONNECTED,
    /**连接中**/
    CONNECTING,
    /**已经连接**/
    CONNECTED,

    /**数据同步中**/
    IS_SYNC_DATA,

    /**同步完成**/
    IS_SYNC_COMPLETE

    ;


   private ConnStatus() {
    }
}
