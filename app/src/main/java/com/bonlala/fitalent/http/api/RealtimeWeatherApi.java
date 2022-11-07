package com.bonlala.fitalent.http.api;

import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * 实时天气的接口
 * Created by Admin
 * Date 2022/10/24
 * @author Admin
 */
public class RealtimeWeatherApi implements IRequestApi {
    /**
     * weatherImgUrl : https://bonlala-ebike.oss-cn-shenzhen.aliyuncs.com/system/1650099190281_qing@2x.png
     * weatherTypeCode : 0
     * temp : 26
     * hiTemp : null
     * lowTemp : null
     * currentDate : 2022-10-24
     */

    private String weatherImgUrl;
    private Integer weatherTypeCode;
    private Integer temp;
    private Object hiTemp;
    private Object lowTemp;
    private String currentDate;

    @NonNull
    @Override
    public String getApi() {
        return "api/app/common/weather/immediate";
    }

    /**城市名称+区域名称,示例值(深圳市宝安区)**/
    private String cityDetailName;

    /**地理纬度,**/
    private double lat;

    /**地理经度**/
    private double lng;

    /**类型**/
    private String deviceType;

    public RealtimeWeatherApi setRealtimeWeatherApi(String cityDetailName, double lat, double lng,String type) {
        this.cityDetailName = cityDetailName;
        this.lat = lat;
        this.lng = lng;
        this.deviceType = type;
        return this;
    }


    public class RealtimeWeatherBean{


        /**
         * weatherImgUrl : https://bonlala-ebike.oss-cn-shenzhen.aliyuncs.com/system/1650099190281_qing@2x.png
         * weatherTypeCode : 0
         * temp : 26
         * hiTemp : null
         * lowTemp : null
         * currentDate : 2022-10-24
         */

        private String weatherImgUrl;
        private int weatherTypeCode;
        private int temp;
        private Object hiTemp;
        private Object lowTemp;
        private String currentDate;

        public String getWeatherImgUrl() {
            return weatherImgUrl;
        }

        public void setWeatherImgUrl(String weatherImgUrl) {
            this.weatherImgUrl = weatherImgUrl;
        }

        public Integer getWeatherTypeCode() {
            return weatherTypeCode;
        }

        public void setWeatherTypeCode(int weatherTypeCode) {
            this.weatherTypeCode = weatherTypeCode;
        }

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public Object getHiTemp() {
            return hiTemp;
        }

        public void setHiTemp(Object hiTemp) {
            this.hiTemp = hiTemp;
        }

        public Object getLowTemp() {
            return lowTemp;
        }

        public void setLowTemp(Object lowTemp) {
            this.lowTemp = lowTemp;
        }

        public String getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(String currentDate) {
            this.currentDate = currentDate;
        }
    }
}
