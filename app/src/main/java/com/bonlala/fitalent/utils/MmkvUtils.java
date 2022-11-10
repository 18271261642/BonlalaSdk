package com.bonlala.fitalent.utils;

import com.tencent.mmkv.MMKV;


/**
 * Created by Admin
 * Date 2022/3/25
 */
public class MmkvUtils {

    //地址，test环境或preview环境
    private static final String NET_SERVER_KEY = "net_server_key";

    private final static String SAVE_FILE_KEY = "bonlala_key";
    public static final String TOKEN_KEY = "token_key";

    //用户注册的时间 long类型 毫秒
    private static final String USER_REGISTER_TIME_KEY = "user_register_time";

    //用户信息的bean
    private static final String USER_INFO_KEY = "user_info_key";
    /**连接的设备mac**/
    private static final String CONN_DEVICE_MAC = "b_conn_device_mac";
    private static final String CONN_DEVICE_NAME = "b_conn_device_name";
    //是否已经同意用户协议
    private static final String IS_AGREE_PRIVACY = "is_privacy";
    //公英制
    private static final String APP_UNIT = "app_unit";
    //减碳量的计算系数
    private static final String CO2_COM_MODULUS = "co2_modulus";
    //植树系数
    private static final String TREE_COM_MODULUS = "tree_modulus";

    /**天气对象Key**/
    private static final String WEATHER_BEAN_KEY = "weather_bean";

    /**计步目标**/
    private static final String STEP_GOAL_KEY = "step_goal";

    /**公英制**/
    private static final String IS_UNIT_KEY = "is_unit_key";

    /**温度单位**/
    private static final String TEMPERATURE_UNIT_KEY = "temperature_unit_key";

    /**W560B的电话提醒**/
    private static final String W560B_PHONE_CALL_KEY = "w560b_phone_switch";
    /**W560B的消息提醒**/
    private static final String W560B_APPS_KEY = "w560b_apps_key";

    /**设备连接失败的指引**/
    private static final String CONN_ERROR_DESC_KEY = "conn_error_desc";


    private static MMKV mmkv;

    public static void initMkv(){
        mmkv = MMKV.mmkvWithID(SAVE_FILE_KEY);
    }

    public static void setSaveParams(String k,String v){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }
        mmkv.putString(k,v);
    }

    public static void setSaveParams(String k,Boolean v){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }
        mmkv.putBoolean(k,v);
    }



    public static void setSaveObjParams(String k,Object v){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }

        if(v instanceof Integer){
            mmkv.putInt(k, (Integer) v);
        }

        if(v instanceof String){
            mmkv.putString(k, (String) v);
        }

        if(v instanceof Boolean){
            mmkv.putBoolean(k, (Boolean) v);
        }

        if(v instanceof  Long){
            mmkv.putLong(k, (Long) v);
        }

        if(v instanceof  Float){
            mmkv.putFloat(k, (Float) v);
        }


    }


    public static Object getSaveParams(String k,Object oj){
        if(mmkv == null){
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }
        if(oj instanceof String){
            return mmkv.getString(k, (String) oj);
        }

        if(oj instanceof Integer){
            return mmkv.getInt(k,(int)oj);
        }

        if(oj instanceof Long){
            return mmkv.getLong(k, (Long) oj);
        }

        if(oj instanceof Float){
            return mmkv.getFloat(k, (Float) oj);
        }

        if(oj instanceof Boolean){
            return mmkv.getBoolean(k, (Boolean) oj);
        }


        return mmkv.getString(k, (String) oj);

    }


    //设置app的环境
    public static void setNetServer(int code){
        setSaveObjParams(NET_SERVER_KEY,code);
    }

    //获取app的环境 0test；1dev;2preview
    public static int getNetServer(){
        return (int) getSaveParams(NET_SERVER_KEY,0);
    }



    //保存用户连接的Mac
    public static void saveConnDeviceMac(String mac){
        setSaveParams(CONN_DEVICE_MAC,mac);
    }

    //获取已经连接的Mac
    public static String getConnDeviceMac(){
        return (String) getSaveParams(CONN_DEVICE_MAC,"");
    }

    //保存用户连接的设备名称
    public static void saveConnDeviceName(String name){
        setSaveParams(CONN_DEVICE_NAME,name);
    }

    public static String getConnDeviceName(){
        return (String) getSaveParams(CONN_DEVICE_NAME,"");
    }


    public static void setIsAgreePrivacy(boolean isAgreePrivacy){
        setSaveParams(IS_AGREE_PRIVACY,isAgreePrivacy);
    }

    public static boolean getPrivacy(){
        return (boolean) getSaveParams(IS_AGREE_PRIVACY,false);
    }


    /**保存隐私政策url 完整的url**/
    public static void savePrivacyUrl(String url){
        setSaveParams("privacy_url",url);
    }
    public static String getPrivacyUrl(){
        return (String) getSaveParams("privacy_url",null);
    }

    /**保存用户协议**/
    public static void saveUserAgreement(String url){
        setSaveParams("user_agreement_url",url);
    }
    public static String getUserAgreement(){
        return (String) getSaveParams("user_agreement_url",null);
    }

    public static void saveGuideUrl(String url){
        setSaveParams("guide_url",url);
    }

    public static String getGuideUrl(String type){
        return (String) getSaveParams("guide_url",null);
    }


    /**保存计步目标**/
    public static void saveStepGoal(int step){
        setSaveObjParams(STEP_GOAL_KEY,step);
    }

    /**获取计步目标**/
    public static int getStepGoal(){
        return (int) getSaveParams(STEP_GOAL_KEY,8000);
    }


    /**保存公英制 true公制，false英制**/
    public static void saveUnit(boolean isUnit){
        setSaveObjParams(IS_UNIT_KEY,isUnit);
    }
    /**获取公英制**/
    public static boolean getUnit(){
        return (boolean) getSaveParams(IS_UNIT_KEY,false);
    }

    /**保存温度单位**/
    public static void saveTemperatureUnit(boolean isC){
        setSaveObjParams(TEMPERATURE_UNIT_KEY,isC);
    }
    /**
     * 获取温度单位 ture 为摄氏度
     */
    public static boolean getTemperature(){
        return (boolean) getSaveParams(TEMPERATURE_UNIT_KEY,false);
    }


    /**
     * 保存消息提醒的状态
     */
    public static void saveW560BAppsStatus(boolean isOpen){
        setSaveObjParams(W560B_APPS_KEY,isOpen);
    }
    /**
     * 获取消息提醒状态
     */
    public static boolean getW560BAppsStatus(){
        return (boolean) getSaveParams(W560B_APPS_KEY,false);
    }



    /**保存电话提醒的状态**/
    public static void saveW560BPhoneStatus(boolean isOpen){
        setSaveObjParams(W560B_PHONE_CALL_KEY,isOpen);
    }
    /**
     * 获取电话提醒的状态
     */
    public static boolean getW560BPhoneStatus(){
        return (boolean) getSaveParams(W560B_PHONE_CALL_KEY,false);
    }

    /**
     * 保存连接失败的指引
     */
    public static void saveConnErrorUrl(String url){
        setSaveParams(CONN_ERROR_DESC_KEY,url);
    }
    /**
     * 获取连接失败指引
     */
    public static String getConnErrorDesc(){
        return (String) getSaveParams(CONN_ERROR_DESC_KEY,null);
    }
}
