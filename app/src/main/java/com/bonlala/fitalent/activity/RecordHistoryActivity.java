package com.bonlala.fitalent.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bonlala.action.AppActivity;
import com.bonlala.fitalent.R;
import com.bonlala.fitalent.activity.history.HistoryBpFragment;
import com.bonlala.fitalent.activity.history.HistoryHeartFragment;
import com.bonlala.fitalent.activity.history.HistorySleepFragment;
import com.bonlala.fitalent.activity.history.HistorySpo2Fragment;
import com.bonlala.fitalent.activity.history.HistoryStepFragment;
import com.bonlala.fitalent.adapter.HistoryTypeSpinnerAdapter;
import com.bonlala.fitalent.bean.RecordTypeBean;
import com.bonlala.fitalent.emu.HomeDateType;
import com.bonlala.fitalent.listeners.OnRecordHistoryRightListener;
import com.bonlala.fitalent.view.LinearProgressView;
import com.hjq.shape.view.ShapeTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

/**
 * Created by Admin
 * Date 2022/9/27
 * @author Admin
 */
public class RecordHistoryActivity extends AppActivity implements View.OnClickListener {




    //测量
    private ShapeTextView recordMeasureTv;
    //历史
    private ShapeTextView recordHistoryTv;


    private OnRecordHistoryRightListener onRecordHistoryRightListener;

    public void setOnRecordHistoryRightListener(OnRecordHistoryRightListener onRecordHistoryRightListener) {
        this.onRecordHistoryRightListener = onRecordHistoryRightListener;
    }

    private FragmentManager fragmentManager;
    //返回
    private ImageView historyBackImg;
    //右侧的按钮
    private ImageView recordHistoryRightImg;

    /**计步目标的布局，非计步隐藏**/
    private LinearLayout recordStepGoalLayout;
    /**非计步布局**/
    private LinearLayout recordMeasureLayout;


    /**计步目标进度条**/
    private LinearProgressView stepGoalProgressView;

    private AppCompatSpinner spinner;
    private HistoryTypeSpinnerAdapter spinnerAdapter;
    private List<RecordTypeBean> typeList;


    //传递过来的类型计步、睡眠等
    private String dataType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {

        return R.layout.activity_record_history_layout;
    }

    @Override
    protected void initView() {
        historyBackImg = findViewById(R.id.historyBackImg);
        spinner = findViewById(R.id.historySpinner);

        recordMeasureLayout = findViewById(R.id.recordMeasureLayout);
        recordStepGoalLayout = findViewById(R.id.recordStepGoalLayout);
        recordHistoryRightImg = findViewById(R.id.recordHistoryRightImg);
        recordHistoryTv = findViewById(R.id.recordHistoryTv);
        recordMeasureTv = findViewById(R.id.recordMeasureTv);

        stepGoalProgressView = findViewById(R.id.stepGoalProgressView);

        setOnClickListener(historyBackImg,recordHistoryRightImg,recordMeasureTv,recordHistoryTv);

        typeList = new ArrayList<>();
        typeList.add(new RecordTypeBean(HomeDateType.HOME_TYPE_STEP,getResources().getString(R.string.string_steps)));
        typeList.add(new RecordTypeBean(HomeDateType.HOME_TYPE_SLEEP,getResources().getString(R.string.string_sleep)));
        typeList.add(new RecordTypeBean(HomeDateType.HOME_TYPE_DETAIL_HR,getResources().getString(R.string.string_heart)));
        typeList.add(new RecordTypeBean(HomeDateType.HOME_TYPE_BP,getResources().getString(R.string.string_bp)));
        typeList.add(new RecordTypeBean(HomeDateType.HOME_TYPE_SPO2,getResources().getString(R.string.string_spo2)));
        spinnerAdapter = new HistoryTypeSpinnerAdapter(this,typeList);

        spinner.setAdapter(spinnerAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RecordTypeBean recordTypeBean = typeList.get(i);
                chooseFragmentItem(recordTypeBean);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int typeId = getInt("home_type");
        Timber.e("-----typeId="+typeId);
        RecordTypeBean rb = new RecordTypeBean(typeId);
        chooseFragmentItem(rb);
        int selectPosition = 0;
        for(int i = 0;i<typeList.size();i++){
            if(typeId == typeList.get(i).getTypeId()){
                selectPosition = i;
                break;
            }
        }
        spinner.setSelection(selectPosition);

    }

    @Override
    protected void initData() {



    }



    private void chooseFragmentItem(RecordTypeBean recordTypeBean){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //计步
        if(recordTypeBean.getTypeId() == HomeDateType.HOME_TYPE_STEP){
            recordMeasureLayout.setVisibility(View.GONE);
            recordStepGoalLayout.setVisibility(View.VISIBLE);

            fragmentTransaction.replace(R.id.recordFragment, new HistoryStepFragment());
            fragmentTransaction.commitAllowingStateLoss();
        }

        //睡眠
        if(recordTypeBean.getTypeId() == HomeDateType.HOME_TYPE_SLEEP){
            recordMeasureLayout.setVisibility(View.VISIBLE);
            recordStepGoalLayout.setVisibility(View.GONE);
            recordMeasureTv.setVisibility(View.GONE);
            recordHistoryTv.setVisibility(View.GONE);

            fragmentTransaction.replace(R.id.recordFragment, new HistorySleepFragment());
            fragmentTransaction.commitAllowingStateLoss();
        }

        //心率
        if(recordTypeBean.getTypeId() == HomeDateType.HOME_TYPE_DETAIL_HR){
            recordMeasureLayout.setVisibility(View.VISIBLE);
            recordStepGoalLayout.setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.recordFragment, new HistoryHeartFragment());
            fragmentTransaction.commitAllowingStateLoss();
            recordMeasureTv.setVisibility(View.VISIBLE);
            recordHistoryTv.setVisibility(View.VISIBLE);

        }
        //血压
        if(recordTypeBean.getTypeId() == HomeDateType.HOME_TYPE_BP){
            recordMeasureLayout.setVisibility(View.VISIBLE);
            recordStepGoalLayout.setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.recordFragment, new HistoryBpFragment());
            fragmentTransaction.commitAllowingStateLoss();
            recordMeasureTv.setVisibility(View.VISIBLE);
            recordHistoryTv.setVisibility(View.GONE);
        }

        //血氧
        if(recordTypeBean.getTypeId() == HomeDateType.HOME_TYPE_SPO2){
            recordMeasureLayout.setVisibility(View.VISIBLE);
            recordStepGoalLayout.setVisibility(View.GONE);
            recordMeasureTv.setVisibility(View.VISIBLE);
            recordHistoryTv.setVisibility(View.GONE);
            fragmentTransaction.replace(R.id.recordFragment, new HistorySpo2Fragment());
            fragmentTransaction.commitAllowingStateLoss();
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == historyBackImg){
            finish();
        }

        if(view == recordHistoryRightImg){
            if(onRecordHistoryRightListener != null)
                onRecordHistoryRightListener.onRightImgClick();
        }

        //测量
        if(view == recordMeasureTv){
            if(onRecordHistoryRightListener != null)
                onRecordHistoryRightListener.onMeasureClick();
        }

        //历史
        if(view == recordHistoryTv){
            if(onRecordHistoryRightListener != null)
                onRecordHistoryRightListener.onHistoryClick();
        }
    }


    /**设置计步进度条**/
    public void setStepSchedule(float value,float goal){
        stepGoalProgressView.setCurrentProgressValue(value,goal);
    }
}
