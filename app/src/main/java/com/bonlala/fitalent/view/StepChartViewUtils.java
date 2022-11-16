package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartHrBean;
import com.bonlala.fitalent.chartview.BarXFormartValue;
import com.bonlala.fitalent.chartview.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Admin
 * Date 2022/9/28
 * @author Admin
 */
public class StepChartViewUtils {

    private LineChart lineChart;

    private List<Entry> stepChartValueList = new ArrayList<>();

    //计步的柱状图
    private BarChart stepBarChart;
    private List<BarEntry> stepValueList = new ArrayList<>();



    public StepChartViewUtils(BarChart stepBarChart) {
        this.stepBarChart = stepBarChart;
    }

    public StepChartViewUtils(LineChart lineChart) {
        this.lineChart = lineChart;
        setEmptyLineChart();
    }


    public void setEmptyLineChart(){
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setNoDataTextColor(Color.parseColor("#6E6E77"));
        lineChart.setNoDataText("No Data");
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.NONE);
        l.setYOffset(-5);
        lineChart.setData(new LineData());
        lineChart.invalidate();
    }


    //设置心率折线图
    public void setChartData(Context context, List<ChartHrBean> stepList, List<String> xList,boolean isDragEnabled){

        Timber.e("------daxiao ="+stepList.size());
        stepChartValueList.clear();
        lineChart.setNoDataTextColor(Color.parseColor("#6E6E77"));
        lineChart.setExtraLeftOffset(15f);
        lineChart.setExtraRightOffset(15f);

//        if(stepList.size() == 0 || xList.isEmpty()){
////            if(lineChart.getData() != null){
////                lineChart.getData().clearValues();
////            }
////            setEmptyLineChart();
//            lineChart.invalidate();
//            return;
//        }



        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleEnabled(false);

        lineChart.clear();

        //是否拖动
        lineChart.setDragEnabled(isDragEnabled);

        //右侧Y轴
        //得到图表的右侧Y轴实例
        YAxis leftAxis = lineChart.getAxisRight();
        //设置横向表格为虚线
        leftAxis.setAxisLineWidth(0.001f);
        //设置y轴显示的数量
//        leftAxis.setLabelCount(2,false);
        leftAxis.setGridLineWidth(0);
        leftAxis.setEnabled(false);

        //左侧y轴
        YAxis leftY = lineChart.getAxisLeft();
        leftY.setEnabled(true);

        leftY.setAxisLineColor(Color.WHITE);
        leftY.setAxisLineWidth(0.001f);
        leftY.setDrawLabels(false);
        leftY.setDrawGridLines(false);
        leftY.setTextColor(Color.parseColor("#FF676767"));

        leftY.setAxisMaximum(320f);
        leftY.setAxisMinimum(0);

        leftY.removeAllLimitLines();

        LimitLine limitLineZero = new LimitLine(0f,"0");
        limitLineZero.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLineZero.setTextSize(10f);
        limitLineZero.setTextColor(Color.parseColor("#FF676767"));
        limitLineZero.setLineWidth(0.5f);
        limitLineZero.setLineColor(Color.parseColor("#FFDBDBDB"));
        leftY.addLimitLine(limitLineZero);

        LimitLine limitLine120 = new LimitLine(120f,"120");
        limitLine120.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine120.setTextSize(10f);
        limitLine120.setTextColor(Color.parseColor("#FF676767"));
        limitLine120.setLineWidth(0.5f);
        limitLine120.setLineColor(Color.parseColor("#FFDBDBDB"));
        leftY.addLimitLine(limitLine120);


        LimitLine limitLine240 = new LimitLine(240f,"240");
        limitLine240.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine240.setTextSize(10f);
        limitLine240.setTextColor(Color.parseColor("#FF676767"));
        limitLine240.setLineWidth(0.5f);
        limitLine240.setLineColor(Color.parseColor("#FFDBDBDB"));
        leftY.addLimitLine(limitLine240);


        IAxisValueFormatter iAxisValueFormatter = new BarXFormartValue(lineChart,stepList);


//        MpChartMarkView mpChartMarkView = new MpChartMarkView(context, R.layout.mpchart_mark_view_layout,new ArrayList<>(),false);


        MyMarkerView myMarkerView = new MyMarkerView(context,R.layout.custom_marker_view,stepList);
        myMarkerView.setChartView(lineChart);

        lineChart.setMarker(myMarkerView);


        for(int i = 0;i<stepList.size();i++){
            int value = stepList.get(i).getHrValue();
            if(value !=0){
                stepChartValueList.add(new Entry(i,value));
            }
        }

        //x轴
        XAxis xAxis = lineChart.getXAxis();  //设置X轴
        xAxis.setDrawGridLines(false);  //不显示网格线
        xAxis.setValueFormatter(iAxisValueFormatter);
        xAxis.setEnabled(stepChartValueList.size() > 0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.parseColor("#FFDBDBDB"));
        xAxis.setAxisLineWidth(1f);
        xAxis.setLabelCount(4,true);
        xAxis.setTextColor(Color.parseColor("#FF676767"));


        if(stepChartValueList.size()==0){
            lineChart.setNoDataText(context.getResources().getString(R.string.string_no_data));
            lineChart.invalidate();
            return;
        }


        Legend mLegend = lineChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        // 样式
        mLegend.setForm(Legend.LegendForm.NONE);
        // 字体
        mLegend.setFormSize(15.0f);
        // 颜色
        mLegend.setTextColor(Color.BLUE);
        mLegend.setEnabled(false);



        ValueFormatter valueFormatter = new ValueFormatter();


        LineDataSet lineDataSet1;
        if(lineChart.getData() != null && lineChart.getData().getDataSetCount()>0){
            Timber.e("-----!=null--");
            lineDataSet1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet1.setValues(stepChartValueList);
            lineDataSet1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();

        }else{
            Timber.e("-----==null--");
            lineDataSet1 = new LineDataSet(stepChartValueList,"");
            lineDataSet1.setDrawIcons(false);
            //虚线
            //lineDataSet1.enableDashedLine(10f,5f,0f);
            //设置该线的颜色
            lineDataSet1.setColor(Color.parseColor("#FFFF2F2F"));
            //设置节点圆圈颜色
            lineDataSet1.setCircleColor(Color.parseColor("#40e0d0"));
            //设置是否显示点的坐标值
            lineDataSet1.setDrawValues(false);
            lineDataSet1.setValueTextColor(Color.parseColor("#FFFF2F2F"));
            // 设置平滑曲线
            lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet1.setLineWidth(1.5f);
            lineDataSet1.setCircleRadius(1f);
            lineDataSet1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            lineDataSet1.setDrawFilled(true);
            lineDataSet1.setFillColor(Color.parseColor("#FCD5D8"));
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setValueFormatter(valueFormatter);


            //线的集合（可单条或多条线）
            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet1);
            //把要画的所有线(线的集合)添加到LineData里
            LineData lineData = new LineData(dataSets);

            lineChart.setDrawGridBackground(false);
            //把最终的数据setData
            lineChart.setData(lineData);
            lineChart.isAnimationCacheEnabled();
            //动画
            lineChart.animateX(800);
        }

    }


    public void setStepBarChartData(List<Integer> valueList,List<String> xList){
        stepValueList.clear();
        for (int i = 0; i < valueList.size(); i++) {
            stepValueList.add(new BarEntry(i, valueList.get(i)));
        }
//        Log.e(TAG, "----步数大小=" + mValues.size());
        BarDataSet barDataSet = new BarDataSet(stepValueList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.parseColor("#DBDBDB"));
        barDataSet.setColor(Color.parseColor("#67E08E"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#67E08E"));
//        barDataSet.setHighLightColor(Color.GREEN);
        Legend mLegend = stepBarChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);

        bardata.setBarWidth(0.2f);  //设置柱子宽度
        barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
//        if (valueList.size() >= 15) {
//            bardata.setBarWidth(0.2f);  //设置柱子宽度
//            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
//        } else {
//            bardata.setBarWidth(0.1f);  //设置柱子宽度
//        }
        stepBarChart.setData(bardata);

        stepBarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
        stepBarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        stepBarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        stepBarChart.setPinchZoom(false);
        stepBarChart.setTouchEnabled(true);
        stepBarChart.setScaleEnabled(false);


        IndexAxisValueFormatter indexAxisValueFormatter = new IndexAxisValueFormatter(xList);


        //X轴
        XAxis xAxis = stepBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.parseColor("#DBDBDB"));
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.parseColor("#DBDBDB"));
        xAxis.setValueFormatter(indexAxisValueFormatter);
        xAxis.setEnabled(true);
        stepBarChart.getDescription().setEnabled(false);
      //  DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
       // dataMarkView.setChartView(stepBarChart);
        //stepBarChart.setMarker(dataMarkView);


        //右侧的Y轴
        YAxis yAxis = stepBarChart.getAxisRight();
        yAxis.setEnabled(true);
        yAxis.setTextColor(Color.parseColor("#DBDBDB"));
        yAxis.setDrawGridLines(true);
        yAxis.setDrawLabels(true);
        yAxis.setGridColor(Color.BLUE);
        yAxis.setValueFormatter(indexAxisValueFormatter);
        yAxis.setAxisLineColor(Color.parseColor("#DBDBDB"));

        stepBarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        stepBarChart.getAxisLeft().setDrawGridLines(true);//不设置Y轴网格
        stepBarChart.getAxisLeft().setEnabled(true);

        stepBarChart.getXAxis().setSpaceMax(0.5f);
        stepBarChart.animateXY(1000, 2000);//设置动画
    }
}
