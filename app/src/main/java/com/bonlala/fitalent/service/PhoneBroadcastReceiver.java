package com.bonlala.fitalent.service;



import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.blala.blalable.BleOperateManager;
import com.blala.blalable.listener.WriteBackDataListener;
import com.bonlala.fitalent.BaseApplication;
import com.bonlala.fitalent.utils.BikeUtils;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by admin on 2017/5/14.\
 * 6.0 广播接收来电
 */

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneBroadcastReceiver";
    private static final String H9_NAME_TAG = "H9";    //H9手表


    String phoneNumber = "";
    private String bleName;


    public PhoneBroadcastReceiver() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null)
            return;
        //未连接设备的状态

        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            Log.e(TAG, "---------action---" + action);
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (BikeUtils.isEmpty(phoneNumber))
            return;
        Log.d(TAG, "---phoneNumber----" + phoneNumber);
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (telephony == null)
            return;
        int state = telephony.getCallState();
        Log.d(TAG, "-----state-----" + state);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://呼入电话，未接听状态
                verticalDeviceType(context);
                break;
            case TelephonyManager.CALL_STATE_IDLE:// 电话挂断
                Log.d(TAG, "------挂断电话--");
            case TelephonyManager.CALL_STATE_OFFHOOK://已接通
                Log.d(TAG, "------通话中--");
                verticalDeviceCancelPhone();
                break;
        }
    }


    //取消来电状态
    private void verticalDeviceCancelPhone() {

    }

    //来电呼入状态
    private void verticalDeviceType(Context context){
        try {
            if (!BikeUtils.isEmpty(phoneNumber) && !BikeUtils.isEmpty(bleName)) {
                Log.d(TAG, "------收到了来电广播---" + phoneNumber);
                //H8
                findPhoneContactsByNumber(phoneNumber,"bb");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public interface OnCallPhoneListener {
        void callPhoneAlert(String phoneTag);
    }

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    boolean isPhone = true;


    /**根据手机号码查询通讯录的联系人姓名**/
    private void findPhoneContactsByNumber(String phoneName,String tag){
        Log.e(TAG,"-----phoneName="+phoneName+"--tag="+tag);
        try {
            ContentResolver resolver = BaseApplication.getInstance().getContentResolver();
            if(resolver == null){
                sendCommVerticalPhone(tag,"",phoneName);
                return;
            }
            // 获取手机联系人
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
            if(phoneCursor == null){
                sendCommVerticalPhone(tag,"",phoneName);
                return;
            }
            while (phoneCursor.moveToNext()) {
                //手机通讯录中的电话号码
                String conNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if(!BikeUtils.isEmpty(conNumber)){
                    if (conNumber.contains("-")) {  //去除“-”
                        conNumber = conNumber.replace("-", "");
                    }
                    if (conNumber.contains(" ")) { //去除空格
                        conNumber = conNumber.replace(" ", "");
                    }
                    if(phoneName.equals(conNumber)){    //呼入的号码和通讯录中的号码相同
                        isPhone = false;
                        //联系人姓名
                        String contactNames = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX) + "";
                        if(BikeUtils.isEmpty(contactNames)) contactNames = "";
                        //退出来电提醒，联系人姓名和电话
                        sendCommVerticalPhone(tag,contactNames,phoneName);
                        return;
                    }

                }

            }
             if (isPhone) {
                sendCommVerticalPhone(tag, "", phoneName);
            }

            phoneCursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //判断设备，发送数据
    private void sendCommVerticalPhone(String tagName,String contentStr,String phoneStr){
        Log.e(TAG,"---name="+tagName+"---=联系人="+contentStr+"---phoneNumber="+phoneStr);

        if(tagName.equals("B30")){
            setB30PhoneMsg(contentStr,phoneStr);
            return;
        }

        BleOperateManager.getInstance().sendAPPNoticeMessage(1, phoneStr, contentStr, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {

            }
        });
    }


    //发送来电指令
    private void setB30PhoneMsg(String peopleName,String resultNumber) {


    }

    /**
     * B30电话挂断
     */
    private void setB30DisPhone() {

    }


}