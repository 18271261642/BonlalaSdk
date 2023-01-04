package com.bonlala.fitalent.ui.home


import androidx.fragment.app.FragmentTransaction
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.db.DBManager
import com.bonlala.fitalent.emu.DeviceType
import timber.log.Timber

/**
 * Created by Admin
 *Date 2022/12/13
 */
class BaseHomeFragment : TitleBarFragment<HomeActivity>() {



    var fragmentTransaction : FragmentTransaction ?= null


    fun  getInstance() : BaseHomeFragment{
        return BaseHomeFragment()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_home_layout
    }

    override fun initView() {

    }

    override fun initData() {
       val fragmentManager  = parentFragmentManager
       fragmentTransaction = fragmentManager.beginTransaction()

        setTypeFragment()
    }


    public fun setTypeFragment(){
        val type = DBManager.getBindDeviceType()

        fragmentTransaction?.replace(R.id.baseFragmentLayout,if(type == DeviceType.DEVICE_561) HrBeltHomeFragment().getInstance() else HomeFragment().newInstance())
        fragmentTransaction?.commit()

    }


    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        Timber.e("-------fragmentResume="+first)
    }


    override fun onResume() {
        super.onResume()

        Timber.e("------onResume---")


    }
}