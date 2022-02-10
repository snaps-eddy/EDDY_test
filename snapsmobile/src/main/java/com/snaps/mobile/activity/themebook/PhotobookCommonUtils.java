package com.snaps.mobile.activity.themebook;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsAnimationListener;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.spc.view.SnapsTextView;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.SnapsTemplatePrice;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.DownloadFileAsync;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICommonConfirmListener;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants;
import com.snaps.mobile.activity.themebook.interfaceis.ISnapsEditTextControlHandleListener;
import com.snaps.mobile.activity.themebook.interfaceis.SnapsEditTextControlHandleData;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SnapsLayoutUpdateInfo;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.component.SnapsNumberPicker;
import com.snaps.mobile.interfaces.ISnapsControl;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultHandleData;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;
import font.FTextView;
import jp.wasabeef.blurry.Blurry;

import static com.snaps.common.data.img.MyPhotoSelectImageData.INVALID_ROTATE_ANGLE;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_UPLOAD_ORG_IMAGES;
import static com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener.eImageUploadState.FINISH;
import static com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener.eImageUploadState.NOT_SUPPORT_THUMBNAIL_UPLOAD;

public class PhotobookCommonUtils {
    private static final String TAG = PhotobookCommonUtils.class.getSimpleName();
    public static final int MAX_LAYOUT_COUNT = 100;

    public enum TEMPLATE_SIZE {
        A5
    }

    public static int getTotalPhotoCountTextOnPages(ArrayList<SnapsPage> pages) {
        if (pages == null) return 0;
        int result = 0;
        for (SnapsPage snapsPage : pages) {
            if (snapsPage != null) {
                result += snapsPage.getImageCountOnPage();
            }
        }
        return result;
    }

