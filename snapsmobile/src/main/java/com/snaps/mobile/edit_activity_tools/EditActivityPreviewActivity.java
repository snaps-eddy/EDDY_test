package com.snaps.mobile.edit_activity_tools;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.board.fragment.DialogSharePopupFragment;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.edit.BaseEditFragmentActivity;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragmentFactory;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.EXTRAS_KEY_ACTIVE_ROTATION_SENSOR;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.EXTRAS_KEY_PAGE_INDEX;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.EXTRAS_KEY_SCREEN_ORIENTATION_ACT_INFO;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.RESULT_CODE_EDIT;
import static com.snaps.mobile.edit_activity_tools.EditActivityPreviewActivity.eActivityResultType.CART;
import static com.snaps.mobile.edit_activity_tools.EditActivityPreviewActivity.eActivityResultType.EDIT;
import static com.snaps.mobile.edit_activity_tools.EditActivityPreviewActivity.eActivityResultType.FINISH;

/**
 * created by caesar
 * 편집 화면에서 미리 보기 버튼을 눌렀을 때, 기존에는 썸네일 파일을 보여주었으나,
 * 그리는 방식으로 변경 함.
 * (단, 우드블럭과 엽서는 미리 보기 화면을 기존과 동일하게 처리 함.)
 */
public class EditActivityPreviewActivity extends BaseEditFragmentActivity implements OrientationSensorManager.OrientationChangeListener {
    private static final String TAG = EditActivityPreviewActivity.class.getSimpleName();

    public enum eActivityResultType {
        CART,
        EDIT,
        FINISH
    }

    EditActivityPreviewDetailPagerAdapter pagerAdapter;
    ViewPager vpagerMyArtworkDetail;
    ImageView btnTopOrder;
    TextView txtTopOrder;

    TextView txtProfileName;

    // data
    public String projCode;
    String prodCode;
    String[] textCollage;
    String[] textNamecard;

    public ArrayList<String> themeBookPageThumbnailPaths = null;
    boolean isVertical = false;

    private SnapsPagerController2 _loadPager;

    Queue<Integer> pageLoadQueue = new LinkedBlockingQueue<Integer>();
    public SnapsCanvasFragment canvasFragment = null;

    private boolean m_isFirstLoad = false;

    private String title = "";

    private boolean isActiveOrientationSensor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        SnapsOrderManager.setSnapsOrderStatePauseCode("");

