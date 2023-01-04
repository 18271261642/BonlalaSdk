package com.bonlala.fitalent.activity.history

import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import com.blala.blalable.BleOperateManager
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.ExerciseAdapter
import com.bonlala.fitalent.bean.ExerciseShowBean
import com.bonlala.fitalent.ble.DataOperateManager
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.dialog.ExerciseFilterDialogView
import com.bonlala.fitalent.emu.DeviceType
import com.bonlala.fitalent.emu.W560BExerciseType
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.CalculateUtils
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.utils.SpannableUtils
import com.bonlala.fitalent.viewmodel.ExerciseViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_sport_record_layout.*
import kotlinx.android.synthetic.main.layout_empty_layout.*
import timber.log.Timber
import java.util.*

/**
 * 锻炼页面
 * Created by Admin
 *Date 2022/10/17
 */
class ExerciseRecordActivity : AppActivity(){

    private val viewModel by viewModels<ExerciseViewModel>()

    private var adapter : ExerciseAdapter ?= null


    //类型
    private var exerciseType = -1


    override fun getLayoutId(): Int {
        return R.layout.activity_sport_record_layout
    }

    override fun initView() {

        adapter = ExerciseAdapter(this)
        exerciseRy.adapter = adapter

        val isKm = MmkvUtils.getUnit()
//        exerciseUnitTv.text = if(isKm) "km" else "mi"


        //判断是哪个设备，
        //是否是心率带
        val isHrBelt = DBManager.getBindDeviceType() == DeviceType.DEVICE_561
        hrBeltHrLayout.visibility = if(isHrBelt) View.VISIBLE else View.GONE
        val rightDrawable = if(isHrBelt) null else resources.getDrawable(R.mipmap.ic_sport_filter)
        titleBar?.rightIcon = rightDrawable

        recordKmLayout.visibility = if(isHrBelt) View.INVISIBLE else View.VISIBLE
    }


    override fun onRightClick(view: View?) {
        super.onRightClick(view)

        showFilterDialog()

    }


    //显示过滤的跳槽
    private fun showFilterDialog(){
        val dialog = ExerciseFilterDialogView(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setData(exerciseType)
        dialog.setOnCommItemClickListener { position ->
            dialog.dismiss()
            exerciseType = position
            title = W560BExerciseType.getW560BTypeName(position,this@ExerciseRecordActivity)

            getAllExerciseData()
        }


        val window: Window? = dialog.window
        val layoutParams = window?.attributes
        val displayMetrics = resources.displayMetrics
//        layoutParams?.height = displayMetrics.heightPixels
        layoutParams?.gravity = Gravity.TOP
        layoutParams?.y = 0
        layoutParams?.width = displayMetrics.widthPixels
        window?.attributes = layoutParams
    }




    override fun initData() {

        viewModel.allExerciseList.observe(this){
            if(it == null){
                adapter?.data = ArrayList<ExerciseShowBean>()
                return@observe
            }
            if(it.isNotEmpty()){
                emptyLayout.visibility = View.GONE
                exerciseRy.visibility = View.VISIBLE
                val list = it
                Collections.sort(list
                ) { p0, p1 ->
                    p1.dayStr.compareTo(p0.dayStr)
                }
                list.get(0).isShow = true
                adapter?.data = list
                Timber.e("-----锻炼数据="+Gson().toJson(list))
                showTotalData(list)
                return@observe
            }

            showEmptyData()
        }
        getAllExerciseData()
    }


    //获取所有的锻炼数据
    private fun getAllExerciseData(){

        val mac = DBManager.getBindMac()
        if(BikeUtils.isEmpty(mac))
            return
        viewModel.queryAllExercise("user_1001",mac, exerciseType)
    }

    //展示空的
    private fun showEmptyData(){
        emptyLayout.visibility = View.VISIBLE

        exerciseTotalKmTv.text = "--"
        exerciseTotalTimeTv.text = "--"
        exerciseTotalTimesTv.text = "--"
        exerciseTotalKcalTv.text = "--"
        adapter?.data = ArrayList<ExerciseShowBean>()
        exerciseRy.visibility = View.GONE

        //是否是心率带
        val isHrBelt = DBManager.getBindDeviceType() == DeviceType.DEVICE_561
        if(!isHrBelt){
            if(exerciseType == -1 || exerciseType == W560BExerciseType.TYPE_WALK || exerciseType == W560BExerciseType.TYPE_RUN){
                recordKmLayout.visibility = View.VISIBLE
            }else{
                recordKmLayout.visibility = View.INVISIBLE
            }
        }
    }

    //展示总的
    private fun showTotalData(list : List<ExerciseShowBean>){
        var totalTime =0
        var distance = 0
        var kcal = 0


        val heartList = mutableListOf<Int>()

        var count = 0
        list.forEach { it ->
            it.exerciseModelList.forEach {
                totalTime+=it.exerciseMinute
                distance += it.distance
                kcal += it.kcal
                count++

                //心率集合
                val hrList = it.hrList
                if(hrList != null){
                    heartList.addAll(hrList)
                }

            }
        }

        val disStr = CalculateUtils.mToKm(distance)
        val isKm = MmkvUtils.getUnit()

        val kmStr = if(isKm) disStr.toString() else CalculateUtils.kmToMiValue(disStr).toString()
        exerciseTotalKmTv.text = getTargetType(kmStr,if(isKm) "km" else "mi")

//        val hourArray = BikeUtils.formatHourAndMinuteArray(totalTime/60)
        val spann = SpannableUtils.getTargetType((totalTime/60).toString(),resources.getString(R.string.string_minute))

        exerciseTotalTimeTv.text = spann
        exerciseTotalTimesTv.text = getTargetType(count.toString(),resources.getString(R.string.string_times))
        exerciseTotalKcalTv.text = getTargetType(kcal.toString(),resources.getString(R.string.string_kcal))


        //计算心率带最大最小平均心率
        val isZero = heartList.size == 0
        val hrBeltMaxHr = if(isZero) "--" else heartList.maxOf { it }.toInt()
        val hrBeltMinHr = if(isZero) "--" else heartList.minOf { it }.toInt()
        val hrBeltAvgHr = if(isZero) "--" else heartList.average().toInt()

        hrBeltRecordAvgHrTv.text = SpannableUtils.getTargetType(hrBeltAvgHr.toString(),"bpm")
        hrBeltRecordMaxHrTv.text = SpannableUtils.getTargetType(hrBeltMaxHr.toString(),"bpm")
        hrBeltRecordMinHrTv.text = SpannableUtils.getTargetType(hrBeltMinHr.toString(),"bpm")

        //是否是心率带
        val isHrBelt = DBManager.getBindDeviceType() == DeviceType.DEVICE_561
        if(!isHrBelt){
            if(exerciseType == -1 || exerciseType == W560BExerciseType.TYPE_WALK || exerciseType == W560BExerciseType.TYPE_RUN){
                recordKmLayout.visibility = View.VISIBLE
            }else{
                recordKmLayout.visibility = View.INVISIBLE
            }
        }

    }
}