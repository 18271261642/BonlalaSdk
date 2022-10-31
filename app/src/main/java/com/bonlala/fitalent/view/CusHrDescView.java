package com.bonlala.fitalent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.google.gson.Gson;

import java.util.List;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Created by Admin
 * Date 2022/10/14
 * @author Admin
 */
public class CusHrDescView extends LinearLayout {

    /**区间1**/
    private CusScheduleView hrColor1View;
    private TextView hr1TimeTv;
    /**区间2**/
    private CusScheduleView hrColor2View;
    private TextView hr2TimeTv;
    /**区间3**/
    private CusScheduleView hrColor3View;
    private TextView hr3TimeTv;
    /**区间4**/
    private CusScheduleView hrColor4View;
    private TextView hr4TimeTv;
    /**区间5**/
    private CusScheduleView hrColor5View;
    private TextView hr5TimeTv;
    /**区间6**/
    private CusScheduleView hrColor6View;
    private TextView hr6TimeTv;


    public CusHrDescView(Context context) {
        super(context);
        initViews(context);
    }

    public CusHrDescView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CusHrDescView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }


    private void initViews(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.item_hr_schedule_layout,this,true);
        hrColor1View = view.findViewById(R.id.hrColor1View);
        hrColor2View = view.findViewById(R.id.hrColor2View);
        hrColor3View = view.findViewById(R.id.hrColor3View);
        hrColor4View = view.findViewById(R.id.hrColor4View);
        hrColor5View = view.findViewById(R.id.hrColor5View);
        hrColor6View = view.findViewById(R.id.hrColor6View);

        hr1TimeTv = view.findViewById(R.id.hr1TimeTv);
        hr2TimeTv = view.findViewById(R.id.hr2TimeTv);
        hr3TimeTv = view.findViewById(R.id.hr3TimeTv);
        hr4TimeTv = view.findViewById(R.id.hr4TimeTv);
        hr5TimeTv = view.findViewById(R.id.hr5TimeTv);
        hr6TimeTv = view.findViewById(R.id.hr6TimeTv);



    }


    public void setEmptyData(){
        hrColor1View.setAllScheduleValue(100);
        hrColor1View.setCurrScheduleValue(0);
        hr1TimeTv.setText("--"+getContext().getResources().getString(R.string.string_minute));

        hrColor2View.setAllScheduleValue(100);
        hrColor2View.setCurrScheduleValue(0);
        hr2TimeTv.setText("--"+getContext().getResources().getString(R.string.string_minute));

        hrColor3View.setAllScheduleValue(100);
        hrColor3View.setCurrScheduleValue(0);
        hr3TimeTv.setText("--"+getContext().getResources().getString(R.string.string_minute));


        hrColor4View.setAllScheduleValue(100);
        hrColor4View.setCurrScheduleValue(0);
        hr4TimeTv.setText("--"+getContext().getResources().getString(R.string.string_minute));


        hrColor5View.setAllScheduleValue(100);
        hrColor5View.setCurrScheduleValue(0);
        hr5TimeTv.setText("--"+getContext().getResources().getString(R.string.string_minute));

        hrColor6View.setAllScheduleValue(100);
        hrColor6View.setCurrScheduleValue(0);
        hr6TimeTv.setText("--"+getContext().getResources().getString(R.string.string_minute));
    }


    public void setScheduleData(List<Integer> list,int maxValue){
        Timber.e("---list="+new Gson().toJson(list)+" "+maxValue);
        if(hrColor1View == null)
            return;
        if(list.size()<5)
            return;
        hrColor1View.setAllScheduleValue(maxValue);
        hrColor1View.setCurrScheduleValue(list.get(0));
        hr1TimeTv.setText(list.get(0)+getContext().getResources().getString(R.string.string_minute));

        hrColor2View.setAllScheduleValue(maxValue);
        hrColor2View.setCurrScheduleValue(list.get(1));
        hr2TimeTv.setText(list.get(1)+getContext().getResources().getString(R.string.string_minute));

        hrColor3View.setAllScheduleValue(maxValue);
        hrColor3View.setCurrScheduleValue(list.get(2));
        hr3TimeTv.setText(list.get(2)+getContext().getResources().getString(R.string.string_minute));


        hrColor4View.setAllScheduleValue(maxValue);
        hrColor4View.setCurrScheduleValue(list.get(3));
        hr4TimeTv.setText(list.get(3)+getContext().getResources().getString(R.string.string_minute));


        hrColor5View.setAllScheduleValue(maxValue);
        hrColor5View.setCurrScheduleValue(list.get(4));
        hr5TimeTv.setText(list.get(4)+getContext().getResources().getString(R.string.string_minute));

        hrColor6View.setAllScheduleValue(maxValue);
        hrColor6View.setCurrScheduleValue(list.get(5));
        hr6TimeTv.setText(list.get(5)+getContext().getResources().getString(R.string.string_minute));
    }
}
