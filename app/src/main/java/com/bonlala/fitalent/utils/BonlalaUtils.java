package com.bonlala.fitalent.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;

/**
 * Created by Admin
 * Date 2022/8/4
 */
public class BonlalaUtils {


    //GPS是否打开

//    public static boolean isOpenLocation(Context context){
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            return locationManager.isLocationEnabled();
//        }
//        return locationManager.getGpsStatus()
//    }


    //判断蓝牙是否打开
    public static boolean isOpenBlue(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return false;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//
//            return false;
//        }
        return bluetoothAdapter.isEnabled();
    }

    //打开蓝牙
    public static void openBluetooth(Activity activity) {
        try {
            // 请求打开 Bluetooth
            Intent requestBluetoothOn = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
            requestBluetoothOn
                    .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            // 设置 Bluetooth 设备可见时间
            requestBluetoothOn.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    30 * 1000);
            // 请求开启 Bluetooth
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    XXPermissions.with(activity).permission(Manifest.permission.BLUETOOTH_CONNECT).request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {

                        }
                    });
                }
                return;
            }
            activity.startActivityForResult(requestBluetoothOn,
                    1001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
