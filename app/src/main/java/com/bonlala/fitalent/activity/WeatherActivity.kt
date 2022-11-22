package com.bonlala.fitalent.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.blala.blalable.BleOperateManager
import com.blala.blalable.bean.WeatherBean
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.WeatherAdapter
import com.bonlala.fitalent.http.api.WeatherRecordApi
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.CalculateUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.WeatherViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_weather_layout.*
import timber.log.Timber
import java.util.*

/**
 * 天气页面
 * Created by Admin
 *Date 2022/10/21
 */
class WeatherActivity : AppActivity() {

    var locationManager: LocationManager? = null

    private val viewModel by viewModels<WeatherViewModel>()

    private var weatherAdapter : WeatherAdapter ?= null



    override fun getLayoutId(): Int {
      return R.layout.activity_weather_layout
    }

    override fun initView() {
        weatherAdapter = WeatherAdapter(this)
        weatherRecyclerView.addItemDecoration(DividerItemDecoration(this@WeatherActivity,DividerItemDecoration.VERTICAL))
        weatherRecyclerView.adapter = weatherAdapter

        weatherGetTv.setOnClickListener {
            weatherGetTv.text = resources.getString(R.string.string_data_sync)
            requestLocation()
        }
    }

    override fun onLeftClick(view: View?) {
        super.onLeftClick(view)
    }

    override fun initData() {
        showNetData()

        requestLocation()
    }



    private fun showNetData(){

//        viewModel.realtimeWeather.observe(this){
//            weatherGetTv.text = "Get weather"
//            if (it != null) {
//                showRealtimeWeather(it)
//            }
//        }

        viewModel.weatherRecord.observe(this){
            weatherGetTv.text = resources.getString(R.string.string_get_weather)
            var list = it?.weathers
            if (list != null) {
                if (it != null) {
                    sendWeatherToDevice(list,it.aqiValue,it.temp)
                }
            }
            if(list?.size!! >8)
                list = list.subList(1,8)

            //获取当天的天气
            list.forEachIndexed { index, weathersBean ->
                if(weathersBean.currentDate == BikeUtils.getCurrDate()){
                    if (it != null) {
                        showRealtimeWeather(weathersBean,it.temp,it.aqiValue)
                    }
                }

            }

            weatherAdapter?.data = list


        }
    }


    //发送天气
    private fun sendWeatherToDevice(list : List<WeatherRecordApi.WeatherRecordBean.WeathersBean>,aqiValue : Int,tem : Int){
        val weatherList = mutableListOf<WeatherBean>()
        val tempList = mutableListOf<WeatherRecordApi.WeatherRecordBean.WeathersBean>()
        tempList.addAll(list.subList(1,list.size-1))
        tempList.forEach {
           val weatherBean = WeatherBean(aqiValue,tem,it.hiTemp,it.lowTemp,it.deviceWeatherCode)
            weatherList.add(weatherBean)
        }

        BleOperateManager.getInstance().sendWeatherData(weatherList
        ) { }
    }



    private fun requestLocation(){
        if (ActivityCompat.checkSelfPermission(
                this@WeatherActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@WeatherActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@WeatherActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),0x00)
            return
        }
        locationManager = this@WeatherActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (locationManager == null) return
        if (locationManager!!.getProvider(LocationManager.GPS_PROVIDER) != null) {
            val provider: List<*> = locationManager!!.allProviders
            if (provider.size > 0) {
                val iterator = provider.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next() as String
                    Timber.e("-----provider=$p")
                }
            }
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                0.1f,
                locationListener
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        requestLocation()

    }



    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Timber.e(
                """
                ---localton=${location.accuracy}
                ${location.latitude}
                ${location.longitude}
                ${BikeUtils.getFormatDate(location.time, "yyyy-MM-dd HH:mm:ss")}
                """.trimIndent()
            )
            val geocoder = Geocoder(this@WeatherActivity)
            val place = geocoder.getFromLocation(location.latitude,
                location.longitude,5
            )

            if(place.size>0){
                val address = place.get(0)
                var cityName = address.locality
                val cityArea = address.subLocality
                getRealWeather(cityName+cityArea,location.latitude,location.longitude)
                weatherCityTv.text = cityName
                weatherCityAreaTv.text = cityArea
                val str = address.adminArea

                for(i in 0 until 10){
                    val st = address.getAddressLine(i)
                    Timber.e("---st="+st)
                }
            }


        }

        override fun onFlushComplete(requestCode: Int) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}


        //请求天气
        private fun getRealWeather(cityDetailName :String ,lat : Double,lng : Double){
            viewModel.getRealtimeWeather(this@WeatherActivity,cityDetailName,lat,lng)
            viewModel.get15DayWeather(this@WeatherActivity,cityDetailName,lat,lng)
        }
    }

    //展示实时天气
    private fun showRealtimeWeather(
        realTimeBean: WeatherRecordApi.WeatherRecordBean.WeathersBean,
        tem: Int,
        aqiValue: Int
    ){
        val isTemp = MmkvUtils.getTemperature()
        val temp = tem

        realtimeTemTv.text = if(isTemp) "$temp℃" else CalculateUtils.celsiusToFahrenheit(temp).toString()+"℉"
        val imgUrl = realTimeBean.weatherImgUrl
        Glide.with(this@WeatherActivity).load(imgUrl).into(realtimeWeatherImg)

        weatherAqiTv.text = resources.getString(R.string.string_air_quality)+": "+aqiValue
       weatherQualityView.setQualitySchedule(aqiValue)
        val isChinese = BaseApplication.getInstance().isChinese
        val str = if(isChinese) BikeUtils.getFormatDate(System.currentTimeMillis(),"MM-dd HH:mm") else BikeUtils.getFormatDate(System.currentTimeMillis(),"MMM dd HH:mm",
            Locale.ENGLISH)
        weatherUpdateTv.text = resources.getString(R.string.string_update_time)+" "+str

    }

    //展示记录的天气
    private fun showRecordWeather(recordBean: WeatherRecordApi.WeatherRecordBean){

    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) locationManager!!.removeUpdates(locationListener)
    }
}