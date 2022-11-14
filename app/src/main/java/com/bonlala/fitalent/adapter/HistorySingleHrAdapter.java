package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.db.model.SingleHeartModel;
import com.bonlala.fitalent.utils.BikeUtils;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * 单次测量心率的列表adapter
 * Created by Admin
 * Date 2022/10/12
 * @author Admin
 */
public class HistorySingleHrAdapter extends AppAdapter<SingleHeartModel>{


    private boolean isChinese ;
    public HistorySingleHrAdapter(@NonNull Context context) {
        super(context);
        isChinese = BaseApplication.getInstance().getIsChinese();
    }



    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistorySingleHrViewHolder();
    }

    class HistorySingleHrViewHolder extends AppAdapter<?>.ViewHolder{

        private TextView timeTv;
        private TextView valueTv;


        public HistorySingleHrViewHolder() {
            super(R.layout.item_history_single_ht_layout);
            timeTv = findViewById(R.id.itemHistoryHrTimeTv);
            valueTv = findViewById(R.id.itemHistoryHrValueTv);
        }

        @Override
        public void onBindView(int position) {
            SingleHeartModel singleHeartModel = getItem(position);
            String timeTxt = isChinese ? BikeUtils.getFormatDate(singleHeartModel.getSaveLongTime(),"yyyy-MM-dd HH:mm:ss") : BikeUtils.getFormatDate(singleHeartModel.getSaveLongTime(),"MMM dd,yyyy HH:mm:ss", Locale.ENGLISH);
            timeTv.setText(timeTxt);
           if(singleHeartModel.getHeartValue() != 255){
               valueTv.setText(singleHeartModel.getHeartValue()+"bpm");
           }
        }
    }

}
