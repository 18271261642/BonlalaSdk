package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/6/2
 */
public class NaviTxt extends androidx.appcompat.widget.AppCompatTextView {
    private Typeface typeface;

    public NaviTxt(@NonNull Context context) {
        super(context);
        typeface = Typeface.createFromAsset(context.getAssets(), "bebasNeue.otf");
        this.setTypeface(typeface);
    }

    public NaviTxt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        typeface = Typeface.createFromAsset(context.getAssets(), "bebasNeue.otf");
        this.setTypeface(typeface);
    }

    public NaviTxt(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typeface = Typeface.createFromAsset(context.getAssets(), "bebasNeue.otf");
        this.setTypeface(typeface);
    }


}
