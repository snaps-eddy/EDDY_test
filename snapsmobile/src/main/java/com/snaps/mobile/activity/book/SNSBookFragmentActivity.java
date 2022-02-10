package com.snaps.mobile.activity.book;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.SnapsTemplatePrice;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.constant.SnapsProductInfoManager;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookTemplateBgRes;
import com.snaps.mobile.activity.diary.activities.SnapsDiaryMainActivity;
import com.snaps.mobile.activity.diary.publish.SnapsDiaryPublishFragmentActivity;
import com.snaps.mobile.activity.edit.BaseEditFragmentActivity;
import com.snaps.mobile.activity.edit.PagerContainer;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragmentFactory;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.edit.view.CircleProgressView;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.DialogSNSBookLoadComplateView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.themebook.ImageEditActivity;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils.ImageCompare;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.cseditor.ImageRatioChecker;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.ISnapsOrderStateListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadStateListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImagePopup;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;


/**
 * Created by songhw on 2016. 3. 28..
 */
public abstract class SNSBookFragmentActivity extends BaseEditFragmentActivity implements View.OnClickListener, SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver, ISnapsOrderStateListener, ISnapsHandler, SnapsOrderActivityBridge, SnapsImageUploadStateListener {
    private static final String TAG = SNSBookFragmentActivity.class.getSimpleName();
    public static final int TYPE_KAKAO_STORY = 0;
    public static final int TYPE_FACEBOOK_PHOTOBOOK = 1;
    public static final int TYPE_DIARY = 3;

    public static final int LIMIT_MIN_PAGE_COUNT = 21;
    public static final int LIMIT_MAX_PAGE_COUNT = 401;// 203; // 401p ==> 203 ==> ((401 + 1) / 2) + 2;//401; 장 수로 계산해야 한다. 홀수로 떨어질 수밖에 없다.
    public static final int LIMIT_MAX_TWICE_PAGE_COUNT = 202; // 401p ==> 203 ==> ((401 + 1) / 2) + 2;//401; 장 수로 계산해야 한다. 홀수로 떨어질 수밖에 없다.

    public static final String INTENT_KEY_REMOVE_DATA_INDEX_ARRAY = "index_key_remove_data_index_array";
    public static final String INTENT_KEY_TOTAL_DATA_COUNT = "index_key_total_data_count";

    protected int type = TYPE_KAKAO_STORY;

    protected boolean IS_EDIT_MODE = false;

    // 테스트 데이터..
    protected String templateId = null;

    protected String productCode = "";

    protected int totalPageCount = 0;
    protected int currentPage;

    protected SnapsTemplate multiTemplate = null;

    protected SnapsCanvasFragment captureFragment = null;

    // 사진 수정 차리를 위한 변수들
    protected final int REQ_PHOTO = 10;
    protected final int REQ_SELECT_REMOVE_POSTS = 11;
    protected final int REQ_MODIFY = 13;
    protected final int MAX_LAYOUT_COUNT = 100;

    protected SnapsBroadcastReceiver receiver = null;
    protected PopoverView mPopupMenuView;
    protected RelativeLayout mRootView;
    protected int tempImageViewID = -1;
    protected SnapsTemplatePrice saveXMLPriceInfo = null;

    // Noti
    protected NotificationManager mNM;
    protected int mNotiID = -999;


    protected CustomizeDialog mConfirmDialog;

    protected PagerContainer _mContainer;
    protected InterceptTouchableViewPager _mViewPager;
    protected int m_iInitedCanvasIdx = 0;
    protected int m_iNowPage = 0;

    protected DialogSNSBookLoadComplateView snsBookInfoDialog;

    protected SnapsHandler mSnapsHandler = null;

