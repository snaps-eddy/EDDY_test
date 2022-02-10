package com.snaps.mobile.activity.home.utils;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.component.SnapsEventView;

/**
 * Created by ysjeong on 2017. 8. 22..
 */

public class SnapsEventHandler {

    private SnapsEventView eventView;
    private boolean isCheckedkakaoEvent = false;
    private boolean isLaunchFromKakaoLink = false;

    private Activity activity = null;

    public SnapsEventHandler(Activity activity) {
        this.activity = activity;
    }

    public void initCheckKakaoEvent() {
        isCheckedkakaoEvent = false;
    }

    public void checkKakaoEvent() {
        // 카카오이벤트 데이터가 있으면 이벤트 페이지로 이동..
        if (Config.getKAKAO_EVENT_RESULT() != null && !isCheckedkakaoEvent) {
            gotoKakaoEventPage(null);
            isCheckedkakaoEvent = true;
        }
    }

    public void gotoKakaoEventPage(String optionUrl) {
        gotoKakaoEventPage(optionUrl, null);
    }

    public boolean gotoKakaoEventPage(String optionUrl, String title) {
        return SnapsMenuManager.gotoKakaoEventPage(getActivity(), optionUrl, title);
    }

    public void removeEventView() {
        if (eventView == null) return;
        ViewGroup vg = ((ViewGroup) eventView.getParent());
        if (vg != null)
            vg.removeView(eventView);
        eventView = null;
    }

    public void checkInstallEvent() {
        if (SnapsLoginManager.isLogOn(getActivity())) {
            if (Setting.getBoolean(getActivity(), Const_VALUE.KEY_SNAPS_REST_ID)) { // 휴면알럿대상 계정이라면.
                SnapsLoginManager.startLogInProcess(getActivity(), Const_VALUES.LOGIN_P_REST_ID);
                //handleInstallEvent(); //2019년 6월 18일 renewal 이후 햊당 처리는 WEB에서
                Setting.set(getActivity(), Const_VALUE.KEY_SNAPS_REST_ID, false); // 알럿대상 취소.
            } else {
                //handleInstallEvent(); // 대상이 아니면 설치 이벤트 체크로 넘어갑시돠.  //2019년 6월 18일 renewal 이후 햊당 처리는 WEB에서
            }
        }
    }

    public void initKakaoEventSenderUserId() {
        if (Const_VALUE.SENDER_USER_ID != null)
            Const_VALUE.SENDER_USER_ID = null;

    }

    private void handleInstallEvent() {
        String myEmail = Setting.getString(getActivity(), Const_VALUE.KEY_SNAPS_USER_ID);

        // 로그인 중이며 이벤트 기간이고.
        if (Setting.getBoolean(getActivity(), Const_VALUE.KEY_EVENT_TERM)) {
            // 스냅스 앱이며.
            if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_KOR)) {
                // 쿠폰이 있으면 팝업 띄움.
                if (Setting.getBoolean(getActivity(), Const_VALUE.KEY_EVENT_COUPON)) {
                    eventView = new SnapsEventView(getActivity());

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    ViewGroup rootView = (ViewGroup) ((ViewGroup) getActivity().getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
                    rootView.addView(eventView, layoutParams);
                    Setting.set(getActivity(), Const_VALUE.KEY_LAST_EVENT_ALERT_ID, myEmail); // 마지막으로 팝업을 띄운 아이디를 저장해둔다.
                }
                // 이벤트 팝업 대상이 아니지만 알럿을 띄우는 대상인지 확인.
                else if (!Setting.getString(getActivity(), Const_VALUE.KEY_LAST_EVENT_ALERT_ID, "").equals(myEmail)) {
                    MessageUtil.alert(getActivity(), R.string.event_failed_msg_already_get_coupon, null); // 알럿을 띄우고.
                    Setting.set(getActivity(), Const_VALUE.KEY_LAST_EVENT_ALERT_ID, myEmail); // 마지막으로 팝업을 띄운 아이디를 저장해둔다.
                }
            }
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isLaunchFromKakaoLink() {
        return isLaunchFromKakaoLink;
    }

    public void setLaunchFromKakaoLink(boolean launchFromKakaoLink) {
        isLaunchFromKakaoLink = launchFromKakaoLink;
    }
}
