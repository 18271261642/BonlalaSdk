package com.bonlala.fitalent.bean;

/**
 * Created by Admin
 * Date 2022/9/15
 */
public class DialBean {

    private int dialId;

    private String previewUrl;

    private boolean isChecked;

    public DialBean() {
    }

    public DialBean(int dialId, String previewUrl, boolean isChecked) {
        this.dialId = dialId;
        this.previewUrl = previewUrl;
        this.isChecked = isChecked;
    }

    public int getDialId() {
        return dialId;
    }

    public void setDialId(int dialId) {
        this.dialId = dialId;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
