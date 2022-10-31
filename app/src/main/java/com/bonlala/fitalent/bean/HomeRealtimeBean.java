package com.bonlala.fitalent.bean;

/**
 * 实时心率bean
 * Created by Admin
 * Date 2022/10/25
 * @author Admin
 */
public class HomeRealtimeBean {

    /**实时心率是否打开**/
    private boolean isOpenRealtimeHr;

    /**实时心率的值**/
    private int hrValue;

    public HomeRealtimeBean() {
    }

    public HomeRealtimeBean(boolean isOpenRealtimeHr, int hrValue) {
        this.isOpenRealtimeHr = isOpenRealtimeHr;
        this.hrValue = hrValue;
    }

    public boolean isOpenRealtimeHr() {
        return isOpenRealtimeHr;
    }

    public void setOpenRealtimeHr(boolean openRealtimeHr) {
        isOpenRealtimeHr = openRealtimeHr;
    }

    public int getHrValue() {
        return hrValue;
    }

    public void setHrValue(int hrValue) {
        this.hrValue = hrValue;
    }
}
