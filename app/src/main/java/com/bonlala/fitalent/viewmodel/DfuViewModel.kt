package com.bonlala.fitalent.viewmodel


import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blala.blalable.BleOperateManager
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.VersionApi
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import com.hjq.http.model.HttpMethod
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * Created by Admin
 *Date 2022/9/19
 */
class DfuViewModel : ViewModel() {

    //后台获取的固件信息
    var getServerData = MutableLiveData<VersionApi.VersionInfo?>()

    //从手表中读取的固件信息
    var deviceDfuVersion = MutableLiveData<String>()



    //从手表中获取固件版本
    fun getDeviceVersion(bleOperateManager: BleOperateManager){
        bleOperateManager.getDeviceVersionData(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {

            }

            override fun onStrDataBack(vararg value: String?) {
                Timber.e("----固件版本="+ Arrays.toString(value))
                deviceDfuVersion.postValue(value[1])
            }

        })
    }




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

    //下载固件信息
    fun startDownloadDfu(context: Context,lifecycle: LifecycleOwner,downUrl : String,saveFileUrl : String){
        EasyHttp.download(lifecycle).method(HttpMethod.GET).url(downUrl).file("$saveFileUrl/w560b_dfu.bin").listener(object :
            OnDownloadListener{
            override fun onStart(file: File?) {

            }

            override fun onProgress(file: File?, progress: Int) {

            }

            override fun onComplete(file: File?) {

            }

            override fun onError(file: File?, e: Exception?) {

            }

            override fun onEnd(file: File?) {

            }

        })
    }
}