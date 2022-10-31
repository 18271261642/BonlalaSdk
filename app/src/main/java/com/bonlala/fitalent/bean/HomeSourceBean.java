package com.bonlala.fitalent.bean;

/**
 * 用于首页数据的数据data
 * Created by Admin
 * Date 2022/9/22
 */
public class HomeSourceBean {

    //类型 0实时心率；1计步汇总；2连续心率详情；3睡眠；4，单次血氧；5，单次血压；6运动详情
    private int dataType;

    //数据源,每个类型对应每个数据源，序列化后再反序列化
    private String dataSource;


    public HomeSourceBean() {
    }

    public HomeSourceBean(int dataType, String dataSource) {
        this.dataType = dataType;
        this.dataSource = dataSource;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
