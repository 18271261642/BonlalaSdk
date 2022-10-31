package com.bonlala.fitalent.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.blala.blalable.UserInfoBean;
import com.bonlala.fitalent.R;


import androidx.appcompat.app.AppCompatDialog;

/**
 * Created by Admin
 * Date 2021/9/7
 */
public class SyncUserInfoDialog extends AppCompatDialog implements View.OnClickListener {

    private EditText yearEdit,monthEdit,dayEdit;
    private EditText weightEdit,heightEdit;
    private EditText maxHeartEdit,minHeartEdit;

    private RadioGroup radioGroup;
    private RadioButton manRB,womenRB;

    private Button cancelBtn,confirmBtn;

    private int sex;


    private OnUserSyncListener onUserSyncListener;

    public void setOnUserSyncListener(OnUserSyncListener onUserSyncListener) {
        this.onUserSyncListener = onUserSyncListener;
    }

    public SyncUserInfoDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_user_info_layout);

        initViews();
    }


    private void initViews(){
        yearEdit = findViewById(R.id.syncUserInfoYearEdit);
        monthEdit = findViewById(R.id.syncUserInfoMonthEdit);
        dayEdit = findViewById(R.id.syncUserInfoDayEdit);
        weightEdit = findViewById(R.id.weightEdit);
        heightEdit = findViewById(R.id.heightEdit);
        maxHeartEdit = findViewById(R.id.maxHeartEdit);
        minHeartEdit = findViewById(R.id.minHeartEdit);

        womenRB = findViewById(R.id.sexWomenRB);
        manRB = findViewById(R.id.sexManRB);

        cancelBtn = findViewById(R.id.syncCancelBtn);
        confirmBtn = findViewById(R.id.syncConfirmBtn);

        radioGroup = findViewById(R.id.sexRG);


        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.sexManRB){
                    sex = manRB.isChecked() ? 1 : 0;
                }

                if(i == R.id.sexWomenRB){
                    sex = womenRB.isChecked() ? 0 :1;
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.syncCancelBtn){
            cancel();
        }

        if(view.getId() == R.id.syncConfirmBtn){
            String yearStr = yearEdit.getText().toString();
            String monthStr = monthEdit.getText().toString();
            String dayStr = dayEdit.getText().toString();
            String heightStr = heightEdit.getText().toString();
            String weightStr = weightEdit.getText().toString();
            String maxHeartStr = maxHeartEdit.getText().toString();
            String minHeartStr = minHeartEdit.getText().toString();

            if(TextUtils.isEmpty(yearStr) || TextUtils.isEmpty(monthStr) || TextUtils.isEmpty(dayStr)
                    || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(maxHeartStr) || TextUtils.isEmpty(minHeartStr))
                return;

            UserInfoBean userInfoBean = new UserInfoBean(Integer.valueOf(yearStr),Integer.valueOf(monthStr),Integer.valueOf(dayStr),Integer.valueOf(weightStr),
                    Integer.valueOf(heightStr),sex,Integer.valueOf(maxHeartStr),Integer.valueOf(minHeartStr));
            if(onUserSyncListener != null)
                onUserSyncListener.onUserInfoData(userInfoBean);
            cancel();
        }
    }


    public interface OnUserSyncListener{
        void onUserInfoData(UserInfoBean userInfoBean);
    }
}