    private boolean isFirstLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpage);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mRootView = (RelativeLayout) findViewById(R.id.rootLayout);

        mSnapsHandler = new SnapsHandler(this);

        // 프로젝트 코드 초기화.
        Config.setPROJ_CODE("");

        applyReceivedIntentInfo();

        init();
        initByType();

        isFirstLoad = true;

        //CS 대응
        //개발자 버전일때만 커버 이미지가 아닌 전체 이미지 편집이 가능하다는 사실을 알린다.
        //혹시 나중에 착각해서 배포 버전도 전체 이미지 편집 가능하다고 착각할까봐
        if (Config.isDevelopVersion()) {
            String msg = "너는 개발자구나!" + "\n";
            msg += "개발자는 전체 페이지의 이미지를 편집할 수 있다.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    protected void applyReceivedIntentInfo() {
        Intent getItt = getIntent();
        if (getItt == null)
            return;

        // 인텐트에서 project코드 product코드를 가져온다.
        // 재편집여부 판단...
        String prjCode = getIntent().getStringExtra(Const_EKEY.MYART_PROJCODE);
        if (prjCode != null) {
            IS_EDIT_MODE = true;
            Config.setPROJ_CODE(prjCode);
            Config.setPROD_CODE(getIntent().getStringExtra(Const_EKEY.MYART_PRODCODE));
            // 재편집 모드인경우 하단 메뉴 필요없음..
        }

        Config.setFromCart(IS_EDIT_MODE);
    }

    /***
     * initByType method 내부에서 각 상품마다 type값을 저장해줘야 한다.
     */
    protected abstract void initByType();

    protected abstract void onPageSelect(int index);

    protected abstract SNSBookInfo getSNSBookInfo();

    protected abstract void makeBookLayout();

    protected abstract void getLoadSaveXML(Activity activity);

    @Override
    public void onResume() {
        super.onResume();

        Config.setIS_MAKE_RUNNING(true);

        onResumeControl();

        if (isFirstLoad) {
            isFirstLoad = false;

            // 템플릿 로드,
            if (IS_EDIT_MODE) getLoadSaveXML(this);
            else makeBookLayout();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            SnapsTutorialUtil.clearTooltip();
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // 로그인이 안되어 있어면 로그인 한후 장바구니 담기 실행..
            if (requestCode == SnapsOrderConstants.LOGIN_REQUSTCODE) {
                if (resultCode == RESULT_OK) {
                    // 장바구니 업로드 시작...
                    findViewById(R.id.textTopOrder).post(new Runnable() {

                        @Override
                        public void run() {
                            SnapsOrderManager.performSaveToBasket(SNSBookFragmentActivity.this);
                        }
                    });
                }
            } else if (requestCode == REQ_PHOTO) {
                if (resultCode == RESULT_OK) {
                    data.getExtras().setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
                    MyPhotoSelectImageData d = (MyPhotoSelectImageData) data.getExtras().getSerializable("imgData");

                    SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(this, tempImageViewID);
                    if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl))
                        return;

                    SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;

                    d.cropRatio = control.getRatio();
                    d.IMG_IDX = getImageIDX(control.getPageIndex(), control.regValue);// Integer.parseInt(control.getPageIndex() + "" +
                    // 기존 사진이 있는지 확인 후 삭제..
                    if (control.imgData != null) {
                        removeImageData(control.imgData);
                        SnapsOrderManager.removeBackgroundUploadOrgImageData(control.imgData);
                    }

                    d.pageIDX = control.getPageIndex();
                    d.mmPageWidth = StringUtil.isEmpty(_template.info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(_template.info.F_PAGE_MM_WIDTH);
                    d.pxPageWidth = StringUtil.isEmpty(_template.info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(_template.info.F_PAGE_PIXEL_WIDTH);
                    d.controlWidth = control.width;

                    control.imgData = d;
                    control.angle = String.valueOf(d.ROTATE_ANGLE);
                    control.imagePath = d.PATH;
                    control.imageLoadType = d.KIND;
                    control.isUploadFailedOrgImg = false;

                    addImageData(d);

                    imageResolutionCheck(_template);

                    ((SnapsPagerController2) _loadPager).pageAdapter.notifyDataSetChanged();

                    SnapsOrderManager.uploadOrgImgOnBackground();
                }
            } else if (requestCode == REQ_MODIFY) {
                if (resultCode == RESULT_OK) {
                    ArrayList<MyPhotoSelectImageData> imgList = new ArrayList<MyPhotoSelectImageData>();
                    DataTransManager dtManager = DataTransManager.getInstance();
                    if (dtManager != null) {
                        imgList = dtManager.getPhotoImageDataList();
                        changeModifiedCoverImages(imgList);
                    } else {
                        DataTransManager.notifyAppFinish(this);
                        return;
                    }

                    imageResolutionCheck(_template);

                    ((SnapsPagerController2) _loadPager).pageAdapter.notifyDataSetChanged();
                }
            } else if (requestCode == REQ_SELECT_REMOVE_POSTS) {
                if (resultCode == RESULT_OK) makeBookLayout();
                else {
                    finishActivity();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private MyPhotoSelectImageData getImageDataByKey(ArrayList<MyPhotoSelectImageData> list, double key) {
        for (MyPhotoSelectImageData data : list) {
            if (data.getImageDataKey() == key) return data;
        }
        return null;
    }

    private void changeModifiedCoverImages(ArrayList<MyPhotoSelectImageData> newImageList) {
        if (_template == null || _template.getPages() == null || _template.getPages().size() < 1 || newImageList == null || newImageList.size() < 1)
            return;

        ArrayList<SnapsControl> layoutList = _template.getPages().get(0).getLayoutList();

        //CS 대응
        if (Config.isDevelopVersion()) {
            //전부 다 집어 넣는다. 효율 같은거 무시
            //이것이 가능한 이유는 getMyPhotoSelectCoverImageData()안에서 PhotobookCommonUtils.getAllImageListFromTemplate(_template); 를 호출하기 때문에
            layoutList = new ArrayList<SnapsControl>(); //반드시 new 할것!
            ArrayList<SnapsPage> pageList = _template.getPages();
            for (SnapsPage snapsPage : pageList) {
                layoutList.addAll(snapsPage.getLayoutList());
            }
        }

        SnapsLayoutControl control;
        MyPhotoSelectImageData data;
        for (int i = 0; i < layoutList.size(); ++i) {
            if (layoutList.get(i) instanceof SnapsLayoutControl) {
                control = (SnapsLayoutControl) layoutList.get(i);
                if (control.type == null || !control.type.equalsIgnoreCase("browse_file") || control.imgData == null)
                    continue;

                data = getImageDataByKey(newImageList, control.imgData.getImageDataKey());
                if (data != null && data.isModify != -1) {
                    control.imgData.set(data);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SnapsUploadFailedImageDataCollector.isShowingUploadFailPopup()) return false;
            performBackKey();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 프로그래스바 사용완료 체크..
        CircleProgressView.destroy();

        Config.setIS_MAKE_RUNNING(false);
        Config.setTMPL_COVER(null);
        Config.setPROJ_CODE("");
        Config.setFromCart(false);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        if (mNM != null)
            mNM.cancel(mNotiID);

        SnapsTimerProgressView.destroyProgressView();
        pageProgressUnload();
        try {

            StoryBookDataManager.releaseInstance();

            SnapsTimerProgressView.destroyProgressView();

            if (snsBookInfoDialog != null && snsBookInfoDialog.isShowing())
                snsBookInfoDialog.dismiss();

            _canvasList = null;

            DataTransManager.releaseInstance();

            ImageSelectManager.finalizeInstance();

            SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
            if (productInfoManager != null)
                productInfoManager.setPROD_CODE("");

            SnapsOrderManager.finalizeInstance();

            SnapsUploadFailedImageDataCollector.clearHistory(Config.getPROJ_CODE());

            ImageSelectUtils.initPhotoLastSelectedHistory();
        } catch (Exception e) {
            Dlog.e(TAG, e);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SnapsOrderManager.unRegisterNetworkChangeReceiver();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.textTopOrder) {
            SnapsOrderManager.performSaveToBasket(SNSBookFragmentActivity.this);
        } else if (id == R.id.btnTopInfo) {
            showSNSBookInfoDialog();
        } else if (id == R.id.btnTopBack || id == R.id.btnTopBackLy) {
            performBackKey();
        } else if (id == R.id.button_input_name) {
            SnapsOrderManager.performSaveToBasket(SNSBookFragmentActivity.this);
        } else if (id == R.id.btn_confim) {
            String confimType = (String) v.getTag();
            Intent intent = null;
            if (confimType.equalsIgnoreCase(DialogConfirmFragment.DIALOG_TYPE_CAPTURE_AGAIN)) {
                // 다시 캡쳐 진행.
                setPageFileOutput(0);
            } else if (confimType.equalsIgnoreCase(DialogConfirmFragment.DIALOG_TYPE_ORDER_COMPLETE)) {
                intent = new Intent(this, RenewalHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("goToCart", true);
                startActivity(intent);
                finishActivity();
            }
        } else if (id == R.id.popup_menu_photo_modify) {
            mPopupMenuView.dissmissPopover(false);

            // 커버 이미지 수정..
            Intent intent = new Intent(getApplicationContext(), ImageEditActivity.class);
            ArrayList<MyPhotoSelectImageData> images = getMyPhotoSelectCoverImageData();

            // 사진데이터를 싱글턴에 넣는다.
            PhotobookCommonUtils.setImageDataScaleable(_template);
            DataTransManager dtMan = DataTransManager.getInstance();
            if (dtMan != null) {
                dtMan.setPhotoImageDataList(images);
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            int idx = PhotobookCommonUtils.getImageIndex(this, images, tempImageViewID);
            if (idx < 0)
                return;

            intent.putExtra("dataIndex", idx);
            startActivityForResult(intent, REQ_MODIFY);
            return;
        }// 사진 삭제
        else if (id == R.id.popup_menu_photo_delete) {
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(this, tempImageViewID);
            if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl))
                return;

            SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;

            SnapsOrderManager.removeBackgroundUploadOrgImageData(control.imgData);

            mPopupMenuView.dissmissPopover(false);
            control.imgData = null;
            control.angle = "";
            control.imagePath = "";
            control.imageLoadType = 0;
            control.isNoPrintImage = false;
            control.isSnsBookCover = true;
            control.isUploadFailedOrgImg = false;

            ((SnapsPagerController2) _loadPager).pageAdapter.notifyDataSetChanged();

            return;

        }// 사진 변경..
        else if (id == R.id.popup_menu_photo_change) {
            SnapsOrderManager.cancelCurrentImageUploadExecutor();

            mPopupMenuView.dissmissPopover(false);
            Setting.set(getApplicationContext(), "themekey", "");

            Intent intent = new Intent(getApplicationContext(), ImageSelectActivityV2.class);

            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setHomeSelectProduct(Config.SELECT_SINGLE_CHOOSE_TYPE)
                    .setOrientationChanged(true).create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            intent.putExtras(bundle);

            startActivityForResult(intent, REQ_PHOTO);
            return;
        }
    }

    private void init() {
        _canvasList = new ArrayList<Fragment>();

        // 공유 버튼은 삭제를 한다.
        findViewById(R.id.btnTopShare).setVisibility(View.GONE);

        // 페이지 카운터 삭제
        LinearLayout lyCounter = (LinearLayout) findViewById(R.id.pager_counter);
        lyCounter.setBackgroundColor(Color.argb(255, 235, 235, 235));
        lyCounter.setVisibility(View.GONE);

        // 정보 버튼 생성
        findViewById(R.id.btnTopInfo).setVisibility(View.VISIBLE);

        // 타이틀 생성
        findViewById(R.id.btnTopTitle).setVisibility(View.VISIBLE);

        ImageView ivOrder = (ImageView) findViewById(R.id.btnTopOrder);
        ivOrder.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivOrder.setImageResource(R.drawable.selector_cart_icon);

        ImageView ivBack = (ImageView) findViewById(R.id.btnTopBack);
        ivBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivBack.setImageResource(R.drawable.btn_prev);

        //CS 대응
        if (Config.isDevelopVersion()) {
            ivBack.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ImageRatioChecker imageRatioChecker = new ImageRatioChecker();
                    imageRatioChecker.show(SNSBookFragmentActivity.this, getTemplate());
                    return true;
                }
            });
        }

        LinearLayout lyPagerContainer = (LinearLayout) findViewById(R.id.pager_container_ly);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lyPagerContainer.getLayoutParams();
        lp.topMargin = UIUtil.convertPXtoDP(this, 100);
        lp.bottomMargin = 0;

        mRootView.setBackgroundColor(Color.argb(255, 235, 235, 235));

        pageProgress = new DialogDefaultProgress(this);
        _loadPager = new SnapsPagerController2(this, findViewById(R.id.pager_container), findViewById(R.id.pager));
        _mViewPager = (InterceptTouchableViewPager) ((SnapsPagerController2) _loadPager).getViewPager();
        _mContainer = ((SnapsPagerController2) _loadPager).getContainer();
        _mViewPager.setOnPageChangeListener(onPageChageListener);
        makeSnapsPageCaptureCanvas();

        FrameLayout frameMain = (FrameLayout) findViewById(R.id.frameMain);
        frameMain.setX(UIUtil.getScreenHeight(this));

        // 폴더 생성.
        FileUtil.initProjectFileSaveStorage();

        // 리시버 등록....
        IntentFilter filter = new IntentFilter(Const_VALUE.CLICK_LAYOUT_ACTION);
        receiver = new SnapsBroadcastReceiver();
        receiver.setImpRecevice(this);
        registerReceiver(receiver, filter);

        // noti 등록
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected void onCompleteLoadTemplate() {
        initSnapsOrderManager();
    }

    /***
     * 재편집시 텍스트 LineText를 구하는 함수..
     *
     * @param template
     */
    protected void calcTextControl(SnapsTemplate template) {
        setLayoutControlInfo();
        // 멀티 라인인 경우에는 lineText를 만들어준다.
        makeMultiTextLineText(template);
        setTextControlInfo();
    }

    /***
     * snapsTextControl인 경우
     */
    private void setTextControlInfo() {
        if (_pageList == null)
            return;

        try {
            for (SnapsPage sPage : _pageList) {
                if (sPage == null)
                    continue;

                ArrayList<SnapsControl> controls = sPage.getTextControlList();
                if (controls == null || controls.isEmpty())
                    continue;

                for (SnapsControl control : controls) {
                    if (control == null || !(control instanceof SnapsTextControl))
                        continue;

                    SnapsTextControl textControl = (SnapsTextControl) control;

                    float y = (float) textControl.getIntY();
                    int lines = textControl.textList.size();
                    if (lines > 0) {
                        for (LineText line : textControl.textList) {
                            line.x = textControl.x;
                            line.y = y + "";
                            y += (Float.parseFloat(line.height) + textControl.lineSpcing);

                        }
                    }// 라인 텍스트를 만들지 않는 경우
                    else if (textControl.text != null && textControl.text.length() > 0) {
                        LineText line = new LineText();
                        line.x = textControl.x;
                        line.y = textControl.y;
                        line.width = textControl.width;
                        line.height = textControl.height;
                        line.text = textControl.text;
                        textControl.textList.add(line);
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected boolean downloadTemplate(String templateId) {
        String url = SnapsAPI.GET_API_MULTITEMPLATE() + "&prmProdCode=" + productCode + "&prmTmplCode=" + templateId;

        Dlog.d("downloadTemplate() url:" + url);

        multiTemplate = GetTemplateLoad.getTemplateByXmlPullParser(url, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

        if (multiTemplate != null && multiTemplate.getPages() != null && !multiTemplate.getPages().isEmpty()) {

            // mask data 파일로 선저장.
            PhotobookCommonUtils.saveMaskData(multiTemplate);

            /**
             * 1.cover 2.index 3.title 4.page(left) 5.page(center) 6.page(right)
             */
            if (multiTemplate.getPages().size() >= 6) {

                SNSBookTemplateBgRes bgInfo = new SNSBookTemplateBgRes();
                SnapsBgControl leftBg, centerBg, rightBg;
                try {
                    SnapsPage page = multiTemplate.getPages().get(3);
                    if (page != null) {
                        leftBg = ((SnapsBgControl) page.getBgList().get(0));
                        bgInfo.setLeftResPath(leftBg.resourceURL);
                        bgInfo.setLeftResId(leftBg.srcTarget);
                    }

                    page = multiTemplate.getPages().get(4);
                    if (page != null) {
                        centerBg = ((SnapsBgControl) page.getBgList().get(0));
                        bgInfo.setCenterResPath(centerBg.resourceURL);
                        bgInfo.setCenterResId(centerBg.srcTarget);
                    }

                    page = multiTemplate.getPages().get(5);
                    if (page != null) {
                        rightBg = ((SnapsBgControl) page.getBgList().get(0));
                        bgInfo.setRightResPath(rightBg.resourceURL);
                        bgInfo.setRightResId(rightBg.srcTarget);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    return false;
                }

                StoryBookDataManager dataMan = StoryBookDataManager.getInstance();
                if (dataMan != null) {
                    dataMan.setBgResources(bgInfo);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected void downloadFont(SnapsTemplate template) {
        FontUtil.downloadFontFiles(this, template.fonts);
    }

    private void setLayoutControlInfo() {
        if (_pageList == null)
            return;

        try {
            for (SnapsPage sPage : _pageList) {
                if (sPage == null)
                    continue;

                ArrayList<SnapsControl> controls = sPage.getLayoutList();
                if (controls == null || controls.isEmpty())
                    continue;

                for (SnapsControl control : controls) {
                    if (control == null || !(control instanceof SnapsLayoutControl))
                        continue;

                    SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;

                    MyPhotoSelectImageData imgData = layoutControl.imgData;
                    if (imgData != null) {
                        if (imgData.F_IMG_WIDTH == null || imgData.F_IMG_WIDTH.length() < 1 || imgData.F_IMG_HEIGHT == null || imgData.F_IMG_HEIGHT.length() < 1) {
                            String width = "640", height = "640";
                            if (imgData.PATH.contains("width") && imgData.PATH.contains("height")) {
                                width = StringUtil.getTitleAtUrl(imgData.PATH, "width", true);
                                height = StringUtil.getTitleAtUrl(imgData.PATH, "height", true);
                            } else if (imgData.PATH.startsWith("http")) {
                                //UI Thread면 죽기 때문에..체크를 해 줌.
                                try {
                                    if (Looper.myLooper() != Looper.getMainLooper()) {
                                        Rect rect = HttpUtil.getNetworkImageRect(imgData.PATH);
                                        if (rect != null) {
                                            width = String.valueOf(rect.width());
                                            height = String.valueOf(rect.height());
                                        }
                                    }
                                } catch (Exception e) {
                                    Dlog.e(TAG, e);
                                }
                            }

                            imgData.F_IMG_WIDTH = width;
                            imgData.F_IMG_HEIGHT = height;
                        }

                        float w = Float.parseFloat(imgData.F_IMG_WIDTH);
                        float h = Float.parseFloat(imgData.F_IMG_HEIGHT);
                        if (imgData.ROTATE_ANGLE == 90 || imgData.ROTATE_ANGLE == 270) {
                            float t = w;
                            w = h;
                            h = t;
                        }

                        String[] arImgInfo = BitmapUtil.getImagePosition(0, layoutControl, w, h);

                        layoutControl.img_x = arImgInfo[0];
                        layoutControl.img_y = arImgInfo[1];
                        layoutControl.img_width = arImgInfo[2];
                        layoutControl.img_height = arImgInfo[3];

                        // 이상한 비율을 보정한다.
                        try {
                            float rectW = Float.parseFloat(layoutControl.img_width);
                            float rectH = Float.parseFloat(layoutControl.img_height);
                            boolean isWrongRatio = false;
                            if (imgData != null) {
                                // 스냅스 스티커의 경우 F_IMG_WIDTH, F_IMG_HEIGHT값이 ""로 저장되기 때문에 0으로 변경.
                                if (imgData.F_IMG_WIDTH == null || imgData.F_IMG_WIDTH.length() < 1)
                                    imgData.F_IMG_WIDTH = "0";
                                if (imgData.F_IMG_HEIGHT == null || imgData.F_IMG_HEIGHT.length() < 1)
                                    imgData.F_IMG_HEIGHT = "0";
                                float imgW = Float.parseFloat(imgData.F_IMG_WIDTH);
                                float imgH = Float.parseFloat(imgData.F_IMG_HEIGHT);
                                isWrongRatio = (imgW > imgH && rectW < rectH) || (imgW < imgH && rectW > rectH);
                            }

                            if (rectW <= 0 || rectH <= 0 || isWrongRatio)
                                BitmapUtil.setImageDimensionInfo(layoutControl);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected void makeMultiTextLineText(SnapsTemplate template) {
        for (SnapsPage page : template.getPages()) {
            for (SnapsControl control : page.getTextControlList()) {
                SnapsTextControl tControl = (SnapsTextControl) control;
                if (tControl.text != null && tControl.text.length() > 0) {
                    String[] textArr = tControl.text.split("\n");

                    if (textArr.length > 1) {
                        float lineHeight = 0.f;

                        if (Config.isSnapsDiary()) {
                            if (tControl.format != null && tControl.format.fontSize != null) {
                                try {
                                    lineHeight = Float.parseFloat(tControl.format.fontSize);
                                } catch (NumberFormatException e) {
                                    Dlog.e(TAG, e);
                                }
                            }

                            if (lineHeight <= 0) {
                                lineHeight = tControl.getIntHeight() / textArr.length;
                            }
                        } else {
                            lineHeight = tControl.getIntHeight() / textArr.length;
                        }

                        for (String s : textArr) {
                            LineText lineText = new LineText();
                            lineText.width = tControl.width;
                            lineText.height = lineHeight + "";
                            lineText.text = s;
                            tControl.textList.add(lineText);
                        }

                    }
                }
            }
        }
    }


    ViewPager.OnPageChangeListener onPageChageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            _mContainer.onPageScrollStateChanged(state);

            if (state == ViewPager.SCROLL_STATE_SETTLING)
                initCanvasMatrix();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            _mContainer.onPageScrolled(arg0, arg1, arg2);
            _mContainer.invalidate();

        }

        @Override
        public void onPageSelected(int index) {
            ((SnapsPagerController2) _loadPager).setPagerSelected(index);
            onPageSelect(index);

            m_iNowPage = index;
        }
    };

    protected void initCanvasMatrix() {
        if (mSnapsHandler != null)
            mSnapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_INIT_CANVAS_MATRIX, 500);
    }

    /***
     * 이미지 인텍스를 구하는 함수..
     *
     * @param images
     * @return
     */
    protected int getImageIndex(ArrayList<MyPhotoSelectImageData> images) {
        if (images == null || images.isEmpty())
            return 0;

        View imgView = findViewById(tempImageViewID);
        if (imgView == null)
            return -1;

        SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(imgView);

        int index = 0;
        if (snapsControl != null && snapsControl instanceof SnapsLayoutControl) {
            for (MyPhotoSelectImageData d : images) {
                if (d == ((SnapsLayoutControl) snapsControl).imgData)
                    return index;
                index++;
            }

        }

        return 0;
    }

    protected boolean checkBookPageCount() {
        SNSBookInfo info = getSNSBookInfo();

        if (info != null) {
            try {
                totalPageCount = Integer.parseInt(info.getPageCount());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        if (IS_EDIT_MODE) {
            createSNSBookInfoDialog();
            showSNSBookInfoDialog();
            return false;
        }

        if (totalPageCount < 1) {
            showPageCountErrDialog(0);
            return false;
        } else if (totalPageCount < LIMIT_MIN_PAGE_COUNT) {
            showPageCountErrDialog(LIMIT_MIN_PAGE_COUNT);
            return false;
        } else if (info.getMaxPageEdited()) {
            showPageCountErrDialog(LIMIT_MAX_PAGE_COUNT);
            return false;
        } else
            createSNSBookInfoDialog();

        return true;
    }

    protected void showPageCountErrDialog(int errType) {
        int iResId = 0;
        if (errType == LIMIT_MIN_PAGE_COUNT) {
            iResId = R.string.kakao_book_make_err_less_then_min_page;
            MessageUtil.alert(this, iResId, false, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    finishActivity();
                }
            });
        } else if (errType == LIMIT_MAX_PAGE_COUNT) {
            iResId = R.string.sns_book_exceed_max_page_error_message;
            MessageUtil.alert(this, iResId, false, new ICustomDialogListener() {

                @Override
                public void onClick(byte clickedOk) {
                    createSNSBookInfoDialog();
                    showSNSBookInfoDialog();
                }
            });
        } else {
            if (type == TYPE_DIARY) iResId = R.string.snaps_diary_empty_post_error_message_2;
            else if (type == TYPE_KAKAO_STORY)
                iResId = R.string.kakao_book_make_err_is_not_exist_page;
            else if (type == TYPE_FACEBOOK_PHOTOBOOK)
                iResId = R.string.facebook_photobook_error_no_post_exist;

            if (iResId > 0) MessageUtil.alert(this, iResId, false, clickedOk -> finishActivity());
        }
    }

    protected SNSBookInfo createSNSBookInfoFromSaveXml() {
        if (_template == null)
            return null;

        SnapsTemplateInfo templateInfo = _template.info;
        if (templateInfo == null)
            return null;

        SNSBookInfo info = new SNSBookInfo();

        info.setThumbUrl(templateInfo.F_SNS_BOOK_INFO_THUMBNAIL);
        info.setUserName(templateInfo.F_SNS_BOOK_INFO_USER_NAME);
        info.setPeriod(templateInfo.F_SNS_BOOK_INFO_PERIOD);
        info.setPageCount(String.valueOf(_template.getPages().size()));

        totalPageCount = Integer.parseInt(info.getPageCount());
        multiTemplate = _template;
        multiTemplate.priceList.add(0, saveXMLPriceInfo);

        return info;
    }

    protected void showSNSBookInfoDialog() {
        if (snsBookInfoDialog == null)
            return;

        if (!snsBookInfoDialog.isShowing())
            snsBookInfoDialog.showDialog();
    }

    private void performBackKey() {
        String msg = IS_EDIT_MODE ? getString(R.string.confirm_go_to_cart) : getString(R.string.no_cart_save_warning_message);
        if (mConfirmDialog == null || !mConfirmDialog.isShowing()) {
            mConfirmDialog = new CustomizeDialog(this, msg, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    if (clickedOk == ICustomDialogListener.OK)
                        finishActivity();
                }
            });
            mConfirmDialog.show();
        }
    }


    protected void loadFinish() {
        // 프로그레스가 완전히 그려지게 하기 위해 잠시 텀을 둠.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        CircleProgressView.getInstance(SNSBookFragmentActivity.this).Unload();

                        checkBookPageCount();
                        createSNSBookInfoDialog();
                        showSNSBookInfoDialog();

                        if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION))
                            requestNotifycation();
                    }
                });
            }
        }, 400);
    }

    /**
     * onDestroy에서 호출하면 IllegalStateException가 발생하므로, 액티비티가 완전히 닫히기 전에 호출 해 줘야 한다.
     */
    protected void finishActivity() {
        if (_loadPager != null) {
            try {
                _loadPager.close();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        finish();
    }

    private void onResumeControl() {
        String orderPauseState = SnapsOrderManager.getSnapsOrderStatePauseCode();
        if (orderPauseState.equalsIgnoreCase(SnapsOrderState.PAUSE_IMGSAVE)) {
            requestMakeMainPageThumbnailFile(getSnapsPageCaptureListener());
        } else if (orderPauseState.equalsIgnoreCase(SnapsOrderState.PAUSE_UPLOAD_COMPLETE)) {
            SnapsOrderManager.showCompleteUploadPopup();
        }

        SnapsOrderManager.setSnapsOrderStatePauseCode("");

        SnapsOrderManager.registerNetworkChangeReceiverOnResume();
    }

    // /***
    // * 인화불가 판정하는 함수... //카카오 사진들은 해상도가 불충분한 것들이 대부분일 수 있으며 정확하지 않으니, 체크 하지 않는다.
    // *
    // * @param template
    // */
    private void imageResolutionCheck(SnapsTemplate template) {
        SnapsLayoutControl layout = null;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.imgData != null) {
                    // 인쇄가능 여부..
                    try {
                        ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                    } catch (Exception e) {
                        SnapsAssert.assertException(this, e);
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    public void setImageDataScaleable(SnapsTemplate template) {
        SnapsLayoutControl layout = null;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);

            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                if (layout.imgData != null) {
                    layout.imgData.isNoPrint = layout.isNoPrintImage;
                    layout.imgData.mmPageWidth = Integer.parseInt(template.info.F_PAGE_MM_WIDTH);
                    layout.imgData.pxPageWidth = Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH);
                    layout.imgData.controlWidth = layout.width;
                }
            }
        }
    }

    /***
     * 이미지데이터를 가지고 이미지가 몇페이지에 있는지 구하는 함수..
     *
     * @param data
     * @return
     */
    protected int getImagePageIdx(MyPhotoSelectImageData data) {
        return data.pageIDX;
    }

    /***
     *
     * @param data
     */
    private void addImageData(MyPhotoSelectImageData data) {
    }

    /***
     * 이미지 데이터를 정리하는 함수...
     *
     * @return
     */
    protected ArrayList<MyPhotoSelectImageData> getMyPhotoSelectImageData(boolean onlyCover) {
        ArrayList<MyPhotoSelectImageData> _imageList = onlyCover ? PhotobookCommonUtils.getCoverImageListFromTemplate(_template) : PhotobookCommonUtils.getImageListFromTemplate(_template);
        if (_imageList == null)
            return null;
        // 순서대로 데이터를 정리한다.
        // 시간순으로 정렬하기
        // 빈칸때문에 예외처리..
        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();
        for (MyPhotoSelectImageData data : _imageList) {
            if (data != null)
                imageList.add(data);
        }

        Collections.sort(imageList, new ImageCompare());

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

    protected ArrayList<MyPhotoSelectImageData> getMyPhotoSelectCoverImageData() {
        ArrayList<MyPhotoSelectImageData> _imageList = PhotobookCommonUtils.getCoverImageListFromTemplate(_template);

        //CS 대응
        //커버 페이지가 아닌 전체 페이지의 이미지 정보를 구한다.
        if (Config.isDevelopVersion()) {
            _imageList = PhotobookCommonUtils.getAllImageListFromTemplate(_template);
        }

        if (_imageList == null)
            return null;
        // 순서대로 데이터를 정리한다.
        // 시간순으로 정렬하기
        // 빈칸때문에 예외처리..
        ArrayList<MyPhotoSelectImageData> imageList = new ArrayList<MyPhotoSelectImageData>();
        for (MyPhotoSelectImageData data : _imageList) {
            if (data != null)
                imageList.add(data);
        }

        Collections.sort(imageList, new ImageCompare());

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

    @Override
    public void onPause() {
        super.onPause();

        Config.setIS_MAKE_RUNNING(false);

        SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_APPLICATION);
    }

    /**
     * 아래 메서드들은 사진 변경을 위한 것들...모듈화하여 통합 할 필요가 있어 보임.
     */
    /***
     * 페이지와 regValue를 가지고 이미지 인덱스를 구하는 함수...
     *
     * @param page
     * @param regValue
     * @return
     */
    protected int getImageIDX(int page, String regValue) {
        return page * 1000 + MAX_LAYOUT_COUNT - Integer.parseInt(regValue);
    }

    protected void removeImageData(MyPhotoSelectImageData data) {
    }

    /***
     * 커버,타이틀, 페이지 를 합친다.
     */
    protected void setTemplate(SnapsTemplate template) {
        _template = template;
        _pageList = _template.getPages();

        String paperCode = Config.getPAPER_CODE();
        if (!StringUtil.isEmpty(paperCode)) _template.info.F_PAPER_CODE = paperCode;

        // 소프트커버 텍스트 설정
        // 책등에서 컬러를 가져와야 한다.
        SnapsTextControl tControl = template.getPages().get(0).getBookStick();
        if (template.info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.SOFT_COVER && tControl != null)
            _template.SNS_BOOK_STICK_COLOR = tControl.format.fontColor;

        _template.SNS_BOOK_STICK = "14";

        if (!IS_EDIT_MODE && (_template.info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.SOFT_COVER)) {
            _template.info.F_SNS_BOOK_INFO_PERIOD = getSNSBookInfo().getPeriod();
        }

        setLayoutControlInfo();

        setTextControlInfo();

        _template.initMaxPageInfo(getApplicationContext());

        _template.addSpine();
        _template.addQRcode(getQRCodeRect());
        _template.setApplyMaxPage();

        // 해상도 체크
        // save.xml를 로드한 경우 정렬을 하지 않는다.
        if (IS_EDIT_MODE)
            imageRange(_template);

        onCompleteLoadTemplate();
    }

    private void imageRange(SnapsTemplate template) {
        SnapsLayoutControl layout = null;
        ;
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);

                if (layout.imgData != null) {
                    layout.imgData.pageIDX = index;
                    layout.imgData.IMG_IDX = getImageIDX(index, layout.regValue);
                    layout.imgData.mmPageWidth = StringUtil.isEmpty(_template.info.F_PAGE_MM_WIDTH) ? 0 : Float.parseFloat(_template.info.F_PAGE_MM_WIDTH);
                    layout.imgData.pxPageWidth = StringUtil.isEmpty(_template.info.F_PAGE_PIXEL_WIDTH) ? 0 : Integer.parseInt(_template.info.F_PAGE_PIXEL_WIDTH);
                    layout.imgData.controlWidth = layout.width;

                    try {
                        ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), layout);
                    } catch (Exception e) {
                        SnapsAssert.assertException(this, e);
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    private void setAddedPageCountInfo() {
        int addedPageCount = PhotobookCommonUtils.getAddedPageCount(multiTemplate, totalPageCount);
        multiTemplate.setF_ADD_PAGE(addedPageCount > 0 ? addedPageCount : 0);
    }

    protected void createSNSBookInfoDialog() {
        SNSBookInfo info = getSNSBookInfo();

        info.setCoverType(parsedCoverTypeStr());
        info.setPaperType(parsedPaperTypeStr());
        info.setPriceOrigin(PhotobookCommonUtils.calculateAddedPageTotalOrgPrice(this, multiTemplate, totalPageCount));
        info.setPriceSale(PhotobookCommonUtils.calculateAddedPageTotalSellPrice(this, multiTemplate, totalPageCount));

        setAddedPageCountInfo();

        snsBookInfoDialog = new DialogSNSBookLoadComplateView(this, this, type);
        snsBookInfoDialog.setDatas(info);

        snsBookInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (currentPage == 0) {
                    findEmptyCoverImage();
                    //예전 튜토리얼
//                        if (Config.useKorean()) {
//                            if( Config.TEST_TUTORIAL || !Setting.getBoolean(getApplicationContext(), Const_VALUE.KEY_INSTAGRAM_BOOK_TUTORIAL) ) {
//                                ThemeBookTutorialDialogFragment diag = ThemeBookTutorialDialogFragment.newInstance();
//                                diag.startPage = 0;
//                                diag.bookcase = "insta_cover";
//
//                                diag.show(getFragmentManager(), "t");
//                                Setting.set(getApplicationContext(), Const_VALUE.KEY_INSTAGRAM_BOOK_TUTORIAL, true);
//                            }
//                        }
                }
            }
        });


        if (!IS_EDIT_MODE)
            saveTemplateInfo(info);

        if (mSnapsHandler != null)
            mSnapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_LOAD_PAGER_ON_FIRST_LOAD, 500);
    }

    private void findEmptyCoverImage() {
        if (_template == null || _template.getPages() == null) return;
        String msg = null;
        View targetView = null;
        SnapsLayoutControl snapsLayoutControl = PhotobookCommonUtils.findEmptyCoverLayoutControlWithPageList(_template.getPages());
        if (snapsLayoutControl != null) {
            if (snapsLayoutControl.imgData == null) {
                msg = getString(R.string.tutorial_touch_this);
            } else {
                msg = getString(R.string.tutorial_touch_edit_exclamation_mark);
            }
            targetView = (View) findViewById(snapsLayoutControl.getControlId());
            SnapsTutorialUtil.showTooltip(SNSBookFragmentActivity.this, new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM)
                    .setText(msg)
                    .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_SNSBOOK_FIND_COVER)
                    .setTargetView(targetView)
                    .create());
        }
    }

    /**
     * 페이지를 화면에 표시를 한다.
     */
    private void loadPager() {
        _loadPager.loadPage(_pageList, _canvasList, 0, 0, 5, true);
    }

    private void saveTemplateInfo(SNSBookInfo info) {
        if (info == null || _template == null)
            return;

        SnapsTemplateInfo saveInfo = _template.info;
        if (saveInfo == null)
            return;

        saveInfo.F_SNS_BOOK_INFO_USER_NAME = info.getUserName();
        saveInfo.F_SNS_BOOK_INFO_PERIOD = info.getPeriod();
        saveInfo.F_SNS_BOOK_INFO_THUMBNAIL = info.getThumbUrl();
    }

    private String parsedCoverTypeStr() {
        String result = "";
        if (multiTemplate != null && multiTemplate.info != null) {
            result = multiTemplate.info.F_COVER_TYPE;
            if (result != null) {
                if (result.equalsIgnoreCase("soft"))
                    result = getString(R.string.soft_cover) + ", ";//"소프트커버, ";
                else
                    result = getString(R.string.hard_cover) + ", ";//"하드커버, ";
            }
        }

        return result;
    }

    private String parsedPaperTypeStr() {
        String result = "";
        if (multiTemplate != null && multiTemplate.info != null) {
            result = multiTemplate.info.F_PAPER_CODE;
            if (result != null) {
                if (result.equalsIgnoreCase("160001"))
                    result = getString(R.string.matt_paper);//무광용지 ";
                else if (result.equalsIgnoreCase("160002"))
                    result = getString(R.string.glossy_paper);//유광용지, ";
//                else if(result.equalsIgnoreCase("160003"))
                else {
                }
            }
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    protected void requestNotifycation() {
        int titleResId = -1;
        int textResId = -1;
        Class c = null;

        switch (type) {
            case TYPE_KAKAO_STORY:
                titleResId = R.string.kakao_book_make_notify_title;
                textResId = R.string.kakao_book_make_notify_desc;
                c = StoryBookFragmentActivity.class;
                break;
            case TYPE_FACEBOOK_PHOTOBOOK:
                titleResId = R.string.facebook_photobook_make_notify_title;
                textResId = R.string.facebook_photobook_make_notify_desc;
                c = FacebookPhotobookFragmentActivity.class;
                break;
            case TYPE_DIARY:
                titleResId = R.string.snaps_diary_make_notify_title;
                textResId = R.string.snaps_diary_make_notify_desc;
                c = SnapsDiaryPublishFragmentActivity.class;
                break;
        }
        if (titleResId < 0 || textResId < 0) return;

        if (mNotiID != -999)
            mNM.cancel(mNotiID);

        CharSequence title = getText(titleResId);
        CharSequence text = getText(textResId);

        Notification notification = null;

        if (c == null) return;

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_status_large);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, getResources().getDisplayMetrics());
        largeIcon = Bitmap.createScaledBitmap(largeIcon, size, size, false);
        try {
            int flag = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, c).setFlags(flag), PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_status_new : R.drawable.ic_status)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setLargeIcon(largeIcon);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) builder.setColor(0xFF000000);
            notification = builder.build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                int smallIconId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
                if (smallIconId != 0)
                    notification.contentView.setViewVisibility(smallIconId, View.INVISIBLE);
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
            return;
        }

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNM.notify(titleResId, notification);
        mNotiID = titleResId;
    }

    protected void makeSnapsPageCaptureCanvas() {
        if (Config.getPROD_CODE() == null || Config.getPROD_CODE().length() < 1) {
            finishActivity();
            return;
        }

        captureFragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
        Bundle bundle = new Bundle();
        bundle.putInt("index", 0);
        bundle.putBoolean("pageSave", true);
        bundle.putBoolean("pageLoad", false);
        bundle.putBoolean("preThumbnail", true);
        bundle.putBoolean("visibleButton", false);
        captureFragment.setArguments(bundle);

        FragmentUtil.replce(R.id.frameMain, this, captureFragment);
    }

    private void requestMakePageThumbnail(ISnapsCaptureListener captureListener) {
        if (!SnapsOrderManager.isUploadingProject()) {
            return;
        }

        setSnapsPageCaptureListener(captureListener);

        if (captureFragment != null) {
            captureFragment.getArguments().clear();
            captureFragment.getArguments().putInt("index", 0);
            captureFragment.getArguments().putBoolean("pageSave", true);
            captureFragment.getArguments().putBoolean("preThumbnail", true);
            captureFragment.getArguments().putBoolean("pageLoad", false);

            if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                // 현재 Destory 상태이면 멈추고 index 값을 줄인다.
                SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_CAPTURE);
            } else {
                captureFragment.makeSnapsCanvas();
            }
        }
    }

    @Override
    public void onOrderStateChanged(int state) {
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

            boolean isEditableKaKaoImg = intent.getBooleanExtra("isEditableImg", false);
            if (!isEditableKaKaoImg)
                return;

            int control_id = intent.getIntExtra("control_id", -1);
            boolean isEdited = intent.getBooleanExtra("isEdited", false);

            tempImageViewID = control_id;
            if (tempImageViewID == -1)
                return;

            View v = findViewById(tempImageViewID);
            if (v == null)
                return;

            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(v);
            if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) return;

            if (!isEdited) {
                Intent broadIntent = new Intent(Const_VALUE.RESET_LAYOUT_ACTION);
                sendBroadcast(broadIntent);

                Setting.set(getApplicationContext(), "themekey", "");

                Intent in = new Intent(getApplicationContext(), ImageSelectActivityV2.class);

                int recommendWidth = 0, recommendHeight = 0;
                Rect rect = ResolutionUtil.getEnableResolution(_template.info.F_PAGE_MM_WIDTH, _template.info.F_PAGE_PIXEL_WIDTH, (SnapsLayoutControl) snapsControl);
                if (rect != null) {
                    recommendWidth = rect.right;
                    recommendHeight = rect.bottom;
                }

                ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                        .setHomeSelectProduct(Config.SELECT_SINGLE_CHOOSE_TYPE)
                        .setRecommendWidth(recommendWidth)
                        .setRecommendHeight(recommendHeight)
                        .setOrientationChanged(true).create();

                Bundle bundle = new Bundle();
                bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
                in.putExtras(bundle);

                startActivityForResult(in, REQ_PHOTO);
                return;
            }

            Dlog.d("onReceiveData() snapsControl:" + snapsControl);

            Rect rect = new Rect();
            View popupView = mRootView.findViewById(tempImageViewID);
            popupView.getGlobalVisibleRect(rect);
            Dlog.d("onReceiveData() getGlobalVisibleRect:" + rect.toString());

            int popWidth = UIUtil.convertDPtoPX(getApplicationContext(), 150);
            int popHeight = UIUtil.convertDPtoPX(getApplicationContext(), 37);

            mPopupMenuView = new PopoverView(SNSBookFragmentActivity.this, R.layout.popmenu_photo);
            View convertView = mPopupMenuView.getConvertView();
            if (convertView != null) {
                TextView modifyBtn = (TextView) convertView.findViewById(R.id.popup_menu_photo_change);
                if (modifyBtn != null) {
                    modifyBtn.setBackgroundResource(R.drawable.menu_bg_right);
                }
            }

            mPopupMenuView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));
            DataTransManager transMan = DataTransManager.getInstance();
            if (transMan != null) {
                ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
                if (coordInfo != null) {
                    Rect orgRect = new Rect(rect);
                    coordInfo.convertPopupOverRect(rect);
                    rect.offset(0, -UIUtil.convertDPtoPX(getApplicationContext(), 20));
                    mPopupMenuView.setArrowPosition(orgRect, coordInfo.getTranslateX(), 1, true);
                }
            } else {
                DataTransManager.notifyAppFinish(this);
                return;
            }

            mPopupMenuView.showPopoverFromRectInViewGroup(mRootView, rect, PopoverView.PopoverArrowDirectionUp, true);

            font.FTextView tvDelete = (font.FTextView) mPopupMenuView.findViewById(R.id.popup_menu_photo_delete);
            tvDelete.setVisibility(View.GONE);

            View lineView = (View) mPopupMenuView.findViewById(R.id.popup_menu_photo_change_line);
            if (lineView != null)
                lineView.setVisibility(View.GONE);

            try {
                // 일단 팝업을 띄우고, 컨트롤 아이디 임시 저장을 한다. 그럼 끝..
                ImageView imgView = (ImageView) findViewById(tempImageViewID);
                SnapsControl control = PhotobookCommonUtils.getSnapsControlFromView(imgView);
                if (control != null) {
                    SnapsLayoutControl press_control = (SnapsLayoutControl) control;
                    if (press_control.imgData != null) {
                        if (press_control.isUploadFailedOrgImg) {
                            MessageUtil.toast(this, R.string.select_upload_failed_org_img_msg, Gravity.CENTER);
                        } else if (press_control.isNoPrintImage) {
                            MessageUtil.noPrintToast(this, ResolutionConstants.NO_PRINT_TOAST_OFFSETX_BASIC, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC);
                        }
                    }
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private void initSnapsOrderManager() {
        try {
            SnapsOrderManager.initialize(this);

            SnapsOrderManager.setImageUploadStateListener(this);
            SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    public ArrayList<MyPhotoSelectImageData> getUploadImageList() {
        return getMyPhotoSelectImageData(true);
    }

    @Override
    public SnapsOrderAttribute getSnapsOrderAttribute() {
        return new SnapsOrderAttribute.Builder()
                .setActivity(this)
                .setEditMode(IS_EDIT_MODE)
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
        requestMakePageThumbnail(captureListener);
    }

    @Override
    public void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {
    }

    @Override
    public void onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {
    }

    @Override
    public void onUploadFailedOrgImgWhenSaveToBasket() {
        SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createUploadFailedImagePopupAttribute(this, Config.getPROJ_CODE(), true);

        SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute, new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
            @Override
            public void onShowUploadFailedImagePopup() {
            }

            @Override
            public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
                PhotobookCommonUtils.setUploadFailedIconVisibleStateToShow(_template);

                try {
                    SnapsPagerController2 controller2 = (SnapsPagerController2) _loadPager;
                    controller2.pageAdapter.notifyDataSetChanged();

                    selectViewPagerToErrorImagePosition(uploadFailedImageList);

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private void selectViewPagerToErrorImagePosition(List<MyPhotoSelectImageData> uploadFailedImageList) {
        try {
            if (_mViewPager != null) {
                MyPhotoSelectImageData uploadErrImgData = PhotobookCommonUtils.findFirstIndexOfUploadFailedOrgImageOnList(uploadFailedImageList);
                int pageIndex = PhotobookCommonUtils.findImageDataIndexOnPageList(_pageList, uploadErrImgData);
                if (pageIndex > 0)
                    _mViewPager.setCurrentItem(pageIndex);
            }

            if (Const_PRODUCT.isSnapsDiary()) {
                if (mSnapsHandler != null)
                    mSnapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_SHOW_DIARY_PHOTO_MODIFY_MSG, 2000);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onDisconnectNetwork() {
    }

    @Override
    public void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener) {
    }

    private void showDiaryBookPhotoErrorModifyMsgAlert() {
        if (isFinishing()) return;
        MessageUtil.alert(this, getString(R.string.snaps_diary_book_image_error_alert_msg), "", R.string.cancel, R.string.diary_modify, false, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (clickedOk == ICustomDialogListener.OK) {
                    goToDiaryMainActivity();
                    GoHomeOpserver.notifyGoHome();
                }
            }
        });
    }

    private void goToDiaryMainActivity() {
        Intent diaryItt = new Intent(this, SnapsDiaryMainActivity.class);
        startActivity(diaryItt);
    }

    private static final int HANDLER_MSG_INIT_CANVAS_MATRIX = 0;
    private static final int HANDLER_MSG_SHOW_DIARY_PHOTO_MODIFY_MSG = 1;
    private static final int HANDLER_MSG_LOAD_PAGER_ON_FIRST_LOAD = 2;

    @Override
    public void handleMessage(Message msg) {
        if (msg == null)
            return;

        try {
            switch (msg.what) {
                case HANDLER_MSG_INIT_CANVAS_MATRIX:
                    if (m_iInitedCanvasIdx == m_iNowPage)
                        return;
                    m_iInitedCanvasIdx = m_iNowPage;

                    if (_mViewPager != null) _mViewPager.initCanvasMatrix();
                    break;
                case HANDLER_MSG_SHOW_DIARY_PHOTO_MODIFY_MSG:
                    showDiaryBookPhotoErrorModifyMsgAlert();
                    break;
                case HANDLER_MSG_LOAD_PAGER_ON_FIRST_LOAD:
                    loadPager();

                    onPageSelect(0);
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
