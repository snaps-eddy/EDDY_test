package com.snaps.mobile.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import androidx.core.app.NotificationCompat;
import android.util.TypedValue;
import android.view.View;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.net.CustomMultiPartEntity.ProgressListener;
import com.snaps.common.structure.photoprint.ImpUploadProject;
import com.snaps.common.structure.photoprint.SnapsXmlMakeResult;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.photoprint.exceptions.SnapsPhotoPrintSizeInfoException;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintProject;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.structure.SnapsHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;


public class SnapsPhotoUploader implements ISnapsHandler {
	private static final String TAG = SnapsPhotoUploader.class.getSimpleName();
	private final static String NOTI_CHANNEL_ID = "snapsPhotoUpload_channel";
	private final static String NOTI_CHANNEL_NAME = "snapsPhotoUpload";

	public static final int NOTIFICATION_ID_ORG_IMG_UPLOAD_FAILED = 1000;

	// ?????? ????????? ????????? bradcast??? ?????????.
	public static String SEND_UPLOADER_ACTION = "send_uploader_action";

	// save file
	final static String UPLOAD_DATA_FILENAME = "uploadfileName";
	final static String UPLOAD_COMPLETE_DATA_FILENAME = "uploadcompletefileName";

	private static final int RETRY_COUNT = 3;

	static volatile SnapsPhotoUploader instance = null;
	Context context = null;

	final public static int UPLOAD_CARTIMAGE_STEP = 1; // ??????????????? ????????? ??????
	final public static int UPLOAD_CAPTUREIMAGE_STEP = 2; // ??????????????? ????????? ??????
	final public static int UPLOAD_ORIGINIMAGE_STEP = 3; // ??????????????? ????????? ??????
	final public static int UPLOAD_PROJECTFILE_STEP = 4; // XML??? ????????? ??????..

	// state
	final public static int UPLOAD_READY = 0; // ????????? ?????? - ????????? ????????????...
	final public static int UPLOAD_START = 1; // ????????? ??????
	final public static int UPLOADING = 2; // ?????????
	final public static int UPLOAD_END = 3; // ????????? ???
	final public static int UPLOAD_CANCEL = 4;// ????????? ??????.
	final public static int UPLOAD_ERROR = 5;
	final public static int UPLOAD_ERROR_CAUSE_IMG_UPLOAD_ERROR = 6;

	final public static int UPLOAD_NONE_ERROR = 0; // ???????????? ??????..
	final public static int UPLOAD_NETWORK_ERROR = 10; // ???????????? ??????..
	final public static int UPLOAD_RETRY_ERROR = 11; // ???????????? ?????? ??????....
	final public static int UPLOAD_IMAGE_SIZE_INFO_ERROR = 12; //
	// final public static int UPLOAD_EXCEPTION_ERROR = 8; // ????????? ??????
	final public static int UPLOAD_CART_IMAGE_NOT_FOUND_ERROR = 20; // ?????????????????? //
																	// ??????.
	final public static int UPLOAD_ORIGIN_IMAGE_NOT_FOUND_ERROR = 21; // ???????????????
	 // ???????????????
																		// //
																		// ??????..
	final public static int UPLOAD_XML_ERROR = 22; // ????????? XML ??????
	final public static int UPLOAD_XML_CREATE_ERROR = 24; // XML??? ????????? ??????.

	final public static int UPLOAD_ORIGIN_IMAGE_UPLOAD_ERROR = 25; //?????? ????????? ????????? ??????

	final public static int REQUEST_UPLOAD_START = 0; // ????????? ?????? - ????????? ????????????...
	final public static int REQUEST_UPLOAD_PAUSE = 1; // ????????? ??????
	final public static int REQUEST_UPLOAD_RETRY = 2; // ?????????
	final public static int REQUEST_UPLOAD_CANCEL = 3; // ????????? ???
	final public static int REQUEST_UPLOAD_COMPLETE = 5; // ????????? ??????
	final public static int REQUEST_SAW_UPLOAD_FAILED_ORG_IMAGE_POPUP = 6; // ?????? ?????? ?????? ?????? ??????

	Queue<ImpUploadProject> mQueue = new LinkedList<ImpUploadProject>();
	Queue<ImpUploadProject> mCompleteWorks = new LinkedList<ImpUploadProject>();

	UploadWorkThread mWorkThread = new UploadWorkThread();

	int mCurrentState = UPLOAD_READY;
	int mCurrentErrorCode = UPLOAD_NONE_ERROR;

	NotificationManager mNM;
	int mNotiID = -999;
	boolean mIsPause = false;
	String mUserID = "";

	String currentProjCode = null;

	SnapsHandler snapsHandler = null;

	private SnapsPhotoUploader() {
		snapsHandler = new SnapsHandler(this);
		imgUploadExecutor = new SnapsPhotoPrintOrgImgUploadExecutor();
	}

	private SnapsPhotoPrintOrgImgUploadExecutor imgUploadExecutor = null;    //?????? ???????????? ??????..

	static public SnapsPhotoUploader getInstance(Context context) {

		if (instance == null) {
			synchronized (SnapsPhotoUploader.class) {
				if (instance == null) {
					instance = new SnapsPhotoUploader();
					instance.context = context;
					// ?????????????????? ??????....
					instance.mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					instance.readUploadData();
					instance.startThread();

				}
			}
		}

		return instance;
	}

	public void cleanInstace() {
		instance = null;
		context = null;
	}

