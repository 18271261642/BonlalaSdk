package com.bonlala.fitalent.bean;

import android.text.SpannableString;

/**
 * Created by Admin
 * Date 2022/10/28
 * @author Admin
 */
public class ExerciseItemBean {

    private String typeName;

    private SpannableString spannableString;


    public ExerciseItemBean() {
    }

    public ExerciseItemBean(String typeName, SpannableString spannableString) {
        this.typeName = typeName;
        this.spannableString = spannableString;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public SpannableString getSpannableString() {
        return spannableString;
    }

    public void setSpannableString(SpannableString spannableString) {
        this.spannableString = spannableString;
    }
}
