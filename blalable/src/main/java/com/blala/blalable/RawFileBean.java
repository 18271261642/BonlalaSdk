package com.blala.blalable;

/**
 * Created by Admin
 * Date 2021/9/16
 */
public class RawFileBean {

    private String fileName;
    private int resId;
    private byte[] arrays;

    public RawFileBean(String fileName, int resId, byte[] arrays) {
        this.fileName = fileName;
        this.resId = resId;
        this.arrays = arrays;
    }


    public byte[] getArrays() {
        return arrays;
    }

    public void setArrays(byte[] arrays) {
        this.arrays = arrays;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
