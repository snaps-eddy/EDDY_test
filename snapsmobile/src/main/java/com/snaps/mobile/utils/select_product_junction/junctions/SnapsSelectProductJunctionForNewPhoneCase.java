package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.utils.select_product_junction.PhoneCaseSkinUrlGenerator;
import com.snaps.mobile.utils.select_product_junction.SkinType;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * 2020.08.18 @Marko
 * 기존과 다른 포맷의 템플릿을 사용하는 폰케이스
 */
public class SnapsSelectProductJunctionForNewPhoneCase implements ISnapsProductLauncher {

    public static final String TAG = SnapsSelectProductJunctionForNewPhoneCase.class.getSimpleName();

    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        Config.setFRAME_ID(urlData.get(Const_EKEY.WEB_FRAME_KEY));

        Config.setNOTE_PAPER_CODE(urlData.get("papertype") == null ? "" : urlData.get("papertype"));
        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));
        Config.setPAPER_CODE(urlData.get(Const_EKEY.WEB_PAPER_CODE));
        if (Config.getPAPER_CODE() == null || Config.getPAPER_CODE().length() < 1) {
            Config.setPAPER_CODE(Config.getNOTE_PAPER_CODE());
        }

        // @Marko 하드나 범퍼 케이스인 경우 강제로 backtype 과 frametype을 urldata 에서 삭제한다. 편집기에 가까운 코드일 수록 Const_PRODUCT 를 이용한 분기를 없애는게 좋아보인다.
        // "frametype" 은 글리터 케이스인 경우 투명인지 블랙인지 사용자가 선택한 값이다. 같은 uv 라도 글리터 케이스에만 frametype 이 들어있다.
        if (Const_PRODUCT.isUvPhoneCaseProduct()) {
            String frameType = urlData.get("frametype");
            Config.setFRAME_TYPE(frameType == null ? "" : frameType);
        } else {
            urlData.remove("backType");
            urlData.remove("frametype"); // 소문자
        }

        Intent intent = new Intent(activity, SnapsEditActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.NEW_PHONE_CASE.ordinal());

        intent.putExtras(bundle);
        intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP, urlData);

        activity.startActivity(intent);
        return true;
    }

}
