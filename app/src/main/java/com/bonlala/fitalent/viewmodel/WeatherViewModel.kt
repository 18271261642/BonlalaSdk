package com.bonlala.fitalent.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonlala.fitalent.http.RequestServer
import com.bonlala.fitalent.http.api.RealtimeWeatherApi
import com.bonlala.fitalent.http.api.WeatherRecordApi
import com.bonlala.fitalent.utils.GsonUtils
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import com.hjq.http.model.BodyType
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by Admin
 *Date 2022/10/24
 */
class WeatherViewModel : ViewModel() {

    //实时天气
    var realtimeWeather = MutableLiveData<RealtimeWeatherApi.RealtimeWeatherBean?>()

    //15天的天气
    var weatherRecord = MutableLiveData<WeatherRecordApi.WeatherRecordBean?>()



    //获取实时的天气
    fun getRealtimeWeather(lifecycleOwner: LifecycleOwner,cityDetailName :String ,lat : Double,lng : Double){
        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM
        val realWeatherApi = RealtimeWeatherApi().setRealtimeWeatherApi(cityDetailName,lat,lng)
        EasyHttp.get(lifecycleOwner).api(realWeatherApi).request(object :
            OnHttpListener<String> {
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                val code = jsonObject.getString("code")
                if(code == "200"){
                    val data = jsonObject.getString("data")
                    val realTimeBean = GsonUtils.getGsonObject<RealtimeWeatherApi.RealtimeWeatherBean>(data)
                    if(realTimeBean != null){
                        realtimeWeather.postValue(realTimeBean)
                    }

                }
            }

            override fun onFail(e: Exception?) {

            }
        })
    }


    //获取15天的天气数据
    fun get15DayWeather(lifecycleOwner: LifecycleOwner,cityDetailName :String ,lat : Double,lng : Double){
        val requestServer = RequestServer()
        requestServer.bodyType = BodyType.FORM
        val recordApi = WeatherRecordApi().setWeatherRecordApi(cityDetailName,lat,lng)
        EasyHttp.get(lifecycleOwner).api(recordApi).request(object : OnHttpListener<String>{
            override fun onSucceed(result: String?) {
                val jsonObject = JSONObject(result)
                val code = jsonObject.getString("code")
                if(code == "200"){
                    val data = jsonObject.getString("data")
                    val recordB = GsonUtils.getGsonObject<WeatherRecordApi.WeatherRecordBean>(data)
                    weatherRecord.postValue(recordB)
                }
            }

            override fun onFail(e: Exception?) {

            }

        })
    }


}