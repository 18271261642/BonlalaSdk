package com.bonlala.fitalent.chartview;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Admin
 * Date 2020/7/22
 */
public class ValueFormatter implements IValueFormatter {

    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return decimalFormat.format(value);
    }
}
