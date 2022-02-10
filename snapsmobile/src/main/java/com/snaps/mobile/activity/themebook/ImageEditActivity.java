package com.snaps.mobile.activity.themebook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap.EffectType;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.OrientationManager.OrientationChangeListener;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.SystemIntentUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.intro.fragment.PwdResetFragment;
import com.snaps.mobile.activity.themebook.SnapsImageEffector.IApplyEffectResultListener;
import com.snaps.mobile.activity.themebook.SnapsImageEffector.IBitmapProcessListener;
import com.snaps.mobile.activity.themebook.SnapsImageEffector.IEffectApplyListener;
import com.snaps.mobile.component.SnapsImgEffectTutorialView;
import com.snaps.mobile.component.image_edit_componet.EditorInitializeListener;
import com.snaps.mobile.component.image_edit_componet.SnapsImageCropView;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.GifTutorialView;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.mobile.activity.themebook.SnapsImageEffector.LOAD_TYPE_PREVIEW;
import static com.snaps.mobile.order.order_v2.datas.SnapsOrderState.IMAGE_EDITING;

@SuppressWarnings({"unchecked"})
public class ImageEditActivity extends CatchActivity implements ImageEditConstants, OrientationChangeListener, ISnapsHandler, EditorInitializeListener {
    private static final String TAG = ImageEditActivity.class.getSimpleName();

    private TextView titleBarText;
    private TextView completeBtn;
    private TextView currentImageIndexText;
    private TextView totalImageCountText;
    private TextView backBtn;

    private SnapsImageCropView imageCropView;

    private SnapsImgEffectTutorialView tutorialView = null;

    private ProgressBar progressBar = null;


    private Map<EffectType, EffectFilterThumbs> thumbnailViewMap = null;

    private HorizontalScrollView effectPreviewBar = null;
    private ScrollView effectPreviewBarForLandscapeMode = null;

    TextView mThemeTitle;
    TextView mCompleteBtn;

    TextView mIndex;

    ImageView mBackBtn;
    TextView mBackTextBtn;

//	SnapsCropImageView imgOrigin;

    SnapsImageEffector effector = null;
    Map<EffectType, EffectFilterThumbs> m_mapThumbs = null;

    HorizontalScrollView effectSelecteView = null;
    ScrollView effectSelecteViewVertical = null;
    boolean m_isActiveAnim = false;
    boolean m_isSaving = false;

    int m_iEachFilterViewSize = 0;

    private int eachEffectThumbnailViewSize = 0;


    // 스티커(6,2,1분할) 비율 넓이/높이
    private float imageRatio = 0.0f;

    private Thread resumeSyncker = null;
    private OrientationManager orientationManager;
    private int prevOrientation = Configuration.ORIENTATION_PORTRAIT;

    private MyPhotoSelectImageData tempImageData = null;
    private MyPhotoSelectImageData singEditImgData = null;

    private ArrayList<MyPhotoSelectImageData> imageList;
    private int imgIdx = 0;
    private int pageIdx = 0;

    private boolean isInitialized = false, isSingleImgEditMode = false, isDiaryProfilePhoto = false;
    private boolean isLandScapeMode = false;
    private boolean isBlockRotate = true;
    private boolean isActivityResumeFinished = false;
    private boolean isClickable = true;
    private boolean isActiveAnim = false;
    private boolean isSaving = false;

    private long lastRotateTime = 0;

    private SnapsHandler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fixScreenOrientationByProduct();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        if (isLandScapeMode) {
            setContentView(R.layout.activity_common_cropimage_landscape);
        } else {
            setContentView(R.layout.activity_common_cropimage);
        }

        initInstances();

        handleIntentData();

        createTempImageDataForRecovery();

        initLayout();

