package com.bonlala.fitalent.ui.notifications

import android.view.View
import com.bonlala.action.TitleBarFragment
import com.bonlala.fitalent.HomeActivity
import com.bonlala.fitalent.R
import com.bonlala.fitalent.activity.AboutActivity
import com.bonlala.fitalent.activity.FeedbackActivity
import com.bonlala.fitalent.activity.PersonalActivity
import com.bonlala.fitalent.activity.ShowPermissionActivity
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : TitleBarFragment<HomeActivity>() {


    fun getInstance() : NotificationsFragment{
        return NotificationsFragment()
    }



    override fun getLayoutId(): Int {
       return R.layout.fragment_notifications
    }

    override fun initView() {

        setOnClickListener(R.id.minePermissionLayout,R.id.mineFeedbackLayout,
            R.id.mineAbout,R.id.minePersonalLayout)
    }

    override fun initData() {

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }


    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id

        when (id){
            R.id.minePersonalLayout->{
                startActivity(PersonalActivity::class.java)
            }
            R.id.minePermissionLayout->{
                startActivity(ShowPermissionActivity::class.java)
            }

            R.id.mineFeedbackLayout->{
                startActivity(FeedbackActivity::class.java)
            }
            R.id.mineAbout->{
                startActivity(AboutActivity::class.java)
            }
        }
    }
}