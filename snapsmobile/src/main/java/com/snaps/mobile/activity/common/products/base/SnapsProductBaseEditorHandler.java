package com.snaps.mobile.activity.common.products.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditControls;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.edit.PagerContainer;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragmentFactory;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.DialogSmartSnapsProgress;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.themebook.ImageEditActivity;
import com.snaps.mobile.activity.themebook.OrientationChecker;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.activity.themebook.ThemeCategoryActivity;
import com.snaps.mobile.activity.themebook.ThemeCoverActivity;
import com.snaps.mobile.activity.themebook.ThemeTitleActivity;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.activity.themebook.design_list.NewThemeDesignListActivity;
import com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList;
import com.snaps.mobile.activity.themebook.interfaceis.ISnapsEditTextControlHandleListener;
import com.snaps.mobile.activity.themebook.interfaceis.SnapsEditTextControlHandleData;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SnapsLayoutUpdateInfo;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.cseditor.ImageRatioChecker;
import com.snaps.mobile.edit_activity_tools.EditActivityPreviewActivity;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.EditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailRecyclerView;
import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailSmoothScroller;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbItemTouchHelperCallback;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImagePopup;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static com.snaps.common.data.img.MyPhotoSelectImageData.INVALID_ROTATE_ANGLE;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_CLICKED_LAYOUT_CONTROL;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_NOTIFY_ORIENTATION_STATE;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_REFRESH_CHANGED_PHOTO;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_SHOW_IMG_EMPTY_LAYOUT_CONTROL_TOOLTIP;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_SHOW_POPOVER_VIEW;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_TRY_SHOW_TEXT_EDIT_TUTORIAL;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_UNLOCK_ROTATE_BLOCK;
import static com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor.HANDLER_MSG_UPLOAD_THUMB_IMAGES;
import static com.snaps.mobile.activity.themebook.PhotobookCommonUtils.myComparator;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_ADD_PAGE;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_CHANGE_PAGE;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_CONTENT;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_COVER_CHANGE;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_COVER_TEXT;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_EDIT_TEXT;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_MODIFY;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_PHOTO;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_PREVIEW;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_PRODUCT_ADD_PAGE;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_PRODUCT_CHANGE_PAGE;
import static com.snaps.mobile.order.ISnapsOrderStateListener.ORDER_STATE_CANCEL;
import static com.snaps.mobile.order.ISnapsOrderStateListener.ORDER_STATE_STOP;
import static com.snaps.mobile.order.ISnapsOrderStateListener.ORDER_STATE_UPLOADING;

/**
 * Created by ysjeong on 2017. 10. 24..
 */

/**
 * FIXME 일단 SnapsProductEditorBase에서 처리하는 내용을 다 여기에 몰아 넣긴 했는데 각 기능별로 분리 시키는 작업이 필요하다
 */
public class SnapsProductBaseEditorHandler {
    private static final String TAG = SnapsProductBaseEditorHandler.class.getSimpleName();

    private static final int VIEW_PAGER_MOVING_TIME = 2000;

    SnapsProductBaseEditor productEditorBase = null;

    SnapsProductEditorSmartSnapsHandler snapsProductEditorSmartSnapsHandler = null;

    private Thread resumeSyncChecker = null;

    private boolean isActivityResumeFinished = false;
    private boolean isAppliedBlurActivity = false;
    private boolean isAccordionCardTutorial = true;

    private PopoverView popupMenuView;
    private int tryCountOfFindEmptyCoverImage = 0, tryCountOfFindTextControl = 0;

    public static SnapsProductBaseEditorHandler createHandlerWithBridge(SnapsProductBaseEditor uiHandleBridge) {
        SnapsProductBaseEditorHandler baseHandler = new SnapsProductBaseEditorHandler();
        baseHandler.productEditorBase = uiHandleBridge;
        baseHandler.snapsProductEditorSmartSnapsHandler = SnapsProductEditorSmartSnapsHandler.createInstanceWithBaseHandler(baseHandler);
        return baseHandler;
    }

    boolean isAddedPage() {
        return (getSnapsTemplate() != null && getPageList() != null)
                && PhotobookCommonUtils.getAddedPageCount(getSnapsTemplate(), PhotobookCommonUtils.getEachPageCount(getPageList().size())) > 0;
    }

