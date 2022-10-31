package com.bonlala.fitalent.activity

import androidx.activity.viewModels
import com.bonlala.action.AppActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.viewmodel.DfuViewModel

/**
 * Created by Admin
 *Date 2022/9/19
 */
class DfuActivity : AppActivity() {

    private val viewModel by viewModels<DfuViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.activity_dfu_layout
    }

    override fun initView() {

    }

    override fun initData() {
        viewModel.getServerData.observe(this){

        }
        //获取后台固件版本信息
        viewModel.getServerVersionInfo(this)
    }
}