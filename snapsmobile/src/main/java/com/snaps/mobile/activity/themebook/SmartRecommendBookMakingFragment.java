package com.snaps.mobile.activity.themebook;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsConfigManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.sound.SnapsSoundPlayer;
import com.snaps.common.utils.sound.data.SnapsSoundData;
import com.snaps.common.utils.sound.interfaceis.ISoundStateChangeListener;
import com.snaps.common.utils.sound.interfaceis.SnapsSoundConstants;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookMakingAnimationViews;
import com.snaps.mobile.utils.smart_snaps.analysis.data.SmartSnapsAnalysisTaskState;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisTaskImp;
import com.snaps.mobile.utils.smart_snaps.analysis.task.SmartRecommendBookTaskFactory;
import com.snaps.mobile.utils.smart_snaps.animations.SmartRecommendBookMakingAnimation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;
import font.FTextView;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.FIT_CENTER_FACE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_COVER_TEMPLATE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PAGE_BG_RES;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PAGE_TEMPLATE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_PROJECT_CODE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_RECOMMEND_TEMPLATE;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.eSmartSnapsAnalysisTaskType.UPLOAD_THUMBNAILS;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookMakingActivity.HANDLE_MSG_CANCEL_CONFIRM;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookMakingActivity.HANDLE_MSG_CHANGE_TITLE_TEXT;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookMakingActivity.HANDLE_MSG_CONSECUTIVE_UPDATE_PROGRESS_BAR;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookMakingActivity.HANDLE_MSG_FINISH_ACTIVITY;
import static com.snaps.mobile.activity.themebook.SmartRecommendBookMakingActivity.HANDLE_MSG_START_NEXT_ACTIVITY;


public class SmartRecommendBookMakingFragment extends Fragment implements View.OnClickListener, SmartSnapsAnalysisListener, ISoundStateChangeListener {
	private static final String TAG = SmartRecommendBookMakingFragment.class.getSimpleName();
	private SnapsHandler snapsHandler = null;
	private Map<SmartSnapsConstants.eSmartSnapsAnalysisTaskType, SmartSnapsAnalysisTaskState> taskState = null;
	private ProgressBar progressBar = null;
	private font.FTextView progressTextView = null;
	private FTextView cancelBtn = null, retryBtn = null;
	private CustomizeDialog errorAlert = null;
	private SmartRecommendBookMakingAnimation displayAnimation = null;
	private AtomicBoolean shouldBeBlockNextActivity = null;
	private ImageView soundIcon = null;
	private boolean isSoundVolumeOn = false;
	private boolean isFistLoad = false;

	private boolean isSuspendTask = false;
	private boolean isActivityFinished = false;
	private boolean isCompletedGetRecommendTemplate = false;

	public static SmartRecommendBookMakingFragment newInstance(SnapsHandler snapsHandler) {
		SmartRecommendBookMakingFragment fragment = new SmartRecommendBookMakingFragment();
		fragment.setSnapsHandler(snapsHandler);
		return fragment;
	}

	public void setSnapsHandler(SnapsHandler snapsHandler) {
		this.snapsHandler = snapsHandler;
	}

	public SmartRecommendBookMakingFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestChangeTitleText();

		startPhotoBookMakeTasks();

