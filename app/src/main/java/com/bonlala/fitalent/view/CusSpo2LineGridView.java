package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bonlala.fitalent.R;
import com.bonlala.widget.utils.MiscUtil;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/10/11
 * @author Admin
 */
public class CusSpo2LineGridView extends View {

    private float mWidth,mHeight;

    private Paint paint;
    private Paint txtPaint;

    /**noData的画笔**/
    private Paint noDataPaint;
    /**是否是nodata**/
    private boolean isNodata = false;


    public CusSpo2LineGridView(Context context) {
        super(context);
        initAttr(context);
    }

    public CusSpo2LineGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context);
    }

    public CusSpo2LineGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context);
    }


    private void initAttr(Context context){
        paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(MiscUtil.dipToPx(getContext(),0.5f));
        paint.setColor(Color.parseColor("#FFDBDBDB"));
        paint.setAntiAlias(true);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.parseColor("#FF676767"));
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(MiscUtil.dipToPx(getContext(),10f));

        noDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        noDataPaint.setColor(Color.parseColor("#6E6E77"));
        noDataPaint.setStrokeWidth(2f);
        noDataPaint.setTextSize(MiscUtil.dipToPx(getContext(),18f));
        noDataPaint.setTextAlign(Paint.Align.CENTER);
        noDataPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isNodata){
            canvas.drawText(getResources().getString(R.string.string_no_data),mWidth/2,mHeight/2,noDataPaint);
            return;
        }

        canvas.translate(0,mHeight);
        canvas.save();
        float modulus = mHeight/1.5f / 25;

        canvasTxt(canvas,modulus);
    }


    private void canvasTxt(Canvas canvas,float modulus){
        int[] array = new int[]{85,90,95,100};

        for(int i = 0;i<array.length;i++){
            int v = array[i];


            String vStr = v+"%";
            float txtHeight = MiscUtil.measureTextHeight(txtPaint);
            float txtWidth = MiscUtil.getTextWidth(txtPaint,vStr);
            float startY = -(v-85) * modulus-txtHeight*2.8f;

            canvas.drawLine(0,startY,mWidth-txtWidth,startY,paint);
            canvas.drawText(vStr,mWidth-txtWidth,-(v-85) * modulus-txtHeight*3f,txtPaint);
        }

    }

    public boolean isNodata() {
        return isNodata;
    }

    public void setNodata(boolean nodata) {
        isNodata = nodata;
        invalidate();
    }
}