        isVertical = PhotobookCommonUtils.isPortraitScreenProduct();
        if (isVertical) {
            UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            int curOrientation = getOrientation();
            if (curOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            } else {
                UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            isActiveOrientationSensor = isActiveOrientationSensor();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        _template = SnapsTemplateManager.getInstance().getSnapsTemplate();

        if (_template != null) {
            _pageList = _template.getPages();
        }

        if (_pageList == null) {
            Toast.makeText(EditActivityPreviewActivity.this, R.string.loading_fail, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.edit_activity_preview_activity);

        RelativeLayout lyTitle = (RelativeLayout) findViewById(R.id.rl_edittitle);
        lyTitle.setBackgroundDrawable(null);

        textCollage = new String[2];
        textCollage[0] = getString(R.string.cover);
        textCollage[1] = getString(R.string.inner_title_page);

        textNamecard = new String[3];
        textNamecard[0] = getString(R.string.front_page);
        textNamecard[1] = getString(R.string.back_page);
        textNamecard[2] = getString(R.string.item_case);

        vpagerMyArtworkDetail = (ViewPager) findViewById(R.id.vpagerMyArtworkDetail);
        txtProfileName = (TextView) findViewById(R.id.ThemeTitleText);

        btnTopOrder = (ImageView) findViewById(R.id.ThemecartBtn);
        txtTopOrder = (TextView) findViewById(R.id.ThemecartTxt);
        txtTopOrder.setPadding(0, 0, UIUtil.convertDPtoPX(this, 16), 0);

        findViewById(R.id.ThemeTitleLeft).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finishActivityAfterSendPageIndex(FINISH);

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_clickBack)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

            }
        });

        findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finishActivityAfterSendPageIndex(FINISH);

                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_clickBack)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

            }
        });

        if (Config.isSnapsSticker(prodCode) || Config.isThemeBook(prodCode)) {
            txtTopOrder.setVisibility(View.GONE);
            btnTopOrder.setVisibility(View.GONE);
        } else {
            txtTopOrder.setVisibility(View.VISIBLE);
            btnTopOrder.setVisibility(View.GONE);
        }

        btnTopOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkLoginState()) {
                    finishActivityAfterSendPageIndex(CART);
                }
            }
        });

        txtTopOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkLoginState()) {
                    finishActivityAfterSendPageIndex(CART);
                }
            }
        });

        m_isFirstLoad = true;
    }

    private int getOrientation() {
        Intent intent = getIntent();
        if (intent != null) {
            int getOrientationValue = intent.getIntExtra(EXTRAS_KEY_SCREEN_ORIENTATION_ACT_INFO, -1);

            if (getOrientationValue != -1) {
                return getOrientationValue;
            }
        }
        return UIUtil.getScreenOrientation(this);
    }

    private boolean isActiveOrientationSensor() {
        Intent intent = getIntent();
        return intent != null && intent.getBooleanExtra(EXTRAS_KEY_ACTIVE_ROTATION_SENSOR, false);
    }

    @Override
    public void onOrientationChanged(int orientation) {
//		if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
//			return;
//		}
        if (OrientationSensorManager.isActiveAutoRotation(getActivity()) == false) {
            return;
        }

        OrientationSensorManager sensorManager = OrientationSensorManager.getInstance();
        if (!sensorManager.isAllowOrientationChangeTime()) {
            return;
        }

        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                finishActivityAfterSendPageIndex(FINISH);

                sensorManager.updateLastOrientationChangeTime();
                break;
        }

        sensorManager.setLastScreenOrientation(orientation);
    }

    @Override
    public void onBackPressed() {
        finishActivityAfterSendPageIndex(FINISH);

        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_clickBack)
                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
    }

    private void finishActivityAfterSendPageIndex(eActivityResultType resultType) {
        int index = _loadPager.getPagerSelected();
        int resultCode = RESULT_CANCELED;
        switch (resultType) {
            case CART:
                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_clickMovecart)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

                resultCode = RESULT_OK;
                break;
            case EDIT:
                resultCode = RESULT_CODE_EDIT;
                break;
            case FINISH: {
                resultCode = RESULT_CANCELED;
            }
            break;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA, index);
        setResult(resultCode, intent);

        this.finish();
    }

    private boolean checkLoginState() {
        String userNo = SnapsLoginManager.getUUserNo(this);
        if ((userNo == null || userNo.length() < 1) && !SnapsTPAppManager.isThirdPartyApp(this)) { // 로그인 체크
            SnapsLoginManager.startLogInProcess(this, Const_VALUES.LOGIN_P_RESULT, null, SnapsOrderConstants.LOGIN_REQUSTCODE);
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            SnapsTimerProgressView.destroyProgressView();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    void layout() {
        _canvasList = new ArrayList<Fragment>();

        ImageView btnTopShare = (ImageView) findViewById(R.id.btnTopShare);
        btnTopShare.setVisibility(View.GONE);// 공유 불가
        txtTopOrder.setVisibility(View.VISIBLE);
        btnTopOrder.setVisibility(View.GONE);

        title = getString(R.string.preview);
        if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
            title = getString(R.string.view_larger);
            ;
        }

        txtProfileName.setText(title);//"미리보기");

        vpagerMyArtworkDetail.setOffscreenPageLimit(4);

        int margin = 40;
        if (isVertical) {
            margin = 0;
            vpagerMyArtworkDetail.getLayoutParams().width = UIUtil.getCalcMyartworkWidthVerticalMode(this);
        } else {
            if (Const_PRODUCT.isPostCardProduct()) {
                margin = 0;
                vpagerMyArtworkDetail.getLayoutParams().width = UIUtil.getCalcMyartworkWidthForPostCard(this);
            } else {
                vpagerMyArtworkDetail.getLayoutParams().width = UIUtil.getCalcMyartworkWidth2(this);
            }
        }

        vpagerMyArtworkDetail.setPageMargin(UIUtil.convertDPtoPX(this, margin));

        final com.snaps.mobile.activity.edit.PagerContainer container = (com.snaps.mobile.activity.edit.PagerContainer) findViewById(R.id.pager_container);
        vpagerMyArtworkDetail.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int position) {
                setStatus();

                if (_loadPager != null) {
                    _loadPager.setPagerSelected(position); //FIXME... onPageScrolled에서 옮겻다.
                }
            }

            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
                if (container != null) {
                    container.invalidate();// 4.0.3 버전에서 PagerContainer가 invalidate 되지 않는 문제 해결법
                }
            }

            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_SETTLING) {
                    if (_loadPager != null) {
                        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_page)
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(_loadPager.getPagerSelected()))
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                    }
                }
            }
        });

        if (Const_PRODUCT.isPostCardProduct() || Const_PRODUCT.isWoodBlockProduct()) {
            makePreviewImage();
        } else {
            loadPage();
        }

        setStatus();
    }

    private void makePreviewImage() {
        if (_pageList == null || _pageList.isEmpty()) {
            return;
        }

        SnapsTimerProgressView.showProgress(this,
                SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_TASKS, getString(R.string.plz_wait_for_load));

        offerQueue(0, _pageList.size() - 1);

        canvasFragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
        if (canvasFragment == null) {
            //Null이면, 이미 상태가 정상이 아니라고 보고 앱을 강제 종료 시킨다.
            DataTransManager.notifyAppFinish(this);
            return;
        }

        canvasFragment.setIsPreview(true);

        //대표 썸네일 만드는 과정도 생략한다. (TODO 마지막에 만들자..)
        Bundle bundle = new Bundle();
        bundle.putInt("index", 0);
        bundle.putBoolean("pageSave", false);
        bundle.putBoolean("pageLoad", false);
        bundle.putBoolean("preThumbnail", false);
        bundle.putBoolean("visibleButton", false);
        canvasFragment.setArguments(bundle);

        FragmentUtil.replce(R.id.frameMain, EditActivityPreviewActivity.this, canvasFragment);

        setPageThumbnail(-1, "");
    }

    private void offerQueue(int start, int end) {
        Dlog.d("offerQueue() start:" + start + ", end:" + end);

        int idx = start;
        for (int i = 0; i < (end + 1 - start); i++) {
            if (!pageLoadQueue.contains(idx)) {
                pageLoadQueue.offer(idx++);
            }
        }
    }

    ArrayList<String> getPageThumbnailPaths() {
        if (_pageList == null || _pageList.isEmpty()) {
            return null;
        }
        ArrayList<String> paths = new ArrayList<String>();

        for (SnapsPage page : _pageList) {
            if (page == null) {
                continue;
            }
            if (Const_PRODUCT.isPackageProduct()) {
                paths.add(page.previewPath);
            } else {
                paths.add(page.thumbnailPath);
            }
        }

        return paths;
    }

    private void completeMakeThumbnail() {
        themeBookPageThumbnailPaths = getPageThumbnailPaths();

        pagerAdapter = new EditActivityPreviewDetailPagerAdapter(this);
        vpagerMyArtworkDetail.setAdapter(pagerAdapter);

        SnapsTimerProgressView.destroyProgressView();
    }

    @Override
    synchronized public void setPageThumbnail(final int pageIdx, String filePath) {
        //FIXME 하단 썸네일 다시 만들도록 처리가 필요 함.
        if (_pageList == null || pageLoadQueue == null || _template == null || _template.getPages() == null) {
            SnapsTimerProgressView.destroyProgressView();
            return;
        }

        SnapsTimerProgressView.updateTasksProgressValue((int) (((pageIdx + 1) / (float) _template.getPages().size()) * 100));

        // 다음 page 처리
        Integer nextPage = pageLoadQueue.poll();

        // 만약에 nextPage가 _page리스트에 없는경우.. 다시 실행을 한다.
        if (nextPage != null && _pageList.size() > nextPage && nextPage >= 0) { //FIXME 여기를 좀 건드려야 겠다.
            try {
                canvasFragment.getArguments().putBoolean("pageSave", false);
                canvasFragment.getArguments().putBoolean("pageLoad", false);
                canvasFragment.getArguments().putInt("index", nextPage);
                canvasFragment.getArguments().putBoolean("visibleButton", false);
                canvasFragment.getArguments().putBoolean("preThumbnail", true);
                canvasFragment.makeSnapsCanvas();
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsTimerProgressView.destroyProgressView();
                MessageUtil.toast(EditActivityPreviewActivity.this, getString(R.string.refresh_screen_error_msg));
            }

        } else if (pageLoadQueue.size() > 0) {
            setPageThumbnail(-1, "");
            return;
        } else {
            completeMakeThumbnail();
        }

        if (pageIdx == -1) {
            return;
        }
    }

    private void loadPage() {
        _loadPager = new SnapsPagerController2(this, findViewById(R.id.pager_container), findViewById(R.id.vpagerMyArtworkDetail));
        _loadPager.setIsPreview(true);
        _loadPager.loadPage(_pageList, _canvasList, 0, 0, 5, !isVertical);

        _loadPager.setPagerCurrentItem(getSelectedPageIndexFromIntent());
        _loadPager.setItemClickListener(new SnapsCommonResultListener<SnapsPageEditRequestInfo>() {
            @Override
            public void onResult(SnapsPageEditRequestInfo pageEditRequestInfo) {
                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_clickPage)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, (pageEditRequestInfo != null ? String.valueOf(pageEditRequestInfo.getPageIndex()) : "0"))
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

                finishActivityAfterSendPageIndex(EDIT);
            }
        });
    }

    private int getSelectedPageIndexFromIntent() {
        try {
            return getIntent().getIntExtra(EXTRAS_KEY_PAGE_INDEX, 0);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    void setStatus() {
        if (_loadPager == null || _loadPager.pageAdapter == null) {
            return;
        }
        String msg = "";
        int curIdx = vpagerMyArtworkDetail.getCurrentItem();

        if (Config.isCalendar()) {
            msg = "";//getCalendarPreviewStatus(curIdx);

        } else {
            if (curIdx < textCollage.length) {
                msg = textCollage[curIdx];
            } else {
                int pp = (curIdx - 2) * 2 + 2;

                msg = Integer.toString(pp) + " , " + Integer.toString(++pp) + " p";
            }
        }

        String text = "";
        if (!Config.isNotCoverPhotoBook()) {
            text = String.format("%s (%s)", title, msg);
        } else {
            text = String.format("%s", title);
        }

        SpannableString ss = new SpannableString(text);
        ss.setSpan(new RelativeSizeSpan(0.8f), title.length(), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtProfileName.setText(ss, TextView.BufferType.SPANNABLE);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ThemeTitleLeft || v.getId() == R.id.ThemeTitleLeftLy) {// 뒤로
            finishActivityAfterSendPageIndex(FINISH);

            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_preview_clickBack)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        } else if (v.getId() == R.id.btnTopShare) {// 공유
            DialogSharePopupFragment.newInstance().show(getSupportFragmentManager(), "dialog");
        } else if (v.getId() == R.id.btn_share_kakaotalk) {// 카카오톡 공유
            Config.setPROJ_CODE(projCode);
            Config.setPROD_CODE(prodCode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_isFirstLoad) {
            m_isFirstLoad = false;
            layout();
        }

        if (isActiveOrientationSensor) {
            OrientationSensorManager.resume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isActiveOrientationSensor) {
            OrientationSensorManager.pause();
        }
    }
}
