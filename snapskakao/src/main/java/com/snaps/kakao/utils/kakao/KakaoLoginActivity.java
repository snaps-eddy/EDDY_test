package com.snaps.kakao.utils.kakao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.snaps.kakao.R;
import com.snaps.kakao.utils.kakao.custom.SnapsKakaoLoginButton;

public class KakaoLoginActivity extends AppCompatActivity {
    private SnapsKakaoLoginButton loginButton;
    private MySessionStatusCallback mySessionCallback = null;

    private Bitmap m_bmPhone = null;
    private BitmapDrawable m_drawablePhone = null;

    /**
     * super.onCreate를 호출하여 Session처리를 맡긴다.
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_login);
        // 로그인 버튼에 로그인 결과를 받을 콜백을 설정한다.
        mySessionCallback = new MySessionStatusCallback();
        Session.getCurrentSession().addCallback(mySessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
        ImageView backBtn = (ImageView) findViewById(R.id.kakao_login_back_iv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mySessionCallback);
    }

    protected void onResume() {
        super.onResume();

        Session s = Session.getCurrentSession();
        if (s == null) {
            s = Session.getCurrentSession();
        }
        if (s != null) {

            s.addCallback(mySessionCallback);
        } else if (s.isOpened()) {
            onSessionOpened();
        }
    }

    public static boolean isKakaoLogin() {
        Session session = Session.getCurrentSession();
        return session != null && session.isOpened();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MySessionStatusCallback implements ISessionCallback {
        /**
         * 세션이 오픈되었으면 가입페이지로 이동 한다.
         */
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            KakaoLoginActivity.this.onSessionOpened();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            //loginButton.setVisibility(View.VISIBLE);
        }
    }

    protected void onSessionOpened() {
        final Intent intent = new Intent(KakaoLoginActivity.this, KakaoSignupActivity.class);
        startActivity(intent);
        finish();
    }
}
