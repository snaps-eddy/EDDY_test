package com.snaps.mobile.activity.diary.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.spc.view.SnapsDiaryTextView;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.IListShapeDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.diary.json.SnapsDiaryCountJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.custom_layouts.AFrameLayoutParams;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * Created by ysjeong on 16. 3. 4..
 */
public class SnapsDiaryConfirmViewActivity extends SnapsDiaryConfirmBaseActivity {
    private static final String TAG = SnapsDiaryConfirmViewActivity.class.getSimpleName();

    private String m_szDiaryContents = "";

    protected void onCreate(Bundle savedInstanceState) {

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.setWriteMode(SnapsDiaryConstants.EDIT_MODE_DETAIL_VIEW);

        super.onCreate(savedInstanceState);

        TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
        themeTitle.setText(R.string.snaps_diary);

        m_isEditedPicture = false;
    }

    @Override
    protected void initHook() {
        if (m_tvRegisteredDate != null)
            m_tvRegisteredDate.setVisibility(View.VISIBLE);
    }

    @Override
    protected void registerModules() {}

    @Override
    protected void checkIntentData() {
        Intent getItt = getIntent();
        if(getItt == null) return;
        mEditItem = (SnapsDiaryListItem) getItt.getSerializableExtra(Const_EKEY.DIARY_DATA);
        if(mEditItem != null && mEditItem.getDiaryNo() != null) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = new SnapsDiaryWriteInfo();
            writeInfo.setYMDToDateStr(mEditItem.getDate());
            writeInfo.setFeels(mEditItem.getFeels());
            writeInfo.setWeather(mEditItem.getWeather());
            dataManager.setWriteInfo(writeInfo);
        }
    }

    @Override
    public void onOverTextArea(String drawnText) {}

    @Override
    protected void setTextViewProcess() {
        if(m_tvContents == null || m_etContents == null) return;

        m_tvContents.setVisibility(View.VISIBLE);
        m_etContents.setVisibility(View.GONE);

        int minHeight = (int) getResources().getDimension(R.dimen.snaps_diary_confirm_textview_min_height);
        m_tvContents.setMinHeight(minHeight);
        m_tvContents.invalidate();
    }

    @Override
    protected void setNextButton(TextView textView) {
        if(textView == null) return;

        textView.setText("");

        ImageView option = (ImageView) findViewById(R.id.ThemecartBtn);
        option.setImageResource(R.drawable.img_diary_list_small_option);
        option.setVisibility(View.VISIBLE);
    }

    @Override
    protected void performClickEditText() {}

    @Override
    protected void performClickDateBar() {}

    @Override
    public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {
        super.onFinishDiaryUpload(isIssuedInk, isNewWrite);

        m_isEditedPicture = true;
    }

    @Override
    protected void performNextButton() {
        SnapsDiaryDialog.showListSelectPopup(SnapsDiaryConfirmViewActivity.this, new IListShapeDialogListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0: //수정하기
                        startModifyMode();
                        break;
                    case 1: //삭제하기
                        checkDiaryCount();
                        break;
                }
            }
        });

    }

    private void checkDiaryCount() {
        if(mEditItem == null) return;
        SnapsDiaryInterfaceUtil.requestGetDiaryCountSameDate(this, mEditItem.getDiaryNo(), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
                if (pageProgress != null)
                    pageProgress.show();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                if (pageProgress != null)
                    pageProgress.dismiss();

                if (result && resultObj != null) {
                    SnapsDiaryCountJson countJson = (SnapsDiaryCountJson) resultObj;
                    String count = countJson.getCount();
                    int iCnt = 0;
                    boolean isWarningMissionFailWhenDeleted = false;
                    if (count != null) {
                        try {
                            iCnt = Integer.parseInt(count);
                            if (iCnt == 1) {
                                isWarningMissionFailWhenDeleted = SnapsDiaryDataManager.isWarningMissionFailedWhenDelete(mEditItem);
                            }
                        } catch (NumberFormatException e) {
                            Dlog.e(TAG, e);
                        }
                    }

                    int msg = 0;
                    int impectMsg = 0;
                    if (isWarningMissionFailWhenDeleted) {
                        msg = R.string.diary_alert_delete_diary_and_mission_fail;
                        impectMsg = R.string.diary_alert_delete_diary_and_mission_fail_impect;
                    } else if (iCnt == 1) {
                        msg = R.string.diary_alert_delete_diary_and_ink;
                        impectMsg = R.string.diary_alert_delete_diary_and_ink_impect;
                    } else {
                        msg = R.string.diary_alert_delete_diary;
                    }

                    SnapsDiaryDialog.showDialogWithImpect(SnapsDiaryConfirmViewActivity.this,
                            msg,
                            impectMsg,
                            getString(R.string.confirm),
                            getString(R.string.cancel), new ICustomDialogListener() {
                                @Override
                                public void onClick(byte clickedOk) {
                                    if (clickedOk == ICustomDialogListener.OK) {
                                        requestDelete();
                                    }
                                }
                            });
                } else {
                    MessageUtil.alert(SnapsDiaryConfirmViewActivity.this, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                checkDiaryCount();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void performBackKeyPressed() {
        if (m_isEditedPicture || m_isEditedDate) {
            Intent putIntent = new Intent();
            putIntent.putExtra(SnapsDiaryConstants.EXTRAS_BOOLEAN_EDITED_DATE, m_isEditedDate);
            setResult(SnapsDiaryConstants.RESULT_CODE_DIARY_UPDATED, putIntent);
        }

        onBackPressed();
    }

    @Override
    protected void setDiaryContents() {
        if(mEditItem == null) return;

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if(writeInfo != null) {

            if (m_tvRegisteredDate != null) {
                m_tvRegisteredDate.setText(mEditItem.getFormattedRegisteredDate());
            }

            if (m_ivWeather != null) {
                if (writeInfo.getWeather() == SnapsDiaryConstants.eWeather.NONE) {
                    m_ivWeather.setVisibility(View.GONE);
                } else {
                    m_ivWeather.setVisibility(View.VISIBLE);
                    m_ivWeather.setImageResource(writeInfo.getWeather().getIconResId(true));
                }
            }

            if (m_ivFeels != null) {
                if (writeInfo.getFeels() == SnapsDiaryConstants.eFeeling.NONE) {
                    m_ivFeels.setVisibility(View.GONE);
                } else {
                    m_ivFeels.setVisibility(View.VISIBLE);
                    m_ivFeels.setImageResource(writeInfo.getFeels().getIconResId(true));
                }
            }

            if(m_tvDate != null)
                m_tvDate.setText(mEditItem.getFormattedDate());

            if(m_tvContents != null && m_szDiaryContents != null) {
                m_tvContents.setText(m_szDiaryContents);
            }
        }

        loadThumbnailImage();

        m_isReadyComplete = true;
    }

    private void loadThumbnailImage() {
        if (mEditItem == null) {
            progressUnload();
            return;
        }
        FrameLayout layout = (FrameLayout) findViewById(R.id.snaps_diary_confirm_fragment_ly);
        layout.removeAllViews();

        ImageView imageView = new ImageView(this);
        AFrameLayoutParams layoutParams = new AFrameLayoutParams(new AFrameLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        layout.addView(imageView);

        final String URL = mEditItem.getThumbnailUrl();
        SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(this, imageView) {
            @Override
            public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                super.onResourceReady(resource, transition);

                progressUnload();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);

                progressUnload();
            }
        };

        ImageLoader.asyncDisplayImage(this, URL, bitmapImageViewTarget);
    }

    @Override
    protected void getTemplateHandler(final String TEMPLATE_URL) {
        if (TEMPLATE_URL != null && TEMPLATE_URL.length() > 0) {
            ATask.executeVoid(new ATask.OnTask() {
                @Override
                public void onPre() {
                    if (pageProgress != null)
                        pageProgress.show();
                }

                @Override
                public void onBG() {

                    _template = GetTemplateLoad.getThemeBookTemplate(TEMPLATE_URL, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

                    m_szDiaryContents = PhotobookCommonUtils.getTextListFromTemplate(_template);
                }

                @Override
                public void onPost() {
                    if (_template != null) {
                        setDiaryContents();
                    } else {
                        progressUnload();
                        finish();

                        Toast.makeText(SnapsDiaryConfirmViewActivity.this, R.string.loading_fail, Toast.LENGTH_SHORT).show();
                        SnapsOrderManager.setSnapsOrderStatePauseCode(getResources().getString(R.string.loading_fail));
                    }
                }
            });
        } else {
            finish();
        }
    }

    private void requestDelete() {
        SnapsDiaryInterfaceUtil.requestDiaryDelete(this, mEditItem.getDiaryNo(), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
                if (pageProgress != null)
                    pageProgress.show();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                if (pageProgress != null)
                    pageProgress.dismiss();
                if(result) {
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryListInfo listInfo = dataManager.getListInfo();
                    listInfo.removeDiaryItem(mEditItem.getDiaryNo());

                    listInfo.setTotalCount(listInfo.getTotalCount() - 1);

                    if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {
                    //FIXME
                    } else {
                        if (SnapsDiaryConstants.isOSTypeEqualsAndroid(mEditItem.getOsType())) {
                            listInfo.setAndroidCount(listInfo.getAndroidCount() - 1);
                        } else {
                            listInfo.setIosCount(listInfo.getIosCount() - 1);
                        }
                    }

                    setResult(SnapsDiaryConstants.RESULT_CODE_DIARY_DELETED);
                    SnapsDiaryConfirmViewActivity.this.finish();
                } else {
                    MessageUtil.alert(SnapsDiaryConfirmViewActivity.this, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK)
                                requestDelete();
                        }
                    });
                }
            }
        });
    }

    private void startModifyMode() {
        SnapsTimerProgressView.destroyProgressView();

        Intent intent = new Intent(this, SnapsDiaryConfirmEditableActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.DIARY_DATA, mEditItem);
        bundle.putBoolean(Const_EKEY.DIARY_IS_MODIFY_MODE, true);

        intent.putExtras(bundle);
        startActivityForResult(intent, SnapsDiaryConstants.RESULT_CODE_DIARY_UPDATED);
    }

    private void refreshThisDiaryItem() {
        if(mEditItem == null) return;
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        SnapsDiaryListItem updatedItem = listInfo.getDiaryItem(mEditItem.getDiaryNo());
        if(updatedItem != null) {
            m_isEditedDate = isEditedDate(updatedItem);
            mEditItem.set(updatedItem);
        }

        getTemplateHandler(SnapsAPI.DOMAIN() + mEditItem.getFilePath());
    }

    private boolean isEditedDate(SnapsDiaryListItem updatedItem) {
        if (mEditItem == null || updatedItem == null) return false;
        if (mEditItem.getDate() != null) {
            return !mEditItem.getDate().equalsIgnoreCase(updatedItem.getDate());
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SnapsDiaryConstants.RESULT_CODE_DIARY_UPDATED :
                refreshThisDiaryItem();
                break;
        }
    }

    @Override
    public SnapsDiaryTextView.ISnapsDiaryTextControlListener getDiaryTextControlListener() {
        return this;
    }
}