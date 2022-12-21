package com.bonlala.fitalent.bean;

/**
 * 心率带，锻炼记录
 * Created by Admin
 * Date 2022/12/16
 * @author Admin
 */
public class ExerciseHomeBean {

    /**平均心率**/
    private int avgHr;

    /**卡路里**/
    private int kcal;

    /**时长 HH:mm:ss**/
    private String sportDuration;

    /**锻炼次数**/
    private int sportTimes;

    /**日期**/
    private String day;

    public ExerciseHomeBean() {
    }

    public ExerciseHomeBean(int avgHr, int kcal, String sportDuration, int sportTimes, String day) {
        this.avgHr = avgHr;
        this.kcal = kcal;
        this.sportDuration = sportDuration;
        this.sportTimes = sportTimes;
        this.day = day;
    }

    public int getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(int avgHr) {
        this.avgHr = avgHr;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public String getSportDuration() {
        return sportDuration;
    }

    public void setSportDuration(String sportDuration) {
        this.sportDuration = sportDuration;
    }

    public int getSportTimes() {
        return sportTimes;
    }

    public void setSportTimes(int sportTimes) {
        this.sportTimes = sportTimes;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
