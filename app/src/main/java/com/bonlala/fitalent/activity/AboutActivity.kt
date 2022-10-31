package com.bonlala.fitalent.activity

import android.content.Intent
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.utils.MmkvUtils
import kotlinx.android.synthetic.main.activity_about_layout.*

/**
 * Created by Admin
 *Date 2022/10/21
 */
class AboutActivity : AppActivity() {


    override fun getLayoutId(): Int {
      return R.layout.activity_about_layout
    }

    override fun initView() {
        aboutVersionTv.text = "--"

        privacyLayout.setOnClickListener {
            val privacyUrl = MmkvUtils.getPrivacyUrl()
           val intent = Intent(this@AboutActivity,ShowWebActivity::class.java)
            intent.putExtra("url",privacyUrl)
            startActivity(intent)
        }
        userAgreementLayout.setOnClickListener {
            val agreeUrl = MmkvUtils.getUserAgreement()
            val intent = Intent(this@AboutActivity,ShowWebActivity::class.java)
            intent.putExtra("url",agreeUrl)
            startActivity(intent)
        }

    }

    override fun initData() {
        //获取版本名称
        val packageManager = this.packageManager
        val packageInfo = packageManager.getPackageInfo(packageName,0)
        aboutVersionTv.text = "v"+packageInfo.versionName

    }
}