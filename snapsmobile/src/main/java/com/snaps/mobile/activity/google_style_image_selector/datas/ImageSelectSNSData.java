package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.content.Context;

import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.instagram.utils.instagram.InstagramApp;

/**
 * Created by ysjeong on 2016. 11. 30..
 */

public class ImageSelectSNSData {
    private IFacebook facebook = null;
    private InstagramApp instagram = null;
    private IKakao kakao = null;

    public ImageSelectSNSData(Context context) {
        init(context);
    }

    private void init(Context context) {}

    public void clear() {
        this.facebook = null;
        this.instagram = null;
        this.kakao = null;
    }

    public IKakao getKakao() {
        return kakao;
    }

    public void setKakao(IKakao kakao) {
        this.kakao = kakao;
    }

    public IFacebook getFacebook() {
        return facebook;
    }

    public ImageSelectSNSData setFacebook(IFacebook facebook) {
        this.facebook = facebook;
        return this;
    }

    public InstagramApp getInstagram() {
        return instagram;
    }

    public ImageSelectSNSData setInstagram(InstagramApp instagram) {
        this.instagram = instagram;
        return this;
    }
}
