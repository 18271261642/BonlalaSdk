package com.bonlala.fitalent.activity

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
import kotlinx.android.synthetic.main.activity_complete_user_info_layout.*

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
                for(i in 80 until 255){
                    list.add(i.toString())
                }
                showSelectDialog(0x01,resources.getString(R.string.string_height),list,userInfo?.userHeight.toString(),"cm")
            }

            //体重
            R.id.completeWeightTv->{
                val list = mutableListOf<String>()
                for(i in 30..150){
                    list.add(i.toString())
                }
                showSelectDialog(0x02,resources.getString(R.string.string_weight),list,userInfo?.userWeight.toString(),"kg")
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
            if(defaultStr == data.get(i)){
                defaultIndex = i
                break
            }
        }

        val heightSelectDialog = HeightSelectDialog.Builder(this@CompleteUserInfoActivity,data)
            .setTitleTx(title)
            .setDefaultSelect(defaultIndex)
            .setUnitShow(true,unitStr)
            .setSignalSelectListener {
                if(code == 1){
                    completeHeightTv.text = it+" cm"
                    userInfo?.userWeight = it.toInt()
                }
                if(code == 0x02){
                    completeWeightTv.text = it+" kg"
                    userInfo?.userWeight = it.toInt()
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