        boolean permissionGranted = true;
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
                else
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION);
                permissionGranted = false;
            }
        }

        if (permissionGranted) {
            startImageEdit();
        }
    }

    private void startImageEdit() {
        loadImg(LOAD_TYPE_PREVIEW, imgIdx);

        showTutorial();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const_VALUE.REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MessageUtil.toast(this, getString(R.string.complete_granted_permission_plz_retry));
                    ImageEditActivity.this.finish();
                } else {
                    MessageUtil.alert(this, getString(R.string.need_to_permission_accept_for_get_phone_pictures), "", R.string.cancel, R.string.confirm_move_to_setting, false, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                SystemIntentUtil.showSystemSetting(ImageEditActivity.this);
                            }
                            ImageEditActivity.this.finish();
                        }
                    });
                }
                break;
        }
    }

    private void fixScreenOrientationByProduct() {
        if (Const_PRODUCT.isSNSBook() || Config.isSnapsSticker()) {
            UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isLandScapeMode = true;
        } else
            isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);

        if (isLandScapeMode)
            prevOrientation = Configuration.ORIENTATION_LANDSCAPE;
    }

    private void handleIntentData() {
        isSingleImgEditMode = getIntent().getBooleanExtra("single_img_edit", false);
        isDiaryProfilePhoto = getIntent().getBooleanExtra("diary_profile", false);

        if (isSingleImgEditMode) {
            getIntent().getExtras().setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
            singEditImgData = (MyPhotoSelectImageData) getIntent().getSerializableExtra("single_img_data");
        } else {
            DataTransManager dtMan = DataTransManager.getInstance();
            if (dtMan != null) {
                imageList = dtMan.getPhotoImageDataList();
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            imgIdx = getIntent().getIntExtra("dataIndex", 0);
            pageIdx = getIntent().getIntExtra("pageIndex", 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void initInstances() {
        handler = new SnapsHandler(this);

        orientationManager = OrientationManager.getInstance(this);
        orientationManager.addOrientationOpserver(this);
        isBlockRotate = true;
        setEnableOrientationSensor(false);
    }

    private void createTempImageDataForRecovery() {
        tempImageData = new MyPhotoSelectImageData();
        tempImageData.set(getCurImgData());
    }

    @Override
    public void onPause() {
        super.onPause();

        SnapsOrderManager.setSnapsOrderStatePauseCode("");

        isActivityResumeFinished = false;
        setEnableOrientationSensor(false);
    }

    private void restoreRotateState() {
        if (isBlockRotate)
            return;
        if (orientationManager != null) {
            // onRestoreInstanceState가 호출 되기 전에 방향 전환이 되면 exception이 발생한다.
            if (handler != null)
                handler.sendEmptyMessageDelayed(HANDLER_MSG_NOTIFY_ORIENTATION_STATE, 700);
        }
    }

    protected void notifyOrientationState() {
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        isBlockRotate = false;
        setEnableOrientationSensor(true);

        int curOrientation = getResources().getConfiguration().orientation;

        changeRotatedLayout(curOrientation);
    }

    @Override
    public void onResume() {
        super.onResume();

        isActivityResumeFinished = true;

        SnapsOrderManager.setSnapsOrderStatePauseCode(IMAGE_EDITING);

        restoreRotateState();
    }

    @Override
    public void onOrientationChanged(int newOrientation) {
        if (orientationManager == null)
            return;
    }

    private void fixCurrentOrientation() {
        isBlockRotate = true;
        setEnableOrientationSensor(false);

        UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);
    }

    /**
     * Resume이 완료 되지 않은 시점에 화면을 복구하려다 보니, 오류가 나서 만든 꽁수..
     */
    private void waitActivityResume() {
        if (resumeSyncker != null && resumeSyncker.getState() == Thread.State.RUNNABLE)
            return;

        resumeSyncker = new Thread(new Runnable() {
            @Override
            public void run() {
                final int MAX = 5;
                int count = 0;
                while (!isActivityResumeFinished && count++ < MAX) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        recoveryLayout();
                    }
                });
            }
        });

        resumeSyncker.start();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        UIUtil.applyLanguage(this);
        super.onConfigurationChanged(newConfig);

//		if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//			changeRotatedLayout(Configuration.ORIENTATION_PORTRAIT);
//			return;
//		}
        if (OrientationSensorManager.isActiveAutoRotation(this) == false) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            changeRotatedLayout(Configuration.ORIENTATION_PORTRAIT);
            return;
        }

        try {
            if (isBlockRotate)
                return;

            changeRotatedLayout(newConfig.orientation);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void changeRotatedLayout(int orientation) {

        if (orientationManager == null)
            return;
//        if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
//            orientation = Configuration.ORIENTATION_PORTRAIT;
//        }
        if (OrientationSensorManager.isActiveAutoRotation(this) == false) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        if (orientation == prevOrientation)
            return;
        prevOrientation = orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isLandScapeMode) {
                isLandScapeMode = true;
            }
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isLandScapeMode) {
                isLandScapeMode = false;
            }
        } else {
            return;
        }

        try {
            saveCropInfoWhenRotate();

            if (isLandScapeMode)
                setContentView(R.layout.activity_common_cropimage_landscape);
            else
                setContentView(R.layout.activity_common_cropimage);

            initLayout();

            recoveryLayout();

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setDisableBeforeAndNextBtn() {
        LinearLayout prevBtn = (LinearLayout) findViewById(R.id.btn_before2);
        prevBtn.setClickable(false);

        TextView tvPrev = (TextView) findViewById(R.id.tv_before2);
        tvPrev.setTextColor(Color.parseColor("#ff555555"));

        ImageView ivPrev = (ImageView) findViewById(R.id.iv_before2);
        ivPrev.setImageResource(R.drawable.btn_prev_02_off);

        LinearLayout nextBtn = (LinearLayout) findViewById(R.id.btn_next2);
        nextBtn.setClickable(false);

        TextView tvNext = (TextView) findViewById(R.id.tv_next2);
        tvNext.setTextColor(Color.parseColor("#ff555555"));

        ImageView ivNext = (ImageView) findViewById(R.id.iv_next2);
        ivNext.setImageResource(R.drawable.btn_next_02_off);
    }

    private void initLayout() {
        try {
            titleBarText = (TextView) findViewById(R.id.ThemeTitleText);

            if (isDiaryProfilePhoto) {
                titleBarText.setText(R.string.snaps_diary);
                setDisableBeforeAndNextBtn();
            } else
                titleBarText.setText(getString(R.string.photo_modify_text));//"사진 편집");

            completeBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
            completeBtn.setText(getString(R.string.confirm));//"확인");

            currentImageIndexText = (TextView) findViewById(R.id.photo_count_current_index_tv);
            totalImageCountText = (TextView) findViewById(R.id.photo_count_current_total_count_tv);

            completeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveCropInfo(SAVE_TYPE_FINISH, imgIdx);// 현재 사진 crop정보 저장

                    SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickEnter)
                            .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                            .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                }
            });

            backBtn = (TextView) findViewById(R.id.ThemeTitleLeft);
            backBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                    SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickCancel)
                            .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                            .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                }
            });

            if (Config.isIdentifyPhotoPrint()) {
                //mIndex.setVisibility(View.GONE);
                findViewById(R.id.btn_before2).setVisibility(View.GONE);
                findViewById(R.id.btn_next2).setVisibility(View.GONE);

                //mBackBtn.setVisibility(View.GONE);
                //	mBackTextBtn = (TextView) findViewById(R.id.ThemeTitleLeftText);
                //	mBackTextBtn.setVisibility(View.VISIBLE);
                //	mBackTextBtn.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						finish();
//					}
//				});
            }

            if (findViewById(R.id.ThemeTitleLeftLy) != null)
                findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();

                        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickCancel)
                                .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                                .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                    }
                });

            LinearLayout navigatorLayout = (LinearLayout) findViewById(R.id.topView);

            if (isSingleImgEditMode) {
                navigatorLayout.setVisibility(View.GONE);
            } else {

                currentImageIndexText.setText(String.valueOf(imgIdx + 1));
                totalImageCountText.setText(String.valueOf(imageList.size()));

                if (imageList.size() <= 1)
                    setDisableBeforeAndNextBtn();

            }

            if (isLandScapeMode)
                effectPreviewBarForLandscapeMode = (ScrollView) findViewById(R.id.activity_effectiamgeview_horizontal_scrollview);
            else
                effectPreviewBar = (HorizontalScrollView) findViewById(R.id.activity_effectiamgeview_horizontal_scrollview);

            imageRatio = 0.7f;

            int iEachThumbnailSize = calculateThumbnailSize();

            progressBar = (ProgressBar) findViewById(R.id.progressImg);

            imageCropView = findViewById(R.id.imgOrigin);
            imageCropView.setProgress(progressBar);
            imageCropView.setLandScapeMode(isLandScapeMode);
            imageCropView.setEditorInitialListener(this);
            imageCropView.setPageIdx(pageIdx);

            if (effector == null) {
                effector = new SnapsImageEffector(this, imageCropView, progressBar, setThumbnailMaps());
            } else {
                effector.setResources(imageCropView, progressBar, setThumbnailMaps());
            }

            effector.setThumbnailSize(iEachThumbnailSize);

            effector.setStatusListener(imageCropView.getEffectStatusListener());
            effector.setRequestCropFile(isDiaryProfilePhoto);
            effector.setPageIdx(pageIdx);

        } catch (Exception e) {
            Dlog.e(TAG, e);
            finish();
        }
    }

    private void recoveryLayout() {
        if (!isActivityResumeFinished) {
            waitActivityResume();
            return;
        }

        MyPhotoSelectImageData curImgData = getCurImgData();

        curImgData.set(tempImageData);

        loadImg(SnapsImageEffector.LOAD_TYPE_CHANGED_ORIENTATION, imgIdx);
    }

    public void setEnableOrientationSensor(boolean enable) {
        if (enable) {
            if (orientationManager != null)
                orientationManager.enable();
        } else {
            if (orientationManager != null)
                orientationManager.disable();
        }
    }

    public void showTutorial() {

        if (Config.shouldShowPassportImageEditGuide()) {
//			MessageUtil.toast(this, getString(R.string.pass_port_image_edit_toast_msg));
        } else {
            if (isDiaryProfilePhoto) return;

            SnapsTutorialUtil.showGifView(this, new SnapsTutorialAttribute.Builder().setGifType(SnapsTutorialAttribute.GIF_TYPE.MOVE_HAND).create(), new GifTutorialView.CloseListener() {
                @Override
                public void close() {
//					if (imageList.size() > 1) {
//						SnapsTutorialUtil.showTooltip(ImageEditActivity.this, new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
//								.setText(getString(R.string.tutorial_next_image))
//								.setTargetView(findViewById(R.id.btn_next2)).create());
//					}
                }
            });
        }

        //예전 튜토리얼 지금은 안쓴다
//		if (Config.useKorean()) {ㅋㅋ
//			if ((Config.TEST_TUTORIAL || !Setting.getBoolean(this, SnapsImgEffectTutorialView.TUTORIAL_IMG_EFFECT))) {
//				TutorialView = new SnapsImgEffectTutorialView(this);
//				addContentView(mTutorialView, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
//			}
//		}

    }

    private int calculateThumbnailSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        float dip_scale = outMetrics.density;
        int marginValue = (int) (VALUE_BOTTOM_VIEW_MARGIN * dip_scale);
        int paddingValue = (int) (VALUE_BOTTOM_VIEW_PADDING * dip_scale);

        int iThumbEachSize = ((UIUtil.getScreenWidth(this) - ((marginValue * 4) + (paddingValue * 2))) / 5);

        eachEffectThumbnailViewSize = (int) ((iThumbEachSize + marginValue) * .6f);

        return iThumbEachSize;
    }

    private LinkedHashMap<EffectType, EffectFilterThumbs> setThumbnailMaps() {
        thumbnailViewMap = new LinkedHashMap<EffectType, EffectFilterThumbs>();

        ImageView ivOrigin = (ImageView) findViewById(R.id.activity_effectimage_tmb_origin_iv);
        ImageView ivGrayScale = (ImageView) findViewById(R.id.activity_effectimage_tmb_gray_scale_iv);
        ImageView ivSharpen = (ImageView) findViewById(R.id.activity_effectimage_tmb_sherpen_iv);
        ImageView ivSephia = (ImageView) findViewById(R.id.activity_effectimage_tmb_sephia_iv);
        ImageView ivWarm = (ImageView) findViewById(R.id.activity_effectimage_tmb_warm_iv);
        ImageView ivDawn = (ImageView) findViewById(R.id.activity_effectimage_tmb_dawn_iv);
        ImageView ivEmerald = (ImageView) findViewById(R.id.activity_effectimage_tmb_emerald_iv);
        ImageView ivVintage = (ImageView) findViewById(R.id.activity_effectimage_tmb_vintage_iv);
        ImageView ivBlackCat = (ImageView) findViewById(R.id.activity_effectimage_tmb_black_cat_iv);
        ImageView ivFilm = (ImageView) findViewById(R.id.activity_effectimage_tmb_film_iv);
        ImageView ivSnow = (ImageView) findViewById(R.id.activity_effectimage_tmb_snow_iv);
        ImageView ivOldLight = (ImageView) findViewById(R.id.activity_effectimage_tmb_old_light_iv);
        ImageView ivAurora = (ImageView) findViewById(R.id.activity_effectimage_tmb_aurora_iv);
        ImageView ivMemory = (ImageView) findViewById(R.id.activity_effectimage_tmb_memory_iv);
        ImageView ivWinter = (ImageView) findViewById(R.id.activity_effectimage_tmb_winter_iv);
        ImageView ivShady = (ImageView) findViewById(R.id.activity_effectimage_tmb_shady_iv);

        ImageView outLineOrigin = (ImageView) findViewById(R.id.activity_effectimage_tmb_origin_outline_iv);
        ImageView outLineGrayScale = (ImageView) findViewById(R.id.activity_effectimage_tmb_gray_scale_outline_iv);
        ImageView outLineSharpen = (ImageView) findViewById(R.id.activity_effectimage_tmb_sherpen_outline_iv);
        ImageView outLineSephia = (ImageView) findViewById(R.id.activity_effectimage_tmb_sephia_outline_iv);
        ImageView outWarm = (ImageView) findViewById(R.id.activity_effectimage_tmb_warm_outline_iv);
        ImageView outDawn = (ImageView) findViewById(R.id.activity_effectimage_tmb_dawn_outline_iv);
        ImageView outEmerald = (ImageView) findViewById(R.id.activity_effectimage_tmb_emerald_outline_iv);
        ImageView outVintage = (ImageView) findViewById(R.id.activity_effectimage_tmb_vintage_outline_iv);
        ImageView outLineBlackCat = (ImageView) findViewById(R.id.activity_effectimage_tmb_black_cat_outline_iv);
        ImageView outLineFilm = (ImageView) findViewById(R.id.activity_effectimage_tmb_film_outline_iv);
        ImageView outLineSnow = (ImageView) findViewById(R.id.activity_effectimage_tmb_snow_outline_iv);
        ImageView outLineOldLight = (ImageView) findViewById(R.id.activity_effectimage_tmb_old_light_outline_iv);
        ImageView outLineAurora = (ImageView) findViewById(R.id.activity_effectimage_tmb_aurora_outline_iv);
        ImageView outLineMemory = (ImageView) findViewById(R.id.activity_effectimage_tmb_memory_outline_iv);
        ImageView outLineWinter = (ImageView) findViewById(R.id.activity_effectimage_tmb_winter_outline_iv);
        ImageView outLineShady = (ImageView) findViewById(R.id.activity_effectimage_tmb_shady_outline_iv);

        TextView nameOrigin = (TextView) findViewById(R.id.activity_effectimage_tmb_origin_tv);
        TextView nameGrayScale = (TextView) findViewById(R.id.activity_effectimage_tmb_gray_scale_tv);
        TextView nameSharpen = (TextView) findViewById(R.id.activity_effectimage_tmb_sherpen_tv);
        TextView nameSephia = (TextView) findViewById(R.id.activity_effectimage_tmb_sephia_tv);
        TextView nameWarm = (TextView) findViewById(R.id.activity_effectimage_tmb_warm_tv);
        TextView nameDawn = (TextView) findViewById(R.id.activity_effectimage_tmb_dawn_tv);
        TextView nameEmerald = (TextView) findViewById(R.id.activity_effectimage_tmb_emerald_tv);
        TextView nameVintage = (TextView) findViewById(R.id.activity_effectimage_tmb_vintage_tv);
        TextView nameBlackCat = (TextView) findViewById(R.id.activity_effectimage_tmb_black_cat_tv);
        TextView nameFilm = (TextView) findViewById(R.id.activity_effectimage_tmb_film_tv);
        TextView nameSnow = (TextView) findViewById(R.id.activity_effectimage_tmb_snow_tv);
        TextView nameOldLight = (TextView) findViewById(R.id.activity_effectimage_tmb_old_light_tv);
        TextView nameAurora = (TextView) findViewById(R.id.activity_effectimage_tmb_aurora_tv);
        TextView nameMemory = (TextView) findViewById(R.id.activity_effectimage_tmb_memory_tv);
        TextView nameWinter = (TextView) findViewById(R.id.activity_effectimage_tmb_winter_tv);
        TextView nameShady = (TextView) findViewById(R.id.activity_effectimage_tmb_shady_tv);

        ProgressBar pbOrigin = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_origin_progress);
        ProgressBar pbGrayScale = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_gray_scale_progress);
        ProgressBar pbSharpen = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_sherpen_progress);
        ProgressBar pbSephia = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_sephia_progress);
        ProgressBar pbWarm = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_warm_progress);
        ProgressBar pbDawn = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_dawn_progress);
        ProgressBar pbEmerald = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_emerald_progress);
        ProgressBar pbVintage = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_vintage_progress);
        ProgressBar pbBlackCat = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_black_cat_progress);
        ProgressBar pbFilm = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_film_progress);
        ProgressBar pbSnow = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_snow_progress);
        ProgressBar pbOldLight = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_old_light_progress);
        ProgressBar pbAurora = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_aurora_progress);
        ProgressBar pbMemory = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_memory_progress);
        ProgressBar pbWinter = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_winter_progress);
        ProgressBar pbShady = (ProgressBar) findViewById(R.id.activity_effectimage_tmb_shady_progress);

        ivOrigin.setOnClickListener(effectClickListener);
        ivGrayScale.setOnClickListener(effectClickListener);
        ivSharpen.setOnClickListener(effectClickListener);
        ivSephia.setOnClickListener(effectClickListener);
        ivWarm.setOnClickListener(effectClickListener);
        ivDawn.setOnClickListener(effectClickListener);
        ivEmerald.setOnClickListener(effectClickListener);
        ivVintage.setOnClickListener(effectClickListener);
        ivBlackCat.setOnClickListener(effectClickListener);
        ivFilm.setOnClickListener(effectClickListener);
        ivSnow.setOnClickListener(effectClickListener);
        ivOldLight.setOnClickListener(effectClickListener);
        ivAurora.setOnClickListener(effectClickListener);
        ivMemory.setOnClickListener(effectClickListener);
        ivWinter.setOnClickListener(effectClickListener);
        ivShady.setOnClickListener(effectClickListener);

        // FIXME 급하게 만들려다보니 이따구로 만들었는데 동적으로 뷰를 생성하여 배치하는 방식으로 수정했으면 한다
        thumbnailViewMap.put(EffectType.ORIGIN, new EffectFilterThumbs.Builder().setIdx(0).setImgView(ivOrigin).setOutline(outLineOrigin).setProgress(pbOrigin).setName(nameOrigin).create());
        thumbnailViewMap.put(EffectType.GRAY_SCALE, new EffectFilterThumbs.Builder().setIdx(1).setImgView(ivGrayScale).setOutline(outLineGrayScale).setProgress(pbGrayScale).setName(nameGrayScale).create());
        thumbnailViewMap.put(EffectType.SEPHIA, new EffectFilterThumbs.Builder().setIdx(2).setImgView(ivSephia).setOutline(outLineSephia).setProgress(pbSephia).setName(nameSephia).create());
        thumbnailViewMap.put(EffectType.SHARPEN, new EffectFilterThumbs.Builder().setIdx(3).setImgView(ivSharpen).setOutline(outLineSharpen).setProgress(pbSharpen).setName(nameSharpen).create());
        thumbnailViewMap.put(EffectType.VINTAGE, new EffectFilterThumbs.Builder().setIdx(4).setImgView(ivVintage).setOutline(outVintage).setProgress(pbVintage).setName(nameVintage).create());
        thumbnailViewMap.put(EffectType.WARM, new EffectFilterThumbs.Builder().setIdx(5).setImgView(ivWarm).setOutline(outWarm).setProgress(pbWarm).setName(nameWarm).create());
        thumbnailViewMap.put(EffectType.DAWN, new EffectFilterThumbs.Builder().setIdx(6).setImgView(ivDawn).setOutline(outDawn).setProgress(pbDawn).setName(nameDawn).create());
        thumbnailViewMap.put(EffectType.AMERALD, new EffectFilterThumbs.Builder().setIdx(7).setImgView(ivEmerald).setOutline(outEmerald).setProgress(pbEmerald).setName(nameEmerald).create());
        thumbnailViewMap.put(EffectType.BLACK_CAT, new EffectFilterThumbs.Builder().setIdx(8).setImgView(ivBlackCat).setOutline(outLineBlackCat).setProgress(pbBlackCat).setName(nameBlackCat).create());
        thumbnailViewMap.put(EffectType.FILM, new EffectFilterThumbs.Builder().setIdx(9).setImgView(ivFilm).setOutline(outLineFilm).setProgress(pbFilm).setName(nameFilm).create());
        thumbnailViewMap.put(EffectType.SNOW, new EffectFilterThumbs.Builder().setIdx(10).setImgView(ivSnow).setOutline(outLineSnow).setProgress(pbSnow).setName(nameSnow).create());
        thumbnailViewMap.put(EffectType.OLD_LIGHT, new EffectFilterThumbs.Builder().setIdx(11).setImgView(ivOldLight).setOutline(outLineOldLight).setProgress(pbOldLight).setName(nameOldLight).create());
        thumbnailViewMap.put(EffectType.AURORA, new EffectFilterThumbs.Builder().setIdx(12).setImgView(ivAurora).setOutline(outLineAurora).setProgress(pbAurora).setName(nameAurora).create());
        thumbnailViewMap.put(EffectType.MEMORY, new EffectFilterThumbs.Builder().setIdx(13).setImgView(ivMemory).setOutline(outLineMemory).setProgress(pbMemory).setName(nameMemory).create());
        thumbnailViewMap.put(EffectType.WINTER, new EffectFilterThumbs.Builder().setIdx(14).setImgView(ivWinter).setOutline(outLineWinter).setProgress(pbWinter).setName(nameWinter).create());
        thumbnailViewMap.put(EffectType.SHADY, new EffectFilterThumbs.Builder().setIdx(15).setImgView(ivShady).setOutline(outLineShady).setProgress(pbShady).setName(nameShady).create());

        return (LinkedHashMap<EffectType, EffectFilterThumbs>) thumbnailViewMap;
    }

    OnClickListener effectClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isClickable)
                return;

            if (effector != null)
                effector.buttonClicked(v);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    void setIndexText() {
        if (isSingleImgEditMode)
            return;

//		mIndex.setText("(" + Integer.toString(imgIdx + 1) + " / " + Integer.toString(imageList.size()) + ")");
        currentImageIndexText.setText(String.valueOf(imgIdx + 1));
        totalImageCountText.setText(String.valueOf(imageList.size()));
    }

    private boolean isEdited() {
        if (imageList == null || imageList.isEmpty() || imageList.size() <= imgIdx) return false;
        boolean isRotated = false;
        MyPhotoSelectImageData data = isSingleImgEditMode ? singEditImgData : imageList.get(imgIdx);
        if (data != null)
            isRotated = data.ROTATE_ANGLE != data.ORIGINAL_ROTATE_ANGLE;

        return isRotated || effector.getCurrentEffectType() != EffectType.ORIGIN || imageCropView.isEdited();
    }

    private MyPhotoSelectImageData getCurImgData() {
        if (!isSingleImgEditMode) {
            if (imageList == null || imageList.size() < 1 || imgIdx > imageList.size() - 1)
                return null;
            else if (imgIdx < 0) imgIdx = 0;
        }
        return isSingleImgEditMode ? singEditImgData : imageList.get(imgIdx);
    }

    public void onClick(View v) {

        if (isClickable) {
            if (R.id.btn_before2 == v.getId()) {// 이전사진

                if ((imageCropView != null && (!imageCropView.isEditable() || imageCropView.isOnTouch())) || isSaving)
                    return;

                if ((imgIdx - 1) < 0) {
                    imgIdx = 0;
                    MessageUtil.toast(ImageEditActivity.this, getString(R.string.PhotoPrintpage_start));
                    return;
                }

                saveCropInfo(SAVE_TYPE_PREV, imgIdx);// 현재 사진 crop정보 저장

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickBefore)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                        .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
            } else if (R.id.btn_next2 == v.getId()) {// 다음사진

                if ((imageCropView != null && (!imageCropView.isEditable() || imageCropView.isOnTouch())) || isSaving || isSingleImgEditMode)
                    return;

                if ((imgIdx + 1) >= imageList.size()) {
                    MessageUtil.toast(ImageEditActivity.this, getString(R.string.PhotoPrintpage_finish));
                    imgIdx = imageList.size() - 1;
                    return;
                }

                saveCropInfo(SAVE_TYPE_NEXT, imgIdx);// 현재 사진 crop정보 저장

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickNext)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                        .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
            } else if (R.id.activity_common_cropimage_effect_rotate_iv == v.getId()) {

                // 회전을 막 누르면 싱크 문제로 오동작하기 때문에 꼼수로 막음.
                if (System.currentTimeMillis() - lastRotateTime < 1000 || effector.isChangingEffect() || imageCropView.isOnTouch() || effector.m_isLoading || isSaving)
                    return;

                lastRotateTime = System.currentTimeMillis();

                isClickable = false;

                MyPhotoSelectImageData data = getCurImgData();
                if (data != null) {
                    int angle = data.ROTATE_ANGLE + 90;
                    int thumbAngle = data.ROTATE_ANGLE_THUMB + 90;

                    if (angle >= 360)
                        angle = 0;

                    // 초기값이 -1이기 때문에 360대신 350 대입 함...
                    if (thumbAngle >= 350)
                        thumbAngle = 0;

                    // 각도가 89가 되는걸 방지하기위해...
                    if (thumbAngle > 80 && thumbAngle <= 90)
                        thumbAngle = 90;

                    data.ROTATE_ANGLE = angle;
                    data.ROTATE_ANGLE_THUMB = thumbAngle;
                    data.FREE_ANGLE = 0;

                    // 회전한 경우 movePercent를 초기화 한다.
                    data.CROP_INFO.movePercent = 0;

                    data.ADJ_CROP_INFO = new AdjustableCropInfo();
                    if (effector != null && effector.getCurrentEffectType() != null)
                        data.EFFECT_TYPE = effector.getCurrentEffectType().toString();

                    loadImg(SnapsImageEffector.LOAD_TYPE_ROTATE, imgIdx);
                }

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickRotate)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                        .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
            } else if (R.id.activity_common_cropimage_effect_filter_iv == v.getId()) {

                if (isSaving || effector.m_isLoading) {
                    return;
                }

                switchVisibleEffectFilterBar();

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickFilter)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                        .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
            } else if (R.id.activity_common_cropimage_effect_init_iv == v.getId()) {

                if (isShownEffectBar())
                    hideBottomEffectFilterBar();

                if (!isEdited() || imageCropView.isOnTouch() || isSaving) {
                    return;
                }

                initEditedContents();

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_clickReset)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                        .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (getCurImgData() != null ? getCurImgData().getImagePathForWebLog() : ""))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(PhotobookCommonUtils.findPageIndexThatContainImage(pageIdx, getCurImgData())))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
            }
        }
    }

    @Override
    public void initEditedContents() {
        isClickable = false;
        isInitialized = true;

        // Rotate 초기화
        MyPhotoSelectImageData data = getCurImgData();

        data.ROTATE_ANGLE = data.ORIGINAL_ROTATE_ANGLE;
        data.ROTATE_ANGLE_THUMB = data.ORIGINAL_THUMB_ROTATE_ANGLE;
        data.FREE_ANGLE = 0;
        data.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
        data.EFFECT_TYPE = EffectType.ORIGIN.toString();
        data.ADJ_CROP_INFO = new AdjustableCropInfo();

        // Crop 영역 초기화
        loadImg(LOAD_TYPE_PREVIEW, imgIdx);
    }

    private void switchVisibleEffectFilterBar() {

        if (isActiveAnim)
            return;

        TranslateAnimation transAnim = null;

        if (isLandScapeMode) {
            if (effectPreviewBarForLandscapeMode == null)
                return;

            if (effectPreviewBarForLandscapeMode.isShown()) {
                transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                makeEffectFilterView(effectPreviewBarForLandscapeMode, transAnim, false);
            } else {
                transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                makeEffectFilterView(effectPreviewBarForLandscapeMode, transAnim, true);
            }
        } else {
            if (effectPreviewBar == null)
                return;

            if (effectPreviewBar.isShown()) {
                transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
                makeEffectFilterView(effectPreviewBar, transAnim, false);
            } else {
                transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
                makeEffectFilterView(effectPreviewBar, transAnim, true);
            }
        }
    }

    private void fixOffsetSelectedEffectFilter() {
        if (effector == null || thumbnailViewMap == null)
            return;

        if (isLandScapeMode) {
            if (effectPreviewBarForLandscapeMode == null)
                return;

            handler.sendEmptyMessageDelayed(HANDLER_MSG_EFFECT_VIEW_SMOOTH_SCROLL_Y, 100);
        } else {
            if (effectPreviewBar == null)
                return;

            handler.sendEmptyMessageDelayed(HANDLER_MSG_EFFECT_VIEW_SMOOTH_SCROLL_X, 100);
        }
    }

    public void makeEffectFilterView(final View view, final TranslateAnimation transAnim, final boolean show) {
        if (show && effector != null)
            effector.requestMakeEffectBitmaps(new IEffectApplyListener() {
                @Override
                public void onReady() {
                    startAnimEffectFilterView(view, transAnim, true);
                }
            });
        else {
            startAnimEffectFilterView(view, transAnim, show);
        }
    }

    public void startAnimEffectFilterView(final View view, TranslateAnimation transAnim, final boolean show) {
        isActiveAnim = true;
        transAnim.setFillAfter(true);
        transAnim.setDuration(ANIM_TIME); // ANIMTIME
        transAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (show) {
                    if (view != null)
                        view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                isActiveAnim = false;
                if (!show) {
                    if (view != null) {
                        view.clearAnimation();
                        view.setVisibility(View.INVISIBLE);
                    }
                } else
                    fixOffsetSelectedEffectFilter();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
        });

        view.startAnimation(transAnim);
    }

    private void hideBottomEffectFilterBar() {

        TranslateAnimation transAnim = null;
        if (isLandScapeMode) {
            if (effectPreviewBarForLandscapeMode == null)
                return;
            transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            makeEffectFilterView(effectPreviewBarForLandscapeMode, transAnim, false);
        } else {
            if (effectPreviewBar == null)
                return;
            transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            makeEffectFilterView(effectPreviewBar, transAnim, false);
        }
    }

    void saveCropInfoWhenRotate() {
        if (!imageCropView.isValidArea() || tempImageData == null)
            return;


        tempImageData.set(getCurImgData());

        tempImageData.isAdjustableCropMode = true;
        tempImageData.isCheckedOldEditInfo = false;
        tempImageData.editorOrientation = isLandScapeMode ? "l" : "p";


        AdjustableCropInfo cropInfo = imageCropView != null ? imageCropView.getAdjustCropInfo() : null;
        if (cropInfo != null) {
            tempImageData.ADJ_CROP_INFO = cropInfo;
            tempImageData.screenWidth = imageCropView.getMeasuredWidth();
            tempImageData.screenHeight = imageCropView.getMeasuredHeight();
        }

        if (effector != null && effector.getCurrentEffectType() != null)
            tempImageData.EFFECT_TYPE = effector.getCurrentEffectType().toString();
    }

    void saveCropInfo(final byte SAVE_TYPE, int idx) {

        if (!imageCropView.isValidArea() || imageCropView.isOnTouch())
            return;

        if (effector != null && effector.m_isLoading) {
            Toast.makeText(this, getString(R.string.image_saving_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isSingleImgEditMode && (imageList == null || imageList.size() <= idx || idx < 0))
            return;

        isSaving = true;

        imageCropView.setRotateMode(false);

        MyPhotoSelectImageData imageData = getCurImgData();

        imageData.isModify = 0;
        imageData.isAdjustableCropMode = true;
        imageData.editorOrientation = isLandScapeMode ? "l" : "p";

        AdjustableCropInfo cropInfo = imageCropView != null ? imageCropView.getAdjustCropInfo() : null;
        if (cropInfo != null) {
            imageData.ADJ_CROP_INFO = cropInfo;
            imageData.screenWidth = imageCropView.getMeasuredWidth();
            imageData.screenHeight = imageCropView.getMeasuredHeight();
        }

        if (isShownEffectBar()) {
            // 하단 바를 숨긴다.
            hideBottomEffectFilterBar();

            Message msg = new Message();
            msg.what = HANDLER_MSG_SAVE_EFFECT;
            msg.arg1 = SAVE_TYPE;
            handler.sendMessageDelayed(msg, ANIM_TIME);
        } else {
            saveAppliedEffect(SAVE_TYPE);
        }
    }

    private boolean isShownEffectBar() {
        boolean isNeedHideEffectBar;
        if (isLandScapeMode) {
            isNeedHideEffectBar = effectPreviewBarForLandscapeMode != null && effectPreviewBarForLandscapeMode.isShown();
        } else {
            isNeedHideEffectBar = effectPreviewBar != null && effectPreviewBar.isShown();
        }
        return isNeedHideEffectBar;
    }

    private void saveAppliedEffect(final byte SAVE_TYPE) {

        if (effector == null) return;

        effector.showProgress(true);

        effector.commitEffect(() -> {

            runOnUiThread(() -> {
                switch (SAVE_TYPE) {
                    case SAVE_TYPE_NEXT:
                        isClickable = false;

                        if (imageCropView != null)
                            imageCropView.setVisibility(View.INVISIBLE);

                        loadImg(LOAD_TYPE_PREVIEW, ++imgIdx);
                        break;
                    case SAVE_TYPE_PREV:
                        isClickable = false;

                        if (imageCropView != null)
                            imageCropView.setVisibility(View.INVISIBLE);

                        loadImg(LOAD_TYPE_PREVIEW, --imgIdx);
                        break;
                    case SAVE_TYPE_FINISH:
                        if (effector != null)
                            effector.waitFinishCommitTask(() -> {
                                if (isSingleImgEditMode) {
                                    if (singEditImgData != null) {
                                        singEditImgData.editorOrientation = isLandScapeMode ? "l" : "p";
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("single_img_data", singEditImgData);
                                    Intent itt = getIntent();
                                    itt.putExtras(bundle);

                                    ImageEditActivity.this.setResult(RESULT_OK, itt);
                                } else {
                                    ImageEditActivity.this.setResult(RESULT_OK);
                                }

                                ImageEditActivity.this.finish();
                            });
                        break;
                }
            });

            isSaving = false;
        });
    }

    void loadImg(byte loadType, int idx) {
        if (!isSingleImgEditMode && (imageList == null || imageList.size() <= idx || idx < 0))
            return;

        MyPhotoSelectImageData currentImg = getCurImgData();
        if (currentImg == null)
            return;

        if (currentImg.isNoPrint) {
            if (isLandScapeMode) {
                MessageUtil.noPrintToast(this, ResolutionConstants.NO_PRINT_TOAST_OFFSETX_LANDSCAPE_PRINT_EDIT, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_LANDSCAPE_PRINT_EDIT);
            } else {
                MessageUtil.noPrintToast(this, ResolutionConstants.NO_PRINT_TOAST_OFFSETX_PRINT_EDIT, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_PRINT_EDIT);
            }
//			MessageUtil.toast(this, R.string.phootoprint_warnning_message, Gravity.CENTER);
        }
        //이미지를 로딩하는 중에는 화면 회전을 막는다.
        fixCurrentOrientation();

        // 인덱스 텍스트를 표시한다.
        setIndexText();

        if (currentImg.ORIGINAL_ROTATE_ANGLE == -999)
            currentImg.ORIGINAL_ROTATE_ANGLE = currentImg.ROTATE_ANGLE;

        if (currentImg.ORIGINAL_THUMB_ROTATE_ANGLE == -999)
            currentImg.ORIGINAL_THUMB_ROTATE_ANGLE = currentImg.ROTATE_ANGLE_THUMB;

        imageRatio = (float) currentImg.cropRatio;

        imageCropView.setAdjustClipBound(getCurImgData(), imageRatio, imageRatio, true);

        imageCropView.setEdited(false);

        if (effector != null) {

            if (isInitialized) {
                isInitialized = false;
            }

            if (loadType == LOAD_TYPE_PREVIEW) {
                if (isSingleImgEditMode)
                    effector.waitFinishAllThread(singEditImgData, idx, loadType);
                else
                    effector.waitFinishAllThread(imageList, idx, loadType);
            } else {
                if (isSingleImgEditMode)
                    effector.loadImage(singEditImgData, idx, loadType);
                else
                    effector.loadImage(imageList, idx, loadType);
            }
        }

        handler.sendEmptyMessageDelayed(HANDLER_MSG_SET_CLICK_ABLE, 1000);

        effector.setBaseBitmapCreateListener(new IBitmapProcessListener() {
            @Override
            public void onBaseBitmapCreated(Bitmap bmp) {
            }

            @Override
            public void onBaseBitmapCreated() {
                if (Const_PRODUCT.isSNSBook() || Config.isSnapsSticker())
                    return; //FIXME 카카오북은 무조건 가로 모드라서..
                if (handler != null)
                    handler.sendEmptyMessageDelayed(HANDLER_MSG_NOTIFY_ORIENTATION_STATE, 1000);
            }
        });
    }

    private void delectSnapsEffectCacheFiles() {
        try {
            String path = Config.getExternalCacheDir(this) + "/snaps/effect/";

            File file = new File(path);
            if (file.isDirectory()) {
                if (getDirectorySize(file) > LIMIT_EFFECT_CASH_MAX_SIZE) {
                    deleteEffectCashDirectory(file);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public long getDirectorySize(File directory) {
        long length = 0;
        File[] files = directory.listFiles();
        if (files == null) return length;

        for (File file : files) {
            if (file.isFile())
                length += file.length();
            else
                length += getDirectorySize(file);
        }
        return length;
    }

    @SuppressWarnings("rawtypes")
    public void deleteEffectCashDirectory(File directoryOrFile) throws Exception {
        if (directoryOrFile == null)
            return;

        File[] files = directoryOrFile.listFiles();
        if (files == null) return;

        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });

        for (int ii = files.length - 1; ii >= 0; ii--) {
            if (getDirectorySize(directoryOrFile) < ORGANIZED_EFFECT_CASH_SIZE)
                break;
            files[ii].delete();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (imageCropView != null)
                imageCropView.recycleBitmaps();

            ImageUtil.recycleBitmap(imageCropView);

            if (effector != null)
                effector.releaseBitmaps();

            delectSnapsEffectCacheFiles();

            if (orientationManager != null) {
                orientationManager.removeOpserver(this);
            }

            //TODO  메모리 릭 발생하면 주석 ..
            View root = findViewById(R.id.image_edit_activity_root_layout);
            if (root != null) {
                ViewUnbindHelper.unbindReferences(root, null, false);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        super.onDestroy();
    }


    private static final int HANDLER_MSG_NOTIFY_ORIENTATION_STATE = 0;
    private static final int HANDLER_MSG_SET_CLICK_ABLE = 1;
    private static final int HANDLER_MSG_EFFECT_VIEW_SMOOTH_SCROLL_Y = 2;
    private static final int HANDLER_MSG_EFFECT_VIEW_SMOOTH_SCROLL_X = 3;
    private static final int HANDLER_MSG_SAVE_EFFECT = 4;

    @Override
    public void handleMessage(Message msg) {
        if (msg == null)
            return;

        try {
            switch (msg.what) {
                case HANDLER_MSG_NOTIFY_ORIENTATION_STATE:
                    notifyOrientationState();
                    break;
                case HANDLER_MSG_SET_CLICK_ABLE:
                    isClickable = true;
                    break;
                case HANDLER_MSG_EFFECT_VIEW_SMOOTH_SCROLL_Y:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (effector == null || thumbnailViewMap == null || effectPreviewBarForLandscapeMode == null)
                                return;
                            final EffectType effectType = effector.getCurrentEffectType();
                            EffectFilterThumbs thumb = thumbnailViewMap.get(effectType);
                            if (thumb != null) {
                                int eachFilterViewHeight = eachEffectThumbnailViewSize + UIUtil.convertDPtoPX(ImageEditActivity.this, 8);
                                int offSet = (eachFilterViewHeight * thumb.getIdx());
                                effectPreviewBarForLandscapeMode.smoothScrollTo(0, offSet);
                            }
                        }
                    });
                    break;
                case HANDLER_MSG_EFFECT_VIEW_SMOOTH_SCROLL_X:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (effector == null || thumbnailViewMap == null || effectPreviewBarForLandscapeMode == null)
                                return;
                            final EffectType effectType = effector.getCurrentEffectType();
                            EffectFilterThumbs thumb = thumbnailViewMap.get(effectType);
                            if (thumb != null) {
                                int offSet = (eachEffectThumbnailViewSize * thumb.getIdx()) - (eachEffectThumbnailViewSize * 2);
                                effectPreviewBar.smoothScrollTo(offSet, 0);
                            }
                        }
                    });
                    break;
                case HANDLER_MSG_SAVE_EFFECT:
                    byte saveType = (byte) msg.arg1;
                    saveAppliedEffect(saveType);
                    break;

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}