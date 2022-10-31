package com.bonlala.fitalent.adapter

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.bonlala.fitalent.R
import com.bonlala.fitalent.bean.RecordTypeBean

/**
 * Created by Admin
 *Date 2022/10/8
 */
class HistoryTypeSpinnerAdapter : SpinnerAdapter {

    private  var mContext: Context
    lateinit var imageView: ImageView
    private var mList = mutableListOf<RecordTypeBean>()

    constructor(context: Context, list: MutableList<RecordTypeBean>) {
        this.mContext = context
        this.mList = list
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getItem(p0: Int): Any {
        return mList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    //展开时的布局
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView = LayoutInflater.from(mContext).inflate(R.layout.item_default_spinner_layout,p2,false);
        val tv = rootView.findViewById<TextView>(R.id.defaultSpinnerTv)
        tv.text = mList[p0].typeName
//        val lp = rootView.layoutParams
//        lp.width =
        return rootView;

    }

    override fun getItemViewType(p0: Int): Int {
        return 1
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return false
    }

    //收起时的布局
    override fun getDropDownView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val root = LayoutInflater.from(mContext).inflate(R.layout.item_spinner_item_layout,p2,false)
        val itemTv = root.findViewById<TextView>(R.id.itemSpinnerContentTv)
        itemTv.text = mList[p0].typeName
        return root;

    }
}