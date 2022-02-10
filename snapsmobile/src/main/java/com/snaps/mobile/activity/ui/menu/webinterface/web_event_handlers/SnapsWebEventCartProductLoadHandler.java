package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.board.MyArtworkDetailActivity;
import com.snaps.mobile.activity.book.FacebookPhotobookFragmentActivity;
import com.snaps.mobile.activity.book.StoryBookFragmentActivity;
import com.snaps.mobile.activity.common.SmartRecommendBookMainActivity;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.diary.publish.SnapsDiaryPublishFragmentActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.SharedPreferenceRepository;
import com.snaps.mobile.activity.home.model.SnapsWebAPI;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.presentation.editor.EditorActivity;
import com.snaps.mobile.presentation.editor.EditorParams;
import com.snaps.mobile.presentation.editor.EditorViewModel;
import com.snaps.mobile.utils.network.ip.SnapsIPManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventCartProductLoadHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventCartProductLoadHandler.class.getSimpleName();

    public SnapsWebEventCartProductLoadHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    private void enableProductEditState() {
        try {
            if (activity != null && activity instanceof RenewalHomeActivity) {
                ((RenewalHomeActivity) activity).enableProductEditState();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean handleEvent() {
        if (activity == null || urlData == null) return false;

        Config.cleanProductInfo();
        String Prod_code = urlData.get(Const_EKEY.WEB_PRODCODE_KEY);

        boolean isPhotoPrintProduct = false; // 사진인화 상품여부

        // 사진인화인지 아니지 판단
        for (int i = 0; i < Config.PRODUCT_PHOTOPRINT_PRODCODE.length; i++) {
            if (Prod_code.equals(Config.PRODUCT_PHOTOPRINT_PRODCODE[i])) {
                isPhotoPrintProduct = true;
                break;
            }
        }

        enableProductEditState();

        // 장바구니의 template 은 WEB_TEMPLE_CODE 에 들어있다.
//        String mTemplateCode = urlData.get(Const_EKEY.WEB_TEMPLE_CODE);

        /**
         * KTBook 은 템플릿으로 구분되기 때문에, Simple Photobook 분기 타기전에 막아야한다.
         */
        if (Config.isKTBook(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.KT_Book);
            return true;
        }

        if (Const_PRODUCT.isCardProduct(Prod_code)) { //가끔 심플 북을 타서, 위로 올림..
//						Intent intent = new Intent(activity, CardActivity.class);
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.CARD.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            // 카드 상품갯수
            String cardCnt = urlData.get(Const_EKEY.WEB_CARD_CNT_KEY);
            if (cardCnt != null)
                intent.putExtra(Const_EKEY.WEB_CARD_CNT_KEY, urlData.get(Const_EKEY.WEB_CARD_CNT_KEY));

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } else if (isPhotoPrintProduct) {
            Intent intent = new Intent(activity, NewPhotoPrintListActivity.class);
            intent.putExtra("unitPrice", urlData.get(Const_EKEY.WEB_UNITPRICE_KEY));
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivityForResult(intent, NewPhotoPrintListActivity.REQUEST_CODE_PHOTOPRINT_CHANGE);

        } else if (Config.isSimplePhotoBook(Prod_code)) {
            Intent intent = null;
            String smartYNStr = urlData.get(Const_EKEY.WEB_SMART_YN);
            String templateCode = urlData.get(Const_EKEY.WEB_TEMPLE_CODE);
            boolean isSmartYN = (!StringUtil.isEmpty(smartYNStr) && smartYNStr.equalsIgnoreCase("Y"));// || Config.isSmartSnapsRecommendLayoutPhotoBook(templateCode);
            boolean isSmartSnapsRecommendLayoutPhotoBook = Config.isSmartSnapsRecommendLayoutPhotoBook(templateCode);

            boolean isSmartSnapsRecommendLayoutPhotoBook_2021 = false;
            if (templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL_VER_2021) ||
                    templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_BABY_VER_2021) ||
                    templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COUPLE_VER_2021) ||
                    templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_FAMILY_VER_2021) ||
                    templateCode.equals(Const_PRODUCT.PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_ETC_VER_2021)) {
                isSmartSnapsRecommendLayoutPhotoBook_2021 = true;
            }

            if (isSmartYN && isSmartSnapsRecommendLayoutPhotoBook) {
                String currentLang = Setting.getString(activity.getApplicationContext(), Const_VALUE.KEY_APPLIED_LANGUAGE, Locale.getDefault().getLanguage());
                if (!SnapsLanguageUtil.isServiceableLanguage(currentLang)) {
                    currentLang = Locale.ENGLISH.getLanguage();  //그냥 한국어!!!
                }
                Config.setTMPL_CODE(templateCode);
                intent = new Intent(activity, SmartRecommendBookMainActivity.class);
            } else if (isSmartYN && isSmartSnapsRecommendLayoutPhotoBook_2021) {
                String currentLang = Setting.getString(activity.getApplicationContext(), Const_VALUE.KEY_APPLIED_LANGUAGE, Locale.getDefault().getLanguage());
                if (!SnapsLanguageUtil.isServiceableLanguage(currentLang)) {
                    currentLang = Locale.ENGLISH.getLanguage();  //그냥 한국어!!!
                }
                SharedPreferenceRepository spManager = new SharedPreferenceRepository(activity);
                SnapsWebAPI snapsWebAPI = new SnapsWebAPI();
                String tutorialUrl = snapsWebAPI.getWebViewDomainForLanguage(spManager, false);
                tutorialUrl += "/overview/ai-photo-book";
                tutorialUrl += snapsWebAPI.getFirstPageParameters(spManager, SystemUtil.getDeviceId(activity));

                EditorParams params = new EditorParams(
                        urlData.get("prjCode"),
                        urlData.get("productCode"),
                        urlData.get("templateCode"),
                        SnapsLoginManager.getUUserNo(activity),
                        SnapsLoginManager.getUserName(activity, 15), // 왜 15인지 모르겠으나 ...
                        SystemUtil.getDeviceId(activity),
                        urlData.get("prmGlossyType"),
                        urlData.get("paperCode"),
                        urlData.get("projectCount"),
                        urlData.toString(),
                        Config.getAPP_VERSION(),
                        currentLang,
                        SnapsIPManager.getInstance().getIPAddress(),
                        SnapsIPManager.getInstance().getISP(),
                        tutorialUrl
                );
                intent = new Intent(activity, EditorActivity.class);
                intent.putExtra(EditorViewModel.PRODUCT_EDIT_PARAM, params);
                activity.startActivity(intent);
                return true;
            } else {
                intent = new Intent(activity, SnapsEditActivity.class);
                intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_PHOTO_BOOK.ordinal());
            }

            // 20141006004093
            // 00800600130001
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);

        } else if (Config.isSimpleMakingBook(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.SIMPLE_MAKING_BOOK);

        } else if (Config.isWoodBlockCalendar(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.WOODBLOCK_CALENDAR);

        } else if (Config.isCalendar(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.CALENDAR);

        } else if (Const_PRODUCT.isPhotoCardProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.PHOTO_CARD);

        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD);

        } else if (Const_PRODUCT.isNewWalletProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.WALLET_PHOTO);

        } else if (Config.isIdentifyPhotoPrint(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.IDENTIFY_PHOTO);

        } else if (Const_PRODUCT.isLegacyPhoneCaseProduct(Prod_code)) {
            // 이전에 폰케이스는 FrameProduct 소속이었기 때문에 frameProduct 체크 위에다가 선언한다.
            startActivity(SnapsProductEditConstants.eSnapsProductKind.PHONE_CASE);

        } else if (Const_PRODUCT.isFrameProduct(Prod_code) || Const_PRODUCT.isSinglePageProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.FRAME);

        } else if (Const_PRODUCT.isPackageProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.PACKAGE_KIT);

        } else if (Const_PRODUCT.isNewYearsCardProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.NEW_YEARS_CARD);

        } else if (Const_PRODUCT.isNewKakaoBook(Prod_code)) {
            Intent intent = new Intent(activity, StoryBookFragmentActivity.class);
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } else if (Const_PRODUCT.isFacebookPhotobook(Prod_code)) {
            Intent intent = new Intent(activity, FacebookPhotobookFragmentActivity.class);
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } else if (Const_PRODUCT.isSnapsDiary(Prod_code)) {
            Intent intent = new Intent(activity, SnapsDiaryPublishFragmentActivity.class);
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } else if (Const_PRODUCT.isDIYStickerProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.DIY_STICKER);

        } else if (Const_PRODUCT.isStikerGroupProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.STICKER);

        } else if (Const_PRODUCT.isAccordionCardProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.ACCORDION_CARD);

        } else if (Const_PRODUCT.isPosterGroupProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.POSTER);

        } else if (Const_PRODUCT.isSloganProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.SLOGAN);

        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.BABY_NAME_STICKER);

        } else if (Const_PRODUCT.isMiniBannerProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.MINI_BANNER);

        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD);

        } else if (Const_PRODUCT.isSmartTalkProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.SMART_TALK);

        } else if (Const_PRODUCT.isAcrylicKeyringProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.ACRYLIC_KEYRING);

        } else if (Const_PRODUCT.isAcrylicStandProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.ACRYLIC_STAND);

        } else if (Const_PRODUCT.isAirpodsCaseProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.AIRPODS_CASE);

        } else if (Const_PRODUCT.isBudsCaseProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.BUDS_CASE);

        } else if (Const_PRODUCT.isTinCaseProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.TIN_CASE);

        } else if (Const_PRODUCT.isFabricPosterProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.FABRIC_POSTER);

        } else if (Const_PRODUCT.isButtonProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.BUTTONS);

        } else if (Const_PRODUCT.isMagicalReflectiveSloganProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.MAGICAL_REFLECTIVE_SLOGAN);

        } else if (Const_PRODUCT.isReflectiveSloganProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.REFLECTIVE_SLOGAN);

        } else if (Const_PRODUCT.isHolographySloganProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.HOLOGRAPHY_SLOGAN);

        } else if (Const_PRODUCT.isUvPhoneCaseProduct(Prod_code) || Const_PRODUCT.isPrintPhoneCaseProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.NEW_PHONE_CASE);

        } else if (Const_PRODUCT.isSealStickerProduct(Prod_code)) {
            startActivity(SnapsProductEditConstants.eSnapsProductKind.SEAL_STICKER);

        } else {
            Intent detailIntent = new Intent(activity, MyArtworkDetailActivity.class);
            detailIntent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            detailIntent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            detailIntent.putExtra("saveexist", "no");
            activity.startActivity(detailIntent);
        }

        return true;
    }

    private void startActivity(SnapsProductEditConstants.eSnapsProductKind value) {
        Intent intent = new Intent(activity, SnapsEditActivity.class);
        intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, value.ordinal());
        intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
        intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }


    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }

}
