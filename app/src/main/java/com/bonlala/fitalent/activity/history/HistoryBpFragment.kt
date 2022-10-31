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
import com.bonlala.fitalent.adapter.SingleBpAdapter
import com.bonlala.fitalent.bean.ChartBpBean
import com.bonlala.fitalent.dialog.MeasureDialog
import com.bonlala.fitalent.dialog.SleepTxtDescDialogView
import com.bonlala.fitalent.emu.ConnStatus
import com.bonlala.fitalent.emu.MeasureType
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.listeners.OnRecordHistoryRightListener
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.view.CusBloadChartView
import com.bonlala.fitalent.viewmodel.SingleBpViewModel
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_record_history_layout.*
import kotlinx.android.synthetic.main.fragment_history_bp_layout.*
import timber.log.Timber

/**
 * 血压
 * Created by Admin
 *Date 2022/10/10
 */
class HistoryBpFragment : TitleBarFragment<RecordHistoryActivity>() ,OnItemClickListener,
    OnRecordHistoryRightListener {


    private val viewModel by viewModels<SingleBpViewModel>()


    private var bpView : CusBloadChartView ?= null

    private var list : MutableList<ChartBpBean> ?= null
    private var adapter : SingleBpAdapter ?= null

    override fun getLayoutId(): Int {
        return R.layout.fragment_history_bp_layout
    }

    override fun initView() {
        bpView = findViewById(R.id.detailBpView)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        linearLayoutManager.reverseLayout = true
        singleBpRecyclerView.layoutManager = linearLayoutManager
        list = ArrayList<ChartBpBean>()
        adapter = SingleBpAdapter(attachActivity,list)
        singleBpRecyclerView.adapter = adapter
        adapter?.setOnItemClickListener(this)

        attachActivity.setOnRecordHistoryRightListener(this)
    }

    override fun initData() {
        getAllDbData()
    }


    private fun showEmptyData(){
        historyBpBgView.isNodata = true
    }

    //获取数据
    private fun getAllDbData(){
        val mac = MmkvUtils.getConnDeviceMac()
        if(BikeUtils.isEmpty(mac)){
            showEmptyData()
            return
        }

        viewModel.allSingleBpList.observe(viewLifecycleOwner){
            if(it == null){
                showEmptyData()
                return@observe
            }
            historyBpBgView.isNodata = false
            Timber.e("---血压="+Gson().toJson(it))
            list?.clear()
            list?.addAll(it)
            list?.reverse() //倒序
            list?.get(0)?.isChecked = true
            adapter?.notifyDataSetChanged()
        }
        viewModel.getAllDbSingleBp("user_1001",mac)
    }




    override fun onIteClick(position: Int) {
//        list?.forEach {
//            it.isChecked = false
//        }
        list?.forEachIndexed { index, chartBpBean ->
            chartBpBean.isChecked = false
            if(index == position)
                chartBpBean.isChecked = true
        }
        adapter?.notifyDataSetChanged()
    }


    override fun onRightImgClick() {
        showBpDesc()
    }

    override fun onHistoryClick() {

    }

    //测量
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
        measureDialog.startToMeasure(MeasureType.BP)
        measureDialog.setOnMeasureCancelListener {
            getAllDbData()
        }
    }

    private fun showBpDesc(){
        val desc = SleepTxtDescDialogView(activity, com.bonlala.base.R.style.BaseDialogTheme)
        desc.show()
        desc.setDesc(resources.getString(R.string.string_bp_txt_desc))
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