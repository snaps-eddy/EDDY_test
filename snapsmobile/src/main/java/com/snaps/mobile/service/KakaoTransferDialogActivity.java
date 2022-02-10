package com.snaps.mobile.service;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

public class KakaoTransferDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIUtil.applyLanguage(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kakao_transfer_dialog_inner);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                // 키잠금 해제하기
                // 화면 켜기
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        RelativeLayout backGround = (RelativeLayout) findViewById(R.id.push_inner_background);
        backGround.setBackgroundColor(Color.argb(100, 0, 0, 0));

        ImageView kakaoClose = (ImageView) findViewById(R.id.kakao_close);
        kakaoClose.setOnClickListener(v -> finish());

        TextView kakaoText = (TextView) findViewById(R.id.kakao_text);

    }

}
