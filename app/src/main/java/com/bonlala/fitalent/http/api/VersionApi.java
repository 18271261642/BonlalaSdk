package com.bonlala.fitalent.http.api;

import android.os.Handler;
import android.os.Looper;

import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/9/19
 */
public class VersionApi implements IRequestApi {




//    private int versionPlatformType;
    private int versionType;
    private String deviceType;

    public VersionApi setVersion(String deviceType,int type){
        this.deviceType = deviceType;
        this.versionType = type;
        return this;


    }

    @NonNull
    @Override
    public String getApi() {
        return "api/app/version/newest";
    }



    public static class VersionInfo  {

        /**
         * fileSize : 262288
         * versionName : 1
         * fileUrl : https://bonlala-ebike.oss-cn-shenzhen.aliyuncs.com/system/1662447603176_360Z_76027.bin
         * updateMethod : 0
         * remark : this is test english remark
         */

        private int fileSize;
        private String versionName;
        private String fileUrl;
        private int updateMethod;
        private String remark;

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public int getUpdateMethod() {
            return updateMethod;
        }

        public void setUpdateMethod(int updateMethod) {
            this.updateMethod = updateMethod;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        @Override
        public String toString() {
            return "VersionInfo{" +
                    "fileSize=" + fileSize +
                    ", versionName='" + versionName + '\'' +
                    ", fileUrl='" + fileUrl + '\'' +
                    ", updateMethod=" + updateMethod +
                    ", remark='" + remark + '\'' +
                    '}';
        }
    }
}
