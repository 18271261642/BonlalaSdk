package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.HistorySingleHrAdapter
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.SingleHeartModel
import com.bonlala.fitalent.utils.MmkvUtils
import kotlinx.android.synthetic.main.dialog_history_heart_layout.*

/**
 * 展示单次心率详细列表，所有的数据，单次测量的心率
 * Created by Admin
 *Date 2022/10/12
 */
class HistoryHeartDialog : AppCompatDialog {


    private var adapter : HistorySingleHrAdapter ?= null
    private var list : MutableList<SingleHeartModel> ?= null


    constructor(context: Context) : super(context){

    }

    constructor(context: Context,theme : Int) : super(context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_history_heart_layout)

        initViews()

    }


    private fun initViews(){
        list = ArrayList<SingleHeartModel>()
        adapter = HistorySingleHrAdapter(context)
        historyHeartRy.adapter = adapter

        dialogHistoryHrOkTv.setOnClickListener {
            dismiss()
        }

        initData()
    }

    private fun initData(){
        val mac = MmkvUtils.getConnDeviceMac()
        //获取数据库中所有的单次心率数据
        val dbList = DBManager.dbManager.querySingleHeart("user_1001",mac)

        if(dbList == null){
            historyHrEmptyLayout.visibility = View.VISIBLE
            historyHeartRy.visibility = View.GONE
            return
        }
        historyHrEmptyLayout.visibility = View.GONE
        historyHeartRy.visibility = View.VISIBLE
        list?.clear()
        list?.addAll(dbList)
        list?.reverse()
        adapter?.data = list


    }
}