package com.bonlala.fitalent.viewmodel


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.VersionApi
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Admin
 *Date 2022/9/19
 */
class DfuViewModel : ViewModel() {

    var getServerData = MutableLiveData<VersionApi.VersionInfo?>()

    //后台获取固件版本信息
    fun getServerVersionInfo(lifecycle: LifecycleOwner){
        val requestServer = RequestServer()
        requestServer.bodyType =BodyType.FORM
        EasyConfig.getInstance().setServer(requestServer).into()
        EasyHttp.get(lifecycle).api(VersionApi().setVersion("W560B",1)).request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.get("code") == "200" && jsonObject.get("success") == true){
                    val dataStr = jsonObject.getString("data")
                    val verBean = GsonUtils.getGsonObject<VersionApi.VersionInfo>(dataStr)

                    Timber.e("----固件版本="+verBean.toString())
                    getServerData.postValue(verBean)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })

    }
}