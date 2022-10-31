package com.bonlala.fitalent.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Admin
 * Date 2022/10/31
 * @author Admin
 */
public class NotifyBean {

    /**
     * 序号
     */
    private int codeIndex;

    /**
     * APP名称
     */
    private String appName;


    /**
     * APP图标
     */
    private int resource;

    /**
     * 开关状态
     *
     */
    private boolean isChecked;


    public NotifyBean() {
    }

    public NotifyBean(String appName, int resource) {
        this.appName = appName;
        this.resource = resource;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getCodeIndex() {
        return codeIndex;
    }

    public void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
