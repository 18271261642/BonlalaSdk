package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.comm_alert_dialog_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Admin
 *Date 2022/11/7
 */
class CommAlertDialogView : AppCompatDialog {


    private var commListener : OnItemClickListener ?= null
     fun setCommListener(itemClickListener: OnItemClickListener){
        this.commListener = itemClickListener
    }

    constructor(context: Context) : super (context){

    }


    constructor(context: Context,theme : Int) : super (context, theme){


    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comm_alert_dialog_layout)


        commDialogBtnTv.setOnClickListener {
            dismiss()
            commListener?.onIteClick(0)

        }
    }


    /**
     * 设置显示 的内容，
     * 是否显示按钮
     */
     fun setShowContent(content : String,isShowBtn : Boolean){
        commDialogContentTv.text = content
        commDialogLineView.visibility = if(isShowBtn) View.VISIBLE else View.GONE
        commDialogBtnTv.visibility = if(isShowBtn) View.VISIBLE else View.GONE
    }


    /**
     * 设置显示的内容及显示的时间，毫秒
     */
     fun setShowContentAndTime(content: String,isShowBtn: Boolean,duringTime : Long){
        setShowContent(content,isShowBtn)
        GlobalScope.launch {
            delay(duringTime)
            dismiss()
        }
    }
}