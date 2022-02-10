package com.snaps.mobile.activity.home.utils;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.IBetween;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.SnsFactory;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeThirdPartyLibHandler {
    private Activity activity = null;
    private IFacebook facebook;
    private IKakao kakao;
    private IBetween between;

    public HomeThirdPartyLibHandler(Activity activity) {
        this.activity = activity;
        createThirdPartyLib();
    }

    private void createThirdPartyLib() {
        if (Config.isFacebookService()) {
            facebook = SnsFactory.getInstance().queryInteface();
            facebook.init(activity);
        }

        kakao = SnsFactory.getInstance().queryIntefaceKakao();
    }

    public IFacebook getFacebook() {
        return facebook;
    }

    public void setFacebook(IFacebook facebook) {
        this.facebook = facebook;
    }

    public IKakao getKakao() {
        return kakao;
    }

    public void setKakao(IKakao kakao) {
        this.kakao = kakao;
    }

    public IBetween getBetween() {
        return between;
    }

    public void setBetween(IBetween between) {
        this.between = between;
    }

//    public void initIgawLiveOps(LiveOpsDeepLinkEventListener liveOpsDeepLinkEventListener) {
//        if (!SnapsLoginManager.isLogOn(activity)) return;
//
//        SnapsAdbrix.initIgawLiveOps(activity, liveOpsDeepLinkEventListener);
//    }
}
