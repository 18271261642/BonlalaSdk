package com.bonlala.fitalent.activity.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.bean.ChartBpBean;
import com.bonlala.fitalent.view.ChartView;
import com.bonlala.fitalent.view.CusScheduleView;
import com.bonlala.fitalent.view.CustomizeSingleBpView;
import com.bonlala.widget.view.CompletedView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.angmarch.views.NiceSpinner;

/**
 * Created by Admin
 * Date 2022/9/28
 */
public class TestA extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TestAdapter adapter;

    private CustomizeSingleBpView customizeSingleBpView;


    private ChartView chartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//        }
        setContentView(R.layout.test_view_layout);


        CusScheduleView cusScheduleView = findViewById(R.id.testV);

        CusScheduleView dfuDownloadTv = findViewById(R.id.dfuDownloadTv);
        dfuDownloadTv.setShowTxt("dddd");

        cusScheduleView.setAllScheduleValue(100f);
        cusScheduleView.setCurrScheduleValue(50f);








        NiceSpinner niceSpinner = findViewById(R.id.niceSpinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        niceSpinner.attachDataSource(dataset);








        chartView = findViewById(R.id.chartView);


        Map<String,Integer> map = new HashMap<>();


        List<String> xVList = new ArrayList<>();
        List<Integer> vList = new ArrayList<>();

        Random random = new Random();

        for(int i = 0;i<10;i++){
            xVList.add("x"+i);
            int v = random.nextInt(100);
            vList.add(v);
            map.put("x"+i,v);

        }

        chartView.setValue(map,xVList,vList);


        CompletedView completedView = findViewById(R.id.testCompleteView);
        completedView.setProgress(60);
    }




    private class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

        private List<ChartBpBean> list;

        public TestAdapter(List<ChartBpBean> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TestA.this).inflate(R.layout.item_single_bp_chart_layout,parent,false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
            holder.customizeSingleBpView.setChartBpBean(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class TestViewHolder extends RecyclerView.ViewHolder{

            private CustomizeSingleBpView customizeSingleBpView;

            public TestViewHolder(@NonNull View itemView) {
                super(itemView);
                customizeSingleBpView = itemView.findViewById(R.id.itemSingleBpView);
            }
        }
    }
}
