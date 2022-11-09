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
 *Date 2022/11/9
 */
class ScanViewModel : ViewModel() {


    //获取搜索不到的指引地址
    var notScanUrl = MutableLiveData<String>()


    //获取搜索不到的指引url
    fun getNotScanUrl(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("api/app/deviceGuide/detail/common").request(object :
            OnHttpListener<String> {
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getString("code") == "200"){
                    val dataStr = jsonObject.getString("data")
                    notScanUrl.postValue(dataStr)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })
    }
}