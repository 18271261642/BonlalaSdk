package com.bonlala.fitalent.http;

import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

/**
 * Created by Admin
 * Date 2022/3/21
 */
public class RequestServer implements IRequestServer {

    private BodyType bodyType = BodyType.FORM;

    @Override
    public String getHost() {
        return "http://192.168.1.203/test/bo/";
    }

    @Override
    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType b){
        this.bodyType = b;
    }
}
