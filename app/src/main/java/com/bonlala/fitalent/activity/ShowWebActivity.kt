package com.bonlala.fitalent.activity

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.*
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_show_web_layout.*
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/21
 */
class ShowWebActivity : AppActivity() {


    override fun getLayoutId(): Int {
        return R.layout.activity_show_web_layout
    }

    override fun initView() {

    }


    private fun setTitleStr(title : String){
        setTitle(title)
    }

    override fun initData() {
       val url = intent.getStringExtra("url")
        val title = intent.getStringExtra("title");
        if(url == null){
            ToastUtils.show("url 为空!")
            return
        }
        setTitle(title)
        Timber.e("-----url="+url)
        showSetting(showWebView)
        showWebView.loadUrl(url)



        showWebView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                setTitleStr(title)
            }
        }
    }


     class WebCh : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            Timber.e("----title="+title)

        }
    }



     class WebChorm : WebChromeClient{

         constructor() : super()


         override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            Timber.e("-----reco="+title+" "+view?.title)

        }

     }


    private class WebCustomer : WebViewClient(){

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }




    }

    private fun showSetting(webView: WebView){

        val settings: WebSettings = webView.settings
        // 允许文件访问
        // 允许文件访问
        settings.allowFileAccess = true
        // 允许网页定位
        // 允许网页定位
        settings.setGeolocationEnabled(true)
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        settings.javaScriptEnabled = true
        // 允许网页弹对话框
        // 允许网页弹对话框
        settings.javaScriptCanOpenWindowsAutomatically = true
        // 加快网页加载完成的速度，等页面完成再加载图片
        // 加快网页加载完成的速度，等页面完成再加载图片
        settings.loadsImagesAutomatically = true
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.setOnLongClickListener(View.OnLongClickListener { true })
    }
}