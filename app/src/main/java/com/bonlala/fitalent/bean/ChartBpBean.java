package com.bonlala.fitalent.bean;

import android.graphics.Point;

/**
 * Created by Admin
 * Date 2022/10/10
 */
public class ChartBpBean {

    //收缩压
    private int sysBp;

    //舒张压
    private int disBp;

    //x轴数据
    private String xValue;

    //是否选中
    private boolean isChecked;

    //点
    private Point point;

    public ChartBpBean() {
    }

    public ChartBpBean(int sysBp, int disBp, String xValue) {
        this.sysBp = sysBp;
        this.disBp = disBp;
        this.xValue = xValue;
    }

    public int getSysBp() {
        return sysBp;
    }

    public void setSysBp(int sysBp) {
        this.sysBp = sysBp;
    }

    public int getDisBp() {
        return disBp;
    }

    public void setDisBp(int disBp) {
        this.disBp = disBp;
    }

    public String getxValue() {
        return xValue;
    }

    public void setxValue(String xValue) {
        this.xValue = xValue;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "ChartBpBean{" +
                "sysBp=" + sysBp +
                ", disBp=" + disBp +
                ", xValue='" + xValue + '\'' +
                ", isChecked=" + isChecked +
                ", point=" + point +
                '}';
    }
}