	// ?????? ??????, ??????
	/**
	 * ????????? ?????? ??????..
	 * 
	 * @return
	 */
	private boolean saveQueueData() {
		synchronized (mQueue) {
			FileOutputStream outputStream;
			ObjectOutputStream os = null;
			try {
				outputStream = context.openFileOutput(UPLOAD_DATA_FILENAME, Context.MODE_PRIVATE);
				os = new ObjectOutputStream(outputStream);

				os.writeObject(mQueue);
				os.close();
				Dlog.d("saveQueueData()");
			} catch (FileNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (OutOfMemoryError oomE) {
				Dlog.e(TAG, oomE);
				try {
					if (os != null) {
						os.reset();
						os.close();
					}
				} catch (IOException e) {
					Dlog.e(TAG, e);
				}
				return false;
			} catch (IOException e) {
				Dlog.e(TAG, e);
				return false;
			}

			return true;
		}
	}

	/**
	 * ????????? ?????? ??????..
	 * 
	 * @return
	 */
	private boolean readQueueData() {
		synchronized (mQueue) {
			FileInputStream inputStream;
			try {
				File dataFile = context.getFileStreamPath(UPLOAD_DATA_FILENAME);
				if (!dataFile.exists()) return false;

				inputStream = context.openFileInput(UPLOAD_DATA_FILENAME);
				ObjectInputStream is = new ObjectInputStream(inputStream);
				mQueue = (Queue<ImpUploadProject>) is.readObject();
				is.close();
				Dlog.d("readQueueData()");
			} catch (FileNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (StreamCorruptedException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (IOException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (ClassNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (ClassCastException e) {
				Dlog.e(TAG, e);
				return false;
			}

			return true;
		}
	}

	/**
	 * ????????? ??? ?????? ??????..
	 * 
	 * @return
	 */
	private boolean saveCompleteData() {
		synchronized (mQueue) {
			FileOutputStream outputStream;

			try {
				outputStream = context.openFileOutput(UPLOAD_COMPLETE_DATA_FILENAME, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(outputStream);
				os.writeObject(mCompleteWorks);
				os.close();
				Dlog.d("saveCompleteData()");
			} catch (FileNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (IOException e) {
				Dlog.e(TAG, e);
				return false;
			}

			return true;
		}
	}

	/***
	 * ????????? ??? ?????? ?????? ??????
	 * 
	 * @return
	 */
	private boolean readCompleteData() {
		synchronized (mCompleteWorks) {
			FileInputStream inputStream;
			try {
				inputStream = context.openFileInput(UPLOAD_COMPLETE_DATA_FILENAME);
				ObjectInputStream is = new ObjectInputStream(inputStream);
				mCompleteWorks = (Queue<ImpUploadProject>) is.readObject();
				is.close();
				Dlog.d("readCompleteData()");
			} catch (FileNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (StreamCorruptedException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (IOException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (ClassNotFoundException e) {
				Dlog.e(TAG, e);
				return false;
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}

			return true;
		}
	}

	/***
	 * ???????????? ????????? ?????? ????????? ????????? ???????????? upload ????????? ???????????? ????????? ??????.
	 * 
	 * @return
	 */
	boolean saveUploadData() {
		return saveQueueData() && saveCompleteData();
	}

	boolean readUploadData() {
		try {
			File dataFile = context.getFileStreamPath(UPLOAD_DATA_FILENAME);
			if (isExceededValidPeriodFile(dataFile)) {
				dataFile.delete();
				return false;
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return readQueueData() && readCompleteData();
	}

	/***
	 * ???????????? ?????? ???????????? ????????? ???????????? ????????? ???????????? ??????..
	 * 
	 * @return
	 */
	boolean orderProcessRemove() {
		// ?????? ???????????? ????????????.
		for (ImpUploadProject project : mCompleteWorks) {
			String path = project.getCartThumbnail();
			if (path.length() > 0) {
				new File(path).delete();
			}
		}

		if (!mCompleteWorks.isEmpty())
			mCompleteWorks.clear();

		saveUploadData();
		return true;
	}

	/***
	 * ?????? ????????? ????????????.
	 */
	boolean allWorkCancel() {
		// ?????? ???????????? ????????????.
		for (ImpUploadProject project : mCompleteWorks) {
			String path = project.getCartThumbnail();
			if (path != null && path.length() > 0) {
				FileUtil.deleteFile(path);
			}
		}

		// ????????? ???????????? ????????????.
		for (ImpUploadProject project : mQueue) {
			String path = project.getCartThumbnail();
			if (path != null && path.length() > 0) {
				FileUtil.deleteFile(path);
			}
		}

		if (!mCompleteWorks.isEmpty())
			mCompleteWorks.clear();
		if (!mQueue.isEmpty())
			mQueue.clear();

		if (imgUploadExecutor != null) {
			imgUploadExecutor.suspendUpload();
			imgUploadExecutor.shutdown();
		}

		saveUploadData();
		return true;
	}

	private void initUploadWorkInfo(PhotoPrintProject projectInfo) {
		if (projectInfo == null) return;
		currentProjCode = projectInfo.getProjectCode();
		SnapsUploadFailedImageDataCollector.clearHistory(currentProjCode);
	}

	/*************
	 * ?????????
	 * ***********/
	class UploadWorkThread extends Thread {
		private static final String TAG = "UploadThread";
		boolean mIsWaiting = false;
		boolean mIsForceWating = false; // ???????????? ?????? ??????.

		@Override
		public void run() {
			while (true) {
				try {
					Dlog.d("UploadWorkThread run()");
					pauseWorker();

					pauseThreadIfnecessary();

					// ????????? work??? ?????????. ?????? ????????? ????????? ?????? ?????????. ???????????????...
					ImpUploadProject project = pop();// mQueue.poll();
					if (project != null) {
						Dlog.d("UploadWorkThread working");
						SnapsLogger.appendOrderLog("start photoPrint work!", true);

						currentProjCode = project.getProjectCode();

						// ???????????? ??????...
						// retryCount ??????...
						// network check
						CNetStatus net_status = CNetStatus.getInstance();
						switch (net_status.getNetType(context)) {
						case CNetStatus.NET_TYPE_WIFI:
						case CNetStatus.NET_TYPE_3G:
						case CNetStatus.NET_TYPE_WIBRO:

							break;
						case CNetStatus.NET_TYPE_NONE:
							// ???????????? ????????? ?????? ??????...
							pauseWorker();

							SnapsLogger.appendOrderLog("network none while upload to snaps photo print.");
							break;
						}

						// ???????????? ????????? ?????? ??????...
						pauseWorker();

						SnapsLogger.appendOrderLog("try snaps Photo print thumbnail");

						orderProcess2_thumbUpload(project); //????????? ?????? ??? ???????????? ???????????? ?????? ???????????? ?????? ??????

						project.setRetryCount(0);

						SnapsLogger.appendOrderLog("succeed Photo print thumbnail");

						// ???????????? ????????? ?????? ??????...
						pauseWorker();

						/*
						 * // ??????????????? ??????????????? ???????????? ????????? ?????????.. // ??????????????? ???????????? ?????????. if (orderProcess3_pageUpload(project)) { continue; }
						 */
						SnapsLogger.appendOrderLog("try Photo print org img");

						// ?????????????????? ?????????.
						if (!orderProcess4_orgImgUpload(project)) {
							continue;
						}

						Dlog.d("UploadWorkThread run() completed photo print org image upload.");

						project.setRetryCount(0);
						// ???????????? ????????? ?????? ??????...
						pauseWorker();

						SnapsLogger.appendOrderLog("succeed Photo print org img");

						SnapsLogger.appendOrderLog("try Photo print xml files");

						// xml????????? ?????????.
						if (!orderProcess5_prjUpload(project)) {
							continue;
						}

						SnapsLogger.appendOrderLog("succeed Photo print xml files");

						// ?????? ????????? ????????? queue?????? ???????????? ?????? arrayList??? ?????????...
						mCompleteWorks.add(poll());
					}
					Dlog.d("UploadWorkThread run() end");
				} catch (Exception e) {
					Dlog.e(TAG, e);
					SnapsLogger.appendOrderLog("exception snaps photo uploader UploadWorkThread : " + e.toString());
					// save??? ??????.
				} finally {
					saveUploadData();
				}
			}
		}

		private void pauseThreadIfnecessary() {
			if (size() != 0) {
				return;
			}
			synchronized (mWorkThread) {
				try {
					Dlog.d("pauseThreadIfnecessary()");
					mIsWaiting = true;
					if (getCompletePhotoCount() > 0 && (getTotalPhotoCount() <= getCompletePhotoCount())) {
						showUploadCompleteNotification();
					}
					// ?????? ????????? ????????????.
					orderProcessRemove();
					wait();
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}
		}
	}

	private void showUploadCompleteNotification() {
		Dlog.d("showUploadCompleteNotification()");
		if (SnapsUploadFailedImageDataCollector.isExistFailedImageData(currentProjCode)) {
			SnapsOrderManager.reportErrorLog("snaps photo print image upload error", SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE);

			showOrgImgUploadFailedNotification();
		} else {
			sendUploadStateInfo(UPLOAD_END, getTotalPhotoCount(), getCompletePhotoCount());
			showNotification(true, R.string.photo_upload_complete);
		}
	}

	/***
	 * ????????? worker Thread??? ????????? ?????? ??????..
	 */
	protected void pauseWorker() {

		synchronized (mWorkThread) {
			if (!mIsPause)
				return;
			mIsPause = false;
			try {

				mWorkThread.mIsWaiting = true;
				mWorkThread.wait();
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

	/****
	 * ????????? ??????..
	 */
	public int size() {
		synchronized (mQueue) {
			return mQueue.size();
		}
	}

	/***
	 * ????????? ???????????? ????????? ??????.
	 * 
	 * @return
	 */
	public ImpUploadProject pop() {
		synchronized (mQueue) {
			try {
				return mQueue.peek();
			} catch (Exception e) {
				Dlog.e(TAG, e);
                mQueue.remove();
				return null;
			}
		}
	}

	public ImpUploadProject poll() {
		synchronized (mQueue) {
			try {
				return mQueue.poll();
			} catch (Exception e) {
				Dlog.e(TAG, e);
				return null;
			}
		}
	}

	/***
	 * object??? ????????? ???????????? ??????..
	 * 
	 * @param project
	 * @return
	 */
	public boolean remove(ImpUploadProject project) {
		synchronized (mQueue) {
			return mQueue.remove(project);
		}
	}

	/***
	 * ?????? ???????????? ?????? ??????..
	 * 
	 * @param object
	 */
	public void enQueue(ImpUploadProject object) {
		synchronized (mQueue) {
			mQueue.add(object);
		}
	}

	String getUserID() {

		mUserID = SnapsLoginManager.getUUserNo(context);

		if (mUserID == null || mUserID.equals(""))
			mUserID = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO, "");

		return mUserID;
	}

	/***
	 * ????????? ???????????? ??????..
	 * 
	 * @param p
	 * @return
	 */
//	boolean addWork(SnapsPhotoPrintProject p) {
//		if (p != null) {
//			enQueue(p);
//			saveUploadData();
//			return true;
//		}
//		return false;
//	}

    boolean addWork(PhotoPrintProject p) {
        if (p != null) {
            enQueue(p);
            saveUploadData();
            return true;
        }
        return false;
    }

	void startThread() {
		if (mWorkThread.getState() == Thread.State.NEW) {
			mWorkThread.start();
			Dlog.d("startThread() start");
			return;
		}
		synchronized (mWorkThread) {
			if (mWorkThread.mIsWaiting) {
				try {
					Dlog.d("startThread() start mWorkThread.mIsWaiting");
					mWorkThread.mIsWaiting = false;
					mWorkThread.notify();
					// ????????? ????????? ????????? ??????....
					if (mCurrentErrorCode < 10 && getTotalPhotoCount() > 0)
						sendUploadStateInfo(UPLOAD_START, getTotalPhotoCount(), getCompletePhotoCount());
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
			}
		}
	}

	private boolean isExceededValidPeriodFile(File file) {
		if (file == null || !file.exists()) return false;

		//5?????? ????????? ?????? ??????.
		Calendar validDate = Calendar.getInstance();
		validDate.add(Calendar.DAY_OF_MONTH, -5);

		//????????? ?????? ?????? ???
		Date lastModDate = new Date(file.lastModified());

		return lastModDate.before(validDate.getTime());
	}


	/***
	 * work Thread??? ????????? ?????? ??????
	 */

	/***
	 * ?????????????????? ????????? ??????...
	 * 
	 * @param project
	 * @return
	 */
	boolean orderProcess2_thumbUpload(ImpUploadProject project) {
		Dlog.d("orderProcess2_thumbUpload()");

		// ????????? ????????? ????????? ??????
		if (project.getProcessStep() > UPLOAD_CAPTUREIMAGE_STEP || (project.getProcessStep() == UPLOAD_CAPTUREIMAGE_STEP && project.getProcessSubStep() == -1))
			return true;

		// ????????? ???????????? ???????????? ??????...
		if (mCompleteWorks.size() == 0)
			sendUploadStateInfo(UPLOAD_START, getTotalPhotoCount(), getCompletePhotoCount());
		project.setProcessStep(UPLOAD_CAPTUREIMAGE_STEP, 0);

		String fileName = project.getCartThumbnail();

		File file = new File(fileName);
		if (file == null || !file.exists()) {
			return false;
		}

		if (fileName != null) {
			GetMultiPartMethod.SnapsImageUploadRequestData requestData = new GetMultiPartMethod.SnapsImageUploadRequestData.Builder()
					.setUserId(getUserID())
					.setFileName(fileName)
					.setListener(null)
					.setAlbumType("O")
					.setPrjCode(project.getProjectCode())
					.setInterfaceLogListener(SnapsInterfaceLogDefaultHandler.createDefaultHandler()).create();

			String message = GetMultiPartMethod.getPageThumbImageUpload(requestData);
			if (message == null) return false;

			String[] returnValue = message.replace("||", "|").split("\\|");
			if (returnValue[0].indexOf("SUCCESS") >= 0) {
				// ???????????? ??????..
				project.setProcessStep(UPLOAD_CAPTUREIMAGE_STEP, -1);
				saveQueueData();
				return true;
			} else {
				SnapsLogger.appendOrderLog("snapsPhotoUploader failed make thumbnail : " + message);
			}
		}
		return false;
	}

	/***
	 * ?????????????????? ????????? ??????...
	 * 
	 * @param project
	 * @return
	 */
	boolean orderProcess4_orgImgUpload(ImpUploadProject project) {
		Dlog.d("orderProcess4_orgImgUpload()");
		// ????????? ????????? ????????? ??????
		if (project.getProcessStep() > UPLOAD_ORIGINIMAGE_STEP || (project.getProcessStep() == UPLOAD_ORIGINIMAGE_STEP && project.getProcessSubStep() == -1))
			return true;

		try {
			startOrgImgUpload(project);

			// ???????????? ??????...
			project.setProcessStep(UPLOAD_ORIGINIMAGE_STEP, -1);
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("snaps photo print upload exception : " + e.toString());
			sendUploadError(project, UPLOAD_NETWORK_ERROR);
			return false;
		}

		return true;
	}

	private SnapsPhotoPrintOrgImgUploadListener photoPrintOrgImgUploadListener = new SnapsPhotoPrintOrgImgUploadListener() {
		@Override
		public void onPhotoPrintOrgImgUploadResult(ePhotoPrintOrgImgUploadResult uploadResult, SnapsPhotoPrintOrgImgUploadResultData uploadResultData) {
			int uploadedImgCnt = 0, imageId = 0;
			ImpUploadProject project = null;
			if (uploadResultData != null) {
				SnapsPhotoPrintUploadImageData imageData = uploadResultData.getImageData();
				if (imageData != null) {
					imageId = imageData.getImageId();
					project = imageData.getProject();
				}
				uploadedImgCnt = uploadResultData.getFinishedCnt();
			}

			try {
				switch (uploadResult) {
					case START:
						Dlog.d("onPhotoPrintOrgImgUploadResult() start photo print img upload");
						break;
					case RESULT_IS_SUCCESS:
					case IMG_KIND_IS_UPLOADED:
						Dlog.d("onPhotoPrintOrgImgUploadResult() success photo print img upload : " + imageId);
						uploadNextOrgImage(uploadedImgCnt, project);
						break;
					case COMPLETED:
						Dlog.d("onPhotoPrintOrgImgUploadResult() completed photo print img upload");
						imgUploadExecutor.finishUploadSyncLock();
						break;
					case RESULT_IS_EMPTY:
					case RESULT_IS_FAIL:
						Dlog.e(TAG, "onPhotoPrintOrgImgUploadResult() failed photo print img upload : " + imageId);
						handleOrgImageUploadFailed(imageId, project);
						uploadNextOrgImage(uploadedImgCnt, project);
						break;
					case EXCEPTION:
					case SUSPENDED:
						Dlog.e(TAG, "onPhotoPrintOrgImgUploadResult() suspended photo print img upload : " + uploadResult.toString());
						imgUploadExecutor.finishUploadSyncLock();
						break;
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	};

	private void startOrgImgUpload(ImpUploadProject project) throws Exception {
		Dlog.e(TAG, "startOrgImgUpload() start photo print org image upload.");
		imgUploadExecutor.setSnapsPhotoPrintOrgImgUploadListener(photoPrintOrgImgUploadListener);
		imgUploadExecutor.startUploadImages(project);
	}

	private void handleOrgImageUploadFailed(int imageId, ImpUploadProject project) throws Exception {
		//???????????? ???????????? ????????? ??????????????? ????????????.
		collectErrorImageData(imageId, project);

		removeErrorImageData(imageId, project);
	}

	private void removeErrorImageData(int imageId, ImpUploadProject project) throws Exception {
		project.removeImageDataWithImageId(imageId);
	}

	private void collectErrorImageData(int imageId, ImpUploadProject project) throws Exception {
		PhotoPrintData photoPrintData = project.getPhotoPrintDataWithImageId(imageId);
		if (photoPrintData == null) return;

		SnapsUploadFailedImageDataCollector.addUploadFailedImageData(project.getProjectCode(), photoPrintData.getMyPhotoSelectImageData());
	}

	private void uploadNextOrgImage(int index, ImpUploadProject project) {
		project.setProcessStep(UPLOAD_ORIGINIMAGE_STEP, index);
		sendUploadStateInfo(UPLOADING, getTotalPhotoCount(), getCompletePhotoCount());
		saveQueueData();
	}

	private void handlePhotoException(SnapsXmlMakeResult xmlMakeResult, ImpUploadProject project) throws Exception {
		PhotoPrintData photoPrintData = xmlMakeResult.getPhotoPrintData();
		if (photoPrintData != null) {
			MyPhotoSelectImageData imageData = photoPrintData.getMyPhotoSelectImageData();
			if (imageData != null) {
				handleOrgImageUploadFailed((int) imageData.IMAGE_ID, project);

				sendUploadError(project, UPLOAD_IMAGE_SIZE_INFO_ERROR);
			}
		}
	}

	private void handleXMlMakeFailed(SnapsXmlMakeResult xmlMakeResult, ImpUploadProject project) {
		if (xmlMakeResult == null) return;

		Exception exception = xmlMakeResult.getException();
		if (exception != null) {
			SnapsLogger.appendOrderLog("snapsPhotoUploader failed upload make xml error : " + exception.toString());

			if (exception instanceof SnapsPhotoPrintSizeInfoException) {
				try {
					handlePhotoException(xmlMakeResult, project);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				return;
			}
		}

		sendUploadError(project, UPLOAD_XML_CREATE_ERROR);
	}

	/***
	 * xml????????? ????????? ??????...
	 * 
	 * @param project
	 * @return
	 */
	boolean orderProcess5_prjUpload(ImpUploadProject project) {
		Dlog.d("orderProcess5_prjUpload()");
		// ????????? ????????? ????????? ??????

		project.setProcessStep(UPLOAD_PROJECTFILE_STEP, 0);

		try {
			SnapsLogger.appendOrderLog("start Photoprint Make Xml. (orderProcess5_prjUpload)", true);

			String order = project.getOrderCode();

			File serviceUploadFileDir = Config.getServiceUploadFileDir();
			if (serviceUploadFileDir == null) throw new SnapsIOException("failed make service uplaod file folder");

			FileUtil.deleteFolderInFiles(serviceUploadFileDir);// ?????? ??? ?????? ??????

            String appVersionString = SystemUtil.getAppVersion(context);
            Config.initAppVersion(appVersionString);// app version ??????

			File saveXML = Config.getSERVICE_UPLOAD_FILE("save.xml");
			SnapsXmlMakeResult saveXMLMakeResult = project.makeSaveXML(saveXML.getAbsolutePath());
			if (saveXMLMakeResult != null && saveXMLMakeResult.isSuccess()) {
				saveXML = saveXMLMakeResult.getXmlFile();
			} else {
				handleXMlMakeFailed(saveXMLMakeResult, project);
				return false;
			}

			File orderXML = Config.getSERVICE_UPLOAD_FILE("auraOrder.xml");
			SnapsXmlMakeResult orderXMLMakeResult = project.makeAuraOrderXML(orderXML.getAbsolutePath());
			if (orderXMLMakeResult != null && orderXMLMakeResult.isSuccess()) {
				orderXML = orderXMLMakeResult.getXmlFile();
			} else {
				handleXMlMakeFailed(orderXMLMakeResult, project);
				return false;
			}

			File optionXML = Config.getSERVICE_UPLOAD_FILE("imgOption.xml");
			SnapsXmlMakeResult optionXMLMakeResult = project.makeOptionXML(optionXML.getAbsolutePath());
			if (optionXMLMakeResult != null && optionXMLMakeResult.isSuccess()) {
				optionXML = optionXMLMakeResult.getXmlFile();
			} else {
				handleXMlMakeFailed(optionXMLMakeResult, project);
				return false;
			}

			if (saveXML == null || orderXML == null || optionXML == null || !saveXML.exists() || !orderXML.exists() || !optionXML.exists()) {
				SnapsLogger.appendOrderLog("snapsPhotoUploader failed upload : UPLOAD_XML_CREATE_ERROR");
				sendUploadError(project, UPLOAD_XML_CREATE_ERROR);
				return false;
			}

			String uType = project instanceof PhotoPrintProject && ( (PhotoPrintProject)project ).isEditMode() ? "m" : "i";

			HttpResponse response = GetMultiPartMethod.getProejctUpload2(getUserID(), order, "O", project.getProjectCode(), saveXML, orderXML, optionXML, uType, new ProgressListener() {
				@Override
				public void transferred(long num, long total) {

				}
			}, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != 200) {
				Dlog.e(TAG,"orderProcess5_prjUpload() snaps photo print xml upload failed => " + statusCode);
				// ???????????? ??????...
				sendUploadError(project, UPLOAD_NETWORK_ERROR);

				SnapsLogger.appendOrderLog("snapsPhotoUploader failed upload statusCode : " + statusCode);
				return false;
			}

			String message = "";
			if (response != null) {
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				message = FileUtil.convertStreamToString(is);
			}

			String[] returnValue = message.replace("||", "|").split("\\|");
			Dlog.d("orderProcess5_prjUpload() userID:" + getUserID() + ", " + "order:" + order + ", message:" + message);
			if (returnValue[0].indexOf("SUCCESS") >= 0) {
				// ?????? ????????? ??????.

				if (order.equalsIgnoreCase("146001")) {
				}


				project.setProcessStep(UPLOAD_PROJECTFILE_STEP, -1);
				saveQueueData();

				// ?????? ????????? ???????????????.. ??????..
				if (mQueue.size() == 1) {
					sendUploadStateInfo(UPLOAD_END, getTotalPhotoCount(), getCompletePhotoCount());
				}

				return true;
			} else {
				SnapsLogger.appendOrderLog("snapsPhotoUploader failed upload error msg : " + message);
				sendUploadError(project, UPLOAD_XML_ERROR);
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("snapsPhotoUploader failed upload exception : " + e.toString());
			sendUploadError(project, UPLOAD_NETWORK_ERROR);
		}
		return false;
	}

	/***
	 * ?????? ????????? ???????????? ??????..
	 * 
	 * @return
	 */
	int getTotalPhotoCount() {

		int totalCount = 0;

		synchronized (mQueue) {
			Iterator<ImpUploadProject> it = mQueue.iterator();
			while (it.hasNext()) {
				ImpUploadProject impUploadProject = (ImpUploadProject) it.next();
				totalCount += impUploadProject.getItemCount();
			}
		}

		for (ImpUploadProject project : mCompleteWorks) {
			totalCount += project.getItemCount();
		}

		Dlog.d("getTotalPhotoCount() result:" + totalCount);

		return totalCount;

	}

	int getCompletePhotoCount() {
		int completeCount = 0;
		synchronized (mQueue) {
			Iterator<ImpUploadProject> it = mQueue.iterator();
			while (it.hasNext()) {
				ImpUploadProject impUploadProject = (ImpUploadProject) it.next();
				completeCount += impUploadProject.getUploadComleteCount();
			}
		}
		for (ImpUploadProject project : mCompleteWorks) {
			completeCount += project.getUploadComleteCount();
		}

		Dlog.d("getCompletePhotoCount result:" + completeCount);

		return completeCount;
	}

	//TODO   ?????? ????????? ??????....??????????????? ????????????.
	private void showOrgImgUploadFailedNotification() {//??????????????????..
		if (StringUtil.isEmpty(currentProjCode)) return;

		int failedCount = SnapsUploadFailedImageDataCollector.getFailedImageDataCount(currentProjCode);
		if (failedCount < 1)
			return;

		mCurrentState = UPLOAD_ERROR_CAUSE_IMG_UPLOAD_ERROR;

		if (mNotiID != -999)
			mNM.cancel(mNotiID);

		// expanded notification
		CharSequence title = context.getText(R.string.snaps);

		CharSequence text = String.format(context.getString(R.string.photo_print_org_img_upload_failed_noti_msg), failedCount);

		Notification notification = null;

		try {
			Intent orgImgUploadFailedIntent = new Intent(context, RenewalHomeActivity.class);
			orgImgUploadFailedIntent.putExtra("orgImgUploadFailedProjCode", currentProjCode);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, orgImgUploadFailedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			int smallIconRes = R.drawable.ic_status_fail;
			int largeIconRes = R.drawable.ic_status_fail_large;
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
				smallIconRes = R.drawable.ic_status_fail_new;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				NotificationChannel notificationChannel = new NotificationChannel(
						NOTI_CHANNEL_ID, NOTI_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
				notificationChannel.enableLights(true);
				notificationChannel.enableVibration(true);
				mNM.createNotificationChannel(notificationChannel);
			}

			Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), largeIconRes );
			int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, context.getResources().getDisplayMetrics());
			largeIcon = Bitmap.createScaledBitmap( largeIcon, size, size, false );
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTI_CHANNEL_ID)
					.setContentIntent(contentIntent)
					.setSmallIcon( smallIconRes )
					.setAutoCancel(true)
					.setContentTitle(title)
					.setContentText(text)
					.setChannelId(NOTI_CHANNEL_ID)
					.setLargeIcon( largeIcon );
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) builder.setColor( 0xFF000000 );
			notification = builder.build();

			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N ) {
				int smallIconId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
				if (smallIconId != 0)
					notification.contentView.setViewVisibility(smallIconId, View.INVISIBLE);
			}

		} catch (Exception e) {
			Dlog.e(TAG, e);
			return;
		}

		mNM.notify(NOTIFICATION_ID_ORG_IMG_UPLOAD_FAILED, notification);
		mNotiID = NOTIFICATION_ID_ORG_IMG_UPLOAD_FAILED;

		//?????????????????? ??? ?????? ??? ?????????, ?????? ???????????????..
		if (snapsHandler != null) {
			snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_SHOW_ORG_IMG_UPLOAD_FAILED_BOTTOM_MSG_VIEW, 3000);
		}
	}

	private String getCartUrl() {
		String naviBarTitle = "";
		int _cart_count = Setting.getInt(context, Const_VALUE.KEY_CART_COUNT);
		String cartCount = Integer.toString(_cart_count);
		try {
			naviBarTitle = URLEncoder.encode(context.getString(R.string.cart), "utf-8");
		} catch (UnsupportedEncodingException e) {
			Dlog.e(TAG, e);
		}
		return SnapsTPAppManager.getCartListUrl(context, naviBarTitle, cartCount);
	}

	private void showNotification(boolean isSucess, int resId) {
		if (mNotiID != -999)
			mNM.cancel(mNotiID);
		// expanded notification
		CharSequence title = context.getText(R.string.snaps);
		CharSequence text = context.getText(resId);

		Notification notification = null;

		try {
			Intent homeIntent = new Intent(context, RenewalHomeActivity.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			PendingIntent contentIntent = null;
			if (resId == R.string.upload_starting)// ????????? ??????
				contentIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			else if (resId == R.string.photo_upload_complete) { // ????????? ??????
				Intent intent = RenewalHomeActivity.getIntent(context, context.getString(R.string.cart), getCartUrl());
				contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			} else if (resId == R.string.photo_upload_failed)
				contentIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			else if (resId == R.string.upload_canceled)// ????????? ???
				contentIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            int smallIconRes = isSucess ? R.drawable.ic_status : R.drawable.ic_status_fail;
            int largeIconRes = isSucess ? R.drawable.ic_status_large : R.drawable.ic_status_fail_large;
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
                smallIconRes = isSucess ? R.drawable.ic_status_new : R.drawable.ic_status_fail_new;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				NotificationChannel notificationChannel = new NotificationChannel(
						NOTI_CHANNEL_ID, NOTI_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
				notificationChannel.enableLights(true);
				notificationChannel.enableVibration(true);
				mNM.createNotificationChannel(notificationChannel);
			}

            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), largeIconRes );
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, context.getResources().getDisplayMetrics());
            largeIcon = Bitmap.createScaledBitmap( largeIcon, size, size, false );
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTI_CHANNEL_ID)
                    .setContentIntent(contentIntent)
                    .setSmallIcon( smallIconRes )
					.setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(text)
					.setChannelId(NOTI_CHANNEL_ID)
                    .setLargeIcon( largeIcon );
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) builder.setColor( 0xFF000000 );
            notification = builder.build();

            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N ) {
                int smallIconId = context.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
                if (smallIconId != 0) {
					if (notification.contentView != null) //OS 7.0 ??? ??????..-_-;;
						notification.contentView.setViewVisibility(smallIconId, View.INVISIBLE);
				}
            }

		} catch (Exception e) {
			Dlog.e(TAG, e);
			return;
		}

		if (notification != null) {
			mNM.notify(resId, notification);
			mNotiID = resId;
		}
	}

	/**************************
	 * client => uploader ??????.
	 */

//	/***
//	 * ????????? ????????? ???????????? ????????? ??????????????? ??????
//	 *
//	 * @param p
//	 * @return
//	 */
//	public boolean addProject(SnapsPhotoPrintProject p) {
//		boolean isStart = addWork(p);
//		if (isStart && mWorkThread.mIsWaiting) {
//			startThread();
//		}
//		return isStart;
//	}

    public boolean addProject(PhotoPrintProject p) {
        boolean isStart = addWork(p);
        if (isStart && mWorkThread.mIsWaiting) {
			initUploadWorkInfo(p);

            startThread();
        }
        return isStart;
    }

	public boolean requestUploadProcess(int kind) {
		switch (kind) {

		case REQUEST_UPLOAD_RETRY: // ????????? ???????????? = 3
			ImpUploadProject project = pop();
			if (project != null) {
				int t = project.getRetryCount();
				project.setRetryCount(++t);
			}
			break;

		case REQUEST_UPLOAD_START: // ????????? ?????? = 1
			startThread();
			break;
		case REQUEST_UPLOAD_PAUSE: // ????????? ?????? = 2
			pauseWorker();

			break;
		case REQUEST_UPLOAD_CANCEL: // ????????? ?????? = 4 // ????????? ??????????????? ????????????...
			// ???????????? ?????? ????????? ?????? ????????????.
			allWorkCancel();
			// startThread();
			// ????????? ????????? ???????????? ?????????.
			sendUploadStateInfo(UPLOAD_CANCEL, 0, 0);

			break;
		case REQUEST_SAW_UPLOAD_FAILED_ORG_IMAGE_POPUP:
			NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			nMgr.cancelAll();

			SnapsUploadFailedImageDataCollector.clearHistory(currentProjCode);

			sendUploadStateInfo(UPLOAD_END, 0, 0);
			break;

		default:
			break;
		}

		return true;
	}

	public boolean requestUploadProgressInfo() {
		if (mCurrentState == UPLOAD_ERROR_CAUSE_IMG_UPLOAD_ERROR) {
			;
		} else if (mCurrentState < UPLOAD_ERROR) // ???????????????
			sendUploadStateInfo(mCurrentState, getTotalPhotoCount(), getCompletePhotoCount());
		else if (mCurrentState >= UPLOAD_ERROR) // error?????????
			sendUploadError2(mCurrentErrorCode);

		return true;
	}

	public boolean isFinishedUpload() {
		return mCurrentState == UPLOAD_END;
	}

	public void initState() {
		mCurrentState = UPLOAD_READY;
	}

	/**********************************
	 * uploader => activity??? ??????..
	 */

	/***
	 * ????????? ?????? ????????? ??????????????????...
	 * 
	 */
	void sendUploadError(ImpUploadProject prj, int errorCode) {
		SnapsLogger.appendOrderLog("snapsPhotoUploader/sendUploadError");
		mCurrentState = UPLOAD_ERROR;
		mCurrentErrorCode = errorCode;

		// retry ????????? ?????? ?????????.
		int retryCnt = prj.getRetryCount();

		// ???????????? ?????????????????? ??????....
		mIsPause = true;

		// ???????????? ????????? ???????????? ???????????? ????????? ????????????.
		if (retryCnt > RETRY_COUNT) {
			// ???????????? ??????????????????.. ???????????? ????????? ????????? ??????????????? ?????????.
			if (errorCode >= 20)
				sendUploadError2(errorCode);
			else {
				mCurrentErrorCode = UPLOAD_RETRY_ERROR;
				sendUploadError2(UPLOAD_RETRY_ERROR);
			}
		} else
			sendUploadError2(errorCode);

		// ??????????????? 20 ???????????? ???????????? ????????? work??? ????????????.
		if (mCurrentErrorCode >= 20)
			allWorkCancel();
	}

	/***
	 * ????????? ?????? ????????? ????????????.
	 * 
	 * @param state
	 * @param completeCount
	 */
	void sendUploadStateInfo(int state, int totalCount, int completeCount) {

		// ?????? ????????? ??????...
		mCurrentState = state;
		if (mCurrentState != UPLOAD_ERROR && mCurrentState != UPLOAD_ERROR_CAUSE_IMG_UPLOAD_ERROR)
			mCurrentErrorCode = UPLOAD_NONE_ERROR;

		// ????????? ????????? ????????????
		if ((state == UPLOAD_START) && mNotiID != R.string.upload_starting) {
			showNotification(true, R.string.upload_starting);
		} else if (state == UPLOAD_CANCEL && mNotiID != R.string.upload_canceled) {
			// ????????? ????????? ?????????..
			showNotification(true, R.string.upload_canceled);
			// mCurrentState = UPLOAD_READY;
		}

		Intent broadcast = new Intent(SEND_UPLOADER_ACTION);
		broadcast.putExtra("cmd", 0);
		broadcast.putExtra("state", state);
		broadcast.putExtra("completeCount", completeCount);
		broadcast.putExtra("totalCount", totalCount);
		broadcast.putExtra("projCode", currentProjCode);

		context.sendBroadcast(broadcast);

	}

	/***
	 * ???????????? ??????????????? ????????? ??????...
	 * 
	 */
	void sendUploadError2(int errCode) {
		// ?????? ????????? ??????...
		mCurrentState = UPLOAD_ERROR;

		if (mNotiID != R.string.photo_upload_failed)
			showNotification(false, R.string.photo_upload_failed);

		Intent broadcast = new Intent(SEND_UPLOADER_ACTION);
		broadcast.putExtra("cmd", 1);
		broadcast.putExtra("errCode", errCode);
		broadcast.putExtra("completeCount", getCompletePhotoCount());
		broadcast.putExtra("totalCount", getTotalPhotoCount());
		broadcast.putExtra("projCode", currentProjCode);
		context.sendBroadcast(broadcast);

		if (errCode >= 20) //???????????? ??????
			SnapsOrderManager.reportErrorLog("snaps photo print upload error : " + errCode, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
	}

	protected class ProgressInfo {
		int mState = 0;
		int mCompleteCount = 0;
		int mTotalCount = 0;

		ProgressInfo(int state, int complete, int total) {
			this.mState = state;
			this.mCompleteCount = complete;
			this.mTotalCount = total;
		}
	}

	/**
	 * ??????????????? ????????? cart ?????????,???????????? ?????????
	 */
    static public boolean uploadNewPhotoPrintXmlOnly( Activity act, PhotoPrintProject project, String userNo ) {

		SnapsLogger.appendOrderLog("start Photoprint Make Xml.(uploadNewPhotoPrintXmlOnly)", true);

        String order = project.getOrderCode();
        String uploadPath = Config.getExternalCacheDir(act) + "/temp_uploadfiles/";
        File f_uploadPath = new File(uploadPath);
        if (!f_uploadPath.exists()) // thumb path ????????? ????????? ?????????.
            f_uploadPath.mkdirs();

        FileUtil.deleteFolderInFiles(f_uploadPath);// ?????? ??? ?????? ??????

		File orderXML = null, saveXML = null, optionXML = null;
		SnapsXmlMakeResult orderXMLMakeResult = project.makeAuraOrderXML(uploadPath + "auraOrder.xml");
		if (orderXMLMakeResult != null && orderXMLMakeResult.isSuccess()) {
			orderXML = orderXMLMakeResult.getXmlFile();
		}

		SnapsXmlMakeResult saveXMLMakeResult = project.makeSaveXML(uploadPath + "save.xml");
		if (saveXMLMakeResult != null && saveXMLMakeResult.isSuccess()) {
			saveXML = saveXMLMakeResult.getXmlFile();
		}

		SnapsXmlMakeResult optionXMLMakeResult = project.makeOptionXML(uploadPath + "imgOption.xml");
		if (optionXMLMakeResult != null && optionXMLMakeResult.isSuccess()) {
			optionXML = optionXMLMakeResult.getXmlFile();
		}

        String message = GetMultiPartMethod.getProejctUpload(userNo, order, "O", project.getProjectCode(), saveXML, orderXML, optionXML, "m", new ProgressListener() {
            @Override
            public void transferred(long num, long total) {

            }
        }, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

        String[] returnValue = message.replace("||", "|").split("\\|");
        if (returnValue[0].indexOf("SUCCESS") >= 0) {
            return true;
        } else {
			SnapsLogger.sendLogOrderException(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML, returnValue[0]);
		}

		return false;
    }

	/***
	 * ?????????????????? ????????? ??????...
	 * 
	 * @return
	 */
	static public boolean thumbUpload(String path, String prjCode, String userNO) {
		String fileName = path;
		if (fileName != null) {
			GetMultiPartMethod.SnapsImageUploadRequestData requestData = new GetMultiPartMethod.SnapsImageUploadRequestData.Builder()
					.setUserId(userNO)
					.setFileName(fileName)
					.setListener(null)
					.setAlbumType("O")
					.setPrjCode(prjCode)
					.setInterfaceLogListener(SnapsInterfaceLogDefaultHandler.createDefaultHandler()).create();

			String message = GetMultiPartMethod.getPageThumbImageUpload(requestData);
			if (message == null) return false;

			String[] returnValue = message.replace("||", "|").split("\\|");
			if (returnValue[0].indexOf("SUCCESS") >= 0) {
				// ???????????? ??????..
				return true;
			}
		}
		return false;
	}

	public String getCurrentProjCode() {
		return currentProjCode;
	}

	private static final int HANDLE_MSG_SHOW_ORG_IMG_UPLOAD_FAILED_BOTTOM_MSG_VIEW = 1;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case HANDLE_MSG_SHOW_ORG_IMG_UPLOAD_FAILED_BOTTOM_MSG_VIEW :
				Intent broadcast = new Intent(SEND_UPLOADER_ACTION);
				broadcast.putExtra("cmd", 1);
				broadcast.putExtra("errCode", UPLOAD_ORIGIN_IMAGE_UPLOAD_ERROR);
				broadcast.putExtra("projCode", currentProjCode);
				broadcast.putExtra("completeCount", getCompletePhotoCount());
				broadcast.putExtra("totalCount", getTotalPhotoCount());
				context.sendBroadcast(broadcast);
				break;
		}
	}
}
