package com.bonlala.fitalent.service;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

/**
 *
 * @author Administrator
 * @date 2016/11/1
 */

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return DfuNotiActvity.class;
    }



}
