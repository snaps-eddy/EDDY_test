package com.snaps.mobile.activity.intro;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import com.snaps.mobile.R;

/**
 * Created by kimduckwon on 2017. 7. 26..
 */

public class VerifyAgreementDialog extends Dialog {

    public VerifyAgreementDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verify_agreement);
        findViewById(R.id.verify_agreement_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }
}
