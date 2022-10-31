package com.bonlala.fitalent.activity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.blala.blalable.BleConstant;
import com.blala.blalable.BleOperateManager;
import com.blala.blalable.UserInfoBean;
import com.blala.blalable.Utils;
import com.blala.blalable.listener.BleConnStatusListener;
import com.blala.blalable.listener.ConnStatusListener;
import com.blala.blalable.listener.OnSendWriteDataListener;
import com.blala.blalable.listener.WriteBackDataListener;
import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.dialog.SyncUserInfoDialog;
import com.bonlala.fitalent.utils.MusicManager;
import com.google.gson.Gson;
import com.inuker.bluetooth.library.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/**
 * Created by Admin
 * Date 2021/9/3
 */
public class OperateActivity extends AppCompatActivity {

    private static final String TAG = "OperateActivity";

    private String currMac;
    private BleOperateManager bleOperateManager;

    private final BleConstant bleConstant = new BleConstant();

    private SwitchCompat takePhotoSwitch;
    //发送的指令
    private TextView showSendTv;
    //连接状态
    private TextView showStatus;
    //返回的指令
    private TextView mcuStatus;

    private EditText inputMsgEdit;

    private EditText exerciseEdit;
    //转腕亮屏
    private SwitchCompat wristLightSwitch,heartSwitch,dndSwitch;
    private SwitchCompat measureHeartSwitch,measureBloodSwitch,measureSpo2Switch;

    //同步用户信息
    private SyncUserInfoDialog userInfoView;

    private Vibrator vibrator;

    private AudioManager audioManager;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mMediaPlayer.stop();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_layout);

        initViews();

        bleOperateManager = BaseApplication.getInstance().getBleOperate();
        bleOperateManager.setOnOperateSendListener(onSendWriteDataListener);
        currMac = getIntent().getStringExtra("bleMac");

        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver,intentFilter);
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                Log.e(TAG,"------蓝牙开关状态="+state);
            }
        }
    };


    private void initViews(){

        exerciseEdit = findViewById(R.id.exerciseEdit);
        showSendTv = findViewById(R.id.showSendTv);
        showStatus = findViewById(R.id.showConnStatus);
        mcuStatus = findViewById(R.id.showMCUStatusTv);
        takePhotoSwitch = findViewById(R.id.takePhotoSwitch);
        wristLightSwitch = findViewById(R.id.wristLightSwitch);
        heartSwitch = findViewById(R.id.heartSwitch);
        dndSwitch = findViewById(R.id.dndSwitch);
        measureHeartSwitch = findViewById(R.id.measureHeartSwitch);
        measureBloodSwitch = findViewById(R.id.measureBloodSwitch);
        measureSpo2Switch = findViewById(R.id.measureSpo2Switch);
        inputMsgEdit = findViewById(R.id.inputMsgEdit);


        takePhotoSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        wristLightSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        heartSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        dndSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        measureHeartSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        measureBloodSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        measureSpo2Switch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    //连接设备
    public void connDevice(View view){

        if(currMac == null)
            return;
        bleOperateManager.setBleConnStatusListener(bleConnStatusListener);
        bleOperateManager.connYakDevice("b", currMac, new ConnStatusListener() {
            @Override
            public void connStatus(int status) {

            }

            @Override
            public void setNoticeStatus(int code) {

            }
        });

    }


    private MediaPlayer mMediaPlayer;
    private void startAlarm(Context context) {
        mMediaPlayer = MediaPlayer.create(context, getSystemDefultRingtoneUri(context));
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        handler.sendEmptyMessageDelayed(0x00,5 * 1000);
    }

    //获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri(Context context) {
        return RingtoneManager.getActualDefaultRingtoneUri(context,
                RingtoneManager.TYPE_RINGTONE);
    }


    /**
     * 01 播放
     * 02 暂停
     * 03 上一首 (需要推送1F命令)
     * 04 下一首 (需要推送1F命令)
     * 05 增加音量
     * 06 减少音量
     * 07 开启音乐协议 (需要推送1F命令)
     * 08 关闭音乐协议
     */
    private void sendMusicData(int sendMusicData){
        if(sendMusicData == 1){ //播放
            MusicManager.playMusic(audioManager,this);

        }
        if(sendMusicData == 2){ //暂停
            MusicManager.pauseMusic(audioManager,this);
        }

        if(sendMusicData == 3){ //上一首
            MusicManager.previousMusic(audioManager,this);
        }

        if(sendMusicData == 4){  //下一首
            MusicManager.nextMusic(audioManager,this);
        }

        if(sendMusicData == 5 || sendMusicData == 6){
            MusicManager.setVoiceStatus(audioManager,sendMusicData == 5  ? 0x01 : 2);
        }

    }

    //查找手机，手机震动
    private void findPhoneStatus(){
        if(vibrator == null)
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(3 * 1000);
    }

    //断开连接
    public void disConnDevice(View view) {

    }

    //查找手表
    public void findWatch(View view) {
        setSendStr("查找手表:"+Arrays.toString(bleConstant.findDevice()));
        bleOperateManager.findDevice(writeBackDataListener);
    }

    //获取版本信息
    public void getDeviceVersionInfo(View view) {
        setSendStr("获取版本信息:"+Arrays.toString(bleConstant.getDeviceVersion()));
        bleOperateManager.getDeviceVersionData(writeBackDataListener);
    }


    //获取电量
    public void getDeviceBattery(View view) {
        setSendStr("获取电量: "+Arrays.toString(bleConstant.getDeviceBattery()));
        bleOperateManager.getDeviceBattery(writeBackDataListener);
    }


    
    private final WriteBackDataListener writeBackDataListener = new WriteBackDataListener() {
        @Override
        public void backWriteData(byte[] data) {
            Log.e(TAG,"---back="+ Arrays.toString(data));
            setBackStr("写入数据返回:"+Arrays.toString(data));
        }
    };

    private final BleConnStatusListener bleConnStatusListener = new BleConnStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
