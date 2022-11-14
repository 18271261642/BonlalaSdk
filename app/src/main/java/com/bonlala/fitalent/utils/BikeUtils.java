package com.bonlala.fitalent.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import com.bonlala.fitalent.R;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin
 * Date 2022/3/25
 * @author Admin
 */
public class BikeUtils {


    private static final long onDay = 86400000L;

    /** Aug 30,2022格式**/
    private static final  SimpleDateFormat enSdf = new SimpleDateFormat("MMM dd,yyyy",Locale.ENGLISH);
    /**格式化历史数据中的周 12/1~12/6 > MM dd**/
    private static final SimpleDateFormat enWeekSdf = new SimpleDateFormat("MM dd",Locale.ENGLISH);
    private static final SimpleDateFormat enWeekSdf2 = new SimpleDateFormat("d,yyyy",Locale.ENGLISH);
    /**月份**/
    private static final SimpleDateFormat enMonth = new SimpleDateFormat("d,yyyy",Locale.ENGLISH);
    public static final String endTimeFormat = "MMM dd,yyyy HH:mm:ss";
    /**年月日时分秒**/
    private static final SimpleDateFormat enTime = new SimpleDateFormat(endTimeFormat,Locale.ENGLISH);

    // 字符串的非空
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 将yyyy-MM-dd格式的日期格式化为英文 周 月 日格式的日期
     * @param dayStr yyyy-MM-dd格式
     * @return 日期
     */
    public static String getFormatEnglishDate(String dayStr){
        try {
            long time = simpleDateFormat.parse(dayStr).getTime();
            return enSdf.format(new Date(time));

        }catch (Exception e){
            e.printStackTrace();
            return enSdf.format(new Date());
        }
    }

