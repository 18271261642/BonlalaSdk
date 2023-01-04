package com.blala.blalable;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.blala.blalable.listener.BleConnStatusListener;
import com.blala.blalable.listener.ConnStatusListener;
import com.blala.blalable.listener.InterfaceManager;
import com.blala.blalable.listener.OnBleStatusBackListener;
import com.blala.blalable.listener.OnCommBackDataListener;
import com.blala.blalable.listener.OnExerciseDataListener;
import com.blala.blalable.listener.OnMeasureDataListener;
import com.blala.blalable.listener.OnRealTimeDataListener;
import com.blala.blalable.listener.OnSendWriteDataListener;
import com.blala.blalable.listener.WriteBack24HourDataListener;
import com.blala.blalable.listener.WriteBackDataListener;
import com.google.gson.Gson;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.UUID;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2021/9/3
 * @author Admin
 */
public class BleManager {

    private static final String TAG = "BleManager";


    private static final String SAVE_BLE_MAC_KEY = "blala_ble_mac";

    private static BleManager bleManager;
    private static BluetoothClient bluetoothClient;

    private static Context mContext;

    //天，0今天；1昨天，2前天
    private int dayTag ;

    private final BleConstant bleConstant = new BleConstant();


    private BleConnStatusListener bleConnStatusListener;


    private final InterfaceManager interfaceManager = new InterfaceManager();

    public void setBleConnStatusListener(BleConnStatusListener bleConnStatusListener) {
        this.bleConnStatusListener = bleConnStatusListener;
    }

    public void setOnBleBackListener (OnBleStatusBackListener onBleBackListener){
        this.interfaceManager.onBleBackListener = onBleBackListener;
    }


    public void setOnSendWriteListener(OnSendWriteDataListener onSendWriteListener){
        this.interfaceManager.onSendWriteDataListener = onSendWriteListener;
    }

    //实时心率数据
    public void setOnRealTimeDataListener(OnRealTimeDataListener onRealTimeDataListener) {
        this.interfaceManager.onRealTimeDataListener = onRealTimeDataListener;
    }


    /**测量数据返回**/
    public void setOnMeasureDataListener(OnMeasureDataListener onMeasureDataListener){
        this.interfaceManager.onMeasureDataListener = onMeasureDataListener;
    }

    /**锻炼数据**/
    public void setOnExerciseDataListener(OnExerciseDataListener onExerciseDataListener){
        this.interfaceManager.onExerciseDataListener = onExerciseDataListener;
    }

