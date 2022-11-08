package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.bean.GuideTypeBean
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by Admin
 *Date 2022/11/2
 */
class LaunchViewModel : ViewModel() {

    //获取操作指引的url
    var guideUrl = MutableLiveData<String>()

    //获取指引类型列表
    var guiderTypeList = MutableLiveData<GuideTypeBean?>()

    fun getGuideUrl(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("api/app/deviceGuide/guideConfigUrl").request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getString("code") == "200"){
                    val dataStr = jsonObject.getJSONObject("data")
                    val guideUrl = dataStr.getString("guideUrl")
                    val listStr = dataStr.getString("deviceTypes")

                    val guideBeanList = GsonUtils.getGsonObject<List<GuideTypeBean.DeviceTypes>>(listStr)
                    val guideTypeBean = GuideTypeBean()
                    guideTypeBean.guideUrl = guideUrl
                    guideTypeBean.list = guideBeanList
                    guiderTypeList.postValue(guideTypeBean)
                }

            }

            override fun onFail(e: Exception?) {

            }

        })

    }



    fun getGuideTypeList(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("/api/app/deviceGuide/guideConfigUrl").request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {

            }

            override fun onFail(e: Exception?) {

            }

        })
    }
}