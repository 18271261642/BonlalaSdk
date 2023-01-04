package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ExerciseItemBean;
import com.bonlala.fitalent.db.DBManager;
import com.bonlala.fitalent.db.model.ExerciseModel;
import com.bonlala.fitalent.emu.W560BExerciseType;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.fitalent.utils.CalculateUtils;
import com.bonlala.fitalent.utils.MmkvUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * Created by Admin
 * Date 2022/10/19
 * @author Admin
 */
public class ExerciseItemAdapter extends AppAdapter<ExerciseModel> {


    public ExerciseItemAdapter(@NonNull Context context) {
        super(context);
    }



    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExerciseItemViewHolder();
    }

    private final class ExerciseItemViewHolder extends AppAdapter<?>.ViewHolder{

        private ConstraintLayout itemRecordWatchLayout;
        private TextView itemExerciseTypeNameTv;
        private ImageView itemSportTypeImg;
        private TextView itemExerciseStartTimeTv;
        private TextView itemSportTimeTv;


        //心率带的布局 ，非心率带时隐藏
        private ConstraintLayout itemHrBeltTitleLayout;
        //类型图片
        private ImageView itemHrSportTypeImg;
        //普通计时或分组计时
        private TextView itemHrTypeTv;
        //运动的名称
        private TextView itemHrExerciseTypeNameTv;
        //运动时长
        private TextView itemHrSportTimeTv;
        //开始和结束时间
        private TextView itemHrSportStartEndTimeTv;



        private RecyclerView itemExerciseTypeRy;

        public ExerciseItemViewHolder() {
            super(R.layout.item_sport_record_item_layout);

            itemHrExerciseTypeNameTv = findViewById(R.id.itemHrExerciseTypeNameTv);
            itemRecordWatchLayout = findViewById(R.id.itemRecordWatchLayout);
            itemExerciseTypeRy = findViewById(R.id.itemExerciseTypeRy);

            itemExerciseTypeNameTv = findViewById(R.id.itemExerciseTypeNameTv);
            itemSportTypeImg = findViewById(R.id.itemSportTypeImg);
            itemExerciseStartTimeTv = findViewById(R.id.itemExerciseStartTimeTv);
            itemSportTimeTv = findViewById(R.id.itemSportTimeTv);
//            itemExerciseDistanceTv = findViewById(R.id.itemExerciseDistanceTv);
//            itemExerciseKcalTv = findViewById(R.id.itemExerciseKcalTv);
//            itemExerciseStepTv = findViewById(R.id.itemExerciseStepTv);
//            itemExerciseAvgHrTv = findViewById(R.id.itemExerciseAvgHrTv);
//            itemExercisePaceTv = findViewById(R.id.itemExercisePaceTv);
//
//            itemExerciseSpeedTv = findViewById(R.id.itemExerciseSpeedTv);



            itemHrBeltTitleLayout = findViewById(R.id.itemHrBeltTitleLayout);
            itemHrSportTypeImg = findViewById(R.id.itemHrSportTypeImg);
            itemHrTypeTv = findViewById(R.id.itemHrTypeTv);
            itemHrSportTimeTv = findViewById(R.id.itemHrSportTimeTv);
            itemHrSportStartEndTimeTv = findViewById(R.id.itemHrSportStartEndTimeTv);




        }

        @Override
        public void onBindView(int position) {
            ExerciseModel exerciseModel = getItem(position);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
            itemExerciseTypeRy.setLayoutManager(gridLayoutManager);
            ItemAdapter itemAdapter = new ItemAdapter(getTypeMap(exerciseModel));
            itemExerciseTypeRy.setAdapter(itemAdapter);



//
//            int distance = exerciseModel.getDistance();
//            float disStr = CalculateUtils.mToKm(distance);
//
//            itemExerciseDistanceTv.setText(getTargetType(disStr+"","km"));
//            itemExerciseKcalTv.setText(getTargetType(exerciseModel.getKcal()+"","kcal"));
//            itemExerciseStepTv.setText(getTargetType(exerciseModel.getCountStep()+"","step"));
//            itemExerciseAvgHrTv.setText(getTargetType(exerciseModel.getAvgHr()+"","bpm"));
//            //itemExercisePaceTv.setText(getTargetType(exerciseModel));
//
//            itemExerciseSpeedTv.setText(getTargetType(exerciseModel.getAvgSpeed()+""," km/s"));


            //显示类型的图片
            int imgType = getTypeResource(exerciseModel.getType());

            Timber.e("----------类型="+DBManager.getBindDeviceType());
            //560B手表
            if(DBManager.getBindDeviceType() == 56011){
                itemRecordWatchLayout.setVisibility(View.VISIBLE);
                itemHrBeltTitleLayout.setVisibility(View.GONE);
                itemExerciseTypeNameTv.setText(W560BExerciseType.getW560BTypeName(exerciseModel.getType(),getContext()));
                itemExerciseStartTimeTv.setText(exerciseModel.getStartTimeStr()+"~"+exerciseModel.getEndTimeStr());
                itemSportTimeTv.setText(exerciseModel.getHourMinute());

                Glide.with(getContext()).load(imgType).into(itemSportTypeImg);
            }else{  //心率带
                itemRecordWatchLayout.setVisibility(View.GONE);
                itemHrBeltTitleLayout.setVisibility(View.VISIBLE);

                String cusName = exerciseModel.getHrBeltInputName();
                Timber.e("----cusName="+cusName);
                itemHrExerciseTypeNameTv.setText(BikeUtils.isEmpty(cusName) ? W560BExerciseType.getHrBeltInputType(exerciseModel.getType(),getContext()) : cusName);

                itemHrTypeTv.setText(W560BExerciseType.getW560BTypeName(exerciseModel.getType(),getContext()));
                itemHrSportTimeTv.setText(exerciseModel.getHourMinute());
                itemHrSportStartEndTimeTv.setText(exerciseModel.getStartTimeStr()+"~"+exerciseModel.getEndTimeStr());
                Glide.with(getContext()).load(imgType).into(itemHrSportTypeImg);
            }

        }
    }


    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemV> {

        private List<ExerciseItemBean> list ;

        public ItemAdapter(List<ExerciseItemBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ItemV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_exercise_type_layout,parent,false);
            return new ItemV(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemV holder, int position) {
           ExerciseItemBean exerciseItemBean = list.get(position);
            holder.type.setText(exerciseItemBean.getTypeName());
            holder.value.setText(exerciseItemBean.getSpannableString());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemV extends RecyclerView.ViewHolder{
            private TextView type;
            private TextView value;
            public ItemV(@NonNull View itemView) {
                super(itemView);
                type = itemView.findViewById(R.id.itemExerciseTypeTypeTv);
                value = itemView.findViewById(R.id.itemExerciseTypeValueTv);
            }
        }

    }



    private int getTypeResource(int type){
        Timber.e("------type="+type);
        if(type == W560BExerciseType.TYPE_WALK){
            return R.mipmap.ic_sport_walk;
        }
        if(type == W560BExerciseType.TYPE_RUN) {
            return R.mipmap.ic_sport_run;
        }
        if(type == W560BExerciseType.TYPE_RIDE) {
            return R.mipmap.ic_sport_ride;
        }
        if(type == W560BExerciseType.TYPE_MOUNTAINEERING){
            return R.mipmap.ic_sport_mountaineering;
        }

        if(type == W560BExerciseType.TYPE_FOOTBALL){
            return R.mipmap.ic_sport_football;
        }

        if(type == W560BExerciseType.TYPE_BASKETBALL){
            return R.mipmap.ic_sport_basketball;
        }

        if(type == W560BExerciseType.TYPE_PINGPONG){
            return R.mipmap.ic_sport_pingpong;
        }

        if(type == W560BExerciseType.TYPE_BADMINTON){
            return R.mipmap.ic_sport_badmination;
        }

        //心率带普通计时
        if(type == W560BExerciseType.HR_BELT_FORWARD_TYPE){
            return R.mipmap.ic_hr_belt_forward;
        }

        //系列带倒计时
        if(type == W560BExerciseType.HR_BELT_COUNTDOWN_TYPE){
            return R.mipmap.ic_hr_belt_countdown;
        }

        //心率带分组计时
        if(type == W560BExerciseType.HR_BELT_GROUP_TYPE){
            return R.mipmap.ic_hr_belt_group;
        }

        return R.mipmap.ic_sport_walk;
    }


    private List<ExerciseItemBean> getTypeMap(ExerciseModel exerciseModel){
        int type = exerciseModel.getType();
        /**
         * 判断是否是心率带，心率带有4组，最大、最小、平均心率和消耗
         */
        if(DBManager.getBindDeviceType() != 56011){
            List<ExerciseItemBean> list = new ArrayList<>();

            //心率的集合
            List<Integer> hrList = exerciseModel.getHrList();
            //最大心率
            int maxValue = hrList == null ? 0 : Collections.max(hrList);
            //最小心率
            int minValue = hrList == null ? 0 : Collections.min(hrList);


            //平均心率
            list.add(new ExerciseItemBean(getResources().getString(R.string.string_avg_hr),getTargetType(exerciseModel.getAvgHr() == 0 ? "--":
                    exerciseModel.getAvgHr()+"","bpm")));

            //最大心率
            list.add(new ExerciseItemBean(getResources().getString(R.string.string_max_hr),getTargetType(maxValue == 0 ? "--":
                    maxValue+"","bpm")));


            //最小心率
            list.add(new ExerciseItemBean(getResources().getString(R.string.string_min_hr),getTargetType(minValue == 0 ? "--":
                    minValue+"","bpm")));
            //卡路里
            list.add(new ExerciseItemBean(getResources().getString(R.string.string_consumption),getTargetType(exerciseModel.getKcal()+"","kcal")));
            return list;
        }else{
            if(type == W560BExerciseType.TYPE_WALK || type == W560BExerciseType.TYPE_RUN){
                int distance = exerciseModel.getDistance();
                float disStr = CalculateUtils.mToKm(distance);
                List<ExerciseItemBean> list = new ArrayList<>();

                //公英制
                boolean isKm = MmkvUtils.getUnit();


                list.add(new ExerciseItemBean(getResources().getString(R.string.string_distance),getTargetType((isKm ? disStr : CalculateUtils.kmToMiValue(disStr))+"",isKm ? "km" : "mi")));
                list.add(new ExerciseItemBean(getResources().getString(R.string.string_consumption),getTargetType(exerciseModel.getKcal()+"","kcal")));
                list.add(new ExerciseItemBean(getResources().getString(R.string.string_count_step),getTargetType(exerciseModel.getCountStep()+"",getResources().getString(R.string.string_step))));
                list.add(new ExerciseItemBean(getResources().getString(R.string.string_avg_hr),getTargetType(exerciseModel.getAvgHr() == 0 ? "--":
                        exerciseModel.getAvgHr()+"","bpm")));

                //计算速度
                float time = exerciseModel.getExerciseMinute();
                //速度=距离/时间
                double speed = CalculateUtils.div(exerciseModel.getAvgSpeed(),10,2);


                //计算配速
                double pace = CalculateUtils.div(time,isKm ? disStr : CalculateUtils.kmToMiValue(disStr),3);

                Timber.e("----配速="+pace);
                list.add(new ExerciseItemBean(getResources().getString(R.string.string_place), getTargetType(CalculateUtils.getFloatPace((float) pace),"")));


                list.add(new ExerciseItemBean(getResources().getString(R.string.string_speed),getTargetType((isKm ? CalculateUtils.keepPoint(speed,2) : CalculateUtils.keepPoint(CalculateUtils.kmToMiValue((float) speed),2))+"",isKm ? "km/h" : "mi/h")));

                return list;

            }else{
                List<ExerciseItemBean> list = new ArrayList<>();
                list.add(new ExerciseItemBean(getResources().getString(R.string.string_avg_hr),getTargetType(exerciseModel.getAvgHr() == 0 ? "--":exerciseModel.getAvgHr()+"","bpm")));
                list.add(new ExerciseItemBean(getResources().getString(R.string.string_consumption),getTargetType(exerciseModel.getKcal()+"","kcal")));

                return list;
            }
        }
    }


    private SpannableString getTargetType(String value, String unitType){

        String distance = value;

        distance = distance+" "+unitType;
        SpannableString spannableString = new SpannableString(distance);
        spannableString.setSpan(new AbsoluteSizeSpan(14,true),distance.length()-unitType.length(),distance.length(),SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK),distance.length()-unitType.length(),distance.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