    public static BleManager getInstance(Context context){
        mContext = context;
        bluetoothClient = new BluetoothClient(mContext);
        if(bleManager == null){
            synchronized (BleManager.class){
                if(bleManager == null){
                    bleManager = new BleManager();
                }
            }
        }
        return bleManager;
    }


    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                sendCommBroadcast(-1);
            }
        }
    };




    /**
     * 搜索设备
     * @param searchResponse 回调
     * @param duration 搜索时间
     * @param times 搜索次数
     *   eg:duration=10 * 1000;times=1 表示：10s搜索一次，每次10s
     */
    public void startScanBleDevice(final SearchResponse searchResponse, int duration, int times){

        final SearchRequest searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(duration,times)
                .build();
        bluetoothClient.search(searchRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                searchResponse.onSearchStarted();
            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                searchResponse.onDeviceFounded(searchResult);
            }

            @Override
            public void onSearchStopped() {
                searchResponse.onSearchStopped();
            }

            @Override
            public void onSearchCanceled() {
                searchResponse.onSearchCanceled();
            }
        });

    }


    /**
     * 搜索设备
     * @param searchResponse 回调
     * @param duration 搜索时间
     * @param times 搜索次数
     *   eg:duration=10 * 1000;times=1 表示：10s搜索一次，每次10s
     */
    public void startScanBleDevice(final SearchResponse searchResponse, boolean scanClass,int duration, int times){

        if(!scanClass){
            startScanBleDevice(searchResponse,duration,times);
            return;
        }
        final SearchRequest searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(duration,times)
                .searchBluetoothClassicDevice(10 * 1000)
                .build();
        bluetoothClient.search(searchRequest, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                searchResponse.onSearchStarted();
            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                searchResponse.onDeviceFounded(searchResult);
            }

            @Override
            public void onSearchStopped() {
                searchResponse.onSearchStopped();
            }

            @Override
            public void onSearchCanceled() {
                searchResponse.onSearchCanceled();
            }
        });

    }



    /**
     * 停止搜索
     */
    public void stopScan(){
        if(bluetoothClient != null){
            bluetoothClient.stopSearch();
        }
    }

    //根据Mac地址连接蓝牙设备
    public void connBleDeviceByMac(String mac, String bleName, ConnStatusListener connStatusListener){
        connBleDevice(mac,bleName,connStatusListener);
    }

    //断连连接
    public void disConnDevice(){
        String spMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(spMac))
            return;
        bluetoothClient.stopSearch();
        bluetoothClient.disconnect(spMac);
        bluetoothClient.unregisterConnectStatusListener(spMac,connectStatusListener);
        BleSpUtils.remove(mContext,SAVE_BLE_MAC_KEY);

    }

    public void disConnDeviceNotRemoveMac(){
        String spMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(spMac))
            return;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(spMac);
            if (bleDevice != null) {
                unpairDevice(bleDevice);
            }
        }
        bluetoothClient.stopSearch();
        bluetoothClient.disconnect(spMac);
        bluetoothClient.unregisterConnectStatusListener(spMac,connectStatusListener);
        BleSpUtils.remove(mContext,SAVE_BLE_MAC_KEY);
    }
    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * // Constants.REQUEST_READ，所有读请求
     * // Constants.REQUEST_WRITE，所有写请求
     * // Constants.REQUEST_NOTIFY，所有通知相关的请求
     * // Constants.REQUEST_RSSI，所有读信号强度的请求
     */
    public void clearRequest(){
        String mac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(mac))
            return;
        bluetoothClient.clearRequest(mac,Constants.REQUEST_WRITE);
        bluetoothClient.clearRequest(mac,Constants.REQUEST_NOTIFY);

    }


    private synchronized void connBleDevice(final String bleMac, final String bleName, final ConnStatusListener connectResponse){
        BleSpUtils.put(mContext,SAVE_BLE_MAC_KEY,bleMac);

        int status = bluetoothClient.getConnectStatus(bleMac);

        Log.e(TAG,"************连接处="+bleMac+"--连接状态="+status);

        bluetoothClient.registerConnectStatusListener(bleMac,connectStatusListener);
        BleConnectOptions options = (new com.inuker.bluetooth.library.connect.options.BleConnectOptions.Builder()).setConnectRetry(2).setConnectTimeout(30000).setServiceDiscoverRetry(1).setServiceDiscoverTimeout(20000).build();
        bluetoothClient.connect(bleMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(final int code, final BleGattProfile data) {
                if(data == null || data.getServices() == null)
                    return;
                List<BleGattService> serviceList = data.getServices();
                Log.e(TAG,"-----onResponse="+code+"\n"+new Gson().toJson(serviceList));

                if(code == 0){  //连接成功了，开始设置通知
                    //判断是否是OTA升级状态，是OTA状态不保存地址
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                        public void run() {

                            if(bleName.toLowerCase(Locale.ROOT).contains("w561b")){
                                w561BNotifyRealData(bleMac,bleConstant.W561B_SERVER_UUID,bleConstant.W561B_REAL_HR_UUID,connectResponse);
                                return;
                            }

                            //实时数据返回，主动通道
                            notifyRealtime(bleMac,bleConstant.SERVICE_UUID,bleConstant.REAL_TIME_UUID);
                            setNotifyData(bleMac,bleConstant.SERVICE_UUID,bleConstant.READ_UUID,connectResponse);
                            setSaveNotifyData(bleMac,bleConstant.SERVICE_UUID,bleConstant.SAVE_DATA_SEND_UUID);

                        }
                    }, 2000L);
                    connectResponse.connStatus(code);
                }
            }
        });

    }


    public synchronized void clearListener(){
        if(interfaceManager.writeBack24HourDataListener != null)
            interfaceManager.writeBack24HourDataListener = null;
    }

    /**存储数据，当日详细计步，心率，锻炼数据等**/
    private synchronized void setSaveNotifyData(String mac,UUID serviceUUid,UUID notifyUid){
        bluetoothClient.notify(mac, serviceUUid, notifyUid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                Log.e(TAG,"-----存储数据返回="+uuid1.toString()+" "+Utils.formatBtArrayToString(bytes));
                if(interfaceManager.writeBack24HourDataListener != null){
                    interfaceManager.writeBack24HourDataListener.onWriteBack(bytes);
                }

                if(interfaceManager.onExerciseDataListener != null){
                    interfaceManager.onExerciseDataListener.backExerciseData(bytes);
                }

            }

            @Override
            public void onResponse(int i) {

            }
        });
    }



    public void setClearListener(){
        if(interfaceManager.writeBackDataListener != null){
            interfaceManager.writeBackDataListener = null;
        }
        if(interfaceManager.writeBack24HourDataListener != null)
            interfaceManager.writeBack24HourDataListener = null;

    }

    public void setClearExercise(){
        if(interfaceManager.onExerciseDataListener != null){
            interfaceManager.onExerciseDataListener = null;
        }
    }



    //数据发送通道返回数据，app端发送数据后设备返回数据
    private synchronized void setNotifyData(String mac,UUID serviceUUid,UUID notifyUUid,ConnStatusListener connStatusListener){
        bluetoothClient.notify(mac, serviceUUid, notifyUUid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                Log.e(TAG,"------写入数据返回="+uuid1.toString()+" "+Utils.formatBtArrayToString(bytes));
                if(interfaceManager.writeBackDataListener != null){
                    interfaceManager.writeBackDataListener.backWriteData(bytes);
                }

                //锻炼结束后手表返回 011701
                if(bytes.length == 3 && bytes[0] == 1 && bytes[1] == 23 && bytes[2] == 1){
                    sendCommBroadcast(BleConstant.BLE_COMPLETE_EXERCISE_ACTION,0);
                }

                //0151ff 测量手表结束后返回 心率

                //023cffff 血压手表结束测量



                if(bytes.length == 3 && bytes[0] ==1 && (bytes[1]& 0xff) == 0x15 && bytes[2] == 1 ){    //拍照
                    sendCommBroadcast(0x01);
                }

                if(bytes.length == 3 && bytes[0] == 1 && (bytes[1]& 0xff) == 81){   //测量心率返回
                    if(interfaceManager.onMeasureDataListener != null)
                        interfaceManager.onMeasureDataListener.onMeasureHeart(bytes[2] & 0xff,System.currentTimeMillis());
                    handler.sendEmptyMessageDelayed(0x00,1500);
                }

                if(bytes.length == 4 && (bytes[0] & 0xff) == 2 && (bytes[1] & 0xff) == 60){ //测量血压返回
                    if(interfaceManager.onMeasureDataListener != null)
                        interfaceManager.onMeasureDataListener.onMeasureBp(bytes[2] & 0xff ,bytes[3] & 0xff,System.currentTimeMillis());
                    handler.sendEmptyMessageDelayed(0x00,1500);
                }

                // 013e60 血氧
                if(bytes.length == 3 && bytes[0] == 1 && (bytes[1]& 0xff) == 62){
                    if(interfaceManager.onMeasureDataListener != null)
                        interfaceManager.onMeasureDataListener.onMeasureSpo2(bytes[2] & 0xff,System.currentTimeMillis());
                    handler.sendEmptyMessageDelayed(0x00,1500);
                }
            }

            @Override
            public void onResponse(int i) {
                connStatusListener.setNoticeStatus(i);
            }
        });
    }

    /**W561B的实时心率返回**/
    private synchronized void w561BNotifyRealData(String bleMac,UUID serverUUid,UUID uuid,ConnStatusListener connStatusListener){
        bluetoothClient.notify(bleMac, serverUUid, uuid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                Log.e(TAG,"------w61b实时心率="+Utils.formatBtArrayToString(bytes));
                if(bytes.length == 2 ){
                    //实时心率
                    int hr = bytes[1] & 0xff;
                    if(interfaceManager.onRealTimeDataListener != null){
                        interfaceManager.onRealTimeDataListener.realTimeData(hr,0,0,0);
                    }

                }
            }

            @Override
            public void onResponse(int i) {
                connStatusListener.setNoticeStatus(i);
            }
        });
    }


    /**实时数据返回**/
    private synchronized void notifyRealtime(String bleMac,UUID serUUId,UUID realUUid){
        bluetoothClient.notify(bleMac, serUUId, realUUid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID uuid, UUID uuid1, byte[] bytes) {
                if(bytes[0] == 2){ //实时的计步返回
                    //实时心率
                    int realHr = bytes[1] & 0xff;
                    //计步
                    int realStep = Utils.getIntFromBytes((byte) 0x00,bytes[6],bytes[5],bytes[4]);
                    //卡路里
                    int realKcal = Utils.getIntFromBytes((byte) 0x00,bytes[9],bytes[8],bytes[7]);
                    //距离
                    int realDis = Utils.getIntFromBytes((byte) 0x00,bytes[12],bytes[11],bytes[10]);

//                    Log.e(TAG,"-----实时数据="+realHr+" "+realStep+" "+realKcal+" "+realDis);

                    if(interfaceManager.onRealTimeDataListener != null)
                        interfaceManager.onRealTimeDataListener.realTimeData(realHr,realStep,realKcal,realDis);
                }
            }

            @Override
            public void onResponse(int i) {

            }
        });
    }



    /**读取电量**/
    public synchronized void readDeviceBatteryValue(OnCommBackDataListener onCommBackDataListener){
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        bluetoothClient.read(bleMac, bleConstant.BATTERY_SERVER_UUID, bleConstant.BATTERY_READ_UUID, new BleReadResponse() {
            @Override
            public void onResponse(int i, byte[] bytes) {
                if(bytes == null)
                    return;
                Log.e(TAG,"-------电量读取="+Utils.formatBtArrayToString(bytes));
                int batteryValue = bytes[0] & 0xff;
                if(onCommBackDataListener != null)
                    onCommBackDataListener.onIntDataBack(new int[]{batteryValue});
            }
        });
    }


    //设置通知
    private synchronized void setNotiData(String bleMac, UUID serUUID, UUID notiUUID, final ConnStatusListener connStatusListener){
        bluetoothClient.notify(bleMac, serUUID, notiUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, final byte[] value) {
                 Log.e(TAG,"---111-----设置通知="+ Arrays.toString(value));
                if(value[0] == 2 && value[1] == -1 && value[2] == 64 && value[3] == dayTag){

                }
                if(interfaceManager.writeBackDataListener != null){
                    interfaceManager.writeBackDataListener.backWriteData(value);
                }

                if(value[0] == 1 && value[1] == 19){    //音乐控制返回
                    if(interfaceManager.onBleBackListener != null)
                        interfaceManager.onBleBackListener.intoMusicStatus(value[2] & 0xff);
                }

                if(value[0] == 1 && value[1] == 20){    //查找手机
                    if(interfaceManager.onBleBackListener != null)
                        interfaceManager.onBleBackListener.findPhone();
                }

                //挂断电话
                if(value[0] == 1 && value[1] == 82 && value[2] == 2){

                }


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 1000L);

            }

            @Override
            public void onResponse(int code) {
                connStatusListener.setNoticeStatus(code);
            }
        });
    }

    //写入设备数据
    public synchronized void writeDataToDevice(byte[] data, WriteBackDataListener writeBackDataListener){
        Log.e(TAG,"-----写入数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac,bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }
    //写入设备数据
    public synchronized void writeDataToDevice(byte[] data){
        Log.e(TAG,"-----写入数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        bluetoothClient.write(bleMac,bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }




    /**写入设备数据,24小时的数据**/
    public synchronized void write24HourDataToDevice(byte[] data, WriteBack24HourDataListener writeBackDataListener){
        Log.e(TAG,"-----写入数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBack24HourDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac,bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }


    //写入设备数据
    public synchronized void writeDataToDevice(int day,byte[] data, WriteBack24HourDataListener writeBack24HourDataListener){
      //  this.dayTag = day;
        Log.e(TAG,"-----写入数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBack24HourDataListener(writeBack24HourDataListener);
        bluetoothClient.write(bleMac, bleConstant.SERVICE_UUID,bleConstant.WRITE_UUID,data,bleWriteResponse);
    }



    //写入设备数据
    public synchronized void writeExcDataToDevice(byte[] data, WriteBackDataListener writeBackDataListener){
        Log.e(TAG,"-----写入数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac, bleConstant.SERVICE_UUID,bleConstant.SAVE_DATA_SEND_UUID,data,bleWriteResponse);
    }

    public synchronized void writeWatchFaceData(byte[] data,WriteBackDataListener writeBackDataListener){
        Log.e(TAG,"-----写入表盘数据="+Arrays.toString(data));
        String bleMac = (String) BleSpUtils.get(mContext,SAVE_BLE_MAC_KEY,"");
        if(TextUtils.isEmpty(bleMac))
            return;
        interfaceManager.setWriteBackDataListener(writeBackDataListener);
        bluetoothClient.write(bleMac, bleConstant.SERVICE_UUID,bleConstant.WATCH_FACE_UUID,data,bleWriteResponse);
    }



    //监听蓝牙连接状态的监听
    private final BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {

            Log.e(TAG,"---mmmmm-连接状态manager="+mac+" "+status);
            if(mac != null && status == Constants.STATUS_DISCONNECTED){
                sendCommBroadcast(BleConstant.BLE_SOURCE_DIS_CONNECTION_ACTION);
            }
            if(bleConnStatusListener != null){
                bleConnStatusListener.onConnectStatusChanged(mac==null?"mac":mac,status);
            }
        }
    };

    private final BleWriteResponse bleWriteResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    private void sendCommBroadcast(int...value){
        Intent intent = new Intent();
        intent.setAction(BleConstant.COMM_BROADCAST_ACTION);
        intent.putExtra(BleConstant.COMM_BROADCAST_KEY,value);
        mContext.sendBroadcast(intent);
    }

    private void sendCommBroadcast(String action,int...value){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(BleConstant.COMM_BROADCAST_KEY,value);
        mContext.sendBroadcast(intent);
    }
}
