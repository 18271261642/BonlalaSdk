package com.bonlala.fitalent.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonlala.base.BaseAdapter;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.http.api.PlaySpinApi;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/11/2
 * @author Admin
 */
public class GuideAdapter extends AppAdapter<PlaySpinApi.PlaySpinBean>{


    public GuideAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GuideViewHolder();
    }

    private class GuideViewHolder extends AppAdapter<?>.ViewHolder{

        private TextView titleTv;
        private TextView contentTv;
        private TextView footTv;
        private ImageView showImg;


        public GuideViewHolder() {
            super(R.layout.item_guide_play_layout);
            titleTv = findViewById(R.id.itemPlayTitleTv);
            contentTv = findViewById(R.id.itemPlayContentTv);
            showImg = findViewById(R.id.itemGuideImgView);
            footTv = findViewById(R.id.itemPlayContentFootTv);
        }

        @Override
        public void onBindView(int position) {
            PlaySpinApi.PlaySpinBean playSpinBean = getItem(position);
            titleTv.setText(playSpinBean.getTitle());
            contentTv.setText(playSpinBean.getRemarkTop());
            footTv.setText(playSpinBean.getRemarkFoot());
            String url = playSpinBean.getImgUrl();

            Glide.with(getContext()).load(url).into(showImg);

        }
    }
}
