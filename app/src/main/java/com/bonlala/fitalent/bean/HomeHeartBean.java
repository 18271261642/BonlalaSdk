package com.bonlala.fitalent.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2022/10/12
 * @author Admin
 */
public class HomeHeartBean {

    /**单次的心率值，手动测量**/
    private int singleHr;

    /**单次心率的测量时间**/
    private long singleHrTime;

    /**连续的心率值，1440个点，每个一分钟**/
    private List<Integer> hrList;

    /**
     * 心率的日期 yyyy-MM-dd格式
     */
    private String dayStr;

    /**连续心率的时间戳**/
    private long detailHrTime;

    public HomeHeartBean() {
    }



    /**计算平均心率，去除0的值**/
    public int calculateAvgHr(){
        if(hrList == null || hrList.isEmpty())
            return 0;
        //有效的总数
        int validCount = 0;
       int validNumbers = 0;
        for(Integer integer : hrList){
            if(integer != 0){
                validCount+=integer;
                validNumbers++;
            }
        }

        if(validCount == 0)
            return 0;
        return validCount / validNumbers;
    }


    /**
     * 将每分钟的心率数据处理成每5分钟的数据
     * @return
     */
    public List<Integer> getFiveMinuteHr(){
        if(hrList == null || hrList.isEmpty())
            return new ArrayList<>();
        List<Integer> fiveList = new ArrayList<>();
        for(int i = 0;i<hrList.size();i+=5){
            if(i+5<hrList.size()-1){
                //5个数据的有效数据
                int fiveValidCount = 0;
                int fiveValidNumbers = 0;
                for(int k = 0;k<5;k++){
                    if(hrList.get(k+i) != 0){
                        int hrv = hrList.get(k+i);
                        if(hrv == 254)
                            hrv = 0;
                        fiveValidCount += hrv;
                        fiveValidNumbers++;
                    }
                }
                if(fiveValidCount == 0){
                    fiveList.add(0);
                }else{
                    fiveList.add(fiveValidCount/fiveValidNumbers);
                }
            }
        }

        return fiveList;
    }


    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public int getSingleHr() {
        return singleHr;
    }

    public void setSingleHr(int singleHr) {
        this.singleHr = singleHr;
    }

    public long getSingleHrTime() {
        return singleHrTime;
    }

    public void setSingleHrTime(long singleHrTime) {
        this.singleHrTime = singleHrTime;
    }

    public List<Integer> getHrList() {
        return hrList;
    }

    public void setHrList(List<Integer> hrList) {
        this.hrList = hrList;
    }

    public long getDetailHrTime() {
        return detailHrTime;
    }

    public void setDetailHrTime(long detailHrTime) {
        this.detailHrTime = detailHrTime;
    }
}
