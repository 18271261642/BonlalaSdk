package com.bonlala.fitalent.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.utils.BikeUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.hjq.shape.view.ShapeTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatDialog;
import timber.log.Timber;

/**
 * 日历选择
 * Created by Admin
 * Date 2022/10/17
 * @author Admin
 */
public class CalendarSelectDialog extends AppCompatDialog implements View.OnClickListener, CalendarView.OnCalendarSelectListener
        , CalendarView.OnMonthChangeListener {


    private OnCusCalendarSelectListener onCalendarSelectListener;

    public void setOnCalendarSelectListener(OnCusCalendarSelectListener onCalendarSelectListener) {
        this.onCalendarSelectListener = onCalendarSelectListener;
    }

    private TextView calendarMonthTv;

    private CalendarView calendarView;
    //回到当前
    private ShapeTextView calendarCurrentTv;
    //上个月
    private ImageView calendarPreviewImg;
    //下个月
    private ImageView calendarNextImg;

    /**标记的颜色**/
    private final int markColor = Color.parseColor("#101D37");


    public CalendarSelectDialog(Context context) {
        super(context);
    }

    public CalendarSelectDialog(Context context, int theme) {
        super(context, theme);
    }

    protected CalendarSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calendar_layout);

        initViews();

    }

    private void initViews(){

        calendarMonthTv = findViewById(R.id.calendarMonthTv);
        calendarCurrentTv = findViewById(R.id.calendarCurrentTv);
        calendarView = findViewById(R.id.commonCalendarView);
        calendarPreviewImg = findViewById(R.id.calendarPreviewImg);
        calendarNextImg = findViewById(R.id.calendarNextImg);

        calendarPreviewImg.setOnClickListener(this);
        calendarNextImg.setOnClickListener(this);
        calendarCurrentTv.setOnClickListener(this);

        calendarView.setSelectRangeMode();
        calendarView.setOnCalendarSelectListener(this);
        calendarView.setOnMonthChangeListener(this);

        int[] currYearArray = BikeUtils.getDayArrayOfStr(BikeUtils.getCurrDate());

        calendarView.setRange(1887,1,1,currYearArray[0],currYearArray[1],currYearArray[2]);
    }


    public void showCalendarData(){
        if(calendarView != null){
            calendarView.scrollToCurrent();
        }

    }


    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if(vId == R.id.calendarPreviewImg){
            if(calendarView != null){
                calendarView.scrollToPre();
            }
        }

        if(vId == R.id.calendarNextImg){
            if(calendarView != null){
                calendarView.scrollToNext();
            }
        }

        if(vId == R.id.calendarCurrentTv){
            if(calendarView != null){
                calendarView.scrollToCurrent();
            }
        }
    }


    /**设置回填的日期**/
    public void setRebackDay(String dayStr){
        int[] array = BikeUtils.getDayArrayOfStr(dayStr);
        //年
        int year = array[0];
        int month = array[1];
        int day = array[2];
        Map<String,Calendar> map = new HashMap<>();
        String keyStr = getSchemeCalendar(year,month,day,Color.parseColor("#4EDD7D") ,dayStr).toString();
        map.put(keyStr,getSchemeCalendar(year,month,day, Color.parseColor("#4EDD7D"),dayStr));
        calendarView.setSchemeDate(map);
    }


    /**标记需要的日期**/
    public void setMarkCalendarDate(List<String> dateList){
        if(dateList == null)
            return;
        Map<String,Calendar> map = new HashMap<>();
        for(String date : dateList){
            int[] array = BikeUtils.getDayArrayOfStr(date);
            //年
            int year = array[0];
            int month = array[1];
            int day = array[2];

            String keyStr = getSchemeCalendar(year,month,day,markColor ,date).toString();
            map.put(keyStr,getSchemeCalendar(year,month,day, markColor,date));
        }
        calendarView.setSchemeDate(map);

    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        //如果单独标记颜色、则会使用这个颜色
        calendar.setSchemeColor(color);
        calendar.setScheme(text);
        return calendar;
    }


    //显示日期
    private void showDate(int year,int month){
        calendarMonthTv.setText(year+"-"+String.format("%02d",month));
    }


    @Override
    public void onMonthChange(int year, int month) {
        showDate(year,month);
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        Timber.e("-----onSelect="+isClick);
        long selectTime = calendar.getTimeInMillis();
        if(selectTime>System.currentTimeMillis())
            return;
        if(onCalendarSelectListener != null && isClick)
            onCalendarSelectListener.onDateSelect(BikeUtils.getFormatDate(calendar.getTimeInMillis(),"yyyy-MM-dd"));
    }


    public interface OnCusCalendarSelectListener{
        void onDateSelect(String day);
    }
}
