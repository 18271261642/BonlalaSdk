package com.bonlala.fitalent.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.bonlala.fitalent.activity.ShowWebActivity;
import com.bonlala.fitalent.utils.MmkvUtils;
import com.hjq.shape.layout.ShapeLinearLayout;
import com.hjq.shape.view.ShapeButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;


/**
 * 展示隐私政策和用户协议的dialog
 * Created by Admin
 * Date 2021/10/12
 * @author Admin
 */
public class ShowPrivacyDialogView extends AppCompatDialog implements View.OnClickListener {


    private TextView privacyAndAgreementTv;
    private TextView privacyTitleTv;

    private ShapeButton privacyCancelBtn;
    private ShapeButton privacyConfirmBtn;

    private Activity activity;

    private TextView privacyDialogContentTv;

    private OnPrivacyClickListener onPrivacyClickListener;

    private ShapeLinearLayout privacyDialogLayout;

    public ShowPrivacyDialogView(Context context) {
        super(context);
    }

    public ShowPrivacyDialogView(Context context, int theme, Activity ac) {
        super(context, theme);
        this.activity = ac;
    }

    public void setOnPrivacyClickListener(OnPrivacyClickListener onPrivacyClickListener) {
        this.onPrivacyClickListener = onPrivacyClickListener;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.show_privacy_dialog_view,null);
        view.setMinimumWidth((int) (getWindow().getWindowManager()
                        .getDefaultDisplay().getWidth() * 0.8));
        setContentView(view);

        initViews();

    }

    private void initViews() {

        privacyAndAgreementTv = findViewById(R.id.privacyAndAgreementTv);
        privacyTitleTv = findViewById(R.id.privacyTitleTv);
        privacyCancelBtn = findViewById(R.id.privacyCancelBtn);
        privacyConfirmBtn = findViewById(R.id.privacyConfirmBtn);


        privacyCancelBtn.setOnClickListener(this);
        privacyConfirmBtn.setOnClickListener(this);

        String lastStr = getContext().getString(R.string.string_last_txt);
        String privacyStr = getContext().getString(R.string.user_agreement_tips);
        String andStr = getContext().getString(R.string.string_and);
        String agreeStr = getContext().getString(R.string.privacy_agreement_tips);


        String contentStr = lastStr+privacyStr+andStr+agreeStr;

        privacyTitleTv.setText(privacyStr+andStr+agreeStr);

         SpannableString spannableString = new SpannableString(contentStr);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),lastStr.length(),lastStr.length()+privacyStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),lastStr.length()+privacyStr.length()+andStr.length(),lastStr.length()+privacyStr.length()+andStr.length()+agreeStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getContext(), ShowWebActivity.class);
                intent.putExtra("url", MmkvUtils.getUserAgreement());
                intent.putExtra("title",getContext().getResources().getString(R.string.user_agreement_tips));
                getContext().startActivity(intent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        },lastStr.length(),lastStr.length()+privacyStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

                Intent intent = new Intent(getContext(), ShowWebActivity.class);
                intent.putExtra("title",getContext().getResources().getString(R.string.privacy_agreement_tips));
                intent.putExtra("url", MmkvUtils.getPrivacyUrl());
                getContext().startActivity(intent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        },lastStr.length()+privacyStr.length()+andStr.length(),lastStr.length()+privacyStr.length()+andStr.length()+agreeStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyAndAgreementTv.setText(spannableString);
        privacyAndAgreementTv.setMovementMethod(LinkMovementMethod.getInstance());

//        String str1 = getContext().getString(R.string.string_privacy_str);
//        String str2 = getContext().getString(R.string.string_privacy_2_str);
//        String agreeStr = getContext().getString(R.string.user_agreement_tips);
//        String privacyStr = getContext().getString(R.string.privacy_agreement_tips);
//
//        String contentStr = str1+agreeStr+"和"+privacyStr+str2;
//
//        SpannableString spannableString = new SpannableString(contentStr);
//        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),str1.length(),str1.length()+agreeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        spannableString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View view) {
//                startAgreementActivity();
//            }
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                ds.setUnderlineText(false);
//            }
//        },str1.length(),str1.length()+agreeStr.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),str1.length()+agreeStr.length()+"和".length(),contentStr.length()-str2.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//
//        spannableString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View view) {
//                startPrivacyActivity();
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                ds.setUnderlineText(false);
//            }
//        },str1.length()+agreeStr.length()+"和".length(),contentStr.length()-str2.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        privacyDialogContentTv.setText(spannableString);
//        privacyDialogContentTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //隐私政策
    public void startPrivacyActivity() {
        getNetUrl(true);

    }

    //用户协议
    public void startAgreementActivity() {
        getNetUrl(false);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.userAgreementTv){   //用户协议
            startAgreementActivity();
        }
        if(view.getId() == R.id.privacyTv){     //隐私政策
            startPrivacyActivity();

        }

        if(view.getId() == R.id.privacyCancelBtn){
            if(onPrivacyClickListener != null)
                onPrivacyClickListener.onCancelClick();
        }

        if(view.getId() == R.id.privacyConfirmBtn){
            if(onPrivacyClickListener != null)
                onPrivacyClickListener.onConfirmClick();
        }
    }


    public interface OnPrivacyClickListener{
        void onCancelClick();
        void onConfirmClick();
    }


    private void getNetUrl(boolean isPrivacy){
//        EasyHttp.get((LifecycleOwner) activity).api(new PrivacyBeanApi().getApi()).request(new OnHttpListener<HttpData<PrivacyBeanApi.DataBean>>() {
//
//
//            @Override
//            public void onSucceed(HttpData<PrivacyBeanApi.DataBean> result) {
//                BrowserActivity.start(getContext(),isPrivacy ? result.getData().getPrivacyAgreement() : result.getData().getUserAgreement(),isPrivacy ? "隐私政策" : "用户协议",1);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//
//            }
//        });
    }
}