    /**
     * 格式化对应格式的中文到英文
     * @param dayStr eg:yyyy-MM-dd HH:mm:ss
     * @param format EEE MMM,yyyy HH:mm:ss
     * @return 对应格式
     */
    public static String getFormatEnglishData(String dayStr,String format){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format,Locale.CHINA);
            long time = simpleDateFormat.parse(dayStr).getTime();
            return enTime.format(new Date(time));

        }catch (Exception e){
            e.printStackTrace();
            return enTime.format(new Date());
        }
    }



    public static String getFormatDate(long time,String format){
       SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);
        return sdf.format(new Date(time));
    }

    public static String getFormatDate(long time,String format,Locale local){
        SimpleDateFormat sdf = new SimpleDateFormat(format,local);
        return sdf.format(new Date(time));
    }


    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime2(String birthTimeString) {
        // 先截取到字符串中的年、月、日
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;// 先大致赋值
        if (yearMinus < 0) {// 选了未来的年份
            age = 0;
        } else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            } else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0) {
            if (monthMinus < 0) {// 当前月>生日月
            } else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {
                } else if (dayMinus >= 0) {
                    age = age - 1;
                }
            } else if (monthMinus > 0) {
                age = age - 1;
            }
        }
        return age;
    }


    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime(String birthTimeString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        if (birthTimeString.contains(".")) {
            formatter = new SimpleDateFormat("yyyy.MM.dd",Locale.CHINA);
        } else if (birthTimeString.contains("-")) {
            formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        }

        Date date = null;
        try {
            date = formatter.parse(birthTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 得到当前时间的年、月、日
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH) + 1;
            int dayNow = cal.get(Calendar.DATE);
            //得到输入时间的年，月，日
            cal.setTime(date);
            int selectYear = cal.get(Calendar.YEAR);
            int selectMonth = cal.get(Calendar.MONTH) + 1;
            int selectDay = cal.get(Calendar.DATE);
            // 用当前年月日减去生日年月日
            int yearMinus = yearNow - selectYear;
            int monthMinus = monthNow - selectMonth;
            int dayMinus = dayNow - selectDay;
            int age = yearMinus;// 先大致赋值
            if (yearMinus <= 0) {
                age = 0;
            }
            if (monthMinus < 0) {
                age = age - 1;
            } else if (monthMinus == 0) {
                if (dayMinus < 0) {
                    age = age - 1;
                }
            }
            return age;
        }
        return 0;
    }


    //将yyyy-MM-dd格式的日期转换成long 豪秒形式
    public static long transToDate(String dateStr){
        try {
            return Objects.requireNonNull(simpleDateFormat.parse(dateStr)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //将long格式转换为天
    public static String formatLongToDay(long time,boolean isMonth){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isMonth ? "MM": "dd",Locale.CHINA);
        return simpleDateFormat.format(time);
    }

    //根据天获取年
    public static int getDayOfYear(String dayStr){
        try {
            long time = simpleDateFormat.parse(dayStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return calendar.get(Calendar.YEAR);
        }catch (Exception e){
            e.printStackTrace();
            return 2000;
        }
    }


    /**
     * 将日期拆分为 年 月 日
     * @param str yyyy-MM-dd格式日期
     * @return 数 组
     */
    public static int[] getDayArrayOfStr(String str){
        int[] array = new int[3];
        try {
            long time = simpleDateFormat.parse(str).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            array[0] = calendar.get(Calendar.YEAR);
            array[1] = calendar.get(Calendar.MONTH)+1;
            array[2] = calendar.get(Calendar.DAY_OF_MONTH);
            return array;
        }catch (Exception e){
            e.printStackTrace();
            return array;
        }
    }


    /**
     * 获取日期的前一天或后一天
     * @param day 日前 yyyy-MM-dd格式
     * @param isBeforeDay true 前一天 false 后一天
     * @return
     */
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    public static long getBeforeOrAfterDay(String day,boolean isBeforeDay){
        try {
            long time = simpleDateFormat.parse(day).getTime();
            return isBeforeDay ? time - onDay : time + onDay;

        } catch (ParseException e) {
            e.printStackTrace();
            return onDay;
        }
    }


    public static String getBeforeOrAfterDayStr(String day,boolean isBeforeDay){
        try {
            long time = simpleDateFormat.parse(day).getTime();
            long d =  isBeforeDay ? time - onDay : time + onDay;
            return getFormatDate(d,"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
            return getCurrDate();
        }
    }


    public static long getBeforeOrAfterDay(String day,int dayNumber){
        try {
            long time = simpleDateFormat.parse(day).getTime();
            return (onDay*dayNumber)+time;

        } catch (ParseException e) {
            e.printStackTrace();
            return onDay;
        }
    }


    /**
     * 获取日期的月
     * @param dayStr yyyy-MM-dd格式
     * @return yyyy-MM
     */
    public static String getDayByMonth(String dayStr){
        try {
            long time = simpleDateFormat.parse(dayStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            long monthTime = calendar.getTimeInMillis();
            return getFormatDate(monthTime,"yyyy-MM");

        } catch (ParseException e) {
            e.printStackTrace();
            return getFormatDate(System.currentTimeMillis(),"yyyy-MM");
        }
    }


    /**
     * 获取日期的月
     * @param dayStr yyyy-MM-dd格式
     * @return yyyy-MM
     */
    public static String getDayByMonthEn(String dayStr){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM",Locale.ENGLISH);
            long time = sdf.parse(dayStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            long monthTime = calendar.getTimeInMillis();
            return getFormatDate(monthTime,"MMM,yyyy",Locale.ENGLISH);

        } catch (ParseException e) {
            e.printStackTrace();
            return getFormatDate(System.currentTimeMillis(),"MMM,yyyy",Locale.ENGLISH);
        }
    }



    /**
     * 获取上一个月或下一个月
     * @param month 当前的月份，传入 yyyy-MM格式
     * @param  isLast 是否是上个月，true上个月，false下个月
     * @return 转换的月份 yyyy-MM格式
     */
    public static String getNextOrLastMonth(String month,boolean isLast){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM",Locale.CHINA);
        try {
            long time = sdf.parse(month).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            calendar.add(Calendar.MONTH,isLast ? -1 : 1);

            long monthTime = calendar.getTimeInMillis();
            return getFormatDate(monthTime,"yyyy-MM");

        } catch (ParseException e) {
            e.printStackTrace();
            return getFormatDate(System.currentTimeMillis(),"yyyy-MM");
        }
    }


    /**
     * 获取月份的第一天和最后一天的日期
     * @param month 月份 yyyy-MM格式
     * @param isFirst 是否是第一天，true第一天，false后一天
     * @return yyyy-MM-dd
     */
    public static String getMonthFirstOrLastDay(String month,boolean isFirst){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM",Locale.CHINA);
        long time = 0;
        try {
            time = sdf.parse(month).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            if(isFirst){
                calendar.set(Calendar.DAY_OF_MONTH,1);
            }else{
                calendar.roll(Calendar.DAY_OF_MONTH,-1);
            }
            return getFormatDate(calendar.getTimeInMillis(),"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
            return  getCurrDate();
        }
    }

    /**
     * 获取月份的第一天和最后一天的日期
     * @param month 月份 yyyy-MM格式
     * @param isFirst 是否是第一天，true第一天，false最后一天
     * @return yyyy-MM-dd
     */
    public static int getMonthLastDay(String month,boolean isFirst){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM",Locale.CHINA);
        long time = 0;
        try {
            time = sdf.parse(month).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            if(isFirst){
                calendar.set(Calendar.DAY_OF_MONTH,1);
            }else{
                calendar.roll(Calendar.DAY_OF_MONTH,-1);
            }
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return 30;
        }

    }



    //判断两个日期是否相等
    public static boolean isEqualDay(String day1,String day2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);

        try {
            long time1 = sdf.parse(day1).getTime();
            long time2 = sdf.parse(day2).getTime();
            return time1 == time2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 比较两个日期是否相等，日期1和日期2的格式必须相同
     * @param format 格式
     * @param day1 日期1
     * @param day2 日期2
     * @return 是否相等
     */
    public static boolean isEqualDay(String format,String day1,String day2){
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);

        try {
            long time1 = sdf.parse(day1).getTime();
            long time2 = sdf.parse(day2).getTime();
            return time1 == time2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 比较两个日期大小
     * @param  format 格式化
     * @param leftDay yyyy-MM
     * @param rightDay yyyy-MM
     * @return
     */
    public static boolean daySize(String format,String leftDay,String rightDay){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.CHINA);
            long left = sdf.parse(leftDay).getTime();
            long right = sdf.parse(rightDay).getTime();
            return left > right;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 比较两个日期大小
     * @param leftDay yyyy-MM-dd
     * @param rightDay yyyy-MM-dd
     * @return
     */
    public static boolean daySize(String leftDay,String rightDay){
        try {
            long left = simpleDateFormat.parse(leftDay).getTime();
            long right = simpleDateFormat.parse(rightDay).getTime();
            return left > right;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //当前前的日期 yyyy-MM-dd转long 0当前，1昨天，2前天
    public static String getBeforeDayStr(int day){
        long date = transToDate(getCurrDate()) + (day * 86400000L);
        return getFormatDate(date,"yyyy-MM-dd");
    }

    //当前前的日期 yyyy-MM-dd转long 0当前，1昨天，2前天
    public static long getBeforeDay(long day,int dayCount){
        return day - (dayCount * 86400000L);
    }

    public static long getBeforeDay(int day){
        return transToDate(getCurrDate()) + (day * 86400000L);
    }


    //获取long类型时间是当月的第几周
    public static int formatDateOfWeek(long time){
        //获取当月的周数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //当月第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);

        return week;

    }


    //获取long类型时间是当月的第几周 年-月-第几周，eg:2022年7月第2周 = 2022-07-2
    public static String formatDateOfWeek(long time,int valid){
        //获取当月的周数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //当月第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;


        return year+"-"+String.format("%02d",month)+"-"+week;

    }


    /**
     * 两个日期相差多少天

     * @return
     */
    public static int dayIntervalOfDay(String day1,String day2){
        try {
            long time1 = simpleDateFormat.parse(day1).getTime();
            long time2 = simpleDateFormat.parse(day2).getTime();
            long interval = time1-time2;
            return (int) Math.abs(interval / onDay);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    //获取日期的周一到周五的日期
    public static String getDateWendAndSunDay(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //是周几
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = weekDay == 1 ? 7 : weekDay - 1;

        //周一
        long wend = time - (dayOfWeek-1) * onDay;
        //周日
        long sun = time+((7-dayOfWeek) * onDay);

        String wendStr = getFormatDate(wend,"dd");
        String sunStr = getFormatDate(sun,"dd");

        return wendStr+"/"+sunStr;
    }


    //周日到周六
    public static String getWeekSunToSta(long time){
        String wFormat = "MM/dd";
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(time);
        Calendar firstW = getWeekFirstDate(start);
        Calendar end = getWeekLastDate(start);

        int lefYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);
        if(lefYear != endYear){
            String result = lefYear+" "+getFormatDate(firstW.getTimeInMillis(),wFormat)+"~"+endYear+" "+getFormatDate(end.getTimeInMillis(),wFormat);
            return result;
        }else{
            String result = getFormatDate(firstW.getTimeInMillis(),wFormat)+"~"+getFormatDate(end.getTimeInMillis(),wFormat);
            return result;
        }

    }


    //周日到周六
    public static String getWeekSunToStaForEnglish(long time){
        String wFormat = "MMM dd";
        String wFormat2 = "d,yyyy";
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(time);
        Calendar firstW = getWeekFirstDate(start);
        Calendar end = getWeekLastDate(start);

        int lefYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);
        if(lefYear != endYear){
            String result = lefYear+" "+getFormatDate(firstW.getTimeInMillis(),wFormat,Locale.ENGLISH)+"~"+endYear+" "+getFormatDate(end.getTimeInMillis(),wFormat2,Locale.ENGLISH);
            return result;
        }else{
            String result = getFormatDate(firstW.getTimeInMillis(),wFormat,Locale.ENGLISH)+"~"+getFormatDate(end.getTimeInMillis(),wFormat2,Locale.ENGLISH);
            return result;
        }

    }




    /**获取日期是周几**/
    public static int getDayForWeek(String day){
        try {
            long time = simpleDateFormat.parse(day).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            //是周几
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取日期是周几
     * @param context context
     * @param day yyyy-MM-dd
     * @return
     */
    public static String getWeekForDay(Context context,String day){
        int week = getDayForWeek(day);

        String[] weekResource = new String[]{context.getResources().getString(R.string.sun),context.getResources().getString(R.string.mon),context.getResources().getString(R.string.tue),
                context.getResources().getString(R.string.wed),context.getResources().getString(R.string.thu),context.getResources().getString(R.string.fri),
                context.getResources().getString(R.string.sat)};

        Map<Integer,String> map = new HashMap<Integer,String>();
        for(int i = 1;i<=weekResource.length;i++){
            map.put(i,weekResource[i-1]);
        }

        return map.get(week);
    }



    //获取日期的周一到周五的日期
    public static String getDateWendAndSunDay(long time,String format){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //是周几
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = weekDay == 1 ? 7 : weekDay - 1;

        //周一
        long wend = time - (dayOfWeek-1) * onDay;
        //周日
        long sun = time+((7-dayOfWeek) * onDay);

        String wendStr = getFormatDate(wend,format);
        String sunStr = getFormatDate(sun,format);

        return wendStr+"~"+sunStr;
    }


    public static Calendar getDayCalendar(String dayStr){
        try {
            long time = simpleDateFormat.parse(dayStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return calendar;
        }catch (Exception e){
            e.printStackTrace();
            return Calendar.getInstance();
        }
    }

    /**
     * 获取日期本周第一天
     *并且是当天的0时0分0秒
     * @return
     */
    public static Calendar getWeekFirstDate(Calendar calendar) {
        Calendar ca = Calendar.getInstance();

        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.setFirstDayOfWeek(Calendar.SUNDAY);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.MILLISECOND, 0);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return ca;
    }


    public static String getWeekFirstDateStr(Calendar calendar) {
        Calendar ca = Calendar.getInstance();

        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.setFirstDayOfWeek(Calendar.SUNDAY);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.MILLISECOND, 0);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return getFormatDate(ca.getTimeInMillis(),"yyyy-MM-dd");
    }

    /**
     * 获取日期本周最后一天
     *
     * @return
     */
    public static Calendar getWeekLastDate(Calendar calendar) {
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.setFirstDayOfWeek(Calendar.WEDNESDAY);
        ca.set(Calendar.HOUR_OF_DAY, 23);
        ca.set(Calendar.SECOND, 59);
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return ca;
    }


    /**
     * 获取日期本周最后一天
     *
     * @return
     */
    public static String getWeekLastDateStr(Calendar calendar) {
        Calendar ca = Calendar.getInstance(Locale.CHINA);
        ca.setTimeInMillis(calendar.getTimeInMillis());
        ca.setFirstDayOfWeek(Calendar.WEDNESDAY);
        ca.set(Calendar.HOUR_OF_DAY, 23);
        ca.set(Calendar.SECOND, 59);
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return getFormatDate(ca.getTimeInMillis(),"yyyy-MM-dd");
    }


    /**
     * 获取上个月月份
     * @return
     */
    public static  String getLastMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM",Locale.CHINA);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        // 设置为上一个月
        //calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        date = calendar.getTime();
        return format.format(date);
    }

    //判断月份有几天 yyyy-MM
    public static int dayOfMonthNumbers(String monthStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM",Locale.CHINA);
        try {
            long time = simpleDateFormat.parse(monthStr).getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int dayNumber = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            return dayNumber;

        } catch (ParseException e) {
            e.printStackTrace();
            return 30;
        }

    }

    //获取一年的第一天和最后一天，1月1日和12月31日
    public static String getYearFirstOrLastData(String dayStr,boolean isFirst){
        try {
            long time = simpleDateFormat.parse(dayStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            int year = calendar.get(Calendar.YEAR);
            return isFirst ? year+"-01-01" : year +"12-31";

        }catch (Exception e){
            e.printStackTrace();
            return isFirst ? "2000-01-01" : "2000-12-31";
        }
    }


    /**
     * 获取上一年或下一年
     * @param dayStr yyyy-MM-dd
     * @param isPreview ture上一年 false 下一年
     * @return
     */
    public static String getPreviewOrNextYear(String dayStr,boolean isPreview){
        try {
            long time = simpleDateFormat.parse(dayStr).getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int year = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR,isPreview ? year-1 : year+1);
            return getFormatDate(calendar.getTimeInMillis(),"yyyy-MM-dd");
        }catch (Exception e){
            e.printStackTrace();
            return getFormatDate(System.currentTimeMillis(),"yyyy-MM-dd");
        }
    }


    public static String getCurrDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
        return sdf.format(new Date());
    }

    public static String getCurrDate2(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        return sdf.format(new Date());
    }

    //邮箱正则
    public static boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    //手机号
    public static boolean isValidPhone(String phoneStr){
        String regex = "^1[3456789]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneStr);
        return matcher.matches();
    }


    /**
     * 将分钟格式化成HH时mm分:ss秒格式
     * @param
     * @return
     */
    public static String formatMinute(int second,Context context){
        if(second == 0){
            return "--";
        }
        int hour = second / 3600;
        int minute = (second - hour * 3600) / 60;

        String hourStr = hour == 0 ? "":(hour+context.getResources().getString(R.string.string_time_hour)+":");
        String minuteStr = minute == 0 ? "" : (minute+context.getResources().getString(R.string.string_time_min)+":");


        return hourStr+minuteStr;
    }

    /**
     * 将分钟格式化成HH时mm分:ss秒格式
     * @param minute 分钟
     * @return
     */
    public static String formatMinuteStr(int second,Context context){
        if(second == 0){
            return "--";
        }
        int hour = second / 3600;
        int minute = (second - hour * 3600) / 60;
        int secondes = second % 60;

        String hourStr = hour == 0 ? "":(hour+context.getResources().getString(R.string.string_time_hour)+":");
        String minuteStr = minute == 0 ? "" : (minute+context.getResources().getString(R.string.string_time_min)+":");
        String secondStr = secondes == 0 ? "" : (secondes+context.getResources().getString(R.string.string_time_second));

        return hourStr+minuteStr+secondStr;
    }

    /**
     * 将分钟格式化成HH:mm格式
     * @param minute 分钟
     * @return
     */
    public static String formatMinute(int minute){
        if(minute == 0)
            return "00:00";
        int hour = minute / 60;
        if(hour>=24)
            hour = hour-24;
        int minuteStr = minute % 60;
        return String.format("%02d",hour)+":"+String.format("%02d",minuteStr<0?1:minuteStr);
    }

    /**
     * 格式化时间
     * @param minute 分钟
     * @return
     */
    public static String formatHourAndMinute(int minute){
        if(minute == 0)
            return "0h0min";
        int hour = minute / 60;
        int minuteStr = minute % 60;
        return hour+"h"+minuteStr+"min";
    }


    public static String formatSecond(int second){
        try {
            if(second == 0){
                return "00:00:00";
            }
            int hour = second / 3600;
            int minute = (second - hour * 3600) / 60;
            int secondes = second % 60;

            return String.format("%02d",hour)+":"+String.format("%02d",minute)+":"+String.format("%02d",secondes);
        }catch (Exception e){
            e.printStackTrace();
            return "00:00:00";

        }
    }


    public static String formatSecond2(int time){
        if(time == 0){
            return "0H0M";
        }

        int hour = time / 3600;
        int minute = (time - hour * 3600) / 60;

        return hour+"H"+minute+"M";
    }


    //判断蓝牙状态
    public static boolean isBleEnable(Context context){
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null)
                return false;
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null)
                return false;
            return bluetoothAdapter.isEnabled();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public static void openBletooth(Activity context) {
        try {
            // 请求打开 Bluetooth
            Intent requestBluetoothOn = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
            requestBluetoothOn
                    .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            // 设置 Bluetooth 设备可见时间
            requestBluetoothOn.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    30 * 1000);
            // 请求开启 Bluetooth
            context.startActivityForResult(requestBluetoothOn,
                    1001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //C078 CB780A33DBF3
    public static String formatNameToMac(String name){
        if(isEmpty(name))
            return null;
        StringBuilder stringBuilder = new StringBuilder();
        String afterStr = StringUtils.substringAfter(name," ");
        char[] ctArray = afterStr.toCharArray();
        for(int i = 0;i<ctArray.length;i+=2){
            if(i+1<ctArray.length){
                char txt2 = ctArray[i];
                char txt = ctArray[i+1];
                stringBuilder.append(String.valueOf(txt2)+String.valueOf(txt)+":");
            }

        }
        if(stringBuilder.length() == 0)
            return null;
        stringBuilder.replace(stringBuilder.length()-1,stringBuilder.length(),"");
        return stringBuilder.toString();

    }



    //将Mac地址转成成无：的字符串 AA:BB->AABB
    public static String transMac(String mac){
        if(mac == null)
            return null;
        if(!mac.contains(":"))
            return null;
        String[] strArray = mac.split(":");
        StringBuilder resultStr = new StringBuilder();
        for(String st: strArray){
            resultStr.append(st);
        }
        return resultStr.toString();
    }


    /**
     * 根据时区获取 对应的 calendar
     *
     * @param tzone
     * @return
     */
    public static Calendar getCalendar(float tzone) {
        Calendar c = Calendar.getInstance();
        TimeZone tz = null;
        if (tzone == -100) {
            tzone = GetTimeZone();
            tz = TimeZone.getTimeZone(getTimeZone(tzone));
        } else {
            tz = TimeZone.getTimeZone(getTimeZone(tzone));
        }

        c.setTimeZone(tz);
        c.setMinimalDaysInFirstWeek(1);
        c.setFirstDayOfWeek(Calendar.SUNDAY);
//        c.setFirstDayOfWeek(Calendar.MONDAY);

        return c;
    }

    /**
     * 描述：获取手机TimeZone时区，单位 小时 包含了 夏时令 和 冬时令的 偏移
     *
     * @return
     */
    public static float GetTimeZone() {
        TimeZone tz = TimeZone.getDefault();

        // String s = "TimeZone:"+tz.getDisplayName(false, TimeZone.SHORT)+",id:"
        // +tz.getID();

        Calendar calendar = Calendar.getInstance();
        float f = (tz.getRawOffset() + calendar.get(Calendar.DST_OFFSET)) / 1000f / 60f / 60f;

        // LogUtil.showMsg(TAG+" GetTimeZone s:"+s+",f:" +f+",str:" +
        // StringUtil.DF_P_2.format(f));

        return (int) (f * 100) / 100f;
    }


    /**
     * 描述：根据时区的值，获取String 时区值
     *
     * @param tzone
     * @return
     */
    public static String getTimeZone(float tzone) {
        String timezone = "";
        int zone = (int) tzone;
        int mode = (int) ((tzone * 100) % 100);
        if (mode == 0) {// 整数
            if (tzone >= 0) {
                timezone = "GMT+" + zone;
            } else if (tzone == -100) {
                timezone = "";
            } else {
                timezone = "GMT" + zone;
            }
            // LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" + timezone);
        } else {

            int minute = (int) (60 * (mode / 100f));

            if (tzone >= 0) {
                timezone = "GMT+" + StringUtil.DF_2.format(zone) + ":" + StringUtil.DF_2.format(minute);
            } else if (tzone == -100) {
                timezone = "";
            } else {
                timezone = "GMT" + StringUtil.DF_2.format(zone) + ":" + StringUtil.DF_2.format(minute);
            }

            // LogUtil.showMsg(TAG+" getTimeZone tzone:" + tzone+",str:" + timezone);
        }
        return timezone;
    }

    public static String formatTwoStr(int number) {
        String strNumber = String.format("%02d", number);
        return strNumber;
    }



    //获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if(null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),    calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
    //获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if(null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }
}
