package com.bonlala.fitalent.chartview;

import com.bonlala.fitalent.bean.ChartHrBean;
import com.bonlala.fitalent.utils.BikeUtils;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import timber.log.Timber;

/**
 *
 * @author Administrator
 * @date 2018/4/8
 */

public class BarXFormartValue implements IAxisValueFormatter {

    private List<ChartHrBean> xListValues;
    private BarLineChartBase<?> chart;
    //是否是float类型
    private boolean isFloat = false;

    public BarXFormartValue(BarLineChartBase<?> chart, List<ChartHrBean> list) {
        this.chart = chart;
        this.xListValues = list;
    }


    @Override
    public String getFormattedValue(float v, AxisBase axisBase) {
        String temp = StringUtils.substringBefore(String.valueOf(v), ".");

        int index = (int) v;
        if(index<0)
            return "";
        if (xListValues != null && xListValues.size() > 0) {

            if (xListValues.size() > index) {
                int time = xListValues.get(index).getTime();
                return BikeUtils.formatMinuteSleep(time);
            }
            return "";
        }
        return "";

    }

}
