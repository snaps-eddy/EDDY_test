package com.snaps.mobile.activity.diary.publish;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.widget.TextView;

import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.book.SNSBookRecorder;
import com.snaps.mobile.activity.book.SnapsDiaryDrawManager;
import com.snaps.mobile.activity.book.SnapsDiaryDrawManager.ProgressListener;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPublishItem;
import com.snaps.mobile.activity.edit.view.CircleProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 16. 3. 21..
 */
public class SnapsDiaryPublishFragmentActivity extends SNSBookFragmentActivity implements SnapsDiaryInterfaceUtil.ISnapsDiaryListProcessListener {
    private SnapsDiaryDrawManager drawManager;

    private boolean getDataDone;
    private boolean getTemplateDone;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        UIUtil.applyLanguage(this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void initByType() {
        this.type = SNSBookFragmentActivity.TYPE_DIARY;

        if (!IS_EDIT_MODE) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            if (dataManager != null) {
                templateId = dataManager.getTemplateId();
                productCode = dataManager.getProductCode();
            }
        }
    }

    @Override
    protected void onPageSelect(int index) {
        currentPage = index;

        String prefix = getResources().getString(R.string.preview);
        String tailText = "";
        if (index == 0) {
            tailText = "(" + getString(R.string.cover) + ")";//"(커버)";
        } else if (index == 1) {
            tailText = "(" + getString(R.string.title) + ")";//"(타이틀)";
        } else {
            int pp = (index - 2) * 2 + 2;
            int totalPage = (_pageList.size() - 3) * 2 + 3;
            tailText = String.format("(%d,%d / %d p)", pp, ++pp, totalPage);
        }

        TextView titleView = (TextView) findViewById(R.id.btnTopTitle);
        titleView.setText(prefix + " " + tailText);
    }

    @Override
    protected SNSBookRecorder.SNSBookInfo getSNSBookInfo() {
        if (IS_EDIT_MODE)
            return createSNSBookInfoFromSaveXml();
        else if (drawManager != null)
            return drawManager.getInfo();
        else return null;
    }

    @Override
    protected void makeBookLayout() {
        drawManager = new SnapsDiaryDrawManager(this);
        drawManager.setProgressListener(new ProgressListener() {
            @Override
            public void updateProgress(final int per) {
                SnapsDiaryPublishFragmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CircleProgressView.getInstance(SnapsDiaryPublishFragmentActivity.this).setValue(per);
                    }
                });
            }

