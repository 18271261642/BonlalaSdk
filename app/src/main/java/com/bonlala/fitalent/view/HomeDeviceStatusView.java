package com.bonlala.fitalent.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.emu.ConnStatus;
import com.bonlala.fitalent.emu.DeviceType;
import com.hjq.shape.view.ShapeTextView;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import timber.log.Timber;

/**
 * 首页设备连接状态
 * Created by Admin
 * Date 2022/9/26
 * @author Admin
 */
public class HomeDeviceStatusView extends LinearLayout {

    /**添加设备的布局，已接连接过不显示**/
    private LinearLayout emptyLayout;
    /**连接记录的布局，展示连接状态**/
    private ConstraintLayout statusLayout;

    /**
     * 右侧设备图片
     */
    private ImageView homeDeviceTopImgView;

    /**设备名称**/
    private TextView homeDeviceNameTv;
    /**连接状态**/
    private ShapeTextView homeDeviceStatusTv;


    public HomeDeviceStatusView(Context context) {
        super(context);
        initViews(context);
    }

    public HomeDeviceStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public HomeDeviceStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }


    private void initViews(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_top_device_layout,this,true);
        emptyLayout = view.findViewById(R.id.homeAddEmptyLayout);
        statusLayout = view.findViewById(R.id.homeDeviceRecordLayout);

        homeDeviceNameTv = view.findViewById(R.id.homeDeviceNameTv);
        homeDeviceStatusTv = view.findViewById(R.id.homeDeviceStatusTv);
        homeDeviceTopImgView = view.findViewById(R.id.homeDeviceTopImgView);
    }


    /**
     * 设置右侧设备类型的图片
     */
    public void setRightImgView(int deviceType){
        int resource = 0;
        //560B
        if(deviceType == DeviceType.DEVICE_W560B){
            resource = R.mipmap.ic_home_top_device_img;
        }

        //心率带
        if(deviceType == DeviceType.DEVICE_561){
            resource = R.mipmap.ic_home_hr_blet_top_img;
        }
        homeDeviceTopImgView.setImageResource(resource);
    }


    /**设置是否连接过**/
    public void setIsConnRecord(boolean hasRecord,String bleName){
        if(emptyLayout == null || statusLayout == null)
            return;
        emptyLayout.setVisibility(hasRecord ? GONE : VISIBLE);
        statusLayout.setVisibility(hasRecord ? VISIBLE : GONE);
        if(hasRecord){
            homeDeviceNameTv.setText(bleName);
        }
        invalidate();
    }

    /**设置显示的状态**/
    public void setHomeConnStatus(ConnStatus connStatus){
        if(homeDeviceStatusTv == null)
            return;
        Timber.e("======="+connStatus);
        String status = null;
        if(connStatus == ConnStatus.CONNECTED || connStatus == ConnStatus.IS_SYNC_COMPLETE){
            status = getContext().getResources().getString(R.string.string_connected);
        }
        if(connStatus == ConnStatus.CONNECTING){
            status = getContext().getResources().getString(R.string.string_conning);
        }
        if(connStatus == ConnStatus.NOT_CONNECTED){
            status = getContext().getResources().getString(R.string.string_not_connect);
        }
        if(connStatus == ConnStatus.IS_SYNC_DATA){
            status = getContext().getResources().getString(R.string.string_data_sync);
        }

        homeDeviceStatusTv.setText(status);
        homeDeviceStatusTv.getShapeDrawableBuilder().setShadowColor(connStatus== ConnStatus.CONNECTED ? com.bonlala.base.R.color.transparent : Color.parseColor("#E8E9ED")).intoBackground();
        invalidate();
    }


    /**是否可以点击,连接状态下不可以点击**/
    public void setCanClickStatus(boolean isConnected,OnStatusViewClick onStatusViewClick){
        if(homeDeviceStatusTv == null)
            return;
        if(isConnected)
            return;
        homeDeviceStatusTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onStatusViewClick != null)
                    onStatusViewClick.onStatusClick();
            }
        });
    }


    public interface OnStatusViewClick{
        void onStatusClick();
    }
}
