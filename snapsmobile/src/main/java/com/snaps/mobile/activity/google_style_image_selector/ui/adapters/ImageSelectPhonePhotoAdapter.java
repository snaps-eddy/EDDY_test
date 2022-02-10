package com.snaps.mobile.activity.google_style_image_selector.ui.adapters;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies.GooglePhotoStyleAdapterStrategyBase;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;

public class ImageSelectPhonePhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = ImageSelectPhonePhotoAdapter.class.getSimpleName();
	private ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> arrImageList;

	private GooglePhotoStyleAdapterStrategyBase googlePhotoStyleStrategy = null;

	private Activity activity = null;

	public ImageSelectPhonePhotoAdapter(Activity activity) {
		this.activity = activity;
	}

	public GooglePhotoStyleAdapterStrategyBase.AdapterAttribute getAttribute() {
		if (googlePhotoStyleStrategy == null) return null;
		return googlePhotoStyleStrategy.getAttribute();
	}

	public void setGooglePhotoStyleStrategy(GooglePhotoStyleAdapterStrategyBase styleStrategy) {
		this.googlePhotoStyleStrategy = styleStrategy;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (googlePhotoStyleStrategy == null) return;
		googlePhotoStyleStrategy.onBindViewHolder(holder, position);
	}

	@Override
	public int getItemViewType(int position) {
		if (googlePhotoStyleStrategy == null) return 0;
		return googlePhotoStyleStrategy.getItemViewType(position);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (googlePhotoStyleStrategy == null) return null;
		return googlePhotoStyleStrategy.onCreateViewHolder(parent, viewType);
	}

	@Override
	public  int getItemCount() {
		return arrImageList != null ? arrImageList.size() + 1 : 1;
	}

	public int getPhotoItemCount() {
		return arrImageList != null ? arrImageList.size() : 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void releaseInstance() {
		if (googlePhotoStyleStrategy != null) {
			googlePhotoStyleStrategy.releaseInstance();
			googlePhotoStyleStrategy = null;
		}
	}

	public void releaseHistory(boolean isFinalize) {
		if (googlePhotoStyleStrategy != null) {
			googlePhotoStyleStrategy.releaseHistory(isFinalize);
		}
	}

	public void setHidden(boolean isHidden) {
		if (googlePhotoStyleStrategy == null) return;
		googlePhotoStyleStrategy.setHidden(isHidden);
	}

	public void setData(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> newList) {
		if (newList == null) return;

		convertPhotoList(newList);

		try {
			notifyItemChangedWithHandler();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void notifyItemChangedWithHandler() throws Exception {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					notifyDataSetChanged();
				} catch (Exception e) { Dlog.e(TAG, e); }
			}
		});
	}

	public void convertPhotoList(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> newList) {
		if (googlePhotoStyleStrategy == null) return;
		arrImageList = googlePhotoStyleStrategy.convertPhotoPhotoList(newList);
	}

	public GridLayoutManager.SpanSizeLookup getScalableSpanSizeLookUp() {
		if (googlePhotoStyleStrategy == null) return null;
		return googlePhotoStyleStrategy.getScalableSpanSizeLookUp();
	}

	public RecyclerView.ItemDecoration getItemDecoration() {
		if (googlePhotoStyleStrategy == null) return null;
		return googlePhotoStyleStrategy.getItemDecoration();
	}

	@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder) {
		super.onViewRecycled(holder);

		if (holder == null || !(holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder)) return;

		ImageSelectAdapterHolders.PhotoFragmentItemHolder photoHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder;

		if (photoHolder.getThumbnail() != null) {
			ImageLoader.clear(activity, photoHolder.getThumbnail());
		}
	}

	public ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> getDataListInAdapter() {
		return arrImageList;
	}

	public GalleryCursorRecord.PhonePhotoFragmentItem getPhotoItem(int pos) {
		if(arrImageList == null || arrImageList.size() <= pos || pos < 0) return null;
		return arrImageList.get(pos);
	}

	public void add(GalleryCursorRecord.PhonePhotoFragmentItem contents) {
		insert(contents, arrImageList.size());
	}

	public void insert(GalleryCursorRecord.PhonePhotoFragmentItem contents, int position) {
		arrImageList.add(position, contents);

		try {
			notifyItemInsertWithHandler(position);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private void notifyItemInsertWithHandler(final int position) throws Exception {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					notifyItemInserted(position);
				} catch (Exception e) { Dlog.e(TAG, e); }
			}
		});
	}

	public int findArrIndexOnAdapterByImageKey(String imageKey) {
		if (imageKey == null || imageKey.length() < 1) return 0;

		for (int ii = 0; ii < getPhotoItemCount(); ii++) {
			GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getPhotoItem(ii);
			if (phonePhotoItem == null) continue;

			String mapKey = ImageSelectUtils.getPhonePhotoMapKey(phonePhotoItem.getPhoneDetailId());
			if (imageKey.equalsIgnoreCase(mapKey)) {
				return ii;
			}
		}

		return 0;
	}

	public int findArrIndexOnAdapterByImageId(long imageId) {
		for (int ii = 0; ii < getPhotoItemCount(); ii++) {
			GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getPhotoItem(ii);
			if (phonePhotoItem == null) continue;

			if (imageId == phonePhotoItem.getPhoneDetailId()) {
				return ii;
			} else if (phonePhotoItem.checkSubKey(imageId)) {
				return ii;
			}
		}

		return 0;
	}

	public GalleryCursorRecord.PhonePhotoFragmentItem findItemByImageKey(String imageKey) {
		if (imageKey == null || imageKey.length() < 1) return null;

		for (int ii = 0; ii < getPhotoItemCount(); ii++) {
			GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getPhotoItem(ii);
			if (phonePhotoItem == null) continue;

			String mapKey = ImageSelectUtils.getPhonePhotoMapKey(phonePhotoItem.getPhoneDetailId());
			if (imageKey.equalsIgnoreCase(mapKey)) {
				return phonePhotoItem;
			}
		}

		return null;
	}

	public void notifyDataSetChangedByImageKey(String imageKey) {
		if (imageKey == null || imageKey.length() < 1) return;

		for (int ii = 0; ii < getPhotoItemCount(); ii++) {
			GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getPhotoItem(ii);
			if (phonePhotoItem == null) continue;

			String mapKey = ImageSelectUtils.getPhonePhotoMapKey(phonePhotoItem.getPhoneDetailId());
			if (imageKey.equalsIgnoreCase(mapKey)) {
				try {
					notifyItemChangedWithHandler(ii + 1); //header 때문에
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				break;
			}
		}
	}

	public void notifyDataSetChangedSection(String imageKey) {
		if (imageKey == null || imageKey.length() < 1) return;

		for (int ii = 0; ii < getPhotoItemCount(); ii++) {
			GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getPhotoItem(ii);
			if (phonePhotoItem == null) continue;

			String mapKey = ImageSelectUtils.getPhonePhotoMapKey(phonePhotoItem.getPhoneDetailId());
			if (imageKey.equalsIgnoreCase(mapKey)) {


				try {
					notifyItemChangedWithHandler(getGroupPosition(ii + 1)); //header 때문에
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}
				break;
			}
		}
	}

	private int getGroupPosition(int position) {

		for(int i = position; i > 0; i--) {
			GalleryCursorRecord.PhonePhotoFragmentItem item = getPhotoItem(i -1);
			if( item.getHolderType() != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL) {
				return i ;
			}
		}
		return 0;

	}

	private void notifyItemChangedWithHandler(final int position) throws Exception {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					notifyItemChanged(position);
				} catch (Exception e) { Dlog.e(TAG, e); }
			}
		});
	}


	/**
	 * @return 변경 되었다면 true
     */
	public boolean notifyPhotoDimensionInfo(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList) {
		if (photoList == null || arrImageList == null) return false;

		boolean isChangedData = false;
		try {
			for (GalleryCursorRecord.PhonePhotoFragmentItem currentItem : arrImageList) {
				if (currentItem == null) continue;

				for (GalleryCursorRecord.PhonePhotoFragmentItem newItem : photoList) {
					if (newItem == null) continue;
					if (currentItem.getPhoneDetailId() == newItem.getPhoneDetailId()) {
						if (!currentItem.isEqualsDimension(newItem)) {
							currentItem.setImageDimension(newItem.getImgOutWidth(), newItem.getImgOutHeight());
							isChangedData = true;
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return isChangedData;
	}
}
