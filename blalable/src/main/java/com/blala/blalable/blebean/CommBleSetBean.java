package com.blala.blalable.blebean;

/**
 * Created by Admin
 * Date 2022/9/15
 * @author Admin
 */
public class CommBleSetBean {

    //公英制1英制，0公制
    private int metric;

    //中英文 1中文，0英文
    private int language;

    //12小时制 1 24小时
    private int timeType;

    //24小时心率开关 1关闭，0打开
    private int is24Heart;

    //温度单位 1华摄氏度 0摄氏度
    private int temperature;

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public int getIs24Heart() {
        return is24Heart;
    }

    public void setIs24Heart(int is24Heart) {
        this.is24Heart = is24Heart;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
