package com.bonlala.fitalent.db.model;

import com.bonlala.fitalent.utils.CalculateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 锻炼数据
 * Created by Admin
 * Date 2022/10/19
 * @author Admin
 */
public class ExerciseModel extends LitePalSupport {

    /**userid**/
    private String userId;

    /**mac**/
    private String deviceMac;

    /**name**/
    private String deviceName;

    /**日期**/
    private String dayStr;

    /**时间戳**/
    private long saveTime;

    /**运动类型**/
    private int type;

    /**平均心率**/
    private int avgHr;

    /**开始的yyyy-MM-dd HH:mm:ss**/
    private String startTime;

    /**运动总小时**/
    private int sportHour;

    /**运动总分钟**/
    private int sportMinute;

    /**运动总秒**/
    private int sportSecond;

    /**结束的yyyy-MM-dd HH:mm:ss**/
    private String endTime;

    /**运动总步数**/
    private int countStep;

    /**运动平均速度 单位米**/
    private int avgSpeed;

    /**运动总距离 米**/
    private int distance;

    /**运动卡路里**/
    private int kcal;

    /**心率数据，集合转字符串**/
    private String hrArray;

    /**计步的集合**/
    private String stepArray;

    /**卡路里的集合**/
    private String kcalArray;


    /**
     * 新增字段 心率带运动 输入的名称
     */
    private String hrBeltInputName;


    public String getHrBeltInputName() {
        return hrBeltInputName;
    }

    public void setHrBeltInputName(String hrBeltInputName) {
        this.hrBeltInputName = hrBeltInputName;
    }

    /**获取时分秒**/
    public String getHourMinute(){
        return String.format("%02d",sportHour)+":"+String.format("%02d",sportMinute)+":"+String.format("%02d",sportSecond);
    }

    /**转换成秒**/
    public int changeSecond(){
        return sportHour * 60 + sportMinute * 60 + sportSecond;
    }


    /**开始时间**/
    public String getStartTimeStr(){
        String hourStr = StringUtils.substringAfter(startTime," ");
        return hourStr;
    }

    public String getEndTimeStr(){
        String endStr = StringUtils.substringAfter(endTime," ");
        return endStr;
    }

    /**获取锻炼的时长**/
    public int getExerciseTime(){
        return sportHour*60 + sportMinute+(sportSecond/60);
    }

    /**获取锻炼的时间，秒**/
    public int getExerciseMinute(){
        return sportHour*60*60 + sportMinute*60+sportSecond;
    }


//    /**
//     * 计算平均速度
//     * @param distance 米
//     * @return
//     */
//    public float calculateSpeed(int distance){
//         int allDistance =
//    }


    /**
     * 获取心率的集合
     * @return 集合
     */
    public List<Integer> getHrList(){
        if(hrArray == null || hrArray.equals("[]")){
            return null;
        }
        List<Integer> list = new Gson().fromJson(hrArray,new TypeToken<List<Integer>>(){}.getType());
        return list == null || list.isEmpty() ? null : list;
    }

    private List<Integer> hourList = new ArrayList<>();
    public List<Integer> getHourHrList(){
        hourList.clear();
        List<Integer> hrList = getHrList();
        if(hrList == null){
            return hourList;
        }

        //判断有多少条数据
        int size = hrList.size();

        int beforeV = size / 60;
        int overValue = size % 60;
        //不够60的情况
        if(beforeV ==0 && overValue > 0){
            int avgHr = getAvgValue(hrList);
            hourList.add(avgHr);
            return hourList;
        }

        for(int i = 0;i<hrList.size();i+=60){
            if(i + 60 <hrList.size()){
                List<Integer> tempList = hrList.subList(i,i+60);
                int hr = getAvgValue(tempList);
                hourList.add(hr);
            }else{
                int v = hrList.size() % 60;
                int vHr = getAvgValue(hrList.subList(i,i + v));
                hourList.add(vHr);
            }
        }

        return hourList;

    }



    /**计算平均值**/
    private int getAvgValue(List<Integer> list){
        int count = 0;
        for(int i = 0;i<list.size();i++){
            count += list.get(i);
        }


        return count == 0 ? 0 : count / list.size();
    }



    public String getHrArray() {
        return hrArray;
    }

    public void setHrArray(String hrArray) {
        this.hrArray = hrArray;
    }

    public String getStepArray() {
        return stepArray;
    }

    public void setStepArray(String stepArray) {
        this.stepArray = stepArray;
    }

    public String getKcalArray() {
        return kcalArray;
    }

    public void setKcalArray(String kcalArray) {
        this.kcalArray = kcalArray;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(int avgHr) {
        this.avgHr = avgHr;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getSportHour() {
        return sportHour;
    }

    public void setSportHour(int sportHour) {
        this.sportHour = sportHour;
    }

    public int getSportMinute() {
        return sportMinute;
    }

    public void setSportMinute(int sportMinute) {
        this.sportMinute = sportMinute;
    }

    public int getSportSecond() {
        return sportSecond;
    }

    public void setSportSecond(int sportSecond) {
        this.sportSecond = sportSecond;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCountStep() {
        return countStep;
    }

    public void setCountStep(int countStep) {
        this.countStep = countStep;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    @Override
    public String toString() {
        return "ExerciseModel{" +
                "userId='" + userId + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", dayStr='" + dayStr + '\'' +
                ", saveTime=" + saveTime +
                ", type=" + type +
                ", avgHr=" + avgHr +
                ", startTime='" + startTime + '\'' +
                ", sportHour=" + sportHour +
                ", sportMinute=" + sportMinute +
                ", sportSecond=" + sportSecond +
                ", endTime='" + endTime + '\'' +
                ", countStep=" + countStep +
                ", avgSpeed=" + avgSpeed +
                ", distance=" + distance +
                ", kcal=" + kcal +
                ", hrArray='" + hrArray + '\'' +
                ", stepArray='" + stepArray + '\'' +
                ", kcalArray='" + kcalArray + '\'' +
                '}';
    }
}
