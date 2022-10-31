package com.bonlala.fitalent.http.api;

import com.hjq.http.config.IRequestApi;

import androidx.annotation.NonNull;

/**
 * 意见反馈
 * Created by Admin
 * Date 2022/10/21
 * @author Admin
 */
public class FeedbackApi implements IRequestApi {
    @NonNull
    @Override
    public String getApi() {
        return "api/app/common/feedback";
    }
}
