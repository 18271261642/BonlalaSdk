package com.blala.blalable;

/**
 * Created by Admin
 * Date 2021/9/8
 */
public class UserInfoBean {

    private int year;
    private int month;
    private int day;

    private int weight;
    private int height;
    private int sex;
    private int maxHeart;
    private int minHeart;


    public UserInfoBean() {
    }

    public UserInfoBean(int year, int month, int day, int weight, int height, int sex, int maxHeart, int minHeart) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.maxHeart = maxHeart;
        this.minHeart = minHeart;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getMaxHeart() {
        return maxHeart;
    }

    public void setMaxHeart(int maxHeart) {
        this.maxHeart = maxHeart;
    }

    public int getMinHeart() {
        return minHeart;
    }

    public void setMinHeart(int minHeart) {
        this.minHeart = minHeart;
    }
}
