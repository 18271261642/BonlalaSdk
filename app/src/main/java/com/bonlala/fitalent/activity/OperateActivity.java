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
    //???????????????
    private TextView showSendTv;
    //????????????
    private TextView showStatus;
    //???????????????
    private TextView mcuStatus;

    private EditText inputMsgEdit;

    private EditText exerciseEdit;
    //????????????
    private SwitchCompat wristLightSwitch,heartSwitch,dndSwitch;
    private SwitchCompat measureHeartSwitch,measureBloodSwitch,measureSpo2Switch;

    //??????????????????
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
                Log.e(TAG,"------??????????????????="+state);
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

    //????????????
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

    //???????????????????????????Uri
    private Uri getSystemDefultRingtoneUri(Context context) {
        return RingtoneManager.getActualDefaultRingtoneUri(context,
                RingtoneManager.TYPE_RINGTONE);
    }


    /**
     * 01 ??????
     * 02 ??????
     * 03 ????????? (????????????1F??????)
     * 04 ????????? (????????????1F??????)
     * 05 ????????????
     * 06 ????????????
     * 07 ?????????????????? (????????????1F??????)
     * 08 ??????????????????
     */
    private void sendMusicData(int sendMusicData){
        if(sendMusicData == 1){ //??????
            MusicManager.playMusic(audioManager,this);

        }
        if(sendMusicData == 2){ //??????
            MusicManager.pauseMusic(audioManager,this);
        }

        if(sendMusicData == 3){ //?????????
            MusicManager.previousMusic(audioManager,this);
        }

        if(sendMusicData == 4){  //?????????
            MusicManager.nextMusic(audioManager,this);
        }

        if(sendMusicData == 5 || sendMusicData == 6){
            MusicManager.setVoiceStatus(audioManager,sendMusicData == 5  ? 0x01 : 2);
        }

    }

    //???????????????????????????
    private void findPhoneStatus(){
        if(vibrator == null)
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(3 * 1000);
    }

    //????????????
    public void disConnDevice(View view) {

    }

    //????????????
    public void findWatch(View view) {
        setSendStr("????????????:"+Arrays.toString(bleConstant.findDevice()));
        bleOperateManager.findDevice(writeBackDataListener);
    }

    //??????????????????
    public void getDeviceVersionInfo(View view) {
        setSendStr("??????????????????:"+Arrays.toString(bleConstant.getDeviceVersion()));
        bleOperateManager.getDeviceVersionData(writeBackDataListener);
    }


    //????????????
    public void getDeviceBattery(View view) {
        setSendStr("????????????: "+Arrays.toString(bleConstant.getDeviceBattery()));
        bleOperateManager.getDeviceBattery(writeBackDataListener);
    }


    
    private final WriteBackDataListener writeBackDataListener = new WriteBackDataListener() {
        @Override
        public void backWriteData(byte[] data) {
            Log.e(TAG,"---back="+ Arrays.toString(data));
            setBackStr("??????????????????:"+Arrays.toString(data));
        }
    };

    private final BleConnStatusListener bleConnStatusListener = new BleConnStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
