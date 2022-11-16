package com.bonlala.fitalent.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.activity.history.ExerciseDetailActivity;
import com.bonlala.fitalent.bean.ExerciseShowBean;
import com.bonlala.fitalent.db.DBManager;
import com.bonlala.fitalent.db.model.ExerciseModel;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.fitalent.utils.MmkvUtils;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 锻炼页面展示的数据
 * Created by Admin
 * Date 2022/10/19
 * @author Admin
 */
public class ExerciseAdapter extends AppAdapter<ExerciseShowBean>{

    boolean isChinese = BaseApplication.getInstance().getIsChinese();

    public ExerciseAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExerciseViewHolder();
    }

    private class ExerciseViewHolder extends AppAdapter<?>.ViewHolder{

        private TextView itemExerciseDateTv;
        private TextView itemExerciseTotalTv;
        private ImageView itemExerciseTempBackImg;

        private RecyclerView recyclerView;

        private CardView itemExerciseTitleCardView;

        public ExerciseViewHolder() {
            super(R.layout.item_exercise_layout);
            itemExerciseDateTv = findViewById(R.id.itemExerciseDateTv);
            itemExerciseTotalTv = findViewById(R.id.itemExerciseTotalTv);
            itemExerciseTempBackImg = findViewById(R.id.itemExerciseTempBackImg);
            recyclerView = findViewById(R.id.itemExerciseRecyclerView);
            itemExerciseTitleCardView = findViewById(R.id.itemExerciseTitleCardView);

        }

        @Override
        public void onBindView(int position) {
            ExerciseShowBean exerciseShowBean = getItem(position);

            itemExerciseDateTv.setText(isChinese ? exerciseShowBean.getDayStr() : BikeUtils.getFormatEnglishDate(exerciseShowBean.getDayStr()));
            itemExerciseTotalTv.setText(exerciseShowBean.getExerciseModelList().size()+" "+getContext().getResources().getString(R.string.string_exercise_times));

            ExerciseItemAdapter exerciseItemAdapter = new ExerciseItemAdapter(getContext());
            exerciseItemAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView recyclerView, View itemView, int position2) {
                    //判断是否有心率，没有心率就不跳转
                    String hrStr = getItem(position).getExerciseModelList().get(position2).getHrArray();
                    if(hrStr == null || "[]".equals(hrStr))
                        return;
                    int hrV = getItem(position).getExerciseModelList().get(position2).getAvgHr();
                    if(hrV == 0)
                        return;
                    Intent intent = new Intent(getContext(), ExerciseDetailActivity.class);
                    String str = new Gson().toJson(getItem(position).getExerciseModelList().get(position2));
                    intent.putExtra("exercise_item",str);
                    intent.putExtra("avg_hr",getItem(position).getExerciseModelList().get(position2).getAvgHr());
                    getContext().startActivity(intent);
                }
            });
            exerciseItemAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position2) {
                    DBManager.getInstance().deleteExercise("user_1001", MmkvUtils.getConnDeviceMac(),getItem(position).getExerciseModelList().get(position2).getStartTime());
                    return true;
                }
            });
            recyclerView.setAdapter(exerciseItemAdapter);
            List<ExerciseModel> list = exerciseShowBean.getExerciseModelList();
            Collections.sort(list, new Comparator<ExerciseModel>() {
                @Override
                public int compare(ExerciseModel exerciseModel, ExerciseModel t1) {
                    return t1.getEndTimeStr().compareTo(exerciseModel.getEndTimeStr());
                }
            });
            exerciseItemAdapter.setData(list);


            /**是否展开**/
            boolean isShow = exerciseShowBean.isShow();

            recyclerView.setVisibility(isShow ? View.VISIBLE : View.GONE);


            itemExerciseTitleCardView.setCardElevation(isShow ? 5f : 0f);

            if(getItem(position).isShow()){
                itemExerciseTempBackImg.setRotation(270);
            }else{
                itemExerciseTempBackImg.setRotation(90);
            }

            itemExerciseTitleCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                    if(getItem(position).isShow()){
//                        itemExerciseTempBackImg.setRotation(90);
//                    }else{
//                        itemExerciseTempBackImg.setRotation(270);
//                    }
                    getItem(position).setShow(!isShow);
                    notifyItemChanged(position);
                }
            });
        }
    }
}
