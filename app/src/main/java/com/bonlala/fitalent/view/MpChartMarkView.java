package com.bonlala.fitalent.view;

import android.content.Context;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Admin
 * Date 2020/7/27
 */
public class MpChartMarkView extends MarkerView {

    private static final String TAG = "MpChartMarkView";


    private TextView dateTv;
    private TextView valueTv;

    private float yValue = 0;

    //中间值
    private float middleValue = 100f;

    //X轴的数据集合
    private List<String> xValue;

    //是否是float类型
    private boolean isFloat = false;

    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MpChartMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        dateTv = findViewById(R.id.mpChartDateTv);
        valueTv = findViewById(R.id.mpChartValueTv);

    }

    public MpChartMarkView(Context context, int layoutResource, List<String> xValue, boolean isFloat) {
        super(context, layoutResource);
        dateTv = findViewById(R.id.mpChartDateTv);
        valueTv = findViewById(R.id.mpChartValueTv);
        this.xValue = xValue;
        this.isFloat = isFloat;
    }

    @Override
    public MPPointF getOffset() {
        MPPointF mpPointF;
        if(yValue <middleValue){    //向上显示
            mpPointF = new MPPointF(-getWidth() /2,-getHeight() / 10 * 12);
        }else{  //向下显示
            mpPointF = new MPPointF(-(getWidth() / 2), getHeight() / 8 * 2);
        }

        return mpPointF;

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        try {
            yValue = e.getY();
            //dateTv.setText(xValue.get((int) e.getX())+"");
            valueTv.setText((isFloat ? decimalFormat.format(e.getY()): (int)e.getY())+"");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        super.refreshContent(e, highlight);
    }
}
