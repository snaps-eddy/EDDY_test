/**
 * Copyright 2014 Kakao Corp.
 * <p>
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.snaps.kakao.utils.kakao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.Session;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.kakaostory.response.model.StoryProfile;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;

//import errorhandle.logger.Logg;

/**
 * 유효한 세션이 있다는 검증 후 me를 호출하여 가입 여부에 따라 가입 페이지를 그리던지 Main 페이지로 이동 시킨다.
 */
@SuppressLint("HandlerLeak")
public class KakaoSignupActivity extends AppCompatActivity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     *
     * @param savedInstanceState
     *            기존 session 정보가 저장된 객체
     */

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    /**
     * 자동가입앱인 경우는 가입안된 유저가 나오는 것은 에러 상황.
     */
    protected void showSignup() {
        redirectLoginActivity();
    }

    public void readProfile() {
        KakaoStoryService.getInstance().requestProfile(new KakaoStoryResponseCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse storyProfile) {
                StoryProfile profile = storyProfile.getProfile();
                final String nickName = profile.getNickName();
                final String profileImageURL = profile.getProfileImageURL();
                final String thumbnailURL = profile.getThumbnailURL();
                final String backgroundURL = profile.getBgImageURL();

                Setting.set(KakaoSignupActivity.this, Const_VALUE.KEY_KAKAO_NAME, nickName);
                Setting.set(KakaoSignupActivity.this, Const_VALUE.KEY_KAKAO_PROFILE_URL, profileImageURL);
                Setting.set(KakaoSignupActivity.this, Const_VALUE.KEY_KAKAO_COVER_URL, backgroundURL);
                // display
                finish();

                // 카카오 로그인이 완료가 되면.. 확인 broadcast 하기...
                Intent intent = new Intent(Const_VALUE.KAKAOLOING_ACTION);
                intent.putExtra("sucess", true);
                KakaoSignupActivity.this.sendBroadcast(intent);
            }
        });
    }

    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, KakaoLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

        @Override
        public void onNotKakaoStoryUser() {
            Toast.makeText(getApplicationContext(), "not a KakaoStory user", Toast.LENGTH_SHORT).show();
            Session session = Session.getCurrentSession();
            session.close();
            // display
            finish();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Toast.makeText(getApplicationContext(), "not a KakaoStory user", Toast.LENGTH_SHORT).show();
            Session session = Session.getCurrentSession();
            session.close();
            // display
            finish();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            Toast.makeText(getApplicationContext(), "not a KakaoStory user", Toast.LENGTH_SHORT).show();
            Session session = Session.getCurrentSession();
            session.close();
            // display
            finish();

        }
    }


    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    private void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                Setting.set(KakaoSignupActivity.this, Const_VALUE.KEY_KAKAO_ID, Long.toString(result.getId()));

                readProfile();
            }

        });

    }

}
