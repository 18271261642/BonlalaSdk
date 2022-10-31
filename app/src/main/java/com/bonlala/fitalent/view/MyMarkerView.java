
package com.bonlala.fitalent.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartHrBean;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.widget.utils.MiscUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

import timber.log.Timber;


/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private  TextView tvContent;
    private  TextView timeTv;

    private List<ChartHrBean> list;

    public MyMarkerView(Context context, int layoutResource,List<ChartHrBean> chartHrBeanList) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        timeTv = findViewById(R.id.hrTimeTv);
        this.list = chartHrBeanList;
    }




    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        Timber.e("---e="+e.getX()+" "+e.getY()+" ");
        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
            int index = (int) e.getX();
            int time = list.get(index).getTime();
            timeTv.setText(BikeUtils.formatMinute(time));
            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            int index = (int) e.getX();
            int time = list.get(index).getTime();
            timeTv.setText(BikeUtils.formatMinute(time));
            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
       // Timber.e("-----getHeight="+getHeight());
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF mpPointF = getOffset();

        return super.getOffsetForDrawingAtPoint(posX, posY);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

       // Timber.e("--postX="+posX+" "+posY);
        MPPointF mpPointF = getOffsetForDrawingAtPoint(posX,posY);
        float width = getWidth();
        float height = getHeight();
        mpPointF.y = -height;
        mpPointF.x = -width/2;
        //绘制方法是直接复制过来的，没动
        int saveId = canvas.save();
       // Timber.e("--------dd="+(posX+mpPointF.x)+" "+posX+" "+mpPointF.x+" "+getWidth());
        // translate to the correct position and draw
        float lefWidth = posX + mpPointF.x;
        if(lefWidth< -25)
            lefWidth = -25;
        if(lefWidth>1600)
            lefWidth = 1600;
        canvas.translate(lefWidth, -30f);
        draw(canvas);
        canvas.restoreToCount(saveId);

       // super.draw(canvas, posX, posY);
    }
}
