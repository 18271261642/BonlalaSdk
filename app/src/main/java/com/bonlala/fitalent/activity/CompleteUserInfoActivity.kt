package com.bonlala.fitalent.activity

import android.content.Context
import android.graphics.Color
import android.view.View
import com.bonlala.action.ActivityManager
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.UserInfoModel
import com.bonlala.fitalent.dialog.DateDialog
import com.bonlala.fitalent.dialog.HeightSelectDialog
import com.bonlala.fitalent.utils.BikeUtils
import com.bonlala.fitalent.utils.CalculateUtils
import com.bonlala.fitalent.utils.MmkvUtils
import kotlinx.android.synthetic.main.activity_complete_user_info_layout.*
import timber.log.Timber

/**
 * 首次进入完善个人资料页面
 * Created by Admin
 *Date 2022/10/21
 */
class CompleteUserInfoActivity : AppActivity() {

    var userInfo : UserInfoModel ?= null


    var isKmUnit = true

    override fun getLayoutId(): Int {
       return R.layout.activity_complete_user_info_layout
    }

    override fun initView() {

        setOnClickListener(R.id.completeWomenImg,R.id.completeMenImg,
            R.id.completeBirthdayTv,R.id.completeKmTv,R.id.completeMiTv,
            R.id.completeHeightTv,R.id.completeWeightTv,R.id.completeFinishTv)
    }

    override fun initData() {
        userInfo = DBManager.getUserInfo()
        if(userInfo == null){
           DBManager.getInstance().initUserInfoData()
            userInfo = DBManager.getUserInfo()
        }

        showUserInfoData()
    }

    private fun showUserInfoData(){
        MmkvUtils.saveUnit(userInfo?.userUnit == 0)
        //性别
        val sex = userInfo?.sex
        completeWomenImg.setImageResource(if(sex == 1) R.mipmap.ic_women_check else R.mipmap.ic_women_normal)
        completeMenImg.setImageResource(if(sex == 1) R.mipmap.ic_men_normal else R.mipmap.ic_men_check)
        completeBirthdayTv.text = userInfo?.userBirthday
        completeHeightTv.text = userInfo?.userHeight.toString()+" cm"
        completeWeightTv.text = userInfo?.userWeight.toString()+" kg"

        //公英制
        val unit = userInfo?.userUnit
        if (unit != null) {
            showUnit(unit)
        }

    }

