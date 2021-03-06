package com.snaps.mobile.activity.diary.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.spc.view.SnapsDiaryTextView;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.NTPClient;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryCommonUtils;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListItemJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUploadSeqInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsDiaryConfirmFragment;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadStateListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImagePopup;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * Created by ysjeong on 16. 3. 4..
 */
public class SnapsDiaryConfirmEditableActivity extends SnapsDiaryConfirmBaseActivity implements DatePickerDialog.OnDateSetListener, SnapsOrderActivityBridge, SnapsImageUploadStateListener {
    private static final String TAG = SnapsDiaryConfirmEditableActivity.class.getSimpleName();
    private final int TEXT_HISTORY_COUNT = 3;

    private DatePickerDialog mDatePicker = null;

    private SnapsDiaryDialog mConfirmDialog = null;

    private LinkedList<String> m_queTextHistory = null;

    private long m_lLastInputHistoryTime = 0l;

    private String m_szPrevModifiedContents = "";

    private boolean isInitializedOrderManager = false;

    private SnapsDiaryTextView mSnapsDiaryTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        m_queTextHistory = new LinkedList<>();
        m_queTextHistory.offer("");

        TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
        themeTitle.setText(R.string.diary_write);

        mSnapsDiaryTextView = null;

        isInitializedOrderManager = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDisconnectNetwork() {}

    @Override
    protected void registerModules() {
        // ????????? ??????....
        IntentFilter filter = new IntentFilter(Const_VALUE.CLICK_LAYOUT_ACTION);
        mReceiver = new SnapsBroadcastReceiver();
        mReceiver.setImpRecevice(this);
        registerReceiver(mReceiver, filter);

        //????????? ?????? ?????????
        makeSnapsPageCaptureCanvas();
    }

    @Override
    protected void initHook() {
        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.snaps_diary_confirm_date_weather_feels_area_ly);
        ViewGroup.MarginLayoutParams dateLayoutParams = (ViewGroup.MarginLayoutParams) dateLayout.getLayoutParams();
        dateLayoutParams.bottomMargin = UIUtil.convertDPtoPX(this, 20);

        m_ivDate.setVisibility(View.VISIBLE);

        m_lyDate.setOnClickListener(this);
        dateLayout.setOnClickListener(this);
        m_etContents.setOnClickListener(this);
        m_ivDate.setOnClickListener(this);

        // ?????? ??????.
        FileUtil.initProjectFileSaveStorage();

