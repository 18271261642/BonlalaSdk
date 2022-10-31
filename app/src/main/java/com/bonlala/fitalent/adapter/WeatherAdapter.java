package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.http.api.WeatherRecordApi;
import com.bonlala.fitalent.utils.BikeUtils;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;

/**
 * 天气的adapter
 * Created by Admin
 * Date 2022/10/24
 * @author Admin
 */
public class WeatherAdapter extends AppAdapter<WeatherRecordApi.WeatherRecordBean.WeathersBean>{


    public WeatherAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder();
    }

    private final class WeatherViewHolder extends AppAdapter<?>.ViewHolder{

        /**最高和最低温度**/
        private TextView itemRecordWeatherScopeTv;
        /**图片**/
        private ImageView itemRecordWeatherImgView;
        /**周**/
        private TextView itemRecordWeekTv;
        /**日期**/
        private TextView itemRecordWeatherDateTv;



        public WeatherViewHolder() {
            super(R.layout.item_record_weather_layout);
            itemRecordWeatherScopeTv = findViewById(R.id.itemRecordWeatherScopeTv);
            itemRecordWeatherImgView = findViewById(R.id.itemRecordWeatherImgView);
            itemRecordWeekTv = findViewById(R.id.itemRecordWeekTv);
            itemRecordWeatherDateTv = findViewById(R.id.itemRecordWeatherDateTv);
        }

        @Override
        public void onBindView(int position) {
            WeatherRecordApi.WeatherRecordBean.WeathersBean wb = getItem(position);
            itemRecordWeatherScopeTv.setText(wb.getLowTemp()+"℃"+"~"+wb.getHiTemp()+"℃");
            itemRecordWeatherDateTv.setText(wb.getCurrentDate());
            itemRecordWeekTv.setText(BikeUtils.getWeekForDay(getContext(),wb.getCurrentDate()));
            Glide.with(getContext()).load(wb.getWeatherImgUrl()).into(itemRecordWeatherImgView);

        }
    }

}
