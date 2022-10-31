package com.bonlala.fitalent.http.api;

import com.hjq.http.config.IRequestApi;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/10/31
 * @author Admin
 */
public class AppVersionApi implements IRequestApi {
    @NonNull
    @Override
    public String getApi() {
        return "api/app/version/newest";
    }



    private int versionPlatformType;
    private int versionType;


    public AppVersionApi setAppVersion(int versionPlatformType,int versionType){
        this.versionPlatformType = versionPlatformType;
        this.versionType = versionType;
        return this;
    }


    public class AppVersionInfo implements Serializable {


        /**
         * fileSize : 1024
         * fileUrl : https://www.baidu.com/test.apk
         * remark : 142
         * updateMethod : 0
         * versionName : 1.4.2
         */

        private int fileSize;
        private String fileUrl;
        private String remark;
        private int updateMethod;
        private String versionName;

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getUpdateMethod() {
            return updateMethod;
        }

        public void setUpdateMethod(int updateMethod) {
            this.updateMethod = updateMethod;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }
    }

}
