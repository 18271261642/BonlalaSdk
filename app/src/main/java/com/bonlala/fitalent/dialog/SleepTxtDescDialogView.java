package com.bonlala.fitalent.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bonlala.fitalent.R;
import com.hjq.shape.view.ShapeTextView;

import androidx.appcompat.app.AppCompatDialog;

/**
 * Created by Admin
 * Date 2022/10/9
 * @author Admin
 */
public class SleepTxtDescDialogView extends AppCompatDialog {

    private ShapeTextView okBtn;
    private TextView dialogDescTv;

    public SleepTxtDescDialogView(Context context) {
        super(context);
    }

    public SleepTxtDescDialogView(Context context, int theme) {
        super(context, theme);
    }

    protected SleepTxtDescDialogView(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sleep_desc_layout);
//        Window window = getWindow();
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        window.setGravity(Gravity.CENTER_VERTICAL);
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.gravity = Gravity.CENTER;
//        window.setAttributes(layoutParams);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialogDescTv = findViewById(R.id.dialogDescTv);

        okBtn = findViewById(R.id.sleepDescOkTv);
        assert okBtn != null;
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setDesc(String desc){
        if(dialogDescTv == null)
            return;
        dialogDescTv.setText(desc);
    }
}
