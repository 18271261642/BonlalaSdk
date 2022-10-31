package com.bonlala.fitalent.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;


/**
 * 消息通知的防止停止的service
 * @author Admin
 */
public abstract class MyNotificationListenerService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        toggleNotificationListenerService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toggleNotificationListenerService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Intent sevice = new Intent(this, AlertService.class);
            sevice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startService(sevice);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 被杀后再次启动时，监听不生效的问题
     */
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(new ComponentName(this, AlertService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, AlertService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
