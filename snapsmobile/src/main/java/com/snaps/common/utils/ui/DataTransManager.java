package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import androidx.core.app.ActivityCompat;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;


public class DataTransManager {
	
	/**
	 * 인텐트로 주고 받지 못하는 데이터를 전달할 목적으로 사용합니다.
	 * 메모리 해제에 주의 해서 사용 할 것.
	 */
	private static volatile DataTransManager gInstance = null;
	
	private ArrayList<MyPhotoSelectImageData> arrPhotoImageDatas = null;
	private ArrayList<MyPhotoSelectImageData> arrTempPhotoImageDatas = null;    //사진 인화에서 임시로 사용할 목적으로

	private boolean isShownPresentPage = false;
	
	private ZoomViewCoordInfo zoomViewCoordInfo = null;

	private SnapsLayoutControl snapsLayoutControl = null;

	private ImageSelectImgDataHolder imageSelectDataHolder = null;    //이미지 선택 화면에서 사용

	/**
	 * 앱이 죽었는 데, 완전히 종료 되지 않았을 때, 앱을 완전히 종료 시키기 위해 사용한다.
	 */
	public static void notifyAppFinish(final Activity activity) {
		if (activity == null) return;

		SnapsLogger.sendExceptionLogWithLog("DataTransManager/notifyAppFinish", "called notifyAppFinish.");

		MessageUtil.alertnoTitleOneBtn(activity, activity.getString(R.string.abnormal_execution_please_restart), new ICustomDialogListener() {
			@Override
			public void onClick(byte clickedOk) {
				activity.moveTaskToBack(true);
				ActivityCompat.finishAffinity(activity);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}, 1000);
			}
		});
	}

	public static ArrayList<MyPhotoSelectImageData> getImageDataFromDataTransManager(Activity activity) {
		DataTransManager dtMan = DataTransManager.getInstance();
		if (dtMan == null) {
			DataTransManager.notifyAppFinish(activity);
			return null;
		}

		return dtMan.getPhotoImageDataList();
	}

	public static void createInstance(Context context) {
		if(gInstance ==  null) {
			synchronized(DataTransManager.class) {
				if(gInstance ==  null) {
					gInstance = new DataTransManager(context);
				}
			}
		}
	}
	
	public static DataTransManager getInstance() {
		SnapsAssert.assertNotNull(gInstance);
		return gInstance;
	}
	
	public static void releaseInstance() {
		if(gInstance != null)  {
			gInstance.releaseAllData();
		}
	}
	
	private DataTransManager(Context context) {
		arrPhotoImageDatas = new ArrayList<MyPhotoSelectImageData>();

		arrTempPhotoImageDatas = new ArrayList<MyPhotoSelectImageData>();

		zoomViewCoordInfo = new ZoomViewCoordInfo(context);
	}
	
	public ArrayList<MyPhotoSelectImageData> getPhotoImageDataList() {
		if (arrPhotoImageDatas == null)
			arrPhotoImageDatas = new ArrayList<>();

		return arrPhotoImageDatas;
	}

	public int getPhotoImageDataListCount() {
		return getPhotoImageDataList() != null ? getPhotoImageDataList().size() : 0;
	}

	public ArrayList<MyPhotoSelectImageData> getTempPhotoImageDataList() {
		if (arrTempPhotoImageDatas == null)
			arrTempPhotoImageDatas = new ArrayList<>();

		return arrTempPhotoImageDatas;
	}

	public void setPhotoImageDataList(ArrayList<MyPhotoSelectImageData> list) {
		if(arrPhotoImageDatas == null) 
			arrPhotoImageDatas = new ArrayList<MyPhotoSelectImageData>();
		
		synchronized(arrPhotoImageDatas) {
			if(arrPhotoImageDatas != null && !arrPhotoImageDatas.isEmpty())
				arrPhotoImageDatas.clear();
			
			if(list != null && !list.isEmpty()) {
				for(MyPhotoSelectImageData imgData : list) {
					MyPhotoSelectImageData copyData = new MyPhotoSelectImageData();
					copyData.set(imgData);
					arrPhotoImageDatas.add(copyData);
				}
			}
		}
	}

	public void setTempPhotoImageDataList(ArrayList<MyPhotoSelectImageData> list) {
		if(arrTempPhotoImageDatas == null)
			arrTempPhotoImageDatas = new ArrayList<MyPhotoSelectImageData>();

		synchronized(arrTempPhotoImageDatas) {
			if(arrTempPhotoImageDatas != null && !arrTempPhotoImageDatas.isEmpty())
				arrTempPhotoImageDatas.clear();

			if(list != null && !list.isEmpty()) {
				for(MyPhotoSelectImageData imgData : list) {
					MyPhotoSelectImageData copyData = new MyPhotoSelectImageData();
					copyData.set(imgData);
					arrTempPhotoImageDatas.add(copyData);
				}
			}
		}
	}

	public boolean isShownPresentPage() {
		return isShownPresentPage;
	}

	public void setShownPresentPage(boolean isShownPresentPage) {
		this.isShownPresentPage = isShownPresentPage;
	}

	public ZoomViewCoordInfo getZoomViewCoordInfo() {
		return zoomViewCoordInfo;
	}

	public void setZoomViewCoordInfo(ZoomViewCoordInfo zoomViewCoordInfo) {
		this.zoomViewCoordInfo = zoomViewCoordInfo;
	}

	public SnapsLayoutControl getSnapsLayoutControl() {
		return snapsLayoutControl;
	}

	public void setSnapsLayoutControl(SnapsLayoutControl snapsLayoutControl) {
		this.snapsLayoutControl = snapsLayoutControl;
	}

	public ImageSelectImgDataHolder getImageSelectDataHolder() {
		return imageSelectDataHolder;
	}

	public void setImageSelectDataHolder(ImageSelectImgDataHolder imageSelectDataHolder) {
		this.imageSelectDataHolder = imageSelectDataHolder;
	}

	public void cloneCurrentSelectedImageList() {
		DataTransManager dataTransManager = DataTransManager.getInstance();
		ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
		if (imageSelectManager != null) {
			ImageSelectImgDataHolder imageSelectDataHolder = imageSelectManager.getImageSelectDataHolder();
			if (imageSelectDataHolder != null) {
				ImageSelectImgDataHolder copyHolder = new ImageSelectImgDataHolder();
				copyHolder.cloneList(imageSelectDataHolder);
				dataTransManager.setImageSelectDataHolder(copyHolder);

				imageSelectDataHolder.clearAllDatas();
			}
		}
	}

	public static void releaseCloneImageSelectDataHolder() {
		if(gInstance ==  null || gInstance.imageSelectDataHolder == null) return;
		gInstance.imageSelectDataHolder.clearAllDatas();
		gInstance.imageSelectDataHolder = null;
	}

	public void releaseAllData() {
		if(gInstance ==  null) return;
		
		if(gInstance.arrPhotoImageDatas != null) {
			if(!gInstance.arrPhotoImageDatas.isEmpty())
				gInstance.arrPhotoImageDatas.clear();
			gInstance.arrPhotoImageDatas = null;
		}

		if(gInstance.arrTempPhotoImageDatas != null) {
			if(!gInstance.arrTempPhotoImageDatas.isEmpty())
				gInstance.arrTempPhotoImageDatas.clear();
			gInstance.arrTempPhotoImageDatas = null;
		}

		if(gInstance.snapsLayoutControl != null) {
			gInstance.snapsLayoutControl = null;
		}
	}
}
