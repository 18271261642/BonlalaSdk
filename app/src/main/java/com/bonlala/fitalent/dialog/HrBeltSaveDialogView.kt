package com.bonlala.fitalent.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.utils.BikeUtils
import kotlinx.android.synthetic.main.dialog_hr_belt_sport_end_dialog_layout.*

/**
 * 心率带运动结束后保存的dialog
 * Created by Admin
 *Date 2022/12/12
 */
class HrBeltSaveDialogView : AppCompatDialog,View.OnClickListener {


    private var onSportSaveClick : OnItemClickListener ?= null

    fun setOnSportSaveClickListener(onclick : OnItemClickListener){
        this.onSportSaveClick = onclick
    }

    constructor(context: Context) : super (context){

    }

    constructor(context: Context ,theme : Int) : super (context, theme){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_hr_belt_sport_end_dialog_layout)

        dialogSportSaveBtn.setOnClickListener(this)
        dialogSportSaveCancelBtn.setOnClickListener(this)
    }



    //设置平均、最大、最小心率
    fun setHeartValue(avg : Int,max : Int,min : Int){
        dialogEndAvgHr.text = getTargetType(avg.toString(),"bpm")
        dialogEndMaxHr.text = getTargetType(max.toString(),"bpm")
        dialogEndMinHr.text = getTargetType(min.toString(),"bpm")
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
        when (id){
            //保存
            R.id.dialogSportSaveBtn->{
                onSportSaveClick?.onIteClick(0x01)
            }

            //取消
            R.id.dialogSportSaveCancelBtn->{
                dismiss()
            }
        }

    }



    //保存运动数据，先判断是否有输入运动名称
    private fun saveSportData(){
        val inputName = dialogEndInputNameEdit.text.toString()
        if(BikeUtils.isEmpty(inputName)){
            return
        }


    }


    /**
     * 设置大小
     * @param value 值
     * @param unitType 单位 eg: km ,kcal ..
     * @return eg:100 km
     */
    private fun getTargetType(value: String, unitType: String): SpannableString? {
        var distance = value
        distance = "$distance $unitType"
        val spannableString = SpannableString(distance)
        spannableString.setSpan(
            AbsoluteSizeSpan(14, true),
            distance.length - unitType.length,
            distance.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#ACACAC")),
            distance.length - unitType.length,
            distance.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
}