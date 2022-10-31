package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.DialBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hjq.shape.view.ShapeView;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/9/15
 */
public class DialAdapter extends AppAdapter<DialBean>{


    public DialAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialViewHolder();
    }

    private class DialViewHolder extends AppAdapter<?>.ViewHolder{

        private ImageView showImg;
        private ShapeView shapeView;

        public DialViewHolder() {
            super(R.layout.item_dial_layout);
            showImg = findViewById(R.id.itemDialImgView);
            shapeView = findViewById(R.id.itemDialCheckView);
        }

        @Override
        public void onBindView(int position) {
            //目前都是本地表盘，R.mip.xx  直接转成integer
            String imgUrl = getItem(position).getPreviewUrl();
            RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(15));
            Glide.with(getContext()).load(Integer.parseInt(imgUrl)).apply(requestOptions).into(showImg);
           boolean isChekced = getItem(position).isChecked();
           shapeView.setVisibility(isChekced ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