		isFistLoad = true;
	}

	private void startPhotoBookMakeTasks() {
		isSuspendTask = false;
		taskState = new HashMap<>();
		shouldBeBlockNextActivity = new AtomicBoolean(false); //동기화 문제때문에 작업 완료 시점이 두 번 연속 같은 수 있기 때문에

		createTaskStateMap();

		/*
		  템플릿 요청
		  썸네일 업로딩
		  얼굴 맞추기
		 */
		performTask(GET_PROJECT_CODE);
	}

	private void createTaskStateMap() {
		taskState = new HashMap<>();
		SmartSnapsConstants.eSmartSnapsAnalysisTaskType[] arTasks = SmartSnapsConstants.eSmartSnapsAnalysisTaskType.values();
		for (SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType : arTasks) {
			taskState.put(taskType, SmartSnapsAnalysisTaskState.createNewInstance());
		}
	}

	private void performTask(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
		if (isActivityFinished()) return;
		SmartSnapsAnalysisTaskImp projectCodeTask = SmartRecommendBookTaskFactory.createTask(getActivity(), this, taskType);
		if (projectCodeTask != null) {
			projectCodeTask.perform();

			SmartSnapsAnalysisTaskState task = getTaskStateWithType(taskType);
			if (task != null) {
				task.setTask(projectCodeTask);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			releaseAnimationView();

			SnapsSoundPlayer.finalizeInstance();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void cancelTasks() throws Exception {
		if (taskState == null) return;
		isSuspendTask = true;
		for (Map.Entry<SmartSnapsConstants.eSmartSnapsAnalysisTaskType, SmartSnapsAnalysisTaskState> entry : taskState.entrySet()) {
			SmartSnapsAnalysisTaskState task = entry.getValue();
			if (task != null) {
				SmartSnapsAnalysisTaskImp taskImp = task.getTask();
				if (taskImp != null) {
					taskImp.cancel();
				}
			}
		}
	}

	public void finishActivity() {
	    isActivityFinished = true;
    }

	private void releaseAnimationView() {
		if (displayAnimation != null)
			displayAnimation.releaseInstance();
	}

	@Override
	public void onProgress(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, int total, int complete) {
		if (total <= 0 || complete <= 0 || taskType == null || isSuspendTask) return;
		try {
			final int WEIGHT = taskType.getProgressWeight();

			float progress = complete/(float)total;
			updateProgressValue(taskType, (int) (WEIGHT * progress));

			updateProgressBar();
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(getActivity(), e);
		}
	}

	private void updateProgressValue(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, int progressValue) throws Exception {
		SmartSnapsAnalysisTaskState task = getTaskStateWithType(taskType);
		if (task != null) {
			task.setProgress(progressValue);
		}
	}

	private SmartSnapsAnalysisTaskState getTaskStateWithType(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
		return taskState != null && taskState.containsKey(taskType) ? taskState.get(taskType) : null;
	}

	@SuppressLint("DefaultLocale")
	private void updateProgressBar() throws Exception {
		int totalValue = 0;
		for (Map.Entry<SmartSnapsConstants.eSmartSnapsAnalysisTaskType, SmartSnapsAnalysisTaskState> entry : taskState.entrySet()) {
			SmartSnapsAnalysisTaskState task = entry.getValue();
			if (task != null) {
				totalValue += task.getProgress();
			}
		}

		if (progressBar != null) {
			progressBar.setProgress(totalValue);
		}

		if (progressTextView != null) {
			progressTextView.setText(String.format("%d%%", totalValue));
		}
	}


	@Override
	public void onCompleteTask(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
		if (SnapsConfigManager.isAutoLaunchProductMakingMode()) {
			onCompleteTaskForFastDevelop(taskType);
			return;
		}

		changeTaskStateToFinish(taskType);

		/*
		 * 1.프로젝트 코드 발급
		 * 2.썸네일 업로드
		 * 3.추천 레이아웃 요청/다운로드
		 * 4.커버 다운로드
		 * 5.배경 다운로드
		 * 6.얼굴 맞추기
		 */
		switch (taskType) {
			case GET_PROJECT_CODE:
				performTask(UPLOAD_THUMBNAILS);
				break;
			case UPLOAD_THUMBNAILS:
				consecutiveUpdateProgressBarForGetRecommendTemplate();

				performTask(GET_RECOMMEND_TEMPLATE);
				break;
			case GET_RECOMMEND_TEMPLATE:
				isCompletedGetRecommendTemplate = true;
				if (snapsHandler != null) snapsHandler.removeMessages(HANDLE_MSG_CONSECUTIVE_UPDATE_PROGRESS_BAR);
				performTask(GET_COVER_TEMPLATE);
				break;
			case GET_COVER_TEMPLATE:
				performTask(GET_PAGE_TEMPLATE);

				performTask(GET_PAGE_BG_RES);
				break;
			case GET_PAGE_BG_RES:
				performFaceFitCenterTaskIfReady();
				break;
		}

		if (checkAllTaskComplete()) {
			if (!shouldBeBlockNextActivity.get()) {
				shouldBeBlockNextActivity.set(true);
				goToNextActivity();
			}
		}
	}

	/**
	 * GET_RECOMMEND_TEMPLATE 이게 오래 걸리니까, 눈속임으로 프로그래스가 흘러가도록 처리 한다.(빅데이터팀 요청)
	 */
	private void consecutiveUpdateProgressBarForGetRecommendTemplate() {
		if (isCompletedGetRecommendTemplate) return;

		try {
			int expectCostTotalTimeSec = getExpectTimeSecForGetRecommendTemplate();
			int totalProgressUpdateCount = (int) Math.max(1, expectCostTotalTimeSec / 1.5f); //나누기를 해 주는 이유는 좀 여러번 나눠서 프로그래스바가 동작하게 하기 위함이다.

			final int PROGRESS_WEIGHT = GET_RECOMMEND_TEMPLATE.getProgressWeight(); //20;
			int updateValueAtOnce = Math.max(1, PROGRESS_WEIGHT / totalProgressUpdateCount);
			int updateTimeSecAtOnce = expectCostTotalTimeSec / totalProgressUpdateCount;

			updateProgressValue(1, updateValueAtOnce, updateTimeSecAtOnce);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void updateProgressValue(int count, int updateValueAtOnce, int updateTimeSecAtOnce) {
		if (isCompletedGetRecommendTemplate) return;

		try {
			int updateValue = updateValueAtOnce*count;
			if (updateValue > GET_RECOMMEND_TEMPLATE.getProgressWeight()) return;

			int updateProgressValue = (GET_PROJECT_CODE.getProgressWeight() + UPLOAD_THUMBNAILS.getProgressWeight()) + updateValue;

			if (progressBar != null) {
				progressBar.setProgress(updateProgressValue);
			}

			if (progressTextView != null) {
				progressTextView.setText(String.format("%d%%", updateProgressValue));
			}

			Message message = new Message();
			message.what = HANDLE_MSG_CONSECUTIVE_UPDATE_PROGRESS_BAR;
			message.obj = ++count;
			message.arg1 = updateValueAtOnce;
			message.arg2 = updateTimeSecAtOnce;

			if (snapsHandler != null)
				snapsHandler.sendMessageDelayed(message, updateTimeSecAtOnce*1000);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private int getExpectTimeSecForGetRecommendTemplate() {
		int totalPhotoCnt = 0;
		DataTransManager dataTransManager = DataTransManager.getInstance();
		if (dataTransManager != null) {
			totalPhotoCnt = dataTransManager.getPhotoImageDataListCount();
		}

		final float avgCostTimeOnce = totalPhotoCnt < 50 ? .5f : (totalPhotoCnt < 100 ? .4f : .3f); //150장이 20초 걸린댄다..재 보니까 좀 더 걸리는 것 같다.
		return (int) (totalPhotoCnt * avgCostTimeOnce);
	}

	public void onCompleteTaskForFastDevelop(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
		changeTaskStateToFinish(taskType);

		switch (taskType) {
			case GET_PROJECT_CODE:
				performTask(GET_RECOMMEND_TEMPLATE);
				performTask(GET_PAGE_BG_RES);
				break;
			case GET_RECOMMEND_TEMPLATE:
				performTask(GET_COVER_TEMPLATE);

				performTask(GET_PAGE_TEMPLATE);
				break;
			case GET_COVER_TEMPLATE:
				performTask(FIT_CENTER_FACE);
				break;
			case FIT_CENTER_FACE:
				goToNextActivity();
				break;
		}
	}

	private void goToNextActivity() {
		hideErrorAlert();

		if (snapsHandler != null)
			snapsHandler.sendEmptyMessage(HANDLE_MSG_START_NEXT_ACTIVITY);

		Dlog.d("goToNextActivity()");
	}

	//분석된 템플릿과 얼굴 좌표가 찍어진 이미지 리스트가 완성되면 조합하고 얼굴을 센터로 맞춰 준다.
	private void performFaceFitCenterTaskIfReady() {
		SmartSnapsAnalysisTaskState analysisTemplateTask = getTaskStateWithType(GET_RECOMMEND_TEMPLATE);
		if (analysisTemplateTask != null && analysisTemplateTask.isCompleteTask()) {
			SmartSnapsAnalysisTaskState thumbnailUploadTask = getTaskStateWithType(UPLOAD_THUMBNAILS);
			if (thumbnailUploadTask != null && thumbnailUploadTask.isCompleteTask()) {
				performTask(FIT_CENTER_FACE);
			}
		}
	}

	private void changeTaskStateToFinish(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
		onProgress(taskType, 1, 1); // 1/1이 100%니까 그냥 완료 상태로 바꾼다...
		SmartSnapsAnalysisTaskState task = getTaskStateWithType(taskType);
		if (task != null) {
			task.setCompleteTask(true);
		}
	}

	private synchronized boolean checkAllTaskComplete() {
		for (Map.Entry<SmartSnapsConstants.eSmartSnapsAnalysisTaskType, SmartSnapsAnalysisTaskState> entry : taskState.entrySet()) {
			SmartSnapsAnalysisTaskState task = entry.getValue();

			if (!task.isCompleteTask()) {
				return false;
			}
		}

		return true;
	}

	private void hideErrorAlert() {
		if (errorAlert != null && errorAlert.isShowing()) {
			errorAlert.dismiss();
		}
	}

	@Override
	public void onException(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, Exception e) {
		handleOnError(taskType, e != null ? e.toString() : "");
	}

	@Override
	public void onFailed(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, String msg) {
		handleOnError(taskType, msg);
	}

	private void handleOnError(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType, String msg) {
		try {
			stopPhotoDisplayAnimation();

			SnapsSoundPlayer.pauseSoundPlay();

			if (showNetworkErrorAlert()) {
				changeCancelBtnStateToContinue();
				cancelTasks();
			} else {
				cancelTasks();
				changeCancelBtnStateToContinue();
				if (isThumbnailExceptionType(taskType)) {
					showThumbnailExceptionAlert(msg);
				} else {
					if (Config.isDevelopVersion()) {
						MessageUtil.alertnoTitleOneBtn(getActivity(), "error : " + taskType + ", " + msg, null);
					} else {
						showAPIErrorAlert();
					}
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(getActivity(), e);
		}
	}

	private boolean isThumbnailExceptionType(SmartSnapsConstants.eSmartSnapsAnalysisTaskType taskType) {
		return taskType == SmartSnapsConstants.eSmartSnapsAnalysisTaskType.UPLOAD_THUMBNAILS;
	}

	private boolean showNetworkErrorAlert() {
		if (isActivityFinished()) return false;
		CNetStatus netStatus = CNetStatus.getInstance();
		if (!netStatus.isAliveNetwork(getActivity())) {
			if (errorAlert == null || !errorAlert.isShowing()) {
				errorAlert = MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.smart_snaps_analysis_network_disconnect_alert), null);
			}
			return true;
		}
		return false;
	}

	private void showAPIErrorAlert() {
		if (isActivityFinished()) return;
		if (errorAlert == null || !errorAlert.isShowing()) {
			errorAlert = MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.smart_snaps_recommend_book_error_while_making_msg), new ICustomDialogListener() {
				@Override
				public void onClick(byte clickedOk) {
					if (snapsHandler != null)
						snapsHandler.sendEmptyMessage(HANDLE_MSG_FINISH_ACTIVITY);
				}
			});
            errorAlert.setCancelable(false);
		}
	}

	private void showThumbnailExceptionAlert(String msg) {
		if (isActivityFinished()) return;
		if (errorAlert == null || !errorAlert.isShowing()) {
			String alertMsg = String.format(getString(R.string.thumbnail_upload_failed_msg_with_name), msg);

			errorAlert = MessageUtil.alertnoTitleOneBtn(getActivity(), alertMsg, null);
            errorAlert.setCancelable(false);
		}
	}

	private void changeCancelBtnStateToContinue() throws Exception  {
		cancelBtn.setVisibility(View.GONE);
		retryBtn.setVisibility(View.VISIBLE);
	}

	private void changeCancelBtnStateToCancel() throws Exception {
		cancelBtn.setVisibility(View.VISIBLE);
		retryBtn.setVisibility(View.GONE);
	}

	private void requestChangeTitleText() {
		Message msg = new Message();
		msg.what = HANDLE_MSG_CHANGE_TITLE_TEXT;

		String title = null;
		if (StringUtil.isEmptyAfterTrim(Config.getPROJ_NAME())) {
			title = getString(R.string.auto_recommand_making_photobook);
		} else {
			String projName = Config.getPROJ_NAME();
			if (projName != null && projName.length() > 12) {
				projName = projName.substring(0, 12) + "...";
			}

			title = String.format(getString(R.string.auto_recommand_making_photobook_with_format), projName);
		}
		msg.obj = title;
		if (snapsHandler != null) {
			snapsHandler.sendMessage(msg);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.smart_snaps_analysis_making_fragment, container, false);

		FTextView userNameView = (FTextView) v.findViewById(R.id.smart_snaps_analysis_making_activity_fragment_user_name_tv);
		String userName = SnapsLoginManager.getUserName(getActivity(), 15);
		if (!StringUtil.isEmpty(userName)) {
			userNameView.setText(String.format("by.%s", userName));
		}

		cancelBtn = (FTextView) v.findViewById(R.id.smart_snaps_analysis_making_activity_fragment_cancel_btn);
		cancelBtn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		cancelBtn.setOnClickListener(this);

		retryBtn = (FTextView) v.findViewById(R.id.smart_snaps_analysis_making_activity_fragment_retry_btn);
		retryBtn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		retryBtn.setOnClickListener(this);

		ImageView centerImageA = (ImageView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_center_a_iv);
		soundIcon = (ImageView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_sound_icon_iv);
		centerImageA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleSoundVolumeState();

				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_make_clickSound)
								.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
			}
		});

		ImageView centerImageB = (ImageView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_center_b_iv);
		centerImageB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleSoundVolumeState();

				SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_make_clickSound)
								.appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
			}
		});

		ImageView memoriesView = (ImageView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_memories_iv);

		TextView descTextViewA = (TextView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_desc_a_tv);
		TextView descTextViewB = (TextView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_desc_b_tv);

		SmartRecommendBookMakingAnimationViews animationViews = new SmartRecommendBookMakingAnimationViews.Builder()
				.setCenterViewA(centerImageA).setCenterViewB(centerImageB).setMemoriesImage(memoriesView)
				.setUserNameView(userNameView).setDescTextViewA(descTextViewA).setDescTextViewB(descTextViewB).create();

		displayAnimation = SmartRecommendBookMakingAnimation.createAnimationWithImageView(getActivity(), animationViews);

		progressBar = (ProgressBar) v.findViewById(R.id.smart_snaps_analysis_making_fragment_progress);
		progressTextView = (FTextView) v.findViewById(R.id.smart_snaps_analysis_making_fragment_progress_tv);
		return v;
	}

	private void toggleSoundVolumeState() {
		isSoundVolumeOn = !isSoundVolumeOn;
		if (isSoundVolumeOn) {
			setSoundStateToResume();
		} else {
			setSoundStateToPause();
		}
	}

	private void setSoundStateToPause() {
		if (soundIcon == null) return;
		soundIcon.setImageResource(R.drawable.img_sound_off);
		SnapsSoundPlayer.pauseSoundPlay();
	}

	private void setSoundStateToResume() {
		if (soundIcon == null) return;
		soundIcon.setImageResource(R.drawable.img_sound_on);
		SnapsSoundPlayer.resumeSoundPlay();
	}

	@Override
	public void onResume() {
		super.onResume();

		registerSoundStateListener();

		if (isFistLoad) {
			isFistLoad = false;
			startBGSound();

			startPhotoDisplayAnimation();
		} else {
			restartPhotoDisplayAnimation();

			SnapsSoundPlayer.resumeSoundPlay();
		}
	}

	private void registerSoundStateListener() {
		SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
		soundPlayer.setSoundStateChangeListener(this);
	}

	private void unregisterSoundStateListener() {
		SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
		soundPlayer.setSoundStateChangeListener(null);
	}

	private void startPhotoDisplayAnimation() {
		if (displayAnimation != null)
			displayAnimation.startAnimation();
	}

	private void restartPhotoDisplayAnimation() {
		if (displayAnimation != null){
			if (displayAnimation.isStop())
				displayAnimation.requestRestartAnimation();
		}
	}

	private void startBGSound() {
		if (isActivityFinished()) return;
		SnapsSoundPlayer soundPlayer = SnapsSoundPlayer.getInstance();
		SnapsSoundData snapsSoundData = new SnapsSoundData.Builder(getActivity())
				.setLocalFile(true).setLocalSoundName(getSoundNameByRandom()).setRepeat(true)
				.create();

		isSoundVolumeOn = true;

		soundPlayer.startSoundPlay(snapsSoundData);
		if (!soundPlayer.isPlayableRingerMode(getActivity()))
			toggleSoundVolumeState();
	}

	private SnapsSoundConstants.eSnapsLocalSoundName getSoundNameByRandom() {
		SnapsSoundConstants.eSnapsLocalSoundName[] soundNames = SnapsSoundConstants.eSnapsLocalSoundName.values();
		return soundNames[new Random().nextInt(soundNames.length-1)];
	}

	private boolean isActivityFinished() {
		return isActivityFinished || getActivity() == null || getActivity().isFinishing();
	}

	@Override
	public void onPause() {
		super.onPause();

		stopPhotoDisplayAnimation();

		SnapsSoundPlayer.pauseSoundPlay();

		unregisterSoundStateListener();
	}

	private void stopPhotoDisplayAnimation() {
		if (displayAnimation != null)
			displayAnimation.stopAllAnimations();
	}

	@Override
	public void onClick(View v) {
		UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

		if (v.getId() == R.id.smart_snaps_analysis_making_activity_fragment_cancel_btn) {
			if (snapsHandler != null) {
				snapsHandler.sendEmptyMessage(HANDLE_MSG_CANCEL_CONFIRM);
			}
		} else if (v.getId() == R.id.smart_snaps_analysis_making_activity_fragment_retry_btn) {
			performRetryTasks();
		}
	}

	private void performRetryTasks() {
		if (showNetworkErrorAlert()) return;

		try {
			isSuspendTask = false;
			changeCancelBtnStateToCancel();

			if (taskState == null) return;
			for (Map.Entry<SmartSnapsConstants.eSmartSnapsAnalysisTaskType, SmartSnapsAnalysisTaskState> entry : taskState.entrySet()) {
				SmartSnapsAnalysisTaskState task = entry.getValue();
				if (task != null && !task.isCompleteTask()) {
					SmartSnapsAnalysisTaskImp taskImp = task.getTask();
					if (taskImp != null) {
						taskImp.perform();
					}
				}
			}

			restartPhotoDisplayAnimation();

			SnapsSoundPlayer snapsSoundPlayer = SnapsSoundPlayer.getInstance();
			if (snapsSoundPlayer.isPlayableRingerMode(getActivity())) {
				SnapsSoundPlayer.resumeSoundPlay();
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsAssert.assertException(getActivity(), e);
		}
	}

	@Override
	public void onSoundStateChanged(eSoundState soundState) {
		if (soundState == null) return;
		try {
			switch (soundState) {
				case PAUSE:
					if (soundIcon != null) {
						soundIcon.setImageResource(R.drawable.img_sound_off);
					}
					isSoundVolumeOn = false;
					break;
				case START:
					if (soundIcon != null) {
						soundIcon.setImageResource(R.drawable.img_sound_on);
					}
					isSoundVolumeOn = true;
					break;
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}