package com.bonlala.fitalent.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;

/**
 * Created by Admin
 * Date 2021/9/23
 */
public  class MusicManager {


    //下一首音乐
    public static void nextMusic(AudioManager audioManager,Context mContext){
        try {
            if(audioManager != null){
                long eventTime2 = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent2 = new KeyEvent(eventTime2,eventTime2,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                audioManager.dispatchMediaKeyEvent(downEvent2);

                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent upEvent = new KeyEvent(eventTime2, eventTime2, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                mContext.sendOrderedBroadcast(upIntent, null);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //上一首音乐
    public static void previousMusic(AudioManager audioManager,Context mContext){
        try {
            if(audioManager != null){
                long eventTime2 = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent2 = new KeyEvent(eventTime2,eventTime2,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                audioManager.dispatchMediaKeyEvent(downEvent2);

                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent upEvent = new KeyEvent(eventTime2, eventTime2, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                mContext.sendOrderedBroadcast(upIntent, null);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //暂停音乐
    public static void pauseMusic(AudioManager audioManager,Context mContext){


        try {
            if(audioManager != null){
                long eventTime2 = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent2 = new KeyEvent(eventTime2,eventTime2,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                audioManager.dispatchMediaKeyEvent(downEvent2);

                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent upEvent = new KeyEvent(eventTime2, eventTime2, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                mContext.sendOrderedBroadcast(upIntent, null);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //播放音乐
    public static void playMusic(AudioManager audioManager,Context mContext){
        try {
            if(audioManager != null){
                long eventTime2 = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent2 = new KeyEvent(eventTime2,eventTime2,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                audioManager.dispatchMediaKeyEvent(downEvent2);

                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent upEvent = new KeyEvent(eventTime2, eventTime2, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                mContext.sendOrderedBroadcast(upIntent, null);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 设置音量大小
     * @param audioManager
     * @param tag
     */
    public static void setVoiceStatus(AudioManager audioManager,int tag){
        try {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, tag == 0x01 ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            audioManager.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_RAISE, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
