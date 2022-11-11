package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonlala.fitalent.R;

import androidx.annotation.Nullable;

/**
 * 舒张压的指示进度+收缩压值
 * Created by Admin
 * Date 2022/9/21
 */
public class CusDiastolicBpScheduleView extends LinearLayout {

    //舒张压值
    private TextView bpScheduleLBpTv;
    //刻度背景图片
    private ImageView bpScheduleLbpImg;
    //刻度指示图片
    private ImageView bpScheduleHScaleImg;


    public CusDiastolicBpScheduleView(Context context) {
        super(context);
        initViews(context);
    }

    public CusDiastolicBpScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CusDiastolicBpScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.item_diastolic_bp_schedule_layout,this,true);

        bpScheduleLBpTv = view.findViewById(R.id.bpScheduleLBpTv);
        bpScheduleLbpImg = view.findViewById(R.id.bpScheduleLbpImg);
        bpScheduleHScaleImg = view.findViewById(R.id.bpScheduleLScaleImg);

    }


    //设置空值，显示--，进度隐藏
    public void setEmptyData(){
        if(bpScheduleLBpTv == null)
            return;
        bpScheduleLBpTv.setText("--");
        bpScheduleLbpImg.setVisibility(GONE);
        bpScheduleHScaleImg.setVisibility(GONE);
    }


    //设置舒张压值，并显示刻度
    public void setDiastolicBpValue(int value){

        if(bpScheduleLBpTv == null)
            return;
        bpScheduleLbpImg.setVisibility(View.VISIBLE);
        bpScheduleHScaleImg.setVisibility(View.VISIBLE);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_low_bp_shedule_img);

        Bitmap scaleBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_bp_scale_img);

        bpScheduleLBpTv.setText(value+"");
        //背景图片的宽度
        float bgWidth = bitmap.getWidth();
        //刻度图片的宽度
        float scaleWidth = scaleBitmap.getWidth();

        //舒张压40-60/60-80/80-90/90-100/100-120
        float coefficientValue = bgWidth / (120-40);

        //搜索压值的进度
        float sbpValue = (value-40) * coefficientValue - scaleWidth/2;

        //开始平移
        TranslateAnimation translateAnimation = new TranslateAnimation(0,sbpValue, Animation.ABSOLUTE,Animation.ABSOLUTE);
        translateAnimation.setDuration(0);
        translateAnimation.setFillAfter(true);
        bpScheduleHScaleImg.startAnimation(translateAnimation);
        invalidate();
    }
}
