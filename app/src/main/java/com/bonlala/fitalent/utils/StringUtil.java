package com.bonlala.fitalent.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/**
	 * yyyy-MM-dd
	 */
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 0.0 格式化数字，保留小数点后1位
	 */
	public static final DecimalFormat DF_P_1 = new DecimalFormat("0.0");
	/**
	 * 0.00 格式化数字，保留小数点后2位
	 */
	public static final DecimalFormat DF_P_2 = new DecimalFormat("0.00");
	/**
	 * 00 格式化数字，保留2位
	 */
	public static final DecimalFormat DF_2 = new DecimalFormat("00");
	
	public static boolean isJsonObject(String str) {
		if(TextUtils.isEmpty(str)) {
			return false;
		}
		try{
			new JSONObject(str);
		    return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean isJsonArray(String str) {
		if(TextUtils.isEmpty(str)) {
			return false;
		}
		try{
			new JSONArray(str);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static JSONArray getJsonArray(String str) {
		JSONArray json = null;
		if(!TextUtils.isEmpty(str)) {
			try{
				json = new JSONArray(str);
			}catch(Exception e){
			}
		}
		return json;
	}
	
	public static String getFileNameFromUrl(String url) {
		if (url != null && url.lastIndexOf("/") != -1) {
			return url.substring(url.lastIndexOf("/") + 1);
		}
		return url;
	}
	
	public static short[] str2ShortArray(String str) {
		short[] res = null;
		if(!TextUtils.isEmpty(str) && str.length() > 0) {
			str = str.replace("[", "");
			str = str.replace("]", "");
			String[] ss = str.split(",");
			res = new short[ss.length];
			for(int i=0;i<ss.length;i++) {
				res[i] = Short.valueOf(ss[i].trim());
			}
		}
		return res;
	}
	
	public static byte[] str2ByteArray(String str) {
		byte[] res = null;
		if(!TextUtils.isEmpty(str) && str.length() > 0) {
			str = str.replace("[", "");
			str = str.replace("]", "");
			String[] ss = str.split(",");
			final int len = ss.length;
			res = new byte[len];
			for(int i=0;i<len;i++) {
				res[i] = Byte.valueOf(ss[i].trim());
			}
		}
		return res;
	}
	
	/**
	 * 功能描述：是否为空白,包括null和""
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0 || "null".equals(str);
	}
	
	public static String formatMac(String mac) {
		if(!TextUtils.isEmpty(mac)) {
			if(mac.indexOf(":") == -1) {
				int len = mac.length();
				StringBuffer temp = new StringBuffer();
				for(int i=0;i<len-1;) {
					temp.append(mac.substring(i, i+2));
					i = i+2;
					if(i < len - 1) {
						temp.append(":");
					}
				}
				mac = temp.toString();
			}
		}
		return mac;
	}
	
	public static boolean checkPhoneNum(String phone) {
		if(TextUtils.isEmpty(phone)) {
			return false;
		}
		Pattern p = Pattern.compile("^((1[3,4,5,6,7,8,9]))\\d{9}$");
		Matcher m = p.matcher(phone);
		return m.matches();
	}
	
	public static boolean checkSN(String sn) {
		if(!TextUtils.isEmpty(sn) && sn.length() == 12) {
			return true;
		}
		return false;
	}
	
	public static String getDateTime(int time) {
		return getDateTime(time * 1000l);
	}
	
	public static String getDateTime(long time) {
		return DATE_FORMAT.format(new Date(time));
	}





}

















