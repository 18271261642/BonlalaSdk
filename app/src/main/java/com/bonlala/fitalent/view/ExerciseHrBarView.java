package com.bonlala.fitalent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.utils.BikeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/10/20
 * @author Admin
 */
public class ExerciseHrBarView extends LinearLayout {

    private TextView exerciseTime6Tv,exerciseTime5Tv,exerciseTime4Tv,exerciseTime3Tv,exerciseTime2Tv,exerciseTime1Tv;

    private List<TextView> textViewList;

    public ExerciseHrBarView(Context context) {
        super(context);
        initViews(context);
    }

    public ExerciseHrBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public ExerciseHrBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }


    private void initViews(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_hr_chart_layout,this,true);
        exerciseTime6Tv = view.findViewById(R.id.exerciseTime6Tv);
        exerciseTime5Tv = view.findViewById(R.id.exerciseTime5Tv);
        exerciseTime4Tv = view.findViewById(R.id.exerciseTime4Tv);
        exerciseTime3Tv = view.findViewById(R.id.exerciseTime3Tv);
        exerciseTime2Tv = view.findViewById(R.id.exerciseTime2Tv);
        exerciseTime1Tv = view.findViewById(R.id.exerciseTime1Tv);
        textViewList = new ArrayList<>();
        textViewList.clear();
        textViewList.add(exerciseTime1Tv);
        textViewList.add(exerciseTime2Tv);
        textViewList.add(exerciseTime3Tv);
        textViewList.add(exerciseTime4Tv);
        textViewList.add(exerciseTime5Tv);
        textViewList.add(exerciseTime6Tv);


    }



    /**设置数据**/
    public void setExerciseTime(List<Integer> pointList){
        for(int i = 0;i<pointList.size();i++){
            int time = pointList.get(i);
            String timeStr = BikeUtils.formatSecond(time*60);
            textViewList.get(i).setText(timeStr);
        }
    }
}
