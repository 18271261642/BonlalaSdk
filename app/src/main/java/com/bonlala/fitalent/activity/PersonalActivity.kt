package com.bonlala.fitalent.activity

import android.view.View
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.db.model.UserInfoModel
import com.bonlala.fitalent.dialog.DateDialog
import com.bonlala.fitalent.dialog.HeightSelectDialog
import com.bonlala.fitalent.utils.BikeUtils
import kotlinx.android.synthetic.main.activity_personal_layout.*

/**
 * 个人资料页面
 * Created by Admin
 *Date 2022/10/21
 */
class PersonalActivity : AppActivity() {

    private var userInfo : UserInfoModel ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_personal_layout
    }

    override fun initView() {
        setOnClickListener(R.id.personalSexBar,R.id.personalBirthdayBar,
            R.id.personalHeightBar,R.id.personalWeightBar)
    }

    override fun initData() {
        showPersonalData()
    }

    //展示个人信息，数据库中查询
    fun showPersonalData(){
       userInfo = DBManager.getUserInfo()
        if(userInfo == null)
            return

        personalSexBar.rightText = if(userInfo?.sex==0) resources.getString(R.string.string_men) else resources.getString(R.string.string_women)
        personalBirthdayBar.rightText = userInfo?.userBirthday
        personalHeightBar.rightText = userInfo?.userHeight.toString() +" cm"
        personalWeightBar.rightText = userInfo?.userWeight.toString()+" kg"


    }

    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id

        when(id){
            R.id.personalSexBar->{
                val list = mutableListOf<String>()
                list.add(resources.getString(R.string.string_men))
                list.add(resources.getString(R.string.string_women))
                showSelectDialog(0x00,resources.getString(R.string.string_height),list,if(userInfo?.sex==0)resources.getString(R.string.string_men) else resources.getString(R.string.string_women),"")
            }

            R.id.personalBirthdayBar->{
                showBirthdayDialog()
            }
            R.id.personalHeightBar->{
                val list = mutableListOf<String>()
                for(i in 80..260){
                    list.add(i.toString())
                }
                showSelectDialog(0x01,resources.getString(R.string.string_height),list,userInfo?.userHeight.toString(),"cm")
            }

            R.id.personalWeightBar->{
                val list = mutableListOf<String>()
                for(i in 30..150){
                    list.add(i.toString())
                }
                showSelectDialog(0x02,resources.getString(R.string.string_weight),list,userInfo?.userWeight.toString(),"kg")
            }
        }
    }

    /**
     * 弹窗
     */
    private fun showSelectDialog(code:Int, title : String, data : List<String>,defaultStr : String,unitStr : String){
        var defaultIndex = 0
        for(i in data.indices){
            if(defaultStr == data.get(i)){
                defaultIndex = i
                break
            }
        }

        val heightSelectDialog = HeightSelectDialog.Builder(this@PersonalActivity,data)
            .setTitleTx(title)
            .setDefaultSelect(defaultIndex)
            .setUnitShow(true,unitStr)
            .setSignalSelectListener {
                if(code == 0){
                    personalSexBar.rightText = it
                    userInfo?.sex = if(it.equals(resources.getString(R.string.string_men))) 0 else 1
                }
                if(code == 1){
                    personalHeightBar.rightText = it+" cm"
                    userInfo?.userHeight = it.toInt()
                }
                if(code == 0x02){
                    personalWeightBar.rightText = it+" kg"
                    userInfo?.userWeight = it.toInt()
                }
                saveUserInfo()
            }
        heightSelectDialog.show()

    }


    //生日
    private fun showBirthdayDialog(){
        val birth = userInfo?.userBirthday
        val yearArr = BikeUtils.getDayArrayOfStr(birth)

        val birthdayDialog = DateDialog.Builder(this@PersonalActivity)
            .setYear(yearArr[0])
            .setMonth(yearArr[1])
            .setTitle(resources.getString(R.string.string_birthday))
            .setDay(yearArr[2])
            .setListener { dialog, year, month, day ->
                val birthdayStr = year.toString()+"-"+String.format("%02d",month)+"-"+String.format("%02d",day)
                personalBirthdayBar.rightText = birthdayStr
                userInfo?.userBirthday = birthdayStr
                saveUserInfo()
            }
            .show()
    }


    private fun saveUserInfo(){
        DBManager.getInstance().updateUserInfo(userInfo)
    }
}