//            if(status == Constants.STATUS_CONNECTED){
//                bleOperateManager.setIntoTestModel(writeBackDataListener);
//            }
            showStatus.setText((status == Constants.STATUS_CONNECTED ? "?????????:":"????????????")+" mac="+mac);

        }
    };



    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            int id = compoundButton.getId();

            if(compoundButton.getId() == R.id.takePhotoSwitch){ //????????????
                bleOperateManager.setIntoTakePhotoStatus(writeBackDataListener);
            }

            if(compoundButton.getId() == R.id.wristLightSwitch){    //????????????
                bleOperateManager.setWristData(b,8,0,22,0,writeBackDataListener);
            }

            if(compoundButton.getId() == R.id.wristLightSwitch){    //????????????
                bleOperateManager.setHeartStatus(b,writeBackDataListener);
            }

            if(compoundButton.getId() ==R.id.dndSwitch){    //????????????
                bleOperateManager.setDNTStatus(b,11,0,12,0,writeBackDataListener);
            }

            if(id == R.id.measureHeartSwitch){  //????????????
                bleOperateManager.measureHeartStatus(b,writeBackDataListener);
            }

            if(id == R.id.measureSpo2Switch){   //??????
                bleOperateManager.measureSo2Status(b,writeBackDataListener);
            }

            if(id == R.id.measureBloodSwitch){  //??????
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

    //????????????
    public void syncDeviceTime(View view) {

        bleOperateManager.syncDeviceTime(writeBackDataListener);
    }
    //????????????
    public void getDeviceTime(View view) {
        setSendStr("????????????:"+Arrays.toString(bleConstant.getCurrTime()));
        bleOperateManager.getDeviceTime(writeBackDataListener);
    }


    //??????????????????
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
    //??????????????????
    public void getUerInfoData(View view) {
        bleOperateManager.getUserInfoData(writeBackDataListener);
    }

    //????????????
    public void sendMsgTypeData(View view) {
        try {
            //        byte[] tmpByte = new byte[]{01, 10, 01 ,00 ,31, 33, 00 ,00 ,00, 00, 00 ,00, 00 ,00 ,00 ,00 ,00, 00 ,00, 00 };
//        bleOperateManager.writeCommonByte(tmpByte, new WriteBackDataListener() {
//            @Override
//            public void backWriteData(byte[] data) {
//                Log.e(TAG,"-----????????????="+Arrays.toString(data));
//            }
//        });

            String inputType =inputMsgEdit.getText().toString();
            if(TextUtils.isEmpty(inputType))
                return;

            bleOperateManager.sendAPPNoticeMessage(Integer.parseInt(inputType),"title","content??????",writeBackDataListener);

            return;

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void readDeviceAlarmData(View view) {


    }

    //??????????????????
    public void setLongSitData(View view) {
        bleOperateManager.setLongSitData(8,0,30,22,0,writeBackDataListener);
    }

    //??????????????????
    public void readLongSitData(View view) {
        setSendStr("??????????????????:"+Arrays.toString(bleConstant.getLongSitData()));
        bleOperateManager.getLongSitData(writeBackDataListener);
    }
    //????????????????????????
    public void readHeartSwitchStatus(View view) {
        bleOperateManager.getHeartStatus(writeBackDataListener);
    }

    //??????????????????
    public void readSwitchLightStatus(View view) {
        setSendStr("???????????????"+Arrays.toString(bleConstant.getWristData()));
        bleOperateManager.getWristData(writeBackDataListener);
    }

    //??????????????????
    public void readDNTStatus(View view) {
        setSendStr("?????????????????????"+Arrays.toString(bleConstant.getDNTStatus()));
        bleOperateManager.getDNTStatus(writeBackDataListener);
    }

    //??????????????????
    public void getCommonSetting(View view) {
        bleOperateManager.getCommonSetting(writeBackDataListener);
    }

    //????????????
    public void setCommonSetting(View view) {
        bleOperateManager.setCommonSetting(true,true,true,false,writeBackDataListener);
    }

    //????????????
    public void readSetThemeData(View view) {
        bleOperateManager.getLocalDial(new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }

    //????????????
    public void setBackLightData(View view) {
        bleOperateManager.getBackLight(writeBackDataListener);
    }

    //????????????
    public void getBackLightData(View view) {
        bleOperateManager.setBackLight(3,8,writeBackDataListener);
    }

    private List<byte[]> bytList = new ArrayList<>();

    //??????????????????
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

        byte[] data = new byte[(bytList.size() - 1) * 19];//????????????????????????????????????
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
            //????????????
            //??????????????????????????????????????????12:32  (12*60+32)/19   39.57  40?????????
            int hourOfDay = instance.get(Calendar.HOUR_OF_DAY);//???????????????
            int minuteOfDay = instance.get(Calendar.MINUTE);//????????????
            float floatF = (hourOfDay * 60 + minuteOfDay) / (float) 19;
            int intI = (hourOfDay * 60 + minuteOfDay) / 19;
            Log.e(TAG, "-----????????????=" + year + "-" + non + "-" + day + " " + hourOfDay + ":" + minuteOfDay + " " + floatF + " " + intI);

            int pakNum;
            if ((floatF - intI) > 0) {
                pakNum = intI + 1;
            } else {
                pakNum = intI;
            }
            if (data.length >= pakNum * 19) {
              Log.e(TAG,"pakNum == " + pakNum * 19 + "????????????????????? data.length == " + data.length);
            } else {

               Log.e(TAG,"pakNum == " + pakNum * 19 + "?????????????????? data.length == " + data.length);
            }


            List<Integer> stepList = new ArrayList<>();
            List<Integer> sleepList = new ArrayList<>();

            for(int i = 0;i<data.length;i+=2){
                byte byte0 = data[i];
                //??????????????????1197?????????
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
                    //????????????
                    Log.e(TAG,"???????????? == " + i);
                    //break;
                }


                if (int0 >= 250 && int0 <= 253) {
                    //???????????????
                    if (int0 == 250) {
                        //??????
//                                Logger.myLog("??????");
                    } else if (int0 == 251) {
                        //?????? level 2
//                                Logger.myLog("?????? level 2");

                    } else if (int0 == 252) {
                        //?????? level 1
//                                Logger.myLog("?????? level 1");

                    } else if (int0 == 253) {
                        //??????
//                                Logger.myLog("??????");

                    }
                    stepList.add(0);
                    sleepList.add(int0);
                } else {
                    //????????????
//                            Logger.myLog("???????????? == " + int0);
                    if (stepList.size() < 1440) {
                        stepList.add(int0);
                        sleepList.add(0);
                    }

                }

            }


            //?????????0??????9???
            int allSleepCountMinute = 9 * 60 ;

            List<Integer> resultSleep = sleepList.subList(0,allSleepCountMinute);

            Log.e(TAG,"-----????????????="+resultSleep.size()+" "+new Gson().toJson(resultSleep));

            Log.e(TAG,"-------????????????="+"??????="+sleepList.size()+" "+new Gson().toJson(sleepList));


        }else{      //???????????????

        }

    }




    private final StringBuilder stringBuilder = new StringBuilder();
    //??????????????????
    public void getCountStepData(View view) {
        stringBuilder.delete(0,stringBuilder.length());
        bleOperateManager.getCountDayData(0, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {


            }
        });
    }

    //???????????????
    public void powerOffDevice(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("????????????")
                .setItems(new String[]{"????????????", "????????????"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        bleOperateManager.setDevicePowerOrRecycler(i+1,writeBackDataListener);
                    }
                }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
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
            setSendStr("????????????:"+Arrays.toString(data));
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

    //??????????????????
    public void openPhoneNotice(View view) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivityForResult(intent, 1001);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //????????????
    public void sendWeatherData(View view) {
        bleOperateManager.sendWeatherData("??????",writeBackDataListener);
    }


    //??????????????????
    public void getDeviceExercise(View view) {
        String exerciseIndex = exerciseEdit.getText().toString();
        if(TextUtils.isEmpty(exerciseIndex))
            return;
        int num = Integer.parseInt(exerciseIndex.trim());
        bleOperateManager.getExerciseData(num,new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
               // Log.e(TAG,"-----????????????="+Arrays.toString(data));
            }
        });
    }

    //??????????????????
    public void clearDeviceExercise(View view) {
        bleOperateManager.clearAllDeviceExerciseData(writeBackDataListener);
    }
}
