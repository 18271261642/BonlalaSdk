package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ExerciseItemBean;
import com.bonlala.fitalent.emu.W560BExerciseType;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/10/31
 * @author Admin
 */
public class ItemExerciseAdapter extends RecyclerView.Adapter<ItemExerciseAdapter.ItemV> {

    private Context context;
    private List<ExerciseItemBean> list ;



    public ItemExerciseAdapter(Context context, List<ExerciseItemBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_type_layout,parent,false);
        return new ItemV(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemV holder, int position) {
        ExerciseItemBean exerciseItemBean = list.get(position);
        holder.type.setText(exerciseItemBean.getTypeName());
        holder.value.setText(exerciseItemBean.getSpannableString());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemV extends RecyclerView.ViewHolder{
        private TextView type;
        private TextView value;


        public ItemV(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.itemExerciseTypeTypeTv);
            value = itemView.findViewById(R.id.itemExerciseTypeValueTv);
        }
    }
}
