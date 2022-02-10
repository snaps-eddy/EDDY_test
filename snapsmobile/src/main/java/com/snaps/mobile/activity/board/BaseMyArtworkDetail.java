package com.snaps.mobile.activity.board;

import android.os.Bundle;
import android.view.View;

import com.snaps.common.data.bitmap.PageBitmap;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;

import errorhandle.CatchFragmentActivity;

/**
 * 작품 미리보기 Activity의 base Activity
 *
 * @author crjung
 */
public class BaseMyArtworkDetail extends CatchFragmentActivity {
    // layout
    DialogDefaultProgress pageProgress;// 이미지 로딩 시 화면가운데 보여줄 로딩 Progress Dialog

    // object
    BaseMyArtworkDetail mainContext;

    String mNickName = "";
    IKakao kakao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainContext = this;
        pageProgress = new DialogDefaultProgress(mainContext);

        if (!Config.isSnapsBitween()) {
            kakao = SnsFactory.getInstance().queryIntefaceKakao();
            String errMsg = "";
            if (kakao != null) {
                if ((errMsg = kakao.createKakaoInstance(this)) != null) {
                    alert(errMsg);

                }

            }
        }
    }

    public void KakaosendData() {
        kakao.sendKakaoData(this);
    }

    public void assignMsgBuilder() {
        kakao.assignMessageBuilder();

    }

    private void alert(String message) {
        MessageUtil.alert(this, getString(R.string.app_name), message);
    }

    /**
     * 로딩 다이얼로그 Show
     */
    public void prgShow() {
        if (pageProgress != null) {
            SystemUtil.toggleScreen(getWindow(), true);
            pageProgress.show();
        }
    }

    /**
     * 로딩 다이얼로그 Hide
     */
    public void prgHide() {
        if (pageProgress != null) {
            SystemUtil.toggleScreen(getWindow(), false);
            pageProgress.dismiss();
        }
    }

    public int getSharePaddingBottom() {
        return 0;
    }

    public int getSharePaddingRight() {
        return 0;
    }

    public PageBitmap getFlipPageBitmap(int flipIdx) {
        return null;
    }

    public PageBitmap getCurlPageBitmap(int curlIdx) {
        return null;
    }

    public void onClick(View v) {
    }
}