    public static int findPageIndexThatContainImage(int defaultPageIndex, MyPhotoSelectImageData imageData) {
        try {
            SnapsTemplateManager templateManager = SnapsTemplateManager.getInstance();
            SnapsTemplate template = templateManager.getSnapsTemplate();
            if (template == null || template.getPages() == null || imageData == null || imageData.getImageSelectMapKey() == null)
                return defaultPageIndex;

            final String KEY = imageData.getImageSelectMapKey();
            for (SnapsPage snapsPage : template.getPages()) {
                if (snapsPage == null) continue;
                List<MyPhotoSelectImageData> imageListOnPage = snapsPage.getImageDataListOnPage();
                if (imageListOnPage == null) continue;

                for (MyPhotoSelectImageData imageDataOnPage : imageListOnPage) {
                    if (imageDataOnPage == null) continue;

                    if (KEY.equalsIgnoreCase(imageDataOnPage.getImageSelectMapKey()))
                        return snapsPage.getPageID();
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return defaultPageIndex;
    }

    public static int getImgCntInPage(int pageIndex, ArrayList<SnapsPage> pageList) {
        try {
            if (pageList == null) return 0;
            SnapsPage snapsPage = pageList.get(pageIndex);
            if (snapsPage == null) return 0;
            return snapsPage.getImageCountOnPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    public static String getLastSavedAuraOrderXmlContents() {
        File xmlFile = null;
        FileInputStream fis = null;
        BufferedReader br = null;

        try {
            xmlFile = Config.getPROJECT_FILE(Config.AURA_ORDER_XML_FILE_NAME);
            if (xmlFile == null || !xmlFile.exists()) return "";

            fis = new FileInputStream(xmlFile);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    public static int convertPageSizeToPageCount(int pageSize) {
        return (pageSize - 2) * 2 + 1;
    }

    public static void sortPagesIndex(ArrayList<SnapsPage> _pageList, int selectedPosition) {
        if (_pageList == null) return;
        // 페이지 인덱스 및 이미지 인덱스 조정...
        for (int i = 0; i < _pageList.size(); i++) {
            SnapsPage p = _pageList.get(i);
            p.setPageID(i);
            p.isSelected = i == selectedPosition;

            for (SnapsControl control : p.getLayoutList()) {
                control.setPageIndex(i);

                // 이미지 정렬를 위해..
                if (control instanceof SnapsLayoutControl) {

                    if (((SnapsLayoutControl) control).imgData != null) {
                        ((SnapsLayoutControl) control).imgData.IMG_IDX = PhotobookCommonUtils.getImageIDX(i, control.regValue);
                        ((SnapsLayoutControl) control).imgData.pageIDX = i;
                    }
                }
            }
        }
    }

    public static ArrayList<Integer> getChangedPhotoPageIndexWithImageList(ArrayList<MyPhotoSelectImageData> imgList, SnapsTemplate snapsTemplate) {
        return handleChangedPhotoPageIndexWithImageList(imgList, snapsTemplate);
    }

    public static Rect getPhotoBookQRCodeRect(SnapsTemplate snapsTemplate) throws Exception {
        // cover 사이즈를 구한다.
        float cover_width = Float.parseFloat(snapsTemplate.info.F_COVER_VIRTUAL_WIDTH);
        float cover_height = Float.parseFloat(snapsTemplate.info.F_COVER_VIRTUAL_HEIGHT);

        int width = 25;
        int height = 29;

        Rect qrRect = new Rect();
        qrRect.left = (int) (cover_width / 2 - width - Const_VALUE.QRCODE_RIGHT_MARGIN);
        qrRect.right = qrRect.left + width;
        qrRect.top = (int) (cover_height - height - Const_VALUE.QRCODE_BOTTOM_MARGIN);
        qrRect.bottom = qrRect.top + height;

        return qrRect;
    }

    public static void initBaseTemplateBaseInfo(Activity activity, final SnapsTemplate snapsTemplate) throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setBaseTemplateBaseInfoOnThread(activity, snapsTemplate);
        } else {
            setBaseTemplateBaseInfo(activity, snapsTemplate);
        }
    }

    private static void setBaseTemplateBaseInfo(Activity activity, final SnapsTemplate snapsTemplate) throws Exception {
        // mask data 파일로 선저장.
        PhotobookCommonUtils.saveMaskData(snapsTemplate);

        // 커버배경에 클릭이 되도록 설정..
        snapsTemplate.setBgClickEnable(0, true);

        if (snapsTemplate.getPages().size() > 0 && snapsTemplate.getPages().get(0).getBgList().size() > 0) {
            SnapsBgControl bg = (SnapsBgControl) snapsTemplate.getPages().get(0).getBgList().get(0);
            Config.setUSER_COVER_COLOR(bg.coverColor);
        }

        if (!Const_PRODUCT.isDesignNoteProduct()) {
            for (SnapsPage page : snapsTemplate.getPages()) {
                if (!page.type.equalsIgnoreCase("hidden")) {

                    if (page.type.equalsIgnoreCase("cover")) {

                        for (SnapsControl c : page.getControlList()) {
                            if (!Config.isNotCoverPhotoBook()) {
                                if (c instanceof SnapsTextControl) {
                                    if ("true".equalsIgnoreCase(((SnapsTextControl) c).format.verticalView)) {
                                        ((SnapsTextControl) c).text = Config.getPROJ_NAME();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 추후에 KT 관련 부분만 쉽게 드러내기 위해.
            if (Config.isKTBook()) {
                List<SnapsPage> pages = snapsTemplate.getPages();
                if (pages.size() > 0) {
                    SnapsPage coverPage = pages.get(0); // 0 번 페이지만 커버로 생각한다.
                    for (SnapsControl c : coverPage.getControlList()) {
                        if (!Config.isNotCoverPhotoBook() && c instanceof SnapsTextControl) {
                            ((SnapsTextControl) c).text = Config.getPROJ_NAME();
                        }
                    }
                }
            }
        }

        snapsTemplate.initMaxPageInfo(activity);

        snapsTemplate.addSpine();
        // loadTemplate().addNoPrintText();
        snapsTemplate.setApplyMaxPage();
    }

    private static void setBaseTemplateBaseInfoOnThread(final Activity activity, final SnapsTemplate snapsTemplate) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    setBaseTemplateBaseInfo(activity, snapsTemplate);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }

    public static void initPaperInfoOnLoadedTemplate(SnapsTemplate template) throws Exception {
        if (Config.isFromCart()) {
            Config.setPAPER_CODE(template.info.F_PAPER_CODE);
            Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
        } else {
            template.info.F_PAPER_CODE = Config.getPAPER_CODE();
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
        }

        if (!Config.getPAPER_CODE().equals(""))
            template.info.F_PAPER_CODE = Config.getPAPER_CODE();

        // 용지설정..값이 없으면 설정....
        if (template.info.F_PAPER_CODE.equals(""))
            template.info.F_PAPER_CODE = "160001";

        PhotobookCommonUtils.checkPaperInfoFromTemplate(template);
    }

    private static SnapsLayoutControl findLayoutControlByRegValue(SnapsPage snapsPage, String regValue) {
        if (snapsPage == null || regValue == null) return null;

        ArrayList<SnapsControl> layoutList = snapsPage.getLayoutList();
        if (layoutList == null) return null;

        for (SnapsControl control : layoutList) {
            if (control == null || !(control instanceof SnapsLayoutControl)) continue;
            String value = control.regValue;
            if (!StringUtil.isEmpty(value) && value.equalsIgnoreCase(regValue))
                return (SnapsLayoutControl) control;
        }
        return null;
    }

    public static void changePageBGWithNewTemplate(SnapsPage newPage, final ArrayList<SnapsPage> pageList, final int index) throws Exception {
        if (newPage == null || newPage.getBgList() == null || newPage.getBgControl() == null || pageList == null || pageList.size() <= index)
            return;

        SnapsPage oldPage = pageList.get(index);
        SnapsBgControl newBgControl = newPage.getBgControl();
        newBgControl.setPageIndex(oldPage.getPageID());
        newBgControl.setControlId(-1);

        oldPage.changeBg(newBgControl);
    }

    public static void changePageImageLayoutWithNewTemplate(final Activity activity, SnapsPage newPage, final ArrayList<SnapsPage> pageList, final int index, final SnapsTemplate snapsTemplate) throws Exception {
        if (newPage == null || pageList == null || pageList.size() <= index)
            return;

        SnapsPage oldPage = pageList.get(index);
        newPage.info = oldPage.info;
        oldPage.multiformId = newPage.multiformId;
        newPage.setPageID(oldPage.getPageID());

        for (SnapsControl control : newPage.getLayoutList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        for (SnapsControl control : newPage.getBgList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        for (SnapsControl control : newPage.getControlList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        for (SnapsControl control : newPage.getFormList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        int imgCnt = newPage.getLayoutList().size();
        int idx = 0;

        ArrayList<SnapsControl> layoutList = oldPage.getLayoutList();
        for (int ii = 0; ii < layoutList.size(); ii++) {
            SnapsControl control = layoutList.get(ii);
            if (control instanceof SnapsLayoutControl) {
                SnapsLayoutControl oldControl = (SnapsLayoutControl) control;
                if (oldControl.imgData != null && oldControl.type.equalsIgnoreCase("browse_file")) {
                    if (idx < imgCnt) {
                        SnapsLayoutControl newControl = findLayoutControlByRegValue(newPage, oldControl.regValue);
                        if (newControl == null)
                            newControl = ((SnapsLayoutControl) newPage.getLayoutList().get(idx));
                        newControl.setControlId(-1);

                        if (oldControl.imgData.ORIGINAL_ROTATE_ANGLE != INVALID_ROTATE_ANGLE)
                            oldControl.imgData.ROTATE_ANGLE = oldControl.imgData.ORIGINAL_ROTATE_ANGLE;

                        //효과 필터가 적용 된 사진은 회전 정보가 반영 되어 있기 때문에 원래 각도로 복구해서 로딩한다.
                        if (oldControl.imgData.isApplyEffect
                                && (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != oldControl.imgData.ROTATE_ANGLE_THUMB || oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE == INVALID_ROTATE_ANGLE)) {
                            try {
                                if (!ImageFilters.updateEffectImageToOrgAngle(activity, oldControl.imgData)) {
                                    oldControl.imgData.isApplyEffect = false;
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                oldControl.imgData.isApplyEffect = false;
                            }
                        }

                        if (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) //만약 ratio 오류같은 게 발생한다면 이부분을 의심해보자
                            oldControl.imgData.ROTATE_ANGLE_THUMB = oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE;

                        newControl.imgData = oldControl.imgData;

                        newControl.imgData.FREE_ANGLE = 0;
                        newControl.imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
                        newControl.imgData.isAdjustableCropMode = false;
                        newControl.imgData.ADJ_CROP_INFO = new AdjustableCropInfo();

                        newControl.imgData.IMG_IDX = Integer.parseInt(0 + "" + newPage.getLayoutList().get(idx).regValue);
                        newControl.freeAngle = 0;// oldControl.imgData.FREE_ANGLE;
                        newControl.angle = String.valueOf(oldControl.imgData.ROTATE_ANGLE);
                        newControl.imagePath = oldControl.imgData.PATH;
                        newControl.imageLoadType = oldControl.imgData.KIND;
                        newControl.imgData.cropRatio = newControl.getRatio();
                        newControl.imgData.increaseUploadPriority();

                        SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(activity, newControl);

                        // 인쇄가능 여부..
                        try {
                            String pageMMWidth = snapsTemplate.info.F_PAGE_MM_WIDTH;
                            String pagePXWidth = snapsTemplate.info.F_PAGE_PIXEL_WIDTH;
                            if (!StringUtil.isEmpty(pageMMWidth) && !StringUtil.isEmpty(pagePXWidth)) {
                                ResolutionUtil.isEnableResolution(Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH), Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH), newControl);
                            }
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }

                        layoutList.remove(ii);
                        layoutList.add(ii, newControl);
                    }

                    idx++;
                }
            }
        }

        // 페이지 교체...
//		pageList.remove(index);
//
//		pageList.add(index, newPage);

//        getEditorBase().exportAutoSaveTemplate();
    }

    public static void changeCoverImageLayerWithNewTemplate(final Activity activity, final SnapsPage newPage, final ArrayList<SnapsPage> pageList, int pageIndexOnList, final SnapsTemplate snapsTemplate) throws Exception {
        if (activity == null || newPage == null) return;

        // 새로운 커버에 이미지가 넣기..
        int imgCnt = newPage.getLayoutList().size();
        newPage.info = snapsTemplate.info;

        // 예전커버.
        int index = 0;
        SnapsPage oldPage = pageList.get(pageIndexOnList);
        oldPage.multiformId = newPage.multiformId;

        ArrayList<SnapsControl> layoutList = oldPage.getLayoutList();
        for (int ii = 0; ii < layoutList.size(); ii++) {
            SnapsControl control = layoutList.get(ii);
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl oldControl = (SnapsLayoutControl) control;
                if (oldControl.imgData != null && oldControl.type.equalsIgnoreCase("browse_file")) {
                    if (index < imgCnt) {
                        SnapsLayoutControl newControl = ((SnapsLayoutControl) newPage.getLayoutList().get(index));
                        newControl.setControlId(-1);

                        if (oldControl.imgData.ORIGINAL_ROTATE_ANGLE != INVALID_ROTATE_ANGLE)
                            oldControl.imgData.ROTATE_ANGLE = oldControl.imgData.ORIGINAL_ROTATE_ANGLE;

                        //효과 필터가 적용 된 사진은 회전 정보가 반영 되어 있기 때문에 원래 각도로 복구해서 로딩한다.
                        if (oldControl.imgData.isApplyEffect
                                && (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != oldControl.imgData.ROTATE_ANGLE_THUMB || oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE == INVALID_ROTATE_ANGLE)) {
                            try {
                                if (!ImageFilters.updateEffectImageToOrgAngle(activity, oldControl.imgData)) {
                                    oldControl.imgData.isApplyEffect = false;
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                oldControl.imgData.isApplyEffect = false;
                            }
                        }

                        if (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) //만약 ratio 오류같은 게 발생한다면 이부분을 의심해보자
                            oldControl.imgData.ROTATE_ANGLE_THUMB = oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE;

                        newControl.imgData = oldControl.imgData;

                        newControl.imgData.FREE_ANGLE = 0;
                        newControl.imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
                        newControl.imgData.isAdjustableCropMode = false;
                        newControl.imgData.ADJ_CROP_INFO = new AdjustableCropInfo();

                        newControl.imgData.IMG_IDX = Integer.parseInt(0 + "" + newPage.getLayoutList().get(index).regValue);
                        newControl.freeAngle = 0;// oldControl.imgData.FREE_ANGLE;
                        newControl.angle = String.valueOf(oldControl.imgData.ROTATE_ANGLE);
                        newControl.imagePath = oldControl.imgData.PATH;
                        newControl.imageLoadType = oldControl.imgData.KIND;
                        newControl.imgData.cropRatio = newControl.getRatio();

                        // 인쇄가능 여부..
                        try {
                            String pageMMWidth = snapsTemplate.info.F_PAGE_MM_WIDTH;
                            String pagePXWidth = snapsTemplate.info.F_PAGE_PIXEL_WIDTH;
                            if (!StringUtil.isEmpty(pageMMWidth) && !StringUtil.isEmpty(pagePXWidth)) {
                                ResolutionUtil.isEnableResolution(Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH), Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH), newControl);
                            }
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }

                        SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(activity, newControl);

                        layoutList.remove(ii);
                        layoutList.add(ii, newControl);
                    }

                    // 커버 이미지 삭제..
//					oldControl = null;
                    index++;
                }
            }
        }

        // 새커버에 제목 입력하기..
//		for (SnapsControl control : newPage.getControlList()) {
//			if (control instanceof SnapsTextControl) {
//				((SnapsTextControl) control).text = Config.getPROJ_NAME();
//			}
//			control.setPageIndex(oldPage.getPageID());
//			control.setControlId(-1);
//		}
//
//		for (SnapsControl control : newPage.getLayoutList()) {
//			control.setPageIndex(oldPage.getPageID());
//			control.setControlId(-1);
//		}

//		for (SnapsControl control : newPage.getBgList()) {
//			control.setPageIndex(oldPage.getPageID());
//			control.setControlId(-1);
//		}
//
//		for (SnapsControl control : newPage.getFormList()) {
//			control.setPageIndex(oldPage.getPageID());
//			control.setControlId(-1);
//		}
//
//		pageList.remove(pageIndexOnList);
//		pageList.add(pageIndexOnList, newPage);
    }

    private static ArrayList<Integer> handleChangedPhotoPageIndexWithImageList(ArrayList<MyPhotoSelectImageData> imgList, SnapsTemplate snapsTemplate) {
        ArrayList<Integer> changeList = new ArrayList<Integer>();

        ArrayList<MyPhotoSelectImageData> _imgList = PhotobookCommonUtils.getImageListFromTemplate(snapsTemplate);
        if (_imgList != null) {
            for (MyPhotoSelectImageData cropData : imgList) {
                if (cropData.isModify == -1)
                    continue;

                MyPhotoSelectImageData d = PhotobookCommonUtils.getMyPhotoSelectImageDataWithImgIdx(_imgList, cropData.getImageDataKey());

                if (d != null) {
                    // d.CROP_INFO = cropData.CROP_INFO;
                    // d.ADJ_CROP_INFO = cropData.ADJ_CROP_INFO;
                    d.CROP_INFO.set(cropData.CROP_INFO);
                    d.ADJ_CROP_INFO.set(cropData.ADJ_CROP_INFO);

                    d.FREE_ANGLE = cropData.FREE_ANGLE;
                    d.ROTATE_ANGLE = cropData.ROTATE_ANGLE;
                    d.ROTATE_ANGLE_THUMB = cropData.ROTATE_ANGLE_THUMB;

                    d.EFFECT_PATH = cropData.EFFECT_PATH;
                    d.EFFECT_THUMBNAIL_PATH = cropData.EFFECT_THUMBNAIL_PATH;
                    d.EFFECT_TYPE = cropData.EFFECT_TYPE;
                    d.ORIGINAL_ROTATE_ANGLE = cropData.ORIGINAL_ROTATE_ANGLE;
                    d.ORIGINAL_THUMB_ROTATE_ANGLE = cropData.ORIGINAL_THUMB_ROTATE_ANGLE;
                    d.isAdjustableCropMode = cropData.isAdjustableCropMode;
                    d.isApplyEffect = cropData.isApplyEffect;
                    d.screenWidth = cropData.screenWidth;
                    d.screenHeight = cropData.screenHeight;
                    d.editorOrientation = cropData.editorOrientation;
                    d.isNoPrint = cropData.isNoPrint;

                    changeList.add(cropData.pageIDX);
                }
            }
        }

        return changeList;
    }

    public static void handleOnClickedTextControl(SnapsProductEditReceiveData editEvent, SnapsEditTextControlHandleData editTextControlHandleData) throws Exception {
        if (editEvent == null) return;

        SnapsControl control = editEvent.getSnapsControl();
        if (control == null || !(control instanceof SnapsTextControl)) return;

        Intent intent = editEvent.getIntent();
        if (intent == null) return;

        String currentWrittenText = ((SnapsTextControl) control).text;
        if (!StringUtil.isEmpty(currentWrittenText)) {
            showEditTextPopupMenu(intent, editTextControlHandleData);
        } else {
            handlePerformEditText(editTextControlHandleData);
        }
    }

    public static SnapsTextControl findCoverTextControl(SnapsPage coverPage) {
        if (coverPage == null || coverPage.getTextControlList() == null) return null;
        for (SnapsControl control : coverPage.getTextControlList()) {
            if (control != null && control._controlType == SnapsControl.CONTROLTYPE_TEXT && control instanceof SnapsTextControl) {
                return (SnapsTextControl) control;
            }
        }
        return null;
    }

    public static void handleNotifyCoverTextFromIntentData(Intent data, Activity activity, SnapsPage coverPage) throws Exception {
        String str = data.getStringExtra("contentText");
        handleNotifyCoverTextFromText(str, activity, coverPage);
    }

    public static void handleNotifyCoverTextFromText(String text, Activity activity, SnapsPage coverPage) throws Exception {
        OrientationManager.fixCurrentOrientation(activity);

        ArrayList<SnapsControl> coverTextControls = coverPage.getTextControlList();
        if (coverTextControls != null) {
            for (SnapsControl control : coverTextControls) {
                if (control instanceof SnapsTextControl) {
                    SnapsTextControl textControl = (SnapsTextControl) control;
                    textControl.text = text;

//                    if (Config.isKTBook()) {
//                        ((SnapsTextControl) control).isEditedText = true;
//                    }
                }
            }
        }

        int[] textIds = PhotobookCommonUtils.getCoverTextViewIdArr(coverPage);
        if (textIds != null) {
            for (int a : textIds) {
                View view = activity.findViewById(a);
                if (view instanceof SnapsTextView) {
                    ((SnapsTextView) view).text(text);
                }
            }
        }
        // 프로젝트 이름도 수정을 한다..
        Config.setPROJ_NAME(text);
    }

    public static int[] getCoverTextViewIdArr(SnapsPage coverPage) {
        if (coverPage == null) return null;
        // 커버페이지를 구한다. 첫번째는 무조건 커버라고 생각하고 가정..
        int[] ids = null;

        int cnt = 0;
        // 텍스트들... 책등은 제외...
        for (SnapsControl control : coverPage.getControlList()) {
            if (control instanceof SnapsTextControl) {
                cnt++;
            }
        }

        if (cnt > 0) {
            ids = new int[cnt];
            int i = 0;
            for (SnapsControl control : coverPage.getControlList()) {
                if (control instanceof SnapsTextControl) {
                    ids[i] = control.getControlId();
                    i++;
                }
            }
        }

        return ids;
    }

    public static void handlePerformEditText(SnapsEditTextControlHandleData editTextControlHandleData) throws Exception {
        if (editTextControlHandleData == null) return;

        OrientationManager.fixCurrentOrientation(editTextControlHandleData.getActivity());

        PopoverView popupMenuView = editTextControlHandleData.getPopoverView();
        if (popupMenuView != null)
            popupMenuView.dissmissPopover(false);

        SnapsTutorialUtil.clearTooltip();

        SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(editTextControlHandleData.getActivity(), editTextControlHandleData.getTempViewId());
        if (snapsControl == null || !(snapsControl instanceof SnapsTextControl))
            return;

        SnapsTextControl control = (SnapsTextControl) snapsControl;

        String currentWrittenText = control.text;

        changeActivityStateToBlurWhenWriteText(editTextControlHandleData);

        if (editTextControlHandleData.getTitleLayout() != null) {
            editTextControlHandleData.getTitleLayout().setVisibility(View.GONE);
        }

        Intent in = new Intent(editTextControlHandleData.getActivity(), SnapsTextWriteActivity.class);
        Bundle bundle = new Bundle();

        SnapsTemplate snapsTemplate = editTextControlHandleData.getSnapsTemplate();
        if (snapsTemplate != null && snapsTemplate.info != null && snapsTemplate.info.snapsTextOption != null) {
            OrientationManager orientationManager = OrientationManager.getInstance(editTextControlHandleData.getActivity());
            snapsTemplate.info.snapsTextOption.initByTextFormat(control.format, orientationManager.isLandScapeMode());
            bundle.putSerializable("snapsTextOption", snapsTemplate.info.snapsTextOption);
        }

        bundle.putString("written_text", currentWrittenText);
        bundle.putBoolean("is_cover_title_edit", editTextControlHandleData.isCoverTitleEdit());
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        in.putExtras(bundle);

        editTextControlHandleData.getActivity().startActivityForResult(in, editTextControlHandleData.getActivityRequestCode());
    }

    private static void changeActivityStateToBlurWhenWriteText(SnapsEditTextControlHandleData editTextControlHandleData) throws Exception {
        if (!editTextControlHandleData.shouldBeBlurBackground() || editTextControlHandleData.isAppliedBlurActivity())
            return;

        Activity activity = editTextControlHandleData.getActivity();

        View rootView = activity.findViewById(R.id.root_layout);
        View titleBarBlindView = activity.findViewById(R.id.snaps_edit_activity_title_bar_blind_view);
        OrientationManager orientationManager = OrientationManager.getInstance(activity);
        if (orientationManager.isLandScapeMode()) {
            View galleryView = activity.findViewById(R.id.activity_edit_themebook_gallery_ly_h);
            PhotobookCommonUtils.handleBlurEditActivityWhenWriteTextForLandScape(activity, rootView, titleBarBlindView, galleryView);
        } else {
            PhotobookCommonUtils.handleBlurEditActivityWhenWriteText(activity, rootView, titleBarBlindView);
        }

        ISnapsEditTextControlHandleListener handleListener = editTextControlHandleData.getHandleListener();
        if (handleListener != null) {
            handleListener.shouldAppliedBlurFlagToTrue();
        }

//		isAppliedBlurActivity = true;
    }

    private static void showEditTextPopupMenu(Intent intent, SnapsEditTextControlHandleData editTextControlHandleData) {
        int id = intent.getIntExtra("control_id", -1);
        showEditTextPopupMenu(id, editTextControlHandleData);
    }

    private static void showEditTextPopupMenu(int targetViewId, SnapsEditTextControlHandleData editTextControlHandleData) {
        if (editTextControlHandleData.getPopoverView() != null && editTextControlHandleData.getPopoverView().isShown())
            return;

        View rootView = editTextControlHandleData.getRootView();
        if (rootView == null) return;

        View foundView = rootView.findViewById(targetViewId);
        if (foundView == null || !(foundView instanceof SnapsTextToImageView)) return;

        SnapsTextToImageView snapsTextToImageView = (SnapsTextToImageView) foundView;
        View targetView = snapsTextToImageView.getOutLineView();
        if (targetView == null) return;

        Rect rect = new Rect();
        targetView.getGlobalVisibleRect(rect);

        Activity activity = editTextControlHandleData.getActivity();

        Dlog.d("showEditTextPopupMenu() GlobalVisibleRect:" + rect.toString());
        int popWidth = UIUtil.convertDPtoPX(activity, 100);
        int popHeight = UIUtil.convertDPtoPX(activity, 37);

        PopoverView popupMenuView = new PopoverView(activity, R.layout.popmenu_card_text);

        ISnapsEditTextControlHandleListener handleListener = editTextControlHandleData.getHandleListener();
        if (handleListener != null) {
            handleListener.shouldSetPopupMenuView(popupMenuView);
//			getEditControls().setPopupMenuView(popupMenuView);
        }

        popupMenuView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));
        DataTransManager transMan = DataTransManager.getInstance();
        if (transMan != null) {
            ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
            if (coordInfo != null) {
                OrientationManager orientationManager = OrientationManager.getInstance(editTextControlHandleData.getActivity());

                boolean shouldFixOffset = !coordInfo.convertPopupOverRect(rect, targetView, editTextControlHandleData.getRootView(), orientationManager.isLandScapeMode());
                if (shouldFixOffset) {
                    if (!orientationManager.isLandScapeMode()) {
                        rect.top += UIUtil.convertDPtoPX(activity, 20);
                        rect.bottom += UIUtil.convertDPtoPX(activity, 20);
                    }

                    int halfHeight = snapsTextToImageView.isEdited() ? (popHeight / 4) : (rect.height() / 2) + UIUtil.convertDPtoPX(activity, 6);
                    rect.offset(0, halfHeight);
                }

                popupMenuView.setArrowPosition(rect, coordInfo.getTranslateX(), coordInfo.getScaleFactor(), coordInfo.getDefualtScaleFactor(), orientationManager.isLandScapeMode());
            }
        } else {
            DataTransManager.notifyAppFinish(activity);
            return;
        }

        popupMenuView.showPopoverFromRectInViewGroup((ViewGroup) editTextControlHandleData.getRootView(), rect, PopoverView.PopoverArrowDirectionUp, true);
    }

    public static boolean isFromLayoutControlReceiveData(Intent intent) {
        return intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(Const_VALUE.CLICK_LAYOUT_ACTION) && intent.getDataString() == null;
    }

    public static boolean isFromTextToImageReceiveData(Intent intent) {
        return intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(Const_VALUE.TEXT_TO_IMAGE_ACTION) && intent.getDataString() == null;
    }

    public static boolean shouldUploadFullSizeThumbnailProduct() {
        return true;//Const_PRODUCT.isPhotoMugCupProduct() || Const_PRODUCT.isTumblerProduct();
    }

    public static boolean shouldForceSetControlIdProduct() {
        return Config.isSmartSnapsRecommendLayoutPhotoBook();
    }

    public static void handleThumbImgUploadStateChanged(SnapsImageUploadResultHandleData resultHandleData) {
        if (resultHandleData == null) return;

        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
            if (resultHandleData.getState() == NOT_SUPPORT_THUMBNAIL_UPLOAD || resultHandleData.getState() == FINISH) {
                SnapsOrderManager.uploadOrgImgOnBackground();
            }
            return;
        }

        switch (resultHandleData.getState()) {
            case START:
                initSmartSnapsImageUploadState();
                break;
            case NOT_SUPPORT_THUMBNAIL_UPLOAD:
            case FINISH:
                if (resultHandleData.getSnapsHandler() != null)
                    resultHandleData.getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_UPLOAD_ORG_IMAGES, 3000);
                break;
            case PROGRESS:
                if (resultHandleData.getUploadResultData() != null) {
                    MyPhotoSelectImageData imageData = resultHandleData.getUploadResultData().getImageData();
                    if (imageData != null && imageData.isSmartSnapsSupport()) {
                        increaseSmartSnapsImageUploadCount();
                        if (imageData.isFindSmartSnapsFaceArea())
                            handleOnSmartSnapsImgUploadSuccess(imageData, resultHandleData.getActivity(), resultHandleData.getSnapsTemplate());
                        else
                            handleOnSmartSnapsImgUploadFailed(imageData, resultHandleData.getActivity());
                    }
                }
                break;
            case FAILED:
                if (resultHandleData.getUploadResultData() != null) {
                    MyPhotoSelectImageData imageData = resultHandleData.getUploadResultData().getImageData();
                    if (imageData != null && imageData.isSmartSnapsSupport()) {
                        increaseSmartSnapsImageUploadCount();
                        handleOnSmartSnapsImgUploadFailed(imageData, resultHandleData.getActivity());
                    }
                }
                break;
        }
    }

    public static void handleOnSmartSnapsImgUploadSuccess(MyPhotoSelectImageData uploadedImageData, Activity activity, SnapsTemplate snapsTemplate) {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) return;
        if (SmartSnapsManager.isSmartAreaSearching()) {
            try {
                if (isReadyStateSmartAnimationImageData(uploadedImageData)) {
                    SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(activity, snapsTemplate, uploadedImageData);

                    SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(uploadedImageData, SmartSnapsConstants.eSmartSnapsImgState.RECEIVE_SMART_SNAPS_INFO);
                    uploadedImageData.requestSmartSnapsAnimation();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsAssert.assertException(activity, e);
            }
        }
    }

    public static void handleOnSmartSnapsImgUploadFailed(MyPhotoSelectImageData uploadedImageData, Activity activity) {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) return;
        ;
        if (SmartSnapsManager.isSmartAreaSearching()) {
            try {
                if (isReadyStateSmartAnimationImageData(uploadedImageData)) {
                    SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(uploadedImageData, SmartSnapsConstants.eSmartSnapsImgState.RECEIVE_SMART_SNAPS_INFO);
                    uploadedImageData.requestSmartSnapsAnimation();
                    uploadedImageData.isAdjustableCropMode = false;
                    uploadedImageData.ADJ_CROP_INFO = new AdjustableCropInfo();
                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    smartSnapsManager.removeUploadReadyImageData(uploadedImageData);
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsAssert.assertException(activity, e);
            }
        }
    }

    private static boolean isReadyStateSmartAnimationImageData(MyPhotoSelectImageData imageData) {
        if (imageData == null || imageData.isEditedImage()) return false;
        SmartSnapsImgInfo smartSnapsImgInfo = imageData.getSmartSnapsImgInfo();
        return smartSnapsImgInfo != null && smartSnapsImgInfo.getSmartSnapsImgState() == SmartSnapsConstants.eSmartSnapsImgState.READY;
    }

    public static void initSmartSnapsImageUploadState() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) return;
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.setSmartSnapsImgUploadCompleteCount(0);
    }

    public static void increaseSmartSnapsImageUploadCount() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) return;
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.increaseSmartSnapsImgUploadCompleteCount();
    }

    public static void replaceNewImageData(SnapsLayoutUpdateInfo layoutUpdateInfo, SmartSnapsAnimationListener animationListener) throws Exception {
        if (layoutUpdateInfo == null) return;

        SnapsLayoutControl control = layoutUpdateInfo.getLayoutControl();
        MyPhotoSelectImageData newImageData = layoutUpdateInfo.getNewImageData();
        SnapsTemplate snapsTemplate = layoutUpdateInfo.getSnapsTemplate();

        newImageData.cropRatio = control.getRatio();
        newImageData.IMG_IDX = PhotobookCommonUtils.getImageIDX(control.getPageIndex(), control.regValue);

        if (control.imgData != null) {
            SnapsOrderManager.removeBackgroundUploadOrgImageData(control.imgData);
        }

        newImageData.pageIDX = control.getPageIndex();
        newImageData.mmPageWidth = StringUtil.isEmpty(snapsTemplate.info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH);
        newImageData.pxPageWidth = StringUtil.isEmpty(snapsTemplate.info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH);
        newImageData.controlWidth = control.width;
        newImageData.increaseUploadPriority();

        if (layoutUpdateInfo.shouldSmartSnapsFitAnimation()) {
            if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                smartSnapsManager.setSingleSmartSnapsImageData(newImageData, control.getPageIndex());
                SmartSnapsManager.startSmartSnapsAutoFitImage(animationListener, SmartSnapsConstants.eSmartSnapsProgressType.ADD_PAGE, control.getPageIndex());
            }
        }

        control.imgData = newImageData;
        control.angle = String.valueOf(newImageData.ROTATE_ANGLE);
        control.imagePath = newImageData.PATH;
        control.imageLoadType = newImageData.KIND;

        // 인쇄가능 여부..
        if (!Const_PRODUCT.isFreeSizeProduct()) {
            //프리사이즈 상품이 아닌 경우만 인쇄가능 여부를 검사한다.
            //프라사이즈인 경우 선택한 이미지에 따라 인쇄가능 여부가 결정된다.
            try {
                ResolutionUtil.isEnableResolution(Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH), Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH), control);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        control.isUploadFailedOrgImg = false;
    }

    public static Dialog createCountPickerDialog(Context context, int defaultValue, int maxValue, final ICommonConfirmListener confirmListener) throws Exception {
        Dialog pickerDialog = new Dialog(context);
        pickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickerDialog.setContentView(R.layout.edit_activity_thumbnail_view_counter_picker_dialog);
        pickerDialog.setCanceledOnTouchOutside(false);

        SnapsNumberPicker numberPicker = (SnapsNumberPicker) pickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value);
            }
        });
        numberPicker.setValue(defaultValue);

