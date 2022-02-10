package com.snaps.mobile.activity.themebook.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.transformations.RotateTransformation;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookEditDragImageInfo;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmartRecommendBookEditorBottomPhotoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = SmartRecommendBookEditorBottomPhotoListAdapter.class.getSimpleName();
	private static int PIXEL_OFFSET_OF_DRAG_START;

	private ArrayList<MyPhotoSelectImageData> imageList;

	private boolean isLandscapeMode = false;
	private SnapsCommonResultListener<SmartRecommendBookEditDragImageInfo> itemDragListener = null;
	private Activity activity = null;

	private float lastTouchY = 0;
	private static final int INVALID_POINTER_ID = 1;
	private int mActivePointerId = INVALID_POINTER_ID;
	private boolean isLockTouchEvent = false;

	public SmartRecommendBookEditorBottomPhotoListAdapter(Activity activity, boolean isLandscapeMode) {
		this.activity = activity;

		PIXEL_OFFSET_OF_DRAG_START = -UIUtil.convertDPtoPX(activity, 10);

		notifyAllImageList();
	}

	public void notifyAllImageList() {
		setLandscapeMode(isLandscapeMode);
		setData(getImageList());
	}

	public void setItemDragListener(SnapsCommonResultListener<SmartRecommendBookEditDragImageInfo> itemDragListener) {
		this.itemDragListener = itemDragListener;
	}

	public void setLandscapeMode(boolean landscapeMode) {
		isLandscapeMode = landscapeMode;
	}

	public void setData(ArrayList<MyPhotoSelectImageData> newList) {
		if (newList == null) return;

		initCoverItemList();

        notifyData(newList);
	}

	private ArrayList<MyPhotoSelectImageData> getImageList() {
		Set<String> imagePathSet = new HashSet<>();
		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
		ArrayList<MyPhotoSelectImageData> allAddedImageList = smartSnapsManager.getAllAddedImageList();
		ArrayList<MyPhotoSelectImageData> copiedImageList = new ArrayList<>();
		if (allAddedImageList != null) {
			for (MyPhotoSelectImageData orgImgData : allAddedImageList) {
				if (orgImgData == null) continue;

				MyPhotoSelectImageData copiedImageData = new MyPhotoSelectImageData();
				copiedImageData.weakCopy(orgImgData);

				if (!imagePathSet.contains(copiedImageData.PATH)) {
					imagePathSet.add(copiedImageData.PATH);
					copiedImageList.add(copiedImageData);
				}
			}
		}
		return copiedImageList;
	}

	public void notifyData(ArrayList<MyPhotoSelectImageData> newList) {
		if (newList == null) return;
        imageList = newList;

        notifyDataSetChanged();
    }

	private RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smart_snaps_analysis_edit_bottom_photo_fragment_item, parent, false);
		return new PhotoItemHolder(view);
	}

	@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder) {
		super.onViewRecycled(holder);

		if (holder == null || !(holder instanceof PhotoItemHolder)) return;

		PhotoItemHolder photoHolder = (PhotoItemHolder) holder;

		if (photoHolder.getPhotoView() != null) {
			ImageLoader.clear(activity, photoHolder.getPhotoView());
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return getItemViewHolder(parent);
	}

	public ArrayList<MyPhotoSelectImageData> getPhotoItemList() {
		return imageList;
	}

	public MyPhotoSelectImageData getItem(int position) {
		return getPhotoItemList().get(position);
	}

	private ArrayList<SnapsPage> getPageList() {
		SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
		if (snapsTemplateManager.getSnapsTemplate() != null) return snapsTemplateManager.getSnapsTemplate().getPages();
		return null;
	}

	private boolean isSelectedImageData(MyPhotoSelectImageData imageData) {
		ArrayList<SnapsPage> snapsPages = getPageList();
		if (snapsPages == null) return false;

		ArrayList<MyPhotoSelectImageData> imageList = PhotobookCommonUtils.getImageListFromPageList(snapsPages, 0);
		for (MyPhotoSelectImageData selectedImageData : imageList) {
			if (selectedImageData != null
					&& selectedImageData.IMAGE_ID == imageData.IMAGE_ID) return true;
		}
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (holder == null || !(holder instanceof PhotoItemHolder) || getItemCount() <= position) return;

		final PhotoItemHolder photoHolder = (PhotoItemHolder) holder;

		final MyPhotoSelectImageData imageData = getItem(position);
		if (imageData == null) return;

		final ImageView photoView = photoHolder.getPhotoView();
		if (photoView != null) {
			photoView.setImageBitmap(null);
			try {
				int emptyImageRes = photoView.getDrawable() != null ? R.drawable.color_drawable_eeeeee : 0;

				String path = ImageUtil.getImagePath(activity, imageData);

				BitmapTransformation bitmapTransformation = null;
				if (shouldApplyRotateAngle(path, imageData)) {
					bitmapTransformation = new RotateTransformation(imageData.ROTATE_ANGLE);
				}

				ImageLoader.with(activity).placeholder( emptyImageRes ).load(path).bitmapTransformation(bitmapTransformation).into(photoView);
			} catch (OutOfMemoryError e) {
				Dlog.e(TAG, e);
			}

			photoView.setOnTouchListener((v, ev) -> {
				final int action = ev.getAction();
				switch (action & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN: {
						if (isLockTouchEvent || ev.getPointerCount() > 1) {
							return false;
						}

						lastTouchY = ev.getY();

						mActivePointerId = ev.getPointerId(0);
//							performWeakVibration();
						break;
					}

					case MotionEvent.ACTION_MOVE: {
						final int pointerIndex = ev.findPointerIndex(mActivePointerId);
						if (pointerIndex == INVALID_POINTER_ID || pointerIndex == -1)
							break;

						float y = 0;
						try {
							y = ev.getY(pointerIndex);
						} catch (Exception e) {
							Dlog.e(TAG, e);
							break;
						}

						float dy = (y - lastTouchY);
						if (dy < PIXEL_OFFSET_OF_DRAG_START) {
							if (itemDragListener != null && ev.getPointerCount() == 1) {
								isLockTouchEvent = true;
								itemDragListener.onResult(new SmartRecommendBookEditDragImageInfo.Builder().setImageData(imageData).setView(photoView).create());
							}
							mActivePointerId = INVALID_POINTER_ID;
							return false;
						}
						break;
					}

					case MotionEvent.ACTION_OUTSIDE:
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						isLockTouchEvent = false;
						mActivePointerId = INVALID_POINTER_ID;
						break;
				}
				return true;
			});
		}

		ImageView selectLineView = photoHolder.getSelectLineView();
		if (selectLineView != null) {
			selectLineView.setVisibility(isSelectedImageData(imageData) ? View.VISIBLE : View.GONE);
		}
	}

	private boolean shouldApplyRotateAngle(String path, MyPhotoSelectImageData imageData) {
		return imageData != null && imageData.ROTATE_ANGLE > 0 && !StringUtil.isEmpty(path) && path.startsWith("http") && path.contains(imageData.THUMBNAIL_PATH); //웹에 올라간 썸네일을 로딩할 때는 exif 정보를 반영해서 로딩한다.
	}

	protected MyPhotoSelectImageData getPhotoItem(int pos) {
		if(imageList == null || imageList.size() <= pos) return null;
		return imageList.get(pos);
	}

	@Override
	public int getItemCount() {
		return imageList != null ? imageList.size() : 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		int size = imageList.size();
		imageList.clear();
		notifyItemRangeRemoved(0, size);
	}

	public void addAll(MyPhotoSelectImageData[] contentses) {
		int startIndex = imageList.size();
		imageList.addAll(startIndex, Arrays.asList(contentses));
		notifyItemRangeInserted(startIndex, contentses.length);
	}

	public void addAll(List<MyPhotoSelectImageData> contentses) {
		int startIndex = imageList.size();
		imageList.addAll(startIndex, contentses);
		notifyItemRangeInserted(startIndex, contentses.size());
	}

	private void initCoverItemList() {
		if (imageList != null)
			imageList.clear();
		else
			imageList = new ArrayList<>();
	}

	public static class PhotoItemHolder extends RecyclerView.ViewHolder {
		private ImageView selectLineView;
		private ImageView photoView;

		PhotoItemHolder(View itemView) {
			super(itemView);
			this.selectLineView = (ImageView) itemView.findViewById(R.id.smart_snaps_analysis_edit_bottom_photo_fragment_item_select_line_iv);
			this.photoView = (ImageView) itemView.findViewById(R.id.smart_snaps_analysis_edit_bottom_photo_fragment_item_photo_iv);
		}

		public ImageView getSelectLineView() {
			return selectLineView;
		}

		public ImageView getPhotoView() {
			return photoView;
		}
	}
}