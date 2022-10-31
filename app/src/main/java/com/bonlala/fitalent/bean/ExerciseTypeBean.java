package com.bonlala.fitalent.bean;

/**
 * 锻炼筛选
 * Created by Admin
 * Date 2022/10/20
 * @author Admin
 */
public class ExerciseTypeBean {

    /**名称**/
    private String typeName;

    /**是否选中**/
    private boolean isChecked;

    /**类别，对应手表的类型**/
    private int type;

    public ExerciseTypeBean() {
    }

    public ExerciseTypeBean(String typeName, int type) {
        this.typeName = typeName;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
