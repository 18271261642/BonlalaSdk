package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import java.lang.Exception

/**
 * Created by Admin
 *Date 2022/11/2
 */
class LaunchViewModel : ViewModel() {

    //获取操作指引的url
    var guideUrl = MutableLiveData<String>()


    fun getGuideUrl(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("api/app/deviceGuide/guideConfigUrl").request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {

            }

            override fun onFail(e: Exception?) {

            }

        })

    }
}