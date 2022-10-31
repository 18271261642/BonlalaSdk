package com.bonlala.fitalent.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bonlala.base.BaseDialog;
import com.bonlala.fitalent.R;
import com.hjq.shape.view.ShapeTextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 项目通用 Dialog 布局封装
 */
public final class CommonDialog {

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder<?>>
            extends BaseDialog.Builder<B> {

        private boolean mAutoDismiss = true;

        private CardView parentBgLayout;

        private final ViewGroup mContainerLayout;
        private final TextView mTitleView;

        private final ShapeTextView mCancelView;
        private final View mLineView;
        private final ShapeTextView mConfirmView;

        private  LinearLayout commBtnLayout ;

        public Builder(Context context) {
            super(context);

            setContentView(R.layout.ui_dialog);


            setAnimStyle(BaseDialog.ANIM_IOS);
            setGravity(Gravity.CENTER);

            parentBgLayout = findViewById(R.id.parentBgLayout);


            mContainerLayout = findViewById(R.id.ll_ui_container);
            mTitleView = findViewById(R.id.tv_ui_title);
            mCancelView  = findViewById(R.id.tv_ui_cancel);
            mLineView = findViewById(R.id.v_ui_line);
            mConfirmView  = findViewById(R.id.tv_ui_confirm);

            commBtnLayout = findViewById(R.id.commBtnLayout);
            setOnClickListener(mCancelView, mConfirmView);
        }




        /**
         * 设置背景渐变色
         * @param isWhite 是否是白色背景，白色背景内容文字为黑色
         * @return
         */
        public B setBgAlpha(boolean isWhite){
            if(!isWhite){
                parentBgLayout.getBackground().setAlpha(90);
            }
            if(isWhite){
                mCancelView.getShapeDrawableBuilder().setStrokeColor(Color.parseColor("#F2F2F7")).setStrokeWidth(2).intoBackground();
            }else{
               // mCancelView.getShapeDrawableBuilder().setStrokeColor(Color.parseColor("#F2F2F7")).intoBackground();
            }
            mCancelView.setTextColor(isWhite ? Color.parseColor("#1C1C1E") : Color.WHITE);
            mTitleView.setTextColor(isWhite ? Color.parseColor("#1C1C1E") : Color.WHITE);
            return (B) this;
        }


        public B setCustomView(@LayoutRes int id) {
            return setCustomView(LayoutInflater.from(getContext()).inflate(id, mContainerLayout, false));
        }

        public B setCustomView(View view) {
            mContainerLayout.addView(view, 1);
            return (B) this;
        }

        public B setTitle(@StringRes int id) {
            return setTitle(getString(id));
        }
        public B setTitle(CharSequence text) {
            mTitleView.setText(text);
            return (B) this;
        }

        public B setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }
        public B setCancel(CharSequence text) {
            mCancelView.setText(text);
            mLineView.setVisibility((text == null || "".equals(text.toString())) ? View.GONE : View.VISIBLE);
            return (B) this;
        }

        public B setConfirm(@StringRes int id) {
            return setConfirm(getString(id));
        }
        public B setConfirm(CharSequence text) {
            mConfirmView.setText(text);
            return (B) this;
        }

        public B setCommBtnLayoutVisible(boolean visible){
            commBtnLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
            return (B) this;
        }

        public B setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return (B) this;
        }

        public void autoDismiss() {
            if (mAutoDismiss) {
                dismiss();
            }
        }
    }
}