            @Override
            public int getProgress() {
                return CircleProgressView.getInstance(SnapsDiaryPublishFragmentActivity.this).getValue();
            }
        });

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        Set<String> removedKeySet = getRemovePostSet(getIntent(), dataManager);
        int totalPostCount = getIntent().getIntExtra(SNSBookFragmentActivity.INTENT_KEY_TOTAL_DATA_COUNT, 0);
        drawManager.initProgress(totalPostCount - removedKeySet.size());

        // 템플릿 가져오기..
        ATask.executeVoid(new ATask.OnTask() {
            boolean isSuccessDownload = false;

            @Override
            public void onPre() {
                CircleProgressView.getInstance(SnapsDiaryPublishFragmentActivity.this).setMessage(getString(R.string.snaps_diary_making_msg));
                CircleProgressView.getInstance(SnapsDiaryPublishFragmentActivity.this).load(CircleProgressView.VIEW_PROGRESS);
            }

            @Override
            public void onBG() {
                // 템플릿 다운로드..
                if (!IS_EDIT_MODE) isSuccessDownload = downloadTemplate(templateId);
            }

            @Override
            public void onPost() {
                if (isSuccessDownload) {
                    if (drawManager != null)
                        drawManager.addProgress(SnapsDiaryDrawManager.PROGRESS_GET_TEMPLATE);

                    getTemplateDone = true;
                    if (getDataDone) drawTemplate();
                } else {
                    MessageUtil.toast(SnapsDiaryPublishFragmentActivity.this, getString(R.string.kakao_book_make_err_template_download));
                    finishActivity();
                }
            }
        });

        SnapsDiaryInterfaceUtil.getCompletionDiaryList(this, dataManager.getPageInfo(true), removedKeySet, this);
    }

    @Override
    protected void getLoadSaveXML(final Activity activity) {
        final String url = SnapsAPI.GET_API_SAVE_XML() + "&prmProjCode=" + Config.getPROJ_CODE();

        ATask.executeVoid(new ATask.OnTask() {
            SnapsTemplate template = null;

            @Override
            public void onPre() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                SnapsTimerProgressView.showProgress(activity,
                        SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING, getString(R.string.templete_data_downloaing));
            }

            @Override
            public void onBG() {
                template = GetTemplateLoad.getTemplateByXmlPullParser(url, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                if (template != null) calcTextControl(template);

                String projectTitle = Config.getPROJ_NAME();
                SnapsDiaryDataManager.getInstance().setCoverTitle(projectTitle);

                if (IS_EDIT_MODE) {
                    String prmProjCode = Config.getPROJ_CODE();
                    // 커버 색상을 구할려면 필
                    templateId = Config.getTMPL_CODE();
                    saveXMLPriceInfo = GetParsedXml.getProductPriceInfo(prmProjCode, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                }

                PhotobookCommonUtils.saveMaskData(template);
            }

            @Override
            public void onPost() {
                if (template == null) {
                    MessageUtil.toast(activity, getString(R.string.kakao_book_make_err_template_download));
                    finishActivity();
                } else {
                    setTemplate(template);
                    SnapsTimerProgressView.destroyProgressView();

                    if (checkBookPageCount()) showSNSBookInfoDialog();

                    if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION))
                        requestNotifycation();
                }
            }
        });
    }

    @Override
    public void onStartGetDiaryList() {

    }

    @Override
    public void onUpdateDiaryList(int totalCount, int complete) {
        if (drawManager != null) drawManager.addProgress(SnapsDiaryDrawManager.PROGRESS_GET_DATA);
    }

    @Override
    public void onResultGetDiaryList(List<SnapsDiaryPublishItem> resultList, int failedDataIdx) {

        // TODO 페이지 다 그리고 나서 페이지 카운트까지 끝내고 나서 그때의 데이터를 저장해 둔다.
        if (drawManager != null) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            if (resultList != null && !resultList.isEmpty()) {
                Collections.reverse(resultList);
                dataManager.setStartDate(resultList.get(0).getDate());
                dataManager.setEndDate(resultList.get(resultList.size() - 1).getDate());
            } else {
                SnapsLogger.sendTextLog("SnapsDiaryPublishFragmentActivity/onResultGetDiaryList", ("resultList is null : " + (resultList == null)));

                SnapsDiaryPublishFragmentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessageUtil.toast(SnapsDiaryPublishFragmentActivity.this, getString(R.string.diary_failed_get_more_list));
                        finishActivity();
                    }
                });
                return;
            }

            drawManager.createInfo(this);
            drawManager.setDiaryList((ArrayList<SnapsDiaryPublishItem>) resultList);
        }

        getDataDone = true;
        if (getTemplateDone) drawTemplate();
    }

    @Override
    protected void setTemplate(SnapsTemplate template) {
        String paperCode = SnapsDiaryDataManager.getInstance().getPaperCode();
        if (!StringUtil.isEmpty(paperCode))
            template.info.F_PAPER_CODE = paperCode;

        super.setTemplate(template);

        if (drawManager != null) drawManager.setTotalPage(_pageList.size());
    }

    private void drawTemplate() {
        ATask.executeVoid(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                drawManager.makePage(multiTemplate);
                Config.setPROJ_NAME(SnapsDiaryDataManager.getInstance().getCoverTitle());

                FontUtil.downloadFontFiles(SnapsDiaryPublishFragmentActivity.this, multiTemplate.fonts); // font download

                drawManager.addProgress(SnapsDiaryDrawManager.PROGRESS_GET_DOWNLOAD_FONT);
                setTemplate(multiTemplate);
                drawManager.updateProgress(100);
            }

            @Override
            public void onPost() {
                loadFinish();
            }
        });
    }

    private Set<String> getRemovePostSet(Intent intent, SnapsDiaryDataManager dataManager) {
        Set<String> keySet = new HashSet<String>();
        ArrayList<Integer> indexList = intent.getIntegerArrayListExtra(SNSBookFragmentActivity.INTENT_KEY_REMOVE_DATA_INDEX_ARRAY);
        if (indexList != null && indexList.size() > 0 && dataManager != null && dataManager.getPublishListInfo() != null) {
            ArrayList<SnapsDiaryListItem> diaryItemList = dataManager.getPublishListInfo().getArrDiaryList();
            if (diaryItemList != null && diaryItemList.size() > 0) {
                int index;
                for (int i = 0; i < indexList.size(); ++i) {
                    index = indexList.get(i);
                    if (diaryItemList.size() > index)
                        keySet.add(diaryItemList.get(index).getDiaryNo());
                }
            }
        }
        return keySet;
    }
}
