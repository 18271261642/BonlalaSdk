package com.bonlala.fitalent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonlala.base.BaseAdapter
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.ExerciseFilterAdapter
import com.bonlala.fitalent.bean.ExerciseTypeBean
import com.bonlala.fitalent.emu.W560BExerciseType
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.hjq.bar.OnTitleBarListener
import kotlinx.android.synthetic.main.activity_sport_record_layout.*
import kotlinx.android.synthetic.main.dialog_exercise_filter_layout.*

/**
 * 锻炼筛选dialog
 * Created by Admin
 *Date 2022/10/20
 */
class ExerciseFilterDialogView : AppCompatDialog
{

    private var commClickListener : OnItemClickListener ?= null

    fun setOnCommItemClickListener(click : OnItemClickListener){
     this.commClickListener = click
    }


    private var adapter : ExerciseFilterAdapter ?= null

    constructor(context: Context) : super (context){

    }


    constructor(context: Context,theme : Int) : super(context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_exercise_filter_layout)

        initViews()

    }


    fun initViews(){
        val gridLayoutManager = GridLayoutManager(context,3)
        exerciseFilterRy.layoutManager = gridLayoutManager
        adapter = ExerciseFilterAdapter(context)
        adapter?.setOnItemClickListener(onItemClick)
        exerciseFilterRy.adapter = adapter
        exerciseDialogBar.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(view: View?) {
                dismiss()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {

            }

        })
    }


    private val onItemClick : BaseAdapter.OnItemClickListener =
        BaseAdapter.OnItemClickListener { recyclerView, itemView, position ->

            val list = adapter?.data
            if(list != null && list.size>0){
                list.forEach {
                    it.isChecked = false
                }
            }
            list?.get(position)?.isChecked = true
            adapter?.data = list

            list?.get(position)?.type?.let { commClickListener?.onIteClick(it) }
        }



    fun setData(type : Int){

        val w560BTypeList = mutableListOf<ExerciseTypeBean>()
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_all_sport),-1))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exericse_walk),W560BExerciseType.TYPE_WALK))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_run),W560BExerciseType.TYPE_RUN))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_ride),W560BExerciseType.TYPE_RIDE))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_badminton),W560BExerciseType.TYPE_BADMINTON))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_basketball),W560BExerciseType.TYPE_BASKETBALL))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_football),W560BExerciseType.TYPE_FOOTBALL))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_mountaineering),W560BExerciseType.TYPE_MOUNTAINEERING))
        w560BTypeList.add(ExerciseTypeBean(context.resources.getString(R.string.string_exercise_pingpang),W560BExerciseType.TYPE_PINGPONG))


        w560BTypeList.forEach {
            if(it.type == type){
                it.isChecked = true
            }
        }

        adapter?.data = w560BTypeList
    }
}