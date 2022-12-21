package com.bonlala.fitalent.dialog

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.dialog_time_mode_layout.*

/**
 * 选择运动计时模式的dialog
 * Created by Admin
 *Date 2022/12/12
 */
class HrBeltModelSelectView : AppCompatDialog {

    private var onItemClick : OnItemClickListener ?= null

    fun setOnBeltModelSelectListener(onclick : OnItemClickListener){
        this.onItemClick = onclick
    }


    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time_mode_layout)

        initViews()

    }

    private fun initViews(){
        //关闭
        dialogModelCancelTv.setOnClickListener {
            dismiss()
        }


        //普通计时，正向计时
        dialogModelForwardTv.setOnClickListener {
            onItemClick?.onIteClick(0x00)
        }

        //倒计时
        dialogModelCountdownTv.setOnClickListener {
            onItemClick?.onIteClick(0x01)
        }

        //组合计时
        dialogModelGroupTv.setOnClickListener {
            onItemClick?.onIteClick(0x02)
        }
    }
}