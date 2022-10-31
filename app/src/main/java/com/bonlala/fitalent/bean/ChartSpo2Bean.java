package com.bonlala.fitalent.bean;

import android.graphics.Point;

/**
 * Created by Admin
 * Date 2022/10/10
 * @author Admin
 */
public class ChartSpo2Bean {

    /**
     *  spo2值
     */
    private int spo2Value;

    /**
     * x轴的值
     */
    private String xValue;

    /**是否选中了**/
    private boolean isChecked;

    /**选中的xy点**/
    private Point point;


    public ChartSpo2Bean() {
    }

    public ChartSpo2Bean(int spo2Value, String xValue) {
        this.spo2Value = spo2Value;
        this.xValue = xValue;
    }

    public int getSpo2Value() {
        return spo2Value;
    }

    public void setSpo2Value(int spo2Value) {
        this.spo2Value = spo2Value;
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
        return "ChartSpo2Bean{" +
                "spo2Value=" + spo2Value +
                ", xValue='" + xValue + '\'' +
                ", isChecked=" + isChecked +
                ", point=" + point +
                '}';
    }
}
