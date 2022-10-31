package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/10/9
 */
public class ScheduleView extends View {

    //背景的画笔
    private Paint bgPaint;
    //背景的颜色
    private int bgColor;

    //进度的画笔
    private Paint schedulePaint;
    //进度的颜色
    private int scheduleColor;


    public ScheduleView(Context context) {
        super(context);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
