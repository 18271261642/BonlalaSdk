package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.dialog_home_conn_alert_layout.*

/**
 * Created by Admin
 *Date 2022/11/8
 */
class ConnTimeOutDialogView : AppCompatDialog {


    private var onItemClick : OnItemClickListener?= null

    fun setOnItemClick(onItemClickListener: OnItemClickListener){
        this.onItemClick = onItemClickListener
    }


    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context, theme){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_home_conn_alert_layout)

        connAlertWhyTv.setOnClickListener {
            onItemClick?.onIteClick(0x00)
        }

        connAlertSkipTv.setOnClickListener {
            dismiss()
        }
    }
}