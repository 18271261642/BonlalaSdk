package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.fitalent.R
import kotlinx.android.synthetic.main.dialog_guide_detail_msg.*

/**
 * Created by Admin
 *Date 2022/10/21
 */
class GuideMsgDialog : AppCompatDialog
{


    private var onCommBackDataListener : OnCommBackDataListener ?= null

    fun setOnCommBackDataListener(onCommBackDataListener: OnCommBackDataListener){
        this.onCommBackDataListener = onCommBackDataListener
    }

    constructor(context: Context) : super (context){

    }


    constructor(context: Context,theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_guide_detail_msg)


        initViews()

    }

    fun initViews(){
        dialogGuideMsgSureTv.setOnClickListener {
            onCommBackDataListener?.onIntDataBack(intArrayOf(0))
        }

        dialogGuideIgnore.setOnClickListener {
            onCommBackDataListener?.onStrDataBack(*arrayOf<String>())
        }
    }
}