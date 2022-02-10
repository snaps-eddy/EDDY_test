package com.snaps.mobile.activity.themebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.XML_BasePage;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SmartSnapsPageEditControls;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.common.products.base.SmartRecommendBookEditorSmartSnapsHandler;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.themebook.adapter.SmartRecommendBookDetailEditPagerAdapter;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants;
import com.snaps.mobile.activity.themebook.interfaceis.SnapsEditTextControlHandleData;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.custom.SmartRecommendBookPageEditDragNDropLayout;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookEditDragImageInfo;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SnapsLayoutUpdateInfo;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies.ISmartRecommendBookPageEditDragNDropBridge;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies.SmartRecommendBookAnimationBridge;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultHandleData;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadStateListener;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import errorhandle.CatchFragmentActivity;
import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.RESULT_CODE_CANCEL;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.RESULT_CODE_EDITED;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eEditorBottomFragment;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eEditorBottomFragment.EDIT_TITLE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eEditorBottomFragment.NONE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eEditorBottomFragment.SELECT_BG_FRAGMENT;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eEditorBottomFragment.SELECT_LAYOUT_FRAGMENT;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eEditorBottomFragment.SELECT_PHOTO_FRAGMENT;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTUTORIAL_TYPE.SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTUTORIAL_TYPE.SMART_RECOMMEND_BOOK_SWIPE_PAGE;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_INIT_CANVAS_MATRIX;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_UPLOAD_THUMB_IMAGES;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_COVER_TEXT;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_INSERT_PHOTO;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_MODIFY;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_PHOTO;
import static com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil.TERM_OF_TUTORIAL_NO_SHOW_10_DAYS;
import static com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil.TERM_OF_TUTORIAL_NO_SHOW_ONLY_ONE_DAYS;

/**
 * changeLayoutWithXmlPage : 레이아웃 바뀔 때
 *
 * onStopDragging : 드래그 앤 드랍으로 이미지 교체 할때
 *
 */

