package com.snaps.mobile.activity.google_style_image_selector.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.performs.ImageSelectPerformForKTBook;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.MenuData;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ysjeong on 2016. 11. 25..
 */

public class ImageSelectUtils {
    private static final String TAG = ImageSelectUtils.class.getSimpleName();

    public interface IImageSelectUtilsInterfaceCallback {
        void onPrepare();

        void onResult(boolean result);
    }

    public static void initPhotoLastSelectedHistory() {
        initLastSelectedAlbumId();

        initLastSelectedPhotoSourceKind();
    }

    private static void initLastSelectedAlbumId() {
        Context context = ContextUtil.getContext();
        if (context == null) return;
        Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_LAST_SELECTED_ALBUM_ID, "");
    }

    public static void initLastSelectedPhotoSourceKind() {
        Context context = ContextUtil.getContext();
        if (context == null) return;
        Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_LAST_SELECTED_PHOTO_SOURCE_KIND, 0);
    }

    public static void saveLastSelectedPhoneAlbumId(String albumId) {
        Context context = ContextUtil.getContext();
        if (context == null) return;
        Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_LAST_SELECTED_ALBUM_ID, albumId);
    }

    public static int loadLastSelectedPhoneAlbumIndexFromAlbumList(ArrayList<IAlbumData> cursors) {
        if (cursors == null || cursors.isEmpty()) return -1;

        Context context = ContextUtil.getContext();
        if (context == null) return 0;

        String lastSelectedAlbumId = Setting.getString(context, ISnapsImageSelectConstants.SETTING_KEY_LAST_SELECTED_ALBUM_ID);
        if (StringUtil.isEmpty(lastSelectedAlbumId)) return 0;

        for (int i = 0; i < cursors.size(); i++) {
            IAlbumData albumData = cursors.get(i);
            String albumId = albumData.getAlbumId();
            if (!StringUtil.isEmpty(albumId) && albumId.equals(lastSelectedAlbumId)) {
                return i;
            }
        }

        return 0;
    }

    public static void saveLastSelectedPhotoSourceOrdinal(@NonNull ISnapsImageSelectConstants.ePhotoSourceType sourceType) {
        Context context = ContextUtil.getContext();
        if (context == null) return;
        Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_LAST_SELECTED_PHOTO_SOURCE_KIND, sourceType.ordinal());
    }

    public static ISnapsImageSelectConstants.ePhotoSourceType loadLastSelectedPhotoSourceOrdinal() {
        Context context = ContextUtil.getContext();
        int ordinal = context != null ? Setting.getInt(context, ISnapsImageSelectConstants.SETTING_KEY_LAST_SELECTED_PHOTO_SOURCE_KIND) : 0;
        ISnapsImageSelectConstants.ePhotoSourceType[] sources = ISnapsImageSelectConstants.ePhotoSourceType.values();
        return ordinal >= 0 && ordinal < sources.length ? sources[ordinal] : ISnapsImageSelectConstants.ePhotoSourceType.NONE;
    }

    //화면 밖에 나가는 Rect는 사이즈를 구할 수가 없으니, 처음 측정된 사이즈를 공유한다.
