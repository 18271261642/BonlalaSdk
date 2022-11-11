package com.bonlala.fitalent.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.db.DBManager;
import com.bonlala.fitalent.db.model.UserInfoModel;

import java.util.Calendar;

/**
 * 心率计算类
 * Created by Admin
 * Date 2022/9/27
 * @author Admin
 */
public class HeartRateConvertUtils {



    //获取用户的最大心率，根据性别和年龄计算
    public static int getUserMaxHt(){
        UserInfoModel userInfoModel = DBManager.getUserInfo();
        if(userInfoModel == null){
            userInfoModel = new UserInfoModel();
            userInfoModel.setSex(0);
            userInfoModel.setUserBirthday("2000-01-01");
        }
        //年龄
        int age =getAge(userInfoModel.getUserBirthday());
        //男性 220-年龄
        return userInfoModel.getSex() == 0 ? 220 - age : 226 - age;
    }



    //点数,单次心率点数
    public static int hearRate2Point(int heartRate) {
        int maxHearRate = getUserMaxHt();
        int point = 0;
        //心率强度
        double hearStrength = hearRate2Percent(heartRate, maxHearRate);
        if (hearStrength < 50) {
            point = 0;
        } else if (hearStrength >= 50 && hearStrength < 60) {
            point = 1;
        } else if (hearStrength >= 60 && hearStrength < 70) {
            point = 2;
        } else if (hearStrength >= 70 && hearStrength < 80) {
            point = 3;
        } else if (hearStrength >= 80 && hearStrength < 90) {
            point = 4;
        } else if (hearStrength >= 90) {
            point = 5;
        }
        return point;
    }

    //点数,单次心率点数
    public static int hearRate2Point(int heartRate,double hrPercent) {
        int maxHearRate = getUserMaxHt();
        int point = 0;
        //心率强度
        double hearStrength =hrPercent;
        if (hearStrength < 50) {
            point = 0;
        } else if (hearStrength >= 50 && hearStrength < 60) {
            point = 1;
        } else if (hearStrength >= 60 && hearStrength < 70) {
            point = 2;
        } else if (hearStrength >= 70 && hearStrength < 80) {
            point = 3;
        } else if (hearStrength >= 80 && hearStrength < 90) {
            point = 4;
        } else if (hearStrength >= 90) {
            point = 5;
        }
        return point;
    }




    //根据点数获取颜色值
    public static int getColorByPoint(Context context,int point){
        if(point == 0){
            return context.getResources().getColor(R.color.hr_color_1);
        }
        if(point == 1){
            return context.getResources().getColor(R.color.hr_color_2);
        }
        if(point == 2){
            return context.getResources().getColor(R.color.hr_color_3);
        }
        if(point == 3){
            return context.getResources().getColor(R.color.hr_color_4);
        }
        if(point == 4){
            return context.getResources().getColor(R.color.hr_color_5);
        }
        if(point == 5){
            return context.getResources().getColor(R.color.hr_color_6);
        }
        return context.getResources().getColor(R.color.hr_color_1);
    }


    //根据点数设置背景图片
    public static Drawable setDrawable(Context context,int point){
        if(point == 0){
            return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_1);
        }
        if(point == 1){
            return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_2);
        }
        if(point == 2){
            return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_3);
        }
        if(point == 3){
            return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_4);
        }
        if(point == 4){
            return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_5);
        }
        if(point == 5){
            return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_6);
        }
        return context.getResources().getDrawable(R.mipmap.ic_real_time_hr_bg_1);
    }





    //心率强度
    public static double hearRate2Percent(int heartRate, int maxHeartRate) {
        double percent = 0;
        //心率强度
        percent = (heartRate * 1.0f / maxHeartRate) * 100;
        return percent;
    }


    //根据生日获取年龄
    public static int getAge(String birthday) {
        int age = 25;
        try {
            if (!TextUtils.isEmpty(birthday)) {
                String[] birthdays = birthday.split("-");
                Calendar calendar = Calendar.getInstance();
                int ageYear = Integer.parseInt(birthdays[0]);
                int currentYear = calendar.get(Calendar.YEAR);
                int ageMonth = Integer.parseInt(birthdays[1]);
                int ageDay = Integer.parseInt(birthdays[2]);
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                age = currentYear - ageYear;
                if (currentMonth < ageMonth) {
                    age--;
                } else if (currentMonth == ageMonth && currentDay < ageDay) {
                    age--;
                }
            } else {
                age = 25;
            }
        } catch (Exception e) {
            e.printStackTrace();
            age = 25;
        }

        return age;
    }
}
