package com.bonlala.fitalent.http.api;

import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * 玩转设备
 * Created by Admin
 * Date 2022/11/2
 * @author Admin
 */
public class PlaySpinApi implements IRequestApi {
    @NonNull
    @Override
    public String getApi() {
        return "api/app/device/playSpin/"+deviceType;
    }


    @HttpIgnore
    private String deviceType;

    public PlaySpinApi setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public class PlaySpinBean{


        /**
         * language : 1
         * groupNum : 1
         * imgUrl : https://bonlala-offline.oss-ap-southeast-1.aliyuncs.com/dev/system/playSpin/16/1/1/2.bmp
         * title : wzsb
         * remarkTop : wzsb
         * remarkFoot : wzsb
         * sort : null
         */

        private int language;
        private int groupNum;
        private String imgUrl;
        private String title;
        private String remarkTop;
        private String remarkFoot;
        private String sort;

        public int getLanguage() {
            return language;
        }

        public void setLanguage(int language) {
            this.language = language;
        }

        public int getGroupNum() {
            return groupNum;
        }

        public void setGroupNum(int groupNum) {
            this.groupNum = groupNum;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRemarkTop() {
            return remarkTop;
        }

        public void setRemarkTop(String remarkTop) {
            this.remarkTop = remarkTop;
        }

        public String getRemarkFoot() {
            return remarkFoot;
        }

        public void setRemarkFoot(String remarkFoot) {
            this.remarkFoot = remarkFoot;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
    }

}
