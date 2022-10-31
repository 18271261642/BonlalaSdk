package com.bonlala.fitalent.activity

import android.os.Looper
import android.view.View
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.FeedbackApi
import com.bonlala.fitalent.utils.BikeUtils
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_feedback_layout.*
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/20
 */
class FeedbackActivity : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_feedback_layout
    }

    override fun initView() {
        setOnClickListener(R.id.feedbackSubmitTv)
    }

    override fun initData() {

    }


    override fun onClick(view: View?) {
        super.onClick(view)
        if(view?.id == R.id.feedbackSubmitTv){
            submitData()
        }
    }

    private fun submitData(){

        val contentStr = feedbackContentEdit.text.toString()
        val addressStr = feedbackAddressEdit.text.toString()

        if(BikeUtils.isEmpty(contentStr) || BikeUtils.isEmpty(addressStr)){
            ToastUtils.show("请输入反馈内容和邮箱!")
            return
        }

        if(!BikeUtils.isValidEmail(addressStr)){
            ToastUtils.show("请输入正确的邮箱格式!")
            return
        }

        showDialog("Loading..")
        val map = HashMap<String,String>()
        map.put("email",addressStr)
        map.put("feedbackContent",contentStr)
        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM

        EasyHttp.post(this).api(FeedbackApi()).server(requestServer)
            .json(Gson().toJson(map)).request(object : OnHttpListener<String>{
                override fun onSucceed(result: String?) {
                    hideDialog()
                    Timber.e("-----susc="+result)
                    ToastUtils.show("submit success")

                    android.os.Handler(Looper.myLooper()!!).postDelayed({
                                   finish()
                    }, 1500)
                }

                override fun onFail(e: Exception?) {
                    hideDialog()
                    Timber.e("-----fff="+e?.message)
                }

            })
    }
}