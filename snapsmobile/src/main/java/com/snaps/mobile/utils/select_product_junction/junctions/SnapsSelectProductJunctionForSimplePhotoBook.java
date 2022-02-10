package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.home.SharedPreferenceRepository;
import com.snaps.mobile.activity.home.model.SnapsWebAPI;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.SmartSnapsTypeSelectActivity;
import com.snaps.mobile.activity.themebook.ThemeTitleActivity;
import com.snaps.mobile.presentation.editor.EditorActivity;
import com.snaps.mobile.presentation.editor.EditorParams;
import com.snaps.mobile.presentation.editor.EditorViewModel;
import com.snaps.mobile.utils.network.ip.SnapsIPManager;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.HashMap;
import java.util.Locale;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.SMART_RECOMMEND_BOOK_GLOSSY_TYPE;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForSimplePhotoBook implements ISnapsProductLauncher {
    private static final String TAG = SnapsSelectProductJunctionForSimplePhotoBook.class.getSimpleName();

    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity != null && Config.getAI_IS_SELFAI()) {
            startImageSelectActivityWithSmartSelectType(activity, attribute);
            return true;
        }

        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        // 템플릿 코드
        // 상품 코드
        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        Config.setPAPER_CODE(urlData.get("paperCode") == null ? "" : urlData.get("paperCode"));
        Config.setTMPL_COVER(urlData.get("prmcTmplCode"));

        // SnapsAuraOrderXML 에 Glossy type 설정하는 부분이 있다. 고치려면 두 곳 모두 변경해야 한다.
        if (Config.isFanBook() || Config.isKTBook()) {
            Config.setGLOSSY_TYPE(urlData.get("glossytype"));
        } else {
            Config.setGLOSSY_TYPE(SMART_RECOMMEND_BOOK_GLOSSY_TYPE);
        }

        Config.setDesignId(urlData.get("designerId") == null ? "" : urlData.get("designerId"));

        //TODO::운영 서버 테스트 코드
