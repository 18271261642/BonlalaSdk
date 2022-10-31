package com.blala.blalable;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;


/**
 * Created by Admin
 * Date 2021/9/22
 */
public class AppUtils {

    public static byte[] getArrayByFaceInd(List<RawFileBean> list, int faceInd) {

        for (RawFileBean rb : list) {
            String cuN = Utils.faceNameArray[faceInd];
            if (rb.getFileName().equals(cuN))
                return rb.getArrays();
        }
        return null;
    }

    /**
     * 获取app版本名称
     */
    public static String getAppVersionName(Context context){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

}
