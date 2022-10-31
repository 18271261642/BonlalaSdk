package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.NotifyBean;
import com.bonlala.fitalent.view.CheckButtonView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 消息提醒的adapter
 * Created by Admin
 * Date 2022/10/31
 * @author Admin
 */
public class AppNotifyAdapter extends RecyclerView.Adapter<AppNotifyAdapter.AppViewHolder> {

    private Context context;
    private List<NotifyBean> list;

    public AppNotifyAdapter(Context context, List<NotifyBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notify_item_layout,parent,false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        NotifyBean notifyBean = list.get(position);
        holder.checkButtonView.setLeftTitle(notifyBean.getAppName());
        holder.checkButtonView.setCheckStatus(notifyBean.isChecked());
        holder.checkButtonView.setLeftImgResource(notifyBean.getResource());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    final class AppViewHolder extends RecyclerView.ViewHolder{

        private CheckButtonView checkButtonView;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            checkButtonView = itemView.findViewById(R.id.itemNotifyView);

        }
    }
}
