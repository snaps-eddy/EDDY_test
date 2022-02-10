package com.snaps.mobile.activity.common.products.multi_page_product;

import android.app.AlertDialog;
import android.graphics.Rect;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.interfaces.DateMonthPickerSelectListener;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.io.File;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class CalendarEditor extends SnapsMultiPageEditor {
    private static final String TAG = CalendarEditor.class.getSimpleName();

    private int monthPickerSelectIndex = 0, startMonth = 0, startYear = 0;
    private AlertDialog calendarMonthPickerDialog = null;

    public CalendarEditor(FragmentActivity fragmentActivity) {
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

        ImageView calendarPeriodModify = getEditControls().getCalendarPeriodModify();
        if (calendarPeriodModify != null)
            calendarPeriodModify.setVisibility(View.VISIBLE);
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setComplete(false, getActivity());
        Config.setPROJ_NAME("");
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();

        initDatePickerInfo();

        createTemplateCache();

        //CS 대응
        if (Config.isDevelopVersion()) {
            if (Const_VALUE.sNotDefineFontList != null && Const_VALUE.sNotDefineFontList.size() > 0) {
                StringBuilder sb  = new StringBuilder();
                for(String fontName : Const_VALUE.sNotDefineFontList) {
                    sb.append(fontName).append("\n");
                }
                MessageUtil.alert(getActivity(), "미등록 폰트", sb.toString());
            }
        }
    }

    private void initDatePickerInfo() {
        startMonth = GetTemplateXMLHandler.getStartMonth();
        startYear = GetTemplateXMLHandler.getStartYear();
        monthPickerSelectIndex = findSelectedPeriodIndex(startYear, startMonth);
    }

    private int findSelectedPeriodIndex(int year, int month) {
        try {
            String[] calendarPeriod = DateUtil.createDateRangeItem(getActivity());
            String selectedPeriod = String.format("%d-%02d", year, month);
            int index = 0;
            for (String period : calendarPeriod) {
                if (period == null) continue;
                if (period.equalsIgnoreCase(selectedPeriod)) return index;
                index++;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
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
        return IAutoSaveConstants.PRODUCT_TYPE_CALENDAR;
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        if(!AutoSaveManager.isAutoSaveRecoveryMode()) {
            //hidden은 나오면 안되는데, 나오고 있다..
            if(template.getPages() != null) {
                for(int ii = template.getPages().size() - 1; ii >= 0; ii--) {
                    SnapsPage page = template.getPages().get(ii);
                    if(page != null && page.type != null && page.type.equalsIgnoreCase("hidden"))
                        template.getPages().remove(page);
                }
            }
        }
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
    public int getLastEditPageIndex() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null || !saveMan.isRecoveryMode())
            return 0;
        return saveMan.getCalendarLastIdx();
    }

    @Override
    public void exportAutoSaveTemplate() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null)
            return;
        try {
            if (!saveMan.isExportCalendarTemplate()) {
                saveMan.exportTemplate(getTemplate());
                saveMan.setExportCalendarTemplate(true);
                return;
            }

            saveMan.exportLayoutControls(getPageList(), getPageThumbnailPaths(), getCurrentPageIndex());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public SnapsTemplate getTemplate(String _url) {
        GetParsedXml.initTitleInfo(SnapsInterfaceLogDefaultHandler.createDefaultHandler());

        return super.handleGetBaseTemplate(_url);
    }

    @Override
    public SnapsTemplate recoveryTemplateFromAutoSavedFile() {
        try {
            return super.handleRecoveryTemplateFromAutoSavedFile();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCalendarTemplateCacheFile();
        GetTemplateXMLHandler.setStartYear(0);
        GetTemplateXMLHandler.setStartMonth(0);
    }

    @Override
    public void onClickedChangePeriod() {
        showCalendarPickerDialog();
    }

    private void showCalendarPickerDialog() {
        if (calendarMonthPickerDialog != null && calendarMonthPickerDialog.isShowing())
            return;

        calendarMonthPickerDialog = DateUtil.showDateMonthPickerDialog(getActivity(), monthPickerSelectIndex, new DateMonthPickerSelectListener() {
            @Override
            public void onDateMonthSelected(int index, String text) {
                try {
                    handleOnCalendarDateMonthSelected(index, text);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private void handleOnCalendarDateMonthSelected(int index, String text) throws Exception {
        saveCalendarPeriodWithStr(text);
        updateCalendarDate(index);
    }

    private void saveCalendarPeriodWithStr(String text) throws Exception {
        if (StringUtil.isEmpty(text) || !text.contains("-")) return;
        String[] yearMonth = text.split("-");
        if (yearMonth.length != 2) return;
        final int startYear = Integer.parseInt(yearMonth[0]);
        final int startMonth = Integer.parseInt(yearMonth[1]);
        GetTemplateXMLHandler.setStartYear(startYear);
        GetTemplateXMLHandler.setStartMonth(startMonth);
    }

    private void showFailedGetCalendarTemplateCacheMsg() {
        SnapsHandler.handleOnMainThread(new SnapsHandler.MainThreadHandleImp() {
            @Override
            public void handle() {
                if (getActivity() == null || getActivity().isFinishing()) return;
                MessageUtil.toast(getActivity(), getString(com.snaps.common.R.string.network_error_message_please_wait));
            }
        });
    }

    private void updateCalendarDate(final int index) throws Exception {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        snapsTemplateManager.getTemplateFromCache(getCalendarTemplateCacheFile(), new SnapsCommonResultListener<SnapsTemplate>() {
            @Override
            public void onResult(SnapsTemplate newTemplate) {
                try {
                    if (isValidTemplateModifyState(newTemplate)) {
                        handleSucceedGetCalendarTemplateCache(newTemplate, index);
                    } else {
                        handleFailedGetCalendarTemplateCache();
                    }
                } catch (Exception e) { Dlog.e(TAG, e); }
            }
        });
    }

    private boolean isValidTemplateModifyState(SnapsTemplate newTemplate) {
        SnapsTemplate currentTemplate = getTemplate();
        return newTemplate != null && newTemplate.getPages() != null && currentTemplate != null && currentTemplate.getPages() != null;
    }

    private void handleFailedGetCalendarTemplateCache() throws Exception {
        createTemplateCache();
        GetTemplateXMLHandler.setStartYear(startYear);
        GetTemplateXMLHandler.setStartMonth(startMonth);
        showFailedGetCalendarTemplateCacheMsg();
    }

    private void handleSucceedGetCalendarTemplateCache(SnapsTemplate newTemplate, int index) throws Exception {
        swapAllTextControl(newTemplate);
        startYear = GetTemplateXMLHandler.getStartYear();
        startMonth = GetTemplateXMLHandler.getStartMonth();
        monthPickerSelectIndex = index;

        selectCenterPager(0, false);

        refreshUI();
    }

    private void createTemplateCache() {
        try {
            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
            snapsTemplateManager.exportTemplateCache(getCalendarTemplateCacheFile(), SnapsTemplate.getTemplateUrl());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private File getCalendarTemplateCacheFile() throws Exception {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        return snapsTemplateManager.getCalendarTemplateCacheFilePath(getActivity());
    }

    private void deleteCalendarTemplateCacheFile() {
        try {
            FileUtil.deleteFile(getCalendarTemplateCacheFile().getAbsolutePath());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected void swapAllTextControl(SnapsTemplate snapsTemplate) throws Exception {
        SnapsTemplate currentTemplate = getTemplate();
        for (SnapsPage snapsPage : currentTemplate.getPages()) {
            if (snapsPage == null || snapsPage.type == null || snapsPage.type.equalsIgnoreCase("cover")) continue;

            snapsPage.removeAllTextControl();

            PhotobookCommonUtils.copyTextControl(snapsPage, snapsTemplate.getPages());
        }
    }
}
