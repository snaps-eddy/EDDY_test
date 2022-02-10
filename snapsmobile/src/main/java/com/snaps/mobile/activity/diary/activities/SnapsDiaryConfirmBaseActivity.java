package com.snaps.mobile.activity.diary.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.imp.ISnapsPageItemInterface;
import com.snaps.common.spc.view.SnapsDiaryTextView;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.SystemIntentUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.interfacies.SnapsDiaryEditActToFragmentBridgeActivity;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadOpserver;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsDiaryConfirmFragment;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.themebook.ImageEditActivity;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.order.ISnapsOrderStateListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;

import java.util.ArrayList;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FTextView;

/**
 * Created by ysjeong on 16. 3. 4..
 */
public abstract class SnapsDiaryConfirmBaseActivity extends SnapsDiaryEditActToFragmentBridgeActivity
        implements ISnapsDiaryUploadOpserver, SnapsDiaryTextView.ISnapsDiaryTextControlListener,
        ISnapsPageItemInterface, View.OnClickListener, ISnapsOrderStateListener, SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver {
    private static final String TAG = SnapsDiaryConfirmBaseActivity.class.getSimpleName();
    protected final int REQUEST_CODE_CHANGE_PICTURE = 101;
    protected final int REQUEST_CODE_MODIFY_PICTURE = 102;

    public SnapsDiaryConfirmFragment mCanvasFragment = null;

    protected LinearLayout m_lyDate = null;
    protected FTextView m_tvDate = null;
    protected FTextView m_tvRegisteredDate = null;
    protected FTextView m_tvContents = null;
    protected EditText m_etContents = null;
    protected ImageView m_ivWeather = null;
    protected ImageView m_ivFeels = null;
    protected ImageView m_ivDate = null;

    protected View mScrollEndView = null;

    protected PopoverView mPopupMenuView;
    protected RelativeLayout mRootView;
    protected ScrollView mScrollView;

    protected boolean m_isReadyComplete = false;

    protected SnapsTextControl mTextControl = null;

    protected SnapsDiaryConfirmFragment mCaptureFragment;

    protected SnapsDiaryListItem mEditItem;

    protected int m_iTempImageViewID = -1;

    protected SnapsBroadcastReceiver mReceiver = null;

    protected boolean m_isEditedPicture = false;

    protected boolean m_isEditedDate = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        checkIntentData();

        setContentView(R.layout.snaps_diary_confirm_layout);

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.registDiaryUploadObserver(this);
        dataManager.setIsWritingDiary(true);

        init();

        boolean permissionGranted = true;
        if(Build.VERSION.SDK_INT > 22 ) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if( shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) )
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
                else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION);
                permissionGranted = false;
            }
        }

        if (permissionGranted) {
            getTemplateHandler(getTemplateUrl());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const_VALUE.REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getTemplateHandler(getTemplateUrl());
                } else {
                    MessageUtil.alert(this, getString(R.string.need_to_permission_accept_for_get_phone_pictures), "", R.string.cancel, R.string.confirm_move_to_setting, false, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                SystemIntentUtil.showSystemSetting(SnapsDiaryConfirmBaseActivity.this);
                            }
                            SnapsDiaryConfirmBaseActivity.this.finish();
                        }
                    });
                }
                break;
        }
    }

    protected abstract void initHook();

    protected abstract void registerModules();

    protected abstract void checkIntentData();

    protected abstract void setNextButton(TextView textView);

    protected abstract void getTemplateHandler(final String TEMPLATE_URL);

    protected abstract void setDiaryContents();

    protected abstract void setTextViewProcess();

    protected abstract void performBackKeyPressed();

    protected abstract void performNextButton();

    protected abstract void performClickEditText();

    protected abstract void performClickDateBar();

    private void init() {
        m_tvDate = (FTextView) findViewById(R.id.snaps_diary_confirm_date_tv);
        m_lyDate = (LinearLayout) findViewById(R.id.snaps_diary_confirm_date_ly);
        m_tvRegisteredDate = (FTextView) findViewById(R.id.snaps_diary_confirm_registered_date_tv);
        m_tvContents = (FTextView) findViewById(R.id.snaps_diary_confirm_contents_tv);
        m_etContents = (EditText) findViewById(R.id.snaps_diary_confirm_contents_et);
        m_ivWeather = (ImageView) findViewById(R.id.snaps_diary_confirm_weather_iv);
        m_ivFeels = (ImageView) findViewById(R.id.snaps_diary_confirm_feels_iv);
        m_ivDate = (ImageView) findViewById(R.id.snaps_diary_confirm_date_icon_iv);
        mScrollView = (ScrollView) findViewById(R.id.snaps_diary_confitm_scroll_ly);
        mScrollEndView = findViewById(R.id.snaps_diary_confirm_contents_scroll_end_view);

        mRootView = (RelativeLayout) findViewById(R.id.rootLayout);

        TextView tvNext = (TextView) findViewById(R.id.ThemebtnTopNext);
        setNextButton(tvNext);
        tvNext.setOnClickListener(this);

        m_etContents.setOnClickListener(this);

        findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(this);
        findViewById(R.id.ThemeTitleLeft).setOnClickListener(this);

        //이미지가 들어가는 영역은 동적으로 바꿈..
        setImageLayerHeight();

        pageProgress = new DialogDefaultProgress(this);

        _pageList = new ArrayList<SnapsPage>();

        _canvasList = new ArrayList<Fragment>();

        initHook();

        registerModules();

        setTextViewProcess();

    }

    private String getTemplateUrl() {
        if(isNewWriteMode()) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            return dataManager.getTemplateFilePath();
        } else {
            if(mEditItem != null)
                return SnapsAPI.DOMAIN(false) + mEditItem.getFilePath();
            else
                return null;
        }
    }

    private void setImageLayerHeight() {
        FrameLayout imageLayer = (FrameLayout) findViewById(R.id.snaps_diary_confirm_fragment_ly);
        ViewGroup.LayoutParams layoutParams = imageLayer.getLayoutParams();
        layoutParams.height = UIUtil.getScreenWidth(this)
                - (int) getResources().getDimension(R.dimen.snaps_diary_list_margin)
                - (int) getResources().getDimension(R.dimen.snaps_diary_confirm_image_layer_fix_height);
        imageLayer.setLayoutParams(layoutParams);
    }

    protected boolean isNewWriteMode() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        return SnapsDiaryConstants.EDIT_MODE_NEW_WRITE == dataManager.getWriteMode();
    }

    protected boolean isModifyMode() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        return SnapsDiaryConstants.EDIT_MODE_MODIFY == dataManager.getWriteMode();
    }

    @Override
    public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {}

    @Override
    public void onPageLoadComplete(int page) {
        setDiaryContents();

        findTextControl();

        progressUnload();
    }

    /**
     *
     * Progress Popup 끝내기.
     */
    public void progressUnload() {
        SnapsTimerProgressView.destroyProgressView();

        if(pageProgress != null)
            pageProgress.dismiss();
    }

    @Override
    public void onOrderStateChanged(int state) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_CHANGE_PICTURE :
                m_isEditedPicture = true;
                refreshCanvasFragment(data);
                break;
            case REQUEST_CODE_MODIFY_PICTURE :
                m_isEditedPicture = true;
                refreshCanvasFragment(null);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SnapsUploadFailedImageDataCollector.isShowingUploadFailPopup()) return false;
            performBackKeyPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

        int id = v.getId();
        if (id == R.id.ThemeTitleLeftLy || id == R.id.ThemeTitleLeft)
            performBackKeyPressed();
        else if (id == R.id.popup_menu_photo_modify) // 사진편집
            requestModifyPhoto();
        else if (id == R.id.popup_menu_photo_change) // 사진 변경
            requestChangePhoto();
        else if(id == R.id.ThemebtnTopNext)
            performNextButton();
        else if(id == R.id.snaps_diary_confirm_contents_et)
            performClickEditText();
        else if(id == R.id.snaps_diary_confirm_date_ly || id == R.id.snaps_diary_confirm_date_icon_iv)
            performClickDateBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if(mReceiver != null)
                unregisterReceiver(mReceiver);

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            dataManager.removeDiaryUploadObserver(this);

            DataTransManager.releaseInstance();

            SnapsTimerProgressView.destroyProgressView();

            SnapsOrderManager.finalizeInstance();

            SnapsUploadFailedImageDataCollector.clearHistory(SnapsDiaryDataManager.getDiarySeq());

            pageProgressUnload();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        Config.setIS_MAKE_RUNNING(true);

        SnapsOrderManager.registerNetworkChangeReceiverOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        Config.setIS_MAKE_RUNNING(false);

        SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_APPLICATION);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SnapsOrderManager.unRegisterNetworkChangeReceiver();
    }

    @Override
    public void onReceiveData(Context context, Intent intent) {
        Dlog.d("onReceiveData() intent:" + intent);
        // 상황에 따라 팝어를 띄운다.
        // 데이터 타입 이미지, 텍스트
        // layer 타입, 배경, 이미지, 텍스트.
        // 편집 여부..()
        // 클래스 명. 아이디

        if (intent != null && intent.getDataString() == null) {
            boolean isLongClick = intent.getBooleanExtra("isLongClick", false);
            if (isLongClick) return;

            int control_id = intent.getIntExtra("control_id", -1);
            // String control_name = intent.getStringExtra("control_name");
            boolean isEdited = intent.getBooleanExtra("isEdited", false);

            m_iTempImageViewID = control_id;
            if (m_iTempImageViewID == -1)
                return;

            View v = findViewById(m_iTempImageViewID);
            if(v == null) return;

            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(v);
            Dlog.d("onReceiveData() snapsControl:" + snapsControl);

            Rect rect = new Rect();
            View popupView = mRootView.findViewById(m_iTempImageViewID);
            popupView.getGlobalVisibleRect(rect);
            Dlog.d("onReceiveData() getGlobalVisibleRect:" + rect.toString());

//			int popWidth = UIUtil.convertDPtoPX(getApplicationContext(), 100);
//			int popHeight = UIUtil.convertDPtoPX(getApplicationContext(), 40);
            int popWidth = UIUtil.convertDPtoPX(getApplicationContext(), 100);
            int popHeight = UIUtil.convertDPtoPX(getApplicationContext(), 37);


            mPopupMenuView = new PopoverView(this, R.layout.popmenu_photo_no_delete);

            //일기 서비스 종료 대응
            /*
            if (SnapsDiaryMainActivity.IS_END_OF_SERVICE) {
                if (!NTPClient.isEnableDiaryNewOrEdit()) {
                    //사진 변경이 불가능하고 편집만 가능한 팝업 메뉴로 변경한다.
                    mPopupMenuView = new PopoverView(this, R.layout.popmenu_photo_only_edit);
                }
            }
            */

            mPopupMenuView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));
            DataTransManager transMan = DataTransManager.getInstance();
            if(transMan != null) {
                ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
                if(coordInfo != null) {
//                    boolean isScaled = coordInfo.convertPopupOverRect(rect,popupView, mRootView, false);
//                    if(isScaled) {
//                        popWidth *= 4;
//                        popHeight *= 2f;
//                    }
                    mPopupMenuView.setArrowPosition(rect, coordInfo.getTranslateX(), coordInfo.getScaleFactor(), false);
                }
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            mPopupMenuView.showPopoverFromRectInViewGroup(mRootView, rect, PopoverView.PopoverArrowDirectionUp, true);

            // 일단 팝업을 띄우고, 컨트롤 아이디 임시 저장을 한다. 그럼 끝..
            try {
                // 일단 팝업을 띄우고, 컨트롤 아이디 임시 저장을 한다. 그럼 끝..
                ImageView imgView = (ImageView) findViewById(m_iTempImageViewID);
                if(imgView != null) {
                    SnapsControl control = PhotobookCommonUtils.getSnapsControlFromView(v);
                    if (control != null && control instanceof SnapsLayoutControl) {
                        SnapsLayoutControl press_control = (SnapsLayoutControl) control;
                        if (press_control.imgData != null) {
                            if (press_control.isUploadFailedOrgImg) {
                                MessageUtil.toast(this, R.string.select_upload_failed_org_img_msg, Gravity.CENTER);
                            } else if (press_control.isNoPrintImage) {
                                MessageUtil.noPrintToast(this, ResolutionConstants.NO_PRINT_TOAST_OFFSETX_BASIC, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC);
//                            MessageUtil.toast(this, R.string.phootoprint_warnning_message, Gravity.CENTER);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    protected void upload() {
        if(!m_isReadyComplete) return;

        SnapsOrderManager.performSaveToBasket(this);
    }

    private void loadLoginInfo() {
    }

    private SnapsDiaryConfirmFragment createDefaultFragment() {
        FrameLayout parentView = (FrameLayout) findViewById(R.id.snaps_diary_confirm_fragment_ly);

        SnapsDiaryConfirmFragment fragment = new SnapsDiaryConfirmFragment();
        Bundle arg = new Bundle();
        arg.putInt("index", 0);
        arg.putBoolean("pageLoad", true);
        arg.putBoolean("pageSave", false);

        if (fragment.getArguments() != null)
            fragment.getArguments().clear();

        fragment.setArguments(arg);

        if (!_canvasList.contains(fragment)) {
            _canvasList.add(fragment);
        }

        fragment.setParentView(parentView);

        return fragment;
    }

    protected void loadCanvas() {

        mCanvasFragment = createDefaultFragment();

        FragmentUtil.replce(R.id.snaps_diary_confirm_fragment_ly, SnapsDiaryConfirmBaseActivity.this, mCanvasFragment);

        m_isReadyComplete = true;

        setDiaryContents();
    }

    private void refreshCanvasFragment(Intent data) {
        if(data != null) {
            data.getExtras().setClassLoader( MyPhotoSelectImageData.class.getClassLoader() );
            MyPhotoSelectImageData d = (MyPhotoSelectImageData) data.getExtras().getSerializable("imgData");
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(this, m_iTempImageViewID);
            if(snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) return;

            SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;

            d.cropRatio = control.getRatio();
            d.IMG_IDX = PhotobookCommonUtils.getImageIDX(control.getPageIndex(), control.regValue);
            d.pageIDX = control.getPageIndex();
            d.mmPageWidth = StringUtil.isEmpty( _template.info.F_PAGE_MM_WIDTH ) ? 0 : Float.parseFloat( _template.info.F_PAGE_MM_WIDTH );
            d.pxPageWidth = StringUtil.isEmpty( _template.info.F_PAGE_PIXEL_WIDTH ) ? 0 : Integer.parseInt( _template.info.F_PAGE_PIXEL_WIDTH );
            d.controlWidth = control.width;

            control.imgData = d;
            control.angle = String.valueOf(d.ROTATE_ANGLE);
            control.imagePath = d.PATH;
            control.imageLoadType = d.KIND;
            control.isUploadFailedOrgImg = false;
            // 인쇄가능 여부..
            try {
                ResolutionUtil.isEnableResolution(Float.parseFloat(_template.info.F_PAGE_MM_WIDTH), Integer.parseInt(_template.info.F_PAGE_PIXEL_WIDTH), control);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            _galleryList = PhotobookCommonUtils.getImageListFromTemplate(_template);
        } else {
            ArrayList<MyPhotoSelectImageData> modifiedImgList = new ArrayList<>();
            DataTransManager dtMan = DataTransManager.getInstance();
            if(dtMan != null) {
                modifiedImgList = dtMan.getPhotoImageDataList();
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            _galleryList = PhotobookCommonUtils.getImageListFromTemplate(_template);
            if(_galleryList != null && modifiedImgList != null) {
                for (MyPhotoSelectImageData cropData : modifiedImgList) {
                    if (cropData.isModify == -1)
                        continue;

                    MyPhotoSelectImageData d = PhotobookCommonUtils.getMyPhotoSelectImageDataWithImgIdx(_galleryList, cropData.getImageDataKey());

                    if (d != null) {
                        d.CROP_INFO = cropData.CROP_INFO;
                        d.FREE_ANGLE = cropData.FREE_ANGLE;
                        d.ROTATE_ANGLE = cropData.ROTATE_ANGLE;
                        d.ROTATE_ANGLE_THUMB = cropData.ROTATE_ANGLE_THUMB;
                        d.isApplyEffect = cropData.isApplyEffect;
                        d.EFFECT_PATH = cropData.EFFECT_PATH;
                        d.EFFECT_THUMBNAIL_PATH = cropData.EFFECT_THUMBNAIL_PATH;
                        d.EFFECT_TYPE = cropData.EFFECT_TYPE;
                        d.isAdjustableCropMode = cropData.isAdjustableCropMode;
                        d.ADJ_CROP_INFO = cropData.ADJ_CROP_INFO;
                        d.ORIGINAL_ROTATE_ANGLE = cropData.ORIGINAL_ROTATE_ANGLE;
                        d.ORIGINAL_THUMB_ROTATE_ANGLE = cropData.ORIGINAL_THUMB_ROTATE_ANGLE;
                        d.screenWidth = cropData.screenWidth;
                        d.screenHeight = cropData.screenHeight;
                        d.editorOrientation = cropData.editorOrientation;
                        d.isUploadFailedOrgImage = cropData.isUploadFailedOrgImage;
                    }
                }
            }
        }

        if(mCanvasFragment != null) {
            mCanvasFragment.makeSnapsCanvas();
            mCanvasFragment.reLoadImageView();
        }
    }

    private void setGalleryDefaultRatio() {
        _galleryList = PhotobookCommonUtils.getImageListFromTemplate(_template);
        if(_galleryList == null || _galleryList.isEmpty()) return;

        double defaultRatio = 1;
        ArrayList<MyPhotoSelectImageData> _imageList = _galleryList;
        for(MyPhotoSelectImageData imgData : _imageList) {
            if(imgData.cropRatio != 0)
                defaultRatio = imgData.cropRatio;
            else
                imgData.cropRatio = defaultRatio;
        }
    }

    private void requestModifyPhoto() {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            if(writeInfo != null) {
                writeInfo.setContents(m_etContents.getText().toString());
            }

        mPopupMenuView.dissmissPopover(false);
        setGalleryDefaultRatio();

        ArrayList<MyPhotoSelectImageData> images = PhotobookCommonUtils.getMyPhotoSelectImageData(_template);

        Intent imageEditIntent = new Intent(getApplicationContext(), ImageEditActivity.class);

        DataTransManager dtMan = DataTransManager.getInstance();
        if(dtMan != null) {
            dtMan.setPhotoImageDataList(images);
        } else {
            DataTransManager.notifyAppFinish(this);
            return;
        }

        int idx = PhotobookCommonUtils.getImageIndex(this, images, m_iTempImageViewID);
        if(idx < 0) return;

        imageEditIntent.putExtra("dataIndex", idx);

        startActivityForResult(imageEditIntent, REQUEST_CODE_MODIFY_PICTURE);

        if (mCanvasFragment != null) {
            mCanvasFragment.destroyCanvas();
        }
    }

    private void requestChangePhoto() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if(writeInfo != null) {
            writeInfo.setContents(m_etContents.getText().toString());
        }

        SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(this, m_iTempImageViewID);
        if(snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) return;

        SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;

        mPopupMenuView.dissmissPopover(false);
        Setting.set(getApplicationContext(), "themekey", "");

        Intent intent = new Intent(getApplicationContext(), ImageSelectActivityV2.class);

        int recommendWidth = 0, recommendHeight = 0;
        Rect rect = ResolutionUtil.getEnableResolution(_template.info.F_PAGE_MM_WIDTH, _template.info.F_PAGE_PIXEL_WIDTH, control);
        if (rect != null) {
            recommendWidth = rect.right;
            recommendHeight = rect.bottom;
        }

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SINGLE_CHOOSE_TYPE)
                .setRecommendWidth(recommendWidth)
                .setRecommendHeight(recommendHeight)
                .create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_CODE_CHANGE_PICTURE);

    }

    private void findTextControl() {
        if(_template == null || _template.getPages() == null || _template.getPages().size() < 1) return;

        SnapsPage page = _template.getPages().get(0);
        ArrayList<SnapsControl> textControls = page.getTextControlList();
        if(textControls == null || textControls.size() < 1) return;

        mTextControl = (SnapsTextControl) textControls.get(0);
    }
}
