package com.bonlala.fitalent.utils;

import java.util.Locale;

/**
 * Created by Admin
 * Date 2022/7/11
 */
public class LanguageUtils {


    //是否是中文地区
    public static boolean isChinese(){
        String locale  = Locale.getDefault().getLanguage();
       return  !BikeUtils.isEmpty(locale) && locale.equals("zh");
    }
}