        numberPicker.changeDividerColor(Color.parseColor("#eeeeee"));

        font.FTextView confirmBtn = (FTextView) pickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) confirmListener.onConfirmed();
            }
        });

        return pickerDialog;
    }

    public static String getMaskUrlWithMaskResName(Context context, String maskResName) {
        String _url = SnapsAPI.GET_API_RESOURCE_IMAGE() + "&rname=" + maskResName;
        Dlog.d("getMaskUrlWithMaskResName() url" + _url);

        String maskFilePath = Const_VALUE.PATH_PACKAGE(context, true) + "/mask/" + maskResName;
        if (FileUtil.isExistFile(maskFilePath)) {
            int[] rect = CropUtil.getBitmapFilesLength(maskFilePath);
            if (rect != null && rect.length >= 2 && rect[0] > 0 && rect[1] > 0) {
                _url = maskFilePath;
            }
        }
        return _url;
    }

    public static void handleBlurEditActivityWhenWriteText(final Activity activity, final View rootView, View titleBarBlindView) {
        if (titleBarBlindView != null) {
            titleBarBlindView.setVisibility(View.VISIBLE);

            titleBarBlindView.post(new Runnable() {
                @Override
                public void run() {
                    handleBlurActivityWithRootView(activity, rootView);
                }
            });
        }
    }

    public static void handleBlurActivityWithRootView(Activity activity, final View rootView) {
        if (rootView != null) {
            try {
                Blurry.with(activity).radius(6).sampling(4).onto((ViewGroup) rootView);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public static void handleBlurEditActivityWhenWriteTextForLandScape(final Activity activity, final View rootView, final View titleBarBlindView, View thumbnailLayout) {
        if (thumbnailLayout != null) {
            thumbnailLayout.setVisibility(View.GONE);
        }

        if (titleBarBlindView != null) {
            titleBarBlindView.setVisibility(View.VISIBLE);

            titleBarBlindView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (rootView != null) {
                        try {
                            Blurry.with(activity).radius(7).sampling(4).onto((ViewGroup) rootView);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }
                }
            }, 500);
        }
    }

    public static void recoverBlurEditActivityWhenWrittenText(View rootView, View titleBarBlindView, View thumbnailLayout) {
        if (thumbnailLayout != null && !thumbnailLayout.isShown()) {
            thumbnailLayout.setVisibility(View.VISIBLE);
        }

        if (titleBarBlindView != null) {
            titleBarBlindView.setVisibility(View.GONE);
        }

        removeBlurEffectWithRootView(rootView);
    }

    public static void removeBlurEffectWithRootView(View rootView) {
        if (rootView != null) {
            try {
                Blurry.delete((ViewGroup) rootView);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    public static int getEachPageCount(int pageCount) {
        return (pageCount - 2) * 2 + 1;
    }

    public static float getPercentOfDiscountPrice(SnapsTemplate snapsTemplate) {
        if (snapsTemplate != null && snapsTemplate.priceList != null && !snapsTemplate.priceList.isEmpty()) {
            SnapsTemplatePrice price = snapsTemplate.priceList.get(0);
            if (price != null) {
                if (StringUtil.isEmpty(price.F_ORG_PRICE)) price.F_ORG_PRICE = "0";
                if (StringUtil.isEmpty(price.F_SELL_PRICE)) price.F_SELL_PRICE = "0";

                try {
                    double orgPrice = Double.parseDouble(price.F_ORG_PRICE);
                    double sellPrice = Double.parseDouble(price.F_SELL_PRICE);
                    if (orgPrice > 0 && sellPrice > 0) {
                        return (float) (((orgPrice - sellPrice) / orgPrice) * 100);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }

        return 0;
    }

    public static String calculateAddedPageTotalOrgPrice(Context context, SnapsTemplate snapsTemplate, int totalPageCount) {
        String result = "";

        if (snapsTemplate != null && snapsTemplate.priceList != null && !snapsTemplate.priceList.isEmpty()) {
            SnapsTemplatePrice price = snapsTemplate.priceList.get(0);
            if (price != null) {
                double value = 0;

                if (StringUtil.isEmpty(price.F_ORG_PRICE)) price.F_ORG_PRICE = "0";
                if (StringUtil.isEmpty(price.F_SELL_PRICE)) price.F_SELL_PRICE = "0";
                if (StringUtil.isEmpty(price.F_ORG_PAGE_ADD_PRICE))
                    price.F_ORG_PAGE_ADD_PRICE = "0";
                if (StringUtil.isEmpty(price.F_PAGE_ADD_PRICE)) price.F_PAGE_ADD_PRICE = "0";

                try {
                    value = Double.parseDouble(price.F_ORG_PRICE);

                    final int BASE_QUANTITY_CNT = Integer.parseInt(snapsTemplate.info.F_BASE_QUANTITY);
                    final int BASE_PAGE_CNT = (BASE_QUANTITY_CNT * 2) + 1;

                    // 추가 금액
                    if (totalPageCount > BASE_PAGE_CNT) {
                        double eachPageAddPrice = Double.parseDouble(price.F_ORG_PAGE_ADD_PRICE);
                        int addPageCount = totalPageCount - BASE_PAGE_CNT;
                        int mod = addPageCount % 2;
                        int totalCount = (addPageCount / 2) + mod;

                        value += (eachPageAddPrice * (float) totalCount);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                result = StringUtil.getCurrencyStr(context, value, false);
            }
        }

        return result;
    }

    public static String calculateAddedPageTotalSellPrice(Context context, SnapsTemplate snapsTemplate, int totalPageCount) {
        String result = "";

        if (snapsTemplate != null && snapsTemplate.priceList != null && !snapsTemplate.priceList.isEmpty()) {
            SnapsTemplatePrice price = snapsTemplate.priceList.get(0);
            if (price != null) {
                double value = 0;

                if (StringUtil.isEmpty(price.F_ORG_PRICE)) price.F_ORG_PRICE = "0";
                if (StringUtil.isEmpty(price.F_SELL_PRICE)) price.F_SELL_PRICE = "0";
                if (StringUtil.isEmpty(price.F_ORG_PAGE_ADD_PRICE))
                    price.F_ORG_PAGE_ADD_PRICE = "0";
                if (StringUtil.isEmpty(price.F_PAGE_ADD_PRICE)) price.F_PAGE_ADD_PRICE = "0";

                try {
                    value = Double.parseDouble(price.F_SELL_PRICE);

                    final int BASE_QUANTITY_CNT = Integer.parseInt(snapsTemplate.info.F_BASE_QUANTITY);
                    final int BASE_PAGE_CNT = (BASE_QUANTITY_CNT * 2) + 1;

                    // 추가 금액
                    if (totalPageCount > BASE_PAGE_CNT) {

                        double eachPageAddPrice = Double.parseDouble(price.F_PAGE_ADD_PRICE);
                        int addPageCount = totalPageCount - BASE_PAGE_CNT;
                        int mod = addPageCount % 2;
                        int totalCount = (addPageCount / 2) + mod;

                        value += (eachPageAddPrice * (float) totalCount);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                result = StringUtil.getCurrencyStr(context, value, false);
            }
        }

        return result;
    }

    public static int getAddedPageCount(SnapsTemplate snapsTemplate, int totalPageCount) {
        if (snapsTemplate != null && snapsTemplate.info != null) {
            try {
                final int BASE_QUANTITY_CNT = Integer.parseInt(snapsTemplate.info.F_BASE_QUANTITY);
                final int BASE_PAGE_CNT = (BASE_QUANTITY_CNT * 2) + 1;

                if (totalPageCount > BASE_PAGE_CNT) {
                    if (BASE_QUANTITY_CNT > 0) {
                        return snapsTemplate.getPages().size() - (BASE_QUANTITY_CNT + 2);
                    }
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        return 0;
    }

    public static void initProductEditInfo() {
        Config.setTMPL_COVER(null);
        Config.setTMPL_CODE("");
        Config.setPROD_CODE("");
        Config.setPAPER_CODE("");

        ImageSelectManager.finalizeInstance();
    }

    public static void changeCoverPage(SnapsPage coverPage, SnapsTemplate snapsTemplate, ArrayList<SnapsPage> snapsPages) throws Exception {
        if (coverPage == null || snapsTemplate == null || snapsPages == null || snapsPages.isEmpty())
            return;
        try {
            coverPage.info = snapsTemplate.info;

            for (SnapsControl newControl : coverPage.getLayoutList()) {
                if (newControl != null && newControl instanceof SnapsLayoutControl) {
                    SnapsLayoutControl newLayoutControl = (SnapsLayoutControl) newControl;
                    if (newLayoutControl.type != null && newLayoutControl.type.equalsIgnoreCase("browse_file")) {
                        MyPhotoSelectImageData imageData = newLayoutControl.imgData;
                        if (imageData != null) {
                            imageData.cropRatio = newControl.getRatio();
                        }

                        // 인쇄가능 여부..
                        try {
                            String pageMMWidth = snapsTemplate.info.F_PAGE_MM_WIDTH;
                            String pagePXWidth = snapsTemplate.info.F_PAGE_PIXEL_WIDTH;
                            if (!StringUtil.isEmpty(pageMMWidth) && !StringUtil.isEmpty(pagePXWidth)) {
                                ResolutionUtil.isEnableResolution(Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH), Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH), newLayoutControl);
                            }
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }
                }
            }

            // 새커버에 제목 입력하기..
            for (SnapsControl control : coverPage.getControlList()) {
                if (control instanceof SnapsTextControl) {
                    ((SnapsTextControl) control).text = Config.getPROJ_NAME();
                }
                control.setPageIndex(coverPage.getPageID());
                control.setControlId(-1);
            }

            for (SnapsControl control : coverPage.getLayoutList()) {
                control.setPageIndex(coverPage.getPageID());
                control.setControlId(-1);
            }

            for (SnapsControl control : coverPage.getBgList()) {
                control.setPageIndex(coverPage.getPageID());
                control.setControlId(-1);
            }

            for (SnapsControl control : coverPage.getFormList()) {
                control.setPageIndex(coverPage.getPageID());
                control.setControlId(-1);
            }

            snapsPages.remove(0);
            snapsPages.add(0, coverPage);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void checkPaperInfoFromTemplate(SnapsTemplate template) {
        if (template == null) return;

        try {
            if (StringUtil.isEmpty(Config.getGLOSSY_TYPE()))
                Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);

            if (StringUtil.isEmpty(Config.getPAPER_CODE()))
                Config.setPAPER_CODE(template.info.F_PAPER_CODE);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void saveMaskData(SnapsTemplate template) {
        ArrayList<SnapsPage> pageList = template.getPages();
        ArrayList<SnapsControl> layoutList;
        String name;
        for (int i = 0; i < pageList.size(); ++i) {
            layoutList = pageList.get(i).getLayoutList();
            for (int j = 0; j < layoutList.size(); ++j) {
                name = ((SnapsLayoutControl) layoutList.get(j)).mask;

                if (name != null && name.length() > 0)
                    saveMaskImageFromUrl(name);
            }
        }
    }

    public static void saveMaskImageFromUrl(final String name) {
        if (!isMaskFileExist(name))
            HttpUtil.saveUrlToFile(SnapsAPI.GET_API_MASK_IMAGE() + "&rname=" + name, Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), true) + "/mask/" + name); // 없으면 다운받아서 파일로 쓴 다음에.
//			HttpUtil.saveUrlToFile(SnapsAPI.GET_API_RESOURCE_IMAGE()+ "&rname=" + name, Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), true) + "/mask/" + name); // 없으면 다운받아서 파일로 쓴 다음에.
    }

    public static boolean isMaskFileExist(String fileName) {
        if (fileName == null || fileName.length() < 1)
            return false;

        String filePath = Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), true) + "/mask/" + fileName;
        return new File(filePath).exists();
    }

    public static MyPhotoSelectImageData findFirstIndexOfUploadFailedOrgImageOnList(List<MyPhotoSelectImageData> uploadFailedImageList) throws Exception {
        if (uploadFailedImageList == null || uploadFailedImageList.isEmpty()) return null;
        for (MyPhotoSelectImageData imageData : uploadFailedImageList)
            if (imageData.isUploadFailedOrgImage) return imageData;
        return null;
    }

    public static int findImageDataIndexOnPageList(ArrayList<SnapsPage> _pageList, MyPhotoSelectImageData uploadErrImgData) throws Exception {
        if (_pageList == null || _pageList.isEmpty()) return -1;

        for (int ii = 0; ii < _pageList.size(); ii++) {
            SnapsPage snapsPage = _pageList.get(ii);
            ArrayList<SnapsControl> controls = snapsPage.getLayoutList();
            for (SnapsControl control : controls) {
                if (control != null && control instanceof SnapsLayoutControl) {
                    if (((SnapsLayoutControl) control).imgData == uploadErrImgData) return ii;
                }
            }
        }
        return -1;
    }

    //원본 이미지 업로드를 실패 할 경우 보여지는 아이콘의 플래그 전환
    public static void setUploadFailedIconVisibleStateToShow(SnapsTemplate _template) {
        try {
            if (_template == null || _template.getPages() == null) return;
            SnapsLayoutControl layout = null;
            for (int index = 0; index < _template.getPages().size(); index++) {
                SnapsPage page = _template.getPages().get(index);
                for (int i = 0; i < page.getLayoutList().size(); i++) {
                    layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                    if (layout.imgData != null && layout.imgData.isUploadFailedOrgImage) {
                        layout.isUploadFailedOrgImg = true;
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static List<MyPhotoSelectImageData> getImageDataListInSnapsPage(SnapsPage page) {
        if (page == null) return null;
        List<MyPhotoSelectImageData> removeImageDataList = new ArrayList<>();
        for (SnapsControl control : page.getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {
                removeImageDataList.add(((SnapsLayoutControl) control).imgData);
            }
        }
        return removeImageDataList;
    }

    /**
     * 비율이 이상한 이미지가 있으면 삭제하고 true 반환.
     */
    public static boolean removeWrongImageData(SnapsTemplate template) {
        if (template == null)
            return false;
        try {
            ArrayList<SnapsPage> pageList = template.getPages();
            getImageListFromTemplate(template);

            if (pageList == null || pageList.isEmpty())
                return false;

            for (SnapsPage page : pageList) {
                if (page == null)
                    continue;
                ArrayList<SnapsControl> controls = page.getLayoutList();

                if (controls == null)
                    continue;

                for (SnapsControl c : controls) {
                    if (c != null && c instanceof SnapsLayoutControl) {
                        SnapsLayoutControl layoutControl = (SnapsLayoutControl) c;
                        MyPhotoSelectImageData data = layoutControl.imgData;

                        if (data == null || data.PATH == null || data.PATH.length() < 1)
                            continue;
                        switch (data.KIND) {
                            // Local File
                            case Const_VALUES.SELECT_PHONE:

                                File localFile = new File(data.PATH);
                                if (localFile.exists()) {
                                    // 이미지의 비율이 맞지 않는 이미지 데이터를 삭제 시켜 버린다.
                                    String[] imageRc = layoutControl.getRc().replace(" ", "|").split("\\|");
                                    String[] imageRcClip = layoutControl.getRcClip().replace(" ", "|").split("\\|");

                                    boolean isWrongRatio = false;

                                    imageRc = BitmapUtil.checkImageRatio(data, imageRc, imageRcClip);

                                    if (imageRc != null && imageRc.length >= 4) {
                                        try {
                                            float rectW = Float.parseFloat(imageRc[2]);
                                            float rectH = Float.parseFloat(imageRc[3]);
                                            float imgW = Float.parseFloat(data.F_IMG_WIDTH);
                                            float imgH = Float.parseFloat(data.F_IMG_HEIGHT);
                                            isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
                                            if (!isWrongRatio) {
                                                if ((imageRc[2] != null && imageRc[2].trim().equals("0")) || (imageRc[3] != null && imageRc[3].trim().equals("0"))) {
                                                    isWrongRatio = true;
                                                }
                                            }
                                        } catch (Exception e) {
                                            Dlog.e(TAG, e);
                                            isWrongRatio = true;
                                        }
                                    } else
                                        isWrongRatio = true;

                                    if (isWrongRatio) {
                                        layoutControl.imgData = null;
                                        layoutControl.srcTargetType = Const_VALUE.USERIMAGE_TYPE;

                                        layoutControl.srcTarget = "";
                                        layoutControl.resourceURL = "";
                                        return true;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    /**
     * SnapsControl 재정렬.
     */
    public static final Comparator<SnapsControl> myComparator = new Comparator<SnapsControl>() {
        @Override
        public int compare(SnapsControl p, SnapsControl n) {

            int one = Integer.parseInt(p.regValue);
            int two = Integer.parseInt(n.regValue);

            if (one > two) {
                return 1;
            } else if (one == two) {
                return 0;
            } else
                return -1;
        }
    };

    /***
     * 페이지 들어가 첫번쩨 이미지 인덱스를 구하는 함수..
     *
     * @param templete
     * @param page
     * @return
     */
    public static int getPageImageIndex(SnapsTemplate templete, int page) {
        int index = 0;
        for (int i = 0; i < page; i++) {
            SnapsPage p = templete.getPages().get(i);
            for (SnapsControl con : p.getLayoutList()) {
                if (((SnapsLayoutControl) con).type.equalsIgnoreCase("browse_file"))
                    index++;
            }
        }

        return index;
    }

    /***
     * 페이지와 regValue를 가지고 이미지 인덱스를 구하는 함수...
     *
     * @param page
     * @param regValue
     * @return
     */
    public static int getImageIDX(int page, String regValue) {
        return page * 1000 + MAX_LAYOUT_COUNT - Integer.parseInt(regValue);
    }

    public static String getTextListFromTemplate(SnapsTemplate template) {
        if (template == null)
            return null;

        ArrayList<SnapsPage> pages = template.getPages();
        if (pages == null || pages.isEmpty())
            return null;

        StringBuffer result = new StringBuffer();

        for (int ii = 0; ii < pages.size(); ii++) {

            SnapsPage page = pages.get(ii);
            if (page == null)
                continue;

            ArrayList<SnapsControl> controls = page.getControlList();

            for (int jj = 0; jj < controls.size(); jj++) {
                SnapsControl control = controls.get(jj);
                if (control != null && control instanceof SnapsTextControl) {
                    SnapsTextControl layoutControl = (SnapsTextControl) control;
                    result.append(layoutControl.text);
                }
            }
        }

        return result.toString();
    }

    public static String getDiaryPublishTextListFromTemplate(SnapsTemplate template) {
        if (template == null)
            return null;

        ArrayList<SnapsPage> pages = template.getPages();
        if (pages == null || pages.isEmpty())
            return null;

        StringBuffer result = new StringBuffer();

        for (int ii = 0; ii < pages.size(); ii++) {

            SnapsPage page = pages.get(ii);
            if (page == null)
                continue;

            ArrayList<SnapsControl> controls = page.getControlList();

            for (int jj = 0; jj < controls.size(); jj++) {
                SnapsControl control = controls.get(jj);
                if (control != null && control instanceof SnapsTextControl) {
                    SnapsTextControl layoutControl = (SnapsTextControl) control;
                    result.append(layoutControl.textForDiaryPublish);
                }
            }
        }

        return result.toString();
    }

    public static ArrayList<MyPhotoSelectImageData> getImageListFromPage(SnapsPage page) {
        if (page == null)
            return null;

        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();
        ArrayList<SnapsControl> controls = page.getLayerLayouts();
        int key;

        for (int i = 0; i < controls.size(); i++) {
            SnapsControl control = controls.get(i);
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                if (layoutControl.type == null || !layoutControl.type.equalsIgnoreCase("browse_file"))
                    continue;

                key = (i * 1000) + i;
                layoutControl.setImageDataKey(key);
                MyPhotoSelectImageData imgData = layoutControl.imgData;
                if (imgData != null) {
                    imgData.setImageDataKey(key);
                    imageList.add(imgData);
                }
            }
        }

        return imageList;
    }

    public static ArrayList<MyPhotoSelectImageData> getImageListFromTemplate(SnapsTemplate template) {
        return getImageListFromTemplate(template, 0);
    }

    public static ArrayList<MyPhotoSelectImageData> getImageListFromTemplate(SnapsTemplate template, int startPageIdx) {
        if (template == null)
            return null;
        return getImageListFromPageList(template.getPages(), startPageIdx);
    }

    public static ArrayList<MyPhotoSelectImageData> getImageListFromPageList(ArrayList<SnapsPage> pages, int startPageIdx) {
        if (pages == null || pages.isEmpty())
            return null;

        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();

        double key = 0;

        for (int ii = startPageIdx; ii < pages.size(); ii++) {

            SnapsPage page = pages.get(ii);
            if (page == null)
                continue;

            ArrayList<SnapsControl> controls = page.getLayerLayouts();

            for (int jj = 0; jj < controls.size(); jj++) {
                SnapsControl control = controls.get(jj);
                if (control != null && control instanceof SnapsLayoutControl) {
                    SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                    if (layoutControl.type == null || !layoutControl.type.equalsIgnoreCase("browse_file"))
                        continue;

                    key = (ii * 1000) + jj;
                    layoutControl.setImageDataKey(key);
                    MyPhotoSelectImageData imgData = layoutControl.imgData;

                    if (imgData != null) {
                        imgData.isNoPrint = layoutControl.isNoPrintImage;
                        imgData.setImageDataKey(key);
                        imageList.add(imgData);
                    }
                }
            }
        }

        return imageList;
    }

    public static ArrayList<MyPhotoSelectImageData> getCoverImageListFromTemplate(SnapsTemplate template) {
        if (template == null)
            return null;

        ArrayList<SnapsPage> pages = template.getPages();
        if (pages == null || pages.isEmpty())
            return null;

        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();

        double key = 0;

        if (pages.size() > 0) {
            int ii = 0;
            SnapsPage page = pages.get(ii);
            ArrayList<SnapsControl> controls = page.getLayerLayouts();
            for (int jj = 0; jj < controls.size(); jj++) {
                SnapsControl control = controls.get(jj);
                if (control != null && control instanceof SnapsLayoutControl) {
                    SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                    if (layoutControl.type == null || !layoutControl.type.equalsIgnoreCase("browse_file"))
                        continue;

                    key = (ii * 1000) + jj;
                    layoutControl.setImageDataKey(key);
                    MyPhotoSelectImageData imgData = layoutControl.imgData;
                    if (imgData != null) {
                        imgData.setImageDataKey(key);
                        imageList.add(imgData);
                    }
                }
            }
        }

        return imageList;
    }


    /**
     * CS 대응
     * 커버 이미지가 아닌 전체 템플릿의 사진 정보를 구한다.
     * 이 메소드는 getCoverImageListFromTemplate 복사본이다.
     * CS 대응 용도 이므로 원본 코드와 합쳐서 구현한다거나 그런것은 하지 않는다.
     *
     * @param template
     * @return
     */
    //CS 대응
    public static ArrayList<MyPhotoSelectImageData> getAllImageListFromTemplate(SnapsTemplate template) {
        if (template == null)
            return null;

        ArrayList<SnapsPage> pages = template.getPages();
        if (pages == null || pages.isEmpty())
            return null;

        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();

        double key = 0;

        if (pages.size() > 0) {
            for (int ii = 0; ii < pages.size(); ii++) {
                SnapsPage page = pages.get(ii);
                ArrayList<SnapsControl> controls = page.getLayerLayouts();
                for (int jj = 0; jj < controls.size(); jj++) {
                    SnapsControl control = controls.get(jj);
                    if (control != null && control instanceof SnapsLayoutControl) {
                        SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                        if (layoutControl.type == null || !layoutControl.type.equalsIgnoreCase("browse_file"))
                            continue;

                        key = (ii * 1000) + jj;
                        layoutControl.setImageDataKey(key);
                        MyPhotoSelectImageData imgData = layoutControl.imgData;
                        if (imgData != null) {
                            imgData.setImageDataKey(key);
                            imageList.add(imgData);
                        }
                    }
                }
            }
        }

        return imageList;
    }


    public static ArrayList<MyPhotoSelectImageData> getMyPhotoSelectImageData(SnapsTemplate _template) {
        ArrayList<MyPhotoSelectImageData> _imgList = PhotobookCommonUtils.getImageListFromTemplate(_template);

        if (_imgList == null || _imgList.isEmpty())
            return new ArrayList<>();
        // 순서대로 데이터를 정리한다.
        // 시간순으로 정렬하기
        // 빈칸때문에 예외처리..
        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();
        for (MyPhotoSelectImageData data : _imgList) {
            if (data != null)
                imageList.add(data);
        }

        Collections.sort(imageList, new PhotobookCommonUtils.ImageCompare());

        ArrayList<MyPhotoSelectImageData> returnData = new ArrayList<MyPhotoSelectImageData>();

        for (MyPhotoSelectImageData d : imageList) {
            if (d == null)
                continue;

            if (d.KIND == Const_VALUES.SELECT_PHONE || d.KIND == Const_VALUES.SELECT_FACEBOOK || d.KIND == Const_VALUES.SELECT_UPLOAD || d.KIND == Const_VALUES.SELECT_KAKAO
                    || d.KIND == Const_VALUES.SELECT_SDK_CUSTOMER || d.KIND == Const_VALUES.SELECT_BETWEEN || d.KIND == Const_VALUES.SELECT_INSTAGRAM) {
                d.isModify = -1; // 수정여부 초기화
                returnData.add(d);
            }
        }

        return returnData;
    }

    /**
     * 스티커킷은 같은 사진이 반복적으로 들어 가 있다..
     */
    public static void setImageDataKey(ArrayList<MyPhotoSelectImageData> galleryList) {
        if (galleryList == null || galleryList.isEmpty()) return;

        for (int ii = 0; ii < galleryList.size(); ii++) {
            MyPhotoSelectImageData imgData = galleryList.get(ii);
            if (imgData == null) continue;
            imgData.setImageDataKey(imgData.IMAGE_ID);
        }
    }

    /**
     * 증명사진은 사진 한장을 템플릿 모든 사진 레이어에 적용 한다
     */
    public static void imageRangeForIdentifyPhoto(SnapsTemplate template, ArrayList<MyPhotoSelectImageData> _imageList) {

        if (template == null || template.getPages() == null || _imageList == null || _imageList.isEmpty())
            return;

        MyPhotoSelectImageData imgData = _imageList.get(0);
        if (imgData == null) return;

        SnapsLayoutControl layout = null;

        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            // 리소스 순으로 정렬 후 뒤집어 줘야 스티커가 레이어 위로 올라온다..-_-;
            Collections.sort(page.getLayoutList(), myComparator);
            Collections.reverse(page.getLayoutList());
            int imgIndex = getPageImageIndex(template, index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.type.equalsIgnoreCase("browse_file")) {
                    imgData.pageIDX = index;
                    if (!imgData.PATH.equalsIgnoreCase("")) {
                        imgData.cropRatio = layout.getRatio();
                        imgData.IMG_IDX = getImageIDX(index, layout.regValue);
                        layout.imgData = new MyPhotoSelectImageData();
                        layout.imgData.set(imgData);
                        layout.angle = String.valueOf(imgData.ROTATE_ANGLE);
                        layout.imagePath = imgData.PATH;
                        layout.imageLoadType = imgData.KIND;
                        layout.imgData.mmPageWidth = Integer.parseInt(template.info.F_PAGE_MM_WIDTH);
                        layout.imgData.pxPageWidth = Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH);
                        layout.imgData.controlWidth = layout.width;

                        // 인쇄가능 여부..
                        try {
                            int wdith = 0;
                            int height = 0;
                            if (Integer.parseInt(layout.imgData.F_IMG_WIDTH) >= Integer.parseInt(layout.imgData.F_IMG_HEIGHT)) {
                                wdith = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                                height = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                            } else {
                                wdith = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                                height = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                            }

                            Rect resolutionRect = ResolutionUtil.getEnableResolution(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_PIXEL_WIDTH, layout);
                            int resolutionWdith = resolutionRect.right;
                            int resolutionHeight = resolutionRect.bottom;
                            if (wdith < resolutionWdith || height < resolutionHeight) {
                                layout.isNoPrintImage = true;
                            }
//							ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }

                    imgIndex++;
                }
            }
        }

        PhotobookCommonUtils.getImageListFromTemplate(template);
    }


    //Ben : 복사... ㅡㅡ;;
    public static void imageRangeForSealSticker(SnapsTemplate template, ArrayList<MyPhotoSelectImageData> _imageList) {
        if (template == null || template.getPages() == null || _imageList == null || _imageList.isEmpty())
            return;
        Dlog.d("imageRangeForSealSticker() imageList.size:" + _imageList.size());
        SnapsLayoutControl layout = null;

        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            // 리소스 순으로 정렬 후 뒤집어 줘야 스티커가 레이어 위로 올라온다..-_-;
            Collections.sort(page.getLayoutList(), myComparator);
            Collections.reverse(page.getLayoutList());
            int imgIndex = getPageImageIndex(template, index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                MyPhotoSelectImageData imgData;

                //이게 핵심
                if (layout.isForBackground()) {
                    continue;
                }

                if (layout.type.equalsIgnoreCase("browse_file")) {

                    // 이미지 갯수가 부족하면 pass한다.
                    if (_imageList.size() <= imgIndex)
                        continue;

                    imgData = _imageList.get(imgIndex);

                    if (imgData != null) {
                        imgData.pageIDX = index;
                        if (!imgData.PATH.equalsIgnoreCase("")) {
                            imgData.cropRatio = layout.getRatio();
                            imgData.IMG_IDX = getImageIDX(index, layout.regValue);
                            layout.imgData = imgData;
                            layout.angle = String.valueOf(imgData.ROTATE_ANGLE);
                            layout.imagePath = imgData.PATH;
                            layout.imageLoadType = imgData.KIND;
                            layout.imgData.mmPageWidth = StringUtil.isEmpty(template.info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(template.info.F_PAGE_MM_WIDTH);
                            layout.imgData.pxPageWidth = StringUtil.isEmpty(template.info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH);
                            layout.imgData.controlWidth = layout.width;

                            // 인쇄가능 여부..
                            try {
                                if (Config.isIdentifyPhotoPrint()) {
                                    int wdith = 0;
                                    int height = 0;
                                    if (Integer.parseInt(layout.imgData.F_IMG_WIDTH) >= Integer.parseInt(layout.imgData.F_IMG_HEIGHT)) {
                                        wdith = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                                        height = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                                    } else {
                                        wdith = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                                        height = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                                    }

                                    Rect resolutionRect = ResolutionUtil.getEnableResolution(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_PIXEL_WIDTH, layout);
                                    int resolutionWdith = resolutionRect.right;
                                    int resolutionHeight = resolutionRect.bottom;
                                    if (wdith < resolutionWdith || height < resolutionHeight) {
                                        layout.isNoPrintImage = true;
                                    }

                                } else {
                                    ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    }

                    imgIndex++;
                }
            }
        }

        PhotobookCommonUtils.getImageListFromTemplate(template);
    }

    public static void imageRange(SnapsTemplate template, ArrayList<MyPhotoSelectImageData> _imageList) {

        if (template == null || template.getPages() == null || _imageList == null || _imageList.isEmpty())
            return;
        Dlog.d("imageRange() imageList.size:" + _imageList.size());
        SnapsLayoutControl layout = null;

        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            // 리소스 순으로 정렬 후 뒤집어 줘야 스티커가 레이어 위로 올라온다..-_-;
            Collections.sort(page.getLayoutList(), myComparator);
            Collections.reverse(page.getLayoutList());
            int imgIndex = getPageImageIndex(template, index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                MyPhotoSelectImageData imgData;

                if (layout.type.equalsIgnoreCase("browse_file")) {

                    // 이미지 갯수가 부족하면 pass한다.
                    if (_imageList.size() <= imgIndex)
                        continue;

                    imgData = _imageList.get(imgIndex);

                    if (imgData != null) {
                        imgData.pageIDX = index;
                        if (!imgData.PATH.equalsIgnoreCase("")) {
                            imgData.cropRatio = layout.getRatio();
                            imgData.IMG_IDX = getImageIDX(index, layout.regValue);
                            layout.imgData = imgData;
                            layout.angle = String.valueOf(imgData.ROTATE_ANGLE);
                            layout.imagePath = imgData.PATH;
                            layout.imageLoadType = imgData.KIND;
                            layout.imgData.mmPageWidth = StringUtil.isEmpty(template.info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(template.info.F_PAGE_MM_WIDTH);
                            layout.imgData.pxPageWidth = StringUtil.isEmpty(template.info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH);
                            layout.imgData.controlWidth = layout.width;

                            // 인쇄가능 여부..
                            try {
                                if (Config.isIdentifyPhotoPrint()) {
                                    int wdith = 0;
                                    int height = 0;
                                    if (Integer.parseInt(layout.imgData.F_IMG_WIDTH) >= Integer.parseInt(layout.imgData.F_IMG_HEIGHT)) {
                                        wdith = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                                        height = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                                    } else {
                                        wdith = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                                        height = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                                    }

                                    Rect resolutionRect = ResolutionUtil.getEnableResolution(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_PIXEL_WIDTH, layout);
                                    int resolutionWdith = resolutionRect.right;
                                    int resolutionHeight = resolutionRect.bottom;
                                    if (wdith < resolutionWdith || height < resolutionHeight) {
                                        layout.isNoPrintImage = true;
                                    }

                                } else {
                                    ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    }

                    imgIndex++;
                }
            }
        }

        PhotobookCommonUtils.getImageListFromTemplate(template);
    }

    public static void imageRange2(SnapsTemplate template) {
        imageRange2(template, false);

    }

    public static SnapsControl getSnapsControl(Activity activity, int control_id) {
        if (activity == null) return null;
        View v = activity.findViewById(control_id);
        if (v == null) return null;

        SnapsControl control = PhotobookCommonUtils.getSnapsControlFromView(v);
        Dlog.d("getSnapsControl() snapsControl:" + control);
        return control;
    }

    /***
     * 간편만들기에서 내지가 편집이 된경우 편집 정보를 초기화 한다.
     *
     * @param template
     * @param isInitModify
     */
    public static void imageRange2(SnapsTemplate template, boolean isInitModify) {
        SnapsLayoutControl layout = null;
        ;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.imgData != null) {
                    layout.imgData.pageIDX = index;
                    layout.imgData.IMG_IDX = getImageIDX(index, layout.regValue);
                    layout.imgData.mmPageWidth = Integer.parseInt(template.info.F_PAGE_MM_WIDTH);
                    layout.imgData.pxPageWidth = Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH);
                    layout.imgData.controlWidth = layout.width;

                    try {
                        ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                    if (index > 0 && isInitModify) {
                        layout.imgData.ADJ_CROP_INFO = null;
                        layout.imgData.isAdjustableCropMode = false;
                    }
                }
            }
        }
    }

    public static void imageRange(SnapsTemplate template) {
        SnapsLayoutControl layout = null;
        ;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.imgData != null) {
                    layout.angle = String.valueOf(layout.imgData.ROTATE_ANGLE);
                    layout.imgData.cropRatio = layout.getRatio();
                    layout.imgData.pageIDX = index;
                    layout.imgData.IMG_IDX = getImageIDX(index, layout.regValue);
                    layout.imgData.controlWidth = layout.width;
                    layout.imagePath = layout.imgData.PATH;
                    layout.imageLoadType = layout.imgData.KIND;
                    layout.imgData.mmPageWidth = StringUtil.isEmpty(template.info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(template.info.F_PAGE_MM_WIDTH);
                    layout.imgData.pxPageWidth = StringUtil.isEmpty(template.info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH);

                    try {
                        ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    public static boolean refreshPagesId(ArrayList<SnapsPage> pageList) throws Exception {
        if (pageList == null)
            return false;

        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage page = pageList.get(ii);
            page.setPageID(ii);

            try {
                ArrayList<SnapsControl> layoutContorls = page.getLayoutList();
                if (layoutContorls != null) {
                    for (SnapsControl control : layoutContorls) {
                        if (control != null) {
                            control.setPageIndex(ii);
                        }
                    }
                }
                ArrayList<SnapsControl> controls = page.getLayerControls();
                if (controls != null) {
                    for (SnapsControl control : controls) {
                        if (control != null) {
                            control.setPageIndex(ii);
                        }
                    }
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        return true;
    }

    public static void copyTextControl(SnapsPage targetPage, ArrayList<SnapsPage> orgSnapsPage) throws Exception {
        if (targetPage == null || orgSnapsPage == null) return;
        ArrayList<SnapsControl> targetControls = targetPage.getControlList();
        if (targetControls == null) return;

        for (SnapsPage newPage : orgSnapsPage) {
            if (newPage == null) continue;
            if (newPage.getPageID() == targetPage.getPageID()) {
                ArrayList<SnapsControl> newControls = newPage.getControlList();
                for (int ii = newControls.size() - 1; ii >= 0; ii--) {
                    SnapsControl c = newControls.get(ii);
                    if (c instanceof SnapsTextControl) {
                        targetControls.add(c);
                        newControls.remove(c);
                    }
                }
            }
        }
    }

    /***
     * 인화불가 판정하는 함수...
     *
     * @param template
     */
    public static void imageResolutionCheck(SnapsTemplate template) {
        if (template == null || template.getPages() == null) return;
        SnapsLayoutControl layout = null;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.imgData != null) {
                    // 인쇄가능 여부..
                    try {
                        layout.isNoPrintImage = ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    public static boolean isContainLowResolutionImageOnPages(ArrayList<SnapsPage> pageList) {
        if (pageList == null)
            return false;

        try {
            for (SnapsPage page : pageList) {
                // 커버인경우..
                for (SnapsControl control : page.getLayoutList()) {
                    // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
                    if (control instanceof SnapsLayoutControl) {
                        if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                            continue;

                        if (((SnapsLayoutControl) control).isNoPrintImage)
                            return true;
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return false;
    }

    public static void imageResolutionCheckForIdentifyPhotoPrint(SnapsTemplate template) {
        SnapsLayoutControl layout = null;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.imgData != null) {
                    // 인쇄가능 여부..
                    try {
                        int wdith = 0;
                        int height = 0;
                        if (Integer.parseInt(layout.imgData.F_IMG_WIDTH) >= Integer.parseInt(layout.imgData.F_IMG_HEIGHT)) {
                            wdith = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                            height = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                        } else {
                            wdith = Integer.parseInt(layout.imgData.F_IMG_HEIGHT);
                            height = Integer.parseInt(layout.imgData.F_IMG_WIDTH);
                        }

                        Rect resolutionRect = ResolutionUtil.getEnableResolution(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_PIXEL_WIDTH, layout);
                        int resolutionWdith = resolutionRect.right;
                        int resolutionHeight = resolutionRect.bottom;
                        if (wdith < resolutionWdith || height < resolutionHeight) {
                            layout.isNoPrintImage = true;
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    /***
     * 텍스트 변경, 커버변경 버튼 삭제하는 버튼...
     */
    public static void removeEditButton(ImageView mThemeTextModify, ImageView mThemeCoverModify) {
        if (mThemeTextModify != null)
            mThemeTextModify.setVisibility(View.GONE);
        if (mThemeCoverModify != null)
            mThemeCoverModify.setVisibility(View.GONE);
    }

    /**
     * Progress Popup 끝내기.
     */
    public static void progressUnload(Context context) {
        SnapsTimerProgressView.destroyProgressView();
    }

    public static void pageProgressUnload(DialogDefaultProgress pageProgress) {
        try {
            if (pageProgress != null)
                pageProgress.dismiss();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void setImageDataScaleable(SnapsTemplate template) {
        SnapsLayoutControl layout = null;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                if (layout.imgData != null) {
                    layout.imgData.isNoPrint = layout.isNoPrintImage;
                }
            }
        }
    }

    /***
     * 이미지 인텍스를 구하는 함수..
     *
     * @param images
     * @return
     */
    public static int getImageIndex(Activity act, ArrayList<MyPhotoSelectImageData> images, int tempImageViewID) {
        View imgView = (View) act.findViewById(tempImageViewID);
        if (imgView == null)
            return -1;

        SnapsControl control = PhotobookCommonUtils.getSnapsControlFromView(imgView);
        int index = 0;
        if (control instanceof SnapsLayoutControl) {
            for (MyPhotoSelectImageData d : images) {
                if (d == ((SnapsLayoutControl) control).imgData)
                    return index;
                index++;
            }

        }

        return 0;
    }

    /***
     * 이미지를 순서에 맞게 정렬하는 클래스
     *
     * @author asmera
     *
     */
    public static class ImageCompare implements Comparator<MyPhotoSelectImageData> {

        @Override
        public int compare(MyPhotoSelectImageData lhs, MyPhotoSelectImageData rhs) {
            double first = lhs.getImageDataKey();
            double second = rhs.getImageDataKey();

            if (first > second) {
                return 1;
            } else if (first == second) {
                return 0;
            } else
                return -1;
        }
    }

    /***
     *
     * @return
     */
    public static MyPhotoSelectImageData getMyPhotoSelectImageDataWithImgIdx(ArrayList<MyPhotoSelectImageData> _imageList, double imgKey) {
        if (_imageList == null)
            return null;

        for (MyPhotoSelectImageData data : _imageList) {
            if (data == null)
                continue;
            if (data.getImageDataKey() == imgKey)
                return data;
        }
        return null;
    }

    public static void updateUI(Activity activity) {
        if (Config.isCalendar()) {
            FontUtil fontUtil = FontUtil.getInstance();
            List<String> fontList = fontUtil.getTextListFontName();
            if (fontList == null) return;

            Const_VALUE.sTypefaceMap = new HashMap<>();
            Const_VALUE.sNotDefineFontList = new ArrayList<>();

            for (String fontName : fontList) {
                String remoteFontFileName = fontUtil.findFontFile(activity, fontName);
                if (TextUtils.isEmpty(remoteFontFileName)) {
                    Dlog.d(Dlog.PRE_FIX_FONT + "폰트 키가 정의되어 있지 않습니다. " + fontName);
                    Const_VALUE.sNotDefineFontList.add(fontName);
                    continue;
                }

                String sdPath = Const_VALUE.PATH_PACKAGE(activity, false) + "/font/" + remoteFontFileName;
                try {
                    Typeface typeface = Typeface.createFromFile(sdPath);
                    Const_VALUE.sTypefaceMap.put(fontName, typeface);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    public static boolean processFontDownloading(Activity act) {
        /*
         * if (!isFont()) return startDownload();
         */
        // Const_VALUE.SNAPS_TYPEFACE_NAME
        if (Config.isCalendar()) {
            FontUtil fontUtil = FontUtil.getInstance();
            List<String> fontNameList = fontUtil.getTextListFontName();
            if (fontNameList == null) {
                if (AutoSaveManager.isAutoSaveRecoveryMode()) return true;
                return false;
            }

            for (String fontName : fontNameList) {
                if (fontName == null) {
                    continue;
                }

                if (isFont(act, fontName)) {
                    continue;
                }

                startDownload(act, fontName);
            }

            /*
            String dayFont = ret.get("day");
            if (dayFont != null && !isFont(act, dayFont))
                startDownload(act, dayFont);
            String dayTitleFont = ret.get("day_title");
            if (dayTitleFont != null && !isFont(act, dayTitleFont))
                startDownload(act, dayTitleFont);

            String monthFont = ret.get("month");
            if (monthFont != null && !isFont(act, monthFont))
                startDownload(act, monthFont);

            String monthTitleFont = ret.get("month_title");
            if (monthTitleFont != null && !isFont(act, monthTitleFont))
                startDownload(act, monthTitleFont);

            String yearFont = ret.get("year");
            if (yearFont != null && !isFont(act, yearFont))
                startDownload(act, yearFont);

            String textlistFont = ret.get("textlist");
            Logg.d("textlistFont", textlistFont);
            if (textlistFont != null && !isFont(act, textlistFont))
                startDownload(act, textlistFont);

            String dayFrontFont = ret.get("day_front");
            if (dayFrontFont != null && !isFont(act, dayFrontFont))
                startDownload(act, dayFrontFont);

            String monthFrontFont = ret.get("month_front");
            if (monthFrontFont != null && !isFont(act, monthFrontFont))
                startDownload(act, monthFrontFont);

            String monthtitleFrontFont = ret.get("monthtitle_front");
            if (monthtitleFrontFont != null && !isFont(act, monthtitleFrontFont))
                startDownload(act, monthtitleFrontFont);

            String yearFrontFont = ret.get("year_front");
            if (yearFrontFont != null && !isFont(act, yearFrontFont))
                startDownload(act, yearFrontFont);
                */

        } else if (!Config.useKorean()) {
            if (!isFont(act))
                return startDownload(act);
        } else {
            if (!isFont(act))
                return startDownload(act);

        }
        return true;

    }

    public static boolean isFont(Activity activity) {
        String sdPath = Const_VALUE.PATH_PACKAGE(activity, false) + "/font";
        File file = new File(sdPath, "SNAPS_YGO33.ttf");

        if (Config.useEnglish())
            file = new File(FontUtil.FONT_FILE_PATH(activity) + FontUtil.TEMPLATE_FONT_NAME_ROBOTO_EN[FontUtil.FONT_NAME]);
        else if (Config.useChinese())
            file = new File(FontUtil.FONT_FILE_PATH(activity) + FontUtil.TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_SC_CH[FontUtil.FONT_NAME]);
        else if (Config.useJapanese())
            file = new File(FontUtil.FONT_FILE_PATH(activity) + FontUtil.TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_JA[FontUtil.FONT_NAME]);

        boolean ret = file.exists();

        return ret;

    }

    public static boolean isPortraitScreenProduct() {
        return Const_PRODUCT.isWoodBlockProduct() || Const_PRODUCT.isTtabujiProduct() || Const_PRODUCT.isSquareProduct() || Const_PRODUCT.isPolaroidPackProduct() || Const_PRODUCT.isNewPolaroidPackProduct() || SnapsDiaryDataManager.isAliveSnapsDiaryService();
    }

    public static boolean isFont(Activity activity, String fontFace) {

        String name = "";

        FontUtil fontUtil = FontUtil.getInstance();
        if (fontUtil != null) {
            name = fontUtil.findFontFile(activity, fontFace);
        }

        if (name != null && name.length() > 0) {
            String sdPath = Const_VALUE.PATH_PACKAGE(activity, false) + "/font";

            File file = new File(sdPath, name);

            return file.exists();
        }

        return false;
    }

    public static boolean startDownload(Context context, String fontFace) {

        String name = "";
        FontUtil fontUtil = FontUtil.getInstance();
        if (fontUtil != null) {
            name = fontUtil.findFontFile(context, fontFace);
        }

        if (name == null || name.length() < 1) return false;

        String url = FontUtil.FONT_DOWNLOAD_BASE_URL + name;
        DownloadFileAsync down = new DownloadFileAsync(context, name, url);

        return down.syncProcess();
    }

    public static boolean startDownload(Context context) {
        // TODO 만약 달력처럼 여러가지 폰트가 적용된다면, 자동 저장 기능 로딩 할때 다른 상품군도 템플릿 로딩을 해야함.

        // 글로벌로 추가. 기존 코드가 하드코딩 되어있고 왜인지 한글 폰트는 mFont에 있고 Roboto는 Font에 있고 경로가 다르다. 새로 추가된건 mobileUiFont.  /
        String url, fileName;
        if (Config.useEnglish()) {
            url = FontUtil.FONT_DOWNLOAD_BASE_URL + FontUtil.TEMPLATE_FONT_NAME_ROBOTO_EN[FontUtil.FONT_PATH];
            fileName = FontUtil.TEMPLATE_FONT_NAME_ROBOTO_EN[FontUtil.FONT_NAME];
        } else if (Config.useChinese()) {
            url = SnapsAPI.MENU_FONT_DOWNLOAD_PATH() + FontUtil.TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_SC_CH[FontUtil.FONT_PATH];
            fileName = FontUtil.TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_SC_CH[FontUtil.FONT_NAME];
        } else if (Config.useJapanese()) {
            url = SnapsAPI.MENU_FONT_DOWNLOAD_PATH() + FontUtil.TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_JA[FontUtil.FONT_PATH];
            fileName = FontUtil.TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_JA[FontUtil.FONT_NAME];
        } else {
            url = SnapsAPI.DOMAIN() + "/Upload/Data1/Resource/mFont/snaps_YGO33.ttf";
            ;
            fileName = "SNAPS_YGO33.ttf";
        }

        DownloadFileAsync down = new DownloadFileAsync(context, fileName, url);
        return down.syncProcess();
    }

    public static ArrayList<SnapsLayoutControl> getLayoutControlsInSnapsPage(SnapsPage _snapsPage) {
        if (_snapsPage == null) return null;

        ArrayList<SnapsLayoutControl> result = new ArrayList<>();
        for (SnapsControl control : _snapsPage.getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {
                SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                if (layoutControl.type.equalsIgnoreCase("browse_file")) {
                    result.add(layoutControl);
                }
            }
        }

        return result;
    }

    public static int findEmptyPageIdxWithPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null)
            return -1;

        int pageIDX = 0;
        for (SnapsPage page : pageList) {
            for (SnapsControl control : page.getLayoutList()) {

                // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
                if (control instanceof SnapsLayoutControl) {
                    if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                        continue;

                    if (((SnapsLayoutControl) control).regName.equalsIgnoreCase(""))
                        continue;

                    if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file") && ((SnapsLayoutControl) control).imgData == null) {
                        return pageIDX;
                    }
                }
            }

            pageIDX++;
        }

        return -1;
    }

    public static boolean isEmptyPageIdxWithPage(SnapsPage snapsPage) {
        if (snapsPage == null)
            return false;

        for (SnapsControl control : snapsPage.getLayoutList()) {

            // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
            if (control instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                    continue;

                if (((SnapsLayoutControl) control).regName.equalsIgnoreCase(""))
                    continue;

                if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file") && ((SnapsLayoutControl) control).imgData == null) {
                    return true;
                }
            }
        }

        return false;
    }

    public static SnapsLayoutControl findEmptyLayoutControlWithPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null)
            return null;

        for (SnapsPage page : pageList) {
            for (SnapsControl control : page.getLayoutList()) {
                // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
                if (control instanceof SnapsLayoutControl) {
                    if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                        continue;

                    if (((SnapsLayoutControl) control).regName.equalsIgnoreCase(""))
                        continue;

                    if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file") && ((SnapsLayoutControl) control).imgData == null) {
                        return ((SnapsLayoutControl) control);
                    }
                }
            }
        }

        return null;
    }

    public static SnapsLayoutControl findContainLowResolutionLayoutControlWithPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null)
            return null;

        for (SnapsPage page : pageList) {
            for (SnapsControl control : page.getLayoutList()) {
                // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
                if (control instanceof SnapsLayoutControl) {
                    if (((SnapsLayoutControl) control).isNoPrintImage)
                        return ((SnapsLayoutControl) control);
                }
            }
        }

        return null;
    }

    public static SnapsLayoutControl findLayoutControlWithPageList(ArrayList<SnapsPage> pageList, int pageIndex) {
        if (pageList == null || pageList.size() <= pageIndex)
            return null;

        SnapsPage page = pageList.get(pageIndex);
        for (SnapsControl control : page.getLayoutList()) {
            // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
            if (control instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                    continue;

                if (((SnapsLayoutControl) control).regName.equalsIgnoreCase(""))
                    continue;

                if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file")) {
                    return ((SnapsLayoutControl) control);
                }
            }
        }

        return null;
    }

    public static SnapsLayoutControl findEmptyCoverLayoutControlWithPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null)
            return null;

        for (SnapsPage page : pageList) {
            if (page.type.equals("cover")) {
                for (SnapsControl control : page.getLayoutList()) {

                    // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
                    if (control instanceof SnapsLayoutControl) {
                        if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                            continue;

                        if (((SnapsLayoutControl) control).regName.equalsIgnoreCase(""))
                            continue;

                        if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file") && ((SnapsLayoutControl) control).imgData == null) {
                            return ((SnapsLayoutControl) control);
                        }
                    }
                }
                for (SnapsControl control : page.getLayoutList()) {

                    // 커버인경우 배경레이아웃이 있는경우 사진데이터가 없는경우 편집중인걸로 판단한다.
                    if (control instanceof SnapsLayoutControl) {
                        if (((SnapsLayoutControl) control).type.equalsIgnoreCase("local_resource"))
                            continue;

                        if (((SnapsLayoutControl) control).regName.equalsIgnoreCase(""))
                            continue;

                        if (((SnapsLayoutControl) control).type.equalsIgnoreCase("browse_file")) {
                            return ((SnapsLayoutControl) control);
                        }
                    }
                }

            }
        }

        return null;
    }


    public static SnapsTextControl findWrittenTextControlWithSnapsPage(SnapsPage page) {
        if (page == null) return null;

        for (SnapsControl control : page.getTextControlList()) {
            if (control != null && control instanceof SnapsTextControl) {
                SnapsTextControl textControl = (SnapsTextControl) control;
                if (textControl.type != null && textControl.type.equalsIgnoreCase("calendar") || StringUtil.isEmpty(textControl.text))
                    continue;
                return textControl;
            }
        }
        return null;
    }

    public static SnapsLayoutControl findFirstOrLastLayoutControlWithPageList(ArrayList<SnapsPage> pageList, boolean first) {
        if (pageList == null)
            return null;
        SnapsControl control = null;
        if (first) {
            control = pageList.get(0).getLayerLayouts().get(0);
        } else {
            control = pageList.get(pageList.size() - 1).getLayerLayouts().get(pageList.get(pageList.size() - 1).getLayerLayouts().size() - 2);
        }
        return ((SnapsLayoutControl) control);
    }

    public static ArrayList<String> getBasePageThumbnailPathsFromPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null || pageList.isEmpty())
            return null;
        ArrayList<String> paths = new ArrayList<String>();

        for (SnapsPage page : pageList) {
            if (page == null)
                continue;
            paths.add(page.thumbnailPath);
        }

        return paths;
    }

    public static boolean isCreatedThumbnailPageOnPageList(ArrayList<SnapsPage> pageList, int idx) throws Exception {
        if (idx < 0 || pageList == null || pageList.size() <= idx) return false;
        SnapsPage snapsPage = pageList.get(idx);
        return (snapsPage != null
                && snapsPage.isMakedPageThumbnailFile
                && snapsPage.thumbnailPath != null
                && snapsPage.thumbnailPath.length() > 0);
    }

    public static boolean isAlreadyCreatedThumbnail(List<SnapsPage> pageList, int idx) {
        if (idx < 0 || pageList == null || pageList.size() <= idx) return false;
        SnapsPage snapsPage = pageList.get(idx);
        return snapsPage != null && snapsPage.isMakedPageThumbnailFile;
    }

    public static void changePageThumbnailState(List<SnapsPage> pageList, int idx, boolean isMaked) {
        if (idx < 0 || pageList == null || pageList.size() <= idx) return;

        SnapsPage snapsPage = pageList.get(idx);
        if (snapsPage == null || snapsPage.thumbnailPath == null || snapsPage.thumbnailPath.length() < 1)
            isMaked = false;

        if (snapsPage != null)
            snapsPage.isMakedPageThumbnailFile = isMaked;
    }

    public static ArrayList<MyPhotoSelectImageData> getMyPhotoSelectImageDataWithTemplate(SnapsTemplate snapsTemplate, IPhotobookCommonConstants.eImageDataRequestType requestType) {
        ArrayList<MyPhotoSelectImageData> _imgList = null;
        switch (requestType) {
            case ALL:
                _imgList = PhotobookCommonUtils.getImageListFromTemplate(snapsTemplate);
                break;
            case ALL_EXCEPT_COVER:
                _imgList = PhotobookCommonUtils.getImageListFromTemplate(snapsTemplate, 1);
                break;
            case ONLY_COVER:
                _imgList = PhotobookCommonUtils.getCoverImageListFromTemplate(snapsTemplate);
                break;
        }

        if (_imgList == null || _imgList.isEmpty())
            return new ArrayList<MyPhotoSelectImageData>();
        // 순서대로 데이터를 정리한다.
        // 시간순으로 정렬하기
        // 빈칸때문에 예외처리..
        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();
        for (MyPhotoSelectImageData data : _imgList) {
            if (data != null)
                imageList.add(data);
        }

        Collections.sort(imageList, new PhotobookCommonUtils.ImageCompare());

        ArrayList<MyPhotoSelectImageData> returnData = new ArrayList<MyPhotoSelectImageData>();

        for (MyPhotoSelectImageData d : imageList) {
            if (d == null)
                continue;

            if (d.KIND == Const_VALUES.SELECT_PHONE || d.KIND == Const_VALUES.SELECT_FACEBOOK || d.KIND == Const_VALUES.SELECT_UPLOAD || d.KIND == Const_VALUES.SELECT_KAKAO
                    || d.KIND == Const_VALUES.SELECT_SDK_CUSTOMER || d.KIND == Const_VALUES.SELECT_BETWEEN || d.KIND == Const_VALUES.SELECT_INSTAGRAM) {
                d.isModify = -1; // 수정여부 초기화
                returnData.add(d);
            }
        }

        return returnData;
    }

    public static SnapsControl getSnapsControlFromView(View view) {
        if (view == null) return null;
        if (view instanceof ISnapsControl) {
            return ((ISnapsControl) view).getSnapsControl();
        } else {
            Object object = view.getTag();
            if (object != null && object instanceof SnapsControl) {
                return (SnapsControl) view.getTag();
            }
        }
        return null;
    }

    public static int calculateTotalPageCount(int pageCount) {
        return (2 * pageCount) - 1;
    }

    public static int getLimitCountOfSelectablePhotoForPhotoBookWithPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null) return 0;

        String maxPageByPaperCode = ImageSelectUtils.getCurrentPaperCodeMaxPage();
        if (StringUtil.isEmpty(maxPageByPaperCode)) return 0;

        int maxPage = (2 * Integer.parseInt(maxPageByPaperCode)) + 1;
        int currentImageControlCnt = getLimitCountOfSelectablePhotoWithPageList(pageList);

        int addPageIndex = 2;
        int totalPageCount = pageList.size();
        while (PhotobookCommonUtils.calculateTotalPageCount(totalPageCount) <= maxPage) {
            if (addPageIndex >= pageList.size())
                addPageIndex = 2;

            SnapsPage snapsPage = pageList.get(addPageIndex);
            currentImageControlCnt += snapsPage.getImageLayoutControlCountOnPage();

            addPageIndex++;
            totalPageCount++;
        }

        Dlog.d("getLimitCountOfSelectablePhotoForPhotoBookWithPageList() "
                + "maxPage:" + maxPage
                + ", totalPageCount:" + PhotobookCommonUtils.calculateTotalPageCount(totalPageCount)
                + ", currentImageControlCn:" + currentImageControlCnt);
        return currentImageControlCnt;
    }

    public static int getLimitCountOfSelectablePhotoWithPageList(ArrayList<SnapsPage> pageList) {
        if (pageList == null) return 0;

        int currentImageControlCnt = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            currentImageControlCnt += snapsPage.getImageLayoutControlCountOnPage();
        }

        return currentImageControlCnt;
    }

    public static void imageRange(SnapsPage page) {
        try {
            SnapsLayoutControl layout;
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                MyPhotoSelectImageData imgData = layout.imgData;
                if (imgData != null)
                    layout.angle = imgData.ROTATE_ANGLE + "";

            }

            if (page.type.equalsIgnoreCase("cover")) {
                SnapsTextControl textLayout;
                ArrayList<SnapsControl> arrTextList = page.getTextControlList();
                for (int i = 0; i < arrTextList.size(); i++) {
                    textLayout = (SnapsTextControl) arrTextList.get(i);
                    if (textLayout != null) {
                        if (page.type.equalsIgnoreCase("cover")) {// 커버-제목
                            if (textLayout.format.verticalView.equalsIgnoreCase("true")) {

                                if (!"".equals(textLayout.width) && !"".equals(textLayout.height)) {
                                    int iWidth = Integer.valueOf(textLayout.width);
                                    int iHeight = Integer.valueOf(textLayout.height);
                                    if (iWidth < iHeight) {
                                        String tmpWidth = textLayout.width;
                                        textLayout.width = textLayout.height;
                                        textLayout.height = tmpWidth;
                                        if (textLayout.textList != null && textLayout.textList.size() > 0) {
                                            for (LineText lineText : textLayout.textList) {
                                                tmpWidth = lineText.width;
                                                lineText.width = lineText.height;
                                                lineText.height = tmpWidth;
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (page.type.equalsIgnoreCase("title")) {// 속지-이름

                        } else if (page.type.equalsIgnoreCase("page")) {// 페이지-날짜,내용

                        }
                    }
                }
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
