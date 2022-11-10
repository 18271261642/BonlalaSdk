package com.bonlala.fitalent.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import com.blala.blalable.BleOperateManager
import com.blala.blalable.Utils
import com.blala.blalable.listener.OnMeasureDataListener
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.emu.MeasureType
import com.bonlala.fitalent.listeners.OnMeasureCancelListener
import com.bonlala.fitalent.utils.MmkvUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_measure_layout.*
import timber.log.Timber
import kotlin.experimental.and

/**
 * 测量的dialog
 * Created by Admin
 *Date 2022/10/11
 */
class MeasureDialog : AppCompatDialog {


    private var onMeasureCancelListener: OnMeasureCancelListener? = null

    private var type = MeasureType.BP

    fun setOnMeasureCancelListener(onMeasureCancelListener: OnMeasureCancelListener?) {
        this.onMeasureCancelListener = onMeasureCancelListener
    }


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, theme: Int) : super(context,theme){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_measure_layout)

        dialogMeasureOkTv.setOnClickListener{
            if(dialogMeasureOkTv.text == context.resources.getString(R.string.string_cancel)){
                startOrEndMeasure(false,type)

            }
            dismiss()
            onMeasureCancelListener?.onMeasureCancel()
        }

    }



    //设置类型
    public fun setMeasureType(type: MeasureType){
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        Glide.with(context).asGif().load(getTypeImg(type)).apply(requestOptions)
            .into(dialogMeasureImg)

        Glide.with(context).clear(dialogMeasureImg)
    }


    //开始测量
    fun startToMeasure(type: MeasureType){
        this.type = type
        dialogMeasureTypeValueTv.text = "--"
        //启动动画
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        Glide.with(context).asGif().load(getTypeImg(type)).apply(requestOptions)
            .into(dialogMeasureImg)
        dialogMeasureOkTv.text = context.resources.getString(R.string.string_cancel)
        startOrEndMeasure(true,type)

    }


    //停止测量
    fun stopToMeasure(type: MeasureType){
        Glide.with(context).clear(dialogMeasureImg)
        startOrEndMeasure(false,type)

    }


    //开始或停止测量
    private fun startOrEndMeasure(isStart : Boolean,type: MeasureType){

        if(type ==MeasureType.BP){
            BleOperateManager.getInstance().measureBloodStatus(isStart
            ) { data ->
                if(data.size == 4 && data.get(0).toInt() == 2 && data.get(1).toInt() == 60 && data.get(2).toInt() == -1){
                    dismiss()
                }
                //023cffff
                Timber.e("-----测量结果=" + Utils.formatBtArrayToString(data))

                if (data.size == 4 && (data[0] and 0xff.toByte()).toInt() == 2 && (data[1] and 0xff.toByte()).toInt() == 60){
                    val sBp = (data[2] and 0xFF.toByte()).toInt()
                    val disBp = (data[3] and 0xFF.toByte()).toInt()
                    if(sBp == 255 || disBp == 255){
                        dialogMeasureTypeValueTv.text = "--"
                        dialogMeasureOkTv.text = "OK"
                        return@measureBloodStatus
                    }
                    dialogMeasureTypeValueTv.text = sBp.toString()+"/"+disBp+"mmHg"
                    dialogMeasureOkTv.text = "OK"
                }
            }
        }

        if(type == MeasureType.SPO2){
            BleOperateManager.getInstance().measureSo2Status(isStart
            ) { data ->
                Timber.e("-----测量结果=" + Utils.formatBtArrayToString(data))
                //013eff
                if(data.size == 3 && data.get(0).toInt() == 1 && data.get(1).toInt() == 62 && data.get(2).toInt() == -1){
                    dismiss()
                }

                if (data.size == 3 && data.get(0).toInt() == 1 && (data.get(1) and 0xff.toByte()).toInt() == 62){
                    val spo2 =( data.get(2) and 0xFF.toByte()).toInt()
                    if(spo2 == 255){
                        dialogMeasureTypeValueTv.text ="--"
                        dialogMeasureOkTv.text = "OK"
                        return@measureSo2Status
                    }
                    dialogMeasureTypeValueTv.text =spo2.toString()+"%"
                    dialogMeasureOkTv.text = "OK"
                   // DBManager.getInstance().saveMeasureSpo2("user_1001",mac,time,spo2)
                }
            }
        }

        if(type == MeasureType.HEART){
            BleOperateManager.getInstance().measureHeartStatus(isStart){
                data->
                //0151ff
                Timber.e("------心率测量="+Utils.formatBtArrayToString(data)+" "+Gson().toJson(data))
                if(data.size == 3 && data.get(0).toInt() == 1 && data.get(1).toInt() == 81 && data.get(2).toInt() ==-1){
                    dismiss()
                }
                if(data.size == 3 && data[0].toInt() == 1 && (data[1] and 0xff.toInt().toByte()).toInt() == 81){
                    val heart = (data.get(2) and 0xff.toByte()).toInt()
                    if(heart == 255){
                        dialogMeasureOkTv.text = "--"
                        dialogMeasureTypeValueTv.text = heart.toString()+"bpm"
                        return@measureHeartStatus
                    }
                    dialogMeasureOkTv.text = "OK"
                    dialogMeasureTypeValueTv.text = heart.toString()+"bpm"
                }

            }
        }

        val mac = MmkvUtils.getConnDeviceMac();

        BleOperateManager.getInstance().setMeasureDataListner(object : OnMeasureDataListener{
            override fun onMeasureHeart(heart: Int, time: Long) {
                if(heart == 255){
                    dialogMeasureOkTv.text = "--"
                    dialogMeasureTypeValueTv.text = heart.toString()+"bpm"
                    return
                }
                dialogMeasureOkTv.text = "OK"
                dialogMeasureTypeValueTv.text = heart.toString()+"bpm"
                DBManager.getInstance().saveMeasureHeartData("user_1001",mac,time,heart)
            }

            override fun onMeasureBp(sBp: Int, disBp: Int, time: Long) {
                if(sBp == 255 || disBp == 255){
                    dialogMeasureTypeValueTv.text = "--"
                    dialogMeasureOkTv.text = "OK"
                    return
                }
                dialogMeasureTypeValueTv.text = sBp.toString()+"/"+disBp+"mmHg"
                dialogMeasureOkTv.text = "OK"
                DBManager.getInstance().saveMeasureBp("user_1001",mac,time,sBp,disBp)
            }

            override fun onMeasureSpo2(spo2: Int, time: Long) {
                if(spo2 == 255){
                    dialogMeasureTypeValueTv.text ="--"
                    dialogMeasureOkTv.text = "OK"
                    return
                }
                dialogMeasureTypeValueTv.text =spo2.toString()+"%"
                dialogMeasureOkTv.text = "OK"
                DBManager.getInstance().saveMeasureSpo2("user_1001",mac,time,spo2)
            }

        })
    }

    //设置类型的图片
    fun getTypeImg(type : MeasureType): Int{
        if(type == MeasureType.HEART)
            return R.drawable.ic_measure_hr
        if(type == MeasureType.BP)
            return R.drawable.ic_measure_bp
        if(type == MeasureType.SPO2)
            return R.drawable.ic_measure_spo2
        return R.drawable.ic_measure_hr
    }


}