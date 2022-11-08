package com.bonlala.fitalent.bean;

import java.util.List;

/**
 * Created by Admin
 * Date 2022/11/8
 * @author Admin
 */
public class GuideTypeBean {

    private String guideUrl;
    private List<DeviceTypes> list;

    public String getGuideUrl() {
        return guideUrl;
    }



    public void setGuideUrl(String guideUrl) {
        this.guideUrl = guideUrl;
    }

    public List<DeviceTypes> getList() {
        return list;
    }

    public void setList(List<DeviceTypes> list) {
        this.list = list;
    }

    public class DeviceTypes{
        private int deviceTypeId;
        private String deviceType;

        public int getDeviceTypeId() {
            return deviceTypeId;
        }

        public void setDeviceTypeId(int deviceTypeId) {
            this.deviceTypeId = deviceTypeId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }
    }
}
