package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bonlala.widget.utils.MiscUtil;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/10/11
 * @author Admin
 */
public class CusLineGridView extends View {

    private float mWidth,mHeight;

    private Paint paint;
    private Paint txtPaint;

    /**noData的画笔**/
    private Paint noDataPaint;
    /**是否是nodata**/
    private boolean isNodata = false;


    public CusLineGridView(Context context) {
        super(context);
        initAttr(context);
    }

    public CusLineGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context);
    }

    public CusLineGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context);
    }


    private void initAttr(Context context){
        paint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(MiscUtil.dipToPx(getContext(),0.5f));
        paint.setColor(Color.parseColor("#FFFC4242"));
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
            canvas.drawText("No Data",mWidth/2,mHeight/2,noDataPaint);
            return;
        }

        canvas.translate(0,mHeight);
        canvas.save();
        float modulus = mHeight/1.5f / 210;

        canvasTxt(canvas,modulus);
    }


    private void canvasTxt(Canvas canvas,float modulus){
        int[] array = new int[]{0,60,90,140,210};
        float topWidth = MiscUtil.dipToPx(getContext(),15f);
        for(int i = 0;i<array.length;i++){
            int v = array[i];

            if( v == 60){
                paint.setColor(Color.parseColor("#6EA1FE"));
            }
            if(v == 90){
                paint.setColor(Color.parseColor("#4DCC4B"));
            }

            if(v == 140){
                paint.setColor(Color.parseColor("#FF0E00"));
            }

            if(v == 210 || v == 0){
                paint.setColor(Color.parseColor("#DBDBDB"));
            }

            String vStr = String.valueOf(v);
            float txtHeight = MiscUtil.measureTextHeight(txtPaint);
            float txtWidth = MiscUtil.getTextWidth(txtPaint,vStr);
            //坐标为0，向上移一点
            if(v == 0){
                canvas.drawLine(0,-v * modulus-topWidth-txtHeight,mWidth-txtWidth,-v * modulus-topWidth-txtHeight,paint);
                canvas.drawText(vStr,mWidth-txtWidth,-v * modulus-txtHeight-topWidth,txtPaint);
            }else{
                canvas.drawLine(0,-v * modulus-topWidth,mWidth-txtWidth,-v * modulus-topWidth,paint);
                canvas.drawText(vStr,mWidth-txtWidth,-v * modulus-txtHeight/2-topWidth,txtPaint);
            }

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
