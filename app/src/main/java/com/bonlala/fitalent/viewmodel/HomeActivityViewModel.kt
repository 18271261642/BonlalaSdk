package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.AppVersionApi
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Admin
 *Date 2022/10/31
 */
class HomeActivityViewModel : ViewModel() {

    //获取app版本
    var appVersion = MutableLiveData<AppVersionApi.AppVersionInfo?>()


    fun getAppVersion(lifecycleOwner: LifecycleOwner){
        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM
        EasyHttp.get(lifecycleOwner).server(requestServer).api(AppVersionApi().setAppVersion(0,0)).request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
              Timber.e("---succ="+result)

                val jsonObject = JSONObject(result)
                if(jsonObject.getString("code") == "200"){
                    val data = jsonObject.getString("data")
                    val appVersionB = GsonUtils.getGsonObject<AppVersionApi.AppVersionInfo>(data)
                    appVersion.postValue(appVersionB)
                }

            }

            override fun onFail(e: Exception?) {

            }

        })
    }
}