    private fun showUnit(unit : Int){
        completeKmTv.shapeDrawableBuilder.setSolidColor(if(unit == 0) Color.parseColor("#E8E9ED") else Color.parseColor("#FFFFFF")).intoBackground()
        completeMiTv.shapeDrawableBuilder.setSolidColor(if(unit == 0) Color.parseColor("#FFFFFF") else Color.parseColor("#E8E9ED")).intoBackground()
        userInfo?.userUnit = unit
        isKmUnit = unit == 0
        //处理公制或英制问题
        completeHeightTv.text = if(unit == 0) (userInfo?.userHeight.toString() +" cm") else (userInfo?.userHeight?.let {
            CalculateUtils.cmToInchValue(
                it
            ).toString()
        } +" inch")

        //体重
            var weight = userInfo?.userWeight
            if(weight == null){
                weight = 80
            }
        completeWeightTv.text =  if(unit == 0) ("$weight kg") else (CalculateUtils.kgToLbValue(weight).toString()+" lb")

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id

        when (id){
            //女
            R.id.completeWomenImg->{
                completeWomenImg.setImageResource(R.mipmap.ic_women_check)
                completeMenImg.setImageResource(R.mipmap.ic_men_normal)
                userInfo?.sex = 1
            }
            //男
            R.id.completeMenImg->{
                completeMenImg.setImageResource(R.mipmap.ic_men_check)
                completeWomenImg.setImageResource(R.mipmap.ic_women_normal)
                userInfo?.sex = 0
            }
            //生日
            R.id.completeBirthdayTv->{
                showBirthdayDialog()
            }

            //身高
            R.id.completeHeightTv->{
                val list = mutableListOf<String>()

                //判断是否是公制
                val isUnit = (userInfo?.userUnit ?: 0) == 0

                if(isUnit){
                    for(i in 80 until 255){
                        list.add(i.toString())
                    }
                }else{
                    val start = CalculateUtils.cmToInchValue(80)
                    val end = CalculateUtils.cmToInchValue(255)
                    for(i in start until end){
                        list.add(i.toString())
                    }
                }

               var h = userInfo?.userHeight
                if(h == null){
                    h = 180
                }

                showSelectDialog(0x01,resources.getString(R.string.string_height),list,if(isUnit) h.toString() else CalculateUtils.cmToInchValue(h).toString(),if(isUnit)"cm" else "inch")
            }

            //体重
            R.id.completeWeightTv->{
                val list = mutableListOf<String>()

                //判断是否是公制
                val isUnit = (userInfo?.userUnit ?: 0) == 0
                if(isUnit){
                    for(i in 30..300){
                        list.add(i.toString())
                    }
                }else{
                    val start = CalculateUtils.kgToLbValue(30)
                    val end = CalculateUtils.kgToLbValue(300)
                    for(i in start until end){
                        list.add(i.toString())
                    }
                }

                var w = userInfo?.userWeight
                if(w == null){
                    w = 80
                }
                showSelectDialog(0x02,resources.getString(R.string.string_weight),list,if(isUnit) w.toString() else CalculateUtils.kgToLbValue(w).toString(),if(isUnit)"kg" else "lb")
            }

            //公制
            R.id.completeKmTv->{
                showUnit(0)
            }

            //英制
            R.id.completeMiTv->{
                showUnit(1)
            }

            R.id.completeFinishTv->{
                finishData()
            }
        }
    }


    private fun finishData(){
        DBManager.getInstance().updateUserInfo(userInfo)
        ActivityManager.getInstance().finishActivity(LaunchActivity::class.java)
        startActivity(HomeActivity::class.java)

        finish()
    }

    private fun showSelectDialog(code:Int, title : String, data : List<String>,defaultStr : String,unitStr : String){
        var defaultIndex = 0
        for(i in data.indices){
            if(defaultStr == data[i]){
                defaultIndex = i
                break
            }
        }

        Timber.e("-----def="+defaultIndex)

        val heightSelectDialog = HeightSelectDialog.Builder(this@CompleteUserInfoActivity,data)
            .setTitleTx(title)
            .setDefaultSelect(defaultIndex)
            .setUnitShow(true,unitStr)
            .setSignalSelectListener {
                if(code == 1){
                    completeHeightTv.text = it+unitStr
                    userInfo?.userHeight = if(isKmUnit) it.toInt() else CalculateUtils.InchToCmValue(it.toInt())
                }
                if(code == 0x02){
                    completeWeightTv.text = it+unitStr
                    userInfo?.userWeight = if(isKmUnit) it.toInt() else CalculateUtils.lbToKg(it.toInt()
                        .toFloat())
                }

            }
        heightSelectDialog.show()

    }


    //生日
    private fun showBirthdayDialog(){
        val birth = userInfo?.userBirthday
        val yearArr = BikeUtils.getDayArrayOfStr(birth)

        val birthdayDialog = DateDialog.Builder(this@CompleteUserInfoActivity)
            .setYear(yearArr[0])
            .setMonth(yearArr[1])
            .setTitle(resources.getString(R.string.string_birthday))
            .setDay(yearArr[2])
            .setListener { dialog, year, month, day ->
                val birthdayStr = year.toString()+"-"+String.format("%02d",month)+"-"+String.format("%02d",day)
                completeBirthdayTv.text = birthdayStr
                userInfo?.userBirthday = birthdayStr
            }
            .show()
    }
}