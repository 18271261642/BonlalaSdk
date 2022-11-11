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
 * 收缩压的指示进度+收缩压值
 * Created by Admin
 * Date 2022/9/21
 * @author Admin
 */
public class CusSystolicBpScheduleView extends LinearLayout {

    //收缩压值
    private TextView bpScheduleHBpTv;
    //刻度背景图片
    private ImageView bpScheduleHbpImg;
    //刻度指示图片
    private ImageView bpScheduleHScaleImg;


    public CusSystolicBpScheduleView(Context context) {
        super(context);
        initViews(context);
    }

    public CusSystolicBpScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CusSystolicBpScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.item_sys_bp_shedule_layout,this,true);

        bpScheduleHBpTv = view.findViewById(R.id.bpScheduleHBpTv);
        bpScheduleHbpImg = view.findViewById(R.id.bpScheduleHbpImg);
        bpScheduleHScaleImg = view.findViewById(R.id.bpScheduleHScaleImg);

    }


    public void setEmptyData(){
        if(bpScheduleHBpTv == null)
            return;
        bpScheduleHBpTv.setText("--");
        bpScheduleHbpImg.setVisibility(GONE);
        bpScheduleHScaleImg.setVisibility(GONE);
    }


    //设置收缩压值，并显示刻度
    public void setSystolicBpValue(int value){
        if(bpScheduleHBpTv == null)
            return;
        bpScheduleHbpImg.setVisibility(VISIBLE);
        bpScheduleHScaleImg.setVisibility(VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_height_bp_shedule_img);
        Bitmap scaleBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_bp_scale_img);
        bpScheduleHBpTv.setText(value+"");
        //背景图片的宽度
        float bgWidth = bitmap.getWidth();
        //刻度图片的宽度
        float scaleWidth = scaleBitmap.getWidth();

        //收缩压40-90/90-120/120-140/140-160/160-180
        float coefficientValue = bgWidth / (180-40);

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
