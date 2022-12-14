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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartHrBean;
import com.bonlala.fitalent.bean.ExerciseHomeBean;
import com.bonlala.fitalent.bean.HomeHeartBean;
import com.bonlala.fitalent.bean.HomeRealtimeBean;
import com.bonlala.fitalent.bean.HomeSourceBean;
import com.bonlala.fitalent.db.model.SingleBpModel;
import com.bonlala.fitalent.db.model.SingleSpo2Model;
import com.bonlala.fitalent.db.model.SleepModel;
import com.bonlala.fitalent.db.model.SumSportModel;
import com.bonlala.fitalent.emu.HomeDateType;
import com.bonlala.fitalent.listeners.OnItemClickListener;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.fitalent.utils.CalculateUtils;
import com.bonlala.fitalent.utils.HeartRateConvertUtils;
import com.bonlala.fitalent.utils.MmkvUtils;
import com.bonlala.fitalent.utils.SpannableUtils;
import com.bonlala.fitalent.view.CusDiastolicBpScheduleView;
import com.bonlala.fitalent.view.CusSystolicBpScheduleView;
import com.bonlala.fitalent.view.RealHrBarChartView;
import com.bonlala.fitalent.view.SleepChartView;
import com.bonlala.fitalent.view.StepChartViewUtils;
import com.bonlala.widget.view.CircleProgress;
import com.bonlala.widget.view.SwitchButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.hjq.shape.view.ShapeTextView;
import com.hjq.toast.ToastUtils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * ??????adapter
 * Created by Admin
 * Date 2022/9/22
 * @author Admin
 */
