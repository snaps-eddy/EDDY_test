package com.snaps.mobile.utils.smart_snaps;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.interfacies.ISmartSnapImgDataAnimationState;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.SnapsViewVisibilityByScrollHandler;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import errorhandle.logger.Logg;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.DEFAULT_MATRIX_ANIMATION_TIME;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.MAX_SMART_SNAPS_PAGING_WAIT_TIME;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.TRANSPARENCY_PHOTO_CARD_ANIMATION_TIME;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.WAIT_FOR_ANIMATE_PREPARE_TIME;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.WAIT_FOR_ANIMATE_PREPARE_TIME_FOR_CALENDAR;

/**
 * Created by ysjeong on 2018. 1. 18..
 */

public class SmartSnapsAnimationHandleLooper extends Thread implements Parcelable, Serializable {
	private static final String TAG = SmartSnapsAnimationHandleLooper.class.getSimpleName();

	private static final long serialVersionUID = 5128105091738716354L;

	private final int CURRENT_PAGE_INDEX;
	private final int LAST_PAGE_INDEX;

	private transient SnapsHandler snapsHandler = null;
	private boolean isSuspend = false;
	private boolean isFirstLoad = false;

	private Set<MyPhotoSelectImageData> uploadReadyImageDataSet = null;

	private Object uploadReadyImageDataWaitSyncLocker = null;

