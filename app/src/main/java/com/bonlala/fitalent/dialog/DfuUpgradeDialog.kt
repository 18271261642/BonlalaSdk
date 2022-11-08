package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.layout_dfu_upgrade_dialog.*

/**
 * Created by Admin
 *Date 2022/11/8
 */
class DfuUpgradeDialog : AppCompatDialog {


    private var onItemClick : OnItemClickListener ?= null

    fun setOnItemClick(onItemClickListener: OnItemClickListener){
        this.onItemClick = onItemClickListener
    }

    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dfu_upgrade_dialog)


        dfuUpdateDialogSkipTv.setOnClickListener {
            dismiss()
            onItemClick?.onIteClick(0x00)

        }

        dfuUpdateDialogUpdateTv.setOnClickListener {
            onItemClick?.onIteClick(0x01)
        }

    }



}