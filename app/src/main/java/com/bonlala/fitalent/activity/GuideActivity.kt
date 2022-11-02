package com.bonlala.fitalent.activity

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bonlala.action.AppActivity
import com.bonlala.action.SingleClick
import com.bonlala.fitalent.R
import com.bonlala.fitalent.adapter.GuideAdapter
import com.bonlala.fitalent.viewmodel.GuideViewModel
import me.relex.circleindicator.CircleIndicator3

/**
 * 玩转设备页面
 * Created by Admin
 *Date 2022/11/2
 */
class GuideActivity : AppActivity(){

    private var mViewPager: ViewPager2? = null
    private var mIndicatorView: CircleIndicator3? = null
    private var mCompleteView: View? = null
    private var mAdapter : GuideAdapter ?= null

    private val viewModel by viewModels<GuideViewModel>()



    override fun getLayoutId(): Int {
       return R.layout.activity_guide_layout
    }

    override fun initView() {
        mViewPager = findViewById(R.id.vp_guide_pager)
        mIndicatorView = findViewById(R.id.cv_guide_indicator)
        mCompleteView = findViewById(R.id.btn_guide_complete)
        setOnClickListener(mCompleteView)
    }

    override fun initData() {
        mAdapter = GuideAdapter(this)
        mViewPager!!.adapter = mAdapter
        mViewPager!!.registerOnPageChangeCallback(mCallback)
        mIndicatorView!!.setViewPager(mViewPager)

        viewModel.playDevice.observe(this){
            mAdapter!!.data = it
            mViewPager!!.adapter = mAdapter
            mIndicatorView!!.setViewPager(mViewPager)
        }
        viewModel.getDevicePlay(this)
    }

    @SingleClick
    override fun onClick(view: View) {
        if (view === mCompleteView) {

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewPager!!.unregisterOnPageChangeCallback(mCallback)
    }


    private val mCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (mViewPager!!.currentItem != mAdapter!!.count - 1 || positionOffsetPixels <= 0) {
                return
            }
            mIndicatorView!!.visibility = View.VISIBLE
            mCompleteView!!.visibility = View.INVISIBLE
            mCompleteView!!.clearAnimation()
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state != ViewPager2.SCROLL_STATE_IDLE) {
                return
            }
            val lastItem = mViewPager!!.currentItem == mAdapter!!.count - 1
            mIndicatorView!!.visibility = if (lastItem) View.INVISIBLE else View.VISIBLE
            mCompleteView!!.visibility = if (lastItem) View.VISIBLE else View.INVISIBLE
//            if (lastItem) {
//                // 按钮呼吸动效
//                val animation = ScaleAnimation(
//                    1.0f, 1.1f, 1.0f, 1.1f,
//                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
//                )
//                animation.duration = 350
//                animation.repeatMode = Animation.REVERSE
//                animation.repeatCount = Animation.INFINITE
//                mCompleteView!!.startAnimation(animation)
//            }
        }
    }

}