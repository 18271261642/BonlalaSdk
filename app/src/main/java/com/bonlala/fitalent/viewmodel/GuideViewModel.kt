package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.http.api.PlaySpinApi
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by Admin
 *Date 2022/11/2
 */
class GuideViewModel : ViewModel() {


    //获取玩转
    var playDevice = MutableLiveData<List<PlaySpinApi.PlaySpinBean>>()






    fun getDevicePlay(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api(PlaySpinApi().setDeviceType("W560B")).request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getString("code") == "200"){
                    val data = jsonObject.getString("data")
                    val list = GsonUtils.getGsonObject<List<PlaySpinApi.PlaySpinBean>>(data)
                    playDevice.postValue(list)
                }

            }

            override fun onFail(e: Exception?) {

            }

        } )
    }



}