public class SmartRecommendBookPageEditActivity extends CatchFragmentActivity
		implements ISnapsHandler, View.OnClickListener,
		SnapsEditActExternalConnectionBridge,
		ISmartRecommendBookPageEditDragNDropBridge,
		SmartRecommendBookAnimationBridge,
		SnapsImageUploadStateListener,
		SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver {
	private static final String TAG = SmartRecommendBookPageEditActivity.class.getSimpleName();

	private SnapsHandler snapsHandler = null;

	private SnapsPageEditRequestInfo editRequestInfo = null;

	private SmartSnapsPageEditControls editControls = null;

	private SmartRecommendBookPageEditDragNDropLayout dragNDropLayout = null;

	private SmartRecommendBookEditorSmartSnapsHandler smartSnapsAnimationHandler = null;

	private SnapsBroadcastReceiver receiver = null;

	private CustomizeDialog errorAlert = null;

	private GIFTutorialView tutorialView = null;

	private int tempControlId = -1;
	private long lastPageScrolledTime = 0l;

	private MyPhotoSelectImageData changedImageData = null;

	private eEditorBottomFragment currentBottomFragment = eEditorBottomFragment.NONE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.smart_snaps_analysis_page_edit_activity);

		initialize();

		showTutorial();
	}

	private void initialize() {
		snapsHandler = new SnapsHandler(this);

		editControls = SmartSnapsPageEditControls.createInstanceWithActivity(this);

		smartSnapsAnimationHandler = SmartRecommendBookEditorSmartSnapsHandler.createInstanceWithBaseHandler(this);

		fetchIntentInfo();

		initViewPager();

		initDragNDropView();

		initTitle();

		initBottomMenu();

		SnapsOrderManager.setImageUploadStateListener(this);

		registerClickLayoutActionReceiver();
	}

	private void showTutorial() {
		if (!showPhotoDragNDropTutorial()) {
			if (!showPageSwipeTutorial()) {
				showLowResolutionImageAlert();
			}
		}
	}

	private boolean showPhotoDragNDropTutorial() {
		if (SnapsTutorialUtil.isShowConditionSatisfaction(this, SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP, TERM_OF_TUTORIAL_NO_SHOW_10_DAYS)) {
			GIFTutorialView.Builder builder = new GIFTutorialView.Builder()
					.setTitle(getString(R.string.smart_recommend_book_drag_tutorial_desc))
					.setAnimation(SnapsFrameAnimationResFactory.eSnapsFrameAnimation.SMART_RECOMMEND_BOOK_TUTORIAL_DRAG_N_DROP)
					.setTutorialType(SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP).create();

			if (builder != null) {
				tutorialView = new GIFTutorialView(this, builder, new GIFTutorialView.CloseListener() {
					@Override
					public void close() {
						showPageSwipeTutorial();
					}
				});
				addContentView(tutorialView, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			}
			return true;
		}
		return false;
	}

	private boolean showPageSwipeTutorial() {
		if (isCover()) return false;

		if (SnapsTutorialUtil.isShowConditionSatisfaction(this, SMART_RECOMMEND_BOOK_SWIPE_PAGE, TERM_OF_TUTORIAL_NO_SHOW_ONLY_ONE_DAYS)) {
			GIFTutorialView.Builder builder = new GIFTutorialView.Builder()
					.setTitle(getString(R.string.smart_recommend_book_swipe_page_tutorial_desc))
					.setAnimation(SnapsFrameAnimationResFactory.eSnapsFrameAnimation.SMART_RECOMMEND_BOOK_TUTORIAL_SWIPE_PAGE)
					.setTutorialType(SMART_RECOMMEND_BOOK_SWIPE_PAGE).create();

			if (builder != null) {
				tutorialView = new GIFTutorialView(this, builder, new GIFTutorialView.CloseListener() {
					@Override
					public void close() {
						showLowResolutionImageAlert();
					}
				});
				addContentView(tutorialView, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			}
			return true;
		}
		return false;
	}

	private void showLowResolutionImageAlert() {
		SnapsPage snapsPage = getCurrentlyVisibleSnapsPage();
		if (snapsPage == null) return;
		if (snapsPage.isExistLowResolutionImageData()) {
			//만약, 가로모드를 지원해야 한다면...아래 코드 처리 필요..
//			if(isLandScapeMode) {
//				MessageUtil.noPrintToast(this, ResolutionConstants.NO_PRINT_TOAST_OFFSETX_LANDSCAPE_PRINT_EDIT,ResolutionConstants.NO_PRINT_TOAST_OFFSETY_LANDSCAPE_PRINT_EDIT);
//			}else{
				MessageUtil.noPrintToast(this, ResolutionConstants.NO_PRINT_TOAST_OFFSETX_PRINT_EDIT, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_SMART_RECOMMEND_BOOK_EDIT);
//			}
		}
	}

	final void unRegisterReceivers() {
		getActivity().unregisterReceiver(receiver);
	}

	private void registerClickLayoutActionReceiver() {
		IntentFilter filter = new IntentFilter(Const_VALUE.CLICK_LAYOUT_ACTION);
		filter.addAction(Const_VALUE.TEXT_TO_IMAGE_ACTION);
		receiver = new SnapsBroadcastReceiver();
		receiver.setImpRecevice(this);
		getActivity().registerReceiver(receiver, filter);
	}

	private void fetchIntentInfo() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			bundle.setClassLoader( MyPhotoSelectImageData.class.getClassLoader() );
			editRequestInfo = (SnapsPageEditRequestInfo) getIntent().getSerializableExtra(EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA);
		}
	}

	private void initViewPager() {
		InterceptTouchableViewPager viewPager = (InterceptTouchableViewPager) findViewById(R.id.smart_snaps_analysis_cover_edit_activity_view_pager);
		SmartRecommendBookDetailEditPagerAdapter viewPagerAdapter = new SmartRecommendBookDetailEditPagerAdapter(this);
		viewPagerAdapter.setDataWithEditRequestInfo(editRequestInfo);
        viewPagerAdapter.setViewPager(viewPager);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				lastPageScrolledTime = System.currentTimeMillis();
				final int pageIndex = isCover() ? 0 : position+1;
				handlePageSelected(pageIndex);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_SETTLING) {
					if (snapsHandler != null) {
						snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_INIT_CANVAS_MATRIX, 200);
					}
				}
			}
		});

		SnapsAssert.assertNotNull(editControls);
		editControls.setViewPager(viewPager);
		editControls.setPagerAdapter(viewPagerAdapter);

        if (editRequestInfo != null) {
            if (!editRequestInfo.isCover()) {
                viewPager.setCurrentItem(editRequestInfo.getPageIndex() - 1); //커버는 안 들어가기 때문에 -1
            }
            editRequestInfo.setMaskCount(getImageLayerCountOnCurrentPage());
			editRequestInfo.setCurrentPageMultiformId(getMultiformIdOnCurrentlySnapsPage());
			editRequestInfo.setBaseMultiformId(getBaseMultiformIdOnCurrentlySnapsPage());
			editRequestInfo.setCurrentPageBGId(getCurrentBGIdOnCurrentlySnapsPage());
			editRequestInfo.setBasePageBGId(getBaseBGIdOnCurrentlySnapsPage());
        }
	}

	private void handlePageSelected(int position) {
		if (isCover() || position == getPageIndex()) return;
		try {
			editRequestInfo.setPageIndex(position);
			editRequestInfo.setMaskCount(getImageLayerCountOnCurrentPage());
			editRequestInfo.setCurrentPageMultiformId(getMultiformIdOnCurrentlySnapsPage());
			editRequestInfo.setBaseMultiformId(getBaseMultiformIdOnCurrentlySnapsPage());
			editRequestInfo.setCurrentPageBGId(getCurrentBGIdOnCurrentlySnapsPage());
			editRequestInfo.setBasePageBGId(getBaseBGIdOnCurrentlySnapsPage());

			FragmentManager fragmentManager = getSupportFragmentManager();
			Fragment fragment = fragmentManager.findFragmentByTag(SELECT_LAYOUT_FRAGMENT.getFragmentTag());
			if (fragment != null && fragment instanceof SmartRecommendBookEditBottomLayoutFragment) {
				replaceFragment(eEditorBottomFragment.SELECT_LAYOUT_FRAGMENT);
			}

			fragment = fragmentManager.findFragmentByTag(SELECT_BG_FRAGMENT.getFragmentTag());
			if (fragment != null && fragment instanceof SmartRecommendBookEditBottomBGFragment) {
				replaceFragment(eEditorBottomFragment.SELECT_BG_FRAGMENT);
			}

			showLowResolutionImageAlert();

			if (snapsHandler != null)
				snapsHandler.removeMessages(HANDLE_MSG_START_PHOTO_DRAGGING);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void initCanvasMatrix() {
		if (editControls == null) return;
		SmartRecommendBookDetailEditPagerAdapter pagerAdapter = editControls.getPagerAdapter();
		if (pagerAdapter == null) return;

		pagerAdapter.initCanvasMatrix();
	}

	private int getImageLayerCountOnCurrentPage() {
		SnapsPage snapsPage = getCurrentlyVisibleSnapsPage();
		if (snapsPage != null) {
			return snapsPage.getImageLayoutControlCountOnPage();
		}
		return 0;
	}

	private boolean isCover() {
		return editRequestInfo != null && editRequestInfo.isCover();
	}

	private boolean isIndexPage() {
		return getPageIndex() == 1;
	}

	private int getPageIndex() {
		if (editRequestInfo == null) return -1;
		return editRequestInfo.getPageIndex();
	}

	private void initDragNDropView() {
		dragNDropLayout = (SmartRecommendBookPageEditDragNDropLayout) findViewById(R.id.smart_snaps_analysis_page_edit_activity_drag_n_drop_layout);
		dragNDropLayout.setEditDragNDropBridge(this);
	}

	private void initTitle() {
		TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
		if (themeTitle != null) {
			themeTitle.setText(R.string.detail_edit_activity_title);
		}

		TextView completeBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
		completeBtn.setText(getString(R.string.done));
		completeBtn.setOnClickListener(this);

		View titleLayout = findViewById(R.id.rl_edittitle);

		SnapsAssert.assertNotNull(editControls);
		editControls.setTitleLayout(titleLayout);
	}

	private void initBottomMenu() {
		View changePhotoBtn = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_photo_ly);
		changePhotoBtn.setOnClickListener(this);
		View changeLayout = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_layout_ly);
		changeLayout.setOnClickListener(this);

		SnapsAssert.assertNotNull(editControls);

		if (isCover()) {
			View titleLayout = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_title_layout);
			titleLayout.setVisibility(View.VISIBLE);
			titleLayout.setOnClickListener(this);
		} else {
			View changeBG = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_bg_ly);
			changeBG.setVisibility(View.VISIBLE);
			changeBG.setOnClickListener(this);
		}

		toggleChangePhotoViewVisibleState();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (dragNDropLayout != null) {
			dragNDropLayout.releaseInstance();
		}

		unRegisterReceivers();
	}

	@Override
	public void onBackPressed() {
		if (isShownTutorial()) {
			tutorialView.closeTutorial();
			return;
		}
		showCancelConfirm();
	}

	private boolean isShownTutorial() {
		return tutorialView != null && tutorialView.isShown();
	}

	@Override
	public void onClick(View v) {
		if (v == null) return;
		UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

		if (SmartSnapsManager.isSmartAreaSearching()) return;

		if (v.getId() == R.id.ThemeTitleLeftLy || (v.getId() == R.id.ThemeTitleLeft)) {
			showCancelConfirm();
		} else if (v.getId() == R.id.ThemebtnTopNext) {
			performCompleteBtn();
		} else if (v.getId() == R.id.smart_snaps_analysis_page_edit_activity_change_photo_ly) {
			WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickEditphoto;
			if (isCover()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_clickEditphoto;
			else if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickEditphoto;

			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
					.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
					.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
					.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

			toggleChangePhotoViewVisibleState();
		} else if (v.getId() == R.id.smart_snaps_analysis_page_edit_activity_change_layout_ly) {
			WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickEditlayout;
			if (isCover()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_clickEditlayout;
			else if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickEditlayout;

			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
					.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
					.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
					.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

			toggleChangeLayoutViewVisibleState();
		} else if (v.getId() == R.id.smart_snaps_analysis_page_edit_activity_change_bg_ly) {
			WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickEditbackground;
			if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickEditbackground;

			SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
					.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
					.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
					.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

			toggleChangeBGViewVisibleState();
		} else if (v.getId() == R.id.smart_snaps_analysis_page_edit_activity_change_title_layout) {
			if (isCover()) {
				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editdetailcover_edittextEdittext)
						.appendPayload(WebLogConstants.eWebLogPayloadType.TITLE_TEXT, Config.getPROD_NAME())
						.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
						.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
			}

			performChangeTitle();
		}
	}

	@Override
	public void onReceiveData(Context context, Intent intent) {
		if (SmartSnapsManager.isSmartAreaSearching()) return;

		if (PhotobookCommonUtils.isFromLayoutControlReceiveData(intent)) {
			int control_id = intent.getIntExtra("control_id", -1);

			SnapsControl control = findSnapsControlWithTempImageViewId(control_id);
			if (control == null || control_id == -1) {
				tempControlId = -1; //FXIME... 이런 부분은 공통화를 하던....리펙토링 하자...
				return;
			}

			tempControlId = control_id;

			boolean isOnClickTextControl = control instanceof SnapsTextControl;

			if (isOnClickTextControl) {
				onClickedTextControl(SnapsProductEditReceiveData.createReceiveData(intent, control));
			} else {
				boolean isLongClick = intent.getBooleanExtra("isLongClick", false);
				if (isLongClick) {
					onLongClickedLayoutControl();
				} else {
					onClickedLayoutControl();
				}
			}
		}
	}

	private void onLongClickedLayoutControl() {
		if (SmartSnapsManager.isSmartAreaSearching() || isOnlyOnePhotoInPage()) return;

		SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), tempControlId);
		if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) {
			return;
		}

		SnapsLayoutControl layoutControl = (SnapsLayoutControl) snapsControl;
		View targetView = findViewById(layoutControl.getControlId());
		if (targetView != null && targetView instanceof ImageView) {
			SmartRecommendBookEditDragImageInfo dragImageInfo = new SmartRecommendBookEditDragImageInfo.Builder().setLongClickedLayoutControl(layoutControl).setSwapping().setImageData(layoutControl.imgData).setView((ImageView)targetView).create();
			Message msg = new Message();
			msg.what = HANDLE_MSG_START_PHOTO_DRAGGING;
			msg.obj = dragImageInfo;

			if (snapsHandler != null)
				snapsHandler.sendMessage(msg);
		}
	}

	private boolean isOnlyOnePhotoInPage() {
		return getImageLayerCountOnCurrentPage() <= 1;
	}

	private void onClickedLayoutControl() {
		if (SmartSnapsManager.isSmartAreaSearching()) return;

		SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), tempControlId);
		if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) {
			return;
		}

		try {
			if (isInsertedImageOnLayout((SnapsLayoutControl) snapsControl)) {
				performEditPhoto((SnapsLayoutControl) snapsControl);
			} else {
				performChangePhoto(snapsControl);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private boolean isInsertedImageOnLayout(SnapsLayoutControl layoutControl) {
		return layoutControl != null && layoutControl.imgData != null && !StringUtil.isEmpty(ImageUtil.getImagePath(getActivity(), layoutControl.imgData));
	}

	private void performEditPhoto(SnapsLayoutControl layoutControl) {
		if (layoutControl == null) return;
		try {
			Intent intent = new Intent(getActivity(), ImageEditActivity.class);

            IPhotobookCommonConstants.eImageDataRequestType requestType = isCover() ? IPhotobookCommonConstants.eImageDataRequestType.ONLY_COVER : IPhotobookCommonConstants.eImageDataRequestType.ALL_EXCEPT_COVER;
			ArrayList<MyPhotoSelectImageData> images = PhotobookCommonUtils.getMyPhotoSelectImageDataWithTemplate(getTemplate(), requestType);
			PhotobookCommonUtils.setImageDataScaleable(getTemplate());
			DataTransManager dtMan = DataTransManager.getInstance();
			if (dtMan != null) {
				dtMan.setPhotoImageDataList(images);
			} else {
				DataTransManager.notifyAppFinish(getActivity());
				return;
			}

			int idx = PhotobookCommonUtils.getImageIndex(getActivity(), images, tempControlId);
			if (idx < 0) return;

			intent.putExtra("dataIndex", idx);
			intent.putExtra("pageIndex", editRequestInfo != null ? editRequestInfo.getPageIndex() : -1);
			startActivityForResult(intent, REQ_MODIFY);

			if (images != null && images.size() > idx) {
				MyPhotoSelectImageData targetImage = images.get(idx);

				WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickImg;
				if (isCover()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_clickImg;
				else if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickImg;

				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
						.appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (targetImage != null ? targetImage.getImagePathForWebLog() : ""))
						.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
						.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
						.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void performChangePhoto(SnapsControl snapsControl) throws Exception {
		Intent broadIntent = new Intent(Const_VALUE.RESET_LAYOUT_ACTION);
		getActivity().sendBroadcast(broadIntent);

		Message msg = new Message();
		msg.what = HANDLER_MSG_CLICKED_LAYOUT_CONTROL;
		msg.obj = snapsControl;

		if (snapsHandler != null)
			snapsHandler.sendMessage(msg);
	}

	private void showTitleLayout() {
		if (editControls == null || editControls.getTitleLayout() == null) return;
		editControls.getTitleLayout().setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			SnapsOrderManager.uploadThumbImgListOnBackground();

			if (requestCode == REQ_COVER_TEXT) {
				setBottomMenuBtnState(NONE);
				showTitleLayout();
			}

			return;
		}

		switch (requestCode) {
			case REQ_INSERT_PHOTO:
				notifyLayoutControlFromIntentData(data);
				break;
			case REQ_PHOTO:
				handleAddPhoto();
				break;
			case IPhotobookCommonConstants.REQ_MODIFY:
				notifyImageDataOnModified();
				break;
			case REQ_COVER_TEXT:
				try {
					showTitleLayout();
					handleNotifyAllCoversTextFromIntentData(data);
					setBottomMenuBtnState(NONE);

					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editdetailcover_updateEdittext)
							.appendPayload(WebLogConstants.eWebLogPayloadType.TITLE_TEXT, Config.getPROJ_NAME())
							.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, "0")
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				break;
		}
	}

	void notifyLayoutControlFromIntentData(Intent data) {
		if (data != null && data.getExtras() != null) {
			data.getExtras().setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
			MyPhotoSelectImageData newImageData = (MyPhotoSelectImageData) data.getExtras().getSerializable("imgData");

			SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(getActivity(), tempControlId);
			if (snapsControl == null || !(snapsControl instanceof SnapsLayoutControl)) return;
			try {
				handleRefreshSelectedNewImageData((SnapsLayoutControl) snapsControl, newImageData, true);
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

	private void handleAddPhoto() {
		refreshBottomPhotoList();

		smoothScrollToPositionBottomPhotoList(0);
	}

	private void notifyImageDataOnModified() {
		ArrayList<MyPhotoSelectImageData> imgList = DataTransManager.getImageDataFromDataTransManager(getActivity());
		if (imgList == null || imgList.isEmpty()) return;

        PhotobookCommonUtils.getChangedPhotoPageIndexWithImageList(imgList, getTemplate());

        handleRefreshImageWithPageIndexList();
	}

	public void handleNotifyAllCoversTextFromIntentData(Intent data) throws Exception {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		ArrayList<SnapsPage> coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
		if (coverList == null) return;

		for (SnapsPage cover : coverList) {
			if (cover == null) continue;
			PhotobookCommonUtils.handleNotifyCoverTextFromIntentData(data, this, cover);
		}

		refreshCurrentCanvasOnViewPager();
	}

	private void handleRefreshImageWithPageIndexList() {
		PhotobookCommonUtils.imageResolutionCheck(getTemplate());

		refreshCurrentCanvasOnViewPager();
	}

	private void onClickedTextControl(SnapsProductEditReceiveData editReceiveData) {
	}

	private SnapsControl findSnapsControlWithTempImageViewId(int tempImageViewID) {
		return PhotobookCommonUtils.getSnapsControlFromView(findViewById(tempImageViewID));
	}

	@Override
	public void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {}

	@Override
	public void onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {
		SnapsImageUploadResultHandleData resultHandleData = new SnapsImageUploadResultHandleData.Builder()
				.setActivity(getActivity()).setSnapsHandler(snapsHandler).setSnapsTemplate(getTemplate()).setState(state).setUploadResultData(resultData).create();
		PhotobookCommonUtils.handleThumbImgUploadStateChanged(resultHandleData);
	}

	private void toggleChangePhotoViewVisibleState() {
		if (currentBottomFragment == SELECT_PHOTO_FRAGMENT) return;
		showBottomListViewVisibleState();
		replaceFragment(eEditorBottomFragment.SELECT_PHOTO_FRAGMENT);
	}

	private void toggleChangeLayoutViewVisibleState() {
		if (currentBottomFragment == SELECT_LAYOUT_FRAGMENT) return;
		showBottomListViewVisibleState();
		replaceFragment(eEditorBottomFragment.SELECT_LAYOUT_FRAGMENT);
	}

	private void toggleChangeBGViewVisibleState() {
		if (currentBottomFragment == SELECT_BG_FRAGMENT) return;
		showBottomListViewVisibleState();
		replaceFragment(eEditorBottomFragment.SELECT_BG_FRAGMENT);
	}

	private void showBottomListViewVisibleState() {
		View bottomListView = findViewById(R.id.smart_snaps_analysis_page_edit_activity_bottom_fragment_layout);
		if (bottomListView != null && !bottomListView.isShown()) bottomListView.setVisibility(View.VISIBLE);
	}

	private void hideBottomListViewVisibleState() {
		View bottomListView = findViewById(R.id.smart_snaps_analysis_page_edit_activity_bottom_fragment_layout);
		if (bottomListView != null && bottomListView.isShown()) bottomListView.setVisibility(View.GONE);
	}

	private void performChangeTitle() {
		if (getPageList() == null || getPageList().isEmpty()) return;

		hideBottomListViewVisibleState();

		SnapsTextControl coverTextControl = PhotobookCommonUtils.findCoverTextControl(getPageList().get(0));
		if (coverTextControl == null) return;

		tempControlId = coverTextControl.getControlId();

		SnapsEditTextControlHandleData textControlHandleData = new SnapsEditTextControlHandleData.Builder()
				.setActivity(getActivity())
				.setCoverTitleEdit(true)
				.setActivityRequestCode(REQ_COVER_TEXT)
				.setTitleLayout(editControls != null ? editControls.getTitleLayout() : null)
				.setSnapsTemplate(getTemplate())
				.setTempViewId(tempControlId)
				.create();

		try {
			PhotobookCommonUtils.handlePerformEditText(textControlHandleData);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		setBottomMenuBtnState(EDIT_TITLE);

		currentBottomFragment = eEditorBottomFragment.NONE;
	}

	private void performCompleteBtn() {
		WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickComplete;
		if (isCover()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_clickComplete;
		else if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickComplete;

		WebLogRequestBuilder webLogRequestBuilder = WebLogRequestBuilder.createBuilderWithLogName(logName)
				.appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, getImagePathOnCurrentlySnapsPage())
				.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
				.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
				.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE());

		if (isCover()) {
			if (changedImageData != null) {
				applyCurrentImageOnAllCovers();
			}
			webLogRequestBuilder.appendPayload(WebLogConstants.eWebLogPayloadType.TITLE_TEXT, Config.getPROJ_NAME());
		} else {
			webLogRequestBuilder.appendPayload(WebLogConstants.eWebLogPayloadType.BACKGROUND, getBaseBGIdOnCurrentlySnapsPage());
		}

		SnapsLogger.sendWebLog(webLogRequestBuilder);

		Bundle bundle = new Bundle();
		bundle.putInt(EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA, getPageIndex());
		Intent itt = getIntent();
		itt.putExtras(bundle);

		setResult(RESULT_CODE_EDITED, itt);
		finish();
	}

	private void applyCurrentImageOnAllCovers() {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		ArrayList<SnapsPage> coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();

		if (coverList == null || changedImageData == null) return;

		for (int ii = 0; ii < coverList.size(); ii++) {
			if (ii == editRequestInfo.getCoverTemplateIndex()) continue;
			SnapsPage cover = coverList.get(ii);
			if (cover == null) continue;

			for (int i = 0; i < cover.getLayoutList().size(); i++) {
				SnapsLayoutControl layout = (SnapsLayoutControl) cover.getLayoutList().get(i);
				if (layout.type.equalsIgnoreCase("browse_file")) {
					MyPhotoSelectImageData copiedImageData = new MyPhotoSelectImageData();
					copiedImageData.weakCopy(changedImageData);

					try {
						layout.initImageRc();

						SnapsLayoutUpdateInfo layoutUpdateInfo = new SnapsLayoutUpdateInfo.Builder().setSnapsTemplate(getTemplate()).setLayoutControl(layout).setNewImageData(copiedImageData).setShouldSmartSnapsFitAnimation(false).create();
						PhotobookCommonUtils.replaceNewImageData(layoutUpdateInfo, null);

						SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(SmartRecommendBookPageEditActivity.this, layout);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
					break;
				}
			}
		}
	}

	public void showCancelConfirm() {
		WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickBack;
		if (isCover())
			logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_clickBack;
		else if (isIndexPage())
			logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickBack;

		SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
				.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : 0))
				.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

		MessageUtil.alertnoTitle(this, getString(R.string.do_not_save_then_move_to_prev_page), new ICustomDialogListener() {
			@Override
			public void onClick(byte clickedOk) {
				if (clickedOk == ICustomDialogListener.OK) {
					WebLogConstants.eWebLogName logName = null;
					if (isCover())
						logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcoverback_clickConfirm;
					else if (isIndexPage())
						logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindexback_clickConfirm;


					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
							.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : 0))
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

					finishActivityByCancel();
				} else {
					WebLogConstants.eWebLogName logName = null;
					if (isCover())
						logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcoverback_clickCancel;
					else if (isIndexPage())
						logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindexback_clickCancel;

					SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
							.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : 0))
							.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
				}
			}
		});
	}

	private void finishActivityByCancel() {
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA, getPageIndex());
		Intent itt = getIntent();
		itt.putExtras(bundle);

		setResult(RESULT_CODE_CANCEL, itt);
		finish();
	}

	private void replaceFragment(eEditorBottomFragment fragment) {
		switch (fragment) {
			case SELECT_BG_FRAGMENT:
				setBottomMenuBtnState(SELECT_BG_FRAGMENT);
				replaceBottomToSelectBGFragment();
				break;
			case SELECT_LAYOUT_FRAGMENT:
				setBottomMenuBtnState(SELECT_LAYOUT_FRAGMENT);
				replaceBottomToSelectLayoutFragment();
				break;
			case SELECT_PHOTO_FRAGMENT:
				setBottomMenuBtnState(SELECT_PHOTO_FRAGMENT);
				replaceBottomToSelectPhotoFragment();
				break;
		}

		currentBottomFragment = fragment;
	}

	private void setBottomMenuBtnState(eEditorBottomFragment fragment) {
		ImageView changePhotoBtn = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_photo_btn);
		ImageView changeLayoutBtn = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_layout_btn);
		ImageView changeTitleBtn = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_title_btn);
		ImageView changeBGBtn = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_bg_btn);

		TextView changePhotoText = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_photo_tv);
		TextView changeLayoutText = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_layout_tv);
		TextView changeTitleText = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_title_tv);
		TextView changeBgText = findViewById(R.id.smart_snaps_analysis_page_edit_activity_change_bg_tv);

		if (changePhotoBtn == null || changeLayoutBtn == null || changeTitleBtn == null || changeBGBtn == null
				|| changePhotoText == null || changeLayoutText == null || changeTitleText == null || changeBgText == null) return;

		int disableTextColor = Color.parseColor("#ff999999");
		int enableTextColor = Color.parseColor("#ff191919");

		switch (fragment) {
			case SELECT_LAYOUT_FRAGMENT:
				changePhotoBtn.setImageResource(R.drawable.btn_edit_photo_off);
				changeLayoutBtn.setImageResource(R.drawable.btn_edit_layout_on);
				changeTitleBtn.setImageResource(R.drawable.btn_edit_title_off);
				changeBGBtn.setImageResource(R.drawable.btn_edit_bg_off);

				changePhotoText.setTextColor(disableTextColor);
				changeLayoutText.setTextColor(enableTextColor);
				changeTitleText.setTextColor(disableTextColor);
				changeBgText.setTextColor(disableTextColor);
				break;
			case SELECT_PHOTO_FRAGMENT:
				changePhotoBtn.setImageResource(R.drawable.btn_edit_photo_on);
				changeLayoutBtn.setImageResource(R.drawable.btn_edit_layout_off);
				changeTitleBtn.setImageResource(R.drawable.btn_edit_title_off);
				changeBGBtn.setImageResource(R.drawable.btn_edit_bg_off);

				changePhotoText.setTextColor(enableTextColor);
				changeLayoutText.setTextColor(disableTextColor);
				changeTitleText.setTextColor(disableTextColor);
				changeBgText.setTextColor(disableTextColor);
				break;
			case SELECT_BG_FRAGMENT:
				changePhotoBtn.setImageResource(R.drawable.btn_edit_photo_off);
				changeLayoutBtn.setImageResource(R.drawable.btn_edit_layout_off);
				changeTitleBtn.setImageResource(R.drawable.btn_edit_title_on);
				changeBGBtn.setImageResource(R.drawable.btn_edit_bg_on);

				changePhotoText.setTextColor(disableTextColor);
				changeLayoutText.setTextColor(disableTextColor);
				changeTitleText.setTextColor(disableTextColor);
				changeBgText.setTextColor(enableTextColor);
				break;
			case EDIT_TITLE:
				changePhotoBtn.setImageResource(R.drawable.btn_edit_photo_off);
				changeLayoutBtn.setImageResource(R.drawable.btn_edit_layout_off);
				changeTitleBtn.setImageResource(R.drawable.btn_edit_title_on);
				changeBGBtn.setImageResource(R.drawable.btn_edit_bg_off);

				changePhotoText.setTextColor(disableTextColor);
				changeLayoutText.setTextColor(disableTextColor);
				changeTitleText.setTextColor(enableTextColor);
				changeBgText.setTextColor(disableTextColor);
				break;
			case NONE:
				changePhotoBtn.setImageResource(R.drawable.btn_edit_photo_off);
				changeLayoutBtn.setImageResource(R.drawable.btn_edit_layout_off);
				changeTitleBtn.setImageResource(R.drawable.btn_edit_title_off);
				changeBGBtn.setImageResource(R.drawable.btn_edit_bg_off);

				changePhotoText.setTextColor(disableTextColor);
				changeLayoutText.setTextColor(disableTextColor);
				changeTitleText.setTextColor(disableTextColor);
				changeBgText.setTextColor(disableTextColor);
				break;
		}
	}

	private void replaceBottomToSelectBGFragment() {
		Fragment fragment = SmartRecommendBookEditBottomBGFragment.newInstance(snapsHandler);

		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA, editRequestInfo);
		fragment.setArguments(bundle);

		FragmentUtil.replce(R.id.smart_snaps_analysis_page_edit_activity_bottom_fragment_layout, this, fragment, SELECT_BG_FRAGMENT.getFragmentTag(), 0, 0);
	}

	private void replaceBottomToSelectLayoutFragment() {
		Fragment fragment = SmartRecommendBookEditBottomLayoutFragment.newInstance(snapsHandler);

		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA, editRequestInfo);
		fragment.setArguments(bundle);

		FragmentUtil.replce(R.id.smart_snaps_analysis_page_edit_activity_bottom_fragment_layout, this, fragment, SELECT_LAYOUT_FRAGMENT.getFragmentTag(), 0, 0);
	}

	private void replaceBottomToSelectPhotoFragment() {
		Fragment fragment = SmartRecommendBookEditBottomPhotoFragment.newInstance(snapsHandler);
		FragmentUtil.replce(R.id.smart_snaps_analysis_page_edit_activity_bottom_fragment_layout, this, fragment, SELECT_PHOTO_FRAGMENT.getFragmentTag(), 0, 0);
	}

	@Override
	public ArrayList<MyPhotoSelectImageData> getGalleryList() {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		return smartSnapsManager.getAllAddedImageList();
	}

	@Override
	public ArrayList<SnapsPage> getPageList() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        if (snapsTemplate == null) return null;
        return snapsTemplate.getPages();
	}

	@Override
	public SnapsTemplate getTemplate() {
		return SnapsTemplateManager.getInstance().getSnapsTemplate();
	}

	@Override
	public void refreshUI() {
		refreshViewPagerList();

		refreshBottomPhotoList();
	}

	private void refreshViewPagerList() {
		if (editControls == null || editControls.getPagerAdapter() == null) return;
		SmartRecommendBookDetailEditPagerAdapter adapter = editControls.getPagerAdapter();
		adapter.notifyDataSetChanged();
	}

	private void refreshCurrentCanvasOnViewPager() {
		if (editControls == null || editControls.getPagerAdapter() == null) return;
		SmartRecommendBookDetailEditPagerAdapter adapter = editControls.getPagerAdapter();
		if (isCover()) {
			adapter.notifyDataSetChanged();
		} else {
			adapter.notifyDataSetChanged(getCurrentPagerPosition()+1); //커버를 빼고 adapter에 넣었기 때문에 1을 더해서 index를 던진다.
		}
	}

	private void refreshBottomPhotoList() {
		try {
			FragmentManager fragmentManager = getSupportFragmentManager();
			Fragment fragment = fragmentManager.findFragmentByTag(SELECT_PHOTO_FRAGMENT.getFragmentTag());
			if (fragment != null && fragment instanceof SmartRecommendBookEditBottomPhotoFragment) {
				SmartRecommendBookEditBottomPhotoFragment bottomPhotoFragment = (SmartRecommendBookEditBottomPhotoFragment)fragment;
				bottomPhotoFragment.refreshPhotoSelectedState();
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void smoothScrollToPositionBottomPhotoList(int position) {
		try {
			FragmentManager fragmentManager = getSupportFragmentManager();
			Fragment fragment = fragmentManager.findFragmentByTag(SELECT_PHOTO_FRAGMENT.getFragmentTag());
			if (fragment != null && fragment instanceof SmartRecommendBookEditBottomPhotoFragment) {
				SmartRecommendBookEditBottomPhotoFragment bottomPhotoFragment = (SmartRecommendBookEditBottomPhotoFragment)fragment;
				bottomPhotoFragment.smoothScrollToPosition(position);
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
	public int getCanvasLoadCompleteCount() {
		return 0;
	}

	@Override
	public void increaseCanvasLoadCompleteCount() {}

	@Override
	public void decreaseCanvasLoadCompleteCount() {}

	@Override
	public void pageProgressUnload() {}

	@Override
	public void showPageProgress() {}

	@Override
	public void setPageThumbnailFail(int index) {}

	@Override
	public void setPageThumbnail(int pageIdx, String filePath) {}

	@Override
	public void setPageFileOutput(int index) {}

	@Override
	public SnapsProductEditorAPI getProductEditorAPI() {
		return null;
	}

	@Override
	public Map<Rect, SnapsLayoutControl> getCurrentlyVisibleControlsRect() {
		try {
			SnapsPage snapsPage = getCurrentlyVisibleSnapsPage();
			return getLayoutControlsRectInSnapsPage(this, snapsPage);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return null;
	}

	private SnapsLayoutControl getCheckExcludeLayoutControl() {
	    if (dragNDropLayout == null || !dragNDropLayout.isSwappingImage()) return null;
	    return dragNDropLayout.getLongClickedLayoutControl();
    }

	private Map<Rect, SnapsLayoutControl> getLayoutControlsRectInSnapsPage(Activity activity, SnapsPage snapsPage) throws Exception {
        SnapsLayoutControl checkExcludeLayoutControl = getCheckExcludeLayoutControl();

		Map<Rect, SnapsLayoutControl> result = new HashMap<>();
		ArrayList<SnapsLayoutControl> layoutControls = PhotobookCommonUtils.getLayoutControlsInSnapsPage(snapsPage);
		if (layoutControls == null) return null;
		for (SnapsLayoutControl layoutControl : layoutControls) {
			if (layoutControl == null || layoutControl == checkExcludeLayoutControl) continue;
			View targetView = activity.findViewById(layoutControl.getControlId());
			if (targetView != null) {
				Rect viewRect = new Rect();
				targetView.getGlobalVisibleRect(viewRect);
				DataTransManager transMan = DataTransManager.getInstance();
				if(transMan != null) {
					ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
					viewRect = coordInfo.covertItemRectForRecommendBookEdit(targetView);
				}

				result.put(viewRect, layoutControl);
			}
		}
		return result;
	}

	@Override
	public void onStopDragging(SnapsLayoutControl targetLayoutControl, MyPhotoSelectImageData newImageData) {
		try {
			if (isSwappingImage()) {
				swapImages(targetLayoutControl, newImageData);
			} else {
				WebLogConstants.eWebLogName logNameREQ = WebLogConstants.eWebLogName.photobook_annie_editdetail_updateImg_REQ;
				if (isCover()) logNameREQ = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_updateImg_REQ;
				else if (isIndexPage()) logNameREQ = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_updateImg_REQ;

				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logNameREQ)
						.appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (targetLayoutControl != null && targetLayoutControl.imgData != null ? targetLayoutControl.imgData.getImagePathForWebLog() : ""))
						.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
						.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE,  String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
						.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

				WebLogConstants.eWebLogName logNameRES = WebLogConstants.eWebLogName.photobook_annie_editdetail_updateImg_RES;
				if (isCover()) logNameRES = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_updateImg_RES;
				else if (isIndexPage()) logNameRES = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_updateImg_RES;

				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logNameRES)
						.appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (newImageData != null ? newImageData.getImagePathForWebLog() : ""))
						.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
						.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE,  String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
						.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

				handleRefreshSelectedNewImageData(targetLayoutControl, newImageData, true);

				changedImageData = new MyPhotoSelectImageData();
				changedImageData.weakCopy(newImageData);

				refreshBottomPhotoList();
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void swapImages(final SnapsLayoutControl targetLayoutControl, final MyPhotoSelectImageData newImageData) throws Exception {
		if (dragNDropLayout == null) return;
		SnapsLayoutControl longClickedLayoutControl = dragNDropLayout.getLongClickedLayoutControl();
		if (longClickedLayoutControl == null) return;

		MyPhotoSelectImageData targetImageData = new MyPhotoSelectImageData();
		targetImageData.weakCopy(targetLayoutControl.imgData);

		handleRefreshSelectedNewImageData(longClickedLayoutControl, targetImageData, false);

		handleRefreshSelectedNewImageData(targetLayoutControl, newImageData, true);

		SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editdetail_swapImg)
				.appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (newImageData != null ? newImageData.getImagePathForWebLog() : ""))
				.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
				.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
	}

	private boolean isSwappingImage() {
		return dragNDropLayout != null && dragNDropLayout.isSwappingImage();
	}

    private void handleRefreshSelectedNewImageData(SnapsLayoutControl control, MyPhotoSelectImageData newImageData, boolean shouldRefresh) throws Exception {
		if (control == null) return;

		try {
			control.initImageRc();

			SnapsLayoutUpdateInfo layoutUpdateInfo = new SnapsLayoutUpdateInfo.Builder().setSnapsTemplate(getTemplate()).setLayoutControl(control).setNewImageData(newImageData).create();
			PhotobookCommonUtils.replaceNewImageData(layoutUpdateInfo, smartSnapsAnimationHandler);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		requestSmartSnapsAnimation(control, newImageData, shouldRefresh);
	}

	private void requestSmartSnapsAnimation(final SnapsLayoutControl targetLayoutControl, final MyPhotoSelectImageData newImageData, boolean shouldRefresh) {
		try {
			SmartSnapsManager.setSmartAreaSearching(true);

			final boolean shouldBeThumbImgUploadWithImageData = shouldBeThumbImgUploadOnDragging(newImageData);

			final SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
			if (shouldBeThumbImgUploadWithImageData) {
				if (newImageData.isFindSmartSnapsFaceArea()) {
					smartSnapsManager.setSmartSnapsAnimationReadyState(newImageData, targetLayoutControl.getPageIndex());
				} else {
					smartSnapsManager.setSingleSmartSnapsImageData(newImageData, targetLayoutControl.getPageIndex());
				}

				uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(newImageData);
			} else {
				SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(newImageData, SmartSnapsConstants.eSmartSnapsImgState.FINISH_ANIMATION);

				SmartSnapsUtil.setSmartImgDataStateReadyOnChangeLayout(newImageData, targetLayoutControl.getPageIndex());
			}

			if (shouldRefresh)
				refreshCurrentCanvasOnViewPager();

			final Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						if (!shouldBeThumbImgUploadWithImageData) {
							List<MyPhotoSelectImageData> smartSnapsImageList = smartSnapsManager.createSmartSnapsImageListWithPageIdx(targetLayoutControl.getPageIndex());
							smartSnapsImageList.add(newImageData);

							SmartSnapsUtil.refreshSmartSnapsImgInfoOnNewLayoutWithImgList(getActivity(), getTemplate(), smartSnapsImageList, targetLayoutControl.getPageIndex());
						}

						SmartSnapsManager.startSmartSnapsAutoFitImage(smartSnapsAnimationHandler, SmartSnapsConstants.eSmartSnapsProgressType.CHANGE_PHOTO, targetLayoutControl.getPageIndex());
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}, 200);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private boolean shouldBeThumbImgUploadOnDragging(MyPhotoSelectImageData orgData) {
		if (!SnapsImageUploadUtil.shouldBeUploadImageDataKind(orgData)) return false;
		String thumbPath = orgData.THUMBNAIL_PATH;
		return StringUtil.isEmpty(thumbPath) || (!thumbPath.startsWith("http") && !thumbPath.startsWith("/Upload"));
	}

	private void uploadThumbImgOnBackgroundAfterSuspendCurrentExecutor(MyPhotoSelectImageData imageData) {
		SnapsOrderManager.cancelCurrentImageUploadExecutor();

		Message msg = new Message();
		msg.what = HANDLER_MSG_UPLOAD_THUMB_IMAGES;
		msg.obj = imageData;

		if (snapsHandler != null)
			snapsHandler.sendMessageDelayed(msg, 200);
	}

	private int getCurrentPagerPosition() {
		return editControls != null && editControls.getViewPager() != null ? editControls.getViewPager().getCurrentItem() : 0;
	}

	private String getMultiformIdOnCurrentlySnapsPage() {
		SnapsPage currentPage = getCurrentlyVisibleSnapsPage();
		return currentPage != null ? currentPage.multiformId : null;
	}

	private String getBaseMultiformIdOnCurrentlySnapsPage() {
		SnapsPage currentPage = getCurrentlyVisibleSnapsPage();
		return currentPage != null ? currentPage.orgMultiformId : null;
	}

	private String getBaseBGIdOnCurrentlySnapsPage() {
		SnapsPage currentPage = getCurrentlyVisibleSnapsPage();
		if (currentPage != null) {
			return currentPage.orgBgId;
		}
		return null;
	}

	private String getCurrentBGIdOnCurrentlySnapsPage() {
		SnapsPage currentPage = getCurrentlyVisibleSnapsPage();
		if (currentPage != null) {
			ArrayList<SnapsControl> bgList = currentPage.getBgList();
			if (bgList != null && !bgList.isEmpty()) {
				SnapsControl bgControl = bgList.get(0);
				if (bgControl != null && bgControl instanceof SnapsBgControl) {
					return ((SnapsBgControl) bgControl).srcTarget;
				}
			}
		}
		return null;
	}

	private String getImagePathOnCurrentlySnapsPage() {
		SnapsPage currentPage = getCurrentlyVisibleSnapsPage();
		if (currentPage != null) {
			SnapsLayoutControl layoutControl = currentPage.getFirstImageLayoutControlOnPage();
			if (layoutControl != null) {
				MyPhotoSelectImageData imageData = layoutControl.imgData;
				return imageData != null ? imageData.ORIGINAL_PATH : "";
			}
		}
		return "";
	}

	private SnapsPage getCurrentlyVisibleSnapsPage() {
		if (editControls == null || editControls.getPagerAdapter() == null) return null;

		return editControls.getPagerAdapter().getSnapsPageOnPageList(getCurrentPagerPosition());
	}

	private void changeBGWithMsg(Message msg) {
		if (msg == null || msg.obj == null || !(msg.obj instanceof XML_BasePage)) return;
		XML_BasePage xmlPage = (XML_BasePage) msg.obj;
		changeBGWithXmlPage(xmlPage);
	}

	private void changeBGWithXmlPage(final XML_BasePage layout) {
		ATask.executeVoidWithThreadPoolBooleanDefProgress(this, new ATask.OnTaskResult() {
			SnapsTemplate newLayoutTemplate = null;
			@Override
			public void onPre() {

			}

			@Override
			public boolean onBG() {
				newLayoutTemplate = GetParsedXml.getSmartSnapsAnalysisPhotoBookLayoutXML(Config.getPROD_CODE(), layout.F_TMPL_ID, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				return newLayoutTemplate != null;
			}

			@Override
			public void onPost(boolean result) {
				if (result) {
					try {
						if (newLayoutTemplate == null || newLayoutTemplate.getPages() == null || newLayoutTemplate.getPages().isEmpty()) return;
						SnapsPage newPage = newLayoutTemplate.getPages().get(0);
						PhotobookCommonUtils.changePageBGWithNewTemplate(newPage, getPageList(), getPageIndex());

						reloadEditPage();

						WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_updateBackground;
						if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_updateBackground;

						SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
								.appendPayload(WebLogConstants.eWebLogPayloadType.BACKGROUND, layout.F_TMPL_ID)
								.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
								.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
								.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				} else {
					showNetworkErrorAlert();
				}
			}
		});
	}

	private void changeLayoutWithMsg(Message msg) {
		if (msg == null || msg.obj == null || !(msg.obj instanceof XML_BasePage)) return;
		XML_BasePage xmlPage = (XML_BasePage) msg.obj;
		changeLayoutWithXmlPage(xmlPage);
	}

	private void changeLayoutWithXmlPage(final XML_BasePage layout) {
		ATask.executeVoidWithThreadPoolBooleanDefProgress(this, new ATask.OnTaskResult() {
			SnapsTemplate newLayoutTemplate = null;
			@Override
			public void onPre() {

			}

			@Override
			public boolean onBG() {
				newLayoutTemplate = GetParsedXml.getSmartSnapsAnalysisPhotoBookLayoutXML(Config.getPROD_CODE(), layout.F_TMPL_ID, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				if (newLayoutTemplate != null && newLayoutTemplate.getPages() != null && newLayoutTemplate.getPages().size() > 0) {
					PhotobookCommonUtils.saveMaskData(newLayoutTemplate);
				}

				return newLayoutTemplate != null;
			}

			@Override
			public void onPost(boolean result) {
				if (result) {
					try {
						if (newLayoutTemplate == null || newLayoutTemplate.getPages() == null || newLayoutTemplate.getPages().isEmpty()) return;
						SnapsPage newPage = newLayoutTemplate.getPages().get(0);
						if (isCover()) {
							PhotobookCommonUtils.changeCoverImageLayerWithNewTemplate(SmartRecommendBookPageEditActivity.this, newPage, getPageList(), 0, getTemplate());

							SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
							ArrayList<SnapsPage> coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
							if (coverList != null) {
								PhotobookCommonUtils.changeCoverImageLayerWithNewTemplate(SmartRecommendBookPageEditActivity.this, newPage, coverList, editRequestInfo.getCoverTemplateIndex(), getTemplate());
							}
						} else {
							PhotobookCommonUtils.changePageImageLayoutWithNewTemplate(SmartRecommendBookPageEditActivity.this, newPage, getPageList(), getPageIndex(), getTemplate());
						}

						reloadEditPage();
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				} else {
					showNetworkErrorAlert();
				}
			}
		});
	}

	private void showNetworkErrorAlert() {
		CNetStatus netStatus = CNetStatus.getInstance();
		if (!netStatus.isAliveNetwork(getActivity())) {
			if (errorAlert == null || !errorAlert.isShowing()) {
				errorAlert = MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.smart_snaps_analysis_network_disconnect_alert), null);
			}
		}
	}

	private void reloadEditPage() throws Exception {
		editControls.getPagerAdapter().setDataWithEditRequestInfo(editRequestInfo);
		editControls.getViewPager().setAdapter(editControls.getPagerAdapter());
		if (!isCover()) {
			editControls.getViewPager().setCurrentItem(getPageIndex()-1);
		}
	}

	private void startPhotoDraggingWithMsg(Message msg) {
		if (msg == null || dragNDropLayout == null) return;

		if (System.currentTimeMillis() - lastPageScrolledTime < 1000) return; //페이징 할때는 기존 페이지에서 롱 클릭한 걸 막는다.

		SmartRecommendBookEditDragImageInfo dragPhoto = (SmartRecommendBookEditDragImageInfo) msg.obj;

		dragNDropLayout.startDragging(dragPhoto);
	}

	public static final int HANDLE_MSG_CHANGE_BOTTOM_FRAGMENT_TO_LAYOUT = 0;
	public static final int HANDLE_MSG_CHANGE_LAYOUT = 1;
	public static final int HANDLE_MSG_START_PHOTO_DRAGGING = 2;
	public static final int HANDLE_MSG_START_IMAGE_SELECT_ACTIVITY = 3;
	public static final int HANDLER_MSG_CLICKED_LAYOUT_CONTROL = 4;
	public static final int HANDLE_MSG_CHANGE_BG = 5;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case HANDLER_MSG_INIT_CANVAS_MATRIX:
				initCanvasMatrix();
				break;
			//편집화면에서는 얼굴을 빨리 맞추도록 처리 하기 위해, 일부러 원본 이미지 업로드 처리를 하지 않도록 했다.
			case HANDLER_MSG_UPLOAD_THUMB_IMAGES:
				Object msgObj = msg.obj;
				if (msgObj != null && msgObj instanceof MyPhotoSelectImageData)
					SnapsOrderManager.uploadThumbImgOnBackground((MyPhotoSelectImageData)msgObj);
//				else
//					SnapsOrderManager.uploadThumbImgListOnBackground();
				break;
			case HANDLE_MSG_CHANGE_BOTTOM_FRAGMENT_TO_LAYOUT:
				replaceFragment(SELECT_LAYOUT_FRAGMENT);
				break;
			case HANDLE_MSG_START_PHOTO_DRAGGING:
				startPhotoDraggingWithMsg(msg);
				break;
			case HANDLE_MSG_CHANGE_LAYOUT:
				changeLayoutWithMsg(msg);
				break;
			case HANDLE_MSG_CHANGE_BG:
				changeBGWithMsg(msg);
				break;
			case HANDLE_MSG_START_IMAGE_SELECT_ACTIVITY:
				startImageSelectActivityForAddPhoto();
				break;
			case HANDLER_MSG_CLICKED_LAYOUT_CONTROL:
				handleClickedLayoutControlAfterRotatedImageView(msg);
				break;
		}
	}

	private void handleClickedLayoutControlAfterRotatedImageView(Message msg) {
		if (msg == null || msg.obj == null || !(msg.obj instanceof SnapsLayoutControl)) return;
		try {
			SnapsOrderManager.cancelCurrentImageUploadExecutor();

			OrientationManager.fixCurrentOrientation(getActivity());

			Intent in = new Intent(getActivity(), ImageSelectActivityV2.class);

			int recommendWidth = 0, recommendHeight = 0;
			SnapsLayoutControl control = (SnapsLayoutControl) msg.obj;
			Rect rect = ResolutionUtil.getEnableResolution(getTemplate().info.F_PAGE_MM_WIDTH, getTemplate().info.F_PAGE_PIXEL_WIDTH, control);
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
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

			getActivity().startActivityForResult(in, REQ_INSERT_PHOTO);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void startImageSelectActivityForAddPhoto() {
		SnapsOrderManager.cancelCurrentImageUploadExecutor();

		Intent intent = new Intent(getApplicationContext(), ImageSelectActivityV2.class);

		ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
				.setPageIndex(getPageIndex())
				.setHomeSelectProduct(Config.SELECT_MULTI_CHOOSE_TYPE)
				.setOrientationChanged(true).create();

		Bundle bundle = new Bundle();
		bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
		intent.putExtras(bundle);

		startActivityForResult(intent, REQ_PHOTO);

		WebLogConstants.eWebLogName logName = WebLogConstants.eWebLogName.photobook_annie_editdetail_clickAddphoto;
		if (isCover()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailcover_clickAddphoto;
		else if (isIndexPage()) logName = WebLogConstants.eWebLogName.photobook_annie_editdetailindex_clickAddphoto;

		SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(logName)
				.appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
				.appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(editRequestInfo != null ? editRequestInfo.getPageIndex() : -1))
				.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
	}
}
