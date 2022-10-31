package com.bonlala.fitalent.emu;

/**
 * Created by Admin
 * Date 2022/10/17
 * @author Admin
 * 用于判断存储到数据库数据类型的日期
 * eg: 类型：详细计步 ： 日期：2000-01-01
 */
public class DbType {

    /**
     * 连续计步的类型
     */
    public static final int DB_TYPE_STEP = 0x00;

    /**
     * 连续心率的类型
     */
    public static final int DB_TYPE_DETAIL_HR = 0x01;

    /**
     * 睡眠的类型
     */
    public static final int DB_TYPE_SLEEP = 0x02;


    /**
     * 锻炼的类型
     */
    public static final int DB_TYPE_EXERCISE = 0x03;

}
