package com.bonlala.fitalent.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bonlala.fitalent.R
import com.bonlala.fitalent.dialog.HeightSelectDialog
import com.bonlala.fitalent.utils.HeartRateConvertUtils
import kotlinx.android.synthetic.main.item_wall_heart_rate_rang_layout.view.*
import timber.log.Timber

/**
 * 心率区间的选择view，直接引入布局
 * Created by Admin
 *Date 2022/12/7
 */
class HeartRangeView : LinearLayout {


    //最大的心率值
    private var maxHrValueTv : TextView ?= null


    //最大心率
    private var maxUserHeartValue : Int ?= null

    constructor(context : Context) : super (context){
        initViews(context)
    }

    constructor(context: Context,attribute : AttributeSet) : super (context,attribute){
        initViews(context)
    }

    constructor(context: Context,attribute: AttributeSet,defStyleAttr : Int) : super (context,attribute,defStyleAttr){
        initViews(context)
    }


    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_wall_heart_rate_rang_layout,this,true)
        val maxHrLayout = view.findViewById(R.id.hrMaxHrLayout) as ConstraintLayout
        maxHrValueTv = view.findViewById(R.id.hrMaxHeartValueTv)


        maxHrLayout.setOnClickListener {
            showMaxHrSelect()
        }
    }


    /**
     * 设置最大心率，范围为最大心率正负20
     */
    fun setUserMaxHeart(maxHr : Int){
        this.maxUserHeartValue = maxHr
        maxHrValueTv?.text = maxHr.toString()+"bpm"

    }

    //点击事件
    private fun showMaxHrSelect(){
        //判断是否已经设置了最大心率，没有设置最大心率return
        if(maxUserHeartValue == null){
            return
        }

        val hrList = mutableListOf<String>()

        for(i in maxUserHeartValue!! -20 .. maxUserHeartValue!! +20){
            hrList.add(i.toString())
        }

        val heightSelectDialog = HeightSelectDialog.Builder(context,hrList)
            .setUnitShow(true,"bpm")
            .setTitleTx(context.resources.getString(R.string.string_max_hr))
            .setSignalSelectListener {
                maxHrValueTv?.text = it+"bpm"
                calculateHrRange(it.toInt())
            }.show()

    }


    //选择了最大心率，自动计算区间范围
    private fun calculateHrRange(maxHr: Int){
        //最大心率
        val max = maxHr
        Timber.e("----max="+max)
        //极限心率
        val maxValue = (max * 0.9f).toInt()
        val hr5 = (max * 0.8).toInt().toString()+"bpm~"+(maxValue-1)
        val hr4 = (max * 0.7).toInt().toString()+"bpm~"+((max * 0.8f).toInt()-1)

        val hr3 = (max * 0.6).toInt().toString()+"bpm~"+((max * 0.7).toInt()-1)

        val hr2 = (max * 0.5).toInt().toString()+"bpm~"+((max * 0.6).toInt()-1)

        val hr1 = ((max * 0.5).toInt()-1).toString()

        hrRange6Tv.text = "≥"+maxValue+"bpm"
        hrRange5Tv.text = hr5+"bpm"
        hrRange4Tv.text = hr4+"bpm"
        hrRange3Tv.text = hr3+"bpm"
        hrRange2Tv.text = hr2+"bpm"
        hrRange1Tv.text = "≤"+hr1+"bpm"
    }
}