package com.bonlala.fitalent.db.model;

import org.litepal.crud.LitePalSupport;

/**
 * 用户bean
 * Created by Admin
 * Date 2022/8/9
 * @author Admin
 */
public class UserInfoModel extends LitePalSupport {


    //id
    private String userId;
    /**昵称**/
    private String nickName;
    /**性别 0男 1女**/
    private int sex;
    //身高
    private int userHeight;
    //体重
    private int userWeight;
    /**生日yyyy-MM-dd格式**/
    private String userBirthday;
    /**单位 0公制 1英制**/
    private int userUnit;

    /**头像图片地址**/
    private String headUrl;
    //背景地址
    private String backUrl;
    //头像的缩略图
    private String headThumbnailUrl;

    //目标，计步目标 步
    private int stepGoal;
    //目标，距离目标 米
    private int distanceGoal;
    //目标，卡路里目标
    private int kcalGoal;


    //备注
    private String remark;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(int userHeight) {
        this.userHeight = userHeight;
    }

    public int getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(int userWeight) {
        this.userWeight = userWeight;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public int getUserUnit() {
        return userUnit;
    }

    public void setUserUnit(int userUnit) {
        this.userUnit = userUnit;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getHeadThumbnailUrl() {
        return headThumbnailUrl;
    }

    public void setHeadThumbnailUrl(String headThumbnailUrl) {
        this.headThumbnailUrl = headThumbnailUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getDistanceGoal() {
        return distanceGoal;
    }

    public void setDistanceGoal(int distanceGoal) {
        this.distanceGoal = distanceGoal;
    }

    public int getKcalGoal() {
        return kcalGoal;
    }

    public void setKcalGoal(int kcalGoal) {
        this.kcalGoal = kcalGoal;
    }
}
