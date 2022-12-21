package com.bonlala.action;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.dialog.CalendarSelectDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/31
 *    desc   : 带标题栏的 Fragment 业务基类
 */
public abstract class TitleBarFragment<A extends AppActivity> extends AppFragment<A>
        implements TitleBarAction {

    /** 标题栏对象 */
    private TitleBar mTitleBar;
    /** 状态栏沉浸 */
    private ImmersionBar mImmersionBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置标题栏点击监听
        if (getTitleBar() != null) {
            getTitleBar().setOnTitleBarListener(this);
            getTitleBar().setLineVisible(false);
        }

        if (isStatusBarEnabled()) {
            // 初始化沉浸式状态栏
            getStatusBarConfig().init();

            if (getTitleBar() != null) {
                // 设置标题栏沉浸
                ImmersionBar.setTitleBar(this, getTitleBar());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStatusBarEnabled()) {
            // 重新初始化状态栏
            getStatusBarConfig().init();
        }
    }

    /**
     * 是否在 Fragment 使用沉浸式
     */
    public boolean isStatusBarEnabled() {
        return false;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @NonNull
    protected ImmersionBar getStatusBarConfig() {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig();
        }
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式
     */
    @NonNull
    protected ImmersionBar createStatusBarConfig() {
        return ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(isStatusBarDarkFont())
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
                .autoDarkModeEnable(true, 0.2f);
    }

    /**
     * 获取状态栏字体颜色
     */
    protected boolean isStatusBarDarkFont() {
        // 返回真表示黑色字体
        return getAttachActivity().isStatusBarDarkFont();
    }

    @Override
    @Nullable
    public TitleBar getTitleBar() {
        if (mTitleBar == null || !isLoading()) {
            mTitleBar = obtainTitleBar((ViewGroup) getView());
        }
        return mTitleBar;
    }


    private CalendarSelectDialog calendarSelectDialog;

    protected void showCalendarSelectDialog(List<String> dayList, CalendarSelectDialog.OnCusCalendarSelectListener onCalendarSelectListener){
        if(calendarSelectDialog == null)
        calendarSelectDialog = new CalendarSelectDialog(getAttachActivity(), com.bonlala.base.R.style.BaseDialogTheme);
        calendarSelectDialog.show();
        Window window = calendarSelectDialog.getWindow();
        WindowManager.LayoutParams layoutParams  = window.getAttributes();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        layoutParams.height = displayMetrics.heightPixels;
        window.getDecorView().setPadding(0,0,0,0);
        window.setAttributes(layoutParams);

        calendarSelectDialog.setMarkCalendarDate(dayList);
        calendarSelectDialog.setOnCalendarSelectListener(onCalendarSelectListener);
    }

//    protected void downloadFile(String downUrl,String savePath,OnDownloadListener onDownloadListener){
//        EasyHttp.download(this).url(downUrl).file(savePath).listener(onDownloadListener).start();
//    }

    protected void hidCalendarDialog(){
        if(calendarSelectDialog != null)
            calendarSelectDialog.dismiss();
    }


    /**
     * 设置大小
     * @param value 值
     * @param unitType 单位 eg: km ,kcal ..
     * @return eg:100 km
     */
    protected SpannableString getTargetType(String value, String unitType){

        String distance = value;

        distance = distance+" "+unitType;
        SpannableString spannableString = new SpannableString(distance);
        spannableString.setSpan(new AbsoluteSizeSpan(14,true),distance.length()-unitType.length(),distance.length(),SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ACACAC")),distance.length()-unitType.length(),distance.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}