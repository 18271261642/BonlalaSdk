package com.bonlala.fitalent.dialog

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat.getSystemService
import com.bonlala.fitalent.R
import com.bonlala.fitalent.emu.MediaPlayerType
import com.bonlala.fitalent.listeners.OnItemClickListener
import com.bonlala.fitalent.utils.MediaPlayerUtils
import java.io.IOException

/**
 * 运动开始倒计时3秒
 * Created by Admin
 *Date 2023/1/2
 */
class CountDownTimerView : AppCompatDialog{


    //是否结束
    private var onItemClickListener : OnItemClickListener ?= null

    fun setOnCompleteListener(onClick : OnItemClickListener){
        this.onItemClickListener = onClick
    }

    private var mediaPlayerUtils : MediaPlayerUtils ?= null

    //显示的数字
    private var countDownTv : TextView ?= null


    private val mHandler : Handler = object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                countTime--
                if(countTime == 0){
                    countDownTv?.text = "GO"
//                    player?.stop()

                    onItemClickListener?.onIteClick(0x00)
                }else{
                    mediaPlayerUtils?.playToAudio(context,if(countTime == 2) MediaPlayerType.AUDIO_TWO else MediaPlayerType.AUDIO_ONE)
                    countDownTv?.text = countTime.toString()
                    countDownTv?.animation = setAnim()
                    countDownTv?.startAnimation(setAnim())
                    startCountDown(countTime)
                }

            }
        }
    }

    //总的计时
     var countTime = 0

    constructor(context: Context) : super (context){

    }


    constructor(context: Context,theme : Int) : super (context,theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_countdown_timer_layout)

        countDownTv = findViewById(R.id.countDownTv)

        mediaPlayerUtils = MediaPlayerUtils()
        initDataPlayer(context)
    }



    //开始倒计时 3,2,1
    fun startCountDown(time : Int){
        //player?.start()


        countTime = time
        countDownTv?.text = time.toString()
        countDownTv?.animation = setAnim()
        countDownTv?.startAnimation(setAnim())
        mHandler.sendEmptyMessageDelayed(0x00,1200)
    }


    /**
     *
     * 提示音
     *
     * @param
     */
    var player: MediaPlayer? = MediaPlayer()
    fun initDataPlayer(context: Context) {
        //1 初始化AudioManager对象
        val mAudioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //2 申请焦点
        mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        val fileDescriptor: AssetFileDescriptor
        try {
            //3 获取音频文件,我从网上下载的歌曲，放到了assets目录下
            fileDescriptor = context.resources.openRawResourceFd(R.raw.end_sport)
            //4 实例化MediaPlayer对象
            //5 设置播放流类型
            player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            //6 设置播放源，有多个参数可以选择，具体参考相关文档，本文旨在介绍音频焦点
            player!!.setDataSource(fileDescriptor.fileDescriptor,
                fileDescriptor.startOffset,
                fileDescriptor.length)
            //7 设置循环播放
            player!!.isLooping = false
            //8 准备监听
            player!!.setOnPreparedListener(MediaPlayer.OnPreparedListener { //9 准备完成后自动播放
                //  mMediaPlayer.start()
                player!!.setVolume(0.5f, 0.5f);
            })
            //10 异步准备
            player!!.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private val mAudioFocusChange: AudioManager.OnAudioFocusChangeListener = object : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    //长时间丢失焦点,当其他应用申请的焦点为AUDIOFOCUS_GAIN时，
                    //会触发此回调事件，例如播放QQ音乐，网易云音乐等
                    //通常需要暂停音乐播放，若没有暂停播放就会出现和其他音乐同时输出声音

                    //释放焦点，该方法可根据需要来决定是否调用
                    //若焦点释放掉之后，将不会再自动获得
                    //mAudioManager.abandonAudioFocus(this)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    //短暂性丢失焦点，当其他应用申请AUDIOFOCUS_GAIN_TRANSIENT或AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE时，
                    //会触发此回调事件，例如播放短视频，拨打电话等。
                    //通常需要暂停音乐播放
                    // stop()
                    player!!.setVolume(0.2f, 0.2f);
                    //Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                }
//                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->                     //短暂性丢失焦点并作降音处理
                   // Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                AudioManager.AUDIOFOCUS_GAIN -> {
                    //当其他应用申请焦点之后又释放焦点会触发此回调
                    //可重新播放音乐
                    player!!.setVolume(0.5f, 0.5f);
                    //Log.d(TAG, "AUDIOFOCUS_GAIN")
                    // start()
                }
            }
        }
    }


    private fun setAnim() : AnimationSet{
        val alphaAnimation = AlphaAnimation(1.0f,0f)
        alphaAnimation.duration = 1000
        val animation = ScaleAnimation(0f,2.6f,0f,2.6f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        animation.duration = 1000
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(animation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.fillAfter = true
        return animationSet

    }
}