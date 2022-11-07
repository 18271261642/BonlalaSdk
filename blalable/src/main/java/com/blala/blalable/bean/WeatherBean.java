package com.blala.blalable.bean;

/**
 * 天气bean
 * Created by Admin
 * Date 2022/11/6
 * @author Admin
 */
public class WeatherBean {

    /**
     * 空气指数
     */
    private int airQuality;

    /**
     *实时温度
     */
    private int temperature;

    /**
     * 最高温度
     */
    private int maxTemper;

    /**
     * 最低温度
     */
    private int minTemper;

    /**
     * 天气    晴 = 0;
     *     云 = 1;
     *     风 = 2;
     *     雨 = 3;
     */
    private int weather;


    public WeatherBean() {
    }

    public WeatherBean(int airQuality, int temperature, int maxTemper, int minTemper, int weather) {
        this.airQuality = airQuality;
        this.temperature = temperature;
        this.maxTemper = maxTemper;
        this.minTemper = minTemper;
        this.weather = weather;
    }

    public int getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(int airQuality) {
        this.airQuality = airQuality;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getMaxTemper() {
        return maxTemper;
    }

    public void setMaxTemper(int maxTemper) {
        this.maxTemper = maxTemper;
    }

    public int getMinTemper() {
        return minTemper;
    }

    public void setMinTemper(int minTemper) {
        this.minTemper = minTemper;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "WeatherBean{" +
                "airQuality=" + airQuality +
                ", temperature=" + temperature +
                ", maxTemper=" + maxTemper +
                ", minTemper=" + minTemper +
                ", weather=" + weather +
                '}';
    }
}
