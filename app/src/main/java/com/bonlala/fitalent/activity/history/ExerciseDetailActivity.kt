package com.bonlala.fitalent.activity.history

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.ItemExerciseAdapter
import com.bonlala.fitalent.bean.ExerciseItemBean
import com.bonlala.fitalent.chartview.LineChartEntity
import com.bonlala.fitalent.chartview.PieChartUtils
import com.bonlala.fitalent.db.model.ExerciseModel
import com.bonlala.fitalent.emu.W560BExerciseType
import com.bonlala.fitalent.utils.*
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_exercise_detail_layout.*
import kotlinx.android.synthetic.main.activity_operate_layout.*
import kotlinx.android.synthetic.main.item_sport_record_item_layout.*
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * 锻炼详情页面
 * Created by Admin
 *Date 2022/10/20
 */
class ExerciseDetailActivity : AppActivity() {

    private var list : MutableList<ExerciseItemBean> ?= null
    private  var itemAdapter : ItemExerciseAdapter ?= null

    override fun getLayoutId(): Int {
       return R.layout.activity_exercise_detail_layout
    }

    override fun initView() {
        itemSportTempBackImg.visibility = View.INVISIBLE
        
        XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request { permissions, all ->  }

        list = mutableListOf()
        val gridLayoutManager = GridLayoutManager(
            context, 3
        )
        itemExerciseTypeRy.layoutManager = gridLayoutManager
        itemAdapter = ItemExerciseAdapter(this,list)
        itemExerciseTypeRy.adapter = itemAdapter
    }

    override fun onRightClick(view: View?) {
        super.onRightClick(view)
        val bitmap = shotNestedScrollView(exerciseShareView)
        val str = FileUtils.savePic(bitmap,this)
        if(BikeUtils.isEmpty(str)){

            return
        }
        Timber.e("---str="+str)
        val u2 = FileProvider.getUriForFile(
            context, context.packageName.toString() + ".provider",
            File(str)
        )

        shareImage(this@ExerciseDetailActivity,u2,"分享")
    }

    override fun initData() {
        var exercise_itemStr = intent.getStringExtra("exercise_item")
        if(exercise_itemStr == null)
            return
        val exceriseB = GsonUtils.getGsonObject<ExerciseModel>(exercise_itemStr)

        Timber.e("-----dbbb="+exceriseB.toString())
        if(exceriseB == null)
            return
        showDetail(exceriseB)
    }


    private fun showDetail(exerciseModel: ExerciseModel){
        itemSportTypeImg.setImageResource(getTypeResource(exerciseModel.type))


        val tempList = getTypeMap(exerciseModel)
        list?.clear()
        if (tempList != null) {
            list?.addAll(tempList)
        }
        itemAdapter?.notifyDataSetChanged()

//        val distance = exerciseModel.distance
//        itemExerciseDistanceTv.text = getTargetType(CalculateUtils.mToKm(distance).toString(),"km")
//
//
//        itemExerciseStartTimeTv.text = exerciseModel.startTimeStr + "~" + exerciseModel.endTimeStr
//        itemSportTimeTv.text = exerciseModel.hourMinute
//        itemExerciseKcalTv.text = getTargetType(exerciseModel.kcal.toString() + "", "kcal")
//        itemExerciseStepTv.text = getTargetType(exerciseModel.countStep.toString() + "", "step")
//        itemExerciseAvgHrTv.text = getTargetType(exerciseModel.avgHr.toString() + "", "bpm")
//        //itemExercisePaceTv.setText(getTargetType(exerciseModel));
//
//        //itemExercisePaceTv.setText(getTargetType(exerciseModel));
//        itemExerciseSpeedTv.text = getTargetType(exerciseModel.avgSpeed.toString() + "", " km/s")

        //心率
        val hrArray = exerciseModel.hrArray
        if(hrArray != null){
            Timber.e("---hr="+hrArray)
            val hrList = GsonUtils.getGsonObject<List<Int>>(hrArray)
            val chartList = mutableListOf<LineChartEntity>()
            Timber.e("-----长度="+hrList?.size)
            //最大心率
            //最大心率
            val maxHr = HeartRateConvertUtils.getUserMaxHt()

            val hrMaxValue = Collections.max(hrList)
            val hrMinValue =Collections.min(hrList)

            var countHr = 0

            var point1 = 0
            var point2 = 0
            var point3 = 0
            var point4 = 0
            var point5 = 0
            var point6 = 0


            hrList?.forEachIndexed { index, i ->
                //心率强度百分比
                //心率强度百分比
                val hrPercent = HeartRateConvertUtils.hearRate2Percent(i, maxHr)
                countHr+=i
                //心率点数，根据点数设置背景和颜色
                val point = HeartRateConvertUtils.hearRate2Point(i, hrPercent)
                if(point == 0){
                    point1++
                }
                if(point == 1){
                    point2++
                }
                if(point == 2){
                    point3++
                }
                if(point == 3){
                    point4++
                }
                if(point == 4){
                    point5++
                }
                if(point == 5){
                    point6++
                }

                val color = HeartRateConvertUtils.getColorByPoint(context, point)

                chartList.add(LineChartEntity(index.toString(), i.toFloat(),color))

            }

            exerciseChartView.setData(chartList,true,hrMaxValue,hrMinValue, exerciseModel.startTimeStr,exerciseModel.endTimeStr)

            exerciseMaxTv.text = hrMaxValue.toString()
            exerciseMinTv.text = hrMinValue.toString()
            if (hrList != null) {
                exerciseAvgTv.text = (countHr / hrList.size).toInt().toString()
            }


            val timeList = mutableListOf<Int>()
            timeList.add(point1)
            timeList.add(point2)
            timeList.add(point3)
            timeList.add(point4)
            timeList.add(point5)
            timeList.add(point6)
            exerciseTimeChart.setExerciseTime(timeList)


            val pieChartUtils = PieChartUtils()
            pieChartUtils.setPieChart(exercisePieChart)
            pieChartUtils.setData(timeList,100f,this@ExerciseDetailActivity,false)
        }
    }


