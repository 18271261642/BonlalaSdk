package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.dialog_hr_sport_reconn_layout.*

/**
 * 心率带断开连接
 * Created by Admin
 *Date 2023/1/4
 */
class HrBletDisconnDialog : AppCompatDialog {

    private var onCommBackListener : OnItemClickListener?= null


    fun setOnSportSaveClickListener(onclick : OnItemClickListener){
        this.onCommBackListener = onclick
    }

    constructor(context: Context) : super (context){

    }

    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_hr_sport_reconn_layout)


        hrEndSportTv.setOnClickListener {
            onCommBackListener?.onIteClick(0x00)
        }

        hrReconnectedTv.setOnClickListener {
            onCommBackListener?.onIteClick(0x01)
        }
    }
}