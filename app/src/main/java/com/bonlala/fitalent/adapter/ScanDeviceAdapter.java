package com.bonlala.fitalent.adapter;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.BleBean;
import com.bonlala.fitalent.listeners.OnItemClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2019/10/30
 */
public class ScanDeviceAdapter extends RecyclerView.Adapter<ScanDeviceAdapter.ScanDeviceViewHolder> {


    private Context mContext;
    private List<BleBean> deviceList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ScanDeviceAdapter(Context mContext, List<BleBean> deviceList) {
        this.mContext = mContext;
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public ScanDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_scan_device, viewGroup, false);
        return new ScanDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ScanDeviceViewHolder scanDeviceViewHolder, int i) {

        BluetoothDevice bluetoothDevice = deviceList.get(i).getBluetoothDevice();

//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
        scanDeviceViewHolder.nameTv.setText(bluetoothDevice.getName());
        scanDeviceViewHolder.macTv.setText(bluetoothDevice.getAddress());
        scanDeviceViewHolder.rssi.setText(deviceList.get(i).getRssi()+"");
        scanDeviceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    int position = scanDeviceViewHolder.getLayoutPosition();
                    onItemClickListener.onIteClick(position);
                }
            }
        });
    }
	

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class ScanDeviceViewHolder extends RecyclerView.ViewHolder{

        TextView nameTv,macTv,rssi;

        public ScanDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.itemBleNameTv);
            macTv = itemView.findViewById(R.id.itemBleMacTv);
            rssi = itemView.findViewById(R.id.itemRssiTv);
        }
    }




}
