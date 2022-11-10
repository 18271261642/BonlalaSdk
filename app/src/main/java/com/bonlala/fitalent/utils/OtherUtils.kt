package com.bonlala.fitalent.utils

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import java.lang.reflect.InvocationTargetException

/**
 * Created by Admin
 *Date 2022/11/10
 */
class OtherUtils {

    /*
    * 判断当前app在手机中是否开启了允许消息推送
    * @param mContext Context
    * @return Boolean
    */
   public inline fun isNotificationEnabled(mContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = mNotificationManager.getNotificationChannel("com.bonlala.fitalent")
            !(!mNotificationManager.areNotificationsEnabled() || channel.importance == NotificationManager.IMPORTANCE_NONE)
        } else if (Build.VERSION.SDK_INT >= 24) {
            val mNotificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.areNotificationsEnabled()
        } else if (Build.VERSION.SDK_INT >= 19) {
            val CHECK_OP_NO_THROW = "checkOpNoThrow"
            val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"
            val appOps = mContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val appInfo = mContext.applicationInfo
            val pkg = mContext.applicationContext.packageName
            val uid = appInfo.uid
            try {
                val appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val checkOpNoThrowMethod = appOpsClass.getMethod(
                    CHECK_OP_NO_THROW, Integer.TYPE,
                    Integer.TYPE, String::class.java
                )
                val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
                val value = opPostNotificationValue[Int::class.java] as Int
                (checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) as Int
                        == AppOpsManager.MODE_ALLOWED)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                true
            } catch (e: NoSuchMethodException) {
                true
            } catch (e: NoSuchFieldException) {
                true
            } catch (e: InvocationTargetException) {
                true
            } catch (e: IllegalAccessException) {
                true
            } catch (e: RuntimeException) {
                true
            }
        } else {
            true
        }
    }

}