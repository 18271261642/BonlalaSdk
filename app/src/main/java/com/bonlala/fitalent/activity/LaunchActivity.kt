package com.bonlala.fitalent.activity

import android.content.Intent
import android.os.Looper
import android.os.Message
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.blala.blalable.listener.OnCommBackDataListener
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.BaseApplication
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.dialog.GuideMsgDialog
import com.bonlala.fitalent.dialog.ShowPrivacyDialogView
import com.bonlala.fitalent.utils.MmkvUtils
import com.bonlala.fitalent.viewmodel.LaunchViewModel
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import org.json.JSONObject
import timber.log.Timber


/**
 * 启动页面
 * Created by Admin
 *Date 2022/9/5
 */
class LaunchActivity : AppActivity() {


    private val viewModel by viewModels<LaunchViewModel>()



    private val handler = object : android.os.Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val intent = Intent(this@LaunchActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun showGuide(){
        val guideMsgDialog = GuideMsgDialog(this@LaunchActivity, com.bonlala.base.R.style.BaseDialogTheme)
        guideMsgDialog.show()
        guideMsgDialog.setCancelable(false)
        guideMsgDialog.setOnCommBackDataListener(object : OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
                guideMsgDialog.dismiss()
                startActivity(CompleteUserInfoActivity::class.java)
            }

            override fun onStrDataBack(vararg value: String?) {
                guideMsgDialog.dismiss()
                val intent = Intent(this@LaunchActivity,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }



    override fun getLayoutId(): Int {
       return R.layout.activity_launch_layout
    }

    override fun initView() {
        getUrls()
        val userInfoModel = DBManager.getUserInfo()
        if(userInfoModel == null){
            showPrivacyDialog()
        }else{
            handler.sendEmptyMessageDelayed(0x00,3000)
        }
    }


    private fun showPrivacyDialog(){
        val privacyDialog = ShowPrivacyDialogView(this@LaunchActivity, com.bonlala.base.R.style.BaseDialogTheme,this@LaunchActivity)
        privacyDialog.show()
        privacyDialog.setOnPrivacyClickListener(object : ShowPrivacyDialogView.OnPrivacyClickListener{
            override fun onCancelClick() {
                privacyDialog.dismiss()
                finish()
            }

            override fun onConfirmClick() {
                privacyDialog.dismiss()
                MmkvUtils.setIsAgreePrivacy(true)
                BaseApplication.getInstance().setInitBugly()
                //初始化一次默认的用户信息
                DBManager.getInstance().initUserInfoData()

                showGuide()

            }

        })
    }



    override fun initData() {

    }


    private fun getUrls(){
        getH5Url()
        viewModel.guideUrl.observe(this){

        }

        viewModel.guiderTypeList.observe(this){
            Timber.e("-------guide="+Gson().toJson(it))

            val bean = it
            val url = bean?.guideUrl

            val list = it?.list
            var typeId = 0
            list?.forEach {
                if(it.deviceType == "W560B"){
                    typeId = it.deviceTypeId
                }
            }
            MmkvUtils.saveGuideUrl(url+typeId)
        }
        viewModel.getGuideUrl(this)
        viewModel.getGuideTypeList(this)
    }

    //获取H5链接
    private fun getH5Url(){
        EasyHttp.get(this).api("/api/app/common/h5urls").request(object : OnHttpListener<String> {

            override fun onSucceed(result: String?) {
                Timber.e("-----ss="+result)
                if(result == null)
                    return
                try {
                    val jsonObject = JSONObject(result)
                    val dataJsonObject = jsonObject.getJSONObject("data");
                    if(dataJsonObject == null)
                        return
                    val userAgreementUrl = dataJsonObject.getString("userAgreementUrl")
                    MmkvUtils.saveUserAgreement(userAgreementUrl)
                    val privacyUrl = dataJsonObject.getString("privacyAgreementUrl")
                    MmkvUtils.savePrivacyUrl(privacyUrl)

//                    val deviceGuideUrl = dataJsonObject.getString("deviceGuideUrl")

//                    MmkvUtils.saveGuideUrl(deviceGuideUrl)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }

            override fun onFail(e: Exception?) {
                Timber.e("-----ee="+e?.message)
            }

        })
    }
}