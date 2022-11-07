package com.bonlala.fitalent.http.api;

import com.hjq.http.config.IRequestApi;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 查询15天的天气数据
 * Created by Admin
 * Date 2022/10/24
 * @author Admin
 */
public class WeatherRecordApi implements IRequestApi {
    @NonNull
    @Override
    public String getApi() {
        return "api/app/common/weather/forecast15days";
    }

    /**城市名称+区域名称,示例值(深圳市宝安区)**/
    private String cityDetailName;

    /**地理纬度,**/
    private double lat;

    /**地理经度**/
    private double lng;

    /**类型**/
    private String deviceType;

    public WeatherRecordApi setWeatherRecordApi(String cityDetailName, double lat, double lng,String deviceType) {
        this.cityDetailName = cityDetailName;
        this.lat = lat;
        this.lng = lng;
        this.deviceType = deviceType;
        return this;
    }

    public class WeatherRecordBean{

        /**
         * aqiValue : 1024
         * temp : 27
         * weathers : [{"currentDate":"2022-02-02","hiTemp":27,"lowTemp":27,"temp":27,"weatherImgUrl":"https://www.baidu.com/test.png","weatherTypeCode":"0：晴天 1：多云 2：阴天 3：雾 4：霾 5：沙尘 6：沙尘 7：阵雨 8：小雨 9：中雨 10：大雨 11：沙尘暴 12：暴雨 13：雷电 14：冰雹 15：阵雪 16：小雪 17：中雪 18：大雪 19：暴雪"}]
         */

        private int aqiValue;
        private int temp;
        private List<WeathersBean> weathers;

        public int getAqiValue() {
            return aqiValue;
        }

        public void setAqiValue(int aqiValue) {
            this.aqiValue = aqiValue;
        }

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public List<WeathersBean> getWeathers() {
            return weathers;
        }

        public void setWeathers(List<WeathersBean> weathers) {
            this.weathers = weathers;
        }

        public  class WeathersBean {
            /**
             * currentDate : 2022-02-02
             * hiTemp : 27
             * lowTemp : 27
             * temp : 27
             * weatherImgUrl : https://www.baidu.com/test.png
             * weatherTypeCode : 0：晴天 1：多云 2：阴天 3：雾 4：霾 5：沙尘 6：沙尘 7：阵雨 8：小雨 9：中雨 10：大雨 11：沙尘暴 12：暴雨 13：雷电 14：冰雹 15：阵雪 16：小雪 17：中雪 18：大雪 19：暴雪
             */

            private String currentDate;
            private int hiTemp;
            private int lowTemp;
            private int temp;
            private String weatherImgUrl;
            private int deviceWeatherCode;

            public String getCurrentDate() {
                return currentDate;
            }

            public void setCurrentDate(String currentDate) {
                this.currentDate = currentDate;
            }

            public Integer getHiTemp() {
                return hiTemp;
            }

            public void setHiTemp(Integer hiTemp) {
                this.hiTemp = hiTemp;
            }

            public Integer getLowTemp() {
                return lowTemp;
            }

            public void setLowTemp(Integer lowTemp) {
                this.lowTemp = lowTemp;
            }

            public Integer getTemp() {
                return temp;
            }

            public void setTemp(Integer temp) {
                this.temp = temp;
            }

            public String getWeatherImgUrl() {
                return weatherImgUrl;
            }

            public void setWeatherImgUrl(String weatherImgUrl) {
                this.weatherImgUrl = weatherImgUrl;
            }

            public int getDeviceWeatherCode() {
                return deviceWeatherCode;
            }

            public void setDeviceWeatherCode(int deviceWeatherCode) {
                this.deviceWeatherCode = deviceWeatherCode;
            }
        }
    }
}
