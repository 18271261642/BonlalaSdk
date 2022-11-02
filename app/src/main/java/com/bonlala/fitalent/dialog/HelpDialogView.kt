package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.layout_help_layout.*

/**
 * Created by Admin
 *Date 2022/10/18
 */
class HelpDialogView : AppCompatDialog ,View.OnClickListener{


    private var onCommItemClick : OnItemClickListener ?= null
     fun setOnCommClickListener(onItem : OnItemClickListener){
        this.onCommItemClick = onItem;
    }



    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_help_layout)

        helpPlayEquipmentTv.setOnClickListener(this)
        helpGuideTv.setOnClickListener(this)
        helpAudioTv.setOnClickListener(this)
    }



    override fun onClick(p0: View?) {
        val id = p0?.id
        when(id){
            R.id.helpPlayEquipmentTv->{
                onCommItemClick?.onIteClick(0)
            }

            R.id.helpGuideTv->{
                onCommItemClick?.onIteClick(1)
            }
            R.id.helpAudioTv->{
                onCommItemClick?.onIteClick(2)
            }
        }
    }
}