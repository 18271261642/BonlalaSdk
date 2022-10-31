package com.bonlala.fitalent.view

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import com.bonlala.fitalent.R
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/10/21
 */
class WeatherQualityView : LinearLayout {

    //背景图片
    private var bgImg : ImageView ?= null
    //进度图片
    private var scheduleImg : ImageView ?= null

    constructor(context: Context) : super (context){
        initViews(context)
    }

    constructor(context: Context,attrs: AttributeSet) : super (context, attrs){
        initViews(context)
    }
    constructor(context: Context,attrs: AttributeSet,defaule : Int) : super (context, attrs,defaule){
        initViews(context)
    }


    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_weather_quality,this,true)
        bgImg = view.findViewById(R.id.weatherBgImg)
        scheduleImg = view.findViewById(R.id.weatherQualityScheImgView)
    }


    //设置进度
    fun setQualitySchedule(value : Int){
        if(scheduleImg == null)
            return

        val bgBitmap = BitmapFactory.decodeResource(resources,R.mipmap.ic_weather_quality_bg)
        val scheduleBitmap = BitmapFactory.decodeResource(resources,R.mipmap.ic_weather_quality_schedule)
        //背景图片的宽度
        val bgWidth: Float = bgBitmap.width.toFloat()
        //刻度图片的宽度
        val scaleWidth: Float = scheduleBitmap.width.toFloat()

        val coefficientValue = bgWidth / 300

        val scheduleV = (300-value) * coefficientValue - scaleWidth/2
        //开始平移
        val translateAnimation = TranslateAnimation(
            0F, scheduleV,
            Animation.ABSOLUTE.toFloat(),
            Animation.ABSOLUTE.toFloat()
        )
        translateAnimation.duration = 0
        translateAnimation.fillAfter = true
        scheduleImg!!.startAnimation(translateAnimation)
        invalidate()
    }

}