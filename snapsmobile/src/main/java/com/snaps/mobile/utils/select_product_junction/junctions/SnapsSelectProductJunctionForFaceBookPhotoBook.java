package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.pref.Setting;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForFaceBookPhotoBook implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        if( !FacebookUtil.isLogin() ) {

            WebView webview = new WebView(activity);
            webview.resumeTimers();

            LoginManager.getInstance().logInWithReadPermissions( activity, FacebookUtil.PERMISSIONS_READ );
            return false;
        }
        else {
            AccessToken token = AccessToken.getCurrentAccessToken();
            Object[] temp = token.getPermissions().toArray();
            ArrayList<String> permissions = new ArrayList<String>();
            for( int i = 0; i < temp.length; ++i ) permissions.add( temp[i].toString() );

            if (!FacebookUtil.isSubsetOf(FacebookUtil.PERMISSIONS_READ, permissions)) {
                LoginManager.getInstance().logInWithReadPermissions( activity, FacebookUtil.PERMISSIONS_READ );
                return false;
            }
        }

        Setting.set(activity, "themekey", "");

        Config.setPROD_CODE(urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));

        String title = urlData.get(Const_EKEY.WEB_TITLE_KEY);

        try {
            title = URLDecoder.decode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            title = activity != null ? activity.getString(R.string.untitled) : ""; //"제목없음";
        }

        String pageCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);
        String startDate = urlData.get(Const_EKEY.WEB_START_DATE_KEY);
        String endDate = urlData.get(Const_EKEY.WEB_END_DATE_KEY);
        String commentCnt = urlData.get(Const_EKEY.WEB_COMMENT_CNT_KEY);
        String photoCnt = urlData.get(Const_EKEY.WEB_PHOTO_CNT_KEY);
        String answerCnt = urlData.get(Const_EKEY.WEB_ANSWER_CNT_KEY);
        String postCnt = urlData.get(Const_EKEY.WEB_POST_CNT_KEY);

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_FACEBOOK_PHOTOBOOK)
                .setThemeSelectTemplate(Config.getTMPL_CODE())
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setWebTitleKey(title)
                .setWebPaperCode(pageCode)
                .setWebStartDate(startDate)
                .setWebEndDate(endDate)
                .setWebCommentCount(commentCnt)
                .setWebPhotoCount(photoCnt)
                .setWebAnswerCount(answerCnt)
                .setWebPostCount(postCnt)
                .create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        activity.startActivity(intent);
        return true;
    }
}