//    public static Size getAnimationHolderDefaultSizeByUIDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth,
//                                                              ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType, int baseWidth, int baseHeight) {
//        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
//        if (imageSelectManager == null) return null;
//        return imageSelectManager.getAnimationHolderDefaultSizeByUIDepth(uiDepth, holderType, baseWidth, baseHeight);
//    }

    public static void setShownDatePhoneFragmentTutorial(Context context, ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType) {
        if (tutorialType == null) return;
        switch (tutorialType) {
            case PHONE_FRAGMENT_PINCH_MOTION:
                Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_TUTORIAL_PINCH_MOTION_SHOWN_DATE, String.valueOf(System.currentTimeMillis()));
                break;
            case SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP:
                Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP, String.valueOf(System.currentTimeMillis()));
                break;
            case SMART_RECOMMEND_BOOK_SWIPE_PAGE:
                Setting.set(context, ISnapsImageSelectConstants.SETTING_KEY_SMART_RECOMMEND_BOOK_SWIPE_PAGE, String.valueOf(System.currentTimeMillis()));
                break;
        }
    }

    public static String getShownDatePhoneFragmentTutorial(Context context, ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType) {
        if (tutorialType == null) return null;
        switch (tutorialType) {
            case PHONE_FRAGMENT_PINCH_MOTION:
                return Setting.getString(context, ISnapsImageSelectConstants.SETTING_KEY_TUTORIAL_PINCH_MOTION_SHOWN_DATE);
            case SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP:
                return Setting.getString(context, ISnapsImageSelectConstants.SETTING_KEY_SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP);
            case SMART_RECOMMEND_BOOK_SWIPE_PAGE:
                return Setting.getString(context, ISnapsImageSelectConstants.SETTING_KEY_SMART_RECOMMEND_BOOK_SWIPE_PAGE);
        }
        return null;
    }

    public static void loadImage(Context context, String thumbnailUrl, int overrideDimension, ImageView imageView, ImageView.ScaleType scaleType) {
        loadImage(context, thumbnailUrl, overrideDimension, imageView, scaleType, false);
    }

    public static void loadImage(Context context, String thumbnailUrl, int overrideDimension, ImageView imageView, ImageView.ScaleType scaleType, boolean asBitmap) {
        if (overrideDimension < 1) {
            overrideDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        }

        if (scaleType != null && scaleType == ImageView.ScaleType.FIT_XY)
            ImageLoader.with(context).load(thumbnailUrl).asBitmap(asBitmap).skipMemoryCache(false).override(overrideDimension, overrideDimension).into(imageView);
        else
            ImageLoader.with(context).load(thumbnailUrl).asBitmap(asBitmap).skipMemoryCache(false).override(overrideDimension, overrideDimension).centerCrop().into(imageView);
    }

    public static void loadImage(Context context, String thumbnailUrl, int overrideDimension, int default_res, ImageView imageView, ImageView.ScaleType scaleType) {
        if (overrideDimension < 1) {
            overrideDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        }

        if (scaleType != null && scaleType == ImageView.ScaleType.FIT_XY)
            ImageLoader.with(context).load(thumbnailUrl).skipMemoryCache(false).override(overrideDimension, overrideDimension).placeholder(default_res).into(imageView);
        else
            ImageLoader.with(context).load(thumbnailUrl).skipMemoryCache(false).override(overrideDimension, overrideDimension).placeholder(default_res).centerCrop().into(imageView);
    }

    public static int getUIDepthOptimumThumbnailDimension(Context context, ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth, boolean isLandscapeMode) {
        float screenWidth = UIUtil.getScreenWidth(context);

        boolean isLowPerformanceDevice = screenWidth <= 720;
        boolean isHighPerformanceDevice = screenWidth >= 1440 && isHighPerformanceDevice();

        int eachThumbnailSize = 0;
        switch (uiDepth) {
            case DEPTH_YEAR:
                eachThumbnailSize = 50; //워낙 많은 사진이 한 화면에 보이니, 버벅거린다...
                break;
            case DEPTH_MONTH:
                eachThumbnailSize = 150;
                break;
            case DEPTH_DAY:
                if (isLowPerformanceDevice)
                    eachThumbnailSize = isLandscapeMode ? 350 : 250;
                else
                    eachThumbnailSize = isLandscapeMode ? 450 : 350;
                break;
            case DEPTH_STAGGERED:
                if (isLowPerformanceDevice)
                    eachThumbnailSize = isLandscapeMode ? 350 : 250;
                else
                    eachThumbnailSize = isLandscapeMode ? 450 : 350;
                break;
        }

        float ratio = eachThumbnailSize / (float) 1080; //가로 해상도가 1080인 단말기 기준으로 적당한 해상도를 책정 했다.

        return Math.max(50, (int) (screenWidth * ratio));
    }

    public static boolean isHighPerformanceDevice() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * x : col
     * y : row
     */
    public static Point getHolderRowAndColumnByKey(String key) throws NumberFormatException {
        if (key == null || !key.contains("_")) return null;
        String[] arMapInfo = key.split("_");
        if (arMapInfo.length < 2) return null;

        return new Point(Integer.parseInt(arMapInfo[1]), Integer.parseInt(arMapInfo[0]));
    }

    public static String getHolderKeyByRowAndColumn(int row, int column) {
        return String.format("%d_%d", row, column);
    }

    public static String getPhonePhotoMapKey(long phonePhotoId) {
        return Const_VALUES.SELECT_PHONE + "_" + phonePhotoId;
    }

    public static String getPhonePhotoGroupKey(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth, GalleryCursorRecord.PhonePhotoFragmentItem photoItem) {
        if (photoItem == null) return "";

        switch (depth) {
            case DEPTH_YEAR:
                return "";
            case DEPTH_MONTH:
                return String.format("month_%d_%d", photoItem.getPhotoTakenYear(), photoItem.getPhotoTakenMonth());
            case DEPTH_DAY:
                return String.format("day_%d_%d_%d", photoItem.getPhotoTakenYear(), photoItem.getPhotoTakenMonth(), photoItem.getPhotoTakenDay());
            case DEPTH_STAGGERED:
                return String.format("staggered_%d_%d_%d", photoItem.getPhotoTakenYear(), photoItem.getPhotoTakenMonth(), photoItem.getPhotoTakenDay());
        }

        return "";
    }

    public static String getGooglePhotoAlbumUrl(IAlbumData albumData) {
        return String.format(ISnapsImageSelectConstants.GOOGLE_PHOTO_ALBUM_URL, albumData.getUserId(), albumData.getAlbumId());
    }

    public static int getCurrentSelectedImageCount() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null) {
                return selectData.getMapSize();
            }
        }

        return 0;
    }

    public static void removeAllImageData() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null) {
                selectData.clearAllDatas();
            }
        }
    }


    public static void showDisableAddPhotoMsg() {
        showDisableAddPhotoMsg(null);
    }

    static boolean isVisivle = false;

    public static void showDisableAddPhotoMsg(Activity activity) {
        if (Config.isIdentifyPhotoPrint()) {
            MessageUtil.toast(getApplicationContext(), getApplicationContext().getString(R.string.disable_add_photo_for_identify_photo, Config.getIdentifyPhotoCount()));

        } else if (Const_PRODUCT.isFrameProduct() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isSinglePageProduct() || Const_PRODUCT.isPackageProduct()
                || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isSnapsDiary() || SnapsDiaryDataManager.isAliveSnapsDiaryService()
                || Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isStikerGroupProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isPosterGroupProduct() || Const_PRODUCT.isNewWalletProduct()
                || Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isSealStickerProduct()) {
            MessageUtil.toast(activity, R.string.disable_add_photo);

        } else if (SmartSnapsManager.isSmartImageSelectType()) {
            MessageUtil.toast(activity, R.string.disable_add_photo);

        } else {
            if (activity != null) {

                if (Config.isKTBook()) {
                    MessageUtil.toast(getApplicationContext(), getApplicationContext().getString(R.string.select_some_photos, ImageSelectPerformForKTBook.MAX_KT_BOOK_IMAGE_COUNT));
                    return;
                }

                if (!isVisivle) {
                    isVisivle = true;
                    MessageUtil.alertnoTitleOneBtn(activity, activity.getString(R.string.add_page_excess_max_page_product), clickedOk -> isVisivle = false);
                }
            }
//            MessageUtil.toast(getApplicationContext(), R.string.disable_add_page);
        }

    }

    public static ArrayList<Integer> getAddPageIdxs() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null) {
                return selectData.getPageAddIdx();
            }
        }
        return null;
    }

    public static boolean isContainsInImageHolder(String imageKey) {
        if (imageKey == null || imageKey.length() < 1) return false;

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectImgDataHolder = imageSelectManager.getImageSelectDataHolder();
            if (selectImgDataHolder != null) {
                return selectImgDataHolder.isSelected(imageKey);
            }
        }

        return false;
    }

    public static void putSelectedImageData(String key, MyPhotoSelectImageData imgData) {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null) {
                selectData.putData(key, imgData);
            }
        }
    }

    public static void removeSelectedImageData(String key) {

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null && selectData.isSelected(key)) {
                selectData.removeData(key);
            }
        }
    }

    public static MyPhotoSelectImageData getSelectedImageData(String imageKey) {
        if (imageKey == null || imageKey.length() < 1) return null;

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectImgDataHolder = imageSelectManager.getImageSelectDataHolder();
            if (selectImgDataHolder != null) {
                return selectImgDataHolder.getData(imageKey);
            }
        }

        return null;
    }

    public static boolean isExistImageKeyFromSelectHolder(ImageSelectTrayCellItem cellItem) {
        if (cellItem == null) return false;

        ImageSelectImgDataHolder imageSelectImgDataHolder = getSelectImageHolder();
        if (imageSelectImgDataHolder == null) return false;

        ArrayList<String> selectImgKeyList = imageSelectImgDataHolder.getSelectImgKeyList();
        if (selectImgKeyList == null) return false;

        for (String selectImgKey : selectImgKeyList) {
            if (selectImgKey != null && selectImgKey.equalsIgnoreCase(cellItem.getImageKey()))
                return true;
        }

        return false;
    }

    public static ImageSelectImgDataHolder getSelectImageHolder() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            return imageSelectManager.getImageSelectDataHolder();
        }
        return null;
    }

    public static String getCurrentPaperCodeMaxPage() {
        MenuDataManager menuDataManager = MenuDataManager.getInstance();
        if (menuDataManager == null) return null;

        MenuData menuData = menuDataManager.getMenuData();
        if (menuData == null || menuData.maxPageInfo == null) return null;

        return menuData.maxPageInfo.getMaxPageWithPaperCode(Config.getPAPER_CODE());
    }

    //서버로 부터 템플릿 XML 파일을 받아서 SnapsTempleteManager에 set해 놓는다.
    public static void requestGetTemplate(final Activity activity, final ImageSelectIntentData intentData, final IImageSelectUtilsInterfaceCallback callback) {
        // 템플릿을 파일로 저장을 한다음 파일 패스를 넘긴다.(편집화면에서 사용하기 위함...)
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {

            @Override
            public void onPre() {
                if (callback != null)
                    callback.onPrepare();
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onResult(result);
            }

            @Override
            public boolean onBG() {
                try {
                    Config.checkServiceThumbnailSimpleFileDir();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                //템플릿을 파싱을 한다
                SnapsTemplateManager.getInstance().cleanInstance();
                SnapsTemplate template = GetTemplateLoad.getTemplate(SnapsTemplate.getTemplateUrl(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                SnapsTemplateManager.getInstance().setSnapsTemplate(template);
                return template != null;
            }
        });
    }

}
