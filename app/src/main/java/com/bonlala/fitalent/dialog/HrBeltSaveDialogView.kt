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
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.fitalent.R
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.LanguageUtils
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.dialog_hr_belt_sport_end_dialog_layout.*

/**
 * 心率带运动结束后保存的dialog
 * Created by Admin
 *Date 2022/12/12
 */
class HrBeltSaveDialogView : AppCompatDialog,View.OnClickListener {


    //是否是有效数据
    private var isValid = true

    private var onCommBackListener : OnCommBackDataListener ?= null


    fun setOnSportSaveClickListener(onclick : OnCommBackDataListener){
        this.onCommBackListener = onclick
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


    //是否是有效数据
    fun setIsValidHeart(valid : Boolean){
        noValidDataTv.visibility = if(valid) View.GONE else View.VISIBLE
        //取消按钮，无有效数据不显示
        dialogSportSaveCancelBtn.visibility = if(valid) View.VISIBLE else View.GONE
        isValid = valid
        dialogSportSaveBtn.text = if(valid) context.resources.getString(R.string.string_save) else context.resources.getString(R.string.string_cancel)
        dialogEndInputNameEdit.visibility = if(valid) View.VISIBLE else View.GONE
    }


    //设置平均、最大、最小心率
    fun setHeartValue(avg : Int,sportTime : String,kcal : Int){
        //平均心率
        dialogEndAvgHr.text = getTargetType(if(avg == 0)"--" else avg.toString(),"bpm")
        //运动时长
        dialogEndMaxHr.text = sportTime
        //运动消耗
        dialogEndMinHr.text = getTargetType(if(kcal == 0) "--" else kcal.toString(),"kcal")
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
        when (id){
            //保存
            R.id.dialogSportSaveBtn->{
                if(!isValid){
                    dismiss()
                    return
                }
                //是否是中文
                val isChinese = LanguageUtils.isChinese()
                val inputName = dialogEndInputNameEdit.text
                if(!BikeUtils.isEmpty(inputName.toString()) && isChinese){
                    //长度
                    val chineseLength = inputName.toString().length
                    if(chineseLength>10){
                        ToastUtils.show("长度太长!")
                        return
                    }
                    val lt = arrayOf(inputName.toString())
                    onCommBackListener?.onStrDataBack(*lt)
                    return
                }

                if(!BikeUtils.isEmpty(inputName.toString()) && !isChinese){

                    //长度
                    val chineseLength = inputName.toString().length
                    if(chineseLength>20){
                        ToastUtils.show("长度太长!")
                        return
                    }
                    val lt = arrayOf(inputName.toString())
                    onCommBackListener?.onStrDataBack(*lt)
                    return
                }


                val lt = arrayOf(inputName.toString())
                onCommBackListener?.onStrDataBack(*lt)
            }

            //取消
            R.id.dialogSportSaveCancelBtn->{
                dismiss()
            }
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