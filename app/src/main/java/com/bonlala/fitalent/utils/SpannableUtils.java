package com.bonlala.fitalent.utils;

import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

/**
 * Created by Admin
 * Date 2022/12/26
 * @author Admin
 */
public class SpannableUtils {


    public static SpannableString getTargetType(String value, String unitType){

        String distance = value;

        distance = distance+" "+unitType;
        SpannableString spannableString = new SpannableString(distance);
        spannableString.setSpan(new AbsoluteSizeSpan(14,true),distance.length()-unitType.length(),distance.length(),SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
