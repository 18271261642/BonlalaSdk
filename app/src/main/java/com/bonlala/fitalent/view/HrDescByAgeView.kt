package com.bonlala.fitalent.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.HeartRateConvertUtils
import kotlinx.android.synthetic.main.item_hr_desc_by_age_layout.view.*
import timber.log.Timber

/**
 * 心率页面心率根据年龄的区间
 * Created by Admin
 *Date 2022/10/28
 */
class HrDescByAgeView : LinearLayout{

    constructor(context: Context) : super (context){
        initViews(context)
    }

    constructor(context: Context,attrs : AttributeSet) : super (context, attrs){
        initViews(context)
    }

    constructor(context: Context,attrs: AttributeSet,defStyleAttr : Int) : super (context, attrs, defStyleAttr){
        initViews(context)
    }


    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_hr_desc_by_age_layout,this,true)
        setHrByAge()
    }

    private fun setHrByAge(){

        //最大心率
        val max = HeartRateConvertUtils.getUserMaxHt()
        Timber.e("----max="+max)
        //极限心率
        val maxValue = (max * 0.9f).toInt()
        val hr5 = (max * 0.8).toInt().toString()+"~"+(maxValue-1)
        Timber.e("---hr="+hr5)
        val hr4 = (max * 0.7).toInt().toString()+"~"+((max * 0.8f).toInt()-1)

        val hr3 = (max * 0.6).toInt().toString()+"~"+((max * 0.7).toInt()-1)

        val hr2 = (max * 0.5).toInt().toString()+"~"+((max * 0.6).toInt()-1)

        val hr1 = ((max * 0.5).toInt()-1).toString()

        hr6ValueTv.text = "≥"+(maxValue)+" bpm"
        hr5ValueTv.text = hr5+" bpm"
        hr4ValueTv.text = hr4+" bpm"
        hr3ValueTv.text = hr3+" bpm"
        hr2ValueTv.text = hr2+" bpm"
        hr1ValueTv.text = "<"+hr1+" bpm"


    }
}