package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.dialog_see_conn_guide_layout.*

/**
 * 连接成功后，弹窗是否查看操作指引的弹窗
 * Created by Admin
 *Date 2022/11/18
 */
class ConnGuideDialogView : AppCompatDialog{

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
        setContentView(R.layout.dialog_see_conn_guide_layout)



        guideDialogSkipTv.setOnClickListener {
            dismiss()
            onItemClick?.onIteClick(1)
        }

        guideDialogSureTv.setOnClickListener {
            onItemClick?.onIteClick(0)
        }
    }




}