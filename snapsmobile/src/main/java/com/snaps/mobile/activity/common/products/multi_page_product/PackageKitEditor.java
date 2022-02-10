package com.snaps.mobile.activity.common.products.multi_page_product;

import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.util.ArrayList;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class PackageKitEditor extends SnapsMultiPageEditor {

    public PackageKitEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistTitleActLayout();

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.GONE);

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null)
            textModify.setVisibility(View.GONE);
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
        showEditActivityTutorial();
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_PACKAGE_KIT;
    }

    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        if (!Config.getPROJ_CODE().equalsIgnoreCase("")) {
            Config.setPAPER_CODE(template.info.F_PAPER_CODE);
            Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
        } else {
            template.info.F_PAPER_CODE = Config.getPAPER_CODE();
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
        }

        if(Const_PRODUCT.isTtabujiProduct()) {
            if (template.info.F_PAPER_CODE.equals(""))
                template.info.F_PAPER_CODE = "160010";
        }
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) { /** 페이지 추가 개념이 없다 **/ }

    @Override
    public void initHiddenPageOnLoadedTemplate(SnapsTemplate template) {
        if(template.getPages() != null) {
            template._hiddenPageList = new ArrayList<SnapsPage>();

            if (Const_PRODUCT.isNewPolaroidPackProduct()) {
                for(int ii = 1; ii >= 0; ii--) { //첫장은 커버, 2번째는 인덱스
                    SnapsPage page = template.getPages().get(ii);
                    if(page != null && page.type != null && page.type.equalsIgnoreCase("hidden")) {
                        template._hiddenPageList.add(0, page);
                        template.getPages().remove(page);
                    }
                }
            } else {
                for(int ii = template.getPages().size() - 1; ii >= 0; ii--) {
                    SnapsPage page = template.getPages().get(ii);
                    if(page != null && page.type != null && page.type.equalsIgnoreCase("hidden")) {
                        template._hiddenPageList.add(0, page);
                        template.getPages().remove(page);
                    }
                }
            }
        }

        if(!Const_PRODUCT.isBothSidePrintProduct()) {
            refreshPagesId(template.getPages());
        }
    }

    @Override
    public boolean isSuccessInitializeTemplate(SnapsTemplate template) {
        if(Const_PRODUCT.isBothSidePrintProduct()) {
            if(!divisionPageListFrontAndBack(template))
                return false;
        }

        return super.checkBaseSuccessInitializeTemplate(template);
    }

    @Override
    public Rect getQRCodeRect() {
        int lastPage = getTemplate().getPages().size() - 1;
        int qrMargin = 10;
        if (Config.isCalendarWide(Config.getPROD_CODE()))
            qrMargin = 30;

        // 커버를 구한다. 기준위치는 테마북으로 한다.
        SnapsPage coverPage = getTemplate().getPages().get(lastPage);
        int width = coverPage.getOriginWidth();
        int height = (int) Float.parseFloat(coverPage.height);

        Rect rect = new Rect(0, 0, 100, 20);
        rect.offset(width - rect.width(), height - rect.height() - qrMargin);

        return rect;
    }

    @Override
    public void exportAutoSaveTemplate() {
//        AutoSaveManager saveMan = AutoSaveManager.getInstance();
//        if (saveMan == null)
//            return;
//        try {
//            if (!saveMan.isExportCalendarTemplate()) {
//                saveMan.exportTemplate(getTemplate());
//                saveMan.setExportCalendarTemplate(true);
//                return;
//            }
//
//            saveMan.exportLayoutControls(getPageList(), getPageThumbnailPaths(), getCurrentPageIndex());
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
    }

    @Override
    public SnapsTemplate getTemplate(String _url) {
        GetParsedXml.initTitleInfo(SnapsInterfaceLogDefaultHandler.createDefaultHandler());

        return super.handleGetBaseTemplate(_url);
    }

    @Override
    public SnapsTemplate recoveryTemplateFromAutoSavedFile() {
        try {
//            AutoSaveManager saveMan = AutoSaveManager.getInstance();
//            GetTemplateLoad.getFileTemplate(saveMan.getFilePath(IAutoSaveConstants.FILE_TYPE_TEMPLATE), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//            super.handleRecoveryTemplateFromAutoSavedFile();
            return super.handleRecoveryTemplateFromAutoSavedFile();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setPreviewBtnVisibleState() {
        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            if (Const_PRODUCT.isSquareProduct() || Const_PRODUCT.isTtabujiProduct() || Const_PRODUCT.isPolaroidPackProduct() || Const_PRODUCT.isNewPolaroidPackProduct() || Const_PRODUCT.isWoodBlockProduct() || Const_PRODUCT.isPostCardProduct()) {
                previewBtn.setVisibility(View.GONE);
            } else
                previewBtn.setVisibility(View.VISIBLE);
        }
    }

    /***
     * 페이지 썸네일 파일 패스를 Arraylist형태로 반환하는 함수...
     *
     * @return
     */
    @Override
    public ArrayList<String> getPageThumbnailPaths() {
        if (getPageList() == null || getPageList().isEmpty())
            return null;
        ArrayList<String> paths = new ArrayList<String>();

        for (SnapsPage page : getPageList()) {
            if (page == null)
                continue;
            paths.add(page.previewPath);
        }

        return paths;
    }
}
