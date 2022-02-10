package com.snaps.mobile.activity.photoprint;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.parser.GetNewPhotoPrintSaveXMLHandler;
import com.snaps.common.data.parser.GetPhotoPrintSaveXMLHandler;
import com.snaps.common.push.PushManager;
import com.snaps.common.structure.photoprint.GridSpacingItemDecoration;
import com.snaps.common.structure.photoprint.PhotoPrintListAdapter;
import com.snaps.common.structure.photoprint.PhotoPrintListItemHolder;
import com.snaps.common.structure.photoprint.json.PhotoPrintJsonObjectLayer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ImageDirectLoader;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_SnapsLoginInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDocumentHandler;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintProject;
import com.snaps.mobile.activity.photoprint.model.ActivityActionListener;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.activity.photoprint.model.ScrollChangeListener;
import com.snaps.mobile.activity.photoprint.model.TemplateDataHandler;
import com.snaps.mobile.activity.photoprint.view.PhotoPrintEditLayout;
import com.snaps.mobile.activity.photoprint.view.PhotoPrintMenuLayout;
import com.snaps.mobile.activity.photoprint.view.ScrollObserveRecyclerView;
import com.snaps.mobile.activity.photoprint.view.SpeechBubbleTutorialView;
import com.snaps.mobile.activity.webview.DetailProductWebviewActivity;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.SnapsUploadDialog;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;
import com.snaps.mobile.service.SnapsPhotoUploader;
import com.snaps.mobile.service.SnapsUploadState;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.mobile.utils.ui.SnapsBitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;

/**
 * Created by songhw on 2017. 3. 2..
 */

public class NewPhotoPrintListActivity extends SnapsBaseFragmentActivity implements ActivityActionListener, TemplateDataHandler, ScrollChangeListener {
    private static final String TAG = NewPhotoPrintListActivity.class.getSimpleName();

    public static final int REQUEST_CODE_PHOTOPRINT_CHANGE = 1000;
    private static final int CART_IMAGE_SIZE = 550;

    private PhotoPrintDocumentHandler documentHandler;

    private PhotoPrintListAdapter smallViewAdapter, largeViewAdapter;
    private PhotoPrintMenuLayout menuLayout;
    private PhotoPrintEditLayout editLayout;

    private android.os.AsyncTask<Void, Void, Void> uploadTask = null;
    private SnapsUploadDialog uploadDialog = null;

    private SpeechBubbleTutorialView tutorialView;

    private DialogDefaultProgress progress;

    private ScrollObserveRecyclerView recyclerView;
    private RecyclerViewDisabler recyclerViewDisabler;

    private ValueAnimator magneticAnimator;

    private PhotoPrintListItemHolder currentItem;
    private ImageView dummyImage;
    private float[] imageInfo;

    private String projCode, prodCode, userNo, userId;

    private boolean isEditMode = false; // 재편집 모드.
    private boolean doingMagneticAnimation = false;
    private boolean doingMenuShowAnimation = false;
    private boolean doingMenuHideAnimation = false;
    private boolean isDoingImageAnimation = false;
    private boolean isDoingCartProcess = false;
    private boolean isBackPressed = false;

