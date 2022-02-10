package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.instagram.utils.instagram.Const;
import com.snaps.instagram.utils.instagram.InstagramApp;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.board.MyArtworkDetailActivity;
import com.snaps.mobile.activity.book.FacebookPhotobookFragmentActivity;
import com.snaps.mobile.activity.book.StoryBookFragmentActivity;
import com.snaps.mobile.activity.common.SmartRecommendBookMainActivity;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.diary.publish.SnapsDiaryPublishFragmentActivity;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.photoprint.NewPhotoPrintListActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventPreviewHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventPreviewHandler.class.getSimpleName();

    public SnapsWebEventPreviewHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        String Prod_code = prodKey;

        boolean isPhotoPrintProduct = false; // 사진인화 상품여부

        // 사진인화인지 아니지 판단
        for (int i = 0; i < Config.PRODUCT_PHOTOPRINT_PRODCODE.length; i++) {
            if (Prod_code.equals(Config.PRODUCT_PHOTOPRINT_PRODCODE[i])) {
                isPhotoPrintProduct = true;
                break;
            }
        }

        if (Config.isSimplePhotoBook(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_PHOTO_BOOK.ordinal());
            // 20140720001977
            // 00800600100006
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, prodKey);

            activity.startActivity(intent);
        } else if (Config.isSimpleMakingBook(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_MAKING_BOOK.ordinal());
            // 20140720001977
            // 00800600100006
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, prodKey);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isFrameProduct(Prod_code) || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct()) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
            // 20140720001977
            // 00800600100006
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, prodKey);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isNewKakaoBook(Prod_code)) {
            Intent intent = new Intent(activity, StoryBookFragmentActivity.class);
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, prodKey);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isFacebookPhotobook(Prod_code)) {
            selectFacebookPhotobook();
        } else if (Const_PRODUCT.isSnapsDiary(Prod_code)) {
            selectSnapsDiary();
        } else if (Const_PRODUCT.isCardProduct(Prod_code)) { //가끔 심플 북을 타서, 위로 올림..
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
            boolean isSmartYN = (!StringUtil.isEmpty(smartYNStr) && smartYNStr.equalsIgnoreCase("Y")) || Config.isSmartSnapsRecommendLayoutPhotoBook(templateCode);
            if (isSmartYN) {
                Config.setTMPL_CODE(templateCode);
                intent = new Intent(activity, SmartRecommendBookMainActivity.class);
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
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_MAKING_BOOK.ordinal());
            // 20141006004093
            // 00800600130001
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);

        } else if (Config.isWoodBlockCalendar(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.WOODBLOCK_CALENDAR.ordinal());
            // 20140720001977
            // 00800600100006
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } else if (Config.isCalendar(Prod_code)) {
//						Intent intent = new Intent(activity, CalendarActivity.class);
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.CALENDAR.ordinal());
            // 20140720001977
            // 00800600100006
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } else if (Const_PRODUCT.isPhotoCardProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.PHOTO_CARD.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isNewWalletProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.WALLET_PHOTO.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);
        } else if (Config.isIdentifyPhotoPrint(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.IDENTIFY_PHOTO.ordinal());

            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isFrameProduct(Prod_code) || Const_PRODUCT.isSinglePageProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
            // 20140720001977
            // 00800600100006
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);
        } else if (Const_PRODUCT.isPackageProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.PACKAGE_KIT.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            activity.startActivity(intent);

        } else if (Const_PRODUCT.isNewYearsCardProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.NEW_YEARS_CARD.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
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
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.DIY_STICKER.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isStikerGroupProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.STICKER.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isAccordionCardProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.ACCORDION_CARD.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isPosterGroupProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.POSTER.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isSloganProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SLOGAN.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.BABY_NAME_STICKER.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isMiniBannerProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.MINI_BANNER.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.TRANSPARENCY_PHOTO_CARD.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isSmartTalkProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SMART_TALK.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else if (Const_PRODUCT.isSealStickerProduct(Prod_code)) {
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SEAL_STICKER.ordinal());
            intent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            intent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        } else {
            Intent detailIntent = new Intent(activity, MyArtworkDetailActivity.class);
            detailIntent.putExtra(Const_EKEY.MYART_PROJCODE, urlData.get(Const_EKEY.WEB_PROJCODE_KEY));
            detailIntent.putExtra(Const_EKEY.MYART_PRODCODE, urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            detailIntent.putExtra("saveexist", "no");
            activity.startActivity(detailIntent);
        }
        WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }

    private boolean selectSnapsDiary() {
        Setting.set(activity, "themekey", "");

        Config.setPROD_CODE(urlData.get(Const_EKEY.WEB_PRODCODE_KEY));
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));

        String title = urlData.get(Const_EKEY.WEB_TITLE_KEY);

        try {
            title = URLDecoder.decode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            title = activity.getString(R.string.untitled);
        } catch (NullPointerException e) {
            title = activity.getString(R.string.untitled);
        }

        String pageCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);
        String startDate = urlData.get(Const_EKEY.WEB_START_DATE_KEY);
        String endDate = urlData.get(Const_EKEY.WEB_END_DATE_KEY);

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SNAPS_REMOVE_DIARY)
                .setThemeSelectTemplate(Config.getTMPL_CODE())
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setWebTitleKey(title)
                .setWebPaperCode(pageCode)
                .setWebStartDate(startDate)
                .setWebEndDate(endDate)
                .create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        activity.startActivity(intent);

        return true;
    }

    private boolean selectFacebookPhotobook() {
        if (!FacebookUtil.isLogin()) {

            WebView webview = new WebView(activity);
            webview.resumeTimers();

            LoginManager.getInstance().logInWithReadPermissions(activity, FacebookUtil.PERMISSIONS_READ);
            return false;
        } else {
            AccessToken token = AccessToken.getCurrentAccessToken();
            Object[] temp = token.getPermissions().toArray();
            ArrayList<String> permissions = new ArrayList<String>();
            for (int i = 0; i < temp.length; ++i) permissions.add(temp[i].toString());

            if (!FacebookUtil.isSubsetOf(FacebookUtil.PERMISSIONS_READ, permissions)) {
                LoginManager.getInstance().logInWithReadPermissions(activity, FacebookUtil.PERMISSIONS_READ);
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
