package com.bonlala.fitalent.bean;

/**
 * Created by Admin
 * Date 2022/10/12
 * @author Admin
 */
public class RecordTypeBean {
    /**
     * 类型Id
     */
    private int typeId;

    /**
     * 名称
     */
    private String typeName;

    public RecordTypeBean() {
    }

    public RecordTypeBean(int typeId) {
        this.typeId = typeId;
    }

    public RecordTypeBean(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