    void refreshUI() {
        try {
            handleNotifyCenterPagerAdapter();

            handleNotifyThumbnailPagerAdapter();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    void handleFirstSmartSnapsAnimationSuspend() throws Exception {
        snapsProductEditorSmartSnapsHandler.handleFirstSmartSnapsAnimationComplete();
        snapsProductEditorSmartSnapsHandler.handleOnSmartSnapsAnimationSuspended();
    }

    void suspendSmartSnapsFaceSearching() throws Exception {
        snapsProductEditorSmartSnapsHandler.suspendSmartSnapsFaceSearching();
    }

    DialogSmartSnapsProgress createBaseSmartSnapsProgressDialog() throws Exception {
        return snapsProductEditorSmartSnapsHandler.createBaseSmartSnapsProgressDialog();
    }

    SnapsProductEditorSmartSnapsHandler getSnapsProductEditorSmartSnapsHandler() {
        return snapsProductEditorSmartSnapsHandler;
    }

    //CS 처리용 구멍 뚫기
    public void setPageCurrentItem_forCS(int index, boolean smoothScroll) {
        setPageCurrentItem(index, smoothScroll);
    }

    void setPageCurrentItem(int index, boolean smoothScroll) {
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null && index != centerPager.getCurrentItem())
            centerPager.setCurrentItem(index, smoothScroll);
    }

    public void handleInitLayout() throws Exception {
        final OrientationManager orientationManager = getOrientationManager();

        FrameLayout dragLayer = (FrameLayout) findViewById(orientationManager.isLandScapeMode() ? R.id.drag_layer_h : R.id.drag_layer_v);
        dragLayer.setVisibility(View.VISIBLE);

        findViewById(orientationManager.isLandScapeMode() ? R.id.drag_layer_v : R.id.drag_layer_h).setVisibility(View.GONE);

        getEditControls().setRootView((RelativeLayout) findViewById(orientationManager.isLandScapeMode() ? R.id.rootLayout_h : R.id.rootLayout_v));

        SnapsCanvasFragment canvasFragment = null;
        if (!StringUtil.isEmpty(Config.getPROD_CODE())) {
            canvasFragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
            getEditControls().setCanvasFragment(canvasFragment);
        }

        final ImageView textModifybtn = (ImageView) dragLayer.findViewById(R.id.theme_text_modify);
        getEditControls().setThemeTextModify(textModifybtn);

        final ImageView coverModifyBtn = (ImageView) dragLayer.findViewById(R.id.theme_cover_modify);
        getEditControls().setThemeCoverModify(coverModifyBtn);

        final ImageView calendarPeriodModify = (ImageView) dragLayer.findViewById(R.id.calendar_change_period_btn);
        getEditControls().setCalendarPeriodModify(calendarPeriodModify);

        final ImageView infoBtn = (ImageView) dragLayer.findViewById(R.id.theme_info);
        getEditControls().setThemeInfo(infoBtn);

        final ImageView previewBtn = (ImageView) dragLayer.findViewById(R.id.theme_preview);
        getEditControls().setThemePreviewBtn(previewBtn);

        setPreviewBtnVisibleState();

        previewBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
                getEditorBase().onClickedPreview();
            }
        });

        // 텍스트 변경...
        textModifybtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
                getEditorBase().onClickedChangeTitle();
            }
        });

        // 커버변경버튼 클릭시...
        coverModifyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
                getEditorBase().onClickedChangeDesign();
            }
        });

        //달력 월 변경 버튼
        if (calendarPeriodModify != null) {
            calendarPeriodModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
                    getEditorBase().onClickedChangePeriod();
                }
            });
        }

        if (infoBtn != null) {
            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
                    getEditorBase().onClickedInfo();
                }
            });
        }

        final RelativeLayout addPageLy = (RelativeLayout) findViewById(orientationManager.isLandScapeMode() ? R.id.pager_container_bottom_add_page_ly_h : R.id.pager_container_bottom_add_page_ly_v);
        getEditControls().setAddPageLy(addPageLy);

        addPageLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditorBase().onClickedAddPage();
            }
        });

        ImageView btnPrev = (ImageView) dragLayer.findViewById(R.id.ThemeTitleLeft);
        ImageView btnCart = (ImageView) dragLayer.findViewById(R.id.ThemecartBtn);

        //CS 대응
        if (Config.isDevelopVersion()) {
            btnPrev.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ImageRatioChecker imageRatioChecker = new ImageRatioChecker();
                    imageRatioChecker.show(getActivity(), getSnapsTemplate(), SnapsProductBaseEditorHandler.this);
                    return true;
                }
            });
        }

        final TextView cartBtn = (TextView) dragLayer.findViewById(R.id.ThemecartTxt);
        getEditControls().setCartTxt(cartBtn);

        cartBtn.setVisibility(View.VISIBLE);
        btnCart.setVisibility(View.GONE);

        btnPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (findViewById(R.id.btnTitleLeftLy) != null) {
            findViewById(R.id.btnTitleLeftLy).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if (findViewById(R.id.ThemeTitleLeftLy) != null) {
            findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // 장바구니에 담기..
        btnCart.setOnClickListener(getOnClickListener());
        cartBtn.setOnClickListener(getOnClickListener());

        // center viewPager

        final PagerContainer container = (PagerContainer) findViewById(orientationManager.isLandScapeMode() ? R.id.pager_container_h : R.id.pager_container_v);
        getEditControls().setContainer(container);
        final InterceptTouchableViewPager centerPager = container.getViewPager();
        getEditControls().setCenterPager(centerPager);

//		mCenterPager.setClipChildren(false);
        centerPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            getEditInfo().setTouchDownX((int) event.getX());
                            break;
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_UP:
                            int curX = (int) event.getX();
                            float moveX = Math.abs(getEditInfo().getTouchDownX() - curX);
                            if (moveX < 50)
                                return false;
                        default:
                            return false;
                    }

                    return true;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }
        });

        centerPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean isPaging = false;

            @Override
            public void onPageSelected(final int page) {
                getEditorBase().onCenterPagerSelected(page);

                if (page >= 1 && previewBtn.isShown()) {
                    if (Config.isPhotobooks()) {
                        int topMargin = 0;
                        if (orientationManager.isLandScapeMode()) {
                            topMargin = 0;
                        } else {
                            topMargin = -24;
                        }
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                                .setText(getActivity().getString(R.string.tutorial_full_screen_preview))
                                .setTargetView(previewBtn)
                                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                                .create());
                    }
                    //3페이지 이상 선택시 페이지 추가 안내
                }
                if (page >= 2 && addPageLy.isShown()) {
                    if (Config.isPhotobooks()) {
                        int topMargin = 0;
                        if (orientationManager.isLandScapeMode()) {
                            topMargin = 5;
                        } else {
                            topMargin = -18;
                        }
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                                .setText(getActivity().getString(R.string.tutorial_add_page_touch))
                                .setTargetView(addPageLy)
                                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                                .create());
                    }
                }
                if (page == getPageList().size() - 1) {
                    int topMargin = 0;
                    if (orientationManager.isLandScapeMode()) {
                        topMargin = 20;
                    } else {
                        topMargin = -24;
                    }
                    if (cartBtn != null) {
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                                .setText(getActivity().getString(R.string.tutorial_cart_save_after_edit))
                                .setTargetView(cartBtn)
                                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                                .create());
                    }
                }
                if (Const_PRODUCT.isAccordionCardProduct()) {
                    currentPage = page;
                    startTutorial(page, orientationManager);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                container.invalidate();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    initCanvasMatrix();
                }
            }
        });

        SnapsPagerController2 loadPager = new SnapsPagerController2((FragmentActivity) getActivity(),
                getActivity().findViewById(orientationManager.isLandScapeMode() ? R.id.pager_container_h : R.id.pager_container_v),
                getActivity().findViewById(orientationManager.isLandScapeMode() ? R.id.pager_h : R.id.pager_v));
        getEditControls().setLoadPager(loadPager);

        handleInitThumbnailRecyclerView();

        final FrameLayout tooltipTutorialLayout = (FrameLayout) findViewById(R.id.tooltip_tutorial_layout);
        getEditControls().setTooltipTutorialLayout(tooltipTutorialLayout);

        //Seal Sticker 에서 추가된 배경 추가 버튼
        getEditControls().setToggleBtnBackgroundToolbox(findViewById(R.id.btn_toggle_background_toolbox));
        getEditControls().setViewBackgroundToolbox(findViewById(R.id.ll_background_toolbox));
        getEditControls().setViewOutsideBackgroundToolbox(findViewById(R.id.background_toolbox_outside));
        getEditControls().setBtnBackgroundToolChangeSource(findViewById(R.id.btn_background_tool_change_source));
        getEditControls().setBtnBackgroundToolEdit(findViewById(R.id.btn_background_tool_edit));
        getEditControls().setBtnBackgroundToolDelete(findViewById(R.id.btn_background_tool_delete));
    }

    private Handler handler = null;
    private int currentPage = 0;

    private void startTutorial(final int page, final OrientationManager orientationManager) {
        if (handler == null) {
            setHandler(orientationManager);
        }
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(page, 500);
    }


    private void setHandler(final OrientationManager orientationManager) {

        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                final int page = msg.what;

                if (shouldShowTooltip(page)) {
                    try {
                        SnapsTutorialUtil.clearTooltip();
                        boolean first = false;

                        int topMargin = 0;
                        String msgStr = getActivity().getString(R.string.back_first_front_last_same);
                        if (msg.what == 0) {

                            msgStr = getActivity().getString(R.string.front_first_back_last_same);
                            first = true;
                        }
                        SnapsLayoutControl snapsLayoutControl = PhotobookCommonUtils.findFirstOrLastLayoutControlWithPageList(getPageList(), first);
                        int y = snapsLayoutControl.getIntY();
                        if (orientationManager.isLandScapeMode()) {
                            topMargin = -y;
                        } else {

                            topMargin = -y;
                        }
                        RelativeLayout rootView = getEditControls().getRootView();
                        if (rootView != null) {
                            View targetView = (View) rootView.findViewById(snapsLayoutControl.getControlId());
                            SnapsTutorialUtil.showTooltipAlways(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(orientationManager.isLandScapeMode() ? SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.ACCORDION_CARD_LANDSCAPE : SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.ACCORDION_CARD)
                                    .setText(msgStr)
                                    .setTargetView(targetView)
                                    .setTopMargin(topMargin)
                                    .setShowResultListener(new SnapsTutorialAttribute.ShowResultListener() {
                                        @Override
                                        public boolean result() {
                                            //실제 툴팁이 계산되는 약간의 딜레이에서 화면이 전환대는 경우가 있어서 마지막으로 한번 더 체크
                                            if (shouldShowTooltip(page)) {
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .create());
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        };
    }

    private boolean shouldShowTooltip(int page) {
        return page == currentPage;
    }

    void handleClickedPreview() {
        OrientationManager.fixCurrentOrientation(getActivity());
        SnapsTimerProgressView.destroyProgressView();

        Intent intent = new Intent(getActivity(), EditActivityPreviewActivity.class);
        intent.putExtra("themebookPreView", true);
        getActivity().startActivityForResult(intent, REQ_PREVIEW);
    }

    void handleClickedChangeTitle() {
        OrientationManager.fixCurrentOrientation(getActivity());
        Intent intent = new Intent(getActivity(), ThemeTitleActivity.class);
        intent.putExtra("whereis", "edit");
        intent.putExtra(Const_EKEY.SCREEN_ORIENTATION_STATE_CHANGE, true);
        getActivity().startActivityForResult(intent, REQ_COVER_TEXT);
    }

    void handleRemoveDragView() throws Exception {
        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter != null)
            thumbnailAdapter.releaseInstance();

        if (getThumbnailRecyclerView() != null) {
            getThumbnailRecyclerView().destroyView();
        }

        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.releaseInstance();
        }
    }

    void handleOnClickedAddPage() {
        if (System.currentTimeMillis() - getEditInfo().getPrevAddPageClickedTime() < 800) {
            return;
        }

        getEditInfo().setPrevAddPageClickedTime(System.currentTimeMillis());
        if (isOverPageCount()) {
            showPageOverCountToastMessage();
            return;
        }

        productEditorBase.addPage();
    }

    private void handleInitThumbnailRecyclerView() throws Exception {
        handleRemoveDragView();

        OrientationManager orientationManager = getOrientationManager();
        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter == null) {
            thumbnailAdapter = createThumbnailAdapter();
            getEditControls().setThumbnailAdapter(thumbnailAdapter);

            thumbnailAdapter.setSnapsProductEditorAPI(getEditorBase());
        }

        if (orientationManager.isLandScapeMode()) {
            EditActivityThumbnailRecyclerView thumbnailVerticalRecyclerView = (EditActivityThumbnailRecyclerView) findViewById(R.id.activity_edit_themebook_thumbnail_recyclerview_h);
            if (thumbnailVerticalRecyclerView != null) {
                getEditControls().setThumbnailVerticalRecyclerView(thumbnailVerticalRecyclerView);
                thumbnailVerticalRecyclerView.setHasFixedSize(true);
                thumbnailVerticalRecyclerView.setIsLandsacpeMode(true);

                ItemTouchHelper verticalItemTouchHelper = getEditControls().getVerticalItemTouchHelper();

                if (verticalItemTouchHelper == null) {
                    EditActivityThumbItemTouchHelperCallback callback = new EditActivityThumbItemTouchHelperCallback(thumbnailAdapter);
                    callback.setIsLandScapeMode(true);

                    verticalItemTouchHelper = new ItemTouchHelper(callback);
                    verticalItemTouchHelper.attachToRecyclerView(getEditControls().getThumbnailVerticalRecyclerView());
                    getEditControls().setVerticalItemTouchHelper(verticalItemTouchHelper);
                }

                thumbnailAdapter.setIsLandscapeMode(true);

                EditActivityThumbnailSmoothScroller linearLayoutManager = new EditActivityThumbnailSmoothScroller(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                thumbnailVerticalRecyclerView.setLayoutManager(linearLayoutManager);
                thumbnailVerticalRecyclerView.setAdapter(thumbnailAdapter);
            }
        } else {
            EditActivityThumbnailRecyclerView thumbnailHorizontalRecyclerView = (EditActivityThumbnailRecyclerView) findViewById(R.id.activity_edit_themebook_thumbnail_recyclerview_v);
            if (thumbnailHorizontalRecyclerView != null) {
                getEditControls().setThumbnailHorizontalRecyclerView(thumbnailHorizontalRecyclerView);
                thumbnailHorizontalRecyclerView.setHasFixedSize(true);
                thumbnailHorizontalRecyclerView.setIsLandsacpeMode(false);

                ItemTouchHelper horizontalItemTouchHelper = getEditControls().getHorizontalItemTouchHelper();
                if (horizontalItemTouchHelper == null) {
                    EditActivityThumbItemTouchHelperCallback callback = new EditActivityThumbItemTouchHelperCallback(thumbnailAdapter);
                    callback.setIsLandScapeMode(false);

                    horizontalItemTouchHelper = new ItemTouchHelper(callback);
                    horizontalItemTouchHelper.attachToRecyclerView(thumbnailHorizontalRecyclerView);

                    getEditControls().setHorizontalItemTouchHelper(horizontalItemTouchHelper);
                }

                thumbnailAdapter.setIsLandscapeMode(false);

                EditActivityThumbnailSmoothScroller linearLayoutManager = new EditActivityThumbnailSmoothScroller(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                thumbnailHorizontalRecyclerView.setLayoutManager(linearLayoutManager);
                thumbnailHorizontalRecyclerView.setAdapter(thumbnailAdapter);
            }
        }

        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null)
            thumbnailUtil.init(getActivity(), getThumbnailRecyclerView(), thumbnailAdapter, orientationManager.isLandScapeMode());
    }

    public void handleShowCoverChangeActcity() throws Exception {
        SnapsOrderManager.cancelCurrentImageUploadExecutor();

        Intent intent = new Intent(getActivity(), ThemeCoverActivity.class);
        intent.putExtra("returnType", true);// false이면 커버아이디, true이면 커버 xml path를 리턴한다.
        intent.putExtra("leatherCover", getSnapsTemplate().info.F_COVER_TYPE.equals("leather"));

        SnapsPage coverpage = getPageList().get(0);

        int coverWidth = coverpage.getOriginWidth();
        int coverHeight = Integer.parseInt(coverpage.height);
        float ratio = 0.0f;

        if (coverWidth >= coverHeight) {
            ratio = coverWidth / coverHeight;
        } else {
            ratio = coverHeight / coverWidth + 101.0f;
        }

        intent.putExtra("simplecoverRatio", ratio);

        getActivity().startActivityForResult(intent, REQ_COVER_CHANGE);
    }

    public void handleNotExistTitleActLayout() throws Exception {
        handleRemoveDesignListButton();

        OrientationManager orientationManager = getOrientationManager();
        View galleryView = getActivity().findViewById(orientationManager.isLandScapeMode() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v);
        if (galleryView != null)
            galleryView.setVisibility(View.VISIBLE);

        if (getThumbnailRecyclerView() != null)
            getThumbnailRecyclerView().setVisibility(View.VISIBLE);

        setPreviewBtnVisibleState();
    }

    private void handleRemoveDesignListButton() throws Exception {
        if (getActivity().findViewById(R.id.horizontal_line) != null)
            getActivity().findViewById(R.id.horizontal_line).setVisibility(View.GONE);

        RelativeLayout addPageLy = getEditControls().getAddPageLy();
        if (addPageLy != null)
            addPageLy.setVisibility(View.GONE);

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null)
            textModify.setVisibility(View.GONE);

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.GONE);
    }

    public void handleDismissPopOvers() throws Exception {
        PopoverView popupMenuGalleryView = getEditControls().getPopupMenuGalleryView();
        if (popupMenuGalleryView != null && popupMenuGalleryView.isShown())
            popupMenuGalleryView.dissmissPopover(false);

        PopoverView popoverView = getEditControls().getPopupMenuView();
        if (popoverView != null && popoverView.isShown())
            popoverView.dissmissPopover(false);

        Dialog numperPickerDialog = getEditControls().getNumperPickerDialog();
        if (numperPickerDialog != null && numperPickerDialog.isShowing()) {
            numperPickerDialog.dismiss();
        }

        FrameLayout tooltipTutorialLayout = getEditControls().getTooltipTutorialLayout();
        if (tooltipTutorialLayout != null) {
            tooltipTutorialLayout.removeAllViews();
        }
    }

    public void handleInitDragView() throws Exception {
        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.setPageList(getPageList());
            thumbnailUtil.setItemDecoration();

            if (Const_PRODUCT.isBothSidePrintProduct()) {
                thumbnailUtil.sortPagesIndex(getActivity(), getPageList(), getSnapsTemplate()._backPageList);
            }

            if (getSnapsTemplate().info.getCoverType() != SnapsTemplateInfo.COVER_TYPE.NONE_COVER) {
                // 최대페이지, 최소페이지, 책등추가 페이지..
                String baseQuantity = getSnapsTemplate().info.F_BASE_QUANTITY;
                if (StringUtil.isEmpty(baseQuantity)) {
                    baseQuantity = "0";
                }

                thumbnailUtil.setMinPage(Integer.parseInt(baseQuantity));
                thumbnailUtil.setMaxPage(Integer.parseInt(getSnapsTemplate().info.F_MAX_QUANTITY));
                // 하드커버인 경우 책등추가메세지를 표시하지 말아야 함..
                if (getSnapsTemplate().info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER)
                    thumbnailUtil.setSpinePage(-1);
                else if (getSnapsTemplate().info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.SOFT_COVER)
                    // 소프트 커버인 경우
                    thumbnailUtil.setSpinePage(getSnapsTemplate().info.getSoftCoverAddSpineText());
            }
        }

        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter != null)
            thumbnailAdapter.setData(getPageList());
    }

    //TODO ... 리펙토링 필요..
    public void handleChangeCover(String xmlPath) throws Exception {
        // 프로그래스바 표시
        // 프로그래스바 제거
        final String url = SnapsAPI.DOMAIN(false) + xmlPath;

        ATask.executeVoid(new ATask.OnTask() {

            SnapsPage newPage = null;
            List<MyPhotoSelectImageData> smartSnapsImageList = null;

            @Override
            public void onPre() {
                showPageProgress();
            }

            @Override
            public void onPost() {
                try {
                    // 페이지 교체...
                    if (newPage != null) {
                        getPageList().remove(0);

                        getPageList().add(0, newPage);

                        getSnapsTemplate().addSpine();
                        getSnapsTemplate().addQRcode(getEditorBase().getQRCodeRect());
                    }

                    // maxpage 설정...
                    getSnapsTemplate().setApplyMaxPage();

                    if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                        SmartSnapsManager.setSmartAreaSearching(true);
                    }

                    handleNotifyCenterPagerAdapter();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getEditorBase().offerQueue(0, 0);
                            getEditorBase().refreshPageThumbnail();

                            if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissPageProgress();

                                        try {
                                            SmartSnapsUtil.refreshSmartSnapsImgInfoOnNewLayoutWithImgList(getActivity(), getSnapsTemplate(), smartSnapsImageList, 0);
                                            SmartSnapsManager.startSmartSnapsAutoFitImage(getEditorBase().getDefaultSmartSnapsAnimationListener(), SmartSnapsConstants.eSmartSnapsProgressType.CHANGE_DESIGN, 0);
                                        } catch (Exception e) {
                                            Dlog.e(TAG, e);
                                        }
                                    }
                                }, 200); //SnapsPageCanvas가 갱신 되게 하기 위해 조금 기다린다
                            }
                        }
                    }, 200);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onBG() {
                // xml 다운로드
                // xml 파싱.
                // pagelist 변
                SnapsTemplate template = GetTemplateLoad.getThemeBookTemplate(url, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                // 커버배경에 클릭이 되도록 설정..
                template.setBgClickEnable(0, true);
                if (template.getPages().size() > 0) {

                    // 새로운 커버에 이미지가 넣기..
                    newPage = template.getPages().get(0);
                    newPage.info = getSnapsTemplate().info;
                    int imgCnt = newPage.getLayoutList().size();

                    int index = 0;

                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    try {
                        smartSnapsImageList = smartSnapsManager.createSmartSnapsImageListWithPageIdx(0);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        SnapsAssert.assertException(e);
                    }

                    // 예전커버.
                    SnapsPage oldPage = getPageList().get(0);
                    for (SnapsControl control : oldPage.getLayoutList()) {
                        if (control != null && control instanceof SnapsLayoutControl) {
                            SnapsLayoutControl oldControl = (SnapsLayoutControl) control;
                            if (oldControl.imgData != null && oldControl.type.equalsIgnoreCase("browse_file")) {
                                if (index < imgCnt) {
                                    SnapsLayoutControl newControl = ((SnapsLayoutControl) newPage.getLayoutList().get(index));
                                    newControl.setControlId(-1);

                                    if (oldControl.imgData.ORIGINAL_ROTATE_ANGLE != INVALID_ROTATE_ANGLE)
                                        oldControl.imgData.ROTATE_ANGLE = oldControl.imgData.ORIGINAL_ROTATE_ANGLE;

                                    //효과 필터가 적용 된 사진은 회전 정보가 반영 되어 있기 때문에 원래 각도로 복구해서 로딩한다.
                                    if (oldControl.imgData.isApplyEffect
                                            && (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != oldControl.imgData.ROTATE_ANGLE_THUMB || oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE == INVALID_ROTATE_ANGLE)) {
                                        try {
                                            if (!ImageFilters.updateEffectImageToOrgAngle(getActivity(), oldControl.imgData)) {
                                                oldControl.imgData.isApplyEffect = false;
                                            }
                                        } catch (Exception e) {
                                            Dlog.e(TAG, e);
                                            oldControl.imgData.isApplyEffect = false;
                                        }
                                    }

                                    if (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) //만약 ratio 오류같은 게 발생한다면 이부분을 의심해보자
                                        oldControl.imgData.ROTATE_ANGLE_THUMB = oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE;

                                    newControl.imgData = oldControl.imgData;

                                    newControl.imgData.FREE_ANGLE = 0;
                                    newControl.imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
                                    newControl.imgData.isAdjustableCropMode = false;
                                    newControl.imgData.ADJ_CROP_INFO = new AdjustableCropInfo();

                                    newControl.imgData.IMG_IDX = Integer.parseInt(0 + "" + newPage.getLayoutList().get(index).regValue);
                                    newControl.freeAngle = 0;// oldControl.imgData.FREE_ANGLE;
                                    newControl.angle = String.valueOf(oldControl.imgData.ROTATE_ANGLE);
                                    newControl.imagePath = oldControl.imgData.PATH;
                                    newControl.imageLoadType = oldControl.imgData.KIND;
                                    newControl.imgData.cropRatio = newControl.getRatio();

                                    // 인쇄가능 여부..
                                    try {
                                        getEditorBase().setPhotoResolutionEnableWithLayoutControl(newControl);
                                    } catch (Exception e) {
                                        Dlog.e(TAG, e);
                                    }

                                    if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                                        try {
                                            MyPhotoSelectImageData imageData = newControl.imgData;
                                            if (imageData != null && imageData.isSmartSnapsSupport()) {
                                                SmartSnapsUtil.setSmartImgDataStateReadyOnChangeLayout(imageData, index);

                                                smartSnapsImageList.add(imageData);
                                            }
                                        } catch (Exception e) {
                                            Dlog.e(TAG, e);
                                        }
                                    }
                                } else {
                                    // _imageList.remove(oldControl.imgData);
                                }

                                SnapsOrderManager.removeBackgroundUploadOrgImageData(oldControl.imgData);
                                // 커버 이미지 삭제..
                                oldControl.imgData = null;
                                index++;
                            }
                        }
                    }

                    // 새커버에 제목 입력하기..
                    for (SnapsControl control : newPage.getControlList()) {
                        if (control instanceof SnapsTextControl) {
                            ((SnapsTextControl) control).text = Config.getPROJ_NAME();
                        }
                        control.setPageIndex(oldPage.getPageID());
                        control.setControlId(-1);
                    }

                    for (SnapsControl control : newPage.getLayoutList()) {
                        control.setPageIndex(oldPage.getPageID());
                        control.setControlId(-1);
                    }

                    for (SnapsControl control : newPage.getBgList()) {
                        control.setPageIndex(oldPage.getPageID());
                        control.setControlId(-1);
                    }

                    for (SnapsControl control : newPage.getFormList()) {
                        control.setPageIndex(oldPage.getPageID());
                        control.setControlId(-1);
                    }
                }
            }
        });
    }

    public void handleInitControls() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null)
            return;

        getEditInfo().setCanvasList(new ArrayList<Fragment>());

        getEditorBase().initProjectCodeAndProdCode();

        SnapsCanvasFragment canvasFragment = null;
        if (!StringUtil.isEmpty(Config.getPROD_CODE()))
            canvasFragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
        getEditControls().setCanvasFragment(canvasFragment);

        if (canvasFragment == null) {
            //Null이면, 이미 상태가 정상이 아니라고 보고 앱을 강제 종료 시킨다.
            DataTransManager.notifyAppFinish(getActivity());
            return;
        }

        getEditorBase().setPageProgress(new DialogDefaultProgress(getActivity()));

        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
            try {
                getEditorBase().setSmartSnapsPageProgress(createBaseSmartSnapsProgressDialog());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        // 폴더 생성.
        FileUtil.initProjectFileSaveStorage();

        OrientationManager orientationManager = getOrientationManager();

        SnapsPagerController2 loadPager = new SnapsPagerController2((FragmentActivity) getActivity(),
                getActivity().findViewById(orientationManager.isLandScapeMode() ? R.id.pager_container_h : R.id.pager_container_v),
                getActivity().findViewById(orientationManager.isLandScapeMode() ? R.id.pager_h : R.id.pager_v));
        getEditControls().setLoadPager(loadPager);
        loadPager.setBackgroundColor(0xFFEEEEEE);

        getEditControls().setPageClipBoard(new ThemeBookClipBoard(getActivity()));

        getActivity().getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true, getRotationObserver());
    }

    public void handleOnScreenRotated(Configuration newConfig) throws Exception {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (SmartSnapsManager.isSupportSmartSnapsProduct() && smartSnapsManager.isScreenRotationLock()) {
            return;
        }

        if (OrientationSensorManager.isActiveAutoRotation(getActivity()) == false) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            handleChangeRotatedLayout(Configuration.ORIENTATION_PORTRAIT);
            return;
        }

        try {
            if (getOrientationManager().isBlockRotate() || !isActivityResumeFinished())
                return;

            handleChangeRotatedLayout(newConfig.orientation);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void handleBasePreviewBtnVisibleState() throws Exception {
        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            previewBtn.setVisibility(View.VISIBLE);
        }
    }

    private void handleChangeRotatedLayout() {
        if (getOrientationManager() == null)
            return;

        try {
            handleChangeRotatedLayout(getOrientationManager().getOrientation());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void handleChangeRotatedLayout(int orientation) throws Exception {
        if (!checkChangedOrientaion(orientation)) return;

        //화면이 회전하면, 팝업은 닫아준다.
        handleDismissPopOvers();

        if (checkRotationExceptionProduct()) return;

        try {
            if (getOrientationManager().isLandScapeMode()) {
                UIUtil.updateFullscreenStatus(getActivity(), true);
//				setContentView(R.layout.activity_edit_themebook_horizontal);
            } else {
                UIUtil.updateFullscreenStatus(getActivity(), false);
//				setContentView(R.layout.activity_edit_themebook);
            }

            SnapsTutorialUtil.clearTooltip();
            handleInitLayout();

            handleRecoveryLayout();

            getEditorBase().handleScreenRotatedHook();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void handleRestoreRotateState() throws Exception {
        if (getOrientationManager().isBlockRotate())
            return;

        // onRestoreInstanceState가 호출 되기 전에 방향 전환이 되면 exception이 발생한다.
        if (getSnapsHandler() != null)
            getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_NOTIFY_ORIENTATION_STATE, 1000);
    }

    private void handleRecoveryLayout() throws Exception {

        if (!isActivityResumeFinished()) {
            waitActivityResume();
            return;
        }

        loadPager();

        handleInitDragView();

        SnapsCanvasFragment canvasFragment = getEditControls().getCanvasFragment();
        //썸네일 새로 만들 이유가 없다..
        if (canvasFragment != null && canvasFragment.getArguments() == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", 0);
            bundle.putBoolean("pageSave", false);
            bundle.putBoolean("pageLoad", false);
            bundle.putBoolean("preThumbnail", false);
            bundle.putBoolean("visibleButton", false);
            canvasFragment.setArguments(bundle);
        }

        FragmentUtil.replce(getOrientationManager().isLandScapeMode() ? R.id.frameMain_h : R.id.frameMain_v, (FragmentActivity) getActivity(), canvasFragment);

        setPageCurrentItem(getCurrentPageIndex(), false);

        //사진 편집에 들어가서 Orientaion을 변경했다면, 화면 전환이 완료 되고 나서야 내용을 적용 시킨다..
        if (getOrientationChecker().isChangedPhoto()
                && getOrientationChecker().isChangedOrientationAtImgEditor()) {
            if (getSnapsHandler() != null)
                getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_REFRESH_CHANGED_PHOTO, 1000);
        }

        getEditorBase().initControlVisibleStateOnActivityCreate();
    }

    public void loadPager() throws Exception {
        SnapsPagerController2 loadPager = getEditControls().getLoadPager();
        if (loadPager != null)
            loadPager.loadPage(getPageList(), getEditInfo().getCanvasList(), 0, 0, 5, getOrientationManager().isLandScapeMode());
    }

    // 단일 페이지 상품은 회전할 때 레이아웃이 바뀌어도 의미가 없다..(썸네일이 없다.)
    private boolean checkRotationExceptionProduct() throws Exception {
        if (Const_PRODUCT.isSinglePageProduct() && !Config.isNotCoverPhotoBook() && !Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            if (getOrientationManager().isLandScapeMode()) {
                UIUtil.updateFullscreenStatus(getActivity(), true);
            } else {
                UIUtil.updateFullscreenStatus(getActivity(), false);
            }

            getEditorBase().handleScreenRotatedHook();

            SnapsPagerController2 loadPager = getEditControls().getLoadPager();
            if (loadPager != null)
                loadPager.loadPage(getPageList(), getEditInfo().getCanvasList(), 0, 0, 5, getOrientationManager().isLandScapeMode());

            //사진 편집에 들어가서 Orientaion을 변경했다면, 화면 전환이 완료 되고 나서야 내용을 적용 시킨다..
            if (getOrientationChecker().isChangedPhoto()
                    && getOrientationChecker().isChangedOrientationAtImgEditor()) {
                getOrientationChecker().setChangedPhoto(false);
                getOrientationChecker().setChangedOrientationAtImgEditor(false);
                if (getSnapsHandler() != null)
                    getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_REFRESH_CHANGED_PHOTO, 1000);
            }

            return true;
        }
        return false;
    }

    public boolean checkChangedOrientaion(int orientation) throws Exception {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (SmartSnapsManager.isSupportSmartSnapsProduct() && smartSnapsManager.isScreenRotationLock())
            return false;

        if (!isActivityResumeFinished()) {
            return false;
        }

//        if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
//            orientation = Configuration.ORIENTATION_PORTRAIT;
//        }

        if (OrientationSensorManager.isActiveAutoRotation(getActivity()) == false) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        if (orientation == getOrientationChecker().getPrevOrientation())
            return false;

        getOrientationChecker().setPrevOrientation(orientation);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!getOrientationManager().isLandScapeMode()) {
                getOrientationManager().setLandScapeMode(true);
            }
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (getOrientationManager().isLandScapeMode()) {
                getOrientationManager().setLandScapeMode(false);
            }
        } else {
            return false;
        }

        return true;
    }

    public void handleShowChangePageActcity(boolean prmSideT) throws Exception {
        SnapsOrderManager.cancelCurrentImageUploadExecutor();

        handleDismissPopOvers();

//        Intent intent = new Intent(getActivity(), ThemeDesignListActivity.class);
        Intent intent = new Intent(getActivity(), NewThemeDesignListActivity.class);

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        int currentItemIdx = centerPager.getCurrentItem();
        SnapsPage page = getPageList().get(currentItemIdx);

        int coverWidth = page.getOriginWidth();
        int coverHeight = Integer.parseInt(page.height);
        float ratio = 0.0f;

        if (coverWidth >= coverHeight) {
            ratio = coverWidth / coverHeight;
        } else {
            ratio = coverHeight / coverWidth + 101.0f;
        }

        intent.putExtra("pageRatio", ratio);

        if (prmSideT)
            intent.putExtra("prmSide", "t");

        getActivity().startActivityForResult(intent, REQ_CHANGE_PAGE);
    }

    public void handleShowAddPageActcity() throws Exception {
        handleDismissPopOvers();

        Intent intent = new Intent(getActivity(), NewThemeDesignListActivity.class);

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        int currentItemIdx = centerPager.getCurrentItem();
        SnapsPage page = getPageList().get(currentItemIdx);
        //
        int coverWidth = page.getOriginWidth();
        int coverHeight = Integer.parseInt(page.height);
        float ratio = 0.0f;
        //
        if (coverWidth >= coverHeight) {
            ratio = coverWidth / coverHeight;
        } else {
            ratio = coverHeight / coverWidth + 101.0f;
        }
        //
        intent.putExtra("pageRatio", ratio);

        getActivity().startActivityForResult(intent, REQ_ADD_PAGE);
    }

    public void handleShowAddProductChangePageActcity() throws Exception {
        handleDismissPopOvers();

        Intent intent = new Intent(getActivity(), NewThemeDesignListActivity.class);

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        int currentItemIdx = centerPager.getCurrentItem();
        SnapsPage page = getPageList().get(currentItemIdx);
        //
        int coverWidth = page.getOriginWidth();
        int coverHeight = Integer.parseInt(page.height);
        float ratio = 0.0f;
        //
        if (coverWidth >= coverHeight) {
            ratio = coverWidth / coverHeight;
        } else {
            ratio = coverHeight / coverWidth + 101.0f;
        }
        //
        intent.putExtra("pageRatio", ratio);
        intent.putExtra(Const_EKEY.NEW_YEARS_CARD_MAX_COUNT, 1);
        intent.putExtra(Const_EKEY.NEW_YEARS_CARD_MODE, BaseThemeDesignList.SELECT_MODE.SINGLE_SELECT_CHANGE_DESIGN);
        getActivity().startActivityForResult(intent, REQ_PRODUCT_CHANGE_PAGE);
    }

    public void handleShowAddProductPageActcity(int maxCount) throws Exception {
        handleDismissPopOvers();

        Intent intent = new Intent(getActivity(), NewThemeDesignListActivity.class);

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        int currentItemIdx = centerPager.getCurrentItem();
        SnapsPage page = getPageList().get(currentItemIdx);
        //
        int coverWidth = page.getOriginWidth();
        int coverHeight = Integer.parseInt(page.height);
        float ratio = 0.0f;
        //
        if (coverWidth >= coverHeight) {
            ratio = coverWidth / coverHeight;
        } else {
            ratio = coverHeight / coverWidth + 101.0f;
        }
        //
        intent.putExtra("pageRatio", ratio);
        intent.putExtra(Const_EKEY.NEW_YEARS_CARD_MAX_COUNT, maxCount);
        intent.putExtra(Const_EKEY.NEW_YEARS_CARD_MODE, BaseThemeDesignList.SELECT_MODE.MULTI_SELECT_ADD_DESIGN);
        getActivity().startActivityForResult(intent, REQ_PRODUCT_ADD_PAGE);
    }

    void handleOnClickedImageEdit() throws Exception {
        getOrientationChecker().setCurrentOrientationPrevInEditor();
        getOrientationChecker().setChangedPhoto(false);

        PopoverView popupMenuView = getEditControls().getPopupMenuView();
        if (popupMenuView != null)
            popupMenuView.dissmissPopover(false);
        handleDismissBackgroundToolBox();

        // 커버 이미지 수정..
        Intent intent = new Intent(getActivity(), ImageEditActivity.class);
        ArrayList<MyPhotoSelectImageData> images = getEditorBase().getMyPhotoSelectImageData(isImageEditableOnlyCover());
        PhotobookCommonUtils.setImageDataScaleable(getSnapsTemplate());
        DataTransManager dtMan = DataTransManager.getInstance();
        if (dtMan != null) {
            dtMan.setPhotoImageDataList(images);
        } else {
            DataTransManager.notifyAppFinish(getActivity());
            return;
        }

        int idx = PhotobookCommonUtils.getImageIndex(getActivity(), images, getEditInfo().getTempImageViewID());
        if (idx < 0) return;

        intent.putExtra("dataIndex", idx);
        getActivity().startActivityForResult(intent, REQ_MODIFY);
    }

    void handleOnClickedLayoutControl(Intent intent) {
        if (getEditorBase().isBlockAllOnClickEvent()) return;
        boolean isInsertedImageOnLayoutControl = intent.getBooleanExtra("isEdited", false);
        if (!isInsertedImageOnLayoutControl) {
            getEditorBase().onClickedImageChange();
            return;
        }

        boolean isShowPopup = intent.getBooleanExtra("isShowPopup", true);
        if (isShowPopup) {
            handleShowPopMenuPhotoTooltip(intent);
            handleShowNoPrintToast();
        }
    }

    private boolean isImageEditableOnlyCover() {
        return getEditorBase().isImageEditableOnlyCover();
    }

    void handleOnClickedImageRemove() throws Exception {
        SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), getEditInfo().getTempImageViewID());
        if (!(snapsControl instanceof SnapsLayoutControl)) return;

        PopoverView popupMenuView = getEditControls().getPopupMenuView();
        if (popupMenuView != null)
            popupMenuView.dissmissPopover(false);

        handleDismissBackgroundToolBox();

        SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;
        handleImageRemove(control);
    }

    public void handleImageRemove(SnapsLayoutControl control) throws Exception {
        SnapsOrderManager.removeBackgroundUploadOrgImageData(control.imgData);

        control.imgData = null;
        control.angle = "";
        control.imagePath = "";
        control.imageLoadType = 0;
        control.isNoPrintImage = false;
        control.isUploadFailedOrgImg = false;

        handleNotifyCenterPagerAdapter();

        // 썸네일을 요청한다.
        getEditorBase().offerQueue(control.getPageIndex(), control.getPageIndex());
        getEditorBase().refreshPageThumbnailsAfterDelay();
    }

    void handleOnClickedChangePhoto() throws Exception {
        SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), getEditInfo().getTempImageViewID());
        if (!(snapsControl instanceof SnapsLayoutControl)) {
            return;
        }

        performChangePhoto(snapsControl);
    }

    public void handleChangePhoto(SnapsLayoutControl control) throws Exception {
        performChangePhoto(control);
    }

    private void performChangePhoto(SnapsControl snapsControl) throws Exception {
        Intent broadIntent = new Intent(Const_VALUE.RESET_LAYOUT_ACTION);
        getActivity().sendBroadcast(broadIntent);

        Message msg = new Message();
        msg.what = HANDLER_MSG_CLICKED_LAYOUT_CONTROL;
        msg.obj = snapsControl;
        getSnapsHandler().sendMessage(msg);
    }

    public void handlePerformSelectPhoto() throws Exception {
        SnapsOrderManager.cancelCurrentImageUploadExecutor();

        OrientationManager.fixCurrentOrientation(getActivity());
        handleDismissPopOvers();
        Setting.set(getActivity(), "themekey", "");
        Intent intent = new Intent(getActivity(), ImageSelectActivityV2.class);
        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SINGLE_CHOOSE_TYPE)
                .setOrientationChanged(true).create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, REQ_PHOTO);
    }

    public void handlePerformChangePageContents() throws Exception {
        OrientationManager.fixCurrentOrientation(getActivity());
        handleDismissPopOvers();
        Intent intent = new Intent(getActivity(), ThemeCategoryActivity.class);
        getActivity().startActivityForResult(intent, REQ_CONTENT);
    }

    void handlePerformEditText() throws Exception {
        SnapsEditTextControlHandleData textControlHandleData = new SnapsEditTextControlHandleData.Builder()
                .setActivity(getActivity())
                .setActivityRequestCode(REQ_EDIT_TEXT)
                .setShouldBeBlurBackground(true)
                .setAppliedBlurActivity(isAppliedBlurActivity)
                .setPopoverView(popupMenuView)
                .setRootView(getEditControls().getRootView())
                .setSnapsTemplate(getSnapsTemplate())
                .setTempViewId(getEditInfo().getTempImageViewID())
                .setHandleListener(new ISnapsEditTextControlHandleListener() {
                    @Override
                    public void shouldAppliedBlurFlagToTrue() {
                        isAppliedBlurActivity = true;
                    }

                    @Override
                    public void shouldSetPopupMenuView(PopoverView popoverView) {
                        if (getEditControls() != null)
                            getEditControls().setPopupMenuView(popupMenuView);
                    }
                }).create();

        PhotobookCommonUtils.handlePerformEditText(textControlHandleData);
    }

    public void handlePerformGoToHomeAct() throws Exception {
        handleDismissPopOvers();
        Intent intent = new Intent(getActivity(), RenewalHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("goToCart", true);
        getActivity().startActivity(intent);
        getActivity().finish();

        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan != null) {
            saveMan.finishAutoSaveMode();
        }
    }

    public void handlePerformCopyPage() throws Exception {
        handleDismissPopOvers();

        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        SnapsPage page = getPageList().get(pageClipBoard.getSelectedPageIndex());
        pageClipBoard.copy(page, true);
    }

    public void handlePerformPastePage() throws Exception {
        handleDismissPopOvers();
        if (isOverPageCount()) {
            MessageUtil.toast(getActivity(), R.string.disable_add_page);
            return;
        }

        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//        Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Edit", "Copy"));
        getEditorBase().addPage(pageClipBoard.getSelectedPageIndex() + 1, pageClipBoard.getCopiedPage());
    }

    public void handlepPerformShowDeletePageConfirm() throws Exception {
        handleDismissPopOvers();

        if (getEditorBase().isLackMinPageCount()) {
            int count = getEditorBase().getLimitPageCount();
            getEditorBase().showCannotDeletePageToast(count);
        } else {
            CustomizeDialog confirmDialog = getEditControls().getConfirmDialog();
            if (confirmDialog == null || !confirmDialog.isShowing()) {
                confirmDialog = new CustomizeDialog(getActivity(), getEditorBase().getDeletePageMessage(), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        if (clickedOk == ICustomDialogListener.OK) {
                            getEditorBase().deletePage();
                        }
                    }
                }, null);
                getEditControls().setConfirmDialog(confirmDialog);

                confirmDialog.show();
            }
        }
    }

    public void handlePerformDeleteText() throws Exception {
        OrientationManager.fixCurrentOrientation(getActivity());

        PopoverView popupMenuView = getEditControls().getPopupMenuView();
        if (popupMenuView != null)
            popupMenuView.dissmissPopover(false);

        MessageUtil.alertnoTitle(getActivity(), getActivity().getString(R.string.card_text_delete_confirm), new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (clickedOk == ICustomDialogListener.OK) {
                    SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), getEditInfo().getTempImageViewID());
                    if (snapsControl == null || !(snapsControl instanceof SnapsTextControl))
                        return;

                    SnapsTextControl control = (SnapsTextControl) snapsControl;
                    control.text = "";
                    control.textDrawableWidth = "";
                    control.textDrawableHeight = "";

                    control.setText("");

                    SnapsPagerController2 loadPager = getEditControls().getLoadPager();
                    if (loadPager != null) {
                        loadPager.pageAdapter.notifyDataSetChanged();
                    }

                    getEditorBase().offerQueue(control.getPageIndex(), control.getPageIndex());

                    OrientationManager.fixCurrentOrientation(getActivity());
                    getEditorBase().refreshPageThumbnailsAfterDelay();
                }
            }
        });
    }

    public void handleNotifyCoverLayoutControlFromIntentData(Intent data) throws Exception {
        OrientationManager.fixCurrentOrientation(getActivity());
        Bundle b = data.getExtras();
        // MyPhotoSelectImageData d = b.getParcelable("imgData");
        b.setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
        MyPhotoSelectImageData d = (MyPhotoSelectImageData) b.getSerializable("imgData");

        View view = (View) findViewById(getEditInfo().getTempImageViewID());
        if (view != null) {
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(view);
            if (snapsControl != null && snapsControl instanceof SnapsLayoutControl) {
                SnapsLayoutControl control = (SnapsLayoutControl) snapsControl;
                // String url = "";
                control.type = "browse_file";
                // 기존 사진이 있으면 삭제를 한다.
                if (control.imgData != null) {
                    SnapsOrderManager.removeBackgroundUploadOrgImageData(control.imgData);
                    control.imgData = null;
                }
                d.pageIDX = 0;
                d.IMG_IDX = 1;
                d.cropRatio = control.getRatio();
                control.imgData = d;

                // loadImage(url, imgView, d.KIND, d.ROTATE_ANGLE_THUMB, d.CROP_INFO);
                handleNotifyCenterPagerAdapter();
                // notifyPageData();

                getEditorBase().offerQueue(0, 0);
                getEditorBase().refreshPageThumbnailsAfterDelay();

                uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(d);
            }
        }
    }

    public void handleNotifyCoverTextFromIntentData(Intent data) throws Exception {
        PhotobookCommonUtils.handleNotifyCoverTextFromIntentData(data, getActivity(), getPageList() != null && !getPageList().isEmpty() ? getPageList().get(0) : null);

        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan != null)
            saveMan.exportProjectInfo();

        handleNotifyCenterPagerAdapter();
        // notifyPageData();

        getEditorBase().offerQueue(0, 0);
        getEditorBase().refreshPageThumbnailsAfterDelay();
    }

    final void handleNotExistThumbnailLayout() throws Exception {
        ImageView textModify = getEditControls().getThemeTextModify();
        ImageView coverModify = getEditControls().getThemeCoverModify();

        PhotobookCommonUtils.removeEditButton(textModify, coverModify);

        if (getThumbnailRecyclerView() != null)
            getThumbnailRecyclerView().setVisibility(View.GONE);

        if (findViewById(getOrientationManager().isLandScapeMode() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v) != null) {
            findViewById(getOrientationManager().isLandScapeMode() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v).setVisibility(View.GONE);
        }

        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null)
            previewBtn.setVisibility(View.GONE);
    }

    final void handleOnThumbnailViewClick(View view, int position) throws Exception {
        if (getPageList() != null && getPageList().size() > position) {
            if (getPageList().get(position).isSelected) {
                getEditorBase().showGalleryPopOverView(view, position);
                return;
            }
        }

        setPageCurrentItem(position, false);
    }

    final void handleBaseOnRearrange(final View view, final int oldIndex, final int newIndex) throws Exception {
        if (oldIndex != newIndex) { //위치가 변경 되었다면(swapped)
            refreshOnRearrange(newIndex);

            //드래그 앤 드랍을 하면 저장할 때, 페이지 썸네일을 다시 딴다..
            int startIdx = Math.min(oldIndex, newIndex);
            int endIdx = Math.max(oldIndex, newIndex);
            for (int ii = startIdx; ii < endIdx; ii++) {
                PhotobookCommonUtils.changePageThumbnailState(getPageList(), ii, false);
            }
        } else {
            if (view != null) {
                getEditorBase().showGalleryPopOverView(view, newIndex);
            }
        }
    }

    /**
     * 썸네일 뷰를 드래그 앤 드랍 했을 때, 재 구성을 처리를 한다.
     */
    private void refreshOnRearrange(final int newIndex) throws Exception {
        getEditInfo().setCurrentPageIndex(newIndex);

        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null)
            thumbnailUtil.sortPagesIndex(getActivity(), newIndex);

        handleNotifyThumbnailPagerAdapter();

        SnapsPagerController2 loadPager = getEditControls().getLoadPager();
        if (loadPager != null && loadPager.pageAdapter != null) {
            loadPager.pageAdapter.setData(getPageList());
        }

        setPageCurrentItem(newIndex, false);

        getEditorBase().refreshPagesId(getPageList());

        getEditorBase().exportAutoSaveTemplate();
    }

    final void handleShowBottomThumbnailPopOverView(View offsetView, int position) throws Exception {
        PopoverView menuGalleryView = getEditControls().getPopupMenuGalleryView();
        if (menuGalleryView != null && menuGalleryView.isShown())
            return;

        if (position <= 1 || Config.isNotCoverPhotoBook())
            return; // 커버와 속지는 복사, 삭제가 불가능 하다.

        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        pageClipBoard.setSelectedPageIndex(position);

        final Rect rect = new Rect();

        try {
            if (getOrientationManager().isLandScapeMode()) {
                int[] newInt = new int[2];
                offsetView.getLocationInWindow(newInt);

                int offsetW = UIUtil.convertDPtoPX(getActivity(), 146);
                int padding = UIUtil.convertDPtoPX(getActivity(), 18);

                int imgTop = newInt[1] - padding;

                offsetView.getGlobalVisibleRect(rect);

                int popWidth = UIUtil.convertDPtoPX(getActivity(), 50);
                int popHeight = UIUtil.convertDPtoPX(getActivity(), 37);
                int colums = 3;
                //
                rect.top = imgTop;
                rect.bottom = imgTop + popHeight;

                menuGalleryView = new PopoverView(getActivity(), R.layout.popmenu_photo_left_gallery);

                View convertView = menuGalleryView.getConvertView();
                if (convertView != null) {
                    if (!pageClipBoard.isExistCopiedPage()) {
                        menuGalleryView = new PopoverView(getActivity(), R.layout.popmenu_photo_left_gallery_only_copy);
                        colums = 2;
                        offsetW = UIUtil.convertDPtoPX(getActivity(), 106);
                    }
                }

                getEditControls().setPopupMenuGalleryView(menuGalleryView);

                popWidth *= colums;

                rect.offset(offsetW, 0);

                menuGalleryView.setContentSizeForViewInPopover(new Point(popWidth + UIUtil.convertDPtoPX(getActivity(), 20), popHeight * 1));
                //
                menuGalleryView.showPopoverFromRectInViewGroup(getEditControls().getRootView(), rect, PopoverView.PopoverArrowDirectionUp, true);
            } else {
                offsetView.getGlobalVisibleRect(rect);

                int imgH = UIUtil.convertDPtoPX(getActivity(), Const_VALUE.PHOTO_BOOK_GALLERY_HORIZONTAL_VIEW_HEIGHT - 5);
                rect.offset(0, -imgH);

                int popWidth = UIUtil.convertDPtoPX(getActivity(), 50);
                int popHeight = UIUtil.convertDPtoPX(getActivity(), 37);
                int colums = 3;

                int layoutId = R.layout.popmenu_photo_bottom_gallery;
                if (!pageClipBoard.isExistCopiedPage()) {
                    layoutId = R.layout.popmenu_photo_bottom_gallery_only_copy;
                    colums = 2;
                }

                popWidth *= colums;
                menuGalleryView = new PopoverView(getActivity(), layoutId);
                getEditControls().setPopupMenuGalleryView(menuGalleryView);

                menuGalleryView.setContentSizeForViewInPopover(new Point(popWidth, popHeight * 1));

                // 화살표가 유동적으로 움직이게 하기 위한 처리.
                int screenWidth = UIUtil.getScreenWidth(getActivity());
                int dragViewCenterX = screenWidth - (rect.left + ((rect.right - rect.left) / 2));
                float fCoordnateRatio = dragViewCenterX / (float) screenWidth;

                View convertView = menuGalleryView.getConvertView();
                ImageView ivArrow = (ImageView) convertView.findViewById(R.id.pop_menu_uparrow);

                int xCoordinate = (int) (popWidth * .5f);
                if (rect.left < 5) {
                    xCoordinate = (int) (popWidth * .22f);
                } else if (rect.left < 25) {
                    xCoordinate = (int) (popWidth * .25f);
                } else if (rect.left < 70) {
                    xCoordinate = (int) (popWidth * .35f);
                } else if (rect.left < 90) {
                    xCoordinate = (int) (popWidth * .4f);
                } else if (rect.left < 160) {
                    xCoordinate = (int) (popWidth * .45f);
                } else if (fCoordnateRatio < .21f) {
                    xCoordinate = (int) (popWidth * .66f);
                } else if (fCoordnateRatio < .23f) {
                    xCoordinate = (int) (popWidth * .65f);
                } else if (fCoordnateRatio < .3f)
                    xCoordinate = (int) (popWidth * .55f);

                ivArrow.setX(xCoordinate - UIUtil.convertDPtoPX(getActivity(), 3));

                menuGalleryView.showPopoverFromRectInViewGroup(getEditControls().getRootView(), rect, PopoverView.PopoverArrowDirectionUp, true);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void handleShowGalleryPopOverView(final View view, final int position) throws Exception {
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null && centerPager.getCurrentItem() != position) {
            centerPager.setCurrentItem(position);
        } else {
            if (getSnapsHandler() != null) {
                Message msg = new Message();
                msg.what = HANDLER_MSG_SHOW_POPOVER_VIEW;
                msg.arg1 = position;
                msg.obj = view;
                if (getSnapsHandler() != null)
                    getSnapsHandler().sendMessageDelayed(msg, 200);
            }
        }
    }

    public void handleAddPage(String xmlPath) throws Exception {

        final String url = SnapsAPI.DOMAIN(false) + xmlPath;

        ATask.executeVoid(new ATask.OnTask() {

            SnapsPage newPage;

            int curIdx = 0;
            int index = 0;

            @Override
            public void onPre() {
                showPageProgress();

                InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
                curIdx = centerPager.getCurrentItem();

                if (Const_PRODUCT.isStikerGroupProduct() || Const_PRODUCT.isPosterGroupProduct()) {
                    index = curIdx + 1;
                } else {
                    if (curIdx > 1)
                        index = curIdx + 1;
                    else
                        index = 2;
                }
            }

            @Override
            public void onPost() {
                dismissPageProgress();

                if (newPage != null)
                    getEditorBase().addPage(index, newPage);
                else {
                    MessageUtil.toast(getActivity(), getActivity().getString(R.string.page_add_error_msg));//"페이지 추가 중 오류가 발생했습니다.");
                }
            }

            @Override
            public void onBG() {
                SnapsTemplate template = GetTemplateLoad.getThemeBookTemplate(url, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                if (template != null && template.getPages().size() > 0) {
                    newPage = template.getPages().get(0);

                    if (newPage != null) {
                        Collections.sort(newPage.getLayoutList(), myComparator);
                        Collections.reverse(newPage.getLayoutList());
                    }
                }
                PhotobookCommonUtils.saveMaskData(template);
            }
        });
    }


    public void handleAddPage(int index) {
        int pageIDX = index + 1;

        // 페이지 추가
        SnapsPage page = createSnapsPage(pageIDX);
        if (page == null) {
            MessageUtil.toast(getActivity(), getActivity().getString(R.string.page_add_error_msg));//"페이지 추가 중 오류가 발생했습니다.");
            return;
        }
        // 페이지 추가
        getEditorBase().addPage(pageIDX, page);
    }

    public SnapsPage createSnapsPage(int insertPageIDX) {
        // 복사할 인덱스가 전체 페이지 인덱스를 넘어갈경우 다시 2페이지부터 복사하는걸로 한다.
        if (getSnapsTemplate().clonePageList.size() - 1 < getEditInfo().getPageAddIndex()) {
            getEditInfo().setPageAddIndex(2);
        }

        SnapsPage page = getSnapsTemplate().getPageLayoutIDX(getEditInfo().getPageAddIndex());
        if (page == null) {
            return null;
        }

        SnapsPage addPage = page.copyPage(insertPageIDX);

        getEditInfo().increasePageAddIndex();

        return addPage;
    }

    public boolean handleAddPage(int pageIDX, SnapsPage... pages) {
        if (isOverPageCount()) {
            showPageOverCountToastMessage();
            return false;
        }

        if (pages != null) {
            int startIndex = pageIDX;
            for (SnapsPage page : pages) {
                getPageList().add(startIndex++, page);
            }
        }

        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.sortPagesIndex(getActivity(), getCurrentPageIndex());
        }

        // maxpage 설정...
        getSnapsTemplate().setApplyMaxPage();
//		exportAutoSaveTemplate(); //TODO  느리다.

        if ("soft".equalsIgnoreCase(getSnapsTemplate().info.F_COVER_TYPE)
                && getSnapsTemplate().info.getSoftCoverAddSpineText() != -1 && getPageList().size() == getSnapsTemplate().info.getSoftCoverAddSpineText() + 2) {
            getEditorBase().showAddStickToastMsg();
        }

        getEditorBase().refreshList(pageIDX, getPageList().size() - 1);
        if (Config.isPhotobooks()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int topMargin = 0;
                    if (getOrientationManager().isLandScapeMode()) {
                        topMargin = 0;
                    } else {
                        topMargin = -26;
                    }

                    BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
                    View selectView = ((EditActivityThumbnailAdapter) thumbnailAdapter).getSelectLayout();
                    if (selectView != null) {
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                                .setText(getActivity().getString(R.string.tutorial_drag_want_position))
                                .setTargetView(selectView)
                                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                                .create());
                    }
                }
            }, 500);
        }
        return true;
    }

    public void handleDeletePage() {
        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        getEditorBase().deletePage(pageClipBoard.getSelectedPageIndex());

        pageClipBoard.deleteClipBoardPage();
        if ("soft".equalsIgnoreCase(getSnapsTemplate().info.F_COVER_TYPE) && getSnapsTemplate().info.getSoftCoverAddSpineText() != -1
                && getPageList().size() == getSnapsTemplate().info.getSoftCoverAddSpineText() + 1) {
            getEditorBase().showCoverSpineDeletedToastMsg();
        }
    }

    public void handleDeletePage(final int index) {
        if (getPageList() == null || getPageList().size() <= index)
            return;

        // 삭제전에 이미지 데이터가 있으면 삭제하기..
        SnapsPage page = getPageList().get(index);

        SnapsOrderManager.removeBackgroundUploadOrgImagesInPage(page);

        // 페이지 삭제
        getPageList().remove(page);

        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.sortPagesIndex(getActivity(), getCurrentPageIndex());
        }

        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter != null)
            thumbnailAdapter.notifyItemRemoved(index);

        // maxpage 설정...
        getSnapsTemplate().setApplyMaxPage();
        getEditorBase().exportAutoSaveTemplate();

        handleChangeRotatedLayout();

        getEditorBase().refreshList(index, getPageList().size() - 1);
    }

    public void handleThumbnailSelectionDragView(int pageChangeType, int page) {
        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.setSelectionDragView(pageChangeType, page);
        }
    }

    public void handleChangePage(String xmlPath) throws Exception {

        final String url = SnapsAPI.DOMAIN(false) + xmlPath;

        ATask.executeVoid(new ATask.OnTask() {

            SnapsPage newPage;

            int curIdx = 0;

            // int index = 0;

            @Override
            public void onPre() {
                showPageProgress();

                InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
                curIdx = centerPager.getCurrentItem();
            }

            @Override
            public void onPost() {
                dismissPageProgress();

                if (newPage != null) {
                    try {
                        changePage(curIdx, newPage);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        SnapsAssert.assertException(e);
                    }

                    //ben 아코디언 카드 디자인 변경시 QR 코드 사라지는 버그 임시 땜방
                    //
                    //상품명: 아코디언카드
                    //뒷면 맨 마지막장 하단에 QR코드가 출력되는데, 편집화면 처음 진입 시에는 QR코드가 노출되지만 디자인을 변경한 후에는 사라집니다.
                    //PC에서는 디자인 변경 후에도 QR코드를 확인할 수 있습니다.
                    //실제 제작 시에는 QR코드가 인쇄되기 때문에, 클레임이 인입될 우려가 있어 확인 후 수정 부탁드립니다.
                    //
//                    if (Const_PRODUCT.isAccordionCardProduct()) {
//                        getSnapsTemplate().addQRcode(getEditorBase().getQRCodeRect());
//                    }

                } else {
                    MessageUtil.toast(getActivity(), getActivity().getString(R.string.page_add_error_msg));//
                }
            }

            @Override
            public void onBG() {
                SnapsTemplate template = GetTemplateLoad.getThemeBookTemplate(url, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                if (template != null && template.getPages().size() > 0) {
                    PhotobookCommonUtils.saveMaskData(template);
                    newPage = template.getPages().get(0);
                }
            }
        });
    }

    //TODO ... 리펙토링 필요..막 코딩이 너무 많다..
    public void changePage(final int index, SnapsPage newPage) throws Exception {
        if (newPage == null)
            return;

        int imgCnt = newPage.getLayoutList().size();
        int idx = 0;

        SnapsPage oldPage = getPageList().get(index);
        newPage.info = oldPage.info;
        newPage.setPageID(oldPage.getPageID());
        newPage.setQuantity(oldPage.getQuantity());

        for (SnapsControl control : newPage.getLayoutList()) {
//			if (control instanceof SnapsLayoutControl) {
//				SnapsLayoutControl newCt = (SnapsLayoutControl) control;
//				newCt.setPageIndex(oldPage.getPageID());
//				newCt.setControlId(-1);
//			}
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        for (SnapsControl control : newPage.getBgList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        for (SnapsControl control : newPage.getControlList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }
        //KT 북 - 텍스트 이미지 복사.. (맞나???)
        if (Config.isKTBook()) {
            int textControlIndex = 0;
            ArrayList<SnapsControl> newSnapsControlList = newPage.getTextControlList();
            for (SnapsControl oldControl : oldPage.getTextControlList()) {
                SnapsTextControl oldTextControl = (SnapsTextControl) oldControl;
                SnapsTextControl newTextControl = (SnapsTextControl) newSnapsControlList.get(textControlIndex);

                newTextControl.text = oldTextControl.text;
                newTextControl.isEditedText = oldTextControl.isEditedText;
                newTextControl.format.fontColor = oldTextControl.format.fontColor;
                newTextControl.format.baseFontColor = oldTextControl.format.fontColor;
                newTextControl.format.align = oldTextControl.format.align;
                newTextControl.format.bold = oldTextControl.format.bold;
                newTextControl.format.italic = oldTextControl.format.italic;
                newTextControl.format.underline = oldTextControl.format.underline;
                textControlIndex++;
            }
        }

        if (Config.isKTBook()) {

        }

        for (SnapsControl control : newPage.getFormList()) {
            control.setPageIndex(oldPage.getPageID());
            control.setControlId(-1);
        }

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        final List<MyPhotoSelectImageData> smartSnapsImageList = smartSnapsManager.createSmartSnapsImageListWithPageIdx(index);

        for (SnapsControl control : oldPage.getLayoutList()) {
            if (control instanceof SnapsLayoutControl) {
                SnapsLayoutControl oldControl = (SnapsLayoutControl) control;
                if (oldControl.imgData != null && oldControl.type.equalsIgnoreCase("browse_file")) {
                    if (idx < imgCnt) {

                        SnapsLayoutControl newControl = ((SnapsLayoutControl) newPage.getLayoutList().get(idx));
                        newControl.setControlId(-1);

                        if (oldControl.imgData.ORIGINAL_ROTATE_ANGLE != INVALID_ROTATE_ANGLE)
                            oldControl.imgData.ROTATE_ANGLE = oldControl.imgData.ORIGINAL_ROTATE_ANGLE;

                        //효과 필터가 적용 된 사진은 회전 정보가 반영 되어 있기 때문에 원래 각도로 복구해서 로딩한다.
                        if (oldControl.imgData.isApplyEffect
                                && (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != oldControl.imgData.ROTATE_ANGLE_THUMB || oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE == INVALID_ROTATE_ANGLE)) {
                            try {
                                if (!ImageFilters.updateEffectImageToOrgAngle(getActivity(), oldControl.imgData)) {
                                    oldControl.imgData.isApplyEffect = false;
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                oldControl.imgData.isApplyEffect = false;
                            }
                        }

                        if (oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) //만약 ratio 오류같은 게 발생한다면 이부분을 의심해보자
                            oldControl.imgData.ROTATE_ANGLE_THUMB = oldControl.imgData.ORIGINAL_THUMB_ROTATE_ANGLE;

                        newControl.imgData = oldControl.imgData;

                        newControl.imgData.FREE_ANGLE = 0;
                        newControl.imgData.RESTORE_ANGLE = SnapsImageDownloader.INVALID_ANGLE;
                        newControl.imgData.isAdjustableCropMode = false;
                        newControl.imgData.ADJ_CROP_INFO = new AdjustableCropInfo();

                        newControl.imgData.IMG_IDX = Integer.parseInt(0 + "" + newPage.getLayoutList().get(idx).regValue);
                        newControl.freeAngle = 0;// oldControl.imgData.FREE_ANGLE;
                        newControl.angle = String.valueOf(oldControl.imgData.ROTATE_ANGLE);
                        newControl.imagePath = oldControl.imgData.PATH;
                        newControl.imageLoadType = oldControl.imgData.KIND;
                        newControl.imgData.cropRatio = newControl.getRatio();
                        newControl.imgData.increaseUploadPriority();

                        // 인쇄가능 여부..
                        try {
                            getEditorBase().setPhotoResolutionEnableWithLayoutControl(newControl);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }

                        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                            MyPhotoSelectImageData imageData = newControl.imgData;
                            if (imageData != null && imageData.isSmartSnapsSupport()) {
                                SmartSnapsUtil.setSmartImgDataStateReadyOnChangeLayout(imageData, index);
                                if (smartSnapsImageList != null)
                                    smartSnapsImageList.add(imageData);
                            }
                        }
                    }

                    SnapsOrderManager.removeBackgroundUploadOrgImageData(oldControl.imgData);
                    // 커버 이미지 삭제..
                    oldControl.imgData = null;
                    idx++;
                }
            }
        }
        if (Const_PRODUCT.isBabyNameStikerGroupProduct()) {
            setChangeNameStickerSet(newPage, oldPage);
        }
        // 페이지 교체...
        getPageList().remove(index);

        getPageList().add(index, newPage);

//        getEditorBase().exportAutoSaveTemplate();

        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
            SmartSnapsManager.setSmartAreaSearching(true);
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getEditorBase().refreshList(index, index);

                dismissPageProgress();

                if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SmartSnapsUtil.refreshSmartSnapsImgInfoOnNewLayoutWithImgList(getActivity(), getSnapsTemplate(), smartSnapsImageList, index);
                                SmartSnapsManager.startSmartSnapsAutoFitImage(getEditorBase().getDefaultSmartSnapsAnimationListener(), SmartSnapsConstants.eSmartSnapsProgressType.CHANGE_DESIGN, index);
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                SnapsAssert.assertException(e);
                            }
                        }
                    }, 800); //SnapsPageCanvas가 갱신 되게 하기 위해 조금 기다린다

                    uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(null);
                }
            }
        }, 50);
    }

    private void setChangeNameStickerSet(SnapsPage newPage, SnapsPage oldPage) {
        for (SnapsControl bg : newPage.getBgList()) {
            bg.isClick = "true";
        }
        int count = setChangeCount(oldPage);
        for (int i = 0; i < newPage.getLayerControls().size(); i++) {

            SnapsControl control = newPage.getLayerControls().get(i);
            if (control instanceof SnapsTextControl) {
                SnapsTextControl newControl = (SnapsTextControl) control;
                SnapsTextControl oldControl = ((SnapsTextControl) oldPage.getLayerControls().get(i));
                newControl.text = oldControl.text;
                newControl.isEditedText = true;
            }
            if (count == i) break;
            ;
        }
        int width = Integer.parseInt(newPage.width);
        int height = Integer.parseInt(newPage.height);
        SnapsControl cellControl = newPage.getLayerControls().get(0);
        int size = newPage.getLayerControls().size();
        for (int i = 1; i < size; i++) {
            SnapsControl control = newPage.getLayerControls().get(i);
            if (control instanceof SnapsTextControl) {
                SnapsTextControl textControl = ((SnapsTextControl) control).copyControl();
                textControl.isEditedText = true;
                cellControl.getIntWidth();
                int cellWidth = cellControl.getIntWidth();
                int cellHeight = cellControl.getIntHeight();
                int moveX = control.getIntX() - cellControl.getIntX();
                int moveY = control.getIntY() - cellControl.getIntY();
                int originalX = cellControl.getIntX();
                int originalY = cellControl.getIntY();
                int x = originalX;
                int y = originalY;

                while (true) {
                    SnapsTextControl addControl = ((SnapsTextControl) control).copyControl();
                    addControl.isEditedText = true;
                    if ((x + (cellWidth * 2)) < width) {
                        x += cellWidth;
                        addControl.setX((x + moveX) + "");
                        addControl.setY((y + moveY) + "");
                        newPage.getLayerControls().add(addControl);
                    } else if ((x + (cellWidth * 2)) > width && (y + (cellHeight * 2)) < (height - 45)) {
                        x = originalX;
                        y += cellHeight;
                        addControl.setX((x + moveX) + "");
                        addControl.setY((y + moveY) + "");
                        newPage.getLayerControls().add(addControl);
                    } else {
                        break;
                    }
                }
            }
        }

    }

    private int setChangeCount(SnapsPage snapsPage) {
        int count = 0;
        SnapsControl cellControl = snapsPage.getLayerControls().get(0);
        int x = cellControl.getIntX();
        int y = cellControl.getIntY();
        int width = cellControl.getIntWidth();
        int height = cellControl.getIntHeight();
        Rect rect = new Rect();
        rect.set(x, y, x + width, y + height);
        int size = snapsPage.getLayerControls().size();
        ArrayList<SnapsControl> list = new ArrayList<SnapsControl>();
        list.add(cellControl);
        for (int i = 1; i < size; i++) {
            SnapsControl control = snapsPage.getLayerControls().get(i);
            if (control instanceof SnapsTextControl) {
                SnapsTextControl textControl = (SnapsTextControl) control;
                Rect textRect = new Rect();
                textRect.set(textControl.getIntX(), textControl.getIntY(), textControl.getIntX() + textControl.getIntWidth(), textControl.getIntY() + textControl.getIntHeight());
                if (rect.contains(textRect)) {
                    count++;
                }
            }
        }
        return count;
    }

    void handleRequestPagerFocusLastEditedPageIdx() {
        try {
            final int LAST_EDITED_PAGE_IDX = getEditorBase().getLastEditPageIndex();
            if (getThumbnailRecyclerView() != null) {
                getThumbnailRecyclerView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPageCurrentItem(LAST_EDITED_PAGE_IDX, false);
                        getEditorBase().setThumbnailSelectionDragView(EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE, LAST_EDITED_PAGE_IDX);
                    }
                }, 500);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void handleRefreshChangePhoto() throws Exception {
        ArrayList<MyPhotoSelectImageData> imgList = DataTransManager.getImageDataFromDataTransManager(getActivity());
        if (imgList == null || imgList.isEmpty()) return;

        ArrayList<Integer> changeList = PhotobookCommonUtils.getChangedPhotoPageIndexWithImageList(imgList, getSnapsTemplate());
        handleRefreshImageWithPageIndexList(changeList);
    }

    final void handleRefreshImageWithPageIndexList(ArrayList<Integer> changeList) throws Exception {
        PhotobookCommonUtils.imageResolutionCheck(getSnapsTemplate());

        handleNotifyCenterPagerAdapter();

        Integer idx = 0;

        // 같은페이지를 동시에 수정했을때는 썸네일을 한번만 만들면 되기때문에 중복 제거...
        HashSet<Integer> set = new HashSet<Integer>(changeList);
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            idx = it.next();
            getEditorBase().offerQueue(idx, idx);
        }

        // 포토북 외의 상품은 커버 개념이 아니기 때문에 썸네일을 다시 만들어 준다.
        if (Const_PRODUCT.isSinglePageProduct() && !Config.isNotCoverPhotoBook())
            getEditorBase().offerQueue(0, 0);

        OrientationManager.fixCurrentOrientation(getActivity());
        getEditorBase().refreshPageThumbnailsAfterDelay();

        getOrientationChecker().setChangedOrientationAtImgEditor(false);
        getOrientationChecker().setChangedPhoto(false);
    }

    public void handleShowNoPrintToast() {
        try {
            SnapsLayoutControl pressedControl = findPressedLayoutControl();

            //원본 이미지 업로드 실패 안내가 우선 순위가 높다
            if (showUploadFailedOrgImgWithLayoutControl(pressedControl)) return;

            if (pressedControl == null || !pressedControl.isNoPrintImage) return;

            if (isExistThumbnailArea()) {
                Point point = getOrientationManager().isLandScapeMode() ? getEditorBase().getNoPrintToastOffsetForScreenLandscape() : getEditorBase().getNoPrintToastOffsetForScreenPortrait();
                if (point != null) MessageUtil.noPrintToast(getActivity(), point.x, point.y);
            } else {
                MessageUtil.noPrintToast(getActivity(), ResolutionConstants.NO_PRINT_TOAST_OFFSETX_BASIC, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC, Const_PRODUCT.isFrameProduct());
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean showUploadFailedOrgImgWithLayoutControl(SnapsLayoutControl pressedControl) {
        if (pressedControl != null) {
            if (pressedControl.isUploadFailedOrgImg) {
                MessageUtil.toast(getActivity(), R.string.select_upload_failed_org_img_msg, Gravity.CENTER);
                return true;
            }
        }
        return false;
    }

    private boolean isExistThumbnailArea() {
        View thumbnailLayout = getCurrentThumbnailContainerView();
        return thumbnailLayout != null && thumbnailLayout.isShown();
    }

    private View getCurrentThumbnailContainerView() {
        int thumbnailAreaLayoutId = getOrientationManager().isLandScapeMode() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v;
        return findViewById(thumbnailAreaLayoutId);
    }

    private SnapsLayoutControl findPressedLayoutControl() {
        // 일단 팝업을 띄우고, 컨트롤 아이디 임시 저장을 한다. 그럼 끝..
        ImageView imgView = (ImageView) findViewById(getEditInfo().getTempImageViewID());
        if (imgView != null) {
            SnapsControl control = PhotobookCommonUtils.getSnapsControlFromView(imgView);
            if (control != null && control instanceof SnapsLayoutControl) {
                SnapsLayoutControl press_control = (SnapsLayoutControl) control;
                if (press_control.imgData != null) {
                    return press_control;
                }
            }
        }
        return null;
    }

    void handleShowPopMenuPhotoTooltip(Intent intent) {
        if (popupMenuView != null && popupMenuView.isShown()) return;

        Rect rect = new Rect();
        View rootView = getEditControls().getRootView();
        if (rootView == null) return;

        View popupView = rootView.findViewById(getEditInfo().getTempImageViewID());
        if (popupView != null)
            popupView.getGlobalVisibleRect(rect);

        int popWidth = UIUtil.convertDPtoPX(getActivity(), 150);
        int popHeight = UIUtil.convertDPtoPX(getActivity(), 37);

        int tooltipLayoutResId = getEditorBase().getPopMenuPhotoTooltipLayoutResId(intent);
        popupMenuView = new PopoverView(getActivity(), tooltipLayoutResId);
        getEditControls().setPopupMenuView(popupMenuView);

        popupMenuView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));
        DataTransManager transMan = DataTransManager.getInstance();
        if (transMan != null) {
            ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
            if (coordInfo != null) {
                coordInfo.convertPopupOverRect(rect, popupView, getEditControls().getRootView(), getOrientationManager().isLandScapeMode());

                if (!getOrientationManager().isLandScapeMode()) {
                    rect.top += UIUtil.convertDPtoPX(getActivity(), 20);
                    rect.bottom += UIUtil.convertDPtoPX(getActivity(), 20);
                }

                popupMenuView.setArrowPosition(rect, coordInfo.getTranslateX(), coordInfo.getScaleFactor(), coordInfo.getDefualtScaleFactor(), getOrientationManager().isLandScapeMode());
            }
        } else {
            DataTransManager.notifyAppFinish(getActivity());
            return;
        }

        popupMenuView.showPopoverFromRectInViewGroup(getEditControls().getRootView(), rect, PopoverView.PopoverArrowDirectionUp, true);

        if (Config.useDrawSmartSnapsImageArea()) {
            SnapsLayoutControl pressedControl = findPressedLayoutControl();
            if (pressedControl != null && pressedControl.imgData != null && pressedControl.imgData.getSmartSnapsImgInfo() != null
                    && !StringUtil.isEmpty(pressedControl.imgData.getSmartSnapsImgInfo().getSearchFaceFailedMsg())) {
                MessageUtil.toast(getActivity(), pressedControl.imgData.getSmartSnapsImgInfo().getSearchFaceFailedMsg());
            }
        }
    }

    void handleOnClickedTextControl(SnapsProductEditReceiveData editEvent) throws Exception {
        if (editEvent == null) return;

        SnapsEditTextControlHandleData textControlHandleData = new SnapsEditTextControlHandleData.Builder()
                .setActivity(getActivity())
                .setActivityRequestCode(REQ_EDIT_TEXT)
                .setShouldBeBlurBackground(true)
                .setAppliedBlurActivity(isAppliedBlurActivity)
                .setPopoverView(popupMenuView)
                .setRootView(getEditControls().getRootView())
                .setSnapsTemplate(getSnapsTemplate())
                .setTempViewId(getEditInfo().getTempImageViewID())
                .setHandleListener(new ISnapsEditTextControlHandleListener() {
                    @Override
                    public void shouldAppliedBlurFlagToTrue() {
                        isAppliedBlurActivity = true;
                    }

                    @Override
                    public void shouldSetPopupMenuView(PopoverView popoverView) {
                        if (getEditControls() != null) {
                            getEditControls().setPopupMenuView(popoverView);
                        }
                        popupMenuView = popoverView;
                    }
                }).create();

        PhotobookCommonUtils.handleOnClickedTextControl(editEvent, textControlHandleData);
    }

    //3분동안 페이지가 안 넘어가면 노출 됨
    private void handleThreeMinWaitCheckTutorial() {
        SnapsTutorialUtil.checkTimeThreeMinDelay(new SnapsTutorialUtil.OnThreeMinListener() {
            @Override
            public void threeMin() {
                try {
                    int topMargin = 0;
                    if (getOrientationManager().isLandScapeMode()) {
                        topMargin = 0;
                    } else {
                        topMargin = -24;
                    }

                    TextView cartBtn = getEditControls().getCartTxt();
                    if (cartBtn != null) {
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                                .setText(getActivity().getString(R.string.tutorial_cart_save_after_edit))
                                .setTargetView(cartBtn)
                                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                                .create());
                    }

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    void handleShowEditActivityTutorial() {
        handleThreeMinWaitCheckTutorial();
    }

    private void handleCoverDescTutorial() {
        int topMargin = 0;
        if (getOrientationManager().isLandScapeMode()) {
            topMargin = 0;
        } else {
            topMargin = -24;
        }

        ImageView coverModify = getEditControls().getThemeCoverModify();
        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                .setText(getActivity().getString(R.string.tutorial_change_cover_and_title))
                .setTargetView(coverModify)
                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                .create());
    }

    private void handleFullScreenTutorial() {
        int topMargin = 0;
        if (getOrientationManager().isLandScapeMode()) {
            topMargin = 0;
        } else {
            topMargin = -24;
        }

        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                    .setText(getActivity().getString(R.string.tutorial_full_screen_preview))
                    .setTargetView(previewBtn)
                    .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                    .create());
        }
    }

    private boolean isShownCoverModifyBtn() {
        ImageView coverModify = getEditControls().getThemeCoverModify();
        return coverModify.isShown();
    }

    void handlePinchZoomTutorialOnClose() {
        if (isShownCoverModifyBtn()) {
            handleCoverDescTutorial();
        } else {
            handleFullScreenTutorial();
        }

        showCoverImageTutorialIfExistEmptyCoverLayer();
        try {
            handleShowTextControlTutorial();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }


    private void showCoverImageTutorialIfExistEmptyCoverLayer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (++tryCountOfFindEmptyCoverImage >= 5) {
                        tryCountOfFindEmptyCoverImage = 0;
                        cancel();
                        return;
                    }

                    if (findEmptyCoverImage()) {
                        tryCountOfFindEmptyCoverImage = 0;
                        cancel();
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }, 500, 500);
    }

    private boolean findEmptyCoverImage() {
        if (getSnapsTemplate() == null || getSnapsTemplate().getPages() == null) return false;
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null && centerPager.getCurrentItem() == 0) {
            String msg = null;
            View targetView = null;
            SnapsLayoutControl snapsLayoutControl = PhotobookCommonUtils.findEmptyCoverLayoutControlWithPageList(getSnapsTemplate().getPages());
            if (snapsLayoutControl != null) {
                if (snapsLayoutControl.imgData == null) {
                    msg = getActivity().getString(R.string.tutorial_touch_this);
                } else {
                    msg = getActivity().getString(R.string.tutorial_touch_edit_exclamation_mark);
                }

                RelativeLayout rootView = getEditControls().getRootView();
                if (rootView != null) {
                    targetView = (View) rootView.findViewById(snapsLayoutControl.getControlId());
                    if (targetView != null) {
                        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(getOrientationManager().isLandScapeMode()
                                ? SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM_LANDSCAPE_WITH_THUMNAILBAR : SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM)
                                .setText(msg)
                                .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_PHOTOBOOK_FIND_COVER)
                                .setTargetView(targetView)
                                .create());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void tryFindTextControl() {
        if (++tryCountOfFindTextControl >= 5) {
            tryCountOfFindTextControl = 0;
            return;
        }
        getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_TRY_SHOW_TEXT_EDIT_TUTORIAL, 500);
    }

    void handleShowTextControlTutorial() throws Exception {
        SnapsTextToImageView textToImageView = findTextToImageViewOnCurrentPage();
        if (textToImageView == null) {
            tryFindTextControl();
            return;
        }

        if (textToImageView.isWrittenText()) {
            Intent intent = new Intent(Const_VALUE.CLICK_LAYOUT_ACTION);
            intent.putExtra("control_id", textToImageView.getId());
            intent.putExtra("isEdit", false);

            getActivity().sendBroadcast(intent);
        }
    }

    private SnapsTextToImageView findTextToImageViewOnCurrentPage() {
        if (getSnapsTemplate() == null || getSnapsTemplate().getPages() == null) return null;
        int currentPage = getCurrentPageIndex();
        ArrayList<SnapsPage> pages = getPageList();
        if (pages == null || pages.size() <= currentPage) return null;

        SnapsPage snapsPage = pages.get(currentPage);
        SnapsTextControl textControl = PhotobookCommonUtils.findWrittenTextControlWithSnapsPage(snapsPage);
        if (textControl == null) return null;

        RelativeLayout rootView = getEditControls().getRootView();
        if (rootView != null) {
            View targetView = rootView.findViewById(textControl.getControlId());
            if (targetView != null && targetView instanceof SnapsTextToImageView)
                return (SnapsTextToImageView) targetView;
        }
        return null;
    }

    public void handleFindEmptyImage() {
        if (getSnapsTemplate() == null || getSnapsTemplate().getPages() == null) return;
        SnapsLayoutControl snapsLayoutControl = PhotobookCommonUtils.findEmptyLayoutControlWithPageList(getSnapsTemplate().getPages());
        if (snapsLayoutControl != null) {
            int pageIdx = snapsLayoutControl.getPageIndex();
            if (pageIdx >= 0) {
                setPageCurrentItem(pageIdx, false);

                Message msg = new Message();
                msg.what = HANDLER_MSG_SHOW_IMG_EMPTY_LAYOUT_CONTROL_TOOLTIP;
                msg.arg1 = snapsLayoutControl.getControlId();
                getSnapsHandler().sendMessageDelayed(msg, VIEW_PAGER_MOVING_TIME);
            }
        }
    }

    public void handleFindLowResolutionImage() {
        if (getSnapsTemplate() == null || getSnapsTemplate().getPages() == null) return;
        SnapsLayoutControl snapsLayoutControl = PhotobookCommonUtils.findContainLowResolutionLayoutControlWithPageList(getSnapsTemplate().getPages());
        if (snapsLayoutControl != null) {
            int pageIdx = snapsLayoutControl.getPageIndex();
            if (pageIdx >= 0) {
                setPageCurrentItem(pageIdx, false);

                Message msg = new Message();
                msg.what = HANDLER_MSG_SHOW_IMG_EMPTY_LAYOUT_CONTROL_TOOLTIP;
                msg.arg1 = snapsLayoutControl.getControlId();
                getSnapsHandler().sendMessageDelayed(msg, VIEW_PAGER_MOVING_TIME);
            }
        }
    }

    void handleShowImageEmptyLayoutControlTooltip(View view) {
        if (view == null) return;
        int[] newInt = new int[2];
        view.getLocationInWindow(newInt);
        Dlog.d("handleShowImageEmptyLayoutControlTooltip() newInt[0]:" + newInt[0] + ", newInt[1]:" + newInt[1]);
        //TODO  여기서 띄우면 됨
        SnapsTutorialUtil.showTooltipAlways(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(getOrientationManager().isLandScapeMode() ? SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM_LANDSCAPE_WITH_THUMNAILBAR : SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM)
                .setText(getActivity().getString(R.string.tutorial_touch_this))
                .setTargetView(view)
                .create());
    }

    void handleNotifyCenterPagerAdapter() throws Exception {
        SnapsPagerController2 loadPager = getEditControls().getLoadPager();
        if (loadPager != null && loadPager.pageAdapter != null) {
            try {
                loadPager.pageAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private void handleNotifyThumbnailPagerAdapter() throws Exception {
        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter != null) {
            try {
                thumbnailAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    void handleSelectUploadErrorImgInPager(List<MyPhotoSelectImageData> uploadFailedImageList) throws Exception {
        try {
            SnapsPagerController2 loadPager = getEditControls().getLoadPager();
            if (loadPager != null && loadPager.pageAdapter != null) {
                MyPhotoSelectImageData uploadErrImgData = PhotobookCommonUtils.findFirstIndexOfUploadFailedOrgImageOnList(uploadFailedImageList);
                int pageIndex = PhotobookCommonUtils.findImageDataIndexOnPageList(getPageList(), uploadErrImgData);
                if (pageIndex > 0)
                    loadPager.setPagerCurrentItem(pageIndex);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    void handleNotifyOrientationState() throws Exception {
        getOrientationManager().setBlockRotate(false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        getOrientationManager().setEnableOrientationSensor(true);

        int curOrientation = getActivity().getResources().getConfiguration().orientation;
        handleChangeRotatedLayout(curOrientation);
    }

    void handleNotifyPortraitOrientation() throws Exception {
        getOrientationManager().setBlockRotate(false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getOrientationManager().setEnableOrientationSensor(true);

        getOrientationManager().setLandScapeMode(false);

        try {
            UIUtil.updateFullscreenStatus(getActivity(), false);

            SnapsTutorialUtil.clearTooltip();
            handleInitLayout();

            handleRecoveryLayout();

            getEditorBase().handleScreenRotatedHook();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    void handleRefreshList(final int startPageIDX, final int endPageIdx) throws Exception {
        PhotobookCommonUtils.refreshPagesId(getPageList());

        if (getThumbnailRecyclerView() != null) {
            getThumbnailRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    getEditorBase().handleAfterRefreshList(startPageIDX, endPageIdx);
                }
            });
        }

        // 화면 리프레쉬..

        SnapsPagerController2 loadPager = getEditControls().getLoadPager();
        if (loadPager != null)
            loadPager.pageAdapter.setData(getPageList());
    }

    void handleRefreshSelectedNewImageData(MyPhotoSelectImageData newImageData, SnapsLayoutControl control) throws Exception {
        if (control == null) return;

        SnapsLayoutUpdateInfo layoutUpdateInfo = new SnapsLayoutUpdateInfo.Builder().setSnapsTemplate(getSnapsTemplate()).setLayoutControl(control).setNewImageData(newImageData).setShouldSmartSnapsFitAnimation(true).create();
        PhotobookCommonUtils.replaceNewImageData(layoutUpdateInfo, getEditorBase().getDefaultSmartSnapsAnimationListener());

        handleNotifyCenterPagerAdapter();

        getEditorBase().offerQueue(control.getPageIndex(), control.getPageIndex());
        OrientationManager.fixCurrentOrientation(getActivity());
        getEditorBase().refreshPageThumbnailsAfterDelay();

        getEditorBase().refreshSelectedNewImageDataHook(newImageData);

        uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(newImageData);

        AutoSaveManager autoSaveManager = AutoSaveManager.getInstance();
        if (autoSaveManager != null) {
            autoSaveManager.exportProjectInfo();
        }
    }

    protected void uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(MyPhotoSelectImageData imageData) {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
            SnapsOrderManager.uploadOrgImgOnBackground();
            return;
        }

        SnapsOrderManager.cancelCurrentImageUploadExecutor();

        Message msg = new Message();
        msg.what = HANDLER_MSG_UPLOAD_THUMB_IMAGES;
        msg.obj = imageData;

        if (getSnapsHandler() != null)
            getSnapsHandler().sendMessageDelayed(msg, 500);
    }

    void handleNotifyLayoutControlFromIntentData(Intent data) {
        if (data != null && data.getExtras() != null) {
            data.getExtras().setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
            MyPhotoSelectImageData newImageData = (MyPhotoSelectImageData) data.getExtras().getSerializable("imgData");

            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), getEditInfo().getTempImageViewID());
            if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) return;
            getEditorBase().refreshSelectedNewImageData(newImageData, (SnapsLayoutControl) snapsControl);
        }
    }

    final void handleNotifyImageDataOnModified() throws Exception {
        //방향이 바뀌었다면, OnResume에서 처리 한다..
        OrientationChecker orientationChecker = getOrientationChecker();
        if (orientationChecker.checkChangedOrientationAtImgEditor()) {
            orientationChecker.setChangedPhoto(true);
        } else {
            getEditorBase().refreshChangedPhoto();
        }
    }

    void handleOnCenterPagerSelected(int page) {
        int pageChangeType = (page > getCurrentPageIndex() ? EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NEXT : (page < getCurrentPageIndex() ? EditActivityThumbnailUtils.PAGE_MOVE_TYPE_PREV
                : EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE));

        getEditInfo().setCurrentPageIndex(page);

        getEditorBase().setThumbnailSelectionDragView(pageChangeType, page);

        if (Config.isNotCoverPhotoBook()) {
            ImageView coverModifyBtn = getEditControls().getThemeCoverModify();
            if (coverModifyBtn != null)
                coverModifyBtn.setVisibility(View.GONE);

            ImageView textModifyBtn = getEditControls().getThemeTextModify();
            if (textModifyBtn != null)
                textModifyBtn.setVisibility(View.GONE);
        }
    }

    void handleBaseChangePageDesign() throws Exception {
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager.getCurrentItem() < 1) {
            getEditorBase().showCoverChangeActcity();
            return;
        }
    }

    void handleBaseAfterRefreshList(int startPageIDX, int endPageIdx) throws Exception {
        int startIdx = Math.min(Math.max(0, startPageIDX), getPageList().size() - 1);
        int endIdx = Math.min(getPageList().size() - 1, endPageIdx);

        setPageCurrentItem(startPageIDX, true);

        getEditorBase().setThumbnailSelectionDragView(EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE, startPageIDX);

        getEditorBase().offerQueue(startIdx, endIdx);
        getEditorBase().refreshPageThumbnailsAfterDelay();
    }

    boolean divisionPageListFrontAndBack(SnapsTemplate snapsTemplate) throws Exception {
        if (!Const_PRODUCT.isBothSidePrintProduct() || snapsTemplate == null)
            return true;

        ArrayList<SnapsPage> pageList = snapsTemplate.getPages();

        if (pageList == null)
            return false;

        try {
            snapsTemplate._backPageList = new ArrayList<SnapsPage>();
            for (int ii = pageList.size() - 1; ii >= 0; ii--) {
                SnapsPage page = pageList.get(ii);
                if (page != null && page.side != null && page.side.equals("back")) {
                    SnapsPage backPage = page;
                    snapsTemplate._backPageList.add(0, backPage);
                    pageList.remove(page);
                }
            }

            if (pageList.size() != snapsTemplate._backPageList.size()) {
                return false;
            }

            getEditorBase().refreshPagesId(pageList);

            getEditorBase().refreshPagesId(snapsTemplate._backPageList);

        } catch (Exception e) {
            Dlog.e(TAG, e);
            return false;
        }

        return true;
    }

    void handleBaseTemplateBaseInfo() throws Exception {
        PhotobookCommonUtils.initBaseTemplateBaseInfo(getActivity(), getSnapsTemplate());

        getSnapsTemplate().addQRcode(getEditorBase().getQRCodeRect());
        setSmartSnapsImgInfo(getSnapsTemplate());
    }

    private void setSmartSnapsImgInfo(SnapsTemplate template) {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct() || !getEditorBase().shouldSmartSnapsAnimateOnActivityStart() || (getEditInfo() != null && getEditInfo().IS_EDIT_MODE()) || AutoSaveManager.isAutoSaveRecoveryMode())
            return;
        try {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.setSmartSnapsImgInfoOnAllImageDataInTemplate(template);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    boolean checkBaseOverPageCount() throws Exception {
        int maxPageCnt = 0;

        try {
            if (Const_PRODUCT.isCardProduct()) {
                maxPageCnt = 999;
            } else {
                maxPageCnt = Integer.parseInt(getSnapsTemplate().info.F_MAX_QUANTITY);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (getPageList() != null && getPageList().size() >= (maxPageCnt + 2))
            return true;
        return false;
    }

    void handleBaseBackPressed() throws Exception {
        if (SnapsUploadFailedImageDataCollector.isShowingUploadFailPopup()) return;

        getEditorBase().dismissPopOvers();
        handleDismissBackgroundToolBox();

        String msg = "";

        if (getEditInfo().IS_EDIT_MODE()) {
            msg = getActivity().getString(R.string.moveto_cartpage_msg);//"편집중인 상품을 저장하지 않고 장바구니 페이지로 이동 하시겠습니까?";
            //KT 북
            if (Config.isKTBook()) {
                msg = Const_VALUES.KT_BOOK_BACK_KEY_MSG;
            }
        } else {
            AutoSaveManager saveMan = AutoSaveManager.getInstance();
            if (saveMan != null && saveMan.isRecoveryMode()) {
                msg = getActivity().getString(R.string.moveto_mainpage_msg);//"편집중인 상품을 저장하지 않고 메인 페이지로 이동 하시겠습니까?";
            } else {
                if (SmartSnapsManager.isSupportSmartSnapsProduct()
                        && Config.isPhotobooks()) {
                    if (SmartSnapsManager.isSmartImageSelectType()) {
                        msg = getActivity().getString(R.string.do_not_save_then_move_to_prev_page_at_main_act);//"편집중인 상품을 저장하지 않고 이전 페이지로 이동 하시겠습니까?";
                    } else {
                        msg = getActivity().getString(R.string.moveto_smart_type_page_msg);//"편집중인 포토북을 저장하지 않고\n사진 선택화면으로 이동하시겠습니까?";
                        //KT 북
                        if (Config.isKTBook()) {
                            msg = Const_VALUES.KT_BOOK_BACK_KEY_MSG;
                        }
                    }
                } else {
                    msg = getActivity().getString(R.string.moveto_detailpage_msg);//"편집중인 상품을 저장하지 않고 디자인 상세 페이지로 이동 하시겠습니까?";
                    //KT 북
                    if (Config.isKTBook()) {
                        msg = Const_VALUES.KT_BOOK_BACK_KEY_MSG;
                    }
                }
            }
        }

        MessageUtil.alertnoTitle(getActivity(), msg, new ICustomDialogListener() {

            @Override
            public void onClick(byte clickedOk) {
                switch (clickedOk) {
                    case ICustomDialogListener.OK:
                        AutoSaveManager saveMan = AutoSaveManager.getInstance();
                        if (saveMan != null) {
                            saveMan.finishAutoSaveMode();
                        }

                        if (getEditInfo().IS_EDIT_MODE()) {
                            Intent intent = new Intent(getActivity(), RenewalHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //intent.putExtra("goToCart", true);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else {
                            checkComebackImageSelectActivity();

                            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
                            snapsTemplateManager.setActivityFinishing(true);
                            getActivity().finish();
                        }
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void checkComebackImageSelectActivity() {
        if (SmartSnapsManager.isSupportSmartSnapsProduct() && SmartSnapsManager.isSmartImageSelectType()) {
            SnapsOrderManager.cancelCurrentImageUploadExecutor();

            Intent intent = new Intent(getActivity(), ImageSelectActivityV2.class);
            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setComebackFromEditActivity(true)
                    .setHomeSelectProduct(Config.SELECT_SMART_SIMPLEPHOTO_BOOK)
                    .setSmartSnapsImageSelectType(SmartSnapsManager.getInstance().getSmartSnapsImageSelectType())
                    .setHomeSelectProductCode(Config.getPROD_CODE())
                    .setHomeSelectKind("").create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
    }

    void handleBaseOnPause() throws Exception {
        getEditorBase().setActivityResumeFinished(false);

        getOrientationManager().setEnableOrientationSensor(false);

        Config.setIS_MAKE_RUNNING(false);

        if (SnapsOrderManager.isUploadingProject())
            SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_APPLICATION);
    }

    void handleBaseOnResume() throws Exception {
        getEditorBase().setActivityResumeFinished(true);

        /**
         * 사진 편집 화면에서 화면 방향이 바뀌었을 경우,
         * 복귀 했을 때, 바로 썸네일을 생성하고 화면 전환을 푼다.
         * (바로 화면 전환이 이루어지면 canvas에 문제가 생겨서..썸네일 생성에 실패한다.)
         */
        if (getOrientationChecker() != null) {
            if (getOrientationChecker().isChangedOrientationAtImgEditor()) {
                //방향 전환을 시키고 loadpaer가 끝나는 시점에 변경 사항을 다시 적용 한다.
                getEditorBase().notifyOrientationState();
            } else
                getEditorBase().restoreRotateState();
        }

        Config.setIS_MAKE_RUNNING(true);

        onResumeControl();

        if (!getEditorBase().isFirstResume()) {
            SnapsOrderManager orderManager = SnapsOrderManager.getInstance();
            orderManager.lockEditorActivityOnResume();
        }

        getEditorBase().onFirstResume();
    }

    void handleBaseOnOrderStateChanged(int state) throws Exception {
        try {
            switch (state) {
                case ORDER_STATE_UPLOADING:
                    getOrientationManager().setEnableOrientationSensor(false);
                    UIUtil.fixCurrentOrientationAndReturnBoolLandScape(getActivity());
                    break;
                case ORDER_STATE_STOP:
//                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
                    } else {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                    getOrientationManager().setEnableOrientationSensor(true);
                    break;
                case ORDER_STATE_CANCEL:
                    if (!Const_PRODUCT.isSinglePageProduct()) {
                        if (PhotobookCommonUtils.isContainLowResolutionImageOnPages(getPageList())) {
                            getEditorBase().findLowResolutionImage();
                        } else {
                            getEditorBase().findEmptyImage();
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    void handleBaseOnUploadFailedOrgImgWhenSaveToBasket() throws Exception {
        SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createUploadFailedImagePopupAttribute(getActivity(), Config.getPROJ_CODE(), getOrientationManager().isLandScapeMode());

        SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute, new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
            @Override
            public void onShowUploadFailedImagePopup() {
                OrientationManager.fixCurrentOrientation(getActivity());
            }

            @Override
            public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
                PhotobookCommonUtils.setUploadFailedIconVisibleStateToShow(getSnapsTemplate());

                getEditorBase().notifyCenterPagerAdapter();

                getEditorBase().requestRefreshThumbnails();

                getEditorBase().selectUploadErrorImgInPager(uploadFailedImageList);

                getEditorBase().notifyOrientationState();
            }
        });
    }

    private void onResumeControl() {
        if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_IMGSAVE)) {
            getEditorBase().requestMakeMainPageThumbnailFile(getEditorBase().getSnapsPageCaptureListener());
        } else if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_UPLOAD_COMPLETE)) {
            SnapsOrderManager.showCompleteUploadPopup();
        }

        SnapsOrderManager.setSnapsOrderStatePauseCode("");

        SnapsOrderManager.registerNetworkChangeReceiverOnResume();
    }

    void handleBaseOnDestroy() throws Exception {
        Config.setIS_MAKE_RUNNING(false);
        Config.setFromCart(false);

        // ProgressView.destroy();
        SnapsTimerProgressView.destroyProgressView();

        SnapsOrderManager.finalizeInstance();

        SnapsUploadFailedImageDataCollector.clearHistory(Config.getPROJ_CODE());

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null) {
            centerPager.setAdapter(null);
        }

        try {
            getEditorBase().removeDragView();

            DataTransManager.releaseInstance();

            ViewUnbindHelper.unbindReferences(getActivity().getWindow().getDecorView());

            // 스마트 스냅스를 선택하는 상품.
            if (!SmartSnapsManager.shouldSelectSmartSnapsTypeProduct()) {
                ImageLoader.clearMemory(getActivity());
                PhotobookCommonUtils.initProductEditInfo();
            }

            SnapsPagerController2 loadPager = getEditControls().getLoadPager();
            if (loadPager != null) {
                try {
                    loadPager.close();
                } catch (IllegalStateException e) {
                    Dlog.e(TAG, e);
                }
            }

            SnapsTemplateManager templateManager = SnapsTemplateManager.getInstance();
            if (templateManager != null) {
                templateManager.cleanInstance();
            }
            SnapsTemplateManager.notifyEditActivityFinishingSyncLocker();

            SmartSnapsManager.finalizeInstance();

            ImageSelectUtils.initPhotoLastSelectedHistory();

            OrientationManager.finalizeInstance();

            releaseSmartSnapsHandler();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        getEditorBase().unRegisterReceivers();
    }

    private void releaseSmartSnapsHandler() {
        if (snapsProductEditorSmartSnapsHandler == null) return;
        snapsProductEditorSmartSnapsHandler = null;
    }

    final void handleBaseRequestDirectUpload() throws Exception {
        getEditorBase().notifyOrientationState();
        // 장바구니 업로드 시작...

        TextView cartBtn = getEditControls().getCartTxt();
        if (cartBtn != null) {
            cartBtn.post(new Runnable() {

                @Override
                public void run() {
                    getEditorBase().onClickedSaveBasket();
                }
            });
        }
    }

    final void handleBasePageThumbnail(final int pageIdx, String filePath) throws Exception {
        Queue<Integer> pageLoadQueue = getEditInfo().getPageLoadQueue();
        if (getPageList() == null || pageLoadQueue == null || getSnapsTemplate() == null || getSnapsTemplate().getPages() == null) {
            SnapsTimerProgressView.destroyProgressView();
            if (getEditorBase().getSnapsPageCaptureListener() != null)
                getEditorBase().getSnapsPageCaptureListener().onFinishPageCapture(false);
            return;
        }

        // String pageCount = String.valueOf(getPageList().size() - pageLoadQueue.size()) + "/" + String.valueOf(getPageList().size());
        // ProgressView.getInstance(BaseThemeBookEditActivity.this).setPageCount(pageCount);
        getOrientationManager().setEnableOrientationSensor(false);
        UIUtil.fixCurrentOrientationAndReturnBoolLandScape(getActivity());

        //페이지 썸네일 한번 딴 것은 다시 따지 않는다.
        PhotobookCommonUtils.changePageThumbnailState(getPageList(), pageIdx, true);

        // 다음 page 처리
        Integer nextPage = pageLoadQueue.poll();

//		if (nextPage != null && nextPage > 0 && snapsPageCaptureListener != null) {
//			snapsPageCaptureListener.endCapturePageThumbnail(pageIdx, filePath);
//		}

        // 만약에 nextPage가 _page리스트에 없는경우.. 다시 실행을 한다.
        if (nextPage != null && getPageList().size() > nextPage && nextPage >= 0) {

            Dlog.d("handleBasePageThumbnail() setPageThumbnail");

//			if (snapsPageCaptureListener != null) {
//				snapsPageCaptureListener.startCapturePageThumbnail();
//			}

            try {
                SnapsCanvasFragment canvasFragment = getEditControls().getCanvasFragment();
                if (canvasFragment != null) {
                    if (canvasFragment.getContext() == null) {
                        throw new Exception("canvasFragment.getContext()  is null");
                    }

                    canvasFragment.getArguments().putBoolean("pageSave", SnapsOrderManager.isUploadingProject()); //저장할때만 true
                    canvasFragment.getArguments().putBoolean("pageLoad", false);
                    canvasFragment.getArguments().putInt("index", nextPage);
                    canvasFragment.getArguments().putBoolean("visibleButton", false);
                    canvasFragment.getArguments().putBoolean("preThumbnail", true);
                    canvasFragment.makeSnapsCanvas();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsTimerProgressView.destroyProgressView();
                MessageUtil.toast(getActivity(), getActivity().getString(R.string.refresh_screen_error_msg));//"화면을 갱신하는 중 오류가 발생 했습니다.");

                if (getEditorBase().getSnapsPageCaptureListener() != null)
                    getEditorBase().getSnapsPageCaptureListener().onFinishPageCapture(false);
            }

        } else if (pageLoadQueue.size() > 0) {
            // Integer nextPagee = pageLoadQueue.poll();
            getEditorBase().setPageThumbnail(-1, "");
        } else {
//					completeMakeThumbnail();
            if (getEditorBase().getSnapsPageCaptureListener() != null) {
                getEditorBase().getSnapsPageCaptureListener().onFinishPageCapture(true);
            }
        }
    }

    final void handleBaseRefreshPageThumbnail() throws Exception {

        //썸네일 갱신
        int startIdx = Integer.MAX_VALUE, endIdx = -1;
        Queue<Integer> pageLoadQueue = getEditInfo().getPageLoadQueue();
        while (pageLoadQueue != null && !pageLoadQueue.isEmpty()) {
            Integer nextPage = pageLoadQueue.poll();
            if (nextPage != null && nextPage >= 0) {
                startIdx = Math.min(startIdx, nextPage);
                endIdx = Math.max(endIdx, nextPage);

                PhotobookCommonUtils.changePageThumbnailState(getPageList(), nextPage, false);
            }
        }

        try {
            BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
            if (thumbnailAdapter != null) {
                if (startIdx >= 0 && startIdx < thumbnailAdapter.getItemCount() && endIdx >= startIdx) {
                    int refreshCount = (endIdx - startIdx) + 1;
                    thumbnailAdapter.notifyItemRangeChanged(startIdx, refreshCount);
                    thumbnailAdapter.refreshThumbnailsLineAndText(getCurrentPageIndex());
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        //자동 저장 파일 갱신
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan != null) {
            if (saveMan.isRecoveryMode()) {
                getEditorBase().finishRecovery();
            } else {
                getEditorBase().exportAutoSaveTemplate();
            }
        }

        //화면 로테이션이 잠겨 있다면 풀어 준다.
        if (getEditorBase().getSnapsHandler() != null)
            getEditorBase().getSnapsHandler().sendEmptyMessageDelayed(HANDLER_MSG_UNLOCK_ROTATE_BLOCK, 1000);
    }

    final void handleBaseRefreshPageThumbnail(int page) throws Exception {

        try {
            BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
            if (thumbnailAdapter != null) {
                if (getEditorBase().isTwinShapeBottomThumbnail()) {
                    page /= 2;
                }

                if (page >= 0 && page < thumbnailAdapter.getItemCount()) {
                    thumbnailAdapter.notifyItemRangeChanged(page, 1);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    SnapsProductBaseEditor getEditorBase() {
        return productEditorBase;
    }

    private OrientationManager getOrientationManager() {
        return getEditorBase().getOrientationManager();
    }

    private OrientationChecker getOrientationChecker() throws Exception {
        return getEditorBase().getOrientationChecker();
    }

    private boolean isOverPageCount() {
        return getEditorBase().isOverPageCount();
    }

    private void showPageOverCountToastMessage() {
        getEditorBase().showPageOverCountToastMessage();
    }

    private void setPreviewBtnVisibleState() {
        getEditorBase().setPreviewBtnVisibleState();
    }

    Activity getActivity() {
        return getEditorBase().getActivity();
    }

    SnapsProductEditControls getEditControls() {
        return getEditorBase().getEditControls();
    }

    public SnapsProductEditInfo getEditInfo() {
        return getEditorBase().getEditInfo();
    }

    private EditActivityThumbnailRecyclerView getThumbnailRecyclerView() {
        return getEditorBase().getThumbnailRecyclerView();
    }

    protected ArrayList<SnapsPage> getPageList() {
        return getEditorBase().getPageList();
    }

    SnapsTemplate getSnapsTemplate() {
        return getEditorBase().getTemplate();
    }

    private View findViewById(@IdRes int id) {
        return getEditorBase().findViewById(id);
    }

    private void onBackPressed() {
        getEditorBase().onBackPressed();
    }

    private View.OnClickListener getOnClickListener() {
        return getEditorBase().getOnClickListener();
    }

    public int getCurrentPageIndex() {
        return getEditorBase().getCurrentPageIndex();
    }

    private void initCanvasMatrix() {
        getEditorBase().initCanvasMatrix();
    }

    private DialogDefaultProgress getPageProgress() {
        return getEditorBase().getPageProgress();
    }

    private BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return getEditorBase().createThumbnailAdapter();
    }

    private SnapsHandler getSnapsHandler() {
        return getEditorBase().getSnapsHandler();
    }

    void handleActivityResumeFinished(boolean activityResumeFinished) {
        isActivityResumeFinished = activityResumeFinished;
    }

    private boolean isActivityResumeFinished() {
        return isActivityResumeFinished;
    }

    private ContentObserver getRotationObserver() {
        return rotationObserver;
    }

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
//            if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//            }
            if (OrientationSensorManager.isActiveAutoRotation(getActivity())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        }
    };

    private void waitActivityResume() {
        if (resumeSyncChecker != null && resumeSyncChecker.getState() == Thread.State.RUNNABLE)
            return;

        resumeSyncChecker = new Thread(new Runnable() {
            @Override
            public void run() {
                final int MAX = 5;
                int count = 0;
                while (!isActivityResumeFinished() && count++ < MAX) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            handleRecoveryLayout();
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }
                });
            }
        });

        resumeSyncChecker.start();
    }

    void dismissPageProgress() {
        try {
            DialogDefaultProgress pageProgress = getEditControls().getPageProgress();
            if (pageProgress != null)
                pageProgress.dismiss();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    private void showPageProgress() {
        try {
            DialogDefaultProgress pageProgress = getEditControls().getPageProgress();
            if (pageProgress != null && !pageProgress.isShowing())
                pageProgress.show();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    private void changeActivityStateToBlurWhenWriteText() throws Exception {
        if (isAppliedBlurActivity) return;

        View rootView = findViewById(R.id.root_layout);
        View titleBarBlindView = findViewById(R.id.snaps_edit_activity_title_bar_blind_view);
        OrientationManager orientationManager = OrientationManager.getInstance(getActivity());
        if (orientationManager.isLandScapeMode()) {
            View galleryView = getActivity().findViewById(R.id.activity_edit_themebook_gallery_ly_h);
            PhotobookCommonUtils.handleBlurEditActivityWhenWriteTextForLandScape(getActivity(), rootView, titleBarBlindView, galleryView);
        } else {
            PhotobookCommonUtils.handleBlurEditActivityWhenWriteText(getActivity(), rootView, titleBarBlindView);
        }

        isAppliedBlurActivity = true;
    }

    void removeBlurActivityWhenWrittenText() throws Exception {
        View rootView = findViewById(R.id.root_layout);
        View galleryView = getActivity().findViewById(R.id.activity_edit_themebook_gallery_ly_h);
        View titleBarBlindView = findViewById(R.id.snaps_edit_activity_title_bar_blind_view);
        PhotobookCommonUtils.recoverBlurEditActivityWhenWrittenText(rootView, titleBarBlindView, galleryView);

        isAppliedBlurActivity = false;
    }

    void handleDismissBackgroundToolBox() {
        View toolBox = getEditControls().getViewBackgroundToolbox();
        if (toolBox != null) {
            toolBox.setVisibility(View.GONE);
        }
    }
}
