package com.blala.blalable;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothContext;

import org.litepal.LitePalApplication;

/**
 * Created by Admin
 * Date 2021/9/3
 */
public class BleApplication extends LitePalApplication {

    private static BleApplication bleApplication;
    private static BleManager bleManager;


    @Override
    public void onCreate() {
        super.onCreate();
        bleApplication = this;
        BluetoothContext.set(this);
    }

    public static BleApplication getInstance(){
        return bleApplication;
    }

    public BleManager getBleManager() {
        if (bleManager == null) {
            bleManager = BleManager.getInstance(this);
        }

        return bleManager;
    }
}