//        Config.setTMPL_CODE(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL_VER_2021);

        if (Config.isSmartSnapsRecommendLayoutPhotoBookVer2021(Config.getTMPL_CODE())) {
            startImageSelectActivityWithSmartSelectType2021(activity, attribute);
            return true;
        }

        if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
            startImageSelectActivityWithSmartSelectType(activity, attribute);
            return true;
        }

        if (Config.isKTBook()) {
            Intent intent = new Intent(activity, ThemeTitleActivity.class);
            intent.putExtra(ThemeTitleActivity.KEY_NEXT_LANDING_ACTION, ThemeTitleActivity.NEXT_LANDING_ACTION_KT_BOOK);
            intent.putExtra(Const_EKEY.THEME_SELECT_TEMPLE, Config.getTMPL_CODE());
            intent.putExtra(Const_EKEY.HOME_SELECT_PRODUCT_CODE, Config.getPROD_CODE());
            activity.startActivity(intent);
            return true;
        }

        // 6*6 적용
        if (Config.getTMPL_COVER() != null && Config.getTMPL_COVER().length() > 0) {
            if (Config.useKorean()) {
                Intent intent = new Intent(activity, SmartSnapsTypeSelectActivity.class);
                activity.startActivity(intent);
            } else {
                Config.setPROJ_NAME("");
                Intent intent = new Intent(activity, ImageSelectActivityV2.class);

                ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                        .setHomeSelectProduct(Config.SELECT_SIMPLEPHOTO_BOOK)
                        .setHomeSelectProductCode(Config.getPROD_CODE())
                        .setHomeSelectKind("").create();

                Bundle bundle = new Bundle();
                bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
                intent.putExtras(bundle);

                activity.startActivity(intent);
            }
        } else {
            Intent intent = new Intent(activity, ThemeTitleActivity.class);
            intent.putExtra(ThemeTitleActivity.KEY_NEXT_LANDING_ACTION, ThemeTitleActivity.NEXT_LANDING_ACTION_SIMPLE_PHOTO_BOOK);
            intent.putExtra(Const_EKEY.THEME_SELECT_TEMPLE, Config.getTMPL_CODE());
            intent.putExtra(Const_EKEY.HOME_SELECT_PRODUCT_CODE, Config.getPROD_CODE());
            activity.startActivity(intent);
        }

        return true;
    }

    //debug
    public static final String AI_PHOTO_BOOK_PREFERENCES_NAME_FOR_DEBUG = "AI_PHOTO_BOOK_PREFERENCES_NAME_FOR_DEBUG";
    public static final String AI_PHOTO_BOOK_KEY_PRODUCT_CODE_FOR_DEBUG = "AI_PHOTO_BOOK_KEY_PRODUCT_CODE_FOR_DEBUG";
    public static final String AI_PHOTO_BOOK_KEY_PAPER_CODE_FOR_DEBUG = "AI_PHOTO_BOOK_KEY_PAPER_CODE_FOR_DEBUG";
    private String getAiPhotoBookProductCode_for_debug(Activity activity) {
        SharedPreferences sp = activity.getApplicationContext()
                .getSharedPreferences(AI_PHOTO_BOOK_PREFERENCES_NAME_FOR_DEBUG, Context.MODE_PRIVATE);
        String productCode = sp.getString(AI_PHOTO_BOOK_KEY_PRODUCT_CODE_FOR_DEBUG, "");
        return productCode;
    }

    private String getAiPhotoBookPaperCode_for_debug(Activity activity) {
        SharedPreferences sp = activity.getApplicationContext()
                .getSharedPreferences(AI_PHOTO_BOOK_PREFERENCES_NAME_FOR_DEBUG, Context.MODE_PRIVATE);
        String paperCode = sp.getString(AI_PHOTO_BOOK_KEY_PAPER_CODE_FOR_DEBUG, "");
        return paperCode;
    }

    private void startImageSelectActivityWithSmartSelectType2021(Activity activity, SnapsProductAttribute attribute) {
        String currentLang = Setting.getString(activity.getApplicationContext(), Const_VALUE.KEY_APPLIED_LANGUAGE, Locale.getDefault().getLanguage());
        if (!SnapsLanguageUtil.isServiceableLanguage(currentLang)) {
            currentLang = Locale.ENGLISH.getLanguage();  //그냥 한국어!!!
        }

        HashMap<String, String> urlData = attribute.getUrlData();
        String prjCode = urlData.get("prjCode");
        String productCode = urlData.get("productCode");
        String paperCode = urlData.get("paperCode");
        String prmTmplCode = urlData.get("prmTmplCode");
        String prmGlossyType = urlData.get("prmGlossyType");
        String projectCount = urlData.get("projectCount");
        if (Config.isDevelopVersion()) {
            String productCode_setting = getAiPhotoBookProductCode_for_debug(activity);
            if (productCode_setting.length() > 0) {
                Toast.makeText(activity.getApplicationContext(), productCode_setting, Toast.LENGTH_SHORT).show();
            }
            productCode = productCode_setting.length() > 0 ? productCode_setting : productCode;

            String paperCode_setting = getAiPhotoBookPaperCode_for_debug(activity);
            paperCode = paperCode_setting.length() > 0 ? paperCode_setting : paperCode;
        }

        SharedPreferenceRepository spManager = new SharedPreferenceRepository(activity);
        SnapsWebAPI snapsWebAPI = new SnapsWebAPI();
        String tutorialUrl = snapsWebAPI.getWebViewDomainForLanguage(spManager, false);
        tutorialUrl += "/overview/ai-photo-book";
        tutorialUrl += snapsWebAPI.getFirstPageParameters(spManager, SystemUtil.getDeviceId(activity));
        EditorParams params = new EditorParams(
                prjCode,
                productCode,
                prmTmplCode,
                SnapsLoginManager.getUUserNo(activity),
                SnapsLoginManager.getUserName(activity, 15), // 왜 15인지 모르겠으나 ...
                SystemUtil.getDeviceId(activity),
                prmGlossyType,
                paperCode,
                projectCount,
                urlData.toString(),
                Config.getAPP_VERSION(),
                currentLang,
                SnapsIPManager.getInstance().getIPAddress(),
                SnapsIPManager.getInstance().getISP(),
                tutorialUrl
        );
        Intent intent = new Intent(activity, EditorActivity.class);
        intent.putExtra(EditorViewModel.PRODUCT_EDIT_PARAM, params);
        activity.startActivity(intent);
    }

    private void startImageSelectActivityWithSmartSelectType(Activity activity, SnapsProductAttribute attribute) {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.setCompleteMakeRecommendBook(false);
        smartSnapsManager.setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT);

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);
        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT)
                .setHomeSelectProduct(Config.SELECT_SMART_ANALYSIS_PHOTO_BOOK)
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setHomeSelectKind("").create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        activity.startActivity(intent);

        sendWebLog();
    }

    private void sendWebLog() {
        try {
            SnapsLogger snapsLogger = SnapsLogger.getInstance();
            String lastClickedMenuUrl = snapsLogger.getLastClickedMenuUrl();

            if (!StringUtil.isEmpty(lastClickedMenuUrl)) {
                HashMap<String, String> urlData = Config.ExtractWebURL(lastClickedMenuUrl);
                if (urlData != null) {
                    String source = urlData.get("utm_source");
                    if (!StringUtil.isEmpty(source)) {
                        if (source.contains("home")) {
                            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.home_event_annie_clickDirect));
                            return;
                        } else if (source.contains("store")) {
                            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_view_click));
                            return;
                        }
                    }
                }
            }

            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.home_event_annie_clickDirect));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
