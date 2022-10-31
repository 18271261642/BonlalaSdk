package com.bonlala.fitalent.activity

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.DialAdapter
import com.bonlala.fitalent.bean.DialBean
import kotlinx.android.synthetic.main.activity_dial_layout.*

/**
 * 表盘
 * Created by Admin
 *Date 2022/9/15
 */
class DialActivity : AppActivity() {


    private var dialAdapter : DialAdapter ? = null
    private var dialList = mutableListOf<DialBean>()

    override fun getLayoutId(): Int {
       return R.layout.activity_dial_layout
    }

    override fun initView() {
        val gridLayoutManager = GridLayoutManager(this,3)
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        dialRecyclerView.layoutManager = gridLayoutManager
        dialAdapter = DialAdapter(this)
        dialAdapter?.setOnItemClickListener(onItemClick)
        dialRecyclerView.adapter = dialAdapter

    }

    override fun initData() {
        dialList.clear()
        dialList.add(DialBean(1,R.mipmap.icon_watch_face_w526_1.toString(),false))
        dialList.add(DialBean(2,R.mipmap.icon_watch_face_w526_3.toString(),false))
        dialList.add(DialBean(3,R.mipmap.icon_watch_face_w526_2.toString(),false))
        dialAdapter?.data = dialList

        readLocalDial()
    }

    //读取当前表盘
    private fun readLocalDial(){
        BaseApplication.getInstance().bleOperate.readCurrentDial(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
               var id = value?.get(0)?.toInt()

                Log.e("DD","---当前表盘="+id)
                if(dialList== null)
                    return
                dialList.forEachIndexed { index, dialBean ->
                    dialBean.isChecked = index+1 == id
                }
                dialAdapter?.notifyDataSetChanged()
            }

            override fun onStrDataBack(vararg value: String?) {

            }

        })
    }


    private val onItemClick : com.bonlala.base.BaseAdapter.OnItemClickListener = object : com.bonlala.base.BaseAdapter.OnItemClickListener{
        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            dialList.forEachIndexed { index, dialBean ->
                dialBean.isChecked = false
            }
            dialList.get(position).isChecked = true
            dialAdapter?.data = dialList

            setChooseDial(position+1)
        }

    }


    //设置表盘
    private fun setChooseDial(id : Int){
        BaseApplication.getInstance().bleOperate.setLocalDial(id)
    }
}