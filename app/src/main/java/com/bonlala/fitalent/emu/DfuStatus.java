package com.bonlala.fitalent.emu;

/**
 * Created by Admin
 * Date 2022/11/7
 * @author Admin
 */
public enum DfuStatus {

    //最新版本状态，点击关闭页面
    DFU_LAST_VERSION,

    //有新版本，点击下载固件状态
    DFU_NEW_VERSION,

    //固件下载中状态
    DFU_DOWNLOADING,

    //固件下载完成，开始升级状态，还没有升级，点击按钮开始升级
    DFU_READY_UPGRADE,

    //固件升级中状态
}
