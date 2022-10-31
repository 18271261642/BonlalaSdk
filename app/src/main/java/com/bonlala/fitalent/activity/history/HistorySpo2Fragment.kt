package com.bonlala.fitalent.activity.history

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.RecordHistoryActivity
import com.bonlala.fitalent.adapter.SingleSpo2Adapter
import com.bonlala.fitalent.bean.ChartSpo2Bean
import com.bonlala.fitalent.dialog.MeasureDialog
import com.bonlala.fitalent.dialog.SleepTxtDescDialogView
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.MeasureType
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.listeners.OnRecordHistoryRightListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.view.CustomerScrollView
import com.bonlala.fitalent.viewmodel.SingleSpo2ViewModel
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.fragment_history_spo2_layout.*

/**
 * 血氧页面
 * Created by Admin
 *Date 2022/10/10
 */
class HistorySpo2Fragment : TitleBarFragment<RecordHistoryActivity>(),OnItemClickListener,OnRecordHistoryRightListener {

    private val viewModel by viewModels<SingleSpo2ViewModel>()

    private var detailSpo2View : CustomerScrollView ?= null

    private var list : MutableList<ChartSpo2Bean> ?= null
    private var adapter : SingleSpo2Adapter ?= null

    override fun getLayoutId(): Int {
        return R.layout.fragment_history_spo2_layout
    }

    override fun initView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        linearLayoutManager.reverseLayout = true
        historySpo2RecyclerView.layoutManager = linearLayoutManager

        list = ArrayList<ChartSpo2Bean>()
        adapter = SingleSpo2Adapter(activity,list)
        historySpo2RecyclerView.adapter = adapter
        adapter?.setOnItemClickListener(this)

        attachActivity.setOnRecordHistoryRightListener(this)
    }

    override fun initData() {

        getAllDbData()

    }

    //展示无数据
    private fun showEmpty(){
        historySpo2BgView.isNodata = true
    }


    //获取所有的血氧
    private fun getAllDbData(){
        var mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac)){
            showEmpty()
            return
        }

        viewModel.allSingleSpo2List.observe(viewLifecycleOwner){
            if(it == null){
                showEmpty()
                return@observe
            }
            historySpo2BgView.isNodata = false
            list?.clear()
            list?.addAll(it)
            list?.reverse() //倒序
            list?.get(0)?.isChecked = true
            adapter?.notifyDataSetChanged()
        }
        viewModel.getAllDbSpo2("user_1001",mac)
    }

    override fun onIteClick(position: Int) {
        list?.forEachIndexed { index, chartSpo2Bean ->
            chartSpo2Bean.isChecked = false
            if(index == position)
                chartSpo2Bean.isChecked = true
        }
        adapter?.notifyDataSetChanged()
    }



    override fun onRightImgClick() {
        showSpo2Desc()
    }

    override fun onHistoryClick() {

    }

    override fun onMeasureClick() {
        if(BaseApplication.getInstance().connStatus != ConnStatus.CONNECTED){
            ToastUtils.show(resources.getString(R.string.string_not_connect))
            return
        }
        showMeasureDialog()
    }



    private fun showMeasureDialog(){
        val measureDialog = MeasureDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        measureDialog.show()
        measureDialog.setCancelable(false)
        measureDialog.startToMeasure(MeasureType.SPO2)
        measureDialog.setOnMeasureCancelListener {
            getAllDbData()

        }
    }


    private fun showSpo2Desc(){
        val desc = SleepTxtDescDialogView(activity, com.bonlala.base.R.style.BaseDialogTheme)
        desc.show()
        desc.setDesc(resources.getString(R.string.string_spo2_txt_desc))
        val windowM = desc.window?.windowManager
        val layoutP = desc.window?.attributes
        layoutP?.gravity = Gravity.CENTER
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels

        layoutP?.width = widthW/2
        desc.window?.attributes = layoutP
    }
}