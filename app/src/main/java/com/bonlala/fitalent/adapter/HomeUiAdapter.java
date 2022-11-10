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

/**
 * 首页adapter
 * Created by Admin
 * Date 2022/9/22
 * @author Admin
 */
public class HomeUiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<HomeSourceBean> list;

    //心率的图表工具类
    StepChartViewUtils stepChartViewUtils;
    //心率图表的x轴
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

        //实时心率
        if(viewType == HomeDateType.HOME_TYPE_REAL_HR){
            view = LayoutInflater.from(context).inflate(R.layout.item_real_time_hr_layout,parent,false);
            return new HomeRealHrViewHolder(view);
        }
        //汇总计步
        if(viewType == HomeDateType.HOME_TYPE_STEP){
            view = LayoutInflater.from(context).inflate(R.layout.item_step_layout,parent,false);
            return new HomeCountStepViewHolder(view);
        }

        //心率
        if(viewType == HomeDateType.HOME_TYPE_DETAIL_HR){
            view = LayoutInflater.from(context).inflate(R.layout.item_home_heart_layout,parent,false);
            return new HomeHeartViewHolder(view);
        }

        //睡眠
        if(viewType == HomeDateType.HOME_TYPE_SLEEP){
            view = LayoutInflater.from(context).inflate(R.layout.item_sleep_layout,parent,false);
            return new HomeSleepViewHolder(view);
        }

        //血氧
        if(viewType == HomeDateType.HOME_TYPE_SPO2){
            view = LayoutInflater.from(context).inflate(R.layout.item_spo2_layout,parent,false);
            return new HomeSpo2ViewHoler(view);
        }

        //血压
        if(viewType == HomeDateType.HOME_TYPE_BP){
            view = LayoutInflater.from(context).inflate(R.layout.item_bp_layout,parent,false);
            return new HomeBpViewHolder(view);
        }
        //运动记录
        if(viewType == HomeDateType.HOME_TYPE_SPORT_RECORD){
            view = LayoutInflater.from(context).inflate(R.layout.item_sport_record_layout,parent,false);
            return new HomeSportRecordViewHolder(view);
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
        /**实时心率**/
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
            //最大心率
            int maxHr = HeartRateConvertUtils.getUserMaxHt();
            //心率强度百分比
            double hrPercent = HeartRateConvertUtils.hearRate2Percent(value,maxHr);

            //心率点数，根据点数设置背景和颜色
            int point = HeartRateConvertUtils.hearRate2Point(value,hrPercent);

            int color = HeartRateConvertUtils.getColorByPoint(context,point);

            realHrBarChartView.addData(value,color,false);
            ((HomeRealHrViewHolder) holder).itemHomeRealHrValue.setText(value+"");
            ((HomeRealHrViewHolder) holder).itemHomeRealHrValue.setTextColor(color);

            itemRealHrDataLayout.setBackground(HeartRateConvertUtils.setDrawable(context,point));

            //心率强度百分比
            ((HomeRealHrViewHolder) holder).itemRealHrPercentageTv.setText(((int) hrPercent)+"%");
            ((HomeRealHrViewHolder) holder).itemRealHrPercentageTv.setTextColor(color);
            ((HomeRealHrViewHolder) holder).itemRealHrUnitTv.setTextColor(color);

        }

        /**
         * 计步
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

            //距离
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


        /**心率**/
        if(holder instanceof HomeHeartViewHolder){
            if(dataSource == null){

                return;
            }
            HomeHeartBean homeHeartBean = gson.fromJson(dataSource,HomeHeartBean.class);

            if(homeHeartBean == null || homeHeartBean.getHrList() == null){

                return;
            }

            //心率集合
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
            //最大心率
            int maxHr = HeartRateConvertUtils.getUserMaxHt();
            //心率强度百分比
            double hrPercent = HeartRateConvertUtils.hearRate2Percent(lastHr,maxHr);

            //心率点数，根据点数设置背景和颜色
            int point = HeartRateConvertUtils.hearRate2Point(lastHr,hrPercent);

            int color = HeartRateConvertUtils.getColorByPoint(context,point);


            //最近一次心率
            ((HomeHeartViewHolder) holder).singleTv.setText(lastHr == 0 ? "--":(lastHr+""));
            ((HomeHeartViewHolder) holder).singleTv.setTextColor(color);

            //平均心率
            ((HomeHeartViewHolder) holder).avgTv.setText(homeHeartBean.calculateAvgHr() == 0 ? "--":(homeHeartBean.calculateAvgHr()+""));

            //时间

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

        //睡眠
        if(holder instanceof  HomeSleepViewHolder){
            if(dataSource == null){
                ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeHourTv.setText("-");
                ((HomeSleepViewHolder) holder).itemHomeSleepAllTimeMinuteTv.setText("--");
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

        //血氧
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

        //血压
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

        //运动记录
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
                //日期
                String dayStr = m.getKey();

                ((HomeSportRecordViewHolder) holder).itemSportRecordLastTimeTv.setText(BikeUtils.isEqualDay(dayStr,BikeUtils.getCurrDate()) ? context.getResources().getString(R.string.string_today) : dayStr);
                String vStr = m.getValue();
                String time = StringUtils.substringBefore(vStr,"+");
                String kcal = StringUtils.substringAfter(vStr,"+");
                ((HomeSportRecordViewHolder) holder).itemSportRecordKcalTv.setText(getTargetType(kcal+"",context.getResources().getString(R.string.string_kcal)));
                ((HomeSportRecordViewHolder) holder).itemSportRecordTimeTv.setText(getTargetType(time+"",context.getResources().getString(R.string.string_minute)));

            }

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    /**实时心率的viewHolder**/
    private class HomeRealHrViewHolder extends RecyclerView.ViewHolder{

        //未开启实时心率时显示的布局
        private LinearLayout itemRealHrEmptyLayout;
        //开启实时心率时显示的布局
        private LinearLayout itemRealHrDataLayout;
        //打开实时心率开关的按钮
        private ShapeTextView homeOpenRealHrTv;
        //开关
        private SwitchButton itemRealHrSwitchBtn;
        //实时心率值
        private TextView itemHomeRealHrValue;
        //百分比
        private TextView itemRealHrPercentageTv;
        //心率柱状图
        private RealHrBarChartView homeRealHtView;
        //单位
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


    //汇总计步的viewHolder
    private class HomeCountStepViewHolder extends RecyclerView.ViewHolder{

        //距离
        private TextView itemHomeCountStepDisTv;
        //卡路里
        private TextView itemHomeCountKcalDisTv;
        //进度条
        private CircleProgress itemHomeCountStepCirView;

        public HomeCountStepViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHomeCountStepDisTv = itemView.findViewById(R.id.itemHomeCountStepDisTv);
            itemHomeCountKcalDisTv = itemView.findViewById(R.id.itemHomeCountKcalDisTv);
            itemHomeCountStepCirView = itemView.findViewById(R.id.itemHomeCountStepCirView);
        }
    }


    /**睡眠的viewHolder**/
    private class HomeSleepViewHolder extends RecyclerView.ViewHolder{

        //日期
        private TextView itemHomeSleepTimeTv;
        //睡眠小时
        private TextView itemHomeSleepAllTimeHourTv;
        //睡眠分钟
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

    //单次血氧的viewViewHolder
    private class HomeSpo2ViewHoler extends RecyclerView.ViewHolder{

        //时间
        private TextView itemHomeSpo2TimeTv;
        //血氧值
        private TextView itemHomeSpo2ValueTv;
        //Gif显示的图片
        private ImageView itemSpo2GifImgView;


        public HomeSpo2ViewHoler(@NonNull View itemView) {
            super(itemView);
            itemHomeSpo2TimeTv = itemView.findViewById(R.id.itemHomeSpo2TimeTv);
            itemHomeSpo2ValueTv = itemView.findViewById(R.id.itemHomeSpo2ValueTv);
            itemSpo2GifImgView = itemView.findViewById(R.id.itemSpo2GifImgView);
        }
    }

    /**单次血压的viewHolder**/
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

    /**单次运动记录的viewHolder**/
    private class HomeSportRecordViewHolder extends RecyclerView.ViewHolder{

        //时间
        private TextView itemSportRecordLastTimeTv;
        //总运动时间
        private TextView itemSportRecordTimeTv;
        //总消耗
        private TextView itemSportRecordKcalTv;

        public HomeSportRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSportRecordLastTimeTv = itemView.findViewById(R.id.itemSportRecordLastTimeTv);
            itemSportRecordTimeTv = itemView.findViewById(R.id.itemSportRecordTimeTv);
            itemSportRecordKcalTv = itemView.findViewById(R.id.itemSportRecordKcalTv);
        }
    }



    /**连续心率的viewHolder**/
    private class HomeHeartViewHolder extends RecyclerView.ViewHolder{

        private LineChart heartChart;
        //单次心率
        private TextView singleTv;
        //平均心率
        private TextView avgTv;
        //时间
        private TextView timeTv;

        public HomeHeartViewHolder(@NonNull View itemView) {
            super(itemView);
            heartChart = itemView.findViewById(R.id.itemHomeHrChartView);
            singleTv = itemView.findViewById(R.id.itemHomeHrSingleHrTv);
            avgTv = itemView.findViewById(R.id.itemHomeHrAvgTv);
            timeTv = itemView.findViewById(R.id.itemHomeHrTimeTv);
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
