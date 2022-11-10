package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartSpo2Bean;
import com.bonlala.fitalent.listeners.OnItemClickListener;
import com.bonlala.fitalent.view.CustomizeSingleSpo2View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/10/11
 * @author Admin
 */
public class SingleSpo2Adapter extends RecyclerView.Adapter<SingleSpo2Adapter.SingleSpo2ViewHolder> {

    private Context mContext;

    private List<ChartSpo2Bean> list;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SingleSpo2Adapter(Context mContext, List<ChartSpo2Bean> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @NonNull
    @Override
    public SingleSpo2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_spo2_chart_layout,parent,false);
        return new SingleSpo2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleSpo2ViewHolder holder, int position) {
       ChartSpo2Bean chartBpBean = list.get(position);
       if(chartBpBean == null)
           return;
       holder.customizeSingleSpo2View.setChartSpo2Bean(chartBpBean);

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               int position = holder.getLayoutPosition();
               if(onItemClickListener != null)
                   onItemClickListener.onIteClick(position);
           }
       });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class SingleSpo2ViewHolder extends RecyclerView.ViewHolder{

        private CustomizeSingleSpo2View customizeSingleSpo2View;

        public SingleSpo2ViewHolder(@NonNull View itemView) {
            super(itemView);
            customizeSingleSpo2View = itemView.findViewById(R.id.itemSingleSpo2View);
        }
    }
}
