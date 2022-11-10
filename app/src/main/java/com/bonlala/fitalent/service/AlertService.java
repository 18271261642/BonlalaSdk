package com.bonlala.fitalent.service;


import android.app.Notification;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import com.blala.blalable.BleOperateManager;
import com.blala.blalable.listener.WriteBackDataListener;
import com.bonlala.fitalent.emu.NotifyConfig;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bonlala.fitalent.utils.CalculateUtils;
import com.bonlala.fitalent.utils.MmkvUtils;
import timber.log.Timber;

/**
 * 提醒服务  MyNotificationListenerService
 * 通过通知获取APP消息内容，需要打开通知功能
 * @author Admin
 */
public class AlertService extends MyNotificationListenerService {


    private static final String TAG = "AlertService";
    private static final String H8_NAME_TAG = "bozlun";


    private AudioManager audioManager;


    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);

    }


    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    //当系统收到新的通知后出发回调
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            //获取应用包名
            String packageName = sbn.getPackageName();
            Timber.e("------通知包名=" + packageName);
            //获取notification对象
            Notification notification = sbn.getNotification();
            if (notification == null) return;

            String msgCont = null;
            Bundle extras = notification.extras;
            //获取消息内容----标题加内容
            CharSequence tickerText = notification.tickerText;
            if (tickerText != null) {
                msgCont = tickerText.toString();
            }
            String content = "null";
            String title = "null";
            if(msgCont == null){
                if (extras != null) {
                    // 获取通知标题
                    Object object = extras.get(Notification.EXTRA_TITLE);
                    if(object == null)
                        return;
                    if(object instanceof  String){
                        title = extras.getString(Notification.EXTRA_TITLE, "");
                        // 获取通知内容
                        content = extras.getString(Notification.EXTRA_TEXT, "");
                        msgCont = title + content;
                    }else{
                        content = object.toString();
                    }
                }
            }
            if (BikeUtils.isEmpty(msgCont))
                return;
            if(msgCont.contains("正在运行"))
                return;

            //判断消息提醒是否打开了
            boolean isOpenApps = MmkvUtils.getW560BAppsStatus();
            if(!isOpenApps)
                return;

            Timber.e("-----消息="+msgCont+" title="+title);
            Timber.e("----notify="+NotifyConfig.notifyMap.get(packageName));
            if(NotifyConfig.notifyMap.get(packageName) != null){
                BleOperateManager.getInstance().sendAPPNoticeMessage(2, " ", msgCont, new WriteBackDataListener() {
                    @Override
                    public void backWriteData(byte[] data) {

                    }
                });
            }



        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }





    //舟海音乐播放推送
    private void zhouhaiMusicStatus(AudioManager am ,String musicStr){
        try {
            //当前手机音量等级
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            //最大音量等级
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            double modulusValue = CalculateUtils.div(10,maxVolume,2);
            int volumeV = (int) (currVolume * 10 * 100 * modulusValue);

            if(volumeV >10000)
                volumeV = 10000;


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //当系统通知被删掉后出发回调
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }


}
