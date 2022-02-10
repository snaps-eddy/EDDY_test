package com.snaps.common.utils.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;


public class PassPortRuleDialog extends Dialog{
    public static final String PASSPORT_DIALOG_COMPLETE = "passport_dialog_complete";
    private boolean isShowDialogSelect;
    private ImageView btn_close;
    private LinearLayout linearLayoutBottom;
    private LinearLayout btn_notAgain;
    private ImageView checkBox_notAgin;
    private LinearLayout linearLayoutConfirm;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cancel();
        }
    };
    public PassPortRuleDialog(@NonNull Context context, boolean isShowDialogSelect) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.isShowDialogSelect = isShowDialogSelect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passport_rule_dialog);
        btn_close = findViewById(R.id.btn_close);
        linearLayoutBottom = findViewById(R.id.linearLayoutBottom);
        btn_notAgain = findViewById(R.id.btn_not_again);
        checkBox_notAgin = findViewById(R.id.checkBox_not_again);
        linearLayoutConfirm = findViewById(R.id.btn_confim);
        if(isShowDialogSelect) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    linearLayoutBottom.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btn_close.setVisibility(View.VISIBLE);
                }
            });

        }

        btn_close.setOnClickListener(onClickListener);
        linearLayoutConfirm.setOnClickListener(onClickListener);

        btn_notAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox_notAgin.setSelected(!checkBox_notAgin.isSelected());
                if(checkBox_notAgin.isSelected()) {
                    Setting.set(getContext(), PASSPORT_DIALOG_COMPLETE, true);
                } else {
                    Setting.set(getContext(), PASSPORT_DIALOG_COMPLETE, false);
                }
            }
        });
        setCanceledOnTouchOutside(false);
    }


}
