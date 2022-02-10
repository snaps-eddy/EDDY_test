package com.snaps.mobile.activity.book;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.widget.TextView;

import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.storybook.IOnStoryDataLoadListener;
import com.snaps.common.storybook.StoryData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.SnapsAPI;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.activity.book.StoryBookDataManager.IOnPageMakeListener;
import com.snaps.mobile.activity.edit.view.CircleProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class StoryBookFragmentActivity extends SNSBookFragmentActivity implements IOnStoryDataLoadListener {
	private static final String TAG = StoryBookFragmentActivity.class.getSimpleName();
	private final int PROGRESS_PER_GET_TEMPLATE_PART = 10; // 템플릿 조회..
	private final int PROGRESS_PER_GET_DETAIL_INFO_PART = 40; // 나머지 상세 정보..
	private final int PROGRESS_PER_MAKE_PAGES = 50; // 페이지 제작....

	/***
	 * 1. download template 2. _canvasList story data 3. font download 4. cover setting 5. title setting
	 */
	@Override
	protected void initByType() {
		this.type = SNSBookFragmentActivity.TYPE_KAKAO_STORY;

		// 스토리 데이터 초기화.
		// 재편집인 경우 스토리 데이터를 가져오지 않는다.
		if (!IS_EDIT_MODE) {
			StoryBookDataManager dataManager = StoryBookDataManager.getInstance();
			if (dataManager != null) {
				templateId = dataManager.getTemplateId();
				productCode = dataManager.getProductCode();
			}
		}
	}

	@Override
	protected void onPageSelect(int index) {
		String prefix = getResources().getString(R.string.preview);
		String tailText = "";
		if (index == 0) {
			tailText = "(" + getString(R.string.cover) +")";

		} else if (index == 1) {
			tailText = "(" + getString(R.string.index) +")";//"(인덱스)";
		} else if (index == 2) {
			tailText = "(" + getString(R.string.inner_title_page) +")";//"(속표지)";
		} else {
			int pp = (index - 2) * 2 + 2;
			int totalPage = (_pageList.size() - 3) * 2 + 3;
			tailText = String.format("(%d,%d / %d p)", pp, ++pp, totalPage);
		}

		TextView titleView = (TextView) findViewById(R.id.btnTopTitle);
		titleView.setText(prefix + " " + tailText);
	}

	@Override
	protected void makeBookLayout() {
		// 스토리 데이터 가져오기..
		ATask.executeVoid(new OnTask() {
			boolean isSuccessDownload = false;

			@Override
			public void onPre() {
				CircleProgressView.getInstance(StoryBookFragmentActivity.this).load(CircleProgressView.VIEW_PROGRESS);
			}

			@Override
			public void onBG() {
				// 템플릿 다운로드..
				isSuccessDownload = downloadTemplate(templateId);
				// 폰트 다운로드..
			}

			@Override
			public void onPost() {
				if (isSuccessDownload) {
					CircleProgressView.getInstance(StoryBookFragmentActivity.this).setValue(PROGRESS_PER_GET_TEMPLATE_PART);
					StoryBookDataManager dataMan = StoryBookDataManager.getInstance();
					if (dataMan != null) {
						dataMan.setStoryDataLoadListener(StoryBookFragmentActivity.this);
						dataMan.requestStoriesDetail();
					}
				} else {
					MessageUtil.toast(StoryBookFragmentActivity.this, getString(R.string.kakao_book_make_err_template_download));
					finishActivity();
				}
			}
		});
	}

	@Override
	protected void getLoadSaveXML( final Activity activity ) {
		final String url = SnapsAPI.GET_API_SAVE_XML() + "&prmProjCode=" + Config.getPROJ_CODE();
		Dlog.d("getLoadSaveXML() url:" + url);

		ATask.executeVoid(new OnTask() {
			SnapsTemplate template = null;

			@Override
			public void onPre() {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				SnapsTimerProgressView.showProgress(activity,
						SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
						getString(R.string.templete_data_downloaing));
			}

			@Override
			public void onBG() {
				template = GetTemplateLoad.getTemplateByXmlPullParser(url, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				if (template != null)
					calcTextControl(template);

				String projectTitle = Config.getPROJ_NAME();

				StoryBookDataManager storyBookManager = StoryBookDataManager.getInstance();
				if (storyBookManager != null) {
					storyBookManager.setProjectTitle(projectTitle);
				}

				if (IS_EDIT_MODE) {
					String prmProjCode = Config.getPROJ_CODE();// "20150217004103";
					// 커버 색상을 구할려면 필
					templateId = Config.getTMPL_CODE();
					saveXMLPriceInfo = GetParsedXml.getProductPriceInfo(prmProjCode, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				}
			}

			@Override
			public void onPost() {
				if (template == null) {
					MessageUtil.toast(activity, getString(R.string.kakao_book_make_err_template_download));
					finishActivity();
				} else {
					setTemplate(template);
					SnapsTimerProgressView.destroyProgressView();

					checkBookPageCount();

					if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) requestNotifycation();
				}
			}
		});
	}


	// /***
	// * 스토리 데이터를 가져온다.
	// *
	// * @param startDate
	// * @param endDate
	// * @param commentCount
	// * @param photoCount
	// * @param listener
	// */
	void calcTemplate() {
		// 페이지가 많을 경우, 생성하는 데만해도 시간이 많이 소요 되어서, 프로그레스 처리를 세분화 처리 한다.
		ATask.executeVoid(new OnTask() {

			StoryBookDataManager dataManager = StoryBookDataManager.getInstance();

			@Override
			public void onPre() {
				CircleProgressView.getInstance(StoryBookFragmentActivity.this).setValue(PROGRESS_PER_GET_TEMPLATE_PART + PROGRESS_PER_GET_DETAIL_INFO_PART);

				if (dataManager != null) {
					dataManager.setOnPageMakeListener(new IOnPageMakeListener() {
						@Override
						public void update(final float per) {
							runOnUiThread(new Runnable() {
								public void run() {
									int value = (int) ((PROGRESS_PER_GET_TEMPLATE_PART + PROGRESS_PER_GET_DETAIL_INFO_PART) + (PROGRESS_PER_MAKE_PAGES * (per / 100.f)));
									CircleProgressView.getInstance(StoryBookFragmentActivity.this).setValue(value);
								}
							});
						}
					});
				}
			}

			@Override
			public void onPost() {
				loadFinish();
			}

			@Override
			public void onBG() {
				// 전체적으로 auraTextFontSize를 설정한다.
				for (SnapsPage p : multiTemplate.getPages()) {
					p.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
				}

				// 템플릿 만들다. 목차 정렬을 위해 페이지를 먼저 제작해야 한다.
				dataManager.setPageTemplate(multiTemplate);
				dataManager.setCoverTemplate(multiTemplate);
				dataManager.setIndexTemplate(multiTemplate);
				dataManager.setTitleTemplate(multiTemplate);

				// font download
				downloadFont(multiTemplate);
				// 페이지를 표시를 한다.
				setTemplate(multiTemplate);
			}
		});
	}


	@Override
	protected void setTemplate(SnapsTemplate template) {
		StoryBookDataManager storyBookMan = StoryBookDataManager.getInstance();
		if (storyBookMan != null) {
			String paperCode = storyBookMan.getPaperCode();
			if ( !StringUtil.isEmpty(paperCode) )
				template.info.F_PAPER_CODE = paperCode;
		}

		super.setTemplate(template);
	}

	@Override
	public void onStoryDetailLoadComplete() {
		// 레이아웃을 구성을 한다.
		calcTemplate();
	}

	@Override
	protected SNSBookInfo getSNSBookInfo() {
		if (IS_EDIT_MODE) {
			return createSNSBookInfoFromSaveXml();
		} else {
			StoryBookDataManager dataManager = StoryBookDataManager.getInstance();
			if (dataManager != null) {
				try {
					return dataManager.getInfo();
				} catch (Exception e) {
					SnapsAssert.assertException(this, e);
					Dlog.e(TAG, e);
				}
			}
		}
		return null;
	}

	@Override
	public void onStoryLoadFail(int errorCode) { StoryBookCommonUtil.showErrMsg(this, getApplicationContext(), errorCode); }

	@Override
	public void onStoryProfileLoadComplete() {}

	@Override
	public void onStoryListLoadComplete(ArrayList<StoryData> list) {}

	@Override
	public void onStoryLoadStateUpdate(int type, int current) {
		if (current <= 0) return;
		int value = 0;

		switch (type) {
		case IOnStoryDataLoadListener.PROGRESS_TYPE_GET_STORY_DETAIL_INFO:
			value = (int) ((PROGRESS_PER_GET_TEMPLATE_PART + PROGRESS_PER_GET_DETAIL_INFO_PART) * (current / 100.f));
			CircleProgressView.getInstance(StoryBookFragmentActivity.this).setValue(value);
			break;
		}
	}
}
