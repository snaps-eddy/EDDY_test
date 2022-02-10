//package com.snaps.mobile.activity.google_style_image_selector.utils;
//
//import android.content.Context;
//
//import com.google.android.gms.common.images.Size;
//import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
//import com.snaps.mobile.activity.google_style_image_selector.datas.GooglePhotoStyleThumbnailSizeInfo;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoData;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoFragmentData;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
//import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
//import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
//import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
//
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//
//public class ImageSelectData {
//
////	private static volatile ImageSelectData gInstance = null;
//
//	private ImageSelectImgDataHolder imageSelectDataHolder = null;    //이미지 선택 화면에서 사용
//
//	private ArrayList<ImageSelectTrayCellItem> tempTrayCellItemList; //트레이 아이템 리스트
//
//	private ImageSelectTrayPageCountInfo pageCountInfo; //트레이 페이지 정보
//
//	private IImageSelectStateChangedListener selectStateChangedListener; //트레이나 Fragment에서 아이템을 클릭할때의 처리
//
//	private ImageSelectPhonePhotoFragmentData phonePhotoFragmentDatas; //폰 데이터 공유를 위해
//
//	private GooglePhotoStyleThumbnailSizeInfo googlePhotoStyleThumbnailSizeInfo; //썸네일 사이즈
//
//	private AtomicBoolean isCreatingPhonePhotoDataList = new AtomicBoolean(false);
//	private final Object photoDataListCreateLock = new Object();
//	private PhonePhotosLoader phonePhotosLoader = null;
//
////	public static void createInstance() {
////		if(gInstance ==  null) {
////			synchronized(ImageSelectData.class) {
////				if(gInstance ==  null) {
////					gInstance = new ImageSelectData();
////				}
////			}
////		}
////	}
//
//	public boolean isContainsInImageHolder(String imageKey) {
//		if (imageKey == null || imageKey.length() < 1) return false;
//
//		ImageSelectImgDataHolder selectImgDataHolder = getImageSelectDataHolder();
//		if (selectImgDataHolder != null) {
//			return selectImgDataHolder.isSelected(imageKey);
//		}
//
//		return false;
//	}
//
//	public ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH getCurrentUIDepth() {
//		if (phonePhotoFragmentDatas == null) return null;
//		return phonePhotoFragmentDatas.getCurrentUIDepth();
//	}
//
//	public boolean isCreatedPhonePhotoDataList() {
//		return phonePhotoFragmentDatas != null && phonePhotoFragmentDatas.isCreatedPhotoPhotosDataList();
//	}
//
//	public void startSyncCreatingPhotoDataList() {
//		isCreatingPhonePhotoDataList.set(true);
//	}
//
//	public void finishCreatingPhotoDataList() {
//		isCreatingPhonePhotoDataList.set(false);
//		synchronized (photoDataListCreateLock) {
//			photoDataListCreateLock.notifyAll();
//		}
//	}
//
//	public void waitIfCreateingPhotoDataList() {
//		if (isCreatingPhonePhotoDataList.get()) {
//			synchronized (photoDataListCreateLock) {
//				if (isCreatingPhonePhotoDataList.get()) {
//					try {
//						photoDataListCreateLock.wait();
//					} catch (InterruptedException e) {
//						Dlog.e(TAG, e);
//					}
//				}
//			}
//		}
//	}
//
//	public void suspendCreatingPhonePhotoData() {
//		if (phonePhotosLoader != null) {
//			phonePhotosLoader.setSuspend(true);
//			phonePhotosLoader.interrupt();
//		}
//	}
//
//	public void createPhonePhotoDatas(ImageSelectActivityV2 activty, PhonePhotosLoader.IPhonePhotoLoadListener loadListener) {
//		//이미 만들어 졋다면 취소
//		if (isCreatedPhonePhotoDataList() || activty == null) return;
//
//		if (phonePhotosLoader != null && phonePhotosLoader.getState() == Thread.State.RUNNABLE) {
//			try {
//				phonePhotosLoader.join();
//			} catch (InterruptedException e) {
//				Dlog.e(TAG, e);
//			}
//		}
//
//		phonePhotosLoader = new PhonePhotosLoader(activty);
//		phonePhotosLoader.setLoadListener(loadListener);
//		phonePhotosLoader.start();
//	}
//
////	public static ImageSelectData getInstance() {
////		if (gInstance ==  null)
////			createInstance();
////		return gInstance;
////	}
////
////	public static void releaseInstance() {
////		if(gInstance != null)  {
////			gInstance.releaseAllData();
////			gInstance = null;
////		}
////	}
//
//	public IImageSelectStateChangedListener getSelectStateChangedListener() {
//		return selectStateChangedListener;
//	}
//
//	public void setSelectStateChangedListener(IImageSelectStateChangedListener selectStateChangedListener) {
//		this.selectStateChangedListener = selectStateChangedListener;
//	}
//
//	public ArrayList<ImageSelectTrayCellItem> getTempTrayCellItemList() {
//		return tempTrayCellItemList;
//	}
//
//	//트레이 전체보기용 리스트를 일반 트레이 리스트로 전환해 줌.
//	public ArrayList<ImageSelectTrayCellItem> getConvertedTrayList(ArrayList<ImageSelectTrayCellItem> list) {
//		if (list == null) return null;
//
//		ArrayList<ImageSelectTrayCellItem> convertedList = new ArrayList<>();
//		for (ImageSelectTrayCellItem cellItem : list) {
//			if (cellItem == null) continue;
//
//			if (cellItem.getCellState() != ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE
//					&& cellItem.getCellState() != ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL
//					&& cellItem.getCellState() != ISnapsImageSelectConstants.eTRAY_CELL_STATE.PLUS_BUTTON) continue;
//
//			convertedList.add(cellItem);
//		}
//
//		return convertedList;
//	}
//
//	public ImageSelectPhonePhotoFragmentData getPhonePhotoFragmentDatas() {
//		if (phonePhotoFragmentDatas == null)
//			phonePhotoFragmentDatas = new ImageSelectPhonePhotoFragmentData();
//		return phonePhotoFragmentDatas;
//	}
//
//	public int getCurrentUIDepthThumbnailSize() {
//		ImageSelectPhonePhotoFragmentData photoFragmentData = getPhonePhotoFragmentDatas();
//		return photoFragmentData != null ?  photoFragmentData.getCurrentUIDepthThumbnailSize() : 0; //default
//	}
//
//	public void setCurrentUIDepthThumbnailSize(Context context, ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth, boolean isLandscapeMode) {
//		ImageSelectPhonePhotoFragmentData photoFragmentData = getPhonePhotoFragmentDatas();
//		if (photoFragmentData != null) {
//			photoFragmentData.setCurrentUIDepthThumbnailSize(ImageSelectUtils.getUIDepthOptimumThumbnailDimension(context, depth, isLandscapeMode));
//		}
//	}
//
//	public void setImageSelectPhonePhotoData(ImageSelectPhonePhotoData phonePhotoData) {
//		if (phonePhotoData == null) return;
//
//		ImageSelectPhonePhotoFragmentData phonePhotoFragmentDatas = getPhonePhotoFragmentDatas();
//		if (phonePhotoFragmentDatas != null) {
//			phonePhotoFragmentDatas.setPhonePhotoData(phonePhotoData);
//		}
//	}
//
//	public ImageSelectTrayPageCountInfo getPageCountInfo() {
//		if (pageCountInfo == null)
//			pageCountInfo = new ImageSelectTrayPageCountInfo();
//		return pageCountInfo;
//	}
//
//	public void setPageCountInfo(ImageSelectTrayPageCountInfo pageCountInfo) {
//		this.pageCountInfo = pageCountInfo;
//	}
//
//	public void cloneTrayCellItemList(ArrayList<ImageSelectTrayCellItem> trayCellItemList) {
//		if (trayCellItemList == null) return;
//
//		this.tempTrayCellItemList = (ArrayList<ImageSelectTrayCellItem>) trayCellItemList.clone();
//	}
//
//	public ImageSelectImgDataHolder getImageSelectDataHolder() {
//		if (imageSelectDataHolder == null)
//			imageSelectDataHolder = new ImageSelectImgDataHolder();
//
//		return imageSelectDataHolder;
//	}
//
//	public GooglePhotoStyleThumbnailSizeInfo getGooglePhotoStyleThumbnailSizeInfo() {
//		if (googlePhotoStyleThumbnailSizeInfo == null)
//			googlePhotoStyleThumbnailSizeInfo = new GooglePhotoStyleThumbnailSizeInfo();
//
//		return googlePhotoStyleThumbnailSizeInfo;
//	}
//
//	public Size getAnimationHolderDefaultSizeByUIDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth,
//													   ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType, int baseWidth, int baseHeight) {
//		GooglePhotoStyleThumbnailSizeInfo thumbnailSizeInfo = getGooglePhotoStyleThumbnailSizeInfo();
//		if (thumbnailSizeInfo == null) return null;
//		return thumbnailSizeInfo.getAnimationHolderDefaultSizeByUIDepth(uiDepth, holderType, baseWidth, baseHeight);
//	}
//
//	public void releaseAllData() {
////		if(gInstance ==  null) return;
//
//		if (phonePhotoFragmentDatas != null) {
//			phonePhotoFragmentDatas.releaseInstance();
//		}
//
//		if (imageSelectDataHolder != null) {
//			imageSelectDataHolder.clearAllDatas();
//			imageSelectDataHolder = null;
//		}
//
//		if (pageCountInfo != null) {
//			pageCountInfo = null;
//		}
//
//		if (tempTrayCellItemList != null) {
//			tempTrayCellItemList.clear();
//		}
//
//		if (selectStateChangedListener != null)
//			selectStateChangedListener = null;
//
//		if (tempTrayCellItemList != null) {
//			tempTrayCellItemList.clear();
//			tempTrayCellItemList = null;
//		}
//
//		if (googlePhotoStyleThumbnailSizeInfo != null) {
//			googlePhotoStyleThumbnailSizeInfo = null;
//		}
//	}
//}