//            if(status == Constants.STATUS_CONNECTED){
//                bleOperateManager.setIntoTestModel(writeBackDataListener);
//            }
            showStatus.setText((status == Constants.STATUS_CONNECTED ? "已连接:":"连接断开")+" mac="+mac);

        }
    };



    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            int id = compoundButton.getId();

            if(compoundButton.getId() == R.id.takePhotoSwitch){ //进入拍照
                bleOperateManager.setIntoTakePhotoStatus(writeBackDataListener);
            }

            if(compoundButton.getId() == R.id.wristLightSwitch){    //转腕亮屏
                bleOperateManager.setWristData(b,8,0,22,0,writeBackDataListener);
            }

            if(compoundButton.getId() == R.id.wristLightSwitch){    //心率开关
                bleOperateManager.setHeartStatus(b,writeBackDataListener);
            }

            if(compoundButton.getId() ==R.id.dndSwitch){    //勿扰模式
                bleOperateManager.setDNTStatus(b,11,0,12,0,writeBackDataListener);
            }

            if(id == R.id.measureHeartSwitch){  //测量心率
                bleOperateManager.measureHeartStatus(b,writeBackDataListener);
            }

            if(id == R.id.measureSpo2Switch){   //血氧
                bleOperateManager.measureSo2Status(b,writeBackDataListener);
            }

            if(id == R.id.measureBloodSwitch){  //血压
                bleOperateManager.measureBloodStatus(b,writeBackDataListener);
            }
        }
    };


    private void setSendStr(String str){
        showSendTv.setText(str);
    }


    private void setBackStr(String str){
        mcuStatus.setText(str);
    }

    //同步时间
    public void syncDeviceTime(View view) {

        bleOperateManager.syncDeviceTime(writeBackDataListener);
    }
    //获取时间
    public void getDeviceTime(View view) {
        setSendStr("获取时间:"+Arrays.toString(bleConstant.getCurrTime()));
        bleOperateManager.getDeviceTime(writeBackDataListener);
    }


    //同步用户信息
    public void syncUserInfoData(View view) {
        if(userInfoView == null)
            userInfoView = new SyncUserInfoDialog(this);
        userInfoView.show();
        userInfoView.setCanceledOnTouchOutside(false);
        userInfoView.setOnUserSyncListener(new SyncUserInfoDialog.OnUserSyncListener() {
            @Override
            public void onUserInfoData(UserInfoBean userInfoBean) {
                userInfoView.dismiss();
                bleOperateManager.setUserInfoData(userInfoBean.getYear(),userInfoBean.getMonth(),userInfoBean.getDay(),userInfoBean.getWeight(),userInfoBean.getHeight(),userInfoBean.getSex(),userInfoBean.getMaxHeart(),userInfoBean.getMinHeart(),writeBackDataListener);
            }
        });
    }
    //获取用户信息
    public void getUerInfoData(View view) {
        bleOperateManager.getUserInfoData(writeBackDataListener);
    }

    //消息推送
    public void sendMsgTypeData(View view) {
        try {
            //        byte[] tmpByte = new byte[]{01, 10, 01 ,00 ,31, 33, 00 ,00 ,00, 00, 00 ,00, 00 ,00 ,00 ,00 ,00, 00 ,00, 00 };
//        bleOperateManager.writeCommonByte(tmpByte, new WriteBackDataListener() {
//            @Override
//            public void backWriteData(byte[] data) {
//                Log.e(TAG,"-----写入消息="+Arrays.toString(data));
//            }
//        });

            String inputType =inputMsgEdit.getText().toString();
            if(TextUtils.isEmpty(inputType))
                return;

            bleOperateManager.sendAPPNoticeMessage(Integer.parseInt(inputType),"title","content内容",writeBackDataListener);

            return;

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void readDeviceAlarmData(View view) {


    }

    //设置久坐提醒
    public void setLongSitData(View view) {
        bleOperateManager.setLongSitData(8,0,30,22,0,writeBackDataListener);
    }

    //读取久坐提醒
    public void readLongSitData(View view) {
        setSendStr("读取久坐提醒:"+Arrays.toString(bleConstant.getLongSitData()));
        bleOperateManager.getLongSitData(writeBackDataListener);
    }
    //读取心率开关状态
    public void readHeartSwitchStatus(View view) {
        bleOperateManager.getHeartStatus(writeBackDataListener);
    }

    //读取转腕亮屏
    public void readSwitchLightStatus(View view) {
        setSendStr("转腕亮屏："+Arrays.toString(bleConstant.getWristData()));
        bleOperateManager.getWristData(writeBackDataListener);
    }

    //读取勿扰模式
    public void readDNTStatus(View view) {
        setSendStr("读取勿扰模式："+Arrays.toString(bleConstant.getDNTStatus()));
        bleOperateManager.getDNTStatus(writeBackDataListener);
    }

    //获取通用设置
    public void getCommonSetting(View view) {
        bleOperateManager.getCommonSetting(writeBackDataListener);
    }

    //设置通用
    public void setCommonSetting(View view) {
        bleOperateManager.setCommonSetting(true,true,true,false,writeBackDataListener);
    }

    //内置表盘
    public void readSetThemeData(View view) {
        bleOperateManager.getLocalDial(new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //获取背光
    public void setBackLightData(View view) {
        bleOperateManager.getBackLight(writeBackDataListener);
    }

    //设置背光
    public void getBackLightData(View view) {
        bleOperateManager.setBackLight(3,8,writeBackDataListener);
    }

    private List<byte[]> bytList = new ArrayList<>();

    //获取当天数据
    public void getCurrDayData(View view) {
        bytList.clear();

        stringBuilder.delete(0,stringBuilder.length());
//        bleOperateManager.getDayForData(1, new WriteBack24HourDataListener() {
//            @Override
//            public void onWriteBack(byte[] data) {
//
//            }
//
//
//        });
    }



    private void analysisSleepData(List<byte[]> bytList) {


        byte[] bytesFirst= bytList.get(0);

        byte[] data = new byte[(bytList.size() - 1) * 19];//减去第一包剩下的数据集合
        for (int i = 1; i < bytList.size(); i++) {
            byte[] tmp = bytList.get(i);
            if (tmp != null) {
                System.arraycopy(tmp, 1, data, (i - 1) * 19, 19);
            }
        }

        int year = Utils.getIntFromBytes(bytesFirst[6],bytesFirst[5]);
        int non = Utils.byte2Int(bytesFirst[7]);
        int day = Utils.byte2Int(bytesFirst[8]);
        Calendar instance = Calendar.getInstance();


        Log.e(TAG,"year = " + year + "non = " + non + "day = " + day );

        // && day == instance.get(Calendar.DAY_OF_MONTH)
        if (year == instance.get(Calendar.YEAR) && non == instance.get
                (Calendar.MONTH) + 1) {
            //当天数据
            //通过获取当前时间来判断包数，12:32  (12*60+32)/19   39.57  40包数据
            int hourOfDay = instance.get(Calendar.HOUR_OF_DAY);//当前的小时
            int minuteOfDay = instance.get(Calendar.MINUTE);//当前分钟
            float floatF = (hourOfDay * 60 + minuteOfDay) / (float) 19;
            int intI = (hourOfDay * 60 + minuteOfDay) / 19;
            Log.e(TAG, "-----睡眠日期=" + year + "-" + non + "-" + day + " " + hourOfDay + ":" + minuteOfDay + " " + floatF + " " + intI);

            int pakNum;
            if ((floatF - intI) > 0) {
                pakNum = intI + 1;
            } else {
                pakNum = intI;
            }
            if (data.length >= pakNum * 19) {
              Log.e(TAG,"pakNum == " + pakNum * 19 + "当天数据未丢包 data.length == " + data.length);
            } else {

               Log.e(TAG,"pakNum == " + pakNum * 19 + "当天数据丢包 data.length == " + data.length);
            }


            List<Integer> stepList = new ArrayList<>();
            List<Integer> sleepList = new ArrayList<>();

            for(int i = 0;i<data.length;i+=2){
                byte byte0 = data[i];
                //当天可能出现1197的情况
                byte byte1;
                if (i + 1 >= data.length) {
                    byte1 = 1;
                } else {
                    byte1 = data[i + 1];
                }
                int int0 = Utils.byteToInt(byte0);
                int int1 = Utils.byteToInt(byte1);
                if (int0 == 255 || int0 == 254) {
                    int0 = 0;
                    //结尾包了
                    Log.e(TAG,"结尾包了 == " + i);
                    //break;
                }


                if (int0 >= 250 && int0 <= 253) {
                    //为睡眠数据
                    if (int0 == 250) {
                        //深睡
//                                Logger.myLog("深睡");
                    } else if (int0 == 251) {
                        //浅睡 level 2
//                                Logger.myLog("浅睡 level 2");

                    } else if (int0 == 252) {
                        //浅睡 level 1
//                                Logger.myLog("浅睡 level 1");

                    } else if (int0 == 253) {
                        //清醒
//                                Logger.myLog("清醒");

                    }
                    stepList.add(0);
                    sleepList.add(int0);
                } else {
                    //步数数据
//                            Logger.myLog("步数数据 == " + int0);
                    if (stepList.size() < 1440) {
                        stepList.add(int0);
                        sleepList.add(0);
                    }

                }

            }


            //睡眠从0点到9点
            int allSleepCountMinute = 9 * 60 ;

            List<Integer> resultSleep = sleepList.subList(0,allSleepCountMinute);

            Log.e(TAG,"-----结果睡眠="+resultSleep.size()+" "+new Gson().toJson(resultSleep));

            Log.e(TAG,"-------睡眠数据="+"长度="+sleepList.size()+" "+new Gson().toJson(sleepList));


        }else{      //非当天数据

        }

    }




    private final StringBuilder stringBuilder = new StringBuilder();
    //获取汇总数据
    public void getCountStepData(View view) {
        stringBuilder.delete(0,stringBuilder.length());
        bleOperateManager.getCountDayData(0, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {


            }
        });
    }

    //使手表关机
    public void powerOffDevice(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("选择操作")
                .setItems(new String[]{"手表关机", "清除数据"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        bleOperateManager.setDevicePowerOrRecycler(i+1,writeBackDataListener);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();


    }


    private final OnSendWriteDataListener onSendWriteDataListener = new OnSendWriteDataListener() {
        @Override
        public void sendWriteData(byte[] data) {
            setSendStr("发送指令:"+Arrays.toString(data));
        }
    };

    public void sendWatchFace(View view) {
//        bleOperateManager.sendIndexBack(1, new OnWatchFaceVerifyListener() {
//            @Override
//            public void isVerify(boolean isSuccess, int position) {
//
//            }
//        });
       // startActivity(new Intent(OperateActivity.this,CusWatchFaceActivity.class));
    }

    //手动打开通知
    public void openPhoneNotice(View view) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivityForResult(intent, 1001);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //发送天气
    public void sendWeatherData(View view) {
        bleOperateManager.sendWeatherData("深圳",writeBackDataListener);
    }


    //获取锻炼数据
    public void getDeviceExercise(View view) {
        String exerciseIndex = exerciseEdit.getText().toString();
        if(TextUtils.isEmpty(exerciseIndex))
            return;
        int num = Integer.parseInt(exerciseIndex.trim());
        bleOperateManager.getExerciseData(num,new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
               // Log.e(TAG,"-----锻炼数据="+Arrays.toString(data));
            }
        });
    }

    //清除锻炼数据
    public void clearDeviceExercise(View view) {
        bleOperateManager.clearAllDeviceExerciseData(writeBackDataListener);
    }
}
