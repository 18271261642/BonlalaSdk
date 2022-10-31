package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ExerciseTypeBean;
import com.hjq.shape.view.ShapeTextView;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/10/20
 * @author Admin
 */
public class ExerciseFilterAdapter extends AppAdapter<ExerciseTypeBean> {


    public ExerciseFilterAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterViewHolder();
    }

    private final class FilterViewHolder extends AppAdapter<?>.ViewHolder{

        private ShapeTextView nameTv;

        public FilterViewHolder() {
            super(R.layout.item_exercise_filter_layout);
            nameTv = findViewById(R.id.itemExerciseFilterTv);
        }

        @Override
        public void onBindView(int position) {
            ExerciseTypeBean exerciseTypeBean = getItem(position);
            nameTv.setText(exerciseTypeBean.getTypeName());
            boolean isChecked = exerciseTypeBean.isChecked();
            nameTv.getShapeDrawableBuilder().setSolidColor(isChecked ? Color.parseColor("#4EDD7D") : Color.parseColor("#F7F7F9")).intoBackground();

            nameTv.setTextColor(isChecked ? Color.parseColor("#FFFFFF") : Color.parseColor("#6E6E77"));

        }
    }
}