    private fun getTypeResource(type: Int): Int {
        if (type == W560BExerciseType.TYPE_WALK) {
            return R.mipmap.ic_sport_walk
        }
        if (type == W560BExerciseType.TYPE_RUN) {
            return R.mipmap.ic_sport_run
        }
        if (type == W560BExerciseType.TYPE_RIDE) {
            return R.mipmap.ic_sport_ride
        }
        if (type == W560BExerciseType.TYPE_MOUNTAINEERING) {
            return R.mipmap.ic_sport_mountaineering
        }
        if (type == W560BExerciseType.TYPE_FOOTBALL) {
            return R.mipmap.ic_sport_football
        }
        if (type == W560BExerciseType.TYPE_BASKETBALL) {
            return R.mipmap.ic_sport_basketball
        }
        if (type == W560BExerciseType.TYPE_PINGPONG) {
            return R.mipmap.ic_sport_pingpong
        }
        return if (type == W560BExerciseType.TYPE_BADMINTON) {
            R.mipmap.ic_sport_badmination
        } else R.mipmap.ic_sport_walk
    }


    private fun shotNestedScrollView(nestedScrollView: NestedScrollView?): Bitmap? {
        return if (nestedScrollView == null) {
            null
        } else try {
            var h = 0
            // 获取ScrollView实际高度
            for (i in 0 until nestedScrollView.childCount) {
                h += nestedScrollView.getChildAt(i).height
            }
            // 创建对应大小的bitmap
            val bitmap = Bitmap.createBitmap(nestedScrollView.width, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            nestedScrollView.draw(canvas)

            // 保存图片
            //savePicture(nestedScrollView.context, bitmap)
            bitmap
        } catch (oom: OutOfMemoryError) {
            null
        }
    }


    //分享图片
    private fun shareImage(context: Context, uri: Uri?, title: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, title))
    }


    private fun getTypeMap(exerciseModel: ExerciseModel): List<ExerciseItemBean>? {
        val type = exerciseModel.type
        return if (type == W560BExerciseType.TYPE_WALK || type == W560BExerciseType.TYPE_RUN) {
            val distance = exerciseModel.distance
            val disStr = CalculateUtils.mToKm(distance)
            val list: MutableList<ExerciseItemBean> = ArrayList()
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_distance),
                    getTargetType(disStr.toString() + "", "km")
                )
            )
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_consumption),
                    getTargetType(exerciseModel.kcal.toString() + "", "kcal")
                )
            )
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_count_step),
                    getTargetType(
                        exerciseModel.countStep.toString() + "",
                        resources.getString(R.string.string_step)
                    )
                )
            )
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_avg_hr),
                    getTargetType(exerciseModel.avgHr.toString() + "", "bpm")
                )
            )

            //计算速度
            val time = exerciseModel.exerciseTime
            //速度=距离/时间
            val speed = CalculateUtils.div(distance.toDouble(), (time * 60).toDouble(), 4)
            val resultSpeed = CalculateUtils.mul(speed, 3.6)


            //计算配速
            val pace = CalculateUtils.div(
                (time * 60).toDouble(),
                CalculateUtils.mToKm(distance).toDouble(),
                3
            )
            val tempPace = pace.toInt()
            Timber.e("-pace=$pace $tempPace")
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_place),
                    getTargetType(CalculateUtils.getPace(tempPace), "/km")
                )
            )
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_speed),
                    getTargetType(CalculateUtils.keepPoint(resultSpeed, 2).toString() + "", "km/h")
                )
            )
            list
        } else {
            val list: MutableList<ExerciseItemBean> = ArrayList()
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_avg_hr),
                    getTargetType(exerciseModel.avgHr.toString() + "", "bpm")
                )
            )
            list.add(
                ExerciseItemBean(
                    resources.getString(R.string.string_consumption),
                    getTargetType(exerciseModel.kcal.toString() + "", "kcal")
                )
            )
            list
        }
    }


}