	protected SmartSnapsAnimationHandleLooper(Parcel in) {
		CURRENT_PAGE_INDEX = in.readInt();
		LAST_PAGE_INDEX = in.readInt();
		isSuspend = in.readByte() != 0;
		isFirstLoad = in.readByte() != 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(CURRENT_PAGE_INDEX);
		dest.writeInt(LAST_PAGE_INDEX);
		dest.writeByte((byte) (isSuspend ? 1 : 0));
		dest.writeByte((byte) (isFirstLoad ? 1 : 0));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SmartSnapsAnimationHandleLooper> CREATOR = new Creator<SmartSnapsAnimationHandleLooper>() {
		@Override
		public SmartSnapsAnimationHandleLooper createFromParcel(Parcel in) {
			return new SmartSnapsAnimationHandleLooper(in);
		}

		@Override
		public SmartSnapsAnimationHandleLooper[] newArray(int size) {
			return new SmartSnapsAnimationHandleLooper[size];
		}
	};

	static SmartSnapsAnimationHandleLooper createNewLooperWithPageIndex(int pageIndex, SnapsHandler snapsHandler) {
		return new SmartSnapsAnimationHandleLooper(pageIndex, snapsHandler);
	}

	private SmartSnapsAnimationHandleLooper(int pageIndex, SnapsHandler snapsHandler) {
		this.setDaemon(true);
		this.CURRENT_PAGE_INDEX = pageIndex;
		this.LAST_PAGE_INDEX = getLastPageIndexWhereSmartImageExists();
		this.snapsHandler = snapsHandler;
		this.uploadReadyImageDataSet = new HashSet<>();
		this.uploadReadyImageDataWaitSyncLocker = new Object();
		this.isFirstLoad = SmartSnapsManager.getInstance().isFirstSmartSearching(); //최초 진입 시점에서 로딩하는 것인지
	}

	private void waitIfImageLoadingOnPageCanvas() {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		if (!smartSnapsManager.isFirstSmartSearching()) {
			return;
		}

		smartSnapsManager = SmartSnapsManager.getInstance();
		smartSnapsManager.waitIfPageCanvasImageLoadSyncLocker(CURRENT_PAGE_INDEX);

		try {
			Thread.sleep(Config.isCalendar() ? WAIT_FOR_ANIMATE_PREPARE_TIME_FOR_CALENDAR : WAIT_FOR_ANIMATE_PREPARE_TIME);
		} catch (InterruptedException e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public void run() {
		super.run();

		try {
			LinkedList<MyPhotoSelectImageData> imageDataList = getImageDataListIfShouldBeAnimate();
			if (imageDataList == null) {
				return;
			}

			waitIfImageLoadingOnPageCanvas(); //첫 페이지에서 전체 사진의 n% 올릴때까지 기다려 준다.

			if (isSuspend()) {
				return;
			}

			List<MyPhotoSelectImageData> copiedImgDataList = (List<MyPhotoSelectImageData>) imageDataList.clone();

			handleSmartSnapsAnimationWithImageList(imageDataList);

			waitIfExistUploadReadyImageData();

			waitIfActiveAnimation(copiedImgDataList);

			if (isSuspend()) {
				return;
			}

			if (isFirstLoad) {
				if (CURRENT_PAGE_INDEX == LAST_PAGE_INDEX) { //마지막 페이지
					sendAnimationFinishMsgWithDelay(DEFAULT_MATRIX_ANIMATION_TIME);
				} else { //스마트 서칭 중 다음페이지로 넘기기
					final long delay = Const_PRODUCT.isTransparencyPhotoCardProduct() ? TRANSPARENCY_PHOTO_CARD_ANIMATION_TIME : DEFAULT_MATRIX_ANIMATION_TIME;
					swipeNextPage(CURRENT_PAGE_INDEX, delay);
				}
			} else { //한장의 사진을 추가하는 경우
				sendAnimationFinishMsgWithDelay(DEFAULT_MATRIX_ANIMATION_TIME);
			}

			if (!isFirstLoad) {
				sendRefreshThumbnailMsg();
			}

		} catch (Exception e) {
			Dlog.e(TAG, e);
			sendExceptionMsg(e);
		}
	}

	private LinkedList<MyPhotoSelectImageData> getImageDataListIfShouldBeAnimate() throws Exception {
		LinkedList<MyPhotoSelectImageData> imageDataList = (LinkedList<MyPhotoSelectImageData>) getImageDataListOnPage(CURRENT_PAGE_INDEX);
		if (imageDataList == null) { //이럴 일이 생기면 안되는데.. 혹시나.
			sendAnimationFinishMsgWithDelay(0);
			return null;
		}

		boolean shouldSkipPage = isFirstLoad && imageDataList.isEmpty() && CURRENT_PAGE_INDEX < LAST_PAGE_INDEX;
		if (shouldSkipPage) {
			swipeNextPage(CURRENT_PAGE_INDEX, 0);
			return null;
		}

		boolean isEmptyRequest = !isFirstLoad && imageDataList.isEmpty();
		if (isEmptyRequest) {
			sendAnimationFinishMsgWithDelay(0);
			return null;
		}

		return imageDataList;
	}

	private void waitIfActiveAnimation(List<MyPhotoSelectImageData> imageDataList) {
		if (imageDataList == null) {
			return;
		}

		try {
			for (MyPhotoSelectImageData imageData : imageDataList) {
				SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
				ISmartSnapImgDataAnimationState listener = smartSnapsManager.getSmartImgAnimationListener(imageData);
				int waitCount = 0;
				int basicCount = 100;
				if (Const_PRODUCT.isDIYStickerProduct()) {
					basicCount = 30000;
				}

				while (isSinglePageImageUploading(imageData) || (listener != null && listener.isActiveAnimation())) {
					Thread.sleep(100);
					if (++waitCount > basicCount) {
						break;
					}
				}
			}

			imageDataList.clear();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private boolean isSinglePageImageUploading(MyPhotoSelectImageData imageData) {
		return Const_PRODUCT.isDIYStickerProduct() && SnapsOrderManager.isUploadingImageData(imageData);
	}

	private void handleSmartSnapsAnimationWithImageList(LinkedList<MyPhotoSelectImageData> imageDataList) {
		if (imageDataList == null || imageDataList.isEmpty()) {
			return;
		}

		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		smartSnapsManager.lockSmartSnapsAnimationImageListHandling();

		for (MyPhotoSelectImageData imageData : imageDataList) {
			sendAnimationHandleMsg(imageData);
		}

		try {
			while (!imageDataList.isEmpty() && !isSuspend()) {
				final MyPhotoSelectImageData imageData = imageDataList.poll();
				if (imageData == null) {
					continue;
				}

				switch (imageData.getSmartSnapsImgInfo().getSmartSnapsImgState()) {
					case READY:
						imageData.getSmartSnapsImgInfo().setSmartSnapImgDataAnimationStateListener(new ISmartSnapImgDataAnimationState() {
							@Override
							public void onRequestedAnimation() {
								try {
									if (imageData != null) {
										sendAnimationHandleMsg(imageData);
										setAnimationStateToFinish(imageData);
										removeUploadReadyImageData(imageData);
										SmartSnapsImgInfo smartSnapsImgInfo = imageData.getSmartSnapsImgInfo();
										if (smartSnapsImgInfo != null) {
											smartSnapsImgInfo.removeAnimationStateListener();
										}
										increaseProgressValue();
									}
								} catch (Exception e) {
									Dlog.e(TAG, e);
								}
							}

							@Override
							public void suspendAnimation() {
							} //TODO  리스너를 따로 만들어서 리펙토링 좀 하자

							@Override
							public boolean isActiveAnimation() {
								return false;
							}

							@Override
							public void setAnimationStateToStart() {
							}
						});
						addUploadReadyImageData(imageData);
						break;
					case RECEIVE_SMART_SNAPS_INFO:
						sendAnimationHandleMsg(imageData);
						setAnimationStateToFinish(imageData);
						increaseProgressValue();
						break;
					default:
						Dlog.w(TAG, "handleSmartSnapsAnimationWithImageList() default:"
								+ imageData.getSmartSnapsImgInfo().getSmartSnapsImgState());
						break;
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} finally {
			smartSnapsManager.notifySmartSnapsAnimationImageListHandling();
		}
	}

	private void setAnimationStateToFinish(MyPhotoSelectImageData imageData) {
		try {
			SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(imageData, SmartSnapsConstants.eSmartSnapsImgState.FINISH_ANIMATION);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void sendRefreshThumbnailMsg() {
		if (snapsHandler != null) {
			Message msg = new Message();
			msg.what = SmartSnapsAnimationHandler.HANDLE_MSG_REFRESH_THUMBNAIL;
			msg.arg1 = CURRENT_PAGE_INDEX;
			snapsHandler.sendMessage(msg);
		}
	}

	private void increaseProgressValue() {
		if (snapsHandler != null) {
			Message msg = new Message();
			msg.what = SmartSnapsAnimationHandler.HANDLE_MSG_INCREASE_PROGRESS;
			snapsHandler.sendMessageDelayed(msg, DEFAULT_MATRIX_ANIMATION_TIME);
		}
	}

	private void sendAnimationFinishMsgWithDelay(long delay) {
		if (snapsHandler != null) {
			snapsHandler.sendEmptyMessageDelayed(SmartSnapsAnimationHandler.HANDLE_MSG_FINISH, delay);
		}
	}

	private void sendExceptionMsg(Exception e) {
		if (snapsHandler != null) {
			Message msg = new Message();
			msg.what = SmartSnapsAnimationHandler.HANDLE_MSG_EXCEPTION;
			msg.obj = e;
			snapsHandler.sendMessage(msg);
		}
	}

	private void sendAnimationHandleMsg(MyPhotoSelectImageData imageData) {
		setAnimationStateToStart(imageData);

		if (snapsHandler != null) {
			Message msg = new Message();
			msg.what = SmartSnapsAnimationHandler.HANDLE_MSG_ANIMATION;
			msg.obj = imageData;
			snapsHandler.sendMessage(msg);
		}
	}

	private void setAnimationStateToStart(MyPhotoSelectImageData imageData) {
		if (imageData == null) {
			return;
		}
		try {
			SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
			ISmartSnapImgDataAnimationState listener = smartSnapsManager.getSmartImgAnimationListener(imageData);
			if (listener != null) {
				listener.setAnimationStateToStart();
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void addUploadReadyImageData(MyPhotoSelectImageData imageData) {
		synchronized (getUploadReadyImageDataSet()) {
			getUploadReadyImageDataSet().add(imageData);
		}
	}

	public void removeUploadReadyImageData(MyPhotoSelectImageData imageData) {
		if (getUploadReadyImageDataSet() == null) {
			return;
		}
		synchronized (getUploadReadyImageDataSet()) {
			if (getUploadReadyImageDataSet().contains(imageData)) {
				getUploadReadyImageDataSet().remove(imageData);
			}
		}

		checkAllImageUploaded();
	}

	private boolean isExistUploadReadyImageData() {
		synchronized (getUploadReadyImageDataSet()) {
			return !getUploadReadyImageDataSet().isEmpty();
		}
	}

	private void swipeNextPage(int pageIndex) {
		swipeNextPage(pageIndex, 0);
	}

	private void swipeNextPage(int pageIndex, long delay) {
		if (isSuspend()) {
			return;
		}
		if (snapsHandler != null) {
			Message msg = new Message();
			msg.what = SmartSnapsAnimationHandler.HANDLE_MSG_SWIPE;
			msg.arg1 = Math.min(LAST_PAGE_INDEX, pageIndex + 1);
			snapsHandler.sendMessageDelayed(msg, delay);
		}
	}

	private boolean waitIfExistUploadReadyImageData() throws Exception {
		if (isExistUploadReadyImageData()) {
			synchronized (getUploadReadyImageDataWaitSyncLocker()) {
				if (isExistUploadReadyImageData()) {
					if (isSuspend()) {
						return false;
					}
					try {
//                        Logg.y("waitIfExistUploadReadyImageData");
						getUploadReadyImageDataWaitSyncLocker().wait(MAX_SMART_SNAPS_PAGING_WAIT_TIME);
					} catch (InterruptedException e) {
						Dlog.e(TAG, e);
					}
//                    Logg.y("notify waitIfExistUploadReadyImageData");
					return true;
				}
			}
		}
		return false;
	}

	private void checkAllImageUploaded() {
		if (!isExistUploadReadyImageData()) {
			notifyUploadReadyImageDataSyncLocker();
		}
	}

	public void notifyUploadReadyImageDataSyncLocker() {
		try {
			synchronized (getUploadReadyImageDataWaitSyncLocker()) {
				getUploadReadyImageDataWaitSyncLocker().notify();
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private SparseArray<List<MyPhotoSelectImageData>> getSmartSnapsImageControlMap() {
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		return smartSnapsManager.getSmartSnapsImageControls();
	}

	private int getLastPageIndexWhereSmartImageExists() {
		try {
			SparseArray<List<MyPhotoSelectImageData>> integerLists = getSmartSnapsImageControlMap();
			if (integerLists == null || integerLists.size() == 0) {
				return 0;
			}

			Set<Integer> pageKeySet = new HashSet<>();
			for (int ii = 0; ii < integerLists.size(); ii++) {
				pageKeySet.add(integerLists.keyAt(ii));
			}

			if (pageKeySet.isEmpty()) {
				return 0;
			}

			int maxPage = Collections.max(pageKeySet);
			int minPage = Collections.min(pageKeySet);

			for (int index = maxPage; index >= minPage; index--) {
				List<MyPhotoSelectImageData> imageDataList = integerLists.get(index);
				if (imageDataList != null && !imageDataList.isEmpty()) {
					return index;
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return 0;
	}

	private List<MyPhotoSelectImageData> getImageDataListOnPage(int pageIndex) throws Exception {
		SparseArray<List<MyPhotoSelectImageData>> integerListMap = getSmartSnapsImageControlMap();
		if (integerListMap == null) {
			return null;
		}

		return integerListMap.get(pageIndex);
	}

	private boolean isSuspend() {
		return isSuspend;
	}

	void setSuspend() {
		isSuspend = true;
	}

	public Set<MyPhotoSelectImageData> getUploadReadyImageDataSet() {
		return uploadReadyImageDataSet;
	}

	public Object getUploadReadyImageDataWaitSyncLocker() {
		return uploadReadyImageDataWaitSyncLocker;
	}
}
