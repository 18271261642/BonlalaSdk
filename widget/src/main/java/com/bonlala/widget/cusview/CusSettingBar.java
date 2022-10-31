package com.bonlala.widget.cusview;

import android.content.Context;
import android.util.AttributeSet;

import com.bonlala.widget.R;
import com.bonlala.widget.layout.SettingBar;

/**
 * Created by Admin
 * Date 2022/8/17
 */
public class CusSettingBar extends SettingBar {

    public CusSettingBar(Context context) {
        super(context);
    }

    public CusSettingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusSettingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void setIsClick(boolean isClick){
        setClickable(isClick);
        setBackground(getResources().getDrawable(R.drawable.gray_shap));
    }
}
