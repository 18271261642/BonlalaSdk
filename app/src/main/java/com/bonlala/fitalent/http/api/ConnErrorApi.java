package com.bonlala.fitalent.http.api;

import com.bonlala.fitalent.emu.ConnStatus;
import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/11/9
 * @author Admin
 */
public class ConnErrorApi implements IRequestApi{
    @NonNull
    @Override
    public String getApi() {
        return "api/app/deviceGuide/connectFailGuide/"+deviceType;
    }

    @HttpIgnore
    private String deviceType;

    public ConnErrorApi setType(String type){
        this.deviceType = type;
        return this;
    }


}
