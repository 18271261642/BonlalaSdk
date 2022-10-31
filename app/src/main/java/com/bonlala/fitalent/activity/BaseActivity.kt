package com.bonlala.fitalent.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.bonlala.action.TitleBarAction
import com.bonlala.fitalent.R
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.TitleBar

/**
 * Created by Admin
 *Date 2022/9/5
 */
open abstract class BaseActivity : AppCompatActivity(),TitleBarAction{


    /** 状态栏沉浸*/
    private var mImmersionBar: ImmersionBar? = null
    private var mTitleBar: TitleBar? = null

    private var dialog : ProgressDialog ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLayoutId()?.let { setContentView(it) }

        initLayout()

        initView()
        initData()

    }


    private fun initLayout(){
       // 初始化沉浸式状态栏
       if (isStatusBarEnabled()) {
           getStatusBarConfig().init()

           // 设置标题栏沉浸
           if (titleBar != null) {
               ImmersionBar.setTitleBar(this, titleBar)
           }
       }
   }

    protected abstract fun getLayoutId(): Int?


    protected abstract fun initView()

    protected abstract fun initData()


    override fun onLeftClick(view: View?) {
        onBackPressed()
    }




    override fun getTitleBar(): TitleBar? {
        if (mTitleBar == null) {
            mTitleBar = obtainTitleBar(getContentView())
        }
        return mTitleBar
    }

    /**
     * 设置标题栏的标题
     */
    override fun setTitle(title: CharSequence?) {
        super<AppCompatActivity>.setTitle(title)
        if (titleBar != null) {
            titleBar!!.title = title
            titleBar!!.setLineVisible(false)
        }
    }

    override fun setTitle(id: Int) {
        setTitle(id)
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    open fun getStatusBarConfig(): ImmersionBar {
        if (mImmersionBar == null) {
            mImmersionBar = createStatusBarConfig()
        }
        return mImmersionBar as ImmersionBar
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this) // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(isStatusBarDarkFont()) // 指定导航栏背景颜色
            .navigationBarColor(R.color.white) // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
            .autoDarkModeEnable(true, 0.2f)
    }

    /**
     * 状态栏字体深色模式
     */
    protected open fun isStatusBarDarkFont(): Boolean {
        return true
    }


    /**
     * 是否使用沉浸式状态栏
     */
    protected open fun isStatusBarEnabled(): Boolean {
        return true
    }


    /**
     * 和 setContentView 对应的方法
     */
    open fun getContentView(): ViewGroup? {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }


    open fun showProgressDialog(content : String){
        if(dialog == null)
            dialog = ProgressDialog(this)
        dialog?.setMessage(content)
       dialog?.create()
        dialog?.show()
    }


    open fun closeDialog(){
        if(dialog != null){
            if(dialog!!.isShowing)
                dialog!!.dismiss()
        }
    }

}