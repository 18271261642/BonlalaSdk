package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartBpBean;
import com.bonlala.fitalent.listeners.OnItemClickListener;
import com.bonlala.fitalent.view.CustomizeSingleBpView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/10/11
 * @author Admin
 */
public class SingleBpAdapter extends RecyclerView.Adapter<SingleBpAdapter.SingleBpViewHolder> {

    private Context mContext;

    private List<ChartBpBean> list;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SingleBpAdapter(Context mContext, List<ChartBpBean> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @NonNull
    @Override
    public SingleBpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_bp_chart_layout,parent,false);
        return new SingleBpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleBpViewHolder holder, int position) {
       ChartBpBean chartBpBean = list.get(position);
       holder.customizeSingleBpView.setChartBpBean(chartBpBean);

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

    class SingleBpViewHolder extends RecyclerView.ViewHolder{

        private CustomizeSingleBpView customizeSingleBpView;

        public SingleBpViewHolder(@NonNull View itemView) {
            super(itemView);
            customizeSingleBpView = itemView.findViewById(R.id.itemSingleBpView);
        }
    }
}