public class HomeUiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<HomeSourceBean> list;

    //????????????????????????
    StepChartViewUtils stepChartViewUtils;
    //???????????????x???
    private final List<String> heartXList= new ArrayList<>();

    private final Gson gson = new Gson();

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public HomeUiAdapter(Context context, List<HomeSourceBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getDataType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        //????????????
        if(viewType == HomeDateType.HOME_TYPE_REAL_HR){
            view = LayoutInflater.from(context).inflate(R.layout.item_real_time_hr_layout,parent,false);
            return new HomeRealHrViewHolder(view);
        }
        //????????????
        if(viewType == HomeDateType.HOME_TYPE_STEP){
            view = LayoutInflater.from(context).inflate(R.layout.item_step_layout,parent,false);
            return new HomeCountStepViewHolder(view);
        }

        //??????
        if(viewType == HomeDateType.HOME_TYPE_DETAIL_HR){
            view = LayoutInflater.from(context).inflate(R.layout.item_home_heart_layout,parent,false);
            return new HomeHeartViewHolder(view);
        }

        //??????
        if(viewType == HomeDateType.HOME_TYPE_SLEEP){
            view = LayoutInflater.from(context).inflate(R.layout.item_sleep_layout,parent,false);
            return new HomeSleepViewHolder(view);
        }

        //??????
        if(viewType == HomeDateType.HOME_TYPE_SPO2){
            view = LayoutInflater.from(context).inflate(R.layout.item_spo2_layout,parent,false);
            return new HomeSpo2ViewHoler(view);
        }

        //??????
        if(viewType == HomeDateType.HOME_TYPE_BP){
            view = LayoutInflater.from(context).inflate(R.layout.item_bp_layout,parent,false);
            return new HomeBpViewHolder(view);
        }
        //????????????
        if(viewType == HomeDateType.HOME_TYPE_SPORT_RECORD){
            view = LayoutInflater.from(context).inflate(R.layout.item_sport_record_layout,parent,false);
            return new HomeSportRecordViewHolder(view);
        }

        //????????? ????????????
        if(viewType == HomeDateType.HOME_HR_WALL_SPORT_RECORD){
            view = LayoutInflater.from(context).inflate(R.layout.item_home_wall_sport_record_layout,parent,false);
            return new HomeHrWallViewHolder(view);
        }
        //????????? ????????????
        if(viewType == HomeDateType.HOME_HR_WALL_REAL_HR){
            view = LayoutInflater.from(context).inflate(R.layout.item_home_wall_real_hr_layout,parent,false);
            return new HomeWallRealHrViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getLayoutPosition();
                int type = list.get(position).getDataType();
                if(onItemClickListener != null)
                    onItemClickListener.onIteClick(type);
            }
        });

        String dataSource = list.get(position).getDataSource();
        /**????????????**/
        if(holder instanceof HomeRealHrViewHolder){
            RealHrBarChartView realHrBarChartView =  ((HomeRealHrViewHolder) holder).homeRealHtView;
            LinearLayout itemRealHrDataLayout = ((HomeRealHrViewHolder) holder).itemRealHrDataLayout;
            LinearLayout emptyLayout = ((HomeRealHrViewHolder) holder).itemRealHrEmptyLayout;

            SwitchButton switchButton = ((HomeRealHrViewHolder) holder).itemRealHrSwitchBtn;

           emptyLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   if(onItemClickListener != null)
                       onItemClickListener.onIteClick(-2);
               }
           });
            switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton button, boolean checked) {
                    if(!checked){
                        if(onItemClickListener != null)
                            onItemClickListener.onIteClick(-1);
                    }
                }
            });



            if(dataSource == null){

                realHrBarChartView.clearData();
                ((HomeRealHrViewHolder) holder).itemHomeRealHrValue.setText("--");
                ((HomeRealHrViewHolder) holder).itemHomeRealHrValue.setTextColor(context.getResources().getColor(R.color.hr_color_1));

                itemRealHrDataLayout.setBackground(HeartRateConvertUtils.setDrawable(context,0));

                //?????????????????????
                ((HomeRealHrViewHolder) holder).itemRealHrPercentageTv.setText("--");
                ((HomeRealHrViewHolder) holder).itemRealHrPercentageTv.setTextColor(context.getResources().getColor(R.color.hr_color_1));
                ((HomeRealHrViewHolder) holder).itemRealHrUnitTv.setTextColor(context.getResources().getColor(R.color.hr_color_1));


                emptyLayout.setVisibility(View.VISIBLE);
                itemRealHrDataLayout.setVisibility(View.GONE);
                return;
            }

            HomeRealtimeBean homeRealtimeBean = gson.fromJson(dataSource,HomeRealtimeBean.class);
            switchButton.setChecked(true);
            emptyLayout.setVisibility(homeRealtimeBean.isOpenRealtimeHr() ? View.GONE : View.VISIBLE);
            itemRealHrDataLayout.setVisibility(homeRealtimeBean.isOpenRealtimeHr() ? View.VISIBLE : View.GONE);
            int value = homeRealtimeBean.getHrValue();
            if(value == 0)
                return;
            //????????????
            int maxHr = HeartRateConvertUtils.getUserMaxHt();
            //?????????????????????
            double hrPercent = HeartRateConvertUtils.hearRate2Percent(value,maxHr);

            //????????????????????????????????????????????????
            int point = HeartRateConvertUtils.hearRate2Point(value,hrPercent);
            int color = HeartRateConvertUtils.getColorByPoint(context,point);


            realHrBarChartView.addData(value,color,false);
            ((HomeRealHrViewHolder) holder).itemHomeRealHrValue.setText(value+"");
            ((HomeRealHrViewHolder) holder).itemHomeRealHrValue.setTextColor(color);

            itemRealHrDataLayout.setBackground(HeartRateConvertUtils.setDrawable(context,point));

            //?????????????????????
            ((HomeRealHrViewHolder) holder).itemRealHrPercentageTv.setText(((int) hrPercent)+"%");
            ((HomeRealHrViewHolder) holder).itemRealHrPercentageTv.setTextColor(color);
            ((HomeRealHrViewHolder) holder).itemRealHrUnitTv.setTextColor(color);

        }

        /**
         * ??????
         */
        if(holder instanceof  HomeCountStepViewHolder){
            if(dataSource == null){
                ((HomeCountStepViewHolder) holder).itemHomeCountKcalDisTv.setText(getTargetType("--","kcal"));
                ((HomeCountStepViewHolder) holder).itemHomeCountStepDisTv.setText(getTargetType("--","km"));
                ((HomeCountStepViewHolder) holder).itemHomeCountStepCirView.setMaxValue(8000f);
                ((HomeCountStepViewHolder) holder).itemHomeCountStepCirView.setValue(0f);

                return;
            }
            SumSportModel sumSportModel = gson.fromJson(dataSource,SumSportModel.class);

            //??????
            int dis = sumSportModel.getSumDistance();
            float kmDis = CalculateUtils.mToKm(dis);
            boolean isKm = MmkvUtils.getUnit();


            SpannableString kmSb = getTargetType((isKm ? kmDis : CalculateUtils.kmToMiValue(kmDis))+"",isKm ? "km" : "mi");
            SpannableString kcalSb = getTargetType(sumSportModel.getSumKcal()+"","kcal");

            ((HomeCountStepViewHolder) holder).itemHomeCountStepDisTv.setText(kmSb);
            ((HomeCountStepViewHolder) holder).itemHomeCountKcalDisTv.setText(kcalSb);
            int stepGoal = MmkvUtils.getStepGoal();
            ((HomeCountStepViewHolder) holder).itemHomeCountStepCirView.setMaxValue(stepGoal);
            ((HomeCountStepViewHolder) holder).itemHomeCountStepCirView.setValue(sumSportModel.getSumStep());
        }


        /**??????**/
        if(holder instanceof HomeHeartViewHolder){
            if(dataSource == null){

                return;
            }
            HomeHeartBean homeHeartBean = gson.fromJson(dataSource,HomeHeartBean.class);

            if(homeHeartBean == null || homeHeartBean.getHrList() == null){

                return;
            }

            //????????????
            List<Integer> fiveList = homeHeartBean.getFiveMinuteHr();

            LineChart lineChart = ((HomeHeartViewHolder) holder).heartChart;
            List<ChartHrBean> chartHrBeanList = new ArrayList<>();
            List<ChartHrBean> lastList = new ArrayList<>();
            for(int i = 0;i<fiveList.size();i++){
                int hrv = fiveList.get(i);

                if(hrv != 0){
                    ChartHrBean chartHrBean = new ChartHrBean(i*5,hrv);
                    chartHrBeanList.add(chartHrBean);
                    lastList.add(chartHrBean);
                }
            }

            if(lastList.isEmpty())
                return;
            int lastHr = lastList.get(lastList.size()-1).getHrValue();
            //????????????
            int maxHr = HeartRateConvertUtils.getUserMaxHt();
            //?????????????????????
            double hrPercent = HeartRateConvertUtils.hearRate2Percent(lastHr,maxHr);

            //????????????????????????????????????????????????
            int point = HeartRateConvertUtils.hearRate2Point(lastHr,hrPercent);

            int color = HeartRateConvertUtils.getColorByPoint(context,point);


            //??????????????????
            ((HomeHeartViewHolder) holder).singleTv.setText(lastHr == 0 ? "--":(lastHr+""));
            ((HomeHeartViewHolder) holder).singleTv.setTextColor(color);

            //????????????
            ((HomeHeartViewHolder) holder).avgTv.setText(homeHeartBean.calculateAvgHr() == 0 ? "--":(homeHeartBean.calculateAvgHr()+""));

            //??????

            boolean isChinese = BaseApplication.getInstance().getIsChinese();

            ((HomeHeartViewHolder) holder).timeTv.setText(isChinese ? homeHeartBean.getDayStr() : BikeUtils.getFormatEnglishDate(homeHeartBean.getDayStr()));

            lineChart.setFocusable(false);
            lineChart.setTouchEnabled(false);
            stepChartViewUtils = new StepChartViewUtils(lineChart);

            heartXList.clear();
            heartXList.add("00:00");
            heartXList.add("08:00");
            heartXList.add("16:00");
            heartXList.add("24:00");

            stepChartViewUtils.setChartData(context,chartHrBeanList,heartXList,false);

        }

        //??????
        if(holder instanceof  HomeSleepViewHolder){
            if(dataSource == null){
                ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeHourTv.setText("-");
                ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeMinuteTv.setText("-");
                ((HomeSleepViewHolder) holder).itemHomeSleepTimeTv.setText("");
                ((HomeSleepViewHolder) holder).itemSleepChartView.setCanvasStartTime(false);
                ((HomeSleepViewHolder) holder).itemSleepChartView.setSleepModel(null);
                return;
            }
            SleepModel sleepModel = gson.fromJson(dataSource,SleepModel.class);
            if(sleepModel.getCountSleepTime() == 0){
                ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeHourTv.setText("-");
                ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeMinuteTv.setText("-");
                ((HomeSleepViewHolder) holder).itemHomeSleepTimeTv.setText("");
                ((HomeSleepViewHolder) holder).itemSleepChartView.setCanvasStartTime(false);
                ((HomeSleepViewHolder) holder).itemSleepChartView.setSleepModel(null);

                return;
            }
            boolean isChinese = BaseApplication.getInstance().getIsChinese();;
            String dayStr = sleepModel.getSaveDay();

            ((HomeSleepViewHolder) holder).itemHomeSleepTimeTv.setText(isChinese ? dayStr : BikeUtils.getFormatEnglishDate(dayStr));
            String timeStr = BikeUtils.formatMinute(sleepModel.getCountSleepTime());
            ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeHourTv.setText(StringUtils.substringBefore(timeStr,":"));
            ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeMinuteTv.setText(StringUtils.substringAfter(timeStr,":"));

            ((HomeSleepViewHolder) holder).itemSleepChartView.setCanvasStartTime(false);
            ((HomeSleepViewHolder) holder).itemSleepChartView.setSleepModel(sleepModel);
        }

        //??????
        if(holder instanceof  HomeSpo2ViewHoler){
            ImageView imageView = ((HomeSpo2ViewHoler) holder).itemSpo2GifImgView;
            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(context).asGif().load(R.drawable.ic_spo2_gif).apply(requestOptions)
                    .into(imageView);
            if(dataSource == null){
                ((HomeSpo2ViewHoler) holder).itemHomeSpo2TimeTv.setText("");
                ((HomeSpo2ViewHoler) holder).itemHomeSpo2ValueTv.setText("--");
//                Glide.with(context).clear(imageView);
                return;
            }
            SingleSpo2Model singleSpo2Model = gson.fromJson(dataSource,SingleSpo2Model.class);

            if(singleSpo2Model.getSpo2Value() == 0){
                ((HomeSpo2ViewHoler) holder).itemHomeSpo2TimeTv.setText("");
                ((HomeSpo2ViewHoler) holder).itemHomeSpo2ValueTv.setText("--");
             //   Glide.with(context).clear(imageView);
                return;
            }
            boolean isChinese = BaseApplication.getInstance().getIsChinese();
            ((HomeSpo2ViewHoler) holder).itemHomeSpo2TimeTv.setText(isChinese ? BikeUtils.getFormatDate(singleSpo2Model.getSaveLongTime(),"yyyy-MM-dd HH:mm:ss") : BikeUtils.getFormatDate(singleSpo2Model.getSaveLongTime(),BikeUtils.endTimeFormat, Locale.ENGLISH));
            ((HomeSpo2ViewHoler) holder).itemHomeSpo2ValueTv.setText(singleSpo2Model.getSpo2Value()+"%");

        }

        //??????
        if(holder instanceof  HomeBpViewHolder){
            if(dataSource == null){
                ((HomeBpViewHolder) holder).itemHomeBpTimeTv.setText("");
                ((HomeBpViewHolder) holder).itemBpSystolicView.setEmptyData();
                ((HomeBpViewHolder) holder).itemBpDiastolicView.setEmptyData();
                return;
            }
            SingleBpModel singleBpModel = gson.fromJson(dataSource,SingleBpModel.class);
            if(singleBpModel.getDiastolicBp() == 0 || singleBpModel.getSysBp() == 0){
                ((HomeBpViewHolder) holder).itemHomeBpTimeTv.setText("");
                ((HomeBpViewHolder) holder).itemBpSystolicView.setEmptyData();
                ((HomeBpViewHolder) holder).itemBpDiastolicView.setEmptyData();
                return;
            }

            boolean isChinese = BaseApplication.getInstance().getIsChinese();

            ((HomeBpViewHolder) holder).itemHomeBpTimeTv.setText(isChinese ? BikeUtils.getFormatDate(singleBpModel.getSaveLongTime(),"yyyy-MM-dd HH:mm:ss") : BikeUtils.getFormatDate(singleBpModel.getSaveLongTime(),BikeUtils.endTimeFormat, Locale.ENGLISH));


            ((HomeBpViewHolder) holder).itemBpSystolicView.setSystolicBpValue(singleBpModel.getSysBp());
            ((HomeBpViewHolder) holder).itemBpDiastolicView.setDiastolicBpValue(singleBpModel.getDiastolicBp());


        }

        //????????????
        if(holder instanceof  HomeSportRecordViewHolder){
            if(dataSource == null){
                ((HomeSportRecordViewHolder) holder).itemSportRecordKcalTv.setText("--");
                ((HomeSportRecordViewHolder) holder).itemSportRecordTimeTv.setText("--");
                ((HomeSportRecordViewHolder) holder).itemSportRecordLastTimeTv.setText("");
                return;
            }
            Map<String,String> map = gson.fromJson(dataSource,Map.class);
            if(map == null){
                ((HomeSportRecordViewHolder) holder).itemSportRecordKcalTv.setText("--");
                ((HomeSportRecordViewHolder) holder).itemSportRecordTimeTv.setText("--");
                ((HomeSportRecordViewHolder) holder).itemSportRecordLastTimeTv.setText("");
                return;
            }
            for(Map.Entry<String,String> m : map.entrySet()){
                //??????
                String dayStr = m.getKey();

                String dayLanguage = BaseApplication.getInstance().getIsChinese() ? dayStr : BikeUtils.getFormatEnglishDate(dayStr);
               //BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate()) ? context.getResources().getString(R.string.string_today) : dayStr

                ((HomeSportRecordViewHolder) holder).itemSportRecordLastTimeTv.setText(dayLanguage);
                String vStr = m.getValue();
                String time = StringUtils.substringBefore(vStr,"+");
                String kcal = StringUtils.substringAfter(vStr,"+");
                int timeInteger = Integer.parseInt(time.trim());
                String timeStr = BikeUtils.formatMinuteNoHour(timeInteger/60,context);


                ((HomeSportRecordViewHolder) holder).itemSportRecordKcalTv.setText(getTargetType(kcal+"",context.getResources().getString(R.string.string_kcal)));
                ((HomeSportRecordViewHolder) holder).itemSportRecordTimeTv.setText(getTargetType(timeStr+"",""));

            }

        }


        //????????????????????????
        if(holder instanceof HomeHrWallViewHolder){
            if(dataSource == null){
                ((HomeHrWallViewHolder) holder).itemHomeWallAvgTv.setText("--");
                ((HomeHrWallViewHolder) holder).itemHomeWallTimesTv.setText("--");
                ((HomeHrWallViewHolder) holder).itemWallKcalTv.setText("--");
                ((HomeHrWallViewHolder) holder).itemWallDurationTv.setText("--");
                ((HomeHrWallViewHolder) holder).homeHrBeltDateTv.setText("--");

                return;
            }

            ExerciseHomeBean exerciseHomeBean = gson.fromJson(dataSource,ExerciseHomeBean.class);

            Timber.e("-------??????Adapter="+gson.toJson(exerciseHomeBean));
            String duration = exerciseHomeBean.getSportDuration();

            SpannableString avgHr = SpannableUtils.getTargetType(exerciseHomeBean.getAvgHr()+"","bpm");
            SpannableString kcalS = SpannableUtils.getTargetType(exerciseHomeBean.getKcal()+"","kcal");

            ((HomeHrWallViewHolder) holder).itemHomeWallAvgTv.setText(avgHr);
            ((HomeHrWallViewHolder) holder).itemHomeWallTimesTv.setText(exerciseHomeBean.getSportTimes()+"");
            ((HomeHrWallViewHolder) holder).itemWallKcalTv.setText(kcalS);
            ((HomeHrWallViewHolder) holder).itemWallDurationTv.setText(duration);
            ((HomeHrWallViewHolder) holder).homeHrBeltDateTv.setText(BikeUtils.isEqualDay(exerciseHomeBean.getDay(),BikeUtils.getCurrDate()) ? context.getResources().getString(R.string.string_today) : exerciseHomeBean.getDay());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    /**???????????????viewHolder**/
    private class HomeRealHrViewHolder extends RecyclerView.ViewHolder{

        //???????????????????????????????????????
        private LinearLayout itemRealHrEmptyLayout;
        //????????????????????????????????????
        private LinearLayout itemRealHrDataLayout;
        //?????????????????????????????????
        private ShapeTextView homeOpenRealHrTv;
        //??????
        private SwitchButton itemRealHrSwitchBtn;
        //???????????????
        private TextView itemHomeRealHrValue;
        //?????????
        private TextView itemRealHrPercentageTv;
        //???????????????
        private RealHrBarChartView homeRealHtView;
        //??????
        private TextView itemRealHrUnitTv;

        public HomeRealHrViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRealHrEmptyLayout = itemView.findViewById(R.id.itemRealHrEmptyLayout);
            itemRealHrDataLayout = itemView.findViewById(R.id.itemRealHrDataLayout);
            itemRealHrSwitchBtn = itemView.findViewById(R.id.itemRealHrSwitchBtn);
            itemHomeRealHrValue = itemView.findViewById(R.id.itemHomeRealHrValue);
            itemRealHrPercentageTv = itemView.findViewById(R.id.itemRealHrPercentageTv);
            homeRealHtView = itemView.findViewById(R.id.homeRealHtView);
            itemRealHrUnitTv = itemView.findViewById(R.id.itemRealHrUnitTv);
            homeOpenRealHrTv = itemView.findViewById(R.id.homeOpenRealHrTv);
        }
    }


    //???????????????viewHolder
    private class HomeCountStepViewHolder extends RecyclerView.ViewHolder{

        //??????
        private TextView itemHomeCountStepDisTv;
        //?????????
        private TextView itemHomeCountKcalDisTv;
        //?????????
        private CircleProgress itemHomeCountStepCirView;

        public HomeCountStepViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHomeCountStepDisTv = itemView.findViewById(R.id.itemHomeCountStepDisTv);
            itemHomeCountKcalDisTv = itemView.findViewById(R.id.itemHomeCountKcalDisTv);
            itemHomeCountStepCirView = itemView.findViewById(R.id.itemHomeCountStepCirView);
        }
    }


    /**?????????viewHolder**/
    private class HomeSleepViewHolder extends RecyclerView.ViewHolder{

        //??????
        private TextView itemHomeSleepTimeTv;
        //????????????
        private TextView itemHomeSleepAllTimeHourTv;
        //????????????
        private TextView itemHomeSleepAllTimeMinuteTv;

        private SleepChartView itemSleepChartView;



        public HomeSleepViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHomeSleepTimeTv = itemView.findViewById(R.id.itemHomeSleepTimeTv);
            itemHomeSleepAllTimeHourTv = itemView.findViewById(R.id.itemHomeSleepAllTimeHourTv);
            itemHomeSleepAllTimeMinuteTv = itemView.findViewById(R.id.itemHomeSleepAllTimeMinuteTv);
            itemSleepChartView = itemView.findViewById(R.id.itemSleepChartView);
        }
    }

    //???????????????viewViewHolder
    private class HomeSpo2ViewHoler extends RecyclerView.ViewHolder{

        //??????
        private TextView itemHomeSpo2TimeTv;
        //?????????
        private TextView itemHomeSpo2ValueTv;
        //Gif???????????????
        private ImageView itemSpo2GifImgView;


        public HomeSpo2ViewHoler(@NonNull View itemView) {
            super(itemView);
            itemHomeSpo2TimeTv = itemView.findViewById(R.id.itemHomeSpo2TimeTv);
            itemHomeSpo2ValueTv = itemView.findViewById(R.id.itemHomeSpo2ValueTv);
            itemSpo2GifImgView = itemView.findViewById(R.id.itemSpo2GifImgView);
        }
    }

    /**???????????????viewHolder**/
    private class HomeBpViewHolder extends RecyclerView.ViewHolder{

        private TextView itemHomeBpTimeTv;
        private CusSystolicBpScheduleView itemBpSystolicView;
        private CusDiastolicBpScheduleView itemBpDiastolicView;

        public HomeBpViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHomeBpTimeTv = itemView.findViewById(R.id.itemHomeBpTimeTv);
            itemBpSystolicView = itemView.findViewById(R.id.itemBpSystolicView);
            itemBpDiastolicView = itemView.findViewById(R.id.itemBpDiastolicView);
        }
    }

    /**?????????????????????viewHolder**/
    private class HomeSportRecordViewHolder extends RecyclerView.ViewHolder{

        //??????
        private TextView itemSportRecordLastTimeTv;
        //???????????????
        private TextView itemSportRecordTimeTv;
        //?????????
        private TextView itemSportRecordKcalTv;

        public HomeSportRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSportRecordLastTimeTv = itemView.findViewById(R.id.itemSportRecordLastTimeTv);
            itemSportRecordTimeTv = itemView.findViewById(R.id.itemSportRecordTimeTv);
            itemSportRecordKcalTv = itemView.findViewById(R.id.itemSportRecordKcalTv);
        }
    }



    /**???????????????viewHolder**/
    private class HomeHeartViewHolder extends RecyclerView.ViewHolder{

        private LineChart heartChart;
        //????????????
        private TextView singleTv;
        //????????????
        private TextView avgTv;
        //??????
        private TextView timeTv;

        public HomeHeartViewHolder(@NonNull View itemView) {
            super(itemView);
            heartChart = itemView.findViewById(R.id.itemHomeHrChartView);
            singleTv = itemView.findViewById(R.id.itemHomeHrSingleHrTv);
            avgTv = itemView.findViewById(R.id.itemHomeHrAvgTv);
            timeTv = itemView.findViewById(R.id.itemHomeHrTimeTv);
        }
    }


    /**
     * ?????????????????????
     */
    private class HomeWallRealHrViewHolder extends RecyclerView.ViewHolder{

        public HomeWallRealHrViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * ?????????????????????
     *
     */
    private class HomeHrWallViewHolder extends RecyclerView.ViewHolder{

        //????????????
        private TextView itemHomeWallAvgTv;
        //?????????
        private TextView itemWallKcalTv;
        //??????
        private TextView itemWallDurationTv;
        //??????
        private TextView itemHomeWallTimesTv;
        //??????
        private TextView homeHrBeltDateTv;


        public HomeHrWallViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHomeWallAvgTv = itemView.findViewById(R.id.itemHomeWallAvgTv);
            itemWallDurationTv = itemView.findViewById(R.id.itemWallDurationTv);
            itemWallKcalTv = itemView.findViewById(R.id.itemWallKcalTv);
            itemHomeWallTimesTv = itemView.findViewById(R.id.itemHomeWallTimesTv);
            homeHrBeltDateTv = itemView.findViewById(R.id.homeHrBeltDateTv);

        }


    }



    private SpannableString getTargetType(String value, String unitType){

        String distance = value;

        distance = distance+" "+unitType;
        SpannableString spannableString = new SpannableString(distance);
        spannableString.setSpan(new AbsoluteSizeSpan(14,true),distance.length()-unitType.length(),distance.length(),SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ACACAC")),distance.length()-unitType.length(),distance.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
