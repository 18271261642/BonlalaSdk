package com.bonlala.fitalent.bean;

import com.bonlala.fitalent.db.model.ExerciseModel;

import java.util.List;

/**
 * Created by Admin
 * Date 2022/10/19
 * @author Admin
 */
public class ExerciseShowBean {

    private String dayStr;

    private List<ExerciseModel> exerciseModelList;

    /**是否展开**/
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getDayStr() {
        return dayStr;
    }

    public void setDayStr(String dayStr) {
        this.dayStr = dayStr;
    }

    public List<ExerciseModel> getExerciseModelList() {
        return exerciseModelList;
    }

    public void setExerciseModelList(List<ExerciseModel> exerciseModelList) {
        this.exerciseModelList = exerciseModelList;
    }
}
