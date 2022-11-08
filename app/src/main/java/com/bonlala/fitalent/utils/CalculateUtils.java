package com.bonlala.fitalent.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 计算类工具
 * Created by Admin
 * Date 2022/6/14
 * @author Admin
 */
public class CalculateUtils {


    /**公制转英制 1km = 0.6214英里**/
    private static final double kmToMiModulus = 0.6214d;

    /**摄氏度转华摄氏度 1摄氏度=33.8华摄氏度**/
    private static final double Fahrenheit_Modulus = 33.8f;

    /**1 cm = 0.3937 inch 英寸**/
    private static final double cmToInchModulus = 0.39737d;

    //1kg = 2.205磅
    private static final double kgToLbModulus = 2.2046226d;

    // 1 米=3.2808399 英尺
    private static final double mToFoot = 3.2808399d;

    private final static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private final static DecimalFormat df = new DecimalFormat("#");

//    //计算减碳量 保留一位小数
//    public static String calculateCo2(double distance){
//        //系数
//        float co2Modulus = MmkvUtils.getCo2Modulus();
//        return decimalFormat.format(mul( distance, (double) co2Modulus).floatValue());
//    }
//
//
//    public static float calculateCo2(float distance,int point){
//        //系数
//        float co2Modulus = MmkvUtils.getCo2Modulus();
//        String str = decimalFormat.format(mul((double) distance, (double) co2Modulus).floatValue());
//
//        return Float.parseFloat(str);
//    }
//
//    //计算植树
//    public static String calculateTree(float distance){
//        //系数
//        float treeModulus = MmkvUtils.getTreeCompute();
//        return decimalFormat.format(mul((double)distance,(double)treeModulus).floatValue());
//    }
//

    //公里转英里
    public static int kmToMiValue(int kmValue){
        double tmpV = mul((double) kmValue,kmToMiModulus);
        return (int) tmpV;
    }


    //公里转英里
    public static float kmToMiValue(float kmValue){
        if(kmValue == 0.0f)
            return 0.0f;
        double tmpV = mul((double) kmValue,kmToMiModulus);
        return Float.parseFloat(String.format("%.2f",tmpV));
    }


    //厘米转英寸
    public static int cmToInchValue(int cmValue){
        if(cmValue == 0)
            return 0;
        double tmpV = mul((double) cmValue,cmToInchModulus);
        return (int) tmpV;
    }

    /**
     * 英寸转厘米
     * @param inch 英寸
     * @return 厘米
     */
    public static int InchToCmValue(int inch){
        if(inch == 0)
            return 0;
        double tempV = mul((double) inch,2.5d);
        return (int) tempV;
    }


    public static float mToInchValue(int cmValue){
        if(cmValue == 0)
            return 0.0f;
        double tmv = mul((double) cmValue,cmToInchModulus);
        return Float.valueOf(String.format("%.2f",tmv));
    }


    //英寸转cm
    public static int footToCm(double ftValue){
        if(ftValue == 0.0d)
            return 0;
        double tmV = div(ftValue,cmToInchModulus,2);
        return (int) tmV;
    }


    //千克转英镑
    public static int kgToLbValue(int kgValue){
        if(kgValue == 0)
            return 0;
        double tmpV = mul((double) kgValue,kgToLbModulus);
        return Integer.parseInt(df.format(tmpV));
    }

    //千克转英镑
    public static float kgToLbValue(float kgValue){
        if(kgValue == 0.0f)
            return 0.0f;
        double tmpV = mul((double) kgValue,kgToLbModulus);
        return Float.parseFloat(String.format("%.2f",tmpV));
    }


    //英镑转千克
    public static int lbToKg(float lbValue){
        if(lbValue == 0.0f)
            return 0;
        double tmV = div(lbValue,kgToLbModulus,2);
        return (int) tmV;
    }


    //米转英尺
    public static float mToFoot(double mValue){
        if(mValue == 0.0d)
            return 0.0f;
        double tMvalue = mul(mValue,mToFoot);
        return Float.parseFloat(String.format("%.2f",tMvalue));
    }

    //米转英尺
    public static int mToFoot(int mValue){
        if(mValue == 0.0d)
            return 0;
        double tMvalue = mul((double) mValue,mToFoot);
        return (int) tMvalue;
    }


    /**
     * 摄氏度转华摄氏度
     * @return 华摄氏度
     * 1华氏度(℉) = 32 + 1摄氏度 x 1.8开氏度(K)
     */
    public static int celsiusToFahrenheit(int celsiusValue){

        double t = mul((double) celsiusValue,1.8d);
        double v = t + 32;

        return (int) v;

    }


    //小数加法
    public static float add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);

        return b1.add(b2).floatValue();
    }


    //米转千米，保利2位小时
    public static float mToKm(int m){
        if(m == 0)
            return 0f;
        return (float) div(m,1000,2);
    }



    //减法
    public static float sub(double d1,double d2){
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);

        return b1.subtract(b2).floatValue();
    }

    /**
     * 除法运算
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        if (v2 <= 0) {
            return 0;
        } else {

            BigDecimal b1 = new BigDecimal(String.valueOf(v1));
            BigDecimal b2 = new BigDecimal(String.valueOf(v2));

//            BigDecimal b1 = new BigDecimal(Double.toString(v1).replace(",", ""));
//            BigDecimal b2 = new BigDecimal(Double.toString(v2).replace(",", ""));
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

    /**
     * double 保留几位小数
     */
    public static float keepPoint(double value,int point){
        return (float) div(value,1,point);
    }


    /**
     * 两个double相乘
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }


    /**
     * 计算计步Y轴的最大值
     * @param value 最大值
     * @return
     */
    public static int calculateStepChartMaxValue(int value){
        if(value == 0)
            return 0;
        char[] charArray = String.valueOf(value).toCharArray();

        int firstIndex = Integer.parseInt(String.valueOf(charArray[0]));
        firstIndex ++;
        for(int i = 1;i<charArray.length;i++){
            firstIndex *=10;
        }

        return firstIndex;
    }

    /**
     * 获取这个最大数 数总共有几位
     *
     * @param value
     * @return
     */
    public static int getScale(float value) {//1203
        if (value >= 1 && value < 10) {
            return 0;
        }
        if (value == 0) {
            return 0;
        }
        if (value >= 10) {
            return 1 + getScale(value / 10);
        } else {
            return getScale(value * 10) - 1;
        }
    }


    /**
     * 计算配置
     */
    public static String getPace(int second){
        if(second == 0)
            return "--";
        int minute = second / 60;
        int se = second % 60;

        return minute+"'"+se+"''";
    }

    public static String getFloatPace(float pace){
        if(pace == 0.0f){
            return "--";
        }

        //保留两位小数
        String temp = decimalFormat.format(pace);
        String beforeStr = StringUtils.substringBefore(temp,".");
        String afterStr = StringUtils.substringAfter(temp,".");
        return beforeStr+"'"+afterStr+"''";
    }
}
