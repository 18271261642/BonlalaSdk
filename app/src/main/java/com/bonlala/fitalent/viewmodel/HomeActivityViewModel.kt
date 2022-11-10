package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.AppVersionApi
import com.bonlala.fitalent.http.api.VersionApi
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*

/**
 * Created by Admin
 *Date 2022/10/31
 */
class HomeActivityViewModel : ViewModel() {

    //获取app版本
    var appVersion = MutableLiveData<AppVersionApi.AppVersionInfo?>()

    //是否需要提醒手表固件版本升级
    var isShowDfuAlert = MutableLiveData<Boolean>()


    //获取连接不上的指引url
    var notConnDescUrl = MutableLiveData<String>()

    fun getAppVersion(lifecycleOwner: LifecycleOwner) {
        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM
        EasyHttp.get(lifecycleOwner).server(requestServer).api(AppVersionApi().setAppVersion(0, 0))
            .request(object : OnHttpListener<String> {
                override fun onSucceed(result: String?) {
                    Timber.e("---succ=" + result)

                    val jsonObject = JSONObject(result)
                    if (jsonObject.getString("code") == "200") {
                        val data = jsonObject.getString("data")
                        val appVersionB =
                            GsonUtils.getGsonObject<AppVersionApi.AppVersionInfo>(data)
                        appVersion.postValue(appVersionB)
                    }

                }

                override fun onFail(e: Exception?) {

                }

            })
    }

    //后台获取固件版本信息
    fun getDeviceVersionInfo(lifecycleOwner: LifecycleOwner, currentVersion: String) {

        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM
        EasyConfig.getInstance().setServer(requestServer).into()
        EasyHttp.get(lifecycleOwner).api(VersionApi().setVersion("W560B", 1)).request(object :
            OnHttpListener<String> {
            override  fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if (jsonObject.get("code") == "200" && jsonObject.get("success") == true) {
                    val dataStr = jsonObject.getString("data")
                    val verBean = GsonUtils.getGsonObject<VersionApi.VersionInfo>(dataStr)

                    Timber.e("--233333--固件版本=" + verBean.toString()+"\n"+currentVersion)
                    if (verBean != null) {
                        if (verBean.versionName.lowercase(Locale.ROOT) != currentVersion.lowercase(
                                Locale.ROOT)
                        ) {
                            GlobalScope.launch {
                                delay(3000)
                                isShowDfuAlert.postValue(true)
                            }

                        }
                    }
                }
            }

            override fun onFail(e: Exception?) {

            }

        })

    }


    //获取连接不上的url
    fun getNotConnUrl(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("api/app/deviceGuide/detail/W560B").request(object :
            OnHttpListener<String> {
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getString("code") == "200"){
                    val dataStr = jsonObject.getString("data")
                    notConnDescUrl.postValue(dataStr)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })
    }
}