        if(isNewWriteMode()) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            if(writeInfo != null) {
                ArrayList<MyPhotoSelectImageData> arrayList =  writeInfo.getPhotoImageDataList();
                if(arrayList != null && !arrayList.isEmpty()) {
                    MyPhotoSelectImageData addImageData;
                    if(_galleryList != null)
                        _galleryList.clear();
                    else
                        _galleryList = new ArrayList<>();

                    for(MyPhotoSelectImageData imgData : arrayList) {
                        addImageData = new MyPhotoSelectImageData();
                        addImageData.set(imgData);
                        _galleryList.add(addImageData);
                    }
                }
            }
        }
    }

    @Override
    protected void checkIntentData() {
        Intent getItt = getIntent();
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();

        if(getItt.getExtras() != null)
            getItt.getExtras().setClassLoader(SnapsDiaryListItem.class.getClassLoader());

        Object editItemObj = getItt.getSerializableExtra(Const_EKEY.DIARY_DATA);
        if(editItemObj != null && editItemObj instanceof SnapsDiaryListItem) {
            mEditItem = (SnapsDiaryListItem) editItemObj;
            SnapsDiaryWriteInfo writeInfo = new SnapsDiaryWriteInfo();
            writeInfo.setYMDToDateStr(mEditItem.getDate());
            writeInfo.setFeels(mEditItem.getFeels());
            writeInfo.setWeather(mEditItem.getWeather());
            dataManager.setWriteInfo(writeInfo);

            //???????????? ???, ????????? seq??? ????????????.
            SnapsDiaryUploadSeqInfo uploadInfo = new SnapsDiaryUploadSeqInfo();
            uploadInfo.setDiaryNo(mEditItem.getDiaryNo());
            uploadInfo.setSeqUserNo(SnapsLoginManager.getUUserNo(this));
            dataManager.setUploadInfo(uploadInfo);
        } else {
            initUploadInfo();
        }

        boolean isModifyMode = getItt.getBooleanExtra(Const_EKEY.DIARY_IS_MODIFY_MODE, false);
        if (isModifyMode) {
            dataManager.setWriteMode(SnapsDiaryConstants.EDIT_MODE_MODIFY);
        } else {
            dataManager.setWriteMode(SnapsDiaryConstants.EDIT_MODE_NEW_WRITE);
        }
    }

    @Override
    public void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener) {}

    @Override
    public void onOverTextArea(final String drawnText) {
        if(mConfirmDialog != null && mConfirmDialog.isShowing() || m_etContents == null) return;

        mConfirmDialog = new SnapsDiaryDialog(this, getString(R.string.diary_over_text_msg), "", new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if(clickedOk == ICustomDialogListener.OK) {
                    if(m_queTextHistory == null || m_queTextHistory.size() <= 1) {
                        m_etContents.setText(drawnText);
                    } else {
                        m_queTextHistory.pollLast(); //???????????? ????????? ???????????? ?????? ????????? ?????? ??? ?????????, ?????????.
                        String prevValidText = m_queTextHistory.pollLast(); //????????? ????????? ????????????...
                        if (m_queTextHistory.isEmpty())
                            m_queTextHistory.offer("");

                        if (StringUtil.isEmpty(prevValidText)) {
                            String prevText = m_etContents.getText().toString();
                            if (prevText.length() > 1) {
                                prevText = prevText.substring(0, prevText.length()-1);
                            }

                            m_etContents.setText(prevText);
                        } else {
                            m_etContents.setText(prevValidText);
                        }
                    }
                }
            }
        });

        mConfirmDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        onResumeControl();
    }

    private void onResumeControl() {
        if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_CAPTURE)) {// ?????? ?????? ????????? ?????? ?????? ?????? ??????.
            requestMakeMainPageThumbnailFile(getSnapsPageCaptureListener());
        } else if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_UPLOAD_COMPLETE)) {
            SnapsOrderManager.showCompleteUploadPopup();
        }

        SnapsOrderManager.setSnapsOrderStatePauseCode("");
    }

    @Override
    protected void setTextViewProcess() {
        if(m_tvContents == null || m_etContents == null) return;

        m_tvContents.setVisibility(View.GONE);
        m_etContents.setVisibility(View.VISIBLE);

        int minHeight = (int) getResources().getDimension(R.dimen.snaps_diary_confirm_edittextview_min_height);
        m_etContents.setMinHeight(minHeight);
        m_etContents.invalidate();

        mRootView.requestFocus();

        m_etContents.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {
                //???????????? ???????????? "\n"??? ????????? ?????? ???????????? ?????? ????????? ?????? ???????????? ????????? ?????? ??????
                String afterString = s.toString().trim();
                if (afterString.length() == 0) {
                    if (m_etContents.getLineCount() >= SnapsDiaryTextView.MAX_DIARY_PUBLISH_TEXT_LINE_COUNT) {
                        m_etContents.setText(previousString);
                        m_etContents.setSelection(m_etContents.length());
                        return;
                    }
                }
                setTextOnControl(s.toString());
            }
        });

        m_etContents.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 0);
            }
        });
    }

    private boolean checkValidText(final String TEXT) {
        final String FILTER_TEXT = StringUtil.getFilterString(TEXT);
        if (TEXT.length() != FILTER_TEXT.length()) {
            if(mConfirmDialog != null && mConfirmDialog.isShowing() || m_etContents == null) return false;
            mConfirmDialog = new SnapsDiaryDialog(SnapsDiaryConfirmEditableActivity.this, "???????????? ????????? ??? ??????????????? ??????????????? ?????? ????????? ?????? ???????????????.\n?????? ?????? ?????? ????????? ?????? ??? ?????????.", "", new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    if (m_etContents != null)
                        m_etContents.setText(FILTER_TEXT);
                }
            });

            mConfirmDialog.show();
            return false;
        }
        return true;
    }

    /**
     * ?????? saveXML??? ????????? control??? et?????? ????????? ???????????? ?????? ??? ??????.
     * m_queTextHistory??? ????????? ?????? ?????? ???, ?????? ???????????? ?????? ?????? ?????? ?????? ??????.
     * @param text
     */
    private void setTextOnControl(String text) {
        if(mTextControl == null || text == null || m_queTextHistory == null) return;

        if (mSnapsDiaryTextView == null) {
            //?????? ???
            mSnapsDiaryTextView = (SnapsDiaryTextView)findViewById(mTextControl.getControlId());
        }

        if (mSnapsDiaryTextView != null) {
            mSnapsDiaryTextView.text(text);
        }

        if (System.currentTimeMillis() - m_lLastInputHistoryTime > 100) {
            m_lLastInputHistoryTime = System.currentTimeMillis();

            String prevText = "";
            if(!m_queTextHistory.isEmpty())
                prevText = m_queTextHistory.peekLast();

            if (prevText.length() != text.length()) {

                if(m_queTextHistory.size() >= TEXT_HISTORY_COUNT)
                    m_queTextHistory.poll();

                while (text.endsWith("\n")) {
                    text = text.substring(0, text.lastIndexOf("\n"));
                }

                if (!isDuplicateText(text))
                    m_queTextHistory.offer(text);
            }
        }
    }

    private boolean isDuplicateText(String text) {
        if (m_queTextHistory == null) return true;
        for (String history : m_queTextHistory)
            if (history != null && history.equals(text)) return true;
        return false;
    }

    @Override
    protected void setNextButton(TextView textView) {
        if(textView == null) return;

        if(isNewWriteMode()) {
            textView.setText(getString(R.string.done));
            textView.setBackgroundResource(0);
        }
        else if(isModifyMode()) {
            textView.setText(getString(R.string.done));
            textView.setBackgroundResource(R.drawable.img_diary_list_small_option);
        }

        textView.setBackgroundColor(Color.WHITE);

        ImageView option = (ImageView) findViewById(R.id.ThemecartBtn);
        option.setVisibility(View.GONE);
    }

    private boolean isEditedText() {
        return m_etContents != null && m_szPrevModifiedContents != null
                && !m_szPrevModifiedContents.equals(m_etContents.getText().toString());
    }

    private boolean isEdited() {
        return isEditedText() || m_isEditedPicture || isEditedDate();
    }

    private boolean isEditedDate() {
        if (mEditItem == null) return false;
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if (writeInfo != null && writeInfo.getDate() != null) {
            return !writeInfo.getDate().equalsIgnoreCase(mEditItem.getDate());
        }
        return false;
    }

    @Override
    protected void performNextButton() {
        //?????? ????????? ?????? ??????
        /*
        if (SnapsDiaryMainActivity.IS_END_OF_SERVICE) {
            if (isNewWriteMode()) {
                if (!NTPClient.isEnableDiaryNewOrEdit()) {
                    MessageUtil.alertnoTitleOneBtn(this, "?????? ???????????? ????????????\n?????? ????????? ????????? ??? ????????????.", null);
                    return;
                }
            }
        }
        */


        if(isModifyMode()) { //????????? ????????? ????????? ??? ?????????.
            if(!isEdited()) {
                onBackPressed();
                return;
            }
        }

        if(m_etContents == null) return;

        setTextOnControl(m_etContents.getText().toString());

        if (!checkValidText(m_etContents.getText().toString())) {
            return;
        }

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if(writeInfo != null) {
            writeInfo.setContents(m_etContents.getText().toString());
        }

        upload();
    }

    @Override
    protected void performBackKeyPressed() {
        if (isNewWriteMode()) {

            //bug fix
            //?????????????????? ????????? ?????? ???????????? back key ????????? ????????? ??????????????? ???????????? ???????????? ????????? ??????.
            if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
                return;
            }

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            if(writeInfo != null) {
                writeInfo.setContents(m_etContents.getText().toString());
                writeInfo.setPhotoImageDataList(_galleryList);
            }

            //????????? ????????????, ??? ???????????? ????????? ??? ??????..
            onBackPressed();
        } else {
            if(isEdited()) {
                MessageUtil.alertnoTitle(this, getString(R.string.confirm_dont_save_msg), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        switch (clickedOk) {
                            case ICustomDialogListener.OK:
                                onBackPressed();
                                break;
                            default:
                                break;
                        }

                    }
                });
            } else {
                onBackPressed();
            }
        }
    }

    @Override
    protected void performClickEditText() {
        if (mScrollView == null || m_etContents == null) return;
        m_etContents.setHint(null);
        m_etContents.setCursorVisible(true);

        /**
         * ????????? ????????????, ???????????? ?????? ????????? ????????? ??? ??? ???????????? ?????? ????????? ???????????? ??????...?????? ????????? ????????? ?????? ??????.
         */
        /*
        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollBy(0, (int) mScrollEndView.getY());
            }
        }, 100);
        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollBy(0, (int) mScrollEndView.getY());
            }
        }, 300);
        */

        //?????? ?????? ???????????? dealy??? ?????? ?????????? ?????? ?????? ???????????? ????????? ?????? ??????
        mScrollView.smoothScrollBy(0, (int) mScrollEndView.getY());
    }

    @Override
    protected void performClickDateBar() {
        SnapsDiaryCommonUtils.showCalendar(this, this, mDatePicker);
    }

    @Override
    public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {
        super.onFinishDiaryUpload(isIssuedInk, isNewWrite);
        if (isNewWrite) {
            finish();
        } else {
            updateModifiedDiarySingleItem();
        }
    }

    private void updateModifiedDiarySingleItem() {
        if(mEditItem ==  null) return;
        SnapsDiaryInterfaceUtil.getSingleDiaryItem(this, mEditItem.getDiaryNo(), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
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
                    SnapsDiaryListJson listResult = (SnapsDiaryListJson) resultObj;

                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryListInfo listInfo = dataManager.getListInfo();
                    List<SnapsDiaryListItemJson> list = listResult.getDiaryList();
                    if (list != null && !list.isEmpty()) {
                        listInfo.updateItem(list.get(0));
                    }

                    Intent putIntent = new Intent();
                    putIntent.putExtra(SnapsDiaryConstants.EXTRAS_BOOLEAN_EDITED_DATE, isEditedDate());
                    setResult(SnapsDiaryConstants.RESULT_CODE_DIARY_UPDATED, putIntent);

                    finish();
                } else {
                    MessageUtil.alert(SnapsDiaryConfirmEditableActivity.this, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                updateModifiedDiarySingleItem();
                            } else {
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void setDiaryContents() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if (writeInfo != null) {
            if (m_tvDate != null)
                m_tvDate.setText(writeInfo.getDateFormatted());

            if (m_etContents != null)
                m_etContents.setText(writeInfo.getContents());

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
        }
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
                    if(isModifyMode()) {
                        _template = GetTemplateLoad.getThemeBookTemplate(TEMPLATE_URL, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    } else {
                        _template = GetTemplateLoad.getFileTemplate(TEMPLATE_URL, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    }

                    if (isModifyMode()) {
                        _galleryList = PhotobookCommonUtils.getImageListFromTemplate(_template);

                        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
                        writeInfo.setPhotoImageDataList(_galleryList);
                        m_szPrevModifiedContents = PhotobookCommonUtils.getTextListFromTemplate(_template);
                        writeInfo.setContents(m_szPrevModifiedContents);

                        if(m_queTextHistory != null)
                            m_queTextHistory.add(writeInfo.getContents());
                    }
                }

                @Override
                public void onPost() {
                    if (_template != null) {
                        if(isNewWriteMode())
                            PhotobookCommonUtils.imageRange(_template, _galleryList);
                        else
                            PhotobookCommonUtils.imageRange2(_template);

                        for (SnapsPage page : _template.getPages()) {
                            if (!page.type.equalsIgnoreCase("hidden"))
                                _pageList.add(page);
                        }

                        loadCanvas();

                        _template.clientInfo.screendpi = String.valueOf(getResources().getDisplayMetrics().densityDpi);
                        _template.clientInfo.screenresolution = SystemUtil.getScreenResolution(SnapsDiaryConfirmEditableActivity.this);

                    } else {
                        progressUnload();
                        finish();

                        Toast.makeText(SnapsDiaryConfirmEditableActivity.this, R.string.loading_fail, Toast.LENGTH_SHORT).show();
                        SnapsOrderManager.setSnapsOrderStatePauseCode(getResources().getString(R.string.loading_fail));
                    }
                }
            });
        } else {
            finish();
        }
    }

    private void makeSnapsPageCaptureCanvas() {
        mCaptureFragment = new SnapsDiaryConfirmFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", 0);
        bundle.putBoolean("pageSave", true);
        bundle.putBoolean("pageLoad", false);
        bundle.putBoolean("preThumbnail", true);
        bundle.putBoolean("visibleButton", false);
        mCaptureFragment.setArguments(bundle);

        FragmentUtil.replce(R.id.frameMain, this, mCaptureFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDatePicker != null)
            mDatePicker.onDestroy();

        initUploadInfo();
    }

    private void initUploadInfo() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.clearUploadSeqInfo();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(m_tvDate == null) return;

        if(!SnapsDiaryCommonUtils.isAllowDiaryRegisterDate(year, monthOfYear, dayOfMonth)) {
            MessageUtil.alertnoTitleOneBtn(this, getString(R.string.diary_invalid_date_msg), null);
            return;
        }

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        writeInfo.setYear(year);
        writeInfo.setMonth(monthOfYear + 1);
        writeInfo.setDay(dayOfMonth);
        writeInfo.setYMDToDateStr();

        m_tvDate.setText(writeInfo.getDateFormatted());
    }

    @Override
    public ArrayList<MyPhotoSelectImageData> getUploadImageList() {
        return PhotobookCommonUtils.getImageListFromTemplate(_template);
    }

    @Override
    public SnapsOrderAttribute getSnapsOrderAttribute() {
        return new SnapsOrderAttribute.Builder()
                .setActivity(this)
                .setEditMode(isModifyMode())
                .setHiddenPageList(getHiddenPageList())
                .setImageList(PhotobookCommonUtils.getImageListFromTemplate(_template))
                .setPageList(_pageList)
                .setPagerController(_loadPager)
                .setSnapsTemplate(_template)
                .setBackPageList(getBackPageList())
                .setCanvasList(_canvasList)
                .setTextOptions(getTextOptions())
                .create();
    }

    @Override
    public SnapsTemplate getTemplate() {
        return _template;
    }

    @Override
    public void requestMakeMainPageThumbnailFile(ISnapsCaptureListener captureListener) {
        if (!SnapsOrderManager.isUploadingProject()) {
            return;
        }

        setSnapsPageCaptureListener(captureListener);

        if(mCaptureFragment != null) {
            mCaptureFragment.getArguments().clear();
            mCaptureFragment.getArguments().putInt("index", 0);
            mCaptureFragment.getArguments().putBoolean("pageSave", true);
            mCaptureFragment.getArguments().putBoolean("preThumbnail", true);
            mCaptureFragment.getArguments().putBoolean("pageLoad", false);
            if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                // ?????? Destory ???????????? ????????? index ?????? ?????????.
                SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_CAPTURE);
            } else {
                mCaptureFragment.makeSnapsCanvas(true);
            }
        }
    }

    @Override
    public void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {}

    @Override
    public void onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {}

    @Override
    public void onUploadFailedOrgImgWhenSaveToBasket() {
        SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createUploadFailedImagePopupAttribute(this, SnapsDiaryDataManager.getDiarySeq(), false);

        SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute, new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
            @Override
            public void onShowUploadFailedImagePopup() {}

            @Override
            public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
                PhotobookCommonUtils.setUploadFailedIconVisibleStateToShow(_template);

                try {
                    if(mCanvasFragment != null) {
                        mCanvasFragment.makeSnapsCanvas();
                        mCanvasFragment.reLoadImageView();
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onPageLoadComplete(int page) {
        super.onPageLoadComplete(page);

        mSnapsDiaryTextView = (SnapsDiaryTextView) findViewById(mTextControl.getControlId());

        initDiaryUploader();
    }

    private void initDiaryUploader() {
        try {
            if (!isInitializedOrderManager) {
                isInitializedOrderManager = true;
                SnapsOrderManager.initialize(this);

                SnapsOrderManager.setImageUploadStateListener(this);
                SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    public SnapsDiaryTextView.ISnapsDiaryTextControlListener getDiaryTextControlListener() {
        return this;
    }
}