    private final int MAGNETIC_ANIMATION_MAX_DURATION = 100;
    private final int BORDER_MM_THICKNESS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.activity_photo_print_list);
        Config.setIS_MAKE_RUNNING(true);

        progress = new DialogDefaultProgress(this);

        menuLayout = (PhotoPrintMenuLayout) findViewById(R.id.menu_layout);

        if (getIntent().hasExtra(Const_EKEY.HOME_SELECT_PRODUCT_CODE))
            prodCode = getIntent().getStringExtra(Const_EKEY.HOME_SELECT_PRODUCT_CODE);
        else if (getIntent().hasExtra(Const_EKEY.MYART_PROJCODE))
            prodCode = getIntent().getStringExtra(Const_EKEY.MYART_PRODCODE);

        // 프로젝트 코드
        if (getIntent().hasExtra(Const_EKEY.MYART_PROJCODE)) { // 재편집 모드.
            projCode = getIntent().getStringExtra(Const_EKEY.MYART_PROJCODE);
            isEditMode = true;
            PhotoPrintDataManager.getInstance().init();
        } else
            projCode = null;

        getPhotoDatas();

        // 폴더 생성 및 설정.
        try {
            Config.checkThumbnailFileDir();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Config.setIS_MAKE_RUNNING(true);
        userNo = SnapsLoginManager.getUUserNo(this);

        if (uploadTask != null) {
            uploadTask.cancel(true);
            uploadTask = null;
            uploadDialog.dismiss();
            uploadDialog = null;
            return;
        }

        if (!isPhotoDetailEditMode()) {
            PhotoPrintDataManager.getInstance().setListener(this);
            if (recyclerView != null) {
                for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                    if (recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) instanceof PhotoPrintListItemHolder)
                        ((PhotoPrintListItemHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i))).clearDatas();
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private boolean isPhotoDetailEditMode() {
        return editLayout != null;
    }

    private void initLayout() {
        findViewById(R.id.title_layout).bringToFront();
        findViewById(R.id.back_button_area).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.cart_button).setOnClickListener(cartButtonClickListener);

        menuLayout.refreshStatus(true);

        PhotoPrintDataManager photoPrintDataManager = PhotoPrintDataManager.getInstance();
        if (photoPrintDataManager.isSelectMode())
            photoPrintDataManager.changeSelectMode(false);
        if (photoPrintDataManager.isEditMode())
            photoPrintDataManager.changeModifyMode(false);

        photoPrintDataManager.setIndicatorHeight(UIUtil.getIndicatorHeight(this));
        largeViewAdapter = new PhotoPrintListAdapter(this, true, itemClickListener);
        smallViewAdapter = new PhotoPrintListAdapter(this, false, itemClickListener);
        recyclerView = (ScrollObserveRecyclerView) findViewById(R.id.list);
        recyclerView.setListener(this);
        int margins = UIUtil.convertDPtoPX(this, 4);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(margins, margins, margins, margins, 1, 1));
        recyclerView.setOnTouchListener(recyclerViewTouchListener);

        changeListMode(photoPrintDataManager.isLargeView());

        showTutorial();
    }

    private void showTutorial() {
        SpeechBubbleTutorialView.Builder builder = null;
        builder = new SpeechBubbleTutorialView.Builder()
                .setTitle(getString(R.string.img_sel_phone_pic_tutorial_pinch))
                .setTutorialType(SpeechBubbleTutorialView.Builder.TUTORIAL_TYPE.TYPE_PHOTO_PRINT).create();

        closeTutorial();
        tutorialView = new SpeechBubbleTutorialView(this, builder);

        if (builder != null)
            addContentView(tutorialView, new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void closeTutorial() {
        if (tutorialView == null || tutorialView.getParent() == null) return;

        ((ViewGroup) tutorialView.getParent()).removeView(tutorialView);
        tutorialView = null;
    }

    private boolean checkNeedToShowTutorial() {
        String lastShownDate = SpeechBubbleTutorialView.getShownDatePhoneFragmentTutorial(this, SpeechBubbleTutorialView.Builder.TUTORIAL_TYPE.TYPE_PHOTO_PRINT);
        if (StringUtil.isEmpty(lastShownDate)) return true;

        try {
            long lLastShownDate = Long.parseLong(lastShownDate);

            Calendar calendarCurrent = Calendar.getInstance();

            Calendar calendarLastShownDate = Calendar.getInstance();
            calendarLastShownDate.setTimeInMillis(lLastShownDate);
            calendarLastShownDate.add(Calendar.DAY_OF_MONTH, SpeechBubbleTutorialView.Builder.TUTORIAL_TYPE.TYPE_PHOTO_PRINT.getNoShowDays()); //마지막으로 보여지고 나서 15일 동안 보여지지 않는다.

            if (calendarCurrent.before(calendarLastShownDate))
                return false;

        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        return true;
    }

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recyclerView == null) return;
            PhotoPrintDataManager photoPrintDataManager = PhotoPrintDataManager.getInstance();
            int itemPosition = recyclerView.getChildLayoutPosition(v) - 1;
            if (itemPosition < 0 || itemPosition > photoPrintDataManager.getDataCount() - 1) return;

            if (PhotoPrintDataManager.getInstance().toggleSelect(itemPosition))
                recyclerView.getAdapter().notifyDataSetChanged();
            else
                showDetailEditLayout(v, itemPosition);
        }
    };

    private View.OnClickListener cartButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //전체적용 버튼이 표시되고 있는 경우 장바구니 버튼 동작 차단 (사용자가 전체적용 버튼을 클릭하지 않고 장바구니 버튼을 누르면 편집 정보가 적용되지 않고 장바구니에 저장됨)
            //고객이 자신이 편집한 것과 다르다는 CS가 인입되고 있음
            if (PhotoPrintDataManager.getInstance().isModifyMode()) {
                MessageUtil.alert(NewPhotoPrintListActivity.this, R.string.photo_print_message_option_not_applied);
                return;
            }

            ArrayList<PhotoPrintData> datas = PhotoPrintDataManager.getInstance().getDatas();
            if (datas == null || datas.isEmpty()) return;

            if (SnapsTPAppManager.isThirdPartyApp(NewPhotoPrintListActivity.this))
                confirmUploadChanged(false);
            else {
                userId = SnapsLoginManager.getUUserNo(NewPhotoPrintListActivity.this);

                if (StringUtil.isEmpty(userId))
                    userId = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_NO, "");

                if (StringUtil.isEmpty(userId)) { // 로그인 안되었을떄
                    String snapsUserId = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_ID);
                    String snapsUserPwd = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_PWD);

                    if (!snapsUserId.equals("") && !snapsUserPwd.equals("")) {
                        doLogin(snapsUserId, snapsUserPwd);
                        return;
                    }
                    SnapsLoginManager.startLogInProcess(NewPhotoPrintListActivity.this, Const_VALUES.LOGIN_P_LOGIN);
                } else
                    confirmUploadChanged(false);
            }
        }
    };

    private void showDetailEditLayout(View v, int itemPosition) {
        if (editLayout != null || isDoingCartProcess) return;

        closeTutorial();
        enableScroll(false, true);
        UIUtil.hideKeyboard(this, v);

        RelativeLayout detailLayoutContainer = (RelativeLayout) findViewById(R.id.detail_layout_container);
        detailLayoutContainer.bringToFront();
        detailLayoutContainer.removeAllViews();
        detailLayoutContainer.setVisibility(View.INVISIBLE);

        editLayout = new PhotoPrintEditLayout(this);
        editLayout.init(detailLayoutContainer, itemPosition);
        detailLayoutContainer.addView(editLayout);

        dummyImage = (ImageView) findViewById(R.id.dummy_image);
        currentItem = (PhotoPrintListItemHolder) recyclerView.getChildViewHolder(v);
        PhotoPrintData data = currentItem.getMyPhotoPrintData();
        imageInfo = currentItem.getImageInfo(data);
        imageInfo[1] -= UIUtil.convertDPtoPX(this, 25); // TODO 보정.
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dummyImage.getLayoutParams();
        params.leftMargin = (int) (imageInfo[0]);
        params.topMargin = (int) (imageInfo[1]);
        params.width = (int) imageInfo[2];
        params.height = (int) imageInfo[3];
        dummyImage.setLayoutParams(params);
        dummyImage.setRotation(data.getAngle());

        //CS 대응
        if (Config.isDevelopVersion()) {
            logImageData(data);
        }

        final Bitmap bitmap = PhotoPrintDataManager.getInstance().getResource(itemPosition);
        if (bitmap == null || bitmap.isRecycled())
            doWhenBitmapNullOrRecycled(data, itemPosition);
        else {
            dummyImage.setImageBitmap(bitmap);
            afterImageSetDone();
        }
    }

    //CS 대응
    private void logImageData(PhotoPrintData photoPrintData) {
        MyPhotoSelectImageData myPhotoSelectImageData = photoPrintData.getMyPhotoSelectImageData();
        if (myPhotoSelectImageData == null) return;

        String domain = SnapsAPI.DOMAIN();
        StringBuilder sb = new StringBuilder();

        sb.append("\n");

        sb.append("IMAGE_ID : ").append(myPhotoSelectImageData.IMAGE_ID).append("\n");

        if (myPhotoSelectImageData.THUMBNAIL_PATH != null && myPhotoSelectImageData.THUMBNAIL_PATH.length() > 0) {
            sb.append("THUMBNAIL_PATH : ").append(domain).append(myPhotoSelectImageData.THUMBNAIL_PATH).append("\n");
        }

        if (myPhotoSelectImageData.ORIGINAL_PATH != null && myPhotoSelectImageData.ORIGINAL_PATH.length() > 0) {
            sb.append("ORIGINAL_PATH : ").append(domain).append(myPhotoSelectImageData.ORIGINAL_PATH).append("\n");
        }

        sb.append("PATH : ").append(myPhotoSelectImageData.PATH).append("\n");
        sb.append("LOCAL_THUMBNAIL_PATH : ").append(myPhotoSelectImageData.LOCAL_THUMBNAIL_PATH).append("\n");

        if (myPhotoSelectImageData.EFFECT_PATH != null && myPhotoSelectImageData.EFFECT_PATH.length() > 0) {
            sb.append("EFFECT_PATH : ").append(myPhotoSelectImageData.EFFECT_PATH).append("\n");
        }

        if (myPhotoSelectImageData.EFFECT_THUMBNAIL_PATH != null && myPhotoSelectImageData.EFFECT_THUMBNAIL_PATH.length() > 0) {
            sb.append("EFFECT_THUMBNAIL_PATH : ").append(myPhotoSelectImageData.EFFECT_THUMBNAIL_PATH).append("\n");
        }

        sb.append("ORIGINAL W H : ").append(myPhotoSelectImageData.F_IMG_WIDTH).append(" x ");
        sb.append(myPhotoSelectImageData.F_IMG_HEIGHT).append("\n");

        ExifUtil.SnapsExifInfo snapsExifInfo = myPhotoSelectImageData.getExifInfo();
        if (snapsExifInfo != null) {
            sb.append("Exif OrientationTag : ").append(snapsExifInfo.getOrientationTag()).append("\n");
        }

        sb.append("ROTATE_ANGLE : ").append(myPhotoSelectImageData.ROTATE_ANGLE).append("\n");
        sb.append("ROTATE_ANGLE_THUMB : ").append(myPhotoSelectImageData.ROTATE_ANGLE_THUMB).append("\n");
        sb.append("FREE_ANGLE : ").append(myPhotoSelectImageData.FREE_ANGLE).append("\n");

        Dlog.d(Dlog.PRE_FIX_CS + sb.toString());
    }


    private void doWhenBitmapNullOrRecycled(PhotoPrintData data, final int itemPositon) {
        String thumbPath = data.getMyPhotoSelectImageData().THUMBNAIL_PATH;
        boolean isUploadedFile = thumbPath.startsWith("/Upload/");
        if (isUploadedFile)
            thumbPath = SnapsAPI.DOMAIN() + thumbPath;

        int size = UIUtil.convertDPtoPX(this, 324);
        if (progress != null && !progress.isShowing()) {
            progress.setCancelable(true);
            progress.show();
        }
        ImageLoader.with(this).load(thumbPath).skipMemoryCache(true).override(size, size).setListener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                Bitmap bitmap = resource instanceof BitmapDrawable ? ((BitmapDrawable) resource).getBitmap() : resource instanceof Bitmap ? (Bitmap) resource : null;
                if (bitmap != null && !bitmap.isRecycled()) {
                    PhotoPrintDataManager.getInstance().setBitmapResource(itemPositon, bitmap);
                    afterImageSetDone();
                    if (progress != null)
                        progress.dismiss();
                }
                return false;
            }
        }).asBitmap().into(dummyImage);
    }

    private void afterImageSetDone() {
        findViewById(R.id.dummy_image_layout).setVisibility(View.VISIBLE);
        currentItem.hideImageView();
        startDummyImageAnimation(currentItem, imageInfo);
    }

    private void startDummyImageAnimation(final PhotoPrintListItemHolder listItem, final float[] imageInfo) {
        final float[] targetPos = getTargetPosition(listItem.getMyPhotoPrintData(), imageInfo);
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float startW = imageInfo[2];
                float startH = imageInfo[3];
                float startX = imageInfo[0];
                float startY = imageInfo[1];
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dummyImage.getLayoutParams();
                params.leftMargin = (int) (startX + (targetPos[0] - startX) * (Float) animation.getAnimatedValue());
                params.topMargin = (int) (startY + (targetPos[1] - startY) * (Float) animation.getAnimatedValue());
                params.width = (int) (startW + (targetPos[2] - startW) * (Float) animation.getAnimatedValue());
                params.height = (int) (startH + (targetPos[3] - startH) * (Float) animation.getAnimatedValue());
                dummyImage.setLayoutParams(params);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isDoingImageAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isDoingImageAnimation = false;
                findViewById(R.id.detail_layout_container).setVisibility(View.VISIBLE);
                findViewById(R.id.dummy_image_layout).setVisibility(View.GONE);
                listItem.showImageView();
                dummyImage.setImageDrawable(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.start();
    }

    private float[] getTargetPosition(PhotoPrintData data, float[] imageInfo) {
        // 사이즈 계산
        float largeSide = UIUtil.getScreenWidth(this) - UIUtil.convertDPtoPX(this, 35);
        int[] frameSize = PhotoPrintDataManager.getFrameRealSize(PhotoPrintDataManager.getFrameSize(data, (int) imageInfo[2], (int) imageInfo[3]), new int[]{(int) largeSide, (int) largeSide});
        int[] newPos = UIUtil.getPosByImageType(data.isImageFull(), frameSize, new int[]{(int) imageInfo[2], (int) imageInfo[3]});

        float rate = UIUtil.getRate(new float[]{data.getSize()[0], data.getSize()[1]}, new float[]{frameSize[0], frameSize[1]});

        // 좌표 계산
        float baseLeft = UIUtil.convertDPtoPX(this, 17.5f);
        float baseTop = UIUtil.convertDPtoPX(this, 109);
        if (frameSize[0] > frameSize[1])
            baseTop += (largeSide - (float) frameSize[1]) / 2;
        else
            baseLeft += (largeSide - (float) frameSize[0]) / 2;
        float targetX = baseLeft + (float) newPos[0] + data.getX() * rate;
        float targetY = baseTop + (float) newPos[1] + data.getY() * rate;

        return new float[]{targetX, targetY, (float) newPos[2], (float) newPos[3]};
    }

    private View.OnTouchListener recyclerViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (doingMenuHideAnimation || doingMenuShowAnimation) return false;

            if (event.getAction() != MotionEvent.ACTION_DOWN)
                closeTutorial();

            if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && checkNeedToStartMagneticAnimation())
                startMagneticAnimation();
            else if (event.getAction() == MotionEvent.ACTION_DOWN && doingMagneticAnimation)
                stopMagneticAnimation();
            return false;
        }
    };

    private void scrollMenuLayout(int dy) {
        if (recyclerView != null && recyclerView.isDoingSelectModeMinScroll()) {
            recyclerView.setSelectModeMinScrollFalse();
            return;
        }

        if (menuLayout == null || PhotoPrintMenuLayout.MENU_LAYOUT_SIZE == 0) return;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
        params.topMargin -= dy;
        if (params.topMargin > 0) params.topMargin = 0;
        else if (params.topMargin < -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE)
            params.topMargin = -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE;
        else if (!(doingMenuHideAnimation || doingMenuShowAnimation) && params.topMargin > -PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE && PhotoPrintDataManager.getInstance().isSelectMode())
            params.topMargin = -PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE;
        menuLayout.setLayoutParams(params);
    }

    private boolean checkNeedToStartMagneticAnimation() {
        if (menuLayout == null || PhotoPrintMenuLayout.MENU_LAYOUT_SIZE == 0) return false;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
        PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
        return (params.topMargin < 0 && !manager.isSelectMode() && params.topMargin > -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE) || (params.topMargin < -PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE && manager.isSelectMode() && params.topMargin > -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE);
    }

    private void startMagneticAnimation() {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
        int currentMargin = params.topMargin;
        int targetMargin = 0;
        if (PhotoPrintDataManager.getInstance().isSelectMode())
            targetMargin = params.topMargin < -(PhotoPrintMenuLayout.MENU_LAYOUT_SIZE - (PhotoPrintMenuLayout.MENU_LAYOUT_SIZE - PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE) / 2) ? -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE : -PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE;
        else
            targetMargin = params.topMargin < -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE / 2 ? -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE : 0;

        stopMagneticAnimation();
        magneticAnimator = ValueAnimator.ofInt(currentMargin, targetMargin);
        magneticAnimator.setDuration((int) ((float) Math.abs(targetMargin - currentMargin) / ((float) PhotoPrintMenuLayout.MENU_LAYOUT_SIZE / 2f) * (float) MAGNETIC_ANIMATION_MAX_DURATION));
        magneticAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                recyclerView.scrollBy(0, params.topMargin - (Integer) animation.getAnimatedValue());
                params.topMargin = (Integer) animation.getAnimatedValue();
                menuLayout.setLayoutParams(params);
            }
        });
        magneticAnimator.addListener(magneticAnimatorListener);
        magneticAnimator.start();
    }

    private Animator.AnimatorListener magneticAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            doingMagneticAnimation = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            doingMagneticAnimation = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            doingMagneticAnimation = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private void stopMagneticAnimation() {
        if (!doingMagneticAnimation || magneticAnimator == null || !magneticAnimator.isRunning())
            return;
        magneticAnimator.removeAllUpdateListeners();
        magneticAnimator.cancel();
        doingMagneticAnimation = false;
    }

    private void getPhotoDatas() {
        ATask.executeVoidDefProgress(this, new ATask.OnTask() {

            @Override
            public void onPre() {
            }

            @Override
            public void onPost() {
                initLayout();
            }

            @Override
            public void onBG() {
                // 사진 사이즈 데이터 받기..
                documentHandler = new PhotoPrintDocumentHandler(NewPhotoPrintListActivity.this, prodCode);
                PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
                if (isEditMode) {
                    manager.init();
                    String xmlString = GetParsedXml.getSaveXML(projCode, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    if (xmlString.contains("prodType=\"photo_print\"")) {
                        GetNewPhotoPrintSaveXMLHandler saveHandler = new GetNewPhotoPrintSaveXMLHandler();
                        saveHandler.setHtml5CompatibleVersion(xmlString.contains("chnlCode=\"KOR0000\"") || xmlString.contains("appType=\"Android\""));
                        saveHandler.parsing(xmlString);
                        manager.setDatas(saveHandler.getDatas(), saveHandler.getBaseData());
                        manager.setSize(documentHandler.getTemplate().getScene().getWidth(), documentHandler.getTemplate().getScene().getHeight());
                    } else {
                        GetPhotoPrintSaveXMLHandler saveHandler = new GetPhotoPrintSaveXMLHandler();
                        saveHandler.parsing(xmlString);
                        manager.setDatas(saveHandler.getMyPhotoSelectImageData());
                        manager.setPageTypeBySaveData(saveHandler.getPrject().mGlossy);
                        manager.setSize(documentHandler.getTemplate().getScene().getWidth(), documentHandler.getTemplate().getScene().getHeight());
                        manager.initPositionWhenEditMode();
                    }

                    PhotoPrintDataManager.getInstance().startEditFromCartData();

                } else {
                    DataTransManager dataTransManager = DataTransManager.getInstance();
                    if (dataTransManager != null) {
                        manager.setDatas(dataTransManager.getPhotoImageDataList());
                        manager.setSize(documentHandler.getTemplate().getScene().getWidth(), documentHandler.getTemplate().getScene().getHeight());
                    } else {
                        DataTransManager.notifyAppFinish(NewPhotoPrintListActivity.this);
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();

        if (isDoingImageAnimation)
            return;

        if (editLayout != null) {
            editLayout.cancelChanges();
            return;
        }

        if (manager.isSelectMode() && menuLayout != null) {
            menuLayout.finishSelectMode();
            return;
        }

        if (manager.isModifyMode() && menuLayout != null) {
            manager.changeModifyMode(false);

            menuLayout.changeLayout(PhotoPrintMenuLayout.LAYOUT_NORMAL);
            menuLayout.refreshStatus();

            showApplyChangeButtonLayout(false);
            return;
        }

//        if (isEditMode && manager.checkIsChangedFromCartData())
//            confirmUploadChanged(true);
        isBackPressed = true;
        super.onBackPressed();
    }

    private String makeCartThumnail() {

        String savePath = null;
        try {
            File thumbnailFile = Config.getTHUMB_PATH(projCode + ".png");
            if (thumbnailFile == null) throw new SnapsIOException("failed make thumbnail file");
            if (!thumbnailFile.exists()) thumbnailFile.createNewFile();
            savePath = thumbnailFile.getAbsolutePath();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        Bitmap bitmap;
        PhotoPrintData data = PhotoPrintDataManager.getInstance().getData(0);
        MyPhotoSelectImageData imageData = data.getMyPhotoSelectImageData();

        if (isEditMode) {
            if (imageData.THUMBNAIL_PATH.startsWith("http"))
                bitmap = ImageLoader.syncLoadBitmap(imageData.THUMBNAIL_PATH, CART_IMAGE_SIZE, CART_IMAGE_SIZE, 0);
            else

                bitmap = ImageLoader.syncLoadBitmap(SnapsAPI.DOMAIN() + imageData.THUMBNAIL_PATH, CART_IMAGE_SIZE, CART_IMAGE_SIZE, 0);

        } else {
            if (imageData.KIND == Const_VALUES.SELECT_PHONE) {
                bitmap = ImageLoader.syncLoadBitmap(imageData.PATH, CART_IMAGE_SIZE, CART_IMAGE_SIZE, 0);
            } else if (imageData.KIND == Const_VALUES.SELECT_UPLOAD) {
                bitmap = ImageLoader.syncLoadBitmap(SnapsAPI.DOMAIN() + imageData.PATH, CART_IMAGE_SIZE, CART_IMAGE_SIZE, 0);
            } else {
                bitmap = ImageLoader.syncLoadBitmap(imageData.PATH, CART_IMAGE_SIZE, CART_IMAGE_SIZE, 0);
            }
        }

        bitmap = getCropBitmap(data, bitmap, 1);
        bitmap = ImageDirectLoader.rotate(bitmap, data.getAngle() + data.getMyPhotoSelectImageData().ROTATE_ANGLE);
        bitmap = SnapsBitmapUtil.makeCartBitmap(getApplicationContext(), bitmap, prodCode, data);

        if (BitmapUtil.saveImgFile(savePath, bitmap))
            PhotoPrintDataManager.getInstance().setThumbnailPath(savePath);

        return savePath;
    }

    private Bitmap getCropBitmap(PhotoPrintData data, Bitmap bitmap, int sampleRat) {
        if (bitmap == null || bitmap.isRecycled())
            return null;

        Bitmap converted = null;
        Bitmap scaled = null;
        try {
            if (sampleRat > 1) {
                scaled = Bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() / sampleRat, bitmap.getHeight() / sampleRat, false);
                if (scaled != bitmap) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            }

            float w, h, frameW, frameH;
            w = scaled != null ? scaled.getWidth() : bitmap.getWidth();
            h = scaled != null ? scaled.getHeight() : bitmap.getHeight();
            float sizeW = (float) data.getSize()[0];
            float sizeH = (float) data.getSize()[1];
            int rotate = data.getMyPhotoSelectImageData().ROTATE_ANGLE + data.getAngle();
            boolean isRotated = rotate == 90 || rotate == 270;
            if ((sizeW - sizeH) * (bitmap.getWidth() - bitmap.getHeight()) < 0) {
                float temp = sizeW;
                sizeW = sizeH;
                sizeH = temp;
            }

            if (data.isImageFull()) {
                if (w / h > sizeW / sizeH) {
                    frameW = w;
                    frameH = w / sizeW * sizeH;
                } else {
                    frameH = h;
                    frameW = h / sizeH * sizeW;
                }
//사진 맞추기에서 이 부분을 주석하면 끝에 맞쳐준다
//                float[] realFrameV = new float[]{ 74, 110 };
//                float[] realFrameH = new float[]{ 106, 78 };
//                if( sizeW > sizeH ) {
//                    frameW = frameW / 96 * (isRotated ? realFrameV[1] : realFrameH[0]);
//                    frameH = frameH / 64 * (isRotated ? realFrameV[0] : realFrameH[1]);
//                }
//                else {
//                    frameW = frameW / 64 * (isRotated ? realFrameH[1] : realFrameV[0]);
//                    frameH = frameH / 96 * (isRotated ? realFrameH[0] : realFrameV[1]);
//                }

                float modX = 0, modY = 0;
                float frameRate = frameW / frameH;
                float sizeRate = w / h;
                if (sizeRate > frameRate && rotate == 0 && frameW < frameH)
                    modY = -(frameH - h) / 2 / 14 * 3;
                else if (sizeRate < frameRate && rotate == 90 && frameW > frameH)
                    modX = -(frameW - w) / 2 / 14 * 3;
                else if (sizeRate > frameRate && rotate == 180 && frameW < frameH)
                    modY = (frameH - h) / 2 / 14 * 3;
                else if (sizeRate < frameRate && rotate == 270 && frameW > frameH)
                    modX = (frameW - w) / 2 / 14 * 3;

                converted = Bitmap.createBitmap((int) frameW, (int) frameH, Bitmap.Config.ARGB_8888);
                Canvas can = new Canvas(converted);
                can.drawARGB(255, 255, 255, 255);
                can.drawBitmap(scaled != null ? scaled : bitmap, (frameW - w) / 2 + modX, (frameH - h) / 2 + modY, null);
            } else {
                float x, y, pos;

                if (w / h > sizeW / sizeH) {
                    pos = isRotated ? data.getY() : data.getX();
                    frameH = h;
                    frameW = h / sizeH * sizeW;
                    x = (w - frameW) / 2 - pos / (isRotated ? sizeH / frameH : sizeW / frameW) * (rotate == 270 || rotate == 180 ? -1 : 1);
                    y = 0;
                    w = frameW;
                    h = frameH;
                } else {
                    pos = isRotated ? data.getX() : data.getY();
                    frameW = w;
                    frameH = w / sizeW * sizeH;
                    x = 0;
                    y = (h - frameH) / 2 - pos / (isRotated ? -sizeW / frameW : sizeH / frameH) * (rotate == 270 || rotate == 180 ? -1 : 1);
                    w = frameW;
                    h = frameH;
                }

                if (x < 0) x = 0;
                if (y < 0) y = 0;
                converted = Bitmap.createBitmap(scaled != null ? scaled : bitmap, (int) x, (int) y, (int) w, (int) h);
            }


        } catch (OutOfMemoryError e) {
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getCropBitmap(data, bitmap, sampleRat);
            else
                return null;
        }

        if (scaled != null && scaled != converted && !scaled.isRecycled())
            scaled.recycle();

        if (converted != null && bitmap != null && converted != bitmap && !bitmap.isRecycled())
            bitmap.recycle();

        return converted;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Glide glide = Glide.get(this);
        if (glide != null)
            glide.clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Glide glide = Glide.get(this);
        if (glide != null)
            glide.trimMemory(level);
    }

    private void uploadThumbnail() {
        ATask.executeBooleanDefProgress(this, new ATask.OnTaskResult() {
            @Override
            public void onPre() {

            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    if (isEditMode)
                        uploadXmlOnly(true);
                    else finish();
                } else
                    MessageUtil.toast(NewPhotoPrintListActivity.this, R.string.photoprint_error);

                if (!result)
                    isDoingCartProcess = false;
            }

            @Override
            public boolean onBG() {
                String path = isEditMode ? makeCartThumnail() : PhotoPrintDataManager.getInstance().getThumbnailPath();
                if (StringUtil.isEmpty(path) || StringUtil.isEmpty(userId))
                    return false;
                return SnapsPhotoUploader.thumbUpload(path, projCode, userId);
            }
        });
    }

    private void finishAndRefreshCartPage() {
        setResult(NewPhotoPrintListActivity.REQUEST_CODE_PHOTOPRINT_CHANGE);
        finish();
    }

    private void uploadXmlOnly(final boolean refreshCart) {
        ATask.executeBooleanDefProgress(this, new ATask.OnTaskResult() {
            @Override
            public void onPre() {
            }

            @Override
            public boolean onBG() {
                PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
                PhotoPrintProject project = new PhotoPrintProject(documentHandler.getTemplateInfo(), Config.getAPP_VERSION(), prodCode, projCode, manager.getThumbnailPath(), getDateStringDatas()[1], isEditMode);
                project.setDatas(manager.getDatas(), null, manager.getBaseData(), PhotoPrintDataManager.getPosFromRc(getDateStringDatas()[0]));
                setPrice(project);
                SnapsPhotoUploader.uploadNewPhotoPrintXmlOnly(NewPhotoPrintListActivity.this, project, userNo);
                return true;
            }

            @Override
            public void onPost(boolean result) {
                if (refreshCart || PhotoPrintDataManager.getInstance().checkIsCountChangedFromCartData())
                    finishAndRefreshCartPage();
                else
                    finish();
            }
        });
    }

    private void setPrice(PhotoPrintProject project) {
        PhotoPrintProductInfo productInfo = PhotoPrintProductInfo.getPhotoPrintTemplate(this, prodCode);
        project.setPrice(Integer.parseInt(productInfo.productSellPrice.replaceAll(",", "")), Integer.parseInt(productInfo.productOrgPrice.replaceAll(",", "")));
    }

    private void moveToHome() {
        Intent intent = new Intent(NewPhotoPrintListActivity.this, RenewalHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        PhotoPrintDataManager.getInstance().init();
    }

    private void fullUpload() {
        ATask.executeBooleanDefProgress(this, new ATask.OnTaskResult() {
            boolean isProjCodeError = false;

            @Override
            public void onPre() {

            }

            @Override
            public boolean onBG() {
                if (!isEditMode || StringUtil.isEmpty(projCode))
                    projCode = GetParsedXml.getProjectCode(SnapsTPAppManager.getProjectCodeParams(getApplicationContext()), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                if (StringUtil.isEmpty(projCode)) {
                    isProjCodeError = true;
                    return false;
                }
                makeCartThumnail();
                return true;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
                    PhotoPrintProject project = new PhotoPrintProject(documentHandler.getTemplateInfo(), Config.getAPP_VERSION(), prodCode, projCode, manager.getThumbnailPath(), getDateStringDatas()[1], isEditMode);
                    project.setDatas(manager.getDatas(), null, manager.getBaseData(), PhotoPrintDataManager.getPosFromRc(getDateStringDatas()[0]));
                    setPrice(project);
                    if (!SnapsPhotoUploader.getInstance(getApplicationContext()).addProject(project)) {
                        MessageUtil.toast(NewPhotoPrintListActivity.this, getResources().getString(R.string.photoprint_upload_fail_message));
                        return;
                    }

                    SnapsUploadState.UploadState state = SnapsUploadState.getInstance().getmState();
                    if (state == SnapsUploadState.UploadState.UPLOAD_READY)
                        state = SnapsUploadState.UploadState.UPLOAD_START;

                    SnapsUploadState.getInstance().setState(state, (manager.getDatas().size()) + SnapsUploadState.getInstance().getmUploadingPhotoCount(),
                            SnapsUploadState.getInstance().getmCompletePhotosCount());

                    uploadTask = ATask.executeVoid(new ATask.OnTask() {
                        @Override
                        public void onPre() {
                            uploadDialog = new SnapsUploadDialog(NewPhotoPrintListActivity.this, SnapsPhotoUploader.REQUEST_UPLOAD_START);
                            uploadDialog.setNewPhotoPrint(true);
                            uploadDialog.show();
                        }

                        @Override
                        public void onPost() {
                            if (uploadTask.isCancelled())
                                return;

                            if (uploadDialog.isMoveToCart()) {
                                finish();
                                PhotoPrintDataManager.getInstance().init();
                            }

                            if (!isEditMode && !uploadDialog.isMoveToCart()) {
                                uploadDialog.dismiss();
                                moveToHome();
                            }
                        }

                        @Override
                        public void onBG() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    });
                } else {
                    if (isProjCodeError) {
                        MessageUtil.toast(NewPhotoPrintListActivity.this, getResources().getString(R.string.photoprint_prjCode_not_receive));
                        isDoingCartProcess = false;
                        return;
                    }
                }
            }
        });
    }

    public void doLogin(String id, String pwd) {
        final String snapsUserId = id;
        final String snapsUserPwd = pwd;

        if (StringUtil.isEmpty(snapsUserId) || StringUtil.isEmpty(snapsUserPwd)) {// 빈칸체크
            MessageUtil.toast(this, R.string.login_validate);
            return;
        }

        ATask.executeVoidDefProgress(this, new ATask.OnTask() {
            Xml_SnapsLoginInfo xmlSnapsLoginInfo = null;
            boolean isNetworkErr = false;

            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                CNetStatus netStatus = CNetStatus.getInstance();
                if (netStatus.isAliveNetwork(NewPhotoPrintListActivity.this)) {
                    String snapsUserName1 = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_NAME1);
                    String snapsUserName2 = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_NAME2);
                    String snapsLoginType = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_LOGIN_TYPE, Const_VALUES.SNAPSLOGIN_SNAPS);
                    xmlSnapsLoginInfo = GetParsedXml.snapsLogin(NewPhotoPrintListActivity.this, snapsUserId, snapsUserPwd, snapsUserName1, snapsUserName2, snapsLoginType);
                } else {
                    isNetworkErr = true;
                }
            }

            @Override
            public void onPost() {
//                if (xmlSnapsLoginInfo != null) {
                if ("true".equals(xmlSnapsLoginInfo.F_RETURN_CODE)) {
                    Setting.set(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_NO, xmlSnapsLoginInfo.F_USER_NO);// userno 저장
                    userId = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_NO);
                    Setting.set(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_ID, snapsUserId);
                    Setting.set(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_PWD, snapsUserPwd);
                    Setting.set(NewPhotoPrintListActivity.this, Const_VALUE.KEY_SNAPS_USER_NAME, xmlSnapsLoginInfo.F_USER_NAME);
                    Setting.set(NewPhotoPrintListActivity.this, Const_VALUE.KEY_EVENT_DEVICE, xmlSnapsLoginInfo.F_DEVICE);

                    PushManager pushService = new PushManager(NewPhotoPrintListActivity.this);
                    pushService.requestRegistPushDevice();

                    fullUpload();

                    // push device 등록
//                    ATask.executeVoid(new ATask.OnTask() {
//                        @Override
//                        public void onPre() {
//                        }
//
//                        @Override
//                        public void onBG() {
//                            String regId = Setting.getString(NewPhotoPrintListActivity.this, Const_VALUE.KEY_GCM_REGID);
//                            if (!"".equals(regId)) {
//                                String userId = xmlSnapsLoginInfo.F_USER_NO;
//                                String userName = xmlSnapsLoginInfo.F_USER_NAME;
//                                String appVer = SystemUtil.getAppVersion(NewPhotoPrintListActivity.this);
//                                String deviceID = SystemUtil.getIMEI(NewPhotoPrintListActivity.this);
//                                HttpReq.regPushDevice(Setting.getBoolean(NewPhotoPrintListActivity.this, Const_VALUE.KEY_GCM_PUSH_RECEIVE) ? regId : "", userId, userName, appVer, deviceID, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//                            }
//                            Logg.d("push device 등록 push device 등록 ");
//                        }
//
//                        @Override
//                        public void onPost() {
//                            fullUpload();
//                        }
//                    });
                } else
                    SnapsLoginManager.startLogInProcess(NewPhotoPrintListActivity.this, Const_VALUES.LOGIN_P_LOGIN);
            }
        });
    }

    private void addToCart() {
        isDoingCartProcess = true;

        if (isEditMode) {
            if (StringUtil.isEmpty(projCode))
                return;

            final PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
            ATask.executeBooleanDefProgress(this, new ATask.OnTaskResult() {
                boolean isDuplicatedVerifyProjectCode = false;

                @Override
                public void onPre() {
                }

                @Override
                public boolean onBG() {
                    // confirm project code
                    String result = GetParsedXml.getResultVerifyProjectCode(projCode, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    if (result == null)
                        return false;
                    else {
                        if (!result.equalsIgnoreCase("SUCCESS")) {
                            isDuplicatedVerifyProjectCode = true;
                            return false;
                        }
                    }

                    return true;
                }

                @Override
                public void onPost(boolean result) {
                    if (result) {
                        if (isEditMode && !PhotoPrintDataManager.getInstance().checkNotUploadedFileExist()) {
                            if (manager.isFirstItemChanged()) {
                                uploadThumbnail();
                            } else {
                                //CS 처리 (장바구니 이미지가 없는 경우 처리)
                                //절차1
                                //아래 if문 주석 처리한다.
                                //아래 uploadXmlOnly(false);를 uploadThumbnail();로 교체
                                uploadXmlOnly(false);
                            }
                        } else {
                            fullUpload();
                        }

                    } else if (isDuplicatedVerifyProjectCode) {
//                        MessageUtil.alertnoTitleOneBtn(NewPhotoPrintListActivity.this, NewPhotoPrintListActivity.this.getResources().getString(R.string.failed_order_because_already_complated_order),
//                                new ICustomDialogListener() {
//                                    @Override
//                                    public void onClick(byte clickedOk) {
//                                        String url = SnapsAPI.WEB_DOMAIN(SnapsAPI.ORDER_URL(), SnapsLoginManager.getUUserNo(NewPhotoPrintListActivity.this), "");
//                                        String titleStr = getString(R.string.order_and_delivery);
//                                        Intent intent = StickyStyleWebviewActivity.getIntent(NewPhotoPrintListActivity.this, titleStr, url);
//
//                                        NewPhotoPrintListActivity.this.startActivity(intent);
//                                        (NewPhotoPrintListActivity.this).finish();
//                                        PhotoPrintDataManager.getInstance().init();
//                                    }
//                                });
                        MessageUtil.alertnoTitleOneBtn(NewPhotoPrintListActivity.this, getString(R.string.failed_order_because_already_complated_order), new ICustomDialogListener() {
                            @Override
                            public void onClick(byte clickedOk) {
                                String url = SnapsAPI.WEB_DOMAIN(SnapsAPI.ORDER_URL(), SnapsLoginManager.getUUserNo(NewPhotoPrintListActivity.this), "");
                                Intent intent = DetailProductWebviewActivity.getIntent(NewPhotoPrintListActivity.this, getString(R.string.order_and_delivery), url, true, SnapsMenuManager.eHAMBURGER_ACTIVITY.ORDER);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } else {
                        MessageUtil.toast(NewPhotoPrintListActivity.this, R.string.photoprint_error);
                    }

                    if (!result) {
                        isDoingCartProcess = false;
                    }
                }
            });
        } else fullUpload();
    }


    private void confirmUploadChanged(final boolean isFinishActivity) {
        //CS 처리 (장바구니 이미지가 없는 경우 처리)
        //절차2
        //아래 if문 주석 처리한다.
        if (isEditMode && !PhotoPrintDataManager.getInstance().checkIsChangedFromCartData()) {
            finish();
            return;
        }
        DialogInputNameFragment diagInput = DialogInputNameFragment.newInstance(Config.ORDR_STAT_ORDER_CODE, new DialogInputNameFragment.IDialogInputNameClickListener() {
            @Override
            public void onClick(boolean isOk) {
                if (isOk) {
                    addToCart();
                } else {
                    if (isFinishActivity) {
                        finish();
                    }
                }
            }

            @Override
            public void onCanceled() {

            }
        });
        if (isCheckResolution()) {
            diagInput.setAlertAttribute(SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute());
        } else {
            diagInput.setAlertAttribute(SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttribute());
        }
        diagInput.show(getSupportFragmentManager(), "dialog");
    }

    private boolean isCheckResolution() {
        PhotoPrintDataManager photoPrintDataManager = PhotoPrintDataManager.getInstance();
        for (PhotoPrintData item : photoPrintDataManager.getDatas()) {
            if (item.getMyPhotoSelectImageData().isNoPrint) {
                return true;
            }
        }
        return false;
    }

    public void showApplyChangeButtonLayout(boolean show) {
        final View layout = findViewById(R.id.apply_change_button_layout);
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        View cancelButton = layout.findViewById(R.id.apply_change_cancel_button);
        View confirmButton = layout.findViewById(R.id.apply_change_confirm_button);
        cancelButton.setClickable(show);
        cancelButton.setOnClickListener(show ? cancelApplyChanges : null);
        confirmButton.setClickable(show);
        confirmButton.setOnClickListener(show ? confirmApplyChanges : null);

        final int MAX_BOTTOM_MARGIN = UIUtil.convertDPtoPX(this, -48);
        final int MAX_DURATION = 200;

        if (layout.getVisibility() == View.GONE) { // 초기화.
            params.bottomMargin = MAX_BOTTOM_MARGIN;
            layout.setLayoutParams(params);
            layout.setVisibility(View.VISIBLE);
            layout.setAlpha(0f);
        }

        int targetPos = show ? 0 : MAX_BOTTOM_MARGIN;
        ValueAnimator anim = ValueAnimator.ofInt(params.bottomMargin, show ? 0 : MAX_BOTTOM_MARGIN);
        anim.setDuration((int) ((float) Math.abs(params.bottomMargin - targetPos) / (float) Math.abs(MAX_BOTTOM_MARGIN) * (float) MAX_DURATION));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params.bottomMargin = (Integer) animation.getAnimatedValue();
                layout.setLayoutParams(params);
                layout.setAlpha(1f - (float) params.bottomMargin / (float) MAX_BOTTOM_MARGIN);
            }
        });
        anim.start();
    }

    @Override
    public void showDeleteButton(boolean show) {
        final View deleteButton = findViewById(R.id.delete_button);
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) deleteButton.getLayoutParams();
        deleteButton.setClickable(show);
        deleteButton.setOnClickListener(show ? deleteSelectedItems : null);

        final int MAX_BOTTOM_MARGIN = UIUtil.convertDPtoPX(this, -48);
        final int MAX_DURATION = 200;

        if (deleteButton.getVisibility() == View.GONE) { // 초기화.
            params.bottomMargin = MAX_BOTTOM_MARGIN;
            deleteButton.setLayoutParams(params);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setAlpha(0f);
        }

        int targetPos = show ? 0 : MAX_BOTTOM_MARGIN;
        ValueAnimator anim = ValueAnimator.ofInt(params.bottomMargin, show ? 0 : MAX_BOTTOM_MARGIN);
        anim.setDuration((int) ((float) Math.abs(params.bottomMargin - targetPos) / (float) Math.abs(MAX_BOTTOM_MARGIN) * (float) MAX_DURATION));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) deleteButton.getLayoutParams();
                params.bottomMargin = (Integer) animation.getAnimatedValue();
                deleteButton.setLayoutParams(params);
                deleteButton.setAlpha(1f - (float) params.bottomMargin / (float) MAX_BOTTOM_MARGIN);
            }
        });
        anim.start();
    }

    private View.OnClickListener confirmApplyChanges = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhotoPrintDataManager manager = PhotoPrintDataManager.getInstance();
            manager.applyChanges();
            manager.changeModifyMode(false);

            if (menuLayout != null) {
                menuLayout.changeLayout(PhotoPrintMenuLayout.LAYOUT_NORMAL);
                menuLayout.refreshStatus(true);
            }

            refreshListItems();
            showApplyChangeButtonLayout(false);
        }
    };

    private View.OnClickListener cancelApplyChanges = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhotoPrintDataManager.getInstance().changeModifyMode(false);

            if (menuLayout != null) {
                menuLayout.changeLayout(PhotoPrintMenuLayout.LAYOUT_NORMAL);
                menuLayout.refreshStatus(true);
            }

            showApplyChangeButtonLayout(false);
        }
    };

    private View.OnClickListener deleteSelectedItems = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PhotoPrintDataManager.getInstance().deleteSelectedDatas();

            if (PhotoPrintDataManager.getInstance().getDataCount() < 1) {
                menuLayout.finishSelectMode();
                finish();
                return;
            }

            menuLayout.finishSelectMode();
            if (recyclerView != null)
                recyclerView.getAdapter().notifyDataSetChanged();

            showMenu();
        }
    };

    @Override
    public void changeListMode(boolean isLargeItemMode) {
        if (recyclerView == null || (isLargeItemMode && largeViewAdapter == null) || (!isLargeItemMode && smallViewAdapter == null))
            return;
        PhotoPrintDataManager.getInstance().setLargeView(isLargeItemMode);

        if (menuLayout != null) {
            ((ImageView) menuLayout.findViewById(R.id.large_view_button)).setImageResource(isLargeItemMode ? R.drawable.icon_module_large_on : R.drawable.icon_module_large_off);
            ((ImageView) menuLayout.findViewById(R.id.small_view_button)).setImageResource(isLargeItemMode ? R.drawable.icon_module_small_off : R.drawable.icon_module_small_on);
        }

        if (isLargeItemMode) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(largeViewAdapter);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position == 0 ? 2 : 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(smallViewAdapter);
        }
    }

    @Override
    public void refreshListItems() {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void editLayoutFinished() {
        editLayout = null;

        (findViewById(R.id.detail_layout_container)).setVisibility(View.GONE);
        PhotoPrintDataManager.getInstance().changeDetailEditMode(false, -1);

        enableScroll(true, false);
        checkMenuLayoutPosition();
    }

    private void checkMenuLayoutPosition() {
        if (menuLayout == null || recyclerView == null) return;

        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
        if (recyclerView.computeVerticalScrollOffset() < 1 && params.topMargin < 0) {
            params.topMargin = 0;
            menuLayout.setLayoutParams(params);
        }
    }

    @Override
    public void enableScroll(boolean flag, boolean showDimArea) {
        if (recyclerView == null) return;

        (findViewById(R.id.dim_area)).setVisibility(!showDimArea ? View.GONE : View.VISIBLE);

        if (recyclerViewDisabler == null)
            recyclerViewDisabler = new RecyclerViewDisabler();

        recyclerView.removeOnItemTouchListener(recyclerViewDisabler);

        if (!flag)
            recyclerView.addOnItemTouchListener(recyclerViewDisabler);
    }

    @Override
    public int getBorderThickness(int frameSize) {
        if (documentHandler == null) return 0;

        float size = (float) frameSize;
        return (int) (size / Float.parseFloat(documentHandler.getTemplateInfo().getF_PAGE_MM_HEIGHT()) * (float) BORDER_MM_THICKNESS);
    }

    @Override
    public String[] getDateStringDatas() {
        String[] data = null;

        if (documentHandler != null) {
            PhotoPrintJsonObjectLayer[] layers = documentHandler.getTemplate().getScene().getLayer();
            PhotoPrintJsonObjectLayer layer = null;
            for (PhotoPrintJsonObjectLayer item : layers) {
                if ("control_layer".equalsIgnoreCase(item.getName())) {
                    layer = item;
                    break;
                }
            }

            if (layer != null) {
                data = new String[2];
                data[0] = layer.getTextlist().getRc();
                data[1] = layer.getTextlist().getHtmlText();
            }
        }

        return data;
    }

    @Override
    public boolean isMattTypeAvailable() {
        boolean flag = true;
        if (documentHandler != null) {
            try {
                int mmWidth = Integer.parseInt(documentHandler.getTemplateInfo().getF_PAGE_MM_WIDTH());
                int mmHeight = Integer.parseInt(documentHandler.getTemplateInfo().getF_PAGE_MM_HEIGHT());
                flag = mmWidth + mmHeight < 500;
            } catch (NumberFormatException e) {
                Dlog.e(TAG, e);
            }
        }
        return flag;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (!doingMagneticAnimation)
            scrollMenuLayout(dy);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (doingMenuHideAnimation && state == 0) {
            if (recyclerView != null)
                recyclerView.setMinScrollPosition(UIUtil.convertDPtoPX(this, 49));
        }

        if (state == 0) {
            doingMenuShowAnimation = false;
            doingMenuHideAnimation = false;
        }
    }

    @Override
    public boolean isMenuHided() {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) menuLayout.getLayoutParams();
        return params.topMargin < -PhotoPrintMenuLayout.MENU_LAYOUT_SIZE + 1;
    }

    @Override
    public void showMenu() {
        doingMenuShowAnimation = true;
        if (recyclerView != null)
            recyclerView.disableMinScrollPosition();
        scrollMenuLayout(-PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE);
    }

    @Override
    public void hideMenu() {
        doingMenuHideAnimation = true;
        if (recyclerView != null)
            recyclerView.smoothScrollBy(0, PhotoPrintMenuLayout.MENU_LAYOUT_SELECT_AREA_SIZE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageSelectUtils.initPhotoLastSelectedHistory();

        DataTransManager.releaseInstance();
        if (!isBackPressed) {
            ImageSelectManager.finalizeInstance();
        }
    }

    private class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
